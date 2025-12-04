package com.vismera.storage;

import com.vismera.models.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.*;

/**
 * Text file-based database storage system.
 * Stores data in simple text files instead of H2/SQL database.
 * @author Vismerá Inc.
 */
public class TextFileDatabase {
    
    private static final Logger LOGGER = Logger.getLogger(TextFileDatabase.class.getName());
    private static TextFileDatabase instance;
    
    // Data folder
    private static final String DATA_FOLDER = ".vismera_data";
    private final Path dataPath;
    
    // File names
    private static final String CARS_FILE = "cars.txt";
    private static final String CUSTOMERS_FILE = "customers.txt";
    private static final String LOANS_FILE = "loans.txt";
    private static final String PAYMENTS_FILE = "payments.txt";
    private static final String AMORTIZATION_FILE = "amortization.txt";
    private static final String SETTINGS_FILE = "settings.txt";
    private static final String SEQUENCE_FILE = "sequences.txt";
    
    // Field separator
    private static final String SEPARATOR = "||";
    private static final String SEPARATOR_REGEX = "\\|\\|";
    
    // Date formatters
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    // ID sequences
    private Map<String, Integer> sequences;
    
    // Cached data
    private List<Car> cars;
    private List<Customer> customers;
    private List<Loan> loans;
    private List<Payment> payments;
    private List<AmortizationRow> amortizationRows;
    private Map<String, String> settings;
    
    private TextFileDatabase() {
        String userHome = System.getProperty("user.home");
        dataPath = Paths.get(userHome, DATA_FOLDER);
        sequences = new HashMap<>();
        initializeStorage();
    }
    
    public static synchronized TextFileDatabase getInstance() {
        if (instance == null) {
            instance = new TextFileDatabase();
        }
        return instance;
    }
    
    /**
     * Initialize storage - create folder and load data
     */
    private void initializeStorage() {
        try {
            // Create data directory if not exists
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                LOGGER.info("Created data directory: " + dataPath);
            }
            
            // Load sequences
            loadSequences();
            
            // Load all data
            loadAllData();
            
            // Insert default data if empty
            if (cars.isEmpty()) {
                insertDefaultData();
            }
            
            LOGGER.info("Text file database initialized successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize storage", e);
        }
    }
    
    private void loadAllData() {
        cars = loadCars();
        customers = loadCustomers();
        loans = loadLoans();
        payments = loadPayments();
        amortizationRows = loadAmortizationRows();
        settings = loadSettings();
    }
    
    // ==================== SEQUENCES ====================
    
    private void loadSequences() {
        sequences.put("cars", 1);
        sequences.put("customers", 1);
        sequences.put("loans", 1);
        sequences.put("payments", 1);
        sequences.put("amortization", 1);
        
        Path seqFile = dataPath.resolve(SEQUENCE_FILE);
        if (Files.exists(seqFile)) {
            try (BufferedReader reader = Files.newBufferedReader(seqFile)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        sequences.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to load sequences", e);
            }
        }
    }
    
    private void saveSequences() {
        Path seqFile = dataPath.resolve(SEQUENCE_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(seqFile)) {
            for (Map.Entry<String, Integer> entry : sequences.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save sequences", e);
        }
    }
    
    public synchronized int getNextId(String table) {
        int id = sequences.getOrDefault(table, 1);
        sequences.put(table, id + 1);
        saveSequences();
        return id;
    }
    
    // ==================== CARS ====================
    
    private List<Car> loadCars() {
        List<Car> list = new ArrayList<>();
        Path file = dataPath.resolve(CARS_FILE);
        if (Files.exists(file)) {
            try (BufferedReader reader = Files.newBufferedReader(file)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        Car car = parseCar(line);
                        if (car != null) list.add(car);
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to load cars", e);
            }
        }
        return list;
    }
    
    private Car parseCar(String line) {
        try {
            String[] parts = line.split(SEPARATOR_REGEX);
            if (parts.length >= 11) {
                Car car = new Car();
                car.setId(Integer.parseInt(parts[0]));
                car.setMake(parts[1]);
                car.setModel(parts[2]);
                car.setYear(Integer.parseInt(parts[3]));
                car.setPriceBigDecimal(new BigDecimal(parts[4]));
                car.setCategory(parts[5]);
                car.setColor(parts[6]);
                car.setMpg(Integer.parseInt(parts[7]));
                car.setImagePath(parts[8].equals("null") ? null : parts[8]);
                car.setNotes(parts[9].equals("null") ? null : parts[9]);
                car.setAvailable(Boolean.parseBoolean(parts[10]));
                if (parts.length > 11 && !parts[11].equals("null")) {
                    car.setCreatedAt(LocalDateTime.parse(parts[11], DATETIME_FORMAT));
                }
                return car;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to parse car: " + line, e);
        }
        return null;
    }
    
    private String formatCar(Car car) {
        return String.join(SEPARATOR,
            String.valueOf(car.getId()),
            car.getMake(),
            car.getModel(),
            String.valueOf(car.getYear()),
            car.getPriceBigDecimal().toPlainString(),
            car.getCategory() != null ? car.getCategory() : "",
            car.getColor() != null ? car.getColor() : "",
            String.valueOf(car.getMpg()),
            car.getImagePath() != null ? car.getImagePath() : "null",
            car.getNotes() != null ? car.getNotes() : "null",
            String.valueOf(car.isAvailable()),
            car.getCreatedAt() != null ? car.getCreatedAt().format(DATETIME_FORMAT) : "null"
        );
    }
    
    public void saveCars() {
        Path file = dataPath.resolve(CARS_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (Car car : cars) {
                writer.write(formatCar(car));
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save cars", e);
        }
    }
    
    public List<Car> getAllCars() {
        return new ArrayList<>(cars);
    }
    
    public Car getCarById(int id) {
        return cars.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }
    
    public int insertCar(Car car) {
        car.setId(getNextId("cars"));
        car.setCreatedAt(LocalDateTime.now());
        cars.add(car);
        saveCars();
        return car.getId();
    }
    
    public boolean updateCar(Car car) {
        for (int i = 0; i < cars.size(); i++) {
            if (cars.get(i).getId() == car.getId()) {
                cars.set(i, car);
                saveCars();
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteCar(int id) {
        boolean removed = cars.removeIf(c -> c.getId() == id);
        if (removed) saveCars();
        return removed;
    }
    
    public List<Car> getAvailableCars() {
        List<Car> available = new ArrayList<>();
        Set<Integer> carsInActiveLoans = new HashSet<>();
        for (Loan loan : loans) {
            if ("active".equals(loan.getStatus())) {
                carsInActiveLoans.add(loan.getCarId());
            }
        }
        for (Car car : cars) {
            if (car.isAvailable() && !carsInActiveLoans.contains(car.getId())) {
                available.add(car);
            }
        }
        return available;
    }
    
    public List<Car> searchCars(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCars();
        }
        String lowerQuery = query.toLowerCase();
        List<Car> results = new ArrayList<>();
        for (Car car : cars) {
            if (car.getMake().toLowerCase().contains(lowerQuery) ||
                car.getModel().toLowerCase().contains(lowerQuery) ||
                String.valueOf(car.getYear()).contains(query) ||
                (car.getCategory() != null && car.getCategory().toLowerCase().contains(lowerQuery))) {
                results.add(car);
            }
        }
        return results;
    }
    
    // ==================== CUSTOMERS ====================
    
    private List<Customer> loadCustomers() {
        List<Customer> list = new ArrayList<>();
        Path file = dataPath.resolve(CUSTOMERS_FILE);
        if (Files.exists(file)) {
            try (BufferedReader reader = Files.newBufferedReader(file)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        Customer customer = parseCustomer(line);
                        if (customer != null) list.add(customer);
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to load customers", e);
            }
        }
        return list;
    }
    
    private Customer parseCustomer(String line) {
        try {
            String[] parts = line.split(SEPARATOR_REGEX);
            if (parts.length >= 5) {
                Customer customer = new Customer();
                customer.setId(Integer.parseInt(parts[0]));
                customer.setFullName(parts[1]);
                customer.setContactNumber(parts[2]);
                customer.setEmail(parts[3]);
                customer.setAddress(parts[4]);
                if (parts.length > 5 && !parts[5].equals("null")) {
                    customer.setCreatedAt(LocalDateTime.parse(parts[5], DATETIME_FORMAT));
                }
                return customer;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to parse customer: " + line, e);
        }
        return null;
    }
    
    private String formatCustomer(Customer customer) {
        return String.join(SEPARATOR,
            String.valueOf(customer.getId()),
            customer.getFullName(),
            customer.getContactNumber() != null ? customer.getContactNumber() : "",
            customer.getEmail() != null ? customer.getEmail() : "",
            customer.getAddress() != null ? customer.getAddress() : "",
            customer.getCreatedAt() != null ? customer.getCreatedAt().format(DATETIME_FORMAT) : "null"
        );
    }
    
    public void saveCustomers() {
        Path file = dataPath.resolve(CUSTOMERS_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (Customer customer : customers) {
                writer.write(formatCustomer(customer));
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save customers", e);
        }
    }
    
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }
    
    public Customer getCustomerById(int id) {
        return customers.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }
    
    public int insertCustomer(Customer customer) {
        customer.setId(getNextId("customers"));
        customer.setCreatedAt(LocalDateTime.now());
        customers.add(customer);
        saveCustomers();
        return customer.getId();
    }
    
    public boolean updateCustomer(Customer customer) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId() == customer.getId()) {
                customers.set(i, customer);
                saveCustomers();
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteCustomer(int id) {
        boolean removed = customers.removeIf(c -> c.getId() == id);
        if (removed) saveCustomers();
        return removed;
    }
    
    public List<Customer> searchCustomers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCustomers();
        }
        String lowerQuery = query.toLowerCase();
        List<Customer> results = new ArrayList<>();
        for (Customer customer : customers) {
            if (customer.getFullName().toLowerCase().contains(lowerQuery) ||
                (customer.getContactNumber() != null && customer.getContactNumber().contains(query)) ||
                (customer.getEmail() != null && customer.getEmail().toLowerCase().contains(lowerQuery))) {
                results.add(customer);
            }
        }
        return results;
    }
    
    // Alias methods for compatibility with controllers
    public int addCustomer(Customer customer) {
        return insertCustomer(customer);
    }
    
    public int addCar(Car car) {
        return insertCar(car);
    }
    
    public boolean customerHasActiveLoans(int customerId) {
        for (Loan loan : loans) {
            if (loan.getCustomerId() == customerId && 
                ("active".equalsIgnoreCase(loan.getStatus()) || "pending".equalsIgnoreCase(loan.getStatus()))) {
                return true;
            }
        }
        return false;
    }
    
    public boolean customerHasLoans(int customerId) {
        for (Loan loan : loans) {
            if (loan.getCustomerId() == customerId) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isCarInActiveLoan(int carId) {
        for (Loan loan : loans) {
            if (loan.getCarId() == carId && 
                ("active".equalsIgnoreCase(loan.getStatus()) || "pending".equalsIgnoreCase(loan.getStatus()))) {
                return true;
            }
        }
        return false;
    }
    
    // ==================== LOANS ====================
    
    private List<Loan> loadLoans() {
        List<Loan> list = new ArrayList<>();
        Path file = dataPath.resolve(LOANS_FILE);
        if (Files.exists(file)) {
            try (BufferedReader reader = Files.newBufferedReader(file)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        Loan loan = parseLoan(line);
                        if (loan != null) list.add(loan);
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to load loans", e);
            }
        }
        return list;
    }
    
    private Loan parseLoan(String line) {
        try {
            String[] parts = line.split(SEPARATOR_REGEX);
            if (parts.length >= 20) {
                Loan loan = new Loan();
                int i = 0;
                loan.setId(Integer.parseInt(parts[i++]));
                loan.setCustomerId(Integer.parseInt(parts[i++]));
                loan.setCarId(Integer.parseInt(parts[i++]));
                loan.setPrincipal(new BigDecimal(parts[i++]));
                loan.setApr(new BigDecimal(parts[i++]));
                loan.setCompounding(parts[i++]);
                loan.setTermMonths(Integer.parseInt(parts[i++]));
                loan.setPaymentFrequency(parts[i++]);
                loan.setStartDate(parts[i].equals("null") ? null : LocalDate.parse(parts[i], DATE_FORMAT)); i++;
                loan.setPenaltyRate(new BigDecimal(parts[i++]));
                loan.setPenaltyType(parts[i++]);
                loan.setGracePeriodDays(Integer.parseInt(parts[i++]));
                loan.setDownPayment(new BigDecimal(parts[i++]));
                loan.setTradeInValue(new BigDecimal(parts[i++]));
                loan.setSalesTaxRate(new BigDecimal(parts[i++]));
                loan.setRegistrationFee(new BigDecimal(parts[i++]));
                loan.setMonthlyPayment(new BigDecimal(parts[i++]));
                loan.setTotalInterest(new BigDecimal(parts[i++]));
                loan.setTotalAmount(new BigDecimal(parts[i++]));
                loan.setStatus(parts[i++]);
                if (parts.length > i && !parts[i].equals("null")) {
                    loan.setCreatedAt(LocalDateTime.parse(parts[i], DATETIME_FORMAT));
                }
                return loan;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to parse loan: " + line, e);
        }
        return null;
    }
    
    private String formatLoan(Loan loan) {
        return String.join(SEPARATOR,
            String.valueOf(loan.getId()),
            String.valueOf(loan.getCustomerId()),
            String.valueOf(loan.getCarId()),
            loan.getPrincipal().toPlainString(),
            loan.getApr().toPlainString(),
            loan.getCompounding(),
            String.valueOf(loan.getTermMonths()),
            loan.getPaymentFrequency() != null ? loan.getPaymentFrequency() : "monthly",
            loan.getStartDate() != null ? loan.getStartDate().format(DATE_FORMAT) : "null",
            loan.getPenaltyRate().toPlainString(),
            loan.getPenaltyType() != null ? loan.getPenaltyType() : "percent_per_month",
            String.valueOf(loan.getGracePeriodDays()),
            loan.getDownPayment().toPlainString(),
            loan.getTradeInValue().toPlainString(),
            loan.getSalesTaxRate().toPlainString(),
            loan.getRegistrationFee().toPlainString(),
            loan.getMonthlyPayment().toPlainString(),
            loan.getTotalInterest().toPlainString(),
            loan.getTotalAmount().toPlainString(),
            loan.getStatus(),
            loan.getCreatedAt() != null ? loan.getCreatedAt().format(DATETIME_FORMAT) : "null"
        );
    }
    
    public void saveLoans() {
        Path file = dataPath.resolve(LOANS_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (Loan loan : loans) {
                writer.write(formatLoan(loan));
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save loans", e);
        }
    }
    
    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }
    
    public Loan getLoanById(int id) {
        return loans.stream().filter(l -> l.getId() == id).findFirst().orElse(null);
    }
    
    public Loan getLoanWithDetails(int id) {
        Loan loan = getLoanById(id);
        if (loan != null) {
            loan.setCustomer(getCustomerById(loan.getCustomerId()));
            loan.setCar(getCarById(loan.getCarId()));
        }
        return loan;
    }
    
    public List<Loan> getAllLoansWithDetails() {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans) {
            loan.setCustomer(getCustomerById(loan.getCustomerId()));
            loan.setCar(getCarById(loan.getCarId()));
            result.add(loan);
        }
        return result;
    }
    
    public List<Loan> getLoansByStatus(String status) {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans) {
            if (loan.getStatus().equalsIgnoreCase(status)) {
                loan.setCustomer(getCustomerById(loan.getCustomerId()));
                loan.setCar(getCarById(loan.getCarId()));
                result.add(loan);
            }
        }
        return result;
    }
    
    public int insertLoan(Loan loan) {
        loan.setId(getNextId("loans"));
        loan.setCreatedAt(LocalDateTime.now());
        loans.add(loan);
        saveLoans();
        return loan.getId();
    }
    
    public boolean updateLoan(Loan loan) {
        for (int i = 0; i < loans.size(); i++) {
            if (loans.get(i).getId() == loan.getId()) {
                loans.set(i, loan);
                saveLoans();
                return true;
            }
        }
        return false;
    }
    
    public boolean updateLoanStatus(int loanId, String status) {
        Loan loan = getLoanById(loanId);
        if (loan != null) {
            loan.setStatus(status);
            saveLoans();
            return true;
        }
        return false;
    }
    
    public boolean deleteLoan(int id) {
        boolean removed = loans.removeIf(l -> l.getId() == id);
        if (removed) saveLoans();
        return removed;
    }
    
    // ==================== PAYMENTS ====================
    
    private List<Payment> loadPayments() {
        List<Payment> list = new ArrayList<>();
        Path file = dataPath.resolve(PAYMENTS_FILE);
        if (Files.exists(file)) {
            try (BufferedReader reader = Files.newBufferedReader(file)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        Payment payment = parsePayment(line);
                        if (payment != null) list.add(payment);
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to load payments", e);
            }
        }
        return list;
    }
    
    private Payment parsePayment(String line) {
        try {
            String[] parts = line.split(SEPARATOR_REGEX);
            if (parts.length >= 11) {
                Payment payment = new Payment();
                int i = 0;
                payment.setId(Integer.parseInt(parts[i++]));
                payment.setLoanId(Integer.parseInt(parts[i++]));
                payment.setPaymentDate(LocalDate.parse(parts[i++], DATE_FORMAT));
                payment.setAmount(new BigDecimal(parts[i++]));
                payment.setAppliedToPeriod(Integer.parseInt(parts[i++]));
                payment.setType(parts[i++]);
                payment.setPenaltyApplied(new BigDecimal(parts[i++]));
                payment.setPrincipalApplied(new BigDecimal(parts[i++]));
                payment.setInterestApplied(new BigDecimal(parts[i++]));
                payment.setNote(parts[i].equals("null") ? null : parts[i]); i++;
                payment.setRecordedBy(parts[i++]);
                if (parts.length > i && !parts[i].equals("null")) {
                    payment.setRecordedAt(LocalDateTime.parse(parts[i], DATETIME_FORMAT));
                }
                return payment;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to parse payment: " + line, e);
        }
        return null;
    }
    
    private String formatPayment(Payment payment) {
        return String.join(SEPARATOR,
            String.valueOf(payment.getId()),
            String.valueOf(payment.getLoanId()),
            payment.getPaymentDate().format(DATE_FORMAT),
            payment.getAmount().toPlainString(),
            String.valueOf(payment.getAppliedToPeriod()),
            payment.getType(),
            payment.getPenaltyApplied().toPlainString(),
            payment.getPrincipalApplied().toPlainString(),
            payment.getInterestApplied().toPlainString(),
            payment.getNote() != null ? payment.getNote() : "null",
            payment.getRecordedBy() != null ? payment.getRecordedBy() : "System",
            payment.getRecordedAt() != null ? payment.getRecordedAt().format(DATETIME_FORMAT) : "null"
        );
    }
    
    public void savePayments() {
        Path file = dataPath.resolve(PAYMENTS_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (Payment payment : payments) {
                writer.write(formatPayment(payment));
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save payments", e);
        }
    }
    
    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments);
    }
    
    public List<Payment> getPaymentsByLoanId(int loanId) {
        List<Payment> result = new ArrayList<>();
        for (Payment payment : payments) {
            if (payment.getLoanId() == loanId) {
                result.add(payment);
            }
        }
        result.sort(Comparator.comparing(Payment::getPaymentDate));
        return result;
    }
    
    public BigDecimal getTotalPaidForLoan(int loanId) {
        BigDecimal total = BigDecimal.ZERO;
        for (Payment payment : payments) {
            if (payment.getLoanId() == loanId) {
                total = total.add(payment.getAmount());
            }
        }
        return total;
    }
    
    public int insertPayment(Payment payment) {
        payment.setId(getNextId("payments"));
        payment.setRecordedAt(LocalDateTime.now());
        payments.add(payment);
        savePayments();
        return payment.getId();
    }
    
    // ==================== AMORTIZATION ROWS ====================
    
    private List<AmortizationRow> loadAmortizationRows() {
        List<AmortizationRow> list = new ArrayList<>();
        Path file = dataPath.resolve(AMORTIZATION_FILE);
        if (Files.exists(file)) {
            try (BufferedReader reader = Files.newBufferedReader(file)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        AmortizationRow row = parseAmortizationRow(line);
                        if (row != null) list.add(row);
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to load amortization rows", e);
            }
        }
        return list;
    }
    
    private AmortizationRow parseAmortizationRow(String line) {
        try {
            String[] parts = line.split(SEPARATOR_REGEX);
            if (parts.length >= 13) {
                AmortizationRow row = new AmortizationRow();
                int i = 0;
                row.setId(Integer.parseInt(parts[i++]));
                row.setLoanId(Integer.parseInt(parts[i++]));
                row.setPeriodIndex(Integer.parseInt(parts[i++]));
                row.setDueDate(LocalDate.parse(parts[i++], DATE_FORMAT));
                row.setOpeningBalance(new BigDecimal(parts[i++]));
                row.setScheduledPayment(new BigDecimal(parts[i++]));
                row.setPrincipalPaid(new BigDecimal(parts[i++]));
                row.setInterestPaid(new BigDecimal(parts[i++]));
                row.setPenaltyAmount(new BigDecimal(parts[i++]));
                row.setExtraPayment(new BigDecimal(parts[i++]));
                row.setClosingBalance(new BigDecimal(parts[i++]));
                row.setPaid(Boolean.parseBoolean(parts[i++]));
                row.setPaidDate(parts[i].equals("null") ? null : LocalDate.parse(parts[i], DATE_FORMAT));
                return row;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to parse amortization row: " + line, e);
        }
        return null;
    }
    
    private String formatAmortizationRow(AmortizationRow row) {
        return String.join(SEPARATOR,
            String.valueOf(row.getId()),
            String.valueOf(row.getLoanId()),
            String.valueOf(row.getPeriodIndex()),
            row.getDueDate().format(DATE_FORMAT),
            row.getOpeningBalance().toPlainString(),
            row.getScheduledPayment().toPlainString(),
            row.getPrincipalPaid().toPlainString(),
            row.getInterestPaid().toPlainString(),
            row.getPenaltyAmount().toPlainString(),
            row.getExtraPayment().toPlainString(),
            row.getClosingBalance().toPlainString(),
            String.valueOf(row.isPaid()),
            row.getPaidDate() != null ? row.getPaidDate().format(DATE_FORMAT) : "null"
        );
    }
    
    public void saveAmortizationRows() {
        Path file = dataPath.resolve(AMORTIZATION_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (AmortizationRow row : amortizationRows) {
                writer.write(formatAmortizationRow(row));
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save amortization rows", e);
        }
    }
    
    public List<AmortizationRow> getAmortizationByLoanId(int loanId) {
        List<AmortizationRow> result = new ArrayList<>();
        for (AmortizationRow row : amortizationRows) {
            if (row.getLoanId() == loanId) {
                result.add(row);
            }
        }
        result.sort(Comparator.comparingInt(AmortizationRow::getPeriodIndex));
        return result;
    }
    
    public void insertAmortizationRows(List<AmortizationRow> rows) {
        for (AmortizationRow row : rows) {
            row.setId(getNextId("amortization"));
            amortizationRows.add(row);
        }
        saveAmortizationRows();
    }
    
    public void deleteAmortizationByLoanId(int loanId) {
        amortizationRows.removeIf(row -> row.getLoanId() == loanId);
        saveAmortizationRows();
    }
    
    public boolean updateAmortizationRow(AmortizationRow row) {
        for (int i = 0; i < amortizationRows.size(); i++) {
            if (amortizationRows.get(i).getId() == row.getId()) {
                amortizationRows.set(i, row);
                saveAmortizationRows();
                return true;
            }
        }
        return false;
    }
    
    public AmortizationRow getNextUnpaidRow(int loanId) {
        return amortizationRows.stream()
            .filter(r -> r.getLoanId() == loanId && !r.isPaid())
            .min(Comparator.comparingInt(AmortizationRow::getPeriodIndex))
            .orElse(null);
    }
    
    // ==================== SETTINGS ====================
    
    private Map<String, String> loadSettings() {
        Map<String, String> map = new HashMap<>();
        Path file = dataPath.resolve(SETTINGS_FILE);
        if (Files.exists(file)) {
            try (BufferedReader reader = Files.newBufferedReader(file)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        map.put(parts[0], parts[1]);
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to load settings", e);
            }
        }
        return map;
    }
    
    public void saveSettings() {
        Path file = dataPath.resolve(SETTINGS_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (Map.Entry<String, String> entry : settings.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save settings", e);
        }
    }
    
    public String getSetting(String key) {
        return settings.get(key);
    }
    
    public String getSetting(String key, String defaultValue) {
        return settings.getOrDefault(key, defaultValue);
    }
    
    public void setSetting(String key, String value) {
        settings.put(key, value);
        saveSettings();
    }
    
    // ==================== DEFAULT DATA ====================
    
    private void insertDefaultData() {
        LOGGER.info("Inserting default data...");
        
        // Default settings
        settings.put("currency_symbol", "₱");
        settings.put("default_apr", "6.5");
        settings.put("default_term", "36");
        settings.put("default_penalty_rate", "2.0");
        settings.put("default_penalty_type", "percent_per_month");
        settings.put("default_grace_period", "5");
        settings.put("app_version", "3.0.0");
        saveSettings();
        
        // Sample cars (Philippine prices)
        Car[] sampleCars = {
            createCar("Toyota", "Camry", 2024, new BigDecimal("2100000"), "Sedan", "White", 28, "toyota_camry.png"),
            createCar("Honda", "Civic", 2024, new BigDecimal("1350000"), "Sedan", "Black", 32, "honda_civic.png"),
            createCar("Ford", "Ranger", 2024, new BigDecimal("1680000"), "Pickup", "Blue", 22, "ford_ranger.png"),
            createCar("Mitsubishi", "Montero Sport", 2024, new BigDecimal("2050000"), "SUV", "Gray", 20, "mitsubishi_montero.png"),
            createCar("Mazda", "CX-5", 2024, new BigDecimal("1890000"), "SUV", "Red", 26, "mazda_cx5.png"),
            createCar("Hyundai", "Accent", 2024, new BigDecimal("898000"), "Sedan", "Silver", 35, "hyundai_accent.png")
        };
        
        for (Car car : sampleCars) {
            insertCar(car);
        }
        
        // Sample customers (Filipino)
        Customer[] sampleCustomers = {
            createCustomer("Juan Carlos Dela Cruz", "+63 917 123 4567", "juan.delacruz@email.com", "123 Rizal Avenue, Makati City"),
            createCustomer("Maria Santos Garcia", "+63 918 234 5678", "maria.garcia@email.com", "456 EDSA, Quezon City"),
            createCustomer("Jose Andres Reyes", "+63 919 345 6789", "jose.reyes@email.com", "789 Ayala Avenue, BGC, Taguig"),
            createCustomer("Ana Patricia Villanueva", "+63 920 456 7890", "ana.villanueva@email.com", "321 Session Road, Baguio City")
        };
        
        for (Customer customer : sampleCustomers) {
            insertCustomer(customer);
        }
        
        LOGGER.info("Default data inserted successfully");
    }
    
    private Car createCar(String make, String model, int year, BigDecimal price, String category, String color, int mpg, String imagePath) {
        Car car = new Car();
        car.setMake(make);
        car.setModel(model);
        car.setYear(year);
        car.setPriceBigDecimal(price);
        car.setCategory(category);
        car.setColor(color);
        car.setMpg(mpg);
        car.setImagePath(imagePath);
        car.setAvailable(true);
        return car;
    }
    
    private Customer createCustomer(String name, String phone, String email, String address) {
        Customer customer = new Customer();
        customer.setFullName(name);
        customer.setContactNumber(phone);
        customer.setEmail(email);
        customer.setAddress(address);
        return customer;
    }
    
    /**
     * Get the data folder path
     */
    public Path getDataPath() {
        return dataPath;
    }
    
    /**
     * Clear all data and reset
     */
    public void clearAllData() {
        cars.clear();
        customers.clear();
        loans.clear();
        payments.clear();
        amortizationRows.clear();
        settings.clear();
        sequences.clear();
        
        // Delete all files
        try {
            Files.deleteIfExists(dataPath.resolve(CARS_FILE));
            Files.deleteIfExists(dataPath.resolve(CUSTOMERS_FILE));
            Files.deleteIfExists(dataPath.resolve(LOANS_FILE));
            Files.deleteIfExists(dataPath.resolve(PAYMENTS_FILE));
            Files.deleteIfExists(dataPath.resolve(AMORTIZATION_FILE));
            Files.deleteIfExists(dataPath.resolve(SETTINGS_FILE));
            Files.deleteIfExists(dataPath.resolve(SEQUENCE_FILE));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to clear data files", e);
        }
        
        // Reinitialize
        initializeStorage();
    }
}
