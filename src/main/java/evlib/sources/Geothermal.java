package evlib.sources;

public class Geothermal extends EnergySource
{
    /**
     * Constructs a new Geothermal instance with no energy packages.
     */
    public Geothermal() { }

    /**
     * Constructor of a new Geothermal object with attached energy packages.
     * @param energyAmoun An array with energy packages.
     */
    public Geothermal(double[] energyAmoun) {
        super(energyAmoun);
    }
}
