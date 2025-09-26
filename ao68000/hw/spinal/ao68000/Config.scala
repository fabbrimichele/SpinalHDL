package ao68000

import spinal.core._
import spinal.core.sim._
import spinal.sim.GhdlFlags

object Config {
  def spinal = SpinalConfig(
    targetDirectory = "hw/gen",
    defaultConfigForClockDomains = ClockDomainConfig(
      resetActiveLevel = HIGH
    ),
    defaultClockDomainFrequency = FixedFrequency(32 MHz),
    onlyStdLogicVectorAtTopLevelIo = false
  )

  val flagExplicit = "-fexplicit" // This is required to make GHDL compile TG68.vhd
  def sim = SimConfig
    .withConfig(spinal)
    .withFstWave
    .withGHDL(GhdlFlags().withElaborationFlags(flagExplicit, "--warn-no-specs"))
    .addSimulatorFlag(flagExplicit) // Something is off, this is required, but it shouldn't
    .addRtl("/home/michele/spinalHDL/ao68000/hw/vhdl/TG68.vhd")
    .addRtl("/home/michele/spinalHDL/ao68000/hw/vhdl/TG68_fast.vhd")
    .addRtl("/home/michele/spinalHDL/ao68000/hw/vhdl/rom_16x1024.vhd")
}
