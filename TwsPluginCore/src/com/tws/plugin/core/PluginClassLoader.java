package com.tws.plugin.core;

import java.util.ArrayList;
import java.util.List;

import tws.component.log.TwsLog;

import com.tws.plugin.content.LoadedPlugin;

import dalvik.system.DexClassLoader;

/**
 * 为了支持插件间依赖，增加此类。如果不需要使用插件依赖插件, 此类为多余。直接使用DexClassLoader即可
 * 
 * @author yongchen
 * 
 */
public class PluginClassLoader extends DexClassLoader {

	private static final String TAG = "rick_Print:PluginClassLoader";
	private String[] dependencies;
	private List<DexClassLoader> multiDexClassLoaderList;

	public PluginClassLoader(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent, String[] dependencies, List<String> multiDexList) {
		super(dexPath, optimizedDirectory, libraryPath, parent);
		this.dependencies = dependencies;

		if (multiDexList != null) {
			if (multiDexClassLoaderList == null) {
				multiDexClassLoaderList = new ArrayList<DexClassLoader>(multiDexList.size());
				for (String path : multiDexList) {
					multiDexClassLoaderList.add(new DexClassLoader(path, optimizedDirectory, libraryPath, parent));
				}
			}
		}
	}

	@Override
	public String findLibrary(String name) {
		TwsLog.d(TAG, "findLibrary:" + name);
		return super.findLibrary(name);
	}

	@Override
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		TwsLog.d(TAG, "findClass:" + className);
		Class<?> clazz = null;
		ClassNotFoundException suppressed = null;
		try {
			clazz = super.findClass(className);
		} catch (ClassNotFoundException e) {
			suppressed = e;
		}

		// 这里判断android.view 是为了解决webview的问题
		if (clazz == null && !className.startsWith("android.view")) {

			if (multiDexClassLoaderList != null) {
				for (DexClassLoader dexLoader : multiDexClassLoaderList) {
					try {
						clazz = dexLoader.loadClass(className);
					} catch (ClassNotFoundException e) {
					}
					if (clazz != null) {
						break;
					}
				}
			}

			if (clazz == null && dependencies != null) {
				for (String dependencePluginId : dependencies) {

					// 插件可能尚未初始化，确保使用前已经初始化
					LoadedPlugin plugin = PluginLauncher.instance().startPlugin(dependencePluginId);

					if (plugin != null) {
						try {
							clazz = plugin.pluginClassLoader.loadClass(className);
						} catch (ClassNotFoundException e) {
						}
						if (clazz != null) {
							break;
						}
					} else {
						TwsLog.e(TAG, "未找到插件 - id=" + dependencePluginId + " name is " + className);
					}
				}
			}
		}

		if (clazz == null && suppressed != null) {
			throw suppressed;
		}

		return clazz;
	}
}
