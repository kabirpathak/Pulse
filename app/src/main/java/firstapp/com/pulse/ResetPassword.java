package firstapp.com.pulse;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private EditText inputEmail;
    private Button resetButton, backButton;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        inputEmail = (EditText) findViewById(R.id.email);
        resetButton = (Button) findViewById(R.id.btn_reset_password);
        backButton = (Button) findViewById(R.id.btn_back);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email = inputEmail.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplication(), "Enter your registered email address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener(){
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPassword.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ResetPassword.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }


                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
