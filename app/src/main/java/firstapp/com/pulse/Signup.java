package firstapp.com.pulse;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Signup extends AppCompatActivity {
    static String usernameSignUp;
    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private EditText name, caste, username, phoneNumber;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    FirebaseDatabase mDatabase;
    DatabaseReference childReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //firebase authentication
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        btnSignIn = (Button) findViewById(R.id.signin_button);
        btnSignUp = (Button) findViewById(R.id.signup_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        name = (EditText) findViewById(R.id.registrationName);
        caste = (EditText) findViewById(R.id.registrationCaste);
        username = (EditText) findViewById(R.id.userName);
        phoneNumber = (EditText) findViewById(R.id.mobile);

        btnResetPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(Signup.this, ResetPassword.class));
                finish();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                 final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                final String caste_string = caste.getText().toString().trim();
                final String name_string = name.getText().toString().trim() + " " + caste_string;
                final String phone_string = phoneNumber.getText().toString().trim();
                final String username_string = username.getText().toString().trim();
                //usernameSignUp.equals(username_string);
                usernameSignUp = username_string;
                if(TextUtils.isEmpty(name_string)){
                    Toast.makeText(getApplicationContext(), "Enter your name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(caste_string)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                    String message = "Enter the full name!";
                    builder.setMessage(message);
                    builder.show();
                    return;

                }

                if(TextUtils.isEmpty(phone_string)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                    String message = "Enter the phone number!";
                    builder.setMessage(message);
                    builder.show();
                    return;
                }

                if(TextUtils.isEmpty(username_string)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                    String message = "Set a username!";
                    builder.setMessage(message);
                    builder.show();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length() < 6){
                    Toast.makeText(getApplicationContext(), "Password is too small! Enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Toast.makeText(Signup.this,  " " + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                        //progressBar.setVisibility(View.GONE);

                            if(task.isSuccessful()) {

                                User user = new User(name_string,  email, phone_string, username_string, password);
                                CompleteUserProfile completeUserProfile = new CompleteUserProfile(user);
                                createUserProfile();
                                String emailString = getEmailString(email);
                                FirebaseDatabase.getInstance().getReference("Profile").child(emailString).setValue(completeUserProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.GONE);

                                        if(task.isSuccessful()){
                                            //continue;
                                        }else{
                                            Toast.makeText(Signup.this, "error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(username_string)
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>(){


                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.GONE);

                                        if (task.isSuccessful()) {
                                            Toast.makeText(Signup.this, "Successfully registered user.", Toast.LENGTH_SHORT).show();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                                            builder.setTitle("Alert.");
                                            //builder.setMessage("Successfully registered user. Please login to continue.");
                                            //builder.show();

                                        } else {
                                            Toast.makeText(Signup.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(Signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable(){
                                @Override
                                public void run(){
                                    //do something
                                    startActivity(new Intent(Signup.this, Login.class));
                                    finish();
                                }
                            }, 3000);





                    }
                });
            }
        });
    }

    private void createUserProfile(){
        String a = firstCapital(name.getText().toString());
        String b = firstCapital(caste.getText().toString());
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(a + " " + b).build();
                   //.setPhotoUri

            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d("TESTING", "User profile updated");

                    }
                }
            });
        }
    }

    @Override
    protected void onStart(){
        super.onStart();

        if(auth.getCurrentUser() != null){
            FirebaseUser user = auth.getCurrentUser();
            auth.signOut();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    public static String firstCapital(String s) {
        String a = "";

        char sarray[] = s.toCharArray();
        for(int i = 0;i < sarray.length;i++) {
            if(i == 0) {
                a += (char)((int)sarray[i]-32);
            }
            else a += sarray[i];
        }



        return a;
    }

    public static String getEmailString(String s){
        String a = "";
        char sarray[] = s.toCharArray();
        int i = 0;
        while(sarray[i] != '@'){
            a += sarray[i];
            i++;
        }

        return a;
    }


}
