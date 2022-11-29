package mas.project.boezio.ay2122.ontology;

import jade.content.Concept;

public class SchoolClass implements Concept {

    private int year;
    private String letter;

    public SchoolClass(){
        this.year = 0;
        this.letter = "";
    }

    public SchoolClass(int year, String letter) {
        this.year = year;
        this.letter = letter;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    @Override
    public String toString() {
        return year + "" + letter;
    }
}
