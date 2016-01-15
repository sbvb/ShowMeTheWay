package com.newhorizon.showmetheway;

import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public static final String SP_FILE = "com.newhorizon.showmetheway.SP";

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getSharedPreferences(SP_FILE, MODE_PRIVATE);
        email = sp.getString("EMAIL", null);

        if(email == null) {
            finish();
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        }

        final SharedPreferences.Editor editor = sp.edit();
        final Context ctx = this;

        Button logout = (Button) findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.remove("EMAIL");
                editor.apply();

                startActivity(new Intent(ctx, LoginActivity.class));
                finish();
            }
        });
    }
}