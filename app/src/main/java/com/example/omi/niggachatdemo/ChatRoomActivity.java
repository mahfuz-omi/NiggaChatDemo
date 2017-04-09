package com.example.omi.niggachatdemo;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.omi.niggachatdemo.adapter.ChatMessageAdapter;
import com.example.omi.niggachatdemo.model.ChatMessage;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by omi on 11/8/2016.
 */

public class ChatRoomActivity  extends AppCompatActivity {
    String PREFERENCE_FIRST_RUN = "isFirstRun";
    LinearLayout parent;
    EditText myMessage;
    private ProgressDialog pDialog;
    LayoutInflater layoutInflater;
    RecyclerView recyclerView;
    ArrayList<ChatMessage> chatMessages;
    ChatMessageAdapter chatMessageAdapter;
    TextView full_name;
    boolean onNewIntent = false;



    public void logout()
    {
        pDialog = new ProgressDialog(ChatRoomActivity.this);
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
                            System.out.println("response: "+response.toString());


                            if(response.has("success"))
                            {
                                ((NiggaChatApplication)getApplication()).logoutUser();
                                Toast.makeText(ChatRoomActivity.this, "You are logged out now!!", Toast.LENGTH_LONG).show();
                            }

                            Intent intent = new Intent(ChatRoomActivity.this,LoginActivity.class);
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
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        Toast.makeText(ChatRoomActivity.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(ChatRoomActivity.this).add(jsonRequest);
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


    private BroadcastReceiver myBroadcastReceiver =
            new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {

                    String from = intent.getStringExtra("from");
                    String message = intent.getStringExtra("message");
                    String time = intent.getStringExtra("time");
                    getMessage(from,message,time);
                    //Toast.makeText(context, "received", Toast.LENGTH_SHORT).show();

                }
            };

    IntentFilter intentFilter = new IntentFilter("receive_message");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        //setContentView(R.layout.test);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setLogo(R.drawable.icon);
//        actionBar.setDisplayUseLogoEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.i);

        this.full_name = (TextView) findViewById(R.id.full_name);
        full_name.setText(((NiggaChatApplication)getApplication()).getFull_name());

        registerReceiver(myBroadcastReceiver,intentFilter);

        if(!((NiggaChatApplication)getApplication()).isUserLoggedIn())
        {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        this.chatMessages = new ArrayList<>();
        this.chatMessageAdapter = new ChatMessageAdapter(chatMessages,this);

        this.recyclerView = (RecyclerView)findViewById(R.id.parent);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chatMessageAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(ChatRoomActivity.this,DividerItemDecoration.VERTICAL));

        this.myMessage = (EditText)findViewById(R.id.myMessage);
        if( ((NiggaChatApplication)getApplication()).isFirstRun())
        {
            //Toast.makeText(this, "firrrrrst", Toast.LENGTH_SHORT).show();
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
                                    ((NiggaChatApplication)getApplication()).setFirstRunFalse();

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
                            Toast.makeText(ChatRoomActivity.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
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
            Volley.newRequestQueue(ChatRoomActivity.this).add(jsonRequest);

        }
        else
        {
            //Toast.makeText(this, "not firrrrrst", Toast.LENGTH_SHORT).show();
        }







    }

    @Override
    protected void onNewIntent(Intent i) {
        super.onNewIntent(i);
        //Toast.makeText(this, "new intent", Toast.LENGTH_SHORT).show();

        if(i.hasExtra("fromName") && i.hasExtra("message") && i.hasExtra("time"))
        {
            String fromName = i.getStringExtra("fromName");
            String message = i.getStringExtra("message");
            String time = i.getStringExtra("time");
            onNewIntent = true;
            this.getMessage(fromName,message,time);

        }
        else
        {
            System.out.println("no data new intent");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(this, "resume", Toast.LENGTH_SHORT).show();
        Intent i = getIntent();
        if(onNewIntent == true)
            return;

        if(i.hasExtra("fromName") && i.hasExtra("message") && i.hasExtra("time"))
        {
            String fromName = i.getStringExtra("fromName");
            String message = i.getStringExtra("message");
            String time = i.getStringExtra("time");
            this.getMessage(fromName,message,time);

        }
        else
        {
            System.out.println("no data resume");
        }
    }

    public void getMessage(String from, String message, String time)
    {
        ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(500);
        try
        {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("get message:  "+from+message+time);
        ChatMessage chatMessage = new ChatMessage(from,message,time);
        this.chatMessages.add(chatMessage);
        this.chatMessageAdapter.notifyDataSetChanged();
        this.recyclerView.scrollToPosition(this.chatMessages.size()-1);


    }

    public void sendMessage(View v)
    {

        String myMessageText  = myMessage.getText().toString();
        myMessage.setText("");
        if(myMessageText.length() != 0)
        {

            final ChatMessage chatMessage = new ChatMessage(((NiggaChatApplication)getApplication()).getFull_name(),myMessageText,"");
            this.chatMessages.add(chatMessage);
            this.chatMessageAdapter.notifyDataSetChanged();
            this.recyclerView.scrollToPosition(this.chatMessages.size()-1);
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(myMessage.getWindowToken(), 0);



            JSONObject sendMessageJson = new JSONObject();
            try {
                //sendMessageJson.put("from",((NiggaChatApplication)getApplication()).getEmail_address() );
                sendMessageJson.put("message",myMessageText);
                //sendMessageJson.put("time",System.currentTimeMillis());

            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("login json: "+sendMessageJson.toString());

//            pDialog = new ProgressDialog(ChatRoomActivity.this);
//            pDialog.setMessage("Please wait...");
//            pDialog.setCancelable(false);
//            pDialog.show();

            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.POST, WebService.SEND_MESSAGE_URL, sendMessageJson, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
//                            if (pDialog.isShowing())
//                                pDialog.dismiss();
                            // the response is already constructed as a JSONObject!
                            try {
                                System.out.println("login response: "+response.toString());

                                if(response.has("success"))
                                {
                                    System.out.println("message sent success");
                                    System.out.println(response);
                                    String time = response.getString("time");
                                    System.out.println(time);
                                    chatMessage.setTime(time);
                                    chatMessage.setShouldShowSentImage();
                                    chatMessageAdapter.notifyDataSetChanged();
//                                    ((NiggaChatApplication)getApplication()).setUserName(user_name);
//                                    Intent intent = new Intent(ChatRoomActivity.this,ChatRoomActivity.class);
//                                    startActivity(intent);
//                                    finish();
                                }
                                else
                                {
                                    JSONObject error = response.getJSONObject("error");
                                    String text = error.getString("text");
                                    Toast.makeText(ChatRoomActivity.this, text, Toast.LENGTH_SHORT).show();
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
//                            if (pDialog.isShowing())
//                                pDialog.dismiss();
                            //Toast.makeText(ChatRoomActivity.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
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
            Volley.newRequestQueue(ChatRoomActivity.this).add(jsonRequest);

        }
        else
        {
            //loginErrorMsg.setText("Please fill up all input");
            Toast.makeText(ChatRoomActivity.this,"plz fill up the box", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }
}
