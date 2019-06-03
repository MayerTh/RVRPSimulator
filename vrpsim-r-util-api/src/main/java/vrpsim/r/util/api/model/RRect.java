package vrpsim.r.util.api.model;

public class RRect {

	private final RPoint buttonLeft;
	private final RPoint topRight;

	public RPoint getButtonLeft() {
		return buttonLeft;
	}

	public RPoint getTopRight() {
		return topRight;
	}

	public RRect(RPoint buttonLeft, RPoint topRight) {
		super();
		this.buttonLeft = buttonLeft;
		this.topRight = topRight;
	}

}
