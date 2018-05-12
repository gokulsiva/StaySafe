package com.example.staysafe;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.staysafe.data.session.SessionManager;

public class AlertActivity extends AppCompatActivity {

    private boolean isCancelled = false;

    private SessionManager sessionManager;
    private TextView countDownTimerView;
    private EditText alertMpin;
    private Button alertStopButton;
    private CountDownTimer countDownTimer;
    private long countDownInterval = 1000;
    private long countDownMultiplier = 1000;
    private long countDownTime = 60;
    private String sessionMPin;
    private String userMPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.userDetails();
        sessionMPin = sessionManager.getMpin();
        countDownTimerView = findViewById(R.id.countDownTimer);
        alertMpin = findViewById(R.id.alertMpin);
        alertStopButton = findViewById(R.id.alertStopButton);
        alertStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMPin = alertMpin.getText().toString().trim();
                if(userMPin.equals(sessionMPin)){
                    isCancelled = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid pin.", Toast.LENGTH_LONG).show();
                }

            }
        });
        countDownTimer = new CountDownTimer((countDownMultiplier * countDownTime), countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                countDownTimerView.setText(""+(millisUntilFinished / 1000));
                if (isCancelled){
                    cancel();
                    Toast.makeText(getApplicationContext(), "Countdown cancelled.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Countdown cancelled.", Toast.LENGTH_LONG).show();
                //TODO add code to send location to server.
                finish();
            }
        }.start();
    }
}
