package ao68000.memory

import ao68000.Config
import spinal.core._
import spinal.core.sim._

import scala.language.postfixOps

object Rom16BitsSim extends App {
  Config.sim
    .compile {
      val dut = Rom16Bits(size = 1024, filename = "led_on.hex")
      dut
    }
    .doSim { dut =>
      dut.clockDomain.forkStimulus(31.25 ns) // period in ns
      dut.clockDomain.waitRisingEdge()

      dut.io.sel #= true
      dut.io.bus.rw #= true // Read
      dut.io.bus.addr #= 0
      dut.io.bus.as #= false // Active
      dut.clockDomain.waitRisingEdge()

      // Read word
      dut.io.bus.addr #= 0x0000
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()
      assertDataIn(dut = dut, expectedDataIn = 0x0000)

      // Read word
      dut.io.bus.addr #= 0x008A
      dut.clockDomain.waitRisingEdge() // readSync means the value is registered → available one clock later.
      dut.clockDomain.waitRisingEdge() // two clock cycles are required TODO: improve RAM?
      assertDataIn(dut = dut, expectedDataIn = 0x4EF8)

      for (i <- 0 until 80) {
        dut.io.bus.addr #= i << 1   // read one word at a time
        dut.clockDomain.waitRisingEdge()
        dut.clockDomain.waitRisingEdge()
        println(f"address:0x${dut.io.bus.addr.toLong}%04X | dataIn: 0x${dut.io.bus.dataIn.toLong}%04X")
      }
    }

  private def assertDataIn(dut: Rom16Bits, expectedDataIn: Long): Unit = {
    val dataIn = dut.io.bus.dataIn.toLong
    assert(dataIn == expectedDataIn, f"Expected dataIn to be equal to 0x$expectedDataIn%04X instead was 0x$dataIn%04X")
  }
}
