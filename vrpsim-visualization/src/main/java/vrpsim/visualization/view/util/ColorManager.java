package vrpsim.visualization.view.util;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;
import vrpsim.core.model.behaviour.tour.ITour;

public class ColorManager {

	static ColorManager colorManager = new ColorManager();
	Color[] colors;
	Map<ITour, Color> colorTours = new HashMap<>();

	private ColorManager() {
		colors = new Color[] { Color.LIGHTBLUE, Color.LIGHTGREEN, Color.AQUA, Color.SIENNA, Color.CHARTREUSE, Color.ORANGE,
				Color.DARKVIOLET, Color.PAPAYAWHIP, Color.IVORY, Color.BLANCHEDALMOND, Color.WHITE, Color.YELLOW, Color.VIOLET,
				Color.SALMON, Color.SILVER, Color.SIENNA, Color.ROYALBLUE, Color.PURPLE, Color.MEDIUMSEAGREEN };
	}

	public static ColorManager get() {
		return colorManager;
	}

	public Color getColorFor(ITour tour) {
		if (!this.colorTours.containsKey(tour)) {
			int index = this.colorTours.size();
			if (index >= colors.length) {
				throw new RuntimeException(
						"Visualisation currently supports only 10 different colors for tours. So when you have more than 10 colors, this exaption will occur.");
			} else {
				this.colorTours.put(tour, colors[index]);
			}
		}
		return this.colorTours.get(tour);
	}

}
