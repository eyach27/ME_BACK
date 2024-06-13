package com.example.backneodoc.Controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backneodoc.Exceptions.ResourceNotFoundException;
import com.example.backneodoc.models.Formation;
import com.example.backneodoc.models.FormationPlan;
import com.example.backneodoc.models.Lot;
import com.example.backneodoc.payload.response.MessageResponse;
import com.example.backneodoc.repository.DepartmentRepository;
import com.example.backneodoc.repository.DocumentRepository;
import com.example.backneodoc.repository.FormationRepository;
import com.example.backneodoc.repository.LotRepository;
import com.example.backneodoc.repository.UserRepository;
import com.example.backneodoc.services.FormationService;
import com.example.backneodoc.services.LotService;
import com.example.backneodoc.services.PlanningService;
@CrossOrigin(origins = "http://10.53.1.149:85")

@RestController
@RequestMapping("/api/lot")
public class LotController {

		 @Autowired
		 	DocumentRepository documentRepository;
		 @Autowired
		 	DepartmentRepository departmentRepository;
 		@Autowired
 			FormationRepository formationRepository;
 		@Autowired
 			FormationService formationService;
 		@Autowired
 			UserRepository userRepository;
 		@Autowired
			LotRepository lotRepository;
 		@Autowired
 			LotService lotService;
 		@Autowired 
 		PlanningService planningService;
 		
 
 		@PostMapping("/creat")
 		public ResponseEntity<?> createLot(@RequestParam(value = "name" )String name,
 		                                    @RequestParam("IDformationsPlan") Set<Long> IDformationsPlan,
 		                                    @RequestParam(value = "IDparticipants") Set<Long> IDparticipants,
 		                                    @RequestParam("departements") Set<String> departement) {
 		    ResponseEntity<?> reponse = lotService.createLot(name, IDformationsPlan, IDparticipants, departement);
 		    return reponse;
 		}

 @GetMapping("/listLot")
 public ResponseEntity<List<Lot>> getListLot() {
     return ResponseEntity.status(HttpStatus.OK).body(lotService.getAllLots());
 }

 @DeleteMapping("/delete/{id}")
 public Map<String, Boolean> deleteLot(@PathVariable(value = "id") Long lotId) throws ResourceNotFoundException {
    
	 return lotService.deleteLot(lotId);
 } 
 
 @PutMapping("/remove/{id_plan}/plan-lot/{id_lot}")
 public Map<String, Boolean> ReomvePlanningFromLot(@PathVariable(value = "id_plan") Long planId,@PathVariable(value = "id_lot") Long lotId) throws ResourceNotFoundException {
   System.out.print("remove");
     return lotService.RemovePlanningFromLot(planId,lotId);
 } 
 
 @PostMapping("/update/{id_plan}/plan-lot/{id_lot}")
 public ResponseEntity<Lot> updatePlanningByLot(@PathVariable(value = "id") Long formationId,
		 						@RequestParam(value ="name", required = false)  String name,
		 						@RequestParam(value ="IDformationsPlan", required = false) Set<Long> IDformationsPlan ,
		 						@RequestParam(value="IDparticipants", required = false) Set<Long> IDparticipants, 
		 						@RequestParam(value="departements", required = false)  Set<String> departement
                                        ) throws ResourceNotFoundException { 
     return lotService.updatePlanningLot(formationId, name, IDformationsPlan, IDparticipants, departement);
 }
 
 @PutMapping("/update/{id_lot}")

 public ResponseEntity<Lot> updateLot(@PathVariable(value = "id_lot") Long IDlot,
         @RequestParam(value = "name", required = false) String name,
         @RequestParam("IDparticipants") List<Long> IDparticipants,
         @RequestParam("departements") List<String> departements) throws ResourceNotFoundException { 

      return lotService.updateLot(IDlot, name, IDparticipants, departements);
 }
 

 @GetMapping("/{id}")
 public ResponseEntity<Lot> getLotById(@PathVariable(value = "id") Long lotId)
         throws ResourceNotFoundException {
      Lot lot= lotService.getLotById(lotId);
     return ResponseEntity.ok().body(lot);
 }
 
 
 @GetMapping("/ListFormationLot")
 public ResponseEntity<List<Object>> getListFormationAndLot() {
     return ResponseEntity.status(HttpStatus.OK).body(lotService.getAllFormationsAndLots());
 }
 
 @PutMapping("/newPlanning/{lotId}")
 public ResponseEntity<MessageResponse> addNewPlanning(@RequestParam("nameFormation") String nameFormation,
         @RequestParam("idFormateur") long idFormateur,
         @RequestParam("heure_date_Debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime heure_date_Debut,
         @RequestParam("heure_date_Fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime heure_date_Fin,
         @RequestParam("TypePlan") String typePlan,
         @RequestParam("repeterChaque") Integer frequence,
         @RequestParam(value = "description", required = false) String description,
         @RequestParam(value = "salle", required = false) String salle,
         @RequestParam(value = "enLigne", required = false) String EnLigne,
         @RequestParam(value="jourSemaine", required = false) Set<String> jourSemaine,
         @RequestParam("idParticipants") Set<Long> idParticipants,
         @RequestParam("idDocuments") Set<Long> idDocuments,
         @RequestParam("idDepartements") Set<String> idDepartements,
         @RequestParam("lotId") long idLot) {
	 
	 System.out.println("test fct newPlan");
	 			boolean enligne;
	 			if (EnLigne=="false") {
	 					enligne=false ;
	 			}else {
	 					enligne=true; 
	 				}
	 			if (typePlan.equals("jour")){
	 				 System.out.println("test jrs");
	 			    ResponseEntity<?> response = planningService.planParjrs(nameFormation, idFormateur, heure_date_Debut, heure_date_Fin, frequence, description, salle, enligne, idParticipants, idDocuments, idDepartements);
	 			   System.out.println("test jrs");
	 			    Map<String, Object> responseBody = (HashMap<String, Object>) response.getBody();
	 			    FormationPlan formationPlan = (FormationPlan) responseBody.get("formationPlan");
	 			   long idPlan = formationPlan.getId();
	 			  System.out.println(idPlan);
	 			lotService.addPlanningToLot(idPlan, idLot);
	 			    
	 			}else {
	 			    ResponseEntity<?> response = planningService.planParSemaine(nameFormation, idFormateur, heure_date_Debut, heure_date_Fin, frequence, description, salle, enligne, jourSemaine, idParticipants, idDocuments, idDepartements);
	 			   Map<String, Object> responseBody = (HashMap<String, Object>) response.getBody();
	 			    FormationPlan formationPlan = (FormationPlan) responseBody.get("formationPlan");
	 			   long idPlan = formationPlan.getId();
	 			 lotService.addPlanningToLot(idPlan, idLot);
	 			}
	 			 // ...code pour récupérer l'objet FormationPlan créé
 			    
	 			return ResponseEntity.ok(new MessageResponse("La formation planifiée est ajoutée au lot avec succès."));

 }
 
}
