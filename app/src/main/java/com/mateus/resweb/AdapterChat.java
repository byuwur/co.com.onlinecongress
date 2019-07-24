package com.mateus.resweb;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdapterChat extends ArrayAdapter<HolderChat> {

    Activity context;
    List<HolderChat> items;
    Integer[] imageId = {
            R.drawable.logo
    };

    public AdapterChat(Activity mainActivity, ArrayList<HolderChat> dataArrayHolderChat) {
        super(mainActivity, 0, dataArrayHolderChat);
        this.context = mainActivity;
        this.items = dataArrayHolderChat;
    }

    private class ViewHolder {
        TextView message, name;
        ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AdapterChat.ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(
                    R.layout.item_chat, parent, false);

            holder = new AdapterChat.ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.image = (ImageView) convertView.findViewById(R.id.image);

            convertView.setTag(holder);
        } else {
            holder = (AdapterChat.ViewHolder) convertView.getTag();
        }
        HolderChat productItems = items.get(position);
        holder.name.setText(productItems.getName());
        holder.message.setText(productItems.getMessage());
        holder.image.setImageResource(productItems.getImageId());
        return convertView;
    }
}