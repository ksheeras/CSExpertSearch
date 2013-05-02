import java.io.File;
import java.io.IOException;

/**
 * The Class Query_Generator.
 * Generates a set of concept queries
 */
public class Query_Generator {
	
	/**
	 * Generate_concept_queries.
	 * @param concept_name the concept_name
	 * @return the string[] - set of queries
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String[] generate_concept_queries(String concept_name) throws IOException{
		String[] queries = new String[CS_Expert_Search.MAX_CONCEPT_QUERIES];
		String[] terms = new String[CS_Expert_Search.MAX_CONCEPT_QUERIES];
		String query_string = "";
		int i =0;
		File file = new File(CS_Expert_Search.solr_concepts_documents_path + "/" + concept_name);
		terms = File_Utility.get_field_list_from_tabular_data(file, 2);
		for(String str:terms){
			if(i==0)
				query_string = str;
			else{
				query_string += " ";
				query_string += str;				
			}
			queries[i] = query_string;
			i++;
		}
		return queries;
	}

	/*public static void main(String[] args) throws IOException {
		String[] queries = new String[CS_Expert_Search.MAX_CONCEPT_QUERIES];	
		queries = generate_concept_queries("data_mining");
		for(String str:queries){
			System.out.println(str);
		}
	}*/
}