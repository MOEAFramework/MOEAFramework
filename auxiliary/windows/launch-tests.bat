@echo off
echo.
echo Starting MOEA Framework Tests
echo.
echo Note: These tests require Apache Ant to be installed.  If Apache Ant is not
echo installed on this computer, please download Ant from "http://ant.apache.org/"
echo and follow the installation instructions.
echo.
echo A summary of the results will be displayed after all tests complete.  Due
echo to the stochastic nature of the MOEA Framework, there is a small chance that
echo some correct tests will fail.  Please repeat the tests prior to notifying the
echo developers.
echo.
pause
echo.
cmd /c "ant -f test.xml"
start test-results/index.html