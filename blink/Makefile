TARGET = Blink

all: $(TARGET).edif

bitstream: $(TARGET).edif
	hw/xilinx/build_bitstream.sh xc6slx9-tqg144-3 TARGET

$(TARGET).edif: $(TARGET)TopLevel.v
	yosys hw/yosys/synth.ys

$(TARGET)TopLevel.v: 
	sbt "runMain blink.BlinkTopLevelVerilog"

$(TARGET)TopLevel.vhdl:
	sbt "runMain blink.BlinkTopLevelVhdl"

clean:
	rm -f hw/gen/*.v
	rm -f hw/gen/*.vhd
	rm -f target/*.edif
	rm -rf _xmsgs
	rm -rf xlnx_auto_0_xdb