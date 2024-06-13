package com.example.backneodoc.Controllers;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.backneodoc.models.Department;
import com.example.backneodoc.payload.request.SignupRequest;
import com.example.backneodoc.payload.response.MessageResponse;
import com.example.backneodoc.repository.DepartmentRepository;
import com.example.backneodoc.services.DepartmentService;
@CrossOrigin(origins = "http://10.53.1.149:85")
@RestController

@RequestMapping("/api/departments")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @GetMapping("")
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @GetMapping("/{id}")
    public Department getDepartmentById(@PathVariable Long id) {
        return departmentService.getDepartmentById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteDepartmentById(@PathVariable Long id) {
        departmentService.deleteDepartmentById(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        Department existingDepartment = departmentService.getDepartmentById(id);
        if (existingDepartment == null) {
            return ResponseEntity.notFound().build();
        }
        existingDepartment.setName(department.getName());
        Department updatedDepartment = departmentService.addDepartment(existingDepartment);
        return ResponseEntity.ok(updatedDepartment);
    }
    
    @PostMapping("")
    public ResponseEntity<Department> addDepartment(@RequestBody String departmentName) {
        Department department = departmentService.addDepartment(departmentName);
        return ResponseEntity.ok(department);
    }
  

    
   
 	 

   
}