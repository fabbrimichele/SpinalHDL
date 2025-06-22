# Blink
A Blink project in SpinalHDL for the Papilio Duo dev board (Xilinx Spartan 6)
* The hardware description is into `hw/spinal/projectname/BlinkTopLevel.scala`
* The testbench is into `hw/spinal/projectname/BlinkTopLevelSim.scala`

## To configure the project
* Java 17 is required

## To run the project
```
make
```

## To programm the bitstream to the Papilio DUO
/opt/GadgetFactory/papilio-loader


## Papilio prog
`papilio-prog` is the programmer for the Papilio DUO board.
For 64 bit OS needs to be recompiled:
* clone the repository: `git clone git@github.com:GadgetFactory/Papilio-Loader.git`
* enter the directory: `cd Papilio-Loader/papilio-prog`
* run: `./configure`
* configure in the Makefile: `CXXFLAGS` with `-std=c++11` in addition to the existing parameters
* run  `make`
* copy `papilio-prog` to a bin path

### To check that the FPGA is found:
```bash
papilio-prog -j
```
It should return something like:
```
Using built-in device list
JTAG chainpos: 0 Device IDCODE = 0x24001093	Desc: XC6SLX9
```

### To program the FPGA (temporary):
```bash
papilio-prog -v -f stream.bit
```

### To program the SPI Flash (permanent):
```bash
papilio-prog
```


## References
* [Papilio-DUO GitHub Repository](https://github.com/GadgetFactory/Papilio-DUO)
* [Programming Papilio-DUO](https://github.com/defano/digital-design/blob/master/docs/papilio-instructions.md)