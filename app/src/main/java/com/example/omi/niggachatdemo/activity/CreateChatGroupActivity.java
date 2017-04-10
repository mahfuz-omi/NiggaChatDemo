package com.example.omi.niggachatdemo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.omi.niggachatdemo.R;
import com.example.omi.niggachatdemo.application.NiggaChatApplication;
import com.example.omi.niggachatdemo.model.ChatMessage;
import com.example.omi.niggachatdemo.webservice.WebService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateChatGroupActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.createGroupBn)
    Button createGroupBn;
    @BindView(R.id.groupNameEt)
    EditText groupNameEt;
    @BindView(R.id.groupType)
    RadioGroup radioGroup;
    @BindView(R.id.publicBn)
    RadioButton publicBn;
    @BindView(R.id.privateBn)
    RadioButton privateBn;
    @BindView(R.id.errorMessage)
    TextView errorMessage;

    ProgressDialog pDialog;

    String is_public = "1"; // 1 means public, 0 means private


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_group);
        ButterKnife.bind(this);

        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.createGroupBn.setOnClickListener(this);
        this.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (publicBn.isChecked())
                    is_public = "1";
                else if (privateBn.isChecked())
                    is_public = "0";

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createGroupBn: {
                createGroup();
                break;
            }
        }
    }


    public void createGroup() {
        if (TextUtils.isEmpty(groupNameEt.getText())) {
            groupNameEt.setError("Group Name can't be empty");
            return;
        }

        String groupName = groupNameEt.getText().toString();
        JSONObject createGroupJson = new JSONObject();
        try {
            //sendMessageJson.put("from",((NiggaChatApplication)getApplication()).getEmail_address() );
            createGroupJson.put("group_name", groupName);
            createGroupJson.put("is_public", is_public);
            //sendMessageJson.put("time",System.currentTimeMillis());

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("create Group json: " + createGroupJson.toString());

        pDialog = new ProgressDialog(CreateChatGroupActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, WebService.CREATE_CHAT_GROUP_URL, createGroupJson, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        // the response is already constructed as a JSONObject!
                        try {
                            System.out.println("login response: " + response.toString());

                            if (response.has("success")) {
                                System.out.println("message sent success");
                                System.out.println(response);
                            } else {
                                JSONObject error = response.getJSONObject("error");
                                String text = error.getString("text");
                                errorMessage.setText(text);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        Toast.makeText(CreateChatGroupActivity.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
                )

        {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("access-token", ((NiggaChatApplication) getApplication()).getAccess_token());
                return headers;

            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(CreateChatGroupActivity.this).add(jsonRequest);

    }

    public void logout() {
        pDialog = new ProgressDialog(CreateChatGroupActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, WebService.LOGOUT_URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        // the response is already constructed as a JSONObject!
                        try {
                            System.out.println("response: " + response.toString());


                            if (response.has("success")) {
                                ((NiggaChatApplication) getApplication()).logoutUser();
                                Toast.makeText(CreateChatGroupActivity.this, "You are logged out now!!", Toast.LENGTH_LONG).show();
                                finish();
                                Intent intent = new Intent(CreateChatGroupActivity.this, LoginActivity.class);
                                startActivity(intent);
                                return;
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        Toast.makeText(CreateChatGroupActivity.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
                ) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String access_token = ((NiggaChatApplication) getApplication()).getAccess_token();
                Map<String, String> headers = new HashMap<>();
                System.out.println("get token from application:" + access_token);
                headers.put("access-token", access_token);
                return headers;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(CreateChatGroupActivity.this).add(jsonRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout: {
                this.logout();
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
    }


}

