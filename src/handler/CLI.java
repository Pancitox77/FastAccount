package handler;

import util.Help;
import util.ModeProcessor;

public class CLI {
    private AccountHandler accountHandler;
    private Help help;
    private String[] args;

    public static final String HELP_MSJ = "Usa 'help' para más información.";

    public CLI(String[] args){
        this.args = args;

        accountHandler = new AccountHandler();
        help = new Help();

        processFlags();
    }


    private void processFlags(){
        if(args.length == 0){
            print("No se han indicado opciones. " + HELP_MSJ);
            return;
        }

        String mode = args[0];
        String[] options = new String[args.length - 1];
        Flag.setFlags(Flag.fromNextArg(args));

        // Modo descriptivo
        accountHandler.setDescriptiveMode((Flag.hasFlag("-k", "--descriptive")));

        // Obtener opciones (si las hay)
        if(args.length > 1){
            for (int j=0; j<args.length-1; j++){
                options[j] = args[j + 1];
            }
        }

        // Indentificar el comando
        boolean descriptive = Flag.hasFlag("-k", "--descriptive");
        ModeProcessor processor = new ModeProcessor(this, descriptive);

        switch (mode) {
            case "add":
                print(processor.processAddMode());
                break;
        
            case "remove":
                print(processor.processRemoveMode());
                break;

            case "search":
                processor.processSearchMode();
                break;

            case "edit":
                processor.processEditMode();
                break;

            case "list":
                processor.processListMode();
                break;

            case "help":
                if(!hasOptions(options))
                    help.printGeneralHelp();
                else
                    help.printSpecificHelp(options[0]);
                break;

            default:
                print("No se ha encontrado el modo. " + HELP_MSJ);
                break;
        }
    }


    private void print(String msj){
        System.out.println(msj);
    }

    private boolean hasOptions(String[] options){
        return options.length > 0;
    }


    /* Getters */

    public AccountHandler getAccountHandler() {
        return accountHandler;
    }

    public String[] getArgs() {
        return args;
    }
}
