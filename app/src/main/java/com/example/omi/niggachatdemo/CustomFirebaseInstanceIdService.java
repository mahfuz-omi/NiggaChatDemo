package com.example.omi.niggachatdemo;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by omi on 11/9/2016.
 */

public class CustomFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        System.out.println("on token refresh");
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("device id: "+refreshedToken);
        JSONObject pnJson = new JSONObject();
        try
        {
            pnJson.put("reg_id",refreshedToken);

        }catch(Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("pn json: "+pnJson.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, WebService.PNREGISTRATION_URL, pnJson, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // the response is already constructed as a JSONObject!
                        try {
                            System.out.println("login response: "+response.toString());

                            if(response.has("success"))
                            {
                                System.out.println("pn successful");
                            }
                            else
                            {
                                System.out.println("pn registration failed");
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
                        Toast.makeText(CustomFirebaseInstanceIdService.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
                )


        {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("access-token",((NiggaChatApplication)getApplication()).getAccess_token());
                return headers;

            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(CustomFirebaseInstanceIdService.this).add(jsonRequest);
    }
}
