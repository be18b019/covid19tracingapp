package com.example.covid_19contacttracingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private final String TAG="LoginActivity";
    private final int REQ_MAIN=1;
    private DataAccessObject dataAccessObject;


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EditText etUserName = findViewById(R.id.etUserName);
        EditText etPassword = findViewById(R.id.etPassword);
        etUserName.setText("");
        etPassword.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUserName=findViewById(R.id.etUserName);
        EditText etPassword=findViewById(R.id.etPassword);

        Button btnLogin=findViewById(R.id.btnLogin);
        Button btnRegister=findViewById(R.id.btnRegister);

        dataAccessObject =new DataAccessObject(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                String username=etUserName.getText().toString();
                String password=etPassword.getText().toString();

                if (username.equals("")||password.equals("")){
                    Toast.makeText(LoginActivity.this,"username or password is missing", Toast.LENGTH_LONG).show();
                }
                else{
                    Boolean matching= dataAccessObject.checkUserNamePassword(username, password);
                    if (matching){
                        Intent intent= new Intent(getApplicationContext(),HomeActivity.class);
                        intent.putExtra("USERNAME", username);
                        startActivityForResult(intent, REQ_MAIN);
                    }
                    else{
                        Toast.makeText(LoginActivity.this,"invalid credentials", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=etUserName.getText().toString();
                String password=etPassword.getText().toString();
                User user=new User(username, password);

                if (username.equals("")||password.equals("")){
                    Toast.makeText(LoginActivity.this,"username or password is missing", Toast.LENGTH_LONG).show();
                }
                else{
                    Boolean userExists= dataAccessObject.checkUserName(username);
                    if (userExists==false){
                        Boolean registeredSuccessfully= dataAccessObject.registerUser(user);
                        if (registeredSuccessfully){
                            Toast.makeText(LoginActivity.this,"registered successfully", Toast.LENGTH_LONG).show();
                            Intent intent= new Intent(getApplicationContext(),HomeActivity.class);
                            intent.putExtra("USERNAME", username);
                            startActivityForResult(intent, REQ_MAIN);
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "registration failed", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "user already exists", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}