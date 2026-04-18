#  NumberBook

A native Android app that reads phone contacts, displays them in a clean UI,
syncs them to a remote MySQL database, and supports real-time search.

---

##  Features

- Read contacts directly from the device
- Display contacts in a RecyclerView with avatar initials
- Sync contacts to a remote server via Retrofit
- Real-time search — local (1 char) then remote (2+ chars)
- Duplicate detection on the server side (same name + same number)
- Contact detail screen with shared number warning
- Clean premium UI with custom color palette

---

##  Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| UI | XML Layouts + RecyclerView |
| Networking | Retrofit 2 + Gson |
| Backend | PHP 8 + PDO |
| Database | MySQL (XAMPP) |
| Min SDK | API 24 (Android 7) |
| Target SDK | API 30 (Android 11) |

---

##  Project Structure

```
NumberBook/
├── app/src/main/java/com/labs/numberbook/
│   ├── MainActivity.java
│   ├── ContactDetailActivity.java
│   ├── PhoneEntry.java
│   ├── ServerResponse.java
│   ├── NumberBookApi.java
│   ├── ApiClient.java
│   ├── PhoneEntryAdapter.java
│   └── PhoneNumberUtils.java
│
└── backend/
    ├── config/DbConnector.php
    ├── model/PhoneRecord.php
    ├── repository/PhoneBookRepository.php
    └── api/
        ├── addEntry.php
        ├── fetchEntries.php
        └── findEntry.php
```

---

##  Setup

### Database

```sql
CREATE DATABASE IF NOT EXISTS numberbook
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE numberbook;

CREATE TABLE phonebook_entry (
    entry_id      INT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(150) NOT NULL,
    phone_number  VARCHAR(50)  NOT NULL,
    entry_source  VARCHAR(50)  DEFAULT 'android',
    recorded_at   DATETIME     DEFAULT CURRENT_TIMESTAMP
);
```

### Backend

- Place the `backend/` folder inside `htdocs/` as `numberbook-api/`
- Start Apache + MySQL in XAMPP
- Test: `http://localhost/numberbook-api/api/fetchEntries.php`

### Android

- Open the project in Android Studio
- In `ApiClient.java`, set your local IP:

```java
// For emulator
private static final String BASE_URL = "http://10.0.2.2/numberbook-api/api/";

// For real device
private static final String BASE_URL = "http://YOUR_LOCAL_IP/numberbook-api/api/";
```

- Run the app on emulator or device (min API 24)

---

##  Testing

### Postman

| Endpoint | Method | Body / Params |
|---|---|---|
| `fetchEntries.php` | GET | — |
| `addEntry.php` | POST | `{"full_name":"...","phone_number":"..."}` |
| `findEntry.php` | GET | `?keyword=name` |

### Screenshots

> XAMPP running Apache + MySQL

<img width="600" height="300" alt="image" src="https://github.com/user-attachments/assets/a07096fb-e442-4cd1-a3f8-9dba79c2ad93" />


> Database entries in phpMyAdmin

<img width="700" height="350" alt="image" src="https://github.com/user-attachments/assets/e0af1763-42f4-4de2-be80-1146ee5a2de1" />


> Postman — addEntry test

<img width="600" height="300" alt="2" src="https://github.com/user-attachments/assets/15c0a906-5849-4289-b6d5-364c3042ad3f" />


### Demo

> App walkthrough — Load, Sync, Search, Detail



---

##  Notes

- The app requests `READ_CONTACTS` permission at runtime
- Phone numbers are cleaned before sync (spaces, dashes removed)
- Duplicate entries (same name + same number) are blocked server-side
- HTTP cleartext traffic is enabled for local testing only

---

##  Author

**ALAOUI SOSSI Sara**  
Android Development — 2026
