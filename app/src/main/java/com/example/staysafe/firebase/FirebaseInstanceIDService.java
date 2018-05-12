package com.example.staysafe.firebase;

import android.app.ProgressDialog;
import android.widget.Toast;

import com.example.staysafe.data.model.FbaseToken;
import com.example.staysafe.data.remote.APIService;
import com.example.staysafe.data.remote.ApiUtils;
import com.example.staysafe.data.session.SessionManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private SessionManager sessionManager;
    private APIService mAPIService;
    private Call<FbaseToken> fbaseTokenCall;


    @Override
    public void onTokenRefresh() {

        sessionManager = new SessionManager(getApplicationContext());
        mAPIService = ApiUtils.getAPIService();
        String token = FirebaseInstanceId.getInstance().getToken();
        if (sessionManager.isLoggedIn()){
            sendTokenToServer(token, "tokenRefresh");
        }
    }

    private void sendTokenToServer(String token, final String operation){

        fbaseTokenCall = mAPIService.updateFbaseToken(token, token);
        fbaseTokenCall.enqueue(new Callback<FbaseToken>() {
            @Override
            public void onResponse(Call<FbaseToken> call, Response<FbaseToken> response) {
                if(response.isSuccessful()){
                    FbaseToken fbaseTokenUpdate = response.body();
                    if (fbaseTokenUpdate.getStatus().equalsIgnoreCase("success")){
//                        sessionManager.updateFbaseToken(fbaseTokenUpdate.getFbaseToken());
                    } else {
                        Toast.makeText(getApplicationContext(), "FbaseToken update failed.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<FbaseToken> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "FbaseToken update failed.", Toast.LENGTH_LONG).show();
            }
        });
    }

}
