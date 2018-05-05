package com.example.staysafe.data.remote;

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
    Call<UserSignUp> signUpUser(@Field("name") String name, @Field("email") String email, @Field("password") String password, @Field("gender") String gender, @Field("dob") String dob, @Field("contact_no") String contact_no);
}