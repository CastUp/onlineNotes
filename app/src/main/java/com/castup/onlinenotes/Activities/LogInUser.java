package com.castup.onlinenotes.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import com.castup.onlinenotes.Models.AccountInformation;
import com.castup.onlinenotes.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;


public class LogInUser extends AppCompatActivity {

    private TextView signGoogle , loginFacebook;
    private TextView textOnline ;
    private CardView  cardViewlog ;

    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient googleSignInClient;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;


    private static final int GOOGLE_SIGN_IN = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_user);
        views();
        CreateRequest();
        ClickAccounts();


    }

    private void views() {

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        signGoogle = findViewById(R.id.loginGoogle);
        loginFacebook = findViewById(R.id.login_facebook);
        cardViewlog = findViewById(R.id.cardViewLog);
        textOnline = findViewById(R.id.textOnline);
        cardViewlog.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.animotion_top_bottom));
        textOnline.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.anim_online_lift_to_right));

    }

    private void ClickAccounts() {

        signGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SingIn();
            }
        });

        //================facebook==========================================

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        loginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginManager.getInstance().logInWithReadPermissions(LogInUser.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {


                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });
            }
        });
    }

    private void CreateRequest() {

        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
    }

    private void SingIn() {

        Intent SignInintent = googleSignInClient.getSignInIntent();
        startActivityForResult(SignInintent, GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GOOGLE_SIGN_IN:

                if (resultCode == RESULT_OK && data != null) {

                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                    try {

                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account.getIdToken(), account);


                    } catch (ApiException e) {

                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(String idToken, final GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            DataUser(account);

                        } else {

                            Snackbar.make(signGoogle, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    private void handleFacebookAccessToken(final AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);


                        } else {

                            Toast.makeText(getBaseContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {


            String UID = user.getUid();

            AccountInformation information = new AccountInformation(user.getDisplayName().toLowerCase(), UID, user.getEmail().toLowerCase(), String.valueOf(user.getPhotoUrl()));

            databaseRef.child("Users").child(UID).setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                        finish();
                    }

                }
            });

        } else {

            Toast.makeText(getBaseContext(), "Please Sign in to continue.", Toast.LENGTH_SHORT).show();
        }

    }

    private void DataUser(GoogleSignInAccount account) {

        String UID = mAuth.getCurrentUser().getUid();
        AccountInformation information = new AccountInformation(account.getDisplayName().toLowerCase(), UID, account.getEmail().toLowerCase(), String.valueOf(account.getPhotoUrl()));

        databaseRef.child("Users").child(UID).setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                     finish();

                } else {

                    Toast.makeText(getBaseContext(), "Please Sign in to continue.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
