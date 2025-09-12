package ao68000

import spinal.core._
import spinal.core.sim._
import spinal.lib.com.uart._

import scala.util.Random

object Ao68000TopLevelSim extends App {
    Config.sim
        .withWave
        .compile(Ao68000TopLevel())
        .doSim { dut =>
            println("Simulation done.")
        }
}