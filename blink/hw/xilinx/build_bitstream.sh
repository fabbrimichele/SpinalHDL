#!/bin/bash

TOPMODULE=$1
DEVICE=$2
UCF=$3

echo
echo "*****************************************************************************"
echo "Building design: '$TOPMODULE' for device '$DEVICE' and UCF '$UCF'"
echo "*****************************************************************************"

source /opt/Xilinx/14.7/ISE_DS/settings64.sh

cd target

echo "**** Running xst"
xst -intstyle ise -ifn "../hw/xilinx/blink.xst" -ofn ${TOPMODULE}.syr

echo "**** Running ngdbuild"
# ngdbuild -intstyle ise -dd _ngo -nt timestamp -uc papilio_duo_computing_shield.ucf -p xc6slx9-tqg144-2 Blinker.ngc Blinker.ngd
ngdbuild -intstyle ise -dd _ngo -nt timestamp -uc ../hw/xilinx/${UCF}.ucf -p ${DEVICE} ${TOPMODULE}.edf ${TOPMODULE}.ngd

echo "**** Running map"
# map -intstyle ise -p xc6slx9-tqg144-2 -w -logic_opt off -ol high -t 1 -xt 0 -register_duplication off -r 4 -global_opt off -mt off -ir off -pr off -lc off -power off -o Blinker_map.ncd Blinker.ngd Blinker.pcf
map -intstyle ise -p ${DEVICE} -w -logic_opt off -ol high -t 1 -xt 0 -register_duplication off -r 4 -global_opt off -mt off -ir off -pr off -lc off -power off -o ${TOPMODULE}_map.ncd ${TOPMODULE}.ngd ${TOPMODULE}.pcf

echo "**** Running par"
# par -w -intstyle ise -ol high -mt off Blinker_map.ncd Blinker.ncd Blinker.pcf
par -w -intstyle ise -ol high -mt off ${TOPMODULE}_map.ncd ${TOPMODULE}.ncd ${TOPMODULE}.pcf

echo "**** Running bitgen"
# bitgen -intstyle ise -f Blinker.ut Blinker.ncd
bitgen -intstyle ise -f ../hw/xilinx/bitgen_config.ut ${TOPMODULE}.ncd