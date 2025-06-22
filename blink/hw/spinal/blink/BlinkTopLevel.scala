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

    val ledReg =  Reg(Bool()) init(False)
    val counter = Reg(UInt(25 bits)) init(0)

    counter := counter + 1
    when(counter === U(16_000_000)) {
        counter := 0
        ledReg := ~ ledReg
    }

    io.led1 := ledReg
    io.led2 := True
    io.led3 := False
    io.led4 := True

    // Remove io_ prefix
    noIoPrefix()
}

object BlinkTopLevelVerilog extends App {
    Config.spinal.generateVerilog(BlinkTopLevel())
}

object BlinkTopLevelVhdl extends App {
    Config.spinal.generateVhdl(BlinkTopLevel())
}
