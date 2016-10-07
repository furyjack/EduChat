package com.example.admin.educhat;

import android.app.Application;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;


public class baseclass extends Application {
    boolean wasinbackground;
    Timer transitiontimer;
    int Thresholdtime=2000;
    TimerTask mActivityTransitionTimerTask;
    FirebaseAuth auth;
    FirebaseUser muser;
    DatabaseReference userlastonline;
    DatabaseReference connectedRef;
    DatabaseReference isonline;


    @Override
    public void onCreate() {
        super.onCreate();
         auth=FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
         muser=auth.getCurrentUser();
       connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        isonline= FirebaseDatabase.getInstance().getReference().child("Users").child(muser.getUid()).child("isonline");

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Toast.makeText(baseclass.this, "online", Toast.LENGTH_SHORT).show();
                    isonline.setValue(true);

                } else {
                    Toast.makeText(baseclass.this, "offline", Toast.LENGTH_SHORT).show();
                    isonline.setValue(false);

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
         userlastonline= FirebaseDatabase.getInstance().getReference().child("Users").child(muser.getUid()).child("lastseen");
    }

    public void startActivityTransitionTimer() {
        this.transitiontimer = new Timer();
        this.mActivityTransitionTimerTask = new TimerTask() {
            public void run() {
                baseclass.this.wasinbackground = true;
                userlastonline.setValue(ServerValue.TIMESTAMP);
                isonline.setValue(false);
            }
        };

        this.transitiontimer.schedule(mActivityTransitionTimerTask,
                Thresholdtime);
    }

    public void stopActivityTransitionTimer() {
        if (this.mActivityTransitionTimerTask != null) {
            this.mActivityTransitionTimerTask.cancel();
        }

        if (this.transitiontimer != null) {
            this.transitiontimer.cancel();
        }

        this.wasinbackground = false;
       connectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               boolean connected = dataSnapshot.getValue(Boolean.class);
               if (connected) {

                   isonline.setValue(true);

               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
    }




}
