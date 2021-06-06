;Include Modern UI
!include "MUI2.nsh"

;Basic configuration
Name "Quadrapassel"
OutFile "quadrapassel.exe"
Unicode True
;Default installation folder
InstallDir "$PROGRAMFILES64\quadrapassel"
;Request admin privileges for Vista/7/8/10
RequestExecutionLevel admin
!define MUI_ABORTWARNING

;Pages
!insertmacro MUI_PAGE_LICENSE "LICENSE"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES

;Languages
!insertmacro MUI_LANGUAGE "Russian"

;Installer Sections
Section "Section" SecExample
    SetOutPath "$INSTDIR"
    FILE basic.nsi
SectionEnd

;Descriptions
LangString DESC_SecExample ${LANG_ENGLISH} "Copies the NSIS configuration."
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecExample} $(DESC_SecExample)
!insertmacro MUI_FUNCTION_DESCRIPTION_END
