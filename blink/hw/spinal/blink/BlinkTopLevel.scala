package blink

import spinal.core._

// Hardware definition
case class BlinkTopLevel() extends Component {
  val io = new Bundle {
    val cond0 = in  Bool()
    val cond1 = in  Bool()
    val flag  = out Bool()
    val state = out UInt(8 bits)
  }

  val counter = Reg(UInt(8 bits)) init 0

  when(io.cond0) {
    counter := counter + 1
  }

  io.state := counter
  io.flag := (counter === 0) | io.cond1
}

object BlinkTopLevelVerilog extends App {
  Config.spinal.generateVerilog(BlinkTopLevel())
}

object BlinkTopLevelVhdl extends App {
  Config.spinal.generateVhdl(BlinkTopLevel())
}
