package com.example.app1.ui.profile;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app1.LoginActivity;
import com.example.app1.R;
import com.example.app1.ReminderReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Locale;


public class ProfileFragment extends Fragment {
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private int tempHour, tempMinute;

    private ProfileViewModel viewModel;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        viewModel.saveReminderTime(requireContext(), tempHour, tempMinute);
                        scheduleReminder(tempHour, tempMinute);
                        Toast.makeText(getContext(), "Emlékeztető beállítva!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Értesítések letiltva.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        TextView nicknameText = view.findViewById(R.id.nicknameTextView);
        TextView emailText = view.findViewById(R.id.emailTextView);
        TextView workoutsText = view.findViewById(R.id.workoutsCompletedTextView);
        TextView journalText = view.findViewById(R.id.journalEntriesTextView);
        Button settingsButton = view.findViewById(R.id.settingsButton);
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


        settingsButton.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(getContext())
                    .inflate(R.layout.dialog_settings, null);

            EditText editNickname = dialogView.findViewById(R.id.editNickname);
            EditText editEmail = dialogView.findViewById(R.id.editEmail);
            EditText editPassword = dialogView.findViewById(R.id.editPassword);

            TextView txtReminderTime = dialogView.findViewById(R.id.txtReminderTime);

            SharedPreferences prefs = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
            int savedHour = prefs.getInt("reminder_hour", -1);
            int savedMinute = prefs.getInt("reminder_minute", -1);

            if (savedHour != -1) {
                String formatted = String.format(Locale.getDefault(), "%02d:%02d", savedHour, savedMinute);
                txtReminderTime.setText("Emlékeztető: " + formatted);
            }

            txtReminderTime.setOnClickListener(v1 -> {
                TimePickerDialog timePicker = new TimePickerDialog(getContext(),
                        (dialogView2, hour, minute) -> {
                            tempHour = hour;
                            tempMinute = minute;

                            String formatted = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                            txtReminderTime.setText("Reminder time: " + formatted);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                    viewModel.saveReminderTime(requireContext(), tempHour, tempMinute);
                                    scheduleReminder(hour, minute);
                                } else {
                                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                                }
                            } else {
                                viewModel.saveReminderTime(requireContext(), tempHour, tempMinute);
                                scheduleReminder(hour, minute);
                            }
                        }, 18, 0, true);
                timePicker.show();
            });

            new AlertDialog.Builder(getContext())
                    .setTitle("Beállítások")
                    .setView(dialogView)
                    .setPositiveButton("Mentés", (dialog, which) -> {

                        String nickname = editNickname.getText().toString();
                        String email = editEmail.getText().toString();
                        String password = editPassword.getText().toString();

                        viewModel.updateProfile(nickname, email, password,
                                () -> Toast.makeText(getContext(), "Sikeres frissítés", Toast.LENGTH_SHORT).show(),
                                error -> Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
                        );
                        viewModel.loadUserData();
                    })
                    .setNegativeButton("Mégse", null)
                    .show();
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

    private void scheduleReminder(int hour, int minute) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(getContext(), ReminderReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager =
                (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

}