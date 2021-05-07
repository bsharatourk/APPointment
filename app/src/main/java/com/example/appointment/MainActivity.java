package com.example.appointment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appointment.Common.Common;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@SuppressWarnings("unchecked")
public class MainActivity extends AppCompatActivity {


    private static final String TAG = "zebe1";
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart() {
        super.onStart();
//        User user = Common.currentUser;
        mAuth.addAuthStateListener(mAuthListener);
//
//        FirebaseUser mfirebaseuser = mAuth.getCurrentUser();
//
//        if(mfirebaseuser!=null){
//           // Common.currentUser.setPhoneNum(user.getPhoneNum());
//            Toast.makeText(MainActivity.this, mfirebaseuser.toString(), Toast.LENGTH_SHORT).show();
//            Common.setIsLogin("IsLogged");
//            Common.currentUser = new User(mfirebaseuser.getDisplayName(),mfirebaseuser.getEmail(),null);
//            Log.e(TAG, "zebe2");
//            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
//            startActivity(intent);
//        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


//        mAuthListener = new FirebaseAuth.AuthStateListener(){
//            @Override
//            public  void  onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//
//                if(user !=null){
//                    Log.e(TAG, "got here 44658");
//                    Common.setIsLogin("IsLogged");
//                    Common.currentUser = new User(user.getDisplayName(),user.getEmail(),user.getPhoneNumber());
//                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        };

        Log.e(TAG, "got here 1");

//        if (user!=null){
//            Common.LOGGED_IN_FLAG = true;
//            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
//            startActivity(intent);
//        }
//
     //   Log.e(TAG, mAuth.getCurrentUser().getDisplayName());

        Log.e(TAG, "got here 2");

        checkuser();
        createRequest();


        findViewById(R.id.google_signIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    protected void onDestroy(){
        super.onDestroy();
        mAuth.signOut();
    }

    private void checkuser() {

        FirebaseUser mUser = mAuth.getCurrentUser();
        if(mUser != null){
            Log.e(TAG, "got here 44658");
            Common.setIsLogin("IsLogged");
            Common.currentUser = new User(mUser.getDisplayName(),mUser.getEmail(),mUser.getPhoneNumber());
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

                mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public  void  onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user !=null){
                    Log.e(TAG, "got here 44658");
                    Common.setIsLogin("IsLogged");
                    Common.currentUser = new User(user.getDisplayName(),user.getEmail(),user.getPhoneNumber());
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }


    private void createRequest() {


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = (GoogleSignInAccount) task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            checkIfUserExists(acct,user);
                            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(MainActivity.this, "Sorry auth failed.", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    public void checkIfUserExists(GoogleSignInAccount accountSign, FirebaseUser user){


        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    Common.currentUser = new User(accountSign.getDisplayName(),accountSign.getEmail(),user.getPhoneNumber());
                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(intent);
                }
                else {
                    Common.currentUser = null;
                }
            }
        };

        //Init and attach
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(authStateListener);



//        if(user==null){
//            Common.currentUser = null;
//        }
//        else{
//            Common.currentUser = new User(accountSign.getDisplayName(),accountSign.getEmail(),user.getPhoneNumber());
//            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
//            startActivity(intent);
//        }
    }



}