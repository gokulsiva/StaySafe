package com.example.staysafe.data.remote;

import com.example.staysafe.data.model.Dependents;
import com.example.staysafe.data.model.FbaseToken;
import com.example.staysafe.data.model.GeneralReq;
import com.example.staysafe.data.model.UserAuth;
import com.example.staysafe.data.model.UserSignUp;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {

    @POST("/user/auth")
    @FormUrlEncoded
    Call<UserAuth> authUser(@Field("email") String email, @Field("password") String password);

    @POST("/user/create")
    @FormUrlEncoded
    Call<UserSignUp> signUpUser(@Field("name") String name, @Field("email") String email, @Field("password") String password, @Field("mpin") String mpin, @Field("gender") String gender, @Field("dob") String dob, @Field("contact_no") String contact_no);

    @POST("/user/updateFbaseToken")
    @FormUrlEncoded
    Call<FbaseToken> updateFbaseToken(@Field("id") String id, @Field("fbaseToken") String fbaseToken);

    @POST("/dependent/create")
    @FormUrlEncoded
    Call<GeneralReq> addDependents(@Field("userId") String userId, @Field("dependentId") String dependentId);

    @POST("/dependent/list")
    @FormUrlEncoded
    Call<Dependents> getDependents(@Field("userId") String userId);

    @POST("/user/updateGuardianId")
    @FormUrlEncoded
    Call<GeneralReq> updateGuardianId(@Field("userId") String userId, @Field("guardianId") String guardianId);
}