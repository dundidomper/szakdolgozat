package com.example.app1;

import android.os.Bundle;

import com.example.app1.model.Workout;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.app1.databinding.ActivityAmountWorkoutBinding;

public class AmountWorkoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_workout);

        Workout workout = (Workout) getIntent().getSerializableExtra("workout");

        TextView title = findViewById(R.id.titleText);
        TextView technique = findViewById(R.id.techniqueText);
        TextView desc = findViewById(R.id.descriptionText);
        Button finish = findViewById(R.id.finishButton);

        title.setText(workout.getTitle());
        technique.setText(workout.getTechnique());
        desc.setText(workout.getDescription());


        finish.setOnClickListener(v -> finish());
    }
}
