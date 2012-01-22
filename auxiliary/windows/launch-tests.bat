@echo off
echo.
echo Starting MOEA Framework Tests
echo.
echo Note: These tests require Apache Ant to be installed.  If Apache Ant is not
echo installed on this computer, please download Ant from "http://ant.apache.org/"
echo and follow the installation instructions.
echo.
pause
echo.
cmd /c "ant -f test.xml"
start test-results/index.html