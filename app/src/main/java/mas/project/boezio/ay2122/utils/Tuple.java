package mas.project.boezio.ay2122.utils;

public class Tuple <A,B>{

    private final A a;
    private final B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getFirstElement(){
        return a;
    }

    public B getSecondElement(){
        return b;
    }
}
