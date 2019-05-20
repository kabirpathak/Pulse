package firstapp.com.pulse;

import android.content.Intent;
import android.provider.ContactsContract;
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

public class DeleteUser extends AppCompatActivity {
    Button button1, button2;
    EditText passwordDelete;
    static String storedPassword;
    TextView textViewCheck;
    static String email_string;
    static String static_username;
    ProgressBar deleteUserProgress;
    static DatabaseReference dataRef;
    static DatabaseReference userRef;

    //user and profile still need to be removed...

    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        user = FirebaseAuth.getInstance().getCurrentUser();
        deleteUserProgress = (ProgressBar) findViewById(R.id.deleteUserProgress);
        dataRef = FirebaseDatabase.getInstance().getReference("Profile");
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        String email = user.getEmail();
        email_string = getEmailString(email);
        textViewCheck = (TextView) findViewById(R.id.textViewCheck);



        button1 = findViewById(R.id.yesButton);
        button2 = findViewById(R.id.deleteButton);
        passwordDelete = findViewById(R.id.enterDeletePassword);

        button1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.VISIBLE);
                passwordDelete.setVisibility(View.VISIBLE);
            }
        });

        button2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                deleteUserProgress.setVisibility(View.VISIBLE);
                deleteUser();
            }
        });
    }

    protected void deleteUser() {

         getStoredPassword();
         textViewCheck.setText(storedPassword);

        if (passwordDelete.getText().toString().equals(storedPassword)) {
            if (user != null) {
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //deleteFromDatabase();
                                    Toast.makeText(DeleteUser.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(DeleteUser.this, Signup.class));
                                    finish();
                                    deleteUserProgress.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(DeleteUser.this, "Failed to delete your account!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    deleteUserProgress.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        }else {
            passwordDelete.setError("Wrong password!");
            deleteUserProgress.setVisibility(View.GONE);
        }
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

    public static void getStoredPassword(){

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot childSnap = dataSnapshot.child(email_string);
                storedPassword = childSnap.getValue(CompleteUserProfile.class).password;
                static_username = childSnap.getValue(CompleteUserProfile.class).username;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
public void deleteFromDatabase(){
        dataRef.child(email_string).removeValue();
        userRef.child(static_username).removeValue();
        Toast.makeText(DeleteUser.this, "database se chala gya", Toast.LENGTH_LONG).show();
}
}
