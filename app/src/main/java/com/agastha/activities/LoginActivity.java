package com.agastha.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.agastha.agastha.R;
import com.agastha.restapis.AgasthaApi;
import com.agastha.restapis.LoginInfo;
import com.agastha.restapis.LoginResponse;
import com.squareup.okhttp.OkHttpClient;


import java.util.concurrent.TimeUnit;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginActivity extends AppCompatActivity {
    Button bu;
    EditText input_name,input_password;
    String username,password,authstring;
    ProgressDialog progressDialog;
    LoginInfo regInfo;
    AgasthaApi api;
    final String MyPREFERENCES = "MyPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        input_name = (EditText)findViewById(R.id.input_name);
        input_password = (EditText)findViewById(R.id.input_password);

        bu = (Button)findViewById(R.id.btn_signup);
        bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgasthaApplication app = (AgasthaApplication)LoginActivity.this.getApplication();
                username= input_name.getText().toString();
                password =  input_password.getText().toString();
                if(username != null && !username.isEmpty()) {

//                      progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
//                      progressDialog.setIndeterminate(true);
//                      progressDialog.setMessage("Registering...");
//                      progressDialog.setCancelable(false);
//                      progressDialog.show();

                      OkHttpClient okHttpClient = new OkHttpClient();
                      okHttpClient.setConnectTimeout(120000, TimeUnit.MILLISECONDS);
                      okHttpClient.setReadTimeout(120000, TimeUnit.MILLISECONDS);


                      Retrofit retrofit = new Retrofit.Builder()
                              .baseUrl("http://sakthi:8044/AgasthaOne")
                              .addConverterFactory(GsonConverterFactory.create())
                              .client(okHttpClient)
                              .build();
                      api = retrofit.create(AgasthaApi.class);
                      regInfo = new LoginInfo();
                      authstring = username +":"+ password;
                      byte[] encodeValue = Base64.encode(authstring.getBytes(), Base64.DEFAULT);

                      String name= new String(new String(encodeValue));

                      byte[] decodeValue = Base64.decode(name, Base64.DEFAULT);

                      String userdecrypt = new String(new String(decodeValue));
                      SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
                      SharedPreferences.Editor editor = prefs.edit();
                      editor.putString("User", userdecrypt);
                      editor.commit();


                      //regInfo.setUser(new String(encodeValue));
                      regInfo.setUser(username);
                      regInfo.setPass(password);
                         regInfo.setAuthorization(name);

                      final Call<LoginResponse> call = api.registerMobile(regInfo);
                            call.enqueue(new Callback<LoginResponse>() {
                                @Override
                                public void onResponse(Response<LoginResponse> response, Retrofit retrofit) {

                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                    }
                                    LoginResponse resp = response.body();


                                    Intent i = new Intent(LoginActivity.this, CompaniesActivity.class);
                                    startActivity(i);
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    Toast.makeText(LoginActivity.this, "Error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    t.printStackTrace();
                                }
                            });


                    }

                else {
                    input_name.setError("Enter valid User Name");
                }
            }
        });


    }

}
