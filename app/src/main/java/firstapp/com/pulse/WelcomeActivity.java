package firstapp.com.pulse;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity {

    static final int WAITING_TIME = 4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){

            @Override
            public void run() {
                Intent mainIntent = new Intent(WelcomeActivity.this, Login.class);
                startActivity(mainIntent);
                finish();

            }
        }, WAITING_TIME);

    }
}
