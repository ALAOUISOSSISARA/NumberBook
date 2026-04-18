package com.example.numberbook;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button            btnFetchContacts, btnPushToServer;
    private TextInputEditText inputKeyword;
    private TextView          tvStatus;
    private ProgressBar       progressBar;
    private RecyclerView      rvPhoneBook;

    private PhoneEntryAdapter      entryAdapter;
    private final List<PhoneEntry> phoneEntries    = new ArrayList<>();
    private final List<PhoneEntry> filteredEntries = new ArrayList<>();

    private NumberBookApi api;

    // Debounce
    private final Handler  searchHandler  = new Handler(Looper.getMainLooper());
    private       Runnable searchRunnable;

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        setupRecyclerView();
        setupSmartSearch();

        api = ApiClient.getApi();

        btnFetchContacts.setOnClickListener(v -> checkPermissionThenLoad());
        btnPushToServer .setOnClickListener(v -> pushEntriesToServer());
    }

    // ─── View Binding ─────────────────────────────────────────────────────────

    private void bindViews() {
        btnFetchContacts = findViewById(R.id.btnFetchContacts);
        btnPushToServer  = findViewById(R.id.btnPushToServer);
        inputKeyword     = findViewById(R.id.inputKeyword);
        tvStatus         = findViewById(R.id.tvStatus);
        progressBar      = findViewById(R.id.progressBar);
        rvPhoneBook      = findViewById(R.id.rvPhoneBook);
    }

    private void setupRecyclerView() {
        rvPhoneBook.setLayoutManager(new LinearLayoutManager(this));
        entryAdapter = new PhoneEntryAdapter(filteredEntries);
        rvPhoneBook.setAdapter(entryAdapter);
    }

    // ─── Smart Search ─────────────────────────────────────────────────────────

    private void setupSmartSearch() {
        inputKeyword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();

                if (searchRunnable != null)
                    searchHandler.removeCallbacks(searchRunnable);

                if (keyword.isEmpty()) {
                    filteredEntries.clear();
                    filteredEntries.addAll(phoneEntries);
                    entryAdapter.refreshList(filteredEntries);
                    showStatus(filteredEntries.size() + " contacts");
                    return;
                }

                if (keyword.length() == 1) {
                    filterLocally(keyword);
                    return;
                }

                // 2+ chars → debounced remote search
                searchRunnable = () -> remoteSearch(keyword);
                searchHandler.postDelayed(searchRunnable, 400);
            }
        });
    }

    // ─── Local Filter ─────────────────────────────────────────────────────────

    private void filterLocally(String keyword) {
        filteredEntries.clear();
        String lower = keyword.toLowerCase();
        for (PhoneEntry entry : phoneEntries) {
            if (entry.getFullName().toLowerCase().contains(lower)
                    || entry.getPhoneNumber().contains(lower)) {
                filteredEntries.add(entry);
            }
        }
        entryAdapter.refreshList(filteredEntries);
        showStatus(filteredEntries.size() + " local result(s)");
    }

    // ─── Remote Search ────────────────────────────────────────────────────────

    private void remoteSearch(String keyword) {
        showProgress(true);
        api.lookupEntries(keyword).enqueue(new Callback<List<PhoneEntry>>() {
            @Override
            public void onResponse(@NonNull Call<List<PhoneEntry>> call,
                                   @NonNull Response<List<PhoneEntry>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<PhoneEntry> results = response.body();
                    entryAdapter.refreshList(results);
                    showStatus(results.size() + " result(s) from server for \""
                            + keyword + "\"");
                } else {
                    showStatus("No results found.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PhoneEntry>> call,
                                  @NonNull Throwable t) {
                showProgress(false);
                filterLocally(keyword);
                showStatus("Offline — showing local results.");
            }
        });
    }

    // ─── Permission ───────────────────────────────────────────────────────────

    private void checkPermissionThenLoad() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            loadPhoneContacts();
        } else {
            permissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        }
    }

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) loadPhoneContacts();
                else showStatus("Permission denied — cannot read contacts.");
            });

    // ─── Load from Device ─────────────────────────────────────────────────────

    private void loadPhoneContacts() {
        // Force full clear before reload
        phoneEntries.clear();
        filteredEntries.clear();
        entryAdapter.refreshList(filteredEntries);
        showProgress(true);

        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String rawNumber = cursor.getString(cursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                String cleanNumber = PhoneNumberUtils.clean(rawNumber);

                if (!PhoneNumberUtils.isDuplicate(cleanNumber, phoneEntries)) {
                    phoneEntries.add(new PhoneEntry(name, cleanNumber));
                }
            }
            cursor.close();
        }

        filteredEntries.clear();
        filteredEntries.addAll(phoneEntries);
        entryAdapter.refreshList(filteredEntries);
        showProgress(false);
        showStatus(phoneEntries.size() + " contacts loaded from device.");
    }

    // ─── Push to Server ───────────────────────────────────────────────────────

    private void pushEntriesToServer() {
        if (phoneEntries.isEmpty()) {
            showStatus("No contacts to sync. Load them first.");
            return;
        }

        showProgress(true);
        final int    total        = phoneEntries.size();
        final int[]  successCount = {0};
        final int[]  failCount    = {0};

        for (PhoneEntry entry : phoneEntries) {
            api.pushEntry(entry).enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(@NonNull Call<ServerResponse> call,
                                       @NonNull Response<ServerResponse> response) {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        successCount[0]++;
                    } else {
                        failCount[0]++;
                    }
                    checkSyncDone(total, successCount[0], failCount[0]);
                }

                @Override
                public void onFailure(@NonNull Call<ServerResponse> call,
                                      @NonNull Throwable t) {
                    failCount[0]++;
                    checkSyncDone(total, successCount[0], failCount[0]);
                }
            });
        }
        showStatus("Syncing " + total + " entries...");
    }

    private void checkSyncDone(int total, int success, int fail) {
        if (success + fail == total) {
            showProgress(false);
            showStatus("Sync ↑ done — ✅ " + success + " saved, ❌ " + fail + " failed.");
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void showStatus(String message) { tvStatus.setText(message); }

    private void showProgress(boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}