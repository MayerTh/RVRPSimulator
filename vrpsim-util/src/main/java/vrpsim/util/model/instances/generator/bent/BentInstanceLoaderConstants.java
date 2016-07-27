/**
 * Copyright (C) 2016 Thomas Mayer (thomas.mayer@unibw.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vrpsim.util.model.instances.generator.bent;

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
