package serial

import spinal.core._
import spinal.lib.com.uart._
import spinal.lib.master

class SerialEchoTopLevel() extends Component {
  val io = new Bundle {
    val switchDown = in Bool()   // Trigger to send
    val led0 = out Bool()
    val uart = master(Uart()) // Expose UART pins (txd, rxd), must be defined in the ucf
  }


  val uartCtrl = UartCtrl(
    config = UartCtrlInitConfig(
      baudrate = 9600,
      dataLength = 7,
      parity = UartParityType.NONE,
      stop = UartStopType.ONE
    )
  )

  io.uart <> uartCtrl.io.uart

  // Echo
  // GTKTerminal can be used to see the output
  // <-< convert the Flow to a Stream by adding a one-element
  // buffer that remembers the data until write.ready is true
  uartCtrl.io.write <-< uartCtrl.io.read
  io.led0 := uartCtrl.io.read.valid


  // Remove io_ prefix
  noIoPrefix()
}
