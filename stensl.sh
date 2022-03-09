#!/bin/bash

filename="$1"
code=""
newline=$'\n'
directory=$(pwd)

while IFS= read -r line || [[ -n "$line" ]]; do
    name="$line"
    code="$code $newline $name"
done < "$filename"

cd out
cd production
cd Stensl
java InputManager "$code"
cd $directory