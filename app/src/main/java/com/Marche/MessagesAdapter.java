package com.Marche;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>
{
    private List<Messages> userMessagesList;
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    private Context mContext;
    private FirebaseAuth mAuth;
    private String userID;
    private FirebaseFirestore fStore;
    private DatabaseReference usersDatabaseRef;
    private static final String TAG = "";

    public MessagesAdapter (Context context, List<Messages> userMessagesList){
        this.userMessagesList = userMessagesList;
        this.mContext = mContext;

    }




    @NonNull
    @Override
    public MessagesAdapter.MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        if(viewType==MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessagesAdapter.MessagesViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessagesAdapter.MessagesViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MessagesViewHolder holder, int position) {
        Messages messages=userMessagesList.get(position);
        holder.show_message.setText(messages.getMessage());
    }

    @Override
    public int getItemCount(){
        return userMessagesList.size();
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder
    {

        public TextView show_message;

        public MessagesViewHolder(View itemView){
            super(itemView);
            show_message=itemView.findViewById(R.id.show_messages);


        }

    }

    @Override
    public int getItemViewType(int position){
        userID = mAuth.getCurrentUser().getUid();
        if(userMessagesList.get(position).getFrom().equals(userID)){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
