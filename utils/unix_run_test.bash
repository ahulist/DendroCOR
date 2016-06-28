#!/bin/bash
cd ..
mkdir -p "logs"
echo "$(date)" > "logs/run_test.txt"
if [ ! -f DendroCOR.jar ]; then
    echo "DendroCOR.jar file does not exist" >> "logs/run_test.txt"
else
    java -jar "DendroCOR.jar" 2>> "logs/run_test.txt"
fi