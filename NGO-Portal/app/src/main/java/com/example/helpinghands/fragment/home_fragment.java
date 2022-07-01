package com.example.helpinghands.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.helpinghands.PostViewholder;
import com.example.helpinghands.Postmember;
import com.example.helpinghands.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
//import com.google.firebase.ui.database.FirebaseRecyclerOptions;
//import com.google.firebase.ui.database.FirebaseRecyclerAdapter;

public class home_fragment extends Fragment {

    Button button;
    RecyclerView recyclerView;
    FirebaseDatabase database= FirebaseDatabase.getInstance();
    DatabaseReference reference,likeref;
    Boolean likechecker = false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        reference= database.getReference("All posts");
        likeref= database.getReference("post likes");
        recyclerView= getActivity().findViewById(R.id.rv_posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Postmember> options=
                new FirebaseRecyclerOptions.Builder<Postmember>()
                    .setQuery(reference,Postmember.class)
                    .build();

        FirebaseRecyclerAdapter<Postmember, PostViewholder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Postmember, PostViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull PostViewholder holder, int position, @NonNull @NotNull Postmember model) {

                        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                        final String currentUserid = user.getUid();

                        final String postkey = getRef(position).getKey();

                        holder.SetPost(getActivity(),model.getName(),model.getUrl(),model.getPostUri(),
                                model.getTime(),model.getUid(),model.getType(),model.getDesc(),model.getPhoneNo());

                        holder.likesChecker(postkey);
                        holder.likebtn.setOnClickListener((v) -> {

                            likechecker=true;

                            likeref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                    if(likechecker.equals(true)){
                                        if (snapshot.child(postkey).hasChild(currentUserid)){
                                            likeref.child(postkey).child(currentUserid).removeValue();
                                            Toast.makeText(getActivity(), "Unliked", Toast.LENGTH_SHORT).show();
                                            likechecker=false;
                                        }else {
                                            likeref.child(postkey).child(currentUserid).setValue(true);
                                            likechecker=false;
                                            Toast.makeText(getActivity(), "liked", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                        } );
                    }

                    @NonNull
                    @NotNull
                    @Override
                    public PostViewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

                        View view=LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.post_layout,parent,false);


                        return new PostViewholder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
}