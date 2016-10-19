package com.example.plugindemo;

import tws.component.log.TwsLog;

import com.tws.plugin.sharelib.SharePOJO;
import com.tws.plugin.sharelib.ShareService;

/**
 * Created by cailiming on 16/5/18.
 */
public class PluginSharedService implements ShareService {

	private static final String TAG = "rick_Pring:PluginSharedService";

	@Override
	public SharePOJO doSomething(String condition) {
		TwsLog.d(TAG, condition);
		return new SharePOJO(condition + " : 插件追加的文字");
	}
}
