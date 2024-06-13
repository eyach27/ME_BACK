package com.example.backneodoc.models;


import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "formation_P")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FormationPlan {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
	@ManyToOne
    @JoinColumn(name = "formation_id", referencedColumnName = "id")
    private Formation formation;
	
	
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    
    @ManyToOne
    @JoinColumn(name = "formateur_id", referencedColumnName = "id")
    private User formateur;
    
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "salle")
    private String salle;
    
    
    @Column(name = "EnLigne")
    private Boolean EnLigne;
    
    
    @Enumerated(EnumType.STRING)
    @Column(name = "typePlan")
    private ETypePlan typePlan;
    
    @ElementCollection(targetClass = DayOfWeek.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "jours_semaine", joinColumns = @JoinColumn(name = "formation_plan_id"))
    @Column(name = "jour_semaine")
    private Set<DayOfWeek> jourSemaine = new HashSet<>();
    
    @Column(name = "frequenceRepetition ", columnDefinition = "number default 1")
    private Integer frequence =1;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "participants_formation_P",
            joinColumns = @JoinColumn(name = "id_formation"),
            inverseJoinColumns = @JoinColumn(name = "id_user"))
    private Set<User> participants = new HashSet<>();
    
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "documents_formation_P",
            joinColumns = @JoinColumn(name = "id_formationP"),
            inverseJoinColumns = @JoinColumn(name = "id_doc"))
    private Set<Document> documents;
    
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "departements_formation_P",
            joinColumns = @JoinColumn(name = "id_formationP"),
            inverseJoinColumns = @JoinColumn(name = "id_dep"))
    private Set<Department> departements;
    
   
	
    
    
    ////////////////// fonction pour calculer enddate //////////////
    
    
   /* private LocalDateTime calculeEndDateParSemaine(Set<DayOfWeek> jourSemaine,LocalDateTime startDate,Integer duree) {
    	int nbDays = jourSemaine.size();
    	LocalDateTime endDate = startDate.plusWeeks(duree).with(TemporalAdjusters.nextOrSame(jourSemaine.iterator().next()));
    	if(nbDays > 1) {
    		Iterator<DayOfWeek> iter =jourSemaine.iterator();
    		iter.next(); // skip first day
    		while(iter.hasNext()) {
    			DayOfWeek day = iter.next();
    			LocalDateTime nextDate = startDate.plusWeeks(duree).with(TemporalAdjusters.nextOrSame(day));
    			if(nextDate.isBefore(endDate)) {
    				endDate = nextDate;
    			}
    		}
    	}
    	return endDate;
    }*/
    //////////////////////////constructeur/////////////////////////////////////////
    
    
    	public FormationPlan() {}
    
   
    	///////PLANIFICATION PAR SEMAINE//////////
    public FormationPlan(Formation formation,User formateur, LocalDateTime startDate, LocalDateTime endDate,Integer frequence, String description,String salle,Boolean EnLigne,
            Set<DayOfWeek> jourSemaine,  Set<User> participants, Set<Document> documents, Set<Department> departements) {
    			this.formation = formation;
    			 if (formateur ==null) {this.formateur=formation.getFormateur();}else { this.formateur = formateur ;}
    			this.startDate = startDate;
    			this.salle=salle;
    			this.EnLigne=EnLigne;
    			this.description = description;
    			this.typePlan = ETypePlan.ParSemaine;
    			this.jourSemaine = jourSemaine;
    			this.endDate = endDate;
    			this.frequence=frequence;
    			this.participants=participants;
    			this.departements=departements;
    			this.documents=documents;
    	}

	
	///////planification par jours successifs///////
    public FormationPlan(Formation formation,User formateur, LocalDateTime startDate,LocalDateTime endDate, Integer frequence,  String description ,String salle, Boolean EnLigne,
    		Set<User> participants, Set<Document> documents, Set<Department> departements) {
        this.formation = formation;
        if (formateur ==null) {this.formateur=formation.getFormateur();}else { this.formateur = formateur ;}
        this.startDate = startDate;
        this.frequence=frequence;
        this.description = description;
    	this.salle=salle;
    	this.EnLigne=EnLigne;
        this.typePlan = ETypePlan.JoursSuccessifs;
        this.participants=participants;
        this.departements=departements;
		this.documents=documents;
		this.endDate = endDate;
      
    }
    
   	public Set<Document> getDocuments() {
		return documents;
	}


	public void setDocuments(Set<Document> documents) {
		this.documents = documents;
	}


	public Set<Department> getDepartements() {
		return departements;
	}


	public void setDepartements(Set<Department> departements) {
		this.departements = departements;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}
	public User getFormateur() {
		return formateur;
	}


	public void setFormateur(User formateur) {
		this.formateur = formateur;
	}




	public Formation getFormation() {
		return formation;
	}


	public void setFormation(Formation formation) {
		this.formation = formation;
	}


	public LocalDateTime getStartDate() {
		return startDate;
	}


	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}


	public LocalDateTime getEndDate() {
		return endDate;
	}


	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}
	public Integer getFrequence() {
		return frequence;
	}


	public void setFrequence(Integer frequence) {
		this.frequence = frequence;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	

	public Set<DayOfWeek> getJourSemaine() {
		return jourSemaine;
	}


	public void setJourSemaine(Set<DayOfWeek> jourSemaine) {
		this.jourSemaine = jourSemaine;
	}





	public ETypePlan getTypePlan() {
		return typePlan;
	}


	public void setTypePlan(ETypePlan typePlan) {
		this.typePlan = typePlan;
	}


	public Set<User> getParticipants() {
		return participants;
	}


	public void setParticipants(Set<User> participants) {
		this.participants = participants;
	}
	 public boolean estEnLigne() {
	        return salle.isEmpty();
	    }

	    public String getSalle() {
	        return salle;
	    }


		public void setSalle(String salle) {
			this.salle = salle;
		}


		public Boolean getEnLigne() {
			return EnLigne;
		}


		public void setEnLigne(Boolean enLigne) {
			EnLigne = enLigne;
		}



}