package application;

/**
 * Represents a 3D printer with its attributes.
 */
public class Printer {
    private String nickname;
    private double powerConsumption; // kWh per hour
    private double printTimePerUnit; // hours per unit
    private double nozzleSize; // mm
    private int bedCapacity; // Maximum units per batch

    /**
     * Constructor for creating a printer object.
     */
    public Printer(String nickname, double powerConsumption, double printTimePerUnit, double nozzleSize, int bedCapacity) {
        this.nickname = nickname;
        this.powerConsumption = powerConsumption;
        this.printTimePerUnit = printTimePerUnit;
        this.nozzleSize = nozzleSize;
        this.bedCapacity = bedCapacity;
    }

    // Getters for printer attributes
    public String getNickname() { return nickname; }
    public double getPowerConsumption() { return powerConsumption; }
    public double getPrintTimePerUnit() { return printTimePerUnit; }
    public double getNozzleSize() { return nozzleSize; }
    public int getBedCapacity() { return bedCapacity; }

    /**
     * Returns a formatted string representing the printer's attributes.
     */
    @Override
    public String toString() {
        return nickname + "," + powerConsumption + "," + printTimePerUnit + "," + nozzleSize + "," + bedCapacity;
    }
}
