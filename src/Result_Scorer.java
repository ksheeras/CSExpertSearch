/**
 * The Class Result_Scorer.
 * Methods to rank authors for the concept or term
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Result_Scorer {

	/**
	 * Score_by_linear_decreasing_weighting.
	 *
	 * @param relevancy_map_list - The relevancy_map_list generated from concept/term queries
	 * @param factor - Indicates a factor for linear weight decrement
	 * @return Final merged relevancy map with decreasing scores
	 */
	public static Map<String, Double> score_by_linear_decreasing_weighting(ArrayList<Map<String, String>> relevancy_map_list,int factor){

		Map<String,Double> weighted_result_map = new HashMap<String, Double>();
		int map_size = relevancy_map_list.size();
		Map<String,String> top_documents_map;
		boolean boost_term_relevancy_map = false;
		double total_weight = 0;
		if(factor == 2){
			boost_term_relevancy_map = true;
			factor = 1;
		}	

		// Merge Hash Maps along with its weight factor
		for(int i=0;i<map_size;i++){			
			double current_weight = 1 + (map_size-i-1)*factor;
			if(boost_term_relevancy_map == true){
				current_weight = 100;
				boost_term_relevancy_map = false;
			}
			total_weight += current_weight;
			top_documents_map = relevancy_map_list.get(i);
			for(int j=0;j<top_documents_map.size();j++){
				Iterator<String> iterator = top_documents_map.keySet().iterator();    
				while (iterator.hasNext()) {  
					String key = iterator.next().toString();  
					String value = top_documents_map.get(key).toString();
					if(weighted_result_map.containsKey(key))
						weighted_result_map.put(key, (weighted_result_map.get(key) + Double.parseDouble(value)*current_weight));
					else
						weighted_result_map.put(key, Double.parseDouble(value)*current_weight);
				}
			}
		}

		// Get weighted mean of relevancy scores
		Iterator<String> iterator = weighted_result_map.keySet().iterator();
		while (iterator.hasNext()) {  
			String key = iterator.next().toString();
			weighted_result_map.put(key, (weighted_result_map.get(key)/total_weight));		
		}

		// Sort the weighted meaned relevancy scores
		List<Entry<String, Double>> entries = new ArrayList<Entry<String, Double>>(weighted_result_map.entrySet());
		Collections.sort(entries, new Comparator<Entry<String, Double>>() {
			public int compare(Entry<String, Double> e1, Entry<String, Double> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		// Build the final result map with decreasing relevancy
		weighted_result_map = new LinkedHashMap<String, Double>();
		for (Entry<String, Double> entry : entries) {
			weighted_result_map.put(entry.getKey(), entry.getValue());
		}

		return weighted_result_map;
	}
}