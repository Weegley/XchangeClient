@echo off
setlocal EnableDelayedExpansion
rem chcp 65001 > NUL

:: ���� � 7-Zip (�������, �᫨ 7z.exe ��室���� � ��㣮� ����)
set SEVENZIP=D:\Progs\7Z\7z.exe

:: ��� ��娢� (�㤥� ᮧ���� � ⥪�饩 ����� � ⥪�饩 ��⮩)
set "ARCHIVE_NAME=XchangeClient_archive_%date:~6,4%%date:~3,2%%date:~0,2%-%time:~0,2%%time:~3,2%%time:~6,2%.zip"

:: ����� ��� ��娢�樨 (⥪��� �����, ��� ��室���� .bat)
set SOURCE_DIR="app" "build.gradle.*"

:: ���᮪ ����� � 䠩��� ��� �᪫�祭�� (������� ᢮� �����/䠩�� �१ �஡��)
set EXCLUDE=-xr^^!node_modules -xr^^!.git -xr^^!*.log -xr^^!build -xr^^!.gitignore

echo EXCLUDE=!EXCLUDE!


:: �஢�ઠ ������ 7-Zip
if not exist "%SEVENZIP%" (
    echo �訡��: 7z.exe �� ������ �� ��� %SEVENZIP%. ������ �ࠢ���� ����.
    pause
    exit /b 1
)

:: �������� ��娢�
echo �������� ��娢� %ARCHIVE_NAME%...

%SEVENZIP% a -tzip "%ARCHIVE_NAME%" %SOURCE_DIR% !EXCLUDE! -bb1

:: �஢�ઠ �ᯥ譮�� ��娢�樨
if %ERRORLEVEL%==0 (
    echo ��娢 %ARCHIVE_NAME% �ᯥ譮 ᮧ���!
) else (
    echo �訡�� �� ᮧ����� ��娢�.
	
)
