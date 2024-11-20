@echo off
for %%f in ("F:\GitHub\QuePasaAPI\build\libs\*.jar") do (
    echo %%~nxf | findstr /v ".plain" >nul
    if not errorlevel 1 (
        scp -P 5952 "%%f" root@canedo.com.ar:/home/quepasa/production.jar
        exit /b
    )
)
echo No se encontró un archivo .jar válido.
