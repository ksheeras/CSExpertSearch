import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;


/**
 * This is the receiver class that executes the appropriate steps
 * according to the command fired.
 *
 * @author chethans
 */
public class Indexer
{
	CSExpertSearch csExpertSearcher;

	public Indexer(CSExpertSearch expertSearcher)
	{
		this.csExpertSearcher = expertSearcher;
	}

	/**
	 * Run_indexing.
	 * Create solr index
	 * @param path the path
	 * @throws SolrServerException the solr server exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void runIndexing(String path) throws SolrServerException, IOException
	{
		SolrUtilities.indexDocuments(path);
	}

	public void indexPeople() throws SolrServerException, IOException
	{
		System.out.println("\nInitiating people indexing..\n");
		CSExpertSearch.resultString += "\nInitiating people indexing..\n";
		SolrUtilities.setCurrentCore("people");
		runIndexing(CSExpertSearch.solrPeopleDocumentsPath);
		CSExpertSearch.resultString += "People indexing completed..\n";
		System.out.println("People indexing completed..\n");
	}

	public void indexConcepts() throws SolrServerException, IOException
	{
		System.out.println("\nInitiating concept indexing..\n");
		CSExpertSearch.resultString += "\nInitiating concept indexing..\n";
		SolrUtilities.setCurrentCore("concept");
		runIndexing(CSExpertSearch.solrConceptsDocumentsPath);
		CSExpertSearch.resultString += "Concept indexing completed..\n";
		System.out.println("Concept indexing completed..\n");
	}

	public void indexAll() throws SolrServerException, IOException
	{
		indexPeople();
		indexConcepts();
	}
}
