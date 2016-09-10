package com.example.admin.educhat.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.admin.educhat.Constants;
import com.example.admin.educhat.OtpActivity;


public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = SmsReceiver.class.getSimpleName();
    OtpActivity main = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "sms");
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.e(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);
                    if (!senderAddress.contains(Constants.SMS_ORIGIN)) {
                        return;
                    }
                    String verificationCode = getVerificationCode(message);

                    Log.e(TAG, "OTP received: " + verificationCode);
                   main.receiveSms(verificationCode);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: ");
            e.printStackTrace();
        }
    }

    private String getVerificationCode(String message) {

        String[] code = message.split(" ");
        try {
            Integer.parseInt(code[0]);
        } catch(NumberFormatException e) {
            return "";
        } catch(NullPointerException e) {
            return "";
        }
        return code[0];
    }
    public void setMainActivityHandler(OtpActivity main){
        this.main=main;
    }
}