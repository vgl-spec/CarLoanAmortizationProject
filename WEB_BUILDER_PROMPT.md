# AI Website Builder Prompt - Car Loan Amortization System

Copy the JSON prompt below and paste it into your AI website builder.

---

```json
{
  "project": {
    "name": "Vismerá Inc. Car Loan Amortization System",
    "description": "A web-based car loan amortization calculator with admin dashboard for managing cars, customers, loans, and payments. Built with classic HTML, CSS, JavaScript, and PHP with MySQL database.",
    "type": "web_application",
    "tech_stack": {
      "frontend": ["HTML5", "CSS3", "JavaScript", "Bootstrap 5"],
      "backend": ["PHP 8+"],
      "database": ["MySQL 8+"],
      "styling": "Modern responsive design with sidebar navigation"
    }
  },

  "database": {
    "name": "vismera_loans",
    "tables": [
      {
        "name": "users",
        "description": "Admin users for login",
        "columns": [
          {"name": "id", "type": "INT", "auto_increment": true, "primary_key": true},
          {"name": "username", "type": "VARCHAR(50)", "unique": true, "not_null": true},
          {"name": "password", "type": "VARCHAR(255)", "not_null": true, "note": "Use password_hash()"},
          {"name": "full_name", "type": "VARCHAR(100)", "not_null": true},
          {"name": "email", "type": "VARCHAR(100)"},
          {"name": "role", "type": "ENUM('admin','staff')", "default": "staff"},
          {"name": "created_at", "type": "TIMESTAMP", "default": "CURRENT_TIMESTAMP"}
        ]
      },
      {
        "name": "cars",
        "description": "Car inventory",
        "columns": [
          {"name": "id", "type": "INT", "auto_increment": true, "primary_key": true},
          {"name": "make", "type": "VARCHAR(50)", "not_null": true},
          {"name": "model", "type": "VARCHAR(50)", "not_null": true},
          {"name": "year", "type": "INT", "not_null": true},
          {"name": "price", "type": "DECIMAL(15,2)", "not_null": true},
          {"name": "category", "type": "VARCHAR(50)"},
          {"name": "color", "type": "VARCHAR(30)"},
          {"name": "mpg", "type": "INT"},
          {"name": "image_path", "type": "VARCHAR(255)"},
          {"name": "is_available", "type": "BOOLEAN", "default": true},
          {"name": "created_at", "type": "TIMESTAMP", "default": "CURRENT_TIMESTAMP"}
        ]
      },
      {
        "name": "customers",
        "description": "Customer records",
        "columns": [
          {"name": "id", "type": "INT", "auto_increment": true, "primary_key": true},
          {"name": "full_name", "type": "VARCHAR(100)", "not_null": true},
          {"name": "contact_number", "type": "VARCHAR(30)"},
          {"name": "email", "type": "VARCHAR(100)"},
          {"name": "address", "type": "TEXT"},
          {"name": "created_at", "type": "TIMESTAMP", "default": "CURRENT_TIMESTAMP"}
        ]
      },
      {
        "name": "loans",
        "description": "Loan records with all calculation parameters",
        "columns": [
          {"name": "id", "type": "INT", "auto_increment": true, "primary_key": true},
          {"name": "customer_id", "type": "INT", "foreign_key": "customers.id"},
          {"name": "car_id", "type": "INT", "foreign_key": "cars.id"},
          {"name": "principal", "type": "DECIMAL(15,2)", "not_null": true, "description": "Amount financed"},
          {"name": "apr", "type": "DECIMAL(5,2)", "not_null": true, "description": "Annual Percentage Rate"},
          {"name": "compounding", "type": "ENUM('monthly','quarterly','semi-annually','annually')", "default": "monthly"},
          {"name": "term_months", "type": "INT", "not_null": true},
          {"name": "start_date", "type": "DATE"},
          {"name": "penalty_rate", "type": "DECIMAL(5,2)", "default": "5.00"},
          {"name": "grace_period_days", "type": "INT", "default": 5},
          {"name": "down_payment", "type": "DECIMAL(15,2)", "default": "0.00"},
          {"name": "trade_in_value", "type": "DECIMAL(15,2)", "default": "0.00"},
          {"name": "sales_tax_rate", "type": "DECIMAL(5,2)", "default": "0.00"},
          {"name": "registration_fee", "type": "DECIMAL(15,2)", "default": "0.00"},
          {"name": "monthly_payment", "type": "DECIMAL(15,2)"},
          {"name": "total_interest", "type": "DECIMAL(15,2)"},
          {"name": "total_amount", "type": "DECIMAL(15,2)"},
          {"name": "status", "type": "ENUM('pending','active','closed','defaulted')", "default": "pending"},
          {"name": "created_at", "type": "TIMESTAMP", "default": "CURRENT_TIMESTAMP"}
        ]
      },
      {
        "name": "payments",
        "description": "Payment records",
        "columns": [
          {"name": "id", "type": "INT", "auto_increment": true, "primary_key": true},
          {"name": "loan_id", "type": "INT", "foreign_key": "loans.id"},
          {"name": "payment_date", "type": "DATE", "not_null": true},
          {"name": "amount", "type": "DECIMAL(15,2)", "not_null": true},
          {"name": "principal_applied", "type": "DECIMAL(15,2)"},
          {"name": "interest_applied", "type": "DECIMAL(15,2)"},
          {"name": "penalty_applied", "type": "DECIMAL(15,2)", "default": "0.00"},
          {"name": "payment_type", "type": "ENUM('regular','extra','penalty','partial')", "default": "regular"},
          {"name": "note", "type": "TEXT"},
          {"name": "recorded_by", "type": "VARCHAR(50)"},
          {"name": "created_at", "type": "TIMESTAMP", "default": "CURRENT_TIMESTAMP"}
        ]
      },
      {
        "name": "amortization_schedule",
        "description": "Pre-calculated amortization rows for each loan",
        "columns": [
          {"name": "id", "type": "INT", "auto_increment": true, "primary_key": true},
          {"name": "loan_id", "type": "INT", "foreign_key": "loans.id"},
          {"name": "period_number", "type": "INT", "not_null": true},
          {"name": "due_date", "type": "DATE"},
          {"name": "opening_balance", "type": "DECIMAL(15,2)"},
          {"name": "scheduled_payment", "type": "DECIMAL(15,2)"},
          {"name": "principal_portion", "type": "DECIMAL(15,2)"},
          {"name": "interest_portion", "type": "DECIMAL(15,2)"},
          {"name": "closing_balance", "type": "DECIMAL(15,2)"},
          {"name": "is_paid", "type": "BOOLEAN", "default": false},
          {"name": "paid_date", "type": "DATE", "nullable": true}
        ]
      },
      {
        "name": "settings",
        "description": "Application settings key-value store",
        "columns": [
          {"name": "setting_key", "type": "VARCHAR(50)", "primary_key": true},
          {"name": "setting_value", "type": "TEXT"},
          {"name": "description", "type": "VARCHAR(255)"}
        ]
      }
    ],
    "sample_data": {
      "users": [
        {"username": "admin", "password": "admin123", "full_name": "System Administrator", "email": "admin@vismera.com", "role": "admin"}
      ],
      "cars": [
        {"make": "Mercedes-Benz", "model": "S-Class", "year": 2024, "price": 5800000.00, "category": "Luxury Sedan", "color": "Silver", "mpg": 24, "is_available": true},
        {"make": "Porsche", "model": "911 Carrera", "year": 2024, "price": 9500000.00, "category": "Sports Car", "color": "Red", "mpg": 20, "is_available": true},
        {"make": "BMW", "model": "X5 M", "year": 2024, "price": 7200000.00, "category": "Luxury SUV", "color": "Black", "mpg": 22, "is_available": true},
        {"make": "Audi", "model": "RS7", "year": 2024, "price": 8500000.00, "category": "Sports Car", "color": "Gray", "mpg": 21, "is_available": true},
        {"make": "Lexus", "model": "LS 500", "year": 2024, "price": 6800000.00, "category": "Luxury Sedan", "color": "White", "mpg": 25, "is_available": true},
        {"make": "Range Rover", "model": "Sport", "year": 2024, "price": 7800000.00, "category": "Luxury SUV", "color": "Green", "mpg": 19, "is_available": true}
      ],
      "customers": [
        {"full_name": "Juan Carlos Dela Cruz", "contact_number": "+63 917 123 4567", "email": "juan.delacruz@email.com", "address": "123 Rizal Avenue, Makati City"},
        {"full_name": "Maria Santos Garcia", "contact_number": "+63 918 234 5678", "email": "maria.garcia@email.com", "address": "456 EDSA, Quezon City"},
        {"full_name": "Jose Andres Reyes", "contact_number": "+63 919 345 6789", "email": "jose.reyes@email.com", "address": "789 Ayala Avenue, BGC, Taguig"},
        {"full_name": "Ana Patricia Villanueva", "contact_number": "+63 920 456 7890", "email": "ana.villanueva@email.com", "address": "321 Session Road, Baguio City"}
      ],
      "settings": [
        {"setting_key": "company_name", "setting_value": "Vismerá Inc.", "description": "Company name"},
        {"setting_key": "currency_symbol", "setting_value": "₱", "description": "Currency symbol"},
        {"setting_key": "default_apr", "setting_value": "6.5", "description": "Default APR"},
        {"setting_key": "default_term", "setting_value": "36", "description": "Default term in months"},
        {"setting_key": "default_penalty_rate", "setting_value": "5.0", "description": "Default penalty rate"}
      ]
    }
  },

  "pages": {
    "static_pages": [
      {
        "name": "login.php",
        "description": "Login page with username and password form",
        "features": ["Username field", "Password field", "Remember me checkbox", "Login button", "Forgot password link"],
        "note": "Static - just redirect to dashboard on any login"
      },
      {
        "name": "register.php",
        "description": "Registration page for new admin users",
        "features": ["Full name", "Email", "Username", "Password", "Confirm password", "Register button"],
        "note": "Static - just show success message and redirect to login"
      },
      {
        "name": "forgot_password.php",
        "description": "Password recovery page",
        "features": ["Email input", "Send reset link button"],
        "note": "Static - just show success message"
      },
      {
        "name": "customers.php",
        "description": "Customer management page",
        "features": ["Customer list table", "Add/Edit/Delete buttons", "Search box"],
        "note": "Static - buttons clickable but no backend functionality"
      },
      {
        "name": "cars.php",
        "description": "Car inventory page",
        "features": ["Car grid/list view", "Add/Edit/Delete buttons", "Filter by category"],
        "note": "Static - buttons clickable but no backend functionality"
      },
      {
        "name": "loans.php",
        "description": "Loan management page",
        "features": ["Loans table", "View details button", "Status badges"],
        "note": "Static - buttons clickable but no backend functionality"
      },
      {
        "name": "payments.php",
        "description": "Payment tracking page",
        "features": ["Payments table", "Record payment button", "Date filter"],
        "note": "Static - buttons clickable but no backend functionality"
      },
      {
        "name": "reports.php",
        "description": "Reports and analytics page",
        "features": ["Summary cards", "Charts placeholder", "Export buttons"],
        "note": "Static - visual only"
      },
      {
        "name": "settings.php",
        "description": "System settings page",
        "features": ["Settings form", "Save button"],
        "note": "Static - buttons clickable but no backend functionality"
      },
      {
        "name": "calculator.php",
        "description": "Loan calculator page",
        "features": ["Input form for loan parameters", "Calculate button", "Results display", "Amortization table"],
        "note": "Static - use JavaScript for calculations (see algorithm below)"
      },
      {
        "name": "profile.php",
        "description": "User profile page",
        "features": ["Profile info", "Change password form"],
        "note": "Static"
      }
    ],
    "working_page": {
      "name": "dashboard.php",
      "description": "FULLY WORKING admin dashboard with CRUD operations",
      "must_work": true,
      "features": {
        "summary_cards": [
          {"title": "Total Customers", "icon": "users", "color": "primary", "query": "SELECT COUNT(*) FROM customers"},
          {"title": "Total Cars", "icon": "car", "color": "success", "query": "SELECT COUNT(*) FROM cars"},
          {"title": "Available Cars", "icon": "check-circle", "color": "info", "query": "SELECT COUNT(*) FROM cars WHERE is_available = 1"},
          {"title": "Active Loans", "icon": "file-text", "color": "warning", "query": "SELECT COUNT(*) FROM loans WHERE status = 'active'"},
          {"title": "Total Outstanding", "icon": "dollar-sign", "color": "danger", "query": "SELECT COALESCE(SUM(total_amount - COALESCE((SELECT SUM(amount) FROM payments WHERE payments.loan_id = loans.id), 0)), 0) FROM loans WHERE status = 'active'"},
          {"title": "This Month Payments", "icon": "credit-card", "color": "success", "query": "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE MONTH(payment_date) = MONTH(CURRENT_DATE()) AND YEAR(payment_date) = YEAR(CURRENT_DATE())"}
        ],
        "recent_tables": [
          {
            "title": "Recent Customers",
            "query": "SELECT id, full_name, contact_number, email, created_at FROM customers ORDER BY created_at DESC LIMIT 5",
            "columns": ["ID", "Name", "Contact", "Email", "Date Added"],
            "actions": ["view", "edit", "delete"]
          },
          {
            "title": "Recent Loans",
            "query": "SELECT l.id, c.full_name as customer, CONCAT(ca.make, ' ', ca.model) as car, l.principal, l.monthly_payment, l.status, l.created_at FROM loans l LEFT JOIN customers c ON l.customer_id = c.id LEFT JOIN cars ca ON l.car_id = ca.id ORDER BY l.created_at DESC LIMIT 5",
            "columns": ["ID", "Customer", "Car", "Principal", "Monthly", "Status", "Date"],
            "actions": ["view"]
          },
          {
            "title": "Recent Payments",
            "query": "SELECT p.id, c.full_name as customer, p.amount, p.payment_date, p.payment_type FROM payments p LEFT JOIN loans l ON p.loan_id = l.id LEFT JOIN customers c ON l.customer_id = c.id ORDER BY p.created_at DESC LIMIT 5",
            "columns": ["ID", "Customer", "Amount", "Date", "Type"],
            "actions": ["view"]
          }
        ],
        "crud_operations": {
          "quick_add_customer": {
            "description": "Modal form to quickly add a new customer",
            "fields": ["full_name (required)", "contact_number", "email", "address"],
            "action": "INSERT INTO customers (full_name, contact_number, email, address) VALUES (?, ?, ?, ?)"
          },
          "quick_add_car": {
            "description": "Modal form to quickly add a new car",
            "fields": ["make (required)", "model (required)", "year (required)", "price (required)", "category", "color"],
            "action": "INSERT INTO cars (make, model, year, price, category, color, is_available) VALUES (?, ?, ?, ?, ?, ?, 1)"
          },
          "edit_customer": {
            "description": "Edit customer inline or modal",
            "action": "UPDATE customers SET full_name=?, contact_number=?, email=?, address=? WHERE id=?"
          },
          "delete_customer": {
            "description": "Delete customer with confirmation",
            "action": "DELETE FROM customers WHERE id=?",
            "confirm_message": "Are you sure you want to delete this customer?"
          },
          "edit_car": {
            "description": "Edit car details",
            "action": "UPDATE cars SET make=?, model=?, year=?, price=?, category=?, color=?, is_available=? WHERE id=?"
          },
          "delete_car": {
            "description": "Delete car with confirmation",
            "action": "DELETE FROM cars WHERE id=?",
            "confirm_message": "Are you sure you want to delete this car?"
          }
        },
        "quick_actions": [
          {"label": "Add Customer", "icon": "user-plus", "modal": "quick_add_customer"},
          {"label": "Add Car", "icon": "plus-circle", "modal": "quick_add_car"},
          {"label": "New Loan", "icon": "file-plus", "link": "loans.php?action=new"},
          {"label": "Record Payment", "icon": "credit-card", "link": "payments.php?action=new"}
        ]
      }
    }
  },

  "file_structure": {
    "root": [
      "index.php (redirect to login or dashboard)",
      "login.php",
      "register.php",
      "forgot_password.php",
      "logout.php"
    ],
    "admin": [
      "dashboard.php (WORKING)",
      "customers.php",
      "cars.php",
      "loans.php",
      "payments.php",
      "reports.php",
      "settings.php",
      "calculator.php",
      "profile.php"
    ],
    "includes": [
      "config.php (database connection)",
      "header.php (common header with sidebar)",
      "footer.php (common footer)",
      "functions.php (helper functions)",
      "auth.php (session check)"
    ],
    "assets": {
      "css": ["style.css", "dashboard.css"],
      "js": ["main.js", "calculator.js", "dashboard.js"],
      "images": ["logo.png", "car-placeholder.png"]
    },
    "sql": ["database.sql (complete schema with sample data)"]
  },

  "algorithms": {
    "loan_calculation": {
      "description": "Standard Amortization Formula with compound interest",
      "formula": "M = P × [r(1+r)^n] / [(1+r)^n - 1]",
      "variables": {
        "M": "Monthly Payment",
        "P": "Principal (Amount Financed)",
        "r": "Effective Monthly Interest Rate",
        "n": "Total Number of Payments (months)"
      },
      "javascript_implementation": "
function calculateLoan(carPrice, taxRate, registrationFee, downPayment, tradeIn, apr, termYears, compounding) {
    // Step 1: Calculate total cost
    const salesTax = carPrice * (taxRate / 100);
    const totalCost = carPrice + salesTax + registrationFee;
    
    // Step 2: Calculate amount financed
    const amountFinanced = totalCost - downPayment - tradeIn;
    
    // Step 3: Calculate effective monthly rate based on compounding
    const compoundingPeriods = {
        'monthly': 12,
        'quarterly': 4,
        'semi-annually': 2,
        'annually': 1
    };
    const m = compoundingPeriods[compounding] || 12;
    const annualRate = apr / 100;
    
    // Effective Annual Rate
    const ear = Math.pow(1 + (annualRate / m), m) - 1;
    
    // Effective Monthly Rate
    const monthlyRate = Math.pow(1 + ear, 1/12) - 1;
    
    // Step 4: Calculate monthly payment
    const n = termYears * 12;
    let monthlyPayment;
    
    if (monthlyRate === 0) {
        monthlyPayment = amountFinanced / n;
    } else {
        monthlyPayment = amountFinanced * (monthlyRate * Math.pow(1 + monthlyRate, n)) / (Math.pow(1 + monthlyRate, n) - 1);
    }
    
    // Step 5: Calculate totals
    const totalPaid = monthlyPayment * n;
    const totalInterest = totalPaid - amountFinanced;
    
    return {
        amountFinanced: amountFinanced,
        monthlyPayment: monthlyPayment,
        totalInterest: totalInterest,
        totalPaid: totalPaid,
        effectiveRate: ear * 100
    };
}

function generateAmortizationSchedule(principal, monthlyRate, monthlyPayment, termMonths) {
    const schedule = [];
    let balance = principal;
    let totalPaid = 0;
    
    for (let month = 1; month <= termMonths; month++) {
        const interest = balance * monthlyRate;
        let principalPortion = monthlyPayment - interest;
        
        // Adjust last payment
        if (month === termMonths || principalPortion > balance) {
            principalPortion = balance;
        }
        
        balance -= principalPortion;
        if (balance < 0) balance = 0;
        
        totalPaid += monthlyPayment;
        
        schedule.push({
            paymentNumber: month,
            payment: monthlyPayment,
            principal: principalPortion,
            interest: interest,
            balance: balance,
            totalPaid: totalPaid
        });
    }
    
    return schedule;
}
"
    },
    "penalty_calculation": {
      "description": "Late payment penalty calculation",
      "formula": "Penalty = Scheduled Payment × (Penalty Rate / 100) × (Days Late / 30)",
      "javascript_implementation": "
function calculatePenalty(scheduledPayment, penaltyRate, daysLate, gracePeriod) {
    if (daysLate <= gracePeriod) return 0;
    const effectiveDaysLate = daysLate - gracePeriod;
    const monthsLate = effectiveDaysLate / 30;
    return scheduledPayment * (penaltyRate / 100) * monthsLate;
}
"
    },
    "secure_export": {
      "description": "Secure TXT export with SHA-256 hash per record (horizontal pipe-delimited, no filler)",
      "format": "ID|Name|Type|Status|Amount|Rate|Payment|Interest|Total|Term|DateRange | SHA256_HASH",
      "example_output": "43|Loan Payment #43|Amortization|Active|₱7,776,500.00|6.50%|₱152,156.15|₱2,408.60|₱6,542,649.45|5|Dec 10, 2025 - Dec 10, 2030 | 1F1E4387A80FFDB6F7FB1569F1F117F25A5FC23C63C52A8490B98F515B5FEFBD",
      "javascript_implementation": "
// SHA-256 hash function using Web Crypto API
async function hashSHA256(input) {
    const encoder = new TextEncoder();
    const data = encoder.encode(input);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(b => b.toString(16).padStart(2, '0').toUpperCase()).join('');
}

// Export amortization schedule to secure TXT format
async function exportSecureTXT(schedule, loan) {
    const startDate = new Date();
    const endDate = new Date(startDate);
    endDate.setMonth(endDate.getMonth() + (loan.termYears * 12));
    
    const formatDate = (d) => d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    const dateRange = formatDate(startDate) + ' - ' + formatDate(endDate);
    
    let output = '';
    let recordId = 1;
    
    for (const entry of schedule) {
        // Build pipe-delimited record (no filler/padding)
        const recordData = [
            recordId,
            'Loan Payment #' + entry.paymentNumber,
            'Amortization',
            'Active',
            '₱' + loan.amountFinanced.toFixed(2),
            loan.apr.toFixed(2) + '%',
            '₱' + entry.payment.toFixed(2),
            '₱' + entry.interest.toFixed(2),
            '₱' + entry.totalPaid.toFixed(2),
            loan.termYears,
            dateRange
        ].join('|');
        
        // Generate SHA-256 hash
        const hash = await hashSHA256(recordData);
        
        // Append: DATA | HASH
        output += recordData + ' | ' + hash + '\\n';
        output += '----------------------------------------\\n';
        recordId++;
    }
    
    return output;
}

// Import and verify secure TXT file
async function importSecureTXT(fileContent) {
    const lines = fileContent.split('\\n');
    const records = [];
    
    for (const line of lines) {
        if (line.trim() === '' || line.startsWith('---')) continue;
        
        const separatorIndex = line.lastIndexOf(' | ');
        if (separatorIndex === -1) continue;
        
        const recordData = line.substring(0, separatorIndex).trim();
        const storedHash = line.substring(separatorIndex + 3).trim();
        
        // Verify hash
        const calculatedHash = await hashSHA256(recordData);
        const hashValid = calculatedHash === storedHash;
        
        // Parse pipe-delimited data
        const parts = recordData.split('|');
        if (parts.length >= 10) {
            records.push({
                id: parts[0],
                name: parts[1],
                type: parts[2],
                status: parts[3],
                amount: parts[4],
                rate: parts[5],
                payment: parts[6],
                interest: parts[7],
                total: parts[8],
                term: parts[9],
                dateRange: parts[10] || 'N/A',
                storedHash: storedHash,
                calculatedHash: calculatedHash,
                hashValid: hashValid
            });
        }
    }
    
    return records;
}
"
    }
  },

  "design_specifications": {
    "color_scheme": {
      "primary": "#3B82F6",
      "secondary": "#6B7280",
      "success": "#10B981",
      "warning": "#F59E0B",
      "danger": "#EF4444",
      "info": "#06B6D4",
      "background": "#F3F4F6",
      "sidebar": "#1F2937",
      "card": "#FFFFFF"
    },
    "typography": {
      "font_family": "'Segoe UI', 'Roboto', 'Helvetica Neue', Arial, sans-serif",
      "heading_font": "'Segoe UI', sans-serif"
    },
    "layout": {
      "sidebar_width": "260px",
      "header_height": "64px",
      "card_border_radius": "8px",
      "responsive_breakpoint": "768px"
    },
    "components": {
      "cards": "White background, subtle shadow, rounded corners",
      "tables": "Striped rows, hover effect, responsive",
      "buttons": "Rounded, primary blue, hover darken",
      "forms": "Floating labels or standard labels, validation styling",
      "modals": "Centered, backdrop blur, slide-in animation"
    }
  },

  "sidebar_menu": [
    {"label": "Dashboard", "icon": "home", "link": "dashboard.php", "active": true},
    {"label": "Customers", "icon": "users", "link": "customers.php"},
    {"label": "Car Inventory", "icon": "car", "link": "cars.php"},
    {"label": "Loans", "icon": "file-text", "link": "loans.php"},
    {"label": "Payments", "icon": "credit-card", "link": "payments.php"},
    {"label": "Calculator", "icon": "calculator", "link": "calculator.php"},
    {"label": "Reports", "icon": "bar-chart-2", "link": "reports.php"},
    {"divider": true},
    {"label": "Settings", "icon": "settings", "link": "settings.php"},
    {"label": "Profile", "icon": "user", "link": "profile.php"},
    {"label": "Logout", "icon": "log-out", "link": "logout.php"}
  ],

  "requirements": {
    "must_have": [
      "Complete database.sql file with all tables and sample data",
      "Working dashboard.php with real CRUD operations",
      "Database connection in config.php",
      "All static pages must be navigable and styled",
      "Responsive design for mobile and desktop",
      "Session-based authentication check on admin pages",
      "Currency formatting for Philippine Peso (₱)",
      "Date formatting in MM/DD/YYYY format"
    ],
    "dashboard_must_work": [
      "Display real counts from database in summary cards",
      "Show recent customers, loans, and payments from database",
      "Add new customer via modal form - saves to database",
      "Add new car via modal form - saves to database",
      "Edit customer - updates database",
      "Delete customer - removes from database with confirmation",
      "Edit car - updates database",
      "Delete car - removes from database with confirmation",
      "All data refreshes after CRUD operations"
    ],
    "static_pages_requirements": [
      "All navigation links work",
      "All buttons are clickable (show alert or do nothing)",
      "Forms are styled but don't need to submit",
      "Tables show placeholder or sample data",
      "Consistent styling with dashboard"
    ]
  },

  "php_config": {
    "database_connection": "
<?php
// config.php
define('DB_HOST', 'localhost');
define('DB_NAME', 'vismera_loans');
define('DB_USER', 'root');
define('DB_PASS', '');

try {
    $pdo = new PDO(
        'mysql:host=' . DB_HOST . ';dbname=' . DB_NAME . ';charset=utf8mb4',
        DB_USER,
        DB_PASS,
        [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES => false
        ]
    );
} catch (PDOException $e) {
    die('Database connection failed: ' . $e->getMessage());
}

session_start();
?>
",
    "helper_functions": "
<?php
// functions.php
function formatCurrency($amount) {
    return '₱' . number_format($amount, 2);
}

function formatDate($date) {
    return date('M d, Y', strtotime($date));
}

function escape($string) {
    return htmlspecialchars($string, ENT_QUOTES, 'UTF-8');
}

function redirect($url) {
    header('Location: ' . $url);
    exit;
}

function isLoggedIn() {
    return isset($_SESSION['user_id']);
}
?>
"
  },

  "output_files": [
    "database.sql",
    "config.php",
    "functions.php",
    "auth.php",
    "index.php",
    "login.php",
    "register.php",
    "forgot_password.php",
    "logout.php",
    "admin/dashboard.php (FULLY WORKING WITH CRUD)",
    "admin/customers.php",
    "admin/cars.php",
    "admin/loans.php",
    "admin/payments.php",
    "admin/calculator.php",
    "admin/reports.php",
    "admin/settings.php",
    "admin/profile.php",
    "includes/header.php",
    "includes/footer.php",
    "includes/sidebar.php",
    "assets/css/style.css",
    "assets/js/main.js",
    "assets/js/calculator.js"
  ]
}
```

---

## Usage Instructions

1. Copy the entire JSON block above (including the ```json and ``` markers or just the content inside)
2. Paste it into your AI website builder prompt
3. The AI should generate:
   - A complete MySQL database schema (`database.sql`)
   - A fully working dashboard with CRUD operations
   - All other pages as static but navigable
   - Consistent styling throughout

## Key Points

- **Only the Dashboard needs to work** - All CRUD operations (Create, Read, Update, Delete) for customers and cars
- **Other pages are static** - They look complete but buttons just show alerts or do nothing
- **Calculator page** - Uses JavaScript only (the algorithm is provided in the JSON)
- **Currency**: Philippine Peso (₱)
- **Sample data**: Filipino names and Philippine addresses included

## Database Quick Setup

After generating the files, run the `database.sql` file in phpMyAdmin or MySQL command line to set up the database with sample data.
