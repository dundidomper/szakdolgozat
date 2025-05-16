package com.example.app1.ui.learn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.app1.R;
import com.example.app1.databinding.FragmentLearnBinding;


public class LearnFragment extends Fragment {

    private LearnViewModel viewModel;
    private RecyclerView recyclerView;
    private LearnAdapter adapter;
    private SharedPreferences sharedPrefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_learn, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sharedPrefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        recyclerView = view.findViewById(R.id.recyclerLearning);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LearnAdapter(material -> handleClick(material.getLink()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(LearnViewModel.class);
        viewModel.getMaterials().observe(getViewLifecycleOwner(), adapter::submitList);
    }

    private void handleClick(String link) {
        boolean skipWarning = sharedPrefs.getBoolean("skipLinkWarning", false);
        if (skipWarning) {
            openLink(link);
        } else {
            showWarningDialog(link);
        }
    }

    private void showWarningDialog(String link) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_link_warning, null);
        CheckBox checkbox = dialogView.findViewById(R.id.checkbox_skip_warning);

        new AlertDialog.Builder(requireContext())
                .setTitle("Figyelem")
                .setMessage("Elhagyod az alkalmazást.")
                .setView(dialogView)
                .setPositiveButton("Tovább", (dialog, which) -> {
                    if (checkbox.isChecked()) {
                        sharedPrefs.edit().putBoolean("skipLinkWarning", true).apply();
                    }
                    openLink(link);
                })
                .setNegativeButton("Mégse", null)
                .show();
    }

    private void openLink(String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(intent);
    }
}
