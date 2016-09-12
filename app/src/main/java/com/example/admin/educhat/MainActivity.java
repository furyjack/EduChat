package com.example.admin.educhat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference userchatref;
    RecyclerView Rvthreads;


    public static class Partnerviewholder extends RecyclerView.ViewHolder
    {
        TextView tvName;

        public Partnerviewholder(View itemView) {
            super(itemView);
            tvName= (TextView) itemView.findViewById(R.id.text1);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword("lakshaytaneja26@gmil.com","viratkohli");
        user=auth.getCurrentUser();
        if(user==null)
        {
            startActivity(new Intent(this,Login.class));
            finish();
        }
        userchatref= FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Threads");
        Rvthreads= (RecyclerView) findViewById(R.id.rv_threads);
        Rvthreads.setHasFixedSize(true);
        Rvthreads.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerAdapter<String,Partnerviewholder> adapter=new FirebaseRecyclerAdapter<String, Partnerviewholder>(String.class,R.layout.partneritemlayout,Partnerviewholder.class,userchatref) {
            @Override
            protected void populateViewHolder(Partnerviewholder viewHolder, String model, int position) {
                viewHolder.tvName.setText(model);

            }
        };

        Rvthreads.setAdapter(adapter);

    }




}
