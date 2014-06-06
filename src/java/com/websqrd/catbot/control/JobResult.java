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

package com.websqrd.catbot.control;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class JobResult {
	
	private BlockingQueue queue = new LinkedBlockingQueue();
	private boolean isSuccess;
	
	public void put(Object result, boolean isSuccess) throws InterruptedException{
		this.isSuccess = isSuccess;
		queue.put(result);
	}
	
	public boolean isSuccess(){
		return isSuccess;
	}
	
	public Object take() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			return null;
		}
	}
	
	public Object poll(int time) {
		try {
			return queue.poll(time, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			return null;
		}
	}
}
