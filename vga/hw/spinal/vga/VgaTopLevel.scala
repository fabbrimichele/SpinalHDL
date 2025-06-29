package vga

import spinal.core._
import vga.HVSyncGeneratorConfig._

// Hardware definition
case class VgaTopLevel(clockInHz: Int) extends Component {
    val countInHz = 4
    val limit = clockInHz / countInHz

    val io = new Bundle {
        val clk = in Bool()
        val vgaRed = out Bits(4 bits)
        val vgaGreen = out Bits(4 bits)
        val vgaBlue = out Bits(4 bits)
        val vgaHSync = out Bool
        val vgaVSync = out Bool
        val led = out Bits(4 bits)
    }

    // DCM Clock
    val dcm = new Dcm25Mhz()
    dcm.io.clk32 := io.clk
    dcm.io.reset := False   

    val pixelClock = ClockDomain(
        clock = dcm.io.clk25,
        reset = ~dcm.io.locked
    )

    new ClockingArea(pixelClock) {
        // HV Sync Generator
        val syncGenerator = HVSyncGenerator(Vga640x480at60Hz)
        io.vgaHSync := syncGenerator.io.hSync
        io.vgaVSync := syncGenerator.io.vSync

        // RGB
        io.vgaRed := 0
        io.vgaGreen := 0
        io.vgaBlue := 0
    }

    val counter = Reg(UInt(25 bits)) init(0)
    val ledCounter = Reg(UInt(4 bits)) init(0)

    counter := counter + 1
    when(counter === U(limit)) {
        counter := 0
        ledCounter := ledCounter + 1
    }

    io.led := ledCounter.asBits

    // Remove io_ prefix
    noIoPrefix()
}

object VgaTopLevelVerilog extends App {
    Config.spinal.generateVerilog(VgaTopLevel(clockInHz = 32_000_000)).printPruned()
}

object VgaTopLevelVhdl extends App {
    Config.spinal.generateVhdl(VgaTopLevel(clockInHz = 32_000_000))
}
