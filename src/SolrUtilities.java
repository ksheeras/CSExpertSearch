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
 * The Class SolrUtility contains methods to interact with solr server
 */
public class SolrUtilities
{
	// current index core in use - Caller to set the core before running any operation
	/** The current_core. */
	public static String currentCore = "people";
	
	/**
	 * Sets the _current solr core.
	 * @param core the new _current_core
	 */
	public static void setCurrentCore(String core){
		currentCore = core;
	}

	/**
	 * Index_documents.
	 * Index the documents by reading the files from the specified location 
	 * @param documentsPath the documents path
	 * @throws SolrServerException the solr server exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void indexDocuments(String documentsPath) throws SolrServerException, IOException{
		File[] files = FileUtilities.getFileListInDir(documentsPath);
		String person_name;
		String file_content;
		int i= 0;
		// Add each document to the index
		HttpSolrServer server = new HttpSolrServer(CSExpertSearch.SOLR_URL + currentCore);

		// Delete current index
		server.deleteByQuery( "*:*" );
		for(File file:files)
		{
			person_name = file.getName();
			file_content = FileUtilities.loadStringFromFile(file);
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("cat", person_name);
			doc.addField("id",  person_name);
			doc.addField("name", person_name);
			doc.addField("content",file_content);
			server.add(doc);
			i++;

			// periodically flush
			if(i%100 == 0)
				server.commit();
		}
		server.commit(); 
	}

	/**
	 * Search_documents.
	 * Search for the input query and return the Solr Doc list
	 * @param queryString the query_string
	 * @return the solr document list
	 * @throws SolrServerException the solr server exception
	 */
	public static SolrDocumentList searchDocuments(String queryString) throws SolrServerException
	{
		SolrServer server = new HttpSolrServer(CSExpertSearch.SOLR_URL + currentCore);
	    SolrQuery query = new SolrQuery();
	    query.setIncludeScore(true);
	    query.setQuery(queryString);
	    QueryResponse response = server.query( query );
		SolrDocumentList docs = response.getResults();
		return docs;
	}

	
	/**
	 * Gets the _top_docs_with_scores.
	 * Search the input query and return relevancy Map with decreasing relevancy scores
	 * @param query_string the queryString
	 * @return the top documents with scores
	 * @throws SolrServerException the solr server exception
	 */
	public static Map<String, String> getTopDocsWithScores(String queryString) throws SolrServerException
	{
		Map<String,String> topDocumentsMap=new HashMap<String, String>();
		SolrDocumentList docs = searchDocuments(queryString);
		for (int i = 0; i < docs.size(); ++i)
		{
			topDocumentsMap.put(docs.get(i).getFieldValue("id").toString(), docs.get(i).getFirstValue("score").toString());
		}

		return topDocumentsMap;
	}
}
