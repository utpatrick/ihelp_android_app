package com.utexas.ee382v.ihelp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

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

import org.json.JSONObject;

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
    private boolean signinStatus;
    private static final String BACKEND_ENDPOINT = "https://firebase-ihelp.appspot.com/";




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

        SignOut = (Button)findViewById(R.id.bn_logout);
        SignIn = (SignInButton)findViewById(R.id.bn_login);
        GotoviewAll = findViewById(R.id.button3);
        SignIn.setOnClickListener(this);
        SignOut.setOnClickListener(this);
        //Name.setVisibility(View.GONE);
        SignOut.setVisibility(View.GONE);
        GotoviewAll.setVisibility(View.GONE);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(new Scope(Scopes.PLUS_LOGIN)).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

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
                            //String birthday = me.optString("user_birthday");

                            //System.out.println(email);
                            //System.out.println(name);
                            //System.out.println(gmail);
                            //System.out.println(gname);
                            //System.out.println(birthday);
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
    }

    public static String getUserEmail(){
        if(gmail != null){
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
        //System.out.print(name);
        //System.out.print(email);
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
            //Name.setVisibility(View.VISIBLE);
            SignIn.setVisibility(View.GONE);
            SignOut.setVisibility(View.VISIBLE);
            GotoviewAll.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
            //mVideoView = (VideoView)findViewById(R.id.bgvideoview);
            //Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ut);
            //mVideoView.setVideoURI(uri);
            //mVideoView.start();

            //mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            //    @Override
            //    public void onPrepared(MediaPlayer mp) {
            //        mp.setLooping(true);
            //
            //    }
            //});
        }
        else{
            signinStatus = false;
            //Name.setVisibility(View.GONE);
            SignIn.setVisibility(View.VISIBLE);
            SignOut.setVisibility(View.GONE);
            GotoviewAll.setVisibility(View.GONE);

            loginButton.setVisibility(View.VISIBLE);
            //mVideoView = (VideoView)findViewById(R.id.bgvideoview);
            //Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ut);
            //mVideoView.setVideoURI(uri);
            //mVideoView.start();

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
}
