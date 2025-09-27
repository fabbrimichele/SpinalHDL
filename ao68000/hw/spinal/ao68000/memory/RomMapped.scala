package ao68000.memory

import ao68000.core.CpuBus
import spinal.core._
import spinal.lib._

import scala.language.postfixOps

case class RomMapped(baseAddress: BigInt) extends Component {
  val io = new Bundle {
    val bus = slave(CpuBus())
  }

  val addr = io.bus.addr  // Internal alias
  val as   = io.bus.as
  val rw   = io.bus.rw

  // ROM instantiation
  val romBB = new Rom16x1024BB
  romBB.io.addr := addr(10 downto 1)

  // Address decode
  val hit = as && rw &&
    (addr >= U(baseAddress, 32 bits)) &&
    (addr < U(baseAddress + 2048, 32 bits))

  // Internal directionless signals
  val dataOut = Bits(16 bits)
  val dtack   = Bool()

  dataOut := Mux(hit, romBB.io.data_out, B(0, 16 bits))
  dtack   := hit

  // Connect to the slave port
  io.bus.dataIn := dataOut
  //io.bus.dtack  := dtack
}
