package serial

import spinal.core._
import spinal.core.sim._
import spinal.lib.com.uart._

import scala.util.Random

object SerialTopLevelSim extends App {
    Config.sim
        .withWave
        .compile(SerialTopLevel())
        .doSim { dut =>
            // Initialize TX signal
            dut.io.uart.rxd #= true // Idle high
            dut.io.send #= false

            // Create the clock stimulus
            dut.clockDomain.forkStimulus(period = 31) // 32 MHz (31.25 ns period)

            // Wait a bit and trigger the send signal
            sleep(100)
            dut.io.send #= true
            sleep(10)
            dut.io.send #= false

            // Wait long enough to transmit one full UART frame
            sleep(50000000)

            println("Simulation done.")
        }
}