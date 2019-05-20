package firstapp.com.pulse;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.system.ErrnoException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;

import static android.support.constraint.Constraints.TAG;
import static firstapp.com.pulse.EditProfileActivity.getEmailString;

public class CameraProfile extends Activity {

    static Bitmap photo;
    static String final_username;
    static Uri selectedImageUri;
    static Uri uriSelectedCamera;
    static DatabaseReference dataRef;

    //StorageReference mStorageReference;

            StorageReference mStorage;
        private static ProgressDialog mProgress;
        private static ProgressDialog mProgress2;
        private ImageView imageView;
        private ImageButton uploadUriButton;
        private Button uploadDevice;
        private ImageButton uploadBitmapButton;
        private static final int MY_CAMERA_PERMISSION_CODE = 100;
        private static final int PICKFILE_REQUEST_CODE = 12;
        private static final int REQUEST_CODE = 231;
        static String username_string_camera;

        static final String TAG = CameraProfile.class.getSimpleName();


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_camera_profile);


            dataRef = FirebaseDatabase.getInstance().getReference("Profile");
             FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



            final String user_email_string = getEmailString(user.getEmail());
            Log.d(TAG, "here is the user_email_string: " + user_email_string);


            dataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DataSnapshot childSnapshot = dataSnapshot.child(user_email_string);
                     username_string_camera = childSnapshot.child("username").getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




            mStorage = FirebaseStorage.getInstance().getReference();
            mProgress = new ProgressDialog(this);
            mProgress2 = new ProgressDialog(this);

            this.imageView = (ImageView) this.findViewById(R.id.imageView1);
            Button photoButton = (Button) this.findViewById(R.id.button1);
            uploadUriButton = (ImageButton) findViewById(R.id.uploadButton);
            uploadBitmapButton = (ImageButton) findViewById(R.id.uploadBitmapButton);
            uploadDevice = (Button) findViewById(R.id.uploadFromDevice);
            uploadUriButton.setVisibility(View.GONE);
            uploadBitmapButton.setVisibility(View.GONE);

            //mStorageReference = FirebaseStorage.getInstance().getReference();

            photoButton.setOnClickListener(new View.OnClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    uploadUriButton.setVisibility(View.GONE);
                    uploadBitmapButton.setVisibility(View.VISIBLE);
                    if (checkSelfPermission(Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_CAMERA_PERMISSION_CODE);
                    } else {

                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, MY_CAMERA_PERMISSION_CODE);

                    }
                }
            });

            uploadDevice.setOnClickListener(new View.OnClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    uploadUriButton.setVisibility(View.VISIBLE);
                    uploadBitmapButton.setVisibility(View.GONE);
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICKFILE_REQUEST_CODE);
                    } else {


                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, PICKFILE_REQUEST_CODE);


                    }
                }
            });


            uploadUriButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(CameraProfile.this, "Uploading your file from device to database", Toast.LENGTH_LONG).show();
                    if (selectedImageUri != null) {
                        mProgress.setMessage("Uploading image.....");
                        mProgress.show();

                        final String path = selectedImageUri.getLastPathSegment();
                        StorageReference filepath = mStorage.child("ProfilePictures").child(username_string_camera).child(path);
                        filepath.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(CameraProfile.this, "Uploading finished", Toast.LENGTH_SHORT).show();
                                mProgress.dismiss();

                                StorageReference childRef = mStorage.child("ProfilePictures").child(username_string_camera).child(path);
                                childRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Toast.makeText(CameraProfile.this, "Got the uri : " + uri.toString(), Toast.LENGTH_SHORT).show();
                                        dataRef.child(user_email_string).child("profileImage").setValue(uri.toString());

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CameraProfile.this, "Failed to get uri : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });

                    }
                }
            });

            final String title = user.getDisplayName();


            uploadBitmapButton.setOnClickListener(new View.OnClickListener(){
                String imageDownloadUri;
                DatabaseReference datRef = FirebaseDatabase.getInstance().getReference("ProfilePicture").child("profileImage");
                @Override
                public void onClick(View v) {


                    imageView.setDrawingCacheEnabled(true);
                    imageView.buildDrawingCache();
                    Bitmap bitmap = imageView.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    final String path = title + Math.random() + Math.random() + Math.random();
                    final UploadTask uploadTask = mStorage.child("ProfilePictures").child(username_string_camera).child(path).putBytes(data);


                    mProgress2.setMessage("Uploading your image....");
                    mProgress2.show();
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(CameraProfile.this, "error : " + exception.getMessage() , Toast.LENGTH_SHORT).show();
                            mProgress2.dismiss();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Toast.makeText(CameraProfile.this, "Saved the Image", Toast.LENGTH_SHORT).show();
                            mProgress2.dismiss();



                            StorageReference childRef = mStorage.child("ProfilePictures").child(username_string_camera).child(path);
                            childRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(CameraProfile.this, "Got the uri : " + uri.toString(), Toast.LENGTH_SHORT).show();
                                    dataRef.child(user_email_string).child("profileImage").setValue(uri.toString());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CameraProfile.this, "Failed : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            });

                           /*
                            final Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            mProgress.setMessage("Updating your profile picture");
                            mProgress.show();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String string_ling = result.toString();

                                    Log.d("MT", "here : " + string_ling);
                                    Toast.makeText(CameraProfile.this, "image added to database " + result.toString(), Toast.LENGTH_SHORT).show();
                                    mProgress.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CameraProfile.this, "failed : " +  e.getMessage(), Toast.LENGTH_LONG).show();
                                    mProgress.dismiss();
                                }
                            });*/

                        }
                    });

                    ///down here is the code that got me all worked up..
                    //I wasted 2 precious days of my life... not wasted i guess, invested rather..
                    //but now i am feeling awesome.. fuck yeah

                    //                            ---------------------------the wrong code is below------------------------------

                    /*
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return ur_firebase_reference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                System.out.println("Upload " + downloadUri);
                                Toast.makeText(CameraProfile.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                                if (downloadUri != null) {

                                    String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                                    System.out.println("Upload " + photoStringLink);

                                }

                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });*/
                }});


        }








                    /*
                    Toast.makeText(CameraProfile.this, "Uploading your file from camera to database", Toast.LENGTH_SHORT).show();
                    if(uriSelectedCamera != null){
                        mProgress2.setMessage("Uploading image.....");
                        mProgress2.show();
                        StorageReference filePath = mStorage.child("ProfilePictures").child(uriSelectedCamera.getLastPathSegment());
                        filePath.putFile(uriSelectedCamera).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //taskSnapshot.getError().getMessage();
                                Toast.makeText(CameraProfile.this, "Image uploaded.", Toast.LENGTH_SHORT).show();
                                mProgress2.dismiss();
                            }
                        });
                                .addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                mProgress2.dismiss();
                                Toast.makeText(CameraProfile.this, "Failed to upload file", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            });*/




        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

            //if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (requestCode == MY_CAMERA_PERMISSION_CODE) {

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, MY_CAMERA_PERMISSION_CODE);

                } else if (requestCode == PICKFILE_REQUEST_CODE) {

                    //desi jugad
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent uploadIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    uploadIntent.setType("image/*");
                    startActivityForResult(uploadIntent, PICKFILE_REQUEST_CODE);
                }
            }

                else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();

                }

        }

            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                if (requestCode == MY_CAMERA_PERMISSION_CODE && resultCode == Activity.RESULT_OK) {

                    photo = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(photo);





                    Log.d(TAG, "onActivityResult: done capturing photo");
                    //mStorageReference.child("photos").putFile();
                }

                else if(requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
                     selectedImageUri = data.getData();
                    imageView.setImageURI(selectedImageUri);
                    Log.d(TAG, "onActivityResult: done taking photo from device");
                }
            }










    }








