package com.example.app1.ui.learn;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.app1.model.LearningMaterial;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LearnViewModel extends ViewModel {

    private MutableLiveData<List<LearningMaterial>> materialsLiveData = new MutableLiveData<>();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LearnViewModel() {
        loadMaterials();
    }

    public LiveData<List<LearningMaterial>> getMaterials() {
        return materialsLiveData;
    }

    private void loadMaterials() {

        if (auth.getCurrentUser() == null) {
            materialsLiveData.setValue(new ArrayList<>());
            return;
        }

        String uid = auth.getCurrentUser().getUid();



        db.collection("users").document(uid).get().addOnSuccessListener(userSnap -> {

            Long finishedCount = userSnap.getLong("finishedWorkouts");
            int finished = finishedCount != null ? finishedCount.intValue() : 0;
            int availableMaterials = finished / 3 + 1;

            db.collection("learningMaterial")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {

                        List<LearningMaterial> allMaterials = new ArrayList<>();

                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            LearningMaterial material = doc.toObject(LearningMaterial.class);
                            if (material != null) {
                                material.setId(doc.getId());
                                allMaterials.add(material);
                            }
                        }

                        List<LearningMaterial> visibleMaterials =
                                allMaterials.subList(
                                        0,
                                        Math.min(availableMaterials, allMaterials.size())
                                );

                        materialsLiveData.setValue(visibleMaterials);
                    })
                    .addOnFailureListener(e -> {
                        materialsLiveData.setValue(new ArrayList<>());
                    });
        });
    }
}
