

import java.math.BigDecimal;

public class Tupla {
	private String document;
	private BigDecimal weight;
	private BigDecimal globalWeight;
	private String word;
	private long ocurrences;
	
	
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public BigDecimal getWeight() {
		return weight;
	}
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public long getOcurrences() {
		return ocurrences;
	}
	public void setOcurrences(long ocurrences) {
		this.ocurrences = ocurrences;
	}
	public BigDecimal getGlobalWeight() {
		return globalWeight;
	}
	public void setGlobalWeight(BigDecimal globalWeight) {
		this.globalWeight = globalWeight;
	}
	
	
}
