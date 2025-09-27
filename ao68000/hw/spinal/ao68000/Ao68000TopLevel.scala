package ao68000

import ao68000.core._
import ao68000.io.LedDevice
import ao68000.memory._
import spinal.core._
import spinal.lib.{MuxOH, PriorityMux}

import scala.language.postfixOps

/**
 * Hardware definition
 * @param romFilename name of the file containing the ROM content
 */
//noinspection TypeAnnotation
case class Ao68000TopLevel(romFilename: String = "blinker.hex") extends Component {
  val io = new Bundle {
    val reset = in Bool()
    val led = out Bits(4 bits)
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

    // Address decoding
    val addrDec = AddressDecoder()
    addrDec.io.addr := cpu.io.bus.addr
    addrDec.io.as := cpu.io.bus.as
    addrDec.io.rw := cpu.io.bus.rw

    // Chip selects
    ram.io.sel := addrDec.io.ramSel
    rom.io.sel := addrDec.io.romSel
    led.io.sel := addrDec.io.ledSel

    // Connect CPU bus to devices
    Seq(rom.io.bus, ram.io.bus, led.io.bus).foreach { deviceBus =>
      deviceBus.addr := cpu.io.bus.addr
      deviceBus.dataOut := cpu.io.bus.dataOut
      deviceBus.as := cpu.io.bus.as
      deviceBus.lds := cpu.io.bus.lds
      deviceBus.uds := cpu.io.bus.uds
      deviceBus.rw := cpu.io.bus.rw
    }

    // DTACK combines all slave hits
    // TODO: Should each device determine its own dtack?
    //cpu.io.bus.dtack := addrDec.io.romEn && addrDec.io.ramEn && addrDec.io.ledEn

    // DTACK mux (any selected device can ack)
    cpu.io.dtack := False

    when(addrDec.io.romSel) {
      cpu.io.bus.dataIn := rom.io.bus.dataIn
    } elsewhen(addrDec.io.ramSel) {
      cpu.io.bus.dataIn := ram.io.bus.dataIn
    } otherwise {
      cpu.io.bus.dataIn := B(0, 16 bits)
    }
  }

  // Remove io_ prefix
  noIoPrefix()
}

object Ao68000TopLevelVhdl extends App {
  val report = Config.spinal.generateVhdl(Ao68000TopLevel())
  report.mergeRTLSource("mergeRTL") // Merge all rtl sources into mergeRTL.vhd and mergeRTL.v files
  report.printPruned()
}
