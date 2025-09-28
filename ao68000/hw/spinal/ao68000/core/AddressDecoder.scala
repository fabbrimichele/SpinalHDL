package ao68000.core

import spinal.core._

import scala.language.postfixOps

//noinspection TypeAnnotation
case class AddressDecoder() extends Component {
  val io = new Bundle {
    val addr    = in UInt(32 bits)
    val as      = in Bool()       // address strobe
    val rw      = in Bool()
    val romSel  = out Bool()
    val ramSel  = out Bool()
    val ledSel  = out Bool()
    val keySel  = out Bool()
  }

  // TODO: consider only AS, RW is managed by the device
  io.romSel := !io.as && (io.addr < U(0x00000800, 32 bits))                                        // 000000 - 000800 (ROM 2KB)
  io.ramSel := !io.as && (io.addr >= U(0x0000800, 32 bits)) && (io.addr < U(0x00001000, 32 bits))  // 000800 - 001000 (RAM 2KB)
  io.ledSel := !io.as && (io.addr === U(0x00FF0000, 32 bits))                                      // FF0000 (LEDs)
  io.keySel := !io.as && (io.addr === U(0x00FF0002, 32 bits))                                      // FF0002 (Keys)
}
