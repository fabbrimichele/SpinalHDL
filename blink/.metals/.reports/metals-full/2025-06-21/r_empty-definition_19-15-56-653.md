error id: file://<WORKSPACE>/hw/spinal/blink/BlinkTopLevelSim.scala:`<none>`.
file://<WORKSPACE>/hw/spinal/blink/BlinkTopLevelSim.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -spinal/core/MyTopLevel.
	 -spinal/core/MyTopLevel#
	 -spinal/core/MyTopLevel().
	 -spinal/core/sim/MyTopLevel.
	 -spinal/core/sim/MyTopLevel#
	 -spinal/core/sim/MyTopLevel().
	 -MyTopLevel.
	 -MyTopLevel#
	 -MyTopLevel().
	 -scala/Predef.MyTopLevel.
	 -scala/Predef.MyTopLevel#
	 -scala/Predef.MyTopLevel().
offset: 126
uri: file://<WORKSPACE>/hw/spinal/blink/BlinkTopLevelSim.scala
text:
```scala
package blink

import spinal.core._
import spinal.core.sim._

object BlinkTopLevelSim extends App {
  Config.sim.compile(MyTop@@Level()).doSim { dut =>
    // Fork a process to generate the reset and the clock on the dut
    dut.clockDomain.forkStimulus(period = 10)

    var modelState = 0
    for (idx <- 0 to 99) {
      // Drive the dut inputs with random values
      dut.io.cond0.randomize()
      dut.io.cond1.randomize()

      // Wait a rising edge on the clock
      dut.clockDomain.waitRisingEdge()

      // Check that the dut values match with the reference model ones
      val modelFlag = modelState == 0 || dut.io.cond1.toBoolean
      assert(dut.io.state.toInt == modelState)
      assert(dut.io.flag.toBoolean == modelFlag)

      // Update the reference model value
      if (dut.io.cond0.toBoolean) {
        modelState = (modelState + 1) & 0xff
      }
    }
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.