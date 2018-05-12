package com.example.staysafe;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.staysafe.data.model.FbaseToken;
import com.example.staysafe.data.remote.APIService;
import com.example.staysafe.data.remote.ApiUtils;
import com.example.staysafe.data.session.SessionManager;
import com.example.staysafe.fragments.FragmentDependents;
import com.example.staysafe.fragments.FragmentHome;
import com.example.staysafe.fragments.FragmentProfile;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private SessionManager sessionManager;
    private APIService mAPIService;
    private Call<FbaseToken> fbaseTokenCall;
    private ProgressDialog progressDialog;

    private int panicCount = 0;

    BroadcastReceiver panicReceiver = new BroadcastReceiver() {
        //When Event is published, onReceive method is called
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) || intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                panicCount++;
            }
            Log.d("MYPOWERTAG", "Clicked");

            if (panicCount == 4){
                //Broadcasr the intent
                panicCount = 0;
                unregisterReceiver(this);
                Log.d("MYPOWERTAG", "Clicked 4 times");
                Intent broadCast = new Intent("com.example.staysafe.AlertActivity");
                sendBroadcast(broadCast);
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        mAPIService = ApiUtils.getAPIService();
        progressDialog = new ProgressDialog(this);
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

        //Session check
        sessionManager = new SessionManager(getApplicationContext());
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            finish();
            startActivity(intent);
            return;
        }
        String fbaseId = FirebaseInstanceId.getInstance().getToken().toString();
        String sessionFbaseId = sessionManager.getFbaseToken();
        if (sessionFbaseId.equalsIgnoreCase("") || !fbaseId.equalsIgnoreCase(sessionFbaseId)){
            sendTokenToServer(fbaseId, "login");

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try {
            registerReceiver(panicReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
            registerReceiver(panicReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        } catch (IllegalArgumentException ex){
            unregisterReceiver(panicReceiver);
            registerReceiver(panicReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
            registerReceiver(panicReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        }

        requestPermissions();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            int count = getFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                super.onBackPressed();
            } else {
                getFragmentManager().popBackStack();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            sendTokenToServer("", "logout");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedFragment(id);
        return true;
    }

    public void displaySelectedFragment(int itemId){

        Fragment fragment = null;
        switch (itemId){
            case R.id.nav_home:
                fragment = new FragmentHome();
                break;
            case R.id.nav_profile:
                fragment = new FragmentProfile();
                break;
            case R.id.nav_dependents:
                fragment = new FragmentDependents();
                break;
        }

        if (fragment != null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, fragment);
            transaction.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void cancelDialog(){
        fbaseTokenCall.cancel();
        progressDialog.dismiss();
    }

    private void sendTokenToServer(String token, final String operation){

        progressDialog.show();

        fbaseTokenCall = mAPIService.updateFbaseToken(sessionManager.getId(), token);
        fbaseTokenCall.enqueue(new Callback<FbaseToken>() {
            @Override
            public void onResponse(Call<FbaseToken> call, Response<FbaseToken> response) {
                if(response.isSuccessful()){
                    FbaseToken fbaseTokenUpdate = response.body();
                    if (fbaseTokenUpdate.getStatus().equalsIgnoreCase("success")){
                        sessionManager.updateFbaseToken(fbaseTokenUpdate.getFbaseToken());
                        if(operation.equalsIgnoreCase("logout")){
                            sessionManager.clearUserDetails();
                            unregisterReceiver(panicReceiver);
                            Intent intent = new Intent(getBaseContext(), NavDrawerActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "FbaseToken update failed.", Toast.LENGTH_LONG).show();
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<FbaseToken> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "FbaseToken update failed.", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                FragmentManager manager = getSupportFragmentManager();
                if (manager != null) {
                    int backStackEntryCount = manager.getBackStackEntryCount();
                    if (backStackEntryCount == 0) {
                        finish();
                    }
                    Fragment fragment = manager.getFragments()
                            .get(backStackEntryCount - 1);
                    fragment.onResume();
                }
            }
        };
        return result;
    }

    /**
     * Only when the app's target SDK is 23 or higher, it requests each dangerous permissions it
     * needs when the app is running.
     */
    private void requestPermissions() {

        String[] RUNTIME_PERMISSIONS = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_WIFI_STATE,
                android.Manifest.permission.ACCESS_NETWORK_STATE};

        // check list of permissions
        boolean isPermissionsGranted = true;
        for (String permission : RUNTIME_PERMISSIONS) {
            if (this.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                isPermissionsGranted = false;
                break;
            }
        }

        // return if all permissions already granted
        if (isPermissionsGranted) {
            displaySelectedFragment(R.id.nav_home);
            return;
        }

        // request permissions
        if (android.os.Build.VERSION.SDK_INT >= 23){
            ActivityCompat.requestPermissions(this,
                    RUNTIME_PERMISSIONS,
                    REQUEST_CODE_ASK_PERMISSIONS);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                for (int index = 0; index < permissions.length; index++) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {

                        /**
                         * If the user turned down the permission request in the past and chose the
                         * Don't ask again option in the permission request system dialog.
                         */
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                                permissions[index])) {
                            Toast.makeText(this,
                                    "Required permission " + permissions[index] + " not granted. "
                                            + "Please go to settings and turn on for sample app",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this,
                                    "Required permission " + permissions[index] + " not granted",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }

                /*
                 * All permission requests are being handled. Create map fragment view.
                 */
                displaySelectedFragment(R.id.nav_home);
                break;
            }
            default:
                this.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
