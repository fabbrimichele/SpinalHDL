package ao68000.core

import spinal.core._

import scala.language.postfixOps

//noinspection TypeAnnotation
case class AddressDecoder() extends Component {
  val io = new Bundle {
    val addr    = in UInt(32 bits)
    val as      = in Bool()       // address strobe
    val rw      = in Bool()
    val romEn   = out Bool()
    val ramEn   = out Bool()
    val ledEn   = out Bool()
  }

  io.romEn := !io.as && io.rw &&
    (io.addr < U(0x00000800, 32 bits))                                        // 000000 - 000800 (2KB)
  io.ramEn := !io.as &&
    (io.addr >= U(0x0000800, 32 bits)) && (io.addr < U(0x00001000, 32 bits))  // 000800 - 001000 (2KB)
  io.ledEn := !io.as && !io.rw &&
    (io.addr === U(0x00FF0000, 32 bits))                                      // FF0000
}
