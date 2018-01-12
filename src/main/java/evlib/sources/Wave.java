package evlib.sources;

public class Wave extends EnergySource {
    /**
     * Constructs a new Wave object.
     */
    public Wave() { }

    /**
     * Constructor of a new Wave instance attached with energy packages.
     * @param energyAmoun An array with energy packages.
     */
    public Wave(final double[] energyAmoun) {
        super(energyAmoun);
    }
}
