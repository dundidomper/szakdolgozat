package com.example.app1.ui.diary;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.app1.databinding.ActivityNewDiaryEntryBinding;

import com.example.app1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewDiaryEntryActivity extends AppCompatActivity {

    private EditText editText;
    private Button btnSave, btnCancel;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_diary_entry);



        editText = findViewById(R.id.editTextEntry);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnSave.setOnClickListener(v -> {
            String text = editText.getText().toString().trim();
            if (!text.isEmpty()) {

                String userId = auth.getCurrentUser().getUid();
                userRef = db.collection("users").document(userId);
                long timestamp = System.currentTimeMillis();

                userRef.update("diaryEntries", FieldValue.increment(1))
                        .addOnSuccessListener(aVoid -> Log.d("UPDATE", "Naplószám frissült"))
                        .addOnFailureListener(e -> Log.w("UPDATE", "Naplószám frissítés sikertelen: ", e));

                Map<String, Object> entry = new HashMap<>();
                entry.put("userId", userId);
                entry.put("text", text);
                entry.put("timestamp", timestamp);

                db.collection("diaryEntries")
                        .add(entry)
                        .addOnSuccessListener(documentReference -> finish());
            }
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}
