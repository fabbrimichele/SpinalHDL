package blink

import spinal.core._
import spinal.core.sim._

object BlinkTopLevelSim extends App {
    Config.sim.compile(BlinkTopLevel()).doSim { dut =>
        // TODO: what is period used for?
        // TODO: how do I make the test faster?
        dut.clockDomain.forkStimulus(period = 10)

        // TODO: transform led1, led2, ... to led(4 bits)?
        val period = 16_000_000
        dut.clockDomain.waitRisingEdge()
        assert(dut.io.led1.toBoolean == false)
        assert(dut.io.led2.toBoolean == false)
        assert(dut.io.led3.toBoolean == false)
        assert(dut.io.led4.toBoolean == false)

        dut.clockDomain.waitRisingEdge(period + 1)
        assert(dut.io.led1.toBoolean == true)
        assert(dut.io.led1.toBoolean == false)  // TODO: why isn't this false?
        assert(dut.io.led1.toBoolean == false)
        assert(dut.io.led1.toBoolean == false)

        dut.clockDomain.waitRisingEdge(period + 1)
        assert(dut.io.led1.toBoolean == false)
        assert(dut.io.led1.toBoolean == true)
        assert(dut.io.led1.toBoolean == false)
        assert(dut.io.led1.toBoolean == false)
    }
}
