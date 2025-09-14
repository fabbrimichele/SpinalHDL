package ao68000.io

import spinal.core._

class SwitchLedBB extends BlackBox {
  // Define IO of the module
  val io = new Bundle {
    val switchDown = in Bool()
    val led0       = out Bool()
  }

  setDefinitionName("switch_led") // This tells SpinalHDL which Verilog module to instantiate
  addRTLPath("hw/verilog/switch_led.v") // Merge the file to the generated 'mergeRTL.v' file
  noIoPrefix() // Remove io_ prefix
}
