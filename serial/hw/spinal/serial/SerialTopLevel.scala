package serial

import spinal.core._
import spinal.lib._
import spinal.lib.com.uart._

//noinspection TypeAnnotation
// Hardware definition
case class SerialTopLevel() extends Component {
    val io = new Bundle {
        val switchDown = in Bool()   // Trigger to send
        val led0 = out Bool()
        val led1 = out Bool()
        val led2 = out Bool()
        val uart = master(Uart()) // Expose UART pins (txd, rxd), must be defined in the ucf
    }

    val uartCtrl = new Apb3UartCtrl(
      UartCtrlMemoryMappedConfig(
        baudrate = 9600,
        txFifoDepth = 1,
        rxFifoDepth = 1,
      )
    )
    uartCtrl.io.uart <> io.uart

    val uartWriter = UartWriter()
    // Connect master to slave
    uartWriter.io.start <> io.switchDown
    uartWriter.io.led0 <> io.led0
    uartWriter.io.led1 <> io.led1
    uartWriter.io.led2 <> io.led2
    uartCtrl.io.apb <> uartWriter.io.apb


    // Remove io_ prefix
    noIoPrefix()
}

object SerialTopLevelVerilog extends App {
  Config.spinal
    .generateVerilog(SerialTopLevel())
    .printPruned()
}

object SerialTopLevelVhdl extends App {
    Config.spinal.generateVhdl(SerialTopLevel())
}
