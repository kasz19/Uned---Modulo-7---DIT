import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Service {
	
	private static final String FILENAME = "./StopWords.txt";
	
	public void moveFiles(int numFiles, String origin, String dest, String[] possibleLetters, int maxPositions) {
		try {
			int cont = 0;
			Set<String> containedIds = new HashSet<String>();
			
			for (int i = 0; i < numFiles; i++) {
				String fileName = getRandomId(possibleLetters, maxPositions, containedIds);
				containedIds.add(fileName);
				File root = new File(origin + "/" + fileName);
				File destination = new File(dest + "/" + fileName);
				cutFileUsingStream(root, destination);
				if(cont == 100){
					cont = 0;
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


private static void cutFileUsingStream(File source, File dest) throws IOException {
    InputStream is = null;
    OutputStream os = null;
    try {
        is = new FileInputStream(source);
        os = new FileOutputStream(dest);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
    } finally {
        is.close();
        os.close();
        source.delete();
    }
}

private static String getRandomId(String [] possibleLetters, int maxPositions, Set<String> containedIds){
	
	String id;
	do{
		id = "";
		int maxLetters = possibleLetters.length;
		id += possibleLetters[ThreadLocalRandom.current().nextInt(0, maxLetters)];
		String number = "" + ThreadLocalRandom.current().nextInt(1, maxPositions);
		while(number.length() < 4){
			number = "0" + number;
		}
		id = id + number + ".txt";
	}while(containedIds.contains(id));
	return id;
	
}


public void procesar(String trainingSetDirectory, String testSetDirectory) throws IOException, ClassNotFoundException{
	
	Set<String> stopWords = Utils.getWords(FILENAME);
	
	Set<File> files = Utils.getFiles(trainingSetDirectory);
	int totalDocuments = files.size();
	// Initialize the tagger
    MaxentTagger tagger = new MaxentTagger("taggers/left3words-wsj-0-18.tagger");
    HashMap<String, String> taggedWords = new HashMap<String, String> ();
    
	HashMap<String, Long> vocabularyTotalCount = new HashMap<String, Long> (); 
	HashMap<String, Long> vocabularyByFileCount = new HashMap<String, Long> (); 
	Set<Tupla> tuplas = new HashSet<Tupla>();
    
	// Por cada fichero
	for (File f : files){
		// 1. Recuperamos las palabras (Solo adjetivos y nombres)
		List<String> words = Utils.getWordsHtml(f, stopWords, tagger, taggedWords);
		
		//2. Las incluimos en el vocabulario (totales)
		for(String word : words){
			vocabularyTotalCount.put(word, vocabularyTotalCount.containsKey(word) ? vocabularyTotalCount.get(word) + 1 : 1);
			vocabularyByFileCount.put(word, vocabularyByFileCount.containsKey(word) ? vocabularyByFileCount.get(word) + 1 : 1);
		}
		
		//3 Añadimos tuplas con sus pesos correspondientes (Term Frequency - TF)
		for(String word : words){
			long occurences = vocabularyByFileCount.get(word).longValue();
			if(occurences < 100 || occurences > 700){
				continue;
			}
			Tupla tupla = new Tupla();
			tupla.setDocument(f.getName());
			tupla.setWord(word);
			BigDecimal count = new BigDecimal(occurences);
			tupla.setWeight(count.divide(new BigDecimal(words.size()), 15, RoundingMode.HALF_UP));
			tupla.setOcurrences(occurences);
			BigDecimal log = new BigDecimal(Math.log10(totalDocuments / tupla.getOcurrences()));
			BigDecimal resul = log.multiply(tupla.getWeight());
			tupla.setGlobalWeight(resul);
			tuplas.add(tupla);
		}
		vocabularyByFileCount = new HashMap<String, Long> ();
		
	}

	
	File fileOutput = new File(testSetDirectory + "/vocabulary.arff");
    PrintWriter writerOutput = new PrintWriter(fileOutput, "UTF-8");
    writerOutput.println("@RELATION Vocabulary");
    writerOutput.println("");
    writerOutput.println("@ATTRIBUTE document string");
    writerOutput.println("@ATTRIBUTE word string");
    writerOutput.println("@ATTRIBUTE ocurrences numeric");
    writerOutput.println("@ATTRIBUTE weight real ");
    writerOutput.println("@ATTRIBUTE globalWeight real");
    
    writerOutput.println("");
    writerOutput.println("@DATA");
	// Por cada palabra y cada documento
	for(Tupla tupla : tuplas){
		writerOutput.println(tupla.getDocument() + "," +  tupla.getWord() + "," + tupla.getOcurrences() + "," +  tupla.getWeight() + "," + tupla.getGlobalWeight());
	}
	writerOutput.close();
	System.out.println("END :::");
//	

}




private int getOcurrences(Tupla tuplaParam, Set<Tupla> totalWords) {
		int res = 0;
		for(Tupla tupla : totalWords){
			if(tupla.getWord().equals(tuplaParam.getWord())){
				res++;
			}
		}
		return res;
	}


private void addVocabulary(String w, Long c, HashMap<String, Long> importantVocabulary) {
	 if(c.longValue() > 100 && c.longValue() < 700) 
     {
		 importantVocabulary.put(w, c);
	 }
}





}