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

package com.websqrd.libs.crypt;

public class Base64 {
	private static char[] DEFAULT_BASE64_MAP;
	static {
		DEFAULT_BASE64_MAP = new char[64];
		for (int i=0;i<1;i++) {
			for (byte c=(byte)'A'; c<=(byte)'Z'; c++) DEFAULT_BASE64_MAP[i++] = (char)c;
			for (byte c=(byte)'a'; c<=(byte)'z'; c++) DEFAULT_BASE64_MAP[i++] = (char)c;
			for (byte c=(byte)'0'; c<=(byte)'9'; c++) DEFAULT_BASE64_MAP[i++] = (char)c;
			DEFAULT_BASE64_MAP[i++] = '+'; 
			DEFAULT_BASE64_MAP[i++] = '/'; 
		}
	}

	public static char[] encode (byte[] inbuf) { return encode(inbuf,inbuf.length,DEFAULT_BASE64_MAP); }
	public static char[] encode (byte[] inbuf, int iLen) { return encode(inbuf,iLen,DEFAULT_BASE64_MAP); }
	
	public static char[] encode (byte[] inbuf,char[] b64map) { return encode(inbuf,inbuf.length,b64map); }
	public static char[] encode (byte[] inbuf, int iLen,char[] b64map) {

	byte[] map2 = new byte[128];
	for (int i=0; i<map2.length; i++) map2[i] = -1;
	for (int i=0; i<64; i++) map2[(byte)b64map[i]] = (byte)i; 

		int oDataLen = (iLen*4+2)/3;       // output length without padding
		int oLen = ((iLen+2)/3)*4;         // output length including padding
		char[] out = new char[oLen];
		int ip = 0;
		int op = 0;
		while (ip < iLen) {
			int i0 = inbuf[ip++] & 0xff;
			int i1 = ip < iLen ? inbuf[ip++] & 0xff : 0;
			int i2 = ip < iLen ? inbuf[ip++] & 0xff : 0;
			int o0 = i0 >>> 2;
			int o1 = ((i0 &   3) << 4) | (i1 >>> 4);
			int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
			int o3 = i2 & 0x3F;
			out[op++] = b64map[o0];
			out[op++] = b64map[o1];
			out[op] = op < oDataLen ? b64map[o2] : '='; op++;
			out[op] = op < oDataLen ? b64map[o3] : '='; op++; 
		}
		return out; 
	}
	public static byte[] decode (char[] inbuf) { return decode(inbuf,inbuf.length,DEFAULT_BASE64_MAP); }
	public static byte[] decode (char[] inbuf,int iLen) { return decode(inbuf,iLen,DEFAULT_BASE64_MAP); }
	
	public static byte[] decode (char[] inbuf, char[] b64map) { return decode(inbuf,inbuf.length,b64map); }
	public static byte[] decode (char[] inbuf,int iLen, char[] b64map) {
		byte[] retbuf = new byte[(int)Math.ceil(iLen*3/4)];
		for(int i=0;i<iLen;i++) {
			if(inbuf[i]=='=') {
				byte[] tmpbuf = new byte[(int)Math.floor(i*3/4)];
				System.arraycopy(retbuf,0,tmpbuf,0,tmpbuf.length);
				retbuf=tmpbuf;
				break; 
			}
			retbuf = decode(retbuf,inbuf[i],i,iLen,b64map);
		}
		return retbuf;
	}
	
	public static byte[] decode (byte[] inbuf) { return decode(inbuf,inbuf.length,DEFAULT_BASE64_MAP); }
	public static byte[] decode (byte[] inbuf,int iLen) { return decode(inbuf,iLen,DEFAULT_BASE64_MAP); }
	
	
	public static byte[] decode (byte[] inbuf,char[] b64map) { return decode(inbuf,inbuf.length,b64map); }
	public static byte[] decode (byte[] inbuf,int iLen,char[] b64map) {
		byte[] retbuf = new byte[(int)Math.ceil(iLen*3/4)];
		for(int i=0;i<iLen;i++) {
			if(inbuf[i]=='=') {
				byte[] tmpbuf = new byte[(int)Math.floor(i*3/4)];
				System.arraycopy(retbuf,0,tmpbuf,0,tmpbuf.length);
				retbuf=tmpbuf;
				break; 
			}
			retbuf = decode(retbuf,(char)inbuf[i],i,iLen,b64map);
		}
		return retbuf;
	}
	private static byte[] decode (byte[] retbuf, char c, int i, int iLen,char[] b64map) {
		for(int j=0;j<b64map.length;j++) {
			if(c==b64map[j]) {
				int si = (int)Math.floor(i*3/4);
				int pi = i%4;
				switch(pi) {
					case 0:
						retbuf[si]=(byte)(j<<2);
						break;
					case 1:
						retbuf[si]=(byte)(retbuf[si]|(j>>4));
						if((si+1)>=retbuf.length) { break; }
						retbuf[si+1]=(byte)((j&0xf)<<4);
						break;
					case 2:
						retbuf[si]=(byte)(retbuf[si]|(j>>2));
						if((si+1)>=retbuf.length) { break; }
						retbuf[si+1]=(byte)((j&0x3)<<6);
						break;
					case 3:
						retbuf[si]=(byte)(retbuf[si]|j);
						break;
				};
				break;
			}
		}
		return retbuf;
	}
}
