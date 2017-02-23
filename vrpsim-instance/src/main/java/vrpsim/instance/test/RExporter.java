package vrpsim.instance.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RExporter {

	public void exportTo(String folder, String name, String titel, List<InstanceDegreeContainer> containers) throws IOException {
		
		File file = new File(folder);
		if(!file.exists()) {
			file.mkdirs();
		}
		
//		Collections.sort(containers);
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(folder + File.separatorChar + name + ".r"))); 
		
		bw.write("X <- c(1:"+containers.size()+")");
		bw.newLine();
		bw.write("dodY <- c(");
		for(int i = 0; i < containers.size();i++) {
			bw.write(containers.get(i).getDod() + "");
			if(i < containers.size() -1) {
				bw.write(",");
			}
		}
		bw.write(")");
		bw.newLine();

		
		bw.write("edodY <- c(");
		for(int i = 0; i < containers.size();i++) {
			bw.write(containers.get(i).getEdod() + "");
			if(i < containers.size() -1) {
				bw.write(",");
			}
		}
		bw.write(")");
		bw.newLine();
		
		bw.write("dedodY <- c(");
		for(int i = 0; i < containers.size();i++) {
			bw.write(containers.get(i).getD_edod() + "");
			if(i < containers.size() -1) {
				bw.write(",");
			}
		}
		bw.write(")");
		bw.newLine();
		
		bw.write("wedodY <- c(");
		for(int i = 0; i < containers.size();i++) {
			bw.write(containers.get(i).getW_edod() + "");
			if(i < containers.size() -1) {
				bw.write(",");
			}
		}
		bw.write(")");
		bw.newLine();
		
		bw.write("plot(X, dodY, type=\"l\", main=\""+titel+"\", ylim=c(0,1.2), col=\"green\", xlab=\"DVRP Instances\", ylab=\"Degree\")");
		bw.newLine();
		bw.write("lines(X, edodY, col=\"red\")");
		bw.newLine();
		bw.write("lines(X, dedodY, col=\"blue\")");
		bw.newLine();
		bw.write("lines(X, wedodY, col=\"black\")");
		bw.newLine();
		bw.write("legend(0,1.2,c(\"DOD\", \"EDOD\", \"D-DOD\", \"W-DOD\"), col=c(\"green\", \"red\", \"blue\", \"black\"), lty=1)");
		
		bw.close();
	}
	
}
