package com.example.admin.educhat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.educhat.utils.Partner;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference userchatref;
    RecyclerView Rvthreads;
    DatabaseReference isonline;
       DatabaseReference userlastonline;
       DatabaseReference connectedRef;
    Toolbar toolbar;


    public static class Partnerviewholder extends RecyclerView.ViewHolder
    {
        TextView tvName;

        public Partnerviewholder(View itemView) {
            super(itemView);
            tvName= (TextView) itemView.findViewById(R.id.text1);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        baseclass myapp=(baseclass)this.getApplication();
        myapp.isonline=isonline;
        myapp.connectedRef=connectedRef;
        myapp.userlastonline=userlastonline;
        isonline.onDisconnect().setValue(false, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });
        if(myapp.wasinbackground)
        {
            Toast.makeText(MainActivity.this, "app was in background", Toast.LENGTH_SHORT).show();
        }
        else
        {

        }
        myapp.stopActivityTransitionTimer();

    }

    @Override
    protected void onPause() {
        super.onPause();
        baseclass myapp=(baseclass)this.getApplication();
        myapp.startActivityTransitionTimer();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar)findViewById(R.id.tb_main);
        toolbar.setTitle("Messages");
        toolbar.setTitleTextColor(-1);

        setSupportActionBar(toolbar);
        auth=FirebaseAuth.getInstance();
       // auth.signInWithEmailAndPassword("lakshaytaneja26@gmail.com","viratkohli");
        user=auth.getCurrentUser();
        if(user==null)
        {
            startActivity(new Intent(this,Login.class));
            finish();
        }
        startService(new Intent(this,MyService.class));
        isonline=FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid()).child("isonline");

        userlastonline=FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("lastseen");
        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        isonline.onDisconnect().setValue(false, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });
        userchatref= FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Threads");
        userchatref.keepSynced(true);
       Rvthreads= (RecyclerView) findViewById(R.id.rv_threads);
        Rvthreads.setHasFixedSize(true);
        Rvthreads.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerAdapter<Partner,Partnerviewholder> adapter=new FirebaseRecyclerAdapter<Partner, Partnerviewholder>(Partner.class,R.layout.partneritemlayout,Partnerviewholder.class,userchatref) {
            @Override
            protected void populateViewHolder(Partnerviewholder viewHolder, final Partner model, int position) {
                viewHolder.tvName.setText(model.name);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getApplicationContext(),chatActivity.class);
                        intent.putExtra("puid",model.uid);
                        intent.putExtra("pname",model.name);
                        startActivity(intent);
                    }
                });
               

            }
        };

        Rvthreads.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
