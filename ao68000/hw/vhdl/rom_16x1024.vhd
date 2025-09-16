library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity rom_16x1024 is
    port (
        clk      : in  std_logic;
        addr     : in  unsigned(9 downto 0);  -- 10-bit address for 1024 words
        data_out : out std_logic_vector(15 downto 0)
    );
end entity rom_16x1024;

architecture rtl of rom_16x1024 is
    type rom_type is array(0 to 1023) of std_logic_vector(15 downto 0);
    constant rom : rom_type := (
        0  => x"0000",
        1  => x"0000",
        2  => x"0000",
        3  => x"0080",
        4  => x"0000",
        5  => x"0000",
        6  => x"0000",
        7  => x"0000",
        8  => x"0000",
        9  => x"0000",
        10 => x"0000",
        11 => x"0000",
        12 => x"0000",
        13 => x"0000",
        14 => x"0000",
        15 => x"0000",
        16 => x"0000",
        17 => x"0000",
        18 => x"0000",
        19 => x"0000",
        20 => x"0000",
        21 => x"0000",
        22 => x"0000",
        23 => x"0000",
        24 => x"0000",
        25 => x"0000",
        26 => x"0000",
        27 => x"0000",
        28 => x"0000",
        29 => x"0000",
        30 => x"0000",
        31 => x"0000",
        32 => x"0000",
        33 => x"0000",
        34 => x"0000",
        35 => x"0000",
        36 => x"0000",
        37 => x"0000",
        38 => x"0000",
        39 => x"0000",
        40 => x"0000",
        41 => x"0000",
        42 => x"0000",
        43 => x"0000",
        44 => x"0000",
        45 => x"0000",
        46 => x"0000",
        47 => x"0000",
        48 => x"0000",
        49 => x"0000",
        50 => x"0000",
        51 => x"0000",
        52 => x"0000",
        53 => x"0000",
        54 => x"0000",
        55 => x"0000",
        56 => x"0000",
        57 => x"0000",
        58 => x"0000",
        59 => x"0000",
        60 => x"41F9",
        61 => x"00FF",
        62 => x"0000",
        63 => x"20BC",
        64 => x"0000",
        65 => x"0001",
        66 => x"4E71",
        67 => x"60FE",
        others => x"4E71" -- fill remaining words with NOP
    );
begin

    -- Synchronous read
    process(clk)
    begin
        if rising_edge(clk) then
            data_out <= rom(to_integer(addr));
        end if;
    end process;

end architecture rtl;