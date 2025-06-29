package vga

import spinal.core._

// Hardware definition
case class HVSyncGenerator(config: HVSyncGeneratorConfig) extends Component {
    val io = new Bundle {
        val displayOn = out Bool
        val hSync = out Bool
        val vSync = out Bool
        // Note config height and width can't be greater than vPos and hPos
        val hPos = out UInt(11 bits)
        val vPos = out UInt(11 bits)
    }
    
    import config._

    val hMaxxed = (io.hPos === hMax) | clockDomain.isResetActive
    val vMaxxed = (io.vPos === vMax) | clockDomain.isResetActive
    val hPos = Reg(UInt(11 bits)) init(0)
    val vPos = Reg(UInt(11 bits)) init(0)

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
    // Vertical    
    val vSyncStart    = vDisplay + vBottom;
    val vSyncEnd      = vDisplay + vBottom + vSync - 1;
    val vMax          = vDisplay + vTop + vBottom + vSync - 1;
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
}
