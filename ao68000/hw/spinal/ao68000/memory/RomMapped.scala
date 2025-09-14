package ao68000.memory

import ao68000.core.CpuBus
import spinal.core._
import spinal.lib._

import scala.language.postfixOps

case class RomMapped(baseAddress: UInt) extends Component {
  val io = new Bundle {
    val bus = slave(CpuBus())
  }

  // Instantiate the black-box ROM
  val romBB = new Rom16x1024BB
  romBB.io.clk := True  // Replace with your actual clock if needed

  // Map 10-bit word address (1024 words)
  // Ignore bit 0 because each word = 2 bytes
  romBB.io.addr := io.bus.addr(10 downto 1)

  // Address decode
  val hit = io.bus.as && io.bus.rw &&
    (io.bus.addr >= baseAddress) &&
    (io.bus.addr < baseAddress + 2048) // 1024 words × 2 bytes

  // Connect data and acknowledge
  io.bus.dataIn := Mux(hit, romBB.io.data_out, B(0, 16 bits))
  io.bus.dtack  := hit
}
