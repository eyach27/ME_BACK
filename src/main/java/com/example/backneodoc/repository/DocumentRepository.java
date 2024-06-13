package com.example.backneodoc.repository;

import com.example.backneodoc.models.Department;
import com.example.backneodoc.models.Document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
	 
	
   // Optional<Document> findByTitre(String name);
    List<Document> findAll(Sort by);

    List<Document> findAllByName(String name);
    List<Document>  findByNameContaining(String name);
    List<Document> findByDepartementsContaining(String dep);
    List<Document> findByContentTypeContaining(String dep);
    //List<Document> findByTagsContaining(String dep);
    List<Document> findByDepartements(String dep);
    Optional<Document> findById(Long id);
    Optional<Document> findByName(String titre);
   // List<Department> getDepartementsByDocId(Long docId);
   
    @Query(value="select document.id,document.name,document.contentType,document.owner,document.path,document.size from document,tags,doc_tag where tags.libelle=?1 and tags.id=doc_tag.tag_id and document.id=doc_tag.doc_id",nativeQuery=true)
            List<Document> searchDocumentByTags(String libelle);
    @Query(value="select document.id,document.name,document.contentType,document.owner,document.path,document.size from document,department,doc_dep where department.name=?1 and department.id=doc_dep.id_dep and document.id=doc_dep.id_doc",nativeQuery=true)
    List<Document> searchDocumentByDepartments(String name);
    
    @Query("SELECT d.departements FROM Document d WHERE d.id = :documentId")
    List<Department> findDepartmentsByDocumentId(@Param("documentId") Long documentId);

   
}
