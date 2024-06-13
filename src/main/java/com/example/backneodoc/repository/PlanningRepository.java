package com.example.backneodoc.repository;


import com.example.backneodoc.models.Department;
import com.example.backneodoc.models.Formation;
import com.example.backneodoc.models.FormationPlan;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface PlanningRepository extends JpaRepository<FormationPlan, Long> {
	List<FormationPlan> findAllByDepartementsContaining(Department dep);

	
	 Set<FormationPlan> findByFormationName(String formationNom);
	@Query("SELECT fp FROM FormationPlan fp JOIN fp.formation f WHERE f.formateur.id = :formateurId")
	Set<FormationPlan> findFormationPlanByFormateurId(@Param("formateurId") Long formateurId);

	 @Query("SELECT fp FROM FormationPlan fp JOIN fp.participants p WHERE p.id = :ParticipantId")
	   Set<FormationPlan> findFormationPlanByParticipantId(@Param("ParticipantId") Long ParticipantId);
	 
	 @Query("SELECT fp FROM FormationPlan fp WHERE fp.formateur.id = :FormateurId")
	    Set<FormationPlan> findFormationPlanForFormateur(@Param("FormateurId") Long formateurId);
	 @Query("SELECT fp FROM FormationPlan fp WHERE NOT EXISTS "
	            + "(SELECT 1 FROM Lot l JOIN l.formationsPlan fp2 WHERE fp2.id = fp.id)")
	    Set<FormationPlan> findUnassignedFormationPlans();
	 
	//Set<FormationPlan> findByFormation(long formationID);
	//	@Query("SELECT fp FROM FormationPlan fp JOIN fp.formation f WHERE f.formateur.id = :formateurId");
	 Set<FormationPlan> findByFormation_Id(Long formationId);
	 
	 @Query("SELECT p FROM FormationPlan p WHERE p.formation.id = :formationId AND (p.startDate >= :currentDate OR p.endDate <= :currentDate) ORDER BY CASE WHEN p.startDate >= :currentDate THEN p.startDate ELSE p.endDate END")
	 Set<FormationPlan> findByFormation_IdOrderByDate(@Param("formationId") Long formationId, @Param("currentDate") LocalDateTime currentDate);

	 @Query("SELECT COUNT(pl) FROM Lot l JOIN l.formationsPlan  pl WHERE pl.id = :idPlanning")
	    Integer findOccurrenceCountByIdPlanning(@Param("idPlanning") Long idPlanning);


}
