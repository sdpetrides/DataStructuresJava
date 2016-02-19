package search;

import java.io.FileNotFoundException;

public class LSEDriver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LittleSearchEngine LSE = new LittleSearchEngine();
		try {
			LSE.makeIndex("docs.txt", "noisewords.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(LSE.top5search("kjh", "jhffg"));
		
	}

}
