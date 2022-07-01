package com.example.helpinghands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;



import com.example.helpinghands.fragment.home_fragment;
import com.example.helpinghands.fragment.profile_fragment;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;



public class final_page extends AppCompatActivity implements ChipNavigationBar.OnItemSelectedListener {
   private FirebaseAuth mFirebaseAuth;
//    TextView user_info;
//    private Button logout_button;
//    GoogleSignInClient mGoogleSignInClient;


    ChipNavigationBar navBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_page);

        loadFragment(new home_fragment());
        navBar=findViewById(R.id.chip_app_bar);
        navBar.setOnItemSelectedListener(this);

        FloatingActionButton addPost=findViewById(R.id.addPost);

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),post.class);
                startActivity(intent);
            }
        });


//        mFirebaseAuth=FirebaseAuth.getInstance();
//        logout_button=(Button)findViewById(R.id.logout_button);
//
//        user_info=findViewById(R.id.user_info);
//        String name=getIntent().getStringExtra("name");
//        String person_give_name = getIntent().getStringExtra("person_given_name");
//        String person_email = getIntent().getStringExtra("email");
//        String person_id = getIntent().getStringExtra("person_id");
//
//        user_info.setText("Person Info:- "+name
//                +"\n Person Email:-" + person_email
//                +"\n Person id:-" + person_id
//                +"\n Person given name:-" + person_give_name);
//
//
//        logout_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                    mFirebaseAuth.signOut();
//                    finish();
//                    startActivity(new Intent(final_page.this,sign_in_page.class));
//
//            }
//        });

    }

    private void loadFragment(Fragment fragment) {
        if(fragment!=null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();
        }
        else {
            Toast.makeText(this, "fragment Error", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.navigation_menu_with_button,menu);
        return true;
    }



    @Override
    public void onItemSelected(int i) {
        Fragment fragment=null;

        switch (i){
            case R.id.home:
                fragment=new home_fragment();
                break;
            case R.id.profile:
                fragment=new profile_fragment();
                break;

        }
        loadFragment(fragment);

    }

}