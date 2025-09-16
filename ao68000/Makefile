TARGET = Ao68000
TOPLEVEL = ao68000.Ao68000TopLevel
DEVICE = xc6slx9-tqg144-2
UCF = papilio_duo_computing_shield

all: $(TARGET)_routed.bit

$(TARGET)_routed.bit: gen/$(TARGET)TopLevel.vhdl gen/rom.hex
	hw/xilinx/build_bitstream.sh ${TARGET} ${DEVICE} ${UCF}

gen/$(TARGET)TopLevel.v: 
	sbt "runMain ${TOPLEVEL}Verilog"

gen/$(TARGET)TopLevel.vhdl:
	sbt "runMain ${TOPLEVEL}Vhdl"

gen/rom.hex:
	vasmm68k_mot -Fbin sw/asm/blinker.asm -o hw/gen/blinker.bin
	xxd -p -c 2 hw/gen/blinker.bin | awk '{print toupper($$0)}' > hw/gen/rom.hex

prog-fpga:
	echo "Programming FPGA"
	papilio-prog -v -f target/$(TARGET).bit

prog-flash:
	echo "Programming Flash"
	papilio-prog -v -s a -r -f target/$(TARGET).bit -b hw/papilio-loader/bscan_spi_xc6slx9.bit

clean:
	rm -f hw/gen/*.v
	rm -f hw/gen/*.vhd
	rm -f hw/gen/*.bin
	rm -f hw/gen/*.hex
	rm -f target/*.edf
	rm -rf _xmsgs
	rm -rf xlnx_auto_0_xdb
	rm -rf target

