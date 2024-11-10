#!/bin/bash

set -e

images="imgs/"
mkdir -p "${images}"

for file in pf/*.pf; do
  filename=$(basename -- "$file")
  extension="${filename##*.}"
  filename="${filename%.*}"

  columns=$(python - <<EOF
import os
import sys

file = "${file}"
columns = -1
lines = []

with open(file, 'r') as input:
  for line in input:
    line = line.strip()

    if line.startswith('#'):
      lines.append(line)
      continue

    values = [x.strip() for x in line.split() if x]

    if columns == -1:
      columns = len(values)
    elif columns != len(values):
      sys.exit(f"Incorrect number of values (expected: {columns}, actual: {len(values)})")

    line = " ".join(values)
    if line not in lines:
      lines.append(line)

with open(file, 'w') as output:
  for line in lines:
    output.write(f"{line}\n")

print(columns)
EOF
)

  echo "Processing ${filename} with ${columns} objectives"

  if [ "${columns}" -eq "2" ]; then
  gnuplot <<EOF
set term png size 320,240
set format x ""
set format y ""
set output '${images}/${filename}.png'
plot '${file}' with points pointtype 7 lc rgb "black" notitle
EOF
  elif [ "${columns}" -eq "3" ]; then
  gnuplot <<EOF
set term png size 480,360
set format x ""
set format y ""
set format z ""
set ticslevel 0
set view 60,100,1.2,1.2
set output '${images}/${filename}.png'
splot '${file}' with points pointtype 7 lc rgb "black" notitle
EOF
  fi
done


