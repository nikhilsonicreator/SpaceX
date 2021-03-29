package com.entwickler.spacex.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.entwickler.spacex.Model.PersonClass;
import com.entwickler.spacex.R;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.MyHolder> {

    private Context context;
    private List<PersonClass> person_list;

    public PersonAdapter(Context context, List<PersonClass> person_list) {
        this.context = context;
        this.person_list = person_list;
    }

    @NonNull
    @Override
    public PersonAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_layout, parent, false);

        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonAdapter.MyHolder holder, int position) {

        PersonClass personClass = person_list.get(position);
        holder.person_status_txt.setText(personClass.getStatus());
        holder.person_name_txt.setText(personClass.getName());
        holder.person_agency_txt.setText(personClass.getAgency());
        holder.person_wiki_txt.setText(personClass.getWikipedia());

        byte[] img = personClass.getImg();
        Bitmap map = BitmapFactory.decodeByteArray(img, 0, img.length);
        Glide.with(context).load(map).into(holder.person_image);

    }

    @Override
    public int getItemCount() {
        return person_list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        TextView person_name_txt, person_wiki_txt, person_status_txt, person_agency_txt;
        ImageView person_image;

        public MyHolder(View itemView) {
            super(itemView);

            person_agency_txt = itemView.findViewById(R.id.person_agency_txt);
            person_name_txt = itemView.findViewById(R.id.person_name_txt);
            person_wiki_txt = itemView.findViewById(R.id.person_wiki_txt);
            person_status_txt = itemView.findViewById(R.id.person_status_txt);
            person_image = itemView.findViewById(R.id.person_image);
        }
    }

}
