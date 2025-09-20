package ao68000.io

import spinal.core._

import scala.language.postfixOps

// Timing info:
// Clock = 32 MHz
// Counter = 19 bits
// Debounce time = 2^19 / 32e6 ≈ 16.384 us (MSB flips at half count)
// Wait, careful: MSB toggles at 2^(19-1)/f_clk ≈ 8.192 ms
// Which is close to 10 ms target
class Debouncer(counterSize: Int = 19) extends Component {
  val io = new Bundle {
    val button = in Bool()   // raw input
    val result = out Bool()  // debounced output
  }

  // Two flip-flops to synchronize input
  val ff0 = RegNext(io.button) init(False)
  val ff1 = RegNext(ff0) init(False)

  // Counter increments while input is stable
  val counter = Reg(UInt(counterSize + 1 bits)) init(0)
  val changed = ff0 =/= ff1

  when(changed) {
    counter := 0
  }.otherwise {
    counter := counter + 1
  }

  // Output updates when input has been stable long enough
  val stable = counter(counterSize)
  val resultReg = Reg(Bool()) init(False)
  when(stable) {
    resultReg := ff1
  }

  io.result := resultReg
}
