#!/bin/bash
for  file  in  *.csv; do
     FILEYEAR=`echo "$file" | cut -d'-' -f 1`
     if [ "$FILEYEAR" -eq '2019-1_CA.csv' ] || [ "$FILEYEAR" -lt '2019' ]; then
    	`iconv -f UTF-16LE  -t  UTF-8  "$file"   -o  "${file%.csv}.utf8"`
     else 
    	`iconv -f ISO-8859-1  -t  UTF-8  "$file"   -o  "${file%.csv}.utf8"`
     fi
     mv "${file%.csv}.utf8" "$file"
done
exit 0
