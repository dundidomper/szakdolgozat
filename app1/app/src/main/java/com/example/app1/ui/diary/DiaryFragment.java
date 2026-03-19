package com.example.app1.ui.diary;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.app1.R;
import com.example.app1.databinding.FragmentDiaryBinding;
import com.example.app1.model.DiaryEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class DiaryFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddEntry;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private DiaryAdapter adapter;
    private List<DiaryEntry> entries = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewDiary);
        fabAddEntry = view.findViewById(R.id.fabAddEntry);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DiaryAdapter(entries);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadEntries();

        fabAddEntry.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewDiaryEntryActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEntries();
    }

    private void loadEntries() {
        String uid = auth.getCurrentUser().getUid();
        db.collection("diaryEntries")
                .whereEqualTo("userId", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    entries.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        DiaryEntry entry = doc.toObject(DiaryEntry.class);
                        entry.setId(doc.getId());
                        entries.add(entry);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
