package blink

import spinal.core._

// Hardware definition
case class BlinkTopLevel() extends Component {
    val io = new Bundle {
        val led1 = out Bool()
        val led2 = out Bool()
        val led3 = out Bool()
        val led4 = out Bool()
    }

    val counter = Reg(UInt(25 bits)) init(0)
    val ledCounter = Reg(UInt(4 bits)) init(0)

    counter := counter + 1
    when(counter === U(16_000_000)) {
        counter := 0
        ledCounter := ledCounter + 1
    }

    io.led1 := ledCounter(0)
    io.led2 := ledCounter(1)
    io.led3 := ledCounter(2)
    io.led4 := ledCounter(3)

    // Remove io_ prefix
    noIoPrefix()
}

object BlinkTopLevelVerilog extends App {
    Config.spinal.generateVerilog(BlinkTopLevel())
}

object BlinkTopLevelVhdl extends App {
    Config.spinal.generateVhdl(BlinkTopLevel())
}
