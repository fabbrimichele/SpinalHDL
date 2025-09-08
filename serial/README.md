# Serial
A serial project in SpinalHDL for the Papilio Duo dev board (Xilinx Spartan 6)
* The hardware description is into `hw/spinal/projectname/SerialTopLevel.scala`
* The testbench is into `hw/spinal/projectname/SerialTopLevelSim.scala`

## RS232 Adapter
https://www.waveshare.com/wiki/RS232_Board

## To configure the project
* Java 17 is required

## To build the project
```bash
make
```

## To run the simulation
```bash
sbt
runMain serial.SerialTopLevelSim
```


## To programm the bitstream to the Papilio DUO
```bash
/opt/GadgetFactory/papilio-loader
```

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


## IDE
Go with IntelliJ IDEA. I couldn’t get VS Code to work reliably.

## SpinalHDL Apb3UartCtrl memory map

| Addr   | Name                 | Bits                      | Description                                  |
|--------|----------------------|---------------------------|----------------------------------------------|
| 0x00   | DATA                 | [7:0]                     | RXDATA (read) / TXDATA (write)               |
|        |                      | [16]                      | RXVALID (1 = data available)                 |
| 0x04   | STATUS / CONTROL     | [0]                       | TX_IE  (TX interrupt enable)                 |
|        |                      | [1]                       | RX_IE  (RX interrupt enable)                 |
|        |                      | [8]                       | TX_IP  (TX interrupt pending)                |
|        |                      | [9]                       | RX_IP  (RX interrupt pending)                |
|        |                      | [15]                      | TXFULL (TX FIFO full)                        |
|        |                      | [23:16]                   | TX_FREE (TX FIFO free space)                 |
|        |                      | [31:24]                   | RX_OCC  (RX FIFO occupancy)                  |
| 0x08   | CLOCK DIVIDER        | [g.clockDividerWidth-1:0] | CLKDIV (baudrate divider)                    |
| 0x0C   | FRAME CONFIG         | [7:0]                     | DATALEN (number of data bits − 1)            |
|        |                      | [15:8]                    | PARITY (0=NONE, 1=EVEN, 2=ODD, …)            |
|        |                      | [17:16]                   | STOP (0=1 stop, 1=2 stops, …)                |
| 0x10   | ERROR / BREAK CTRL   | [0]                       | RX_ERR (sticky, cleared on read)             |
|        |                      | [1]                       | RX_OVF (overflow error, sticky)              |
|        |                      | [8]                       | RX_BRK (break active)                        |
|        |                      | [9]                       | BRK_D (break detected, sticky)               |
|        |                      | [10]                      | BRK_S (set TX break)                         |
|        |                      | [11]                      | BRK_C (clear TX break)                       |


## References
* [Papilio-DUO GitHub](https://github.com/GadgetFactory/Papilio-DUO)
* [Papilio-Loader GitHub](https://github.com/GadgetFactory/Papilio-Loader)
* [Programming Papilio-DUO](https://github.com/defano/digital-design/blob/master/docs/papilio-instructions.md)
* [Computing Shield Doc](https://oe7twj.at/index.php?title=FPGA/PapilioDuo#Computing_Shield)
* [Computing Shield Schematic](https://oe7twj.at/images/1/17/BPS6001_Classic_Computing_Shield.pdf)
* [Installing Xilinx ISE WebPACK 14.7](https://blog.rcook.org/blog/2019/papilio-duo-part-1/)