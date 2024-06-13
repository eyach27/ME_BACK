package com.example.backneodoc.services;
import org.springframework.data.domain.Sort.Order;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.hibernate.engine.internal.Collections;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.backneodoc.Exceptions.ResourceNotFoundException;
import com.example.backneodoc.models.Department;
import com.example.backneodoc.models.Document;
import com.example.backneodoc.models.ETypePlan;
import com.example.backneodoc.models.Formation;
import com.example.backneodoc.models.FormationPlan;
import com.example.backneodoc.models.Tag;
import com.example.backneodoc.models.User;
import com.example.backneodoc.payload.response.MessageResponse;
import com.example.backneodoc.repository.DepartmentRepository;
import com.example.backneodoc.repository.DocumentRepository;
import com.example.backneodoc.repository.FormationRepository;
import com.example.backneodoc.repository.PlanningRepository;
import com.example.backneodoc.repository.UserRepository;

import java.time.LocalDateTime;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
@Service
public class PlanningService {
	 @Autowired
	 	FormationRepository formationRepository;
	 @Autowired
	 	PlanningRepository planningRepository;
	 @Autowired
	    UserRepository userRepository;
	 @Autowired
	 	DocumentRepository documentRepository;
	 @Autowired
	 	DepartmentRepository departementRepository;
	 @Autowired
	 private DataSource dataSource;

	 public ResponseEntity<?> planParSemaine(String name_formation,long id_formateur, LocalDateTime startDate,LocalDateTime endDate, Integer frequence, String description, String salle,Boolean EnLigne,
	            Set<String> STRjourSemaine,  Set<Long> id_participants,
	            Set<Long> id_documents, 
	            Set<String> id_departements) {
		 Formation formation = formationRepository.findByName(name_formation);
         if (formation == null) {
            
             return ResponseEntity.badRequest().body(new MessageResponse("Formation n'existe pas."));
         }
         Optional<User> userOptional = userRepository.findById(id_formateur);
         User formateur = userOptional.orElse(null);
         if (formateur == null) {
       	  formateur =formation.getFormateur();
         }
         if(formateur.getUsername().equals("FORMATEUR.INCONNU")) {
        	 
        	 throw new RuntimeException("Le formateur est inconnu.");
         }
         
         
         Set<DayOfWeek> jourSemaine = new HashSet<>();
         for (String STRjour : STRjourSemaine) {
        	  String inputUppercase = STRjour.toUpperCase() ;
        	  DayOfWeek dow = DayOfWeek.valueOf( inputUppercase );
        	  jourSemaine.add(dow);
		    }
       
		    Set<User> participants = new HashSet<>();
		    
		    for (long id_participant : id_participants) {
		        User participant = userRepository.findById(id_participant)
		                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id_participant));
		      
		        participants.add(participant);
		    }
		    
		  /*  Set<User> participants = id_participants.stream()
		            .map(id -> userRepository.findById(id)
		                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + id)))
		            .collect(Collectors.toSet());*/
		    
		  
		    // Create sets for documents and departements
		    Set<Document> documents = new HashSet<>(formation.getDocuments());
		    Set<Department> departements = new HashSet<>(formation.getDepartments());

		    // Add documents to the set
		    for (long id_document : id_documents) {
		        Document document = documentRepository.findById(id_document)
		                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + id_document));
		        documents.add(document);
		    }

		    
		    // Add departements to the set
		    for (String id_departement : id_departements) {
		        Department departement = departementRepository.findByName(id_departement);
		        departements.add(departement);
		    }
		    
		    Set<LocalDateTime[]> set = getPlanningIntervals( jourSemaine, startDate,endDate, frequence); 

		    LocalDateTime max = null;
		    LocalDateTime min = null;

		    for (LocalDateTime[] array : set) {
		        for (LocalDateTime date : array) {
		            if (max == null || date.isAfter(max)) {
		                max = date;
		            }
		            if (min == null || date.isBefore(min)) {
		                min = date;
		            }
		        }
		    }

		    
		    FormationPlan formationPlan = new FormationPlan(formation,formateur,  min, max,frequence , description,salle,EnLigne,jourSemaine, participants,documents,departements);
		   
		    FormationPlan formationCree =planningRepository.save(formationPlan);
		    
		    Map<String, Object> response = new HashMap<>();
		    response.put("message", "Votre formation " + formation.getName() + " est planifiée avec succès.");
		    response.put("formationPlan", formationPlan);
		    return ResponseEntity.ok(response);
		}
	 
	 
	 
	 
	 
	 
	 
	 
	 public ResponseEntity<?> planParjrs(String name_formation,long id_formateur, LocalDateTime startDate,LocalDateTime endDate, Integer frequence, String description, String salle,Boolean EnLigne,
	            Set<Long> id_participants,
	            Set<Long> id_documents, 
	            Set<String> id_departements) {
		 Formation formation = formationRepository.findByName(name_formation);
      if (formation == null) {
         
          return ResponseEntity.badRequest().body(new MessageResponse("Formation n'existe pas."));
      }
      Optional<User> userOptional = userRepository.findById(id_formateur);
      User formateur = userOptional.orElse(null);
      if (formateur == null) {
    	  formateur =formation.getFormateur();
      }
      System.out.println(formateur.getUsername());
      System.out.println(formateur.getUsername().equals("FORMATEUR.INCONNU"));
      if(formateur.getUsername().equals("FORMATEUR.INCONNU")) {
     	 
     	 throw new RuntimeException("Le formateur est inconnu.");
      }
    
    
      
		    Set<User> participants = new HashSet<>();
		    
		    for (long id_participant : id_participants) {
		        User participant = userRepository.findById(id_participant)
		                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id_participant));
		      
		        participants.add(participant);
		    }
		    // Create sets for documents and departements
		    Set<Document> documents = new HashSet<>(formation.getDocuments());
		    Set<Department> departements = new HashSet<>(formation.getDepartments());

		    // Add documents to the set
		    for (long id_document : id_documents) {
		        Document document = documentRepository.findById(id_document)
		                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + id_document));
		        documents.add(document);
		    }

		    

		    // Add departements to the set
		    for (String id_departement : id_departements) {
		        Department departement = departementRepository.findByName(id_departement);
		               
		        departements.add(departement);
		    }

		    Set<LocalDateTime[]> set = getPlanningIntervalsJRS( startDate,endDate, frequence); 

		    LocalDateTime max = null;
		    LocalDateTime min = null;

		    for (LocalDateTime[] array : set) {
		        for (LocalDateTime date : array) {
		            if (max == null || date.isAfter(max)) {
		                max = date;
		            }
		            if (min == null || date.isBefore(min)) {
		                min = date;
		            }
		        }
		    }

		    FormationPlan formationPlan = new FormationPlan(formation, formateur, min, max, frequence,  description,salle,EnLigne, participants, documents,departements);
		
		    planningRepository.save(formationPlan);
		    Map<String, Object> response = new HashMap<>();
		    response.put("message", "Votre formation " + formation.getName() + " est planifiée avec succès.");
		    response.put("formationPlan", formationPlan);
		    return ResponseEntity.ok(response);
		}
	 
	 
	 
	 public ResponseEntity<MessageResponse> update(Long PlanId,String nom_formation,long IDFormateur, LocalDateTime startDate,LocalDateTime endDate, String description, String salle,Boolean EnLigne,
	          String TypePlan ,Set<String> STRjourSemaine, Integer frequence, Set<Long> id_participants,
	            Set<Long> id_documents, Set<String> id_departements) {

		// FormationPlan formationPlan = planningRepository.getById(PlanId);
		 Formation formation = formationRepository.findByName(nom_formation);
		 User formateur = userRepository.getById(IDFormateur);
	   if (formation == null) {
      
		   	return ResponseEntity.badRequest().body(new MessageResponse("Formation n'existe pas."));
	   	}
	   if(formateur.getUsername()=="FORMATEUR.INCONNU") {
  	 
		   return ResponseEntity.badRequest().body(new MessageResponse("Le formateur est inconnu."));
	   	}
   
	        FormationPlan existingFormationPlan = planningRepository.findById(PlanId)
	                .orElseThrow(() -> new NoSuchElementException("Formation planifiée non trouvée pour l'identifiant : " + PlanId));
	         existingFormationPlan.setFormateur(formateur);
	         existingFormationPlan.setFrequence(frequence);
	         existingFormationPlan.setDescription(description);
		     existingFormationPlan.setSalle(salle);
		     existingFormationPlan.setEnLigne(EnLigne);
		     /////update participants /////
		        Set<User> participants = new HashSet<>();  
			    for (long id_participant : id_participants) {
			    		User participant = userRepository.findById(id_participant)
			                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id_participant));
			      
			    		participants.add(participant);
			    }
		     existingFormationPlan.setParticipants(participants);
		      
		   
			    Set<Document> documents = new HashSet<>(formation.getDocuments());
			    Set<Department> departements = new HashSet<>(formation.getDepartments());
		
			    // update doc
			    for (long id_document : id_documents) {
			        Document document = documentRepository.findById(id_document)
			                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + id_document));
			        documents.add(document);
			    }
		
		
			    // update dep
			    for (String id_departement : id_departements) {
			        Department departement = departementRepository.findByName(id_departement);
			        departements.add(departement);
			    }
		
			   existingFormationPlan.setDocuments(documents);
			   existingFormationPlan.setDepartements(departements);   
	        
			    LocalDateTime max = null;
			    LocalDateTime min = null;
	        if (TypePlan.equals("semaine") ) {
	        	
	        	/////update jourSemaine//////
	        	  Set<DayOfWeek> jourSemaine = new HashSet<>();
	       	   for (String STRjour : STRjourSemaine) {
	       		   	String inputUppercase = STRjour.toUpperCase() ;
	       		   	DayOfWeek dow = DayOfWeek.valueOf( inputUppercase );
	       		   	jourSemaine.add(dow);
	       		    }
	       	   existingFormationPlan.setJourSemaine(jourSemaine);
	       	   
	       	   
	           Set<LocalDateTime[]> set = getPlanningIntervals( jourSemaine, startDate,endDate, frequence); 
			    for (LocalDateTime[] array : set) {
			        for (LocalDateTime date : array) {
			            if (max == null || date.isAfter(max)) {
			                max = date;
			            }
			            if (min == null || date.isBefore(min)) {
			                min = date;
			            }
			        }
			    }
	       	   
	       	   
	            existingFormationPlan.setStartDate(min);
	            existingFormationPlan.setEndDate(max);
	            existingFormationPlan.setTypePlan(ETypePlan.ParSemaine);
	       
	        } else if (TypePlan.equals("jour")) {
	        	  Set<LocalDateTime[]> set = getPlanningIntervalsJRS( startDate,endDate, frequence); 
				    for (LocalDateTime[] array : set) {
				        for (LocalDateTime date : array) {
				            if (max == null || date.isAfter(max)) {
				                max = date;
				            }
				            if (min == null || date.isBefore(min)) {
				                min = date;
				            }
				        }
				    }
		       	   
		       	   
		            existingFormationPlan.setStartDate(min);
		            existingFormationPlan.setEndDate(max);
		            existingFormationPlan.setTypePlan(ETypePlan.JoursSuccessifs);
	      
	        }

	     
	     
		    planningRepository.save(existingFormationPlan);
	        return  ResponseEntity.ok().body(new MessageResponse("update successfully")) ;
	    }
	 
	 public Set<FormationPlan> getByFormationNom(String formationNom) {
	        return planningRepository.findByFormationName(formationNom);
	    }
	 
	 public Set<FormationPlan> getByFormationID(long id) {
		
		
		// LocalDateTime now = LocalDateTime.now();

		 
		Set<FormationPlan> filteredPlans = planningRepository.findByFormation_Id(id);
	
	        return filteredPlans;
	    }
	
	 public ResponseEntity<?> DeleteParticipationFromPlanning(long id_planification, long id_participant) {
		    FormationPlan planification = planningRepository.findById(id_planification)
		            .orElseThrow(() -> new IllegalArgumentException("Planification not found: " + id_planification));
		    
		    User participant = userRepository.findById(id_participant)
		            .orElseThrow(() -> new IllegalArgumentException("User not found: " + id_participant));
		    
		    if (!planification.getParticipants().contains(participant)) {
		        return ResponseEntity.badRequest().body(new MessageResponse("Cet utilisateur ne participe pas à cette planification."));
		    }
		    
		    planification.getParticipants().remove(participant);
		    planningRepository.save(planification);
		    
		    return ResponseEntity.ok(new MessageResponse("L'utilisateur " + participant.getUsername() + " a été retiré de la planification."));
		}
	 
	 
	 public void DeleteParticipationFromAllPlanning(long id_participant) {
		 List<FormationPlan> plannings = planningRepository.findAll();
		 for (FormationPlan planning : plannings) {
			 
			 DeleteParticipationFromPlanning(planning.getId(), id_participant);
		 }
		
	 }
	 
	 public ResponseEntity<?> deleteFormationPlan(long id_plan) {

		    FormationPlan formationPlan = planningRepository.findById(id_plan)
		            .orElseThrow(() -> new IllegalArgumentException("Formation Plan not found: " + id_plan));

		    planningRepository.delete(formationPlan);
		    return ResponseEntity.ok(new MessageResponse("Formation plan deleted successfully."));
		}
	 
	 //debutFormation : la valeur de debut ajoutee d apres le user (le ../../....)
	 //heureDebut : l heur de debut de formation ( a 9h ou a 14h...)
	 //nbHours : nombre d heure par seance 
	 //duree : nombre de semaine 

	 
	 public Set<LocalDateTime[]> getPlanningIntervals(Set<DayOfWeek> jourSemaine, LocalDateTime heure_date_Debut, LocalDateTime heure_date_Fin, int repeterChaque) {
		    Set<LocalDateTime[]> intervals = new HashSet<>();
		    LocalDate currentDate = heure_date_Debut.toLocalDate().with(TemporalAdjusters.nextOrSame(jourSemaine.iterator().next()));
		    LocalTime heureDebut = heure_date_Debut.toLocalTime();
		    System.out.println(heureDebut);
		    LocalTime heureFin = heure_date_Fin.toLocalTime();
		    System.out.println(heureFin);
		    int nbHours = (int) Duration.between(heureDebut, heureFin).toHours();
		    long nbMinutes = (int) Duration.between(heureDebut, heureFin).toMinutes(); // Durée totale en minutes

		    while (!currentDate.isAfter(heure_date_Fin.toLocalDate())) {
		        for (DayOfWeek jour : jourSemaine) {
		            LocalDate firstMatchingDate = currentDate.with(TemporalAdjusters.firstInMonth(jour));
		            LocalDate matchingDate = firstMatchingDate;
		            while (matchingDate.isEqual(heure_date_Fin.toLocalDate()) ||(! matchingDate.isAfter(heure_date_Fin.toLocalDate()))) {
		                if (!matchingDate.isBefore(heure_date_Debut.toLocalDate())) {
		                    LocalDateTime[] interval = new LocalDateTime[2];
		                    LocalDateTime currentDateTime = LocalDateTime.of(matchingDate, heureDebut);
		                    interval[0] = currentDateTime;
		                    //interval[1] = currentDateTime.plusHours(nbHours);
		                    interval[1] = currentDateTime.plusMinutes(nbMinutes); // date et heure de fin
		                    if (!interval[1].isAfter(heure_date_Fin)) {
		                        intervals.add(interval);
		                    }
		                }
		                matchingDate = matchingDate.plusWeeks(repeterChaque);
		            }
		        }
		        currentDate = currentDate.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
		    }

		    return intervals;
		}


	 
	 public Set<LocalDateTime[]> getPlanningIntervalsJRS(LocalDateTime heure_date_Debut, LocalDateTime heure_date_Fin, int repeterChaque) {
		    Set<LocalDateTime[]> intervals = new HashSet<>();
		    LocalDateTime currentDateTime = heure_date_Debut;
		    long duree = ChronoUnit.DAYS.between(heure_date_Debut.toLocalDate(), heure_date_Fin.toLocalDate());
		  ;
		    Duration duration = Duration.between(heure_date_Debut.toLocalTime(), heure_date_Fin.toLocalTime());
		    //long nbHours = duration.toHours();
		    long nbMinutes = duration.toMinutes(); // Durée totale en minutes

		    int i = 0;
		    while (i < duree+1) {
		        LocalDateTime[] interval = new LocalDateTime[2];
		        interval[0] = currentDateTime; // date et heure de début
		       // interval[1] = currentDateTime.plusHours(nbHours); // date et heure de fin
		        interval[1] = currentDateTime.plusMinutes(nbMinutes); // date et heure de fin

		        intervals.add(interval);
		        currentDateTime = currentDateTime.plusDays(repeterChaque );
		       i= i+repeterChaque;
		    }

		    return intervals;
		}


		    
	 public Set<LocalDateTime[]> getOccupiedIntervalsForFormateur(long id_formateur) {
		 
			Set<LocalDateTime[]> intervals = new HashSet<>();
			LocalDateTime currentDate = LocalDateTime.now(); // Obtenir la date actuelle
			Set<FormationPlan> listePlanning= planningRepository.findFormationPlanByFormateurId(id_formateur);
		
			for(FormationPlan plan : listePlanning) {
				LocalDateTime endDate = plan.getEndDate();
				if(endDate != null && endDate.isAfter(currentDate)) { // Vérifier si la date de fin est définie et supérieure à la date actuelle
					LocalDateTime startDate = plan.getStartDate();
					int repeterChaque = plan.getFrequence();
					if (plan.getTypePlan() == ETypePlan.ParSemaine) {
						Set<DayOfWeek> jourSemaine = plan.getJourSemaine();
						intervals.addAll(getPlanningIntervals(jourSemaine, startDate, endDate, repeterChaque));
					}
					if (plan.getTypePlan() == ETypePlan.JoursSuccessifs) {
						intervals.addAll(getPlanningIntervalsJRS(startDate, endDate, repeterChaque));
					}	
				}
			}
				 
			return intervals;
		}
	 
	 public boolean isIntervalAvailableForFormateur(long id_formateur, Set<LocalDateTime[]> intervalPlanning) {
		// System.out.println(intervalPlanning);  
		 Set<LocalDateTime[]> occupiedIntervals = getOccupiedIntervalsForFormateur(id_formateur);
		 if (occupiedIntervals.isEmpty()) { return true;}
		    for (LocalDateTime[] occupiedInterval : occupiedIntervals) {
		        for (LocalDateTime[] interval : intervalPlanning) {
		            LocalDateTime occupiedStart = occupiedInterval[0];
		            LocalDateTime occupiedEnd = occupiedInterval[1];
		            LocalDateTime intervalStart = interval[0];
		            LocalDateTime intervalEnd = interval[1];
		            if (occupiedStart.isBefore(intervalEnd) && intervalStart.isBefore(occupiedEnd)) {
		                return false;
		            }
		        }
		    }
		    
		    return true;
		}

	public Set<FormationPlan> getListPlanningForParticipants(long id ) {
		return planningRepository.findFormationPlanByParticipantId(id);
}
	
	
	public Set<FormationPlan> getListPlanningForFormateur(long id ) {
		return planningRepository.findFormationPlanForFormateur(id);
}
	
	
	public Set<FormationPlan> GetListPlanFormationNonAssignees ( ) {
		return planningRepository.findUnassignedFormationPlans();
}
	
	
	//les planning de les formations non pas encore terminer et qui n appartient pas a un lot 
	public  Set<FormationPlan> getUnfinishedPlans() {
	    LocalDateTime currentDate = LocalDateTime.now();
	    Set<FormationPlan> unfinishedPlans = new HashSet<>();
	    
	    // Parcourir tous les enregistrements de FormationPlan
	    for (FormationPlan plan : planningRepository.findUnassignedFormationPlans()) {
	   // for (FormationPlan plan : planningRepository.findAll()) {
	        if (plan.getEndDate().isAfter(currentDate)) {
	            unfinishedPlans.add(plan);
	        }
	    }
	    
	    return unfinishedPlans;
	}

	 public Map<String, Boolean> deletePlanning(Long id) {
		    planningRepository.deleteById(id);
	        Map<String, Boolean> response = new HashMap<>();
	        response.put("supprimée", Boolean.TRUE);
	        return response;
	    }
	
	
}
