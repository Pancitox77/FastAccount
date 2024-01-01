package cli;

import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Help {
    private static final String CREATE = "create";

    private JSONArray items;
    private String[] modes;
    
    public Help(){
        loadJSON();
        modes = new String[items.size()];
    }

    public void printGeneralHelp(){
        print("\nUso: ./fast-account [modo] [opciones]");

        print("Modos disponibles:");
        print("· Acciones:");
        option(CREATE, "Agregar una cuenta");
        option("remove", "Eliminar una cuenta");
        option("search", "Buscar una cuenta");
        option("edit", "Editar una cuenta existente");

        print("\n· Información:");
        option("list", "Mostrar todas las cuentas");
        option("help", "Mostrar esta ayuda");

        print("\n· Opciones comúnes:");
        print(fill("\t-k, --descriptive", "Modo descriptivo. Más mensajes de log"));

        print("\nUsa 'help [modo]' para obtener una descripción más detallada\n");

        // Nota: Todas las acciones (o la mayoría) tienen la opción "--verbose" o "-k"
        // Nota: "help" incluye la licencia y la versión
    }

    public void printSpecificHelp(String mode){
        for(int i=0; i<items.size(); i++){
            JSONObject item = (JSONObject) items.get(i);
            
            String itemMode = (String) item.get("modo");
            if(!itemMode.equalsIgnoreCase(mode)) continue;
            print("Uso:");

            JSONArray options = (JSONArray) item.get("opciones");
            for (int j=0; j<options.size(); j++){
                JSONArray option = (JSONArray) options.get(j);
                print(String.format("\t%s %s", mode, fill((String)option.get(0), (String)option.get(1))));
            }
            return;
        }

        print("No se ha encontrado la opción.");
        print("Usa 'help' para obtener una lista de las opciones.");
    }


    /* UTIL */

    private void loadJSON(){
        StringBuilder builder = new StringBuilder();

        Scanner scan = new Scanner(getClass().getResourceAsStream("/data/help.json"));
        while(scan.hasNextLine()){
            builder.append(scan.nextLine());
        }

        JSONParser parser = new JSONParser();
        try {
            items = (JSONArray) parser.parse(builder.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean hasFlag(String[] flags, String shortFlag, String longFlag){
        for (String flag : flags) {
            if ((flag.equalsIgnoreCase(shortFlag) || flag.equalsIgnoreCase(longFlag)))
                return true;
        }
        return false;
    }

    private void option(String simb, String description){
        print(fill("\t" + simb + " [opciones]", description));
    }

    private String fill(String init, String description){
        int initL = init.length();
        int blankSpaces =  (42 > initL ? 42 - initL : initL - 42);
        return init + (" ".repeat(blankSpaces)) + "| " + description;
    }

    private void print(String msj){
        System.out.println(msj);
    }

    /* Getters */

    public JSONArray getItems() {
        return items;
    }

    public String[] getModes() {
        return modes;
    }
}
