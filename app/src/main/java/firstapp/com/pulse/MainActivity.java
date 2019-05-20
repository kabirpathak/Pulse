package firstapp.com.pulse;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static String address, age;
    private Button logoutButton;
    private TextView myView;
    private DrawerLayout drawerBar;
    private ActionBarDrawerToggle drawerToggle;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private NavigationView navigationView;
    private Toolbar mToolbar;

    TextView t1, t2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //myView = (TextView) findViewById(R.id.myView);
        drawerBar = (DrawerLayout) findViewById(R.id.drawerBar);
        //mToolbar = (Toolbar)  findViewById(R.id.nav_action);                                                                                    //very important part...
        navigationView = (NavigationView) findViewById(R.id.navigationView);                     //this shows how to link to a textView of navigation view.
        View headerView = navigationView.getHeaderView(0);                            //first link navigationView and then create a view that gets header of navigation view
        t1 = (TextView) headerView.findViewById(R.id.t1);                                   //and then link the textView of the navigation view to the view.
        t2 = (TextView) headerView.findViewById(R.id.t2);

        navigationView.setNavigationItemSelectedListener(this);

        drawerToggle = new ActionBarDrawerToggle(this, drawerBar, R.string.open, R.string.close);

        //logoutButton = (Button) findViewById(R.id.logout_button);


        drawerBar.addDrawerListener(drawerToggle);
        drawerToggle.syncState();


        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.profile_nav);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//show the home button











        auth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //myView.setText(auth.getCurrentUser().getEmail());
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                }
            }
        };


        t1.setText(auth.getCurrentUser().getDisplayName());
        t2.setText(auth.getCurrentUser().getEmail());

/*
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //auth.signOut();
                logoutSession();
            }
        });
*/

    }
    @Override
    protected void onResume() {
        super.onResume();
       //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if (drawerBar.isDrawerOpen(GravityCompat.START)) {
            drawerBar.closeDrawer(GravityCompat.START);
        }else

        exitApplication();
        //ogoutSession();
    }

    public void exitApplication(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void logoutSession(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want to logout?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which){
                finish();
                auth.signOut();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.profile_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                Toast.makeText(MainActivity.this, "Profile ", Toast.LENGTH_SHORT).show();
                navigationView.setCheckedItem(R.id.profile_nav);
                break;

            case R.id.receipts_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ReceiptsFragment()).commit();
                Toast.makeText(MainActivity.this, "Receipts", Toast.LENGTH_SHORT).show();
                navigationView.setCheckedItem(R.id.receipts_nav);
                break;

            case R.id.application_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ConsultFragment()).commit();
                Toast.makeText(MainActivity.this, "New Application", Toast.LENGTH_SHORT).show();
                navigationView.setCheckedItem(R.id.application_nav);
                break;

            case R.id.settings_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                navigationView.setCheckedItem(R.id.settings_nav);
                break;

            case R.id.logout_nav:
                logoutSession();
                Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerBar.closeDrawer(GravityCompat.START);
        return true;
    }
}