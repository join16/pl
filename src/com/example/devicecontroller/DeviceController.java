package com.example.devicecontroller;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

public class DeviceController {
	
	private Context mContext;
	
	final private WifiManager mWifi;
	private String gpsSetting;
	final private BluetoothAdapter mBluetooth;
	final private ConnectivityManager mNetwork;
	
	/*----------- constructor -----------*/
	public DeviceController(Context _mContext) {
		mContext = _mContext;
		mWifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		gpsSetting = null;
		mBluetooth = BluetoothAdapter.getDefaultAdapter();
		mNetwork = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	public void switchWifi(boolean isEnable) {  // true => wi-fi on   false => wi-fi off
		mWifi.setWifiEnabled(isEnable);
	}
	public void switchGps(boolean isEnable) {  /* ....need system root permissions....*/
		Log.e("test", "function entered");
		if (isEnable) {
		    gpsSetting = Settings.Secure.getString(mContext.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			String newSet = String.format("%s,%s", gpsSetting, LocationManager.GPS_PROVIDER);
			try {
			Settings.Secure.putString(mContext.getContentResolver(),
			         Settings.Secure.LOCATION_PROVIDERS_ALLOWED,
			         newSet);
			} catch(Exception e) {
				Log.d("exception error", e.toString());
			}
		}
	}
	public void switchBluetooth(boolean isEnable) {
		if (isEnable) {
			mBluetooth.enable();
		} else {
			mBluetooth.disable();
		}
	}
	public void switchNetwork(boolean isEnable) {
		try {
		    final Class mNetworkClass = Class.forName(mNetwork.getClass().getName());
		    final Field iConnectivityManagerField = mNetworkClass.getDeclaredField("mService");
		    iConnectivityManagerField.setAccessible(true);
		    final Object iConnectivityManager = iConnectivityManagerField.get(mNetwork);
		    final Class iConnectivityManagerClass = Class.forName(
		        iConnectivityManager.getClass().getName());
		    final Method setMobileDataEnabledMethod = iConnectivityManagerClass
		        .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		    setMobileDataEnabledMethod.setAccessible(true);
		    setMobileDataEnabledMethod.invoke(iConnectivityManager, isEnable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	public void switchSync(boolean isEnable) {
		ContentResolver.setMasterSyncAutomatically(isEnable);
	}
}
