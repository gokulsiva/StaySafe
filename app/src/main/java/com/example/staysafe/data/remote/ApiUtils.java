package com.example.staysafe.data.remote;

public class ApiUtils {

    private ApiUtils() {}

//    For emaluator
//    public static final String BASE_URL = "http://10.0.2.2:3000/";

//    For Guest Network
//    public static final String BASE_URL = "http://172.22.100.79:3000/";

//    For Mobile Hotspot
    public static final String BASE_URL = "http://192.168.43.62:3000/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
