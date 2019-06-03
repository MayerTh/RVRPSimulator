package vrpsim.simulationmodel.dynamicbehaviour.impl.usutil;

public class OpNode {

	private Integer value;
	private OpNode next;
	private OpNode nextOld;
	private OpNode previous;
	private OpNode previousOld;

	public OpNode(Integer value) {
		super();
		this.value = value;
	}

	public void revert() {
		OpNode tmp = this.next;
		this.next = this.previous;
		this.previous = tmp;
	}

	// public void revert(OpNode to) {
	// OpNode tmp = this.next;
	// this.next = this.previous;
	// this.previous = tmp;
	// }

	public void insert(OpNode to) {
		this.nextOld = this.next;
		this.next = to;
		to.setPreviousOld(to.getPrevious());
		to.setPrevious(this);
	}

	// public void delete(OpNode to) {
	// to.setPrevious(null);
	// this.next = null;
	// }

	public void deleteTo() {
		if (this.next != null) {
			this.getNext().setPrevious(null);
			this.next = null;
		}
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public OpNode getNext() {
		return next;
	}

	public void setNext(OpNode next) {
		this.next = next;
	}

	public OpNode getPrevious() {
		return previous;
	}

	public void setPrevious(OpNode previous) {
		this.previous = previous;
	}

	public OpNode getNextOld() {
		return nextOld;
	}

	public void setNextOld(OpNode nextOld) {
		this.nextOld = nextOld;
	}

	public OpNode getPreviousOld() {
		return previousOld;
	}

	public void setPreviousOld(OpNode previousOld) {
		this.previousOld = previousOld;
	}

	@Override
	public String toString() {
		String next = this.next == null ? "null" : this.next.getValue() + "";
		String previous = this.previous == null ? "null" : this.previous.getValue() + "";
		String nextOld = this.nextOld == null ? "null" : this.nextOld.getValue() + "";
		String previousOld = this.previousOld == null ? "null" : this.previousOld.getValue() + "";
		return "v(" + this.value + ")[next=v(" + next + ")nextOld=v(" + nextOld + "),previous=v(" + previous + ")previousOld=v("
				+ previousOld + ")]";
	}

}
