package com.example.admin.educhat;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyService extends Service {
    FirebaseAuth auth=FirebaseAuth.getInstance();
    DatabaseReference ref= FirebaseDatabase.getInstance().getReference();
    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(MyService.this, "boot completed", Toast.LENGTH_SHORT).show();
        if(auth.getCurrentUser()!=null)
        {
            DatabaseReference user=ref.child(auth.getCurrentUser().getUid()).child("Threads");
            user.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setContentTitle("EduChat")
                                    .setContentText("New Message");

                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(1007,mBuilder.build());

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setContentTitle("EduChat")
                                    .setContentText("New Message");

                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(1007,mBuilder.build());

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setContentTitle("EduChat")
                                    .setContentText("New Message");

                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(1007,mBuilder.build());


                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setContentTitle("EduChat")
                                    .setContentText("New Message");

                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(1007,mBuilder.build());


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
        else
        {

        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
