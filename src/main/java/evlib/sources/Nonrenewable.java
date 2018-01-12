package evlib.sources;

public class Nonrenewable extends EnergySource{

    /**
     * Creates a new Nonrenewable object with no energy packages.
     */
    public Nonrenewable() { }

    /**
     * Constructor of a new Nonrenewable object attached with energy packages.
     * @param energyAmoun An array with energy packages.
     */
    public Nonrenewable(final double[] energyAmoun) {
        super(energyAmoun);
    }
}
