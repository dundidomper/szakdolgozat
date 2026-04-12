package com.example.app1.ui.training;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app1.AmountWorkoutActivity;
import com.example.app1.R;
import com.example.app1.TimedWorkoutActivity;
import com.example.app1.databinding.FragmentTrainingBinding;
import com.example.app1.model.Workout;


public class TrainingFragment extends Fragment {

    private TrainingViewModel viewModel;

    public TrainingFragment() {
        super(R.layout.fragment_training);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("TrainingFragment", "onViewCreated called");
        super.onViewCreated(view, savedInstanceState);



        viewModel = new ViewModelProvider(this).get(TrainingViewModel.class);

        TextView titleText = view.findViewById(R.id.titleText);
        TextView dayText = view.findViewById(R.id.dayText);
        TextView descriptionText = view.findViewById(R.id.descriptionText);
        Button startButton = view.findViewById(R.id.startButton);
        LinearLayout completedContainer = view.findViewById(R.id.completedContainer);

        viewModel.getNextWorkout().observe(getViewLifecycleOwner(), workout -> {
            if (workout != null) {
                titleText.setText(workout.getTitle());
                dayText.setText(workout.getDay() + ". nap");
                descriptionText.setText(workout.getDescription());
            }
        });

        viewModel.canStartToday().observe(getViewLifecycleOwner(), canStart -> {
            startButton.setEnabled(canStart);
            startButton.setAlpha(canStart ? 1.0f : 0.5f);
        });

        startButton.setOnClickListener(v -> {
            Workout workout = viewModel.getNextWorkout().getValue();
            if (workout != null) {
                Intent intent;
                if ("amount".equals(workout.getType())) {
                    intent = new Intent(requireContext(), AmountWorkoutActivity.class);
                } else if ("timed".equals(workout.getType())) {
                    intent = new Intent(requireContext(), TimedWorkoutActivity.class);
                } else {
                    Toast.makeText(requireContext(), "Ismeretlen edzéstípus", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("workout", workout);
                startActivity(intent);
                }
        });

        viewModel.getCompletedWorkouts().observe(getViewLifecycleOwner(), workouts -> {
            completedContainer.removeAllViews();
            for (Workout w : workouts) {
                View card = LayoutInflater.from(requireContext())
                        .inflate(R.layout.completed_workout_card, completedContainer, false);
                ((TextView) card.findViewById(R.id.title)).setText(w.getTitle());
                ((TextView) card.findViewById(R.id.day)).setText(w.getDay() + ". nap");
                ((TextView) card.findViewById(R.id.description)).setText(w.getDescription());
                completedContainer.addView(card);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TrainingFragment", "onResume called - Refreshing data!");

        if (viewModel != null) {
            viewModel.loadWorkouts();
        }
    }
}
