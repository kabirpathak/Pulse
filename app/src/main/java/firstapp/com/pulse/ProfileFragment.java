package firstapp.com.pulse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static firstapp.com.pulse.R.id.fragment_container;
import static firstapp.com.pulse.R.id.loginUsername;
import static firstapp.com.pulse.R.id.phoneTextView;

public class ProfileFragment extends Fragment {
    static String phone;
    static String static_username;
    static String dob;
    static String age;
    static String email;
    static String email_string;

    TextView nameTextView;

    static TextView ageTextView;
    static TextView addressTextView;
    static TextView genderTextView;
    static TextView dateOfBirthTextView;

    TextView emailTextView;
    static TextView phoneTextView;
    TextView usernameTextView;
    ImageButton myButton;
    private FirebaseAuth auth;
    private DatabaseReference firebase;
    private DatabaseReference dataRef;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
     View v = inflater.inflate(R.layout.profile_fragment, container, false);

     //check if profile is updated


     nameTextView = (TextView) v.findViewById(R.id.nameTextView);
     emailTextView = (TextView) v.findViewById(R.id.emailTextView);
     usernameTextView = (TextView)v.findViewById(R.id.phoneTextView);
     phoneTextView = (TextView)v.findViewById(R.id.phoneTextView);
     ageTextView = (TextView)v.findViewById(R.id.ageTextView);
     genderTextView = (TextView)v.findViewById((R.id.genderTextView));
     addressTextView = (TextView)v.findViewById(R.id.addressTextView);
     dateOfBirthTextView = (TextView)v.findViewById(R.id.dateOfBirthTextView);

     firebase = FirebaseDatabase.getInstance().getReference("Users");
     auth = FirebaseAuth.getInstance();
     FirebaseUser user = auth.getCurrentUser();

     String name = user.getDisplayName();
     nameTextView.setText(name);

     String email = user.getEmail();

     emailTextView.setText(email);
     email_string = getEmailString(email);

     Toast.makeText(getContext(), email_string, Toast.LENGTH_SHORT).show();
     dataRef = FirebaseDatabase.getInstance().getReference("Profile");

     dataRef.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             DataSnapshot childSnapshot = dataSnapshot.child(email_string);
             String username_string2 = childSnapshot.child("username").getValue().toString();
             if(childSnapshot.child("dateOfBirth").getValue().toString().equals("dd/mm/yyyy") && childSnapshot.child("age").getValue().toString().equals("age") && childSnapshot.child("address").getValue().toString().equals("address") && childSnapshot.child("gender").getValue().toString().equals("gender")){
                 updateProfile();

             }else if(childSnapshot.child("dateOfBirth").getValue().toString().isEmpty() || childSnapshot.child("age").getValue().toString().isEmpty() || childSnapshot.child("address").getValue().toString().equals("address") || childSnapshot.child("gender").getValue().toString().isEmpty()){
                 updateProfile();
             }else{

                 //continue;
                 static_username = username_string2;
                 setAllTexts(username_string2, email_string);

             }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
         }
     });




     User userLocal = new User();
     //sabse important part..  agar maine database aise access kar liya to matlab app ban jaegi pyaar se.. wrna aur padhna hoga!!
        String mySecondString = "";
        /*
        if(TextUtils.isEmpty(Login.username_global) && (TextUtils.isEmpty(Signup.usernameSignUp))){
            mySecondString.equals(emailTextView.getText().toString().trim());
        }
        else if(TextUtils.isEmpty(Login.username_global)){
            mySecondString = Signup.usernameSignUp;
            mySecondString.equals(Signup.usernameSignUp);
        }else if(TextUtils.isEmpty(Signup.usernameSignUp)){
            mySecondString = Login.username_global;
            mySecondString.equals(Login.username_global);
        }else mySecondString="nothing";

        String username_panga = firebase.child(mySecondString).child("username").toString();
*/

        //setAllTexts(Login.username_global);







        myButton = (ImageButton) v.findViewById(R.id.myButton);
        myButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Fragment mFragment = new EditProfileFragment();
                //getSupportFragmentManager().beginTransaction().replace(fragment_container, mFragment).commit();
                //Toast.makeText(getActivity(), "Working", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(i);

            }
        });
        return v;
    }

    public static void setAllTexts(final String myString, final String email_string){
        DatabaseReference dataRef  = FirebaseDatabase.getInstance().getReference("Users");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot childSnapshot = dataSnapshot.child(myString);

                String myUserPhone = childSnapshot.getValue(User.class).phone;
                phoneTextView.setText(myUserPhone);
                //phoneTextView.setText(email_string);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference dataRef2 = FirebaseDatabase.getInstance().getReference("Profile");
        dataRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot childSnapshot = dataSnapshot.child(email_string);

                //String myUserPhone = childSnapshot.getValue(CompleteUserProfile.class).phone;
                String myUserPhone = childSnapshot.child("phoneNumber").getValue().toString();
                //String age = childSnapshot.getValue(CompleteUserProfile.class).age;
                String age = childSnapshot.child("age").getValue().toString();
                //String address = childSnapshot.getValue(CompleteUserProfile.class).address;
                String address = childSnapshot.child("address").getValue().toString();

                //String dateOfBirth = childSnapshot.getValue(CompleteUserProfile.class).dateOfBirth;
                String dateOfBirth = childSnapshot.child("dateOfBirth").getValue().toString();
                //String gender = childSnapshot.getValue(CompleteUserProfile.class).gender;
                String gender = childSnapshot.child("gender").getValue().toString();

                if(!gender.isEmpty()) {
                    genderTextView.setText(gender);
                }
                if(!dateOfBirth.isEmpty()) {
                    dateOfBirthTextView.setText(dateOfBirth);
                }
                 if(!myUserPhone.isEmpty()){
                    phoneTextView.setText(myUserPhone);
                 }
                 if(!age.isEmpty()){
                    ageTextView.setText(age);
                 }
                 if(!address.isEmpty()) {
                     addressTextView.setText(address);
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

    public void updateProfile(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String message = "Your profile is not updated. Would you like to update your profile?";
        String title = "ALERT!";
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //setAllTexts(static_username, email_string);
                dialogInterface.cancel();
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i){

                startActivity(new Intent(getContext(), EditProfileActivity.class));
                //System.exit(0);
            }
        });
        builder.show();
    }

}
