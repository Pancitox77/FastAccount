package util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

public class AccountHandler {
    private static final String NO_ACCOUNT_FINDED = "No se ha encontrado la cuenta";
    private static final String EMPTY_LIST_ERROR = "No hay cuentas cargadas";

    private List<Account> accounts;
    private FileHandler fileHandler;
    private boolean verbose = false;

    public AccountHandler() {
        fileHandler = new FileHandler();
        this.accounts = fileHandler.getAccounts();
        Collections.sort(accounts, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
    }

    public void addAccount(Account account) {
        accounts.add(account);
        Collections.sort(accounts, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        onVerbose("Se ha agregado la cuenta: " + account.formated(false));
        fileHandler.save(accounts);
    }

    public void deleteAccount(String name) {
        List<Account> toRemove = accounts.stream().filter(a -> a.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());

        if (toRemove.isEmpty())
            System.out.println(NO_ACCOUNT_FINDED);

        else {
            for (Account account : toRemove) {
                accounts.remove(account);
                onVerbose(name + " eliminado");
            }
            fileHandler.save(accounts);
        }
    }

    public String searchAccount(String name) {
        if (accounts.isEmpty())
            return EMPTY_LIST_ERROR;

        List<Account> matchingAccounts = accounts.stream().filter(a -> a.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
        if (matchingAccounts.isEmpty())
            return NO_ACCOUNT_FINDED;
        return matchingAccounts.stream().map(a -> a.formated(verbose)).collect(Collectors.joining("\n"));
    }

    public String findByEmail(String email) {
        if (accounts.isEmpty())
            return EMPTY_LIST_ERROR;

        List<Account> accountsFound = accounts.stream()
                .filter(ac -> ac.getEmail().equalsIgnoreCase(email.toLowerCase()))
                .collect(Collectors.toList());

        int results = accountsFound.size();
        if (results == 0)
            return "No se ha encontrado el email";
        else if (results == 1)
            System.out.println("Se ha encontrado 1 cuenta:");
        else
            System.out.println("Se han encontrado " + results + " cuentas:");

        return accountsFound.stream()
                .map(acc -> acc.formated(verbose))
                .collect(Collectors.joining("\n"));
    }

    public String findByTagName(String tag) {
        if (accounts.isEmpty())
            return EMPTY_LIST_ERROR;

        List<Account> accountsFound = accounts.stream()
                .filter(ac -> ac.getTag().equalsIgnoreCase(tag.toLowerCase()))
                .collect(Collectors.toList());

        int results = accountsFound.size();
        if (results == 0)
            return "No se ha encontrado la etiqueta";
        else if (results == 1)
            System.out.println("Se ha encontrado 1 cuenta:");
        else
            System.out.println("Se han encontrado " + results + " cuentas:");

        return accountsFound.stream()
                .map(acc -> acc.formated(verbose))
                .collect(Collectors.joining("\n"));
    }

    public String listAccounts() {
        if (accounts.isEmpty())
            return EMPTY_LIST_ERROR;

        StringBuilder builder = new StringBuilder();
        Account lastAccount = this.accounts.get(accounts.size() - 1);

        for (Account account : this.accounts) {
            builder.append(account.formated(verbose));
            if (account != lastAccount)
                builder.append("\n");
        }
        return builder.toString();
    }

    public void clearAllData() {
        onVerbose("Se han eliminado " + accounts.size() + " cuentas.");
        this.accounts.clear();
        fileHandler.save(accounts);
    }

    /* Util */

    private void onVerbose(String message) {
        if (verbose)
            System.out.println(message);
    }

    /* Getters/Setters */

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

}
