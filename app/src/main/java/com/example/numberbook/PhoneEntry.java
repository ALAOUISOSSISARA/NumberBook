package com.example.numberbook;

import com.google.gson.annotations.SerializedName;

public class PhoneEntry {

    @SerializedName("entry_id")
    private int entryId;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("entry_source")
    private String entrySource;

    @SerializedName("recorded_at")
    private String recordedAt;

    public PhoneEntry() {}

    public PhoneEntry(String fullName, String phoneNumber) {
        this.fullName    = fullName;
        this.phoneNumber = phoneNumber;
    }

    public int    getEntryId()      { return entryId; }
    public String getFullName()     { return fullName; }
    public String getPhoneNumber()  { return phoneNumber; }
    public String getEntrySource()  { return entrySource; }
    public String getRecordedAt()   { return recordedAt; }

    public void setEntryId(int entryId)            { this.entryId = entryId; }
    public void setFullName(String fullName)        { this.fullName = fullName; }
    public void setPhoneNumber(String phoneNumber)  { this.phoneNumber = phoneNumber; }
    public void setEntrySource(String entrySource)  { this.entrySource = entrySource; }
    public void setRecordedAt(String recordedAt)    { this.recordedAt = recordedAt; }
}