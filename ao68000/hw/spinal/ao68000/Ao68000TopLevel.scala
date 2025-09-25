package ao68000

import ao68000.core._
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

    // ROM
    // TODO: implement LDS/UDS
    val rom = Rom16Bits(size = 1024, filename = romFilename) // 2 KB

    // RAM
    // TODO: implement LDS/UDS
    val ram = Ram16Bits(size = 1024) // 2 KB

    // LEDs
    val ledReg = Reg(Bits(4 bits)) init 0
    io.led := ledReg

    // Address decoding
    val addrDec = AddressDecoder()
    addrDec.io.addr := cpu.io.bus.addr
    addrDec.io.as := cpu.io.bus.as
    addrDec.io.rw := cpu.io.bus.rw

    // DTACK combines all slave hits
    // TODO: Should each device determine its own dtack?
    cpu.io.bus.dtack := False //addrDec.io.romEn && addrDec.io.ramEn && addrDec.io.ledEn

    when(addrDec.io.romEn) {
      cpu.io.bus.dataIn := rom.io.dataOut
    } elsewhen(addrDec.io.ramEn) {
      cpu.io.bus.dataIn := ram.io.dataOut
    } otherwise {
      cpu.io.bus.dataIn := B(0, 16 bits)
    }

    // ROM binding
    rom.io.addr := cpu.io.bus.addr(10 downto 1)
    rom.io.en := addrDec.io.romEn

    // RAM binding
    ram.io.rw := cpu.io.bus.rw
    ram.io.addr := cpu.io.bus.addr(10 downto 1)
    ram.io.dataIn := cpu.io.bus.dataOut
    ram.io.en := addrDec.io.ramEn

    // LED binding
    when(addrDec.io.ledEn) {
      ledReg := cpu.io.bus.dataOut(3 downto 0)
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
