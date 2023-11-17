package main.test;

public class Test {
    public static void main(String[] args) {
        String[] parts = "Academia.edu|jhourcade77@gmail.com|null|null|null".split("\\|");
        System.out.println("Parts: " + parts.length);

        for(String i: parts){
            System.out.println(i);
        }
    }
}
