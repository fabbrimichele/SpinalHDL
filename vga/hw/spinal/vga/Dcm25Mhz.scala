package vga

import spinal.core._

class Dcm25Mhz extends BlackBox {
    val io = new Bundle {
        val clk32  = in Bool()
        val reset  = in Bool()
        val clk25  = out Bool()
        val locked = out Bool()
    }

    noIoPrefix()
    addRTLPath("dcm25mhz.v")
}
