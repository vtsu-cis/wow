;
;	WOW Install Script
;
; Definitions for Java 1.5 Detection
!define JRE_VERSION "1.6"
;!define JRE_URL "http://javadl.sun.com/webapps/download/AutoDL?BundleId=11292"

;!include NSISdl.nsh
!include LogicLib.nsh

;Name of installer:

Name "Install Window on the World (Admin)"

OutFile "Install WoW Admin.exe"

InstallDir C:\$user\WoW\Admin
Icon "icon.ico"

;pages

Page directory
Page instfiles
UninstPage instfiles

Section "Install WoW Admin (required)"
	Call DetectJRE
	
	SetOutPath $INSTDIR
	
	File "WoWAdminGUI.jar"
	File "swing-layout-1.0.jar"
	File "wowicon.gif"
	File "wow.conf"
	File "dept-list.txt"
	File "Readme.txt"
	File "backupPage.htm"
	File "backupFax.htm"
	File "icon.ico"
	File "JRE1.6.exe"
	CreateShortCut "$SMPROGRAMS\WoW Admin.lnk" "$INSTDIR\WoWAdminGUI.jar" "" "$INSTDIR\icon.ico" 0
	CreateShortCut "$DESKTOP\WoW Admin.lnk" "$INSTDIR\WoWAdminGUI.jar" "" "$INSTDIR\icon.ico" 0
	
	WriteUninstaller "$INSTDIR\Uninstall WoW Administrator.exe"

SectionEnd

Section "Uninstall"

	Delete "$SMPROGRAMS\WoW Admin.lnk"
	Delete "$DESKTOP\WoW Administrator.lnk"
	Delete $INSTDIR\WoWAdminGUI.jar
	Delete $INSTDIR\swing-layout-1.0.jar
	Delete $INSTDIR\wowicon.gif
	Delete $INSTDIR\wow.conf
	Delete $INSTDIR\dept-list.txt
	Delete $INSTDIR\Readme.txt
	Delete "$INSTDIR\Uninstall WoW Administrator.exe"
	Delete $INSTDIR\backupPage.htm
	Delete $INSTDIR\backupFax.htm
	Delete $INSTDIR\FB.htm
	Delete $INSTDIR\JRE1.6.exe

SectionEnd

Function GetJRE
        MessageBox MB_OK "Window on the World uses Java ${JRE_VERSION}, it will now \
                         be installed."
		ExecWait JRE1.6.exe $0
		${If} $0 == 1602
			MessageBox MB_OK "${JRE_VERSION} is required.  The installation will abort."
			Abort
		${Endif}
		
        ;StrCpy $2 "$TEMP\Java Runtime Environment.exe"
        ;NSISdl::download /TIMEOUT=30000 ${JRE_URL} $2
        ;Pop $R0 ;Get the return value
        ;        StrCmp $R0 "success" +3
        ;        MessageBox MB_OK "Download failed: $R0"
        ;        Quit
FunctionEnd
 
 
Function DetectJRE
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" \
             "CurrentVersion"
  StrCmp $2 ${JRE_VERSION} done
  
  Call GetJRE
  
  done:
FunctionEnd