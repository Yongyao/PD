package PlanetaryDefense.PD.driver;

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.hppc.cursors.ObjectObjectCursor;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionFuzzyBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import PlanetaryDefense.PD.parse.contentReader;

public class ESdriver {
	String cluster = "elasticsearch";
    String index = "pd";
    String uploadedType = "uploadedInfo";
    
    String contentType = "contentAndMeta";
    public String vocabType = "vocabList";
    final Integer MAX_CHAR = 500;
	
	//important!!!  when deployed to VM   
    Settings settings =	System.getProperty("file.separator").equals("/") ? ImmutableSettings.settingsBuilder()
							.put("http.enabled", "false")
							.put("transport.tcp.port", "9300-9400")
							.put("discovery.zen.ping.multicast.enabled", "false")
							.put("discovery.zen.ping.unicast.hosts", "localhost")
							.build() : ImmutableSettings.settingsBuilder().put("http.enabled", false).build();

    Node node = nodeBuilder().client(true).settings(settings).clusterName(cluster).node();
	
    public Client client = node.client();
    
    public BulkProcessor bulkProcessor = null;
	
    public ESdriver(){
    	putMapping(index);
    	//putVocabMapping(index);	
    }
    
	public void RefreshIndex(){
		node.client().admin().indices().prepareRefresh().execute().actionGet();
	}

	public void putMapping(String index){
		boolean exists = client.admin().indices().prepareExists(index).execute().actionGet().isExists();
		if(exists){
			return;
		}
		
        String settings_json = "{\r\n    \"analysis\": {\r\n      \"filter\": {\r\n        \"cody_stop\": {\r\n          \"type\":        \"stop\",\r\n          \"stopwords\": \"_english_\"  \r\n        },\r\n        \"cody_stemmer\": {\r\n          \"type\":       \"stemmer\",\r\n          \"language\":   \"light_english\" \r\n        }       \r\n      },\r\n      \"analyzer\": {\r\n        \"cody\": {\r\n          \"tokenizer\": \"standard\",\r\n          \"filter\": [ \r\n            \"lowercase\",\r\n            \"cody_stop\",\r\n            \"cody_stemmer\"\r\n          ]\r\n        }\r\n      }\r\n    }\r\n  }";		
		String mapping_json = "{\r\n      \"_default_\": {\r\n         \"properties\": {            \r\n            \"fullName\": {\r\n                \"type\": \"string\",\r\n               \"index\": \"not_analyzed\"\r\n            },\r\n            \"shortName\": {\r\n                \"type\" : \"string\", \r\n                \"analyzer\": \"cody\"\r\n            },\r\n            \"name_suggest\" : {\r\n                \"type\" :  \"completion\"\r\n            },\r\n            \"content\": {\r\n               \"type\": \"string\",\r\n               \"analyzer\": \"cody\"\r\n            },\r\n            \"metaAuthor\": {\r\n               \"type\": \"string\",\r\n               \"analyzer\": \"cody\"\r\n            },\r\n            \"metaContent\": {\r\n               \"type\": \"string\",\r\n               \"analyzer\": \"cody\"\r\n            }\r\n         }\r\n      }\r\n   }";        
		//set up mapping
		client.admin().indices().prepareCreate(index).setSettings(ImmutableSettings.settingsBuilder().loadFromSource(settings_json)).execute().actionGet();
		client.admin().indices()
								.preparePutMapping(index)
						        .setType("_default_")				            
						        .setSource(mapping_json)
						        .execute().actionGet();
	}
	
	public BulkProcessor createBulkProcesser(){
		bulkProcessor = BulkProcessor.builder(
				client,
				new BulkProcessor.Listener() {
					public void beforeBulk(long executionId,
							BulkRequest request) {/*System.out.println("New request!");*/} 

					public void afterBulk(long executionId,
							BulkRequest request,
							BulkResponse response) {/*System.out.println("Well done!");*/} 

					public void afterBulk(long executionId,
							BulkRequest request,
							Throwable failure) {
						System.out.println("Bulk fails!");
						throw new RuntimeException("Caught exception in bulk: " + request + ", failure: " + failure, failure);
					} 
				}
				)
				.setBulkActions(1000) 
				.setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB)) 
				.setConcurrentRequests(1) 
				.build();
		return bulkProcessor;
	}

	public void destroyBulkProcessor(){
		try {
			bulkProcessor.awaitClose(20, TimeUnit.MINUTES);
			bulkProcessor = null;
			node.client().admin().indices().prepareRefresh().execute().actionGet();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
/*	public boolean putVocabMapping(String index){
		String mapping_json = "{\r\n    \"vocabList\":{\r\n        \"properties\": {\r\n        \"Concept\": {\r\n               \"type\": \"string\",\r\n               \"index\": \"not_analyzed\"\r\n            },\r\n             \"Definition\": {\r\n               \"type\": \"string\",\r\n               \"index\": \"not_analyzed\"\r\n            }\r\n        }\r\n    }\r\n}";
		client.admin().indices()
								.preparePutMapping(index)
						        .setType(vocabType)				            
						        .setSource(mapping_json)
						        .execute().actionGet();
		
		return true;
	}
	
	public void addConcept(String concept, String def) throws IOException{
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
	}*/
	
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
	
	public boolean checkTypeExist(String index, String type)
	{
		GetMappingsResponse res;
		try {
			res = client.admin().indices().getMappings(new GetMappingsRequest().indices(index)).get();
			ImmutableOpenMap<String, MappingMetaData> mapping  = res.mappings().get(index);
			for (ObjectObjectCursor<String, MappingMetaData> c : mapping) {
				if(c.key.equals(type))
				{
					return true;
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;	    
	}
		
	public boolean indexNewFileInfo(String fullName, long size, String fileType) throws IOException{
		if(checkItemExist(uploadedType, "fullName", fullName))
		{
			IndexResponse re = client.prepareIndex(index, uploadedType)
			        .setSource(jsonBuilder()
			                    .startObject()
			                    .field("fullName", fullName)		                    
			    				.field("Time", new Date())
			    				.field("fileType", fileType)
			    				.field("size", size)
			                    .endObject()
			                  )
			        .get();
			
			node.client().admin().indices().prepareRefresh().execute().actionGet();
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
			    				.field("Time", new Date())
			    				.field("fileType", fileType)
			    				.field("size", size)
			    				.field("content", content)
			    				.field("metaAuthor", meta_map.get("author"))
			    				.field("metaContent", meta_map.get("content"))
			                    .endObject()
			                  )
			        .get();
			
		node.client().admin().indices().prepareRefresh().execute().actionGet();
	}
	
	public String getFileList(){
		boolean exists = node.client().admin().indices().prepareExists(index).execute().actionGet().isExists();	
		if(!exists){
			return null;
		}

		if(!checkTypeExist(index, uploadedType))
		{
			return null;
		}else{
			SearchResponse response = client.prepareSearch(index)
					.setTypes(uploadedType)		        
					.setQuery(QueryBuilders.matchAllQuery())
					.setSize(500)
					.addSort("Time", SortOrder.DESC)  
					.execute()
					.actionGet();

			Gson gson = new Gson();		
			List<JsonObject> fileList = new ArrayList<JsonObject>();

			for (SearchHit hit : response.getHits().getHits()) {
				Map<String,Object> result = hit.getSource();
				String fileName = (String) result.get("fullName");
				String time = (String) result.get("Time");
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
			return PDResults.toString();
		}
	}
	
/*	public String getVocabList(){
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
		return vocabList_Element.toString();
	}
	
	public void deleteByQuery(String type, QueryBuilder query) {
		SearchResponse scrollResp = client.prepareSearch(index)
				.setSearchType(SearchType.SCAN)
				.setTypes(type)
				.setScroll(new TimeValue(60000))
				.setQuery(query)
				.setSize(10000)
				.execute().actionGet();  
		
		while (true) {
			for (SearchHit hit : scrollResp.getHits().getHits()) {
				DeleteResponse dr = client.prepareDelete(index, type, hit.getId()).execute().actionGet();
				RefreshIndex();
			}
			
			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
					.setScroll(new TimeValue(600000)).execute().actionGet();
			if (scrollResp.getHits().getHits().length == 0) {
				break;
			}

		}
	}*/
	
	public String searchByQuery(String query, String filter, String filter_field){
		boolean exists = node.client().admin().indices().prepareExists(index).execute().actionGet().isExists();	
		if(!exists){
			return null;
		}
		
		QueryBuilder qb = null;
		if(filter.equals(""))
		{
			qb = QueryBuilders.queryStringQuery(query);
		}
		else
		{
		FilterBuilder filter_search = FilterBuilders.boolFilter()
		          .must(FilterBuilders.termFilter(filter_field, filter));
		qb = QueryBuilders
		          .filteredQuery(QueryBuilders.queryStringQuery(query), filter_search);
		}
		SearchResponse response = client.prepareSearch(index)
		        .setTypes(contentType, "crawler4j")		        
		        .setQuery(qb)
		        .setSize(500)
		        .addAggregation(AggregationBuilders.terms("Types").field("fileType").size(0))
		        /*.addAggregation(AggregationBuilders.dateHistogram("TimeStamp")
		                .field("UploadedTime").interval((DateHistogram.Interval.DAY))
		                .order(DateHistogram.Order.COUNT_DESC))*/
		        .execute()
		        .actionGet();
		
		Terms Types = response.getAggregations().get("Types");
		List<JsonObject> TypeList = new ArrayList<JsonObject>();
		for (Terms.Bucket entry : Types.getBuckets()) {
			JsonObject Type = new JsonObject();
    		Type.addProperty("Key", entry.getKey());
    		Type.addProperty("Value", entry.getDocCount());
    		TypeList.add(Type);
		}
		
		/*DateHistogram TimeStamps = response.getAggregations().get("TimeStamp");
		List<JsonObject> TimeList = new ArrayList<JsonObject>();
	    List<? extends Bucket> TimeStampList = TimeStamps.getBuckets();
	    for(int i =0; i<TimeStampList.size();i++)
	    {
	    	JsonObject TimeStamp = new JsonObject();
	    	TimeStamp.addProperty("Key", TimeStampList.get(i).getKey());
	    	TimeStamp.addProperty("Value", TimeStampList.get(i).getDocCount());
    		TimeList.add(TimeStamp);
	    }*/
        
        Gson gson = new Gson();		
        List<JsonObject> fileList = new ArrayList<JsonObject>();

        for (SearchHit hit : response.getHits().getHits()) {
        	Map<String,Object> result = hit.getSource();
        	String fileType = (String) result.get("fileType");
        	String Time = (String) result.get("Time");
        	String Content = (String) result.get("content");
        	String Title, URL=null;
        	if(fileType.equals("webpage"))
        	{
        		Title = (String) result.get("Title");
        		URL = (String) result.get("URL");
        	}else{
        		Title = (String) result.get("fullName");
        	}
        	//Integer size = (Integer) result.get("size");
        	
        	
        	if(!Content.equals(""))
        	{
        	int maxLength = (Content.length() < MAX_CHAR)?Content.length():MAX_CHAR;
        	Content = Content.trim().substring(0, maxLength-1) + "...";
        	}
        	
        	JsonObject file = new JsonObject();
    		file.addProperty("Title", Title);
    		file.addProperty("Time", Time);
    		file.addProperty("Type", fileType);
    		file.addProperty("URL", URL);
    		file.addProperty("Content", Content);
    		fileList.add(file);       	
        	          
        }
        JsonElement fileList_Element = gson.toJsonTree(fileList);
        JsonElement TypeList_Element = gson.toJsonTree(TypeList);
        //JsonElement TimeList_Element = gson.toJsonTree(TimeList);
        
        JsonObject PDResults = new JsonObject();
        JsonObject FacetResults = new JsonObject();
        PDResults.add("SearchResults", fileList_Element);
        
        FacetResults.add("fileType", TypeList_Element);
       // FacetResults.add("UploadedTime", TimeList_Element);
        
        PDResults.add("FacetResults", FacetResults);
		return PDResults.toString();
	}
	
	public List<String> autoComplete(String chars){
		boolean exists = node.client().admin().indices().prepareExists(index).execute().actionGet().isExists();	
		if(!exists){
			return null;
		}
		
		List<String> SuggestList = new ArrayList<String>();
		
		CompletionSuggestionFuzzyBuilder suggestionsBuilder = new CompletionSuggestionFuzzyBuilder("completeMe");
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
	    return SuggestList;
		
	}
	
	public void closeES() throws InterruptedException{
		//bulkProcessor.awaitClose(20, TimeUnit.MINUTES);
        node.close();  
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub	
		/*ESdriver esd = new ESdriver();
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
		System.out.print("Done.\n");*/
	}

}
