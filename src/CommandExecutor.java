import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;


/**
 * This is the invoker class.
 * @author chethans
 */
public class CommandExecutor
{
	public void executeCommand(Command cmd) throws SolrServerException, IOException
	{
		cmd.execute();        
	}
}
