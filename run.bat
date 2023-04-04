set ProjectPath=%~dp0

cd %ProjectPath%

set classpath=.\bin;.\lib\*

java executionEngine.RunTestscript

pause