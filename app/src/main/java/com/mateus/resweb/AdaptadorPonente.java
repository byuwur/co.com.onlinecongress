package com.mateus.resweb;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdaptadorPonente extends RecyclerView.Adapter<AdaptadorPonente.PonenteViewHolder> {

    private ArrayList<HolderPonente> listaponente;
    private onItemClickListener mlistener;

    public interface onItemClickListener{
        void onItemClick (int position);
    }

    public void setonItemClickListener(onItemClickListener listener){
        mlistener = listener;
    }

    public AdaptadorPonente(ArrayList<HolderPonente> listaponente){
        this.listaponente = listaponente;
    }

    @NonNull
    @Override
    public PonenteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ponente,null,false);
        return new PonenteViewHolder(view, mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull final PonenteViewHolder holder, int position) {
        holder.ponentenombre.setText(listaponente.get(position).getNombre());
        holder.ponenteidc.setText(listaponente.get(position).getIdC());
        holder.ponentepais.setText(listaponente.get(position).getDia());
        holder.ponenteidioma.setText(listaponente.get(position).getIdR());
        holder.ponentenivel.setText(listaponente.get(position).getDireccion());
        holder.ponenteinst.setText(listaponente.get(position).getCiudad());
        //load image
        Picasso.get().load(listaponente.get(position).getImg())
                //.networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                .fit().centerCrop()
                .into(holder.ponenteimg, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("Carga","Cargada");
            }
            @Override
            public void onError(Exception e) {
                Log.d("Carga","Error al cargar");
                holder.ponenteimg.setImageResource(R.drawable.no_image);
                //Toast.makeText(ctx, "Ocurrieron errores al cargar algunas imágenes.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaponente.size();
    }

    public class PonenteViewHolder extends RecyclerView.ViewHolder {

        TextView ponentenombre,ponentenivel,ponenteinst,ponentepais, ponenteidc, ponenteidioma;
        ImageView ponenteimg;

        private PonenteViewHolder(View itemView, final onItemClickListener listener) {
            super(itemView);
            ponentenombre= itemView.findViewById(R.id.ponentenombre);
            ponenteidc= itemView.findViewById(R.id.ponenteid);
            ponentepais= itemView.findViewById(R.id.ponentepais);
            ponenteidioma= itemView.findViewById(R.id.ponenteidioma);
            ponentenivel= itemView.findViewById(R.id.ponentenivel);
            ponenteinst= itemView.findViewById(R.id.ponenteinst);
            ponenteimg=  itemView.findViewById(R.id.ponenteimg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}