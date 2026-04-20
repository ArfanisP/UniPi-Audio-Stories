package com.example.unipiaudiostories;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;

import com.example.unipiaudiostories.adapter.StoryAdapter;
import com.example.unipiaudiostories.model.Story;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

//Κεντρική οθόνη
public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private StoryAdapter adapter;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Αρχικοποίηση Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Ρύθμιση RecyclerView
        rv = findViewById(R.id.rvStories);
        rv.setLayoutManager(new LinearLayoutManager(this));
        //Με click σε ιστορία ανοίγει το StoryActivity
        adapter = new StoryAdapter(story -> {
            Intent i = new Intent(MainActivity.this, StoryActivity.class);
            i.putExtra("storyId", story.getId());
            startActivity(i);
        });
        rv.setAdapter(adapter);

        //Άνοιγμα οθόνης στατιστικών
        findViewById(R.id.btnOpenStats).setOnClickListener(v ->
                startActivity(new Intent(this, StatsActivity.class)));

        //Έλεγχος ότι υπάρχει χρήστης
        ensureAuthThenLoad();
    }

    //Κάνει anonymous login και φορτώνει
    private void ensureAuthThenLoad() {
        if (auth.getCurrentUser() != null) {
            loadStories();
            return;
        }

        auth.signInAnonymously()
                .addOnSuccessListener(r -> loadStories())
                .addOnFailureListener(e -> {
                    Toast.makeText(this, getString(R.string.error_load), Toast.LENGTH_LONG).show();
                });
    }

    //Φέρνει τις ιστορίες και ενημερώνει τον adapter
    private void loadStories() {
        db.collection("stories")
                .get()
                .addOnSuccessListener(q -> {
                    List<Story> list = new ArrayList<>();
                    // Μετατροπή κάθε document του Firestore σε αντικείμενο Story
                    for (DocumentSnapshot doc : q.getDocuments()) {
                        Story s = doc.toObject(Story.class);
                        if (s != null) {
                            s.setId(doc.getId());
                            list.add(s);
                        }
                    }
                    adapter.setItems(list);
                })
                .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.error_load), Toast.LENGTH_LONG).show());
    }
}