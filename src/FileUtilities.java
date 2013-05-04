import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * The Class FileUtility contains file utility/helper methods.
 */
public class FileUtilities
{
	/**
	 * Gets the file list in directory
	 * @param directoryPath
	 * @return
	 */
	public static File[] getFileListInDir(String directoryPath)
	{
		File directory = new File(directoryPath);  
		File[] files = directory.listFiles();
		return files;
	}

	/**
	 * Puts string to file
	 *
	 * @param text: text to write on to file
	 * @param filePath: The file path
	 */
	public static void stringToFile(String text, String filePath)
	{
		try
		{
			File newTextFile = new File(filePath);

			FileWriter fw = new FileWriter(newTextFile);
			fw.write(text);
			fw.close();
		}
		catch (IOException ioex)
		{
			ioex.printStackTrace();
		}
	}

	/**
	 * Loads string from file
	 *
	 * @param file: The file to load
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String loadStringFromFile(File file) throws IOException
	{
		BufferedReader reader = new BufferedReader( new FileReader (file));
		String         line = null;
		StringBuilder  stringBuilder = new StringBuilder();
		String         ls = System.getProperty("line.separator");
		while( ( line = reader.readLine() ) != null )
		{
			stringBuilder.append( line );
			stringBuilder.append( ls );
		}
		reader.close();
		return stringBuilder.toString();
	}

	/**
	 * Gets the field list from tabular_data.
	 *
	 * @param file - the file
	 * @param field_number - the field number
	 * @return the _field_list_from_tabular_data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String[] getFieldListFromTabularData(File file, int field_number) throws IOException
	{
		String[] fieldValues = new String[CSExpertSearch.MAX_CONCEPT_QUERIES];
		//create BufferedReader to concept file
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = "";
		StringTokenizer st = null;
		int lineNumber = 0; 
		int tokenNumber = 0;

		//read space separated file line by line
		while ((line = br.readLine()) != null && lineNumber < CSExpertSearch.MAX_CONCEPT_QUERIES)
		{
			lineNumber++;

			//use space as token separator
			st = new StringTokenizer(line, "\t");

			while (st.hasMoreTokens())
			{
				tokenNumber++;
				//Add field
				if(tokenNumber == field_number)
					fieldValues[lineNumber - 1] = st.nextToken();
				else
					st.nextToken();
			}
			//reset token number
			tokenNumber = 0;
		}
		br.close();
		return fieldValues;
	} 
}
