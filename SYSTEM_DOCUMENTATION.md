# Vismerá Inc. Car Loan Amortization System with Database
## Technical Documentation for Defense Presentation
### Document Version: 3.0 (H2 Embedded Database - No Server Required)

---

## Table of Contents
1. [Algorithm for Car Loan Amortization](#1-algorithm-for-car-loan-amortization-with-penalty-and-compound-interest)
2. [System Architecture](#2-system-architecture)
3. [System Structure](#3-system-structure)
4. [Database Architecture](#4-database-architecture)
5. [Data Access Layer (DAO)](#5-data-access-layer-dao)
6. [Admin System](#6-admin-system)
7. [Key Terms and Definitions](#7-key-terms-and-definitions)
8. [Function Documentation](#8-how-each-function-works)

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
│   │   ├── Car.java                        # Vehicle entity (DB-backed)
│   │   ├── Customer.java                   # Customer entity (NEW)
│   │   ├── Loan.java                       # Loan entity (NEW)
│   │   ├── AmortizationEntry.java          # Single payment record (in-memory)
│   │   ├── AmortizationRow.java            # DB-backed amortization row (NEW)
│   │   ├── Payment.java                    # Payment entity (NEW)
│   │   ├── AdminSetting.java               # System settings entity (NEW)
│   │   ├── LoanCalculation.java            # Core calculation engine
│   │   └── LoanScenario.java               # Comparison scenario model
│   │
│   ├── dao/                                # DATA ACCESS LAYER
│   │   ├── CustomerDAO.java                # Customer CRUD operations
│   │   ├── CarDAO.java                     # Car CRUD operations
│   │   ├── LoanDAO.java                    # Loan CRUD operations
│   │   ├── AmortizationRowDAO.java         # Amortization CRUD operations
│   │   ├── PaymentDAO.java                 # Payment CRUD operations
│   │   └── AdminSettingDAO.java            # Settings CRUD operations
│   │
│   ├── config/                             # CONFIGURATION LAYER
│   │   └── DatabaseConfig.java             # H2 connection & auto-schema
│   │
│   ├── controllers/                        # BUSINESS LOGIC LAYER
│   │   ├── CarController.java              # Car inventory management
│   │   ├── CustomerController.java         # Customer management (NEW)
│   │   ├── LoanController.java             # Loan calculation (in-memory)
│   │   ├── LoanControllerDB.java           # Loan management (DB-backed, NEW)
│   │   ├── PaymentController.java          # Payment processing (NEW)
│   │   ├── SettingsController.java         # System settings (NEW)
│   │   ├── ReportController.java           # Analytics & reports (NEW)
│   │   └── ComparisonController.java       # Scenario comparison logic
│   │
│   ├── views/                              # PRESENTATION LAYER (Customer)
│   │   ├── MainFrame.java                  # Main application window
│   │   ├── CarsPanel.java                  # Car selection interface
│   │   ├── CalculatePanel.java             # Loan input form
│   │   ├── LoanSummaryDialog.java          # Results popup
│   │   ├── AmortizationScheduleFrame.java  # Payment schedule table
│   │   └── ComparePanel.java               # Scenario comparison
│   │
│   ├── views/admin/                        # ADMIN PRESENTATION LAYER (NEW)
│   │   ├── AdminMainFrame.java             # Admin main window with sidebar
│   │   ├── DashboardPanel.java             # Admin dashboard with stats
│   │   ├── CustomerManagementPanel.java    # Customer CRUD panel
│   │   ├── CarInventoryPanel.java          # Car inventory management
│   │   ├── LoanManagementPanel.java        # Loan management panel
│   │   ├── PaymentTrackingPanel.java       # Payment tracking panel
│   │   ├── ReportsPanel.java               # Reports and analytics
│   │   ├── SettingsPanel.java              # System settings panel
│   │   ├── CustomerFormDialog.java         # Customer add/edit dialog
│   │   ├── CarFormDialog.java              # Car add/edit dialog
│   │   ├── LoanFormDialog.java             # Loan create/edit dialog
│   │   ├── PaymentRecordDialog.java        # Record payment dialog
│   │   ├── LoanDetailsDialog.java          # View loan details dialog
│   │   └── AmortizationScheduleDialog.java # View amortization dialog
│   │
│   └── utils/                              # UTILITY LAYER
│       ├── FormatUtils.java                # Number/currency formatting
│       ├── ValidationUtils.java            # Input validation
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

## 4. Database Architecture

### 4.1 Database Overview

The system uses **H2 Embedded Database** for persistent data storage, enabling:
- **Zero Configuration** - No database server installation required
- **Portable** - Database file travels with user profile
- **Auto-Initialization** - Schema created automatically on first run
- Customer relationship management
- Loan lifecycle tracking
- Payment history and reporting
- Admin configuration persistence

#### Key Benefits of H2 Embedded Database:
| Feature | Benefit |
|---------|--------|
| No Server Required | Application runs standalone |
| Single JAR (~2.5MB) | Minimal footprint |
| Auto-Create Schema | No manual database setup |
| MySQL Compatibility Mode | Standard SQL syntax |
| File-Based Storage | Data persists in user directory |

### 4.2 Database Schema

```
Database: car_loan_amortization_db
```

#### Entity Relationship Diagram

```
┌─────────────────────┐         ┌─────────────────────┐
│     customers       │         │        cars         │
├─────────────────────┤         ├─────────────────────┤
│ PK customer_id      │         │ PK car_id           │
│    first_name       │         │    make             │
│    last_name        │         │    model            │
│    email            │         │    year             │
│    phone            │         │    category         │
│    address          │         │    color            │
│    date_of_birth    │         │    mpg              │
│    created_at       │         │    price            │
│    updated_at       │         │    image_path       │
└─────────┬───────────┘         │    status (ENUM)    │
          │                     │    created_at       │
          │                     │    updated_at       │
          │                     └─────────┬───────────┘
          │                               │
          │         ┌─────────────────────┘
          │         │
          ▼         ▼
┌─────────────────────────────────────────────────────┐
│                       loans                          │
├─────────────────────────────────────────────────────┤
│ PK loan_id                                          │
│ FK customer_id → customers.customer_id              │
│ FK car_id → cars.car_id                             │
│    car_price, sales_tax_rate, registration_fee     │
│    down_payment, trade_in_value                    │
│    annual_interest_rate, loan_term_years           │
│    compounding_frequency, penalty_rate             │
│    extra_payment_per_month, total_amount_financed  │
│    monthly_payment, total_interest                 │
│    total_cost, status (ENUM), start_date           │
│    created_at, updated_at                          │
└─────────────────┬───────────────────────────────────┘
                  │
        ┌─────────┴─────────┐
        │                   │
        ▼                   ▼
┌─────────────────┐  ┌─────────────────┐
│amortization_rows│  │    payments     │
├─────────────────┤  ├─────────────────┤
│ PK row_id       │  │ PK payment_id   │
│ FK loan_id      │  │ FK loan_id      │
│    payment_num  │  │    amount       │
│    payment      │  │    principal    │
│    principal    │  │    interest     │
│    interest     │  │    penalty      │
│    penalty      │  │    payment_date │
│    balance      │  │    payment_type │
│    total_paid   │  │    notes        │
└─────────────────┘  │    created_at   │
                     └─────────────────┘

┌─────────────────────────────────────────────────────┐
│                   admin_settings                     │
├─────────────────────────────────────────────────────┤
│ PK setting_key (VARCHAR)                            │
│    setting_value                                    │
│    description                                      │
│    updated_at                                       │
└─────────────────────────────────────────────────────┘
```

### 4.3 Table Definitions

#### `customers` Table
| Column | Type | Description |
|--------|------|-------------|
| `customer_id` | INT (PK, AUTO_INCREMENT) | Unique customer identifier |
| `first_name` | VARCHAR(100) | Customer's first name |
| `last_name` | VARCHAR(100) | Customer's last name |
| `email` | VARCHAR(255) UNIQUE | Contact email |
| `phone` | VARCHAR(20) | Contact phone |
| `address` | TEXT | Full address |
| `date_of_birth` | DATE | Birth date |
| `created_at` | TIMESTAMP | Record creation time |
| `updated_at` | TIMESTAMP | Last update time |

#### `cars` Table
| Column | Type | Description |
|--------|------|-------------|
| `car_id` | INT (PK, AUTO_INCREMENT) | Unique car identifier |
| `make` | VARCHAR(100) | Manufacturer (e.g., Toyota) |
| `model` | VARCHAR(100) | Model name (e.g., Camry) |
| `year` | INT | Model year |
| `category` | VARCHAR(50) | Category (Sedan, SUV, etc.) |
| `color` | VARCHAR(50) | Exterior color |
| `mpg` | INT | Miles per gallon |
| `price` | DECIMAL(12,2) | Vehicle price |
| `image_path` | VARCHAR(500) | Path to car image |
| `status` | ENUM('AVAILABLE','SOLD','RESERVED') | Inventory status |

#### `loans` Table
| Column | Type | Description |
|--------|------|-------------|
| `loan_id` | INT (PK, AUTO_INCREMENT) | Unique loan identifier |
| `customer_id` | INT (FK) | Reference to customer |
| `car_id` | INT (FK) | Reference to car |
| `car_price` | DECIMAL(12,2) | Price at time of loan |
| `sales_tax_rate` | DECIMAL(5,2) | Applied tax rate |
| `registration_fee` | DECIMAL(10,2) | Registration fees |
| `down_payment` | DECIMAL(12,2) | Down payment amount |
| `trade_in_value` | DECIMAL(12,2) | Trade-in credit |
| `annual_interest_rate` | DECIMAL(5,2) | APR percentage |
| `loan_term_years` | INT | Loan duration in years |
| `compounding_frequency` | VARCHAR(20) | Monthly/Quarterly/Annually |
| `penalty_rate` | DECIMAL(5,2) | Late payment penalty rate |
| `extra_payment_per_month` | DECIMAL(10,2) | Additional monthly payment |
| `total_amount_financed` | DECIMAL(12,2) | Principal amount |
| `monthly_payment` | DECIMAL(10,2) | Fixed monthly payment |
| `total_interest` | DECIMAL(12,2) | Total interest over term |
| `total_cost` | DECIMAL(12,2) | Total of all payments |
| `status` | ENUM('ACTIVE','PAID_OFF','DEFAULTED','CANCELLED') | Loan status |
| `start_date` | DATE | Loan start date |

#### `payments` Table
| Column | Type | Description |
|--------|------|-------------|
| `payment_id` | INT (PK, AUTO_INCREMENT) | Unique payment identifier |
| `loan_id` | INT (FK) | Reference to loan |
| `amount` | DECIMAL(10,2) | Total payment amount |
| `principal_portion` | DECIMAL(10,2) | Principal paid |
| `interest_portion` | DECIMAL(10,2) | Interest paid |
| `penalty_portion` | DECIMAL(10,2) | Penalty paid |
| `payment_date` | DATE | Date of payment |
| `payment_type` | ENUM('REGULAR','EXTRA','PENALTY','FINAL') | Payment type |
| `notes` | TEXT | Optional notes |

#### `admin_settings` Table
| Column | Type | Description |
|--------|------|-------------|
| `setting_key` | VARCHAR(100) (PK) | Setting identifier |
| `setting_value` | TEXT | Setting value |
| `description` | TEXT | Setting description |
| `updated_at` | TIMESTAMP | Last update time |

### 4.4 Database Connection Configuration

```java
// DatabaseConfig.java - H2 Embedded Database
public class DatabaseConfig {
    // Database stored in user home directory: ~/.vismera/data/carloan_db
    private static final String DB_URL = "jdbc:h2:file:" + dbPath + ";MODE=MySQL;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    // Auto-initializes schema on first run
    static {
        Class.forName("org.h2.Driver");
        initializeSchema(); // Creates tables if not exist
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
```

#### Database File Location
- **Windows**: `C:\Users\{username}\.vismera\data\carloan_db.mv.db`
- **macOS/Linux**: `~/.vismera/data/carloan_db.mv.db`

---

## 5. Data Access Layer (DAO)

### 5.1 DAO Pattern Overview

The Data Access Object (DAO) pattern separates data persistence logic from business logic:

```
┌──────────────┐     ┌─────────┐     ┌──────────────┐
│  Controller  │────▶│   DAO   │────▶│   H2 File    │
└──────────────┘     └─────────┘     └──────────────┘
     (Logic)         (Persistence)      (Embedded)
```

### 5.2 DAO Classes

| DAO Class | Entity | Key Operations |
|-----------|--------|----------------|
| `CustomerDAO` | Customer | CRUD + findByEmail + search |
| `CarDAO` | Car | CRUD + findByStatus + search + updateStatus |
| `LoanDAO` | Loan | CRUD + findByCustomer + findByStatus |
| `AmortizationRowDAO` | AmortizationRow | CRUD + findByLoan + deleteByLoan |
| `PaymentDAO` | Payment | CRUD + findByLoan + findByDateRange |
| `AdminSettingDAO` | AdminSetting | CRUD + getByKey + updateValue |

### 5.3 Common DAO Methods

```java
// Standard CRUD Pattern
public interface GenericDAO<T> {
    int create(T entity);         // Returns generated ID
    T findById(int id);           // Returns entity or null
    List<T> findAll();            // Returns all entities
    boolean update(T entity);     // Returns success status
    boolean delete(int id);       // Returns success status
}
```

### 5.4 Example: CustomerDAO

```java
public class CustomerDAO {
    // Create - Insert new customer
    public int create(Customer customer) {
        String sql = "INSERT INTO customers (first_name, last_name, email, phone, address, date_of_birth) VALUES (?, ?, ?, ?, ?, ?)";
        // Execute and return generated ID
    }
    
    // Read - Find by ID
    public Customer findById(int id) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        // Execute and map ResultSet to Customer
    }
    
    // Update - Modify existing customer
    public boolean update(Customer customer) {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, ... WHERE customer_id = ?";
        // Execute and return affected rows > 0
    }
    
    // Delete - Remove customer
    public boolean delete(int id) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        // Execute and return affected rows > 0
    }
    
    // Search - Find by name or email
    public List<Customer> search(String query) {
        String sql = "SELECT * FROM customers WHERE first_name LIKE ? OR last_name LIKE ? OR email LIKE ?";
        // Execute with wildcards
    }
}
```

---

## 6. Admin System

### 6.1 Admin Interface Overview

The Admin System provides comprehensive management capabilities:

```
┌────────────────────────────────────────────────────────────────────┐
│                        AdminMainFrame                               │
├──────────────┬─────────────────────────────────────────────────────┤
│              │                                                      │
│   SIDEBAR    │              CONTENT AREA (CardLayout)              │
│              │                                                      │
│  ┌────────┐  │  ┌──────────────────────────────────────────────┐  │
│  │Dashboard│  │  │                                              │  │
│  ├────────┤  │  │    DashboardPanel                            │  │
│  │Customers│  │  │    CustomerManagementPanel                  │  │
│  ├────────┤  │  │    CarInventoryPanel                         │  │
│  │  Cars  │  │  │    LoanManagementPanel                       │  │
│  ├────────┤  │  │    PaymentTrackingPanel                      │  │
│  │ Loans  │  │  │    ReportsPanel                              │  │
│  ├────────┤  │  │    SettingsPanel                             │  │
│  │Payments│  │  │                                              │  │
│  ├────────┤  │  └──────────────────────────────────────────────┘  │
│  │Reports │  │                                                      │
│  ├────────┤  │                                                      │
│  │Settings│  │                                                      │
│  └────────┘  │                                                      │
└──────────────┴─────────────────────────────────────────────────────┘
```

### 6.2 Admin Panels

| Panel | Purpose | Features |
|-------|---------|----------|
| **DashboardPanel** | System overview | Stats cards, charts, recent activity |
| **CustomerManagementPanel** | Customer CRUD | Table, search, add/edit/delete dialogs |
| **CarInventoryPanel** | Car inventory | Table, status filters, stock management |
| **LoanManagementPanel** | Loan management | Active loans, status tracking, details view |
| **PaymentTrackingPanel** | Payment processing | Record payments, history, overdue tracking |
| **ReportsPanel** | Analytics | Revenue, loan performance, export options |
| **SettingsPanel** | Configuration | Interest rates, fees, penalties |

### 6.3 Admin Dialogs

| Dialog | Purpose | Fields |
|--------|---------|--------|
| `CustomerFormDialog` | Add/Edit customer | Name, email, phone, address, DOB |
| `CarFormDialog` | Add/Edit car | Make, model, year, category, price, status |
| `LoanFormDialog` | Create loan | Customer, car, rates, terms, payments |
| `PaymentRecordDialog` | Record payment | Amount, date, type, notes |
| `LoanDetailsDialog` | View loan details | Summary, customer info, car info, history |
| `AmortizationScheduleDialog` | View schedule | Full payment-by-payment breakdown |

### 6.4 Admin Controllers

```java
// Controller Hierarchy
┌───────────────────────┐
│  CustomerController   │ ─── Manages customers via CustomerDAO
├───────────────────────┤
│    CarController      │ ─── Manages cars via CarDAO
├───────────────────────┤
│  LoanControllerDB     │ ─── Manages loans via LoanDAO
├───────────────────────┤
│  PaymentController    │ ─── Manages payments via PaymentDAO
├───────────────────────┤
│  SettingsController   │ ─── Manages settings via AdminSettingDAO
├───────────────────────┤
│   ReportController    │ ─── Aggregates data for analytics
└───────────────────────┘
```

### 6.5 ReportController Analytics

```java
// Available Reports
public class ReportController {
    // Revenue metrics
    BigDecimal getTotalRevenue();
    BigDecimal getRevenueInPeriod(LocalDate start, LocalDate end);
    
    // Loan metrics
    int getTotalActiveLoans();
    int getLoansByStatus(String status);
    BigDecimal getAverageLoanAmount();
    
    // Payment metrics
    int getTotalPaymentsReceived();
    BigDecimal getTotalInterestEarned();
    
    // Customer metrics
    int getTotalCustomers();
    List<Customer> getTopCustomers(int limit);
    
    // Performance metrics
    BigDecimal getDefaultRate();
    BigDecimal getOnTimePaymentRate();
}
```

---

## 7. Key Terms and Definitions

### 7.1 Financial Terms

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

### 7.2 System-Specific Terms

| Term | Definition | Location in System |
|------|------------|-------------------|
| **Compounding Frequency** | How often interest is calculated and added | `LoanCalculation.compoundingFrequency` |
| **Missed Payments** | Simulated number of payments skipped | `LoanCalculation.missedPayments` |
| **Extra Payment** | Additional amount paid above the minimum | `LoanCalculation.extraPaymentPerMonth` |
| **Amount Financed** | The actual loan amount after deductions | `calculateAmountFinanced()` |
| **Total Vehicle Cost** | Car price + taxes + fees | `calculateTotalCost()` |
| **Best Deal** | Scenario with lowest total cost | `ComparisonController.findBestDeal()` |

### 7.3 Technical Terms

| Term | Definition | Application |
|------|------------|-------------|
| **MVC Pattern** | Model-View-Controller architectural pattern | Separates data, UI, and logic |
| **DAO Pattern** | Data Access Object - separates data persistence from logic | DAOs handle database operations |
| **Singleton Pattern** | Design pattern ensuring only one instance exists | Controllers use this pattern |
| **JDBC** | Java Database Connectivity API | Connects Java to H2 database |
| **CardLayout** | Swing layout manager for switching between panels | Navigation in MainFrame |
| **JDialog** | Modal popup window | LoanSummaryDialog |
| **JTable** | Swing component for tabular data | Amortization schedule display |
| **TableCellRenderer** | Customizes cell appearance in JTable | Color-coding columns |

### 7.4 Database Terms

| Term | Definition | Application |
|------|------------|-------------|
| **CRUD** | Create, Read, Update, Delete operations | Standard database operations |
| **Primary Key (PK)** | Unique identifier for each record | `customer_id`, `car_id`, `loan_id` |
| **Foreign Key (FK)** | Reference to another table's primary key | `loans.customer_id → customers.customer_id` |
| **ENUM** | Predefined set of values | Loan status: ACTIVE, PAID_OFF, etc. |
| **INDEX** | Database optimization for faster queries | Indexed on frequently searched columns |
| **Transaction** | Group of operations that succeed or fail together | Used in payment processing |

### 7.5 Calculation Factors

| Factor | Impact | Range |
|--------|--------|-------|
| **Interest Rate** | Higher rate = more interest paid | 0% - 50% |
| **Loan Term** | Longer term = lower payments but more total interest | 1 - 10 years |
| **Down Payment** | Higher down payment = lower loan amount | $0 - Car Price |
| **Compounding** | More frequent = slightly higher effective rate | Monthly → Annually |
| **Penalty Rate** | Higher penalty = more cost for missed payments | 0% - 10% |
| **Extra Payments** | Reduces total interest and loan duration | $0+ |

---

## 8. How Each Function Works

### 8.1 Model Functions

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

#### `CSVExporter.exportAmortizationSchedule(...)`
```java
/**
 * Exports amortization schedule to CSV file.
 * 
 * Output Format:
 * Payment #,Payment,Principal,Interest,Penalty,Balance,Total Paid
 * 1,$773.31,$559.23,$214.08,$0.00,$38940.77,$773.31
 * 2,$773.31,$562.26,$211.05,$0.00,$38378.51,$1546.62
 * ...
 * 
 * @param entries List of AmortizationEntry
 * @param filePath Destination file path
 * @return true if successful
 */
public static boolean exportAmortizationSchedule(List<AmortizationEntry> entries, 
                                                  String filePath) {
    try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
        writer.println("Payment #,Payment,Principal,Interest,Penalty,Balance,Total Paid");
        
        for (AmortizationEntry entry : entries) {
            writer.printf("%d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%n",
                entry.getPaymentNumber(),
                entry.getPayment(),
                // ... remaining fields
            );
        }
        return true;
    } catch (IOException e) {
        return false;
    }
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
| Export Type | Format | Extension |
|-------------|--------|-----------|
| Amortization Schedule | CSV | `.csv` |

---

*Documentation prepared for Vismerá Inc. Car Loan Amortization System with Database*
*Version 2.0 | Database Integrated | December 2025*
