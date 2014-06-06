@echo off

setlocal enabledelayedexpansion

set SCRAPER_HOME=%~dp0
chdir /d %SCRAPER_HOME%
set CONF=%SCRAPER_HOME%\conf
set LIB=%SCRAPER_HOME%\lib
set SCRAPER_CLASSPATH=.

for /f "tokens=*" %%x in ('dir /s /b %LIB%\*.jar') do (set SCRAPER_CLASSPATH=!SCRAPER_CLASSPATH!;%%x)

java -Xmx512m -Dserver.home=. -Dfile.encoding=UTF-8 -Dlogback.configurationFile=%CONF%\logback.xml -Dderby.stream.error.file=logs\db.log -classpath %SCRAPER_CLASSPATH% com.websqrd.catbot.server.CatBotServer

endlocal