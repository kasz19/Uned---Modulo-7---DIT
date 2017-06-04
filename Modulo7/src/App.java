import java.io.IOException;

public class App {
	
	public static void main(String [] args) throws IOException, ClassNotFoundException{
		
		Service service = new Service();
//		String [] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
		String dest = "J:/UNED/Descubrimiento de información en textos/Tema_7/test set";
		String origin = "J:/UNED/Descubrimiento de información en textos/Tema_7/training set";
//		int maxPositions = 100;
//		int numFiles = 300;
		//service.moveFiles(numFiles, origin, dest, letters, maxPositions);
		
		service.procesar(origin, dest);
	}
	
}
