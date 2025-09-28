package ao68000.core

import ao68000.Config
import spinal.core.sim.{SimBoolPimper, SimClockDomainHandlePimper, SimEquivBitVectorBigIntPimper}
import spinal.core._

import scala.language.postfixOps

object AddressDecoderSim extends App {
  Config.sim
    .compile {
      val dut = AddressDecoder()
      dut
    }
    .doSim { dut =>
      dut.io.addr #= 0x00000000
      dut.io.as #= true // disabled
      dut.io.rw #= true // read

      // Clock stimulus for 32 MHz
      // It should also trigger the reset, but it seems it doesn't work.
      dut.clockDomain.forkStimulus(31.25 ns) // period in ns

      // ROM enabled on read
      dut.io.addr #= 0x00000000
      dut.io.as #= false
      dut.io.rw #= true // read
      sim.sleep(1)  // let the simulator propagate events
      assert(dut.io.romSel.toBoolean, "Expected romEn high on write to 0x00000000")
      assert(!dut.io.ramSel.toBoolean, "Expected ramEn low on write to 0x00000000")
      assert(!dut.io.ledSel.toBoolean, "Expected ledEn low on write to 0x00000000")

      dut.io.addr #= 0x000007FF
      dut.io.as #= false
      dut.io.rw #= true // read
      sim.sleep(1)  // let the simulator propagate events
      assert(dut.io.romSel.toBoolean, "Expected romEn high on write to 0x000007FF")
      assert(!dut.io.ramSel.toBoolean, "Expected ramEn low on write to 0x000007FF")
      assert(!dut.io.ledSel.toBoolean, "Expected ledEn low on write to 0x000007FF")

      // RAM enabled on read
      dut.io.addr #= 0x00000800
      dut.io.as #= false
      dut.io.rw #= true // read
      sim.sleep(1)  // let the simulator propagate events
      assert(!dut.io.romSel.toBoolean, "Expected romEn low on read to 0x00000800")
      assert(dut.io.ramSel.toBoolean, "Expected ramEn high on read to 0x00000800")
      assert(!dut.io.ledSel.toBoolean, "Expected ledEn low on read to 0x00000800")

      // RAM enabled on write
      dut.io.addr #= 0x00000FFF
      dut.io.as #= false
      dut.io.rw #= false // write
      sim.sleep(1)  // let the simulator propagate events
      assert(!dut.io.romSel.toBoolean, "Expected romEn low on write to 0x00000FFF")
      assert(dut.io.ramSel.toBoolean, "Expected ramEn high on write to 0x00000FFF")
      assert(!dut.io.ledSel.toBoolean, "Expected ledEn low on write to 0x00000FFF")

      // LED enabled on write
      dut.io.addr #= 0x00FF0000
      dut.io.as #= false
      dut.io.rw #= false // write
      sim.sleep(1)  // let the simulator propagate events
      assert(!dut.io.romSel.toBoolean, "Expected romEn low on write to 0x00FF0000")
      assert(!dut.io.ramSel.toBoolean, "Expected ramEn low on write to 0x00FF0000")
      assert(dut.io.ledSel.toBoolean, "Expected ledEn high on write to 0x00FF0000")

      // Key enabled on read
      dut.io.addr #= 0x00AA0000
      dut.io.as #= false
      dut.io.rw #= true // read
      sim.sleep(1)  // let the simulator propagate events
      assert(!dut.io.romSel.toBoolean, "Expected romEn low on write to 0x00FF0002")
      assert(!dut.io.ramSel.toBoolean, "Expected ramEn low on write to 0x00FF0002")
      assert(!dut.io.ledSel.toBoolean, "Expected ledEn low on write to 0x00FF0002")
      assert(dut.io.keySel.toBoolean, "Expected keyEn high on write to 0x00FF0002")

      // ROM, RAM and LED disabled out of address range
      dut.io.addr #= 0x00BB0000
      dut.io.as #= false
      dut.io.rw #= false // write
      sim.sleep(1)  // let the simulator propagate events
      assert(!dut.io.romSel.toBoolean, "Expected romEn low on write to 0x00FF0000")
      assert(!dut.io.ramSel.toBoolean, "Expected ramEn low on write to 0x00FF0000")
      assert(!dut.io.ledSel.toBoolean, "Expected ledEn low on write to 0x00FF0000")

      // ROM, RAM and LED disable when as disabled
      dut.io.addr #= 0x00FF0000
      dut.io.as #= true // disabled
      dut.io.rw #= false // write
      sim.sleep(1)  // let the simulator propagate events
      assert(!dut.io.romSel.toBoolean, "Expected romEn low on write to 0x00FF0000")
      assert(!dut.io.ramSel.toBoolean, "Expected ramEn low on write to 0x00FF0000")
      assert(!dut.io.ledSel.toBoolean, "Expected ledEn low on write to 0x00FF0000")
    }
}
