TARGET = Blink
DEVICE = xc6slx9-2tqg144
UCF = papilio_duo_computing_shield

all: $(TARGET)_routed.bit

$(TARGET)_routed.bit: $(TARGET).edif
	hw/xilinx/build_bitstream.sh ${TARGET} ${DEVICE} ${UCF}

$(TARGET).edif: $(TARGET)TopLevel.v
	yosys hw/yosys/synth.ys

$(TARGET)TopLevel.v: 
	sbt "runMain blink.BlinkTopLevelVerilog"

$(TARGET)TopLevel.vhdl:
	sbt "runMain blink.BlinkTopLevelVhdl"

prog-fpga:
	papilio-prog -v -f target/$(TARGET)_routed.bit

prog-flash:
	echo TODO

clean:
	rm -f hw/gen/*.v
	rm -f hw/gen/*.vhd
	rm -f target/*.edf
	rm -rf _xmsgs
	rm -rf xlnx_auto_0_xdb
	rm -rf target

