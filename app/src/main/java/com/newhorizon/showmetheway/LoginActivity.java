package com.newhorizon.showmetheway;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class LoginActivity extends AppCompatActivity {
    public static final String SP_FILE = "com.newhorizon.showmetheway.SP";

    private EditText et_email;
    private EditText et_password;

    private boolean result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email = (EditText) findViewById(R.id.email);
        et_password = (EditText) findViewById(R.id.password);

        Button sign_in = (Button) findViewById(R.id.sign_in_button);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String username = et_email.getText().toString();
        String password = et_password.getText().toString();

        View focusView = null;
        boolean cancel = false;

        if (TextUtils.isEmpty(username)) {
            et_email.setError(getString(R.string.error_field_required));
            focusView = et_email;
            cancel = true;
        } else if (!isEmailValid(username)) {
            et_email.setError(getString(R.string.error_invalid_email));
            focusView = et_email;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            et_password.setError(getString(R.string.error_field_required));
            focusView = et_password;
            cancel = true;
        }

        if(cancel) {
            focusView.requestFocus();
        } else {
            requestLogin(username, password);
        }
    }

    /**
     * Request server login
     * @return true/false
     */
    private void requestLogin(String username, String password) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://54.233.119.112:8080/axis2/services/HelloClass/login?";
        url = url + "username=" + username;
        url = url + "&password=" + password;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("Login Successfull")) {
                            loginSuccessfull();
                        } else {
                            loginFailed();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loginFailed();
                    }
                });

        queue.add(stringRequest);
    }

    private void loginSuccessfull() {
        SharedPreferences sp = getSharedPreferences(SP_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("EMAIL", et_email.getText().toString());
        editor.apply();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void loginFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.error_invalid_login);
        builder.setTitle(R.string.error_invalid_login_title);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing, dismiss dialog
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }
}
