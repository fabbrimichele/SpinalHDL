# Blink
A Blink project in SpinalHDL for the Papilio Duo dev board (Xilinx Spartan 6)

## To run the project
```sh
// To generate the Verilog from the example
sbt "runMain projectname.MyTopLevelVerilog"

// To generate the VHDL from the example
sbt "runMain projectname.MyTopLevelVhdl"

// To run the testbench
sbt "runMain projectname.MyTopLevelSim"
```

* The example hardware description is into `hw/spinal/projectname/MyTopLevel.scala`
* The testbench is into `hw/spinal/projectname/MyTopLevelSim.scala`



