#!/bin/sh
#-------------------------------------------------------------------------------
# Copyright (C) 2011 WebSquared Inc. http://websqrd.com
# 
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
#-------------------------------------------------------------------------------
#Webscraper start script
cd `dirname $0`/../
SCRAPER_HOME=`pwd`

CONF=$SCRAPER_HOME/conf
LIB=$SCRAPER_HOME/lib


#for background service
SCRAPER_CLASSPATH=".:bin"
for jarfile in `find $LIB | grep [.]jar$`; do SCRAPER_CLASSPATH="$SCRAPER_CLASSPATH:$jarfile"; done
nohup java -Dserver.home=$SCRAPER_HOME -Xmx512m -Dfile.encoding=UTF-8 -server -Dlogback.configurationFile=$CONF/logback.xml -Dderby.stream.error.file=logs/db.log -classpath $SCRAPER_CLASSPATH com.websqrd.catbot.server.CatBotServer 2>&1 &
