package com.Marche;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.FontResourcesParserCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Marche.Perfil.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private Toolbar ChattoolBar;
    private ImageButton SendMessageButton, SendImagefileButton;
    private EditText userMessageInput;
    private String messageReceicverID, messageReceiverName, messageSenderID;
    private DatabaseReference RootRef;
    private String saveCurrentDate, saveCurrentTime;
    private FirebaseAuth mAuth;
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messageAdapter;
    private TextView receiverName;
    private CircleImageView receiverProfileImage;
    private String user_id = "";
    List<Messages> userMessagesList;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        IntializeFields();

        RootRef= FirebaseDatabase.getInstance().getReference();
        recyclerView=findViewById(R.id.messages_list_users);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAuth= FirebaseAuth.getInstance();
        messageSenderID=mAuth.getCurrentUser().getUid();



        messageReceicverID=getIntent().getExtras().get("user_id").toString();
        //messageReceiverName=getIntent().getExtras().get("userName").toString();



        SendMessageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String msg=userMessageInput.getText().toString();
                if(!msg.equals("")){
                    sendMessage(messageSenderID,messageReceicverID,msg);
                }else {
                    Toast.makeText(ChatActivity.this, "Necesitas escribir un mensaje", Toast.LENGTH_SHORT).show();
                }
                userMessageInput.setText("");

            }
        });
        readMessage(messageSenderID,messageReceicverID);

    }
    private void sendMessage(String from, String para, String message){

        DatabaseReference reference =FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("message", message);
        hashMap.put("time", saveCurrentTime);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("type", "text");
        hashMap.put("from", from);
        hashMap.put("para", para);
        reference.child("Messages").push().setValue(hashMap);

    }
    private void readMessage(final String myid, final String userid){
        userMessagesList=new ArrayList<>();
        RootRef=FirebaseDatabase.getInstance().getReference("Messages");
        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userMessagesList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Messages messages=snapshot.getValue(Messages.class);
                    if(messages.getPara().equals(myid)&& messages.getFrom().equals(userid)||
                        messages.getPara().equals(userid)&&messages.getFrom().equals(myid)){
                        userMessagesList.add(messages);
                    }
                    messageAdapter=new MessagesAdapter(ChatActivity.this,userMessagesList);
                    recyclerView.setAdapter(messageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void IntializeFields()
    {
        ChattoolBar = (Toolbar)findViewById(R.id.chat_bar_layout);
        setSupportActionBar(ChattoolBar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        receiverName=(TextView) findViewById(R.id.custom_profile_name);

        SendMessageButton=(ImageButton) findViewById(R.id.send_menssage_button);
        SendImagefileButton=(ImageButton) findViewById(R.id.send_image_file_button);
        userMessageInput = (EditText) findViewById(R.id.input_message);

    }
}
