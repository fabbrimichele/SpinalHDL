package ao68000

import ao68000.core._
import ao68000.io.{KeyDevice, LedDevice}
import ao68000.memory._
import spinal.core.{True, _}
import spinal.lib.{MuxOH, PriorityMux}

import scala.language.postfixOps

/**
 * Hardware definition
 * @param romFilename name of the file containing the ROM content
 */
//noinspection TypeAnnotation
case class Ao68000TopLevel(romFilename: String) extends Component {
  val io = new Bundle {
    val reset = in Bool()
    val led = out Bits(4 bits)
    val key = in Bits(4 bits)
  }

  val resetController = ResetController()
  resetController.io.button := io.reset

  val resetArea = new ResetArea(resetController.io.resetOut, cumulative = false) {
    // CPU
    val cpu = Cpu68000()

    // Peripherals
    val rom = Mem16Bits(size = 1024, readOnly = true, initFile = Some(romFilename)) // 2 KB
    val ram = Mem16Bits(size = 1024) // 2 KB
    val led = LedDevice()
    io.led := led.io.ledOut
    val key = KeyDevice()
    key.io.keyIn := io.key

    val deviceBuses = Seq(rom.io.bus, ram.io.bus, led.io.bus, key.io.bus)

    // Connect CPU bus to devices
    deviceBuses.foreach { deviceBus =>
      deviceBus.addr := cpu.io.bus.addr
      deviceBus.dataOut := cpu.io.bus.dataOut
      deviceBus.as := cpu.io.bus.as
      deviceBus.lds := cpu.io.bus.lds
      deviceBus.uds := cpu.io.bus.uds
      deviceBus.rw := cpu.io.bus.rw
    }

    // Combines all devices dtack (active low)
    cpu.io.dtack := deviceBuses.map(_.dtack).reduce(_ && _)

    // Address decoding
    val addrDec = AddressDecoder()
    addrDec.io.addr := cpu.io.bus.addr
    addrDec.io.as := cpu.io.bus.as

    // Chip selects
    ram.io.sel := addrDec.io.ramSel
    rom.io.sel := addrDec.io.romSel
    led.io.sel := addrDec.io.ledSel
    key.io.sel := addrDec.io.keySel

    // DataIn mux
    cpu.io.bus.dataIn := PriorityMux(
      Seq(
        addrDec.io.romSel -> rom.io.bus.dataIn,
        addrDec.io.ramSel -> ram.io.bus.dataIn,
        addrDec.io.ledSel -> led.io.bus.dataIn,
        addrDec.io.keySel -> key.io.bus.dataIn,
        True -> B(0, 16 bits)
      )
    )
  }

  // Remove io_ prefix
  noIoPrefix()
}

object Ao68000TopLevelVhdl extends App {
  private val romFilename = "keys.hex"
  //private val romFilename = "blinker.hex"
  private val report = Config.spinal.generateVhdl(Ao68000TopLevel(romFilename))
  report.mergeRTLSource("mergeRTL") // Merge all rtl sources into mergeRTL.vhd and mergeRTL.v files
  report.printPruned()
}
