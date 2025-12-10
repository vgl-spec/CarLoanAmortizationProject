# DOCUMENTATION BUILDER PROMPT
## Instructions for AI Documentation Generation

**Use this prompt template to generate complete technical documentation for the Car Loan Amortization Calculator system. Copy the sections below and provide to ChatGPT or Claude to generate professional documentation.**

---

# 🔧 DOCUMENTATION BUILDER CODE

Copy everything below this line and paste to AI (ChatGPT/Claude) to generate documentation:

---

## PROMPT START

You are a technical documentation specialist. Using the following system information, create comprehensive technical documentation following the EXACT structure and format shown below. This is for an academic submission.

---

## REQUIRED DOCUMENT FORMAT

### TITLE PAGE FORMAT:
```
# CAR LOAN AMORTIZATION CALCULATOR

**A Project Presented to the**  
**Faculty of the College of Computer Studies**  
**[University Name], [City]**

**In Partial Fulfillment**  
**of the Requirements for the Degree**  
**Bachelor of Science in Information Technology**

**Submitted by:**
- [Student Name 1]
- [Student Name 2]
- [Student Name 3]
- [Student Name 4]

**Submitted to:**  
[Professor Name]  
[Course Title] Professor

---
```

### TABLE OF CONTENTS FORMAT:
```
## Table of Contents

1. [Project Name](#project-name)
2. [Project Description](#project-description)
3. [Project Mechanics Rule/Instruction](#project-mechanics-ruleinstruction)
   - [System Setup](#system-setup)
   - [Computation Rules](#computation-rules)
   - [User Instructions](#user-instructions)
   - [Limitations](#limitations)
4. [Kind of Applied Algorithm](#kind-of-applied-algorithm)
5. [Procedures and Computational Arithmetic](#procedures-and-computational-arithmetic)
   - [Matrix Appendix](#matrix-appendix)
   - [Bullet Procedures](#bullet-procedures)
6. [Logic Formulation (Algorithm)](#logic-formulation-algorithm)
   - [Flowchart](#program-flowchart)
   - [Pseudocode of the Control Structure](#pseudocode-of-the-control-structure)
7. [Defining the Constructive/Destructive Variables](#defining-the-constructivedestructive-variables)
   - [Declaration and Data Types](#declaration-and-data-types)
8. [Formulation of Data Structure (Syntax and Semantics)](#formulation-of-data-structure-syntax-and-semantics)
   - [Functionalities](#functionalities)
   - [Parameters](#parameters)
   - [Arrays](#arrays)
9. [Iterative Simulation (Logical Algorithm)](#iterative-simulation-logical-algorithm)
10. [Screen Shot (UI Frame)](#screen-shot-ui-frame)
11. [Program Listing](#program-listing)
    - [Code With line Numbers](#code-with-line-numbers)
12. [Appendix](#appendix)
    - [References](#references)
```

---

## SYSTEM INFORMATION TO USE

### Project Overview
```
PROJECT NAME: Car Loan Amortization Calculator
ORGANIZATION: Vismerá Inc.
VERSION: 5.0.0
PLATFORM: Java Desktop Application (Swing)
LANGUAGE: Java 17+
BUILD SYSTEM: Apache Ant (NetBeans)
DATA STORAGE: In-Memory (ArrayList-based, no database)
CURRENCY: Philippine Peso (₱)
```

### Core Functionality
- Calculate loan payments with compound interest
- Generate amortization schedules showing principal, interest, and balance breakdown
- Simulate missed payments with penalties and interest capitalization
- Compare multiple loan scenarios side-by-side
- Browse sample cars for demonstration
- Export data to secure TXT files with SHA-256 hashing

### System Architecture (MVC Pattern)
```
PRESENTATION LAYER (Views):
- MainFrame.java          - Main window with CardLayout navigation
- CarsPanel.java          - Car browsing and selection
- CalculatePanel.java     - Loan calculation input form
- AmortizationScheduleFrame.java - Payment schedule display
- LoanSummaryDialog.java  - Results summary dialog

CONTROLLER LAYER:
- LoanController.java     - Loan calculations (Singleton)
- CarController.java      - Car data management (Singleton)
- ComparisonController.java - Scenario comparison (Singleton)

MODEL LAYER:
- Car.java               - Vehicle entity
- LoanCalculation.java   - Loan parameters and results
- AmortizationEntry.java - Single payment record
- LoanScenario.java      - Comparison scenario

UTILITY LAYER:
- FormatUtils.java       - Currency/number formatting
- ValidationUtils.java   - Input validation
- UIStyler.java          - UI styling
- CSVExporter.java       - Export functionality
- SecureFileExporter.java - SHA-256 hashed export
```

### Core Formulas

**Standard Amortization Formula:**
```
M = P × [r(1+r)^n] / [(1+r)^n - 1]

Where:
- M = Monthly Payment
- P = Principal (Amount Financed)
- r = Effective Monthly Interest Rate
- n = Total Number of Payments (months)
```

**Effective Annual Rate (EAR):**
```
EAR = (1 + r_annual/m)^m - 1

Where:
- r_annual = Annual Interest Rate (decimal)
- m = Compounding periods per year
```

**Effective Monthly Rate:**
```
r_monthly = (1 + EAR)^(1/12) - 1
```

**Penalty Calculation:**
```
Penalty = Outstanding_Balance × (Penalty_Rate / 100)
```

**Total Vehicle Cost:**
```
Total_Cost = Car_Price + (Car_Price × Tax_Rate/100) + Registration_Fee
```

**Amount Financed:**
```
Amount_Financed = Total_Cost - Down_Payment - Trade_In_Value
```

### Key Variables
```java
// LoanCalculation Variables
double carPrice;                    // Vehicle base price
double salesTaxRate;                // Tax percentage (e.g., 8.0)
double registrationFee;             // Fixed registration cost
double downPayment;                 // Initial payment
double tradeInValue;                // Trade-in deduction
double annualInterestRate;          // APR percentage
int loanTermYears;                  // Loan duration in years
String compoundingFrequency;        // "Monthly"/"Quarterly"/"Semi-Annually"/"Annually"
double penaltyRate;                 // Late payment penalty %
int missedPayments;                 // Missed payments count
double extraPaymentPerMonth;        // Additional principal payment

// Calculated Results
double monthlyPayment;              // Calculated payment
double totalInterest;               // Total interest
double totalPenalties;              // Total penalties
List<AmortizationEntry> amortizationSchedule; // Payment schedule

// AmortizationEntry Variables
int paymentNumber;                  // Payment sequence
double payment;                     // Payment amount
double principal;                   // Principal portion
double interest;                    // Interest portion
double penalty;                     // Penalty amount
double balance;                     // Remaining balance
double totalPaid;                   // Cumulative paid

// Car Variables
int id;                             // Unique ID
String make;                        // Manufacturer
String model;                       // Model name
int year;                           // Model year
BigDecimal price;                   // Vehicle price
String category;                    // Type (Sedan/SUV/etc.)
String color;                       // Color
int mpg;                            // Fuel efficiency
```

---

## GENERATE DOCUMENTATION WITH THESE 12 SECTIONS

### Section 1: Project Name
**Format:**
```
## Project Name

**Car Loan Amortization Calculator**
```
- Simple, one-line project name with bold formatting

---

### Section 2: Project Description
**Format:**
```
## Project Description

The Car Loan Amortization Calculator is designed to [describe purpose]. The system provides [list key features] for [target users].
```
- Write 1-2 paragraphs describing the system
- Include: purpose, key features, target users
- Mention: Java Swing, MVC architecture, Philippine Peso currency

---

### Section 3: Project Mechanics Rule/Instruction
**Format with subsections:**

#### 3.1 System Setup
- Installation requirements
- Software prerequisites (Java 17+, NetBeans)

#### 3.2 Computation Rules
- Amortization formula application
- Compound interest frequency options
- Penalty calculation rules
- Input range validations

#### 3.3 User Instructions
- **Input:** What user enters (car price, interest rate, term, etc.)
- **Process:** How system calculates
- **Output:** What user receives (amortization schedule, summary)
- **Error Handling:** How errors are handled

#### 3.4 Limitations
- System constraints
- Scope boundaries

---

### Section 4: Kind of Applied Algorithm
**Format:**
```
## Kind of Applied Algorithm

### [Algorithm Name]
- **Application:** [How it's used]
- **Example:** [Specific example]
- **Benefit:** [Why it helps]
```

Include these algorithms:
1. **Standard Amortization Algorithm** - For monthly payment calculation
2. **Compound Interest Algorithm** - For effective rate calculation
3. **Iteration/Loop Algorithm** - For schedule generation
4. **Validation Algorithms** - For input checking
5. **Design Patterns** - MVC, Singleton, Factory, Observer, Strategy

---

### Section 5: Procedures and Computational Arithmetic

#### 5.1 Matrix Appendix
**Create table format:**
```
| Input Variable | Symbol | Formula | Output |
|----------------|--------|---------|--------|
| Car Price | P_car | P_car × Tax_Rate | Tax Amount |
| ... | ... | ... | ... |
```

Include matrix for:
- Vehicle cost calculation
- Amount financed calculation
- Monthly payment calculation
- Interest/Principal split
- Penalty calculation

#### 5.2 Bullet Procedures
**Step-by-step format:**
```
**Step 1: Input Vehicle Data**
- User enters car price, tax rate, registration fee
- System validates numeric input > 0

**Step 2: Calculate Total Cost**
- Total = Car Price + Tax Amount + Registration Fee

**Step 3: Calculate Amount Financed**
- Amount = Total Cost - Down Payment - Trade-In

**Step 4: Determine Interest Rate**
- Convert annual rate to effective monthly rate
- Apply compounding frequency

**Step 5: Compute Monthly Payment**
- Apply amortization formula
- M = P × [r(1+r)^n] / [(1+r)^n - 1]

**Step 6: Generate Schedule**
- Loop through each month
- Calculate interest, principal, balance

**Step 7: Apply Penalties (if any)**
- Check for missed payments
- Calculate penalty amount
- Capitalize unpaid interest

**Step 8: Display Results**
- Show summary dialog
- Enable schedule view and export
```

---

### Section 6: Logic Formulation (Algorithm)

#### 6.1 Program Flowchart
**Create ASCII flowchart:**
```
┌─────────────────────┐
│       START         │
└──────────┬──────────┘
           ▼
┌─────────────────────┐
│   INPUT: Car Price, │
│   Rate, Term, etc.  │
└──────────┬──────────┘
           ▼
      ◇─────────◇
     ╱           ╲
    ╱  Valid      ╲
   ╱   Input?      ╲───NO──→ [Error Message]
   ╲              ╱              │
    ╲            ╱               │
     ╲          ╱                ▼
      ◇────────◇          [Return to Input]
           │
          YES
           ▼
┌─────────────────────┐
│ Calculate Total Cost│
└──────────┬──────────┘
           ▼
[Continue flowchart...]
```

Include validation diamonds (◇) for:
- Input validation
- Rate validation (0-50%)
- Term validation
- Down payment validation
- Balance > 0 check in loop

#### 6.2 Pseudocode of the Control Structure
**Format:**
```
PROGRAM CarLoanAmortizationCalculator

CLASS LoanCalculation
    VARIABLES:
        carPrice ← 0
        salesTaxRate ← 0
        registrationFee ← 0
        downPayment ← 0
        tradeInValue ← 0
        annualInterestRate ← 0
        loanTermYears ← 0
        compoundingFrequency ← "Monthly"
        penaltyRate ← 0
        missedPayments ← 0
        extraPaymentPerMonth ← 0
        amortizationSchedule ← empty list
    
    CONSTANT COMPOUNDING_MONTHLY ← 12
    CONSTANT COMPOUNDING_QUARTERLY ← 4
    CONSTANT COMPOUNDING_SEMI_ANNUALLY ← 2
    CONSTANT COMPOUNDING_ANNUALLY ← 1
    
    METHOD calculateTotalCost()
        taxAmount ← carPrice × (salesTaxRate / 100)
        RETURN carPrice + taxAmount + registrationFee
    
    METHOD calculateAmountFinanced()
        totalCost ← calculateTotalCost()
        reductions ← downPayment + tradeInValue
        RETURN MAX(0, totalCost - reductions)
    
    METHOD getCompoundingPeriodsPerYear()
        SWITCH compoundingFrequency
            CASE "Annually": RETURN 1
            CASE "Semi-Annually": RETURN 2
            CASE "Quarterly": RETURN 4
            CASE "Monthly": RETURN 12
            DEFAULT: RETURN 12
        END SWITCH
    
    METHOD calculateMonthlyPayment()
        principal ← calculateAmountFinanced()
        
        IF principal ≤ 0 THEN
            RETURN 0
        END IF
        
        totalMonths ← loanTermYears × 12
        
        IF totalMonths ≤ 0 THEN
            RETURN 0
        END IF
        
        annualRate ← annualInterestRate / 100
        compoundingPeriods ← getCompoundingPeriodsPerYear()
        
        // Calculate Effective Annual Rate
        EAR ← (1 + annualRate / compoundingPeriods)^compoundingPeriods - 1
        
        // Convert to monthly rate
        monthlyRate ← (1 + EAR)^(1/12) - 1
        
        IF monthlyRate = 0 THEN
            RETURN principal / totalMonths
        END IF
        
        // Amortization formula
        numerator ← monthlyRate × (1 + monthlyRate)^totalMonths
        denominator ← (1 + monthlyRate)^totalMonths - 1
        
        RETURN principal × (numerator / denominator)
    
    METHOD generateAmortizationSchedule()
        schedule ← empty list
        principal ← calculateAmountFinanced()
        
        IF principal ≤ 0 THEN
            RETURN schedule
        END IF
        
        totalMonths ← loanTermYears × 12
        payment ← calculateMonthlyPayment()
        adjustedPayment ← payment + extraPaymentPerMonth
        
        // Calculate monthly rate
        annualRate ← annualInterestRate / 100
        compoundingPeriods ← getCompoundingPeriodsPerYear()
        EAR ← (1 + annualRate / compoundingPeriods)^compoundingPeriods - 1
        monthlyRate ← (1 + EAR)^(1/12) - 1
        
        balance ← principal
        cumulativePaid ← 0
        cumulativeInterest ← 0
        cumulativePenalties ← 0
        paymentNum ← 1
        
        WHILE balance > 0.01 AND paymentNum ≤ totalMonths + missedPayments
            interestPayment ← balance × monthlyRate
            penalty ← 0
            
            IF missedPayments > 0 AND paymentNum ≤ missedPayments THEN
                // Missed payment
                penalty ← balance × (penaltyRate / 100)
                principalPayment ← 0
                currentPayment ← 0
                balance ← balance + interestPayment  // Capitalize interest
                cumulativePenalties ← cumulativePenalties + penalty
            ELSE
                // Normal payment
                currentPayment ← MIN(adjustedPayment, balance + interestPayment)
                principalPayment ← currentPayment - interestPayment
                
                IF principalPayment < 0 THEN
                    principalPayment ← 0
                END IF
                
                balance ← balance - principalPayment
                
                IF balance < 0 THEN
                    balance ← 0
                END IF
            END IF
            
            cumulativeInterest ← cumulativeInterest + interestPayment
            cumulativePaid ← cumulativePaid + currentPayment + penalty
            
            entry ← NEW AmortizationEntry(
                paymentNum,
                currentPayment,
                principalPayment,
                interestPayment,
                penalty,
                balance,
                cumulativePaid
            )
            
            ADD entry TO schedule
            paymentNum ← paymentNum + 1
        END WHILE
        
        RETURN schedule

END CLASS
```

---

### Section 7: Defining the Constructive/Destructive Variables

#### 7.1 Declaration and Data Types
**Table format:**
```
| Variable Name | Description | Type | Constructive/Destructive |
|---------------|-------------|------|--------------------------|
| `carPrice` | Vehicle base price | double | Constructive |
| `monthlyPayment` | Calculated payment amount | double | Destructive (computed) |
| `balance` | Remaining loan balance | double | Destructive (decreases) |
| `totalInterest` | Accumulated interest | double | Constructive (accumulates) |
| ... | ... | ... | ... |
```

**Include all variables from:**
- LoanCalculation class (inputs and outputs)
- AmortizationEntry class
- Car class
- UI components (JTextField, JLabel, JButton, JComboBox, JTable)

---

### Section 8: Formulation of Data Structure (Syntax and Semantics)

#### 8.1 Functionalities
**Table format:**
```
| Function Name | Description |
|---------------|-------------|
| `calculateTotalCost()` | Computes total vehicle cost including tax and fees |
| `calculateAmountFinanced()` | Computes loan principal after down payment |
| `calculateMonthlyPayment()` | Computes monthly payment using amortization formula |
| `generateAmortizationSchedule()` | Creates complete payment schedule |
| ... | ... |
```

#### 8.2 Parameters
**Table format:**
```
| Function | Parameters | Purpose |
|----------|------------|---------|
| `createLoanCalculation(...)` | carPrice: double, salesTaxRate: double, ... | Creates loan calculation object |
| `validatePositiveNumber(input)` | input: String | Validates numeric input > 0 |
| ... | ... | ... |
```

#### 8.3 Arrays
**Format:**
```java
// Compounding frequency options
String[] frequencies = {"Monthly", "Quarterly", "Semi-Annually", "Annually"};

// Compounding periods mapping
int[] compoundingPeriods = {12, 4, 2, 1};  // Corresponding to frequencies

// Amortization schedule
List<AmortizationEntry> amortizationSchedule = new ArrayList<>();

// Sample car data
List<Car> cars = new ArrayList<>();
```

**Matrix meaning table:**
```
| Index | Frequency | Periods/Year |
|-------|-----------|--------------|
| 0 | Monthly | 12 |
| 1 | Quarterly | 4 |
| 2 | Semi-Annually | 2 |
| 3 | Annually | 1 |
```

---

### Section 9: Iterative Simulation (Logical Algorithm)

**Sample Simulation Table:**
```
## Iterative Simulation (Logical Algorithm)

**Sample Loan Calculation:**
- Car Price: ₱1,500,000
- Tax Rate: 8%
- Registration Fee: ₱500
- Down Payment: ₱300,000
- Trade-In: ₱0
- Interest Rate: 6.5%
- Term: 5 years (60 months)
- Compounding: Monthly

**Calculated Values:**
- Total Cost: ₱1,620,500
- Amount Financed: ₱1,320,500
- Monthly Payment: ₱25,823.45
- Total Interest: ₱228,907.00

**Amortization Schedule (First 5 Payments):**

| Month | Payment | Principal | Interest | Balance |
|-------|---------|-----------|----------|---------|
| 1 | ₱25,823.45 | ₱18,667.73 | ₱7,155.72 | ₱1,301,832.27 |
| 2 | ₱25,823.45 | ₱18,768.92 | ₱7,054.53 | ₱1,283,063.35 |
| 3 | ₱25,823.45 | ₱18,870.67 | ₱6,952.78 | ₱1,264,192.68 |
| 4 | ₱25,823.45 | ₱18,972.98 | ₱6,850.47 | ₱1,245,219.70 |
| 5 | ₱25,823.45 | ₱19,075.85 | ₱6,747.60 | ₱1,226,143.85 |
```

---

### Section 10: Screen Shot (UI Frame)

**Format with UI component table:**
```
## Screen Shot (UI Frame)

*[Screenshot images would be placed here with arrows pointing to each component]*

| No. | Object (Variable) | Function |
|-----|-------------------|----------|
| 1. | carPriceField | Input Car Price<br>If carPrice is not numeric or ≤ 0<br>Then MsgBox("Please enter a valid car price") |
| 2. | salesTaxField | Input Sales Tax Rate (%)<br>If rate < 0 or > 100<br>Then MsgBox("Invalid tax rate") |
| 3. | registrationFeeField | Input Registration Fee |
| 4. | downPaymentField | Input Down Payment<br>If downPayment > totalCost<br>Then MsgBox("Down payment exceeds total cost") |
| 5. | tradeInValueField | Input Trade-In Value |
| 6. | annualInterestRateField | Input APR (%)<br>If rate < 0 or > 50<br>Then MsgBox("Interest rate must be 0-50%") |
| 7. | loanTermYearsField | Input Loan Term (Years)<br>If term < 1 or > 30<br>Then MsgBox("Term must be 1-30 years") |
| 8. | compoundingFrequencyCombo | Select Compounding Frequency<br>Options: Monthly, Quarterly, Semi-Annually, Annually |
| 9. | penaltyRateField | Input Penalty Rate (%) |
| 10. | missedPaymentsField | Input Number of Missed Payments |
| 11. | extraPaymentField | Input Extra Payment Per Month |
| 12. | btnCalculate | Calculate Loan<br>If all inputs valid<br>Then computeLoan() and showSummary()<br>Else showValidationErrors() |
| 13. | btnViewSchedule | View Amortization Schedule<br>Opens AmortizationScheduleFrame |
| 14. | btnExport | Export to File<br>Saves schedule as TXT with SHA-256 hash |
| 15. | carsGrid | Display Car Cards<br>Click to select car for calculation |
| 16. | searchField | Search Cars<br>Filters displayed cars by make/model/category |
| 17. | btnContinue | Continue to Calculator<br>If car selected Then navigateToCalculatePanel() |
```

---

### Section 11: Program Listing

**Format with line numbers:**
```
## Program Listing

### Code With line Numbers

**LoanCalculation.java**
```java
1   package com.vismera.models;
2   
3   import java.util.ArrayList;
4   import java.util.List;
5   
6   public class LoanCalculation {
7       private double carPrice;
8       private double salesTaxRate;
9       private double registrationFee;
...
```

**Include complete source code for:**
1. LoanCalculation.java
2. AmortizationEntry.java
3. LoanController.java
4. ValidationUtils.java
5. FormatUtils.java

---

### Section 12: Appendix

#### References
**Format:**
```
## Appendix

### References

[Author Last, First Initial]. ([Year]). *[Title]*. [Publisher/URL].

Examples:
- Oracle. (2020). *Java Platform, Standard Edition documentation*. https://docs.oracle.com/javase
- Oracle. (2020). *Java Swing tutorial: Building graphical user interfaces*. https://docs.oracle.com/javase/tutorial/uiswing
- Cormen, T. H., Leiserson, C. E., Rivest, R. L., & Stein, C. (2022). *Introduction to algorithms* (4th ed.). MIT Press.
- Sommerville, I. (2020). *Software engineering* (11th ed.). Pearson.
```

---

## OUTPUT FORMAT REQUIREMENTS

1. Use proper Markdown headings (##, ###)
2. Use tables for structured data
3. Use code blocks with ```java for code
4. Include ASCII flowcharts with diamond symbols (◇) for validation
5. Number all code listings with line numbers
6. Maintain academic/formal tone
7. Follow the exact section order from Table of Contents
8. Include "---" separators between major sections

---

## END OF PROMPT

---

# 📋 QUICK REFERENCE

## Section-by-Section Prompts

If the full prompt is too long, use these individual section prompts:

### Sections 1-3:
```
Generate Project Name, Description, and Mechanics sections for Car Loan Amortization Calculator (Java Swing, MVC, Philippine Peso). Include: System Setup, Computation Rules (amortization formula, compound interest), User Instructions (Input/Process/Output/Error Handling), Limitations.
```

### Sections 4-5:
```
Document algorithms for car loan amortization: Standard Amortization M = P × [r(1+r)^n]/[(1+r)^n-1], Compound Interest EAR = (1+r/m)^m-1, Iteration for schedule. Create Matrix Appendix table and Bullet Procedures (Step 1-8).
```

### Section 6:
```
Create ASCII flowchart with validation diamonds (◇) for loan calculation flow. Write pseudocode for LoanCalculation class with methods: calculateTotalCost(), calculateAmountFinanced(), calculateMonthlyPayment(), generateAmortizationSchedule().
```

### Section 7:
```
Create Constructive/Destructive Variables table with columns: Variable Name, Description, Type, Constructive/Destructive. Include: carPrice, salesTaxRate, monthlyPayment, balance, totalInterest, amortizationSchedule, UI components.
```

### Section 8:
```
Document data structures: Functionalities table (function name, description), Parameters table (function, parameters, purpose), Arrays (compounding frequencies, amortization schedule List<AmortizationEntry>).
```

### Sections 9-10:
```
Create Iterative Simulation with sample loan (₱1,500,000 car, 6.5% rate, 5 years) showing first 5 payments. Create UI Frame table with columns: No., Object (Variable), Function with validation pseudocode.
```

### Sections 11-12:
```
Format Java source code with line numbers for: LoanCalculation.java, AmortizationEntry.java, LoanController.java. Add References section with Oracle Java docs, algorithm textbooks, software engineering references.
```

---

**Document Version:** 2.0 (Aligned with PhilHealth Reference Format)  
**Created:** December 2024  
**For:** Car Loan Amortization Calculator v5.0.0
