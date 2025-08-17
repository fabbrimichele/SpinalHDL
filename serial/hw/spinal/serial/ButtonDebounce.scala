package serial

import spinal.core._

class ButtonDebounce(clockFreqHz: Int = 32000000, debounceMs: Int = 10) extends Component {
  val io = new Bundle {
    val button = in Bool()
    val debounced = out Bool()
  }

  // Calculate counter width
  val maxCount = (clockFreqHz.toLong * debounceMs / 1000).toInt
  val counterWidth = log2Up(maxCount)

  val counter = Reg(UInt(counterWidth bits)) init(0)
  val btnSync = RegNext(RegNext(io.button))
  val btnStable = Reg(Bool()) init(False)

  when(btnSync === btnStable) {
    counter := 0
  } otherwise {
    counter := counter + 1
    when(counter === maxCount - 1) {
      btnStable := btnSync
      counter := 0
    }
  }

  io.debounced := btnStable
}
