package handler;

import java.util.Arrays;

public class Flag {
    private static String[] flags;

    private Flag(){
    }
    
    public static boolean hasFlag(String shortFlag, String longFlag){
        for (String flag : flags) {
            if ((flag.equalsIgnoreCase(shortFlag) || flag.equalsIgnoreCase(longFlag)))
                return true;
        }
        return false;
    }

    public static boolean hasFlag(String longFlag){
        for (String flag : flags) {
            if (flag.equalsIgnoreCase(longFlag))
                return true;
        }
        return false;
    }

    public static String getFlagValue(String shortFlag, String longFlag){
        for(int i=0; i<flags.length; i++){
            if(flags[i].startsWith(shortFlag) || flags[i].startsWith(longFlag))
                // Se comprueba si se indicó un valor luego de activar la bandera.
                // Ejemplo: '--new a', donde se verifica que haya un valor 'a' y no solo '--new'
                return (flags.length > (i+1) ? flags[i + 1] : null);
        }
        return null;
    }

    public static String getFlagValue(String longFlag){
        for(int i=0; i<flags.length; i++){
            if(flags[i].startsWith(longFlag))
                return (flags.length == (i) ? flags[i + 1] : null);
        }
        return null;
    }

    public static String[] fromNextArg(String[] args){
        if(args.length <= 1) return new String[0];

        String[] newArgs = new String[args.length-1];
        for(int i=1; i<args.length; i++)
            newArgs[i-1] = args[i];
        return newArgs;
    }

    public static String[] getFlags() {
        return flags;
    }

    public static void setFlags(String[] flags) {
        Flag.flags = flags;
    }

    public static String[] getFlagValues(String shortFlag, String longFlag) {
        int flagIndex = -1;
        int nextFlagIndex = flags.length;

        // Buscar dónde empieza la etiqueta
        for(int i=0; i<flags.length; i++){
            if(flags[i].equalsIgnoreCase(shortFlag) || flags[i].equalsIgnoreCase(longFlag)){
                flagIndex = i;
                break;
            }
        }

        // Manejo de errores
        if(flagIndex == -1) return new String[0];
        if(getFlagValue(shortFlag, longFlag) == null) return new String[0];

        // Buscar donde termina la etiqueta
        for(int i=flagIndex+1; i<flags.length; i++){
            if(flags[i].startsWith("-")){
                nextFlagIndex = i;
                break;
            }
        }

        // Conseguir los valores que están en el medio
        return Arrays.copyOfRange(flags, flagIndex+1, nextFlagIndex);
    }
}
