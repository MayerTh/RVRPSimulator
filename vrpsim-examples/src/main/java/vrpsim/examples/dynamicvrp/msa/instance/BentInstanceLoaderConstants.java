package vrpsim.examples.dynamicvrp.msa.instance;

public class BentInstanceLoaderConstants {
	
	/*
	 * First line 
	 * Customers:	100	Capacity:	200	Time Bins	4 	Vehicles	16
	 * 
	 * 
	 * Header for request overview with one example line
	 * Cust	X	Y	Demand	Start	End	Service	PreBin	0-160	160-320	320-480
	 * D	40	50	0	0	240	0	0	0	0	0
	 *
	 *
	 * Header for known requests with one example line
	 * Known Requests *number requests*
	 * Cust	Arrival	Start	Deadline	Demand	Service
	 * 1	-1	50	80		30	10
	 * 
	 * 
	 * Header for unknown requests with one example line
	 * Unknown Requests 37
	 * Cust	Arrival	Start	Deadline	Demand	Service
	 * 30	0	61	91		20	10
	 */
	
	public static String START_KNOWN_REQUESTS = "Known Requests";
	public static String START_UNKNOWN_REQUESTS = "Unknown Requests";
	
	public static String TEABLE_HEADER_CUSTOMER = "Cust";
	public static String DEPOT_INDEX = "D";
	
	public static String TEABLE_HEADER_LONG = "Cust	X	Y	Demand	Start	End	Service	PreBin	0-160	160-320	320-480";
	public static String TEABLE_HEADER_SHORT = "Cust	Arrival	Start	Deadline	Demand	Service";
	
}
