cd ..
if not exist logs mkdir logs
echo %date% %time% > logs\run_test.txt
if not exist DendroCOR.jar echo DendroCOR.jar file does not exist >> logs\run_test.txt & exit
java -jar DendroCOR.jar 2>> logs\run_test.txt