package vrpsim.core.model.events;

import java.util.List;

public interface IEventStrategy {

	/**
	 * Returns all {@link IEventType}'s the strategy can create.
	 * 
	 * @return
	 */
	public List<IEventType> getEventTypes();
	
}
