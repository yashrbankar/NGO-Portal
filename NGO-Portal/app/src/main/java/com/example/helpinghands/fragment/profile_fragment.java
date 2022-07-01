package com.example.helpinghands.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.helpinghands.ImagesFragment;
import com.example.helpinghands.PostViewholder;
import com.example.helpinghands.Postmember;
import com.example.helpinghands.setup_profile;
import com.example.helpinghands.sign_in_page;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpinghands.R;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class  profile_fragment extends Fragment  {


    private Button sign_out_button;
    private ImageButton edit_profile;
    ImageView imageView;
    TextView full_name,emailid,phone_no;

    FirebaseDatabase database;
    DatabaseReference ref;
    RecyclerView recyclerView;
    

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageView=getActivity().findViewById(R.id.profile_image);
        full_name=getActivity().findViewById(R.id.full_name);
        phone_no=getActivity().findViewById(R.id.phone_no);
        emailid=getActivity().findViewById(R.id.emailid);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid= user.getUid();
        database= FirebaseDatabase.getInstance();

        recyclerView= getActivity().findViewById(R.id.rv_All_images_);
        ref = database.getReference("All images").child(uid);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));



    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String currentid = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firestore= FirebaseFirestore.getInstance();

        reference = firestore.collection("user").document(currentid);
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {

                        if(task.getResult().exists()){

                            String fullnameResult= task.getResult().getString("name");
                            String emailidResult= task.getResult().getString("email");
                            String phonenoResult= task.getResult().getString("phoneNo");
                            String uriResult= task.getResult().getString("uri");

                            Picasso.get().load(uriResult).into(imageView);
                            full_name.setText(fullnameResult);
                            emailid.setText(emailidResult);
                            phone_no.setText(phonenoResult);



                        }else {
                            Intent intent= new Intent(getActivity(),setup_profile.class);
                            startActivity(intent);
                        }
                    }
                });

        FirebaseRecyclerOptions<Postmember> options=
                new FirebaseRecyclerOptions.Builder<Postmember>()
                        .setQuery(ref,Postmember.class)
                        .build();

        FirebaseRecyclerAdapter<Postmember, ImagesFragment> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Postmember, ImagesFragment>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull ImagesFragment holder, int position, @NonNull @NotNull Postmember model) {

                        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                        final String currentUserid = user.getUid();

                        final String postkey = getRef(position).getKey();

                        holder.SetImage(getActivity(),model.getName(),model.getUrl(),model.getPostUri(),
                                model.getTime(),model.getUid(),model.getType(),model.getDesc(),model.getPhoneNo());


                        }


                    @NonNull
                    @NotNull
                    @Override
                    public ImagesFragment onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

                        View view=LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.post_images,parent,false);


                        return new ImagesFragment(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();

        GridLayoutManager glm= new GridLayoutManager(getActivity(),3,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(glm);
        recyclerView.setAdapter(firebaseRecyclerAdapter);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.profile_fragment, container, false);


          sign_out_button=v.findViewById(R.id.sign_out_button);
          edit_profile=v.findViewById(R.id.edit_profile);
          edit_profile.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent i1=new Intent(getActivity(), setup_profile.class);
                  startActivity(i1);
              }
          });
          sign_out_button.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  FirebaseAuth.getInstance().signOut();
                  Intent i=new Intent(getActivity(),sign_in_page.class);
                  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                  startActivity(i);
              }
          });

        return v;
    }

}