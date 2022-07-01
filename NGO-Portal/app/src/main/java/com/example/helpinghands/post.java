package com.example.helpinghands;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

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

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class post extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    private Uri selectedUri;
    private static final int PICK_FILE=1;
    UploadTask uploadTask;
    EditText etdesc;
    EditText etPno;
    Button btnChooseFile,btnUploadFile;
    VideoView videoView;
    String url,name;
    StorageReference storageReference;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference db1,db2,db3;

    MediaController mediaController;
    String type;
    Postmember postmember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postmember = new Postmember();
         mediaController = new MediaController(this);

         progressBar = findViewById(R.id.pb_post);
         imageView = findViewById(R.id.iv_post);
         videoView=findViewById(R.id.vv_post);
         btnChooseFile=findViewById(R.id.btn_choosen_post);
         btnUploadFile=findViewById(R.id.btn_upload_post);
         etdesc=findViewById(R.id.et_desc_post);
         etPno=findViewById(R.id.et_phoneNo_post);

         storageReference= FirebaseStorage.getInstance().getReference("User post");


        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        db1=database.getReference("All images").child(currentuid);
        db2=database.getReference("All videos").child(currentuid);
        db3=database.getReference("All posts");

        btnUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dopost();
            }
        });

        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImage();
            }
        });

    }

    private void ChooseImage() {

        Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_FILE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_FILE || resultCode == RESULT_OK || data != null || data.getData() != null){

            selectedUri= data.getData();
            if (selectedUri.toString().contains("image")){

                Picasso.get().load(selectedUri).into(imageView);
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.INVISIBLE);
                type= "iv";

            }else if (selectedUri.toString().contains("video")){

                videoView.setMediaController(mediaController);
                imageView.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(selectedUri);
                videoView.start();
                type="vv";
            }else {
                Toast.makeText(this, "NO FILE SELECTED", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver =getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        FirebaseFirestore db =FirebaseFirestore.getInstance();
        DocumentReference documentReference =db.collection("user").document(currentuid);
        documentReference.get()
                .addOnCompleteListener(task -> {
                    if (task.getResult().exists()){
                        name = task.getResult().getString("name");
                        url = task.getResult().getString("url");

                    }else {
                        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                    }

                });


    }

    private void Dopost() {

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        String desc = etdesc.getText().toString();
        String pNo= etPno.getText().toString();


        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String savedate = currentDate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
        final String savetime = currenttime.format(ctime.getTime());

        String time = savedate + ":"+ savetime;


        if (TextUtils.isEmpty(desc) || TextUtils.isEmpty(pNo) || selectedUri != null){

            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference= storageReference.child(System.currentTimeMillis()+"."+getFileExt(selectedUri));
            uploadTask = reference.putFile(selectedUri);

            Task<Uri> urlTask = uploadTask.continueWithTask((task) -> {

                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();

            }).addOnCompleteListener((task) ->  {

                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();

                        if (type.equals("iv")){
                            postmember.setDesc(desc);
                            postmember.setPhoneNo(pNo);
                            postmember.setName(name);
                            postmember.setPostUri(downloadUri.toString());
                            postmember.setTime(time);
                            postmember.setUid(currentuid);
                            postmember.setUrl(url);
                            postmember.setType("iv");

                            String id= db1.push().getKey();
                            db1.child(id).setValue(postmember);

                            String id1= db3.push().getKey();
                            db3.child(id1).setValue(postmember);

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(post.this, "Post Uploaded", Toast.LENGTH_SHORT).show();

                        }else if(type.equals("vv")){

                            postmember.setDesc(desc);
                            postmember.setPhoneNo(pNo);
                            postmember.setName(name);
                            postmember.setPostUri(downloadUri.toString());
                            postmember.setTime(time);
                            postmember.setUid(currentuid);
                            postmember.setUrl(url);
                            postmember.setType("vv");

                            String id3= db2.push().getKey();
                            db2.child(id3).setValue(postmember);

                            String id4= db3.push().getKey();
                            db3.child(id4).setValue(postmember);

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(post.this, "Post Uploaded", Toast.LENGTH_SHORT).show();

                        }else {

                            Toast.makeText(post.this, "Error", Toast.LENGTH_SHORT).show();

                        }

                    }

            });

        }else {
            Toast.makeText(this, "Please fill All Fields", Toast.LENGTH_SHORT).show();
        }
    }
}
