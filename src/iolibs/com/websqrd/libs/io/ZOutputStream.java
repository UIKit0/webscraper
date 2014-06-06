/*
 * Copyright (C) 2011 Web Squared Inc. http://websqrd.com
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

package com.websqrd.libs.io;

import java.io.IOException;
import java.io.OutputStream;

import com.jcraft.jzlib.ZStream;
import com.jcraft.jzlib.JZlib;

/**
 * Wrap OutputStream as Zipped Stream
 * @author lupfeliz
 *
 */
public class ZOutputStream extends OutputStream {
	
	private ZStream zs;
	private OutputStream zos;
	private byte[] zbuf;
	
	public ZOutputStream (OutputStream os) { this(os,JZlib.Z_DEFAULT_COMPRESSION); }
	
	public ZOutputStream (OutputStream os, int rate) {
		this.zos = os;
		this.zbuf = new byte[1024];
		zs = new ZStream();
		zs.deflateInit(rate);
	}
	
	public ZOutputStream (OutputStream os, int buflen, int rate, int wbit) {
		this.zos = os;
		this.zbuf = new byte[buflen];
		zs = new ZStream();
		zs.deflateInit(rate, wbit);
	}
	
	@Override
	public void write(int b) throws IOException { throw new IOException("Not implemented method ZOutputStream.write"); }
	
	public void write(byte[] buf,int pos, int len) throws IOException {
		
		zs.next_in = buf;
		zs.next_in_index = pos;
		zs.next_out = zbuf;
		zs.next_out_index = 0;
		
		long tipos = zs.total_in;
		long topos = zs.total_out;
		
		while( zs.total_in < (tipos+len) ) {
			zs.avail_in = zs.avail_out = 1;
			int err = zs.deflate(JZlib.Z_FULL_FLUSH);
			if(err == JZlib.Z_STREAM_END) { break; }
			if(err != JZlib.Z_OK) { throw new IOException("Can't deflate !" + zs.msg); }
			if(zs.total_out > (topos+zbuf.length)) {
				zos.write(zbuf,0,zbuf.length);
				topos = zs.total_out;
			}
		}
		while(true){
			zs.avail_out=1;
			int err=zs.deflate(JZlib.Z_FINISH);
			if(err==JZlib.Z_STREAM_END)break;
			if(err != JZlib.Z_OK) { throw new IOException("Can't deflate !" + zs.msg); }
		}

		//System.out.println(zs.total_in+":"+zs.total_out+":"+topos);
		
		if(topos < zs.total_out) {
			zos.write(zbuf,0,(int)(zs.total_out - topos));
		}
	}
	
	@Override
	public void close() throws IOException {
		zos.close();
	}
}
