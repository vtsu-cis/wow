@echo OFF
:: 		FILE: Start_WOW.bat								        
:: 		SUBJECT: Start the WOW server in a Windows-friendly environment.
::		DATE CREATED: 01/16/09
::		DATE MODIFIED: 01/16/09

:: 		Double-click the Start_WOW.bat file to run the server. This action requires Java
::		to either be on the PATH (default) or on the JAVA_PATH set in this batch file.
SET JAVA_PATH=java

SET ARGS=%1 %2 %3

@start "WOW Server" /SEPARATE /D. "%JAVA_PATH%" -jar bin/WOWd.jar %ARGS%

@echo %0: Action complete.