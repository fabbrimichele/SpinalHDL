module rom_16x1024 (
    input  wire        clk,
    input  wire [9:0]  addr,     // 1024 words -> 10-bit address
    output reg  [15:0] data_out
);

    reg [15:0] rom [0:1023];

    integer i;
    initial begin
        $readmemh("rom.hex", rom); // file should contain 16-bit hex words, one per line
    end

    always @(posedge clk) begin
        data_out <= rom[addr];
    end

endmodule