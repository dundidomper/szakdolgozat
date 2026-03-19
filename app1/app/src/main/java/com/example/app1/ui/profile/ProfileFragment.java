package com.example.app1.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app1.LoginActivity;
import com.example.app1.R;


public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        TextView nicknameText = view.findViewById(R.id.nicknameTextView);
        TextView emailText = view.findViewById(R.id.emailTextView);
        TextView workoutsText = view.findViewById(R.id.workoutsCompletedTextView);
        TextView journalText = view.findViewById(R.id.journalEntriesTextView);
        Button logoutButton = view.findViewById(R.id.logoutButton);
        Button deleteButton = view.findViewById(R.id.deleteProfileButton);

        viewModel.getNickname().observe(getViewLifecycleOwner(), nickname -> {
            if (nickname != null) nicknameText.setText(nickname);
        });

        viewModel.getEmail().observe(getViewLifecycleOwner(), email -> {
            if (email != null) emailText.setText(email);
        });

        viewModel.getWorkouts().observe(getViewLifecycleOwner(), count -> {
            workoutsText.setText("Befejezett edzések: " + count);
        });

        viewModel.getDiaries().observe(getViewLifecycleOwner(), count -> {
            journalText.setText("Naplóbejegyzések: " + count);
        });

        logoutButton.setOnClickListener(v -> {
            viewModel.logout();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Fiók törlése")
                    .setMessage("Biztosan törölni szeretnéd a profilodat?")
                    .setPositiveButton("Igen", (dialog, which) -> {
                        viewModel.deleteAccount(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(), "Fiók törölve", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(requireContext(), LoginActivity.class));
                                requireActivity().finish();
                            } else {
                                Toast.makeText(requireContext(), "Nem sikerült törölni a fiókot", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Mégse", null)
                    .show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadUserData();
    }

}



//@Override
//public void onDestroyView() {
//    super.onDestroyView();
//    binding = null;
//}