package blink

import spinal.core._

// Hardware definition
case class BlinkTopLevel(clockInHz: Int) extends Component {
    val countInHz = 2
    val limit = clockInHz / countInHz

    val io = new Bundle {
        val leds = out Bits(4 bits)
    }

    val counter = Reg(UInt(25 bits)) init(0)
    val ledCounter = Reg(UInt(4 bits)) init(0)

    counter := counter + 1
    when(counter === U(limit)) {
        counter := 0
        ledCounter := ledCounter + 1
    }

    io.leds := ledCounter.asBits

    // Remove io_ prefix
    noIoPrefix()
}

object BlinkTopLevelVerilog extends App {
    Config.spinal.generateVerilog(BlinkTopLevel(clockInHz = 32_000_000))
}

object BlinkTopLevelVhdl extends App {
    Config.spinal.generateVhdl(BlinkTopLevel(clockInHz = 32_000_000))
}
