package com.example.lostandfoundapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdvertAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Advert> advertList;

    public AdvertAdapter(Context context, ArrayList<Advert> advertList) {
        this.context = context;
        this.advertList = advertList;
    }

    @Override
    public int getCount() {
        return advertList.size();
    }

    @Override
    public Object getItem(int position) {
        return advertList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return advertList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_advert, parent, false);
        }

        ImageView rowImage = convertView.findViewById(R.id.rowImage);
        TextView rowTitle = convertView.findViewById(R.id.rowTitle);
        TextView rowDescription = convertView.findViewById(R.id.rowDescription);
        TextView rowDate = convertView.findViewById(R.id.rowDate);

        Advert advert = advertList.get(position);

        rowTitle.setText(advert.getPostType() + " - " + advert.getCategory());
        rowDescription.setText(advert.getName() + ": " + advert.getDescription());
        rowDate.setText("Posted: " + advert.getCreatedAt());

        try {
            rowImage.setImageURI(Uri.parse(advert.getImageUri()));
        } catch (Exception e) {
            rowImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        return convertView;
    }
}