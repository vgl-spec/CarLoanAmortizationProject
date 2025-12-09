# Vismerá Inc. Car Loan Amortization Calculator
## Technical Documentation for Defense Presentation
### Document Version: 5.0 (Calculator Application - No Database Required)

---

## Table of Contents
1. [Algorithm for Car Loan Amortization](#1-algorithm-for-car-loan-amortization-with-penalty-and-compound-interest)
2. [System Architecture](#2-system-architecture)
3. [System Structure](#3-system-structure)
4. [Key Terms and Definitions](#4-key-terms-and-definitions)
5. [Function Documentation](#5-how-each-function-works)

---

## 1. Algorithm for Car Loan Amortization with Penalty and Compound Interest

### 1.1 Core Amortization Formula

The system uses the **Standard Amortization Formula** with adjustments for compound interest frequency:

$$M = P \times \frac{r(1+r)^n}{(1+r)^n - 1}$$

Where:
- **M** = Monthly Payment
- **P** = Principal (Amount Financed)
- **r** = Effective Monthly Interest Rate
- **n** = Total Number of Payments (months)

### 1.2 Compound Interest Calculation

The system supports multiple compounding frequencies: **Monthly, Quarterly, Semi-Annually, and Annually**.

#### Step 1: Calculate Effective Annual Rate (EAR)
$$EAR = \left(1 + \frac{r_{annual}}{m}\right)^m - 1$$

Where:
- $r_{annual}$ = Annual Interest Rate (as decimal)
- $m$ = Compounding periods per year (12 for monthly, 4 for quarterly, etc.)

#### Step 2: Convert to Effective Monthly Rate
$$r_{monthly} = (1 + EAR)^{\frac{1}{12}} - 1$$

### 1.3 Algorithm Flowchart

```
┌─────────────────────────────────────────────────────────────┐
│                    START CALCULATION                         │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  INPUT: Car Price, Tax Rate, Registration Fee,              │
│         Down Payment, Trade-In, Interest Rate,              │
│         Term (Years), Compounding Frequency,                │
│         Penalty Rate, Missed Payments, Extra Payment        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  STEP 1: Calculate Total Vehicle Cost                       │
│  Total Cost = Car Price + (Car Price × Tax Rate) + Reg Fee  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  STEP 2: Calculate Amount Financed                          │
│  Amount Financed = Total Cost - Down Payment - Trade-In     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  STEP 3: Calculate Effective Monthly Rate                   │
│  Based on Compounding Frequency                             │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  STEP 4: Calculate Monthly Payment using Amortization       │
│  Formula: M = P × [r(1+r)^n] / [(1+r)^n - 1]               │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  STEP 5: Generate Amortization Schedule                     │
│  FOR each month (1 to n):                                   │
│    - Calculate Interest = Balance × Monthly Rate            │
│    - IF missed payment: Apply Penalty, Capitalize Interest  │
│    - ELSE: Principal = Payment - Interest                   │
│    - Update Balance = Balance - Principal                   │
│    - Track Cumulative Totals                                │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  OUTPUT: Monthly Payment, Total Interest, Total Penalties,  │
│          Amortization Schedule (Payment-by-Payment)         │
└─────────────────────────────────────────────────────────────┘
```

### 1.4 Penalty Calculation Algorithm

When a payment is missed:

```
Penalty = Outstanding Balance × (Penalty Rate / 100)
```

**Missed Payment Behavior:**
1. No payment is made for that month
2. Interest for the month is **capitalized** (added to principal)
3. Penalty is calculated and tracked
4. Loan term may extend to accommodate missed payments

### 1.5 Extra Payment Handling

```
Adjusted Payment = Standard Monthly Payment + Extra Payment Per Month
```

Extra payments are applied directly to the **principal**, reducing:
- Total interest paid over the life of the loan
- The effective loan term (fewer payments needed)

---

## 2. System Architecture

### 2.1 Architecture Pattern: **MVC (Model-View-Controller)**

The system follows the **Model-View-Controller** architectural pattern, which separates the application into three interconnected components:

```
┌─────────────────────────────────────────────────────────────────────┐
│                         USER INTERFACE                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                 │
│  │  CarsPanel  │  │CalculatePanel│ │ ComparePanel │                 │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘                 │
│         │                │                │                         │
│         └────────────────┼────────────────┘                         │
│                          │                                          │
│                    ┌─────┴─────┐                                    │
│                    │ MainFrame │ (CardLayout Navigation)            │
│                    └─────┬─────┘                                    │
└──────────────────────────┼──────────────────────────────────────────┘
                           │
                    ┌──────┴──────┐
                    │   VIEWS     │ ◄─── Presentation Layer
                    └──────┬──────┘
                           │
         ┌─────────────────┼─────────────────┐
         │                 │                 │
         ▼                 ▼                 ▼
┌─────────────┐   ┌─────────────┐   ┌─────────────────┐
│    Car      │   │    Loan     │   │   Comparison    │
│ Controller  │   │ Controller  │   │   Controller    │
└──────┬──────┘   └──────┬──────┘   └────────┬────────┘
       │                 │                   │
       └─────────────────┼───────────────────┘
                         │
                  ┌──────┴──────┐
                  │ CONTROLLERS │ ◄─── Business Logic Layer
                  └──────┬──────┘
                         │
         ┌───────────────┼───────────────┐
         │               │               │
         ▼               ▼               ▼
   ┌──────────┐   ┌────────────┐   ┌─────────────┐
   │   Car    │   │    Loan    │   │   Loan      │
   │  Model   │   │Calculation │   │  Scenario   │
   └──────────┘   └────────────┘   └─────────────┘
         │               │               │
         └───────────────┼───────────────┘
                         │
                  ┌──────┴──────┐
                  │   MODELS    │ ◄─── Data Layer
                  └─────────────┘
```

### 2.2 Layer Responsibilities

| Layer | Package | Responsibility |
|-------|---------|----------------|
| **View** | `com.vismera.views` | User interface, input handling, display formatting |
| **Controller** | `com.vismera.controllers` | Business logic, data processing, calculation orchestration |
| **Model** | `com.vismera.models` | Data structures, calculation algorithms, state management |
| **Utilities** | `com.vismera.utils` | Helper functions, formatting, validation, export |

### 2.3 Design Patterns Used

| Pattern | Implementation | Purpose |
|---------|---------------|---------|
| **Singleton** | Controllers (CarController, LoanController, ComparisonController) | Single instance for centralized data management |
| **MVC** | Overall architecture | Separation of concerns |
| **Observer** | Event listeners on UI components | React to user input |
| **Factory** | `LoanController.createLoanCalculation()` | Object creation encapsulation |
| **Strategy** | Compounding frequency selection | Interchangeable calculation strategies |

---

## 3. System Structure

### 3.1 Package Organization

```
src/
├── carloanamortizationproject/
│   └── CarLoanAmortizationProject.java    # Application Entry Point
│
├── com/vismera/
│   │
│   ├── models/                             # DATA LAYER (Entities)
│   │   ├── Car.java                        # Vehicle entity (in-memory sample data)
│   │   ├── AmortizationEntry.java          # Single payment record
│   │   ├── LoanCalculation.java            # Core calculation engine
│   │   └── LoanScenario.java               # Comparison scenario model
│   │
│   ├── controllers/                        # BUSINESS LOGIC LAYER
│   │   ├── CarController.java              # Car sample data provider
│   │   ├── LoanController.java             # Loan calculation logic
│   │   └── ComparisonController.java       # Scenario comparison logic
│   │
│   ├── views/                              # PRESENTATION LAYER
│   │   ├── MainFrame.java                  # Main application window
│   │   ├── CarsPanel.java                  # Car selection interface
│   │   ├── CalculatePanel.java             # Loan input form
│   │   ├── LoanSummaryDialog.java          # Results popup
│   │   ├── AmortizationScheduleFrame.java  # Payment schedule table
│   │   └── ComparePanel.java               # Scenario comparison
│   │
│   └── utils/                              # UTILITY LAYER
│       ├── FormatUtils.java                # Number/currency formatting
│       ├── ValidationUtils.java            # Input validation
│       ├── SecureFileExporter.java         # TXT export with SHA-256 hashing
│       ├── CSVExporter.java                # Data export functionality
│       └── UIStyler.java                   # UI theming and styling
│
└── resources/
    └── images/                             # Car images (placeholder)
```

### 3.2 Class Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                           MODELS                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────┐      ┌─────────────────────────────────┐  │
│  │      Car        │      │       LoanCalculation           │  │
│  ├─────────────────┤      ├─────────────────────────────────┤  │
│  │ - id: int       │      │ - carPrice: double              │  │
│  │ - make: String  │      │ - salesTaxRate: double          │  │
│  │ - model: String │      │ - registrationFee: double       │  │
│  │ - year: int     │      │ - downPayment: double           │  │
│  │ - category: Str │      │ - tradeInValue: double          │  │
│  │ - color: String │      │ - annualInterestRate: double    │  │
│  │ - mpg: int      │      │ - loanTermYears: int            │  │
│  │ - price: double │      │ - compoundingFrequency: String  │  │
│  │ - imagePath: Str│      │ - penaltyRate: double           │  │
│  ├─────────────────┤      │ - missedPayments: int           │  │
│  │ + getFullName() │      │ - extraPaymentPerMonth: double  │  │
│  └─────────────────┘      ├─────────────────────────────────┤  │
│                           │ + calculateTotalCost()          │  │
│  ┌─────────────────┐      │ + calculateAmountFinanced()     │  │
│  │AmortizationEntry│      │ + calculateMonthlyPayment()     │  │
│  ├─────────────────┤      │ + calculateTotalInterest()      │  │
│  │ - paymentNumber │      │ + calculateTotalPenalties()     │  │
│  │ - payment       │      │ + generateAmortizationSchedule()│  │
│  │ - principal     │      └─────────────────────────────────┘  │
│  │ - interest      │                                           │
│  │ - penalty       │      ┌─────────────────────────────────┐  │
│  │ - balance       │      │        LoanScenario             │  │
│  │ - totalPaid     │      ├─────────────────────────────────┤  │
│  └─────────────────┘      │ - scenarioName: String          │  │
│                           │ - loanAmount: double            │  │
│                           │ - interestRate: double          │  │
│                           │ - termYears: int                │  │
│                           │ - monthlyPayment: double        │  │
│                           │ - totalInterest: double         │  │
│                           │ - totalCost: double             │  │
│                           │ - isBestDeal: boolean           │  │
│                           ├─────────────────────────────────┤  │
│                           │ + calculateMetrics()            │  │
│                           └─────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### 3.3 Data Flow Diagram

```
┌──────────┐     ┌──────────────┐     ┌────────────────┐
│   User   │────▶│  CarsPanel   │────▶│ Selected Car   │
└──────────┘     └──────────────┘     └───────┬────────┘
                                              │
                                              ▼
                                    ┌─────────────────┐
                                    │ CalculatePanel  │
                                    │ (Loan Details)  │
                                    └────────┬────────┘
                                             │
                         ┌───────────────────┼───────────────────┐
                         │                   │                   │
                         ▼                   ▼                   ▼
              ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
              │ ValidationUtils │ │ LoanController  │ │  FormatUtils    │
              │   (Validate)    │ │  (Calculate)    │ │   (Format)      │
              └─────────────────┘ └────────┬────────┘ └─────────────────┘
                                           │
                                           ▼
                                 ┌─────────────────┐
                                 │ LoanCalculation │
                                 │ (Core Engine)   │
                                 └────────┬────────┘
                                          │
                         ┌────────────────┼────────────────┐
                         │                │                │
                         ▼                ▼                ▼
              ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
              │ Monthly Payment │ │ Amortization    │ │ Total Interest  │
              │    Result       │ │   Schedule      │ │  & Penalties    │
              └────────┬────────┘ └────────┬────────┘ └────────┬────────┘
                       │                   │                   │
                       └───────────────────┼───────────────────┘
                                           │
                                           ▼
                                 ┌─────────────────┐
                                 │LoanSummaryDialog│
                                 └────────┬────────┘
                                          │
                         ┌────────────────┴────────────────┐
                         │                                 │
                         ▼                                 ▼
              ┌─────────────────────┐          ┌─────────────────┐
              │AmortizationSchedule │          │   CSVExporter   │
              │      Frame          │          │   (Export)      │
              └─────────────────────┘          └─────────────────┘
```

---

## 4. Key Terms and Definitions

### 4.1 Financial Terms

| Term | Definition | Formula/Example |
|------|------------|-----------------|
| **Principal** | The original amount borrowed (amount financed) | Total Cost - Down Payment - Trade-In |
| **Amortization** | The process of spreading loan payments over time, with each payment covering both principal and interest | Monthly schedule showing payment breakdown |
| **Interest** | The cost of borrowing money, calculated as a percentage of the outstanding balance | Balance × Monthly Rate |
| **Compound Interest** | Interest calculated on both the initial principal and accumulated interest | $(1 + r/n)^{nt}$ |
| **APR (Annual Percentage Rate)** | The yearly interest rate charged on a loan | 6.5% per year |
| **Effective Annual Rate (EAR)** | The actual annual rate when compounding is considered | $(1 + r/m)^m - 1$ |
| **Down Payment** | An upfront payment reducing the loan amount | Cash paid at purchase |
| **Trade-In Value** | Credit for an existing vehicle applied to the new purchase | Value of old car |
| **Penalty** | A fee charged when a payment is missed | Balance × Penalty Rate |
| **Capitalization** | Adding unpaid interest to the principal balance | Increases total debt |

### 4.2 System-Specific Terms

| Term | Definition | Location in System |
|------|------------|-------------------|
| **Compounding Frequency** | How often interest is calculated and added | `LoanCalculation.compoundingFrequency` |
| **Missed Payments** | Simulated number of payments skipped | `LoanCalculation.missedPayments` |
| **Extra Payment** | Additional amount paid above the minimum | `LoanCalculation.extraPaymentPerMonth` |
| **Amount Financed** | The actual loan amount after deductions | `calculateAmountFinanced()` |
| **Total Vehicle Cost** | Car price + taxes + fees | `calculateTotalCost()` |

---

## 5. How Each Function Works

### 5.1 Model Functions

#### `LoanCalculation.calculateTotalCost()`
```java
/**
 * Calculates the total vehicle cost including taxes and fees.
 * 
 * Formula: Total = Car Price + Tax Amount + Registration Fee
 * Tax Amount = Car Price × (Tax Rate / 100)
 * 
 * Example:
 *   Car Price: $50,000
 *   Tax Rate: 8%
 *   Registration: $500
 *   Total = $50,000 + $4,000 + $500 = $54,500
 */
public double calculateTotalCost() {
    return carPrice + calculateTaxAmount() + registrationFee;
}
```

#### `LoanCalculation.calculateAmountFinanced()`
```java
/**
 * Calculates the loan principal after deductions.
 * 
 * Formula: Amount Financed = Total Cost - Down Payment - Trade-In
 * 
 * Example:
 *   Total Cost: $54,500
 *   Down Payment: $10,000
 *   Trade-In: $5,000
 *   Amount Financed = $54,500 - $10,000 - $5,000 = $39,500
 */
public double calculateAmountFinanced() {
    double totalCost = calculateTotalCost();
    double reductions = downPayment + tradeInValue;
    return Math.max(0, totalCost - reductions);
}
```

#### `LoanCalculation.calculateMonthlyPayment()`
```java
/**
 * Calculates the fixed monthly payment using the amortization formula.
 * 
 * Steps:
 * 1. Get principal (amount financed)
 * 2. Calculate effective monthly rate based on compounding frequency
 * 3. Apply standard amortization formula
 * 
 * Formula: M = P × [r(1+r)^n] / [(1+r)^n - 1]
 * 
 * Example:
 *   Principal: $39,500
 *   Annual Rate: 6.5%
 *   Term: 5 years (60 months)
 *   Monthly Payment ≈ $773.31
 */
public double calculateMonthlyPayment() {
    double principal = calculateAmountFinanced();
    int totalMonths = loanTermYears * 12;
    
    // Convert annual rate to effective monthly rate
    double annualRate = annualInterestRate / 100.0;
    int compoundingPeriods = getCompoundingPeriodsPerYear();
    double effectiveAnnualRate = Math.pow(1 + (annualRate / compoundingPeriods), 
                                          compoundingPeriods) - 1;
    double monthlyRate = Math.pow(1 + effectiveAnnualRate, 1.0 / 12.0) - 1;
    
    // Amortization formula
    double numerator = monthlyRate * Math.pow(1 + monthlyRate, totalMonths);
    double denominator = Math.pow(1 + monthlyRate, totalMonths) - 1;
    
    return principal * (numerator / denominator);
}
```

#### `LoanCalculation.generateAmortizationSchedule()`
```java
/**
 * Generates a complete payment-by-payment schedule.
 * 
 * For each month:
 * 1. Calculate interest portion: Balance × Monthly Rate
 * 2. Check if payment is missed:
 *    - YES: Apply penalty, capitalize interest (add to balance)
 *    - NO: Calculate principal = Payment - Interest
 * 3. Update remaining balance
 * 4. Track cumulative totals
 * 
 * Output: List of AmortizationEntry objects
 * 
 * Handles:
 * - Regular payments
 * - Missed payments with penalties
 * - Extra payments (reduces principal faster)
 * - Interest capitalization
 */
public List<AmortizationEntry> generateAmortizationSchedule() {
    // Initialize
    List<AmortizationEntry> schedule = new ArrayList<>();
    double balance = calculateAmountFinanced();
    double payment = calculateMonthlyPayment() + extraPaymentPerMonth;
    
    for (int month = 1; balance > 0.01; month++) {
        double interest = balance * monthlyRate;
        double penalty = 0;
        double principal = 0;
        
        if (month <= missedPayments) {
            // Missed payment
            penalty = balance * (penaltyRate / 100);
            balance += interest; // Capitalize interest
        } else {
            // Normal payment
            principal = payment - interest;
            balance -= principal;
        }
        
        schedule.add(new AmortizationEntry(month, payment, principal, 
                                           interest, penalty, balance, cumulative));
    }
    
    return schedule;
}
```

### 8.2 Controller Functions

#### `CarController.searchCars(String query)`
```java
/**
 * Filters cars based on search query.
 * 
 * Searches across multiple fields:
 * - Make (e.g., "Mercedes")
 * - Model (e.g., "S-Class")
 * - Category (e.g., "Luxury Sedan")
 * - Color (e.g., "Silver")
 * - Year (e.g., "2024")
 * 
 * Case-insensitive matching using contains().
 * 
 * @param query The search string
 * @return List of matching Car objects
 */
public List<Car> searchCars(String query) {
    String lowerQuery = query.toLowerCase();
    return cars.stream()
        .filter(car -> 
            car.getMake().toLowerCase().contains(lowerQuery) ||
            car.getModel().toLowerCase().contains(lowerQuery) ||
            car.getCategory().toLowerCase().contains(lowerQuery))
        .collect(Collectors.toList());
}
```

#### `ComparisonController.findBestDeal()`
```java
/**
 * Identifies the scenario with the lowest total cost.
 * 
 * Algorithm:
 * 1. Reset all scenarios' bestDeal flag to false
 * 2. Find minimum total cost using stream reduction
 * 3. Mark that scenario as bestDeal = true
 * 4. Return the best scenario
 * 
 * Used to highlight the optimal loan option in the comparison table.
 */
public LoanScenario findBestDeal() {
    scenarios.forEach(s -> s.setBestDeal(false));
    
    LoanScenario best = scenarios.stream()
        .min(Comparator.comparingDouble(LoanScenario::getTotalCost))
        .orElse(null);
    
    if (best != null) {
        best.setBestDeal(true);
    }
    return best;
}
```

### 8.3 Utility Functions

#### `FormatUtils.formatCurrency(double amount)`
```java
/**
 * Formats a number as US currency.
 * 
 * Input: 54500.00
 * Output: "$54,500.00"
 * 
 * Uses NumberFormat.getCurrencyInstance(Locale.US)
 */
public static String formatCurrency(double amount) {
    return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
}
```

#### `ValidationUtils.validateLoanInputs(...)`
```java
/**
 * Validates all loan input fields.
 * 
 * Checks:
 * - Car Price: Must be positive number
 * - Interest Rate: Must be between 0-50%
 * - Loan Term: Must be positive integer
 * - Down Payment: Must be non-negative
 * - Trade-In: Must be non-negative
 * 
 * @return Empty string if valid, error messages if invalid
 */
public static String validateLoanInputs(String carPrice, String interestRate, 
                                        String loanTerm, String downPayment, 
                                        String tradeIn) {
    StringBuilder errors = new StringBuilder();
    
    if (!validatePositiveNumber(carPrice)) {
        errors.append("• Car Price must be a positive number\n");
    }
    // ... additional validations
    
    return errors.toString();
}
```

#### `SecureFileExporter.exportSecureSchedule(...)`
```java
/**
 * Exports amortization schedule to secure TXT file with SHA-256 hash per record.
 * Uses horizontal pipe-delimited format with no filler characters.
 * 
 * Output Format (per record):
 * ID|Name|Type|Status|Amount|Rate|Payment|Interest|Total|Term|DateRange | SHA256_HASH
 * ----------------------------------------
 * 
 * Example:
 * 43|Loan Payment #43|Amortization|Active|₱7,776,500.00|6.50%|₱152,156.15|₱2,408.60|₱6,542,649.45|5|Dec 10, 2025 - Dec 10, 2030 | 1F1E4387A80FFDB6F7FB1569F1F117F25A5FC23C63C52A8490B98F515B5FEFBD
 * ----------------------------------------
 * 
 * @param entries List of AmortizationEntry
 * @param loan LoanCalculation object with loan details
 * @param filePath Destination file path
 * @return true if successful
 */
public static boolean exportSecureSchedule(List<AmortizationEntry> entries, 
                                           LoanCalculation loan,
                                           String filePath) {
    try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
        int recordId = 1;
        for (AmortizationEntry entry : entries) {
            // Build pipe-delimited record (no filler/padding)
            String recordData = String.format("%d|Loan Payment #%d|Amortization|Active|₱%.2f|%.2f%%|₱%.2f|₱%.2f|₱%.2f|%d|%s",
                recordId, entry.getPaymentNumber(), loan.calculateAmountFinanced(),
                loan.getAnnualInterestRate(), entry.getPayment(), entry.getInterest(),
                entry.getTotalPaid(), loan.getLoanTermYears(), dateRange);
            
            // Generate SHA-256 hash and write: DATA | HASH
            String hash = hashSHA256(recordData);
            writer.println(recordData + " | " + hash);
            writer.println("----------------------------------------");
            recordId++;
        }
        return true;
    } catch (IOException e) {
        return false;
    }
}

/**
 * Generate SHA-256 hash (uppercase hex, 64 characters)
 */
public static String hashSHA256(String input) {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
    StringBuilder hexString = new StringBuilder();
    for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex.toUpperCase());
    }
    return hexString.toString();
}
```

### 8.4 View Event Handlers

#### `CalculatePanel.calculateLoan()`
```java
/**
 * Triggered when user clicks "Calculate Loan" button.
 * 
 * Flow:
 * 1. Validate all input fields
 * 2. If validation fails → Show error dialog
 * 3. If valid → Create LoanCalculation object
 * 4. Perform calculations via LoanController
 * 5. Display LoanSummaryDialog with results
 */
private void calculateLoan() {
    // Step 1: Validate
    String errors = ValidationUtils.validateLoanInputs(...);
    
    if (!errors.isEmpty()) {
        // Step 2: Show errors
        JOptionPane.showMessageDialog(this, errors, "Validation Error", ERROR_MESSAGE);
        return;
    }
    
    // Step 3-4: Create and calculate
    LoanCalculation loan = LoanController.getInstance().createLoanCalculation(
        FormatUtils.parseDouble(carPriceField.getText()),
        // ... all parameters
    );
    
    // Step 5: Show results
    LoanSummaryDialog dialog = new LoanSummaryDialog(parentFrame, loan);
    dialog.setVisible(true);
}
```

#### `ComparePanel.addScenario()`
```java
/**
 * Adds a new comparison scenario to the table.
 * 
 * Flow:
 * 1. Validate scenario inputs
 * 2. Create LoanScenario via controller
 * 3. Refresh comparison table
 * 4. Update best deal indicator
 * 5. Reset input fields for next entry
 */
private void addScenario() {
    // Validation
    if (!ValidationUtils.validatePositiveNumber(loanAmountField.getText())) {
        showError("Please enter a valid loan amount.");
        return;
    }
    
    // Create scenario
    controller.createScenario(
        scenarioNameField.getText(),
        FormatUtils.parseDouble(loanAmountField.getText()),
        FormatUtils.parseDouble(interestRateField.getText()),
        FormatUtils.parseInt(termYearsField.getText())
    );
    
    // Refresh display
    refreshTable();
}
```

### 5.5 Import/Export System Algorithms

#### `SecureFileExporter.importSecureSchedule(String filePath)`
```java
/**
 * Imports and validates TXT file with SHA-256 hash verification.
 * 
 * Algorithm Flow:
 * 1. Read file line by line
 * 2. Parse pipe-delimited records
 * 3. Extract data and hash from each line
 * 4. Recalculate SHA-256 hash from data
 * 5. Compare calculated vs stored hash
 * 6. If mismatch → Mark as tampered
 * 7. Display import dialog with verification results
 * 
 * Input Format (per line):
 * ID|Name|Type|Status|Amount|Rate|Payment|Interest|Total|Term|DateRange | ORIGINAL_HASH
 * 
 * Validation Process:
 * - Extract: "43|Loan Payment #43|Amortization|Active|₱7,776,500.00|..."
 * - Calculate: SHA256("43|Loan Payment #43|Amortization|Active|₱7,776,500.00|...")
 * - Compare: CALCULATED_HASH == STORED_HASH
 * 
 * @param filePath Path to TXT file
 * @return ImportResult object containing data and validation status
 */
public static ImportResult importSecureSchedule(String filePath) {
    List<ImportRecord> records = new ArrayList<>();
    List<HashValidation> validations = new ArrayList<>();
    
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        int lineNumber = 1;
        
        while ((line = reader.readLine()) != null) {
            // Skip separator lines
            if (line.startsWith("----")) continue;
            
            // Parse: DATA | HASH
            String[] parts = line.split(" \\| ");
            if (parts.length != 2) continue;
            
            String recordData = parts[0];
            String storedHash = parts[1];
            
            // Recalculate hash
            String calculatedHash = hashSHA256(recordData);
            
            // Parse record fields
            String[] fields = recordData.split("\\|");
            ImportRecord record = new ImportRecord(
                Integer.parseInt(fields[0]),    // ID
                fields[1],                      // Name
                fields[2],                      // Type
                fields[3],                      // Status
                parseAmount(fields[4]),         // Amount
                parseRate(fields[5]),           // Rate
                parseAmount(fields[6]),         // Payment
                parseAmount(fields[7]),         // Interest
                parseAmount(fields[8]),         // Total
                Integer.parseInt(fields[9]),    // Term
                fields[10]                      // DateRange
            );
            
            // Validation result
            boolean isValid = calculatedHash.equals(storedHash);
            validations.add(new HashValidation(
                lineNumber, recordData, storedHash, 
                calculatedHash, isValid
            ));
            
            records.add(record);
            lineNumber++;
        }
        
        return new ImportResult(records, validations);
        
    } catch (IOException | NumberFormatException e) {
        throw new ImportException("File format error: " + e.getMessage());
    }
}
```

#### Import Dialog Display Algorithm
```java
/**
 * Creates tabbed dialog showing import results.
 * 
 * Tab 1: Loan Records Table
 * ┌─────┬──────────────────┬──────────┬─────────┬─────────────┐
 * │ ID  │ Name             │ Type     │ Payment │ Interest    │
 * ├─────┼──────────────────┼──────────┼─────────┼─────────────┤
 * │ 43  │ Loan Payment #43 │ Amortiz. │₱152,156 │₱2,408      │
 * └─────┴──────────────────┴──────────┴─────────┴─────────────┘
 * 
 * Tab 2: Hash Verification Table
 * ┌──────┬──────────┬─────────────────┬─────────────────┬─────────┐
 * │ Line │ Status   │ Stored Hash     │ Calculated Hash │ Valid   │
 * ├──────┼──────────┼─────────────────┼─────────────────┼─────────┤
 * │ 1    │ ✓ Valid  │ 1F1E4387A8...   │ 1F1E4387A8...   │ ✓ Match │
 * │ 2    │ ✗ Invalid│ ABC123DEF4...   │ XYZ789GHI1...   │ ✗ Tamper│
 * └──────┴──────────┴─────────────────┴─────────────────┴─────────┘
 * 
 * Color Coding:
 * - Valid records: Green background
 * - Tampered records: Red background
 * - Hash mismatch: Red text
 */
private static void showImportDialog(ImportResult result) {
    JDialog dialog = new JDialog();
    JTabbedPane tabbedPane = new JTabbedPane();
    
    // Tab 1: Records
    JTable recordsTable = createRecordsTable(result.getRecords());
    tabbedPane.addTab("Loan Records", new JScrollPane(recordsTable));
    
    // Tab 2: Hash Verification
    JTable hashTable = createHashVerificationTable(result.getValidations());
    tabbedPane.addTab("SHA-256 Hash Verification", new JScrollPane(hashTable));
    
    dialog.add(tabbedPane);
    dialog.setModal(true);
    dialog.setVisible(true);
}
```

#### Export Algorithm Flowchart
```
┌─────────────────────────────────────────────────────────────┐
│                    EXPORT ALGORITHM                         │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  INPUT: List<AmortizationEntry>, LoanCalculation           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  Initialize: recordId = 1, FileWriter                      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
                     ◇─────────────────◇
                    ╱                   ╲
                   ╱ For each entry in   ╲
                   ╲    entries list     ╱
                    ╲                   ╱
                     ◇────────┬────────◇
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  Build Record String (Pipe-Delimited):                     │
│  ID|Name|Type|Status|Amount|Rate|Payment|Interest|Total|    │
│  Term|DateRange                                             │
│                                                             │
│  Example:                                                   │
│  "43|Loan Payment #43|Amortization|Active|₱7,776,500.00|   │
│  6.50%|₱152,156.15|₱2,408.60|₱6,542,649.45|5|             │
│  Dec 10, 2025 - Dec 10, 2030"                              │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  Calculate SHA-256 Hash:                                    │
│  1. Convert string to UTF-8 bytes                          │
│  2. Apply SHA-256 algorithm                                 │
│  3. Convert result to uppercase hex                         │
│                                                             │
│  Result: "1F1E4387A80FFDB6F7FB1569F1F117F25A5FC23C63C52A   │
│          8490B98F515B5FEFBD"                                │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  Write to File:                                             │
│  recordData + " | " + hash                                  │
│  "----------------------------------------"                 │
│                                                             │
│  Output Line:                                               │
│  43|Loan Payment #43|...|Dec 10, 2030 | 1F1E4387A8...      │
│  ----------------------------------------                   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  recordId++, Continue Loop                                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  Close File, Return Success                                 │
└─────────────────────────────────────────────────────────────┘
```

#### Import Algorithm Flowchart
```
┌─────────────────────────────────────────────────────────────┐
│                    IMPORT ALGORITHM                         │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  INPUT: File Path (TXT file)                                │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  Initialize: records = [], validations = [], lineNum = 1   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
                     ◇─────────────────◇
                    ╱                   ╲
                   ╱ Read next line from ╲
                   ╲       file          ╱
                    ╲                   ╱
                     ◇────────┬────────◇
                              │
                        ┌─────┴─────┐
                        │           │
                   END OF FILE    HAS DATA
                        │           │
                        ▼           ▼
               ┌─────────────┐ ┌─────────────────────────────┐
               │   FINISH    │ │ Skip if separator line      │
               │   IMPORT    │ │ (starts with "----")        │
               └─────────────┘ └──────────┬──────────────────┘
                        │                 │
                        │                 ▼
                        │    ┌─────────────────────────────┐
                        │    │ Split line: "DATA | HASH"   │
                        │    └──────────┬──────────────────┘
                        │               │
                        │               ▼
                        │    ┌─────────────────────────────┐
                        │    │ Extract: recordData, hash   │
                        │    └──────────┬──────────────────┘
                        │               │
                        │               ▼
                        │    ┌─────────────────────────────┐
                        │    │ Recalculate SHA-256 hash    │
                        │    │ from recordData             │
                        │    └──────────┬──────────────────┘
                        │               │
                        │               ▼
                        │         ◇─────────────────◇
                        │        ╱                   ╲
                        │       ╱ calculatedHash ==   ╲
                        │       ╲    storedHash ?     ╱
                        │        ╲                   ╱
                        │         ◇────────┬────────◇
                        │                  │
                        │         ┌────────┴────────┐
                        │         │                 │
                        │      MATCH           NO MATCH
                        │         │                 │
                        │         ▼                 ▼
                        │ ┌──────────────┐ ┌──────────────────┐
                        │ │ Mark as VALID│ │ Mark as TAMPERED │
                        │ │ (Green)      │ │ (Red)            │
                        │ └──────┬───────┘ └──────┬───────────┘
                        │        │                │
                        │        └────────────────┘
                        │                 │
                        │                 ▼
                        │    ┌─────────────────────────────┐
                        │    │ Parse record fields:        │
                        │    │ Split by "|"                │
                        │    │ Create ImportRecord object  │
                        │    └──────────┬──────────────────┘
                        │               │
                        │               ▼
                        │    ┌─────────────────────────────┐
                        │    │ Add to records list         │
                        │    │ Add to validations list     │
                        │    │ lineNum++                   │
                        │    └──────────┬──────────────────┘
                        │               │
                        │               └─────────────────────────┐
                        │                                         │
                        └─────────────────────────────────────────┘
                                                 │
                                                 ▼
                                ┌─────────────────────────────┐
                                │ Return ImportResult:        │
                                │ - List of parsed records    │
                                │ - Hash validation results   │
                                │ - Tampering indicators      │
                                └─────────────────────────────┘
```

#### Hash Verification Algorithm
```java
/**
 * SHA-256 Hash Verification Process
 * 
 * Purpose: Detect data tampering in exported files
 * 
 * Steps:
 * 1. Original Export: Calculate SHA-256 of record data
 * 2. File Storage: Store "DATA | HASH" format
 * 3. Import Verification: Recalculate hash, compare
 * 4. Result: VALID (untampered) or INVALID (tampered)
 * 
 * Security Features:
 * - 256-bit cryptographic hash (virtually impossible to forge)
 * - Detects any modification: single character change = different hash
 * - Uppercase hex format for consistency
 * - UTF-8 encoding for international character support
 */
public static String hashSHA256(String input) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex.toUpperCase());
        }
        
        return hexString.toString();
        
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("SHA-256 algorithm not available", e);
    }
}
```

#### Data Integrity Validation
```java
/**
 * Validation Results Processing
 * 
 * Classification:
 * - VALID: Hash matches → Data is authentic
 * - TAMPERED: Hash mismatch → Data modified after export
 * - CORRUPTED: Parse error → File format issues
 * - MISSING: Required fields absent → Incomplete record
 * 
 * Visual Indicators:
 * - Green checkmark (✓): Verified authentic
 * - Red X mark (✗): Detected tampering
 * - Yellow warning (⚠): Format issues
 * - Gray dash (-): Unable to verify
 */
public enum ValidationStatus {
    VALID("✓ Valid", Color.GREEN),
    TAMPERED("✗ Tampered", Color.RED),
    CORRUPTED("⚠ Corrupted", Color.ORANGE),
    MISSING("- Missing", Color.GRAY);
    
    private final String displayText;
    private final Color color;
    
    ValidationStatus(String displayText, Color color) {
        this.displayText = displayText;
        this.color = color;
    }
    
    public String getDisplayText() { return displayText; }
    public Color getColor() { return color; }
}
```

---

## Appendix: Quick Reference

### Color Scheme
| Color | Hex Code | Usage |
|-------|----------|-------|
| Primary Blue | `#2563EB` | Buttons, headers, primary actions |
| Accent Green | `#10B981` | Success states, best deal indicator |
| Error Red | `#EF4444` | Errors, penalties, delete buttons |
| Text Dark | `#1F2937` | Primary text |
| Background | `#F3F4F6` | Page backgrounds |

### Keyboard Shortcuts
| Action | Shortcut |
|--------|----------|
| Search Cars | Type in search field |
| Calculate | Click button or Enter |
| Close Dialog | Escape or Click Close |

### File Formats
| Export Type | Format | Extension | Security |
|-------------|--------|-----------|----------|
| Amortization Schedule | TXT (Pipe-delimited) | `.txt` | SHA-256 Hash per record |

### Secure Export Format
```
ID|Name|Type|Status|Amount|Rate|Payment|Interest|Total|Term|DateRange | SHA256_HASH
----------------------------------------
```

**Example Record:**
```
43|Loan Payment #43|Amortization|Active|₱7,776,500.00|6.50%|₱152,156.15|₱2,408.60|₱6,542,649.45|5|Dec 10, 2025 - Dec 10, 2030 | 1F1E4387A80FFDB6F7FB1569F1F117F25A5FC23C63C52A8490B98F515B5FEFBD
----------------------------------------
```

**Features:**
- Horizontal pipe-delimited format (no filler characters)
- SHA-256 hash appended to each record for integrity verification
- Import validates hash to detect data tampering
- Hash displayed in uppercase hexadecimal (64 characters)

---

*Documentation prepared for Vismerá Inc. Car Loan Amortization Calculator*
*Version 5.0 | Calculator Application - No Database Required | December 2025*
