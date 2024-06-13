package com.example.backneodoc.Controllers;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backneodoc.Exceptions.ResourceNotFoundException;
import com.example.backneodoc.models.ETypePlan;
import com.example.backneodoc.models.FormationPlan;
import com.example.backneodoc.payload.response.MessageResponse;
import com.example.backneodoc.repository.PlanningRepository;
import com.example.backneodoc.repository.UserRepository;
import com.example.backneodoc.services.FormationService;

import com.example.backneodoc.services.PlanningService;

//@CrossOrigin(origins = "http://10.53.1.149:85")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/planning")
public class PlanningController {
	 @Autowired
	    FormationService formationService;
	 
	 @Autowired
	    PlanningService planningService;
	 @Autowired
	    PlanningRepository planningRepository;
	 @Autowired
	   UserRepository userRepository;

	 
	 
	 @PostMapping("/formateur/{idFormateur}/disponibilite")
	 public ResponseEntity<?> checkAvailability(@PathVariable("idFormateur") long idFormateur,
			 @RequestParam("TypePlan") String TypePlan,
             @RequestParam("jourSemaine") @Nullable Set<String> jourSemaine,
             @RequestParam("heure_date_Debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime heure_date_Debut,
             @RequestParam("heure_date_Fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime heure_date_Fin,
             @RequestParam("repeterChaque") int repeterChaque){
	        try {
	        	
	        	Set<LocalDateTime[]> intervalPlanning = new HashSet<>();
	        	if (TypePlan.equals("semaine")) {
	        		//System.out.print(TypePlan == "semaine");
	        		Set<DayOfWeek> joursSemaine = new HashSet<>();
	        		for (String jour : jourSemaine) {
	        		    joursSemaine.add(DayOfWeek.valueOf(jour.toUpperCase()));
	        		}
	        		intervalPlanning.addAll(planningService.getPlanningIntervals(joursSemaine, heure_date_Debut, heure_date_Fin, repeterChaque));
				}
	        	System.out.println(TypePlan.toString() );
				if (TypePlan.equals("jour")) {
			
					intervalPlanning.addAll(planningService.getPlanningIntervalsJRS(heure_date_Debut, heure_date_Fin, repeterChaque));
				}
				
					/*for (LocalDateTime[] interval : intervalPlanning) {
					    System.out.println(interval[0] + " - " + interval[1]);
					}*/
			if (intervalPlanning.isEmpty()) {
				 return ResponseEntity.badRequest().body("{\"message\": \"La plage horaire saisie ne correspond à aucune date valide. \"}");
			}
			
	            boolean isAvailable;
	            if  (idFormateur== userRepository.findByUsername("FORMATEUR.INCONNU").getId()){
	        		isAvailable= true;
	        	}
	            else {
	            	isAvailable = planningService.isIntervalAvailableForFormateur(idFormateur, intervalPlanning);
	            }
	            System.out.println("isAvailable "+isAvailable );
	            //isAvailable=false;
	            if (isAvailable) {
	            	 return ResponseEntity.ok().body("{\"message\": \"L'ensemble d'intervalles de planification est disponible pour le formateur.\"}");
	            } else {
	                return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"message\": \"L'ensemble d'intervalles de planification n'est pas disponible pour le formateur.\"}");
	            }
	        } catch (Exception e) {
	            return ResponseEntity.badRequest().body("{\"message\": \"Erreur lors de la vérification de la disponibilité des intervalles pour le formateur : " + e.getMessage() + "\"}");
	        }
	    }
	 
	 
	 @PostMapping("/plan-semaine")
	 public ResponseEntity<MessageResponse> planifierFormationParSemaine(
	         @RequestParam("nameFormation") String nameFormation,
	         @RequestParam("idFormateur") long idFormateur,
	         @RequestParam("heure_date_Debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime heure_date_Debut,
             @RequestParam("heure_date_Fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime heure_date_Fin,
	         @RequestParam("repeterChaque") Integer frequence,
	         @RequestParam(value = "description", required = false) String description,
	         @RequestParam(value = "salle", required = false) String salle,
	         @RequestParam(value = "enLigne", required = false) String EnLigne,
	         @RequestParam("jourSemaine") Set<String> jourSemaine,
	         @RequestParam("idParticipants") Set<Long> idParticipants,
	         @RequestParam("idDocuments") Set<Long> idDocuments,
	         @RequestParam("idDepartements") Set<String> idDepartements) {
	     
	   try { 
		   boolean enligne;
		   if (EnLigne=="false") {
		   enligne=false ;
	   }else {
		   enligne=true; 
	   }
		   planningService.planParSemaine(nameFormation, idFormateur, heure_date_Debut, heure_date_Fin, frequence, description, salle,enligne, jourSemaine, idParticipants, idDocuments, idDepartements);
	   }catch (Exception e) {
           return ResponseEntity.badRequest().body(new MessageResponse("Erreur lors de la planification."));
       }
	     return ResponseEntity.ok(new MessageResponse("Votre formation " + nameFormation + " est planifiée avec succès."));
	 }
	 
	 
	 @PostMapping("/plan-jour")
	 public ResponseEntity<?> planifierFormationParJour(
	         @RequestParam("nameFormation") String name_formation,
	         @RequestParam("idFormateur") long id_formateur,
	         @RequestParam("heure_date_Debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime heure_date_Debut,
             @RequestParam("heure_date_Fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime heure_date_Fin,
	         @RequestParam("repeterChaque") Integer frequence,
	         @RequestParam(value = "description", required = false) String description,
	         @RequestParam(value = "salle", required = false) String salle,
	         @RequestParam(value = "enLigne", required = false) String EnLigne,
	         @RequestParam("idParticipants") Set<Long> idParticipants,
	         @RequestParam("idDocuments") Set<Long> idDocuments,
	         @RequestParam("idDepartements") Set<String> idDepartements) {
	     
		 try { boolean enligne;
		 if (EnLigne=="false") {
			 	enligne=false ;
		   	}else {
		   		enligne=true; 
		   		} 
			 planningService.planParjrs(name_formation, id_formateur, heure_date_Debut, heure_date_Fin, frequence, description, salle,enligne, idParticipants, idDocuments,idDepartements);
		   }catch (Exception e) {
	           return ResponseEntity.badRequest().body(new MessageResponse("Erreur lors de la planification."));
	       }
	     
	     return ResponseEntity.ok(new MessageResponse("Votre formation " + name_formation + " est planifiée avec succès."));
	 }
	 
	 
	 @PostMapping("/planUpdate")
	 public ResponseEntity<MessageResponse> UpdatePlan(
			 @RequestParam("idPlan") long idPlan,
	         @RequestParam("nameFormation") String nameFormation,
	         @RequestParam("idFormateur") long idFormateur,
	         @RequestParam("heure_date_Debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime heure_date_Debut,
             @RequestParam("heure_date_Fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime heure_date_Fin,
	         @RequestParam("repeterChaque") Integer frequence,
	         @RequestParam(value = "description", required = false) String description,
	         @RequestParam(value = "salle", required = false) String salle,
	         @RequestParam(value = "enLigne", required = false) String EnLigne,
	         @RequestParam(value = "TypePlan") String TypePlan,
	         @RequestParam(value ="jourSemaine", required = false) Set<String> jourSemaine,
	         @RequestParam("idParticipants") Set<Long> idParticipants,
	         @RequestParam("idDocuments") Set<Long> idDocuments,
	         @RequestParam("idDepartements") Set<String> idDepartements) {
	     
	   try { 
		   boolean enligne;
		   if (EnLigne=="false") {
		   enligne=false ;
	   }else {
		   enligne=true; 
	   }
		   System.out.println(idPlan);
		   System.out.println(nameFormation);
		   System.out.println(idFormateur);
		   System.out.println(heure_date_Debut);
		   System.out.println(description);
		   planningService.update(idPlan,nameFormation, idFormateur, heure_date_Debut, heure_date_Fin,  description, salle,enligne, TypePlan, jourSemaine,frequence,idParticipants, idDocuments, idDepartements);
	   }catch (Exception e) {
           return ResponseEntity.badRequest().body(new MessageResponse("Erreur lors de la planification."));
       }
	     return ResponseEntity.ok(new MessageResponse("Votre formation " + nameFormation + " a été mis à jour avec succès."));
	 }
	 
	 
	 
	 
	 // la liste de planning de une formation donnee
	 @GetMapping("/list/{nomFomration}")
	    public ResponseEntity<Set<FormationPlan>> getListPlanning(@PathVariable("nomFomration") String nomFomration) {
	        return ResponseEntity.status(HttpStatus.OK).body(planningService.getByFormationNom(nomFomration));
	    }
	 @GetMapping("/listBYid/{idFormation}")
	    public ResponseEntity<Set<FormationPlan>> getListPlanningByID(@PathVariable("idFormation") long idFormation) {
	        return ResponseEntity.status(HttpStatus.OK).body(planningService.getByFormationID(idFormation));
	    }
	 
	 
	//Dispense: le user est un formateur 
	@GetMapping("/listD/{idFormateur}")
	    public ResponseEntity<Set<FormationPlan>> getPlanningListD(@PathVariable("idFormateur") Long id) {
	        return ResponseEntity.status(HttpStatus.OK).body(planningService.getListPlanningForFormateur(id));
	    }
	 
	 //Suivies: le User est un participant 
	 @GetMapping("/listS/{idUser}")
	    public ResponseEntity<Set<FormationPlan>> getPlanningListS(@PathVariable("idUser") Long idUser) {
	        return ResponseEntity.status(HttpStatus.OK).body(planningService.getListPlanningForParticipants(idUser));
	    }
	 
	 @GetMapping("/intervalle/{idPlanning}")
	    public ResponseEntity<Set<LocalDateTime[]>> getIntervalle(@PathVariable("idPlanning") Long idPlanning) {
		FormationPlan planning= planningRepository.getById(idPlanning);
		  Set<LocalDateTime[]> intervals = new HashSet<>();
		if(planning.getTypePlan().equals(ETypePlan.JoursSuccessifs)) {
			int frequence =planning.getFrequence();
			LocalDateTime dateDebut =planning.getStartDate();
			LocalDateTime dateFin =planning.getEndDate();	
		    intervals = planningService.getPlanningIntervalsJRS( dateDebut, dateFin,frequence);
		}else if(planning.getTypePlan().equals(ETypePlan.ParSemaine)) {
			Set <DayOfWeek> jourSemaine =planning.getJourSemaine();
			int frequence =planning.getFrequence();
			LocalDateTime dateDebut =planning.getStartDate();
			LocalDateTime dateFin =planning.getEndDate();	
			intervals =planningService.getPlanningIntervals(jourSemaine,  dateDebut, dateFin,frequence);
		}
		
	        return ResponseEntity.status(HttpStatus.OK).body(intervals);
	    }
	    
	 @GetMapping("/All")
	    public ResponseEntity<List<FormationPlan>> getAllPlanning() {
	        return ResponseEntity.status(HttpStatus.OK).body(planningRepository.findAll());
	    }
	   
		 @GetMapping("/{id}")
		    public ResponseEntity<FormationPlan> geById(@PathVariable(value = "id") Long planId) {
		        return ResponseEntity.status(HttpStatus.OK).body(planningRepository.getById(planId));
		    }
	 @GetMapping("/planningNonAssignees")
	    public ResponseEntity<Set<FormationPlan>> GetListPlanFormationNonAssignees() {
	        return ResponseEntity.status(HttpStatus.OK).body(planningService.GetListPlanFormationNonAssignees());
	    }
	 
	 @GetMapping("/PlanningNotExpedited")
	    public ResponseEntity<Set<FormationPlan>> getPlanningNotExpedited() {
	        return ResponseEntity.status(HttpStatus.OK).body(planningService.getUnfinishedPlans());
	    }
	 
	 @DeleteMapping("/delete/{id}")
	 public Map<String, Boolean> deletePlanning(@PathVariable(value = "id") Long planId) throws ResourceNotFoundException {
	    
		 return planningService.deletePlanning(planId);
	 }
	 @DeleteMapping("/{id_planification}/participants/{id_participant}")
	    public ResponseEntity<?> deleteParticipationFromPlanning(@PathVariable("id_planification") Long id_planification, @PathVariable("id_participant") Long id_participant) {
	        return planningService.DeleteParticipationFromPlanning(id_planification, id_participant);
	    }
	 
	 @DeleteMapping("/participants/{id}")
	 public ResponseEntity<MessageResponse> deleteParticipationFromAllPlanning(@PathVariable("id") long idParticipant) {
		 planningService.DeleteParticipationFromAllPlanning(idParticipant);
	     return ResponseEntity.ok(new MessageResponse("La participation de l'utilisateur a été retirée de toutes les planifications."));
	 }

}
