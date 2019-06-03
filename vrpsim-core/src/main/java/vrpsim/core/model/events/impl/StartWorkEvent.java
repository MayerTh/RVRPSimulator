package vrpsim.core.model.events.impl;

import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventOwner;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.simulator.IClock;

public class StartWorkEvent implements IEvent {
	
	private final IEventOwner owner;
	private final IEventType type;
	private final Integer priority;
	private final Double relativeTimeTillOccurence;
	private Double simulationtimeTillOccurence;

	public StartWorkEvent(IEventOwner owner, IEventType type, Integer priority, Double relativeTimeTillOccurence) {
		this.owner = owner;
		this.type = type;
		this.priority = priority;
		this.relativeTimeTillOccurence = relativeTimeTillOccurence;
	}

	@Override
	public Double getRelativeTimeTillOccurrence() {
		return this.relativeTimeTillOccurence;
	}

	@Override
	public void setSimulationTimeOfOccurrence(IClock clock) {
		this.simulationtimeTillOccurence = clock.getCurrentSimulationTime() + this.relativeTimeTillOccurence;
	}

	@Override
	public Double getSimulationTimeOfOccurence() {
		return this.simulationtimeTillOccurence;
	}

	@Override
	public Integer getPriority() {
		return this.priority;
	}

	@Override
	public IEventType getType() {
		return this.type;
	}

	@Override
	public IEventOwner getOwner() {
		return this.owner;
	}
}
