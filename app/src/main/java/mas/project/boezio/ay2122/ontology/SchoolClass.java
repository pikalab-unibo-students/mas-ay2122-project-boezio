package mas.project.boezio.ay2122.ontology;

import jade.content.Concept;

public class SchoolClass implements Concept {

    private int year;
    private String letter;

    public SchoolClass(int year, String letter){
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

    public static SchoolClass parseSchoolClass(String schoolClass){
        int year = Integer.parseInt(String.valueOf(schoolClass.charAt(0)));
        String letter = String.valueOf(schoolClass.charAt(1));
        return new SchoolClass(year, letter);
    }
}
