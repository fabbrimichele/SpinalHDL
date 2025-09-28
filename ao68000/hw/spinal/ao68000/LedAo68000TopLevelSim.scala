package ao68000

import spinal.core._
import spinal.core.sim._

import scala.language.postfixOps

object LedAo68000TopLevelSim extends App {
  Config.sim
    .compile {
      val dut = Ao68000TopLevel(romFilename = "led_on.hex")
      dut.resetArea.cpu.io.bus.simPublic() // <-- make it accessible
      dut.resetArea.cpu.tg68000.io.simPublic()
      dut.resetArea.addrDec.io.simPublic()
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
        val tg68 = dut.resetArea.cpu.tg68000
        val address = tg68.io.addr.toLong
        val dataIn = tg68.io.data_in.toLong
        val dataOut = tg68.io.data_out.toLong
        val as = boolToBit(tg68.io.as)
        val rw = boolToBit(tg68.io.rw)
        val lds = boolToBit(tg68.io.lds)
        val uds = boolToBit(tg68.io.uds)

        // LEDs
        val ledEn = boolToBit(dut.resetArea.addrDec.io.ledSel)
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
          f"| ledEn: $ledEn" +
          f"| LED: $led%04X")
      }

      assert(dut.io.led.toLong == 1, "Expected LED to be '0001'")
    }

  private def boolToBit(b: Bool) = if (b.toBoolean) "1" else "0"
}