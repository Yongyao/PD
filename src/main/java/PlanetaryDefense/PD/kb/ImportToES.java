package PlanetaryDefense.PD.kb;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.elasticsearch.action.index.IndexRequest;

import PlanetaryDefense.PD.driver.ESdriver;

public class ImportToES {

	public ImportToES() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		ESdriver esd = new ESdriver();
		esd.createBulkProcesser();

		File directory = new File("C:/Users/Yongyao/Desktop/reverb/dataoutput/");
		File[] fList = directory.listFiles();
		for (File file : fList) {
			BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
			try {
				String line = br.readLine();
				while (line != null) {	
					String info[] = line.split("&&");

					if(info.length==5 && !info[2].equals("you") && !info[2].equals("You") && !info[2].equals("it") &&!info[2].equals("It") ){
						if(info[2].length() < 30 && Float.parseFloat(info[0]) >0.5)
						{
						IndexRequest ir;
						try {
							ir = new IndexRequest("test", "kb").source(jsonBuilder()
									.startObject()
									.field("confidence", Float.parseFloat(info[0]))
									.field("context", info[1])
									.field("sub", info[2])
									.field("predicate", info[3])
									.field("obj", info[4])
									.endObject());
							esd.bulkProcessor.add(ir);
							//esd.closeES();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					}
					}

					line = br.readLine();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				br.close();
			}
		}
		esd.destroyBulkProcessor();
		esd.closeES();

	}

}
