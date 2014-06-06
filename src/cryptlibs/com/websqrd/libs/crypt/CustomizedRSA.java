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

import java.util.Random;
import java.math.BigInteger;
import java.io.ByteArrayOutputStream;

public class CustomizedRSA {
	private static final int certainty = 50;

	public static byte[][] generateKey(int keyLength) {
		boolean found = false;
		BigInteger p,q,phi,e,n,d;
		p = new BigInteger(keyLength/2,certainty, new Random());
		q = new BigInteger(keyLength/2+1,certainty, new Random());
		phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		do {
			e = new BigInteger(keyLength, certainty, new Random());
			if(phi.gcd(e).equals(BigInteger.ONE)) { found = true; }
		} while(!found);
		n = p.multiply(q);
		d = e.modInverse(phi);

		byte[][] ret = new byte[3][];

		byte[] cbuf = null;
		byte[] rbuf = new byte[(int)Math.ceil(keyLength/8)+1];

		//byte array size reallocating with zero padding
		cbuf = e.toByteArray();
		rbuf = new byte[rbuf.length];
		System.arraycopy(cbuf,0,rbuf,(rbuf.length-cbuf.length),cbuf.length);
		ret[0]=rbuf;

		cbuf = d.toByteArray();
		rbuf = new byte[rbuf.length];
		System.arraycopy(cbuf,0,rbuf,(rbuf.length-cbuf.length),cbuf.length);
		ret[1]=rbuf;

		cbuf = n.toByteArray();
		rbuf = new byte[rbuf.length];
		System.arraycopy(cbuf,0,rbuf,(rbuf.length-cbuf.length),cbuf.length);
		ret[2]=rbuf;

		return ret;
	}
	public static byte[] encrypt(byte[] msg,byte[] key,byte[] mod) {
		BigInteger m,e,n;
		e = new BigInteger(key);
		n = new BigInteger(mod);
		byte[] buf = null;
		buf = new byte[key.length];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//byte array divide into same size reallocating with zero padding
		for(int i=0,j=0;i<=msg.length;i++) {
			if(i>0 && ((i%(buf.length-2))==0 || (i==msg.length))) {
				buf = new byte[buf.length];
				System.arraycopy(msg,j,buf,buf.length-(i-j),(i-j));
				//padding first bit set to 1 to deter ignoring front zero-padding byte
				buf[buf.length-(i-j)-1]=1;
				m = new BigInteger(buf);
				BigInteger iret = m.modPow(e,n);
				byte[] rbuf = iret.toByteArray();
				for(int k=rbuf.length;k<key.length;k++) { baos.write(0); }
				baos.write(rbuf,0,rbuf.length);
				j=i;
			}
		}

		byte[] ret = baos.toByteArray();
		return ret;
	}
	public static byte[] decrypt(byte[] msg,byte[] key,byte[] mod) {
		BigInteger m,d,n;
		d = new BigInteger(key);
		n = new BigInteger(mod);
		byte[] buf = null;
		buf = new byte[key.length];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//byte array must be combined same size with zero padding
		for(int i=0,j=0;i<=msg.length;i++) {
			if(i>0 && ((i%buf.length)==0 || (i==msg.length))) {
				buf = new byte[buf.length];
				System.arraycopy(msg,j,buf,buf.length-(i-j),(i-j));
				m = new BigInteger(buf);
				BigInteger iret = m.modPow(d,n);
				byte[] rbuf = iret.toByteArray();
				//ignore padding byte
				baos.write(rbuf,1,rbuf.length-1);
				j=i;
			}
		}
		byte[] ret = baos.toByteArray();
		return ret;
	}

	public static String mergePublicKey(byte[][] keys) {
		return new String(Base64.encode(keys[0]))+new String(Base64.encode(keys[2]));
	}

	public static String mergePrivateKey(byte[][] keys) {
		return new String(Base64.encode(keys[1]))+new String(Base64.encode(keys[2]));
	}

	public static byte[][] takeKey(String key) {
		//key must be same size with zero padding
		byte[][] ret = new byte[2][];
		ret[0] = Base64.decode(key.substring(0,key.length()/2).getBytes());
		ret[1] = Base64.decode(key.substring(key.length()/2).getBytes());
		return ret;
	}

//	public static void main(String[] arg) throws Exception {
//		Base64 b64 = new Base64();
//		String msg = ""+
//		"abc"+
//		"!";
//		//byte[] b = msg.getBytes();
//		java.io.File f = new java.io.File(".cache/0NJmfL5CPYgKPxg3MiSPJK==");
//		byte[] b = new byte[(int)f.length()];
//		java.io.FileInputStream fis = new java.io.FileInputStream(f);
//		fis.read(b,0,b.length);
//		fis.close();
//
//		byte[][] keys = RSA.generateKey(1024);
//		
//		String pubk = RSA.mergePublicKey(keys);
//		String prik = RSA.mergePrivateKey(keys);
//
//		byte[][] pubkb = RSA.takeKey(pubk);
//		byte[][] prikb = RSA.takeKey(prik);
//
//		byte[] c = RSA.encrypt(b,pubkb[0],pubkb[1]);
//		byte[] r = RSA.decrypt(c,prikb[0],prikb[1]);
//
//		java.io.File f2 = new java.io.File(".cache/abc");
//		java.io.FileOutputStream fos = new java.io.FileOutputStream(f2);
//		fos.write(r,0,r.length);
//		fos.close();
//
//		System.out.println(pubk);
//		System.out.println(prik);
//	}
}
