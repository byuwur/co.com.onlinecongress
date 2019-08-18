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

public class AdaptadorConferencia extends RecyclerView.Adapter<AdaptadorConferencia.ConferenciaViewHolder> {

    private ArrayList<HolderConferencia> listaconferencia;
    private onItemClickListener mlistener;

    public AdaptadorConferencia(ArrayList<HolderConferencia> listaconferencia) {
        this.listaconferencia = listaconferencia;
    }

    public void setonItemClickListener(onItemClickListener listener) {
        mlistener = listener;
    }

    @NonNull
    @Override
    public ConferenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conferencia, null, false);
        return new ConferenciaViewHolder(view, mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ConferenciaViewHolder holder, int position) {
        holder.conferencianombre.setText(listaconferencia.get(position).getNombre());
        holder.conferenciaid.setText(listaconferencia.get(position).getId());
        holder.conferenciavalor.setText(listaconferencia.get(position).getValor());
        holder.conferenciaidioma.setText(listaconferencia.get(position).getDireccion());
        holder.conferenciacategoria.setText(listaconferencia.get(position).getCiudad());
        holder.conferenciadias.setText(listaconferencia.get(position).getDias());
        holder.conferenciahorario.setText(listaconferencia.get(position).getHorario());
        //load image
        Picasso.get().load(listaconferencia.get(position).getImg())
                //.networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                .fit().centerCrop()
                .into(holder.conferenciaimg, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Carga", "Cargada");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("Carga", "Error al cargar");
                        holder.conferenciaimg.setImageResource(R.drawable.no_image);
                        //Toast.makeText(ctx, "Ocurrieron errores al cargar algunas imágenes.",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return listaconferencia.size();
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public class ConferenciaViewHolder extends RecyclerView.ViewHolder {
        TextView conferencianombre, conferenciaid, conferenciavalor, conferenciaidioma, conferenciacategoria, conferenciadias, conferenciahorario;
        ImageView conferenciaimg;

        private ConferenciaViewHolder(View itemView, final onItemClickListener listener) {
            super(itemView);
            conferencianombre = itemView.findViewById(R.id.conferencianombre);
            conferenciaid = itemView.findViewById(R.id.conferenciaid);
            conferenciavalor = itemView.findViewById(R.id.conferenciavalor);
            conferenciaidioma = itemView.findViewById(R.id.conferenciaidioma);
            conferenciacategoria = itemView.findViewById(R.id.conferenciacategoria);
            conferenciadias = itemView.findViewById(R.id.conferenciadias);
            conferenciahorario = itemView.findViewById(R.id.conferenciahorario);
            conferenciaimg = itemView.findViewById(R.id.conferenciaimg);

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