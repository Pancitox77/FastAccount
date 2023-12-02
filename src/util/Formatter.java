package util;

import java.util.Collections;
import java.util.List;

public class Formatter {
    private Formatter() {
    }

    public static String toString(Account account) {
        String tagStr = account.getTags().toString();
        if (tagStr.equals("[]"))
            tagStr = "null";
        else {
            tagStr = tagStr.replace("[", "");
            tagStr = tagStr.replace("]", "");
        }

        StringBuilder builder = new StringBuilder();
        builder.append(account.getName() + "|");
        builder.append(account.getEmail() + "|");
        builder.append(account.getUser() + "|");
        builder.append(account.getPassword() + "|");
        builder.append(tagStr);

        return builder.toString();
    }

    public static Account fromString(String str) {
        String[] parts = str.split("\\|"); // 5 partes

        Account account = new Account();
        account.setName(parts[0]);
        account.setEmail(onNullGetNull(parts[1]));
        account.setUser(onNullGetNull(parts[2]));
        account.setPassword(onNullGetNull(parts[3]));

        List<String> tagsList;
        if (!parts[4].equals("null"))
            tagsList = List.of(parts[4].split(", "));

        else
            tagsList = Collections.emptyList();
        account.setTags(tagsList);
        return account;
    }

    public static String format(Account account, boolean descriptive) {
        // Etiquetas
        StringBuilder tagBuilder = new StringBuilder();
        List<String> tags = account.getTags();

        for (String tag : account.getTags()) {
            tagBuilder.append("<" + tag + ">");

            String lastTag = tags.get(tags.size() - 1);
            if (!tag.equalsIgnoreCase(lastTag))
                tagBuilder.append(", ");
        }

        // General
        StringBuilder builder = new StringBuilder();

        if (descriptive) {
            builder.append(account.getName() + ":" + onNullSkip(" ", account.getEmail(), ""));
            builder.append(onNullSkip("\n   usuario: ", account.getUser(), ""));
            builder.append(onNullSkip("\n   contraseña: ", account.getPassword(), ""));
            builder.append(onNullSkip("\n   tags: ", tagBuilder.toString(), ""));

        } else {
            builder.append(account.getName() + ": ");
            builder.append(onNullSkip("", account.getEmail(), " | "));
            builder.append(onNullSkip("", account.getUser(), " | "));
            builder.append(onNullSkip("", account.getPassword(), ""));
            builder.append(onNullSkip(" | ", tagBuilder.toString(), ""));
        }

        return builder.toString();
    }

    /* Util */
    private static String onNullGetNull(String str) {
        return (str.equals("null") ? null : str);
    }

    private static String onNullSkip(String init, String str, String end) {
        if (str == null || str.equals(""))
            return "";
        return init + str + end;
    }
}
