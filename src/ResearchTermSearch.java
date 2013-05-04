import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
/**
 * Research term search
 * @author chethans
 */
public class ResearchTermSearch implements Command
{
	private Searcher searcher;

	public ResearchTermSearch(Searcher searcher)
	{
		this.searcher = searcher;
	}

	public void execute() throws SolrServerException, IOException
	{
		searcher.researchTermSearch();
	}
}
