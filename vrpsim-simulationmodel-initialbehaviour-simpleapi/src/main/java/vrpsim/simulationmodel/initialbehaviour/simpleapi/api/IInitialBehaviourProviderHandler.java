package vrpsim.simulationmodel.initialbehaviour.simpleapi.api;

import java.util.List;

import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.CustomerAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DepotAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DriverAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.TourAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.VehicleAPI;

/**
 * Depot kann nur beladen, Customer nur entladen (beliebig viel)
 * Depot lädt vehicle voll (alternative: Depot läd so viel auf, wie bei load angegeben (macht Auto nicht zwangsläufig voll))
 * keine verschiedenen Fahrertypen
 * kein verzögertes Starten der Tour
 * load wird zu int gecastet
 * es wird nur ein Typ von Material berücksichtigt
 * eine Order pro Customer
 * jedes Vehicle darf nur eine Tour bekommen!
 * erstellt werden müssen Touren, mit einer Job-Reihenfolge und einem zugewiesenem Fahrzeug
 * valide Tour: Beginn und Ende mit Depot, Fahrzeug nicht überladen, jedes Fahrzeug genau einer Tour zugewiesen
 * Customer kann keine dynamischen und statischen Anfragen haben
 */

public interface IInitialBehaviourProviderHandler {

	/**
	 * @param vehicles 
	 * @param customers
	 * @return
	 */
	public List<TourAPI> handleOrder(List<VehicleAPI> vehicles, List<CustomerAPI> customers, List<DepotAPI> depots, List<DriverAPI> drivers);

}
