import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrServerException;

public class CS_Expert_Search {
	// Function Constants 
	// Index types - Mulicore Solr enabled
	public static final int INDEX_PEOPLE = 1;
	public static final int INDEX_CONCEPTS = 2;
	public static final int INDEX_ALL = 3;
	// Search functions
	public static final int RESEARCH_CONCEPT_SEARCH = 4;
	public static final int RESEARCH_TERMS_SEARCH = 5;

	// Maximum number of concept queries generated
	public static int MAX_CONCEPT_QUERIES = 50;
	/* If indexing, location of documents path. Each document is a person profile
	   extracted from web using focused on selected domains */
	public static String solr_people_documents_path;
	/* Research Concept files location - Research concept files contain top K terms
	   along with weights for each research concept. eg. Data Mining */
	public static String solr_concepts_documents_path;
	/* Solr server home path */
	public static String SOLR_URL;

	// Function: Search or index ?
	public int function_type;
	/* If searching, search string - Concept or terms*/
	public String search_string;
	/* Result String */
	public static String result_string;
	/* Result String */
	public static String result_file_path;
	
	public void set_function_type(int function_type) {
		this.function_type = function_type;
	}	

	public void set_search_string(String search_string){
		this.search_string = search_string;
	}	

	public String get_search_string(){
		return search_string;
	}

	private ArrayList<Map<String, String>> run_concept_search(String concept) throws IOException, SolrServerException{
		result_string += "\nResearch concept string:\n";
		result_string +=  concept;
		ArrayList<Map<String, String>> relevancy_map_list = new ArrayList<Map<String, String>>();
		Map<String,String> top_documents_map;
		Map<String,Double> final_weighted_map;
		result_string += "\n\nResearch concept search results:\n\n";		
		String[] concept_queries = Query_Generation.generate_concept_queries(concept);
		for(String concept_query:concept_queries){
			top_documents_map = Solr_Utility.get_top_docs_with_scores(concept_query);
			relevancy_map_list.add(top_documents_map);
		}
		final_weighted_map = Result_Scorer.score_by_linear_decreasing_weighting(relevancy_map_list,1);
		result_string += "Person Name\t" + "Weighted Relevancy score for concept queries\n";
		Iterator<String> iterator = final_weighted_map.keySet().iterator();  	   
		while (iterator.hasNext()) {  
		   String key = iterator.next();  
		   String value = final_weighted_map.get(key).toString();     
		   result_string += key + "\t" + value + "\n";
		}
		result_string += "\n";
		return relevancy_map_list;
	}

	private String run_term_search() throws SolrServerException, IOException{
		String query_string = this.get_search_string();
		ArrayList<Map<String, String>> final_relevancy_list = new ArrayList<Map<String, String>>();;
		ArrayList<Map<String, String>> concept_relevancy_list;
		Map<String,Double> final_weighted_map;
		Solr_Utility.set_current_core("concept");
		Map<String, String> relevant_concepts = Solr_Utility.get_top_docs_with_scores(query_string);
		Map.Entry<String, String> best_relevant_concept = null;
		for (Map.Entry<String, String> entry : relevant_concepts.entrySet()){
			if (best_relevant_concept == null || entry.getValue().compareTo(best_relevant_concept.getValue()) > 0){
				best_relevant_concept = entry;
			}
		}
		String tmp_string = result_string;
		Solr_Utility.set_current_core("people");	
		concept_relevancy_list = run_concept_search(best_relevant_concept.getKey().toString());	
		Map<String, String> term_relevancy_map = Solr_Utility.get_top_docs_with_scores(query_string);
		result_string = tmp_string;
		result_string += "\nInput term search string:\n";
		result_string += query_string;
		result_string += "\n\nMost relevant concept to input terms:\n";
		result_string += best_relevant_concept.getKey().toString();		
		result_string += "\n\nInput terms search results:\n";
		final_relevancy_list.add(term_relevancy_map);
		final_relevancy_list.addAll(concept_relevancy_list);
		final_weighted_map = Result_Scorer.score_by_linear_decreasing_weighting(final_relevancy_list,2);
		Iterator<String> iterator = final_weighted_map.keySet().iterator();  	   
		result_string += "Person Name\t" + "Weighted Relevancy score for term/concept queries\n";
		while (iterator.hasNext()) {  
		   String key = iterator.next();  
		   String value = final_weighted_map.get(key).toString();     
		   result_string += key + "\t" + value + "\n";
		}
		result_string += "\n";	
		return result_string;
	}

	private void run_indexing(String path) throws SolrServerException, IOException{
		Solr_Utility.index_documents(path);
	}

	private static void load_from_config_file() throws IOException{

		Properties prop = new Properties();
		// the configuration file name
		String fileName = System.getProperty("user.dir") + "/CSExpertSearch.config";            
		InputStream is = new FileInputStream(fileName);
		// load the properties file
		prop.load(is);
		// get all the config parameters
		solr_people_documents_path = prop.getProperty("solr_people_documents_path");
		solr_concepts_documents_path = prop.getProperty("solr_concepts_documents_path");
		result_file_path = prop.getProperty("result_file_path");
		SOLR_URL = prop.getProperty("SOLR_URL");
		MAX_CONCEPT_QUERIES = Integer.parseInt(prop.getProperty("max_concept_queries"));
	}

	public static void main(String[] args) throws IOException, SolrServerException {		
		String arg;
		String query_string = null;
		int function_type = 0,i =0;	

		load_from_config_file();
		// Parse command
		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];
			if (arg.equals("-csearch")) {
				function_type = RESEARCH_CONCEPT_SEARCH;
				// add all remaining arguments as query string
				query_string = args[i++];
				while(i < args.length){
					query_string += " ";
					query_string += args[i++];
				}
				if(query_string.isEmpty()){
					System.out.println("Empty research concept terms - Exiting");
					System.exit(-1);
				}
			}
			else if (arg.equals("-tsearch")) {
				function_type = RESEARCH_TERMS_SEARCH;
				// add all remaining arguments as query string
				query_string = args[i++];
				while(i < args.length){
					query_string += " ";
					query_string += args[i++];;
				}
				if(query_string.isEmpty()){
					System.out.println("Empty query terms - Exiting");
					System.exit(-1);
				}
			}	            
			else if (arg.equals("-pindex")) {
				function_type = INDEX_PEOPLE;
				if(solr_people_documents_path.isEmpty()){
					// default path - from working directory
					solr_people_documents_path = System.getProperty("user.dir") + "/people_data";
				}
			}
			else if (arg.equals("-cindex")) {
				function_type = INDEX_CONCEPTS;
				if(solr_concepts_documents_path.isEmpty()){
					// default path - from working directory
					solr_concepts_documents_path = System.getProperty("user.dir") + "/concept_data";
				}
			}
			else if (arg.equals("-index")) {
				function_type = INDEX_ALL;
				if(solr_concepts_documents_path.isEmpty()){
					// default path - from working directory
					solr_concepts_documents_path = System.getProperty("user.dir") + "/concept_data";
				}
			}
		}

		// Initiate object for search/indexing
		CS_Expert_Search cs_expert_search = new CS_Expert_Search();
		cs_expert_search.set_function_type(function_type);
		cs_expert_search.set_search_string(query_string);

		result_string = "Computer Science Expert Finder from Web:\n";
		// Perform requested functions
		if (function_type == INDEX_PEOPLE || function_type == INDEX_ALL ){
			System.out.println("\nInitiating people indexing..\n");
			result_string += "\nInitiating people indexing..\n";
			Solr_Utility.set_current_core("people");
			cs_expert_search.run_indexing(solr_people_documents_path);
			result_string += "People indexing completed..\n";
			System.out.println("People indexing completed..\n");			
		}

		if (function_type == INDEX_CONCEPTS || function_type == INDEX_ALL){
			System.out.println("\nInitiating concept indexing..\n");
			result_string += "\nInitiating concept indexing..\n";
			Solr_Utility.set_current_core("concept");
			cs_expert_search.run_indexing(solr_concepts_documents_path);
			result_string += "Concept indexing completed..\n";
			System.out.println("Concept indexing completed..\n");
		}

		if(function_type == RESEARCH_CONCEPT_SEARCH){
			System.out.println("\nResearch concept search started...\n");
			result_string += "\nResearch concept search started...\n";
			Solr_Utility.set_current_core("people");
			cs_expert_search.run_concept_search(query_string);
			result_string += "Research concept search completed.\n";
			System.out.println("Research concept search completed.\n");			
		}
		else if (function_type == RESEARCH_TERMS_SEARCH) {
			System.out.println("\nInput terms search started...\n");
			result_string += "\nInput terms search started...\n";
			Solr_Utility.set_current_core("people");
			cs_expert_search.run_term_search();	
			System.out.println("Input terms search completed.\n");
			result_string += "Input terms search completed.\n";
		}
		else if (function_type == 0) {
			System.out.println("Not a valid command. Exiting");				
			System.exit(-1);
		}
		File_Utility.string_to_file(result_string,result_file_path);
		System.out.println(result_string);
	}
}