package com.soulfriends.meditation.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.soulfriends.meditation.R;

public class AuthManager {

    private static final String TAG = "authmanager";
    private FirebaseAuth mAuth;
    //private ProgressBar progressBar;

    private final static int RC_SIGN_IN = 123;

    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth.AuthStateListener mAuthListner;
    CallbackManager callbackManager = CallbackManager.Factory.create();

    private ResultListener resultListener;

    public void setResultListener(ResultListener listener) {
        this.resultListener = listener;
    }

    public void onStart() {
        mAuth.addAuthStateListener(mAuthListner);
    }

    public void onStop() {
        if (mAuthListner != null) {
            mAuth.removeAuthStateListener(mAuthListner);
        }
    }

    public void init(Activity activity) {
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
    }


    public Boolean IsLogin(Activity activity) {
        final Boolean[] bLogin = {false};
        mAuth = FirebaseAuth.getInstance();

        //check the current user
        if (mAuth.getCurrentUser() != null) {
            bLogin[0] = true;
        }

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    bLogin[0] = true;
                }
            }
        };
        return bLogin[0];
    }

    // 이메일 로그인
    public void DoEmail(Activity activity, String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            PreferenceManager.setString(activity.getBaseContext(), "email", email);
                            PreferenceManager.setString(activity.getBaseContext(), "certification", "email");
                            resultListener.onSuccess(50, "success");
                        } else {
                            resultListener.onFailure(50, "success");
                            //DoAccountCreate(activity, email, password);
                        }
                    }
                });
    }

    // 이메일 계정 생성
    public void DoAccountCreate(Activity activity, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            PreferenceManager.setString(activity.getBaseContext(), "email", email);
                            PreferenceManager.setString(activity.getBaseContext(), "certification", "email");
                            resultListener.onSuccess(0, "success");
                        } else {
                            resultListener.onFailure(0, "failed");
                        }
                    }
                });
    }


    public void DoGoogle(Activity activity) {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(activity, account);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }

        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void firebaseAuthWithGoogle(Activity activity, GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            PreferenceManager.setString(activity.getBaseContext(), "certification", "google");
                            resultListener.onSuccess(50, "success");
                        } else {
                            resultListener.onFailure(50, "failed");
                        }
                    }
                });
    }

    public void sendPasswordResetEmail(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            resultListener.onSuccess(100, "success");
                            //Log.d(TAG, "Email sent.");
                        } else {
                            resultListener.onFailure(100, "success");
                        }
                    }
                });
    }
}
