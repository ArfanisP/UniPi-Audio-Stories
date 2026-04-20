package com.example.unipiaudiostories;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unipiaudiostories.adapter.StatsAdapter;
import com.example.unipiaudiostories.model.StatsRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Activity στατιστικών
public class StatsActivity extends AppCompatActivity {

    //Στοιχεία UI
    private TextView tvSummary;
    private RecyclerView rv;
    private StatsAdapter adapter;

    //Firebase Auth και Firestore
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        //Σύνδεση views και ρύθμιση RecyclerView
        tvSummary = findViewById(R.id.tvSummary);
        rv = findViewById(R.id.rvStats);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StatsAdapter();
        rv.setAdapter(adapter);

        //Αρχικοποίηση Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Φόρτωση στατιστικών
        loadStats();
    }

    //Υπολογίζει τα στατιστικά από Firestore
    private void loadStats() {
        // Αν δεν υπάρχει χρήστης εμφανίζεται μήνυμα σφάλματος
        if (auth.getCurrentUser() == null) {
            tvSummary.setText(getString(R.string.error_load));
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        //Ανάκτηση όλων των listens του χρήστη
        db.collection("users").document(uid).collection("listens")
                .get()
                .addOnSuccessListener(q -> {
                    //πόσες φορές ακούστηκε κάθε ιστορία
                    Map<String, Integer> counts = new HashMap<>();
                    int total = 0;

                    for (DocumentSnapshot d : q.getDocuments()) {
                        String storyId = d.getString("storyId");
                        if (storyId == null) continue;
                        counts.put(storyId, counts.getOrDefault(storyId, 0) + 1);
                        total++;
                    }

                    //Εμφάνιση συνολικού αριθμού ακροάσεων
                    tvSummary.setText("Total listens: " + total);

                    //Αν δεν υπάρχουν δεδομένα καθαρίζει τη λίστα
                    if (counts.isEmpty()) {
                        adapter.setItems(new ArrayList<StatsRow>());
                        return;
                    }

                    //Φέρνουμε τα story titles για να δείξουμε “όνομα” αντί για id
                    db.collection("stories").get()
                            .addOnSuccessListener(storiesQ -> {
                                Map<String, String> titles = new HashMap<>();
                                for (DocumentSnapshot sdoc : storiesQ.getDocuments()) {
                                    String id = sdoc.getId();
                                    String title = sdoc.getString("title");
                                    if (title != null) titles.put(id, title);
                                }

                                //Δημιουργία λίστας στατιστικών
                                List<StatsRow> rows = new ArrayList<>();
                                for (Map.Entry<String, Integer> e : counts.entrySet()) {
                                    String id = e.getKey();
                                    int c = e.getValue();
                                    String title = titles.getOrDefault(id, id);
                                    rows.add(new StatsRow(id, title, c));
                                }

                                //Ταξινόμηση με βάση τις περισσότερες ακροάσεις
                                rows.sort((a, b) -> Integer.compare(b.count, a.count));

                                adapter.setItems(rows);
                            })
                            .addOnFailureListener(e -> tvSummary.setText(getString(R.string.error_load)));
                })
                .addOnFailureListener(e -> tvSummary.setText(getString(R.string.error_load)));
    }
}