package ao68000.io

import ao68000.core.CpuBus
import spinal.core._
import spinal.lib.slave

import scala.language.postfixOps

case class LedDevice() extends Component {
  val io = new Bundle {
    val bus   = slave(CpuBus())
    val sel   = in Bool() // chip select from decoder
    val ledOut = out Bits(4 bits)
  }

  val ledReg = Reg(Bits(16 bits)) init 0
  io.ledOut := ledReg(3 downto 0)

  // Default response
  io.bus.dataIn := 0
  io.bus.dtack := True

  when(!io.bus.as && io.sel) {
    io.bus.dtack := False // active

    when(io.bus.rw) {
      // Read
      // TODO: manage UDS/LDS
      io.bus.dataIn := ledReg
    } otherwise  {
      // Write if not read only
      // TODO: manage UDS/LDS
      ledReg := io.bus.dataOut
    }
  }
}
