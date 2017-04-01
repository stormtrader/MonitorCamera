package com.example.administrator.monitorcamera;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity1 extends Activity {

    protected static final String TAG = "MainActivity";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		//关闭WIFI
        /*
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {  

        	wifiManager.setWifiEnabled(false);  
        	
        } */
        //获取当前网络
        ConnectivityManager conManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE); 
        NetworkInfo info = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
        String currentAPN = info.getExtraInfo(); 
        conManager.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "mms");
        currentAPN = info.getExtraInfo(); 
        //只有CMWAP才能发送彩信
        Log.i(TAG,"jlzou currentAPN:" + currentAPN);
        if("cmwap".equals(currentAPN))
        {
        	sendMMS();
        }
        sendSMS();
    }
    
    private void sendMMS()
    {
    	final MMSInfo mms = new MMSInfo(this, "13392162106");//发送的手机号
    	Uri path = getUri();
    	Log.i(TAG,"jlzou path:" + path);
        System.out.println("--->" + path);
        mms.addPart(path.toString());
        final MMSSender sender = new MMSSender();
        new Thread() {
                public void run() {
                        try {
                                byte[] res = sender.sendMMS(MainActivity1.this,mms.getMMSBytes());
                                System.out.println("-==-=-=>>> " + res.toString());
                        } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                };
        }.start();
    }
    
    private Uri getUri(){
    	String picPath = "/storage/sdcard0/testcamera/20170317_085932.jpg";
    	Uri mUri = Uri.parse("content://media/external/images/media"); 
        Uri mImageUri = null;

        Cursor cursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String data = cursor.getString(cursor
                    .getColumnIndex(MediaStore.MediaColumns.DATA));
            if (picPath.equals(data)) {
            	Log.i(TAG,"jlzou picPath:" + picPath);
                int ringtoneID = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.MediaColumns._ID));
                mImageUri = Uri.withAppendedPath(mUri, ""
                        + ringtoneID);
                return mImageUri;
            }
            cursor.moveToNext();
        }
		return null;
    }
    
    private void sendSMS(){
    	SMSSender.sendSMS("13802258141", "hello man!");
    }
    
}
