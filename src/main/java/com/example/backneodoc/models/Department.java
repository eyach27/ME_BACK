package com.example.backneodoc.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@Table(	name = "department",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        })
public class Department {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @NotBlank
	    @Size(max = 20)
	    private String name;
	    
	    public Department() {
	    }

	    public Department(String name) {
	    	this.name = name;
	    }
	    
	    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "departements")
	    private Set<Document> documents = new HashSet<>();
	    
	  
	    
	   public Department(String name, Set<Document> document) {
	    	this.name = name;
	        this.documents = document;
	    }
	    
	    
	   public Department(Long id, String name) {
	        this.id = id;
	        this.name = name;
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
		
		

	

} 
