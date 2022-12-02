package mas.project.boezio.ay2122.ontology;

import jade.content.Concept;

import java.util.Objects;

public class SchoolClass implements Concept {

    private int year;
    private String letter;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchoolClass that = (SchoolClass) o;
        return year == that.year && letter.equals(that.letter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, letter);
    }

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
