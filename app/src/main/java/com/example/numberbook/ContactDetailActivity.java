package com.example.numberbook;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NAME   = "extra_name";
    public static final String EXTRA_PHONE  = "extra_phone";
    public static final String EXTRA_SOURCE = "extra_source";
    public static final String EXTRA_DATE   = "extra_date";

    private TextView    tvDetailInitial, tvDetailName, tvDetailPhone;
    private TextView    tvDetailSource, tvDetailDate;
    private TextView    tvDuplicateNames;
    private LinearLayout cardDuplicates;

    private NumberBookApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        bindViews();
        api = ApiClient.getApi();

        // Get data from Intent
        String name   = getIntent().getStringExtra(EXTRA_NAME);
        String phone  = getIntent().getStringExtra(EXTRA_PHONE);
        String source = getIntent().getStringExtra(EXTRA_SOURCE);
        String date   = getIntent().getStringExtra(EXTRA_DATE);

        // Fill UI
        String initial = (name != null && !name.isEmpty())
                ? String.valueOf(name.charAt(0)).toUpperCase() : "?";

        tvDetailInitial.setText(initial);
        tvDetailName   .setText(name);
        tvDetailPhone  .setText(phone);
        tvDetailSource .setText("Source : " + (source != null ? source : "unknown"));
        tvDetailDate   .setText("Synced : " + (date != null ? date : "—"));

        // Check for shared numbers
        checkSharedNumber(phone, name);
    }

    private void bindViews() {
        tvDetailInitial  = findViewById(R.id.tvDetailInitial);
        tvDetailName     = findViewById(R.id.tvDetailName);
        tvDetailPhone    = findViewById(R.id.tvDetailPhone);
        tvDetailSource   = findViewById(R.id.tvDetailSource);
        tvDetailDate     = findViewById(R.id.tvDetailDate);
        tvDuplicateNames = findViewById(R.id.tvDuplicateNames);
        cardDuplicates   = findViewById(R.id.cardDuplicates);
    }

    private void checkSharedNumber(String phone, String currentName) {
        if (phone == null) return;

        api.lookupEntries(phone).enqueue(new Callback<List<PhoneEntry>>() {
            @Override
            public void onResponse(@NonNull Call<List<PhoneEntry>> call,
                                   @NonNull Response<List<PhoneEntry>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> otherNames = new ArrayList<>();
                    for (PhoneEntry entry : response.body()) {
                        if (!entry.getFullName().equals(currentName)) {
                            otherNames.add("• " + entry.getFullName());
                        }
                    }
                    if (!otherNames.isEmpty()) {
                        cardDuplicates.setVisibility(View.VISIBLE);
                        tvDuplicateNames.setText(
                                "Same number also used by :\n" +
                                        String.join("\n", otherNames)
                        );
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PhoneEntry>> call,
                                  @NonNull Throwable t) {
                // Silently fail — duplicate check is non-critical
            }
        });
    }
}