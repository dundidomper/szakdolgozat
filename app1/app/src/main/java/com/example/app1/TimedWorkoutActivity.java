package com.example.app1;

import android.os.Bundle;

import com.example.app1.model.Workout;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.app1.databinding.ActivityTimedWorkoutBinding;

public class TimedWorkoutActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timed_workout);

        Workout workout = (Workout) getIntent().getSerializableExtra("workout");

        TextView title = findViewById(R.id.titleText);
        TextView technique = findViewById(R.id.techniqueText);
        TextView desc = findViewById(R.id.descriptionText);
        TextView timerText = findViewById(R.id.timerText);
        finishButton = findViewById(R.id.finishButton);

        finishButton.setEnabled(false);

        title.setText(workout.getTitle());
        technique.setText(workout.getTechnique());
        desc.setText(workout.getDescription());

        long duration = workout.getTarget() * 1000L;

        countDownTimer = new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                timerText.setText("Hátralévő idő: " + millisUntilFinished / 1000 + " mp");
            }

            public void onFinish() {
                timerText.setText("Kész!");
                finishButton.setEnabled(true);
            }
        }.start();

        finishButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
