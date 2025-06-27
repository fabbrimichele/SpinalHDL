package blink

import spinal.core._
import spinal.core.sim._

object BlinkTopLevelSim extends App {
    Config.sim.compile(BlinkTopLevel(clockInHz = 10)).doSim { dut =>
        // period is the clock period in ns
        dut.clockDomain.forkStimulus(period = 10)

        val period = 5
        dut.clockDomain.waitRisingEdge()
        assert(dut.io.leds.toInt == 0)

        dut.clockDomain.waitRisingEdge(period + 1)
        assert(dut.io.leds.toInt == 1)

        dut.clockDomain.waitRisingEdge(period + 1)
        assert(dut.io.leds.toInt == 2)
    }
}
