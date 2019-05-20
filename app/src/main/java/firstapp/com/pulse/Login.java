package firstapp.com.pulse;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
    private TextView finalTextView;
    private static String demo;
    public static  String username_global;
    private EditText username, password;
    private Button loginBtn, passwordResetButton, registerBtn;
    private ProgressBar progressBar;
    ArrayList<String> arrayList = new ArrayList<String>();
    FirebaseAuth auth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //firebase authentication instance..
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            //startActivity(new Intent(Login.this, MainActivity.class));
            //finish();


            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();

        }


        setContentView(R.layout.activity_login);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //<-- not required now.. but agar sir ko toolbar sirf fixed activities me chahiye hoga then i will need this..
        //setSupportActionBar(toolbar);
        finalTextView = (TextView) findViewById(R.id.finalTextView);

        username = (EditText) findViewById(R.id.loginUsername);
        password = (EditText) findViewById(R.id.loginPassword);


        loginBtn = (Button) findViewById(R.id.login_btn);
        passwordResetButton = (Button) findViewById(R.id.resetPassword);
        registerBtn = (Button) findViewById(R.id.register_btn);

        progressBar = (ProgressBar) findViewById(R.id.progressBarLogin);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
                //finish();       <-- this has been commented out because mai chahta hu ki registration activity se back dabane par ye waali activity khule.
                //  if i and finish() method, then back dabane par activity khatam ho jaegi.
            }
        });

        passwordResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ResetPassword.class);
                startActivity(intent);

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final String username_string1 = username.getText().toString().trim();
                final String password_string1 = password.getText().toString().trim();
                username_global = username_string1;
                username_global.equals(username_string1);

                isRegistered(username_string1, username_string1, password_string1);

            }


        });
    }

    protected void isRegistered(final String s, final String username_string1, final String password_string1){
        Boolean returner = false;
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users");
        //FirebaseUser user = auth.getCurrentUser();
        //String loginEmail = dataRef.orderByChild().;
        ValueEventListener listener = new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(username_string1).exists()){
                    startLogin(username_string1, password_string1);
                }else{
                    Toast.makeText(Login.this, "Username not found", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                /*for(DataSnapshot childrenSnapshot : dataSnapshot.getChildren()){
                    //User aUser = childrenSnapshot.getValue(User.class);
                    if(childrenSnapshot.child("username").getValue().toString().equals(s)){


                        startLogin(username_string1, password_string1);

                    }
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Login.this, "Error .", Toast.LENGTH_SHORT).show();
            }
        };

        dataRef.addValueEventListener(listener);//.orderByChild("username").equalTo(username_string)


        progressBar.setVisibility(View.VISIBLE);

    }
    protected void startLogin(final String username_string, final String password_string){
            if(TextUtils.isEmpty(username_string)){
                Toast.makeText(getApplicationContext(), "Enter the username!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(password_string)){
                Toast.makeText(getApplicationContext(), "Enter the password", Toast.LENGTH_SHORT).show();
                return;
            }

            else if(password_string.length() < 7){
                Toast.makeText(getApplicationContext(), "Invalid password! Too short.", Toast.LENGTH_SHORT).show();
                return;
            }


/*

                mDatabase = FirebaseDatabase.getInstance().getReference("Users");
                mDatabase.addValueEventListener(new ValueEventListener(){

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String imp_string = dataSnapshot.getValue(String.class);
                        Log.d("myTag", "Its here " + imp_string);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
*/

            DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users");
            //FirebaseUser user = auth.getCurrentUser();
            //String loginEmail = dataRef.orderByChild().;
            ValueEventListener listener = new ValueEventListener(){

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //DataSnapshot childSnapshot = dataSnapshot.child();
                    //demo  = dataSnapshot.getValue(User.class).email;

                    for(DataSnapshot childrenSnapshot : dataSnapshot.getChildren()){
                        //User aUser = childrenSnapshot.getValue(User.class);
                        if(childrenSnapshot.child("username").getValue().toString().equals(username_string)){
                            String temp_string = childrenSnapshot.child("email").getValue().toString();
                            temp_string.equals(childrenSnapshot.child("email").getValue().toString());

                            //demo.equals(temp_string);
                            demo = temp_string;
                            signIn(demo, password_string);
                            //demo.equals(childrenSnapshot.child("email").getValue().toString());
                            if(!TextUtils.isEmpty(temp_string)){
                                Toast.makeText(Login.this,demo, Toast.LENGTH_SHORT).show();


                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Login.this, "Error .", Toast.LENGTH_SHORT).show();
                }
            };

            dataRef.addValueEventListener(listener);//.orderByChild("username").equalTo(username_string)


            progressBar.setVisibility(View.VISIBLE);


            //authenticate user..

        }


    protected void signIn(final String ax, final String bx){
        auth.signInWithEmailAndPassword(ax, bx).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>(){

            String a = ax;
            String b = bx;
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressBar.setVisibility(View.GONE);
                if(!task.isSuccessful()){
                    //print the error message
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setMessage("Invalid username or password!" + demo);
                    builder.setTitle("Authentication failed.");
                    builder.show();

                }

                else{

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
    public void exitApp(View v){
        exitApplication();


    }

    @Override
    public void onBackPressed(){
        exitApplication();

    }
    public static String returnUsername(){
        return username_global;
    }

    public void exitApplication(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        String message = "Are you sure you want to exit the application?";
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.cancel();
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                finish();
                //System.exit(0);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
