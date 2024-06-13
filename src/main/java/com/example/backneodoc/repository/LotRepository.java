package com.example.backneodoc.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backneodoc.models.Lot;
import com.example.backneodoc.models.Department;
import com.example.backneodoc.models.FormationPlan;
@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
	  Lot findByName(String name);
	  List<Lot> findAllByDepartmentsContaining(Department dep);  
}
