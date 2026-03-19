package com.example.app1.ui.diary;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.R;
import com.example.app1.model.DiaryEntry;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {

    private List<DiaryEntry> entryList;
    private FirebaseFirestore db;
    private String userId;


    public DiaryAdapter(List<DiaryEntry> entryList) {
        this.entryList = entryList;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_diary_entry, parent, false);
        return new DiaryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        DiaryEntry entry = entryList.get(position);
        holder.textView.setText(entry.getText());
        String date = DateFormat.getDateTimeInstance().format(new Date(entry.getTimestamp()));
        holder.dateView.setText(date);
        db = FirebaseFirestore.getInstance();
        userId = entry.getUserId();

        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Bejegyzés törlése")
                    .setMessage("Biztosan törlöd ezt a bejegyzést?")
                    .setPositiveButton("Igen", (dialog, which) -> {
                        db.collection("diaryEntries")
                                .document(entry.getId())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    int currentPosition = holder.getAdapterPosition();
                                    if (currentPosition != RecyclerView.NO_POSITION) {
                                        entryList.remove(currentPosition);
                                    notifyItemRemoved(position);
                                    Toast.makeText(holder.itemView.getContext(),
                                            "Bejegyzés törölve", Toast.LENGTH_SHORT).show();
                                    }
                                    db.collection("users")
                                            .document(userId)
                                            .update("diaryEntries", FieldValue.increment(-1))
                                            .addOnSuccessListener(aVoid -> Log.d("UPDATE", "Naplószám frissült"))
                                            .addOnFailureListener(e -> Log.w("UPDATE", "Naplószám frissítés sikertelen: ", e));

                                });
                    })
                    .setNegativeButton("Mégse", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

    static class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView textView, dateView;
        ImageButton deleteButton;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textContent);
            dateView = itemView.findViewById(R.id.textDate);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
