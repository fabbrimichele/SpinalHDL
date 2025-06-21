#!/bin/bash

TOPMODULE=$1
DEVICE=$2

echo "Building design: $TOPMODULE for device $DEVICE"

source /opt/Xilinx/14.7/ISE_DS/settings64.sh

cd target
ngdbuild -uc hw/wilinx/${TOPMODULE}.ucf target/${TOPMODULE}.edf
map -p ${DEVICE} target/${TOPMODULE}.ngd
par target/${TOPMODULE}.ncd target/${TOPMODULE}_routed.ncd
bitgen target/${TOPMODULE}_routed.ncd