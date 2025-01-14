package com.example.backneodoc.payload.request;

import com.example.backneodoc.models.Document;
import com.example.backneodoc.models.Role;
import javax.validation.constraints.NotEmpty;
import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

public class SignupRequest {

    @NotEmpty
    @Size(min = 3, max = 20)
    private String firstname;

    @NotEmpty
    @Size(min = 3, max = 20)
    private String lastname;

    @NotEmpty
    @Size(min = 3, max = 20)
    private String username;

    @NotEmpty
    @Size(max = 50)
    @Email
    private String email;
    
    @NotEmpty
    private Set<String> role;
//private String role;
    
    @NotEmpty
    @Size(min = 8, max = 40)
    private String password;

    @NotEmpty
    private String poste;

    private Boolean enabled=false;

    private Set<Document> favoriteFiles = new HashSet<>();


    public Set<Document> getFavoriteFiles() {
        return favoriteFiles;
    }

    public void setFavoriteFiles(Set<Document> favoriteFiles) {
        this.favoriteFiles = favoriteFiles;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Set<String> getRole() {return this.role;}

    public void setRole(Set<String> role) {this.role = role;}

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public Boolean getEnabled() {return enabled;}

    public void setEnabled(Boolean enabled) {this.enabled = enabled;}
}