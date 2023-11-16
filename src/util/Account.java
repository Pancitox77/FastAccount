package util;

public class Account {
    private String name;
    private String email;
    private String password;
    private String tag;

    public Account(String name, String email, String password) {
        this.name = formatName(name);
        this.email = email;
        this.password = password;
    }

    public Account(String name, String email, String password, String tag) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.tag = (tag.equals("<null>") ? null : tag);
    }

    public Account() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = formatName(name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String formated(boolean verbose) {
        if (verbose)
            return name + ":\n   Email: " + email + "\n   Contraseña: " + password
                    + (hasTag() ? "\n   Tag: <" + tag + ">" : "");
        else
            return name + ": " + email + " | " + password + (hasTag() ? " | <" + tag + ">" : "");
    }

    @Override
    public String toString() {
        return name + " " + email + " " + password + " " + tag;
    }

    public static Account fromString(String str) {
        String[] parts = str.split(" ");
        return new Account(parts[0], parts[1], parts[2], parts[3]);
    }

    private String formatName(String str) {
        if (str.length() == 0)
            return str.toUpperCase();

        String firtsPart = str.substring(0, 1);
        String secondPart = str.substring(1, str.length());

        return firtsPart.toUpperCase() + secondPart;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    private boolean hasTag(){
        return (this.tag != null) && (!this.tag.equals("null"));
    }
}
