package com.example.staysafe.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.staysafe.R;
import com.example.staysafe.data.session.SessionManager;

public class FragmentProfile extends Fragment {

    private TextView profileView;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        sessionManager = new SessionManager(getContext());
        profileView = view.findViewById(R.id.profileTextView);
        profileView.setText("ID : "+sessionManager.getId() +"\n"+
                "Name : "+sessionManager.getName() +"\n"+
                "Email : "+sessionManager.getEmail() +"\n"+
                "Gender : "+sessionManager.getGender() +"\n"+
                "DOB : "+sessionManager.getDob() +"\n"+
                "Contact No : "+sessionManager.getContact_no() +"\n"+
                "Guardian Id : "+sessionManager.getGuardianId() +"\n"
        );
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
