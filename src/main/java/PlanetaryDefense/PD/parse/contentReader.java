package PlanetaryDefense.PD.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
public class contentReader {
	public String fileName = null;
	
	public contentReader(String fName){
		fileName = fName;
	}

	public Map<String, String> readMetadata() throws IOException{
		File file = new File(fileName);
		Map<String, String> meta_map = new HashMap<String, String>();
		//Parser method parameters
		Parser parser = new AutoDetectParser();
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream;
		inputstream = new FileInputStream(file);	
		ParseContext context = new ParseContext();

		try {
			parser.parse(inputstream, handler, metadata, context);
			//getting the list of all meta data elements 
			String[] metadataNames = metadata.names();

			for(String name : metadataNames) {	
				String name_low = null;
				name_low = name.toLowerCase();
				if(name_low.equals("author")||name_low.equals("title")){
					meta_map.put(name, metadata.get(name));
					//System.out.println(name + ": " + metadata.get(name));
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(handler.toString());
		inputstream.close();
		return meta_map;

		
	}
	
	public String readContent(){
		File file = new File(fileName);

		Tika tika = new Tika();
		tika.setMaxStringLength(5000);   //-1 unlimited

		String content = null;
		try {
			content = tika.parseToString(file);
			//content = content.replaceAll("[\r\n]+", "\n").replaceAll("\\s+", " ");
			//content = content.replaceAll("\\s+", " ");
			content = content.replaceAll("[\r\n]+", " ");

			//System.out.println("The Content: " + content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return content;
		  	
	}
	
	public static void main(String[] args) throws IOException, SAXException, TikaException {
		// TODO Auto-generated method stub
		contentReader cr = new contentReader("C:/Users/Yongyao/Desktop/good semantic papers/crowds_query_aug_GOOD paper.pdf");
		/*Map<String, String> map = cr.readMetadata();
		System.out.println(map);*/
		String str = cr.readContent();
		System.out.println(str);
		
	}

}
