package vga

import spinal.core._
import vga.HVSyncGeneratorConfig._

// Hardware definition
case class VgaTopLevel(clockInHz: Int) extends Component {
    val io = new Bundle {
        val clk      = in Bool()
        val vgaRed   = out Bits(4 bits)
        val vgaGreen = out Bits(4 bits)
        val vgaBlue  = out Bits(4 bits)
        val vgaHSync = out Bool
        val vgaVSync = out Bool
        val led      = out Bits(4 bits)
    }

    // Instantiate the 25MHz clock generator (DCM)
    val dcm = new Dcm25Mhz()
    dcm.io.clk32 := io.clk
    dcm.io.reset := False   

    val pixelClock = ClockDomain(
        clock = dcm.io.clk25,
        reset = ~dcm.io.locked
    )

    // Clock domain area for VGA timing logic
    new ClockingArea(pixelClock) {
        // HV Sync Generator
        val syncGen = HVSyncGenerator(Vga640x480at60Hz)
        io.vgaHSync := syncGen.io.hSync
        io.vgaVSync := syncGen.io.vSync

        // RGB
        val stripes = syncGen.io.displayOn && (syncGen.io.vPos(4) === True)
        val grid = syncGen.io.displayOn &&
            (((syncGen.io.vPos & U(7)) === U(0)) || ((syncGen.io.hPos & U(7)) === U(0)))
        
        io.vgaRed   := Mux(grid, B(15), B(0))
        io.vgaGreen := Mux(stripes, B(15), B(0))
        io.vgaBlue  := Mux(stripes, B(15), B(0))
    }

    io.led := B"0101"

    // Remove io_ prefix
    noIoPrefix()
}

object VgaTopLevelVerilog extends App {
    Config.spinal.generateVerilog(VgaTopLevel(clockInHz = 32_000_000)).printPruned()
}

object VgaTopLevelVhdl extends App {
    Config.spinal.generateVhdl(VgaTopLevel(clockInHz = 32_000_000))
}
