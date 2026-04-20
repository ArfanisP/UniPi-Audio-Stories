package com.example.unipiaudiostories;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.unipiaudiostories.model.Story;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//Activity προβολής ιστορίας
public class StoryActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    //UI
    private TextView tvTitle, tvMeta, tvText;
    private ImageView img;
    private Button btnPlay, btnStop;

    //slider έντασης ήχου
    private SeekBar seekVolume;
    private AudioManager audioManager;

    //Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    //tts
    private TextToSpeech tts;
    private boolean ttsReady = false;

    private String storyId = "";
    private String storyText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        //Σύνδεση των views από το layout
        tvTitle = findViewById(R.id.tvStoryTitle);
        tvMeta  = findViewById(R.id.tvStoryMeta);
        tvText  = findViewById(R.id.tvStoryText);
        img     = findViewById(R.id.imgStory);
        btnPlay = findViewById(R.id.btnPlay);
        btnStop = findViewById(R.id.btnStop);

        seekVolume = findViewById(R.id.seekVolume);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        //Ρύθμιση slider
        if (audioManager != null) {
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            seekVolume.setMax(maxVolume);
            seekVolume.setProgress(currentVolume);

            seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });
        }

        //Αρχικοποίηση Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Λήψη storyId από το intent
        storyId = getIntent().getStringExtra("storyId");
        if (storyId == null) storyId = "";

        //Δημιουργία αντικειμένου tts
        tts = new TextToSpeech(this, this);

        //Κουμπιά play και stop
        btnPlay.setOnClickListener(v -> speak());
        btnStop.setOnClickListener(v -> stopSpeak());

        //Μετάβαση στα στατιστικα
        findViewById(R.id.btnStatsFromStory).setOnClickListener(v ->
                startActivity(new Intent(this, StatsActivity.class)));

        //Φόρτωση δεδομένων ιστορίας
        loadStory(storyId);
    }

    //Φέρνει τα στοιχεία της ιστορίας
    private void loadStory(String id) {
        if (id.trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.error_load), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        db.collection("stories").document(id)
                .get()
                .addOnSuccessListener(doc -> {
                    // Μετατροπή document σε αντικείμενο Story
                    Story s = doc.toObject(Story.class);
                    if (s == null) {
                        Toast.makeText(this, getString(R.string.error_load), Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    tvTitle.setText(s.getTitle());
                    tvMeta.setText(s.getAuthor() + " • " + s.getYear());
                    tvText.setText(s.getText());

                    storyText = (s.getText() != null) ? s.getText() : "";

                    if (s.getImageUrl() != null && !s.getImageUrl().trim().isEmpty()) {
                        Glide.with(this).load(s.getImageUrl()).into(img);
                    } else {
                        img.setImageDrawable(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, getString(R.string.error_load), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    //Αρχικοποίηση tts
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int res = tts.setLanguage(Locale.UK);
            ttsReady = (res != TextToSpeech.LANG_MISSING_DATA &&
                    res != TextToSpeech.LANG_NOT_SUPPORTED);
        }
    }

    //Ξεκινά την ιστορia και καταγράφει listen event
    private void speak() {
        if (!ttsReady) return;
        if (storyText.trim().isEmpty()) return;

        tts.speak(storyText, TextToSpeech.QUEUE_FLUSH, null, "STORY_UTT");

        logListen(storyId);
    }

    //Διακόπτει την εκφώνηση
    private void stopSpeak() {
        if (tts != null) tts.stop();
    }

    //Καταγραφή ότι ο χρήστης άκουσε την ιστορία
    private void logListen(String storyId) {
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();
        Map<String, Object> data = new HashMap<>();
        data.put("storyId", storyId);
        data.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users")
                .document(uid)
                .collection("listens")
                .add(data);
    }

    //Απελευθέρωση πόρων όταν κλείνει το Activity
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}