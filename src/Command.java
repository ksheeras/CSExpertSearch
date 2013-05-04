import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;

/**
 * The command interface.
 * @author chethans
 */
public interface Command
{
	void execute() throws SolrServerException, IOException;
}
