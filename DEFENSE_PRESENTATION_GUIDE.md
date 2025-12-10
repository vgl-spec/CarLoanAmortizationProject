# CAR LOAN AMORTIZATION SYSTEM - DEFENSE PRESENTATION GUIDE

## Overview
This guide provides each team member with their specific roles, responsibilities, and key points to cover during the defense presentation.

---

## KEY ABBREVIATIONS & DEFINITIONS

| Abbreviation | Full Name | Definition |
|--------------|-----------|------------|
| **APR** | Annual Percentage Rate | The yearly interest rate charged on a loan, expressed as a percentage |
| **EAR** | Effective Annual Rate | The actual annual rate after accounting for compounding periods |
| **EMR** | Effective Monthly Rate | The monthly interest rate derived from EAR |
| **EMI** | Equated Monthly Installment | Fixed monthly payment amount (same as Monthly Payment) |

---

## KEY FORMULAS

### 1. Tax Amount
```
taxAmount = carPrice × (salesTaxRate / 100)
```

### 2. Total Vehicle Cost
```
totalVehicleCost = carPrice + taxAmount + registrationFee
```

### 3. Down Payment Plus Trade-In
```
downPlusTrade = downPayment + tradeInValue
```

### 4. Principal (Amount Financed)
```
principal = totalVehicleCost - downPlusTrade
```

### 5. Effective Annual Rate (EAR)
```
EAR = (1 + APR/m)^m - 1

Where:
- APR = Annual Percentage Rate (as decimal)
- m = Number of compounding periods per year
  - Monthly: m = 12
  - Quarterly: m = 4
  - Semi-Annually: m = 2
  - Annually: m = 1
```

### 6. Effective Monthly Rate (EMR)
```
EMR = (1 + EAR)^(1/12) - 1
```

### 7. Monthly Payment (EMI)
```
EMI = P × [r(1+r)^n] / [(1+r)^n - 1]

Where:
- P = Principal (Amount Financed)
- r = EMR (Effective Monthly Rate)
- n = Total number of months (termYears × 12)
```

### 8. Total Amount Paid
```
totalAmount = monthlyPayment × termMonths
```

### 9. Total Interest
```
totalInterest = totalAmount - principal
```

### 10. Penalty Calculation
```
penalty = scheduledPayment × (penaltyRate / 100) × monthsLate

Where:
- monthsLate = (daysLate - gracePeriodDays) / 30
- Only applies if daysLate > gracePeriodDays
```

### 11. Amortization Schedule (Per Period)
```
interestPortion = openingBalance × EMR
principalPortion = monthlyPayment - interestPortion
closingBalance = openingBalance - principalPortion
```

---

## PERSON 1: LOGICAL ANALYSIS

### What You Need to Know:
- **Purpose**: Explain the PROBLEM the system solves and WHY it's needed
- **Business Logic**: Understand how car loans work in real life
- **User Requirements**: What users need from this calculator

### Key Points to Cover:

#### 1. Problem Statement
> "Customers need a way to calculate and visualize their car loan payments before committing to a purchase."

#### 2. System Objectives
- Calculate accurate monthly payments
- Generate complete amortization schedules
- Allow comparison of different loan scenarios
- Provide secure export/import of data

#### 3. Input-Process-Output Analysis

| INPUT | PROCESS | OUTPUT |
|-------|---------|--------|
| Car Price | Calculate Tax | Total Vehicle Cost |
| Sales Tax Rate | Calculate Principal | Amount Financed |
| Registration Fee | Calculate EAR/EMR | Effective Rates |
| Down Payment | Calculate EMI | Monthly Payment |
| Trade-In Value | Generate Schedule | Amortization Table |
| APR | Calculate Totals | Total Interest |
| Term (Years) | Hash Records | Secure Export File |
| Compounding | Verify Hashes | Import Validation |

#### 4. Decision Logic Summary
```
IF selectedCar != null THEN
    Navigate to Calculator
ELSE
    Show "Please select a car"

IF all inputs valid AND principal > 0 THEN
    Calculate Loan
    Generate Schedule
ELSE
    Show validation error

IF amortization_schedule.length > 0 THEN
    Allow Export/Import
ELSE
    Show "Calculate first"
```

### Presentation Flow:
1. Introduce the problem (1 min)
2. Explain objectives (1 min)
3. Walk through Input-Process-Output (2 min)
4. Explain decision points (1 min)

---

## PERSON 2: OBJECT FUNCTIONALITY ALGORITHM AND SIMULATION PRESENTATION

### What You Need to Know:
- **Classes/Objects**: All model classes and their purposes
- **Methods**: Key functions and what they do
- **Algorithms**: Step-by-step calculation procedures
- **Live Demo**: How to simulate the system

### Key Objects and Their Functions:

#### 1. Car Object
```
Variables:
- id, make, model, year, price, category, color, mpg

Purpose: Store car information for selection
```

#### 2. Loan Object
```
Variables:
- principal, apr, compounding, termMonths
- downPayment, tradeInValue, salesTaxRate, registrationFee
- monthlyPayment, totalInterest, totalAmount, penaltyRate

Purpose: Store all loan parameters and results
```

#### 3. AmortizationSchedule Object
```
Variables:
- periodNumber, dueDate, openingBalance
- scheduledPayment, principalPortion, interestPortion
- closingBalance, isPaid, status

Purpose: Store each payment row in the schedule
```

#### 4. LoanResult Object
```
Variables:
- principal, monthlyPayment, totalInterest, totalAmount
- EAR, EMR, termMonths, totalVehicleCost, taxAmount

Purpose: Return calculated results from LoanCalculator
```

### Algorithm Walkthrough:

#### Loan Calculation Algorithm:
```
ALGORITHM: calculateLoan(carPrice, salesTaxRate, registrationFee, 
                         downPayment, tradeInValue, apr, termYears, compounding)

STEP 1: Calculate Total Vehicle Cost
    taxAmount = carPrice × (salesTaxRate / 100)
    totalVehicleCost = carPrice + taxAmount + registrationFee

STEP 2: Calculate Amount Financed
    downPlusTrade = downPayment + tradeInValue
    principal = totalVehicleCost - downPlusTrade

STEP 3: Calculate Effective Rates
    m = getCompoundingPeriods(compounding)
    EAR = (1 + (apr/100) / m)^m - 1
    EMR = (1 + EAR)^(1/12) - 1

STEP 4: Calculate Monthly Payment
    termMonths = termYears × 12
    IF EMR = 0 THEN
        monthlyPayment = principal / termMonths
    ELSE
        numerator = EMR × (1 + EMR)^termMonths
        denominator = (1 + EMR)^termMonths - 1
        monthlyPayment = principal × (numerator / denominator)

STEP 5: Calculate Totals
    totalAmount = monthlyPayment × termMonths
    totalInterest = totalAmount - principal

STEP 6: Return Results
    RETURN LoanResult(principal, monthlyPayment, totalInterest, 
                      totalAmount, EAR, EMR, termMonths)
END ALGORITHM
```

#### Amortization Schedule Algorithm:
```
ALGORITHM: generateSchedule(principal, EMR, monthlyPayment, termMonths, startDate)

STEP 1: Initialize
    schedule = empty list
    balance = principal

STEP 2: Generate Each Period
    FOR month = 1 TO termMonths DO
        interestPortion = balance × EMR
        principalPortion = monthlyPayment - interestPortion
        
        IF month = termMonths THEN
            principalPortion = balance  // Final adjustment
        
        openingBalance = balance
        balance = balance - principalPortion
        dueDate = startDate + month months
        
        ADD ROW TO schedule:
            periodNumber = month
            dueDate = dueDate
            openingBalance = openingBalance
            scheduledPayment = monthlyPayment
            principalPortion = principalPortion
            interestPortion = interestPortion
            closingBalance = balance
    END FOR

STEP 3: Return Schedule
    RETURN schedule
END ALGORITHM
```

### Simulation Demo Script:
1. Select a car (Toyota Camry - ₱1,850,000)
2. Show carPrice populates automatically
3. Enter: salesTaxRate=8%, registrationFee=₱500
4. Enter: downPayment=₱0, tradeInValue=₱0
5. Enter: apr=6.5%, termYears=5, compounding=Monthly
6. Click Calculate - Show results
7. View Amortization Schedule
8. Demonstrate Export/Import

### Presentation Flow:
1. Introduce objects (1 min)
2. Explain key algorithms with formulas (3 min)
3. Live simulation demo (3 min)

---

## PERSON 3: STANDARD PROGRAM FLOWCHART AND PSEUDOCODE

### What You Need to Know:
- **Flowchart Symbols**: Oval (Start/End), Rectangle (Process), Diamond (Decision), Parallelogram (I/O)
- **System Flow**: From car selection to export
- **Pseudocode Format**: Structured English

### Main System Flowchart:

```
                    ┌─────────┐
                    │  START  │
                    └────┬────┘
                         ▼
    ┌────────────────────────────────────────┐
    │         INITIALIZATION BLOCK           │
    │                                        │
    │  selectedCar = null                    │
    │  carPrice = 0.00                       │
    │  salesTaxRate = 0.00                   │
    │  registrationFee = 0.00               │
    │  downPayment = 0.00                    │
    │  tradeInValue = 0.00                   │
    │  apr = 0.00                            │
    │  termYears = 0                         │
    │  compounding = ""                      │
    │  amortization_schedule = []            │
    │  scheduleGenerated = false             │
    │  exportedFileContent = ""              │
    │  importedFileContent = ""              │
    └────────────────────┬───────────────────┘
                         ▼
              ╔═══════════════════╗
              ║  Display Cars     ║
              ║  Panel            ║
              ╚═════════╤═════════╝
                        ▼
              ◇─────────────────────◇
             ╱                       ╲
            ╱  selectedCar != null ? ╲
           ╲                         ╱
            ╲                       ╱
             ◇───────────┬─────────◇
                    YES  │  NO
                         │   │
                         ▼   └──► [Wait for selection]
              ╔═══════════════════╗
              ║  Display          ║
              ║  carMake;         ║
              ║  carModel;        ║
              ║  carPrice         ║
              ╚═════════╤═════════╝
                        ▼
              ◇─────────────────────◇
             ╱                       ╲
            ╱  Continue to           ╲
           ╱   Calculator clicked?   ╲
          ╲                         ╱
           ╲                       ╱
            ◇───────────┬─────────◇
                   YES  │  NO
                        │   │
                        ▼   └──► [Stay on Cars Panel]
    ┌────────────────────────────────────────┐
    │  Navigate to Calculator Panel          │
    │  Populate carPrice field               │
    └────────────────────┬───────────────────┘
                         ▼
              ╔═══════════════════╗
              ║  INPUT:           ║
              ║  salesTaxRate     ║
              ║  registrationFee  ║
              ║  downPayment      ║
              ║  tradeInValue     ║
              ║  apr              ║
              ║  termYears        ║
              ║  compounding      ║
              ║  penaltyRate      ║
              ╚═════════╤═════════╝
                        ▼
              ◇─────────────────────◇
             ╱                       ╲
            ╱  All inputs valid AND  ╲
           ╱   amountFinanced > 0 ?  ╲
          ╲                         ╱
           ╲                       ╱
            ◇───────────┬─────────◇
                   YES  │  NO
                        │   │
                        ▼   └──► [Show Error Message]
    ┌────────────────────────────────────────┐
    │  CALCULATE:                            │
    │  termMonths = termYears × 12           │
    │  taxAmount = carPrice × (salesTaxRate/100) │
    │  totalVehicleCost = carPrice + taxAmount   │
    │                   + registrationFee    │
    │  downPlusTrade = downPayment +         │
    │                  tradeInValue          │
    │  principal = totalVehicleCost -        │
    │              downPlusTrade             │
    │  EAR = (1 + apr/m)^m - 1              │
    │  EMR = (1 + EAR)^(1/12) - 1           │
    │  monthlyPayment = P×[r(1+r)^n]/       │
    │                   [(1+r)^n-1]          │
    │  totalAmount = monthlyPayment ×        │
    │                termMonths              │
    │  totalInterest = totalAmount - principal│
    └────────────────────┬───────────────────┘
                         ▼
    ┌────────────────────────────────────────┐
    │  GENERATE AMORTIZATION SCHEDULE:       │
    │  FOR month = 1 TO termMonths:          │
    │    interestPortion = balance × EMR     │
    │    principalPortion = payment -        │
    │                       interestPortion  │
    │    closingBalance = balance -          │
    │                     principalPortion   │
    │    ADD row to amortization_schedule    │
    │  END FOR                               │
    │  scheduleGenerated = true              │
    └────────────────────┬───────────────────┘
                         ▼
              ╔═══════════════════╗
              ║  DISPLAY:         ║
              ║  monthlyPayment   ║
              ║  totalAmount      ║
              ║  totalInterest    ║
              ║  apr              ║
              ║  termYears        ║
              ╚═════════╤═════════╝
                        ▼
              ◇─────────────────────────────◇
             ╱                               ╲
            ╱  amortization_schedule &&      ╲
           ╱   schedule.length > 0 ?         ╲
          ╲                                 ╱
           ╲                               ╱
            ◇───────────────┬─────────────◇
                       YES  │  NO
                            │   │
                            ▼   └──► [Error: Calculate first]
              ╔═══════════════════════════╗
              ║  DISPLAY Amortization     ║
              ║  Schedule Table:          ║
              ║  period_number            ║
              ║  scheduled_payment        ║
              ║  principal_portion        ║
              ║  interest_portion         ║
              ║  closing_balance          ║
              ╚═══════════╤═══════════════╝
                          ▼
              ◇───────────────────────────────────◇
             ╱                                     ╲
            ╱  exportedFileContent != null AND    ╲
           ╱   length > 0 ?                        ╲
          ╲                                       ╱
           ╲                                     ╱
            ◇─────────────────┬─────────────────◇
                         YES  │  NO
                              │   │
                              ▼   │
    ┌────────────────────────────────────────┐  │
    │  EXPORT:                               │  │
    │  recordId = 1                          │  │
    │  FOR EACH entry IN schedule:           │  │
    │    recordData = buildRecord(entry)     │  │
    │    hash = SHA256(recordData)           │  │
    │    output += recordData + "|" + hash   │  │
    │    recordId++                          │  │
    │  END FOR                               │  │
    │  fileName = "amortization_" + date     │  │
    │  downloadFile(output, fileName)        │  │
    └────────────────────┬───────────────────┘  │
                         ▼                      │
              ◇────────────────────────────────◇│
             ╱                                  ╲│
            ╱  importedFileContent != null AND ╲│
           ╱   length > 0 ?                     ╲
          ╲                                    ╱
           ╲                                  ╱
            ◇─────────────────┬──────────────◇
                         YES  │  NO
                              │   │
                              ▼   │
    ┌────────────────────────────────────────┐  │
    │  IMPORT:                               │  │
    │  lines = split(fileContent, "\n")      │  │
    │  FOR EACH line IN lines:               │  │
    │    recordData = extractData(line)      │  │
    │    storedHash = extractHash(line)      │  │
    │    calculatedHash = SHA256(recordData) │  │
    │    IF calculatedHash = storedHash THEN │  │
    │      ADD to amortization_schedule      │  │
    │    ELSE                                │  │
    │      THROW "Invalid hash"              │  │
    │  END FOR                               │  │
    │  scheduleGenerated = true              │  │
    └────────────────────┬───────────────────┘  │
                         ▼                      ▼
                    ┌─────────┐
                    │   END   │
                    └─────────┘
```

### Complete Pseudocode:

```
PROGRAM CarLoanAmortizationCalculator

// ==========================================
// INITIALIZATION
// ==========================================
BEGIN
    DECLARE selectedCar = NULL
    DECLARE carPrice = 0.00
    DECLARE salesTaxRate = 0.00
    DECLARE registrationFee = 0.00
    DECLARE downPayment = 0.00
    DECLARE tradeInValue = 0.00
    DECLARE apr = 0.00
    DECLARE termYears = 0
    DECLARE termMonths = 0
    DECLARE compounding = ""
    DECLARE penaltyRate = 0.00
    
    DECLARE principal = 0.00
    DECLARE monthlyPayment = 0.00
    DECLARE totalInterest = 0.00
    DECLARE totalAmount = 0.00
    DECLARE EAR = 0.00
    DECLARE EMR = 0.00
    
    DECLARE amortization_schedule = EMPTY ARRAY
    DECLARE scheduleGenerated = FALSE
    DECLARE exportedFileContent = ""
    DECLARE importedFileContent = ""
END

// ==========================================
// MAIN PROGRAM
// ==========================================
PROCEDURE Main()
BEGIN
    DISPLAY "Cars Panel"
    LOAD car list
    
    WAIT FOR user to select car
    
    IF selectedCar != NULL THEN
        carPrice = selectedCar.price
        DISPLAY carMake, carModel, carPrice
        
        IF user clicks "Continue to Calculator" THEN
            CALL CalculatorPanel()
        END IF
    ELSE
        DISPLAY "Please select a car first"
    END IF
END

// ==========================================
// CALCULATOR PANEL
// ==========================================
PROCEDURE CalculatorPanel()
BEGIN
    DISPLAY Calculator Form
    
    INPUT salesTaxRate
    INPUT registrationFee
    INPUT downPayment
    INPUT tradeInValue
    INPUT apr
    INPUT termYears
    INPUT compounding
    INPUT penaltyRate
    
    IF user clicks "Calculate Loan" THEN
        IF ValidateInputs() = TRUE THEN
            CALL CalculateLoan()
            CALL GenerateAmortizationSchedule()
            CALL DisplayLoanSummary()
        ELSE
            DISPLAY validation error message
        END IF
    END IF
END

// ==========================================
// VALIDATE INPUTS
// ==========================================
FUNCTION ValidateInputs() RETURNS BOOLEAN
BEGIN
    IF carPrice <= 0 THEN RETURN FALSE
    IF apr <= 0 OR apr > 50 THEN RETURN FALSE
    IF termYears <= 0 THEN RETURN FALSE
    IF downPayment < 0 THEN RETURN FALSE
    IF salesTaxRate < 0 OR salesTaxRate > 100 THEN RETURN FALSE
    RETURN TRUE
END

// ==========================================
// CALCULATE LOAN
// ==========================================
PROCEDURE CalculateLoan()
BEGIN
    // Step 1: Calculate term in months
    termMonths = termYears × 12
    
    // Step 2: Calculate tax amount
    taxAmount = carPrice × (salesTaxRate / 100)
    
    // Step 3: Calculate total vehicle cost
    totalVehicleCost = carPrice + taxAmount + registrationFee
    
    // Step 4: Calculate down payment plus trade-in
    downPlusTrade = downPayment + tradeInValue
    
    // Step 5: Calculate principal (amount financed)
    principal = totalVehicleCost - downPlusTrade
    
    // Step 6: Get compounding periods
    SWITCH compounding
        CASE "monthly": m = 12
        CASE "quarterly": m = 4
        CASE "semi-annually": m = 2
        CASE "annually": m = 1
        DEFAULT: m = 12
    END SWITCH
    
    // Step 7: Calculate EAR
    EAR = (1 + (apr / 100) / m)^m - 1
    
    // Step 8: Calculate EMR
    EMR = (1 + EAR)^(1/12) - 1
    
    // Step 9: Calculate monthly payment
    IF EMR = 0 THEN
        monthlyPayment = principal / termMonths
    ELSE
        numerator = EMR × (1 + EMR)^termMonths
        denominator = (1 + EMR)^termMonths - 1
        monthlyPayment = principal × (numerator / denominator)
    END IF
    
    // Step 10: Calculate totals
    totalAmount = monthlyPayment × termMonths
    totalInterest = totalAmount - principal
END

// ==========================================
// GENERATE AMORTIZATION SCHEDULE
// ==========================================
PROCEDURE GenerateAmortizationSchedule()
BEGIN
    CLEAR amortization_schedule
    balance = principal
    startDate = TODAY
    
    FOR month = 1 TO termMonths DO
        // Calculate interest portion
        interestPortion = balance × EMR
        
        // Calculate principal portion
        principalPortion = monthlyPayment - interestPortion
        
        // Adjust for last payment
        IF month = termMonths THEN
            principalPortion = balance
        END IF
        
        // Store opening balance
        openingBalance = balance
        
        // Update balance
        balance = balance - principalPortion
        IF balance < 0 THEN balance = 0
        
        // Calculate due date
        dueDate = startDate + month MONTHS
        
        // Create schedule row
        row = NEW AmortizationSchedule
        row.periodNumber = month
        row.dueDate = dueDate
        row.openingBalance = openingBalance
        row.scheduledPayment = monthlyPayment
        row.principalPortion = principalPortion
        row.interestPortion = interestPortion
        row.closingBalance = balance
        row.isPaid = FALSE
        
        // Add to schedule
        ADD row TO amortization_schedule
    END FOR
    
    scheduleGenerated = TRUE
END

// ==========================================
// DISPLAY LOAN SUMMARY
// ==========================================
PROCEDURE DisplayLoanSummary()
BEGIN
    DISPLAY "Loan Calculation Results"
    DISPLAY "Monthly Payment: ₱" + FORMAT(monthlyPayment, "###,##0.00")
    DISPLAY "Total Amount Paid: ₱" + FORMAT(totalAmount, "###,##0.00")
    DISPLAY "Total Interest: ₱" + FORMAT(totalInterest, "###,##0.00")
    DISPLAY "Interest Rate: " + FORMAT(apr, "0.00") + "%"
    DISPLAY "Loan Duration: " + termYears + " Years"
    
    IF user clicks "View Amortization Schedule" THEN
        IF amortization_schedule.length > 0 THEN
            CALL DisplayAmortizationTable()
        ELSE
            DISPLAY "Please calculate the loan first"
        END IF
    END IF
END

// ==========================================
// DISPLAY AMORTIZATION TABLE
// ==========================================
PROCEDURE DisplayAmortizationTable()
BEGIN
    DISPLAY Table Header:
        "Payment #", "Payment", "Principal", "Interest", 
        "Penalty", "Balance", "Total Paid"
    
    totalPaid = 0
    
    FOR EACH row IN amortization_schedule DO
        totalPaid = totalPaid + row.scheduledPayment
        
        DISPLAY row.periodNumber
        DISPLAY "₱" + FORMAT(row.scheduledPayment)
        DISPLAY "₱" + FORMAT(row.principalPortion)
        DISPLAY "₱" + FORMAT(row.interestPortion)
        DISPLAY "₱0.00"  // Penalty
        DISPLAY "₱" + FORMAT(row.closingBalance)
        DISPLAY "₱" + FORMAT(totalPaid)
    END FOR
END

// ==========================================
// EXPORT SECURE FILE
// ==========================================
PROCEDURE ExportSecureFile()
BEGIN
    IF amortization_schedule.length = 0 THEN
        DISPLAY "Schedule not generated"
        RETURN
    END IF
    
    output = ""
    recordId = 1
    
    FOR EACH entry IN amortization_schedule DO
        // Build pipe-delimited record
        recordData = recordId + "|" +
                     "Payment #" + entry.periodNumber + "|" +
                     FORMAT(entry.scheduledPayment) + "|" +
                     FORMAT(entry.principalPortion) + "|" +
                     FORMAT(entry.interestPortion) + "|" +
                     FORMAT(entry.closingBalance)
        
        // Generate SHA-256 hash
        hash = SHA256(recordData)
        
        // Append to output
        output = output + recordData + " | " + hash + NEWLINE
        output = output + "----------------------------------------" + NEWLINE
        
        recordId = recordId + 1
    END FOR
    
    // Generate filename
    fileName = "amortization_schedule_" + FORMAT(TODAY, "yyyy-MM-dd") + ".txt"
    
    // Save file
    SAVE output TO fileName
    DISPLAY "File exported: " + fileName
    
    exportedFileContent = ""
END

// ==========================================
// IMPORT SECURE FILE
// ==========================================
PROCEDURE ImportSecureFile(filePath)
BEGIN
    // Read file content
    importedFileContent = READ FILE(filePath)
    
    IF importedFileContent = EMPTY THEN
        DISPLAY "File is empty"
        RETURN
    END IF
    
    // Split into lines
    lines = SPLIT(importedFileContent, NEWLINE)
    
    CLEAR amortization_schedule
    
    FOR EACH line IN lines DO
        // Skip empty and filler lines
        IF line IS EMPTY OR line STARTS WITH "---" THEN
            CONTINUE
        END IF
        
        // Find separator
        separatorIndex = LAST INDEX OF " | " IN line
        IF separatorIndex = -1 THEN CONTINUE
        
        // Extract data and hash
        recordData = SUBSTRING(line, 0, separatorIndex)
        storedHash = SUBSTRING(line, separatorIndex + 3)
        
        // Verify hash
        calculatedHash = SHA256(recordData)
        
        IF calculatedHash != storedHash THEN
            DISPLAY "Invalid hash - file may be corrupted"
            RETURN
        END IF
        
        // Parse data
        parts = SPLIT(recordData, "|")
        
        // Create schedule row
        row = NEW AmortizationSchedule
        row.periodNumber = PARSE INTEGER(parts[1])
        row.scheduledPayment = PARSE AMOUNT(parts[2])
        row.principalPortion = PARSE AMOUNT(parts[3])
        row.interestPortion = PARSE AMOUNT(parts[4])
        row.closingBalance = PARSE AMOUNT(parts[5])
        row.isPaid = FALSE
        
        ADD row TO amortization_schedule
    END FOR
    
    scheduleGenerated = TRUE
    importedFileContent = ""
    DISPLAY "File imported successfully"
END

END PROGRAM
```

### Presentation Flow:
1. Explain flowchart symbols used (30 sec)
2. Walk through main flowchart (2 min)
3. Explain key pseudocode sections (2 min)
4. Show how flowchart matches actual system (1 min)

---

## PERSON 4: FORM DESIGN (KISS) WITH OBJECT FUNCTIONALITY RELATIONSHIP

### What You Need to Know:
- **KISS Principle**: Keep It Simple, Stupid
- **UI Components**: What each form element does
- **Object Relationship**: How UI connects to code

### KISS Principle Applied:

| Principle | Application |
|-----------|-------------|
| **Simple Navigation** | Two main panels: Cars → Calculator |
| **Clear Labels** | All fields have descriptive labels |
| **Logical Grouping** | Related fields grouped together |
| **Visual Feedback** | Selected car highlighted, real-time summary |
| **Minimal Clicks** | Most actions require 1-2 clicks |

### Form Layout and Object Mapping:

#### SCREEN 1: Cars Panel
```
┌─────────────────────────────────────────────────────────┐
│  🚗 Auto Loan Calculator Pro                            │
│  "Smart financing for your dream car..."                │
├─────────────────────────────────────────────────────────┤
│  [🔢 Calculate]  [🚗 Cars]                              │
├─────────────────────────────────────────────────────────┤
│  🔍 Search by make, model, or type...                   │
├─────────────────────────────────────────────────────────┤
│  [Toyota Camry Selected]                                │
│  Click "Continue to Calculator" to finance this vehicle │
├─────────────────────────────────────────────────────────┤
│  ┌──────────┐  ┌──────────┐  ┌──────────┐              │
│  │    TC    │  │    HC    │  │    M3    │              │
│  │   2024   │  │   2024   │  │   2024   │              │
│  │ Toyota   │  │ Honda    │  │ Mazda 3  │              │
│  │ Camry    │  │ Civic    │  │ Sedan    │              │
│  │ Sedan    │  │ Sedan    │  │          │              │
│  │₱1,850,000│  │₱1,350,000│  │₱1,450,000│              │
│  └──────────┘  └──────────┘  └──────────┘              │
├─────────────────────────────────────────────────────────┤
│                        [Continue to Calculator →]       │
└─────────────────────────────────────────────────────────┘

OBJECT MAPPING:
┌──────────────────┬──────────────────────────────────────┐
│ UI Element       │ Variable/Object                      │
├──────────────────┼──────────────────────────────────────┤
│ Car Card Click   │ selectedCar = car object             │
│ Car Price        │ carPrice = selectedCar.price         │
│ Car Name         │ selectedCar.make + selectedCar.model │
│ Continue Button  │ IF selectedCar != null → navigate    │
└──────────────────┴──────────────────────────────────────┘
```

#### SCREEN 2: Calculator Panel
```
┌─────────────────────────────────────────────────────────┐
│  🚗 2024 Toyota Camry                                   │
│     Sedan • ₱1,850,000.00                               │
├───────────────────────────┬─────────────────────────────┤
│  VEHICLE COST             │  LOAN SUMMARY               │
│  ─────────────            │  ────────────               │
│  Car Price (₱)            │  Total Vehicle Cost:        │
│  [1850000.00]────────(3)  │  ₱1,998,500.00              │
│                           │                             │
│  Sales Tax Rate (%)       │  Tax Amount:                │
│  [8.0]───────────────(4)  │  ₱148,000.00                │
│                           │                             │
│  Registration Fee (₱)     │  Down Payment + Trade-In:   │
│  [500.00]────────────(5)  │  ₱0.00                      │
│                           │                             │
│  DOWN PAYMENT & TRADE-IN  │  Amount Financed:           │
│  ─────────────────────    │  ₱1,998,500.00              │
│  Down Payment (₱)         │                             │
│  [0.00]──────────────(6)  │  PENALTY & EXTRA PAYMENTS   │
│                           │  ─────────────────────────  │
│  Trade-In Value (₱)       │  Penalty Rate (%)           │
│  [0.00]──────────────(7)  │  [2.0]─────────────────(11) │
│                           │                             │
│  LOAN TERMS               │  Simulated Missed Payments  │
│  ──────────               │  [0]─────────────────────(12)│
│  Annual Interest Rate (%) │                             │
│  [6.5]───────────────(8)  │  Extra Payment Per Month(₱)│
│                           │  [0.00]──────────────────(13)│
│  Loan Term (Years)        │                             │
│  [5]─────────────────(9)  │                             │
│                           │                             │
│  Compounding Frequency    │                             │
│  [Monthly ▼]────────(10)  │                             │
├───────────────────────────┴─────────────────────────────┤
│          [Calculate Loan]────(14)     [Clear All]       │
└─────────────────────────────────────────────────────────┘

OBJECT MAPPING:
┌─────┬──────────────────────┬────────────────────────────┐
│ No  │ UI Element           │ Variable                   │
├─────┼──────────────────────┼────────────────────────────┤
│ (3) │ Car Price field      │ carPrice                   │
│ (4) │ Sales Tax Rate       │ salesTaxRate               │
│ (5) │ Registration Fee     │ registrationFee            │
│ (6) │ Down Payment         │ downPayment                │
│ (7) │ Trade-In Value       │ tradeInValue               │
│ (8) │ Annual Interest Rate │ apr                        │
│ (9) │ Loan Term            │ termYears                  │
│(10) │ Compounding Dropdown │ compounding                │
│(11) │ Penalty Rate         │ penaltyRate                │
│(12) │ Missed Payments      │ missedPayments             │
│(13) │ Extra Payment        │ extraPayment               │
│(14) │ Calculate Button     │ CALL calculateLoan()       │
└─────┴──────────────────────┴────────────────────────────┘
```

#### SCREEN 3: Loan Summary Dialog
```
┌─────────────────────────────────────────────────────────┐
│  Loan Summary - Vismera Inc.                       [X]  │
├─────────────────────────────────────────────────────────┤
│                Loan Calculation Results                 │
│                                                         │
│  ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ │
│  │Monthly Payment│ │Total Amount   │ │Total Interest │ │
│  │               │ │Paid           │ │               │ │
│  │ ₱39,102.95   │ │ ₱2,346,176.83│ │ ₱347,676.83  │ │
│  └───────────────┘ └───────────────┘ └───────────────┘ │
│                                                         │
│  ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ │
│  │Total Penalties│ │Interest Rate  │ │Loan Duration  │ │
│  │               │ │               │ │               │ │
│  │    ₱0.00     │ │    6.50%     │ │   5 Years    │ │
│  └───────────────┘ └───────────────┘ └───────────────┘ │
│                                                         │
│  [View Amortization Schedule]──(15)        [Close]      │
└─────────────────────────────────────────────────────────┘

OBJECT MAPPING:
┌──────────────────────┬──────────────────────────────────┐
│ UI Element           │ Variable                         │
├──────────────────────┼──────────────────────────────────┤
│ Monthly Payment      │ monthlyPayment                   │
│ Total Amount Paid    │ totalAmount                      │
│ Total Interest       │ totalInterest                    │
│ Total Penalties      │ totalPenalty                     │
│ Interest Rate        │ apr                              │
│ Loan Duration        │ termYears                        │
│ View Schedule (15)   │ IF schedule.length > 0 → display │
└──────────────────────┴──────────────────────────────────┘
```

#### SCREEN 4: Amortization Schedule
```
┌─────────────────────────────────────────────────────────┐
│  Amortization Schedule - Vismera Inc.             [─][X]│
├─────────────────────────────────────────────────────────┤
│  Amortization Schedule                                  │
│  Loan Amount: ₱1,998,500.00 | Rate: 6.50% | Term: 5 Yrs│
├─────────────────────────────────────────────────────────┤
│  Payment# │ Payment   │ Principal │ Interest │ Balance  │
│  ─────────┼───────────┼───────────┼──────────┼──────────│
│     1     │₱39,102.95 │₱28,277.74 │₱10,825.21│₱1,970,222│
│     2     │₱39,102.95 │₱28,430.91 │₱10,672.04│₱1,941,791│
│     3     │₱39,102.95 │₱28,584.91 │₱10,518.04│₱1,913,206│
│    ...    │    ...    │    ...    │   ...    │   ...    │
│    60     │₱39,102.95 │₱38,893.27 │  ₱209.68 │    ₱0.00 │
├─────────────────────────────────────────────────────────┤
│        [Import]──(16)    [Export]──(17)    [Close]      │
└─────────────────────────────────────────────────────────┘

OBJECT MAPPING:
┌──────────────────────┬──────────────────────────────────┐
│ UI Element           │ Variable                         │
├──────────────────────┼──────────────────────────────────┤
│ Payment #            │ periodNumber                     │
│ Payment              │ scheduledPayment                 │
│ Principal            │ principalPortion                 │
│ Interest             │ interestPortion                  │
│ Balance              │ closingBalance                   │
│ Import Button (16)   │ importedFileContent → parse      │
│ Export Button (17)   │ exportedFileContent → SHA256     │
└──────────────────────┴──────────────────────────────────┘
```

### KISS Principle Summary:
1. **Simple**: Only 4 screens total
2. **Intuitive**: Select car → Enter details → Calculate → View results
3. **Grouped**: Related fields are together
4. **Clear**: Labels describe exactly what to enter
5. **Responsive**: Real-time summary updates

### Presentation Flow:
1. Explain KISS principle (30 sec)
2. Walk through each screen (2 min)
3. Show object-to-variable mapping (2 min)
4. Demonstrate user flow (1 min)

---

## PERSON 5: COMPLETENESS OF DOCUMENTATION

### What You Need to Know:
- **All System Documents**: What exists and where
- **Technical Specs**: Formulas, data structures
- **User Guide**: How to use the system

### Documentation Checklist:

| Document | Status | Purpose |
|----------|--------|---------|
| README.md | ✓ | Project overview |
| CAR_LOAN_DOCUMENTATION.md | ✓ | Academic documentation |
| SYSTEM_DOCUMENTATION.md | ✓ | Technical specifications |
| TECHNICAL_DOCUMENT.md | ✓ | Detailed technical info |
| WEB_BUILDER_PROMPT.md | ✓ | Web implementation guide |
| DEFENSE_PRESENTATION_GUIDE.md | ✓ | This guide |

### System Features Summary:

#### Feature 1: Car Selection
- Display available cars from list
- Allow selection with visual feedback
- Auto-populate price on selection

#### Feature 2: Loan Calculator
- Input validation for all fields
- Real-time loan summary calculation
- Support for multiple compounding frequencies

#### Feature 3: Amortization Schedule
- Generate complete payment schedule
- Display principal/interest breakdown
- Show running balance

#### Feature 4: Secure Export
- Generate pipe-delimited TXT file
- SHA-256 hash for each record
- Tamper-evident format

#### Feature 5: Secure Import
- Read exported TXT file
- Verify SHA-256 hashes
- Alert on corrupted data

### Data Structures Used:

#### 1. Car List (In-Memory)
```
cars = [
    { id: 1, make: "Toyota", model: "Camry", year: 2024, price: 1850000 },
    { id: 2, make: "Honda", model: "Civic", year: 2024, price: 1350000 },
    { id: 3, make: "Mazda", model: "3", year: 2024, price: 1450000 }
]
```

#### 2. Loan Result Object
```
loanResult = {
    principal: 1998500.00,
    monthlyPayment: 39102.95,
    totalInterest: 347676.83,
    totalAmount: 2346176.83,
    EAR: 6.70,
    EMR: 0.54,
    termMonths: 60
}
```

#### 3. Amortization Schedule Array
```
amortization_schedule = [
    {
        periodNumber: 1,
        dueDate: "Jan 10, 2026",
        openingBalance: 1998500.00,
        scheduledPayment: 39102.95,
        principalPortion: 28277.74,
        interestPortion: 10825.21,
        closingBalance: 1970222.26,
        isPaid: false
    },
    // ... 59 more entries
]
```

#### 4. Exported File Format
```
1|Payment #1|Amortization|Active|₱1,998,500.00|6.50%|₱39,102.95|₱28,277.74|₱10,825.21|₱1,970,222.26|5 | A1B2C3D4...
----------------------------------------
2|Payment #2|Amortization|Active|₱1,998,500.00|6.50%|₱39,102.95|₱28,430.91|₱10,672.04|₱1,941,791.35|5 | E5F6G7H8...
----------------------------------------
```

### Error Handling Summary:

| Error | Message | Solution |
|-------|---------|----------|
| No car selected | "Please select a car first" | Select a car from grid |
| Invalid APR | "APR must be between 0-50%" | Enter valid APR |
| Invalid term | "Loan term must be greater than 0" | Enter positive years |
| No schedule | "Please calculate the loan first" | Click Calculate |
| Invalid hash | "File may be corrupted" | Use original export |

### Test Cases Summary:

| Test | Input | Expected Output |
|------|-------|-----------------|
| Basic Loan | ₱1,850,000, 6.5%, 5 years | ₱39,102.95/month |
| With Down Payment | + ₱200,000 down | Lower monthly payment |
| With Trade-In | + ₱100,000 trade-in | Further reduced payment |
| Different Compounding | Quarterly vs Monthly | Slightly different EAR |
| Export/Import | Export then Import | Same schedule displayed |

### Presentation Flow:
1. Show documentation list (30 sec)
2. Summarize system features (1 min)
3. Explain data structures (1 min)
4. Show error handling (1 min)
5. Present test case results (1 min)

---

## PRESENTATION ORDER & TIMING

| Order | Person | Topic | Time |
|-------|--------|-------|------|
| 1 | Person 1 | Logical Analysis | 5 min |
| 2 | Person 2 | Object Functionality & Simulation | 7 min |
| 3 | Person 3 | Flowchart & Pseudocode | 5 min |
| 4 | Person 4 | Form Design (KISS) | 5 min |
| 5 | Person 5 | Documentation Completeness | 5 min |
| - | All | Q&A | 3 min |
| **Total** | | | **30 min** |

---

## QUICK REFERENCE CARD

### Formulas at a Glance:
```
taxAmount = carPrice × (salesTaxRate / 100)
totalVehicleCost = carPrice + taxAmount + registrationFee
principal = totalVehicleCost - (downPayment + tradeInValue)
EAR = (1 + APR/m)^m - 1
EMR = (1 + EAR)^(1/12) - 1
EMI = P × [r(1+r)^n] / [(1+r)^n - 1]
totalAmount = EMI × n
totalInterest = totalAmount - principal
```

### Abbreviations:
- **APR** = Annual Percentage Rate
- **EAR** = Effective Annual Rate
- **EMR** = Effective Monthly Rate
- **EMI** = Equated Monthly Installment
- **KISS** = Keep It Simple, Stupid

### Key Variables:
- `selectedCar` - Currently selected car object
- `carPrice` - Price of selected car
- `principal` - Amount financed after down payment
- `amortization_schedule` - Array of payment records
- `scheduleGenerated` - Boolean flag for schedule status
- `exportedFileContent` - Generated TXT file content
- `importedFileContent` - Read file content for import

---

**Good luck with your defense presentation!**
