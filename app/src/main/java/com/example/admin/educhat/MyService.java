package com.example.admin.educhat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyService extends Service {
    FirebaseAuth auth=FirebaseAuth.getInstance();
    DatabaseReference ref;


    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(auth.getCurrentUser()!=null)
        ref= FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid()).child("isonline");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        ref.setValue(false);
        auth.signOut();
        super.onTaskRemoved(rootIntent);


    }
}
