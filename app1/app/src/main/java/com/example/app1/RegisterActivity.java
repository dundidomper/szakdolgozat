package com.example.app1;

import android.content.Intent;
import android.os.Bundle;

import com.example.app1.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.registerButton.setOnClickListener(view -> {
            String email = binding.emailEditText.getText().toString().trim();
            String nickname = binding.nicknameEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString();
            String confirmPassword = binding.confirmPasswordEditText.getText().toString();

            if (email.isEmpty() || nickname.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Minden mező kitöltése kötelező!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length()<6) {
                Toast.makeText(this, "A jelszónak legalább 6 karakter hosszúnak kell lennie!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length()>4096) {
                Toast.makeText(this, "A jelszó túl hosszú!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "A jelszavak nem egyeznek!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String uid = mAuth.getCurrentUser().getUid();

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("nickname", nickname);
                        userData.put("email", email);
                        userData.put("finishedWorkouts", 0);
                        userData.put("diaryEntries", 0);
                        userData.put("lastWorkoutDate", "");

                        db.collection("users").document(uid)
                                .set(userData)
                                        .addOnSuccessListener(aVoid -> Log.d("REG", "Sikeres mentés"))
                                        .addOnFailureListener(e -> Log.w("REG", "Hiba a mentés során", e));

                        Toast.makeText(RegisterActivity.this, "Sikeres regisztráció. Üdv " + nickname + "!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció. Hiba: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
}
