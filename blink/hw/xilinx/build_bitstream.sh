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
echo ngdbuild
ngdbuild -p ${DEVICE} -uc ../hw/xilinx/${UCF}.ucf ${TOPMODULE}.edf
echo map
map -p ${DEVICE} ${TOPMODULE}.ngd
echo par
par ${TOPMODULE}.ncd ${TOPMODULE}_routed.ncd
echo bitgen
bitgen ${TOPMODULE}_routed.ncd