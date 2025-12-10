# SECURE FILE IMPLEMENTATION - SHA-256 HASHING

## Overview
This document explains how the Car Loan Amortization System implements secure file export and import using SHA-256 hashing **WITHOUT any third-party libraries**.

---

## ❓ DOES IT USE THIRD-PARTY LIBRARIES?

### **NO** - The system uses **BUILT-IN Java libraries only**

| Component | Library Used | Third-Party? |
|-----------|--------------|--------------|
| SHA-256 Hashing | `java.security.MessageDigest` | ❌ NO - Built into Java |
| File Writing | `java.io.FileWriter` | ❌ NO - Built into Java |
| File Reading | `java.io.BufferedReader` | ❌ NO - Built into Java |
| String Encoding | `java.nio.charset.StandardCharsets` | ❌ NO - Built into Java |

**All cryptographic functions are native to the Java Development Kit (JDK).**

---

## 🔐 WHAT IS SHA-256?

**SHA-256** = Secure Hash Algorithm 256-bit

- Produces a **64-character hexadecimal** hash from any input
- **One-way function** - cannot reverse the hash to get original data
- **Deterministic** - same input always produces same hash
- **Collision-resistant** - virtually impossible for two different inputs to produce same hash

### Example:
```
Input:  "1|Payment #1|₱39,102.95|₱28,277.74"
Output: "A3F2B1C4D5E6F7A8B9C0D1E2F3A4B5C6D7E8F9A0B1C2D3E4F5A6B7C8D9E0F1A2"
        ↑
        64 characters (256 bits in hexadecimal)
```

---

## 📤 EXPORT PROCESS

### Step-by-Step Flow:

```
┌─────────────────────────────────────────────────────────────┐
│                    EXPORT PROCESS                           │
└─────────────────────────────────────────────────────────────┘

     ┌──────────────────────┐
     │  User clicks EXPORT  │
     └──────────┬───────────┘
                ▼
     ┌──────────────────────┐
     │  Check if schedule   │
     │  exists              │
     └──────────┬───────────┘
                ▼
        ◇───────────────◇
       ╱                 ╲
      ╱ schedule.length   ╲
     ╱    > 0 ?            ╲
     ╲                    ╱
      ╲                  ╱
       ◇────────┬───────◇
           YES  │  NO
                │   └──► [Error: "Calculate first"]
                ▼
     ┌──────────────────────┐
     │  Initialize:         │
     │  recordId = 1        │
     │  output = ""         │
     └──────────┬───────────┘
                ▼
     ┌──────────────────────┐
     │  FOR EACH entry IN   │◄─────────────────┐
     │  amortization_       │                  │
     │  schedule            │                  │
     └──────────┬───────────┘                  │
                ▼                              │
     ┌──────────────────────┐                  │
     │  BUILD recordData:   │                  │
     │  "1|Payment #1|      │                  │
     │  ₱39,102.95|..."     │                  │
     └──────────┬───────────┘                  │
                ▼                              │
     ┌──────────────────────┐                  │
     │  GENERATE HASH:      │                  │
     │  hash = SHA256(      │                  │
     │    recordData)       │                  │
     └──────────┬───────────┘                  │
                ▼                              │
     ┌──────────────────────┐                  │
     │  APPEND TO OUTPUT:   │                  │
     │  recordData + " | "  │                  │
     │  + hash + "\n"       │                  │
     └──────────┬───────────┘                  │
                ▼                              │
     ┌──────────────────────┐                  │
     │  recordId++          │──────────────────┘
     └──────────┬───────────┘
                │ (loop complete)
                ▼
     ┌──────────────────────┐
     │  SAVE TO FILE:       │
     │  fileName =          │
     │  "amortization_      │
     │  schedule_[date].txt"│
     └──────────┬───────────┘
                ▼
     ┌──────────────────────┐
     │  Display: "File      │
     │  exported            │
     │  successfully"       │
     └──────────────────────┘
```

### Export Code Logic:
```java
// STEP 1: Build record data (pipe-delimited)
String recordData = recordId + "|" +
                    "Payment #" + periodNumber + "|" +
                    scheduledPayment + "|" +
                    principalPortion + "|" +
                    interestPortion + "|" +
                    closingBalance;

// STEP 2: Generate SHA-256 hash using BUILT-IN Java
MessageDigest digest = MessageDigest.getInstance("SHA-256");
byte[] hashBytes = digest.digest(recordData.getBytes(StandardCharsets.UTF_8));

// STEP 3: Convert bytes to hexadecimal string
StringBuilder hexString = new StringBuilder();
for (byte b : hashBytes) {
    String hex = Integer.toHexString(0xff & b);
    if (hex.length() == 1) hexString.append('0');
    hexString.append(hex);
}
String hash = hexString.toString().toUpperCase();

// STEP 4: Combine data and hash
String line = recordData + " | " + hash;
```

### Exported File Format:
```
AMORTIZATION SCHEDULE EXPORT
Loan ID: 1
Principal: ₱1,998,500.00
APR: 6.50%
Term: 5 Years
==========================================

1|Payment #1|₱39,102.95|₱28,277.74|₱10,825.21|₱1,970,222.26 | A3F2B1C4D5E6F7A8B9C0D1E2F3A4B5C6D7E8F9A0B1C2D3E4F5A6B7C8D9E0F1A2
------------------------------------------
2|Payment #2|₱39,102.95|₱28,430.91|₱10,672.04|₱1,941,791.35 | B4G3H2I1J0K9L8M7N6O5P4Q3R2S1T0U9V8W7X6Y5Z4A3B2C1D0E9F8G7H6I5J4
------------------------------------------
3|Payment #3|₱39,102.95|₱28,584.91|₱10,518.04|₱1,913,206.44 | C5D4E3F2G1H0I9J8K7L6M5N4O3P2Q1R0S9T8U7V6W5X4Y3Z2A1B0C9D8E7F6G5
------------------------------------------
... (60 total records for 5-year loan)
```

---

## 📥 IMPORT PROCESS

### Step-by-Step Flow:

```
┌─────────────────────────────────────────────────────────────┐
│                    IMPORT PROCESS                           │
└─────────────────────────────────────────────────────────────┘

     ┌──────────────────────┐
     │  User clicks IMPORT  │
     └──────────┬───────────┘
                ▼
     ┌──────────────────────┐
     │  Open File Dialog    │
     │  Select .txt file    │
     └──────────┬───────────┘
                ▼
     ┌──────────────────────┐
     │  Read file content   │
     │  into string         │
     └──────────┬───────────┘
                ▼
        ◇───────────────◇
       ╱                 ╲
      ╱ fileContent       ╲
     ╱   is empty ?        ╲
     ╲                    ╱
      ╲                  ╱
       ◇────────┬───────◇
           YES  │  NO
            │   │
            │   ▼
            │  ┌──────────────────────┐
            │  │  Split file into     │
            │  │  lines               │
            │  └──────────┬───────────┘
            │             ▼
            │  ┌──────────────────────┐
            │  │  FOR EACH line       │◄────────────────┐
            │  └──────────┬───────────┘                 │
            │             ▼                             │
            │     ◇───────────────◇                     │
            │    ╱                 ╲                    │
            │   ╱ Is empty or      ╲                   │
            │  ╱  header line ?     ╲                  │
            │  ╲                   ╱                   │
            │   ╲                 ╱                    │
            │    ◇───────┬───────◇                     │
            │        YES │  NO                         │
            │         │  │                             │
            │         │  ▼                             │
            │  [Skip] │ ┌──────────────────────┐       │
            │    │    │ │  Find " | " separator│       │
            │    │    │ └──────────┬───────────┘       │
            │    │    │            ▼                   │
            │    │    │ ┌──────────────────────┐       │
            │    │    │ │  EXTRACT:            │       │
            │    │    │ │  recordData = before │       │
            │    │    │ │  storedHash = after  │       │
            │    │    │ └──────────┬───────────┘       │
            │    │    │            ▼                   │
            │    │    │ ┌──────────────────────┐       │
            │    │    │ │  RECALCULATE:        │       │
            │    │    │ │  calculatedHash =    │       │
            │    │    │ │  SHA256(recordData)  │       │
            │    │    │ └──────────┬───────────┘       │
            │    │    │            ▼                   │
            │    │    │    ◇───────────────◇           │
            │    │    │   ╱                 ╲          │
            │    │    │  ╱ calculatedHash    ╲         │
            │    │    │ ╱  === storedHash ?   ╲        │
            │    │    │ ╲                    ╱         │
            │    │    │  ╲                  ╱          │
            │    │    │   ◇───────┬────────◇           │
            │    │    │       YES │  NO                │
            │    │    │           │   │                │
            │    │    │           │   ▼                │
            │    │    │           │  ┌────────────────┐│
            │    │    │           │  │ ERROR:         ││
            │    │    │           │  │ "Invalid hash -││
            │    │    │           │  │ file corrupted"││
            │    │    │           │  └───────┬────────┘│
            │    │    │           │          │         │
            │    │    │           │          ▼         │
            │    │    │           │       [STOP]       │
            │    │    │           ▼                    │
            │    │    │ ┌──────────────────────┐       │
            │    │    │ │  Parse recordData    │       │
            │    │    │ │  Add to schedule     │───────┘
            │    │    │ └──────────────────────┘
            │    │    │
            │    └────┼────────────────────────────────┘
            │         │ (loop complete)
            │         ▼
            │  ┌──────────────────────┐
            └─►│  Display: "File      │
               │  imported            │
               │  successfully"       │
               └──────────────────────┘
```

### Import Code Logic:
```java
// STEP 1: Read file and split into lines
String[] lines = fileContent.split("\n");

// STEP 2: Process each line
for (String line : lines) {
    // Skip empty and header lines
    if (line.isEmpty() || line.startsWith("=") || line.startsWith("-")) {
        continue;
    }
    
    // STEP 3: Find the separator
    int separatorIndex = line.lastIndexOf(" | ");
    if (separatorIndex == -1) continue;
    
    // STEP 4: Extract data and stored hash
    String recordData = line.substring(0, separatorIndex).trim();
    String storedHash = line.substring(separatorIndex + 3).trim();
    
    // STEP 5: Recalculate hash using SAME built-in method
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hashBytes = digest.digest(recordData.getBytes(StandardCharsets.UTF_8));
    String calculatedHash = bytesToHex(hashBytes);
    
    // STEP 6: Verify hash matches
    if (!calculatedHash.equals(storedHash)) {
        throw new SecurityException("File corrupted - hash mismatch!");
    }
    
    // STEP 7: Parse and add to schedule
    String[] parts = recordData.split("\\|");
    // ... create AmortizationSchedule object
}
```

---

## 🛡️ HOW HASHING PROVIDES SECURITY

### Tamper Detection Example:

#### Original Record:
```
1|Payment #1|₱39,102.95|₱28,277.74|₱10,825.21|₱1,970,222.26 | A3F2B1C4...
```

#### If Someone Modifies the Payment Amount:
```
1|Payment #1|₱99,999.99|₱28,277.74|₱10,825.21|₱1,970,222.26 | A3F2B1C4...
            ↑
            Changed from ₱39,102.95 to ₱99,999.99
```

#### What Happens During Import:
```
STEP 1: Read stored hash from file
        storedHash = "A3F2B1C4..."

STEP 2: Recalculate hash from modified data
        calculatedHash = SHA256("1|Payment #1|₱99,999.99|...")
        calculatedHash = "X7Y8Z9W0..."  ← DIFFERENT!

STEP 3: Compare hashes
        "X7Y8Z9W0..." ≠ "A3F2B1C4..."
        
STEP 4: ALERT USER!
        "Invalid hash - file may be corrupted or tampered!"
```

### Visual Security Flow:
```
┌─────────────────────────────────────────────────────────────┐
│                    SECURITY CHECK                           │
└─────────────────────────────────────────────────────────────┘

   EXPORT TIME                      IMPORT TIME
   ───────────                      ───────────
   
   Original Data                    Read Data
        │                               │
        ▼                               ▼
   ┌─────────┐                    ┌─────────┐
   │ SHA-256 │                    │ SHA-256 │
   └────┬────┘                    └────┬────┘
        │                               │
        ▼                               ▼
   Hash: ABC123                   Hash: ABC123 ✓ (if unchanged)
        │                         Hash: XYZ789 ✗ (if modified)
        │                               │
        ▼                               ▼
   Save to file ──────────────► Compare hashes
   with hash                          │
                                      ▼
                              ◇───────────────◇
                             ╱                 ╲
                            ╱ Hashes match ?    ╲
                           ╲                   ╱
                            ╲                 ╱
                             ◇───────┬───────◇
                                 YES │  NO
                                     │   │
                                     ▼   ▼
                               [VALID]  [CORRUPTED!]
                               [Import]  [Reject]
```

---

## 📋 SUMMARY FOR DEFENSE

### Key Points to Remember:

1. **NO third-party libraries** - Uses only Java's built-in `java.security.MessageDigest`

2. **SHA-256** is a one-way cryptographic hash function built into Java

3. **Export Process:**
   - Build pipe-delimited record data
   - Generate SHA-256 hash of data
   - Save both data and hash separated by " | "

4. **Import Process:**
   - Read file line by line
   - Extract data and stored hash
   - Recalculate hash from data
   - Compare calculated vs stored hash
   - Accept only if hashes match

5. **Security Benefit:**
   - Any modification to data will produce different hash
   - System detects tampering automatically
   - Protects data integrity

### Simple Explanation for Defense:
> "The system uses Java's built-in SHA-256 hashing. When exporting, each record gets a unique 64-character hash attached. When importing, the system recalculates the hash and compares it. If someone modifies even one character in the file, the hash won't match, and the system will reject the import as corrupted. No third-party libraries are needed - everything is native Java."

---

## 🔧 JAVA CLASSES USED

| Class | Package | Purpose |
|-------|---------|---------|
| `MessageDigest` | `java.security` | Generate SHA-256 hash |
| `StandardCharsets` | `java.nio.charset` | UTF-8 encoding |
| `FileWriter` | `java.io` | Write to file |
| `BufferedReader` | `java.io` | Read from file |
| `StringBuilder` | `java.lang` | Build strings efficiently |

### Import Statements (All Built-in):
```java
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
```

**All imports are from the standard Java library - NO external dependencies!**
