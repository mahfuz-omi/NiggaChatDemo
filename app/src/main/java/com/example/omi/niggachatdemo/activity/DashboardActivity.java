package com.example.omi.niggachatdemo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.omi.niggachatdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.createGroupBn) Button createGroupBn;
    @BindView(R.id.joinGroupBn) Button joinGroupBn;

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
}
