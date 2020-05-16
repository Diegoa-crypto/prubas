package com.Marche.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Marche.Interface.ItemClickListner;
import com.Marche.R;

public class CommentsViewHolder extends RecyclerView.ViewHolder
{
    public TextView myUserName,myComment,myDate,myTime;
    public ItemClickListner listner;

    public CommentsViewHolder(@NonNull View itemView) {
        super(itemView);

        myUserName=(TextView)itemView.findViewById(R.id.comment_username);
        myComment=(TextView)itemView.findViewById(R.id.comment_text);
        myDate=(TextView)itemView.findViewById(R.id.comment_date);
        myUserName=(TextView)itemView.findViewById(R.id.comment_username);
    }


}
