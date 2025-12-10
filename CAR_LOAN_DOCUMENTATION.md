# CAR LOAN AMORTIZATION CALCULATOR

**A Project Presented to the**  
**Faculty of the College of Computer Studies**  
**Quezon City University, Quezon City**

**In Partial Fulfillment**  
**of the Requirements for the Degree**  
**Bachelor of Science in Information Technology**

**Submitted by:**
- [Student Name 1]
- [Student Name 2]
- [Student Name 3]
- [Student Name 4]

**Submitted to:**  
Dr. CAYETANO A. NICOLAS  
DSA Professor

---

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

---

## Project Name

**Car Loan Amortization Calculator**

---

## Project Description

The Car Loan Amortization Calculator is a Java Swing desktop application designed to automate the computation of car loan payments using compound interest formulas. The system calculates monthly payments based on the standard amortization formula, generates detailed payment schedules showing principal and interest breakdown, and simulates penalty scenarios for missed payments.

The application follows the Model-View-Controller (MVC) architectural pattern and provides comprehensive loan analysis features including multiple compounding frequency options (Monthly, Quarterly, Semi-Annually, Annually), penalty simulation with interest capitalization, and secure data export with SHA-256 hashing. All monetary values are displayed in Philippine Peso (₱) format for local market relevance.

---

## Project Mechanics Rule/Instruction

### System Setup

- The application requires Java Runtime Environment (JRE) 17 or higher installed on the computer.
- Built using Apache Ant build system in NetBeans IDE.
- No database installation required - all data is processed in-memory using ArrayList data structures.

### Computation Rules

- Monthly payments are computed using the Standard Amortization Formula:
  - **M = P × [r(1+r)^n] / [(1+r)^n - 1]**
- The system supports four compounding frequencies:
  - Monthly (12 periods/year)
  - Quarterly (4 periods/year)
  - Semi-Annually (2 periods/year)
  - Annually (1 period/year)
- Effective Annual Rate (EAR) is calculated based on compounding frequency
- Penalties are computed as a percentage of outstanding balance when payments are missed
- Interest is capitalized (added to principal) for missed payment periods

### User Instructions

- **Input:** User enters Car Price, Sales Tax Rate (%), Registration Fee, Down Payment, Trade-In Value, Annual Interest Rate (%), Loan Term (Years), Compounding Frequency, Penalty Rate (%), Number of Missed Payments, and Extra Payment Per Month.
- **Process:** The system validates all inputs, calculates total vehicle cost, determines amount financed, computes effective interest rate based on compounding frequency, and generates the complete amortization schedule.
- **Output:** A loan summary displaying monthly payment, total interest, total cost, and a detailed amortization schedule showing payment-by-payment breakdown. Results can be exported to a secure TXT file with SHA-256 hash verification.
- **Error Handling:** If input data is missing, invalid, or out of acceptable range, the system displays appropriate error messages and prompts user correction before proceeding.

### Limitations

- The system is designed specifically for car loan calculations with fixed-rate amortization.
- Does not support variable interest rate loans or balloon payments.
- Export functionality is limited to TXT format.
- Currency is fixed to Philippine Peso (₱).

---

## Kind of Applied Algorithm

### Standard Amortization Algorithm

- **Application:** Used to calculate the fixed monthly payment amount that will fully amortize the loan over the specified term.
- **Example:** For a ₱1,000,000 loan at 6% annual interest for 5 years, the algorithm calculates the exact monthly payment of ₱19,332.80 that pays off principal and interest.
- **Benefit:** Provides accurate, mathematically proven payment amounts ensuring the loan is fully paid at term end.

### Compound Interest Algorithm

- **Application:** Converts annual interest rate to effective monthly rate based on selected compounding frequency.
- **Example:** A 12% annual rate compounded quarterly results in EAR = (1 + 0.12/4)^4 - 1 = 12.55%, then converted to monthly rate.
- **Benefit:** Accurately reflects the true cost of borrowing based on how frequently interest compounds.

### Iteration Algorithm (While Loop)

- **Application:** Generates the complete amortization schedule by iterating through each payment period.
- **Example:** The while loop continues calculating interest, principal, and remaining balance until balance reaches zero or maximum payments exceeded.
- **Benefit:** Dynamically handles variable scenarios including missed payments and extra payments.

### Validation Algorithm

- **Application:** Input validation ensures all user-entered data is numeric, within acceptable ranges, and logically consistent.
- **Example:** Interest rate must be between 0-50%, down payment cannot exceed total cost, term must be positive integer.
- **Benefit:** Prevents calculation errors and ensures data integrity.

### Design Patterns Applied

- **MVC (Model-View-Controller):** Separates data (Model), user interface (View), and business logic (Controller).
- **Singleton:** Controllers use single instance pattern for centralized data management.
- **Factory:** LoanController.createLoanCalculation() encapsulates object creation.

---

## Procedures and Computational Arithmetic

### Matrix Appendix

| Input Variable | Symbol | Formula | Output |
|----------------|--------|---------|--------|
| Car Price | P_car | P_car × (Tax_Rate / 100) | Tax Amount |
| Car Price + Tax + Reg Fee | - | P_car + Tax + Reg_Fee | Total Cost |
| Total Cost - Down - Trade | P | Total - Down - Trade | Amount Financed |
| Annual Rate / Compounding Periods | r_period | r_annual / m | Period Rate |
| (1 + r_period)^m - 1 | EAR | (1 + r/m)^m - 1 | Effective Annual Rate |
| (1 + EAR)^(1/12) - 1 | r | (1 + EAR)^(1/12) - 1 | Monthly Rate |
| P × [r(1+r)^n] / [(1+r)^n - 1] | M | Amortization Formula | Monthly Payment |
| Balance × Monthly Rate | I | Balance × r | Interest Payment |
| Payment - Interest | Prin | M - I | Principal Payment |
| Balance - Principal | B_new | Balance - Prin | New Balance |
| Balance × (Penalty_Rate / 100) | Pen | Balance × Pen_Rate | Penalty Amount |

### Bullet Procedures

**Step 1: Input Vehicle Data**
- User enters car price, sales tax rate, and registration fee
- System validates that car price is numeric and greater than 0
- System validates tax rate is between 0-100%

**Step 2: Input Payment Reductions**
- User enters down payment and trade-in value
- System validates both are non-negative numbers
- System checks that reductions don't exceed total cost

**Step 3: Calculate Total Vehicle Cost**
- Tax Amount = Car Price × (Tax Rate / 100)
- Total Cost = Car Price + Tax Amount + Registration Fee

**Step 4: Calculate Amount Financed**
- Amount Financed = Total Cost - Down Payment - Trade-In Value
- If result < 0, set to 0

**Step 5: Determine Effective Interest Rate**
- Get compounding periods per year (12, 4, 2, or 1)
- Calculate EAR = (1 + annual_rate / periods)^periods - 1
- Calculate Monthly Rate = (1 + EAR)^(1/12) - 1

**Step 6: Compute Monthly Payment**
- If monthly rate = 0: Payment = Principal / Total Months
- Else: Apply amortization formula
- M = P × [r(1+r)^n] / [(1+r)^n - 1]

**Step 7: Generate Amortization Schedule**
- Initialize balance = amount financed
- Loop through each month:
  - Calculate interest = balance × monthly rate
  - If missed payment: add penalty, capitalize interest
  - Else: calculate principal = payment - interest
  - Update balance = balance - principal
  - Store entry in schedule

**Step 8: Display Results and Export**
- Show loan summary dialog with totals
- Display amortization schedule in table
- Enable export to secure TXT file

---

## Logic Formulation (Algorithm)

### Program Flowchart

```
                    ┌─────────────────┐
                    │      START      │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │  INPUT: Car     │
                    │  Price, Rate,   │
                    │  Term, etc.     │
                    └────────┬────────┘
                             │
                             ▼
                       ◇───────────◇
                      ╱             ╲
                     ╱   Car Price   ╲
                    ╱     > 0 ?       ╲────NO────┐
                    ╲                 ╱          │
                     ╲               ╱           ▼
                      ╲             ╱    ┌──────────────┐
                       ◇───────────◇     │ ERROR: Enter │
                             │           │ valid price  │
                            YES          └──────┬───────┘
                             │                  │
                             ▼                  │
                       ◇───────────◇            │
                      ╱             ╲           │
                     ╱  Rate between ╲          │
                    ╱   0% - 50% ?    ╲───NO────┤
                    ╲                 ╱         │
                     ╲               ╱          │
                      ╲             ╱           │
                       ◇───────────◇            │
                             │                  │
                            YES                 │
                             │                  │
                             ▼                  │
                    ┌─────────────────┐         │
                    │ Calculate Total │         │
                    │ Cost = Price +  │         │
                    │ Tax + Reg Fee   │         │
                    └────────┬────────┘         │
                             │                  │
                             ▼                  │
                    ┌─────────────────┐         │
                    │ Amount Financed │         │
                    │ = Total - Down  │         │
                    │ - Trade-In      │         │
                    └────────┬────────┘         │
                             │                  │
                             ▼                  │
                       ◇───────────◇            │
                      ╱             ╲           │
                     ╱   Amount      ╲          │
                    ╱    > 0 ?        ╲───NO────┤
                    ╲                 ╱         │
                     ╲               ╱          │
                      ╲             ╱           │
                       ◇───────────◇            │
                             │                  │
                            YES                 │
                             │                  │
                             ▼                  │
                    ┌─────────────────┐         │
                    │ Calculate EAR   │         │
                    │ and Monthly     │         │
                    │ Rate            │         │
                    └────────┬────────┘         │
                             │                  │
                             ▼                  │
                    ┌─────────────────┐         │
                    │ Calculate       │         │
                    │ Monthly Payment │         │
                    │ M = P×[r(1+r)^n]│         │
                    │   /[(1+r)^n-1]  │         │
                    └────────┬────────┘         │
                             │                  │
                             ▼                  │
                    ┌─────────────────┐         │
                    │ Initialize:     │         │
                    │ balance = P     │         │
                    │ paymentNum = 1  │         │
                    └────────┬────────┘         │
                             │                  │
                             ▼                  │
                       ◇───────────◇            │
                      ╱             ╲           │
                     ╱  balance > 0  ╲          │
                    ╱  AND payNum <=  ╲         │
                    ╲  maxPayments?   ╱         │
                     ╲               ╱          │
                      ╲             ╱           │
                       ◇───────────◇            │
                        │         │             │
                       YES        NO            │
                        │         │             │
                        ▼         │             │
               ┌────────────────┐ │             │
               │ interest =     │ │             │
               │ balance × rate │ │             │
               └───────┬────────┘ │             │
                       │          │             │
                       ▼          │             │
                 ◇───────────◇    │             │
                ╱             ╲   │             │
               ╱  Is Missed    ╲  │             │
              ╱   Payment?      ╲ │             │
              ╲                 ╱ │             │
               ╲               ╱  │             │
                ╲             ╱   │             │
                 ◇───────────◇    │             │
                  │         │     │             │
                 YES        NO    │             │
                  │         │     │             │
                  ▼         ▼     │             │
         ┌──────────┐ ┌──────────┐│             │
         │ penalty= │ │principal=││             │
         │ balance× │ │payment - ││             │
         │ penRate  │ │interest  ││             │
         │ balance+=│ │balance-= ││             │
         │ interest │ │principal ││             │
         └────┬─────┘ └────┬─────┘│             │
              │            │      │             │
              └─────┬──────┘      │             │
                    │             │             │
                    ▼             │             │
           ┌────────────────┐     │             │
           │ Add entry to   │     │             │
           │ schedule       │     │             │
           │ paymentNum++   │     │             │
           └───────┬────────┘     │             │
                   │              │             │
                   └──────────────┘             │
                             │                  │
                             ▼                  │
                    ┌─────────────────┐         │
                    │ Display Loan    │◄────────┘
                    │ Summary and     │
                    │ Schedule        │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │      END        │
                    └─────────────────┘
```

### Pseudocode of the Control Structure

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
        monthlyPayment ← 0
        totalInterest ← 0
        totalPenalties ← 0
        amortizationSchedule ← empty list
    
    METHOD calculateTotalCost()
        taxAmount ← carPrice × (salesTaxRate / 100)
        RETURN carPrice + taxAmount + registrationFee
    END METHOD
    
    METHOD calculateAmountFinanced()
        totalCost ← calculateTotalCost()
        reductions ← downPayment + tradeInValue
        RETURN MAX(0, totalCost - reductions)
    END METHOD
    
    METHOD getCompoundingPeriodsPerYear()
        SWITCH compoundingFrequency
            CASE "Annually": RETURN 1
            CASE "Semi-Annually": RETURN 2
            CASE "Quarterly": RETURN 4
            CASE "Monthly": RETURN 12
            DEFAULT: RETURN 12
        END SWITCH
    END METHOD
    
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
        
        // Calculate Effective Annual Rate (EAR)
        EAR ← (1 + annualRate / compoundingPeriods)^compoundingPeriods - 1
        
        // Convert EAR to monthly rate
        monthlyRate ← (1 + EAR)^(1/12) - 1
        
        IF monthlyRate = 0 THEN
            RETURN principal / totalMonths
        END IF
        
        // Standard amortization formula
        numerator ← monthlyRate × (1 + monthlyRate)^totalMonths
        denominator ← (1 + monthlyRate)^totalMonths - 1
        
        monthlyPayment ← principal × (numerator / denominator)
        RETURN monthlyPayment
    END METHOD
    
    METHOD generateAmortizationSchedule()
        amortizationSchedule ← empty list
        principal ← calculateAmountFinanced()
        
        IF principal ≤ 0 THEN
            RETURN amortizationSchedule
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
        
        WHILE balance > 0.01 AND paymentNum ≤ totalMonths + missedPayments DO
            interestPayment ← balance × monthlyRate
            penalty ← 0
            
            IF missedPayments > 0 AND paymentNum ≤ missedPayments THEN
                // Missed payment - interest capitalizes, penalty applies
                penalty ← balance × (penaltyRate / 100)
                principalPayment ← 0
                currentPayment ← 0
                balance ← balance + interestPayment
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
            
            ADD entry TO amortizationSchedule
            paymentNum ← paymentNum + 1
        END WHILE
        
        totalInterest ← cumulativeInterest
        totalPenalties ← cumulativePenalties
        
        RETURN amortizationSchedule
    END METHOD

END CLASS

CLASS LoanController
    VARIABLE instance ← null (Singleton)
    VARIABLE currentLoan ← null
    
    METHOD getInstance()
        IF instance = null THEN
            instance ← NEW LoanController()
        END IF
        RETURN instance
    END METHOD
    
    METHOD createLoanCalculation(carPrice, salesTaxRate, registrationFee,
                                  downPayment, tradeInValue, annualInterestRate,
                                  loanTermYears, compoundingFrequency,
                                  penaltyRate, missedPayments, extraPayment)
        loan ← NEW LoanCalculation()
        loan.setCarPrice(carPrice)
        loan.setSalesTaxRate(salesTaxRate)
        loan.setRegistrationFee(registrationFee)
        loan.setDownPayment(downPayment)
        loan.setTradeInValue(tradeInValue)
        loan.setAnnualInterestRate(annualInterestRate)
        loan.setLoanTermYears(loanTermYears)
        loan.setCompoundingFrequency(compoundingFrequency)
        loan.setPenaltyRate(penaltyRate)
        loan.setMissedPayments(missedPayments)
        loan.setExtraPaymentPerMonth(extraPayment)
        
        loan.calculateMonthlyPayment()
        loan.generateAmortizationSchedule()
        
        currentLoan ← loan
        RETURN loan
    END METHOD
END CLASS
```

---

## Defining the Constructive/Destructive Variables

### Declaration and Data Types

| Variable Name | Description | Type | Constructive/Destructive |
|---------------|-------------|------|--------------------------|
| `carPrice` | Vehicle base price | double | Constructive |
| `salesTaxRate` | Tax percentage (e.g., 8.0 for 8%) | double | Constructive |
| `registrationFee` | Fixed registration cost | double | Constructive |
| `downPayment` | Initial payment reducing loan | double | Constructive |
| `tradeInValue` | Trade-in deduction | double | Constructive |
| `annualInterestRate` | APR as percentage | double | Constructive |
| `loanTermYears` | Loan duration in years | int | Constructive |
| `compoundingFrequency` | Compounding period selection | String | Constructive |
| `penaltyRate` | Late payment penalty % | double | Constructive |
| `missedPayments` | Number of missed payments | int | Constructive |
| `extraPaymentPerMonth` | Additional monthly payment | double | Constructive |
| `monthlyPayment` | Calculated monthly payment | double | Destructive (computed) |
| `totalInterest` | Accumulated total interest | double | Destructive (computed) |
| `totalPenalties` | Accumulated penalties | double | Destructive (computed) |
| `balance` | Remaining loan balance | double | Destructive (decreases each iteration) |
| `principal` | Principal portion of payment | double | Destructive (computed each iteration) |
| `interest` | Interest portion of payment | double | Destructive (computed each iteration) |
| `cumulativePaid` | Running total of payments | double | Constructive (accumulates) |
| `paymentNumber` | Current payment sequence | int | Constructive (increments) |
| `amortizationSchedule` | List of payment entries | List<AmortizationEntry> | Constructive |
| `carPriceField` | GUI input for car price | JTextField | Constructive |
| `salesTaxField` | GUI input for tax rate | JTextField | Constructive |
| `annualInterestRateField` | GUI input for APR | JTextField | Constructive |
| `loanTermYearsField` | GUI input for term | JTextField | Constructive |
| `compoundingFrequencyCombo` | GUI dropdown for frequency | JComboBox | Constructive |
| `btnCalculate` | Calculate button | JButton | Constructive |
| `amortizationTable` | Schedule display table | JTable | Constructive |
| `lblMonthlyPayment` | Output label for payment | JLabel | Constructive |
| `lblTotalInterest` | Output label for interest | JLabel | Constructive |

---

## Formulation of Data Structure (Syntax and Semantics)

### Functionalities

| Function Name | Description |
|---------------|-------------|
| `calculateTotalCost()` | Computes total vehicle cost: Car Price + Tax Amount + Registration Fee |
| `calculateAmountFinanced()` | Computes loan principal: Total Cost - Down Payment - Trade-In Value |
| `calculateTaxAmount()` | Computes sales tax: Car Price × (Tax Rate / 100) |
| `getCompoundingPeriodsPerYear()` | Returns compounding periods: 12, 4, 2, or 1 based on frequency |
| `calculateMonthlyPayment()` | Computes monthly payment using amortization formula with compound interest |
| `generateAmortizationSchedule()` | Creates complete payment schedule with all entries |
| `calculateTotalInterest()` | Returns accumulated interest from schedule |
| `calculateTotalPenalties()` | Returns accumulated penalties from schedule |
| `getTotalAmountPaid()` | Returns total of all payments made |
| `validatePositiveNumber(input)` | Validates input is numeric and > 0 |
| `validateNonNegativeNumber(input)` | Validates input is numeric and >= 0 |
| `validateInterestRate(input)` | Validates rate is between 0% and 50% |
| `validatePercentage(input)` | Validates percentage is between 0% and 100% |
| `formatCurrency(amount)` | Formats number as Philippine Peso (₱XX,XXX.XX) |
| `formatRate(rate)` | Formats number as percentage (X.XX%) |
| `parseDouble(text)` | Safely parses string to double, returns 0 on failure |
| `parseInt(text)` | Safely parses string to integer, returns 0 on failure |

### Parameters

| Function | Parameters | Purpose |
|----------|------------|---------|
| `createLoanCalculation(...)` | carPrice: double, salesTaxRate: double, registrationFee: double, downPayment: double, tradeInValue: double, annualInterestRate: double, loanTermYears: int, compoundingFrequency: String, penaltyRate: double, missedPayments: int, extraPayment: double | Creates and initializes a complete loan calculation object |
| `validatePositiveNumber(input)` | input: String | Validates that string represents a positive number |
| `validateInterestRate(input)` | input: String | Validates rate is within 0-50% range |
| `formatCurrency(amount)` | amount: double | Formats double as Philippine Peso string |
| `parseDouble(text, defaultValue)` | text: String, defaultValue: double | Parses string to double with fallback |
| `AmortizationEntry(...)` | paymentNumber: int, payment: double, principal: double, interest: double, penalty: double, balance: double, totalPaid: double | Constructs a single schedule entry |
| `quickCalculate(principal, rate, years)` | principal: double, rate: double, years: int | Quick simple interest calculation for comparison |

### Arrays

```java
// Compounding frequency options for dropdown
String[] frequencies = {"Monthly", "Quarterly", "Semi-Annually", "Annually"};

// Corresponding compounding periods per year
int[] compoundingPeriods = {12, 4, 2, 1};

// Amortization schedule - dynamic list of payment entries
List<AmortizationEntry> amortizationSchedule = new ArrayList<>();

// Sample car inventory for demonstration
List<Car> cars = new ArrayList<>();
```

**Compounding Frequency Matrix:**

| Index | Frequency | Periods/Year | Period Rate Formula |
|-------|-----------|--------------|---------------------|
| 0 | Monthly | 12 | annual_rate / 12 |
| 1 | Quarterly | 4 | annual_rate / 4 |
| 2 | Semi-Annually | 2 | annual_rate / 2 |
| 3 | Annually | 1 | annual_rate / 1 |

---

## Iterative Simulation (Logical Algorithm)

**Sample Loan Calculation:**
- Car Price: ₱1,500,000.00
- Sales Tax Rate: 8%
- Registration Fee: ₱500.00
- Down Payment: ₱300,000.00
- Trade-In Value: ₱0.00
- Annual Interest Rate: 6.5%
- Loan Term: 5 Years (60 months)
- Compounding Frequency: Monthly
- Penalty Rate: 0%
- Missed Payments: 0

**Step-by-Step Calculation:**

1. **Tax Amount** = ₱1,500,000 × (8/100) = ₱120,000.00
2. **Total Cost** = ₱1,500,000 + ₱120,000 + ₱500 = ₱1,620,500.00
3. **Amount Financed** = ₱1,620,500 - ₱300,000 - ₱0 = ₱1,320,500.00
4. **EAR** = (1 + 0.065/12)^12 - 1 = 0.06697 (6.697%)
5. **Monthly Rate** = (1 + 0.06697)^(1/12) - 1 = 0.005417 (0.5417%)
6. **Monthly Payment** = ₱1,320,500 × [0.005417(1.005417)^60] / [(1.005417)^60 - 1] = ₱25,823.45

**Amortization Schedule (First 5 Payments):**

| Month | Payment (₱) | Principal (₱) | Interest (₱) | Balance (₱) |
|-------|-------------|---------------|--------------|-------------|
| 1 | 25,823.45 | 18,667.73 | 7,155.72 | 1,301,832.27 |
| 2 | 25,823.45 | 18,768.85 | 7,054.60 | 1,283,063.42 |
| 3 | 25,823.45 | 18,870.53 | 6,952.92 | 1,264,192.89 |
| 4 | 25,823.45 | 18,972.77 | 6,850.68 | 1,245,220.12 |
| 5 | 25,823.45 | 19,075.58 | 6,747.87 | 1,226,144.54 |

**Totals After 60 Payments:**
- Total Paid: ₱1,549,407.00
- Total Interest: ₱228,907.00
- Principal Paid: ₱1,320,500.00

---

## Screen Shot (UI Frame)

*[Screenshot images would be placed here with arrows pointing to each component]*

| No. | Object (Variable) | Function |
|-----|-------------------|----------|
| 1. | carPriceField | Input Car Price (₱)<br>If carPrice is not numeric or ≤ 0<br>Then MsgBox("Please enter a valid car price") |
| 2. | salesTaxField | Input Sales Tax Rate (%)<br>If rate < 0 or > 100<br>Then MsgBox("Invalid tax rate") |
| 3. | registrationFeeField | Input Registration Fee (₱)<br>If fee < 0<br>Then MsgBox("Invalid registration fee") |
| 4. | downPaymentField | Input Down Payment (₱)<br>If downPayment > totalCost<br>Then MsgBox("Down payment exceeds total cost") |
| 5. | tradeInValueField | Input Trade-In Value (₱)<br>If tradeIn < 0<br>Then MsgBox("Invalid trade-in value") |
| 6. | annualInterestRateField | Input Annual Interest Rate (%)<br>If rate < 0 or > 50<br>Then MsgBox("Interest rate must be between 0-50%") |
| 7. | loanTermYearsField | Input Loan Term (Years)<br>If term < 1 or > 30 or not integer<br>Then MsgBox("Term must be 1-30 years") |
| 8. | compoundingFrequencyCombo | Select Compounding Frequency<br>Options: Monthly, Quarterly, Semi-Annually, Annually<br>Default: Monthly |
| 9. | penaltyRateField | Input Penalty Rate (%)<br>If rate < 0 or > 100<br>Then MsgBox("Invalid penalty rate") |
| 10. | missedPaymentsField | Input Number of Missed Payments<br>If value < 0 or not integer<br>Then MsgBox("Invalid missed payments count") |
| 11. | extraPaymentField | Input Extra Payment Per Month (₱)<br>If value < 0<br>Then MsgBox("Invalid extra payment amount") |
| 12. | btnCalculate | Calculate Loan Button<br>If all inputs valid Then<br>  calculateLoan()<br>  showLoanSummary()<br>Else<br>  showValidationErrors() |
| 13. | btnViewSchedule | View Amortization Schedule Button<br>If loan calculated Then<br>  openScheduleFrame()<br>Else<br>  MsgBox("Calculate loan first") |
| 14. | btnExport | Export to File Button<br>If schedule exists Then<br>  exportToSecureTXT()<br>Else<br>  MsgBox("No schedule to export") |
| 15. | carsGrid | Display Car Cards Panel<br>For each car in cars<br>  Display card with make, model, price<br>OnClick: selectCar(car) |
| 16. | searchField | Search Cars Input<br>OnKeyRelease:<br>  filteredCars = filterByQuery(searchText)<br>  refreshCarsGrid(filteredCars) |
| 17. | btnContinue | Continue to Calculator Button<br>If selectedCar != null Then<br>  navigateToCalculatePanel()<br>  setCarPrice(selectedCar.price)<br>Else<br>  MsgBox("Please select a car") |

---

## Program Listing

### Code With line Numbers

**LoanCalculation.java**

```java
1   package com.vismera.models;
2   
3   import java.util.ArrayList;
4   import java.util.List;
5   
6   /**
7    * Model class for loan calculation parameters and results.
8    * Handles compound interest and penalty calculations.
9    * @author Vismerá Inc.
10   */
11  public class LoanCalculation {
12      // Input fields
13      private double carPrice;
14      private double salesTaxRate;
15      private double registrationFee;
16      private double downPayment;
17      private double tradeInValue;
18      private double annualInterestRate;
19      private int loanTermYears;
20      private String compoundingFrequency;
21      private double penaltyRate;
22      private int missedPayments;
23      private double extraPaymentPerMonth;
24  
25      // Calculated results (cached)
26      private double monthlyPayment;
27      private double totalInterest;
28      private double totalPenalties;
29      private double totalAmountPaid;
30      private List<AmortizationEntry> amortizationSchedule;
31  
32      public LoanCalculation() {
33          this.compoundingFrequency = "Monthly";
34          this.amortizationSchedule = new ArrayList<>();
35      }
36  
37      public double calculateTotalCost() {
38          return carPrice + calculateTaxAmount() + registrationFee;
39      }
40  
41      public double calculateAmountFinanced() {
42          double totalCost = calculateTotalCost();
43          double reductions = downPayment + tradeInValue;
44          return Math.max(0, totalCost - reductions);
45      }
46  
47      public double calculateTaxAmount() {
48          return carPrice * (salesTaxRate / 100.0);
49      }
50  
51      public int getCompoundingPeriodsPerYear() {
52          switch (compoundingFrequency) {
53              case "Annually": return 1;
54              case "Semi-Annually": return 2;
55              case "Quarterly": return 4;
56              case "Monthly": 
57              default: return 12;
58          }
59      }
60  
61      public double calculateMonthlyPayment() {
62          double principal = calculateAmountFinanced();
63          if (principal <= 0) return 0;
64  
65          int totalMonths = loanTermYears * 12;
66          if (totalMonths <= 0) return 0;
67  
68          double annualRate = annualInterestRate / 100.0;
69          int compoundingPeriods = getCompoundingPeriodsPerYear();
70          
71          double effectiveAnnualRate = Math.pow(1 + (annualRate / compoundingPeriods), 
72                                                 compoundingPeriods) - 1;
73          double monthlyRate = Math.pow(1 + effectiveAnnualRate, 1.0 / 12.0) - 1;
74  
75          if (monthlyRate == 0) {
76              return principal / totalMonths;
77          }
78  
79          double numerator = monthlyRate * Math.pow(1 + monthlyRate, totalMonths);
80          double denominator = Math.pow(1 + monthlyRate, totalMonths) - 1;
81          
82          this.monthlyPayment = principal * (numerator / denominator);
83          return this.monthlyPayment;
84      }
85  
86      public List<AmortizationEntry> generateAmortizationSchedule() {
87          amortizationSchedule = new ArrayList<>();
88          
89          double principal = calculateAmountFinanced();
90          if (principal <= 0) return amortizationSchedule;
91  
92          int totalMonths = loanTermYears * 12;
93          double payment = calculateMonthlyPayment();
94          double adjustedPayment = payment + extraPaymentPerMonth;
95          
96          double annualRate = annualInterestRate / 100.0;
97          int compoundingPeriods = getCompoundingPeriodsPerYear();
98          double effectiveAnnualRate = Math.pow(1 + (annualRate / compoundingPeriods), 
99                                                 compoundingPeriods) - 1;
100         double monthlyRate = Math.pow(1 + effectiveAnnualRate, 1.0 / 12.0) - 1;
101         
102         double balance = principal;
103         double cumulativePaid = 0;
104         double cumulativeInterest = 0;
105         double cumulativePenalties = 0;
106         
107         int paymentNum = 1;
108         
109         while (balance > 0.01 && paymentNum <= totalMonths + missedPayments) {
110             double interestPayment = balance * monthlyRate;
111             double penalty = 0;
112             double principalPayment;
113             double currentPayment;
114             
115             if (missedPayments > 0 && paymentNum <= missedPayments) {
116                 penalty = balance * (penaltyRate / 100.0);
117                 principalPayment = 0;
118                 currentPayment = 0;
119                 balance += interestPayment;
120                 cumulativePenalties += penalty;
121             } else {
122                 currentPayment = Math.min(adjustedPayment, balance + interestPayment);
123                 principalPayment = currentPayment - interestPayment;
124                 
125                 if (principalPayment < 0) {
126                     principalPayment = 0;
127                 }
128                 
129                 balance -= principalPayment;
130                 if (balance < 0) balance = 0;
131             }
132             
133             cumulativeInterest += interestPayment;
134             cumulativePaid += currentPayment + penalty;
135             
136             AmortizationEntry entry = new AmortizationEntry(
137                 paymentNum, currentPayment, principalPayment,
138                 interestPayment, penalty, balance, cumulativePaid
139             );
140             
141             amortizationSchedule.add(entry);
142             paymentNum++;
143             
144             if (paymentNum > totalMonths * 2) break;
145         }
146         
147         this.totalInterest = cumulativeInterest;
148         this.totalPenalties = cumulativePenalties;
149         this.totalAmountPaid = cumulativePaid;
150         
151         return amortizationSchedule;
152     }
153 
154     // Getters and Setters
155     public double getCarPrice() { return carPrice; }
156     public void setCarPrice(double carPrice) { this.carPrice = carPrice; }
157     public double getSalesTaxRate() { return salesTaxRate; }
158     public void setSalesTaxRate(double salesTaxRate) { this.salesTaxRate = salesTaxRate; }
159     public double getRegistrationFee() { return registrationFee; }
160     public void setRegistrationFee(double registrationFee) { this.registrationFee = registrationFee; }
161     public double getDownPayment() { return downPayment; }
162     public void setDownPayment(double downPayment) { this.downPayment = downPayment; }
163     public double getTradeInValue() { return tradeInValue; }
164     public void setTradeInValue(double tradeInValue) { this.tradeInValue = tradeInValue; }
165     public double getAnnualInterestRate() { return annualInterestRate; }
166     public void setAnnualInterestRate(double rate) { this.annualInterestRate = rate; }
167     public int getLoanTermYears() { return loanTermYears; }
168     public void setLoanTermYears(int years) { this.loanTermYears = years; }
169     public String getCompoundingFrequency() { return compoundingFrequency; }
170     public void setCompoundingFrequency(String freq) { this.compoundingFrequency = freq; }
171     public double getPenaltyRate() { return penaltyRate; }
172     public void setPenaltyRate(double rate) { this.penaltyRate = rate; }
173     public int getMissedPayments() { return missedPayments; }
174     public void setMissedPayments(int missed) { this.missedPayments = missed; }
175     public double getExtraPaymentPerMonth() { return extraPaymentPerMonth; }
176     public void setExtraPaymentPerMonth(double extra) { this.extraPaymentPerMonth = extra; }
177     public double getMonthlyPayment() { return monthlyPayment; }
178     public double getTotalInterest() { return totalInterest; }
179     public double getTotalPenalties() { return totalPenalties; }
180     public List<AmortizationEntry> getAmortizationSchedule() { return amortizationSchedule; }
181 }
```

---

**AmortizationEntry.java**

```java
1   package com.vismera.models;
2   
3   /**
4    * Model class representing a single entry in the amortization schedule.
5    * @author Vismerá Inc.
6    */
7   public class AmortizationEntry {
8       private int paymentNumber;
9       private double payment;
10      private double principal;
11      private double interest;
12      private double penalty;
13      private double balance;
14      private double totalPaid;
15  
16      public AmortizationEntry() {
17      }
18  
19      public AmortizationEntry(int paymentNumber, double payment, double principal, 
20                              double interest, double penalty, double balance, 
21                              double totalPaid) {
22          this.paymentNumber = paymentNumber;
23          this.payment = payment;
24          this.principal = principal;
25          this.interest = interest;
26          this.penalty = penalty;
27          this.balance = balance;
28          this.totalPaid = totalPaid;
29      }
30  
31      // Getters and Setters
32      public int getPaymentNumber() { return paymentNumber; }
33      public void setPaymentNumber(int paymentNumber) { this.paymentNumber = paymentNumber; }
34  
35      public double getPayment() { return payment; }
36      public void setPayment(double payment) { this.payment = payment; }
37  
38      public double getPrincipal() { return principal; }
39      public void setPrincipal(double principal) { this.principal = principal; }
40  
41      public double getInterest() { return interest; }
42      public void setInterest(double interest) { this.interest = interest; }
43  
44      public double getPenalty() { return penalty; }
45      public void setPenalty(double penalty) { this.penalty = penalty; }
46  
47      public double getBalance() { return balance; }
48      public void setBalance(double balance) { this.balance = balance; }
49  
50      public double getTotalPaid() { return totalPaid; }
51      public void setTotalPaid(double totalPaid) { this.totalPaid = totalPaid; }
52  
53      public double getTotalPaymentWithPenalty() {
54          return payment + penalty;
55      }
56  }
```

---

**LoanController.java**

```java
1   package com.vismera.controllers;
2   
3   import com.vismera.models.AmortizationEntry;
4   import com.vismera.models.LoanCalculation;
5   import com.vismera.utils.CSVExporter;
6   import java.util.List;
7   
8   /**
9    * Controller for loan calculations and amortization.
10   * @author Vismerá Inc.
11   */
12  public class LoanController {
13      
14      private static LoanController instance;
15      private LoanCalculation currentLoan;
16  
17      private LoanController() {
18      }
19  
20      public static LoanController getInstance() {
21          if (instance == null) {
22              instance = new LoanController();
23          }
24          return instance;
25      }
26  
27      public LoanCalculation calculateLoan(LoanCalculation loan) {
28          this.currentLoan = loan;
29          loan.calculateMonthlyPayment();
30          loan.generateAmortizationSchedule();
31          return loan;
32      }
33  
34      public LoanCalculation createLoanCalculation(
35              double carPrice, double salesTaxRate, double registrationFee,
36              double downPayment, double tradeInValue, double annualInterestRate,
37              int loanTermYears, String compoundingFrequency,
38              double penaltyRate, int missedPayments, double extraPayment) {
39          
40          LoanCalculation loan = new LoanCalculation();
41          loan.setCarPrice(carPrice);
42          loan.setSalesTaxRate(salesTaxRate);
43          loan.setRegistrationFee(registrationFee);
44          loan.setDownPayment(downPayment);
45          loan.setTradeInValue(tradeInValue);
46          loan.setAnnualInterestRate(annualInterestRate);
47          loan.setLoanTermYears(loanTermYears);
48          loan.setCompoundingFrequency(compoundingFrequency);
49          loan.setPenaltyRate(penaltyRate);
50          loan.setMissedPayments(missedPayments);
51          loan.setExtraPaymentPerMonth(extraPayment);
52          
53          return calculateLoan(loan);
54      }
55  
56      public List<AmortizationEntry> generateAmortizationSchedule(LoanCalculation loan) {
57          return loan.generateAmortizationSchedule();
58      }
59  
60      public boolean exportToCSV(List<AmortizationEntry> schedule, String filename) {
61          return CSVExporter.exportAmortizationSchedule(schedule, filename);
62      }
63  
64      public LoanCalculation getCurrentLoan() {
65          return currentLoan;
66      }
67  
68      public void clearCurrentLoan() {
69          this.currentLoan = null;
70      }
71  
72      public double[] quickCalculate(double principal, double annualRate, int years) {
73          int totalMonths = years * 12;
74          double monthlyRate = (annualRate / 100.0) / 12.0;
75          
76          double monthlyPayment;
77          if (monthlyRate == 0) {
78              monthlyPayment = principal / totalMonths;
79          } else {
80              double numerator = monthlyRate * Math.pow(1 + monthlyRate, totalMonths);
81              double denominator = Math.pow(1 + monthlyRate, totalMonths) - 1;
82              monthlyPayment = principal * (numerator / denominator);
83          }
84          
85          double totalPaid = monthlyPayment * totalMonths;
86          double totalInterest = totalPaid - principal;
87          
88          return new double[] { monthlyPayment, totalInterest, totalPaid };
89      }
90  }
```

---

**ValidationUtils.java**

```java
1   package com.vismera.utils;
2   
3   import java.util.Map;
4   
5   /**
6    * Utility class for validating user inputs.
7    * @author Vismerá Inc.
8    */
9   public class ValidationUtils {
10  
11      public static boolean validatePositiveNumber(String input) {
12          if (input == null || input.trim().isEmpty()) {
13              return false;
14          }
15          try {
16              String cleaned = input.replaceAll("[\\$,\\s]", "").trim();
17              double value = Double.parseDouble(cleaned);
18              return value > 0;
19          } catch (NumberFormatException e) {
20              return false;
21          }
22      }
23  
24      public static boolean validateNonNegativeNumber(String input) {
25          if (input == null || input.trim().isEmpty()) {
26              return true;
27          }
28          try {
29              String cleaned = input.replaceAll("[\\$,\\s]", "").trim();
30              double value = Double.parseDouble(cleaned);
31              return value >= 0;
32          } catch (NumberFormatException e) {
33              return false;
34          }
35      }
36  
37      public static boolean validatePercentage(String input) {
38          if (input == null || input.trim().isEmpty()) {
39              return false;
40          }
41          try {
42              String cleaned = input.replaceAll("[%\\s]", "").trim();
43              double value = Double.parseDouble(cleaned);
44              return value >= 0 && value <= 100;
45          } catch (NumberFormatException e) {
46              return false;
47          }
48      }
49  
50      public static boolean validateInterestRate(String input) {
51          if (input == null || input.trim().isEmpty()) {
52              return false;
53          }
54          try {
55              String cleaned = input.replaceAll("[%\\s]", "").trim();
56              double value = Double.parseDouble(cleaned);
57              return value >= 0 && value <= 50;
58          } catch (NumberFormatException e) {
59              return false;
60          }
61      }
62  
63      public static boolean validatePositiveInteger(String input) {
64          if (input == null || input.trim().isEmpty()) {
65              return false;
66          }
67          try {
68              String cleaned = input.replaceAll("[,\\s]", "").trim();
69              int value = Integer.parseInt(cleaned);
70              return value > 0;
71          } catch (NumberFormatException e) {
72              return false;
73          }
74      }
75  
76      public static boolean validateNonNegativeInteger(String input) {
77          if (input == null || input.trim().isEmpty()) {
78              return true;
79          }
80          try {
81              String cleaned = input.replaceAll("[,\\s]", "").trim();
82              int value = Integer.parseInt(cleaned);
83              return value >= 0;
84          } catch (NumberFormatException e) {
85              return false;
86          }
87      }
88  
89      public static boolean isEmpty(String input) {
90          return input == null || input.trim().isEmpty();
91      }
92  
93      public static String validateLoanInputs(String carPrice, String interestRate, 
94                                             String loanTerm, String downPayment, 
95                                             String tradeIn) {
96          StringBuilder errors = new StringBuilder();
97  
98          if (!validatePositiveNumber(carPrice)) {
99              errors.append("• Car Price must be a positive number\n");
100         }
101         if (!validateInterestRate(interestRate)) {
102             errors.append("• Interest Rate must be between 0% and 50%\n");
103         }
104         if (!validatePositiveInteger(loanTerm)) {
105             errors.append("• Loan Term must be a positive whole number\n");
106         }
107         if (!isEmpty(downPayment) && !validateNonNegativeNumber(downPayment)) {
108             errors.append("• Down Payment must be a non-negative number\n");
109         }
110         if (!isEmpty(tradeIn) && !validateNonNegativeNumber(tradeIn)) {
111             errors.append("• Trade-In Value must be a non-negative number\n");
112         }
113 
114         return errors.toString();
115     }
116 }
```

---

**FormatUtils.java**

```java
1   package com.vismera.utils;
2   
3   import java.text.DecimalFormat;
4   
5   /**
6    * Utility class for formatting currency, percentages, and parsing numbers.
7    * @author Vismerá Inc.
8    */
9   public class FormatUtils {
10      
11      private static final String CURRENCY_SYMBOL = "₱";
12      private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");
13      private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("0.00%");
14      private static final DecimalFormat RATE_FORMAT = new DecimalFormat("0.00");
15  
16      public static String formatCurrency(double amount) {
17          return CURRENCY_SYMBOL + CURRENCY_FORMAT.format(amount);
18      }
19  
20      public static String formatPercentage(double rate) {
21          return PERCENTAGE_FORMAT.format(rate);
22      }
23  
24      public static String formatRate(double rate) {
25          return RATE_FORMAT.format(rate) + "%";
26      }
27  
28      public static String formatYears(int years) {
29          return years + (years == 1 ? " Year" : " Years");
30      }
31  
32      public static String formatMonths(int months) {
33          int years = months / 12;
34          int remainingMonths = months % 12;
35          
36          if (years == 0) {
37              return remainingMonths + (remainingMonths == 1 ? " Month" : " Months");
38          } else if (remainingMonths == 0) {
39              return formatYears(years);
40          } else {
41              return formatYears(years) + ", " + remainingMonths + " Mo";
42          }
43      }
44  
45      public static double parseDouble(String text) {
46          return parseDouble(text, 0.0);
47      }
48  
49      public static double parseDouble(String text, double defaultValue) {
50          if (text == null || text.trim().isEmpty()) {
51              return defaultValue;
52          }
53          try {
54              String cleaned = text.replaceAll("[₱\\$P,\\s]", "").trim();
55              return Double.parseDouble(cleaned);
56          } catch (NumberFormatException e) {
57              return defaultValue;
58          }
59      }
60  
61      public static int parseInt(String text) {
62          return parseInt(text, 0);
63      }
64  
65      public static int parseInt(String text, int defaultValue) {
66          if (text == null || text.trim().isEmpty()) {
67              return defaultValue;
68          }
69          try {
70              String cleaned = text.replaceAll("[,\\s]", "").trim();
71              return Integer.parseInt(cleaned);
72          } catch (NumberFormatException e) {
73              return defaultValue;
74          }
75      }
76  
77      public static String formatNumber(double number) {
78          DecimalFormat df = new DecimalFormat("#,##0.00");
79          return df.format(number);
80      }
81  
82      public static String formatNumber(int number) {
83          DecimalFormat df = new DecimalFormat("#,##0");
84          return df.format(number);
85      }
86  }
```

---

## Appendix

### References

Cormen, T. H., Leiserson, C. E., Rivest, R. L., & Stein, C. (2022). *Introduction to algorithms* (4th ed.). MIT Press.

Gamma, E., Helm, R., Johnson, R., & Vlissides, R. (2021). *Design patterns: Elements of reusable object-oriented software* (Reprint ed.). Addison-Wesley Professional.

IEEE Standards Association. (2022). *IEEE Standard 1012-2022: Standard for system, software, and hardware verification and validation*. IEEE.

ISO/IEC/IEEE. (2021). *ISO/IEC/IEEE 24765:2021—Systems and software engineering — Vocabulary*. International Organization for Standardization.

Oracle. (2020). *Java Platform, Standard Edition 17 documentation*. https://docs.oracle.com/javase

Oracle. (2020). *Java Swing tutorial: Building graphical user interfaces*. https://docs.oracle.com/javase/tutorial/uiswing

Skiena, S. (2020). *The algorithm design manual* (3rd ed.). Springer.

Sommerville, I. (2020). *Software engineering* (11th ed.). Pearson.

Weiss, M. A. (2020). *Data structures and algorithm analysis in Java* (3rd ed., Updated printing). Pearson.

---

*End of Document*
