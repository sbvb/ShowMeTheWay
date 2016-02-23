package com.newhorizon.showmetheway;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String SP_FILE = "com.newhorizon.showmetheway.SP";

    private SharedPreferences sp;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp = getSharedPreferences(SP_FILE, MODE_PRIVATE);
        final SharedPreferences.Editor edit = sp.edit();
        email = sp.getString("EMAIL", null);

        if(email == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        final Context context = this;

        int aux = 0;
        while (true) {
            String route_name = sp.getString("ROUTE_NAME;" + aux, null);

            if(route_name == null) {
                break;
            }
            else {
                TextView tv = new TextView(this);
                tv.setPadding(50, 0, 20, 50);
                tv.setTextSize(18);
                tv.setTextColor(Color.BLACK);
                tv.setText(route_name);
                tv.setId(aux);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edit.putInt("MODE_MAPS", v.getId());
                        edit.apply();

                        finish();
                        startActivity(new Intent(context, MapsActivity.class));
                    }
                });
                LinearLayout ll = (LinearLayout) findViewById(R.id.main_layout);
                ll.addView(tv);
                aux += 1;
            }
        }
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
                logout();
            case R.id.draw_route_button:
                sp = getSharedPreferences(SP_FILE, MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putInt("MODE_MAPS", -1);
                edit.apply();
                startActivity(new Intent(this, MapsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("EMAIL");
        editor.apply();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}