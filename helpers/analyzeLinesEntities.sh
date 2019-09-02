#!/usr/bin/env bash

inFile=$1
outFile=$2

echo "[" >> $outFile

while read line
do
	gcloud ml language analyze-entities --content="$line" >> $outFile
	echo "," >> $outFile
done < $inFile

echo "]" >> $outFile
