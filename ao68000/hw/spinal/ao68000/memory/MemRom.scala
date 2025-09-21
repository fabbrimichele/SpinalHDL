package ao68000.memory

import spinal.core._

import scala.io.Source
import scala.language.postfixOps
import scala.util.Using

case class MemRom(size: Int = 1024, filename: String = "../../../gen/rom.hx") extends Component {
  val io = new Bundle {
    val addr = in UInt(log2Up(size) bits)
    val en      = in Bool()
    val dataOut = out Bits(16 bits)
  }

  val romInit = Seq.fill(size)(B(0x0000, 16 bits))
    .updated(index = 3, B(0x0080, 16 bits))
    .patch(from = 64, replaced = 8, other = Seq(
      B(0x41F9, 16 bits),
      B(0x00FF, 16 bits),
      B(0x0000, 16 bits),
      B(0x30BC, 16 bits),
      B(0x0001, 16 bits),
      B(0x4E71, 16 bits),
      B(0x4EF8, 16 bits),
      B(0x008C, 16 bits),
    ))

  val rom = Mem(Bits(16 bits), size)
  rom.init(romInit)

  io.dataOut := rom.readSync(io.addr, io.en)

  private def readFile() = Using.resource( Source.fromFile(filename)) { source =>
    source.getLines()
      .map { line => B(java.lang.Long.parseLong(line), 16 bits) }
      .toSeq
  }
}


