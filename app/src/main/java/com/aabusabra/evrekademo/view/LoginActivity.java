package com.aabusabra.evrekademo.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aabusabra.evrekademo.R;
import com.aabusabra.evrekademo.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class LoginActivity extends AppCompatActivity {

    private String TAG = LoginActivity.class.getSimpleName();
    private Context context;

    private Button loginbtn;
    private TextInputEditText usernametxt, passwordtxt;
    private TextInputLayout usernameLayout, passwordLayout;




    //set font
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        Utils.overrideFonts(getApplicationContext(), getWindow().getDecorView().getRootView());
        context = getApplicationContext();



        //hide keyboard when click at anyplace inside window
        Utils.setupKeyboardHider(LoginActivity.this, getWindow().getDecorView().getRootView());


        loginbtn = findViewById(R.id.loginbtn);
        usernametxt = findViewById(R.id.usernametxt);
        passwordtxt = findViewById(R.id.passwordtxt);

        usernameLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);



        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Utils.haveNetworkConnection(context))
                {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.activity_customized_toast, findViewById(R.id.custom_toast_container));

                    TextView text = layout.findViewById(R.id.text);
                    text.setText(R.string.no_internet_connection_msg);

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 40);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();

                    loginbtn.setEnabled(false);
                }
                else{

                    if (usernametxt.getText().toString().equalsIgnoreCase("evreka") && passwordtxt.getText().toString().equalsIgnoreCase("123456")) {
                        Intent i = new Intent(LoginActivity.this, OperationActivity.class);
                        startActivity(i);
                        finish();
                    }else{
                        if (!usernametxt.getText().toString().equalsIgnoreCase("evreka"))
                        {
                            usernametxt.setError("");
                            usernameLayout.setErrorEnabled(true);
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.activity_customized_toast, findViewById(R.id.custom_toast_container));

                            TextView text = layout.findViewById(R.id.text);
                            text.setText(getResources().getString(R.string.error_login_msg)+" ("+usernametxt.getText().toString().trim()+")");

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.BOTTOM, 0, 40);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            loginbtn.setEnabled(false);

                        }else{
                            if (!passwordtxt.getText().toString().equalsIgnoreCase("123456"))
                            {
                                usernameLayout.setErrorEnabled(false);
                                passwordtxt.setError("");
                                passwordLayout.setErrorEnabled(true);
                                passwordLayout.setPasswordVisibilityToggleEnabled(false);
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.activity_customized_toast, findViewById(R.id.custom_toast_container));

                                TextView text = layout.findViewById(R.id.text);
                                text.setText(getResources().getString(R.string.error_login_pass_msg));

                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.BOTTOM, 0, 40);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();

                                loginbtn.setEnabled(false);

                            }
                        }
                    }

                }

            }
        });


        usernametxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                loginbtn.setEnabled(true);
                usernameLayout.setErrorEnabled(false);
                passwordLayout.setErrorEnabled(false);
                passwordLayout.setPasswordVisibilityToggleEnabled(true);



            }
        });

        passwordtxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                loginbtn.setEnabled(true);
                usernameLayout.setErrorEnabled(false);
                passwordLayout.setErrorEnabled(false);
                passwordLayout.setPasswordVisibilityToggleEnabled(true);


            }
        });



    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}