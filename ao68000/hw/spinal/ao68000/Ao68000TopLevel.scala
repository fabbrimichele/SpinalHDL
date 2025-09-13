package ao68000

import spinal.core._
import spinal.lib._
import spinal.lib.com.uart._

//noinspection TypeAnnotation
// Hardware definition
case class Ao68000TopLevel() extends Component {
  val io = new Bundle {
    val switchDown = in Bool()   // Trigger to send
    val led0 = out Bool()
  }

  // Instantiate the BlackBox
  val switchLed = new SwitchLedBB
  io.led0 := switchLed.io.led0
  switchLed.io.switchDown := io.switchDown

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
