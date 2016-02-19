package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		
		HashMap<String,Occurrence> kws = new HashMap<String, Occurrence>();
		
		Scanner sc = new Scanner(new File(docFile));
		while (sc.hasNext()) {
			String word = sc.next();
			if (!kws.containsKey(getKeyWord(word))){
				String newWord = getKeyWord(word);
				if (newWord!=null){
					kws.put(newWord,new Occurrence(docFile, 1));
				}
			} else {
				Occurrence occ = kws.get(getKeyWord(word));
				occ.frequency++;
				String newWord = getKeyWord(word);
				if (newWord!=null){
					kws.put(newWord,occ);
				}
			}
		}
		return kws;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		Set<String> keys = kws.keySet();  // Set is of type String since keys are Strings
		Iterator<String> iterator = keys.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (this.keywordsIndex.containsKey(key)){
				this.keywordsIndex.get(key).add(kws.get(key));
				ArrayList<Integer> intList = insertLastOccurrence(this.keywordsIndex.get(key));
			} else {
				ArrayList<Occurrence> oList = new ArrayList<Occurrence>();
				oList.add(kws.get(key));
				this.keywordsIndex.put(key, oList);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {

		word = word.toLowerCase();
		
		if (word.charAt(0) == '.' || word.charAt(0) == ',' || word.charAt(0) == '?' || word.charAt(0) == ':' || 
			word.charAt(0) == ';' || word.charAt(0) == '!' || word.charAt(0) == '\''|| word.charAt(0) == '_' || 
			word.charAt(0) == ')' || word.charAt(0) == '(' || word.charAt(0) == '"'){
			for (int i = 1; i < word.length(); i++){
				if (word.charAt(i) != '.' && word.charAt(i) != ',' && word.charAt(i) != '?' && 
					word.charAt(i) != ':' && word.charAt(i) != ';' && word.charAt(i) != '!'  && 
					word.charAt(i) != '\''&& word.charAt(i) != '_' && word.charAt(i) != ')' && 
					word.charAt(i) != '(' && word.charAt(i) != '"'){
					word = word.substring(i);
				}
			}
		}
		
		if (word.charAt(word.length()-1) == '.' || word.charAt(word.length()-1) == ',' || word.charAt(word.length()-1) == '?' || 
			word.charAt(word.length()-1) == ':' || word.charAt(word.length()-1) == ';' || word.charAt(word.length()-1) == '!' || 
			word.charAt(word.length()-1) == '\''|| word.charAt(word.length()-1) == '_' || word.charAt(word.length()-1) == ')' || 
			word.charAt(word.length()-1) == '(' || word.charAt(word.length()-1) == '"'){
			for (int i = word.length()-2; i >= 0; i--){
				if (word.charAt(i) != '.' && word.charAt(i) != ',' && word.charAt(i) != '?' && 
					word.charAt(i) != ':' && word.charAt(i) != ';' && word.charAt(i) != '!'  && 
					word.charAt(i) != '\''&& word.charAt(i) != '_' && word.charAt(i) != ')' && 
					word.charAt(i) != '(' && word.charAt(i) != '"'){
					word = word.substring(0, i+1);
				}
			}
		}
		
		for(int i = 0; i <= word.length()-1; i++){
			if (word.charAt(i) == '.' || word.charAt(i) == ',' || word.charAt(i) == '?' || 
				word.charAt(i) == ':' || word.charAt(i) == ';' || word.charAt(i) == '!' || 
				word.charAt(i) == '.' || word.charAt(i) == ',' || word.charAt(i) == '?' || 
				word.charAt(i) == ':' || word.charAt(i) == ';' || word.charAt(i) == '!' || word.charAt(i) == '\''){
				return null;
			}	
		}
		
		for (int i = 0; i <= noiseWords.size()-1; i++){
			if (noiseWords.containsKey(word)){
				return null;
			}
		}
		
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		int i = 0; int f = occs.size()-2; int m = (f-i)/2; int target = occs.get(occs.size()-1).frequency;

		while (true){
			if (occs.get(m).frequency == target || (i == m && target > occs.get(m).frequency)){
				occs.add(m, occs.get(occs.size()-1));
				occs.remove(occs.size()-1);
				break;
			} else if (i == m && target < occs.get(m).frequency){
				if (target < occs.get(f).frequency){
					break;
				} else {
					occs.add(m+1, occs.get(occs.size()-1));
					occs.remove(occs.size()-1);
					break;
				}
			} else if (occs.get(m).frequency < target){
				f = m; m = (f-i)/2 + i;
			} else {
				i = m; m = (f-i)/2 + i;
			}
		}
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int k = 0; k < occs.size(); k++){
			list.add(occs.get(k).frequency);
		}
		return list;
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {

		ArrayList<Occurrence> docs = new ArrayList<Occurrence>();
		
		if (!keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2)){return null;}
		
		if (keywordsIndex.containsKey(kw1) && keywordsIndex.containsKey(kw2)){
			for (int i = 0; i < keywordsIndex.get(kw1).size() && i < 5; i++){
				docs.add(keywordsIndex.get(kw1).get(i));
			}
			for (int i = 0; i < keywordsIndex.get(kw2).size() && i < 5; i++){
				boolean added = false; boolean existed = false;
				for (int j = 0; j < docs.size() && !added; j++){
					if (docs.get(j).document == keywordsIndex.get(kw2).get(i).document){
						existed = true;
						if (keywordsIndex.get(kw2).get(i).frequency > docs.get(j).frequency){
							docs.remove(j);
							for (int k = 0; k < docs.size(); k++){
								if (keywordsIndex.get(kw2).get(i).frequency >= docs.get(k).frequency){
									docs.add(k, keywordsIndex.get(kw2).get(i));
									added = true;
									break;
								}
							}
							if (!added && docs.size() < 5){
								docs.add(keywordsIndex.get(kw2).get(i));
							}
						}
					}
				}
				for (int k = 0; k < docs.size() && !added && !existed; k++){
					if (keywordsIndex.get(kw2).get(i).frequency >= docs.get(k).frequency){
						docs.add(k, keywordsIndex.get(kw2).get(i));
						added = true;
						break;
					}
				}
				if (!added && docs.size() < 5 && !existed){
					docs.add(keywordsIndex.get(kw2).get(i));
				}
			}
		} else if (keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2)){
			for (int i = 0; i < keywordsIndex.get(kw1).size() && i < 5; i++){
				docs.add(keywordsIndex.get(kw1).get(i));
			}
		} else if (!keywordsIndex.containsKey(kw1) && keywordsIndex.containsKey(kw2)){
			for (int i = 0; i < keywordsIndex.get(kw2).size() && i < 5; i++){
				docs.add(keywordsIndex.get(kw2).get(i));
			}
		}
		
		ArrayList<String> docsList = new ArrayList<String>();
		System.out.println(docs);
		
		for (int i = 0; i < docs.size(); i++){docsList.add(docs.get(i).document);}
		
		return docsList;
	}
}
