package vga

import spinal.core._

// Hardware definition
case class VgaTopLevel(clockInHz: Int) extends Component {
    val countInHz = 4
    val limit = clockInHz / countInHz

    val io = new Bundle {
        val led = out Bits(4 bits)
    }

    val counter = Reg(UInt(25 bits)) init(0)
    val ledCounter = Reg(UInt(4 bits)) init(0)

    counter := counter + 1
    when(counter === U(limit)) {
        counter := 0
        ledCounter := ledCounter + 1
    }

    io.led := ledCounter.asBits

    // Remove io_ prefix
    noIoPrefix()
}

object VgaTopLevelVerilog extends App {
    Config.spinal.generateVerilog(VgaTopLevel(clockInHz = 32_000_000))
}

object VgaTopLevelVhdl extends App {
    Config.spinal.generateVhdl(VgaTopLevel(clockInHz = 32_000_000))
}
