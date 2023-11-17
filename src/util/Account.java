package util;

import java.util.Collections;
import java.util.List;

public class Account {
    private String name;
    private String email;
    private String username;
    private String password;
    private List<String> tags;

    /* Constructores */
    public Account() {
        this("", "", "", "", Collections.emptyList());
    }

    public Account(String name, String email, String user, String password, List<String> tags) {
        this.name = formatName(name);
        this.email = email;
        this.username = user;
        this.password = password;
        this.tags = tags;
    }

    /* Getters */
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getTags() {
        return tags;
    }

    /* Setters */
    public void setName(String name) {
        this.name = formatName(name);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /* Util */

    private String formatName(String str) {
        if (str.length() == 0)
            return str.toUpperCase();

        String firtsPart = str.substring(0, 1);
        String secondPart = str.substring(1, str.length());

        return firtsPart.toUpperCase() + secondPart;
    }

    @Override
    public String toString() {
        return "Account(name=" + name + ", email=" + email + ", user=" + username + ", password=" + password + ", tags="
                + tags + ")";
    }
}
