package ao68000.memory

import spinal.core._

import scala.language.postfixOps

case class Ram16Bits(size: Int) extends Component {
  val io = new Bundle {
    val addr    = in UInt(log2Up(size) bits)
    val en      = in Bool()
    val rw      = in Bool()
    val dataOut = out Bits(16 bits)
    val dataIn  = in Bits(16 bits)
  }

  val ram = Mem(Bits(16 bits), size)

  io.dataOut := ram.readSync(
    address = io.addr,
    enable = io.en & io.rw
  )

  when (io.en && !io.rw) {
    ram.write(
      address = io.addr,
      data = io.dataIn
    )
  }
}
