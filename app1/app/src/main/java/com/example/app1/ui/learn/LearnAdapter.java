package com.example.app1.ui.learn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.R;
import com.example.app1.model.LearningMaterial;

import java.util.ArrayList;
import java.util.List;

public class LearnAdapter extends RecyclerView.Adapter<LearnAdapter.MaterialViewHolder> {

    public interface OnItemClickListener {
        void onClick(LearningMaterial material);
    }

    private List<LearningMaterial> data = new ArrayList<>();
    private OnItemClickListener listener;

    public LearnAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<LearningMaterial> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.learning_card, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MaterialViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        Button moreButton;

        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            description = itemView.findViewById(R.id.textDescription);
            moreButton = itemView.findViewById(R.id.buttonMore);
        }

        void bind(LearningMaterial material) {
            title.setText(material.getTitle());
            description.setText(material.getDescription());
            moreButton.setOnClickListener(v -> listener.onClick(material));
        }
    }
}
