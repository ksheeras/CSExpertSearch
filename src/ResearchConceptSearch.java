import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
/**
 * Research concept search
 * @author chethans
 */

public class ResearchConceptSearch implements Command
{
	private Searcher searcher;

	public ResearchConceptSearch(Searcher searcher)
	{
		this.searcher = searcher;
	}

	public void execute() throws SolrServerException, IOException
	{
		searcher.researchConceptSearch();
	}
}
