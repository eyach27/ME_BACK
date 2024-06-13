package com.example.backneodoc.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
@Table(	name = "lot")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Lot {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	
		@Column(name = "name")
	    private String name;


	   
	    @ManyToMany(fetch = FetchType.LAZY)
	    @JoinTable(	name = "plannings_lot",
	            joinColumns = @JoinColumn(name = "id_lot"),
	            inverseJoinColumns = @JoinColumn(name = "id_planning"))
	    private Set<FormationPlan> formationsPlan = new HashSet<>();
	    
	    
	    @ManyToMany(fetch = FetchType.LAZY)
	    @JoinTable(	name = "participants_lot",
	            joinColumns = @JoinColumn(name = "id_lot"),
	            inverseJoinColumns = @JoinColumn(name = "id_user"))
	    private Set<User> participants = new HashSet<>();
	    
	    @ManyToMany
	    @JoinTable(name = "lot_dep", joinColumns =  @JoinColumn(name = "id_lot") , inverseJoinColumns =  @JoinColumn(name = "id_departement"))
	    private Set<Department> departments = new HashSet<>();

	    
	    @Column(nullable = false, columnDefinition = "boolean default true",name = "version")
	    private boolean version=true;
	    
	    @Column(name = "start_date")
	    private LocalDateTime startDate;
	    
	    @Column(name = "end_date")
	    private LocalDateTime endDate;
	
	    
	    public Lot() {}



		public Lot(String name, Set<FormationPlan> formationsPlan, Set<User> participants, Set<Department> departments,boolean version) {
		
			this.name = name;
			this.formationsPlan = formationsPlan;
			this.participants = participants;
			this.departments = departments;
			this.version = version;
			this.startDate = getMinStartDate(formationsPlan);
			this.endDate = getMaxEndDate(formationsPlan);
			
		}

		
		public Lot(String name, Set<FormationPlan> formationsPlan, Set<User> participants, Set<Department> departments) {
			
			this.name = name;
			this.formationsPlan = formationsPlan;
			this.participants = participants;
			this.departments = departments;
			this.startDate = getMinStartDate(formationsPlan);
			this.endDate = getMaxEndDate(formationsPlan);
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


		public Set<FormationPlan> getFormationsPlan() {
			return formationsPlan;
		}


		public void setFormationsPlan(Set<FormationPlan> formationsPlan) {
			this.formationsPlan = formationsPlan;
		}


		public Set<User> getParticipants() {
			return participants;
		}


		public void setParticipants(Set<User> participants) {
			this.participants = participants;
		}


		public Set<Department> getDepartments() {
			return departments;
		}


		public void setDepartments(Set<Department> departments) {
			this.departments = departments;
		}


		public boolean isVersion() {
			return version;
		}


		public void setVersion(boolean version) {
			this.version = version;
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
		
		
		
		 public static LocalDateTime getMinStartDate(Set<FormationPlan> plans) {
			 		return plans.stream()
			 					.map(FormationPlan::getStartDate)
			 					.min(LocalDateTime::compareTo)
			 					.orElse(null);
		    }
		    
		 
		  public static LocalDateTime getMaxEndDate(Set<FormationPlan> plans) {
			  		return plans.stream()
			  					.map(FormationPlan::getEndDate)
			  					.max(LocalDateTime::compareTo)
			  					.orElse(null);
		    }
	    
}
