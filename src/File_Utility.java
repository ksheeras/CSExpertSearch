import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class File_Utility {

	public static File[] get_file_list_in_dir(String directory_path){
		File directory = new File(directory_path);  
		File[] files = directory.listFiles();
		return files;
	}

	public static void string_to_file(String str, String file_path){
		try {
			File newTextFile = new File(file_path);

			FileWriter fw = new FileWriter(newTextFile);
			fw.write(str);
			fw.close();
		} catch (IOException iox) {
			iox.printStackTrace();
		}
	}

	public static String load_string_from_file(File file) throws IOException{
		BufferedReader reader = new BufferedReader( new FileReader (file));
		String         line = null;
		StringBuilder  stringBuilder = new StringBuilder();
		String         ls = System.getProperty("line.separator");
		while( ( line = reader.readLine() ) != null ) {
			stringBuilder.append( line );
			stringBuilder.append( ls );
		}
		reader.close();
		return stringBuilder.toString();
	}

	public static String[] get_field_list_from_tabular_data(File file, int field_number) throws IOException
	{
		String[] field_values = new String[CS_Expert_Search.MAX_CONCEPT_QUERIES];
		//create BufferedReader to concept file
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = "";
		StringTokenizer st = null;
		int lineNumber = 0; 
		int tokenNumber = 0;
		
		//read space separated file line by line
		while ((line = br.readLine()) != null && lineNumber < CS_Expert_Search.MAX_CONCEPT_QUERIES) {
			lineNumber++;

			//use space as token separator
			st = new StringTokenizer(line, "\t");

			while (st.hasMoreTokens()) {
				tokenNumber++;
				//Add field
				if(tokenNumber == field_number)
					field_values[lineNumber - 1] = st.nextToken();
				else
					st.nextToken();
			}
			//reset token number
			tokenNumber = 0;
		}
		br.close();
		return field_values;
	} 
}