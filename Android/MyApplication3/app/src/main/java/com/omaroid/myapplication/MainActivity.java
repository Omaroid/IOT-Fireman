package com.omaroid.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button reg,signin;
    private EditText email, password;
/*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        email= (EditText) findViewById(R.id.email);
        password= (EditText) findViewById(R.id.password);

        signin= (Button) findViewById(R.id.signup);
        reg= (Button) findViewById(R.id.registernewuser);

    }

    public void signup(View view){
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("fef", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("dfez", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
    private void updateUI(FirebaseUser user){
        Intent myIntent = new Intent(this, HomeActivity.class);
        startActivity(myIntent);
    }

    public void create(View view) {
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SuccessCreationUser", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Authentification réussie.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FailureCreationUser", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentification echouée.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
