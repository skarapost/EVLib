# EVLib
EVLib is a library for the management and the simulation of EV activities in a charging station level which makes use of a set of available energy sources. It is implemented in the JAVA programming language, and its main goal is to manage the charging, discharging and battery swap functions and support their integration into a single charging station. The library supports a large number of functions to properly manage EV-related activities. There are three main functions, as well as a number of secondary ones, while each function is executed in 2 phases, namely the pre-processing and the execution.

## Compilation
The library has to be compiled using Maven.

## Main Functions

### Charging: 
There are 2 types of charging depending on the charging time, namely the fast and the slow charging. The execution of a charging event requires first the pre-processing phase where a quest for an empty charger and available energy is performed.

### DisCharging: 
Similarly to a charging event, a discharging event first demands the pre-processing phase where a quest for an empty dis-charger is made.

### Battery Exchange: 
The pre-processing phase requires for a battery with enough range to be available in the charging station. If such a battery is found, the battery exchange function can be called and the battery is swapped into the EV.

## Extra Functions
The library also supports a number of secondary functions: The creation of a charging station, as well as the creation and integration of a charger, dis-charger or battery swapper in the station. Additional operations are the recharging of batteries which are later to be swapped into EVs, as well as the ability to add new batteries to the storage in order seamless operation of the battery exchange process to be achieved. The total cost of the charging, discharging and battery swapping can be calculated based on a series of costs (e.g., energy cost) defined by the user. During the creation of the charging station, 4 waiting lists are created. A list for the charging events which want fast charging, a list for the charging events which want slow charging, a list for the discharging events, and a list for the vehicles waiting for battery exchange.

## Examples
```
  String[] kinds = new string[4] { "slow", "fast", "fast", "slow" };
  String[] sources = new string[4] { "geothermal", "nonrenewable", "wind", "wave" };
  float[][] energyAm = new float[4][ 5];
  for (int i = 0; i<4; i++)
      for (int j = 0; j<5; j++)
        energyAm [i][j] = 150;
  
  ChargingStation station = new ChargingStation("Miami", kinds, sources, energyAm);
  DisCharger dsc = new DisCharger(station);
  ExchangeHandler handler = new ExchangeHandler(station);
	
  station.insertExchangeHandler(handler);

  station.insertDisCharger(dsc);

  //Sets the space between every update in milliseconds.
  station.setUpdateSpace(10000);
  
  station.setChargingRatioFast(0.01);
  station.setDisChargingRatio(0.1);

  //Sets the duration of a battery exchange in milliseconds
  station.setTimeofExchange(5000);

  Driver a = new Driver("Tom");

  ElectricVehicle vec1 = new ElectricVehicle("Honda", 1950);
  ElectricVehicle vec2 = new ElectricVehicle("Toyota", 1400);
  ElectricVehicle vec3 = new ElectricVehicle("Mitsubishi", 1500);
  ElectricVehicle vec4 = new ElectricVehicle("Fiat", 1600);

  Battery bat1 = new Battery(1500, 5000);
  Battery bat2 = new Battery(2000, 6000);
  Battery bat3 = new Battery(2500, 6000);
  Battery bat4 = new Battery(800, 3000);
  Battery bat5 = new Battery(0, 800);

  //Links a battery with a charging station for the exchange battery function
  station.joinBattery(bat4);

  vec1.setDriver(a);
  vec1.vehicleJoinBattery(bat1);
  vec2.setDriver(a);
  vec2.vehicleJoinBattery(bat2);
  vec3.setDriver(a);
  vec3.vehicleJoinBattery(bat3);
  vec4.setDriver(a);
  vec4.vehicleJoinBattery(bat5);

  ChargingEvent ev1 = new ChargingEvent(station, vec1, 300, "fast");
  ChargingEvent ev2 = new ChargingEvent(station, vec2, 600, "fast");
  ChargingEvent ev3 = new ChargingEvent(station, vec3, 200, "fast");
  ChargingEvent ev5 = new ChargingEvent(station, vec1, 300, "fast");
  ChargingEvent ev7 = new ChargingEvent(station, vec4, "exchange");

  DisChargingEvent ev4 = new DisChargingEvent(station, vec1, 500);
  DisChargingEvent ev6 = new DisChargingEvent(station, vec1, 800);

  //Sets the maximum time a vehicle can wait in milliseconds
  ev3.setWaitingTime(50000);
  ev5.setWaitingTime(120000);
  ev6.setWaitingTime(450000);

  ev1.execution();

  ev2.execution();

  ev3.execution();

  ev4.execution();

  ev5.execution();

  ev6.execution();

  ev7.execution();
  
```
