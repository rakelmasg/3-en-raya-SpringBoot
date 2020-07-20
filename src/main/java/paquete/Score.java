package paquete;

public class Score {

	private String name;
	private int victories;		

	public Score(String name) {
		this.setName(name);
		this.victories = 1;
	}
	
	public int getVictories() {
		return victories;
	}

	public void addVictory() {
		this.victories++;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}		
}
