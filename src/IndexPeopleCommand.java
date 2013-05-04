import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
/**
 * index people command
 * @author chethans
 */
public class IndexPeopleCommand implements Command
{
	private Indexer indexer;

	public IndexPeopleCommand(Indexer indexer)
	{
		this.indexer = indexer;
	}

	public void execute() throws SolrServerException, IOException
	{
		indexer.indexPeople();
	}
}
