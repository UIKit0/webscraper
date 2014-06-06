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

package com.websqrd.libs.common;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicClassLoader {
	private final static Logger logger = LoggerFactory.getLogger(DynamicClassLoader.class);
	public static String OS_NAME = System.getProperty("os.name");

//	private Map<String, URLClassLoader> classLoaderList = new HashMap<String, URLClassLoader>();
	private static Map<String, URLClassLoader> classLoaderList = new HashMap<String, URLClassLoader>();
	
	private Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();

	private String homePath;
	private String jarPath;
	private String pathSeparator;

	public DynamicClassLoader() {
	}

	public DynamicClassLoader(String homePath, String jarPath) {
		this(homePath, jarPath, System.getProperty("path.separator"));
	}

	public DynamicClassLoader(String homePath, String jarPath, String pathSeparator) {
		this.homePath = homePath;
		this.jarPath = jarPath;
		this.pathSeparator = pathSeparator;

		if (homePath.length() > 0 && !homePath.endsWith("/") && !homePath.endsWith("\\")) {
			homePath = homePath + System.getProperty("file.separator");
			;
		}
	}

	public boolean start() {

		if (jarPath != null) {
			// ',' makes different classloader
			String[] pathList = jarPath.split(",");
			for (int i = 0; i < pathList.length; i++) {
				String tmp = pathList[i];
				String[] tl = tmp.split(pathSeparator);
				for (int k = 0; k < tl.length; k++) {
					tl[k] = tl[k].trim();
					if (tl[k].length() == 0)
						continue;

					if (OS_NAME.startsWith("Windows")) {
						if (!tl[k].matches("^[a-zA-Z]:\\\\.*")) {
							tl[k] = homePath + tl[k];
						}
					} else {
						if (!tl[k].startsWith("/") && !tl[k].startsWith("\\")) {
							tl[k] = homePath + tl[k];
						}
					}
				}
				addClassLoader(tmp, tl);
			}

		}
		logger.info("ScrapClassLoader statrted!");
		return true;
	}

	public boolean restart() {
		shutdown();
		start();
		return true;
	}

	public boolean shutdown() {
		classLoaderList.clear();
		classCache.clear();
		logger.info("shutdown IRClassLoader");
		return true;
	}

	public boolean addClassLoader(String tag, String[] jarFilePath) {
		URL[] jarUrls = new URL[jarFilePath.length];
		for (int i = 0; i < jarFilePath.length; i++) {
			File f = new File(jarFilePath[i]);
			try {
				jarUrls[i] = f.toURI().toURL();
				logger.debug("Add jar = " + jarUrls[i]);
			} catch (MalformedURLException e) {
				logger.error("Dynamic jar filepath is strange. path=" + jarFilePath[i] + "(" + f.getAbsolutePath() + ")", e);
			}
		}

		URLClassLoader l = new URLClassLoader(jarUrls);
		classLoaderList.put(tag, l);
		logger.info("Class Loader " + tag + " = " + l);
		return true;
	}

	public Object loadObject(String className) {
		// 1. cache?
		Class<?> clazz = classCache.get(className);
		if (clazz != null) {
			try {
				return clazz.newInstance();
			} catch (InstantiationException e) {
				logger.warn("", e);
			} catch (IllegalAccessException e) {
				logger.warn("", e);
			} catch (Exception e) {
				logger.error("", e);
			}
			return null;
		}
		// 2. default CL?
		try {
			clazz = Class.forName(className);
			if (clazz != null) {
				try {
					return clazz.newInstance();
				} catch (InstantiationException e) {
					logger.warn("", e);
				} catch (IllegalAccessException e) {
					logger.warn("", e);
				} catch (Exception e) {
					logger.error("", e);
				}
				return null;
			}
		} catch (ClassNotFoundException e) {
			logger.debug("Not found default class " + className);
		}

		// 3. custom CL?
		Iterator<URLClassLoader> iter = classLoaderList.values().iterator();
		while (iter.hasNext()) {
			URLClassLoader l = (URLClassLoader) iter.next();
			try {
				clazz = Class.forName(className, true, l);
			} catch (ClassNotFoundException e) {

				continue;
			}

			if (clazz != null) {
				try {
					logger.info("Found dynamic class " + className);
					return clazz.newInstance();
				} catch (InstantiationException e) {
					logger.warn("", e);
				} catch (IllegalAccessException e) {
					logger.warn("", e);
				} catch (Exception e) {
					logger.error("", e);
				}
				return null;
			} else {
				logger.error("Not found dynamic class " + className);
			}
		}
		return null;
	}

	public Object loadObject(String className, Class<?>[] paramTypes, Object[] initargs) {
		// 1. cache?
		Class<?> clazz = classCache.get(className);
		if (clazz != null) {
			try {
				Constructor<?> constructor = clazz.getConstructor(paramTypes);
				return constructor.newInstance(initargs);
			} catch (InstantiationException e) {
				logger.warn("", e);
			} catch (IllegalAccessException e) {
				logger.warn("", e);
			} catch (SecurityException e) {
				logger.warn("", e);
			} catch (NoSuchMethodException e) {
				logger.warn("", e);
			} catch (IllegalArgumentException e) {
				logger.warn("", e);
			} catch (InvocationTargetException e) {
				logger.warn("", e);
			} catch (Exception e) {
				logger.error("", e);
			}
			return null;
		}

		// 2. default CL?
		try {
			clazz = Class.forName(className);
			if (clazz != null) {
				try {
					Constructor<?> constructor = clazz.getConstructor(paramTypes);
					return constructor.newInstance(initargs);
				} catch (InstantiationException e) {
					logger.warn("", e);
				} catch (IllegalAccessException e) {
					logger.warn("", e);
				} catch (SecurityException e) {
					logger.warn("", e);
				} catch (NoSuchMethodException e) {
					logger.warn("", e);
				} catch (IllegalArgumentException e) {
					logger.warn("", e);
				} catch (InvocationTargetException e) {
					logger.warn("", e);
				} catch (Exception e) {
					logger.error("", e);
				}
				return null;
			}
		} catch (ClassNotFoundException e) {
			logger.debug("Not found default class " + className);
		}

		// 3. custom CL?
		Iterator<URLClassLoader> iter = classLoaderList.values().iterator();
		while (iter.hasNext()) {
			URLClassLoader l = (URLClassLoader) iter.next();
			try {

				clazz = Class.forName(className, true, l);
				logger.info("clazz={}", clazz);
			} catch (ClassNotFoundException e) {

				continue;
			}

			if (clazz != null) {
				try {
					Constructor<?> constructor = clazz.getConstructor(paramTypes);
					logger.info("Found dynamic class " + className);
					return constructor.newInstance(initargs);
				} catch (InstantiationException e) {
					logger.error("", e);
				} catch (IllegalAccessException e) {
					logger.error("", e);
				} catch (SecurityException e) {
					logger.error("", e);
				} catch (NoSuchMethodException e) {
					logger.error("", e);
				} catch (IllegalArgumentException e) {
					logger.error("", e);
				} catch (InvocationTargetException e) {
					logger.error("", e);
				} catch (Exception e) {
					logger.error("", e);
				}
				return null;
			} else {
				logger.error("Not found dynamic class " + className);
			}
		}
		return null;
	}

	public static Class<?> loadClass(String className) {

		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
			if (clazz != null) {
				return clazz;
			}
		} catch (ClassNotFoundException ignore) {

		}

		synchronized (classLoaderList) {
			Iterator<URLClassLoader> iter = classLoaderList.values().iterator();
			while (iter.hasNext()) {
				URLClassLoader l = (URLClassLoader) iter.next();
				try {
					clazz = Class.forName(className, true, l);
				} catch (ClassNotFoundException e) {
					continue;
				}

				if (clazz != null) {
					return clazz;
				}
			}
		}

		logger.warn("Classloader cannot find {}", className);
		return null;
	}

	public static <T> T loadObject(String className, Class<T> type, Class<?>[] paramTypes, Object[] initargs) {
		try {
			Class<?> clazz = loadClass(className);
			if (clazz != null) {
				try {
					Constructor<?> constructor = clazz.getConstructor(paramTypes);
					return (T) constructor.newInstance(initargs);
				} catch (NoSuchMethodException e) {
					logger.trace("해당 생성자가 없습니다. {} >> {} {} {} {} {} {} {}", className, paramTypes);
				}
			}
		} catch (Exception ignore) {
			ignore.printStackTrace();
		}
		return null;
	}

}
