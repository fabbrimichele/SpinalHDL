package ao68000

import spinal.core._
import spinal.core.sim._
import spinal.sim.GhdlFlags

import scala.language.postfixOps

object Ao68000TopLevelSim extends App {
  val flagExplicit = "-fexplicit" // This is required to make GHDL compile TG68.vhd
  Config.sim
    .withGHDL(GhdlFlags().withElaborationFlags(flagExplicit))
    .addSimulatorFlag(flagExplicit) // Something is off, this is required, but it shouldn't
    .addRtl("/home/michele/spinalHDL/ao68000/hw/vhdl/TG68.vhd")
    .addRtl("/home/michele/spinalHDL/ao68000/hw/vhdl/TG68_fast.vhd")
    .addRtl("/home/michele/spinalHDL/ao68000/hw/vhdl/rom_16x1024.vhd")
    .compile {
      val dut = Ao68000TopLevel()
      dut.resetArea.cpu.io.bus.addr.simPublic() // <-- make it accessible
      dut
    }
    .doSim { dut =>
      // Clock stimulus for 32 MHz
      dut.clockDomain.forkStimulus(31.25 ns) // period in ns

      // Apply reset
      dut.io.reset #= true
      dut.clockDomain.waitRisingEdge(5)
      dut.io.reset #= false
      dut.clockDomain.waitRisingEdge(5)

      // Run simulation for a while (adjust cycles as needed)
      for(i <- 0 until 2000){
        dut.clockDomain.waitRisingEdge()

        // Optional: print CPU address bus
        println(f"Cycle $i%4d, CPU addr: 0x${dut.resetArea.cpu.io.bus.addr.toLong}%08X, LED: ${dut.io.led.toLong}%01X")
      }
    }
}