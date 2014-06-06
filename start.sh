#!/bin/sh

#start script
cd `dirname $0`
SCRAPER_HOME=`pwd`

CONF=$SCRAPER_HOME/conf
LIB=$SCRAPER_HOME/lib

#for background service
SCRAPER_CLASSPATH=".:bin"
for jarfile in `find $LIB | grep [.]jar$`; do SCRAPER_CLASSPATH="$SCRAPER_CLASSPATH:$jarfile"; done
nohup java -Dserver.home=$SCRAPER_HOME -Xmx512m -Dfile.encoding=UTF-8 -server -Dlogback.configurationFile=$CONF/logback.xml -Dderby.stream.error.file=logs/db.log -classpath $SCRAPER_CLASSPATH com.websqrd.catbot.server.CatBotServer
