package firstapp.com.pulse;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {
    ImageButton saveProfileButton;


    static Spinner genderSpinner;
    static EditText  ageEdit, addressEdit;
    static TextView dateBirth;
    static EditText profileName;
    static TextView emailtv, phoneEdit;
    static ImageButton myButton;
    static String email, email_string;
    static ImageButton imageViewEditProfile;
    static FirebaseUser user;
    DatePickerDialog.OnDateSetListener mDateSetListener;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        saveProfileButton = (ImageButton)findViewById(R.id.dummyEditProfileButton);
        myButton = (ImageButton) findViewById(R.id.myButton);

        emailtv = (TextView) findViewById(R.id.emailTextViewEditProfile);


        genderSpinner = (Spinner) findViewById(R.id.gtv);
        ageEdit = (EditText) findViewById(R.id.atv);
        addressEdit = (EditText) findViewById(R.id.htv);
        dateBirth = (TextView) findViewById(R.id.dtv);
        profileName = (EditText) findViewById(R.id.editProfileName);
        phoneEdit = (EditText) findViewById(R.id.phoneTextViewEditProfile);

        FirebaseAuth auth = FirebaseAuth.getInstance();
         user = auth.getCurrentUser();

        String name = user.getDisplayName();
        profileName.setText(name);

        //gender select and store
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditProfileActivity.this,
                R.array.gender_list, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        genderSpinner.setAdapter(adapter);


        email = user.getEmail();
        emailtv.setText(email);

        email_string = getEmailString(email);

        setAllTexts(email_string);

        //open the DatePickerDialog when textView is clicked..
        dateBirth.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int date = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDialog = new DatePickerDialog(EditProfileActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog, mDateSetListener, year, month, date);

                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.rgb(0, 119, 189)));
                mDialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                String dateOfBirthString = dayOfMonth + "th " + months[month] + " " + year;
                dateBirth.setText(dateOfBirthString);
            }
        };


        saveProfileButton.setOnClickListener(new View.OnClickListener(){

            //get all textView texts from database..


        public void onClick(View v){

            //set all the values for the text fields...


            //display the message that profile has been changed..
            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
            String message = "Save changes made to the profile?";
            builder.setMessage(message);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setAllValue(email_string);
                    Toast.makeText(EditProfileActivity.this, "Profile has been updated", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
            //
            //MainActivity.address = " address changed";
            //MainActivity.age = "1";

        }
    });

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                String message = "Opens activity to upload photo!";
                String title = "Alert!";
                builder.setMessage(message);
                builder.setTitle(title);
                //builder.show();

                startActivity(new Intent(EditProfileActivity.this, CameraProfile.class));


            }
        });


    }

    public void setAllTexts(final String email_string1){
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Profile");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot childSnapshot = dataSnapshot.child(email_string1);

                if(childSnapshot.child("profileImage").exists() && !childSnapshot.child("profileImage").getValue().toString().isEmpty()){
                    String name = childSnapshot.child("profileImage").getValue().toString();
                    //myButton.setImageURI(Uri.parse(name));
                    Log.d("HERE", "Checking condition");
                    //if(!user.getPhotoUrl().toString().isEmpty()){
                        Log.d("HERE", "Upload uriphoto" + name);
                       // myButton.setImageURI(user.getPhotoUrl());

                    myButton.setImageURI(Uri.parse(name));

                    StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
                    //StorageReference imagereference = mStorageReference.getReferenceFromUrl

                    /*
                    if(name != null) {
                        Picasso.with(EditProfileActivity.this).load(Uri.parse(name))
                                .into(imageViewEditProfile);
                    }*/


                    //}
                }


                if((childSnapshot.child("name").getValue().toString()  != null)) {
                    String name = childSnapshot.child("name").getValue().toString();
                    profileName.setText(name);

                    //here i had an error
                    // the caste was getting added to the name more than once..
                    //so display name will be displayed using Firebase auth

                }
                if(childSnapshot.child("age").getValue().toString() != null && childSnapshot.child("age").exists()) {
                    String age = childSnapshot.child("age").getValue().toString();
                    ageEdit.setText(age);
                }
                if(childSnapshot.child("address").exists() ) {
                    String address = childSnapshot.child("address").getValue().toString();
                    addressEdit.setText(address);
                }
                if( childSnapshot.child("dateOfBirth").exists()) {
                    String birthDate = childSnapshot.child("dateOfBirth").getValue().toString();
                    dateBirth.setText(birthDate);
                }
                if( childSnapshot.child("gender").exists()){
                    String gString = childSnapshot.child("gender").getValue().toString();

                }

                if(childSnapshot.child("phoneNumber").exists()){
                    String phoneString = childSnapshot.child("phoneNumber").getValue().toString();
                    phoneEdit.setText(phoneString);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void setAllValue(final String email_string1){
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Profile");

                final DatabaseReference childRef = dataRef.child(email_string);
                    String name = profileName.getText().toString();
                    childRef.child("name").setValue(name);
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();

                    String age = ageEdit.getText().toString();
                    childRef.child("age").setValue(age);

                    String gender = genderSpinner.getSelectedItem().toString();
                    dataRef.child(email_string).child("gender").setValue(gender);

                    String dob = dateBirth.getText().toString();
                    childRef.child("dateOfBirth").setValue(dob);

                    String address = addressEdit.getText().toString();
                    childRef.child("address").setValue(address);

        }



    static String getEmailString(String s){
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
