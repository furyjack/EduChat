package com.example.admin.educhat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.educhat.utils.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

//import com.edunutspartners.R;
//import com.edunutspartners.utils.Constants;
//import com.edunutspartners.utils.PreferenceManager;
//import com.edunutspartners.utils.Urls;
//import com.edunutspartners.utils.WebRequest;

public class Login extends BaseActivity {
    EditText etUserName, etPassword;
    TextView tvWarningMessage, tvWarningMessage2;
    String username, password;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null)
        {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        String u=PreferenceManager.getPrefString("user");
        etUserName = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        etPassword.setTypeface(Typeface.DEFAULT);
        etPassword.setTransformationMethod(new PasswordTransformationMethod());
        etUserName.setText(u);
        tvWarningMessage = (TextView) findViewById(R.id.tv_warning);
        tvWarningMessage2 = (TextView) findViewById(R.id.tv_warning2);

    }

    public void onClickLogin(View view) {
        if (isValid()) {
            JSONObject data = new JSONObject();
            try {
                data.put("username", username);
                data.put("password", password);
                auth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            PreferenceManager.setPrefString("user",username);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        else
                        {
                            Toast.makeText(Login.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//                WebRequest request = new WebRequest(this, handler, Urls.LOGIN_URL, "POST");
//                request.execute(data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    public void onClickForgetPassword(View view) {
//        startActivity(new Intent(getApplicationContext(), ForgetPasswordActivity.class));
//    }

    public void onClickSignUp(View view) {
        // ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this);
        startActivity(new Intent(getApplicationContext(), Signup.class));
    }

    private boolean isValid() {
        username = etUserName.getText().toString();
        password = etPassword.getText().toString();

        if (username.isEmpty()) {
            tvWarningMessage.setText(R.string.empty_username);
            tvWarningMessage.setVisibility(View.VISIBLE);
            etUserName.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorWarning), PorterDuff.Mode.SRC_ATOP);

        } else if (password.isEmpty()) {
            tvWarningMessage2.setText(R.string.empty_password);
            tvWarningMessage2.setVisibility(View.VISIBLE);
            etPassword.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorWarning), PorterDuff.Mode.SRC_ATOP);

        }

        else {
            return true;
        }
        return false;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.arg1== HttpURLConnection.HTTP_OK) {
                auth.signInWithEmailAndPassword(username,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });

            }
            if (message.arg1== HttpURLConnection.HTTP_UNAUTHORIZED){
                setToastMessage("Invalid Credentials");
            }

            if (message.arg1== HttpURLConnection.HTTP_INTERNAL_ERROR){
                setToastMessage("Internal Server Error");
            }
            return false;
        }
    });

}
