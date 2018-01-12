package evlib.sources;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class EnergySource
{
    private int id;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private final ArrayList<Double> energyAmount;

    /**
     * Constructor of a new EnergySource object without energy packages.
     */
    public EnergySource() {
        this.id = idGenerator.incrementAndGet();
        this.energyAmount = new ArrayList();
    }

    /**
     * Constructor of EnergySource with attached energy packages.
     * @param energyAmoun An array with energy packages.
     */
    public EnergySource(final double[] energyAmoun) {
      this.id = idGenerator.incrementAndGet();
      this.energyAmount = new ArrayList<>();
      for (double anEnergyAmoun : energyAmoun)
          energyAmount.add(anEnergyAmoun);
    }

    /**
     * @return The first package of energy to be given to the EnergySource.
     */
    public double popAmount() {
         if ((energyAmount == null) || (energyAmount.size() == 0))
             return 0;
         else
             return energyAmount.remove(0);
    }

    /**
     * Inserts a package of energy to the queue for update of the storage.
     * @param am The package's amount of energy.
     */
    public void insertAmount(final double am) {
         energyAmount.add(am);
    }

    /**
     * @return The id of the EnergySource.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id for the EnergySource.
     * @param d The id to be set.
     */
    public void setId(final int d) {
      this.id = d;
    }
}
