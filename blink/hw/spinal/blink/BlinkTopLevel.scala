package blink

import spinal.core._

// Hardware definition
case class BlinkTopLevel() extends Component {
    val io = new Bundle {
        val clk = in Bool()
        val led = out Bool()
    }

    // Use internal clock domain from io.clk
    val clkDomain = ClockDomain(
        clock = io.clk,
        config = ClockDomainConfig(resetKind = BOOT)
    )

    val area = new ClockingArea(clkDomain) {
        val counter = Reg(UInt(25 bits)) init(0)  // Enough bits for 16 million
        val ledReg  = Reg(Bool()) init(False)

        counter := counter + 1
        when(counter === U(16_000_000 - 1)) {
            counter := 0
            ledReg := ~ledReg
        }

        io.led := ledReg
    }
}

object BlinkTopLevelVerilog extends App {
    Config.spinal.generateVerilog(BlinkTopLevel())
}

object BlinkTopLevelVhdl extends App {
    Config.spinal.generateVhdl(BlinkTopLevel())
}
