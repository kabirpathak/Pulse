package firstapp.com.pulse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomerSupport extends AppCompatActivity {
    TextView textView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_support);
        textView1 = (TextView) findViewById(R.id.textView1);
    }

    public void message(View v){
        //Toast.makeText(CustomerSupport.this, "Message has been sent! We will get back to you shortly.", Toast.LENGTH_SHORT).show();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");


        String aEmailList[] = { "kabirpathak99@gmail.com" };
       // String aEmailCCList[] = { "user3@fakehost.com","user4@fakehost.com"};
        //String aEmailBCCList[] = { "user5@fakehost.com" };

        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
        //emailIntent.putExtra(android.content.Intent.EXTRA_CC, aEmailCCList);
        //emailIntent.putExtra(android.content.Intent.EXTRA_BCC, aEmailBCCList);

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Issue with pulse application.");

        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, textView1.getText().toString());

        startActivity(emailIntent);

    }
}
