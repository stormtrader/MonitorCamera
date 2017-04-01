package com.example.administrator.monitorcamera;

import android.telephony.SmsManager;

public class SMSSender {
    public static void sendSMS(String destinationAddress,String text){
    	SmsManager sms = SmsManager.getDefault();
    	sms.sendTextMessage(destinationAddress, null, text, null, null);
    }
}
