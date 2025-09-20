package ao68000

import spinal.core.Component.push
import spinal.core._
import spinal.core.sim._
import spinal.sim.GhdlFlags

import scala.language.postfixOps

object Ao68000TopLevelSim extends App {
  val flagExplicit = "-fexplicit" // This is required to make GHDL compile TG68.vhd
  Config.sim
    .withGHDL(GhdlFlags().withElaborationFlags(flagExplicit, "--warn-no-specs"))
    .addSimulatorFlag(flagExplicit) // Something is off, this is required, but it shouldn't
    .addRtl("/home/michele/spinalHDL/ao68000/hw/vhdl/TG68.vhd")
    .addRtl("/home/michele/spinalHDL/ao68000/hw/vhdl/TG68_fast.vhd")
    .addRtl("/home/michele/spinalHDL/ao68000/hw/vhdl/rom_16x1024.vhd")
    .compile {
      val dut = Ao68000TopLevel()
      dut.resetArea.cpu.io.bus.simPublic() // <-- make it accessible
      dut.resetArea.cpu.tg68000.io.simPublic()
      dut
    }
    .doSim { dut =>
      // Clock stimulus for 32 MHz
      dut.clockDomain.forkStimulus(31.25 ns) // period in ns, this also trigger reset

      // Apply reset
      dut.io.reset #= true
      dut.clockDomain.waitRisingEdge(5)
      dut.io.reset #= false
      dut.clockDomain.waitRisingEdge(5)

      // Run simulation for a while (adjust cycles as needed)
      for (i <- 0 until 50) {
        dut.clockDomain.waitRisingEdge()
        val bus = dut.resetArea.cpu.io.bus

        // CPU bus
        val tg68IO = dut.resetArea.cpu.tg68000.io
        val addr = tg68IO.addr.toLong
        val dataIn = tg68IO.data_in.toLong
        val dataOut = tg68IO.data_out.toLong
        val as = boolToBit(tg68IO.as)
        val lds = boolToBit(tg68IO.lds)
        val uds = boolToBit(tg68IO.uds)

        // LEDs
        val led = dut.io.led.toLong

        val color = {
          if (addr > 0x90 && !bus.as.toBoolean) "\u001b[33m"
          else if (!bus.as.toBoolean) "\u001b[32m"
          else "\u001b[0m"
        }

        print(color)
        println(f"Cycle: $i%4d" +
          f"| addr: 0x$addr%08X" +
          f"| dataIn: 0x$dataIn%04X" +
          f"| dataOut: 0x$dataOut%04X" +
          f"| as: $as" +
          f"| lds: $lds" +
          f"| uds: $uds" +
          f"| LED: $led%04X")
      }
    }

  def boolToBit(b: Bool) = if (b.toBoolean) "1" else "0"
}