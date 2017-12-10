package com.utexas.ee382v.ihelp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "ERROR";
    private Button SignOut;
    private SignInButton SignIn;
    private GoogleApiClient googleApiClient;
    private GoogleSignInAccount account;
    private Button GotoviewAll;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private static final int REQ_CODE = 9001;
    private static String name;
    private static String email;
    private static String gname;
    private static String gmail;
    private boolean signinStatus;
//    private static final String BACKEND_ENDPOINT = "https://firebase-ihelp.appspot.com/";
    private static final String BACKEND_ENDPOINT = "http://10.0.2.2:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_main);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","public_profile");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String user_id = loginResult.getAccessToken().getUserId();
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response.getError() != null) {
                            // handle error
                        } else {
                            email = object.optString("email");
                            name = object.optString("name");
                        }
                        updateUI(true);
                    }
                });
                //email = jsonObject.getString("email");
                Bundle parameters = new Bundle();
                parameters.putString("fields","name, email, id");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
                //email = .getString()

            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });

        // Google Sign in part
        SignOut = (Button)findViewById(R.id.bn_logout);
        SignIn = (SignInButton)findViewById(R.id.bn_login);
        GotoviewAll = findViewById(R.id.button3);
        SignIn.setOnClickListener(this);
        SignOut.setOnClickListener(this);
        //Name.setVisibility(View.GONE);
        SignOut.setVisibility(View.GONE);
        GotoviewAll.setVisibility(View.GONE);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

    }

    public static String getUserEmail(){
        if(gmail != null) {
            return gmail;
        }
        else {
            return email;
        }
    }

    public static String getUserName() {
        if(gname != null) {
            return gname;
        }
        else {
            return name;
        }
    }

    private void signIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        signinStatus = true;
        startActivityForResult(intent, REQ_CODE);
    }

    public void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }

    private void handleResult(GoogleSignInResult result){

        if(result.isSuccess()){
            account = result.getSignInAccount();
            Log.d("login", "login: " + String.valueOf(account == null));
            if (account != null) {
                this.gname = account.getDisplayName();
                this.gmail = account.getEmail();
                updateUI(true);
                createUser();
            }
            Intent intent = new Intent(this, ViewAll.class);
            startActivity(intent);
        }
        else{
            Log.d("login", "not successful");
            updateUI(false);
        }
    }


    private void updateUI(boolean isLogin){
        if(isLogin){
            signinStatus = true;
            SignIn.setVisibility(View.GONE);
            SignOut.setVisibility(View.VISIBLE);
            GotoviewAll.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        }
        else{
            signinStatus = false;
            SignIn.setVisibility(View.VISIBLE);
            SignOut.setVisibility(View.GONE);
            GotoviewAll.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }



    public void goToViewAll(View view) {
        Intent intent = new Intent(this, ViewAll.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bn_login:
                signIn();
                break;

            case R.id.bn_logout:
                new PreferenceManager(this).clearPreference();
                signOut();
                break;
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d("signin", GoogleSignInStatusCodes.getStatusCodeString(result.getStatus().getStatusCode()));
            handleResult(result);
        }
    }

    public static String getEndpoint() {
        return BACKEND_ENDPOINT;
    }

    private String createUser(){
        final String request_url = MainActivity.getEndpoint() + "/android/create_user";

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, request_url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    Log.d("create_status", status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_email", MainActivity.getUserEmail());
                params.put("user_name", MainActivity.getUserName());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("profile_image", new DataPart("default_profile", AppHelper.getFileDataFromDrawable(getBaseContext(), R.drawable.default_profile), "image/jpeg"));
                return params;
            }
        };

        VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(multipartRequest);

        return "";
    }
}
