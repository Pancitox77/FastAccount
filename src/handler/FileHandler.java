package handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;

import util.Account;
import util.Formatter;

public class FileHandler {
    /**
     * Los datos se guardan en un archivo txt (.data, pero es lo mismo)
     * Cada cuenta se escribe en una sola línea, con el formato:
     * ' nombre email contraseña '.
     * Para las cuentas que usan Facebook, Google, etc. se reemplaza email por:
     * ' @cuenta '. Ejemplo: ' @Google '
     */

    private File accountFile;
    private List<Account> accounts;

    public FileHandler() {
        // Buscar el archivo data/accounts.data y crearlo si no existe
        try {
            this.accountFile = new File(getClass().getResource("/data").toURI().getPath(), "accounts.data");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (!this.accountFile.exists()) {
            createDefaultFile();
            accounts = new ArrayList<>();
            save(accounts);

        } else
            accounts = readFile();
    }

    private void createDefaultFile() {
        try {
            boolean created = this.accountFile.createNewFile();
            System.out.println((created ? "Creando datos porque no existen" : "No se ha podido crear el archivo"));
        } catch (IOException e) {
            System.out.println("Error al crear el archivo: " + e);
        }
    }

    public void save(List<Account> accountList) {
        try (FileWriter writer = new FileWriter(accountFile)) {
            for (Account account : accountList)
                writer.append(Formatter.toString(account) + "\n");

        } catch (IOException e) {
            System.out.println("Error al guardar");
            e.printStackTrace();
        }
    }

    private List<Account> readFile() {
        List<Account> accountList = new ArrayList<>();

        try (Scanner scanner = new Scanner(accountFile)) {
            while (scanner.hasNextLine())
                accountList.add(Formatter.fromString(scanner.nextLine()));

        } catch (FileNotFoundException e) {
            System.err.print("No existe el archivo");
        }
        return accountList;
    }

    public void exportJson(JSONArray array){
        try {
            File file = new File(System.getProperty("user.dir"), "cuentas.json");
            FileWriter writer = new FileWriter(file);

            boolean created = file.createNewFile();
            System.out.println((created ? "Archivo json creado.." : "Sobreescribiendo json.."));

            writer.write(array.toJSONString());
            writer.close();
        } catch (IOException e) { e.printStackTrace(); }

    }

    public List<Account> getAccounts() {
        return accounts;
    }
}
