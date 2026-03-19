package com.example.app1.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.app1.R;
import com.example.app1.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private TextView textWelcome, textReady, textTree;
    private ImageView imageLevel;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        textWelcome = view.findViewById(R.id.textWelcome);
        imageLevel = view.findViewById(R.id.imageLevel);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("users").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nickname = documentSnapshot.getString("nickname");
                            Long workouts = documentSnapshot.getLong("finishedWorkouts");

                            if (nickname != null) {
                                textWelcome.setText("Üdv, " + nickname + "!");
                            }

                            if (workouts != null) {
                                if (workouts < 5) {
                                    imageLevel.setImageResource(R.drawable.beginner);
                                } else if (workouts < 15) {
                                    imageLevel.setImageResource(R.drawable.intermediate);
                                } else {
                                    imageLevel.setImageResource(R.drawable.advanced);
                                }
                            }
                        }
                    });
        }

        return view;
    }
}



//@Override
//public void onDestroyView() {
//    super.onDestroyView();
//    binding = null;
//}