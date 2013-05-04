import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;


/**
 * This is another receiver class that executes the appropriate steps
 * according to the command fired.
 *
 * @author chethans
 */
public class Searcher
{
	CSExpertSearch csExpertSearcher;

	public Searcher(CSExpertSearch expertSearcher)
	{
		this.csExpertSearcher = expertSearcher;
	}

	public void researchConceptSearch() throws IOException, SolrServerException
	{
		System.out.println("\nResearch concept search started...\n");
		CSExpertSearch.resultString += "\nResearch concept search started...\n";
		SolrUtilities.setCurrentCore("people");
		runConceptSearch(csExpertSearcher.getSearchString());
		CSExpertSearch.resultString += "Research concept search completed.\n";
		System.out.println("Research concept search completed.\n");
	}

	public void researchTermSearch() throws SolrServerException, IOException
	{
		System.out.println("\nInput terms search started...\n");
		CSExpertSearch.resultString += "\nInput terms search started...\n";
		SolrUtilities.setCurrentCore("people");
		runTermSearch();	
		System.out.println("Input terms search completed.\n");
		CSExpertSearch.resultString += "Input terms search completed.\n";
	}

	/**
	 * Run concept search.
	 * Perform concept based search for find experts
	 * @param concept the concept
	 * @return the array list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SolrServerException the solr server exception
	 */
	private ArrayList<Map<String, String>> runConceptSearch(String concept) throws IOException, 
		SolrServerException
	{
		CSExpertSearch.	resultString += "\nResearch concept string:\n";
		CSExpertSearch.resultString +=  concept;
		ArrayList<Map<String, String>> relevancyMapList = new ArrayList<Map<String, String>>();
		Map<String,String> topDocumentsMap;
		Map<String,Double> finalWeightedMap;
		CSExpertSearch.resultString += "\n\nResearch concept search results:\n\n";		
		String[] conceptQueries = QueryGenerator.generateConceptQueries(concept);

		for(String conceptQuery:conceptQueries)
		{
			topDocumentsMap = SolrUtilities.getTopDocsWithScores(conceptQuery);
			relevancyMapList.add(topDocumentsMap);
		}

		finalWeightedMap = ResultScorer.scoreByLinearDecreasingWeighting(relevancyMapList,1);
		CSExpertSearch.resultString += "Person Name\t" + "Weighted Relevancy score for concept queries\n";
		Iterator<String> iterator = finalWeightedMap.keySet().iterator();  	   
		while (iterator.hasNext()) {  
			String key = iterator.next();  
			String value = finalWeightedMap.get(key).toString();     
			CSExpertSearch.resultString += key + "\t" + value + "\n";
		}
		CSExpertSearch.resultString += "\n";
		return relevancyMapList;
	}

	/**
	 * Run_term_search.
	 * Perform term based search to find experts
	 * @return the string
	 * @throws SolrServerException the solr server exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String runTermSearch() throws SolrServerException, IOException
	{
		String queryString = csExpertSearcher.getSearchString();
		ArrayList<Map<String, String>> finalRelevancyList = new ArrayList<Map<String, String>>();
		ArrayList<Map<String, String>> conceptRelevancyList = null;
		Map<String,Double> finalWeightedMap;
		Map<String, String> relevantConcepts;
		Map.Entry<String, String> bestRelevantConcept;

		SolrUtilities.setCurrentCore("concept");
		relevantConcepts = SolrUtilities.getTopDocsWithScores(queryString);
		bestRelevantConcept = null;

		if(relevantConcepts.size() > 0)
		{
			for (Map.Entry<String, String> entry : relevantConcepts.entrySet())
			{
				if (bestRelevantConcept == null || entry.getValue().compareTo(
						bestRelevantConcept.getValue()) > 0)
				{
					bestRelevantConcept = entry;
				}
			}

			SolrUtilities.setCurrentCore("people");
			conceptRelevancyList = runConceptSearch(bestRelevantConcept.getKey().toString());	
		}

		String tmpString = CSExpertSearch.resultString;	
		Map<String, String> termRelevancyMap = SolrUtilities.getTopDocsWithScores(queryString);

		CSExpertSearch.resultString = tmpString;
		CSExpertSearch.resultString += "\nInput term search string:\n";
		CSExpertSearch.resultString += queryString;
		CSExpertSearch.resultString += "\n\nMost relevant concept to input terms:\n";

		if(relevantConcepts.size() > 0)
			CSExpertSearch.resultString += bestRelevantConcept.getKey().toString();		

		CSExpertSearch.resultString += "\n\nInput terms search results:\n";
		finalRelevancyList.add(termRelevancyMap);

		if(relevantConcepts.size() > 0)
			finalRelevancyList.addAll(conceptRelevancyList);

		finalWeightedMap = ResultScorer.scoreByLinearDecreasingWeighting(finalRelevancyList,2);
		Iterator<String> iterator = finalWeightedMap.keySet().iterator();  	   
		CSExpertSearch.resultString += "Person Name\t" + "Weighted Relevancy score for term/concept queries\n";

		while (iterator.hasNext())
		{  
			String key = iterator.next();  
			String value = finalWeightedMap.get(key).toString();     
			CSExpertSearch.resultString += key + "\t" + value + "\n";
		}

		CSExpertSearch.resultString += "\n";	
		return CSExpertSearch.resultString;
	}
}
