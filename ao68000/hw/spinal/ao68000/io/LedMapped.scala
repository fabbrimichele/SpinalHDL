package ao68000.io

import ao68000.core.CpuBus
import spinal.core._
import spinal.lib._

import scala.language.postfixOps

case class LedMapped(baseAddress: UInt) extends Component {
  val io = new Bundle {
    val bus  = slave(CpuBus())
    val leds = out Bits(4 bits)
  }

  val ledReg = Reg(Bits(4 bits)) init(0)
  io.leds := ledReg

  val writeEnable = io.bus.as && !io.bus.rw && (io.bus.addr === baseAddress)

  when(writeEnable && io.bus.lds){
    ledReg := io.bus.dataOut(3 downto 0)
  }

  io.bus.dtack := writeEnable
}
