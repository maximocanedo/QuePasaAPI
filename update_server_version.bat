@echo off
set /p FILE=Nombre del archivo: 
rem echo "F:\GitHub\QuePasaAPI\build\libs\%FILE%"
scp -P 5952 "F:\GitHub\QuePasaAPI\build\libs\%FILE%" root@canedo.com.ar:/home/quepasa/production.jar

rem scp "build\libs\quepasa-v8.10.9-beta (Imperial Ruso).jar" root@canedo.com.ar:/home/quepasa/production.jar -P 5952
