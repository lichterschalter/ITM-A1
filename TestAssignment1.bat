@echo off

REM ---------------------------------------------------------------------------------------------------------
REM	Set input params - %~dp0 returns working path
REM ---------------------------------------------------------------------------------------------------------

set input=%~dp0%1
set output=%~dp0%2

REM ---------------------------------------------------------------------------------------------------------
REM	Check input
REM ---------------------------------------------------------------------------------------------------------

if [%1] == [] goto wrongparam
if [%2] == [] goto createoutputfolder

:checkinput
if not exist %input% goto directorynotfound
if not exist %output% goto createoutputfolder

goto ant

REM ---------------------------------------------------------------------------------------------------------
REM	ANT
REM ---------------------------------------------------------------------------------------------------------

:ant
call ant.bat clean
call ant.bat compile
call ant.bat ImageConverter 		    -Dinput=%input% 	-Doutput=%output% 	-Dformat=bmp
call ant.bat ImageConverter 		    -Dinput=%input% 	-Doutput=%output% 	-Dformat=png
call ant.bat ImageConverter 		    -Dinput=%input%		-Doutput=%output% 	-Dformat=jpeg 	-Dquality=0.1
call ant.bat ImageConverter 		    -Dinput=%input%		-Doutput=%output% 	-Dformat=jpeg 	-Dquality=0.3
call ant.bat ImageConverter 		    -Dinput=%input%		-Doutput=%output% 	-Dformat=jpeg 	-Dquality=0.9
call ant.bat ImageMetadataGenerator  	-Dinput=%input% 	-Doutput=%output%
call ant.bat ImageThumbnailGenerator 	-Dinput=%input% 	-Doutput=%output% 	-Drotation=90
call ant.bat ImageHistogramGenerator 	-Dinput=%input% 	-Doutput=%output% 	-Dbins=256
goto end

REM ---------------------------------------------------------------------------------------------------------
REM	Try to create output folder
REM ---------------------------------------------------------------------------------------------------------

:createoutputfolder
if [%2] == [] set output=%~dp0out 
mkdir %output%
goto checkinput

REM ---------------------------------------------------------------------------------------------------------
REM	ERR
REM ---------------------------------------------------------------------------------------------------------

:directorynotfound
echo "Input Folder does not exists"
goto end

:wrongparam
echo "One or more parameter are missing"
echo "Example usage: cmd media\img output"

REM ---------------------------------------------------------------------------------------------------------
REM	END
REM ---------------------------------------------------------------------------------------------------------

:end