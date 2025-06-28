package vga

import spinal.core._
import spinal.core.sim._

object VgaTopLevelSim extends App {
    Config.sim.compile(VgaTopLevel(clockInHz = 10)).doSim { dut =>
        // period is the clock period in ns
        dut.clockDomain.forkStimulus(period = 10)

        val period = 5
        dut.clockDomain.waitRisingEdge()
        assert(dut.io.led.toInt == 0)

        dut.clockDomain.waitRisingEdge(period + 1)
        assert(dut.io.led.toInt == 1)

        dut.clockDomain.waitRisingEdge(period + 1)
        assert(dut.io.led.toInt == 2)
    }
}
