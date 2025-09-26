package ao68000.core

import ao68000.Config
import spinal.core._
import spinal.core.sim._

import scala.language.postfixOps

object ResetControllerSim extends App {
  val resetHoldCycles = 5
  Config.sim
    .compile {
      val dut = ResetController(resetHoldCycles)
      dut.counter.simPublic()
      dut
    }
    .doSim { dut =>
      var cycle = 0

      // Init
      dut.io.button #= false

      // Run
      dut.clockDomain.forkStimulus(31.25 ns) // period in ns
      dut.clockDomain.onRisingEdges {
        println(f"Cycle: $cycle%4d" +
          f"| button: ${boolToBit(dut.io.button)}" +
          f"| reset: ${boolToBit(dut.io.resetOut)}" +
          f"| counter: ${dut.counter.toLong}%4d"
        )
        cycle += 1
      }

      // Give the simulation the time to init the registers
      // Real hardware does not need it
      dut.clockDomain.waitRisingEdge(1)
      assert(dut.io.resetOut.toBoolean, "Expected reset active")

      // After resetHoldCycles cycles reset inactive
      dut.clockDomain.waitRisingEdge(resetHoldCycles)
      assert(!dut.io.resetOut.toBoolean, "Expected reset inactive")

      // Push reset button
      dut.io.button #= true
      dut.clockDomain.waitRisingEdge(2) // Give time to set registers
      assert(dut.io.resetOut.toBoolean, "Expected reset active")

      // Release reset button, reset still active
      dut.io.button #= false
      dut.clockDomain.waitRisingEdge(resetHoldCycles - 1)
      assert(dut.io.resetOut.toBoolean, "Expected reset active")

      // After reset period, reset inactive
      dut.clockDomain.waitRisingEdge(resetHoldCycles)
      assert(!dut.io.resetOut.toBoolean, "Expected reset inactive")

    }

  private def boolToBit(b: Bool) = if (b.toBoolean) "1" else "0"
}
