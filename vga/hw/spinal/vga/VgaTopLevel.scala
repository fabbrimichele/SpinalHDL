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
        val vgaConfig = Vga640x480at60Hz
        val syncGen = HVSyncGenerator(vgaConfig)
        io.vgaHSync := syncGen.io.hSync
        io.vgaVSync := syncGen.io.vSync

        // TODO: find examples about how to create modules, it would
        //       be great to move the pattern logic into a module.

        // Pattern
        /*
        val stripes = syncGen.io.displayOn && (syncGen.io.vPos(4) === True)
        val grid = syncGen.io.displayOn &&
            (((syncGen.io.vPos & U(7)) === U(0)) || ((syncGen.io.hPos & U(7)) === U(0)))
        
        io.vgaRed   := Mux(grid, B(15), B(0))
        io.vgaGreen := Mux(stripes, B(15), B(0))
        io.vgaBlue  := Mux(stripes, B(15), B(0))
        */

        // Ball
        val ball = new Bundle {
            val hPos = Reg(UInt(vgaConfig.hBitNum bits)) init(0)
            val vPos = Reg(UInt(vgaConfig.vBitNum bits)) init(0)
            val hMove = Reg(SInt(vgaConfig.hBitNum bits)) init(1)
            val vMove = Reg(SInt(vgaConfig.vBitNum bits)) init(2)
            val size = 10
        }

        when(syncGen.io.vSync.rise) {
            ball.hPos := (ball.hPos.asSInt + ball.hMove).asUInt
            ball.vPos := (ball.vPos.asSInt + ball.vMove).asUInt
        }

        val hCollide = ball.hPos >= (vgaConfig.hDisplay - ball.size)
        val vCollide = ball.vPos >= (vgaConfig.vDisplay - ball.size)

        when(hCollide) {
            // The motion inversion is delayed by one clock cycle,
            // so it needs to be triggered one pixel earlier.
            ball.hPos := (ball.hPos.asSInt - ball.hMove).asUInt
            ball.hMove := -ball.hMove 
        }

        when(vCollide) {
            // The motion inversion is delayed by one clock cycle,
            // so it needs to be triggered one pixel earlier.
            ball.vPos := (ball.vPos.asSInt - ball.vMove).asUInt
            ball.vMove := -ball.vMove 
        }

        val ballHGfx = (syncGen.io.hPos - ball.hPos) < ball.size
        val ballVGfx = (syncGen.io.vPos - ball.vPos) < ball.size
        val ballGfx = ballHGfx && ballVGfx


        io.vgaRed   := Mux(ballGfx, B(15), B(0))
        io.vgaGreen := Mux(ballGfx, B(15), B(0))
        io.vgaBlue  := Mux(False, B(15), B(0))
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
