package ao68000

import ao68000.core._
import ao68000.io._
import ao68000.memory._
import spinal.core._

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

    // Address decoding
    val hitRom = !cpu.io.bus.as && cpu.io.bus.rw &&
      (cpu.io.bus.addr < U(2048, 32 bits)) // ROM: 1024 words x 2 bytes
    val hitLed = !cpu.io.bus.as && !cpu.io.bus.rw &&
      (cpu.io.bus.addr === U(0x00FF0000, 32 bits))
    // Future devices can be added by defining more `hitDevice` signals

    // DTACK combines all slave hits
    cpu.io.bus.dtack := !(!hitRom || !hitLed)

    // ROM
    val rom = Rom16Bits(size = 1024, filename = romFilename)
    cpu.io.bus.dataIn := rom.io.dataOut
    rom.io.addr := cpu.io.bus.addr(10 downto 1)
    rom.io.en := hitRom

    // LEDs
    val ledReg = Reg(Bits(4 bits)) init 0
    io.led := ledReg

    // CPU writes to LED register
    when(hitLed) {
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
