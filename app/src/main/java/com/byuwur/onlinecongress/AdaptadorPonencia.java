package com.byuwur.onlinecongress;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdaptadorPonencia extends RecyclerView.Adapter<AdaptadorPonencia.PonenciaViewHolder> {

    private ArrayList<HolderPonencia> listaponencia;
    private onItemClickListener mlistener;

    AdaptadorPonencia(ArrayList<HolderPonencia> listaponencia) {
        this.listaponencia = listaponencia;
    }

    void setonItemClickListener(onItemClickListener listener) {
        mlistener = listener;
    }

    @NonNull
    @Override
    public PonenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ponencia, null, false);
        return new PonenciaViewHolder(view, mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull final PonenciaViewHolder holder, int position) {
        holder.ponencianombre.setText(listaponencia.get(position).getNombre());
        holder.ponenciaid.setText(listaponencia.get(position).getId());
        holder.ponenciainst.setText(listaponencia.get(position).getInst());
        holder.ponenciaidioma.setText(listaponencia.get(position).getIdioma());
        holder.ponenciacategoria.setText(listaponencia.get(position).getCategoria());
        holder.ponenciadias.setText(listaponencia.get(position).getDias());
        //load image
        Picasso.get().load(listaponencia.get(position).getImg())
                //.networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                .fit().centerCrop()
                .into(holder.ponenciaimg, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Carga", "Cargada");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("Carga", "Error al cargar");
                        holder.ponenciaimg.setImageResource(R.drawable.no_image);
                        //Toast.makeText(ctx, "Ocurrieron errores al cargar algunas imágenes.",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return listaponencia.size();
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    class PonenciaViewHolder extends RecyclerView.ViewHolder {
        TextView ponencianombre, ponenciaid, ponenciainst, ponenciaidioma, ponenciacategoria, ponenciadias;
        ImageView ponenciaimg;

        private PonenciaViewHolder(View itemView, final onItemClickListener listener) {
            super(itemView);
            ponencianombre = itemView.findViewById(R.id.ponencianombre);
            ponenciaid = itemView.findViewById(R.id.ponenciaid);
            ponenciainst = itemView.findViewById(R.id.ponenciainst);
            ponenciaidioma = itemView.findViewById(R.id.ponenciaidioma);
            ponenciacategoria = itemView.findViewById(R.id.ponenciacategoria);
            ponenciadias = itemView.findViewById(R.id.ponenciadias);
            ponenciaimg = itemView.findViewById(R.id.ponenciaimg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}