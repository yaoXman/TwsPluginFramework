package com.tws.plugin.core.localservice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tws.component.log.TwsLog;

import com.tws.plugin.content.LoadedPlugin;
import com.tws.plugin.content.PluginDescriptor;
import com.tws.plugin.core.PluginLauncher;
import com.tws.plugin.util.ProcessUtil;

/**
 * Created by cailiming on 16/1/1.
 */
public class LocalServiceManager {

	private static final HashMap<String, LocalServiceFetcher> SYSTEM_SERVICE_MAP = new HashMap<String, LocalServiceFetcher>();
	protected static final String TAG = "LocalServiceManager";

	private LocalServiceManager() {
	}

	public static void registerService(PluginDescriptor plugin) {
		HashMap<String, String> localServices = plugin.getFunctions();
		if (localServices != null) {
			Iterator<Map.Entry<String, String>> serv = localServices.entrySet().iterator();
			while (serv.hasNext()) {
				Map.Entry<String, String> entry = serv.next();
				LocalServiceManager.registerService(plugin.getPackageName(), entry.getKey(), entry.getValue());
			}
		}
	}

	public static void registerService(final String pluginId, final String serviceName, final String serviceClass) {
		if (!SYSTEM_SERVICE_MAP.containsKey(serviceName)) {
			LocalServiceFetcher fetcher = new LocalServiceFetcher() {
				@Override
				public Object createService(int serviceId) {
					mPluginId = pluginId;

					String[] classNames = serviceClass.split("\\|");
					if (ProcessUtil.isPluginProcess()) {
						// 插件可能尚未初始化，确保使用前已经初始化
						LoadedPlugin plugin = PluginLauncher.instance().startPlugin(pluginId);
						if (plugin != null) {
							try {
								Class clazz = plugin.pluginClassLoader.loadClass(classNames[0]);
								return clazz.newInstance();
							} catch (Exception e) {
								TwsLog.e(TAG, "获取服务失败", e);
							}
						} else {
							TwsLog.e(TAG, "未找到插件:" + pluginId);
						}
					} else if (classNames.length == 2) {
						return ServiceBinderBridge.queryService(serviceName, classNames[1]);
					} else {
						TwsLog.e(TAG, "不支持跨进程!!! 插件 id:" + pluginId + " 服务Name is " + serviceName + " serviceClass is " + serviceClass);
					}
					return null;
				}
			};
			fetcher.mServiceId++;
			SYSTEM_SERVICE_MAP.put(serviceName, fetcher);
			TwsLog.d(TAG, "registerService:" + serviceName);
		} else {
			TwsLog.e(TAG, "已注册 serviceName=" + serviceName);
		}
	}

	public static Object getService(String name) {
		LocalServiceFetcher fetcher = SYSTEM_SERVICE_MAP.get(name);
		return fetcher == null ? null : fetcher.getService();
	}

	public static void unRegistService(PluginDescriptor plugin) {
		Iterator<Map.Entry<String, LocalServiceFetcher>> itr = SYSTEM_SERVICE_MAP.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, LocalServiceFetcher> item = itr.next();
			if (plugin.getPackageName().equals(item.getValue().mPluginId)) {
				itr.remove();
			}
		}
	}

	public static void unRegistAll() {
		SYSTEM_SERVICE_MAP.clear();
	}

}
