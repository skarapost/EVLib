package evlib.sources;

import java.util.ArrayList;

public class Hydroelectric extends EnergySource {
    /**
     * Constructs a new Hydroelectric instance, with no energy packages.
     */
    public Hydroelectric() { }

    /**
     * Constructor of a new Hydroelectric object with attached energy packages.
     * @param energyAmoun An array with energy packages.
     */
    public Hydroelectric(double[] energyAmoun) {
        super(energyAmoun);
    }
}
