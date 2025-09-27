package ao68000.io

import ao68000.core.CpuBus
import spinal.core._
import spinal.lib.slave

import scala.language.postfixOps

case class LedDevice() extends Component {
  val io = new Bundle {
    val bus   = slave(CpuBus())
    val sel   = in Bool() // chip select from decoder
    val dtack = out Bool()
    val ledOut = out Bits(4 bits)
  }

  val ledReg = Reg(Bits(4 bits)) init 0
  io.ledOut := ledReg

  // Default response
  io.bus.dataIn := 0
  io.dtack := True

  when(!io.bus.as && io.sel && !io.bus.rw) {
    io.dtack := False // active
    // TODO: Should I use LDS/UDS?
    ledReg := io.bus.dataOut(3 downto 0)
  }
}
