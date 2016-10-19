package com.tws.plugin.core.localservice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import tws.component.log.TwsLog;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.tws.plugin.core.PluginLoader;

/**
 * @author yongchen
 * 
 *         利用ContentProvider实现同步跨进程调用
 * 
 */
public class LocalServiceBinder extends ContentProvider {

	private static final String TAG = "rick_Print:LocalServiceBinder";
	private static Uri CONTENT_URI;

	public static Uri buildUri() {
		if (CONTENT_URI == null) {
			CONTENT_URI = Uri.parse("content://" + PluginLoader.getApplication().getPackageName() + ".localservice" + "/call");
		}
		return CONTENT_URI;
	}

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// doNothing
		return null;
	}

	@Override
	public String getType(Uri uri) {
		// doNothing
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// doNothing
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// doNothing
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// doNothing
		return 0;
	}

	@Override
	public Bundle call(String method, String arg, Bundle extras) {

		if (Build.VERSION.SDK_INT >= 19) {
			TwsLog.d(TAG, "callingPackage = " + getCallingPackage());
		}

		TwsLog.d(TAG, "Thead : id = " + Thread.currentThread().getId() + ", name = " + Thread.currentThread().getName() + ", method = " + method + ", arg = "
				+ arg);

		Object service = LocalServiceManager.getService(method);
		Object result = null;
		if (service != null) {
			Method[] methods = service.getClass().getInterfaces()[0].getDeclaredMethods();
			if (methods != null) {
				boolean found = false;
				for (Method m : methods) {
					if (m.toGenericString().equals(arg)) {
						try {
							if (!m.isAccessible()) {
								m.setAccessible(true);
							}
							result = m.invoke(service, ServiceBinderBridge.unwrapperParams(extras));
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
						found = true;
						break;
					}
				}
				if (!found) {
					TwsLog.e(TAG, "No Such Method:" + method + " arg:" + arg);
				}
			}
		} else {
			TwsLog.e(TAG, "service not Found Method:" + method + " arg:" + arg);
		}

		Bundle bundle = new Bundle();
		ServiceBinderBridge.putToBundle(bundle, "result", result);
		return bundle;
	}
}
