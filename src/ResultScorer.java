import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The Class ResultScorer encloses methods to rank authors for the concept or term.
 */
public class ResultScorer
{
	/**
	 * ScoreByLinearDecreasingWeighting.
	 *
	 * @param relevancyMapList - The relevancy map list generated from concept/term queries
	 * @param factor - Indicates a factor for linear weight decrement
	 * @return Final merged relevancy map with decreasing scores
	 */
	public static Map<String, Double> scoreByLinearDecreasingWeighting(
			ArrayList<Map<String, String>> relevancyMapList, int factor)
	{
		Map<String,Double> weightedResultMap = new HashMap<String, Double>();
		int mapSize = relevancyMapList.size();
		Map<String,String> topDocumentsMap;
		boolean boostTermRelevancyMap = false;
		double totalWeight = 0;
		if(factor == 2)
		{
			boostTermRelevancyMap = true;
			factor = 1;
		}	

		// Merge Hash Maps along with its weight factor
		for(int i=0;i < mapSize;i++)
		{			
			double current_weight = 1 + (mapSize - i - 1)*factor;
			// Indicates if there is term relevancy map to boost
			if(boostTermRelevancyMap == true)
			{
				current_weight = 100;
				boostTermRelevancyMap = false;
			}

			totalWeight += current_weight;
			topDocumentsMap = relevancyMapList.get(i);

			// Compute the weighted average mean with linear decreasing weights
			for(int j=0;j < topDocumentsMap.size();j++)
			{
				Iterator<String> iterator = topDocumentsMap.keySet().iterator();    
				while (iterator.hasNext())
				{  
					String key = iterator.next().toString();  
					String value = topDocumentsMap.get(key).toString();
					if(weightedResultMap.containsKey(key))
						weightedResultMap.put(key, (weightedResultMap.get(key) + Double.parseDouble(value)*current_weight));
					else
						weightedResultMap.put(key, Double.parseDouble(value)*current_weight);
				}
			}
		}

		// Get weighted mean of relevancy scores
		Iterator<String> iterator = weightedResultMap.keySet().iterator();
		while (iterator.hasNext())
		{  
			String key = iterator.next().toString();
			weightedResultMap.put(key, (weightedResultMap.get(key) / totalWeight));		
		}

		// Sort the weighted meaned relevancy scores
		List<Entry<String, Double>> entries = new ArrayList<Entry<String, Double>>(
				weightedResultMap.entrySet());

		Collections.sort(entries, new Comparator<Entry<String, Double>>() {
			public int compare(Entry<String, Double> e1, Entry<String, Double> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		// Build the final result map with decreasing relevancy
		weightedResultMap = new LinkedHashMap<String, Double>();
		for (Entry<String, Double> entry : entries)
		{
			weightedResultMap.put(entry.getKey(), entry.getValue());
		}

		return weightedResultMap;
	}
}
