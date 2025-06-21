error id: file://<WORKSPACE>/hw/spinal/blink/BlinkTopLevel.scala:`<none>`.
file://<WORKSPACE>/hw/spinal/blink/BlinkTopLevel.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -spinal/core/ledInternal.
	 -spinal/core/ledInternal#
	 -spinal/core/ledInternal().
	 -ledInternal.
	 -ledInternal#
	 -ledInternal().
	 -scala/Predef.ledInternal.
	 -scala/Predef.ledInternal#
	 -scala/Predef.ledInternal().
offset: 325
uri: file://<WORKSPACE>/hw/spinal/blink/BlinkTopLevel.scala
text:
```scala
package blink

import spinal.core._

// Hardware definition
case class BlinkTopLevel() extends Component {
    val io = new Bundle {
        val clk = in Bool()
        val led = out Bool()
    }

  // Declare ledInternal as a register (not just a wire)
  val ledInternal = Reg(Bool())
  ledInternal := io.clk
  io.led := led@@Internal
}

object BlinkTopLevelVerilog extends App {
    Config.spinal.generateVerilog(BlinkTopLevel())
}

object BlinkTopLevelVhdl extends App {
    Config.spinal.generateVhdl(BlinkTopLevel())
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.