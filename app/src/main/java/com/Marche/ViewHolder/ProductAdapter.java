package com.Marche.ViewHolder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.Marche.ChatActivity;
import com.Marche.Messages;
import com.Marche.Perfil.Chatlist;
import com.Marche.Perfil.Products;
import com.Marche.Perfil.Usuarios;
import com.Marche.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.EventListener;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>
{
    private Context mContext;
    private List<Usuarios> mUsuarios;
    private String theLasMessage, pname_mensaje, pprice_mensaje, pimage_mensaje;
    private boolean ischat;


    public ProductAdapter(Context mContext, List<Usuarios> mUsuarios, boolean ischat)
    {
        this.mUsuarios = mUsuarios;
        this.mContext=mContext;
        this.ischat=ischat;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.new_mensaje_item,parent,false);
        return new ProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductAdapter.ViewHolder holder, final int position)
    {
        final Usuarios usuarios = mUsuarios.get(position);
        final String userid= usuarios.getUserID();
        holder.username.setText(usuarios.getfName());


        if(ischat){
            lastMessage(usuarios.getUserID(),holder.last_message);

        }else{
            holder.last_message.setVisibility(View.GONE);
        }

        if(ischat){
            if(usuarios.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder .img_off.setVisibility(View.GONE);
            }else {
                holder.img_on.setVisibility(View.GONE);
                holder .img_off.setVisibility(View.VISIBLE);
            }
        }else{
            holder.img_on.setVisibility(View.GONE);
            holder .img_off.setVisibility(View.GONE);
        }





        //SharedPreferences sharedPreferences = mContext.getSharedPreferences("nombre",mContext.MODE_PRIVATE);
        //final String pimage = sharedPreferences.getString("pimage", "nada");





        nombre_producto(usuarios.getUserID(), holder.pname,holder.pimage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("userid", userid);
                intent.putExtra("pname_mesage",pname_mensaje);
                intent.putExtra("pprice_mensaje",pprice_mensaje);
                intent.putExtra("pimage_mensaje",pimage_mensaje);
                mContext.startActivity(intent);

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext(),R.style.MyDialogTheme);
                builder.setPositiveButton("Borrrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        final DatabaseReference userDatabase= FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
                        userDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                                    Chatlist chatlist =snapshot.getValue(Chatlist.class);
                                    if(usuarios.getUserID().equals(chatlist.getId()))
                                    {
                                        HashMap<String, Object> result = new HashMap<>();
                                        result.put("deleted", firebaseUser.getUid());
                                        snapshot.getRef().updateChildren(result);


                                    }

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //Toast.makeText(mContext, "userdatabase", Toast.LENGTH_SHORT).show();
                        //userDatabase.child(usuarios.getUserID()).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                builder.setTitle("Eliminar conversacion");
                builder.setMessage("¿Estas seguro de eliminar toda la conversacion?");
                builder.show();
                return true;
            }
        });


    }


    @Override
    public int getItemCount()
    {
        return mUsuarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView username;
        private TextView pname;
        private CircleImageView pimage;
        private TextView last_message;
        private ImageView img_on;
        private ImageView img_off;
        //ImageView moreBtn;






        public ViewHolder(View itemView){
            super(itemView);

            username= itemView.findViewById(R.id.nombre_producto_mensaje);
            pname=itemView.findViewById(R.id.nombre_producto_mensaje_2);
            pimage=itemView.findViewById(R.id.product_image_new_mensaje);
            last_message=itemView.findViewById(R.id.last_meesage);
            img_on=itemView.findViewById(R.id.img_on);
            img_off=itemView.findViewById(R.id.img_off);
            //moreBtn= itemView.findViewById(R.id.moreBtn);



        }

    }
    private void lastMessage(final String userid, final TextView last_message){
        theLasMessage="default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Messages messages = snapshot.getValue(Messages.class);
                    if (messages.getPara().equals(firebaseUser.getUid()) && messages.getFrom().equals(userid) ||
                            messages.getPara().equals(userid) && messages.getFrom().equals(firebaseUser.getUid()))
                    {
                        theLasMessage=messages.getMessage();

                    }

                }
                switch (theLasMessage){
                    case"default":
                        last_message.setText("Sin mensaje");
                        break;
                        default:
                            last_message.setText(theLasMessage);
                            break;
                }
                theLasMessage="default";


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void nombre_producto(final String userid, final TextView pname, final CircleImageView pimage)
    {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages");
        Query query = reference.orderByChild("pname");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Messages messages = snapshot.getValue(Messages.class);

                    if (messages.getPara().equals(firebaseUser.getUid()) && messages.getFrom().equals(userid) ||
                            messages.getPara().equals(userid) && messages.getFrom().equals(firebaseUser.getUid()))
                    {
                        pname.setText(messages.getPname());
                        pname_mensaje=messages.getPname();
                        pprice_mensaje=messages.getPprice();
                        pimage_mensaje=messages.getPimage();


                        /*

                        SharedPreferences sharedPreferences = mContext.getSharedPreferences("nombre", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("pimage",messages.getPimage());
                        editor.clear();
                        editor.commit();

                         */



                        Picasso.get().load(messages.getPimage()).placeholder(R.drawable.nini).into(pimage);


                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
