package main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import util.Account;
import util.AccountHandler;

public class App {
    private static final String NEW = "new";
    private static final String SEARCH = "search";
    private static final String TAG = "tagged";
    private static final String DELETE = "delete";
    private static final String EMAIL = "email";
    private static final String LIST = "list";
    private static final String VERBOSE = "verbose";
    private static final String HELP = "help";
    private static final String VERSION = "version";
    private static final String REMOVE_ALL = "remove-all";

    private static AccountHandler accountHandler;

    public static void main(String[] args) {
        accountHandler = new AccountHandler();
        handleOptions(args);
    }

    private static void handleOptions(String[] args) {
        try {
            Options options = options();
            CommandLine cmd = new DefaultParser().parse(options, args);
            accountHandler.setVerbose(cmd.hasOption(VERBOSE));

            if (cmd.hasOption(HELP)) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("FastAccount", options);
                return;
            }

            if (cmd.hasOption(VERSION)) {
                System.out.println("Version 1.0");
                return;
            }
    
            if (cmd.hasOption(NEW)) {
                String[] accountData = cmd.getOptionValues(NEW);
                
                Account account;
                if (accountData.length >= 4)
                    account = new Account(accountData[0], accountData[1], accountData[2], accountData[3]);
                else
                    account = new Account(accountData[0], accountData[1], accountData[2]);

                accountHandler.addAccount(account);
                return;
            }
    
            if (cmd.hasOption(DELETE)) {
                accountHandler.deleteAccount(cmd.getOptionValue(DELETE));
                return;
            }

            if (cmd.hasOption(TAG)) {
                System.out.println(accountHandler.findByTagName(cmd.getOptionValue(TAG)));
                return;
            }

            if (cmd.hasOption(EMAIL)) {
                System.out.println(accountHandler.findByEmail(cmd.getOptionValue(EMAIL)));
                return;
            }
    
            if (cmd.hasOption(LIST)) {
                System.out.println(accountHandler.listAccounts());
                return;
            }
    
            if (cmd.hasOption(REMOVE_ALL)) {
                accountHandler.clearAllData();
                return;
            }
    
            if (cmd.hasOption(SEARCH)) {
                System.out.println(accountHandler.searchAccount(cmd.getOptionValue(SEARCH)));
                return;
            }
    
            System.out.println("No se ha indicado ninguna opción. Usa -h o --help para ver la ayuda.");
        } catch (ParseException e) {
            System.out.println("Ha ocurrido un error. Algunas opciones requieren de uno o más parámetros.");
        }
    }

    private static Options options() {
        Options options = new Options();

        // Acciones
        options.addOption("d", DELETE, true, "Elimina una cuenta por su nombre");
        options.addOption("l", LIST, false, "Lista todas las cuentas disponibles");

        Option newAccountOption = Option.builder("n")
                .longOpt(NEW)
                .hasArgs()
                .valueSeparator()
                // .numberOfArgs(3)
                .desc("Agrega una nueva cuenta")
                .build();
        options.addOption(newAccountOption);

        Option removeAllOption = Option.builder()
            .longOpt(REMOVE_ALL)
            .desc("Elimina todos los datos de las cuentas cargadas")
            .build();
        options.addOption(removeAllOption);

        // Información de la cuenta

        options.addOption("t", TAG, true,
                "Busca cuentas que tengan la etiqueta a buscar. La etiqueta se escribe sin '<>'");
        options.addOption("s", SEARCH, true, "Busca una cuenta por su nombre");
        options.addOption("e", EMAIL, true, "Listar cuentas que usen un email en específico");

        // Información adicional

        options.addOption("h", HELP, false, "Muestra esta ayuda");
        options.addOption("r", VERBOSE, false, "Muestra mensajes de log adicionales");
        options.addOption("v", VERSION, false, "Versión del CLI");
        return options;
    }
}
