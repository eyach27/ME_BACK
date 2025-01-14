package com.example.backneodoc.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "document")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "contentType", nullable = false)
    private String contentType;

    @Column(name = "owner", nullable = false)
    private String owner;

    @Column(name = "path", nullable = false)
    private String path;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE},mappedBy ="doc_favoris" )
    @JsonIgnoreProperties("doc_favoris")
    private Set<User> favoris = new HashSet<>();


    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "doc_tag", joinColumns = { @JoinColumn(name = "doc_id") }, inverseJoinColumns = { @JoinColumn(name = "tag_id") })
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "doc_dep", joinColumns = { @JoinColumn(name = "id_doc") }, inverseJoinColumns = { @JoinColumn(name = "id_dep") })
    private Set<Department> departements = new HashSet<>();


	public Document() {
    }
    
   
    public Document(String name, Long size, String contentType, String owner, String path, Set<Tag> tags, Set<Department> departments){
        this.name = name;
        this.size = size;
        this.contentType = contentType;
        this.owner = owner;
        this.path = path;
        this.tags = tags;
        this.departements = departments;
    }
  
    public Document(Document file) {
        this.id=file.getId();
        this.name = file.getName();
        this.size = file.getSize();
        this.contentType = file.getContentType();
        this.owner = file.getOwner();
        this.path = file.getPath();
        this.tags = file.getTags();
        this.departements = file.getDepartments();
    }


    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    
    public Set<Department>  getDepartments() {
        return departements;
    }

    public void setDepartements(Set<Department> departments) {
        this.departements = departments;
    }

    public Set<Tag> getTags() {return tags;}

    public void setTags(Set<Tag> tags) {this.tags = tags;}
    
    
  

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Set<User> getFavoris() {
        return favoris;
    }

    public void setFavoris(Set<User> favoris) {
        this.favoris = favoris;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    
}