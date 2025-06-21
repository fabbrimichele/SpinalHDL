error id: file://<WORKSPACE>/hw/spinal/blink/BlinkTopLevelFormal.scala:`<none>`.
file://<WORKSPACE>/hw/spinal/blink/BlinkTopLevelFormal.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 386
uri: file://<WORKSPACE>/hw/spinal/blink/BlinkTopLevelFormal.scala
text:
```scala
package blink

import spinal.core._
import spinal.core.formal._

// You need SymbiYosys to be installed.
// See https://spinalhdl.github.io/SpinalDoc-RTD/master/SpinalHDL/Formal%20verification/index.html#installing-requirements
object BlinkTopLevelFormal extends App {
  FormalConfig
    .withBMC(10)
    .doVerify(new Component {
      val dut = FormalDut(MyTopLevel())

      // Ensur@@e the formal test start with a reset
      assumeInitial(clockDomain.isResetActive)

      // Provide some stimulus
      anyseq(dut.io.cond0)
      anyseq(dut.io.cond1)

      // Check the state initial value and increment
      assert(dut.io.state === past(dut.io.state + U(dut.io.cond0)).init(0))
    })
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.