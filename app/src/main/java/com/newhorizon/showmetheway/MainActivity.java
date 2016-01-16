package com.newhorizon.showmetheway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final String SP_FILE = "com.newhorizon.showmetheway.SP";

    private SharedPreferences sp;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp = getSharedPreferences(SP_FILE, MODE_PRIVATE);
        email = sp.getString("EMAIL", null);

        if(email == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        FloatingActionButton request = (FloatingActionButton) findViewById(R.id.request_route);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle request
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_button:
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("EMAIL");
                editor.apply();

                startActivity(new Intent(this, LoginActivity.class));
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}