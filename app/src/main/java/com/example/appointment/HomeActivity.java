package com.example.appointment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.appointment.Common.Common;
import com.example.appointment.Fragments.HomeFragment;
import com.example.appointment.Fragments.ShoppingFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

public class HomeActivity extends AppCompatActivity {

    //DataBase Ref
    DatabaseReference databaseUser;
    private FirebaseFirestore db;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    //if code sent failed , it will resend OTP
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId;

    private static  final String TAG = "MAIN_TAG";

    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog pd;

    EditText codeEt, phoneEt;


    LinearLayout code;
    LinearLayout phone;

    Dialog phoneVerify;

    TextView name, mail, family;


    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    BottomSheetDialog bottomSheetDialog;

    CollectionReference userRef;

    AlertDialog dialog;

    @Override
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {
            //cancel the back button.
        }
    }

    private boolean shouldAllowBack() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(HomeActivity.this);


        databaseUser = FirebaseDatabase.getInstance().getReference("Client");
        //Log.e(TAG, "Common.LOGGED_IN_FLAG");
//
//        if (!Common.LOGGED_IN_FLAG){
//            FirebaseAuth.getInstance().signOut();
//            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//            startActivity(intent);
//        }

        //FireStore

        //Show phone authentication
        phoneVerify = new Dialog(HomeActivity.this);
        phoneVerify.setContentView(R.layout.custom_dialog);
        phoneVerify.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        phoneVerify.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        phoneVerify.setCancelable(false);

        code = (LinearLayout) phoneVerify.findViewById(R.id.CodeLl);
        phone = (LinearLayout) phoneVerify.findViewById(R.id.phoneLl);

        codeEt = (EditText)phoneVerify.findViewById(R.id.codeEt);
        phoneEt = (EditText)phoneVerify.findViewById(R.id.phoneEt);

        phone.setVisibility(View.VISIBLE);
        code.setVisibility(View.INVISIBLE);

        phoneVerify.show();

        //DataBase
        FirebaseApp.initializeApp(this);
        databaseUser = FirebaseDatabase.getInstance().getReference("Client");
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        //init progress dialog
        pd = new ProgressDialog( this);
        pd.setTitle("Please Wait ...");
        pd.setCanceledOnTouchOutside(false);


//        if(Common.IS_LOGIN=="IsLogged"){
//            Intent intent = new Intent(getApplicationContext(), HomeFragment.class);
//            startActivity(intent);
//        }


        //Toast.makeText(this,firebaseAuth.getCurrentUser().getPhoneNumber(),Toast.LENGTH_SHORT).show();
        Log.e(TAG, "GOY HERE 3");
        if(firebaseAuth.getCurrentUser() != null){
            Log.e(TAG, "GOY HERE 4: "+firebaseAuth.getCurrentUser().getPhoneNumber());
            if (firebaseAuth.getCurrentUser().getPhoneNumber()==null) {
                Log.e(TAG, "GOY HERE 5");
                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        /* This will unfold in two situations:
                            1-the phone num will instantly be verified.
                            2-google will auto detect the OTP code and will
                            fill it auto.
                         */
                        signInWithPhoneAuthCredntial(phoneAuthCredential);

                        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(HomeActivity.this);
                        Log.e(TAG, "GOY HERE 6");
                        if (signInAccount != null) {
                            Log.e(TAG, "GOY HERE 7");
                            showUpdateDialog(phoneEt.getText().toString(), signInAccount.getDisplayName(), signInAccount.getEmail());
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                    /*
                        this will unfold when invalid request happen
                        for instance if the phone num format isnt valid
                     */
                        pd.dismiss();
                        Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        /*
                        The Sms verification has been sent to the provided phone number and we
                        now need to ask the user to enter the code and then construct a credential
                        by combining the code with a verification ID.
                         */
                        Log.d(TAG, "onCodeSent " + verificationId);

                        mVerificationId = verificationId;
                        forceResendingToken = token;
                        pd.dismiss();

                        Toast.makeText(HomeActivity.this, "Verification Code Sent", Toast.LENGTH_SHORT).show();

                        //numTextView.setText("Please Type The Verification Code We Sent \nto " + findViewById(R.id.phoneNum).toString());
                    }
                };
            }else {
                phoneVerify.dismiss();
            }
        } else {
            LogOut();
        }


        //Init
        userRef = FirebaseFirestore.getInstance().collection("Client");
        dialog = new SpotsDialog.Builder().setContext(HomeActivity.this).setCancelable(false).build();


        //Check intent if its login = true , enable full access
        //if is login = false let user around shopping to view
        if(getIntent() !=null)
        {

            Log.e(TAG,"check was successful");

            boolean isLogin = getIntent().getBooleanExtra(Common.IS_LOGIN,false);
            if(isLogin)
            {
                dialog.show();
                if(Common.currentUser !=null){
                    DocumentReference currentUser = userRef.document(Common.currentUser.getPhoneNum());
                    currentUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot userSnapShot = task.getResult();
                                if(!userSnapShot.exists()){
                                    showUpdateDialog(Common.currentUser.getPhoneNum(),Common.currentUser.getFullName(),Common.currentUser.getUserEmail());
                                }
                                if (dialog.isShowing())
                                {
                                    dialog.dismiss();
                                }

                            }
                        }
                    });
                }
                else{
                    Toast.makeText(HomeActivity.this,"Error Please Logg In",Toast.LENGTH_SHORT).show();
                }
                dialog.show();

                if(Common.IS_LOGIN.equals("IsLogged")){

                    dialog.dismiss();
                }
            }

        }

        //View
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment = null;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_home){
                    fragment = new HomeFragment();
                }
                else if(menuItem.getItemId() == R.id.action_shopping){
                    fragment = new ShoppingFragment();
                }



                return loadFragment(fragment);
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_home);

    }

    private void startPhoneNumberVerification(String phone){
        pd.setMessage("Verifying Phone Number");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone) //Phone number must be with coutry code for example israel +972
                        .setTimeout(60L , TimeUnit.SECONDS) // the timeout and unit
                        .setActivity(this) // activity (for the callback binding )
                        .setCallbacks(mCallbacks) // OnVerificationStateChangedCallBacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredntial(PhoneAuthCredential credential) {
        pd.setMessage("Logging In");

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //Successfully signed in
                        pd.dismiss();
                        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
                        Toast.makeText(HomeActivity.this , "Logged In as"+ phone, Toast.LENGTH_SHORT).show();
                        Common.currentUser.setPhoneNum(phone);

                        //Saving in Realtime
                        databaseUser.setValue(Common.currentUser);

                        //SAVE IN FIRESTORE DATABASE
                        CollectionReference ref = db.collection("Client");

                        ref.document(phone).set(Common.currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(HomeActivity.this,"The Client Is Added.",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(HomeActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });

                        //start profile activity
                        phoneVerify.dismiss();
                        Common.setPhone(phone);
                        //dialog.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //faile Sign in
                        pd.dismiss();
                        Toast.makeText(HomeActivity.this , "Logged In as"+e.getMessage() , Toast.LENGTH_SHORT).show();

                    }
                });
    }


    private void resendVerification(String phone , PhoneAuthProvider.ForceResendingToken token){
        pd.setMessage("Resending Code");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone) //Phone number must be with coutry code for example israel +972
                        .setTimeout(60L , TimeUnit.SECONDS) // the timeout and unit
                        .setActivity(this) // activity (for the callback binding )
                        .setCallbacks(mCallbacks) // OnVerificationStateChangedCallBacks
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        pd.setMessage("Verifing Code");
        pd.show();

        PhoneAuthCredential credential = PhoneAuthProvider
                .getCredential(verificationId,code);
        signInWithPhoneAuthCredntial(credential);
    }


    public void ResendBtn(View view) {
        String phone = "+972"+findViewById(R.id.phoneEt).toString();
        resendVerification(phone , forceResendingToken);
    }

    public void SubmitBtn(View view) {
        String code = codeEt.getText().toString();
        if (TextUtils.isEmpty(code)){
            Toast.makeText(this,"Please Enter Verification Code ...",Toast.LENGTH_SHORT).show();
        }
        else{
            verifyPhoneNumberWithCode(mVerificationId,code);
        }
    }

    public void SendCode(View view) {
        //register user to fireBase
        if (phoneEt.getText().length()!=10){
            Toast.makeText(this, "Enter your phone number", Toast.LENGTH_SHORT).show();
        }else {
            startPhoneNumberVerification("+972"+phoneEt.getText().toString());

            phone.setVisibility(View.INVISIBLE);
            code.setVisibility(View.VISIBLE);
        }



        //progressBar.setVisibility(View.GONE);

    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            //...
        } else {
            LogOut();
        }
    }

    public void LogOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }


    private boolean loadFragment(Fragment fragment) {
        if(fragment != null ){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment)
                    .commit();
            return true;
        }
        return false;
    }

    // this dialog should be in the check if user exists
    private void showUpdateDialog(final String phoneNumber , String fullname , String email){

        //Init dialog
        bottomSheetDialog = new BottomSheetDialog(HomeActivity.this);
        bottomSheetDialog.setTitle("One More Step!");
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_update_information,null);

        Button btn_update = (Button)sheetView.findViewById(R.id.btn_approve);

        //
        final TextInputEditText edt_name = (TextInputEditText)sheetView.findViewById(R.id.edit_name);
        final TextInputEditText edt_email = (TextInputEditText)sheetView.findViewById(R.id.edit_email);
        edt_email.setText(email);
        edt_name.setText(fullname);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialog.isShowing())
                {
                    dialog.show();
                }

                final User user = new User(edt_name.getText().toString(),edt_email.getText().toString(),phoneNumber);
                userRef.document(phoneNumber).set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                bottomSheetDialog.dismiss();
                                if (dialog.isShowing())
                                {
                                    dialog.dismiss();
                                }

                                Common.currentUser = user;
                                bottomNavigationView.setSelectedItemId(R.id.action_home);
                                Toast.makeText(HomeActivity.this,"Thank You",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        bottomSheetDialog.dismiss();
                        if (dialog.isShowing())
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText(HomeActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }
}
