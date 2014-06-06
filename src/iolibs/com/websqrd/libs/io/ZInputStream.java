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
import java.io.InputStream;
import com.jcraft.jzlib.ZStream;
import com.jcraft.jzlib.JZlib;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Wrap InputStream as Zipped Stream
 * @author lupfeliz
 *
 */
public class ZInputStream extends InputStream {

	private ZStream zs;
	private InputStream iis;
	private byte[] ibuf;
	private int iend = 0;
	
	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(ZInputStream.class);
	
	public ZInputStream (InputStream is) {
		this.iis = is;
		this.ibuf = new byte[1024];
		zs = new ZStream();
		zs.inflateInit();
	}
	
	/**
	 * 
	 * @param is real input stream
	 * @param buflen buffer length
	 * @param wbit windowbit (reference from Zlib)
	 */
	public ZInputStream (InputStream is, int buflen, int wbit) {
		this.iis = is;
		this.ibuf = new byte[buflen];
		zs = new ZStream();
		zs.inflateInit(wbit);
	}
	
	@Override
	public int read() throws IOException { throw new IOException("Not implemented method ZInputStream.read"); }
	
	@Override
	public int read(byte[] obuf,int opos, int olen) throws IOException {
		int ret = -1;
		int rlen = 0;
		
		if((ibuf.length - iend) >0) {
			rlen = iis.read(ibuf,iend,(ibuf.length-iend));
			if(rlen != -1) {
				iend += rlen;
			} else {
				rlen = 0;
			}
		}
		
		long tipos = zs.total_in; //전체입력된 길이
		long topos = zs.total_out; //전체출력된 길이
		
		zs.next_in = ibuf;
		zs.next_in_index = 0;
		zs.next_out = obuf;
		zs.next_out_index = opos;
		
		int cinp = 0;
		
		while((zs.total_in - tipos) < iend && (zs.total_out - topos) < olen) {
			//logger.debug(zs.total_in+":"+zs.total_out+":"+tipos+":"+topos+":"+ibuf.length+":"+iend+":"+rlen+":"+olen+":"+opos+":"+cinp);
			zs.avail_in = zs.avail_out = 1;
			int err = zs.inflate(JZlib.Z_FULL_FLUSH);
			if(err == JZlib.Z_STREAM_END) { break; }
			if(err!=JZlib.Z_OK) { throw new IOException("Can't inflate ! " + zs.msg); }
			//cinp++;
		}
		
		cinp = (int)(zs.total_in - tipos);//입력버퍼를 어디까지 사용했는가.
		//logger.debug(zs.total_in+":"+zs.total_out+":"+tipos+":"+topos+":"+ibuf.length+":"+iend+":"+rlen+":"+olen+":"+opos+":"+cinp);
		
		if((zs.total_out - topos)==0) {
			return -1;
		} else {
			ret = (int)(zs.total_out - topos);
		}
		
		int dinp = iend-cinp;
		//logger.debug(zis+" zpos : "+zpos+" rlen : "+rlen+" total in : "+zs.total_in+" total out : "+zs.total_out+" size : "+cinp+" ret : "+ret+" delta : "+dinp);
		//logger.debug(" iend : "+iend+" rlen : "+rlen+" total in : "+zs.total_in+" total out : "+zs.total_out+" size : "+cinp+" ret : "+ret+" delta : "+dinp);
		System.arraycopy(ibuf, cinp, ibuf, 0, dinp);
		iend -=cinp;
		//iend = dinp;
		return ret;
	}
	
	@Override
	public void close() throws IOException {
		zs.inflateEnd();
		iis.close();
	}
}
