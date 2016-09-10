package com.example.admin.educhat;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.admin.educhat.utils.SmsReceiver;
import com.example.admin.educhat.utils.Urls;
import com.example.admin.educhat.utils.WebRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;


public class OtpActivity extends BaseActivity {
    SmsReceiver receiver;
    String mobileNumber;
    String TYPE;
    EditText etOtp;
    RelativeLayout rlMain, rlBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(20f);
        }
        else {
            ViewCompat.setElevation(toolbar,20f);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            mobileNumber = getIntent().getExtras().getString("Mobile Number");
            TYPE = getIntent().getExtras().getString("TYPE");
        }

        etOtp = (EditText) findViewById(R.id.et_otp);
        rlMain = (RelativeLayout) findViewById(R.id.rl_main);
        rlBackground = (RelativeLayout) findViewById(R.id.rl_background);

        sendOtp();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Constants.OTP_FLAG) {
                    rlBackground.setVisibility(View.GONE);
                    rlMain.setVisibility(View.VISIBLE);
                }
            }
        }, 30000);

        receiver = new SmsReceiver();
        receiver.setMainActivityHandler(this);
        IntentFilter fltr_smsreceived = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver, fltr_smsreceived);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickOtpVerify(View view) {
        verifyOtp();
    }

    public void onClickReset(View view) {
        rlMain.setVisibility(View.GONE);
        rlBackground.setVisibility(View.VISIBLE);
        sendOtp();
    }

    public void sendOtp() {
        JSONObject data = new JSONObject();
        Constants.OTP_FLAG = true;
        try {
            Log.d("NUMBER", mobileNumber);
            data.put("mobile", mobileNumber);
            if (TYPE.equals(Constants.SIGN_UP_ACTIVITY)) {
                WebRequest request = new WebRequest(this, otpRequestHandler, Urls.OTP_REQUEST, "POST");
                request.execute(String.valueOf(data));
            }
            else {
                WebRequest request = new WebRequest(this, otpRequestHandler, Urls.FORGOT_PASSWORD, "POST");
                request.execute(String.valueOf(data));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void verifyOtp() {
        String otp = etOtp.getText().toString();
        try {
            JSONObject data = new JSONObject();
            Log.d("OTP final", otp);
            data.put("mobile", mobileNumber);
            data.put("otp", otp);
            if (TYPE.equals(Constants.SIGN_UP_ACTIVITY)) {
                WebRequest request = new WebRequest(this, otpVerifyHandler, Urls.OTP_VERIFY, "PUT");
                request.execute(String.valueOf(data));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void receiveSms(String otp) {
        try {
            rlMain.setVisibility(View.VISIBLE);
            rlBackground.setVisibility(View.GONE);
            etOtp.setText(otp);
            verifyOtp();
            Log.d("Code", otp);
        } catch (Exception e) {
        }
    }

    Handler otpRequestHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.arg1 == HttpURLConnection.HTTP_OK) {

            }
            return false;
        }
    });

    Handler otpVerifyHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.arg1 == HttpURLConnection.HTTP_OK) {
                if (TYPE.equals(Constants.SIGN_UP_ACTIVITY)) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
            return false;
        }
    });

    @Override
    protected void onStop() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onStop();
    }
}
