package com.example.backneodoc.services;

import com.example.backneodoc.Controllers.FileUploadController;
import com.example.backneodoc.Exceptions.ResourceNotFoundException;
import com.example.backneodoc.models.*;
import com.example.backneodoc.payload.response.MessageResponse;
import com.example.backneodoc.repository.DepartmentRepository;
import com.example.backneodoc.repository.DocumentRepository;
import com.example.backneodoc.repository.RoleRepository;
import com.example.backneodoc.repository.TagRepository;
import com.example.backneodoc.repository.UserRepository;
import com.example.backneodoc.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;


@Service
public class DocumentServices implements FileStorageServie {
    public DocumentServices(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    @Autowired
    private DataSource dataSource;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    TagRepository tagRepository;
    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    RoleRepository roleRepository;

   // private final Path root = Paths.get("/usr/share/tomcat/webapps/uploads/");
    private final Path root = Paths.get("uploads/");
//private final Path tomcat = Paths.get("usr/share/tomcat/");

    public void init() {
        try {
            Files.createDirectory(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public void save(MultipartFile file, String customFilename) {
        try {
            String filename = StringUtils.isEmpty(customFilename) ? file.getOriginalFilename() : customFilename;
            Files.copy(file.getInputStream(), this.root.resolve(filename));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> saveMultiple(MultipartFile[] uploadedFile, Set<String> tags, Set<String> deps, String name ) {
        for (MultipartFile fileDto : uploadedFile) {
            String originalName = fileDto.getOriginalFilename();
            int dotIndex = originalName.lastIndexOf(".");
            String filename = StringUtils.isEmpty(name) ? originalName : name + originalName.substring(dotIndex);
          
          List<Document> documents = documentRepository.findAllByName(filename);
          if (!documents.isEmpty()) {
        	    System.out.println("Nom du fichier existe déjà");
        	    throw new IllegalArgumentException("Ajout du document est échoué : Le nom du fichier existe déjà");
        	}
          
         // String path = this.root.toString() + "/" + filename;
          String path = "/usr/share/tomcat/webapps/uploads/" + filename;
            //String path = "uploads/" + filename;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String owner = userDetails.getUsername();
            String docname = filename;


    	            Set<Tag> stag=new HashSet<>();
    	            for ( String tag : tags) {
    	                
    	                if (tagRepository.findByLibelle(tag) != null) {
    	                    
    	                    stag.add(tagRepository.findByLibelle(tag));
    	                   
    	                } else {
    	                    Tag ntag = new Tag(tag);
    	                    tagRepository.save(ntag);
    	                    stag.add(ntag);
    	                }
    	            }
    	            
    	            
    	            
    	            Set<Department> sdep = new HashSet<>();
    	            for (String dep : deps) {
    	             
    	                Department department = departmentRepository.findByName(dep);
    	            
	                  
    	                if (department != null) {
    	                    System.out.println("dep trouvee ");
    	                    sdep.add(department);
    	                } else {
    	                    System.out.println("dep non trouvee!!");
    	                }
    	            }   
    	            
    	            
    	            
    	            try {
    	                Files.copy(fileDto.getInputStream(), this.root.resolve(filename));
    	                
    	            } catch (Exception e) {
    	            	 System.out.println("Erreur lors du traitement du fichier : " + e.getMessage()+"\n LocalizedMessage:  "+e.getLocalizedMessage());
    	                throw new RuntimeException("Erreur lors du traitement du fichier.");
    	               
    	            }
    	            Document file = new Document(filename,fileDto.getSize(),fileDto.getContentType(), owner,path,stag,sdep);
    	           
    	            Document docc =documentRepository.save(file);
    	            System.out.println("path fichier enregistree :"+docc.getPath());
    /*	            Role admin = roleRepository.findByName(ERole.ROLE_ADMIN)
    	                    .orElseThrow(() -> new RuntimeException("Erreur: Role n'existe pas."));
    	            List<String> mails=userRepository.searchUserByRole(admin.getId().longValue());
    	            for (String mail:mails){
    	                System.out.println(mail);
    	            }
    	            for (String mail:mails){
    	                System.out.println(mail);
    	                SimpleMailMessage mailMessage = new SimpleMailMessage();
    	                mailMessage.setTo(mail);
    	                mailMessage.setSubject("Ajout d'un nouveau document");
    	                mailMessage.setFrom("issra.khemir@etudiant-isi.utm.tn");
    	                mailMessage.setText("L'employé ' " + owner +" ' a ajouté un nouveau document intitulé ' " + docname + " ' dans le département " +deps);
    	                javaMailSender.send(mailMessage);
    	            }*/
    	        }
    	        return ResponseEntity.ok().body(new MessageResponse("Document ajouté avec succcés."));

    	    }
    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
          
            Resource resource = new UrlResource(file.toUri());
          
            System.out.println(resource.isReadable());
            if (resource.exists() || resource.isReadable()) {
               
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    @Override
    public List<Document> getAllFiles() {
        List<Document> fileList= documentRepository.findAll(Sort.by(Sort.Direction.ASC,"contentType","name"));
        List<Document> fileInfos = fileList.stream().map(file -> {
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FileUploadController.class, "getFile",file.getName()).build().toString();
file.setPath(url);
            return new Document(file);
        }).collect(Collectors.toList());
        return fileInfos;
    }

    public Map<String, Boolean> deleteDoc(Long docId) throws ResourceNotFoundException {
    	
    	
        Document document = documentRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Document non trouvé pour cet id:: " + docId));
        List<User> users=userRepository.searchUserByFav(document.getId());
        for(User user:users){
            Set<Document> fav = user.getDoc_favoris();
            fav.remove(document);
            user.setDoc_favoris(fav);
            System.out.println(user.getUsername());
            for (Document doc:(user.getDoc_favoris()) ) {
            	 System.out.println(doc.getName());
            }
           
        }
        Path path= Paths.get(document.getPath()) ;

        try {
            // Delete file or directory
            Files.deleteIfExists(path);
         //   Files.deleteIfExists(pathServer);
            System.out.println("File or directory deleted successfully");
        } catch (NoSuchFileException ex) {
            System.out.printf("No such file or directory: %s\n", path);
        } catch (DirectoryNotEmptyException ex) {
            System.out.printf("Directory %s is not empty\n", path);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        documentRepository.delete(document);
        Map<String, Boolean> response = new HashMap<>();
        response.put("supprimé", Boolean.TRUE);
        return response;
    }

    public Document getDocByName(String name){
        Optional<Document> fileOptional=documentRepository.findByName(name);
        if(fileOptional.isPresent()){ System.out.println("here");
            return fileOptional.get();}
        return null;}

    public Document getDocById(Long id){
        Optional<Document> fileOptional=documentRepository.findById(id);
        if(fileOptional.isPresent()){ System.out.println("here");
            return fileOptional.get();}
        return null;}

    public List<Document> getSearchDocTitre(String titre){
        //   return documentRepository.searchDocumentByTitreContaining(titre);
        return documentRepository.findByNameContaining(titre);
    }
    public List<Document> getSearchDocDep(String dep){
        return documentRepository.searchDocumentByDepartments(dep);
    }
    public List<Document> getSearchDocType(String type){
        return documentRepository.findByContentTypeContaining(type);
    }
    public List<Document> getSearchDocTag(String tag){
        return documentRepository.searchDocumentByTags(tag);
    }
    public List<Document> getbyDep(String dep){
        return documentRepository.findByDepartements(dep);
    }
    public List<Department> getDepartmentsByDocumentId(Long documentId) {
        // Find the document by ID
        Optional<Document> document = documentRepository.findById(documentId);
        if (!document.isPresent()) {
            // Handle case where document is not found
            throw new IllegalArgumentException("Document not found");
        }

        // Get the list of departments for the document
        return documentRepository.findDepartmentsByDocumentId(documentId);
    }
    public ResponseEntity<Document> updateDoc(Long docId,String titre,Set<String> deps,Set<String> tags) throws ResourceNotFoundException {
  System.out.println(titre);
    	    Set<Tag> stag = new HashSet<>();
    	    Set<Department> sdep = new HashSet<>();
    	    
    	    // récupération du document existant dans la base de données
    	    Document document = documentRepository.findById(docId)
    	            .orElseThrow(() -> new ResourceNotFoundException("document non trouvé pour cet id: " + docId));

    	    // Vérification si le nom a été modifié et si c'est le cas, vérification de l'unicité du nom
    	    if (!titre.equals(document.getName())) {
    	        List<Document> documentsByNewName = documentRepository.findAllByName(titre);
    	        if (documentsByNewName.size() > 0) {
    	            throw new IllegalArgumentException("Modification du document échouée : Le nom du fichier " + titre + " existe déjà");
    	        }
    	        String oldFilename = document.getName();
    	        String newFilename = titre ;
    	        Path oldFilePath = Paths.get("uploads/" + oldFilename);
    	        Path newFilePath = Paths.get("uploads/"+ newFilename);
    	        try {
    	            Files.move(oldFilePath, newFilePath);
    	        } catch (IOException e) {
    	            throw new RuntimeException("Failed to rename file", e);
    	        }
    	        document.setName(titre);
    	        document.setPath(root +"/" + titre);
    	    }

    	    // mise à jour des tags et des départements
    	    Set<Tag> tagsToUpdate = new HashSet<>();
    	    if (tags != null) {
    	        for (String tag : tags) {
    	            Tag newTag = new Tag(tag);
    	            Tag tagInDB = tagRepository.findByLibelle(tag);
    	            if (tagInDB != null) {
    	                tagsToUpdate.add(tagInDB);
    	            } else {
    	                tagsToUpdate.add(newTag);
    	                tagRepository.save(newTag);
    	            }
    	        }
    	    }

    	    Set<Department> depsToUpdate = new HashSet<>();
    	    if (deps != null) {
    	        for (String dep : deps) {
    	            Department depInDB = departmentRepository.findByName(dep);
    	            if (depInDB != null) {
    	                depsToUpdate.add(depInDB);
    	                
    	            }
    	        }
    	        document.setDepartements(depsToUpdate);
    	        
    	    }else {
    	    	document.setDepartements(document.getDepartments());
    	    }

    	    document.setTags(tagsToUpdate);
    	    
    	    documentRepository.save(document);

    	    return ResponseEntity.ok(document);
    	}
    
}


