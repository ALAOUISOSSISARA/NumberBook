package com.example.numberbook;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhoneEntryAdapter extends RecyclerView.Adapter<PhoneEntryAdapter.EntryViewHolder> {

    private List<PhoneEntry> entryList;

    public PhoneEntryAdapter(List<PhoneEntry> entryList) {
        this.entryList = entryList;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_phone_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        PhoneEntry entry = entryList.get(position);

        holder.tvEntryName .setText(entry.getFullName());
        holder.tvEntryPhone.setText(entry.getPhoneNumber());

        String initial = (entry.getFullName() != null && !entry.getFullName().isEmpty())
                ? String.valueOf(entry.getFullName().charAt(0)).toUpperCase() : "?";
        holder.tvInitial.setText(initial);

        // Click → open detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ContactDetailActivity.class);
            intent.putExtra(ContactDetailActivity.EXTRA_NAME,   entry.getFullName());
            intent.putExtra(ContactDetailActivity.EXTRA_PHONE,  entry.getPhoneNumber());
            intent.putExtra(ContactDetailActivity.EXTRA_SOURCE, entry.getEntrySource());
            intent.putExtra(ContactDetailActivity.EXTRA_DATE,   entry.getRecordedAt());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return entryList.size(); }

    public void refreshList(List<PhoneEntry> updatedList) {
        this.entryList = updatedList;
        notifyDataSetChanged();
    }

    static class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView tvEntryName, tvEntryPhone, tvInitial;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitial    = itemView.findViewById(R.id.tvInitial);
            tvEntryName  = itemView.findViewById(R.id.tvEntryName);
            tvEntryPhone = itemView.findViewById(R.id.tvEntryPhone);
        }
    }
}