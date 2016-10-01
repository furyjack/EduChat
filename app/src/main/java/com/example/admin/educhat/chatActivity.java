package com.example.admin.educhat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.educhat.utils.Message;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class chatActivity extends AppCompatActivity {


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;
        public TextView messagetime;


        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messagetime=(TextView)itemView.findViewById(R.id.date);


        }
    }



    private Button mSendButton;
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
    private FirebaseRecyclerAdapter<Message, MessageViewHolder>
            mFirebaseAdapter;


    @Override
    protected void onResume() {
        super.onResume();
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        PartnerName=getIntent().getStringExtra("pname");
        Partneruid=getIntent().getStringExtra("puid");
        mtoolbar=(Toolbar)findViewById(R.id.tb_groupchat);

        setSupportActionBar(mtoolbar);



        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(PartnerName);
        if(mFirebaseUser==null)
        {
            startActivity(new Intent(this,Login.class));
            finish();
            return;

        }
        String uid=mFirebaseUser.getUid();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mrefMessage=mFirebaseDatabaseReference.child("Users").child(uid).child("Threads").child(Partneruid).child("Messages");
       mrefMessage.keepSynced(true);

        mUsername = "lakshay"; //set it in signup activity till then its hardcoded
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);


        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message,
                MessageViewHolder>(
                Message.class,
                R.layout.item_message,
                MessageViewHolder.class,
                mrefMessage) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder,
                                              Message friendlyMessage, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.messageTextView.setText(friendlyMessage.getText());
                viewHolder.messengerTextView.setText(friendlyMessage.getName());
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

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Message friendlyMessage = new
                        Message(mMessageEditText.getText().toString(),
                        mUsername,Calendar.getInstance().getTime());
                mrefMessage
                        .push().setValue(friendlyMessage);
                mMessageEditText.setText("");


            }
        });


    }
}
