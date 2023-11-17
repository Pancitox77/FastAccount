package main.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class FileTest {
    public static void main(String[] args) {
        try {
            File file = new File(FileTest.class.getResource("/main/test/").toURI().getPath(), "load.txt");
            boolean error = false;

            // comprobar línea por línea
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                String[] parts = line.split("\\|");

                if(parts.length != 5){
                    System.out.println("Error en la línea: "+line+"\nFormato incorrecto.");
                    error = true;
                    break;
                }
            }
            if(!error) System.out.println("El archivo no tiene errores");
            scanner.close();

        } catch (FileNotFoundException|URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
