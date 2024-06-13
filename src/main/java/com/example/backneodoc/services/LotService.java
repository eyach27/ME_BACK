package com.example.backneodoc.services;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;



import com.example.backneodoc.Exceptions.ResourceNotFoundException;
import com.example.backneodoc.models.Department;
import com.example.backneodoc.models.Document;
import com.example.backneodoc.models.ETypePlan;
import com.example.backneodoc.models.Formation;
import com.example.backneodoc.models.FormationPlan;
import com.example.backneodoc.models.Lot;
import com.example.backneodoc.models.User;
import com.example.backneodoc.payload.response.MessageResponse;
import com.example.backneodoc.repository.DepartmentRepository;
import com.example.backneodoc.repository.FormationRepository;
import com.example.backneodoc.repository.LotRepository;
import com.example.backneodoc.repository.PlanningRepository;
import com.example.backneodoc.repository.UserRepository;
@Service
public class LotService {
	 @Autowired
	    private LotRepository lotRepository;
	 @Autowired
	    private PlanningRepository planningRepository;
	 @Autowired
	    private UserRepository userRepository;
	 @Autowired
	    private DepartmentRepository departementRepository;
	 @Autowired
	    private FormationRepository formationRepository;
	 @Autowired
	    private PlanningService planningService;
	 @Autowired
	 private DataSource dataSource;

	
	 
	    public List<Lot> getAllLots() {
	        return lotRepository.findAll();
	    }

	    public Lot getLotById(Long id) {
	        return lotRepository.getById(id);
	    }

	    public ResponseEntity<?>  createLot(String name, Set<Long> IDformationsPlan, Set<Long> IDparticipants, Set<String> NAMEdepartments) {
	    	 try {
	    		 Lot l = lotRepository.findByName(name);
	    	 
	         if (l != null) {
	             System.out.println("Nom du lot existe déjà");
	             return ResponseEntity.badRequest().body(new MessageResponse("Nom du lot existe déjà"));
	         }
	       
	    	Set<FormationPlan> formationsPlan = new HashSet<>();
	    	for(long id :IDformationsPlan) {
	    	FormationPlan formationPlan = planningRepository.getById(id);
	    	formationsPlan.add(formationPlan);}
	    	
	    	Set<User> participants = new HashSet<>();
	    	for(long id :IDparticipants) {
	    	User participant = userRepository.getById(id);
	    	participants.add(participant);}
	    	

	    	Set<Department> departments = new HashSet<>();
	    	for(String dep :NAMEdepartments) {
	    	Department departement =departementRepository.findByName(dep) ;
	    	departments.add(departement);
	    	}
	    	
	    	
	    	for(FormationPlan plan :formationsPlan) {
		    	plan.setParticipants(participants);
		    	planningRepository.save(plan);
		    	}
	    	
	    	Lot lot = new Lot( name, formationsPlan,  participants, departments);
	    	
	    	 lotRepository.save(lot);
	         return ResponseEntity.ok(new MessageResponse("Lot créé avec succès"));
	     } catch (Exception e) {
	         return ResponseEntity.badRequest().body(new MessageResponse("Erreur lors de la création du lot"));
	     }
	    }

	  public ResponseEntity<Lot> updatePlanningLot(Long idLot ,String name, Set<Long> IDformationsPlan, Set<Long> IDparticipants, Set<String> NAMEdepartments) throws ResourceNotFoundException {
	    	Lot lot = lotRepository.findById(idLot)
	    			.orElseThrow(() -> new ResourceNotFoundException("lot non trouvé pour cet id: " + idLot));
	    	

	    	Set<FormationPlan> formationsPlan = new HashSet<>();
	    	for(long id :IDformationsPlan) {
	    	FormationPlan formationPlan = planningRepository.getById(id);
	    	formationsPlan.add(formationPlan);}
	    	
	    	Set<User> participants = new HashSet<>();
	    	for(long id :IDparticipants) {
	    	User participant = userRepository.getById(id);
	    	participants.add(participant);}
	    	

	    	Set<Department> departments = new HashSet<>();
	    	for(String dep :NAMEdepartments) {
	    	Department departement =departementRepository.findByName(dep) ;
	    	departments.add(departement);
	    	}
	    	lot.setName(name);
	    	lot.setDepartments(departments);
	    	lot.setFormationsPlan(formationsPlan);
	    	lot.setParticipants(participants);
	        return ResponseEntity.ok(lotRepository.save(lot));
	    }
	    
	      public Map<String, Boolean> deleteLot(Long id) {
	        lotRepository.deleteById(id);
	        Map<String, Boolean> response = new HashMap<>();
	        response.put("supprimée", Boolean.TRUE);
	        return response;
	    }
	      
	      
	      public List<Object> getAllFormationsAndLots() {
	    	    List<Object> formationsAndLots = new ArrayList<Object>();

	    	    List<Formation> formationList = formationRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
	    	    formationsAndLots.addAll(formationList);
	    	    System.out.println(formationList);

	    	    List<Lot> lotList = lotRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
	    	    formationsAndLots.addAll(lotList);

	    	    return formationsAndLots;
	    	}
	   public Lot updatePlanningLot(long idLot , FormationPlan plan ) {
	    	  ///////// cherche si le planning est representer dans d autre lots ; 
	    	int nbLot = planningRepository.findOccurrenceCountByIdPlanning(plan.getId());
	    	Lot lotUpdated = lotRepository.getById(idLot);
	    	// recupere le plan par id 
	    	FormationPlan oldPlan = planningRepository.findById(plan.getId()).orElse(null);
	        if (oldPlan != null)    {
	        	 FormationPlan newPlan ;
	    		    Formation formation = plan.getFormation();
	    		    User formateur = plan.getFormateur();
	    		    LocalDateTime startDate = plan.getStartDate();
	    		    LocalDateTime endDate = plan.getEndDate();
	    		    int frequence = plan.getFrequence();
	    		    String description = plan.getDescription();
	    		    String salle = plan.getSalle();
	    		    boolean enLigne = plan.getEnLigne();
	    		    ETypePlan typePlan = plan.getTypePlan();
	    		    Set<User> participants = plan.getParticipants();
	    		    Set<Document> documents = plan.getDocuments();
	    		    Set<Department> departements = plan.getDepartements();
	    		// cree un nv plan 
	    		if (nbLot>=2) {
	    		
	    		 if (plan.getTypePlan().toString()=="parSemaine") { 
	    			 Set<DayOfWeek> joursSemaine = plan.getJourSemaine();
	    			  newPlan = new FormationPlan(formation, formateur, startDate, endDate, frequence, description,salle, enLigne,
	    					  joursSemaine,  participants,  documents,  departements);

	    		 }
	    		 //// else le type est par jour
	    		 else {
	    			 newPlan = new FormationPlan(formation, formateur, startDate, endDate, frequence, description,salle, enLigne,
	    					    participants,  documents,  departements);

	    		 }
	    		  FormationPlan savedPlan = planningRepository.save(newPlan);
	    			  
	    			 // add the new plan and remove the old one
	    			
	    			Set<FormationPlan> formationsPlan = lotUpdated.getFormationsPlan();
	    			formationsPlan.remove(oldPlan);
	    			formationsPlan.add(savedPlan);
	    			lotUpdated.setFormationsPlan(formationsPlan);
                     
	    		 }                  
	    	else {
	    	    // Il y a un seul lot pour cette formation, on peut donc modifier le plan existant directement

	    		oldPlan.setFormation(plan.getFormation());
	    		oldPlan.setFormateur(plan.getFormateur());
	    		oldPlan.setStartDate(plan.getStartDate());
    		    oldPlan.setEndDate(plan.getEndDate());
    		    oldPlan.setFrequence(plan.getFrequence());
    		    oldPlan.setDescription(plan.getDescription());
    		    oldPlan.setSalle(plan.getSalle());
    		    oldPlan.setEnLigne(plan.getEnLigne());
    		    oldPlan.setTypePlan(plan.getTypePlan());
    		    oldPlan.setParticipants(plan.getParticipants());
    		    oldPlan.setDocuments( plan.getDocuments());
    		    oldPlan.setDepartements(plan.getDepartements());
    		    if (plan.getTypePlan().toString()=="parSemaine") { 
    		    	 oldPlan.setJourSemaine(plan.getJourSemaine());
	    			}
    		    planningRepository.save(oldPlan);
    	
	    	   }
	    			
	          }
	    	 
	    	  return lotUpdated ;
	      
          }
	   
public Map<String, Boolean> RemovePlanningFromLot(long idPlanning ,long idLot) {
	FormationPlan plan=planningRepository.getById(idPlanning);
	Set<FormationPlan> planningsLot =lotRepository.getById(idLot).getFormationsPlan();
	planningsLot.remove(plan);
	Lot lotUpdated=	lotRepository.getById(idLot);
	lotUpdated.setFormationsPlan(planningsLot);
	lotUpdated.setStartDate(Lot.getMinStartDate(lotUpdated.getFormationsPlan()));
	lotUpdated.setEndDate(Lot.getMaxEndDate(lotUpdated.getFormationsPlan()));
	lotRepository.save(lotRepository.getById(idLot));
	 Map<String, Boolean> response = new HashMap<>();
     response.put("supprimé", Boolean.TRUE);
     return response;}

public Map<String, Boolean>  addPlanningToLot(long idPlanning ,long idLot) {
	FormationPlan plan=planningRepository.getById(idPlanning);
	Set<FormationPlan> planningsLot =lotRepository.getById(idLot).getFormationsPlan();
	planningsLot.add(plan);
	Lot lotUpdated=	lotRepository.getById(idLot);
	lotUpdated.setFormationsPlan(planningsLot);
	lotUpdated.setStartDate(Lot.getMinStartDate(lotUpdated.getFormationsPlan()));
	lotUpdated.setEndDate(Lot.getMaxEndDate(lotUpdated.getFormationsPlan()));
	lotRepository.save(lotRepository.getById(idLot));
	 Map<String, Boolean> response = new HashMap<>();
     response.put("La formation planifiée a été ajoutée au lot", Boolean.TRUE);
     return response;
}


public ResponseEntity<Lot> updateLot(Long idLot, String name ,List<Long> id_participants, List<String> name_departments) throws ResourceNotFoundException {
	System.out.println("test update");
	  Lot lot =lotRepository.findById(idLot)
      .orElseThrow(() -> new ResourceNotFoundException("lot non trouvé pour cet id: " + idLot));
	
	  lot.setName(name);
	  
	  System.out.println(id_participants);
	 Set<User> participants = id_participants.stream()
	            .map(id -> userRepository.findById(id)
	                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + id)))
	            .collect(Collectors.toSet());
	  System.out.println("test participants");
	  lot.setParticipants(participants);
	  
	  Set <FormationPlan> formationsPlan =lot.getFormationsPlan();
		for(FormationPlan plan :formationsPlan) {
	    	plan.setParticipants(participants);
	    	planningRepository.save(plan);
	    	}
	
	  Set<Department> departements = new HashSet<>();
	  System.out.println("test dep");
	  // Add departements to the set
	    for (String id_departement : name_departments) {
	        Department departement = departementRepository.findByName(id_departement);
	        departements.add(departement);
	    }
	  lot.setDepartments(departements);
	  System.out.println("t save");
	 Lot l= lotRepository.save(lot);
	 return  ResponseEntity.ok(l);

	
}



}
