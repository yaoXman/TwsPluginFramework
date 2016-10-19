package com.tws.plugin.core.systemservice;

import java.lang.reflect.Method;

import tws.component.log.TwsLog;
import android.app.NotificationManager;
import android.widget.Toast;

import com.tws.plugin.core.PluginLoader;
import com.tws.plugin.core.proxy.MethodDelegate;
import com.tws.plugin.core.proxy.MethodProxy;
import com.tws.plugin.core.proxy.ProxyUtil;
import com.tws.plugin.util.RefInvoker;

/**
 * @author yongchen
 */
public class AndroidWidgetToast extends MethodProxy {

	private static final String TAG = "rick_Print:AndroidWidgetToast";

	static {
		sMethods.put("enqueueToast", new enqueueToast());
		sMethods.put("cancelToast", new cancelToast());
	}

	public static void installProxy() {
		TwsLog.d(TAG, "安装NotificationManagerProxy");
		Object androidAppINotificationStubProxy = RefInvoker.invokeStaticMethod(Toast.class.getName(), "getService", (Class[]) null, (Object[]) null);
		Object androidAppINotificationStubProxyProxy = ProxyUtil.createProxy(androidAppINotificationStubProxy, new AndroidWidgetToast());
		RefInvoker.setStaticOjbect(NotificationManager.class.getName(), "sService", androidAppINotificationStubProxyProxy);
		TwsLog.d(TAG, "安装完成");
	}

	public static class enqueueToast extends MethodDelegate {
		@Override
		public Object beforeInvoke(Object target, Method method, Object[] args) {
			TwsLog.e(TAG, "enqueueToast beforeInvoke method:" + method.getName());
			args[0] = PluginLoader.getApplication().getPackageName();
			return super.beforeInvoke(target, method, args);
		}
	}

	public static class cancelToast extends MethodDelegate {
		@Override
		public Object beforeInvoke(Object target, Method method, Object[] args) {
			TwsLog.e(TAG, "cancelToast beforeInvoke method:" + method.getName());
			args[0] = PluginLoader.getApplication().getPackageName();
			return super.beforeInvoke(target, method, args);
		}
	}

}
