module Dcm25Mhz (
    input wire clk32,      // 32 MHz input clock
    input wire reset,      // Active high reset
    output wire clk25,     // ~25.143 MHz output
    output wire locked     // High when clock is stable
);

    wire clkfb;
    wire clk0;
    wire clkfx;

    DCM_SP #(
        .CLKFX_MULTIPLY(22),
        .CLKFX_DIVIDE(28),
        .CLKIN_PERIOD(31.25), // 1/32MHz in ns
        .CLK_FEEDBACK("1X")
    ) dcm_inst (
        .CLKIN(clk32),
        .CLKFB(clkfb),
        .RST(reset),
        .CLK0(clk0),
        .CLKFX(clkfx),
        .LOCKED(locked),
        .PSEN(1'b0),
        .PSINCDEC(1'b0),
        .PSCLK(1'b0),
        .DSSEN(1'b0)
    );

    assign clkfb = clk0;
    assign clk25 = clkfx;

endmodule