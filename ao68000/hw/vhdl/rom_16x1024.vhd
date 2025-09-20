library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity rom_16x1024 is
    port (
        clk      : in  std_logic;
        addr     : in  unsigned(9 downto 0) := (others => '0');  -- 10-bit address for 1024 words
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
        64 => x"41F9", -- 80: lea 0xff0000,%a0
        65 => x"00FF",
        66 => x"0000",
        67 => x"30BC", -- 86: movew #1,%a0@
        68 => x"0001",
        69 => x"4E71", -- 8a: nop
        70 => x"4EF8", -- 8c: jmp 0x8c
        71 => x"008C",
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