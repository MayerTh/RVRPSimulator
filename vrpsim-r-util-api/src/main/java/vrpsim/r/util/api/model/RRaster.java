package vrpsim.r.util.api.model;

import java.util.List;

public class RRaster {

	private final List<RRect> rects;

	public RRaster(List<RRect> rects) {
		super();
		this.rects = rects;
	}

	public List<RRect> getRects() {
		return rects;
	}

}
