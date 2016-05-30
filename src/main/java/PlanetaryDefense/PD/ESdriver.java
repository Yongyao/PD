package PlanetaryDefense.PD;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionFuzzyBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import PlanetaryDefense.PD.parse.contentReader;

public class ESdriver {

    String index = "files";
    String uploadedType = "uploadedInfo";
    
    String contentType = "contentAndMeta";
    public String vocabType = "vocabList";
    final Integer MAX_CHAR = 500;
	
	/*static Node node =
		    nodeBuilder()
		        .settings(ImmutableSettings.settingsBuilder().put("http.enabled", false))
		        .client(true)
		    .node();*/
	
	//important!!!  when deployed to VM
    static Settings settings = 
	System.getProperty("file.separator").equals("/") ? ImmutableSettings.settingsBuilder()
		.put("http.enabled", "false")
		.put("transport.tcp.port", "9300-9400")
		.put("discovery.zen.ping.multicast.enabled", "false")
		.put("discovery.zen.ping.unicast.hosts", "localhost")
		.build() : ImmutableSettings.settingsBuilder().put("http.enabled", false).build();

    static Node node = nodeBuilder().client(true).settings(settings).clusterName("elasticsearch").node();
	
    static Client client = node.client();
	
    public ESdriver(){
    	try {
			putMapping(index);
			putVocabMapping(index);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
	public void RefreshIndex(){
		node.client().admin().indices().prepareRefresh().execute().actionGet();
	}
	
//	static BulkProcessor bulkProcessor = BulkProcessor.builder(   //important!!! bulkprocessor need to be closed every main step.

	public void putMapping(String index) throws IOException{

		boolean exists = client.admin().indices().prepareExists(index).execute().actionGet().isExists();
		if(exists){
			return;
		}
		
        String settings_json = "{\r\n    \"analysis\": {\r\n      \"filter\": {\r\n        \"cody_stop\": {\r\n          \"type\":        \"stop\",\r\n          \"stopwords\": \"_english_\"  \r\n        },\r\n        \"cody_stemmer\": {\r\n          \"type\":       \"stemmer\",\r\n          \"language\":   \"light_english\" \r\n        }       \r\n      },\r\n      \"analyzer\": {\r\n        \"cody\": {\r\n          \"tokenizer\": \"standard\",\r\n          \"filter\": [ \r\n            \"lowercase\",\r\n            \"cody_stop\",\r\n            \"cody_stemmer\"\r\n          ]\r\n        }\r\n      }\r\n    }\r\n  }";		
		/*XContentParser parser;
		parser = XContentFactory.xContent(XContentType.JSON).createParser(mapping_json.getBytes());

		parser.close();
		XContentBuilder setting = jsonBuilder().copyCurrentStructure(parser);
		System.out.print(setting.string());*/
        //String mapping_json = "{\r\n      \"_default_\": {\r\n         \"properties\": { \r\n            \"fileName\": {\r\n            \"type\": \"multi_field\",\r\n            \"fields\": {\r\n                \"indexed\": {\r\n                \"type\": \"string\",\r\n                \"analyzer\": \"cody\"\r\n                },\r\n                \"original\": {\r\n                            \"type\" : \"string\", \r\n                            \"index\": \"not_analyzed\"\r\n                }\r\n            }\r\n         },\r\n            \"content\": {\r\n               \"type\": \"string\",\r\n               \"analyzer\": \"cody\"\r\n            },\r\n            \"metaAuthor\": {\r\n               \"type\": \"string\",\r\n               \"analyzer\": \"cody\"\r\n            },\r\n            \"metaContent\": {\r\n               \"type\": \"string\",\r\n               \"analyzer\": \"cody\"\r\n            }\r\n         }\r\n      }\r\n   }";
        String mapping_json = "{\r\n      \"_default_\": {\r\n         \"properties\": {            \r\n            \"fullName\": {\r\n                \"type\": \"string\",\r\n               \"index\": \"not_analyzed\"\r\n            },\r\n            \"shortName\": {\r\n                \"type\" : \"string\", \r\n                \"analyzer\": \"cody\"\r\n            },\r\n            \"name_suggest\" : {\r\n                \"type\" :  \"completion\"\r\n            },\r\n            \"content\": {\r\n               \"type\": \"string\",\r\n               \"analyzer\": \"cody\"\r\n            },\r\n            \"metaAuthor\": {\r\n               \"type\": \"string\",\r\n               \"analyzer\": \"cody\"\r\n            },\r\n            \"metaContent\": {\r\n               \"type\": \"string\",\r\n               \"analyzer\": \"cody\"\r\n            }\r\n         }\r\n      }\r\n   }";        
		//set up mapping
		client.admin().indices().prepareCreate(index).setSettings(ImmutableSettings.settingsBuilder().loadFromSource(settings_json)).execute().actionGet();
		client.admin().indices()
								.preparePutMapping(index)
						        .setType("_default_")				            
						        .setSource(mapping_json)
						        .execute().actionGet();
	}
	
	public void putVocabMapping(String index) throws IOException{
		String mapping_json = "{\r\n    \"vocabList\":{\r\n        \"properties\": {\r\n        \"Concept\": {\r\n               \"type\": \"string\",\r\n               \"index\": \"not_analyzed\"\r\n            },\r\n             \"Definition\": {\r\n               \"type\": \"string\",\r\n               \"index\": \"not_analyzed\"\r\n            }\r\n        }\r\n    }\r\n}";
		client.admin().indices()
								.preparePutMapping(index)
						        .setType(vocabType)				            
						        .setSource(mapping_json)
						        .execute().actionGet();
	}
	
	public void addConcept(String concept, String def) throws IOException{
		//putVocabMapping(index);
		if(checkItemExist(vocabType, "Concept", concept))
		{
			IndexResponse re = client.prepareIndex(index, vocabType)
			        .setSource(jsonBuilder()
			                    .startObject()
			                    .field("Concept", concept)		                    
			    				.field("Definition", def)
			                    .endObject()
			                  )
			        .get();
			
			node.client().admin().indices().prepareRefresh().execute().actionGet();	
		}
	}
	
	public boolean checkItemExist(String type, String keyName, String value){   	
    	CountResponse count = client.prepareCount(index)
				.setTypes(type)
		        .setQuery(QueryBuilders.termQuery(keyName, value))
		        .execute()
		        .actionGet();
			
		if(count.getCount()==0){
			return true;
		}else{
			return false;
		}
	}
	
	/*public boolean checkFileExist(String fileName){   	
    	CountResponse count = client.prepareCount(index)
				.setTypes(uploadedType)
		        .setQuery(QueryBuilders.termQuery("fullName", fileName))
		        .execute()
		        .actionGet();
			
		if(count.getCount()==0){
			return true;
		}else{
			return false;
		}
	}*/
	
	
	
	public boolean indexNewFileInfo(String fullName, long size, String fileType) throws IOException{
		//putMapping(index);		
		//if(checkFileExist(fullName))
		if(checkItemExist(fileType, "fullName", fullName))
		{
			IndexResponse re = client.prepareIndex(index, uploadedType)
			        .setSource(jsonBuilder()
			                    .startObject()
			                    .field("fullName", fullName)		                    
			    				.field("UploadedTime", new Date())
			    				.field("fileType", fileType)
			    				.field("size", size)
			                    .endObject()
			                  )
			        .get();
			
			node.client().admin().indices().prepareRefresh().execute().actionGet();
			
			System.out.println(fullName + " has been indexed.");
			return true;
		}else{
			return false;
		}		
	}
	
	public void indexNewFileContentM(String shortName, String fullName, long size, String fileType, String filePath) throws IOException{		
		contentReader cr = new contentReader(filePath);
		String content = null;
		Map<String, String> meta_map = new HashMap<String, String>();
		
		content = cr.readContent();
		meta_map = cr.readMetadata();
		IndexResponse re = client.prepareIndex(index, contentType)
			        .setSource(jsonBuilder()
			                    .startObject()
			                    .field("fullName", fullName)
			                    .field("shortName", shortName)
			                    .field("name_suggest", shortName)
			    				.field("UploadedTime", new Date())
			    				.field("fileType", fileType)
			    				.field("size", size)
			    				.field("content", content)
			    				.field("metaAuthor", meta_map.get("author"))
			    				.field("metaContent", meta_map.get("content"))
			                    .endObject()
			                  )
			        .get();
			
		node.client().admin().indices().prepareRefresh().execute().actionGet();
		System.out.println(shortName + " has become searchable.");
			
		
	}
	
	public String getFileList(){
		boolean exists = node.client().admin().indices().prepareExists(index).execute().actionGet().isExists();	
		if(!exists){
			return null;
		}
		
		SearchResponse response = client.prepareSearch(index)
		        .setTypes(uploadedType)		        
		        .setQuery(QueryBuilders.matchAllQuery())
		        .setSize(500)
		        .addSort("UploadedTime",SortOrder.DESC)  
		        .execute()
		        .actionGet();
        
        Gson gson = new Gson();		
        List<JsonObject> fileList = new ArrayList<JsonObject>();

        for (SearchHit hit : response.getHits().getHits()) {
        	Map<String,Object> result = hit.getSource();
        	String fileName = (String) result.get("fullName");
        	String time = (String) result.get("UploadedTime");
        	String fileType = (String) result.get("fileType");
        	Integer size = (Integer) result.get("size");
        	
        	JsonObject file = new JsonObject();
    		file.addProperty("Name", fileName);
    		file.addProperty("Uploaded Time", time);
    		file.addProperty("Type", fileType);
    		file.addProperty("Size", size);
    		fileList.add(file);       	
        	          
        }
        JsonElement fileList_Element = gson.toJsonTree(fileList);
        
        JsonObject PDResults = new JsonObject();
        PDResults.add("PDResults", fileList_Element);
        System.out.print("Browse list has been returned." + "\n");
		return PDResults.toString();
	}
	
	public String getVocabList(){
		boolean exists = node.client().admin().indices().prepareExists(index).execute().actionGet().isExists();	
		if(!exists){
			return null;
		}
		
		SearchResponse response = client.prepareSearch(index)
		        .setTypes(vocabType)		        
		        .setQuery(QueryBuilders.matchAllQuery())
		        .setSize(500)
		        .execute()
		        .actionGet();
        
        Gson gson = new Gson();		
        List<JsonObject> vocabList = new ArrayList<JsonObject>();

        for (SearchHit hit : response.getHits().getHits()) {
        	Map<String,Object> result = hit.getSource();
        	String concept = (String) result.get("Concept");
        	String def = (String) result.get("Definition");
        	
        	
        	JsonObject item = new JsonObject();
    		item.addProperty("Concept", concept);
    		item.addProperty("Definition", def);
    		
    		vocabList.add(item);       	
        	          
        }
        JsonElement vocabList_Element = gson.toJsonTree(vocabList);
        
        /*JsonObject PDResults = new JsonObject();
        PDResults.add("PDResults", vocabList_Element);
        System.out.print("Vocab list has been returned." + "\n");
		return PDResults.toString();*/
        
        System.out.print("Vocab list has been returned." + "\n");
		return vocabList_Element.toString();
	}
	
	public void deleteByQuery(String type, QueryBuilder query) {
		SearchResponse scrollResp = client.prepareSearch(index)
				.setSearchType(SearchType.SCAN)
				.setTypes(type)
				.setScroll(new TimeValue(60000))
				.setQuery(query)
				.setSize(10000)
				.execute().actionGet();  //10000 hits per shard will be returned for each scroll

		//SearchHit[] searchHits = scrollResp.getHits().getHits();

		while (true) {
			for (SearchHit hit : scrollResp.getHits().getHits()) {
				DeleteResponse dr = client.prepareDelete(index, type, hit.getId()).execute().actionGet();
				RefreshIndex();
			}
			
			//System.out.println("Need to delete " + scrollResp.getHits().getHits().length + " records");
			
			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
					.setScroll(new TimeValue(600000)).execute().actionGet();
			if (scrollResp.getHits().getHits().length == 0) {
				break;
			}

		}
	}
	
	public String searchByQuery(String query){
		boolean exists = node.client().admin().indices().prepareExists(index).execute().actionGet().isExists();	
		if(!exists){
			return null;
		}
		
		QueryBuilder qb = QueryBuilders.queryStringQuery(query); 
		SearchResponse response = client.prepareSearch(index)
		        .setTypes(contentType)		        
		        .setQuery(qb)
		        .setSize(500)
		        .execute()
		        .actionGet();
        
        Gson gson = new Gson();		
        List<JsonObject> fileList = new ArrayList<JsonObject>();

        for (SearchHit hit : response.getHits().getHits()) {
        	Map<String,Object> result = hit.getSource();
        	String fileName = (String) result.get("fullName");
        	String time = (String) result.get("UploadedTime");
        	String fileType = (String) result.get("fileType");
        	Integer size = (Integer) result.get("size");
        	String content = (String) result.get("content");
        	
        	int maxLength = (content.length() < MAX_CHAR)?content.length():MAX_CHAR;
        	content = content.trim().substring(0, maxLength-1) + "...";
        	
        	JsonObject file = new JsonObject();
    		file.addProperty("Name", fileName);
    		file.addProperty("Uploaded Time", time);
    		file.addProperty("Type", fileType);
    		file.addProperty("Size", size);
    		file.addProperty("content", content);
    		fileList.add(file);       	
        	          
        }
        JsonElement fileList_Element = gson.toJsonTree(fileList);
        
        JsonObject PDResults = new JsonObject();
        PDResults.add("PDResults", fileList_Element);
		System.out.print("Search results returned." + "\n");
		return PDResults.toString();
	}
	
	public List<String> autoComplete(String chars){
		boolean exists = node.client().admin().indices().prepareExists(index).execute().actionGet().isExists();	
		if(!exists){
			return null;
		}
		
		List<String> SuggestList = new ArrayList<String>();
		
		CompletionSuggestionFuzzyBuilder suggestionsBuilder = new CompletionSuggestionFuzzyBuilder("completeMe");
		//CompletionSuggestionBuilder suggestionsBuilder = new CompletionSuggestionBuilder("completeMe");
	    suggestionsBuilder.text(chars);
	    suggestionsBuilder.size(10);
	    suggestionsBuilder.field("name_suggest");
	    suggestionsBuilder.setFuzziness(Fuzziness.fromEdits(2));  
	    
	    SuggestRequestBuilder suggestRequestBuilder =
	            client.prepareSuggest(index).addSuggestion(suggestionsBuilder);


	    SuggestResponse suggestResponse = suggestRequestBuilder.execute().actionGet();

	    Iterator<? extends Suggest.Suggestion.Entry.Option> iterator =
	            suggestResponse.getSuggest().getSuggestion("completeMe").iterator().next().getOptions().iterator();

	    while (iterator.hasNext()) {
	        Suggest.Suggestion.Entry.Option next = iterator.next();
	        SuggestList.add(next.getText().string());
	    }
	    //System.out.print(SuggestList);
	    return SuggestList;
		
	}
	
	public void closeES() throws InterruptedException{
		//bulkProcessor.awaitClose(20, TimeUnit.MINUTES);
        node.close();  
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		ESdriver esd = new ESdriver();
		
		//BufferedReader br = new BufferedReader(new FileReader("C:/Users/Yongyao/Dropbox/tmp/colloqium/100PDconcepts.csv"));
		BufferedReader br = new BufferedReader(new FileReader("/usr/local/tomcat7/webapps/100PDconcepts.csv"));

		try {
			String line = br.readLine();
		    while (line != null) {	
		    	esd.addConcept(line, "Please double-click to edit");
		    	
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
		
		esd.closeES();
		System.out.print("Done.\n");
		
	

	}

}
