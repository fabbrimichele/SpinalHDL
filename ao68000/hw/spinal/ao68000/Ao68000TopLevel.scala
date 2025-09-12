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

    io.switchDown <> io.led0

    // Remove io_ prefix
    noIoPrefix()
}

object Ao68000TopLevelVerilog extends App {
  Config.spinal
    .generateVerilog(Ao68000TopLevel())
    .printPruned()
}

object Ao68000TopLevelVhdl extends App {
    Config.spinal.generateVhdl(Ao68000TopLevel())
}
