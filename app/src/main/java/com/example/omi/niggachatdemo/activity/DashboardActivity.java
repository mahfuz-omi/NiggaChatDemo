package com.example.omi.niggachatdemo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.omi.niggachatdemo.R;
import com.example.omi.niggachatdemo.application.NiggaChatApplication;
import com.example.omi.niggachatdemo.webservice.WebService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.createGroupBn) Button createGroupBn;
    @BindView(R.id.joinGroupBn) Button joinGroupBn;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);


        createGroupBn.setOnClickListener(this);
        joinGroupBn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.createGroupBn:
            {
                createGroup();
                break;
            }
            case R.id.joinGroupBn:
            {
                joinGroup();
                break;
            }
        }

    }


    public void createGroup()
    {
        Intent intent = new Intent(this,CreateChatGroupActivity.class);
        startActivity(intent);

    }

    public void joinGroup()
    {
        Intent intent = new Intent(this,ChatRoomActivity.class);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {

        final AlertDialog logoutDialog  = new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        logout();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                }).create();

        logoutDialog.show();

    }


    public void logout()
    {
        pDialog = new ProgressDialog(DashboardActivity.this);
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


                            if (response.has("success"))
                            {
                                ((NiggaChatApplication) getApplication()).logoutUser();
                                Toast.makeText(DashboardActivity.this, "You are logged out now!!", Toast.LENGTH_LONG).show();
                                finish();
                                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
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
                        Toast.makeText(DashboardActivity.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(DashboardActivity.this).add(jsonRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_logout:
            {
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
