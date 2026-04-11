package com.example.app1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.app1.model.Workout;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.app1.databinding.ActivityAmountWorkoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AmountWorkoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_workout);

        Workout workout = (Workout) getIntent().getSerializableExtra("workout");

        TextView title = findViewById(R.id.titleText);
        TextView technique = findViewById(R.id.techniqueText);
        TextView desc = findViewById(R.id.descriptionText);
        Button finishButton = findViewById(R.id.finishButton);

        title.setText(workout.getTitle());
        technique.setText(workout.getTechnique());
        desc.setText(workout.getDescription());


        finishButton.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();

                SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String today = sdf.format(new Date());
                prefs.edit().putString("lastWorkoutDate_" + uid, today).apply();

                Log.d("WORKOUT", "Workout finished! Local date saved: " + today + " user: " + uid);
            }

            saveWorkoutToDatabase();

            Toast.makeText(this, "Edzés befejezve! Szép munka!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void saveWorkoutToDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String today = sdf.format(new Date());

            db.collection("users").document(uid).update(
                    "finishedWorkouts", FieldValue.increment(1),
                    "lastWorkoutDate", today
            ).addOnSuccessListener(unused -> {
                Log.d("Firestore", "Firebase stats updated successfully!");

            }).addOnFailureListener(e -> {
                Log.e("Firestore", "Error updating Firebase stats", e);
            });
        }
    }
}
