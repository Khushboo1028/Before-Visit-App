package com.beforevisit.beforevisit.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beforevisit.beforevisit.R;
import com.beforevisit.beforevisit.utility.AddUserDataToFirestore;
import com.beforevisit.beforevisit.utility.DefaultTextConfig;
import com.beforevisit.beforevisit.utility.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;


public class LoginMainActivity extends AppCompatActivity {

    private static final String TAG = "LoginMainActivity";
    private static final int RC_SIGN_IN = 9001;

    Button btn_create_account,btn_login;
    TextView tv_skip;
    ImageView image_facebook_login,image_google_login;
    RelativeLayout super_rel;
    ProgressBar progress_bar;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseUser currentUser;

    //facebook login
    private CallbackManager mCallbackManager;
    private LoginButton btn_facebook_login;

    //firebasefirestore
    AddUserDataToFirestore addUserDataToFirestore;
    Utils utils;
    ListenerRegistration listenerRegistration;
    String mobile_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), LoginMainActivity.this);
        setContentView(R.layout.activity_login_main);

        init();

        btn_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });



        //configure google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("708801181328-1f6kcpjchanm8qg5lls8pcdp1ovf3v5k.apps.googleusercontent.com")
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(LoginMainActivity.this, gso);


        image_google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(utils.isInternetAvailable(LoginMainActivity.this)){
                    signInGoogle();
                }

            }
        });


        //configure facebook login
        FacebookSdk.sdkInitialize(getApplicationContext());
      //  AppEventsLogger.activateApp(getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();
        image_facebook_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_facebook_login.performClick();
            }
        });

        btn_facebook_login.setPermissions("email", "public_profile", "user_friends");

        btn_facebook_login.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);

            }
        });


    }

    private void init(){

        btn_create_account = (Button) findViewById(R.id.btn_create_account);
        btn_login = (Button) findViewById(R.id.btn_login);

        tv_skip = (TextView) findViewById(R.id.tv_skip);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

        utils = new Utils();

        image_facebook_login = (ImageView) findViewById(R.id.image_facebook_login);
        image_google_login = (ImageView) findViewById(R.id.image_google_login);
        super_rel = (RelativeLayout) findViewById(R.id.super_rel);

        mAuth = FirebaseAuth.getInstance();

        //facebook
        btn_facebook_login = (LoginButton) findViewById(R.id.image_facebook_login_hidden);

        //firestore
        addUserDataToFirestore = new AddUserDataToFirestore();


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Facebook
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Log.i(TAG, "Data is "+data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);

            }
        }


    }



    private void firebaseAuthWithGoogle(String idToken) {
        // [START_EXCLUDE silent]
        progress_bar.setVisibility(View.VISIBLE);
        // [END_EXCLUDE]
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();

                            listenerRegistration = FirebaseFirestore.getInstance().collection(getString(R.string.users)).document(user.getUid())
                                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                            if(e!=null){
                                                Log.i(TAG,"Error: "+e.getMessage());
                                            }else{
                                                if(snapshot!=null && snapshot.exists()) {
                                                    mobile_no = snapshot.getString(getString(R.string.mobile_no));

                                                    if (mobile_no == null) {
                                                        Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                                                        startActivity(intent);

                                                    } else {

                                                        finish();

                                                    }
                                                }else{
                                                    Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    });




                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(super_rel, "Authentication Failed: "+task.getException().getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();

                        }

                        progress_bar.setVisibility(View.GONE);

                    }
                });
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    //TODO:Put this code wherever in future

    public void signOut() {
        // Firebase sign out
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser!=null) {
            mAuth.signOut();

            // Google sign out
            if (mGoogleSignInClient != null) {
                mGoogleSignInClient.signOut().addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.i(TAG, "User has signed out");
                            }
                        });
            }

            //facebook logout
            LoginManager.getInstance().logOut();
        }



    }


    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
       progress_bar.setVisibility(View.VISIBLE);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            listenerRegistration = FirebaseFirestore.getInstance().collection(getString(R.string.users)).document(user.getUid())
                                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                            if(e!=null){
                                                Log.i(TAG,"Error: "+e.getMessage());
                                            }else{
                                                if(snapshot!=null && snapshot.exists()) {
                                                    mobile_no = snapshot.getString(getString(R.string.mobile_no));

                                                    if (mobile_no == null) {
                                                        Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                                                        startActivity(intent);

                                                    } else {

                                                        finish();

                                                    }
                                                }else{
                                                    Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginMainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                       progress_bar.setVisibility(View.GONE);
                    }
                });
    }
    // [END auth_with_facebook]




    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
         currentUser = mAuth.getCurrentUser();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(listenerRegistration!=null){
            listenerRegistration=null;
        }
    }
}
