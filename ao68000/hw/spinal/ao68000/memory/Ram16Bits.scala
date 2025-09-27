package ao68000.memory

import ao68000.core.CpuBus
import spinal.core._
import spinal.lib._

import scala.language.postfixOps

case class Ram16Bits(size: Int) extends Component {
  val io = new Bundle {
    val bus   = slave(CpuBus())
    val sel   = in Bool() // chip select from decoder
    val dtack = out Bool()
  }

  val mem = Mem(Bits(16 bits), size)

  // Default response
  io.bus.dataIn := 0
  io.dtack := True // inactive

  when(!io.bus.as && io.sel) {
    io.dtack := False // active
    val wordAddr = io.bus.addr(log2Up(size) downto 1)

    when(io.bus.rw) {
      // Read
      io.bus.dataIn := mem.readSync(wordAddr) // TODO: try to use (wordAddr, enable = true)
    } otherwise {
      // Write
      // TODO: manage UDS/LDS
      mem.write(wordAddr, io.bus.dataOut)
    }
  }
}
