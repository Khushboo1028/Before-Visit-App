package com.beforevisit.beforevisit.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beforevisit.beforevisit.R;
import com.beforevisit.beforevisit.Utility.DefaultTextConfig;
import com.beforevisit.beforevisit.Utility.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    public static final String TAG = "ForgotPasswordActivity";
    RelativeLayout main_rel;
    ImageView img_done;
    EditText et_email;
    TextView tv_error_email;
    String email;

    AlertDialog.Builder builder;
    AlertDialog alert;

    Utils utils;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ForgotPasswordActivity.this);
        setContentView(R.layout.activity_forgot_password);

        init();


        main_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(main_rel.getWindowToken(), 0);
            }
        });

        img_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_error_email.setVisibility(View.GONE);
                email = et_email.getText().toString().trim();

                if(utils.isInternetAvailable(ForgotPasswordActivity.this)) {
                    if (email.equals("")) {
                        utils.alertDialog(ForgotPasswordActivity.this,"Incomplete Details","Please enter all the details");

                    } else if (!utils.isValidEmailAddress(email)) {
                        tv_error_email.setVisibility(View.VISIBLE);
                        et_email.requestFocus();
                        et_email.setBackgroundResource(R.drawable.red_border_rectangle);
                        utils.vibrate(getApplicationContext());
                    } else {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("ForgotPasswordActivity", "Email sent.");
                                            utils.alertDialog(ForgotPasswordActivity.this,"Success","Kindly check your email to change password!");
                                        }else{
                                            Log.i(TAG,"Error in sending password" + task.getException().getLocalizedMessage());
                                            utils.alertDialog(ForgotPasswordActivity.this,"Error!",task.getException().getLocalizedMessage());

                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private void init() {
        main_rel = (RelativeLayout) findViewById(R.id.main_rel);


        img_done = (ImageView) findViewById(R.id.img_done);

        tv_error_email = (TextView) findViewById(R.id.tv_error_email);

        et_email = (EditText) findViewById(R.id.et_email);
        utils = new Utils();

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
