import java.io.File;
import java.io.IOException;

/**
 * The Class Query_Generator.
 * Generates a set of concept queries
 */
public class QueryGenerator
{
	/**
	 * Generate_concept_queries.
	 * @param concept_name the concept_name
	 * @return the string[] - set of queries
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String[] generateConceptQueries(String conceptName) throws IOException
	{
		String[] queries = new String[CSExpertSearch.MAX_CONCEPT_QUERIES];
		String[] terms = new String[CSExpertSearch.MAX_CONCEPT_QUERIES];
		String queryString = "";
		int i =0;
		File file = new File(CSExpertSearch.solrConceptsDocumentsPath + "/" + conceptName);
		terms = FileUtilities.getFieldListFromTabularData(file, 2);

		for(String str:terms)
		{
			if(i==0)
			{
				queryString = str;
			}
			else
			{
				queryString += " ";
				queryString += str;				
			}
			queries[i] = queryString;
			i++;
		}

		return queries;
	}
}
