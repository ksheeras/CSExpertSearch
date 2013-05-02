import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

/**
 * The Class Solr_Utility.
 * Contains methods to interact with solr server
 */
public class Solr_Utility {
	
	// current index core in use - Caller to set the core before running any operation
	/** The current_core. */
	public static String current_core = "people";
	
	/**
	 * Sets the _current solr core.
	 * @param core the new _current_core
	 */
	public static void set_current_core(String core){
		current_core = core;
	}

	/**
	 * Index_documents.
	 * Index the documents by reading the files from the specified location 
	 * @param documents_path the documents_path
	 * @throws SolrServerException the solr server exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void index_documents(String documents_path) throws SolrServerException, IOException{
		File[] files = File_Utility.get_file_list_in_dir(documents_path);
		String person_name;
		String file_content;
		int i= 0;
		// Add each document to the index
		HttpSolrServer server = new HttpSolrServer(CS_Expert_Search.SOLR_URL + current_core);
		// Delete current index
		server.deleteByQuery( "*:*" );
		for(File file:files){
			person_name = file.getName();
			file_content = File_Utility.load_string_from_file(file);
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("cat", person_name);
			doc.addField("id",  person_name);
			doc.addField("name", person_name);
			doc.addField("content",file_content);
			server.add(doc);
			i++;
			if(i%100==0) server.commit();  // periodically flush
		}
		server.commit(); 
	}

	/**
	 * Search_documents.
	 * Search for the input query and return the Solr Doc list
	 * @param query_string the query_string
	 * @return the solr document list
	 * @throws SolrServerException the solr server exception
	 */
	public static SolrDocumentList search_documents(String query_string) throws SolrServerException{
		SolrServer server = new HttpSolrServer(CS_Expert_Search.SOLR_URL + current_core);
	    SolrQuery query = new SolrQuery();
	    query.setIncludeScore(true);
	    query.setQuery(query_string);
	    QueryResponse response = server.query( query );
		SolrDocumentList docs = response.getResults();
		return docs;
	}

	
	/**
	 * Gets the _top_docs_with_scores.
	 * Search the input query and return relevancy Map with decreasing relevancy scores
	 * @param query_string the query_string
	 * @return the _top_docs_with_scores
	 * @throws SolrServerException the solr server exception
	 */
	public static Map<String, String> get_top_docs_with_scores(String query_string) throws SolrServerException{
		Map<String,String> top_documents_map=new HashMap<String, String>();
		SolrDocumentList docs = search_documents(query_string);
		//System.out.println("Results size = " + docs.size());
		for (int i = 0; i < docs.size(); ++i) {
			top_documents_map.put(docs.get(i).getFieldValue("id").toString(), docs.get(i).getFirstValue("score").toString());
		}
		return top_documents_map;
	}

	/*public static void main(String[] args) throws SolrServerException, IOException {
		//Map<String,String> top_documents_map=new HashMap<String, String>();
		set_current_core("concept");
		index_documents(System.getProperty("user.dir") + "/concept_data");
		set_current_core("people");
		index_documents(System.getProperty("user.dir") + "/people_data");
		top_documents_map = get_top_docs_with_scores("information");
		Iterator<String> iterator = top_documents_map.keySet().iterator();  	   
		while (iterator.hasNext()) {  
		   String key = iterator.next();  
		   String value = top_documents_map.get(key).toString();     
		   System.out.println(key + " " + value);  
		}
	}*/
}