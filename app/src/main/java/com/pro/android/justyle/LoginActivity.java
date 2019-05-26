package com.pro.android.justyle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditTextEmail;
    private EditText mEditTextPassword;

    private ProgressDialog mProgressDialog;
    private FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        mEditTextEmail = findViewById(R.id.editTextEmail);
        mEditTextPassword = findViewById(R.id.editTextPassword);
        TextView mTextViewSignUp = findViewById(R.id.textViewSignUp);
        Button mButtonSignIn = findViewById(R.id.Login);


        if (mFirebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(),FrontPageActivity.class));
        }
        mButtonSignIn.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param v is the position that the onClick requires on the UI for the method to be called
             *              *          onClick method calls a method when its corresponding UI item is pressed
             *              *          this onClick calls the method userLogin
             */
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
        mTextViewSignUp.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param v is the position that the onClick requires on the UI for the method to be called
             *              *          onClick method calls a method when its corresponding UI item is pressed
             *              *          this onClick starts the SignUpActivity
             */
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    /**
     * userLogin is the method that logs users in through Firebase Autentication, by calling
     * Firebase's method signInWIthEmailAndPassword
     * userLogin also warns the user to fill the EditText fields and also tells the user that
     * the login is in progress. once the login is successful the method takes the user to the
     * FrontPageActivity
     */
    private void userLogin(){
        String email = mEditTextEmail.getText().toString().trim();
        String password = mEditTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Enter Email, please", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Enter Password, please",Toast.LENGTH_SHORT).show();
            return;
        }
        //if validation is ok you will end up here
        mProgressDialog.setMessage("Logging in, Please Wait...");
        mProgressDialog.show();

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(),FrontPageActivity.class));
                        }
                        else{
                            Toast.makeText(LoginActivity.this,
                                    "wrong password or Email",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}