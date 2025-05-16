package com.example.app1.ui.training;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.app1.model.Workout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TrainingViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private final MutableLiveData<Workout> nextWorkout = new MutableLiveData<>();
    private final MutableLiveData<List<Workout>> completedWorkouts = new MutableLiveData<>();
    private final MutableLiveData<Boolean> canStartToday = new MutableLiveData<>(true);

    public TrainingViewModel() {
        loadWorkouts();
    }

    public void loadWorkouts() {
        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid).get().addOnSuccessListener(userSnap -> {
            Long finishedCount = userSnap.getLong("finishedWorkouts");
            String lastWorkoutDate = userSnap.getString("lastWorkoutDate");

            int finished = finishedCount != null ? finishedCount.intValue() : 0;
            String today = getTodayDateString();

            canStartToday.setValue(lastWorkoutDate == null || !lastWorkoutDate.equals(today));

            // Következő edzés
            db.collection("workouts").document(String.valueOf(finished)).get().addOnSuccessListener(doc -> {
                Workout workout = doc.toObject(Workout.class);
                if (workout != null) {
                    workout.setDay(finished + 1);
                    nextWorkout.setValue(workout);
                }
            });

            // Befejezett edzések
            List<Workout> completed = new ArrayList<>();
            for (int i = 0; i < finished; i++) {
                int index = i;
                db.collection("workouts").document(String.valueOf(index)).get().addOnSuccessListener(doc -> {
                    Workout w = doc.toObject(Workout.class);
                    if (w != null) {
                        w.setDay(index + 1);
                        completed.add(w);
                        if (completed.size() == finished) {
                            completedWorkouts.setValue(completed);
                        }
                    }
                });
            }
        });
    }

    public LiveData<Workout> getNextWorkout() { return nextWorkout; }
    public LiveData<List<Workout>> getCompletedWorkouts() { return completedWorkouts; }
    public LiveData<Boolean> canStartToday() { return canStartToday; }

    public void markWorkoutAsFinished() {
        String uid = auth.getCurrentUser().getUid();
        String today = getTodayDateString();

        db.collection("users").document(uid).update(
                "finishedWorkouts", FieldValue.increment(1),
                "lastWorkoutDate", today
        ).addOnSuccessListener(unused -> loadWorkouts());
    }

    private String getTodayDateString() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}
