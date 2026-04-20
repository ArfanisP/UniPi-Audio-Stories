package com.example.unipiaudiostories.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.unipiaudiostories.R;
import com.example.unipiaudiostories.model.Story;

import java.util.ArrayList;
import java.util.List;

//Adapter για το RecyclerView ιστοριών
public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.VH> {

    //Interface για click σε μία ιστορία
    public interface OnStoryClick {
        void onClick(Story story);
    }

    //Λίστα με ιστορίες που θα εμφανιστούν
    private final List<Story> items = new ArrayList<>();
    //Listener που ενημερώνεται όταν ο χρήστης πατήσει μια ιστορία
    private final OnStoryClick listener;

    //Constructor που λαμβάνει το click listener από το Activity
    public StoryAdapter(OnStoryClick listener) {
        this.listener = listener;
    }

    //Ενημερώνει τη λίστα ιστοριών και κάνει refresh το RecyclerView
    public void setItems(List<Story> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    //Δημιουργία ViewHolder για κάθε γραμμή της λίστας
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_story, parent, false);
        return new VH(v);
    }

    //Connection δεδομένων της ιστορίας στα στοιχεία της γραμμής
    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Story s = items.get(position);
        h.tvTitle.setText(s.getTitle());
        h.tvMeta.setText(s.getAuthor() + " • " + s.getYear());

        //Φόρτωση εικόνας ιστορίας αν υπάρχει URL
        if (s.getImageUrl() != null && !s.getImageUrl().trim().isEmpty()) {
            Glide.with(h.itemView.getContext()).load(s.getImageUrl()).into(h.img);
        } else {
            h.img.setImageDrawable(null);
        }

        h.itemView.setOnClickListener(v -> listener.onClick(s));
    }

    //Επιστρέφει πόσες ιστορίες υπάρχουν στη λίστα
    @Override
    public int getItemCount() { return items.size(); }

    //ViewHolder που κρατά references στα views κάθε item
    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvTitle, tvMeta;

        VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgThumb);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvMeta = itemView.findViewById(R.id.tvItemMeta);
        }
    }
}