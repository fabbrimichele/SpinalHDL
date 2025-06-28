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
Run the following command (or use the Makefile):
```bash
papilio-prog -v -f stream.bit
```

### To program the SPI Flash (permanent):
Run the following command (or use the Makefile):
```bash
papilio-prog -v -s a -r -f target/$(TARGET).bit -b hw/papilio-loader/bscan_spi_xc6slx9.bit
```
| Option        | Meaning                                                 |
|---------------|---------------------------------------------------------|
| -v            | verbose                                                 |
| -s a          | write to the flash                                      |
| -r            | reset FPGA after programming                            |
| -f bitfile    | bitstream to program                                    |
| -b bitfile    | bscan_spi bit file - required to program the flash      |


## To restore the Visual Studio Code Metal plugin
The Metal plugin from time to time has issues building the project, in order to fix it:
1. Delete the folders: `.metals` and `.bloop`
2. Exit Visual Studio Code
3. If it complains the `.bloop` is missing press: 
   1. `<CTRL>+<Shift>+<P>`
   2. Type: `Metals: Restart Build Server`
4. The build should start and a pop up will ask which builder to use
   1. Select `sbt`

## References
* [Papilio-DUO GitHub](https://github.com/GadgetFactory/Papilio-DUO)
* [Papilio-Loader GitHub](https://github.com/GadgetFactory/Papilio-Loader)
* [Programming Papilio-DUO](https://github.com/defano/digital-design/blob/master/docs/papilio-instructions.md)
* [Computing Shield](https://oe7twj.at/index.php?title=FPGA/PapilioDuo#Computing_Shield)
* [Installing Xilinx ISE WebPACK 14.7](https://blog.rcook.org/blog/2019/papilio-duo-part-1/)