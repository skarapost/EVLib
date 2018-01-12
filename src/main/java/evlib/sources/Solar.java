package evlib.sources;

public class Solar extends EnergySource {
    /**
     * Creates a new Solar object with no energy packages.
     */
    public Solar() { }

    /**
     * Constructor of a new Solar object attached with energy packages.
     * @param energyAmoun An array with energy packages.
     */
    public Solar(final double[] energyAmoun) {
        super(energyAmoun);
    }
}
