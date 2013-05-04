import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;


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
