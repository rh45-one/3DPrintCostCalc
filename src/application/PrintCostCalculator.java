package application;

import calculations.CostCalculator;
import calculations.Optimizer;
import java.io.*;
import java.util.*;

/**
 * The main class for handling user interaction and controlling the 3D Print Cost Calculator.
 * It provides a CLI-based menu where users can input their settings, add printers, and calculate costs.
 */
public class PrintCostCalculator {
    private static Scanner scanner = new Scanner(System.in);
    private static List<Printer> printers = new ArrayList<>();
    private static int totalUnits;
    private static double materialPerUnit;
    private static double commissionPerUnit;
    private static double materialCostPerKg;
    private static boolean hasDiscount;
    private static double discountRate;
    private static double energyCostPerKwh;

    /**
     * Starts the interactive menu for the program.
     */
    public static void startMenu() {
        while (true) {
            System.out.println("\n3D Print Cost Calculator");
            System.out.println("1. Set Printing Parameters");
            System.out.println("2. Manage Printers");
            System.out.println("3. Calculate Costs");
            System.out.println("4. Export Settings");
            System.out.println("5. Import Settings");
            System.out.println("6. Exit");
            System.out.print("Select an option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine()); // Parse input safely
                switch (choice) {
                    case 1: setPrintingParameters(); break;
                    case 2: managePrinters(); break;
                    case 3: calculateCosts(); break;
                    case 4: exportSettings(); break;
                    case 5: importSettings(); break;
                    case 6: System.exit(0);
                    default: System.out.println("Invalid option. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Sets the printing parameters by asking the user for input.
     */
    private static void setPrintingParameters() {
        totalUnits = getValidInt("Enter the number of units to print: ");
        materialPerUnit = getValidDouble("Enter material required per unit (grams): ");
        commissionPerUnit = getValidDouble("Enter commission per unit ($): ");
        materialCostPerKg = getValidDouble("Enter material cost per Kg ($): ");

        System.out.print("Does the supplier provide a discount? (yes/no): ");
        hasDiscount = scanner.nextLine().trim().equalsIgnoreCase("yes");

        if (hasDiscount) {
            discountRate = getValidDouble("Enter discount percentage: ") / 100;
        }

        energyCostPerKwh = getValidDouble("Enter energy cost per kWh ($): ");
    }

    /**
     * Manages printer settings: adding, listing, or removing printers.
     */
    private static void managePrinters() {
        System.out.println("\n1. Add Printer");
        System.out.println("2. List Printers");
        System.out.println("3. Remove Printer");
        System.out.println("4. Back");
        System.out.print("Select an option: ");

        try {
            int option = Integer.parseInt(scanner.nextLine());
            switch (option) {
                case 1:
                    addPrinter();
                    break;
                case 2:
                    listPrinters();
                    break;
                case 3:
                    removePrinter();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    /**
     * Adds a new printer to the list.
     */
    private static void addPrinter() {
        System.out.print("Nickname: ");
        String nickname = scanner.nextLine();
        double powerConsumption = getValidDouble("Power consumption (kWh per hour): ");
        double printTimePerUnit = getValidDouble("Print time per unit (hours): ");
        double nozzleSize = getValidDouble("Nozzle size (mm): ");
        int bedCapacity = getValidInt("Bed capacity (units per batch): ");

        printers.add(new Printer(nickname, powerConsumption, printTimePerUnit, nozzleSize, bedCapacity));
        System.out.println("Printer added successfully.");
    }

    /**
     * Lists all printers in the system.
     */
    private static void listPrinters() {
        if (printers.isEmpty()) {
            System.out.println("No printers available.");
        } else {
            for (Printer printer : printers) {
                System.out.println(printer.getNickname() + " | " + printer.getPowerConsumption() + " kWh/h | " + printer.getPrintTimePerUnit() + "h/unit | " + printer.getNozzleSize() + "mm | " + printer.getBedCapacity() + " units/batch");
            }
        }
    }

    /**
     * Removes a printer by nickname.
     */
    private static void removePrinter() {
        System.out.print("Enter nickname of printer to remove: ");
        String toRemove = scanner.nextLine();
        printers.removeIf(p -> p.getNickname().equalsIgnoreCase(toRemove));
        System.out.println("Printer removed if it existed.");
    }

    /**
     * Performs cost calculations and displays the results.
     */
    private static void calculateCosts() {
        Map<Printer, Integer> distribution = Optimizer.optimizeDistribution(totalUnits, printers);
        double materialCost = CostCalculator.calculateMaterialCost(totalUnits, materialPerUnit, materialCostPerKg, hasDiscount, discountRate);
        double totalEnergyCost = CostCalculator.calculateEnergyCost(printers, energyCostPerKwh, distribution);
        double totalCost = CostCalculator.calculateTotalCost(materialCost, totalEnergyCost);
        double totalCostWithCommission = CostCalculator.calculateTotalCostWithCommission(totalCost, commissionPerUnit, totalUnits);

        System.out.println("\nOptimized Printer Distribution:");
        for (Map.Entry<Printer, Integer> entry : distribution.entrySet()) {
            Printer printer = entry.getKey();
            int units = entry.getValue();
            double printerEnergyCost = units * printer.getPrintTimePerUnit() * printer.getPowerConsumption() * energyCostPerKwh;
            System.out.printf("%s -> Units: %d, Energy Cost: $%.2f (Nozzle: %.2fmm)\n",
                    printer.getNickname(), units, printerEnergyCost, printer.getNozzleSize());
        }

        System.out.printf("Total Material Cost: $%.2f\nTotal Energy Cost: $%.2f\nTotal Production Cost: $%.2f\nTotal Cost with Commission: $%.2f\n",
                materialCost, totalEnergyCost, totalCost, totalCostWithCommission);
    }

    /**
     * Exports the current settings and printers to a file.
     */
    private static void exportSettings() {
        try (PrintWriter writer = new PrintWriter("settings.txt")) {
            writer.println(totalUnits + "," + materialPerUnit + "," + commissionPerUnit + "," + materialCostPerKg + "," + hasDiscount + "," + discountRate + "," + energyCostPerKwh);
            for (Printer p : printers) {
                writer.println(p);
            }
            System.out.println("Settings exported successfully.");
        } catch (Exception e) {
            System.out.println("Error exporting settings.");
        }
    }

    /**
     * Imports settings from a file.
     */
    private static void importSettings() {
        try (Scanner fileScanner = new Scanner(new File("settings.txt"))) {
            String[] data = fileScanner.nextLine().split(",");
            totalUnits = Integer.parseInt(data[0]);
            materialPerUnit = Double.parseDouble(data[1].replace(",", "."));
            commissionPerUnit = Double.parseDouble(data[2].replace(",", "."));
            materialCostPerKg = Double.parseDouble(data[3].replace(",", "."));
            hasDiscount = Boolean.parseBoolean(data[4]);
            discountRate = Double.parseDouble(data[5].replace(",", "."));
            energyCostPerKwh = Double.parseDouble(data[6].replace(",", "."));
            printers.clear();
            while (fileScanner.hasNextLine()) {
                String[] pData = fileScanner.nextLine().split(",");
                printers.add(new Printer(pData[0], Double.parseDouble(pData[1].replace(",", ".")), Double.parseDouble(pData[2].replace(",", ".")), Double.parseDouble(pData[3].replace(",", ".")), Integer.parseInt(pData[4])));
            }
            System.out.println("Settings imported successfully.");
        } catch (Exception e) {
            System.out.println("Error importing settings.");
        }
    }

    /**
     * Gets a valid double input, handling both "." and "," as decimal separators.
     */
    private static double getValidDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please try again.");
            }
        }
    }

    /**
     * Gets a valid integer input.
     */
    private static int getValidInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a whole number.");
            }
        }
    }
}
