package serial

import spinal.core._
import spinal.lib._
import spinal.lib.com.uart._
import spinal.lib.misc._

// Hardware definition
case class SerialTopLevel() extends Component {
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

    /* 
    io.led0 := io.switchDown
     // Write 'A's when switchDown is pressed     
-    uartCtrl.io.write.valid := io.switchDown
-    uartCtrl.io.write.payload := B('A'.toInt, 8 bits) // ASCII 'A'    
    */


    // Send a sequence of "\nA", each every second
    /*
    val write = Stream(Fragment(Bits(8 bits)))
    write.valid := CounterFreeRun(16_000_000).willOverflow
    write.fragment := B('A'.toInt, 8 bits) 
    write.last := True
    write.stage().insertHeader('\n').toStreamOfFragment >> uartCtrl.io.write
    */
    // Echo
    // For VS Code Serial Monitor plugin, toggle the terminal mode.
    // It might be needed to toggle it more than once to make it work.
    // GTKTerminal can also be used.
    // <-< convert the Flow to a Stream by adding a one-element 
    // buffer that remembers the data until write.ready is true
    uartCtrl.io.write <-< uartCtrl.io.read
    io.led0 := uartCtrl.io.read.valid
   
    
    // Remove io_ prefix
    noIoPrefix()
}

object SerialTopLevelVerilog extends App {
    SpinalConfig(
        defaultClockDomainFrequency = FixedFrequency(32 MHz),
        targetDirectory = "hw/gen"
    ).generateVerilog(SerialTopLevel()).printPruned()
}

object SerialTopLevelVhdl extends App {
    Config.spinal.generateVhdl(SerialTopLevel())
}
