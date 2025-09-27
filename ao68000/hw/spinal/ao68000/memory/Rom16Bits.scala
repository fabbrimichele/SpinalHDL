package ao68000.memory

import ao68000.core.CpuBus
import spinal.core._
import spinal.lib.slave

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
    val bus   = slave(CpuBus())
    val sel   = in Bool() // chip select from decoder
    val dtack = out Bool()
  }

  val mem = Mem(Bits(16 bits), size)
  mem.init(readContentFromFile())

  // Default response
  io.bus.dataIn := 0
  io.dtack := True

  when(!io.bus.as && io.sel) {
    io.dtack := False // active
    val wordAddr = io.bus.addr(log2Up(size) downto 1)

    when(io.bus.rw) {
      // Read
      io.bus.dataIn := mem.readSync(wordAddr)
    }
  }

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


