package vga

import spinal.core._

// Hardware definition
case class HVSyncGenerator(config: HVSyncGeneratorConfig) extends Component {
    import config._

    val io = new Bundle {
        val displayOn = out Bool
        val hSync = out Bool
        val vSync = out Bool
        val hPos = out UInt(hBitNum bits)
        val vPos = out UInt(vBitNum bits)
    }   

    val hMaxxed = (io.hPos === hMax) | clockDomain.isResetActive
    val vMaxxed = (io.vPos === vMax) | clockDomain.isResetActive
    val hPos = Reg(UInt(hBitNum bits)) init(0)
    val vPos = Reg(UInt(vBitNum bits)) init(0)

    io.hPos := hPos
    io.vPos := vPos
    io.displayOn := (hPos <= hDisplay) && (vPos <= vDisplay);
    io.hSync := hPos >= hSyncStart && hPos<= hSyncEnd
    io.vSync := vPos >= vSyncStart && vPos<= vSyncEnd

    // Horizontal position counter
    when(hMaxxed) {
        hPos := 0;
    } otherwise {
        hPos := hPos + 1;
    }
   
    // Vertical position counter
    when(hMaxxed) {
        when(vMaxxed) {
            vPos := 0;
        } otherwise {
            vPos := vPos + 1
        }
    }
}

case class HVSyncGeneratorConfig(
    // Horizontal
    val hDisplay: Int,  // horizontal display with
    val hBack: Int,     // horizontal back porch
    val hFront: Int,    // horizontal front porch
    val hSync: Int,     // horizontal sync width
    // Vertical
    val vDisplay: Int,  // vertical display height
    val vTop: Int,      // vertical top border
    val vBottom: Int,   // vertical bottom border
    val vSync: Int,     // vertical synch height    
) {
    // Horizontal
    val hSyncStart    = hDisplay + hFront;
    val hSyncEnd      = hDisplay + hFront + hSync - 1;
    val hMax          = hDisplay + hBack + hFront + hSync - 1;
    val hBitNum       = log2Up(hMax)
    // Vertical    
    val vSyncStart    = vDisplay + vBottom;
    val vSyncEnd      = vDisplay + vBottom + vSync - 1;
    val vMax          = vDisplay + vTop + vBottom + vSync - 1;
    val vBitNum       = log2Up(vMax)
}

object HVSyncGeneratorConfig {
    val Vga640x480at60Hz = HVSyncGeneratorConfig(
        hDisplay   = 640,
        hBack      = 48, 
        hFront     = 16,
        hSync      = 96, 
        vDisplay   = 480,
        vTop       = 33, 
        vBottom    = 10,
        vSync      = 2,    
    ) 
    val Vga640x400at70Hz = HVSyncGeneratorConfig(
        hDisplay   = 640,
        hBack      = 48, 
        hFront     = 16,
        hSync      = 96, 
        vDisplay   = 400,
        vTop       = 35, 
        vBottom    = 12,
        vSync      = 2,    
    ) 
}
