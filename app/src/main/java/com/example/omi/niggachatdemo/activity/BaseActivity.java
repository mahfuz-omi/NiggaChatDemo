package com.example.omi.niggachatdemo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.BaseAdapter;
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
import com.example.omi.niggachatdemo.webservice.WebService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {
    ProgressDialog pDialogBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void logout()
    {
        pDialogBase = new ProgressDialog(BaseActivity.this);
        pDialogBase.setMessage("Please wait...");
        pDialogBase.setCancelable(false);
        pDialogBase.show();

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, WebService.LOGOUT_URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (pDialogBase.isShowing())
                            pDialogBase.dismiss();
                        // the response is already constructed as a JSONObject!
                        try {
                            System.out.println("response: "+response.toString());


                            if(response.has("success"))
                            {
                                ((NiggaChatApplication)getApplication()).logoutUser();
                                Toast.makeText(BaseActivity.this, "You are logged out now!!", Toast.LENGTH_LONG).show();
                            }

                            Intent intent = new Intent(BaseActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                            return;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()
                {

                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (pDialogBase.isShowing())
                            pDialogBase.dismiss();
                        Toast.makeText(BaseActivity.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
                )
        {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String access_token = ((NiggaChatApplication)getApplication()).getAccess_token();
                Map<String,String> headers = new HashMap<>();
                System.out.println("get token from application:"+access_token);
                headers.put("access-token",access_token);
                return headers;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(BaseActivity.this).add(jsonRequest);
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
