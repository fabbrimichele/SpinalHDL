package ao68000

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
      val dut = Ao68000TopLevel(romFilename = "led_on.hex")
      dut.resetArea.cpu.io.bus.simPublic() // <-- make it accessible
      dut.resetArea.cpu.tg68000.io.simPublic()
      dut.resetArea.hitLed.simPublic()
      dut
    }
    .doSim { dut =>
      // Clock stimulus for 32 MHz
      // It should also trigger the reset, but it seems it doesn't work.
      dut.clockDomain.forkStimulus(31.25 ns) // period in ns

      // Reset button not pushed
      dut.io.reset #= false

      // TODO: ResetController makes GHDL log warnings
      // Run simulation for a while (adjust cycles as needed)
      for (i <- 0 until 80) {
        dut.clockDomain.waitRisingEdge()
        val bus = dut.resetArea.cpu.io.bus

        // CPU bus
        val tg68IO = dut.resetArea.cpu.tg68000
        val address = tg68IO.io.addr.toLong
        val dataIn = tg68IO.io.data_in.toLong
        val dataOut = tg68IO.io.data_out.toLong
        val as = boolToBit(tg68IO.io.as)
        val rw = boolToBit(tg68IO.io.rw)
        val lds = boolToBit(tg68IO.io.lds)
        val uds = boolToBit(tg68IO.io.uds)

        // LEDs
        val hitLed = boolToBit(dut.resetArea.hitLed)
        val led = dut.io.led.toLong

        val color = {
          if (address > 0x90 && !bus.as.toBoolean) "\u001b[33m"
          else if (!bus.as.toBoolean) "\u001b[32m"
          else "\u001b[0m"
        }

        print(color)
        println(f"Cycle: $i%4d" +
          f"| address: 0x$address%08X" +
          f"| dataIn: 0x$dataIn%04X" +
          f"| dataOut: 0x$dataOut%04X" +
          f"| as: $as" +
          f"| rw: $rw" +
          f"| uds: $uds" +
          f"| lds: $lds" +
          f"| hitLed: $hitLed" +
          f"| LED: $led%04X")
      }

      assert(dut.io.led.toLong == 1, "Expected LED to be '0001'")
    }

  private def boolToBit(b: Bool) = if (b.toBoolean) "1" else "0"
}