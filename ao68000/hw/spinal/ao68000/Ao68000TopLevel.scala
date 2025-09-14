package ao68000

import ao68000.core.{CpuBus, Tg68000BB}
import ao68000.io.Debounce
import ao68000.memory.Rom16x1024BB
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
    val rom = new Rom16x1024BB

    val tg68000 = new Tg68000BB
    // 68000 control lines
    tg68000.io.dtack := False // Always valid (active low)
    tg68000.io.clkena_in := True
    tg68000.io.IPL := 0b111 // No interrupts (active low)

    // Map Data bus
    tg68000.io.data_in := rom.io.data_out

    // Map Address bus
    rom.io.addr := tg68000.io.addr(9 downto 0) // ROM is duplicated into the whole space address
    io.led := tg68000.io.addr(25 downto 22).asBits // most 4 significant bits
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
