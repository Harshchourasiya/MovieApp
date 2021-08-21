package com.harshchourasiya.movies.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.harshchourasiya.movies.MainActivity;
import com.harshchourasiya.movies.Model.Users;
import com.harshchourasiya.movies.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.harshchourasiya.movies.Data.Data.EMAIL;
import static com.harshchourasiya.movies.Data.Data.EMAILKEY;
import static com.harshchourasiya.movies.Data.Data.NO_PASSWORD;
import static com.harshchourasiya.movies.Data.Data.RC_SIGN_IN;
import static com.harshchourasiya.movies.Data.Data.USERS;
import static com.harshchourasiya.movies.Data.Data.VERIFY;
import static com.harshchourasiya.movies.Data.Data.toCheckInternet;

public class Login extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // for back
        toBack();
        // to check internet
        toCheckInternet(this);
        // login with google
        toLoginWithGoogle();
        // to Register User
        toRegister();
        // to Login User
        toLogin();
    }

    /*

    Login and Saving data to firebase System code start from here

     */

    // to Login

    private void toLogin() {
        MaterialButton login = (MaterialButton) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((TextInputEditText) findViewById(R.id.Email)).getText().toString().trim();
                String password = ((TextInputEditText) findViewById(R.id.Password)).getText().toString();
                if (checkEmail(email)) {
                    if (password.length() > 5) {
                        if (login.getText().toString() == getString(R.string.login_text)) {
                            // To Verify Data
                            toVerifyData(email, password);
                        } else {
                            // Save Data to make user register
                            toSaveData(email, password);
                        }
                    } else {
                        // Set Invalid Password if password is wrong
                        ((TextInputLayout) findViewById(R.id.passwordLayout)).setError("Invalid Password");
                    }
                } else {
                    ((TextInputLayout) findViewById(R.id.emailLayout)).setError("Invalid Email");
                }
            }
        });
    }


    private void toSaveData(String email, String password) {
        // Saving User database in firebase database
        Users user = new Users(email, password, email + "_" + password);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS);
        myRef.orderByChild(EMAIL).equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Snackbar.make(findViewById(R.id.login), R.string.already_created, Snackbar.LENGTH_SHORT).show();
                } else {
                    myRef.push().setValue(user);
                    // make your login
                    getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putString(EMAILKEY, email).apply();
                    toOpenHome();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void toVerifyData(String email, String password) {
        // Verifying data weather user enter correct info or not to login
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS);
        myRef.orderByChild(VERIFY).equalTo(email + "_" + password).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putString(EMAILKEY, email).apply();
                    toOpenHome();
                } else {
                    ((TextInputLayout) findViewById(R.id.passwordLayout)).setError("Incorrect Password");
                    ((TextInputLayout) findViewById(R.id.emailLayout)).setError("Incorrect Email");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // Cheking email
    private boolean checkEmail(String email) {
        String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }


    // to Register

    private void toRegister() {
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTextView heading = (MaterialTextView) findViewById(R.id.login_text);
                heading.setText(R.string.register);
                MaterialButton loginButton = (MaterialButton) findViewById(R.id.login);
                loginButton.setText(R.string.register);
                MaterialTextView registerButton = (MaterialTextView) findViewById(R.id.register);
                registerButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    /*

    END HERE

     */


    /*

    To login with google Code start from here

     */


    // To Login with google
    private void toLoginWithGoogle() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.loginWithGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    // Getting Results from Google Sign in activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // Google Sign in Task handler
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            toSaveDataFromGoogle(account.getEmail(), NO_PASSWORD);
        } catch (ApiException e) {
            Snackbar.make(findViewById(R.id.login), R.string.retry, Snackbar.LENGTH_SHORT).show();
        }
    }


    // to Save data from google signin button

    private void toSaveDataFromGoogle(String email, String password) {
        // saving data from google Signin button
        Users user = new Users(email, password, email + "_" + password);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS);
        myRef.push().setValue(user);
        getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putString(EMAILKEY, email).apply();
        toOpenHome();
    }


    /*

    END HERE

     */


    // For back
    private void toBack() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toOpenHome();
            }
        });
    }



    // finish the Login


    // open MainActivity

    private void toOpenHome() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

}