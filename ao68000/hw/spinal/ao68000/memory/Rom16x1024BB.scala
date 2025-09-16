package ao68000.memory

import spinal.core._

import scala.language.postfixOps

class Rom16x1024BB extends BlackBox {
  val io = new Bundle {
    val clk      = in Bool()
    val addr     = in UInt(10 bits) // 1024 words
    val data_out = out Bits(16 bits)
  }

  // Map the clock domain
  mapClockDomain(clock = io.clk)

  setDefinitionName("rom_16x1024") // This tells SpinalHDL which Verilog module to instantiate
  addRTLPath("hw/vhdl/rom_16x1024.vhd") // Merge the file to the generated 'mergeRTL.v' file
  noIoPrefix() // Remove io_ prefix
}
