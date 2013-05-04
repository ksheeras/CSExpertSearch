import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;

/**
 * index all command
 * @author chethans
 */
public class IndexAllCommand implements Command
{
	private Indexer indexer;

	public IndexAllCommand(Indexer indexer)
	{
		this.indexer = indexer;
	}

	public void execute() throws SolrServerException, IOException
	{
		indexer.indexAll();
	}
}
