package ao68000.memory

import spinal.core._

import scala.io.Source
import scala.language.postfixOps
import scala.util.Using

/**
 * ROM component with 16-bit words.
 *
 * @param size     The size of the ROM in 16-bit words.
 * @param filename The path to the ROM content file. Each line should contain one word in hexadecimal format.
 *                 The file is read from the **classpath**, so it should be placed under
 *                 `src/main/resources/ao68000/hw/spinal/ao68000/memory/`.
 */
case class Rom16Bits(size: Int, filename: String) extends Component {
  val io = new Bundle {
    val addr = in UInt(log2Up(size) bits)
    val en      = in Bool()
    val dataOut = out Bits(16 bits)
  }

  val rom = Mem(Bits(16 bits), size)
  rom.init(readContentFromFile())

  io.dataOut := rom.readSync(io.addr, io.en)

  private def readContentFromFile() = {
    val romContent = Using.resource(getClass.getResourceAsStream(filename)) { stream =>
      val source = Source.fromInputStream(stream)
      try {
        source.getLines()
          .map { line => B(java.lang.Long.parseLong(line.trim, 16), 16 bits) }
          .toSeq
      } finally source.close()
    }
    assert(romContent.size <= size, s"ROM content file greater than $size")
    romContent ++ Seq.fill(1024 - romContent.size)(B(0, 16 bits))
  }
}


