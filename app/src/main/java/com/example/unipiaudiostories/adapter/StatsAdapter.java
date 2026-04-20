package com.example.unipiaudiostories.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.unipiaudiostories.R;
import com.example.unipiaudiostories.model.StatsRow;

import java.util.ArrayList;
import java.util.List;

//Adapter για το RecyclerView των στατιστικών
public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.VH> {
    //Λίστα με τα δεδομένα στατιστικών
    private final List<StatsRow> items = new ArrayList<>();

    //Ενημερώνει τη λίστα και κάνει refresh το RecyclerView
    public void setItems(List<StatsRow> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    //Δημιουργία ViewHolder για κάθε γραμμή του RecyclerView
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stat, parent, false);
        return new VH(v);
    }

    //Δέσιμο δεδομένων στο ViewHolder
    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        StatsRow r = items.get(position);
        h.tvTitle.setText(r.title);
        h.tvCount.setText(String.valueOf(r.count));
    }

    //Επιστρέφει πόσα στοιχεία υπάρχουν στη λίστα
    @Override
    public int getItemCount() { return items.size(); }

    //ViewHolder που κρατά references στα views κάθε γραμμής
    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCount;
        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvStatTitle);
            tvCount = itemView.findViewById(R.id.tvStatCount);
        }
    }
}
