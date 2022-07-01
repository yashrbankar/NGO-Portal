package com.example.helpinghands;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;



import android.content.ContentResolver;
import android.content.Intent;

import android.net.Uri;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class setup_profile extends AppCompatActivity {


    EditText fullName,phoneNo,emailID;
    Button update_info;
    ImageView imageView;
    ProgressBar progressBar;
    TextView skip;
    Uri imageUri;
    UploadTask uploadTask;
    StorageReference storageReference;
    FirebaseDatabase database= FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    private static final int PICK_IMAGE = 1;
    All_UserMmber member;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_profile);

        member= new All_UserMmber();
        imageView = findViewById(R.id.Pprofile_image);
        fullName= findViewById(R.id.Pfull_name);
        phoneNo= findViewById(R.id.Pphone_no);
        emailID= findViewById(R.id.PemailID);
        update_info= findViewById(R.id.Pupdate_detail_button);
        progressBar= findViewById(R.id.PprogressBar);
        skip=findViewById(R.id.skip);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        currentUserId= user.getUid();

        documentReference= db.collection("user").document(currentUserId);
        storageReference= FirebaseStorage.getInstance().getReference("Profile images");
        databaseReference = database.getReference("All Users");

        update_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(setup_profile.this, final_page.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if(requestCode == PICK_IMAGE || resultCode==RESULT_OK || data != null || data.getData() != null){
                imageUri= data.getData();

                Picasso.get().load(imageUri).into(imageView);

            }

        }catch (Exception e){
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }

    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver =getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    private void uploadData() {

        String name = fullName.getText().toString();
        String email = emailID.getText().toString();
        String phoneno = phoneNo.getText().toString();


        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(phoneno) && imageUri != null){

            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference= storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageUri));
            uploadTask = reference.putFile(imageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
               return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Uri> task) {

                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();

                        Map<String,String> profile = new HashMap<>();
                        profile.put("name",name);
                        profile.put("phoneNo",phoneno);
                        profile.put("email",email);
                        profile.put("uri",downloadUri.toString());
                        profile.put("privacy","Public");

                        member.setName(name);
                        member.setUid(currentUserId);
                        member.setUrl(downloadUri.toString());

                        databaseReference.child(currentUserId).setValue(member);

                        documentReference.set(profile)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(setup_profile.this, "Profile uploaded", Toast.LENGTH_SHORT).show();

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent= new Intent(setup_profile.this, final_page.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        },2000);
                                    }
                                });
                    }
                }
            });

        }else {
            Toast.makeText(this, "Please fill Personal info. and Upload image", Toast.LENGTH_SHORT).show();
        }

    }



}