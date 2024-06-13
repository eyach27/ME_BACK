package com.example.backneodoc.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backneodoc.models.Department;
import com.example.backneodoc.models.Document;
import com.example.backneodoc.models.Formation;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {
	
	List<Formation> findAllByName(String name );
    Formation findByName(String name);
    @Query("SELECT f FROM Formation f JOIN FETCH f.formateur")
    List<Formation> findAll();
    
    Formation findById(long id);
	List<Formation> findAllByDepartmentsContaining(Department dep);

}