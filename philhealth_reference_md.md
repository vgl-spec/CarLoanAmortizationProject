# PHIL-HEALTH PREMIUM DEDUCTION SYSTEM

**A Project Presented to the**  
**Faculty of the College of Computer Studies**  
**Quezon City University, Quezon City**

**In Partial Fulfillment**  
**of the Requirements for the Degree**  
**Bachelor of Science in Information Technology**

**Submitted by:**
- Baloloy, Victoria T.
- De Guzman, Arman
- Mendez, Joshua
- Monton, Jana Trexie T.

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
   - [Recursion](#recursion)
   - [Dynamic Programming](#dynamic-programming)
   - [Validation Algorithms](#validation-algorithms)
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

**Employee PhilHealth Premium Deduction System**

---

## Project Description

The Employee PhilHealth Premium Deduction System is designed to automate the computation of the PhilHealth contribution, deducted by the government-mandated premium rate which is 5% for the year 2025, in which is deducted from the Basic Monthly Salary of Employees for accurate deductions, faster processing, and clear transparency.

---

## Project Mechanics Rule/Instruction

### System Setup

- The application must be installed on the computer.
- Update salary details per payroll to ensure accurate computation.

### Computation Rules

- PhilHealth premiums are computed based on the latest official contribution table released by PhilHealth.
- The system automatically applies the salary bracket to compute the monthly basic salary and determine the correct premium.
- Contributions are split between employee share and employer share.

### User Instructions

- **Input:** Salary and Select: Payroll Period Dates (From and To)
- **Process:** The system calculates salary and number of days based on the payroll period, if days is equivalent to a month, the system will automatically compute the monthly basic salary and calculates the PhilHealth deduction.
- **Output:** A deduction report can be exported and printed showing the total amount, employer share, and employee share.
- **Error Handling:** If salary data is missing or invalid, the system prompts the user to correct it before proceeding.

### Limitations

- The system is designed only for PhilHealth deductions.
- Can only be used for formal sector (employed)

---

## Kind of Applied Algorithm

### Recursion

- **Application:** Applied in iterative deduction computations, especially when handling progressive salary brackets or recursive validation of contribution rules.
- **Example:** A recursive function that checks salary ranges until the correct bracket is found, then computes the deduction.
- **Benefit:** Simplifies complex, repetitive calculations into elegant, maintainable code.

### Dynamic Programming

- **Application:** Optimizes repeated calculations of contribution brackets by storing intermediate results.
- **Example:** Memoization of salary-to-deduction mappings to avoid recalculating for employees with identical salaries.
- **Benefit:** Improves performance and reduces redundant computations.

### Validation Algorithms

- **Application:** Input validation for salary entries, ensuring only valid numeric ranges are processed.
- **Example:** Rule-based validation combined with recursive error-checking.
- **Benefit:** Prevents miscalculations and ensures data integrity.

---

## Procedures and Computational Arithmetic

### Matrix Appendix

| Monthly Basic Salary Range (₱) | Premium Rate (%) 2025 | Employee Share (₱) | Employer Share (₱) | Contributions (₱) |
|--------------------------------|----------------------|-------------------|-------------------|------------------|
| ≤10,000                        | 5%                   | 250               | 250               | 500              |
| 10,000 - 100,000               | 5%                   | 250 - 2,500       | 250 - 2,500       | 500 - 5,000      |
| ≥100,000                       | 5%                   | 2,500             | 2,500             | 5,000            |

**Formula:**

```
Total = Salary × 0.05
EmployeeShare = Total / 2
EmployerShare = Total / 2
```

### Bullet Procedures

**Step 1: Input Employee Data**
- User salary and PayrollPeriod (From and To).
- Check if the salary is numeric and above zero.
- Calculate the PayrollPeriod to Number of Days

**Step 2: Identify Salary Bracket**
- The system checks the PayrollPeriod and the salary bracket
- Recursion may be used if ranges are nested.

**Step 3: Compute Premium**
- The function computes the PhilHealth premium based on the 2025 rate (5%).
- The system applies the formula:
  - Total = Salary × 0.05

**Step 4: Split Contribution**
- This function divides the premium equally between employee and employer.

**Step 5: Generate Deduction Report**
- The system formats and displays the results in a report form.

**Step 6: Error Handling**
- The system blocks invalid inputs (non-numeric, negative, missing fields).
- If the contribution table is outdated, an admin warning appears.

**Step 7: Audit & Compliance**
- Automatically generates monthly PhilHealth deduction reports.

---

## Logic Formulation (Algorithm)

### Program Flowchart

*[Flowchart image would be placed here]*

### Pseudocode of the Control Structure

```
PROGRAM EmployeePhilHealthPremiumDeduction

CLASS Form1
    VARIABLES:
        contributions ← list of EmployeeContribution
        payrollRows ← list of PayrollRow
    
    CONSTANT PH_RATE ← 0.05
    CONSTANT PH_FLOOR ← 10000
    CONSTANT PH_CEILING ← 100000
    CONSTANT PH_MIN_TOTAL ← 500
    CONSTANT PH_MAX_TOTAL ← 5000
    
    CLASS PayrollRow
        salary
        payrollPeriod
        daysCount
        startDate
        endDate
    
    CLASS EmployeeContribution
        salary
        payrollPeriod
        monthYear
        monthlyBasicSalary
        premiumRate
        interestRate
        employeeShare
        employerShare
        totalContribution
        status
        dueDate
        computationTrace
        daysCount
        monthsCount
        isOngoing
    
    METHOD computeContribution(mbs, interestPct)
        baseSalary ← clamp(mbs between PH_FLOOR and PH_CEILING)
        rawTotal ← baseSalary × PH_RATE
        
        IF baseSalary ≤ PH_FLOOR THEN rawTotal ← PH_MIN_TOTAL
        IF baseSalary ≥ PH_CEILING THEN rawTotal ← PH_MAX_TOTAL
        
        interestAmt ← rawTotal × (interestPct ÷ 100)
        finalTotal ← rawTotal + interestAmt
        empShare ← finalTotal ÷ 2
        erShare ← finalTotal ÷ 2
        
        RETURN (empShare, erShare, finalTotal, PH_RATE, traceString)
    
    METHOD recomputeAllContributions()
        FOR each contribution c IN contributions
            IF c is null OR c.isOngoing THEN skip
            result ← computeContribution(c.monthlyBasicSalary, c.interestRate)
            update c with result values
        END FOR
    
    METHOD getYearMonthKeys(startDate, endDate)
        keys ← empty list
        cursor ← first day of startDate's month
        lastMonth ← first day of endDate's month
        
        WHILE cursor ≤ lastMonth
            add (year × 100 + month) to keys
            cursor ← cursor + 1 month
        END WHILE
        
        RETURN keys
    
    METHOD addPayrollAndContribution(inputSalary, fromDate, toDate)
        VALIDATE salary and date range
        daysCount ← (toDate - fromDate) + 1
        payrollPeriodDisplay ← formatted string of fromDate-toDate
        monthYearDisplay ← formatted string of month/year range
        
        CHECK for duplicate months in payrollRows
        
        newEmployeeId ← generate unique ID
        ADD new PayrollRow with salary, period, days, employeeID, startDate, endDate
        
        monthlyBasicSalary ← full month or prorated based on daysCount
        status ← "Pending"
        dueDate ← toDate
        
        result ← computeContribution(monthlyBasicSalary, 0)
        CREATE new EmployeeContribution with result values
        ADD to contributions
        
        recomputeAllContributions()
    
    METHOD deleteContribution(employeeID)
        REMOVE contribution with employeeID
        REMOVE linked payrollRows
        recomputeAllContributions()
    
    METHOD deletePayroll(employeeID)
        REMOVE payrollRow with employeeID
        REMOVE linked contribution
        recomputeAllContributions()
    
    METHOD updateContributionStatus(contribution)
        IF status = "OverDue" THEN interestRate ← 30 ELSE 0
        
        IF not ongoing THEN
            result ← computeContribution(contribution.monthlyBasicSalary, contribution.interestRate)
            update contribution with result values
        END IF
        
        recomputeAllContributions()
    
    METHOD updateSummary()
        fullRows ← contributions where not ongoing
        total ← count of fullRows
        pendingCount ← count with status "Pending"
        overdueCount ← count with status "OverDue"
        paidCount ← count with status "Paid"
        
        totalContributionsSum ← sum of TotalContribution
        totalEmployeeShares ← sum of EmployeeShare
        totalEmployerShares ← sum of EmployerShare
        grandTotal ← totalContributionsSum
        totalPaid ← sum of contributions with status "Paid"
        
        RETURN summary object with counts and totals
    
    METHOD exportReport()
        IF contributions empty THEN RETURN "No records"
        
        CREATE document
        ADD title "Employee Philhealth Premium Deduction Report"
        ADD personal info (name, PHIC number)
        ADD salary table from payrollRows
        ADD contribution table from contributions
        ADD totals (Employee Share, Employer Share, Grand Total)
        ADD benefits section
        SAVE document
        
        RETURN "Export successful"

END CLASS
```

---

## Defining the Constructive/Destructive Variables

### Declaration and Data Types

| Variable Name | Description | Type | Constructive/Destructive |
|--------------|-------------|------|--------------------------|
| `employeeID` | Unique identifier of employee | string | Constructive |
| `employeeName` | Full name of employee | string | Constructive |
| `salary` | Monthly basic salary | double | Destructive (input may change) |
| `premiumRate` | PhilHealth fixed rate (5%) | final double | Constructive (constant) |
| `totalContribution` | Computed contribution (salary × rate) | double | Destructive |
| `employeeShare` | 50% of totalContribution | double | Destructive |
| `employerShare` | 50% of totalContribution | double | Destructive |
| `salaryBracket` | Bracket index (1, 2, or 3) | int | Destructive |
| `isValid` | Validation flag for salary | boolean | Destructive |
| `errorMessage` | Validation or error text | String | Destructive |
| `txtEmployeeID` | GUI input field for employee ID | JTextField | Constructive |
| `txtEmployeeName` | GUI input field for employee name | JTextField | Constructive |
| `txtSalary` | GUI input field for salary | JTextField | Constructive |
| `lblTotal` | Output label for total contribution | JLabel | Constructive |
| `lblErShare` | Output label for employer share | JLabel | Constructive |
| `lblBracket` | Output label for bracket number | JLabel | Constructive |

---

## Formulation of Data Structure (Syntax and Semantics)

### Functionalities

| Function Name | Description |
|--------------|-------------|
| `inputValidation()` | Ensures salary input is valid, numeric, and greater than zero. |
| `determineBracket(double salary)` | Determines if the employee falls under Bracket 1, 2, or 3. |
| `computePhilHealth(double salary)` | Computes total contribution, employee share, employer share. |
| `generateReport()` | Generates structured output and display result in labels. |

### Parameters

| Function | Parameters | Purpose |
|----------|-----------|---------|
| `inputValidation(String salaryText)` | salaryText: String | Validates numeric input. |
| `determineBracket(double salary)` | salary: double | Identifies salary bracket. |
| `computePhilHealth(double salary)` | salary: double | Computes contribution values. |
| `generateReport(String id, String name, double salary)` | multiple | Displays output summary. |

### Arrays

```java
double[][] SalaryBracket = {
    {0, 10000, 500},        // Bracket 1
    {10001, 100000, 5000},  // Bracket 2
    {100001, 999999, 5000}  // Bracket 3 (capped)
};
```

**Matrix Meaning:**

| Row | Range | Max Contribution |
|-----|-------|------------------|
| 0 | ≤ 10,000 | ₱500 |
| 1 | 10,001-100,000 | ₱500-₱5,000 |
| 2 | ≥ 100,000 | Capped at ₱5,000 |

---

## Iterative Simulation (Logical Algorithm)

**Sample Simulation for 3 Employees**

| Employee | Salary | Total = 5% | Employee Share | Employer Share | Bracket |
|----------|--------|-----------|----------------|----------------|---------|
| A | ₱9,000 | ₱450 | ₱225 | ₱225 | 1 |
| B | ₱25,000 | ₱1,250 | ₱625 | ₱625 | 2 |
| C | ₱120,000 | ₱6,000 → **capped to ₱5,000** | ₱2,500 | ₱2,500 | 3 |

---

## Screen Shot (UI Frame)

*[UI Screenshot would be placed here]*

| No. | Object (Variable) | Function |
|-----|------------------|----------|
| 1. | Salary | Input Salary<br>If Salary is a String or Empty<br>Then MsgBox("Input Invalid Salary") |
| 2. | toDate | Select the Starting Date<br>If toDate < fromDate Then MsgBox("Invalid Date") |
| 3. | fromDate | Select the End Date |
| 4. | btnAdd | Add a Record to Display to the dgvPayroll |
| 5. | btnDelete | Delete a Record from the dgvPayroll |
| 6. | Status | Select "Paid" or "OverDue"<br>If "Paid" Then Calculate and Display in Total Paid<br>If "OverDue" Then Add Interest by 3% of the TotalContribution to the Total Contribution itself and Display<br>Else Display "Pending". |
| 7. | Export | Export a printable copy of the Report (.docx) |

---

## Program Listing

### Code With line Numbers

```java
1   import javax.swing.*;
2   import java.awt.*;
3   import java.awt.event.*;
4   
5   public class PhilHealthSwing extends JFrame {
6   
7       private JTextField txtEmployeeID;
8       private JTextField txtEmployeeName;
9       private JTextField txtSalary;
10  
11      private JLabel lblTotal;
12      private JLabel lblEmpShare;
13      private JLabel lblErShare;
14      private JLabel lblBracket;
15  
16      private static final double PREMIUM_RATE = 0.05;
17  
18      public PhilHealthSwing() {
19          setTitle("PhilHealth Contribution Calculator");
20          setSize(420, 420);
21          setLocationRelativeTo(null);
22          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
23          setLayout(new BorderLayout());
24  
25          JPanel panelInput = new JPanel();
26          panelInput.setBorder(BorderFactory.createTitledBorder("Employee Information"));
27          panelInput.setLayout(new GridLayout(5, 2, 10, 10));
28  
29          panelInput.add(new JLabel("Employee ID:"));
30          txtEmployeeID = new JTextField();
31          panelInput.add(txtEmployeeID);
32  
33          panelInput.add(new JLabel("Employee Name:"));
34          txtEmployeeName = new JTextField();
35          panelInput.add(txtEmployeeName);
36  
37          panelInput.add(new JLabel("Monthly Basic Salary:"));
38          txtSalary = new JTextField();
39          panelInput.add(txtSalary);
40  
41          JButton btnCalculate = new JButton("Calculate");
42          JButton btnClear = new JButton("Clear");
43  
44          panelInput.add(btnCalculate);
45          panelInput.add(btnClear);
46  
47          add(panelInput, BorderLayout.NORTH);
48  
49          JPanel panelOutput = new JPanel();
50          panelOutput.setBorder(BorderFactory.createTitledBorder("PhilHealth Contribution Summary"));
51          panelOutput.setLayout(new GridLayout(5, 2, 10, 10));
52  
53          panelOutput.add(new JLabel("Salary Bracket:"));
54          lblBracket = new JLabel("-");
55          panelOutput.add(lblBracket);
56  
57          panelOutput.add(new JLabel("Total Contribution (5%):"));
58          lblTotal = new JLabel("-");
59          panelOutput.add(lblTotal);
60  
61          panelOutput.add(new JLabel("Employee Share:"));
62          lblEmpShare = new JLabel("-");
63          panelOutput.add(lblEmpShare);
64  
65          panelOutput.add(new JLabel("Employer Share:"));
66          lblErShare = new JLabel("-");
67          panelOutput.add(lblErShare);
68  
69          add(panelOutput, BorderLayout.CENTER);
70  
71          btnCalculate.addActionListener(e -> calculate());
72          btnClear.addActionListener(e -> clearFields());
73      }
74  
75      private void calculate() {
76          try {
77              double salary = Double.parseDouble(txtSalary.getText());
78  
79              if (salary <= 0) {
80                  JOptionPane.showMessageDialog(this, "Salary must be greater than 0.", 
81                                                "Invalid Input", JOptionPane.WARNING_MESSAGE);
82                  return;
83              }
84  
85              int bracket = determineBracket(salary);
86              double total = salary * PREMIUM_RATE;
87              double empShare = total / 2;
88              double erShare = total / 2;
89  
90              if (total > 5000) {
91                  total = 5000;
92                  empShare = 2500;
93                  erShare = 2500;
94              }
95  
96              lblBracket.setText("BRACKET " + bracket);
97              lblTotal.setText("₱" + String.format("%.2f", total));
98              lblEmpShare.setText("₱" + String.format("%.2f", empShare));
99              lblErShare.setText("₱" + String.format("%.2f", erShare));
100 
101         } catch (NumberFormatException ex) {
102             JOptionPane.showMessageDialog(this, "Please enter a valid numeric salary.", 
103                                           "Error", JOptionPane.ERROR_MESSAGE);
104         }
105     }
106 
107     private int determineBracket(double salary) {
108         if (salary <= 10000) return 1;
109         else if (salary <= 100000) return 2;
110         else return 3;
111     }
112 
113     private void clearFields() {
114         txtEmployeeID.setText("");
115         txtEmployeeName.setText("");
116         txtSalary.setText("");
117 
118         lblBracket.setText("-");
119         lblTotal.setText("-");
120         lblEmpShare.setText("-");
121         lblErShare.setText("-");
122     }
123 
124     public static void main(String[] args) {
125         SwingUtilities.invokeLater(() -> {
126             new PhilHealthSwing().setVisible(true);
127         });
128     }
129 }
```

---

## Appendix

### References

Cormen, T. H., Leiserson, C. E., Rivest, R. L., & Stein, C. (2022). *Introduction to algorithms* (4th ed.). MIT Press.

Gamma, E., Helm, R., Johnson, R., & Vlissides, R. (2021). *Design patterns: Elements of reusable object-oriented software* (Reprint ed.). Addison-Wesley Professional.

IEEE Standards Association. (2022). *IEEE Standard 1012-2022: Standard for system, software, and hardware verification and validation*. IEEE.

ISO/IEC/IEEE. (2021). *ISO/IEC/IEEE 24765:2021—Systems and software engineering — Vocabulary*. International Organization for Standardization.

Nielsen Norman Group. (2020). *10 usability heuristics for user interface design*. https://www.nngroup.com

Norman, D. A. (2013/2023). *The design of everyday things* (Updated ed.). Basic Books.

Oracle. (2020). *Java Platform, Standard Edition 15 documentation*. https://docs.oracle.com/javase

Oracle. (2020). *Java Swing tutorial: Building graphical user interfaces*. https://docs.oracle.com/javase/tutorial/uiswing

Philippine Health Insurance Corporation. (2021). *PhilHealth Circular 2021-0009: Adjusted premium contribution schedule in accordance with the Universal Health Care Act*. https://www.philhealth.gov.ph/circulars

Philippine Health Insurance Corporation. (2023). *PhilHealth premium contribution table*. https://www.philhealth.gov.ph

Philippine Health Insurance Corporation. (2024). *PhilHealth contribution schedule for 2024-2025*. https://www.philhealth.gov.ph

Republic of the Philippines. (2020). *Universal Health Care Act: Implementing rules and regulations (IRR)*. Official Gazette. https://www.officialgazette.gov.ph

Skiena, S. (2020). *The algorithm design manual* (3rd ed.). Springer.

Sommerville, I. (2020). *Software engineering* (11th ed.). Pearson.

Weiss, M. A. (2020). *Data structures and algorithm analysis in Java* (3rd ed., Updated printing). Pearson.

---

*End of Document*