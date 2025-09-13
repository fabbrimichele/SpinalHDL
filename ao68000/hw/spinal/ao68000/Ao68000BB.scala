package ao68000

import spinal.core._

import scala.annotation.unused
import scala.language.postfixOps

class Ao68000BB extends BlackBox {
  // Define IO
  val io = new Bundle {
    // Clock domain will be mapped to CLK_I
    val CLK_I    = in Bool()
    val reset_n  = in Bool()

    //****************** WISHBONE
    val CYC_O    = out Bool()
    val ADR_O    = out UInt(30 bits)  // [31:2] in Verilog
    val DAT_O    = out UInt(32 bits)
    val DAT_I    = in  UInt(32 bits)
    val SEL_O    = out Bits(4 bits)
    val STB_O    = out Bool()
    val WE_O     = out Bool()
    val ACK_I    = in  Bool()
    val ERR_I    = in  Bool()
    val RTY_I    = in  Bool()

    // TAG signals
    val SGL_O    = out Bool()
    val BLK_O    = out Bool()
    val RMW_O    = out Bool()
    val CTI_O    = out Bits(3 bits)
    val BTE_O    = out Bits(2 bits)
    val fc_o     = out Bits(3 bits)

    // Interrupts and control
    val ipl_i    = in Bits(3 bits)
    val reset_o  = out Bool()
    val blocked  = out Bool()
  }

  // Map the clock domain
  mapClockDomain(clock = io.CLK_I)

  setDefinitionName("ao68000") // This tells SpinalHDL which Verilog module to instantiate
  addRTLPath("hw/verilog/ao68000.v") // Merge the file to the generated 'mergeRTL.v' file
  noIoPrefix() // Remove io_ prefix
}
