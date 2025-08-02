package serial

import spinal.core._
import spinal.lib._
import spinal.lib.com.uart._

// Hardware definition
case class SerialTopLevel() extends Component {
    val io = new Bundle {
        val send = in Bool()   // Trigger to send
        val uart = master(Uart()) // Expose UART pins (txd, rxd), must be defined in the ucf
    }

    // UART config: 38400 baud, 8N1, assuming 32 MHz clock
    val uartCtrl = new UartCtrl(
        UartCtrlGenerics(
            dataWidthMax = 8,
            clockDividerWidth = 20, // 12 should be enough, to be tested
            preSamplingSize = 1,
            samplingSize = 5,
            postSamplingSize = 2
        )
    )

    // connect all matching signals between the two bundles
    io.uart <> uartCtrl.io.uart

    // UART runtime configuration
    uartCtrl.io.config.clockDivider := 833  // 32_000_000 / 38400
    uartCtrl.io.config.frame.dataLength := 7  // 8 bits (0 to 7)
    uartCtrl.io.config.frame.stop := UartStopType.ONE
    uartCtrl.io.config.frame.parity := UartParityType.NONE
    uartCtrl.io.writeBreak := False

    // uartCtrl.io.write.valid := io.send
    uartCtrl.io.write.valid := True
    uartCtrl.io.write.payload := B("8'x41") // ASCII 'A'

    // Remove io_ prefix
    noIoPrefix()
}

object SerialTopLevelVerilog extends App {
    Config.spinal.generateVerilog(SerialTopLevel()).printPruned()
}

object SerialTopLevelVhdl extends App {
    Config.spinal.generateVhdl(SerialTopLevel())
}
