;
;	WOW Install Script
;	NOTE: Please run this in the WOW/trunk/Server/Java/dist directory, after a successful ant build.
;	Script by Andrew Sibley
;	asibley@vtc.vsc.edu
;
; Definitions for Java Detection
!define JRE_VERSION "1.6"

; NOTE: Uncomment the next two lines out to download the JRE. Please see below in the GetJRE section.
; Requires NSISdl.nsh library in the NSIS_Install_Dir\Include directory.
!define JRE_URL "http://javadl.sun.com/webapps/download/AutoDL?BundleId=11292"
!include NSISdl.nsh

!include LogicLib.nsh

;Name of installer:

Name "Install Window on the World Server"

OutFile "Install WOW Server.exe"

InstallDir C:\WOWd
Icon "icon.ico"

;pages

Page directory
Page instfiles
UninstPage instfiles

Section "Install WOW Server (required)"
	Call DetectJRE
	
	SetOutPath $INSTDIR
	
	CreateDirectory $INSTDIR\bin
	CreateDirectory $INSTDIR\lib
	File /oname=bin\WOWd.jar "bin\WOWd.jar" 
	File /oname=lib\smtp.jar "lib\smtp.jar" 
	File /oname=lib\mailapi.jar "lib\mailapi.jar" 
	File "Start_WOW.bat"
	File "wow.conf"
	File "profiles.xml"
	File "icon.ico"
	CreateShortCut "$SMPROGRAMS\WOW Server.lnk" "$INSTDIR\Start_WOW.bat" "" "$INSTDIR\icon.ico" 0
	CreateShortCut "$DESKTOP\WOW Server.lnk" "$INSTDIR\Start_WOW.bat" "" "$INSTDIR\icon.ico" 0
	
	WriteUninstaller "$INSTDIR\Uninstall WOWd.exe"

SectionEnd

Section "Uninstall"

	Delete "$SMPROGRAMS\WOW Server.lnk"
	Delete "$DESKTOP\WOW Server.lnk"
	RMDir /r $INSTDIR\bin
	RMDir /r $INSTDIR\lib
	Delete $INSTDIR\*.* ; Cleanup any other files we missed.

SectionEnd

Function GetJRE
		; NOTE: This can be split into two sections, but only one should be used.
		; [INSTALL JRE]
		; Uncomment the following code to execute a JRE installer that was packaged with this installer.
		; Note that the ExecWait statement will run the file. The first command (JRE[version].exe) is the filename.
		; Change the code if the file name changes.
		; Comment out the following code if you are using the code from the [DOWNLOAD JRE] section:
        ;MessageBox MB_OK "Window on the World uses Java ${JRE_VERSION}, it will now \
        ;                 be installed."
		;ExecWait JRE1.6.exe $0
		;${If} $0 != 0
		;	MessageBox MB_OK "${JRE_VERSION} is required.  The installation will abort."
		;	Abort
		;${Endif}
		
		; [DOWNLOAD JRE]
		; Uncomment the following code to automatically download the JRE. This obviously requires an internet connection,
		; and preferably a fast one.  It's generally a better idea to package the JRE with the installer, but if they already
		; have Java, it could be a waste of space, too.
		; Note that JRE_URL is a variable specified at the top of this script. If the location of the JRE changes, change
		; the JRE_URL variable to reflect that.
		; Comment out the following code if you uare using the code from the [INSTALL JRE] section:
        StrCpy $2 "$TEMP\Java Runtime Environment.exe"
        NSISdl::download /TIMEOUT=30000 ${JRE_URL} $2
        Pop $R0 ;Get the return value
                StrCmp $R0 "success" +3
                MessageBox MB_OK "Download failed: $R0"
                Quit
FunctionEnd
 
 
Function DetectJRE
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" \
             "CurrentVersion"
  StrCmp $2 ${JRE_VERSION} done
  
  Call GetJRE
  
  done:
FunctionEnd