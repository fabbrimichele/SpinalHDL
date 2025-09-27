package ao68000.core

import spinal.core._
import spinal.lib._

case class Cpu68000() extends Component {
  val io = new Bundle {
    val bus   = master(CpuBus())
    val dtack = in Bool()
  }

  val tg68000 = new Tg68000BB

  // core clocks / reset
  //tg68000.io.clk := io.clock
  //tg68000.io.reset := !io.reset
  tg68000.io.clkena_in := True
  tg68000.io.IPL := 0b111 // TODO: move interrupts into the CpuBus?

  // Bus <-> Core mapping
  io.bus.addr := tg68000.io.addr.asUInt
  io.bus.dataOut := tg68000.io.data_out
  io.bus.rw := tg68000.io.rw
  io.bus.as := tg68000.io.as
  io.bus.uds := tg68000.io.uds
  io.bus.lds := tg68000.io.lds

  // master bus read from bus (driven by slaves)
  tg68000.io.data_in := io.bus.dataIn
  tg68000.io.dtack := io.dtack
}
