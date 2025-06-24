TARGET = Blink
DEVICE = xc6slx9-tqg144-2
UCF = papilio_duo_computing_shield

all: $(TARGET)_routed.bit

$(TARGET)_routed.bit: $(TARGET)TopLevel.v
	hw/xilinx/build_bitstream.sh ${TARGET} ${DEVICE} ${UCF}

$(TARGET)TopLevel.v: 
	sbt "runMain blink.BlinkTopLevelVerilog"

$(TARGET)TopLevel.vhdl:
	sbt "runMain blink.BlinkTopLevelVhdl"

prog-fpga:
	echo "Programming FPGA"
	papilio-prog -v -f target/$(TARGET).bit

prog-flash:
	echo "Programming Flash"
	papilio-prog -v -f target/$(TARGET).bit -sa

clean:
	rm -f hw/gen/*.v
	rm -f hw/gen/*.vhd
	rm -f target/*.edf
	rm -rf _xmsgs
	rm -rf xlnx_auto_0_xdb
	rm -rf target

