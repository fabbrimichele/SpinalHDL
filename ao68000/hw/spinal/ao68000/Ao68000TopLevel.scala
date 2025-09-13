package ao68000

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
  }

  val debounce = new Debounce
  debounce.io.button := io.reset

  val tg68000 = new Tg68000BB

  // 68000 control lines
  tg68000.io.reset := !debounce.io.result // No reset (active low)
  tg68000.io.dtack := False // Always valid (active low)
  tg68000.io.clkena_in := True
  tg68000.io.IPL := 0b111 // No interrupts (active low)

  // Memory Data bus
  tg68000.io.data_in := 0x4E71 // NOP - no operation

  // Map Address bus to LEDs
  io.led := tg68000.io.addr(25 downto 22).asBits // most 4 significant bits


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
