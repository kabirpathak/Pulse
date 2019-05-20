package firstapp.com.pulse;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.MimeTypeFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.sql.Time;
import java.util.Date;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;
import static firstapp.com.pulse.EditProfileActivity.getEmailString;

public class ConsultFragment extends Fragment {

    TextView getMyCharges;
    static int a = 0;

    static Uri mImageUri;
    Button goButton;
    ImageButton goButton2;
    Button uploadButton;
    Receipt receipt;
    static int Charges[][];
    static ProgressBar mProgressBar;
    //Date currentDate = Calendar.getInstance().getTime();

    Spinner spinner;
    Spinner spinner2;
    ImageButton fileButton;
    TextView chargesTextView, timeTextView;
    Button timeButton;
    static String email_string;
    private StorageReference mStorageRef;
    private DatabaseReference dataRef;
    static TextView countTextView;
    static int count = 0;

    DateFormat df = new SimpleDateFormat(" d MMM yyyy ");
    String date = df.format(Calendar.getInstance().getTime());

    public static final int FILE_READ_REQUEST_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.consult_fragment, container, false);
        ImageButton imgButton = (ImageButton) v.findViewById(R.id.fileButton);
        getMyCharges = (TextView) v.findViewById(R.id.getMyChargesTextView);
        timeTextView = (TextView) v.findViewById(R.id.getTimeTextView);
        timeButton = (Button) v.findViewById(R.id.getTimeButton);
        uploadButton = (Button)v.findViewById(R.id.uploadFileButton);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBarFileUpload);
        countTextView = (TextView)v.findViewById(R.id.countTextView);

        goButton2 = (ImageButton) v.findViewById(R.id.goButton);
        Toast.makeText(getContext(), " date : " + date, Toast.LENGTH_SHORT).show();

        goButton = (Button) v.findViewById(R.id.goButton2);

        mProgressBar = new ProgressBar(getContext());
        mProgressBar.setVisibility(View.GONE);
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        email_string = getEmailString(email);

        //trying to create the database..

        DatabaseReference dateRootRef = FirebaseDatabase.getInstance().getReference("AllDates");
        DatabaseReference dateRef = dateRootRef.child(date);
        dateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long doesExist = dataSnapshot.getChildrenCount();
                if(doesExist == 0){
                    createDatabaseForToday(date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });









        mStorageRef = FirebaseStorage.getInstance().getReference("UserDocuments");
        dataRef = FirebaseDatabase.getInstance().getReference("Profile");
        /*
        uploadButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });*/
        uploadButton.setVisibility(View.GONE);

        goButton2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
               if(!timeTextView.getText().toString().isEmpty()) {
                   makeAppointment(email_string);
               }else {
                   //timeTextView.setError("Please generate the available time first!!");
                   Toast.makeText(getContext(), "Please select the available time first" , Toast.LENGTH_SHORT).show();
               }

            }
        });
        goButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(!timeTextView.getText().toString().isEmpty()){
                    makeAppointment(email_string);
                }else{
                    //timeTextView.setError("Please generate the available time first!!");
                    Toast.makeText(getContext(), "Please generate the available time first!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        imgButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Working", Toast.LENGTH_SHORT).show();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {

                if (count == 0) {
                    final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("AllDates").child(date);
                    databaseRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (count == 0) {


                                    if (snapshot.getValue().toString().equals("null")) {
                                        String currentTime = snapshot.getKey();
                                        databaseRef.child(currentTime).setValue(1);
                                        timeTextView.setText(currentTime);
                                        count++;
                                    }
                                }
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                    else{

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        String message = "Only one appointment can be made by one patient. Your appointment is already set.";
                        String title = "Alert!";
                        builder.setMessage(message);
                        builder.setTitle(title);
                        builder.show();

                    }
                }


        });

         Charges  = new int[6][6];
        for(int i = 0;i < 6;i++){
            for(int j = 0;j < 6;j++){
                Charges[i][j] = (int)(Math.random()*4 + 1)*100;
            }
        }


        spinner = (Spinner) v.findViewById(R.id.spinner1);
        chargesTextView = (TextView) v.findViewById(R.id.chargesTextView);
        fileButton = (ImageButton) v.findViewById(R.id.fileButton);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.planets_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner2 = (Spinner) v.findViewById(R.id.spinner2);

        getMyCharges.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                getCharges();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.good_array, android.R.layout.simple_spinner_item);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    spinner2.setAdapter(adapter2);
                } else {
                    ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.bad_array, android.R.layout.simple_spinner_item);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    spinner2.setAdapter(adapter2);
                }

                getCharges();
            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }

/*
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                */

        });



        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, FILE_READ_REQUEST_CODE);
                } else {



                    final PackageManager packageManager = getContext().getPackageManager();
                    final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/*");
                    //ComponentName testedActivity = intent.resolveActivity(getContext().getPackageManager());
                    //if (testedActivity != null) {
                        startActivityForResult(intent,FILE_READ_REQUEST_CODE);
                    //} else {
                      //  Toast.makeText(getContext(),"No file explorer available.Please install a file explorer!",Toast.LENGTH_LONG).show();
                        //Intent i = new Intent(getContext(), MainActivity.class);
                        //startActivity(i);
                    //}

                }
            }
        });





        return v;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

        //if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                startActivityForResult(fileIntent, FILE_READ_REQUEST_CODE);


        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, FILE_READ_REQUEST_CODE);
        }



    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        mImageUri = data.getData();

        if (mImageUri != null) {
        Toast.makeText(getContext(), "Uploading file ", Toast.LENGTH_SHORT).show();

        uploadFile();
    }else{
            Toast.makeText(getContext(), "Please select a file!", Toast.LENGTH_SHORT).show();
        }

    }



    public void makeAppointment(String emailstring) {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Profile").child(emailstring).child("Receipts");
        DatabaseReference dataAll = FirebaseDatabase.getInstance().getReference("All Receipts").child(emailstring);
        final ProgressDialog mDialog = new ProgressDialog(getContext());
        mDialog.setMessage("Making appointment.....");
        mDialog.show();
        String department = spinner.getSelectedItem().toString();
        String doctor = spinner2.getSelectedItem().toString();
        String charges = chargesTextView.getText().toString();
        String dateTime = timeTextView.getText().toString();
        receipt = new Receipt(department, doctor, charges, dateTime);


        //for all the receipts
        dataAll.child(dateTime).setValue(receipt).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Toast.makeText(getContext(), "Appointment set", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }
        });

        //for the user receipt
        dataRef.child(dateTime).setValue(receipt).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Appointment set", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }
        });

    }
        public void getCharges(){



            int i = spinner.getSelectedItemPosition() + 1;
            int j = spinner2.getSelectedItemPosition() + 1;

            chargesTextView.setText(Charges[i][j] + " RUPEES");

        }
        private String getFileExtension(Uri uri){
            ContentResolver cr = getActivity().getApplicationContext().getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cr.getType(uri));
        }

        private void uploadFile(){

            if(mImageUri != null){
                final ProgressDialog mDialogFileUpload = new ProgressDialog(getContext());
                mDialogFileUpload.setMessage("Uploading file.....");
                mDialogFileUpload.show();
                mProgressBar.setVisibility(View.VISIBLE);
                final String path = System.currentTimeMillis() + "." + getFileExtension(mImageUri);
                final StorageReference fileReference = mStorageRef.child(path);
                fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mDialogFileUpload.dismiss();
                        final String[] myString = new String[1];
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setProgress(0);
                            }
                        }, 500);

                        StorageReference childRef = mStorageRef.child(path);
                        childRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                count++;
                                countTextView.setVisibility(View.VISIBLE);
                                countTextView.setText("  " + count + " attachments.");

                                Toast.makeText(getContext(), "Got the url : " + uri.toString(), Toast.LENGTH_SHORT).show();
                                myString[0] = uri.toString();
                                Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                                Upload upload = new Upload(System.currentTimeMillis() + "title", myString[0]);
                                //Toast.makeText(getContext(), "Uri stored is : " + myString[0], Toast.LENGTH_LONG).show();
                                String store = dataRef.push().getKey();
                                dataRef.child(email_string).child("Receipts").child("Files").child(store).setValue(upload);
                                mProgressBar.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                                mDialogFileUpload.dismiss();
                            }
                        });



                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()/ taskSnapshot.getTotalByteCount());
                        mProgressBar.setProgress((int) progress);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
            }
        }

        public void createDatabaseForToday(String date){


        Toast.makeText(getContext(), "Making the database", Toast.LENGTH_SHORT).show();
        DatabaseReference finalDateRef = FirebaseDatabase.getInstance().getReference("AllDates");
        DatabaseReference myRef = finalDateRef.child(date);

        myRef.child("9:00").setValue("null");
        myRef.child("9:15").setValue("null");
        myRef.child("9:30").setValue("null");
        myRef.child("9:45").setValue("null");

            myRef.child("10:00").setValue("null");
            myRef.child("10:15").setValue("null");
            myRef.child("10:30").setValue("null");
            myRef.child("10:45").setValue("null");

            myRef.child("11:00").setValue("null");
            myRef.child("11:15").setValue("null");
            myRef.child("11:30").setValue("null");
            myRef.child("11:45").setValue("null");

            myRef.child("12:00").setValue("null");
            myRef.child("12:15").setValue("null");
            myRef.child("12:30").setValue("null");
            myRef.child("12:45").setValue("null");

            myRef.child("01:00").setValue("null");
            myRef.child("01:15").setValue("null");
            myRef.child("01:30").setValue("null");
            myRef.child("01:45").setValue("null");

            myRef.child("02:00").setValue("null");
            myRef.child("02:15").setValue("null");
            myRef.child("02:30").setValue("null");
            myRef.child("02:45").setValue("null");

            myRef.child("03:00").setValue("null");
            myRef.child("03:15").setValue("null");
            myRef.child("03:30").setValue("null");
            myRef.child("03:45").setValue("null");

            myRef.child("04:00").setValue("null");
            myRef.child("04:15").setValue("null");
            myRef.child("04:30").setValue("null");
            myRef.child("04:45").setValue("null");

            myRef.child("04:00").setValue("null");
            myRef.child("04:15").setValue("null");
            myRef.child("04:30").setValue("null");
            myRef.child("04:45").setValue("null");





        }

}