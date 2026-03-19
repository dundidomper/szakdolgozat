package com.example.app1.ui.profile;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileViewModel extends ViewModel {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<String> nickname = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<Integer> workouts = new MutableLiveData<>();
    private final MutableLiveData<Integer> diaries = new MutableLiveData<>();

    public ProfileViewModel() {
        loadUserData();
    }

    public void loadUserData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            email.setValue(user.getEmail());

            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        nickname.setValue(documentSnapshot.getString("nickname"));
                        Long workoutCount = documentSnapshot.getLong("finishedWorkouts");
                        Long diaryCount = documentSnapshot.getLong("diaryEntries");

                        workouts.setValue(workoutCount != null ? workoutCount.intValue() : 0);
                        diaries.setValue(diaryCount != null ? diaryCount.intValue() : 0);
                    });
        }
    }

    public LiveData<String> getNickname() { return nickname; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<Integer> getWorkouts() { return workouts; }
    public LiveData<Integer> getDiaries() { return diaries; }

    public void logout() {
        auth.signOut();
    }

    public void deleteAccount(OnCompleteListener<Void> onCompleteListener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            db.collection("users").document(uid)
                    .delete()
                    .addOnCompleteListener(unused -> {
                        user.delete().addOnCompleteListener(onCompleteListener);
                    });
        }
    }

}