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

public class AdaptadorAgendado extends RecyclerView.Adapter<AdaptadorAgendado.CongresoViewHolder> {

    private ArrayList<HolderAgendado> listaagendado;
    private onItemClickListener mlistener;

    public interface onItemClickListener{
        void onItemClick (int position);
    }

    public void setonItemClickListener(onItemClickListener listener){
        mlistener = listener;
    }

    public AdaptadorAgendado(ArrayList<HolderAgendado> listaagendado){
        this.listaagendado = listaagendado;
    }

    @NonNull
    @Override
    public CongresoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_agendado,null,false);
        return new CongresoViewHolder(view, mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull final CongresoViewHolder holder, int position) {
        holder.agendadonombre.setText(listaagendado.get(position).getNombre());
        holder.agendadoid.setText(listaagendado.get(position).getId());
        holder.agendadovalor.setText(listaagendado.get(position).getValor());
        holder.agendadoidioma.setText(listaagendado.get(position).getDireccion());
        holder.agendadocategoria.setText(listaagendado.get(position).getCiudad());
        holder.agendadodias.setText(listaagendado.get(position).getDias());
        holder.agendadohorario.setText(listaagendado.get(position).getHorario());
        //load image
        Picasso.get().load(listaagendado.get(position).getImg())
                //.networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                .fit().centerCrop()
                .into(holder.agendadoimg, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("Carga","Cargada");
            }
            @Override
            public void onError(Exception e) {
                Log.d("Carga","Error al cargar");
                holder.agendadoimg.setImageResource(R.drawable.no_image);
                //Toast.makeText(ctx, "Ocurrieron errores al cargar algunas imágenes.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaagendado.size();
    }

    public class CongresoViewHolder extends RecyclerView.ViewHolder {
        TextView agendadonombre,agendadoid, agendadovalor,agendadoidioma,agendadocategoria,agendadodias,agendadohorario;
        ImageView agendadoimg;

        private CongresoViewHolder(View itemView, final onItemClickListener listener) {
            super(itemView);
            agendadonombre= itemView.findViewById(R.id.agendadonombre);
            agendadoid = itemView.findViewById(R.id.agendadoid);
            agendadovalor = itemView.findViewById(R.id.agendadovalor);
            agendadoidioma= itemView.findViewById(R.id.agendadoidioma);
            agendadocategoria= itemView.findViewById(R.id.agendadocategoria);
            agendadodias= itemView.findViewById(R.id.agendadodias);
            agendadohorario= itemView.findViewById(R.id.agendadohorario);
            agendadoimg= itemView .findViewById(R.id.agendadoimg);

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