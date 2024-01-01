package handler;

import java.util.List;
import java.util.stream.Collectors;

import util.Account;
import util.Formatter;

import java.util.ArrayList;
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

    public String listAllAccounts(int offset, int limit, boolean revert, boolean numerate) {
        if (accounts.isEmpty()) return EMPTY_LIST_MESSAGE;
        if(limit == -1) limit = accounts.size();

        // Control de errores
        if(offset < 0) return "La opción '--offset' debe ser igual o mayor a 1.";
        if(limit < offset) return "El límite debe ser mayor al offset.";

        // Leer cuentas
        StringBuilder builder = new StringBuilder();
        Account lastAccount = this.accounts.get(accounts.size() - 1);
        String results = "Se han cargado " + (limit-offset+1) + " cuentas (desde ";

        // Revertir lista (si es necesario)
        if(revert){
            results +=  limit + " hasta " + offset + ")\n\n";

            for(int i=limit-1; i>=offset-1; i--){
                appendAccount(i, builder, lastAccount, numerate);
            }

        } else {
            results +=  offset + " hasta " + limit + ")\n\n";
            for(int i=offset-1; i<limit; i++){
                appendAccount(i, builder, lastAccount, numerate);
            }
        }
        builder.insert(0, results);
        return builder.toString();
    }

    /* Acciones */

    public void addAccount(Account account) {
        accounts.add(account);
        Collections.sort(accounts, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        onVerbose("Se ha agregado la cuenta " + account.getName());
        fileHandler.save(accounts);
    }

    public void removeAccount(Account acc){
        accounts.remove(acc);
        fileHandler.save(accounts);
    }

    public void removeAccountByName(String name) {
        List<Account> toRemove = accounts.stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());

        if (toRemove.isEmpty())
            System.out.println(NO_RESULTS_FINDED_MESSAGE);

        else {
            accounts.remove(toRemove.get(0));
            onVerbose(name + " eliminado");
            fileHandler.save(accounts);
        }
    }

    public void removeAccountsByEmail(String email){
        List<Account> toRemove = accounts.stream()
            .filter(a -> a.getEmail().equalsIgnoreCase(email))
            .collect(Collectors.toList());

        if (toRemove.isEmpty())
            System.out.println(NO_RESULTS_FINDED_MESSAGE);

        else{
            for (Account account: toRemove){
                accounts.remove(account);
                onVerbose(email+" eliminada");
            }
            fileHandler.save(accounts);
        }
    }

    public void removeAccountsByTag(String tag){
        List<Account> toRemove = accounts.stream()
            .filter(a -> a.hasTag(tag))
            .collect(Collectors.toList());

        if (toRemove.isEmpty())
            System.out.println(NO_RESULTS_FINDED_MESSAGE);

        else{
            for (Account account : toRemove){
                accounts.remove(account);
                onVerbose(tag+" eliminada");
            }
        }
    }

    public void removeAllAccounts() {
        onVerbose("Se han eliminado " + accounts.size() + " cuentas.");
        this.accounts.clear();
        fileHandler.save(accounts);
    }

    /* Opciones de búsqueda y filtrado */

    public List<Account> searchAccountsByName(String name, boolean strict, boolean caseSensitive) {
        if (accounts.isEmpty())
            return new ArrayList<>(0);

        if(strict)
            return accounts.stream()
                .filter(acc -> acc.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
        
        if(caseSensitive)
            return accounts.stream()
                .filter(acc -> acc.getName().equals(name))
                .collect(Collectors.toList());

        return accounts.stream()
                .filter(a -> a.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }


    public List<Account> searchAccountsByEmail(String email) {
        if (accounts.isEmpty())
            return new ArrayList<>(0);

        return accounts.stream()
                .filter(acc -> acc.getEmail() != null && acc.getEmail().contains(email))
                .collect(Collectors.toList());
    }


    public List<Account> searchAccountsByUser(String user) {
        if (accounts.isEmpty())
            return new ArrayList<>(0);

        return accounts.stream()
                .filter(a -> a.getUser() != null && a.getUser().equalsIgnoreCase(user))
                .collect(Collectors.toList());
    }


    public List<Account> searchAccountsByTag(String tag) {
        if (accounts.isEmpty())
            return new ArrayList<>(0);

        return accounts.stream()
            .filter(acc -> acc.hasTag(tag))
            .collect(Collectors.toList());
    }

    /* Opciones de edición */

    public void updateAccount(Account updatedAccount){
        int index = -1;
        for (int i=0;i < accounts.size();i++){
            if (accounts.get(i).getName().equalsIgnoreCase(updatedAccount.getName())){
                index = i;
                break;
            }
        }

        if (index != -1){
            accounts.remove(index);
            accounts.add(index, updatedAccount);
            fileHandler.save(accounts);
        } else
            System.out.println("No se ha encontrado la cuenta con el nombre: " + updatedAccount.getName());
    }

    public void updateAccount(String oldName, Account updatedAccount){
        int index = -1;
        for (int i=0;i < accounts.size();i++){
            if (accounts.get(i).getName().equalsIgnoreCase(oldName)){
                index = i;
                break;
            }
        }

        if (index != -1){
            accounts.remove(index);
            accounts.add(index, updatedAccount);
            fileHandler.save(accounts);
        } else
            System.out.println("No se ha encontrado la cuenta con el nombre: " + oldName);
    }

    /* Util */

    private void onVerbose(String message) {
        if (descriptiveMode)
            System.out.println(message);
    }

    private void appendAccount(int i, StringBuilder builder, Account lastAccount, boolean numerate){
        Account account = this.accounts.get(i);
        if(numerate) builder.append((i + 1) + ". ");
        builder.append(Formatter.format(account, descriptiveMode));
        if (account != lastAccount)
            builder.append("\n");
    }

    /* Getters/Setters */

    public void setDescriptiveMode(boolean verbose) {
        this.descriptiveMode = verbose;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }
}
