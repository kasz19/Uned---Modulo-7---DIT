import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Utils {

	public static Set<String> getWords(String fileName) {

		BufferedReader br = null;
		FileReader fr = null;
		Set<String> resul = new HashSet<String>();
		try {

			fr = new FileReader(fileName);
			br = new BufferedReader(fr);

			String sCurrentLine;

			br = new BufferedReader(new FileReader(fileName));

			while ((sCurrentLine = br.readLine()) != null) {
				if (!sCurrentLine.isEmpty() && !resul.contains(sCurrentLine)){
					resul.add(sCurrentLine.toLowerCase());
				}
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
		return resul;
	}

	public static Set<File> getFiles(String directory) throws IOException {
		Set<File> resul = new HashSet<File>();

		try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
			paths.filter(Files::isRegularFile).forEach(f -> resul.add((f.toFile())));
		}

		return resul;
	}

	public static List<String> getWordsHtml(File f, Set<String> stopWords, MaxentTagger tagger, HashMap<String, String> taggedWords) throws IOException, ClassNotFoundException {
		
		List<String> resul = new ArrayList<String>();
	
		try(BufferedReader br = new BufferedReader(new FileReader(f))) {
		    for(String line; (line = br.readLine()) != null; ) {
		        resul.addAll(getWordsFromString(line, stopWords, tagger, taggedWords));
		    }
		    // line is not visible here.
		}
		
		
		return resul;
	}

	private static List<String> getWordsFromString(String line, Set<String> stopWords, MaxentTagger tagger, HashMap<String, String> taggedWords) {
		ArrayList<String> res = new ArrayList<String>();
		try{
		String lineStripped = line.replaceAll("<[^>]*>", "");	
		Pattern p = Pattern.compile("[\\w']+");
		Matcher m = p.matcher(lineStripped);
		
		while ( m.find() ) {
		    String word = lineStripped.substring(m.start(), m.end()).trim().toLowerCase();
		    word = word.replaceAll("[^A-Za-z]", "").toLowerCase(); // Eliminamos todo lo que no sea alfabetico
			if(word.isEmpty() || stopWords.contains(word)){ // Si es una "stopWord" no la contemplamos
				continue;
			}
		    String wordTagged = null;
		    if(taggedWords.containsKey(word)){
		    	 wordTagged = taggedWords.get(word);
		    }
		    else{
		    	 wordTagged = tagger.tagString(word);
		    	 taggedWords.put(word, wordTagged);
		    }
			String tag = wordTagged.substring(wordTagged.indexOf("/") + 1).trim();
			if(isAdjectiveOrNoun(tag) ){
				res.add(word.toLowerCase());
			}
			
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}
	
	private static boolean isAdjectiveOrNoun(String tag) {
		for(ValidWordType wt : ValidWordType.values()){
			if(tag.equals(wt.name())){
				return true;
			}
		}
		return false;
	}

	private static boolean  isNumber(String word) {
		try{
			Long.parseLong(word);
			return true;
		}catch(Exception e) 
		{
			return false;
		}
		
	}

}
