package com.example.admin.educhat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.educhat.utils.Message;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;


public class chatActivity extends AppCompatActivity {

    DatabaseReference isonline;
    DatabaseReference lastmessage;
    DatabaseReference plastmessage;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageTextView;
        private TextView messagetime;
        private FrameLayout messagebackhround;
        private ImageView icsent;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.textview_message);
            messagebackhround=(FrameLayout)itemView.findViewById(R.id.incoming_layout_bubble);
           icsent=(ImageView)itemView.findViewById(R.id.ic_sent);
            messagetime=(TextView)itemView.findViewById(R.id.textview_time);


        }
    }



    private ImageButton mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private String mUsername;
    private String PartnerName;
    private String Partneruid;
    private Toolbar mtoolbar;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference mrefMessage;
    private DatabaseReference trefMessage;
    private DatabaseReference PisOnline;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder>
            mFirebaseAdapter;
    private DatabaseReference PartnerLastseen;


    @Override
    protected void onResume() {
        super.onResume();
        isonline.onDisconnect().setValue(false);
        query_msgs();
        baseclass myapp=(baseclass)this.getApplication();
        if(myapp.wasinbackground)
        {
            Toast.makeText(chatActivity.this, "app was in background", Toast.LENGTH_SHORT).show();
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

private void query_msgs()
{

    Query nsmsgs=mrefMessage.orderByChild("viewcount").equalTo(1);
    nsmsgs.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Iterable<DataSnapshot> no= dataSnapshot.getChildren();
            Iterator<DataSnapshot> i=no.iterator();
            while(i.hasNext())
            {
                DataSnapshot f=i.next();
                if(f.child("name").getValue().equals(PartnerName)) {
                    String uid = f.getKey();
                    mrefMessage.child(uid).child("viewcount").setValue(2);
                    trefMessage.child(uid).child("viewcount").setValue(2);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });


}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        PartnerName=getIntent().getStringExtra("pname");
        Partneruid=getIntent().getStringExtra("puid");
        mtoolbar=(Toolbar)findViewById(R.id.tb_groupchat);
        mtoolbar.setTitleTextColor(-1);
        mtoolbar.setSubtitleTextColor(-1);

        setSupportActionBar(mtoolbar);


        if(getSupportActionBar()!=null)
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(PartnerName);
        if(mFirebaseUser==null)
        {
            startActivity(new Intent(this,Login.class));
            finish();
            return;

        }
        PisOnline=FirebaseDatabase.getInstance().getReference().child("Users").child(Partneruid).child("isonline");
        PartnerLastseen=FirebaseDatabase.getInstance().getReference().child("Users").child(Partneruid).child("lastseen");
        plastmessage=FirebaseDatabase.getInstance().getReference().child("Users").child(Partneruid).child("Threads").child(mFirebaseUser.getUid()).child("lastmessage");
        lastmessage=FirebaseDatabase.getInstance().getReference().child("Users").child(mFirebaseUser.getUid()).child("Threads").child(Partneruid).child("lastmessage");
        isonline=FirebaseDatabase.getInstance().getReference().child("Users").child(mFirebaseAuth.getCurrentUser().getUid()).child("isonline");
       isonline.onDisconnect().setValue(false, new DatabaseReference.CompletionListener() {
           @Override
          public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });

        PisOnline.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Boolean is= (Boolean) dataSnapshot.getValue();
                if(is)
                {
                    mtoolbar.setSubtitle("Online");
                }
                else
                {

                    PartnerLastseen.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                           long timestamp= (long) dataSnapshot.getValue();
                           Date msgdate=new Date(timestamp);
                            mtoolbar.setSubtitle("Last seen at "+msgdate.getHours() +":"+ msgdate.getMinutes());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    ;
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        PisOnline.keepSynced(true);
        String uid=mFirebaseUser.getUid();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        trefMessage=mFirebaseDatabaseReference.child("Users").child(Partneruid).child("Threads").child(uid).child("Messages");
        mrefMessage=mFirebaseDatabaseReference.child("Users").child(uid).child("Threads").child(Partneruid).child("Messages");
       mrefMessage.keepSynced(true);



        mUsername = mFirebaseUser.getDisplayName(); //set it in signup activity till then its hardcoded
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);


        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message,
                MessageViewHolder>(
                Message.class,
                R.layout.chat_user1_item,
                MessageViewHolder.class,
                mrefMessage) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder,
                                              Message friendlyMessage, int position) {
                RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(300,0,8,0);
                mProgressBar.setVisibility(ProgressBar.GONE);
                if(friendlyMessage.getViewcount()==1)
                {
                    viewHolder.icsent.setColorFilter(Color.argb(255,0,0,0));
                }

                if(!friendlyMessage.getName().equals(PartnerName))
                {
                    viewHolder.messagebackhround.setLayoutParams(params);
                    viewHolder.messagebackhround.setBackgroundResource(R.drawable.balloon_outgoing_normal);
                    viewHolder.messagetime.setPadding(40,0,70,30);
                    viewHolder.icsent.setVisibility(View.VISIBLE);

                }
                else
                {
                    params.setMargins(8,0,300,0);
                    viewHolder.messagetime.setPadding(40,0,70,30);
                    viewHolder.messagebackhround.setLayoutParams(params);
                    viewHolder.messagebackhround.setBackgroundResource(R.drawable.balloon_incoming_normal);
                    viewHolder.icsent.setVisibility(View.INVISIBLE);
                }
                viewHolder.messageTextView.setText(friendlyMessage.getText());

               viewHolder.messagetime.setText(friendlyMessage.getMsgdate().getHours()+":"+friendlyMessage.getMsgdate().getMinutes());


            }
        };


        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
                query_msgs();
            }
        });
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);


        mMessageEditText = (EditText) findViewById(R.id.messageEditText);

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Message friendlyMessage = new
                        Message(mMessageEditText.getText().toString(),
                        mUsername,Calendar.getInstance().getTime());


                        DatabaseReference ref=mrefMessage.push();
                        friendlyMessage.setUid(ref.getKey());
                        friendlyMessage.setViewcount(1);
                        ref.setValue(friendlyMessage);

                        trefMessage.child(friendlyMessage.getUid()).setValue(friendlyMessage);
                        plastmessage.setValue(friendlyMessage);
                        lastmessage.setValue(friendlyMessage);


                mMessageEditText.setText("");


            }
        });


    }
}
