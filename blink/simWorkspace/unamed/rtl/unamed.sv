// Generator : SpinalHDL v1.12.0    git head : 1aa7d7b5732f11cca2dd83bacc2a4cb92ca8e5c9
// Component : unamed
// Git hash  : 2aebdfd17c8bf1e75a34864b3e241b8959aa9add

`timescale 1ns/1ps

module unamed (
  input  wire          reset,
  input  wire          clk
);

  wire                dut_io_cond0;
  wire                dut_io_cond1;
  wire                dut_io_flag;
  wire       [7:0]    dut_io_state;
  wire       [7:0]    _zz__zz_1;
  wire       [0:0]    _zz__zz_1_1;
  reg        [7:0]    _zz_1;

  assign _zz__zz_1_1 = dut_io_cond0;
  assign _zz__zz_1 = {7'd0, _zz__zz_1_1};
  MyTopLevel dut (
    .io_cond0 (dut_io_cond0     ), //i
    .io_cond1 (dut_io_cond1     ), //i
    .io_flag  (dut_io_flag      ), //o
    .io_state (dut_io_state[7:0]), //o
    .clk      (clk              ), //i
    .reset    (reset            )  //i
  );
  initial begin
    assume(reset); // MyTopLevelFormal.scala:L15
  end

  assign dut_io_cond0 = $anyseq(1);
  assign dut_io_cond1 = $anyseq(1);
  always @(posedge clk) begin
    if(reset) begin
      _zz_1 <= 8'h0;
    end else begin
      _zz_1 <= (dut_io_state + _zz__zz_1);
      assert((dut_io_state == _zz_1)); // MyTopLevelFormal.scala:L22
    end
  end


endmodule

module MyTopLevel (
  input  wire          io_cond0,
  input  wire          io_cond1,
  output wire          io_flag,
  output wire [7:0]    io_state,
  input  wire          clk,
  input  wire          reset
);

  reg        [7:0]    counter;

  assign io_state = counter;
  assign io_flag = ((counter == 8'h0) || io_cond1);
  always @(posedge clk or posedge reset) begin
    if(reset) begin
      counter <= 8'h0;
    end else begin
      if(io_cond0) begin
        counter <= (counter + 8'h01);
      end
    end
  end


endmodule
