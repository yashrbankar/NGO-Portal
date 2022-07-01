package com.example.helpinghands;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class PostViewholder extends RecyclerView.ViewHolder {

    ImageView imageViewprofile,iv_post;
    TextView tv_name,tv_desc,tv_like,tv_comment,tv_time,tv_nameprofile,tv_PNo;
    public ImageButton likebtn,menuoptions,commentbtn;
    DatabaseReference likesref;
    FirebaseDatabase database= FirebaseDatabase.getInstance();
    int likescount;

    public PostViewholder(@NonNull @NotNull View itemView) {
        super(itemView);
    }

    @SuppressLint("RestrictedApi")
    public void SetPost(FragmentActivity activity, String name, String url, String postUri, String time,
                        String uid, String type, String desc, String phoneNo){

        imageViewprofile= itemView.findViewById(R.id.ivprofile_item);
        iv_post= itemView.findViewById(R.id.iv_post_item);
        //tv_comment=itemView.findViewById(R.id.tv_comment_post);
        tv_desc=itemView.findViewById(R.id.tv_desc_post);
        commentbtn=itemView.findViewById(R.id.commentbutton_posts);
        likebtn= itemView.findViewById(R.id.likebutton_posts);
        tv_like= itemView.findViewById(R.id.tv_likes_post);
        menuoptions= itemView.findViewById(R.id.morebutton_posts);
        tv_time= itemView.findViewById(R.id.tv_time_post);
        tv_nameprofile= itemView.findViewById(R.id.tv_name_post);
        tv_PNo= itemView.findViewById(R.id.tv_pNo_post);


        if(type.equals("iv")){

            Picasso.get().load(url).into(imageViewprofile);
            Picasso.get().load(postUri).into(iv_post);
            tv_desc.setText(desc);
            tv_time.setText(time);
            tv_nameprofile.setText(name);
            tv_PNo.setText(phoneNo);
            //playerView.setVisibility(View.INVISIBLE);


        }else if (type.equals("vv")){
            //iv_post.setVisibility(View.INVISIBLE);
            tv_desc.setText(desc);
            tv_time.setText(time);
            tv_nameprofile.setText(name);
            tv_PNo.setText(phoneNo);
            //Picasso.get().load(url).into(imageViewprofile);

        }

    }

    public void likesChecker(final String postkey){
        likebtn=itemView.findViewById(R.id.likebutton_posts);

        likesref=database.getReference("post likes");
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

        likesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (snapshot.child(postkey).hasChild(uid)){
                    likebtn.setImageResource(R.drawable.ic_like);
                    likescount= (int)snapshot.child(postkey).getChildrenCount();
                    tv_like.setText(Integer.toString(likescount)+"likes");
                }else {
                    likebtn.setImageResource(R.drawable.ic_dislike);
                    likescount= (int)snapshot.child(postkey).getChildrenCount();
                    tv_like.setText(Integer.toString(likescount)+"likes");
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}












