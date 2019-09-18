package com.byuwur.onlinecongress;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorNotif extends RecyclerView.Adapter<AdaptadorNotif.NotifViewHolder> {

    private ArrayList<HolderNotif> listanotif;
    private onItemClickListener mlistener;

    public AdaptadorNotif(ArrayList<HolderNotif> listanotif) {
        this.listanotif = listanotif;
    }

    public void setonItemClickListener(onItemClickListener listener) {
        mlistener = listener;
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, null, false);
        return new NotifViewHolder(view, mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotifViewHolder holder, int position) {
        holder.notifnombre.setText(listanotif.get(position).getNombre());
        holder.notifid.setText(listanotif.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return listanotif.size();
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public class NotifViewHolder extends RecyclerView.ViewHolder {
        TextView notifnombre, notifid;

        private NotifViewHolder(View itemView, final onItemClickListener listener) {
            super(itemView);
            notifnombre = itemView.findViewById(R.id.notiftext);
            notifid = itemView.findViewById(R.id.notiffecha);

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