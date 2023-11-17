package handler;

import java.util.Arrays;
import java.util.function.Consumer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import util.Account;
import util.ISearch;

public class FlagsHandler {
    private static final String LIST = "list";
    private static final String HELP = "help";
    private static final String DESCRIPTIVE = "descriptive";
    private static final String VERSION = "version";

    private static final String CREATE = "create";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private static final String TAG = "tag";

    private static final String SEARCH_NAME = "search-name";
    private static final String SEARCH_EMAIL = "search-email";
    private static final String SEARCH_USER = "search-user";
    private static final String SEARCH_TAG = "search-tag";
    private static final String REMOVE = "remove";
    private static final String REMOVE_ALL = "remove-all";

    private AccountHandler accountHandler;
    private Options options;
    private CommandLine cmd;

    public FlagsHandler(String[] args) {
        accountHandler = new AccountHandler();
        options = initOptions();
        proccessFlags(args);
    }

    private Options initOptions() {
        Options opt = new Options();

        // Opciones de información
        opt.addOption("l", LIST, false, "Muestra todas las cuentas, por orden alfabético");
        opt.addOption("h", HELP, false, "Muestra esta ayuda");
        opt.addOption("k", DESCRIPTIVE, false, "Muestra mensajes de log adicionales");
        opt.addOption("v", VERSION, false, "Imprime la versión del software");

        // Opciones de creación
        Option create = Option.builder("c")
            .longOpt(CREATE)
            .hasArg()
            .argName("nombre de la cuenta")
            .desc("Crea una nueva cuenta con el nombre indicado")
            .build();
        Option createWithEmail = Option.builder("e")
            .longOpt(EMAIL)
            .hasArg()
            .argName("correo electronico")
            .desc("Solo al crear una cuenta. Agrega el correo electrónico a la cuenta")
            .build();
        Option createWithPassword = Option.builder("p")
            .longOpt(PASSWORD)
            .hasArg()
            .argName("contraseña")
            .desc("Solo al crear una cuenta. Agrega la contraseña a la cuenta")
            .build();
        Option createWithUser = Option.builder("u")
            .longOpt(USERNAME)
            .hasArg()
            .argName("usuario")
            .desc("Solo al crear una cuenta. Agrega el usuario a la cuenta")
            .build();
        Option createWithTags = Option.builder("t")
            .longOpt(TAG)
            .hasArgs()
            .argName("etiqueta1 etiqueta2 ...")
            .desc("Solo al crear una cuenta. Agrega las etiquetas separadas por espacios a la cuenta")
            .build();

        opt.addOption(create);
        opt.addOption(createWithEmail);
        opt.addOption(createWithPassword);
        opt.addOption(createWithUser);
        opt.addOption(createWithTags);

        // Opciones de acciones
        Option searchName = Option.builder()
            .longOpt(SEARCH_NAME)
            .hasArg()
            .argName("nombre de la cuenta")
            .desc("Busca las cuentas que contengan el nombre a buscar")
            .build();
        Option searchEmail = Option.builder()
            .longOpt(SEARCH_EMAIL)
            .hasArg()
            .argName("correo electrónico de la cuenta")
            .desc("Busca las cuentas que contengan el correo electrónico a buscar")
            .build();
        Option searchUser = Option.builder()
            .longOpt(SEARCH_USER)
            .hasArg()
            .argName("usuario a buscar")
            .desc("Busca las cuentas que contengan el usuario a buscar")
            .build();
        Option searchTag = Option.builder()
            .longOpt(SEARCH_TAG)
            .hasArg()
            .argName("etiqueta a buscar")
            .desc("Busca las cuentas que tengan la etiqueta a buscar")
            .build();
        Option remove = Option.builder("r")
            .longOpt(REMOVE)
            .hasArg()
            .argName("nombre de la cuenta")
            .desc("Elimina una cuenta concreta")
            .build();
        Option clear = Option.builder()
            .longOpt(REMOVE_ALL)
            .desc("Borra todos los datos de las cuentas")
            .build();
        
        opt.addOption(searchName);
        opt.addOption(searchEmail);
        opt.addOption(searchUser);
        opt.addOption(searchTag);
        opt.addOption(remove);
        opt.addOption(clear);

        return opt;
    }

    private void proccessFlags(String[] args){
        if (args.length == 0){
            System.out.println("No se ha indicado ninguna opción");
            System.out.println("Usa -h o --help para ver la ayuda");
            return;
        }

        try {
            cmd = new DefaultParser().parse(options, args);
            accountHandler.setDescriptiveMode(cmd.hasOption(DESCRIPTIVE));

            // Información
            onHasOption(LIST, n -> System.out.println(accountHandler.listAllAccounts()));
            onHasOption(HELP, n -> new HelpFormatter().printHelp("FastAccount", options));
            onHasOption(VERSION, n -> System.out.println("Versión 1.1.2"));

            // Creación
            onHasOption(CREATE, n -> {
                // La cuenta solo se crea si se indica esta opción
                Account account = new Account();
                account.setName(cmd.getOptionValue(CREATE));

                onHasOption(EMAIL, n2 -> account.setEmail(cmd.getOptionValue(EMAIL)));
                onHasOption(USERNAME, n3 -> account.setUsername(cmd.getOptionValue(USERNAME)));
                onHasOption(PASSWORD, n4 -> account.setPassword(cmd.getOptionValue(PASSWORD)));
                onHasOption(TAG, n5 -> account.setTags(Arrays.asList(cmd.getOptionValues(TAG))));

                accountHandler.addAccount(account);
            });

            // Acciones
            onHasOption(SEARCH_NAME, n -> showSearchResult(SEARCH_NAME, accountHandler::filterAccountsByName));
            onHasOption(SEARCH_EMAIL, n -> showSearchResult(SEARCH_EMAIL, accountHandler::filterAccountsByEmail));
            onHasOption(SEARCH_USER, n -> showSearchResult(SEARCH_USER, accountHandler::filterAccountsByUser));
            onHasOption(SEARCH_TAG, n -> showSearchResult(SEARCH_TAG, accountHandler::filterAccountsByTag));
            onHasOption(REMOVE, n -> {
                String name = cmd.getOptionValue(REMOVE);
                accountHandler.deleteAccountByName(name);
            });
            onHasOption(REMOVE_ALL, n -> accountHandler.clearAllData());


        } catch (ParseException e){
            System.out.println("Ha ocurrido un error. Algunas opciones requieren de uno o más parámetros.");
        }
    }

    /* Util */

    private void onHasOption(String optName, Consumer<Void> callback){
        if(cmd.hasOption(optName)) callback.accept(null);
    }

    private void showSearchResult(final String KEY, ISearch func){
        String searchValue = cmd.getOptionValue(KEY);
        System.out.println(func.call(searchValue) + "\n");
    }
}
