package com.example.backneodoc.Controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backneodoc.Exceptions.ResourceNotFoundException;
import com.example.backneodoc.models.Department;
import com.example.backneodoc.models.Document;
import com.example.backneodoc.models.ERole;
import com.example.backneodoc.models.Formation;
import com.example.backneodoc.models.Role;
import com.example.backneodoc.models.User;
import com.example.backneodoc.payload.request.SignupRequest;
import com.example.backneodoc.payload.response.MessageResponse;
import com.example.backneodoc.repository.DepartmentRepository;
import com.example.backneodoc.repository.DocumentRepository;
import com.example.backneodoc.repository.FormationRepository;
import com.example.backneodoc.repository.UserRepository;
import com.example.backneodoc.services.DocumentServices;
import com.example.backneodoc.services.FormationService;

@CrossOrigin(origins = "http://10.53.1.149:85")
@RestController
@RequestMapping("/api/formations")
public class FormationController {
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
    
    
    @PostMapping(path = "/create")
    public ResponseEntity<?> createFormation(@RequestParam("name")  String name,@RequestParam(value="formateur", required = false) long id_formateur ,@RequestParam(value="documents", required = false) Set<Long> documents, @RequestParam(value="departements", required = false)  Set<Long> departement) {
      System.out.println(departement);
    	if (id_formateur == 0L) {
    	    User formateur = userRepository.findByUsername("FORMATEUR.INCONNU");
    	    if (formateur != null) {
    	        id_formateur = formateur.getId();
    	    } }
    	System.out.println(id_formateur);
        formationService.cree(name, id_formateur, documents, departement);
    	System.out.println("apres cree");
        return ResponseEntity.ok(new MessageResponse("success"));
    }

    @GetMapping("/list")
    public ResponseEntity<List<Formation>> getListFor() {
        return ResponseEntity.status(HttpStatus.OK).body(formationService.getAllFormations());
    }

    @DeleteMapping("/delete/{id}")
    public Map<String, Boolean> deleteFormation(@PathVariable(value = "id") Long formationId) throws ResourceNotFoundException {
        return formationService.DeleteById(formationId);
    } 
    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateFormation(@PathVariable(value = "id") Long formationId,
    		@RequestParam("name")  String name,@RequestParam("formateur") String username_formateur ,@RequestParam(value="documents", required = false) Set<Long> documents, @RequestParam(value ="departements" , required = false)  Set<String> departement
                                           ) throws ResourceNotFoundException { 
    	System.out.print("test");
    	long id_formateur=userRepository.findByUsername(username_formateur).getId();
        return formationService.update(formationId, name, id_formateur, documents, departement);
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<Formation> getFormationById(@PathVariable(value = "id") Long formationId)
            throws ResourceNotFoundException {
         Formation formation=formationService.GetFormationById(formationId);
        return ResponseEntity.ok().body(formation);
    }
    @GetMapping("/GetByName/{name}")
    public ResponseEntity<Formation> getFormationByName(@PathVariable(value = "name")String nameFormation)
            throws ResourceNotFoundException {
         Formation formation=formationService.GetFormationByName(nameFormation);
        return ResponseEntity.ok().body(formation);
    }
    
    
}