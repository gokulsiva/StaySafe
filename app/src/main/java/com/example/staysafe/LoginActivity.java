package com.example.staysafe;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.staysafe.data.model.User;
import com.example.staysafe.data.model.UserAuth;
import com.example.staysafe.data.remote.APIService;
import com.example.staysafe.data.remote.ApiUtils;
import com.example.staysafe.data.session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private APIService mAPIService;
    private Call<UserAuth> userAuthCall;
    private EditText email;
    private EditText password;
    private Button loginBtn;
    private Button signupBtn;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAPIService = ApiUtils.getAPIService();
        sessionManager = new SessionManager(getApplicationContext());
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_btn);
        signupBtn = findViewById(R.id.login_signup_btn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("SignIn");
        progressDialog.setMessage("Logging in please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelDialog();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login_email = email.getText().toString().trim();
                String login_password = password.getText().toString().trim();
                if (login_email.equals("") || login_password.equals("")) {
                    Toast.makeText(getBaseContext(), "Invalid Email or Password.", Toast.LENGTH_LONG).show();
                    return;
                }
                progressDialog.show();
                login(login_email, login_password);
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SignUpActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    public void login(String email, String password){
        authUser(email, password);
    }

    public void authUser(String email, String password){
        userAuthCall = mAPIService.authUser(email, password);
        userAuthCall.enqueue(new Callback<UserAuth>() {
            @Override
            public void onResponse(Call<UserAuth> call, Response<UserAuth> response) {
                if (response.isSuccessful()){
                    UserAuth userAuth = response.body();
                    progressDialog.dismiss();
                    if (userAuth.getStatus().equalsIgnoreCase("failure")){
                        Toast.makeText(getBaseContext(), userAuth.getMsg(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    User user = userAuth.getUser();
                    sessionManager.createLoginSession(user.getId(), user.getName(), user.getEmail(), user.getDob(), user.getContactNo(), user.getGender());
                    Intent intent = new Intent(getBaseContext(), NavDrawerActivity.class);
                    finish();
                    startActivity(intent);
                    return;
                }
            }

            @Override
            public void onFailure(Call<UserAuth> call, Throwable t) {
                if (userAuthCall.isCanceled()){
                    Toast.makeText(getBaseContext(),"LogIn cancelled.", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("Login error", "Unable to auth user.\n" + t);
                }
                progressDialog.dismiss();
            }
        });
    }

    public void cancelDialog(){
        userAuthCall.cancel();
        progressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                String emailId = data.getStringExtra("email");
                email.setText(emailId);
            }
        }
    }
}
