package com.bustracking.myschool.bustracker.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.bustracking.myschool.bustracker.Models.User;
import com.bustracking.myschool.bustracker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserRegistrationActivity extends AppCompatActivity {

    @BindView(R.id.editTextUserName)
    EditText editTextUserName;
    @BindView(R.id.editTextUserEmail)
    EditText editTextUserEmail;
    @BindView(R.id.editTextUserPassword)
    EditText editTextUserPassword;
    @BindView(R.id.userToolbar)
    Toolbar toolbar;

    FirebaseAuth auth;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        ButterKnife.bind(this);

        toolbar.setTitle("Student Register");
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().goOnline();
        dialog = new ProgressDialog(this);
    }


    public void registerUser(View v) {
        dialog.setTitle("Creating account");
        dialog.setMessage("Please wait");
        dialog.show();
        final String name = editTextUserName.getText().toString();
        final String email = editTextUserEmail.getText().toString();
        final String password = editTextUserPassword.getText().toString();

        if (name.equals("") && email.equals("") && password.equals("")) {
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Please enter correct details", Toast.LENGTH_SHORT).show();
        } else {
            doStuffUser();

        }
    }


    public void doStuffUser() {
        auth.createUserWithEmailAndPassword(editTextUserEmail.getText().toString(),
                editTextUserPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            User userobj = new User(editTextUserName.getText().toString(),
                                    editTextUserEmail.getText().toString(),
                                    editTextUserPassword.getText().toString());

                            FirebaseUser user = auth.getCurrentUser();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                                    getReference().child("Users").child(user.getUid());


                            databaseReference.setValue(userobj)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dialog.dismiss();
                                                Toast.makeText(UserRegistrationActivity.this,
                                                        "Account created successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                                Intent myIntent = new Intent(UserRegistrationActivity.this, NavigationActivity.class);
                                                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(myIntent);

                                            } else {
                                                Toast.makeText(getApplicationContext(), "Could not create account", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    });


                        }
                        else
                        {
                            dialog.dismiss();
                            Toast.makeText(UserRegistrationActivity.this,"Could not register student.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}
