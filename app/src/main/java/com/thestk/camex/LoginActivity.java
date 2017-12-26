package com.thestk.camex;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.thestk.camex.database.UserSharedPreference;
import com.thestk.camex.models.UserModel;
import com.thestk.camex.web.CheckInternetConnection;
import com.thestk.camex.web.RequestHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Neba on 12-Oct-17.
 */


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, LoaderManager.LoaderCallbacks<String> {

    private final int RC_SIGN_IN = 10;
    private final int MY_LOADER_ID = 200;
    @BindView(R.id.login_button)
    LoginButton loginButton;
    @BindView(R.id.sign_in_button)
    SignInButton signInButton;
    @BindView(R.id.imv_close)
    ImageView imv_close;
    @BindView(R.id.tv_facebook_error)
    TextView tv_fb_error;
    @BindView(R.id.tv_gmail_erro)
    TextView tv_gmail_error;
    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.et_username)
    EditText et_user_name;
    @BindView(R.id.btn_submit)
    Button btn_submit;
    private CallbackManager callbackManager;
    private String email = "";
    private String user_name = "";
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;
    private RequestHttp requestHttp;
    private String submitUrl;
    private UserSharedPreference userSharedPreference;
    private String errorMessage = "";
    private int responseCode = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        ButterKnife.bind(this);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
        callbackManager = CallbackManager.Factory.create();
        tv_fb_error.setVisibility(View.GONE);
        tv_gmail_error.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);
        requestHttp = new RequestHttp();
        submitUrl = getString(R.string.baseUrl) + "/user/add";

        userSharedPreference = new UserSharedPreference(this);

        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallbacks());

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void startLoader() {
        if (CheckInternetConnection.isNetworkAvailable(getApplicationContext())) {

            if (getSupportLoaderManager().getLoader(MY_LOADER_ID) == null) {
                getSupportLoaderManager().initLoader(MY_LOADER_ID, null, LoginActivity.this).forceLoad();
            } else {
                getSupportLoaderManager().restartLoader(MY_LOADER_ID, null, LoginActivity.this).forceLoad();
            }
        } else {
            showDialog(getResources().getString(R.string.message), getResources().getString(R.string.no_int), 1);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("results", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e("name", acct.getDisplayName());
            Log.e("email", acct.getEmail());
            user_name = acct.getDisplayName();
            email = acct.getEmail();
            startLoader();

        } else {
            // Signed out, show unauthenticated UI.

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("connection failed", connectionResult.toString());
        tv_gmail_error.setText(getString(R.string.fb_error) + ": " + connectionResult.getErrorMessage());
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<String>(LoginActivity.this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                progressDialog.setMessage(getString(R.string.submitting));
                progressDialog.show();
                errorMessage = "";
                responseCode = 0;
            }

            @Override
            public String loadInBackground() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_name", user_name);
                hashMap.put("email", email);


                String response = null;
                try {
                    response = requestHttp.setUP(submitUrl, RequestHttp.Method.POST, 10000, 10000).withData(hashMap).sendAndReadString();
                    responseCode = requestHttp.getResponseCode();
                    Log.e("response", response);
                    Log.e("response code", responseCode + "");
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    errorMessage = getString(R.string.connection_timeout);
                    return null;
                } catch (IOException ee) {
                    ee.printStackTrace();
                    errorMessage = getString(R.string.failed_server);
                    return null;
                } catch (Exception e) {
                    errorMessage = getString(R.string.failed_server);
                    e.printStackTrace();
                }

                return response;
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<String> loader, String response) {
        progressDialog.dismiss();
        try {
            if (responseCode == 200) {
                JSONObject jsonObject = new JSONObject(response);
                int id = Integer.parseInt(jsonObject.getString("id"));
                String user_name = jsonObject.getString("name");
                String email = jsonObject.getString("email");
                UserModel userModel = new UserModel(id, user_name, email);
                if (userSharedPreference.loginUser(userModel)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    finish();
                }

            } else {
                showDialog(getString(R.string.message), getString(R.string.failed_server), 1);
            }
        } catch (JSONException e) {
            showDialog(getString(R.string.message), getString(R.string.failed_server), 1);
        } catch (Exception e) {
            e.printStackTrace();
            showDialog(getString(R.string.message), errorMessage, 1);
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @OnClick(R.id.imv_close)
    void closeActivity() {
        onBackPressed();
    }

    @OnClick(R.id.btn_submit)
    void submitLoginDetails() {
        Validation validatation = new Validation(getApplicationContext());
        if (validatation.hasText(et_user_name) && validatation.isEmailAddress(et_email)) {
            user_name = et_user_name.getText().toString();
            email = et_email.getText().toString();
            startLoader();
        }
    }

    /**
     * Function shows a dismissable dialog to the user with important information
     *
     * @param title {@link String} title of dialog
     * @param body  {@link String} body of dialog
     * @param type  int indicates the action to perform on dialog click
     */

    private void showDialog(final String title, String body, final int type) {
        //use libary to beautify alertdialog
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(LoginActivity.this);
        dialogBuilder.withTitle(title)
                .withIcon(android.R.drawable.ic_dialog_alert)
                .withMessage(body)
                .withMessageColor(getResources().getColor(R.color.colorWhite))
                .withTitleColor(getResources().getColor(R.color.colorWhite))
                .withDialogColor(getResources().getColor(R.color.colorPrimary))
                .withDividerColor(getResources().getColor(R.color.colorWhite2))
                .withButton1Text(getString(R.string.exit))                                      //def gone
                .withButton2Text(getString(R.string.reload))                                  //def gone
                .isCancelableOnTouchOutside(true)
                .withEffect(Effectstype.RotateLeft)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        if (type == 1) {
                            startLoader();
                        }
                    }
                })
                .show();
    }

    private class FacebookCallbacks implements FacebookCallback<LoginResult> {

        @Override
        public void onSuccess(LoginResult loginResult) {

            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.i("LoginActivity", response.toString());
                    // Get facebook data from login
                    //Bundle bFacebookData = getFacebookData(object);
                    try {
                        Log.e("name", object.getString("first_name"));
                        Log.e("email", object.getString("email"));
                        user_name = object.getString("first_name");
                        email = object.getString("email");
                        startLoader();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email");
            request.setParameters(parameters);
            request.executeAsync();

        }

        @Override
        public void onCancel() {

            tv_fb_error.setText(getString(R.string.connection_cancelled));
        }

        @Override
        public void onError(FacebookException error) {

            tv_fb_error.setText(getString(R.string.fb_error) + ": " + error.getMessage());
        }
    }
}
