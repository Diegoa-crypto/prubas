package com.Marche.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Marche.ChatActivity;
import com.Marche.Messages;
import com.Marche.Perfil.Products;
import com.Marche.Perfil.Usuarios;
import com.Marche.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.EventListener;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>
{
    private Context mContext;
    private List<Usuarios> mUsuarios;




    public ProductAdapter(Context mContext, List<Usuarios> mUsuarios)
    {
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
    public void onBindViewHolder(@NonNull final ProductAdapter.ViewHolder holder, int position)
    {

        Usuarios usuarios = mUsuarios.get(position);
        final String userid= usuarios.getUserID();
        holder.username.setText(usuarios.getfName());

        //SharedPreferences sharedPreferences = mContext.getSharedPreferences("pname",mContext.MODE_PRIVATE);
        //String pname2 = sharedPreferences.getString("pname", "nada");
        //holder.pname.setText(pname2);


        nombre_producto(usuarios.getUserID(), holder.pname);

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
    public int getItemCount()
    {
        return mUsuarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        private TextView pname;
        private CircleImageView pimage;

        public ViewHolder(View itemView){
            super(itemView);


            username= itemView.findViewById(R.id.nombre_producto_mensaje);
            pname=itemView.findViewById(R.id.nombre_producto_mensaje_2);
            pimage=itemView.findViewById(R.id.product_image_new_mensaje);


        }

    }

    private void nombre_producto(final String userid, final TextView pname)
    {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages");
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Messages messages = snapshot.getValue(Messages.class);

                    if(messages.getPara().equals(firebaseUser.getUid())&& messages.getFrom().equals(userid)||
                            messages.getPara().equals(userid)&& messages.getFrom().equals(firebaseUser.getUid())){
                        pname.setText(messages.getPname());
                        //Picasso.get().load(messages.getPimage()).placeholder(R.drawable.nini).into(pimage);
                  }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
