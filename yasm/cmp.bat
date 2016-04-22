@echo off

set asmfile=%1
set objfile=%asmfile:.asm=.obj%
set exefile=%asmfile:.asm=.exe%

echo YASM (from asm to obj):
call yasm.exe -f win32 %asmfile% -o %objfile%
echo.%space%

echo GCC (from obj to exe):
call E:\Code\MinGW\bin\gcc.exe %objfile% -o %exefile% 
echo.%space%


rem echo LINK (from obj to exe):
rem call E:\Code\Sygwin\bin\link.exe %objfile% %exefile%
rem call E:\Code\MinGW\bin\ld.exe -e _start -o %exefile% %objfile%
rem echo.%space%

echo RUN EXE:
echo.%space%
call %exefile% 
echo.%space%

pause