package evlib.sources;

public class Wind extends EnergySource
{
    /**
     * Creates a new Wind object with no energy packages.
     */
    public Wind() { }

    /**
     * Constructor of a new Wind instance attached with energy packages.
     * @param energyAmoun An array with energy packages.
     */
    public Wind(double[] energyAmoun) {
        super(energyAmoun);
    }
}
