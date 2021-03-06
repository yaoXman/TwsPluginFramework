package com.tws.plugin.util;

import tws.component.log.TwsLog;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.Build;

import com.tws.plugin.core.PluginLoader;
import com.tws.plugin.core.manager.PluginManagerProvider;

public class ProcessUtil {

	private static final String TAG = "rick_Print:=========ProcessUtil";
	private static Boolean isPluginProcess = null;

	public ProcessUtil() {
	}

	public static boolean isPluginProcess(Context context) {
		if (isPluginProcess == null) {
			String processName = getCurProcessName(context);
			String pluginProcessName = getPluginProcessName(context);

			isPluginProcess = processName.equals(pluginProcessName);
			TwsLog.d(TAG, "isPluginProcess == null processName=" + processName + " pluginProcessName=" + pluginProcessName + " isPluginProcess="
					+ isPluginProcess);
		} else {
			TwsLog.d(TAG, "isPluginProcess=" + isPluginProcess + " context is " + context);
		}
		return isPluginProcess;
	}

	public static void printInfo() {
		final Context context = PluginLoader.getApplication();
		String processName = getCurProcessName(context);
		String pluginProcessName = getPluginProcessName(context);
		TwsLog.d(TAG, "processName=" + processName + " pluginProcessName=" + pluginProcessName);
	}

	public static boolean isPluginProcess() {
		return isPluginProcess(PluginLoader.getApplication());
	}

	private static String getCurProcessName(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
			if (appProcess.pid == android.os.Process.myPid()) {
				return appProcess.processName;
			}
		}
		return "";
	}

	private static String getPluginProcessName(Context context) {
		try {
			if (Build.VERSION.SDK_INT >= 9) {
				// 这里取个巧, 直接查询ContentProvider的信息中包含的processName
				// 因为Contentprovider是被配置在插件进程的.
				// 但是这个api只支持9及以上,
				ProviderInfo pinfo = context.getPackageManager().getProviderInfo(new ComponentName(context, PluginManagerProvider.class), 0);
				return pinfo.processName;
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
}
