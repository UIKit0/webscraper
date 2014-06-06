/*
 * Copyright (C) 2011 WebSquared Inc. http://websqrd.com
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.websqrd.catbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CatBotServiceComponent {
	protected static Logger logger = LoggerFactory.getLogger(CatBotServiceComponent.class);
	
	protected boolean isRunning;
	
	public boolean isRunning() throws ServiceException{
		return isRunning;
	}
	
	public boolean start() throws ServiceException{
		if(isRunning){
			logger.error("이미 실행중입니다.");
			return false;
		}
		if(start0()){
			isRunning = true;
			logger.info(getClass().getSimpleName()+" 시작!");
			return true;
		}else{
			return false;
		}
	}
	
	protected abstract boolean start0() throws ServiceException;
		
	public boolean shutdown() throws ServiceException{
		if(!isRunning){
			logger.error(getClass().getSimpleName()+"가 시작중이 아닙니다.");
			return false;
		}
		if(shutdown0()){
			isRunning = false;
			logger.info(getClass().getSimpleName()+" 정지!");
			return true;
		}else{
			return false;
		}
	}
	
	protected abstract boolean shutdown0() throws ServiceException;
		
	public boolean restart() throws ServiceException{
		logger.info(this.getClass().getName()+" restart..");
		//시작중이든 정지중이든 restart는 무조건 정지명령을 내린다.
		shutdown0();
		//start는 성공해야 하므로 해당값을 리턴해준다.
		return start0();
	}
	
}
