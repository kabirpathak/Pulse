package firstapp.com.pulse;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends AppCompatActivity {
    EditText oldPass, newPass, confirmPass;
    Button changepw;
    ProgressBar progressBar;
    static String email_string;
    FirebaseUser user;
    DatabaseReference dataRef;
    DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        user = FirebaseAuth.getInstance().getCurrentUser();
        dataRef = FirebaseDatabase.getInstance().getReference("Profile");
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        //to get current child
        String email = user.getEmail();
        email_string = getEmailString(email);






        oldPass = (EditText) findViewById(R.id.old1);
        oldPass.setVisibility(View.GONE);
        newPass = (EditText) findViewById(R.id.new1);
        confirmPass = (EditText) findViewById(R.id.new2);
        changepw = (Button)findViewById(R.id.changePasswordButton);
        progressBar = (ProgressBar) findViewById(R.id.progressbarchangepw);



        changepw.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {

                changeUserPassword();
            }
        });

    }

    protected void changeUserPassword(){
        progressBar.setVisibility(View.VISIBLE);
        String oldPassword = oldPass.getText().toString().trim();
        final String newPassword = newPass.getText().toString().trim();
        String confirmPassword = confirmPass.getText().toString().trim();
        //matchOldPassword(oldPassword);

        //check with old password


        if (user != null && !newPassword.equals("")) {



            if (newPassword.length() < 8) {
                newPass.setError("Password too short, enter minimum 6 characters");
                confirmPass.setError("Password too short, end minimum 6 characters");
                progressBar.setVisibility(View.GONE);
            } else if (newPassword.equals("")) {
                newPass.setError("Enter password");
                progressBar.setVisibility(View.GONE);
            }
            else if(!newPassword.equals(confirmPassword)){
                newPass.setError("Passwords do not match");
                confirmPass.setError("Passwords do not match");
                progressBar.setVisibility(View.GONE);
            }else {
                user.updatePassword(newPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ChangePassword.this, "Password is updated!", Toast.LENGTH_SHORT).show();

                                    dataRef.child(email_string).child("password").setValue(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ChangePassword.this, "password updated in database", Toast.LENGTH_LONG).show();
                                        }
                                    });


                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(ChangePassword.this, "Failed to update password!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }


                        });
            }
        }

    }


    public void matchOldPassword(final String oldPassword){
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot childSnapshot = dataSnapshot.child(email_string);
                if(!childSnapshot.child("password").getValue().toString().equals(oldPassword)){
                    oldPass.setError("Old password is not correct!");

                }else{
                    newPass.setVisibility(View.VISIBLE);
                    confirmPass.setVisibility(View.VISIBLE);
                    oldPass.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getEmailString(String s){
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
