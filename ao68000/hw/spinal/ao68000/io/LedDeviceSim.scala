package ao68000.io

import ao68000.Config
import spinal.core.{DoubleToBuilder, assert}
import spinal.core.sim.{SimBitVectorPimper, SimBoolPimper, SimClockDomainHandlePimper, SimEquivBitVectorBigIntPimper}

import scala.language.postfixOps

object LedDeviceSim extends  App {
  Config.sim
    .compile {
      LedDevice()
    }
    .doSim { dut =>
      dut.clockDomain.forkStimulus(31.25 ns) // period in ns
      dut.clockDomain.waitRisingEdge()

      // Write
      dut.io.sel #= true
      dut.io.bus.rw #= false // Write
      dut.io.bus.as #= false // Active
      dut.io.bus.dataOut #= 0x5555
      dut.clockDomain.waitRisingEdge() // readSync means the value is registered → available one clock later.
      dut.clockDomain.waitRisingEdge() // two clock cycles are required

      // Assert LEDs
      assert(dut.io.ledOut.toLong == 0b0101, f"Expected ledOut to be equal to 0b0101 instead was ob${dut.io.ledOut.toLong}" )

      // Read back
      dut.io.sel #= true
      dut.io.bus.rw #= true // Read
      dut.io.bus.as #= false // Active
      dut.clockDomain.waitRisingEdge() // readSync means the value is registered → available one clock later.
      dut.clockDomain.waitRisingEdge() // two clock cycles are required
      val dataIn = dut.io.bus.dataIn.toLong
      val expectedData = 0x5555
      assert(dataIn == expectedData, f"Expected dataIn to be equal to 0x$expectedData%04X instead was 0x$dataIn%04X")

    }
}
