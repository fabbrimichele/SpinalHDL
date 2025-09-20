package ao68000

import ao68000.core._
import ao68000.io._
import ao68000.memory._
import spinal.core._

import scala.language.postfixOps

//noinspection TypeAnnotation
// Hardware definition
case class Ao68000TopLevel() extends Component {
  val io = new Bundle {
    val reset = in Bool()
    val led = out Bits(4 bits)
/*
    val switchLeft = in Bool()
    val switchDown = in Bool()
    val switchUp = in Bool()
    val switchRight = in Bool()
*/
  }

  val debounce = new Debounce
  debounce.io.button := io.reset

  //val resetArea = new ResetArea(debounce.io.result, cumulative = false) { // TODO: the debounce is not working
  val resetArea = new ResetArea(io.reset, cumulative = false) {
    // CPU
    val cpu = Cpu68000()

    // ROM
    val rom = new Rom16x1024BB()
    rom.io.addr := cpu.io.bus.addr(10 downto 1)

    // LEDs
    val ledReg = Reg(Bits(4 bits)) init(0)
    io.led := ledReg

    // Address decoding
    val hitRom = !cpu.io.bus.as && cpu.io.bus.rw &&
      (cpu.io.bus.addr < U(2048, 32 bits)) // ROM: 1024 words x 2 bytes
    val hitLed = !cpu.io.bus.as && !cpu.io.bus.rw &&
      (cpu.io.bus.addr === U(0x00FF0000, 32 bits))

    // DTACK combines all slave hits
    //cpu.io.bus.dtack := !(!hitRom || !hitLed)
    cpu.io.bus.dtack := False

    // Data routing: CPU reads from ROM
    //cpu.io.bus.dataIn := Mux(hitRom, rom.io.data_out, B(0,16 bits))
    cpu.io.bus.dataIn := rom.io.data_out

    // CPU writes to LED register
    when(hitLed && !cpu.io.bus.lds){
      ledReg := cpu.io.bus.dataOut(3 downto 0)
    }

    // Future devices can be added by defining more `hitDevice` signals
    // and expanding the data mux & dtack OR chain
  }

  // Remove io_ prefix
  noIoPrefix()
}

object Ao68000TopLevelVhdl extends App {
  val report = Config.spinal.generateVhdl(Ao68000TopLevel())
  report.mergeRTLSource("mergeRTL") // Merge all rtl sources into mergeRTL.vhd and mergeRTL.v files
  report.printPruned()
}
