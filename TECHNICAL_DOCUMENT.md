# CAR LOAN AMORTIZATION CALCULATOR
## Technical Documentation

**Project Name:** Car Loan Amortization Calculator  
**Organization:** Vismerá Inc.  
**Version:** 5.0.0  
**Date:** December 2025  
**Platform:** Java Desktop Application (Swing)  
**Data Storage:** In-Memory (No Database Required)  

---

# TABLE OF CONTENTS

1. [Project Description](#1-project-description)
2. [Project Mechanics / Rules / Instructions](#2-project-mechanics--rules--instructions)
3. [Kind of Applied Algorithm](#3-kind-of-applied-algorithm)
4. [Procedures and Computational Arithmetic](#4-procedures-and-computational-arithmetic)
5. [Logic Formulation (Algorithm)](#5-logic-formulation-algorithm)
6. [Defining the Constructive / Destructive Variables](#6-defining-the-constructive--destructive-variables)

---

# 1. PROJECT DESCRIPTION

## 1.1 System Overview

The **Car Loan Amortization Calculator** is a Java Swing desktop application designed for automotive loan calculations. The system provides loan calculation capabilities with compound interest, penalty simulation, amortization schedule generation, and loan scenario comparison. This is a standalone calculator that requires no database - all data is processed in-memory.

## 1.2 Purpose and Objectives

The system is designed to:

- **Calculate loan payments** using compound interest formulas with configurable compounding frequencies
- **Generate amortization schedules** showing principal, interest, and balance breakdown for each payment period
- **Simulate penalties** for missed payments with interest capitalization
- **Compare loan scenarios** to help users find optimal financing terms
- **Browse sample cars** for demonstration purposes
- **Export schedules** to secure TXT files with SHA-256 hashing

## 1.3 Key Features

| Feature | Description |
|---------|-------------|
| **Loan Calculator** | Calculate monthly payments with compound interest |
| **Amortization Schedule** | Generate detailed payment schedules |
| **Penalty Simulation** | Simulate missed payments with penalty calculations |
| **Scenario Comparison** | Compare multiple loan options side-by-side |
| **Car Browser** | Browse sample car inventory for demonstration |
| **Secure Export** | Export amortization data to TXT with SHA-256 hashing |

## 1.4 System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                         │
│  ┌──────────────┐  ┌────────────────────────────────────────────┐  │
│  │  MainFrame   │  │       Panels & Dialog Windows          │  │
│  │ (Calculator) │  │ CarsPanel, CalculatePanel, ComparePanel │  │
│  └──────────────┘  └────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      CONTROLLER LAYER                           │
│  ┌────────────────┐  ┌──────────────┐  ┌────────────────────┐  │
│  │ LoanController │  │ CarController  │  │ComparisonController│  │
│  └────────────────┘  └──────────────┘  └────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        MODEL LAYER                              │
│  ┌──────────────────┐  ┌────────────────┐  ┌────────────────┐  │
│  │ LoanCalculation  │  │      Car       │  │  LoanScenario  │  │
│  │AmortizationEntry│  └────────────────┘  └────────────────┘  │
│  └──────────────────┘                                         │
└─────────────────────────────────────────────────────────────────┘
```

## 1.5 Technology Stack

| Component | Technology |
|-----------|------------|
| **Language** | Java 17+ |
| **UI Framework** | Java Swing |
| **Data Storage** | In-Memory (ArrayList) |
| **Build System** | Apache Ant (NetBeans) |
| **IDE** | NetBeans IDE |
| **Currency** | Philippine Peso (₱) |
| **Security** | SHA-256 for data export hashing |

---

# 2. PROJECT MECHANICS / RULES / INSTRUCTIONS

## 2.1 User Interface

The system provides a single unified interface for loan calculations:

### 2.1.1 Calculator Interface (MainFrame)

| Panel | Purpose | Access |
|-------|---------|--------|
| **Cars Panel** | Browse sample vehicles | Public |
| **Calculate Panel** | Calculate loan payments | Public |
| **Compare Panel** | Compare loan scenarios | Public |

## 2.2 Business Rules

### 2.2.1 Loan Creation Rules

```
RULE 1: Customer Selection
├── Customer must exist in the system
└── Customer information must be complete (name, contact, email)

RULE 2: Car Selection
├── Car must exist in inventory
├── Car must be marked as available
└── Car cannot be in an active loan

RULE 3: Loan Terms
├── Principal must be greater than 0
├── APR must be between 0% and 50%
├── Term must be between 6 and 84 months
├── Down payment cannot exceed car price
└── Trade-in value cannot exceed car price

RULE 4: Compounding Frequency
├── Monthly (12 periods/year)
├── Quarterly (4 periods/year)
├── Semi-Annually (2 periods/year)
└── Annually (1 period/year)
```

### 2.2.2 Payment Rules

```
RULE 1: Payment Recording
├── Payment must be associated with an active loan
├── Payment amount must be greater than 0
├── Payment date must be valid
└── Payment is applied to the earliest unpaid period

RULE 2: Penalty Calculation
├── Grace period: Configurable (default 5 days)
├── Penalty types:
│   ├── percent_per_day: Daily percentage of scheduled payment
│   ├── percent_per_month: Monthly percentage of scheduled payment
│   └── flat: Fixed amount per late payment
└── Penalties are added to the payment amount

RULE 3: Loan Closure
├── Loan automatically closes when fully paid
├── Loan can be manually closed by admin
└── Closed loans cannot receive new payments
```

### 2.2.3 Loan Status Definitions

| Status | Description | Allowed Actions |
|--------|-------------|-----------------|
| `active` | Loan is current and accepting payments | Record Payment, View Schedule, Close |
| `closed` | Loan manually closed | View Only |
| `paid_off` | Loan fully paid | View Only |
| `defaulted` | Loan in default status | View, Restructure |
| `archived` | Historical record | View Only |

## 2.3 System Usage Instructions

### 2.3.1 Creating a New Loan

```
Step 1: Navigate to Loan Management Panel
Step 2: Click "New Loan" button
Step 3: Select Customer from dropdown
Step 4: Select Car from dropdown (price auto-fills)
Step 5: Adjust Principal if needed
Step 6: Enter APR (Annual Percentage Rate)
Step 7: Select Term (months)
Step 8: Choose Compounding frequency
Step 9: Enter optional: Down Payment, Trade-in Value
Step 10: Configure Sales Tax Rate and Registration Fee
Step 11: Set Penalty Rate and Type
Step 12: Click "Calculate" to preview
Step 13: Click "Save" to create loan
```

### 2.3.2 Recording a Payment

```
Step 1: Navigate to Loan Management Panel
Step 2: Select the loan from the table
Step 3: Click "Record Payment" button
Step 4: Verify loan details displayed
Step 5: Enter payment amount
Step 6: Select payment date
Step 7: Add optional note
Step 8: Click "Record Payment" to save
```

---

# 3. KIND OF APPLIED ALGORITHM

## 3.1 Algorithm Classification

The system implements the following categories of algorithms:

### 3.1.1 Financial Algorithms

| Algorithm | Location | Purpose |
|-----------|----------|---------|
| Compound Interest Calculation | `LoanCalculation.java`, `Loan.java` | Calculate monthly payments |
| Amortization Schedule Generation | `LoanCalculation.java`, `LoanControllerDB.java` | Generate payment schedules |
| Penalty Calculation | `PaymentController.java` | Calculate late payment penalties |
| Effective Rate Conversion | `LoanCalculation.java` | Convert APR to effective rate |

### 3.1.2 Search Algorithms

| Algorithm | Location | Purpose |
|-----------|----------|---------|
| Linear Search | `CarController.java` | Search cars by make/model |
| SQL LIKE Search | `CarDAO.java`, `CustomerDAO.java` | Database text search |
| Filter by Status | `LoanDAO.java` | Filter loans by status |

### 3.1.3 Sorting Algorithms

| Algorithm | Location | Purpose |
|-----------|----------|---------|
| TableRowSorter | All Panel classes | Sort table data by column |
| ORDER BY (SQL) | All DAO classes | Database result ordering |

### 3.1.4 Data Structure Algorithms

| Algorithm | Location | Purpose |
|-----------|----------|---------|
| ArrayList Iteration | All Controllers | Process collections |
| HashMap Caching | `AdminSettingDAO.java` | Cache settings values |
| Queue-like Processing | `PaymentController.java` | Process payments in order |

### 3.1.5 Validation Algorithms

| Algorithm | Location | Purpose |
|-----------|----------|---------|
| Regex Matching | `ValidationUtils.java` | Email validation |
| Range Checking | `ValidationUtils.java` | Numeric bounds validation |
| Null/Empty Checking | All Controllers | Input validation |

## 3.2 Detailed Algorithm Descriptions

### 3.2.1 Compound Interest Monthly Payment Algorithm

**Location:** `LoanCalculation.java` - `calculateMonthlyPayment()`

**Mathematical Formula:**

$$M = P \times \frac{r(1+r)^n}{(1+r)^n - 1}$$

Where:
- $M$ = Monthly Payment
- $P$ = Principal (Amount Financed)
- $r$ = Monthly Interest Rate
- $n$ = Total Number of Payments

**Pseudocode:**

```
FUNCTION calculateMonthlyPayment()
    INPUT: principal, annualRate, compoundingPeriods, termMonths
    
    // Step 1: Calculate effective annual rate
    effectiveAnnualRate = (1 + (annualRate / compoundingPeriods))^compoundingPeriods - 1
    
    // Step 2: Convert to monthly rate
    monthlyRate = (1 + effectiveAnnualRate)^(1/12) - 1
    
    // Step 3: Handle zero interest case
    IF monthlyRate = 0 THEN
        RETURN principal / termMonths
    END IF
    
    // Step 4: Apply amortization formula
    numerator = monthlyRate × (1 + monthlyRate)^termMonths
    denominator = (1 + monthlyRate)^termMonths - 1
    
    monthlyPayment = principal × (numerator / denominator)
    
    RETURN monthlyPayment
END FUNCTION
```

### 3.2.2 Amortization Schedule Generation Algorithm

**Location:** `LoanCalculation.java` - `generateAmortizationSchedule()`

**Pseudocode:**

```
FUNCTION generateAmortizationSchedule()
    INPUT: principal, monthlyPayment, monthlyRate, termMonths, 
           missedPayments, penaltyRate, extraPayment
    
    schedule = NEW ArrayList<AmortizationEntry>()
    balance = principal
    cumulativePaid = 0
    cumulativeInterest = 0
    paymentNumber = 1
    
    WHILE balance > 0.01 AND paymentNumber <= termMonths + missedPayments DO
        // Calculate interest for this period
        interestPayment = balance × monthlyRate
        
        IF paymentNumber <= missedPayments THEN
            // Missed payment - apply penalty
            penalty = balance × (penaltyRate / 100)
            principalPayment = 0
            currentPayment = 0
            balance = balance + interestPayment  // Interest capitalizes
        ELSE
            // Normal payment
            currentPayment = MIN(monthlyPayment + extraPayment, balance + interestPayment)
            principalPayment = currentPayment - interestPayment
            IF principalPayment < 0 THEN principalPayment = 0
            balance = balance - principalPayment
            penalty = 0
        END IF
        
        // Update cumulative totals
        cumulativeInterest = cumulativeInterest + interestPayment
        cumulativePaid = cumulativePaid + currentPayment + penalty
        
        // Create entry
        entry = NEW AmortizationEntry(paymentNumber, currentPayment, 
                                      principalPayment, interestPayment, 
                                      penalty, balance, cumulativePaid)
        schedule.ADD(entry)
        
        paymentNumber = paymentNumber + 1
    END WHILE
    
    RETURN schedule
END FUNCTION
```

### 3.2.3 Penalty Calculation Algorithm

**Location:** `PaymentController.java` - `calculatePenalty()`

**Pseudocode:**

```
FUNCTION calculatePenalty(scheduledPayment, penaltyRate, penaltyType, dueDate, paymentDate)
    
    // Calculate days late
    daysLate = DAYS_BETWEEN(dueDate, paymentDate)
    
    IF daysLate <= 0 THEN
        RETURN 0  // No penalty for on-time or early payments
    END IF
    
    SWITCH penaltyType
        CASE "percent_per_day":
            penalty = scheduledPayment × (penaltyRate / 100) × daysLate
            
        CASE "percent_per_month":
            monthsLate = CEILING(daysLate / 30)
            penalty = scheduledPayment × (penaltyRate / 100) × monthsLate
            
        CASE "flat":
            penalty = penaltyRate  // Fixed amount
            
        DEFAULT:
            penalty = 0
    END SWITCH
    
    RETURN penalty
END FUNCTION
```

### 3.2.4 Loan Comparison Algorithm

**Location:** `ComparisonController.java` - `findBestDeal()`

**Pseudocode:**

```
FUNCTION findBestDeal(scenarios)
    IF scenarios IS EMPTY THEN
        RETURN null
    END IF
    
    bestScenario = null
    lowestTotalCost = INFINITY
    
    FOR EACH scenario IN scenarios DO
        IF scenario.totalCost < lowestTotalCost THEN
            lowestTotalCost = scenario.totalCost
            bestScenario = scenario
        END IF
    END FOR
    
    // Mark all scenarios
    FOR EACH scenario IN scenarios DO
        IF scenario = bestScenario THEN
            scenario.isBestDeal = TRUE
        ELSE
            scenario.isBestDeal = FALSE
        END IF
    END FOR
    
    RETURN bestScenario
END FUNCTION
```

---

# 4. PROCEDURES AND COMPUTATIONAL ARITHMETIC

## 4.1 Core Financial Formulas

### 4.1.1 Total Vehicle Cost Calculation

$$\text{Total Cost} = \text{Car Price} + \text{Sales Tax} + \text{Registration Fee}$$

Where:

$$\text{Sales Tax} = \text{Car Price} \times \frac{\text{Sales Tax Rate}}{100}$$

**Example Calculation:**
```
Car Price:        ₱2,500,000.00
Sales Tax Rate:   12%
Registration Fee: ₱25,000.00

Sales Tax = ₱2,500,000 × (12/100) = ₱300,000.00
Total Cost = ₱2,500,000 + ₱300,000 + ₱25,000 = ₱2,825,000.00
```

### 4.1.2 Amount Financed Calculation

$$\text{Amount Financed} = \text{Total Cost} - \text{Down Payment} - \text{Trade-in Value}$$

**Example Calculation:**
```
Total Cost:      ₱2,825,000.00
Down Payment:    ₱500,000.00
Trade-in Value:  ₱200,000.00

Amount Financed = ₱2,825,000 - ₱500,000 - ₱200,000 = ₱2,125,000.00
```

### 4.1.3 Effective Annual Rate Calculation

$$\text{EAR} = \left(1 + \frac{r}{n}\right)^n - 1$$

Where:
- $r$ = Annual Percentage Rate (as decimal)
- $n$ = Compounding periods per year

**Compounding Periods:**

| Frequency | Periods (n) |
|-----------|-------------|
| Monthly | 12 |
| Quarterly | 4 |
| Semi-Annually | 2 |
| Annually | 1 |

**Example Calculation (Quarterly Compounding):**
```
APR = 8% = 0.08
n = 4 (Quarterly)

EAR = (1 + 0.08/4)^4 - 1
EAR = (1 + 0.02)^4 - 1
EAR = (1.02)^4 - 1
EAR = 1.08243216 - 1
EAR = 0.08243216 = 8.24%
```

### 4.1.4 Monthly Interest Rate from Effective Rate

$$r_{\text{monthly}} = (1 + \text{EAR})^{1/12} - 1$$

**Example Calculation:**
```
EAR = 8.24% = 0.0824

r_monthly = (1 + 0.0824)^(1/12) - 1
r_monthly = (1.0824)^0.08333 - 1
r_monthly = 1.006623 - 1
r_monthly = 0.006623 = 0.6623% per month
```

### 4.1.5 Monthly Payment Calculation

$$M = P \times \frac{r(1+r)^n}{(1+r)^n - 1}$$

**Example Calculation:**
```
Principal (P) = ₱2,125,000.00
Monthly Rate (r) = 0.006623
Term (n) = 60 months

Numerator = 0.006623 × (1.006623)^60
          = 0.006623 × 1.4859
          = 0.009842

Denominator = (1.006623)^60 - 1
            = 1.4859 - 1
            = 0.4859

Monthly Payment = ₱2,125,000 × (0.009842 / 0.4859)
                = ₱2,125,000 × 0.02025
                = ₱43,031.25
```

### 4.1.6 Total Interest Calculation

$$\text{Total Interest} = (M \times n) - P$$

**Example Calculation:**
```
Monthly Payment = ₱43,031.25
Term = 60 months
Principal = ₱2,125,000.00

Total Payments = ₱43,031.25 × 60 = ₱2,581,875.00
Total Interest = ₱2,581,875.00 - ₱2,125,000.00 = ₱456,875.00
```

### 4.1.7 Amortization Period Breakdown

For each payment period $i$:

$$\text{Interest}_i = \text{Balance}_{i-1} \times r_{\text{monthly}}$$

$$\text{Principal}_i = M - \text{Interest}_i$$

$$\text{Balance}_i = \text{Balance}_{i-1} - \text{Principal}_i$$

**Example (First 3 Periods):**

| Period | Opening Balance | Payment | Interest | Principal | Closing Balance |
|--------|----------------|---------|----------|-----------|-----------------|
| 1 | ₱2,125,000.00 | ₱43,031.25 | ₱14,073.78 | ₱28,957.47 | ₱2,096,042.53 |
| 2 | ₱2,096,042.53 | ₱43,031.25 | ₱13,881.97 | ₱29,149.28 | ₱2,066,893.25 |
| 3 | ₱2,066,893.25 | ₱43,031.25 | ₱13,688.90 | ₱29,342.35 | ₱2,037,550.90 |

## 4.2 Penalty Calculations

### 4.2.1 Percent Per Day Penalty

$$\text{Penalty} = \text{Scheduled Payment} \times \frac{\text{Penalty Rate}}{100} \times \text{Days Late}$$

**Example:**
```
Scheduled Payment = ₱43,031.25
Penalty Rate = 2%
Days Late = 10

Penalty = ₱43,031.25 × (2/100) × 10 = ₱8,606.25
```

### 4.2.2 Percent Per Month Penalty

$$\text{Penalty} = \text{Scheduled Payment} \times \frac{\text{Penalty Rate}}{100} \times \lceil\frac{\text{Days Late}}{30}\rceil$$

**Example:**
```
Scheduled Payment = ₱43,031.25
Penalty Rate = 2%
Days Late = 45

Months Late = CEILING(45/30) = 2
Penalty = ₱43,031.25 × (2/100) × 2 = ₱1,721.25
```

### 4.2.3 Flat Rate Penalty

$$\text{Penalty} = \text{Fixed Amount}$$

**Example:**
```
Fixed Penalty = ₱1,000.00
Penalty = ₱1,000.00 (regardless of days late)
```

## 4.3 Loan Statistics Calculations

### 4.3.1 Remaining Balance

$$\text{Remaining Balance} = \text{Total Amount} - \text{Total Paid}$$

### 4.3.2 Payment Progress

$$\text{Progress \%} = \frac{\text{Total Paid}}{\text{Total Amount}} \times 100$$

### 4.3.3 Payments Remaining

$$\text{Payments Remaining} = \text{Term Months} - \text{Payments Made}$$

---

# 5. LOGIC FORMULATION (ALGORITHM)

## 5.1 Loan Creation Workflow

### 5.1.1 Flowchart Description

```
[START]
    │
    ▼
[Select Customer] ──NO──► [Display Error: "Select Customer"]
    │ YES                           │
    ▼                               │
[Select Car] ──NO──► [Display Error: "Select Car"]
    │ YES                           │
    ▼                               │
[Enter Principal] ──INVALID──► [Display Error: "Invalid Principal"]
    │ VALID                         │
    ▼                               │
[Enter APR] ──INVALID──► [Display Error: "Invalid APR (0-50%)"]
    │ VALID                         │
    ▼                               │
[Select Term] 
    │
    ▼
[Enter Optional Fields]
(Down Payment, Trade-in, Tax Rate, Reg Fee)
    │
    ▼
[Calculate Loan]
    │
    ▼
[Display Summary]
    │
    ▼
◇ Confirm Save? ──NO──► [Return to Form]
    │ YES
    ▼
[Save Loan to Database]
    │
    ▼
[Generate Amortization Schedule]
    │
    ▼
[Update Car Availability]
    │
    ▼
[Display Success Message]
    │
    ▼
[END]
```

### 5.1.2 Validation/Verification (VV) Logic

```
VV-LOAN-001: Customer Selection Validation
┌─────────────────────────────────────────┐
│  ◇ Is Customer Selected?               │
│  │                                      │
│  ├─ YES → Continue to next validation  │
│  │                                      │
│  └─ NO → Display: "Please select a     │
│          customer" → Return to form    │
└─────────────────────────────────────────┘

VV-LOAN-002: Car Selection Validation
┌─────────────────────────────────────────┐
│  ◇ Is Car Selected?                    │
│  │                                      │
│  ├─ YES → ◇ Is Car Available?          │
│  │        │                             │
│  │        ├─ YES → Continue            │
│  │        └─ NO → "Car already in      │
│  │                 active loan"        │
│  │                                      │
│  └─ NO → "Please select a car"         │
└─────────────────────────────────────────┘

VV-LOAN-003: Principal Validation
┌─────────────────────────────────────────┐
│  ◇ Is Principal > 0?                   │
│  │                                      │
│  ├─ YES → ◇ Is Principal Numeric?      │
│  │        │                             │
│  │        ├─ YES → Continue            │
│  │        └─ NO → "Invalid number      │
│  │                 format"             │
│  │                                      │
│  └─ NO → "Principal must be positive"  │
└─────────────────────────────────────────┘

VV-LOAN-004: APR Validation
┌─────────────────────────────────────────┐
│  ◇ Is APR between 0 and 50?            │
│  │                                      │
│  ├─ YES → Continue                     │
│  │                                      │
│  └─ NO → "APR must be between 0%       │
│          and 50%"                      │
└─────────────────────────────────────────┘

VV-LOAN-005: Down Payment Validation
┌─────────────────────────────────────────┐
│  ◇ Is Down Payment <= Total Cost?      │
│  │                                      │
│  ├─ YES → Continue                     │
│  │                                      │
│  └─ NO → "Down payment cannot exceed   │
│          total cost"                   │
└─────────────────────────────────────────┘
```

## 5.2 Payment Recording Workflow

### 5.2.1 Flowchart Description

```
[START]
    │
    ▼
[Select Loan]
    │
    ▼
◇ Is Loan Active? ──NO──► [Display Error: "Loan not active"]
    │ YES                           │
    ▼                               │
[Enter Payment Amount]
    │
    ▼
◇ Amount > 0? ──NO──► [Display Error: "Invalid amount"]
    │ YES                           │
    ▼                               │
[Select Payment Date]
    │
    ▼
[Calculate Days Late]
    │
    ▼
◇ Days Late > Grace Period?
    │
    ├─ YES ──► [Calculate Penalty]
    │              │
    ▼              ▼
◇ Days Late <= Grace Period
    │
    ▼
[Apply Payment to Earliest Unpaid Period]
    │
    ▼
[Update Amortization Row (mark as paid)]
    │
    ▼
[Insert Payment Record]
    │
    ▼
◇ Is Loan Fully Paid?
    │
    ├─ YES ──► [Update Loan Status to "paid_off"]
    │
    └─ NO ──► [Continue]
    │
    ▼
[Display Success Message]
    │
    ▼
[END]
```

### 5.2.2 Payment Validation VV Logic

```
VV-PAY-001: Loan Status Check
┌─────────────────────────────────────────┐
│  ◇ Is Loan Status = "active"?          │
│  │                                      │
│  ├─ YES → Continue                     │
│  │                                      │
│  └─ NO → "Cannot record payment for    │
│          inactive loan"                │
└─────────────────────────────────────────┘

VV-PAY-002: Payment Amount Validation
┌─────────────────────────────────────────┐
│  ◇ Is Amount > 0?                      │
│  │                                      │
│  ├─ YES → ◇ Is Amount Numeric?         │
│  │        │                             │
│  │        ├─ YES → Continue            │
│  │        └─ NO → "Invalid format"     │
│  │                                      │
│  └─ NO → "Amount must be positive"     │
└─────────────────────────────────────────┘

VV-PAY-003: Unpaid Period Check
┌─────────────────────────────────────────┐
│  ◇ Are there unpaid periods?           │
│  │                                      │
│  ├─ YES → Get earliest unpaid period   │
│  │                                      │
│  └─ NO → "All payments complete"       │
└─────────────────────────────────────────┘
```

## 5.3 Amortization Schedule Generation Pseudocode

```
ALGORITHM: GenerateAmortizationSchedule

INPUT:
    loan: Loan object with all terms
    
OUTPUT:
    List<AmortizationRow>: Complete payment schedule

BEGIN
    // Initialize variables
    rows ← empty list
    balance ← loan.principal
    startDate ← loan.startDate
    monthlyRate ← calculateMonthlyRate(loan.apr, loan.compounding)
    monthlyPayment ← loan.monthlyPayment
    
    // Generate each period
    FOR period FROM 1 TO loan.termMonths DO
        // Calculate due date
        dueDate ← addMonths(startDate, period)
        
        // Calculate interest for this period
        interestPaid ← balance × monthlyRate
        
        // Calculate principal portion
        principalPaid ← monthlyPayment - interestPaid
        
        // Ensure principal doesn't go negative
        IF principalPaid < 0 THEN
            principalPaid ← 0
        END IF
        
        // Calculate closing balance
        closingBalance ← balance - principalPaid
        
        // Ensure balance doesn't go negative
        IF closingBalance < 0 THEN
            closingBalance ← 0
            principalPaid ← balance
        END IF
        
        // Create row
        row ← NEW AmortizationRow()
        row.loanId ← loan.id
        row.periodIndex ← period
        row.dueDate ← dueDate
        row.openingBalance ← balance
        row.scheduledPayment ← monthlyPayment
        row.principalPaid ← principalPaid
        row.interestPaid ← interestPaid
        row.closingBalance ← closingBalance
        row.paid ← FALSE
        
        // Add to list
        rows.ADD(row)
        
        // Update balance for next iteration
        balance ← closingBalance
        
        // Exit if loan is paid off
        IF balance <= 0.01 THEN
            EXIT FOR
        END IF
    END FOR
    
    RETURN rows
END
```

## 5.4 Best Deal Comparison Pseudocode

```
ALGORITHM: FindBestLoanScenario

INPUT:
    scenarios: List<LoanScenario>
    
OUTPUT:
    LoanScenario: The scenario with lowest total cost

BEGIN
    IF scenarios IS EMPTY THEN
        RETURN null
    END IF
    
    // Initialize with first scenario
    bestScenario ← scenarios[0]
    lowestCost ← bestScenario.totalCost
    
    // Find minimum
    FOR EACH scenario IN scenarios DO
        IF scenario.totalCost < lowestCost THEN
            lowestCost ← scenario.totalCost
            bestScenario ← scenario
        END IF
    END FOR
    
    // Mark best deal flag
    FOR EACH scenario IN scenarios DO
        scenario.isBestDeal ← (scenario = bestScenario)
    END FOR
    
    // Calculate savings for display
    highestCost ← MAX(scenarios.totalCost)
    potentialSavings ← highestCost - lowestCost
    
    RETURN bestScenario
END
```

---

# 6. DEFINING THE CONSTRUCTIVE / DESTRUCTIVE VARIABLES

## 6.1 Model Class Variables

### 6.1.1 Car.java Variables

| Variable Name | Data Type | Purpose | Category |
|---------------|-----------|---------|----------|
| `id` | `int` | Unique identifier for the car | Primary Key |
| `make` | `String` | Manufacturer name (e.g., "Toyota") | Descriptive |
| `model` | `String` | Model name (e.g., "Camry") | Descriptive |
| `year` | `int` | Manufacturing year | Descriptive |
| `price` | `BigDecimal` | Vehicle price in PHP | Financial |
| `category` | `String` | Vehicle category (Sedan, SUV, etc.) | Classification |
| `color` | `String` | Vehicle color | Descriptive |
| `mpg` | `int` | Miles per gallon fuel efficiency | Technical |
| `imagePath` | `String` | Path to vehicle image file | Media |
| `notes` | `String` | Additional notes | Descriptive |
| `available` | `boolean` | Availability status | State |
| `createdAt` | `LocalDateTime` | Record creation timestamp | Audit |

### 6.1.2 Customer.java Variables

| Variable Name | Data Type | Purpose | Category |
|---------------|-----------|---------|----------|
| `id` | `int` | Unique identifier | Primary Key |
| `fullName` | `String` | Customer's full name | Identity |
| `contactNumber` | `String` | Phone number (+63 format) | Contact |
| `email` | `String` | Email address | Contact |
| `address` | `String` | Physical address | Contact |
| `createdAt` | `LocalDateTime` | Record creation timestamp | Audit |

### 6.1.3 Loan.java Variables

| Variable Name | Data Type | Purpose | Category |
|---------------|-----------|---------|----------|
| `id` | `int` | Unique loan identifier | Primary Key |
| `customerId` | `int` | Foreign key to customer | Relationship |
| `carId` | `int` | Foreign key to car | Relationship |
| `principal` | `BigDecimal` | Amount financed | Financial |
| `apr` | `BigDecimal` | Annual Percentage Rate | Financial |
| `compounding` | `String` | Compounding frequency | Terms |
| `termMonths` | `int` | Loan duration in months | Terms |
| `paymentFrequency` | `String` | Payment frequency | Terms |
| `startDate` | `LocalDate` | Loan start date | Terms |
| `penaltyRate` | `BigDecimal` | Late payment penalty rate | Penalty |
| `penaltyType` | `String` | Type of penalty calculation | Penalty |
| `gracePeriodDays` | `int` | Days before penalty applies | Penalty |
| `downPayment` | `BigDecimal` | Initial down payment | Financial |
| `tradeInValue` | `BigDecimal` | Trade-in value applied | Financial |
| `salesTaxRate` | `BigDecimal` | Sales tax percentage | Financial |
| `registrationFee` | `BigDecimal` | Registration fee amount | Financial |
| `monthlyPayment` | `BigDecimal` | Calculated monthly payment | Calculated |
| `totalInterest` | `BigDecimal` | Total interest over loan life | Calculated |
| `totalAmount` | `BigDecimal` | Total amount to be paid | Calculated |
| `status` | `String` | Current loan status | State |
| `customer` | `Customer` | Associated customer object | Navigation |
| `car` | `Car` | Associated car object | Navigation |
| `createdAt` | `LocalDateTime` | Record creation timestamp | Audit |
| `updatedAt` | `LocalDateTime` | Last update timestamp | Audit |

### 6.1.4 Payment.java Variables

| Variable Name | Data Type | Purpose | Category |
|---------------|-----------|---------|----------|
| `id` | `int` | Unique payment identifier | Primary Key |
| `loanId` | `int` | Foreign key to loan | Relationship |
| `paymentDate` | `LocalDate` | Date of payment | Transaction |
| `amount` | `BigDecimal` | Payment amount | Financial |
| `appliedToPeriod` | `int` | Period this payment covers | Application |
| `type` | `String` | Payment type | Classification |
| `penaltyApplied` | `BigDecimal` | Penalty amount included | Financial |
| `principalApplied` | `BigDecimal` | Amount applied to principal | Financial |
| `interestApplied` | `BigDecimal` | Amount applied to interest | Financial |
| `note` | `String` | Payment notes | Descriptive |
| `recordedBy` | `String` | User who recorded payment | Audit |
| `recordedAt` | `LocalDateTime` | Recording timestamp | Audit |

### 6.1.5 AmortizationRow.java Variables

| Variable Name | Data Type | Purpose | Category |
|---------------|-----------|---------|----------|
| `id` | `int` | Unique row identifier | Primary Key |
| `loanId` | `int` | Foreign key to loan | Relationship |
| `periodIndex` | `int` | Payment period number | Sequence |
| `dueDate` | `LocalDate` | Payment due date | Schedule |
| `openingBalance` | `BigDecimal` | Balance at period start | Financial |
| `scheduledPayment` | `BigDecimal` | Expected payment amount | Financial |
| `principalPaid` | `BigDecimal` | Principal portion | Financial |
| `interestPaid` | `BigDecimal` | Interest portion | Financial |
| `penaltyAmount` | `BigDecimal` | Any penalty applied | Financial |
| `extraPayment` | `BigDecimal` | Additional payment made | Financial |
| `closingBalance` | `BigDecimal` | Balance at period end | Financial |
| `paid` | `boolean` | Payment status | State |
| `paidDate` | `LocalDate` | Actual payment date | Transaction |
| `createdAt` | `LocalDateTime` | Record creation timestamp | Audit |

### 6.1.6 LoanCalculation.java Variables

| Variable Name | Data Type | Purpose | Category |
|---------------|-----------|---------|----------|
| `carPrice` | `double` | Vehicle price | Input |
| `salesTaxRate` | `double` | Tax rate percentage | Input |
| `registrationFee` | `double` | Registration fee | Input |
| `downPayment` | `double` | Down payment amount | Input |
| `tradeInValue` | `double` | Trade-in value | Input |
| `annualInterestRate` | `double` | APR percentage | Input |
| `loanTermYears` | `int` | Term in years | Input |
| `compoundingFrequency` | `String` | Compounding type | Input |
| `penaltyRate` | `double` | Penalty percentage | Input |
| `missedPayments` | `int` | Number of missed payments | Input |
| `extraPaymentPerMonth` | `double` | Extra payment amount | Input |
| `monthlyPayment` | `double` | Calculated payment | Output |
| `totalInterest` | `double` | Total interest | Output |
| `totalPenalties` | `double` | Total penalties | Output |
| `totalAmountPaid` | `double` | Total paid | Output |
| `amortizationSchedule` | `List<AmortizationEntry>` | Payment schedule | Output |

## 6.2 Controller Variables

### 6.2.1 Singleton Instance Variables

| Controller | Variable | Type | Purpose |
|------------|----------|------|---------|
| `LoanController` | `instance` | `LoanController` | Singleton instance |
| `LoanControllerDB` | `instance` | `LoanControllerDB` | Singleton instance |
| `CarController` | `instance` | `CarController` | Singleton instance |
| `CustomerController` | `instance` | `CustomerController` | Singleton instance |
| `PaymentController` | `instance` | `PaymentController` | Singleton instance |
| `ComparisonController` | `instance` | `ComparisonController` | Singleton instance |
| `ReportController` | `instance` | `ReportController` | Singleton instance |
| `SettingsController` | `instance` | `SettingsController` | Singleton instance |

### 6.2.2 CarController State Variables

| Variable | Type | Purpose |
|----------|------|---------|
| `cars` | `List<Car>` | In-memory car list |
| `useDatabaseMode` | `boolean` | Database vs hardcoded mode |
| `carDAO` | `CarDAO` | Data access object |

### 6.2.3 ComparisonController Variables

| Variable | Type | Purpose |
|----------|------|---------|
| `scenarios` | `List<LoanScenario>` | Comparison scenarios |
| `maxScenarios` | `int` | Maximum allowed scenarios (10) |

## 6.3 Constants and Enumerations

### 6.3.1 Loan Status Constants

```java
// Location: Loan.java
public static final String STATUS_ACTIVE = "active";
public static final String STATUS_CLOSED = "closed";
public static final String STATUS_PAID_OFF = "paid_off";
public static final String STATUS_DEFAULTED = "defaulted";
public static final String STATUS_ARCHIVED = "archived";
```

### 6.3.2 Compounding Frequency Constants

```java
// Location: Loan.java
public static final String MONTHLY = "monthly";
public static final String QUARTERLY = "quarterly";
public static final String SEMIANNUALLY = "semiannually";
public static final String ANNUALLY = "annually";
```

### 6.3.3 Payment Type Constants

```java
// Location: Payment.java
public static final String TYPE_REGULAR = "regular";
public static final String TYPE_PARTIAL = "partial";
public static final String TYPE_ADVANCE = "advance";
public static final String TYPE_LATE = "late";
public static final String TYPE_FULL = "full";
public static final String TYPE_EXTRA = "extra";
public static final String TYPE_EARLY_PAYOFF = "early_payoff";
public static final String TYPE_PENALTY_ONLY = "penalty_only";
```

### 6.3.4 Admin Setting Key Constants

```java
// Location: AdminSetting.java
public static final String KEY_DEFAULT_APR = "default_apr";
public static final String KEY_DEFAULT_TERM = "default_term";
public static final String KEY_DEFAULT_COMPOUNDING = "default_compounding";
public static final String KEY_DEFAULT_PENALTY_RATE = "default_penalty_rate";
public static final String KEY_DEFAULT_PENALTY_TYPE = "default_penalty_type";
public static final String KEY_DEFAULT_GRACE_PERIOD = "default_grace_period";
public static final String KEY_CURRENCY_SYMBOL = "currency_symbol";
public static final String KEY_DATE_FORMAT = "date_format";
public static final String KEY_APP_VERSION = "app_version";
```

### 6.3.5 UI Color Constants

```java
// Location: UIStyler.java
public static final Color PRIMARY_BLUE = new Color(37, 99, 235);      // #2563EB
public static final Color ACCENT_GREEN = new Color(16, 185, 129);     // #10B981
public static final Color WARNING_YELLOW = new Color(245, 158, 11);   // #F59E0B
public static final Color DANGER_RED = new Color(239, 68, 68);        // #EF4444
public static final Color TEXT_DARK = new Color(31, 41, 55);          // #1F2937
public static final Color TEXT_LIGHT = new Color(107, 114, 128);      // #6B7280
public static final Color BACKGROUND_LIGHT = new Color(243, 244, 246); // #F3F4F6
public static final Color BACKGROUND_WHITE = Color.WHITE;
public static final Color BORDER_GRAY = new Color(209, 213, 219);     // #D1D5DB
```

## 6.4 In-Memory Configuration

### 6.4.1 Sample Data Storage

The system uses in-memory ArrayList to store sample car data. No external database or configuration files are required.

| Variable | Type | Purpose |
|----------|------|---------|
| `cars` | `ArrayList<Car>` | Stores sample car inventory |
| `scenarios` | `ArrayList<LoanScenario>` | Stores comparison scenarios |

---

*[End of Part 1 - Calculator Application]*

---

**Document Information:**
- **Generated:** December 2025
- **Author:** Technical Documentation System
- **Version:** 5.0 (Calculator Only - No Database)
- **Status:** Complete
