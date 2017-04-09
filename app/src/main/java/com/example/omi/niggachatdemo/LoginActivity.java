package com.example.omi.niggachatdemo;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    SignInButton login;
    TextView loginErrorMessage;
    GoogleApiClient googleApiClient;
    GoogleSignInOptions googleSignInOptions;
    int SIGN_IN_REQUEST_ID = 1;
    private ProgressDialog pDialog;
    TextView appName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        this.appName = (TextView)findViewById(R.id.appName);
        Animation animation;
        animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.animation);
        appName.startAnimation(animation);
        this.login = (SignInButton) findViewById(R.id.login);
        this.loginErrorMessage = (TextView)findViewById(R.id.loginErrorMessage);
        this.googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        this.googleApiClient = new GoogleApiClient.Builder(this)
                                    .enableAutoManage(this,this)
                                    .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                                    .build();
        this.login.setSize(SignInButton.SIZE_WIDE);
        this.login.setScopes(googleSignInOptions.getScopeArray());
        this.login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, SIGN_IN_REQUEST_ID);
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "no connection", Toast.LENGTH_SHORT).show();

    }


    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_ID)
        {
            //Toast.makeText(this, "on ActivityResult", Toast.LENGTH_SHORT).show();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess())
            {
                GoogleSignInAccount acct = result.getSignInAccount();
                //Uri uri = acct.getPhotoUrl();
                //System.out.println("llllll  "+uri.toString());
                //Toast.makeText(this, acct.toString(), Toast.LENGTH_LONG).show();
                final String mFullName = acct.getDisplayName();
                final String mEmail = acct.getEmail();
                JSONObject loginJson = new JSONObject();

                try {
                    loginJson.put("full_name",mFullName);
                    loginJson.put("email_address",mEmail);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println("login json: "+loginJson.toString());

                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();



                JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.POST, WebService.LOGIN_URL, loginJson, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                            // the response is already constructed as a JSONObject!
                            try {
                                System.out.println("login response: "+response.toString());

                                if(response.has("success"))
                                {
                                    String full_name = response.getString("full_name");
                                    String email_address = response.getString("email_address");
                                    ((NiggaChatApplication)getApplication()).loggedInUser(full_name,email_address);
                                    Intent intent = new Intent(LoginActivity.this,ChatRoomActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    JSONObject error = response.getJSONObject("error");
                                    String text = error.getString("text");
                                    loginErrorMessage.setText(text);

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }, new Response.ErrorListener()
                    {

                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    }
                    )

                {
                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        Map<String, String> responseHeaders = response.headers;
                        for(String key:responseHeaders.keySet())
                        {
                            if("access-token".equalsIgnoreCase(key))
                                ((NiggaChatApplication)getApplication()).setAccess_token(responseHeaders.get(key));
                            System.out.println(key+":"+responseHeaders.get(key));

                        }
                        System.out.println("omi token"+responseHeaders.get("access-token"));
                        return super.parseNetworkResponse(response);
                    }
                };


            {
            };
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(LoginActivity.this).add(jsonRequest);

            }
            else
            {
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


