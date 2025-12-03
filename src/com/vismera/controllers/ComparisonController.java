package com.vismera.controllers;

import com.vismera.models.LoanScenario;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for managing loan comparison scenarios.
 * @author Vismerá Inc.
 */
public class ComparisonController {
    
    private static ComparisonController instance;
    private List<LoanScenario> scenarios;

    private ComparisonController() {
        scenarios = new ArrayList<>();
    }

    /**
     * Get singleton instance
     */
    public static ComparisonController getInstance() {
        if (instance == null) {
            instance = new ComparisonController();
        }
        return instance;
    }

    /**
     * Add a new scenario
     */
    public void addScenario(LoanScenario scenario) {
        scenario.calculateMetrics();
        scenarios.add(scenario);
        updateBestDeal();
    }

    /**
     * Create and add a new scenario
     */
    public LoanScenario createScenario(String name, double loanAmount, double interestRate, int termYears) {
        LoanScenario scenario = new LoanScenario(name, loanAmount, interestRate, termYears);
        addScenario(scenario);
        return scenario;
    }

    /**
     * Remove scenario at index
     */
    public void removeScenario(int index) {
        if (index >= 0 && index < scenarios.size()) {
            scenarios.remove(index);
            updateBestDeal();
        }
    }

    /**
     * Remove a specific scenario
     */
    public void removeScenario(LoanScenario scenario) {
        scenarios.remove(scenario);
        updateBestDeal();
    }

    /**
     * Get all scenarios
     */
    public List<LoanScenario> getAllScenarios() {
        return new ArrayList<>(scenarios);
    }

    /**
     * Get scenario count
     */
    public int getScenarioCount() {
        return scenarios.size();
    }

    /**
     * Clear all scenarios
     */
    public void clearScenarios() {
        scenarios.clear();
    }

    /**
     * Calculate metrics for a scenario
     */
    public void calculateScenario(LoanScenario scenario) {
        scenario.calculateMetrics();
        updateBestDeal();
    }

    /**
     * Find and mark the best deal (lowest total cost)
     */
    public LoanScenario findBestDeal() {
        if (scenarios.isEmpty()) {
            return null;
        }

        // Reset all best deal flags
        scenarios.forEach(s -> s.setBestDeal(false));

        // Find scenario with lowest total cost
        LoanScenario best = scenarios.stream()
            .min(Comparator.comparingDouble(LoanScenario::getTotalCost))
            .orElse(null);

        if (best != null) {
            best.setBestDeal(true);
        }

        return best;
    }

    /**
     * Update best deal after any change
     */
    private void updateBestDeal() {
        findBestDeal();
    }

    /**
     * Get scenario by index
     */
    public LoanScenario getScenario(int index) {
        if (index >= 0 && index < scenarios.size()) {
            return scenarios.get(index);
        }
        return null;
    }

    /**
     * Update scenario at index
     */
    public void updateScenario(int index, LoanScenario scenario) {
        if (index >= 0 && index < scenarios.size()) {
            scenario.calculateMetrics();
            scenarios.set(index, scenario);
            updateBestDeal();
        }
    }

    /**
     * Get savings compared to worst deal
     */
    public double getSavingsFromBestDeal() {
        if (scenarios.size() < 2) {
            return 0;
        }

        double maxCost = scenarios.stream()
            .mapToDouble(LoanScenario::getTotalCost)
            .max()
            .orElse(0);

        double minCost = scenarios.stream()
            .mapToDouble(LoanScenario::getTotalCost)
            .min()
            .orElse(0);

        return maxCost - minCost;
    }

    /**
     * Sort scenarios by total cost (ascending)
     */
    public List<LoanScenario> getSortedByTotalCost() {
        return scenarios.stream()
            .sorted(Comparator.comparingDouble(LoanScenario::getTotalCost))
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Sort scenarios by monthly payment (ascending)
     */
    public List<LoanScenario> getSortedByMonthlyPayment() {
        return scenarios.stream()
            .sorted(Comparator.comparingDouble(LoanScenario::getMonthlyPayment))
            .collect(java.util.stream.Collectors.toList());
    }
}
