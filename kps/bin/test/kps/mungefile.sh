#!/bin/bash
cp $1 TMP
sed -f mungepackages.sed TMP > $1
