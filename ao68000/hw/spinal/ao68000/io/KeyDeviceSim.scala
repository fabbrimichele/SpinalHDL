package ao68000.io

import ao68000.Config
import spinal.core.sim.{SimBitVectorPimper, SimBoolPimper, SimClockDomainHandlePimper, SimEquivBitVectorBigIntPimper}
import spinal.core.{DoubleToBuilder, assert}

import scala.language.postfixOps

object KeyDeviceSim extends  App {
  Config.sim
    .compile {
      KeyDevice()
    }
    .doSim { dut =>
      dut.io.keyIn #= 0

      dut.clockDomain.forkStimulus(31.25 ns) // period in ns
      dut.clockDomain.waitRisingEdge()

      // Read back
      dut.io.keyIn #= 0b0011
      dut.io.sel #= true
      dut.io.bus.rw #= true // Read
      dut.io.bus.as #= false // Active
      dut.clockDomain.waitRisingEdge() // readSync means the value is registered → available one clock later.
      dut.clockDomain.waitRisingEdge() // two clock cycles are required
      val dataIn = dut.io.bus.dataIn.toLong
      val expectedData = 0x0003
      assert(dataIn == expectedData, f"Expected dataIn to be equal to 0x$expectedData%04X instead was 0x$dataIn%04X")
    }
}
