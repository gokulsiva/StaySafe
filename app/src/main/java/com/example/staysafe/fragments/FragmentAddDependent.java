package com.example.staysafe.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.staysafe.R;
import com.example.staysafe.data.model.GeneralReq;
import com.example.staysafe.data.remote.APIService;
import com.example.staysafe.data.remote.ApiUtils;
import com.example.staysafe.data.session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentAddDependent extends Fragment {

    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    private APIService mAPIService;
    private Call<GeneralReq> generalReqCall;

    private EditText dependentEditText;
    private Button addDependent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View view = inflater.inflate(R.layout.frabment_add_dependent, container, false);
        sessionManager = new SessionManager(getContext());
        mAPIService = ApiUtils.getAPIService();
        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setTitle("Hold on");
        progressDialog.setMessage("Processing please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelDialog();
            }
        });
        dependentEditText = view.findViewById(R.id.dependentIdEditText);
        addDependent = view.findViewById(R.id.dependentAddButton);
        addDependent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = sessionManager.getId();
                String dependentId = dependentEditText.getText().toString().trim();
                addDependentToServer(userId, dependentId);
            }
        });
        return view;
    }

    private void addDependentToServer(String userId, String dependentId){
        progressDialog.show();
        generalReqCall = mAPIService.addDependents(userId, dependentId);
        generalReqCall.enqueue(new Callback<GeneralReq>() {
            @Override
            public void onResponse(Call<GeneralReq> call, Response<GeneralReq> response) {
                if (response.isSuccessful()){
                    GeneralReq generalReq = response.body();
                    if (generalReq.getStatus().equalsIgnoreCase("success")){
                        dependentEditText.setText("");
                    }
                    Toast.makeText(getContext(), generalReq.getMsg(), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<GeneralReq> call, Throwable t) {
                Toast.makeText(getContext(), "Unable to add this dependent.", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void cancelDialog(){

        if (generalReqCall != null){
            generalReqCall.cancel();
        }
        progressDialog.dismiss();
    }


}
