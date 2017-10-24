# EVLib
EVLib is a library for the management and the simulation of EV activities in a charging station level which makes use of a set of available energy sources. It is implemented in the JAVA programming language, and its main goal is to manage the charging, discharging, battery swap functions and parking/charging inductively and support their integration into a single charging station. The library supports a large number of functions to properly manage EV-related activities. There are four main functions, as well as a number of secondary ones.

## Compilation
The library has to be compiled using Maven.

## Main Functions

### Charging: 
There are 2 types of charging depending on the ratio, namely the fast and the slow charging.

### DisCharging: 
Similar to a charging event, a discharging event first demands the pre-processing phase where a quest for an empty dis-charger is made.

### Battery Exchange: 
The pre-processing phase requires for a battery with enough range to be available in the charging station. If such a battery is found, the battery is swapped into the EV.

### Parking Event:
The vehicle can either simply park or charging inductively. The pre-processing phase looks for an empty parking slot. If the parking event requires energy as well, the parking slot needs to have enabled the switch for inductive charging. The next check is for energy. The waiting list is not supported in this operation.

## Extra Functions
The library also supports a number of secondary functions: The creation of a charging station, as well as the creation and integration of a charger, dis-charger, battery swapper or parking slot in the station. Additional operations are the recharging of batteries which are later to be swapped into EVs, as well as the ability to add new batteries to the storage in order seamless operation of the battery exchange process to be achieved. The total cost of the charging, discharging and battery swapping can be calculated based on a series of costs (e.g., energy cost) defined by the user. During the creation of the charging station, 4 waiting lists are created. A list for the charging events which want fast charging, a list for the charging events which want slow charging, a list for the discharging events, and a list for the vehicles waiting for battery exchange.

## Example
```
  String[] kinds = { "slow", "fast", "fast", "slow" };
  String[] sources = { "geothermal", "nonrenewable", "wind", "wave" };
  double[][] energyAm = new double[4][ 5];
  for (int i = 0; i<4; i++)
      for (int j = 0; j<5; j++)
          energyAm [i][j] = 1500;
  
  ChargingStation station = new ChargingStation("Miami", kinds, sources, energyAm);
  
  //Creation of a Charger, a DisCharger and an ExchangeHandler
  DisCharger dsc = new DisCharger(station);
  ExchangeHandler handler = new ExchangeHandler(station);
  ParkingSlot slot = new ParkingSlot(station);
  
  station.addExchangeHandler(handler);
  station.addDisCharger(dsc);
  station.addParkingSlot(slot);
  
  //Sets the space between every update in milliseconds.
  station.setAutomaticUpdateMode(true);
  station.setUpdateSpace(10000);
  
  station.setTimeOfExchange(5000);
  
  station.setChargingRatioFast(0.01);
  station.setDisChargingRatio(0.1);
  station.setInductiveChargingRatio(0.001);
  
  station.setUnitPrice(5);
  station.setDisUnitPrice(5);
  station.setInductivePrice(3);
  station.setExchangePrice(20);
  
  Driver a = new Driver("Tom");
  Driver b = new Driver("Ben");
  
  ElectricVehicle vec1 = new ElectricVehicle("Honda");
  ElectricVehicle vec2 = new ElectricVehicle("Toyota");
  ElectricVehicle vec3 = new ElectricVehicle("Mitsubishi");
  ElectricVehicle vec4 = new ElectricVehicle("Fiat");
  ElectricVehicle vec5 = new ElectricVehicle("BMW");
  
  Battery bat1 = new Battery(1500, 5000);
  Battery bat2 = new Battery(2000, 6000);
  Battery bat3 = new Battery(2500, 6000);
  Battery bat4 = new Battery(800, 3000);
  Battery bat5 = new Battery(0, 800);
  Battery bat6 = new Battery(500, 9000);
  
  //Links a battery with a charging station for the exchange battery function
  station.joinBattery(bat4);
  
  vec1.setDriver(a);
  vec1.setBattery(bat1);
  vec2.setDriver(a);
  vec2.setBattery(bat2);
  vec3.setDriver(a);
  vec3.setBattery(bat3);
  vec4.setDriver(b);
  vec4.setBattery(bat5);
  vec5.setDriver(b);
  vec5.setBattery(bat6);
  
  //ChargingEvent
  ChargingEvent ev1 = new ChargingEvent(station, vec1, 300, "fast");
  ChargingEvent ev2 = new ChargingEvent(station, vec2, 600, "fast");
  ChargingEvent ev3 = new ChargingEvent(station, vec3, 200, "fast");
  ChargingEvent ev5 = new ChargingEvent(station, vec1, 300, "fast");
  
  //Exchange event
  ChargingEvent ev7 = new ChargingEvent(station, vec4);
  
  //DisChargingEvent
  DisChargingEvent ev4 = new DisChargingEvent(station, vec1, 500);
  DisChargingEvent ev6 = new DisChargingEvent(station, vec1, 800);
  
  ParkingEvent ev8 = new ParkingEvent(station, vec5, 20000, 200);
  
  //Sets the maximum time a vehicle can wait in milliseconds
  ev3.setWaitingTime(500000);
  ev5.setWaitingTime(1200000);
  ev6.setWaitingTime(450000);
  
  //Call of pre-processing and execution methods for each event 
  ev1.preProcessing();
  ev1.execution();
  
  ev2.preProcessing();
  ev2.execution();
          
  ev3.preProcessing();
  ev3.execution();
          
  ev4.preProcessing();
  ev4.execution();
          
  ev5.preProcessing();
  ev5.execution();
          
  ev6.preProcessing();
  ev6.execution();
          
  ev7.preProcessing();
  ev7.execution();
          
  ev8.preProcessing();
  ev8.execution();
  
  //Extraction of a report for the station
  station.generateReport("~/report.txt")
  
```
