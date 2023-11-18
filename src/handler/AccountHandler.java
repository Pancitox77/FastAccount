package handler;

import java.util.List;
import java.util.stream.Collectors;

import util.Account;
import util.Formatter;

import java.util.Collections;

public class AccountHandler {
    private static final String NO_RESULTS_FINDED_MESSAGE = "No se han encontrado resultados";
    private static final String EMPTY_LIST_MESSAGE = "No hay cuentas cargadas";

    private List<Account> accounts;
    private FileHandler fileHandler;
    private boolean descriptiveMode = false;

    public AccountHandler() {
        fileHandler = new FileHandler();
        this.accounts = fileHandler.getAccounts();
        Collections.sort(accounts, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
    }

    /* Opciones de información */

    public String listAllAccounts() {
        if (accounts.isEmpty())
            return EMPTY_LIST_MESSAGE;

        StringBuilder builder = new StringBuilder();
        builder.append("Se han cargado " + accounts.size() + " cuentas\n");
        Account lastAccount = this.accounts.get(accounts.size() - 1);

        for (Account account : this.accounts) {
            builder.append(Formatter.format(account, descriptiveMode));
            if (account != lastAccount)
                builder.append("\n");
        }
        return builder.toString();
    }

    /* Acciones */

    public void addAccount(Account account) {
        accounts.add(account);
        Collections.sort(accounts, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        onVerbose("Se ha agregado la cuenta " + account.getName());
        fileHandler.save(accounts);
    }

    public void deleteAccountByName(String name) {
        List<Account> toRemove = accounts.stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());

        if (toRemove.isEmpty())
            System.out.println(NO_RESULTS_FINDED_MESSAGE);

        else {
            for (Account account : toRemove) {
                accounts.remove(account);
                onVerbose(name + " eliminado");
            }
            fileHandler.save(accounts);
        }
    }

    public void clearAllData() {
        onVerbose("Se han eliminado " + accounts.size() + " cuentas.");
        this.accounts.clear();
        fileHandler.save(accounts);
    }

    /* Opciones de búsqueda y filtrado */

    public String filterAccountsByName(String name) {
        if (accounts.isEmpty())
            return EMPTY_LIST_MESSAGE;

        List<Account> matchingAccounts = accounts.stream()
                .filter(a -> a.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());

        if (matchingAccounts.isEmpty())
            return NO_RESULTS_FINDED_MESSAGE;

        return matchingAccounts.stream()
                .map(a -> Formatter.format(a, descriptiveMode))
                .collect(Collectors.joining("\n"));
    }

    public String filterAccountsByEmail(String email) {
        if (accounts.isEmpty())
            return EMPTY_LIST_MESSAGE;

        List<Account> accountsFound = accounts.stream()
                .filter(a -> a.getEmail() != null && a.getEmail().toLowerCase().contains(email.toLowerCase()))
                .collect(Collectors.toList());

        boolean results = showResultsFinded("el email "+email, accountsFound.size());
        if (!results)
            return NO_RESULTS_FINDED_MESSAGE;

        return accountsFound.stream()
                .map(acc -> Formatter.format(acc, descriptiveMode))
                .collect(Collectors.joining("\n"));
    }

    public String filterAccountsByUser(String user) {
        if (accounts.isEmpty())
            return EMPTY_LIST_MESSAGE;

        List<Account> accountsFound = accounts.stream()
                .filter(a -> a.getUsername().equalsIgnoreCase(user))
                .collect(Collectors.toList());

        boolean results = showResultsFinded("el usuario " + user, accountsFound.size());
        if (!results)
            return NO_RESULTS_FINDED_MESSAGE;

        return accountsFound.stream()
                .map(acc -> Formatter.format(acc, descriptiveMode))
                .collect(Collectors.joining("\n"));
    }

    public String filterAccountsByTag(String tag) {
        if (accounts.isEmpty())
            return EMPTY_LIST_MESSAGE;

        List<Account> accountsFound = accounts.stream()
                .filter(ac -> {
                    List<String> tags = ac.getTags();
                    for (String t : tags) {
                        if (t.equalsIgnoreCase(tag.toLowerCase()))
                            return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());

        boolean results = showResultsFinded("la etiqueta " + tag, accountsFound.size());
        if (!results)
            return NO_RESULTS_FINDED_MESSAGE;

        return accountsFound.stream()
                .map(acc -> Formatter.format(acc, descriptiveMode))
                .collect(Collectors.joining("\n"));
    }

    /* Util */

    private void onVerbose(String message) {
        if (descriptiveMode)
            System.out.println(message);
    }

    private boolean showResultsFinded(String resultName, int results) {
        if (results == 0)
            return false;
        else if (results == 1)
            System.out.println("Se ha encontrado 1 cuenta con " + resultName + ":\n");
        else
            System.out.println("Se han encontrado " + results + " cuentas con " + resultName + ":\n");
        return true;
    }

    /* Getters/Setters */

    public void setDescriptiveMode(boolean verbose) {
        this.descriptiveMode = verbose;
    }
}
