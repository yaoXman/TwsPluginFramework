package com.example.plugindemo.service;

import tws.component.log.TwsLog;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.example.plugindemo.R;
import com.example.plugindemo.vo.ParamVO;
import com.tws.plugin.test.IMyAidlInterface;

/**
 * @author cailiming
 * 
 */
public class PluginTestService extends Service {
	private static String TAG = "PluginTestService";

	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, " PluginTestService onCreate " + getResources().getText(R.string.hello_world3), Toast.LENGTH_LONG).show();

		Log.d("xx", "PluginTestService onCreate" + getApplication() + " " + getApplicationContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			Log.d("xx", ((ParamVO) intent.getSerializableExtra("paramvo")) + ", action:" + intent.getAction());
		}

		Log.d("PluginTestService", "PluginTestService onStartCommand " + " " + getResources().getText(R.string.hello_world3));

		Toast.makeText(this, " PluginTestService " + getResources().getText(R.string.hello_world3), Toast.LENGTH_LONG).show();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("xx", "PluginTestService onDestroy");
		Toast.makeText(this, "停止PluginTestService", Toast.LENGTH_LONG).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new IMyAidlInterface.Stub() {

			@Override
			public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
				TwsLog.d(TAG, "aString is " + aString + " anInt:" + anInt + " aLong:" + aLong);
			}
		};
	}

}
