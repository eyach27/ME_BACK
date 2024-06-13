package com.example.backneodoc;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.example.backneodoc.Controllers.FormationController;
import com.example.backneodoc.Controllers.LotController;
import com.example.backneodoc.models.Department;
import com.example.backneodoc.models.Document;
import com.example.backneodoc.models.Formation;
import com.example.backneodoc.models.FormationPlan;
import com.example.backneodoc.models.Lot;
import com.example.backneodoc.models.User;
import com.example.backneodoc.payload.response.MessageResponse;
import com.example.backneodoc.repository.DocumentRepository;
import com.example.backneodoc.repository.FormationRepository;
import com.example.backneodoc.repository.LotRepository;
import com.example.backneodoc.repository.PlanningRepository;
import com.example.backneodoc.repository.UserRepository;
import com.example.backneodoc.services.DepartmentService;
import com.example.backneodoc.services.DocumentServices;
import com.example.backneodoc.services.FormationService;
import com.example.backneodoc.services.LotService;
import com.example.backneodoc.services.PlanningService;
import com.example.backneodoc.repository.DepartmentRepository;

@SpringBootApplication
public class BackNeoDocApplication  extends SpringBootServletInitializer implements CommandLineRunner 
{	
	@Autowired
	private DocumentServices DocumentServices;
	
	
	  @Autowired
	  private PlanningRepository planRepository ;
	  
    public static void main(String[] args) {
        SpringApplication.run(BackNeoDocApplication.class, args);
    }
    
   
    /* sont ajouter pour la creation de  fichier war*/
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
	{
		return application.sources(BackNeoDocApplication.class);
	}
    
    @RequestMapping(value = "/pfe")
    @Override public void run(String... args) throws Exception {
    
    	Demo();
    	
    }
    private void Demo() {
    	try {
    		//Long plan = (long) 5;
    		//int i =planRepository.findOccurrenceCountByIdPlanning(plan);
    	//	System.out.println(i);
    		
    		/* String name = "Lot 2";
    		    Set<Long> IDformationsPlan = new HashSet<>(Arrays.asList(6L, 7L, 12L));
    		    Set<Long> IDparticipants = new HashSet<>(Arrays.asList(8L,51L));
    		    Set<String> NAMEdepartments = new HashSet<>(Arrays.asList("RH", "IT"));
    		 //  lotService.createLot(name, IDformationsPlan, IDparticipants, NAMEdepartments);
    		    Set<FormationPlan> formationsPlan= (Set<FormationPlan>) planningServices.getByFormationNom("Introduction to Programming");
    		    Set<User> participants = new HashSet<>();
    		    
    		    for (long id_participant : IDparticipants) {
    		        User participant = userRepository.findById(id_participant)
    		                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id_participant));
    		      
    		        participants.add(participant);
    		    }
    		    Set<Department> departements = new HashSet<>();
    		    
    		    for (String namedep : NAMEdepartments) {
    		        Department departement = departementRepository.findByName(namedep);
    		        departements.add(departement);
    		    }   
    		    Lot l = new Lot( name,  formationsPlan,  participants,  departements);
    		    System.out.println(l);
    		  //lotRepository.save(l);
    		   // ResponseEntity<MessageResponse> response =  (ResponseEntity<MessageResponse>) lotController.createLot(name, IDformationsPlan, IDparticipants, NAMEdepartments);

    		    /*if (response.getStatusCode() == HttpStatus.OK) {
    		        MessageResponse messageResponse = response.getBody();
    		        System.out.println(messageResponse.getMessage()); // Affiche "success"
    		    } else {
    		        System.out.println("Error: " + response.getStatusCodeValue());
    		    }*/
    		
    		/*LocalDateTime heure_date_Debut = LocalDateTime.of(2023, 5, 15, 9, 0); // start date and time
    	LocalDateTime heure_date_Fin = LocalDateTime.of(2023, 5, 29, 10, 0); // end date and time
    	int repeterChaque = 1; 
    	Set<DayOfWeek> jourSemaine = new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));

    	//Set<LocalDateTime[]> intervals = planService.getPlanningIntervalsJRS(heure_date_Debut, heure_date_Fin, repeterChaque);
    	Set<LocalDateTime[]> intervals = planService.getPlanningIntervals(jourSemaine, heure_date_Debut, heure_date_Fin, repeterChaque);
    	// Print out the intervals
    	 System.out.println(intervals);
    	for (LocalDateTime[] interval : intervals) {
    	    System.out.println(interval[0] + " - " + interval[1]);
    	}*/
    	
    	/*Formation formation = formationRepository.findByName("Introduction to Programming");
    	if (formation != null) {
    	    System.out.println("Formation found: " + formation);
    	} else {
    	    System.out.println("Formation not found");
    	  
    	}
    	 long id_formation=formation.getId();
    	List<User> participants = userRepository.findAllfromateur();
    	Set<Long> id_participants= new HashSet<>();
    	
    	for ( User participant : participants) {
    		id_participants.add(participant.getId());
    	}
    	
    	LocalDateTime startDate = LocalDateTime.of(2023, 4, 13, 15, 00);
    	
    	
    	 Set<String> STRjourSemaine= new HashSet<>();
    	// STRjourSemaine.add("MONDAY");
    	 STRjourSemaine.add("wednesday");
    	
    	 String description="test 2 formation par semaine ";
    	 Integer duree=2;
    	 
    	 //planningServices.planParSemaine( id_formation,  startDate, description,STRjourSemaine,  duree,  id_participants); 
    	 
    	
    		*/
    	/*	Set<DayOfWeek> jourSemaine = new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
    		LocalDateTime heure_date_Debut = LocalDateTime.of(2023, 5, 1, 10, 0);
    		LocalDateTime heure_date_Fin = LocalDateTime.of(2023, 5, 25, 12, 0);
    		int repeterChaque = 3;

    		Set<LocalDateTime[]> intervals = planService.getPlanningIntervalsTest(jourSemaine, heure_date_Debut, heure_date_Fin, repeterChaque);
    		 
    		for (LocalDateTime[] interval : intervals) {
    		    System.out.println("Interval start: " + interval[0] + " - Interval end: " + interval[1]);
    		}
    		*/
    		
    		
    		} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }

private void Demo2() {
	try {
	Document document = DocumentServices.getDocByName("Backlog.pdf");
			System.out.println("id: " + document.getId());
			System.out.println("name: " + document.getName());
			//System.out.println("departement: " + document.getDepartements());
			System.out.println("================================= "  );
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
private void Demo3() {
	try {
		/*Document document = DocumentServices.getDocById((long) 14);
		System.out.println("id: " + document.getId());
		System.out.println("name: " + document.getName());
		System.out.println("departement: " + document.getDepartements());
		System.out.println("================================= "  );*/
	
		for(Document document : ( List<Document>) DocumentServices.getSearchDocTitre("Backlog.pdf"))
	
			{System.out.println("id: " + document.getId());
			System.out.println("name: " + document.getName());
		//	System.out.println("departement: " + document.getDepartements());
			System.out.println("================================= "  );}
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

}
