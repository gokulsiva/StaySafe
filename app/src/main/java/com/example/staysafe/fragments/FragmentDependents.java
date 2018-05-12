package com.example.staysafe.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.staysafe.R;
import com.example.staysafe.adapters.DependentsViewAdapter;
import com.example.staysafe.data.model.Dependents;
import com.example.staysafe.data.model.GeneralReq;
import com.example.staysafe.data.model.User;
import com.example.staysafe.data.remote.APIService;
import com.example.staysafe.data.remote.ApiUtils;
import com.example.staysafe.data.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDependents extends Fragment{

    private ArrayList<User> dependentsList = new ArrayList<User>(1);
    private ListView listView;
    private Button addDependent;
    private DependentsViewAdapter viewAdapter;
    private ProgressDialog progressDialog;
    private APIService mAPIService;
    private Call<Dependents> dependentsCall;
    private Call<GeneralReq> generalReqCall;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View view =  inflater.inflate(R.layout.fragment_dependents, container, false);
        listView = view.findViewById(R.id.dependentsListView);
        addDependent = view.findViewById(R.id.addDependentButton);
        addDependent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FragmentAddDependent();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null);
                transaction.replace(R.id.content_frame, fragment);
                transaction.commit();
            }
        });

        sessionManager = new SessionManager(view.getContext());
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
        loadList();
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadList(){
        progressDialog.show();
        dependentsCall = mAPIService.getDependents(sessionManager.getId());
        dependentsCall.enqueue(new Callback<Dependents>() {
            @Override
            public void onResponse(Call<Dependents> call, Response<Dependents> response) {
                if (response.isSuccessful()){
                    Dependents dependents = response.body();
                    if (dependents.getStatus().equalsIgnoreCase("success")){
                        List<User> depList =  dependents.getUsers();
                        for (int i=0; i<depList.size(); i++){
                            dependentsList.add(depList.get(i));
                        }
                        viewAdapter = new DependentsViewAdapter(dependentsList, getContext());
                        listView.setAdapter(viewAdapter);
                        progressDialog.dismiss();
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                final User user = dependentsList.get(position);
                                Snackbar.make(view, "Make " + user.getName() + " as guardian.", Snackbar.LENGTH_LONG)
                                        .setAction("Yes", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                updateGuardianId(sessionManager.getId(), user.getId());
                                            }
                                        }).show();
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), dependents.getMsg(), Toast.LENGTH_LONG).show();
                    }

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Unable to retrive data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Dependents> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Unable to retrive data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cancelDialog(){
        if(dependentsCall != null){
            dependentsCall.cancel();
        }
        if (generalReqCall != null){
            generalReqCall.cancel();
        }
        progressDialog.dismiss();
    }

    private void updateGuardianId(String userId, String guardianId){
        progressDialog.show();
        generalReqCall = mAPIService.updateGuardianId(userId, guardianId);
        generalReqCall.enqueue(new Callback<GeneralReq>() {
            @Override
            public void onResponse(Call<GeneralReq> call, Response<GeneralReq> response) {
                if (response.isSuccessful()){
                    GeneralReq req = response.body();
                    if (req.getStatus().equalsIgnoreCase("success")){
                        Toast.makeText(getContext(), req.getMsg(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Guardian updation failed.", Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GeneralReq> call, Throwable t) {
                Toast.makeText(getContext(), "Guardian updation failed.", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        dependentsList.clear();
        loadList();
    }
}
