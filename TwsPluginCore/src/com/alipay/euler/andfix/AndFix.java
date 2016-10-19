/*
 * 
 * Copyright (c) 2015, alipay.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alipay.euler.andfix;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import tws.component.log.TwsLog;
import android.os.Build;

/**
 * Native interface
 * 
 * @author sanping.li@alipay.com
 * 
 */
public class AndFix {
	private static final String TAG = "rick_Print:AndFix";

	static {
		try {
			Runtime.getRuntime().loadLibrary("andfix");
		} catch (Throwable e) {
			TwsLog.e(TAG, "loadLibrary", e);
		}
	}

	private static native boolean setup(boolean isArt, int apilevel);

	private static native void replaceMethod(Method dest, Method src);

	private static native void setFieldFlag(Field field);

	/**
	 * replace method's body
	 * 
	 * @param src
	 *            source method
	 * @param dest
	 *            target method
	 * 
	 */
	public static void addReplaceMethod(Method src, Method dest) {
		try {
			replaceMethod(src, dest);
			initFields(dest.getDeclaringClass());
		} catch (Throwable e) {
			TwsLog.e(TAG, "addReplaceMethod", e);
		}
	}

	/**
	 * initialize the target class, and modify access flag of class’ fields to
	 * public
	 * 
	 * @param clazz
	 *            target class
	 * @return initialized class
	 */
	public static Class<?> initTargetClass(Class<?> clazz) {
		try {
			Class<?> targetClazz = Class.forName(clazz.getName(), true, clazz.getClassLoader());

			initFields(targetClazz);
			return targetClazz;
		} catch (Exception e) {
			TwsLog.e(TAG, "initTargetClass", e);
		}
		return null;
	}

	/**
	 * modify access flag of class’ fields to public
	 * 
	 * @param clazz
	 *            class
	 */
	private static void initFields(Class<?> clazz) {
		Field[] srcFields = clazz.getDeclaredFields();
		for (Field srcField : srcFields) {
			TwsLog.d(TAG, "modify " + clazz.getName() + "." + srcField.getName() + " flag:");
			setFieldFlag(srcField);
		}
	}

	/**
	 * initialize
	 * 
	 * @return true if initialize success
	 */
	public static boolean setup() {
		try {
			final String vmVersion = System.getProperty("java.vm.version");
			boolean isArt = vmVersion != null && vmVersion.startsWith("2");
			int apilevel = Build.VERSION.SDK_INT;
			return setup(isArt, apilevel);
		} catch (Exception e) {
			TwsLog.e(TAG, "setup", e);
			return false;
		}
	}
}
