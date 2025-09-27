package ao68000.memory

import ao68000.core.CpuBus
import spinal.core._
import spinal.lib._

import scala.io.Source
import scala.language.postfixOps
import scala.util.Using

/**
 * ROM component with 16-bit words.
 *
 * @param size     The size of the ROM in 16-bit words.
 * @param initFile The path to the ROM content file. Each line should contain one word in hexadecimal format.
 *                 The file is read from the **classpath**, so it should be placed under
 *                 `src/main/resources/ao68000/hw/spinal/ao68000/memory/`.
 */
case class Mem16Bits(size: Int, readOnly: Boolean = false, initFile: Option[String] = None) extends Component {
  val io = new Bundle {
    val bus   = slave(CpuBus())
    val sel   = in Bool() // chip select from decoder
    val dtack = out Bool()
  }

  val mem = Mem(Bits(16 bits), size)
  initFile.foreach { filename => mem.init(readContentFromFile(filename)) }

  // Default response
  io.bus.dataIn := 0
  io.dtack := True

  when(!io.bus.as && io.sel) {
    io.dtack := False // active
    val wordAddr = io.bus.addr(log2Up(size) downto 1)

    when(io.bus.rw) {
      // Read
      // TODO: manage UDS/LDS
      io.bus.dataIn := mem.readSync(wordAddr)
    } otherwise  {
      if (!readOnly) {
        // Write if not read only
        // TODO: manage UDS/LDS
        mem.write(wordAddr, io.bus.dataOut)
      }
    }
  }

  private def readContentFromFile(initFile: String) = {
    val romContent = Using.resource(getClass.getResourceAsStream(initFile)) { stream =>
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

