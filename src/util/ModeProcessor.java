package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import handler.AccountHandler;
import handler.CLI;
import handler.Flag;

public class ModeProcessor {
    private static final String NAME_FLAG = "--name";
    private static final String PASSWORD_FLAG = "--password";
    private static final String USER_FLAG = "--user";
    private static final String TAGS_FLAG = "--tags";
    private static final String EMAIL_FLAG = "--email";
    private static final String FORCE_FLAG = "--force";

    private String[] args;
    private AccountHandler accountHandler;
    private boolean descriptive;

    public ModeProcessor(CLI cli, boolean descriptive) {
        this.args = cli.getArgs();
        this.accountHandler = cli.getAccountHandler();
        this.descriptive = descriptive;
    }

    private static final String ARGS_NEEDED_MSJ = "Se deben indicar parámetros. " + CLI.HELP_MSJ;

    public String processAddMode(){
        String[] newArgs = Flag.fromNextArg(args);


        if(newArgs.length == 0) return ARGS_NEEDED_MSJ;

        // A) Pase rápido de argumentos
        if(newArgs[0].equals("auto")){
            if (newArgs.length >= 3) return addAutoMode(Flag.fromNextArg(newArgs));
            return "La opción 'auto' recibe como mínimo 3 parámetros. " +
                "Nombre (pueden ser varias palabras), email y contraseña";
        }

        // B) Pase de argumentos manual
        return addManualMode();
    }

    public String processRemoveMode(){
        String[] newArgs = Flag.fromNextArg(args);
        if(newArgs.length == 0) return ARGS_NEEDED_MSJ;

        if(!Flag.hasFlag("-f",FORCE_FLAG) && (!confirmRemoval())) return "Cancelando eliminación.";

        if(!newArgs[0].startsWith("-")){
            // Nombre de una cuenta
            accountHandler.removeAccountByName(newArgs[0]);
            return newArgs[0] + " eliminado";
        }

        if(Flag.hasFlag("-a", "--all")){
            // Elimina todas las cuentas
            accountHandler.removeAllAccounts();
            return "Todas las cuentas eliminadas";
        }

        String email = Flag.getFlagValue("-e", EMAIL_FLAG);
        if(email != null){
            // Elimina por correo electrónico
            accountHandler.removeAccountsByEmail(email);
            return "Eliminados usuarios con el correo: "+email;
        }

        String[] tags = Flag.getFlagValues("-t", TAGS_FLAG);
        if(tags.length > 1){
            // Elimina por etiquetas
            for(String tag : tags)
                accountHandler.removeAccountsByTag(tag);

            return "Eliminados usuarios con las etiquetas: "+Arrays.toString(tags);
        }

        return "No se han pasado los parámetros correctamente para la eliminación. " + CLI.HELP_MSJ;
    }

    public void processSearchMode(){
        String[] newArgs = Flag.fromNextArg(args);            
        if(newArgs.length == 0) System.out.println(ARGS_NEEDED_MSJ);
        
        if(!newArgs[0].startsWith("-"))
            searchByNameMode(newArgs[0]);


        String email = Flag.getFlagValue("-e", EMAIL_FLAG);
        if(email != null)
            searchByEmailMode(email);


        String user = Flag.getFlagValue("-u", USER_FLAG);
        if(user != null)
            searchByUserMode(user);


        String[] tags = Flag.getFlagValues("-t", TAGS_FLAG);
        if(tags.length > 0)
            searchByTagsMode(tags);
    }

    public void processEditMode(){
        String[] newArgs = Flag.fromNextArg(args);
        if(newArgs.length == 0){
            System.out.println(ARGS_NEEDED_MSJ);
            return;
        }

        boolean strict = Flag.hasFlag("-s", "--strict");
        boolean caseSensitive = Flag.hasFlag("-ins", "--case-insensitive");

        // Buscar las cuentas que coincidan
        List<Account> results = accountHandler.searchAccountsByName(newArgs[0], strict, caseSensitive);
        if(results.isEmpty()){
            System.out.println("No hay resultados para editar");
            return;
        }

        // Mostrar resultados
        System.out.println("Resultados:");
        for(int i=0; i<results.size(); i++){
            Account acc = results.get(i);
            System.out.println(i + ". " + acc.getName());
        }
        
        // Seleccionar cuenta a editar
        System.out.println("\nIngrese el N° de la cuenta a editar (-1 para cancelar):");
        Scanner scan2 = new Scanner(System.in);
        int index = scan2.nextInt();
        if(index == -1 || index >= results.size()) { scan2.close(); return; }

        Account acc = results.get(index);
        String oldName = acc.getName();

        System.out.println("\n" + Formatter.format(acc, true) + "\n");

        // Modo interactivo
        if (Flag.hasFlag("-i", "--interactive")){
            accountHandler.updateAccount(oldName, interactiveEdit(acc));
            scan2.close();
            return;
        }
        scan2.close();

        // Modo manual
        StringBuilder nameBuilder = new StringBuilder();
        String[] nameValues = Flag.getFlagValues("-n", NAME_FLAG);
        if(nameValues.length > 0)
            for (String s : nameValues)
                nameBuilder.append(s + " ");
        else 
            nameBuilder.append(acc.getName());

        acc.setName(getDefaultNullOrNew(nameBuilder.toString().trim(), acc.getName()));
        acc.setPassword(getDefaultNullOrNew(Flag.getFlagValue("-p", PASSWORD_FLAG), acc.getPassword()));
        acc.setEmail(getDefaultNullOrNew(Flag.getFlagValue("-e", EMAIL_FLAG), acc.getEmail()));
        acc.setUser(getDefaultNullOrNew(Flag.getFlagValue("-u", USER_FLAG), acc.getUser()));
        acc.setTags(getDefaultNullOrNewArray(Flag.getFlagValues("-t", TAGS_FLAG), acc.getTags()));

        accountHandler.updateAccount(acc);
        System.out.println(acc.getName() + " actualizado correctamente.");
    }

    @SuppressWarnings("unchecked")
    public void processListMode(){
        String offsetFlag = Flag.getFlagValue("--offset");
        String limitFlag = Flag.getFlagValue("--limit");

        int offset = (offsetFlag != null ? Integer.parseInt(offsetFlag) : 1);
        int limit = (limitFlag != null ? Integer.parseInt(limitFlag) : -1);
        boolean reverse = Flag.hasFlag("--reverse");
        boolean numerate = Flag.hasFlag("-n", "--numerate");

        System.out.println(accountHandler.listAllAccounts(offset, limit, reverse, numerate));

        if(!Flag.hasFlag("--export-json")) return;

        JSONArray array = new JSONArray();
        List<Account> accounts = accountHandler.getAccounts();
        for (Account acc : accounts) {
            JSONObject accountObject = new JSONObject();
            accountObject.put("nombre", acc.getName());
            accountObject.put("contraseña", acc.getPassword());
            accountObject.put("correo electrónico", acc.getEmail());
            accountObject.put("usuario", acc.getUser());
            accountObject.put("etiquetas", Arrays.toString(acc.getTags().toArray()));
            array.add(accountObject);
        }
        accountHandler.getFileHandler().exportJson(array);
        System.out.println("Cuentas exportadas a: bin/cuentas.json");
    }


    /* Específicos: Agregar */


    private String addAutoMode(String[] newArgs){
        Account account = new Account();

        String password = newArgs[newArgs.length - 1];
        String email = newArgs[newArgs.length - 2];
        
        String[] reArgs = Arrays.copyOfRange(newArgs, 0, newArgs.length - 2);

        StringBuilder nameBuilder = new StringBuilder();
        for (String i : reArgs)
            nameBuilder.append(i + " ");
        
        account.setName(nameBuilder.toString().trim());
        account.setEmail(email);
        account.setPassword(password);

        accountHandler.addAccount(account);
        return "La cuenta " + nameBuilder.toString().trim() + " ha sido agregada.";
    }

    private String addManualMode(){
        StringBuilder nameBuilder = new StringBuilder();
        String[] nameValues = Flag.getValuesNotFlagged();
        for (String s : nameValues)
            nameBuilder.append(s + " ");

        String name = nameBuilder.toString().trim();
        String email = Flag.getFlagValue("-e", EMAIL_FLAG);
        String password = Flag.getFlagValue("-p", PASSWORD_FLAG);
        String user = Flag.getFlagValue("-u", USER_FLAG);
        String[] tags = Flag.getFlagValues("-t", TAGS_FLAG);

        // Manejo de errores
        if(name == null) return "La cuenta debe tener un nombre.";

        // Agregar el argumento a la cuenta si no es nulo
        Account account = new Account();
        account.setName(name);
        if (email != null) account.setEmail(email);
        if (password != null) account.setPassword(password);
        if (user != null) account.setUser(user);
        if (tags.length != 0) account.setTags(Arrays.asList(tags));

        accountHandler.addAccount(account);
        return "La cuenta " + name + " ha sido agregada.";
    }


    /* Específicos: Eliminar */


    private boolean confirmRemoval(){
        System.out.println("¿Estás seguro que quieres eliminar la/s cuenta/s? [s/n]");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine().toLowerCase();
        scanner.close();

        return (answer.matches("s|si"));
    }


    /* Específicos: Buscar */


    private void searchByNameMode(String name){
        boolean strict = Flag.hasFlag("--strict");
        boolean caseSensitive = Flag.hasFlag("-ins", "--case-insensitive");

        List<Account> foundAccounts = accountHandler.searchAccountsByName(name, strict, caseSensitive);
        if(foundAccounts.isEmpty()) System.out.println("Cuenta no encontrada");

        else {
            printResults(foundAccounts.size());
            for (int i=1; i<=foundAccounts.size(); i++){
                Account account = foundAccounts.get(i-1);
                System.out.println(String.format("%d. %s%n", i, Formatter.format(account, descriptive)));
            }
        }
    }

    private void searchByEmailMode(String email){
        List<Account> foundAccounts = accountHandler.searchAccountsByEmail(email);
        if(foundAccounts.isEmpty()) System.out.println("Correo no encontrado.");
        else {
            printResults(foundAccounts.size());
            for (int i=1; i<=foundAccounts.size(); i++)
                System.out.println(Formatter.format(foundAccounts.get(i-1), descriptive));
        }
    }

    private void searchByUserMode(String user){
        List<Account> foundAccounts = accountHandler.searchAccountsByUser(user);
        if(foundAccounts.isEmpty()) System.out.println("Usuario no encontrado.");
        else {
            printResults(foundAccounts.size());
            for (int i=1; i<=foundAccounts.size(); i++)
                System.out.println(Formatter.format(foundAccounts.get(i-1), descriptive));
        }
    }

    private void searchByTagsMode(String[] tags){
        List<Account> foundAccounts = new ArrayList<>();
        for(String tag: tags)
            foundAccounts.addAll(accountHandler.searchAccountsByTag(tag));

        if(foundAccounts.isEmpty()) System.out.println("Etiqueta(s) no encontrada(s).");
        else {
            System.out.println("Se ha encontrado " + foundAccounts.size() + " cuenta(s):\n");
            for (int i=1; i<=foundAccounts.size(); i++)
                System.out.println(Formatter.format(foundAccounts.get(i-1), descriptive));
        }
    }


    /* Específicos: Editar */


    private Account interactiveEdit(Account account){
        Scanner scan = new Scanner(System.in);

        System.out.println("Escriba el nuevo valor, presione Enter para dejarlo como está o '-' para valor nulo.");

        String name = askEdit("Nombre", account.getName(), scan);
        String email = askEdit("Email", account.getEmail(), scan);
        String user = askEdit("Usuario", account.getUser(), scan);
        String password = askEdit("Contraseña", account.getPassword(), scan);

        String tagsArray = Arrays.toString(account.getTags().toArray(new String[account.getTags().size()]));
        String[] tags = askEdit("Tags (separadas por ',')", tagsArray, scan).split(",");

        scan.close();

        // Actualizar valores (pueden ser iguales, nulos o nuevos)
        Account editedAccount = new Account();

        // El nombre nunca es nulo. Puede ser igual o nuevo
        if(name.isBlank()) editedAccount.setName(account.getName());
        else editedAccount.setName(name);
        
        editedAccount.setEmail(getDefaultNullOrNew(email, account.getEmail()));
        editedAccount.setUser(getDefaultNullOrNew(user, account.getUser()));
        editedAccount.setPassword(getDefaultNullOrNew(password, account.getPassword()));
        editedAccount.setTags(getDefaultNullOrNewArray(tags, account.getTags()));

        System.out.println(Formatter.format(editedAccount, true));

        return editedAccount;
    }

    private String askEdit(String paramName, String oldValue, Scanner scan){
        System.out.println(paramName + " '" + oldValue + "' ->");
        return scan.nextLine();
    }

    private String getDefaultNullOrNew(String value, String defaultValue){
        if(value == null || value.isBlank()) return defaultValue;
        if(value.equals("-")) return null;
        return value;
    }

    private List<String> getDefaultNullOrNewArray(String[] array, List<String> defaultArray){
        if(array.length == 1 && array[0].isBlank()) return defaultArray;
        if(array.length > 0 && array[0].equals("-")) return Collections.emptyList();
        return Arrays.asList(array);
    }
    

    /* Util */


    private void printResults(int results){
        System.out.println("Se ha encontrado " + results + " cuenta(s):\n");
    }
}