package com.Marche;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder>
{
    private List<Messages> mMensaje;
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    private Context mContext;
    private FirebaseAuth fAuth;
    private String userID;

    FirebaseUser fuser;

    private FirebaseFirestore fStore;

    private DatabaseReference usersDatabaseRef;
    private static final String TAG = "";

    public MessagesAdapter (Context mContext, List<Messages> mMensaje){
        this.mMensaje = mMensaje;
        this.mContext = mContext;

    }
    @Override
    public int getItemCount()
    {
        return mMensaje.size();
    }
    @Override
    public int getItemViewType(int position){
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(mMensaje.get(position).getFrom().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }


    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        if(viewType==MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessagesAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessagesAdapter.ViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, final int position) {
        Messages messages=mMensaje.get(position);
        holder.show_message.setText(messages.getMessage());

        if(position==mMensaje.size()-1){
            if(messages.isVisto()){
                holder.txt_seen.setText("Visto");
            }else{
                holder.txt_seen.setText("Entregado");
            }
        }else{
            holder.txt_seen.setVisibility(View.GONE);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView show_message;
        public TextView txt_seen;
        RelativeLayout messageRelative;


        public ViewHolder(View itemView){
            super(itemView);
            show_message=itemView.findViewById(R.id.show_messages);
            txt_seen=itemView.findViewById(R.id.txt_seen);
            messageRelative=itemView.findViewById(R.id.messageLayout);



        }

    }


}
