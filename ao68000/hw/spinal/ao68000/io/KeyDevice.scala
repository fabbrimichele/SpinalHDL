package ao68000.io

import ao68000.core.CpuBus
import spinal.core._
import spinal.lib.slave

import scala.language.postfixOps

case class KeyDevice() extends Component {
  val io = new Bundle {
    val bus   = slave(CpuBus())
    val sel   = in Bool() // chip select from decoder
    val dtack = out Bool()
    val keyIn = in Bits(4 bits)
  }

  val keyReg = Reg(Bits(16 bits)) init 0
  keyReg := io.keyIn.resize(16 bits)

  // Default response
  io.bus.dataIn := 0
  io.dtack := True

  when(!io.bus.as && io.sel) {
    io.dtack := False // active

    when(io.bus.rw) {
      // Read
      // TODO: manage UDS/LDS
      io.bus.dataIn := keyReg
    }
  }
}
