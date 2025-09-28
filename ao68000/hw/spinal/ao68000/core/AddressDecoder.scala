package ao68000.core

import spinal.core._

import scala.language.postfixOps

//noinspection TypeAnnotation
case class AddressDecoder() extends Component {
  val io = new Bundle {
    val addr    = in UInt(32 bits)
    val as      = in Bool()       // address strobe
    val rw      = in Bool()
    val romSel   = out Bool()
    val ramSel   = out Bool()
    val ledSel   = out Bool()
  }

  io.romSel := !io.as && io.rw &&
    (io.addr < U(0x00000800, 32 bits))                                        // 000000 - 000800 (2KB)
  io.ramSel := !io.as &&
    (io.addr >= U(0x0000800, 32 bits)) && (io.addr < U(0x00001000, 32 bits))  // 000800 - 001000 (2KB)
  io.ledSel := !io.as && !io.rw &&
    (io.addr === U(0x00FF0000, 32 bits))                                      // FF0000
}
