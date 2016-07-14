package PlanetaryDefense.PD.wiki;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.action.index.IndexRequest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class MediaWiki {
	//String baseUrl = "http://localhost/mediawiki/";
	String baseUrl = "http://199.26.254.186/pdwiki/";
	List<String> m_cookies = new ArrayList<String>();

	public void addPages(String filename) {
		List<String> words;
		try {
			// get edit token
			String csrftoken = this.getEditTokens();
			csrftoken = csrftoken.substring(1, csrftoken.length() - 2);
			words = this.loadwords(filename);
			// add page
			for (int i = 0; i < words.size(); i++) {
				this.editpage(csrftoken, words.get(i));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void changePwd(String user, String password) {
		String cmdline = "/usr/bin/php changePassword.php --user " + user + " --password " + password;
		String directory = "/var/www/html/pdwiki/maintenance";
		ArrayList<String> output = command(cmdline, directory);
		if (null == output)
			System.out.println("\n\n\t\tCOMMAND FAILED: " + cmdline);
		else
			for (String line : output)
				System.out.println(line);
	}

	private void editpage(String editToken, String title) throws IOException {

		String urlParameters = "action=edit" + "&title=" + title + "&appendtext=[[Category:VocabularyList]]"
				+ "&format=json" + "&token=" + URLEncoder.encode(editToken);
		byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
		int postDataLength = postData.length;
		String urlname = baseUrl + "api.php";
		URL url = new URL(urlname);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", "" + postDataLength);
		connection.setRequestProperty("Content-Language", "en-US");
		String cookieStr = String.join(";", m_cookies);
		System.out.println(cookieStr);
		connection.setRequestProperty("Cookie", cookieStr);
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
			wr.write(postData);
		}
		
		// Get Response
		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuffer buffer = new StringBuffer();
		while ((line = rd.readLine()) != null) {
			buffer.append(line);
		}
		rd.close();

		String result = buffer.toString();
		System.out.println(result);
	}
	
	private String getEditTokens() throws UnsupportedEncodingException, IOException {

		String params3 = "?format=json&action=query&meta=tokens";
		String uri = baseUrl + "/api.php" + params3;
		URL url = new URL(uri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		String cookieStr = String.join(";", m_cookies);
		connection.setRequestProperty("Cookie", cookieStr);
		InputStream stream = connection.getInputStream();

		int b;
		StringBuilder stringBuilder = new StringBuilder();
		JsonObject jsonObject = new JsonObject();
		while ((b = stream.read()) != -1) {
			stringBuilder.append((char) b);
		}

		connection.disconnect();

		JsonParser parser = new JsonParser();
		JsonObject jsonData = parser.parse(stringBuilder.toString()).getAsJsonObject();
		JsonObject tokens = (JsonObject) ((JsonObject) jsonData.get("query")).get("tokens");
		JsonElement csrftoken = tokens.get("csrftoken");

		return csrftoken.toString();
	}

	private List<String> loadwords(String inputFileName) throws IOException {
		
		List<String> words = new ArrayList<String>();
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(inputFileName));
        //loader.setOptions(new String[] {"-S", "first"});  // if the first attribute is a string, numAttributes still equals numValues, but the first one is replaced by 0, so the index should start with 1
        Instances data = loader.getDataSet();
    
		for(int i =0; i<data.numInstances();i++)
		{
			String word = data.instance(i).stringValue(0);
			words.add(word);
		}
		
		return words;
	}
	
	/** Returns null if it failed for some reason.
     */
    private static ArrayList<String> command(final String cmdline,
    final String directory) {
        try {
            Process process = 
                new ProcessBuilder(new String[] {"bash", "-c", cmdline})
                    .redirectErrorStream(true)
                    .directory(new File(directory))
                    .start();

            ArrayList<String> output = new ArrayList<String>();
            BufferedReader br = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            String line = null;
            while ( (line = br.readLine()) != null ){
				//System.out.println(line);
				output.add(line);
			}
               
            //There should really be a timeout here.
            if (0 != process.waitFor())
                return null;

            return output;

        } catch (Exception e) {
            //Warning: doing this is no good in high quality applications.
            //Instead, present appropriate error messages to the user.
            //But it's perfectly fine for prototyping.
            return null;
        }
    }

	public static void main(String[] args) throws IOException, InterruptedException {
		long startTime = System.currentTimeMillis();
		MediaWiki wiki = new MediaWiki();
		// login example
	
		//get edit token
		String csrftoken = wiki.getEditTokens();
		csrftoken = csrftoken.substring(1, csrftoken.length() - 2);
		
		//add page
		String filename = "D:/PD/100PDconcepts.csv";
		List<String> words = wiki.loadwords(filename);
	
		for(int i=0; i<words.size(); i++){
			wiki.editpage(csrftoken, words.get(i));
		}
		
		
		long endTime = System.currentTimeMillis();
		System.out.println("Preprocessing is done!" + "Time elapsed： " + (endTime - startTime) / 1000 + "s");
	}
}
