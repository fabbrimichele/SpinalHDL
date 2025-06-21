package blink

import spinal.core._

// Hardware definition
case class BlinkTopLevel() extends Component {
    val io = new Bundle {
        val clk = in Bool()
        val led = out Bool()
    }

  // Declare ledInternal as a register (not just a wire)
  val ledInternal = Reg(Bool())
  ledInternal := io.clk
  io.led := ledInternal
}

object BlinkTopLevelVerilog extends App {
    Config.spinal.generateVerilog(BlinkTopLevel())
}

object BlinkTopLevelVhdl extends App {
    Config.spinal.generateVhdl(BlinkTopLevel())
}
