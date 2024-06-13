package com.example.backneodoc.services;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.example.backneodoc.Controllers.FileUploadController;
import com.example.backneodoc.Controllers.FormationController;
import com.example.backneodoc.models.Department;
import com.example.backneodoc.models.Document;
import com.example.backneodoc.models.ERole;
import com.example.backneodoc.models.Formation;
import com.example.backneodoc.models.FormationPlan;
import com.example.backneodoc.models.Role;
import com.example.backneodoc.models.Tag;
import com.example.backneodoc.models.User;
import com.example.backneodoc.payload.response.MessageResponse;
import com.example.backneodoc.repository.DepartmentRepository;
import com.example.backneodoc.repository.DocumentRepository;
import com.example.backneodoc.repository.FormationRepository;
import com.example.backneodoc.repository.PlanningRepository;
import com.example.backneodoc.repository.UserRepository;
import com.example.backneodoc.security.services.UserDetailsImpl;

@Service
public class FormationService {
	 @Autowired
	    DocumentRepository documentRepository;
	 @Autowired
	    DepartmentRepository departmentRepository;
	 @Autowired
	    UserRepository userRepository;
	 @Autowired
	 	FormationRepository formationRepository;
	 
	 @Autowired
	 	PlanningRepository planningRepository;
	 @Autowired
	 private DataSource dataSource;

	 public ResponseEntity<?> cree(String name, Long id_formateur, Set<Long> longDocuments, Set<Long> longDepartements) {
		 List<Formation> Formation = formationRepository.findAllByName(name);
         if (!Formation.isEmpty()) {
       	    System.out.println("Nom du fichier existe déjà");
       	    throw new IllegalArgumentException("Ajout de formation est échoué : Le nom de la formation existe déjà");
       	}	
		 
		 
		 /*Formation f = formationRepository.findByName(name);
         if (f != null) {
         
             return ResponseEntity.badRequest().body(new MessageResponse("Nom du formation existe déjà"));
         }*/
           
         User formateur = userRepository.getById(id_formateur);
         System.out.println(formateur.getUsername());
         System.out.println(formateur.getUsername());
         System.out.println(formateur.getUsername());
		   Set<Document> documents = new HashSet<>();
		 
		    if (longDocuments!= null) {
		    for (long doc : longDocuments) {
		        Document document = documentRepository.findById(doc)
		                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + doc));
		      
		        documents.add(document);
		    }}
		    
		    Set<Department> departements = new HashSet<>();
		    if (longDepartements!=null) {
		    for (long dep : longDepartements) {
		        Department departement=departmentRepository.findById(dep)
		        		.orElseThrow(() -> new IllegalArgumentException("Departement not found: " + dep));
		        departements.add(departement);
		    }}
		  
	
		    Formation formation = new Formation(name, formateur,documents,departements);
		  formationRepository.save(formation);
		    return ResponseEntity.ok(new MessageResponse(""));
		}

	 
	
	 public ResponseEntity<?> update(Long id, String name, Long id_formateur, Set<Long> longDocuments, Set<String> nameDepartements) {
		    Formation formation = formationRepository.findById(id)
		            .orElseThrow(() -> new IllegalArgumentException("Formation not found: " + id));

		    if (name != null && !name.isEmpty()) {
		        Formation f = formationRepository.findByName(name);
		        if (f != null && !f.getId().equals(formation.getId())) {
		            System.out.println("Nom de formation " + name + " existe déjà");
		            return ResponseEntity.badRequest().body(null);
		        }
		        formation.setName(name);
		    }

		    if (id_formateur != null) {
		        User formateur = userRepository.findById(id_formateur)
		                .orElseThrow(() -> new IllegalArgumentException("Formateur not found: " + id_formateur));
		        formation.setFormateur(formateur);
		    }
System.out.println(longDocuments);
Set<Document> documents = new HashSet<>();
		    if (longDocuments != null && !longDocuments.isEmpty()) {
		    
		       
		        for (Long doc : longDocuments) {
		            Document document = documentRepository.findById(doc)
		                    .orElseThrow(() -> new IllegalArgumentException("Document not found: " + doc));
		            documents.add(document);
		        }
		      
		    }
		    formation.setDocuments(documents);
		    if (nameDepartements != null && !nameDepartements.isEmpty()) {
		        Set<Department> departements = new HashSet<>();
		        for (String dep : nameDepartements) {
		            Department departement = departmentRepository.findByName(dep);
		            if (departement != null) {
		                departements.add(departement);
		            } else {
		                System.out.println("Département " + dep + " non trouvé");
		            }
		        }
		        formation.setDepartments(departements);
		    }

		    formationRepository.save(formation);

		    return ResponseEntity.ok(new MessageResponse(""));
		}
	 
	 public Formation GetFormationById(long id_formation) {
		 return formationRepository.getById(id_formation);
	 }
	 public Formation GetFormationByName(String name) {
		 return formationRepository.findByName(name);
	 }
	 
	 
	 public Map<String, Boolean> DeleteById(long id_formation) {
		Set<FormationPlan> list =planningRepository.findByFormation_Id(id_formation);
		for (FormationPlan  plan : list ) {
			planningRepository.deleteById(plan.getId());
		}
		 formationRepository.deleteById(id_formation);
		 Map<String, Boolean> response = new HashMap<>();
	        response.put("supprimée", Boolean.TRUE);
	        return response;
	 }
	 
	 
	 public List<Formation> getAllFormations() {
	        List<Formation> formationList= formationRepository.findAll(Sort.by(Sort.Direction.ASC,"name"));
	        return formationList;
	    }
	 
    

	 
}
