package calculations;

import application.Printer;
import java.util.Map;

/**
 * Handles cost calculations for 3D printing.
 */
public class CostCalculator {

    /**
     * Calculates the total material cost, applying a discount if applicable.
     */
    public static double calculateMaterialCost(int totalUnits, double materialPerUnit, double materialCostPerKg, boolean hasDiscount, double discountRate) {
        double totalMaterialKg = (totalUnits * materialPerUnit) / 1000; // Convert grams to kilograms
        double materialCost = totalMaterialKg * materialCostPerKg;
        return hasDiscount ? materialCost * (1 - discountRate) : materialCost;
    }

    /**
     * Calculates total energy cost based on printers' power consumption and print time.
     */
    public static double calculateEnergyCost(Iterable<Printer> printers, double energyCostPerKwh, Map<Printer, Integer> distribution) {
        double totalEnergyCost = 0;
        for (Printer printer : printers) {
            int units = distribution.getOrDefault(printer, 0);
            double printerTime = units * printer.getPrintTimePerUnit();
            totalEnergyCost += printerTime * printer.getPowerConsumption() * energyCostPerKwh;
        }
        return totalEnergyCost;
    }

    /**
     * Calculates the total cost of material and energy combined.
     */
    public static double calculateTotalCost(double materialCost, double energyCost) {
        return materialCost + energyCost;
    }

    /**
     * Calculates the total cost including commission per unit.
     */
    public static double calculateTotalCostWithCommission(double totalCost, double commissionPerUnit, int totalUnits) {
        return totalCost + (commissionPerUnit * totalUnits);
    }
}
