package ao68000.core

import ao68000.Config
import spinal.core._
import spinal.core.sim._
import spinal.sim.GhdlFlags

import scala.language.postfixOps

object ResetControllerSim extends App {
  val flagExplicit = "-fexplicit" // This is required to make GHDL compile TG68.vhd
  Config.sim
    .withGHDL(GhdlFlags().withElaborationFlags(flagExplicit, "--warn-no-specs"))
    .addSimulatorFlag(flagExplicit) // Something is off, this is required, but it shouldn't
    .addRtl("/home/michele/spinalHDL/ao68000/hw/vhdl/TG68.vhd")
    .addRtl("/home/michele/spinalHDL/ao68000/hw/vhdl/TG68_fast.vhd")
    .addRtl("/home/michele/spinalHDL/ao68000/hw/vhdl/rom_16x1024.vhd")
    .compile {
      val dut = ResetController(resetHoldCycles = 5)
      dut.counter.simPublic()
      dut
    }
    .doSim { dut =>
      // Init
      dut.io.button #= false
      var cycle = 0

      // Run
      dut.clockDomain.forkStimulus(31.25 ns) // period in ns
      while (cycle <= 7) {
        dut.clockDomain.waitRisingEdge()
        println(f"Cycle: $cycle%4d" +
          f"| button: ${boolToBit(dut.io.button)}" +
          f"| reset: ${boolToBit(dut.io.resetOut)}" +
          f"| counter: ${dut.counter.toLong}%4d"
        )
        cycle += 1
      }

      dut.io.button #= true
      // dut.clockDomain.waitSampling(5) // Wait for 5 clock cycles
      while (cycle <= 10) {
        dut.clockDomain.waitRisingEdge()
        println(f"Cycle: $cycle%4d" +
          f"| button: ${boolToBit(dut.io.button)}" +
          f"| reset: ${boolToBit(dut.io.resetOut)}" +
          f"| counter: ${dut.counter.toLong}%4d"
        )
        cycle += 1
      }
      dut.io.button #= false

      while (cycle <= 20) {
        dut.clockDomain.waitRisingEdge()
        println(f"Cycle: $cycle%4d" +
          f"| button: ${boolToBit(dut.io.button)}" +
          f"| reset: ${boolToBit(dut.io.resetOut)}" +
          f"| counter: ${dut.counter.toLong}%4d"
        )
        cycle += 1
      }
    }

  private def boolToBit(b: Bool) = if (b.toBoolean) "1" else "0"
}
