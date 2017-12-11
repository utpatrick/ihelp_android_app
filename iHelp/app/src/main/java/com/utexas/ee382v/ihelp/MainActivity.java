package com.utexas.ee382v.ihelp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.applozic.audiovideo.activity.AudioCallActivityV2;
import com.applozic.audiovideo.activity.VideoActivity;
import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserClientService;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.api.account.user.UserLogoutTask;
import com.applozic.mobicomkit.feed.ApiResponse;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.google.android.gms.auth.api.signin.GoogleSignIn;

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
    protected static String email;
    private static String gname;
    protected static String gmail;
    private static String user_display_name;
    private boolean signinStatus;
    private Context mContext;
    private static final String BACKEND_ENDPOINT = "https://firebase-ihelp.appspot.com";
//    private static final String BACKEND_ENDPOINT = "http://10.0.2.2:8080";
    private VideoView mVideoView;

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
        this.mContext = this;

        setContentView(R.layout.activity_main);

        mVideoView = findViewById(R.id.bgvideoview);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg);
        mVideoView.setVideoURI(uri);
        mVideoView.start();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);

            }
        });
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

//    @Override
//    protected void onResume() {
//        super.onResume();
//        mVideoView = (VideoView)findViewById(R.id.bgvideoview);
//        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg);
//        mVideoView.setVideoURI(uri);
//        mVideoView.start();
//
//        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
//
//            }
//        });
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }

    public static String getUserName() {
        if(gname != null) {
            return gname;
        }
        else {
            return name;
        }
    }

    public static String getUserDisplayName() {
        if(user_display_name != null) {
            return user_display_name;
        }
        else {
            return name;
        }
    }

    private void setUserDisplayName(String name) {
        this.user_display_name = name;
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

        if(MobiComUserPreference.getInstance(this).isLoggedIn()){
            Log.d("msg_status", "logged in");
        }

        UserLogoutTask.TaskListener userLogoutTaskListener = new UserLogoutTask.TaskListener() {
            @Override
            public void onSuccess(Context context) {
                //Logout success
                Log.d("msg_logout", "successful");
            }
            @Override
            public void onFailure(Exception exception) {
                //Logout failure
                Log.d("msg_logout", "failed");
            }
        };
        UserLogoutTask userLogoutTask = new UserLogoutTask(userLogoutTaskListener, mContext);
        userLogoutTask.execute((Void) null);
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
                createMsgUser();
            }
            Intent intent = new Intent(this, ViewAll.class);
            startActivity(intent);
        }
        else{
            Log.d("login", "not successful");
            updateUI(false);
        }
    }

    private void createMsgUser(){
        UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {

            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                //After successful registration with Applozic server the callback will come here
                Log.d("create_msg_user", "successful");
                PushNotificationTask pushNotificationTask = null;
                PushNotificationTask.TaskListener listener=  new PushNotificationTask.TaskListener() {
                    @Override
                    public void onSuccess(RegistrationResponse registrationResponse) {

                    }
                    @Override
                    public void onFailure(RegistrationResponse registrationResponse, Exception exception) {

                    }

                };
                pushNotificationTask = new PushNotificationTask(Applozic.getInstance(context).getDeviceRegistrationId(),listener,context);
                pushNotificationTask.execute((Void)null);
                ApplozicClient.getInstance(context).setHandleDial(true).setIPCallEnabled(true);
                Map<ApplozicSetting.RequestCode, String> activityCallbacks = new HashMap<ApplozicSetting.RequestCode, String>();
                activityCallbacks.put(ApplozicSetting.RequestCode.AUDIO_CALL, AudioCallActivityV2.class.getName());
                activityCallbacks.put(ApplozicSetting.RequestCode.VIDEO_CALL, VideoActivity.class.getName());
                ApplozicSetting.getInstance(context).setActivityCallbacks(activityCallbacks);
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                //If any failure in registration the callback  will come here
                Log.d("create_msg_user", exception.toString());
            }};

        User user = new User();
        user.setUserId(getUserEmail()); //userId it can be any unique user identifier NOTE : +,*,? are not allowed chars in userId.
        user.setDisplayName(getUserName()); //displayName is the name of the user which will be shown in chat messages
        user.setEmail(getUserEmail()); //optional
        user.setAuthenticationTypeId(User.AuthenticationType.APPLOZIC.getValue());  //User.AuthenticationType.APPLOZIC.getValue() for password verification from Applozic server and User.AuthenticationType.CLIENT.getValue() for access Token verification from your server set access token as password
        user.setPassword(""); //optional, leave it blank for testing purpose, read this if you want to add additional security by verifying password from your server https://www.applozic.com/docs/configuration.html#access-token-url
        user.setImageLink("");//optional, set your image link if you have
        List<String> featureList =  new ArrayList<>();
        featureList.add(User.Features.IP_AUDIO_CALL.getValue());// FOR AUDIO
        featureList.add(User.Features.IP_VIDEO_CALL.getValue());// FOR VIDEO
        user.setFeatures(featureList);
        new UserLoginTask(user, listener, this).execute((Void) null);
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
        //callbackManager.onActivityResult(requestCode, resultCode, data);
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

    private void getUserDisplayNameFromServer(){
        final String profile_url = MainActivity.getEndpoint()
                + "/android/update_profile?user_email=" + getUserEmail()
                + "&time=" + Double.toString(System.nanoTime());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, profile_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    user_display_name = response.get("display_name").toString();
                    Log.d("displayName_req", user_display_name);
                    setUserDisplayName(user_display_name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(this.getApplicationContext()).add(jsonRequest);
    }
}
