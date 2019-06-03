package vrpsim.classificationadapter;

import java.util.HashSet;
import java.util.Set;

public class LabelUtil {

	public synchronized static String[] generateLableList(double[][] data) {
		Set<String> labels = new HashSet<>();
		String[] labelList = new String[data.length];
		for (int i = 0; i < data.length; i++) {
			String label = getLabel(data[i]);
			boolean labelIsUniqu = false;
			while (!labelIsUniqu) {
				if (!labels.contains(label)) {
					labelIsUniqu = true;
					labels.add(label);
					labelList[i] = label;
				} else {
					String[] parts = label.split("#");
					int index = Integer.valueOf(parts[1]) + 1;
					label = parts[0] + "#" + index;
				}
			}
		}
		return labelList;
	}
	
	public synchronized static String getLabel(double[] data) {
		String str = "(";
		for (int i = 0; i < data.length; i++) {
			str += data[i];
			if (i < data.length - 1) {
				str += "-";
			}
		}
		return str + ")#1";
	}
}
