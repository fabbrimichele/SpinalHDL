package ao68000

import ao68000.core._
import ao68000.io.Debounce
import ao68000.memory._
import spinal.core._
import spinal.lib._

import scala.language.postfixOps

//noinspection TypeAnnotation
// Hardware definition
case class Ao68000TopLevel() extends Component {
  val io = new Bundle {
    val reset = in Bool()
    val led = out Bits(4 bits)
    val switchLeft = in Bool()
    val switchDown = in Bool()
    val switchUp = in Bool()
    val switchRight = in Bool()
    //val cpu  = master(CpuBus()) // CPU is master
  }

  val debounce = new Debounce
  debounce.io.button := io.reset

  new ResetArea(debounce.io.result, cumulative = false) {
    val cpu = Cpu68000()

    val rom = RomMapped(baseAddress = 0x00000000)
    cpu.io.bus <> rom.io.bus

    io.led := cpu.io.bus.addr(25 downto 22).asBits
  }

  // Remove io_ prefix
  noIoPrefix()
}

object Ao68000TopLevelVerilog extends App {
  val report = Config.spinal
    .generateVerilog(Ao68000TopLevel())
  report.mergeRTLSource("mergeRTL") // Merge all rtl sources into mergeRTL.vhd and mergeRTL.v files
  report.printPruned()
}

object Ao68000TopLevelVhdl extends App {
    Config.spinal.generateVhdl(Ao68000TopLevel())
}
