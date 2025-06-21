TARGET = BlinkTopLevel

all: $(TARGET).v

$(TARGET).v: 
	sbt "runMain blink.BlinkTopLevelVerilog"

$(TARGET).vhdl:
	sbt "runMain blink.BlinkTopLevelVhdl"

clean:
	rm hw/gen/*.v
	rm hw/gen/*.vhd	