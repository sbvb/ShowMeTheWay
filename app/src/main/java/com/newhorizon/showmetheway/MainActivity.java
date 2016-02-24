package com.newhorizon.showmetheway;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String SP_FILE = "com.newhorizon.showmetheway.SP";

    private SharedPreferences sp;
    private String email;

    Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp = getSharedPreferences(SP_FILE, MODE_PRIVATE);
        email = sp.getString("EMAIL", null);

        if(email == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        final Context context = this;

        requestRoutes();
    }

    private void requestRoutes() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://54.233.119.112:8080/axis2/services/HelloClass/request_routes";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String[] partsTotal = response.split("<ns:return>");
                        ArrayList<String> parts = new ArrayList<>();
                        for (int i = 1; i < partsTotal.length; i++) {
                            parts.add(partsTotal[i].replace("</ns:return>","").replace("amp;",""));
                        }

                        for (int i = 0; i < parts.size(); i++ ) {
                            String[] pieces = parts.get(i).split("&");

                            TextView tv = new TextView(ctx);
                            tv.setPadding(50, 0, 20, 50);
                            tv.setTextSize(18);
                            tv.setTextColor(Color.BLACK);
                            tv.setText(pieces[2].replace("NAME=", ""));
                            tv.setId(Integer.parseInt(pieces[0].replace("ID=", "")));
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences.Editor edit = sp.edit();
                                    edit.putInt("MODE_MAPS", v.getId());
                                    edit.apply();

                                    finish();
                                    startActivity(new Intent(ctx, MapsActivity.class));
                                }
                            });
                            LinearLayout ll = (LinearLayout) findViewById(R.id.main_layout);
                            ll.addView(tv);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestFailed();
            }
        });

        queue.add(stringRequest);
    }

    private void requestFailed() {

    };

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