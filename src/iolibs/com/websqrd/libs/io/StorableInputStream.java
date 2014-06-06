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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author lupfeliz
 *
 */
public class StorableInputStream extends InputStream {
	
	private InputStream is;
	private FileOutputStream fos;
	private File tmpFile;
	private byte[] buf;
	
	public StorableInputStream(int bufsize) throws IOException {
		buf = new byte[bufsize];
		tmpFile = File.createTempFile(StorableInputStream.class.getName(),".tmp");
		tmpFile.deleteOnExit();
		fos = new FileOutputStream(tmpFile);
		fos.flush();
		fos.close();
		this.is = new FileInputStream(tmpFile);
		
	}
	
	public StorableInputStream(InputStream is,int bufsize) throws IOException {
		this(bufsize);
		this.addStream(is);
	}
	
	public int rewind() throws IOException {
		this.is.close();
		this.is = new FileInputStream(tmpFile);
		return 0;
	}
	
	public int addStream(InputStream is) throws IOException {
		fos = new FileOutputStream(tmpFile,true);
		if(is !=null) {
			for(int rlen = 0; (rlen = is.read(buf,0,buf.length))!=-1; ) { fos.write(buf,0,rlen); }
		}
		try { fos.flush(); } catch (IOException e) { }
		try { fos.close(); } catch (IOException e) { }
		return 0;
	}

	@Override
	public int available() throws IOException {
		return is.available();
	}

	@Override
	public void close() throws IOException {
		is.close();
	}

	@Override
	public boolean equals(Object obj) {
		return is.equals(obj);
	}

	@Override
	public int hashCode() {
		return is.hashCode();
	}

	@Override
	public void mark(int readlimit) {
		is.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		return is.markSupported();
	}

	@Override
	public int read() throws IOException {
		return is.read();
	}

	@Override
	public int read(byte[] arg0, int arg1, int arg2) throws IOException {
		return is.read(arg0, arg1, arg2);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return is.read(b);
	}

	@Override
	public void reset() throws IOException {
		is.reset();
	}

	@Override
	public long skip(long arg0) throws IOException {
		return is.skip(arg0);
	}

	@Override
	public String toString() {
		return is.toString();
	}
	
	public void realClose() throws IOException {
		try { is.close(); } catch (IOException e) { }
		try { fos.close(); } catch (IOException e) { }
		tmpFile.delete();
		is.close();
	}
	
	public File getFile() {
		return tmpFile;
	}
	
	public void deleteTmp() {
		if(tmpFile!=null && tmpFile.exists() && tmpFile.isFile()) {
			try {
				tmpFile.delete();
			} catch (Exception e) { }
		}
	}
}
