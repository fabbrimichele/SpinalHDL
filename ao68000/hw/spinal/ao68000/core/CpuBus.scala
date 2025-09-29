package ao68000.core

import spinal.core._
import spinal.lib.IMasterSlave

import scala.language.postfixOps

case class CpuBus() extends Bundle with IMasterSlave {
  val addr    = UInt(32 bits)
  val dataIn  = Bits(16 bits)
  val dataOut = Bits(16 bits)
  val rw      = Bool()       // 1 = read, 0 = write
  val as      = Bool()       // address strobe
  val dtack   = Bool()
  val uds     = Bool()       // upper data strobe
  val lds     = Bool()       // lower data strobe

  override def asMaster(): Unit = {
    out(addr, dataOut, rw, as, uds, lds)
    in(dtack, dataIn)
  }
}
