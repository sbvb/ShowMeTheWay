package com.newhorizon.showmetheway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    public static final String SP_FILE = "com.newhorizon.showmetheway.SP";

    private EditText et_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email = (EditText) findViewById(R.id.email);

        Button sign_in = (Button) findViewById(R.id.sign_in_button);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String email = et_email.getText().toString();

        View focusView = null;
        boolean cancel = false;

        if (TextUtils.isEmpty(email)) {
            et_email.setError(getString(R.string.error_field_required));
            focusView = et_email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            et_email.setError(getString(R.string.error_invalid_email));
            focusView = et_email;
            cancel = true;
        }

        if(cancel) {
            focusView.requestFocus();
        } else {
            SharedPreferences sp = getSharedPreferences(SP_FILE, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("EMAIL", email);
            editor.apply();

            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }
}
