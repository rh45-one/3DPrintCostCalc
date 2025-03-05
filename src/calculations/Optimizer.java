package calculations;

import application.Printer;
import java.util.*;

public class Optimizer {

    public static Map<Printer, Integer> optimizeDistribution(int totalUnits, List<Printer> printers) {
        Map<Printer, Integer> distribution = new LinkedHashMap<>();

        // Initialize distribution
        for (Printer p : printers) {
            distribution.put(p, 0);
        }

        for (int i = 0; i < totalUnits; i++) {
            Printer bestPrinter = null;
            double bestTime = Double.MAX_VALUE;

            // Find the printer that would have the earliest completion time after adding this unit
            for (Printer p : printers) {
                int currentAssigned = distribution.get(p);
                int newAssigned = currentAssigned + 1;
                int bedCapacity = p.getBedCapacity();
                double batches = Math.ceil((double) newAssigned / bedCapacity);
                double newCompletionTime = batches * p.getPrintTimePerUnit();

                if (newCompletionTime < bestTime) {
                    bestTime = newCompletionTime;
                    bestPrinter = p;
                }
            }

            if (bestPrinter == null) {
                // This case should not occur as long as printers list is non-empty
                break;
            }

            // Assign the unit to the best printer found
            distribution.put(bestPrinter, distribution.get(bestPrinter) + 1);
        }

        return distribution;
    }
}