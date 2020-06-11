package com.Marche.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Marche.ChatActivity;
import com.Marche.Perfil.Products;
import com.Marche.Perfil.Usuarios;
import com.Marche.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private Context mContext;
    private List<Usuarios> mUsuarios;


    public ProductAdapter(Context mContext, List<Usuarios> mUsuarios){
        this.mUsuarios = mUsuarios;
        this.mContext=mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.new_mensaje_item,parent,false);
        return new ProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Usuarios usuarios = mUsuarios.get(position);
        final String userid= usuarios.getUserID();
        holder.username.setText(usuarios.getfName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("userid", userid);
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsuarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;

        public ViewHolder(View itemView){
            super(itemView);

            username= itemView.findViewById(R.id.nombre_producto_mensaje);
        }

    }
}
