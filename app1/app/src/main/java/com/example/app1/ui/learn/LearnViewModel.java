package com.example.app1.ui.learn;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.app1.model.LearningMaterial;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LearnViewModel extends ViewModel {

    private MutableLiveData<List<LearningMaterial>> materialsLiveData = new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LearnViewModel() {
        loadMaterials();
    }

    public LiveData<List<LearningMaterial>> getMaterials() {
        return materialsLiveData;
    }

    private void loadMaterials() {
        db.collection("learningMaterial")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<LearningMaterial> materials = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        LearningMaterial material = doc.toObject(LearningMaterial.class);
                        if (material != null) {
                            material.setId(doc.getId());
                            materials.add(material);
                        }
                    }
                    materialsLiveData.setValue(materials);
                })
                .addOnFailureListener(e -> {
                    materialsLiveData.setValue(new ArrayList<>());
                });
    }
}
