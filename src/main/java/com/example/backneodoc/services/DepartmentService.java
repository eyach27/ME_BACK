package com.example.backneodoc.services;
import com.example.backneodoc.repository.DepartmentRepository;
import com.example.backneodoc.repository.FormationRepository;
import com.example.backneodoc.repository.LotRepository;
import com.example.backneodoc.repository.PlanningRepository;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.backneodoc.models.Department;
import com.example.backneodoc.models.Document;
import com.example.backneodoc.models.Formation;
import com.example.backneodoc.models.FormationPlan;
import com.example.backneodoc.models.Lot;

@Service
public class DepartmentService {
	 public DepartmentService(DepartmentRepository departmentRepository) {
	        this.departmentRepository = departmentRepository;
	    }
	 @Autowired
	    private DepartmentRepository departmentRepository;
	 @Autowired
	    private FormationRepository  formationRepository;
	 @Autowired
	    private PlanningRepository  planningRepository;
	 @Autowired
	    private LotRepository  lotRepository;
	 
	    public  List<Department> getAllDepartments() {
	        return departmentRepository.findAll();
	    }

	    public Department getDepartmentById(Long id) {
	        return departmentRepository.findById(id).orElse(null);
	    }
	    public Department getDepartmentByName(String name) {
	        return departmentRepository.findByName(name);
	    }

	    public Department addDepartment(Department department) {
	        return departmentRepository.save(department);
	    }
	    
	    
	    public Department addDepartment(String departmentName) {
	        Department department = new Department();
	        department.setName(departmentName);
	        return departmentRepository.save(department);
	    }
	    

	    public void deleteDepartmentById(Long id) {
	    	Department dep = departmentRepository.getById(id);
	        
	        // Récupérez toutes les formations qui contiennent ce département
	        List<Formation> formations = formationRepository.findAllByDepartmentsContaining(dep);
	        // Récupérez toutes les formationsPlan qui contiennent ce département
	        List<FormationPlan> formationsPlan = planningRepository.findAllByDepartementsContaining(dep);
	        // Récupérez toutes les lots qui contiennent ce département
	        List<Lot> lots = lotRepository.findAllByDepartmentsContaining(dep);
	        
	        // Supprimez le département de chaque formation
	        for (Formation formation : formations) {
	            formation.getDepartments().remove(dep);
	            formationRepository.save(formation);
	        }
	     // Supprimez le département de chaque formationPlan
	        for (FormationPlan formationPlan : formationsPlan) {
	            formationPlan.getDepartements().remove(dep);
	            planningRepository.save(formationPlan);
	        }
	     // Supprimez le département de chaque lot
	        for (Lot lot : lots) {
	            lot.getDepartments().remove(dep);
	            lotRepository.save(lot);
	        }
	        
	        // Supprimez le département lui-même
	        departmentRepository.delete(dep);
	    }
	    public Department updateDepartment(Long id, Department department) {
	        Department existingDepartment = departmentRepository.findById(id).orElse(null);
	        if (existingDepartment != null) {
	            existingDepartment.setName(department.getName());
	            return departmentRepository.save(existingDepartment);
	        }
	        return null;
	    }
	    
	  
	    
	    
	    
	    
	
}
