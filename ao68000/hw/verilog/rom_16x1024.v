module rom_16x1024 (
    input  wire        clk,
    input  wire [9:0]  addr,     // 1024 words -> 10-bit address
    output reg  [15:0] data_out
);

    // Simple ROM content: all NOPs (0x4E71)
    reg [15:0] rom [0:1023];

    integer i;
    initial begin
        for (i = 0; i < 1024; i = i + 1) begin
            rom[i] = 16'h4E71; // NOP
            // rom[i] = 16'h4E72; // STOP
        end
    end

    always @(posedge clk) begin
        data_out <= rom[addr];
    end

endmodule