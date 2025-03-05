package calculations;

import application.Printer;
import java.util.*;

/**
 * Optimizes the distribution of print units across available printers.
 */
public class Optimizer {

    /**
     * Distributes print units efficiently among available printers.
     * Prioritizes printers with lower power consumption.
     */
    public static Map<Printer, Integer> optimizeDistribution(int totalUnits, List<Printer> printers) {
        Map<Printer, Integer> distribution = new LinkedHashMap<>();
        printers.sort(Comparator.comparingDouble(Printer::getPowerConsumption)); // Prioritize efficient printers

        int remainingUnits = totalUnits;
        while (remainingUnits > 0) {
            for (Printer printer : printers) {
                int unitsToAssign = Math.min(remainingUnits, printer.getBedCapacity());
                distribution.put(printer, distribution.getOrDefault(printer, 0) + unitsToAssign);
                remainingUnits -= unitsToAssign;
                if (remainingUnits <= 0) break;
            }
        }
        return distribution;
    }
}
