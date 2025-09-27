package ao68000.memory

import ao68000.Config
import spinal.core._
import spinal.core.sim._

import scala.language.postfixOps

object Mem16BitsSim extends App {
  // ROM test
  Config.sim
    .compile {
      Mem16Bits(size = 1024, readOnly = true, initFile = Some("led_on.hex"))
    }
    .doSim { dut =>
      dut.clockDomain.forkStimulus(31.25 ns) // period in ns
      dut.clockDomain.waitRisingEdge()

      // Read word
      dut.io.sel #= true
      dut.io.bus.rw #= true // Read
      dut.io.bus.as #= false // Active
      dut.io.bus.addr #= 0x008A
      dut.clockDomain.waitRisingEdge() // readSync means the value is registered → available one clock later.
      dut.clockDomain.waitRisingEdge() // two clock cycles are required
      val dataIn = dut.io.bus.dataIn.toLong
      val expectedData = 0x4EF8
      assert(dataIn == expectedData, f"Expected dataIn to be equal to 0x$expectedData%04X instead was 0x$dataIn%04X")
    }

  // RAM test
  Config.sim
    .compile {
      Mem16Bits(size = 1024)
    }
    .doSim { dut =>
      dut.clockDomain.forkStimulus(31.25 ns) // period in ns
      dut.clockDomain.waitRisingEdge()

      // Write word
      dut.io.sel #= true
      dut.io.bus.rw #= false // Write
      dut.io.bus.as #= false // Active
      dut.io.bus.addr #= 0x008A
      dut.io.bus.dataOut #= 0x5555
      dut.clockDomain.waitRisingEdge() // readSync means the value is registered → available one clock later.
      dut.clockDomain.waitRisingEdge() // two clock cycles are required

      // Read it back
      dut.io.sel #= true
      dut.io.bus.rw #= true // Read
      dut.io.bus.as #= false // Active
      dut.io.bus.addr #= 0x008A
      dut.clockDomain.waitRisingEdge() // readSync means the value is registered → available one clock later.
      dut.clockDomain.waitRisingEdge() // two clock cycles are required
      val dataIn = dut.io.bus.dataIn.toLong
      val expectedData = 0x5555
      assert(dataIn == expectedData, f"Expected dataIn to be equal to 0x$expectedData%04X instead was 0x$dataIn%04X")
    }
}
