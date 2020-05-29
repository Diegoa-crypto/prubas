package com.Marche.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.Marche.Interface.ItemClickListner;
import com.Marche.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MensajesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtproductName_chat;
    public CircleImageView imagen_del_producto_chat;
    public ItemClickListner listner;

    public MensajesViewHolder(View itemView) {
        super(itemView);

        txtproductName_chat = (TextView) itemView.findViewById(R.id.nombre_producto_mensaje);


    }
    public void setItemClickListner (ItemClickListner listner)
    {
        this.listner = listner;

    }

    @Override
    public void onClick(View v)
    {
        listner.onClick(v, getAdapterPosition(), false);

    }
}