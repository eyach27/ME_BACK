package com.example.backneodoc.models;

import java.util.HashSet;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(	name = "formation")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Formation {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	
	 @Column(name = "name")
	    private String name;

	    @ManyToOne(fetch = FetchType.EAGER)
	    @JoinColumn(name = "id_formateur", referencedColumnName = "id")
	    private User formateur;

	    @Column(nullable = false, columnDefinition = "boolean default true",name = "version")
	    private boolean version=true;
	    
	    
	
	    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
	    @JoinTable(name = "for_doc", joinColumns = @JoinColumn(name = "id_formation"), inverseJoinColumns = @JoinColumn(name = "id_document"))
	    private Set<Document> documents = new HashSet<>();

	    

	    @ManyToMany(cascade = CascadeType.REMOVE)
	    @JoinTable(name = "for_dep", joinColumns =  @JoinColumn(name = "id_formation") , inverseJoinColumns =  @JoinColumn(name = "id_departement"))
	    private Set<Department> departments = new HashSet<>();
	  

	    public Formation( String name, User formateur, Set<Document> documents,Set<Department> departments) {
	     
	        this.name = name;
	        this.formateur = formateur;
	        this.documents=documents;
	       this.departments=departments;
	      
	    }
	    public Formation() {}
	    
	    public Formation(String name, User formateur) {
	      
	        this.name = name;
	        this.formateur = formateur;
	      
	    }

	    
	    
	    
	    
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public User getFormateur() {
			return formateur;
		}

		public void setFormateur(User formateur) {
			this.formateur = formateur;
		}

		public boolean getVersion() {
			return version;
		}

		public void setVersion(boolean version) {
			this.version = version;
		}

		public Set<Document> getDocuments() {
			return documents;
		}

		public void setDocuments(Set<Document> documents) {
			this.documents = documents;
		}

		public Set<Department> getDepartments() {
			return departments;
		}

		public void setDepartments(Set<Department> departments) {
			this.departments = departments;
		}

	 
	 
}
