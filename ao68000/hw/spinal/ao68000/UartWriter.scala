package serial

import spinal.core._
import spinal.lib.bus.amba3.apb.Apb3
import spinal.lib.com.uart.Apb3UartCtrl
import spinal.lib.fsm._
import spinal.lib.master

//noinspection TypeAnnotation
case class UartWriter() extends Component {
  val io = new Bundle {
    val start = in Bool()       // Trigger to send
    val apb = master(Apb3(Apb3UartCtrl.getApb3Config))
    val led0 = out Bool()
    val led1 = out Bool()
    val led2 = out Bool()
  }

  // Default assignments (avoid latches)
  io.apb.PSEL    := False.asBits
  io.apb.PENABLE := False
  io.apb.PWRITE  := False
  io.apb.PADDR   := 0x0
  io.apb.PWDATA  := 0x0
  io.led0        := False
  io.led1        := False
  io.led2        := False

  val fsm = new StateMachine {
    val idle: State = new State with EntryPoint {
      whenIsActive {
        io.led0 := True
        goto(write)
      }
    }

    val write: State = new State { // Wait one second
      whenIsActive {
        // Drive APB3 signals
        io.led1 := True
        apbWrite(0x00, 0x41) // 0x00 = write FIFO, 0x41 = 'A'
        goto(done)
      }
    }

    val done: State = new StateDelay(cyclesCount = 32_000_000) {
      whenIsActive {
        io.led2 := True
      }
      whenCompleted {
        goto(idle)
      }
    }
  }

    // Helper to write to APB3
  def apbWrite(addr: Int, data: Int): Unit = {
    io.apb.PSEL    := True.asBits
    io.apb.PENABLE := True
    io.apb.PWRITE  := True
    io.apb.PADDR   := addr
    io.apb.PWDATA  := data
  }
}
