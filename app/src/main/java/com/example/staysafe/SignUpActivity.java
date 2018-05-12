package com.example.staysafe;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.staysafe.data.model.UserAuth;
import com.example.staysafe.data.model.UserSignUp;
import com.example.staysafe.data.remote.APIService;
import com.example.staysafe.data.remote.ApiUtils;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {


    private APIService mAPIService;
    private Call<UserSignUp> signUpUserCall;
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirm_password;
    private RadioGroup gender;
    private EditText dob;
    private EditText mpin;
    private EditText contact_no;
    private Button signup;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAPIService = ApiUtils.getAPIService();
        name = findViewById(R.id.signup_name);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        confirm_password = findViewById(R.id.signup_confirm_password);
        mpin = findViewById(R.id.signup_mpin);
        gender = findViewById(R.id.signup_genderGrp);
        dob = findViewById(R.id.signup_dob);
        contact_no = findViewById(R.id.signup_contact_no);
        signup = findViewById(R.id.signup_Btn);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpuser();
            }
        });
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

    }


    public void signUpuser(){
        String signUpName = name.getText().toString().trim();
        String signUpEmail = email.getText().toString().trim();
        String signUpPass = password.getText().toString().trim();
        String signUpCnfPass = confirm_password.getText().toString().trim();
        String signUpMpin = mpin.getText().toString().trim();
        String signUpGender = (String) ((RadioButton) findViewById(gender.getCheckedRadioButtonId())).getText();
        String signUpDOB = dob.getText().toString().trim();
        String signUpContact = contact_no.getText().toString().trim();

        String validation = isValidArguments(signUpName, signUpEmail, signUpPass, signUpCnfPass, signUpMpin, signUpGender, signUpDOB, signUpContact);
        if (validation.equalsIgnoreCase("valid")){
            progressDialog.show();
            signUp(signUpName, signUpEmail, signUpPass, signUpMpin, signUpGender, signUpDOB, signUpContact);
        } else {
            Toast.makeText(getBaseContext(), validation, Toast.LENGTH_LONG).show();
        }

    }

    public String isValidArguments(String name, String email, String pass, String cnfPass, String mpin, String gender, String dob, String contactNo){

        if (!pass.equals(cnfPass)){
            return "invalid_pass_not_match";
        }

        if (name.equals("") || email.equals("") || pass.equals("") || cnfPass.equals("") || mpin.equals("") || gender.equals("") || dob.equals("") || contactNo.equals("")){
            return "invalid_fields";
        }
        try {
            String[] dobArr = dob.split("-");
            int month = Integer.valueOf(dobArr[0]);
            int date =  Integer.valueOf(dobArr[1]);
            int year = Integer.valueOf(dobArr[2]);
            Calendar now = Calendar.getInstance();
            int currYear = now.get(Calendar.YEAR);
            if (!((month > 0 && month <13) && (date > 0 && date <32) && (dobArr[2].length() == 4 && year > 1900 && year <= currYear))){
                return "invalid_date";
            }
        } catch (Exception e){
            return "invalid_date";
        }
        if (contactNo.length() != 10){
            return "invalid_contact_number";
        }
        return "valid";
    }

    public void signUp(String name, final String email, String pass, String mpin, String gender, String dob, String contactNo){
        signUpUserCall = mAPIService.signUpUser(name, email, pass, mpin, gender, dob, contactNo);
        signUpUserCall.enqueue(new Callback<UserSignUp>() {
            @Override
            public void onResponse(Call<UserSignUp> call, Response<UserSignUp> response) {
                progressDialog.dismiss();
                UserSignUp userSignUp = response.body();
                if(userSignUp.getStatus().equalsIgnoreCase("success")){
                    Toast.makeText(getApplicationContext(),  userSignUp.getMsg() + "\nPlease LogIn.", Toast.LENGTH_LONG).show();
                    returnToLoginActivity(email);
                } else {
                    Toast.makeText(getBaseContext(), userSignUp.getMsg(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserSignUp> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "SignUp failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void cancelDialog(){
        signUpUserCall.cancel();
        progressDialog.dismiss();
    }

    public void returnToLoginActivity(String email){
        Intent intent = new Intent();
        intent.putExtra("email", email);
        setResult(RESULT_OK, intent);
        finish();
    }
}
