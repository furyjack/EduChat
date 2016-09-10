package com.example.admin.educhat;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.admin.educhat.utils.PreferenceManager;
import com.example.admin.educhat.utils.Urls;
import com.example.admin.educhat.utils.Utility;
import com.example.admin.educhat.utils.WebRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;


//import com.edunutspartners.R;
//import com.edunutspartners.models.InstituteModels;
//import com.edunutspartners.utils.Constants;
//import com.edunutspartners.utils.PreferenceManager;
//import com.edunutspartners.utils.Urls;
//import com.edunutspartners.utils.Utility;
//import com.edunutspartners.utils.WebRequest;


public class Signup extends BaseActivity {

    CheckBox cbTermAndConditions;
    EditText etFullName, etPassword, etMobileNumber, etEmail;
    RadioButton rbMale, rbFemale;
    TextView tvTnC;
    AutoCompleteTextView etInstituteName;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;

    String fullName, password, email, mobileNumber, instituteName, gender, credits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(20f);
        }
        else {
            ViewCompat.setElevation(toolbar,20f);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        arrayList = new ArrayList<>();

        etFullName = (EditText) findViewById(R.id.et_username);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        etPassword.setTypeface(Typeface.DEFAULT);
        etPassword.setTransformationMethod(new PasswordTransformationMethod());

        etInstituteName = (AutoCompleteTextView) findViewById(R.id.et_institution_name);
        etInstituteName.addTextChangedListener(watcher);

        etMobileNumber = (EditText) findViewById(R.id.et_phone_number);

        rbMale = (RadioButton) findViewById(R.id.rb_male);
        rbMale.setChecked(true);
        rbFemale = (RadioButton) findViewById(R.id.rb_female);

        cbTermAndConditions = (CheckBox) findViewById(R.id.cb_tnc);
        tvTnC = (TextView) findViewById(R.id.tnc);
        tvTnC.setText(Html.fromHtml("<a href=>Term & Conditions</a>"));
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Log.d("i", "" + i);
            WebRequest request = new WebRequest(getApplicationContext(), instituteHandler, Urls.INSTITUTE_URL + charSequence, "GET");
            request.execute("");
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickLogIn(View view) {
        finish();
    }

    public void onClickSignUp(View view) {
        if (isValid()) {
            JSONObject data = new JSONObject();
            try {
                data.put("name", fullName);
                data.put("password", password);
                data.put("email", email);
                data.put("mobile", mobileNumber);
                if (rbMale.isChecked())
                    gender = "m";
                else gender = "f";
                data.put("gender", gender);
                data.put("inst_name", instituteName);
                WebRequest request = new WebRequest(this, handler, Urls.SIGN_UP_URL, "POST");
                request.execute(data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void onClickTermAndConditions(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.edunuts.com/terms-of-use"));
        startActivity(browserIntent);
    }

    public boolean isValid() {
        fullName = etFullName.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        instituteName = etInstituteName.getText().toString();
        mobileNumber = etMobileNumber.getText().toString();
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || mobileNumber.isEmpty() || instituteName.isEmpty())
            setToastMessage("Field can not be Empty");
        else if (!Utility.isValidEmail(email))
            setToastMessage("Enter a valid Email");
        else if (password.length() < 6)
            setToastMessage("Password must be of 6 digits");
        else if (mobileNumber.length() != 10)
            setToastMessage("Mobile number must be of 10 digits");
        else if (!cbTermAndConditions.isChecked())
            setToastMessage("Please agree with Terms & Conditions");
        else
            return true;
        return false;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            Log.e("StatusCode: ", "" + message.arg1);
            if (!isNetworkAvailable()) {
                setToastMessage("No Network available");
            }
            if (message.arg1 == HttpURLConnection.HTTP_CREATED) {
                try {
                    JSONObject object = new JSONObject(message.obj.toString());
                    credits =String.valueOf(object.getInt("credit"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PreferenceManager.setPrefString(Constants.EMAIL, email);
                PreferenceManager.setPrefString(Constants.USERNAME, fullName);
                PreferenceManager.setPrefString(Constants.PASSWORD, password);
                PreferenceManager.setPrefString(Constants.MOBILE_NUMBER, mobileNumber);
                PreferenceManager.setPrefString(Constants.INST__NAME, instituteName);
                PreferenceManager.setPrefString(Constants.GENDER, gender);
                PreferenceManager.setPrefString(Constants.AVAILABLE_CREDITS, credits);
                Intent intent = new Intent(getApplicationContext(), OtpActivity.class);
                intent.putExtra("Mobile Number", mobileNumber);
                intent.putExtra("TYPE", Constants.SIGN_UP_ACTIVITY);
                intent.putExtra("EMAIL",email);
                intent.putExtra("pass",password);
                startActivity(intent);
            }
            if (message.arg1== HttpURLConnection.HTTP_INTERNAL_ERROR){
                setToastMessage("Internal Server Error");
            }
            return false;
        }
    });

    Handler instituteHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (!isNetworkAvailable()) {
                setToastMessage("No Network available");
            }
            if (message.arg1 == HttpURLConnection.HTTP_OK) {
                try {
                    if (arrayList != null) {
                        arrayList.clear();
                    }
                    JSONArray array = new JSONArray(message.obj.toString());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        arrayList.add(object.getString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, arrayList.toArray(new String[arrayList.size()]));
                etInstituteName.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            if (message.arg1== HttpURLConnection.HTTP_INTERNAL_ERROR){
                setToastMessage("Internal Server Error");
            }
            return false;
        }
    });
}
