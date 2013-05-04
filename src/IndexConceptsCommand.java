import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;


public class IndexConceptsCommand implements Command
{
	private Indexer indexer;

	public IndexConceptsCommand(Indexer indexer)
	{
		this.indexer = indexer;
	}

	public void execute() throws SolrServerException, IOException
	{
		indexer.indexConcepts();
	}
}
