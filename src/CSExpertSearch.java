import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrServerException;

/**
 * The Class CSExpertSearch
 * Main program for expert search
 */
public class CSExpertSearch
{
	// Function Constants 
	// Index types - Mulicore Solr enabled
	/** The Constant INDEX_PEOPLE. */
	public static final int INDEX_PEOPLE = 1;

	/** The Constant INDEX_CONCEPTS. */
	public static final int INDEX_CONCEPTS = 2;

	/** The Constant INDEX_ALL. */
	public static final int INDEX_ALL = 3;
	// Search functions
	/** The Constant RESEARCH_CONCEPT_SEARCH. */
	public static final int RESEARCH_CONCEPT_SEARCH = 4;

	/** The Constant RESEARCH_TERMS_SEARCH. */
	public static final int RESEARCH_TERMS_SEARCH = 5;

	/** Invalid command constant */
	public static final int INVALID_COMMAND = 0;

	// Maximum number of concept queries generated
	/** The max concept queries. */
	public static int MAX_CONCEPT_QUERIES = 50;
	/* If indexing, location of documents path. Each document is a person profile
	   extracted from web using focused on selected domains */
	/** The solr_people_documents_path. */
	public static String solrPeopleDocumentsPath;
	/* Research Concept files location - Research concept files contain top K terms
	   along with weights for each research concept. eg. Data Mining */
	/** The solr_concepts_documents_path. */
	public static String solrConceptsDocumentsPath;
	/* Solr server home path */
	/** The solr url. */
	public static String SOLR_URL;

	/* Indicates type of command */
	public int functionType;
	/* If searching, search string - Concept or terms*/
	public String searchString;
	/* Result String */
	public static String resultString;
	/* Result file path */
	public static String resultFilePath;

	/**
	 * Sets the functionType.
	 * @param functionType the new functionType
	 */
	public void setFunctionType(int functionType)
	{
		this.functionType = functionType;
	}	

	/**
	 * Sets the _search_string.
	 * @param search_string the new searchstring
	 */
	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
	}	

	/**
	 * Gets the _search_string.
	 * @return the _search_string
	 */
	public String getSearchString()
	{
		return searchString;
	}

	/**
	 * Load_from_config_file.
	 * Get parameters from config file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void loadFromConfigFile() throws IOException
	{
		Properties property = new Properties();
		// the configuration file name
		String fileName = System.getProperty("user.dir") + "/CSExpertSearch.config";            
		InputStream is = new FileInputStream(fileName);
		// load the properties file
		property.load(is);
		// get all the config parameters
		solrPeopleDocumentsPath = property.getProperty("solr_people_documents_path");
		solrConceptsDocumentsPath = property.getProperty("solr_concepts_documents_path");
		resultFilePath = property.getProperty("result_file_path");
		SOLR_URL = property.getProperty("SOLR_URL");
		MAX_CONCEPT_QUERIES = Integer.parseInt(property.getProperty("max_concept_queries"));
	}

	/**
	 * The main method.
	 * Entry method for all operations
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SolrServerException the solr server exception
	 */
	public static void main(String[] args) throws IOException, SolrServerException
	{
		try
		{
			String arg;
			String queryString = null;
			int functionType = 0, i = 0;
			Command command = new InvalidCommand();

			loadFromConfigFile();

			// Initiate object for search/indexing
			CSExpertSearch csExpertSearcher = new CSExpertSearch();
			
			// Receivers
			Indexer indexer = new Indexer(csExpertSearcher);
			Searcher searcher = new Searcher(csExpertSearcher);

			// Invoker
			CommandExecutor commandExecutor = new CommandExecutor();

			// Indexing Commands
			Command indexPeopleCommand = new IndexPeopleCommand(indexer);
			Command indexConceptsCommand = new IndexConceptsCommand(indexer);
			Command indexAllCommand = new IndexAllCommand(indexer);

			// Searching Commands
			Command researchConceptSearchCommand = new ResearchConceptSearch(searcher);
			Command researchTermSearchCommand = new ResearchTermSearch(searcher);

			// Parsing to identify the command
			while (i < args.length && args[i].startsWith("-"))
			{
				arg = args[i++];
				if (arg.equals("-csearch"))
				{
					functionType = RESEARCH_CONCEPT_SEARCH;
					command = researchConceptSearchCommand;

					// add all remaining arguments as query string
					queryString = args[i++];
					while(i < args.length)
					{
						queryString += " ";
						queryString += args[i++];
					}
					if(queryString.isEmpty())
					{
						System.out.println("Empty research concept terms - Exiting");
						System.exit(-1);
					}
				}
				else if (arg.equals("-tsearch"))
				{
					functionType = RESEARCH_TERMS_SEARCH;
					command = researchTermSearchCommand;

					// add all remaining arguments as query string
					queryString = args[i++];
					while(i < args.length)
					{
						queryString += " ";
						queryString += args[i++];;
					}
					if(queryString.isEmpty())
					{
						System.out.println("Empty query terms - Exiting");
						System.exit(-1);
					}
				}	            
				else if (arg.equals("-pindex"))
				{
					functionType = INDEX_PEOPLE;
					command = indexPeopleCommand;

					if(solrPeopleDocumentsPath.isEmpty())
					{
						// default path - from working directory
						solrPeopleDocumentsPath = System.getProperty("user.dir") + "/people_data";
					}
				}
				else if (arg.equals("-cindex"))
				{
					functionType = INDEX_CONCEPTS;
					command = indexConceptsCommand;

					if(solrConceptsDocumentsPath.isEmpty())
					{
						// default path - from working directory
						solrConceptsDocumentsPath = System.getProperty("user.dir") + "/concept_data";
					}
				}
				else if (arg.equals("-index"))
				{
					functionType = INDEX_ALL;
					command = indexAllCommand;

					if(solrConceptsDocumentsPath.isEmpty())
					{
						// default path - from working directory
						solrConceptsDocumentsPath = System.getProperty("user.dir") + "/concept_data";
					}
				}
			}

			csExpertSearcher.setFunctionType(functionType);
			csExpertSearcher.setSearchString(queryString);

			resultString = "Computer Science Expert Finder from Web:\n";

			// Perform requested function
			commandExecutor.executeCommand(command);

			FileUtilities.stringToFile(resultString, resultFilePath);
			System.out.println(resultString);
		}
		catch(SolrServerException solrException)
		{
			System.out.println("Solr server exception message: " + solrException.getMessage());
		}
		catch(IOException ioex)
		{
			System.out.println("IO exception message: " + ioex.getMessage());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}