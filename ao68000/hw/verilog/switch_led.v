module switch_led (
  input  wire          switchDown,
  output wire          led0
);
  assign led0 = switchDown;
endmodule