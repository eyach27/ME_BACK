package com.example.backneodoc.Controllers;

import com.example.backneodoc.Exceptions.ResourceNotFoundException;
import com.example.backneodoc.models.Document;
import com.example.backneodoc.models.ERole;
import com.example.backneodoc.models.Role;
import com.example.backneodoc.models.Tag;
import com.example.backneodoc.models.User;
import com.example.backneodoc.payload.request.SignupRequest;
import com.example.backneodoc.payload.response.MessageResponse;
import com.example.backneodoc.repository.DocumentRepository;
import com.example.backneodoc.repository.RoleRepository;
import com.example.backneodoc.repository.UserRepository;
import com.example.backneodoc.services.DocumentServices;
//import com.example.backneodoc.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.Valid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.springframework.http.HttpStatus;

@CrossOrigin(origins = "http://10.53.1.149:85")
@RestController
@RequestMapping("/api/gestion")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    DocumentServices documentServices;
    
    //@Autowired
   // private UserService userService;
    

    @GetMapping("/users/enattente")
    public List<User> getAllEnAttente() {
        return userRepository.findAllByEnabled(false,Sort.by(Sort.Direction.ASC, "poste","username"));
    }

    @GetMapping("/users")
    public List<User> GetAllByEnabled() {
        return userRepository.findAllByEnabled(true,Sort.by(Sort.Direction.ASC, "poste","username"));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long userId)
            throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé pour l'id : " + userId));
        return ResponseEntity.ok().body(user);
    }

   @PostMapping("/users")
   public ResponseEntity<?> createUser(@Valid @RequestBody SignupRequest signUpRequest) {
	   Set<String> strRoles = signUpRequest.getRole();
	 
	   
      if (userRepository.existsByUsername(signUpRequest.getUsername())) {
           return ResponseEntity
                   .badRequest()
                   .body(new MessageResponse(" Création de nouveau compte échouée : Nom utilisateur existe déja!"));}

       if (userRepository.existsByEmail(signUpRequest.getEmail())) {
           return ResponseEntity
                   .badRequest()
                   .body(new MessageResponse(" Email existe déja!")); }

       // Create new user's account
       User user = new User(
               signUpRequest.getFirstname(),
               signUpRequest.getLastname(),
               signUpRequest.getUsername(),
               signUpRequest.getEmail(),
               encoder.encode(signUpRequest.getPassword()),
               signUpRequest.getPoste() );

    
       Set<Role> roles = new HashSet<>();

       if (strRoles == null) {
           Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                   .orElseThrow(() -> new RuntimeException("Erreur: Role n'existe pas."));
           roles.add(adminRole);
       } else {
           strRoles.forEach(role -> {
               switch (role) {
                   case "formateur":
                       Role formateurRole = roleRepository.findByName(ERole.ROLE_FORMATEUR)
                               .orElseThrow(() -> new RuntimeException("Erreur: Role n'existe pas."));
                       roles.add(formateurRole);
                       break;
                   case "user":
                       Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                               .orElseThrow(() -> new RuntimeException("Erreur: Role n'existe pas."));
                       roles.add(userRole);
                       break;

                   default:
                       Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                               .orElseThrow(() -> new RuntimeException("Erreur: Role n'existe pas."));
                       roles.add(adminRole);
               }
           });
       }

       user.setRoles(roles);
       user.setEnabled(true);
       userRepository.save(user);

       return ResponseEntity.ok(new MessageResponse("utilisateur enregistré avec succée!"));
   }
   
   
   @PostMapping("users/admin")
   public ResponseEntity<?> createUserAdmin(@Valid @RequestBody SignupRequest signUpRequest) {
	   Set<String> strRoles = signUpRequest.getRole();
	  
      if (userRepository.existsByUsername(signUpRequest.getUsername())) {
           return ResponseEntity
                   .badRequest()
                   .body(new MessageResponse(" Création de nouveau compte échouée : Nom utilisateur existe déja!"));}
       if (userRepository.existsByEmail(signUpRequest.getEmail())) {
           return ResponseEntity
                   .badRequest()
                   .body(new MessageResponse(" Email existe déja!")); }
       // Create new user's account
       User user = new User(
               signUpRequest.getFirstname(),
               signUpRequest.getLastname(),
               signUpRequest.getUsername(),
               signUpRequest.getEmail(),
               encoder.encode(signUpRequest.getPassword()),
               signUpRequest.getPoste() );
 	
       Set<Role> roles = new HashSet<>();

           strRoles.forEach(role -> {
               switch (role) {
               case "formateur":
                   Role formateurRole = roleRepository.findByName(ERole.ROLE_FORMATEUR)
                           .orElseThrow(() -> new RuntimeException("Erreur: Role n'existe pas."));
                   roles.add(formateurRole);
                   break;
                   case "user":
                       Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                               .orElseThrow(() -> new RuntimeException("Erreur: Role n'existe pas."));
                       roles.add(userRole);
                       break;

                   default:
                	   System.out.println("role admin");
                       Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                               .orElseThrow(() -> new RuntimeException("Erreur: Role n'existe pas."));
                       roles.add(adminRole);
               }
           });
       

       user.setRoles(roles);
       user.setEnabled(true);
       userRepository.save(user);

       return ResponseEntity.ok(new MessageResponse("utilisateur enregistré avec succée!"));
   }



    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id") Long userId,
                                           @RequestBody SignupRequest signupRequest
                                           ) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé pour cet id: " + userId));

        user.setEmail(signupRequest.getEmail());
        user.setLastname(signupRequest.getLastname());
        user.setFirstname(signupRequest.getFirstname());
        user.setUsername(signupRequest.getUsername());
        user.setEnabled(true);
        user.setPoste(signupRequest.getPoste());

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {

            for(Role r:user.getRoles()){
            roles.add(r);
            }
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                case "formateur":
                    Role formateurRole = roleRepository.findByName(ERole.ROLE_FORMATEUR)
                            .orElseThrow(() -> new RuntimeException("Erreur: Role n'existe pas."));
                    roles.add(formateurRole);
                    break;
                    case "user":
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Erreur: Role n'existe pas."));
                        roles.add(userRole);
                        break;

                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Erreur: Role n'existe pas."));
                        roles.add(adminRole);
                }
            });}
        user.setRoles(roles);



        final User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }
    
    @PutMapping("/users/{id}/PDP")
    public ResponseEntity<User> updatePhoto(@PathVariable(value = "id") Long userId,@RequestParam("photo")MultipartFile photo ) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé pour cet id: " + userId));
       
        String name=savePhoto(photo);
       // user.setPhoto("/usr/share/tomcat/webapps/back-0.0.2/uploads/PDP/" + name);
        user.setPhoto("/uploads/PDP/" + name);
        final User updatedPhoto = userRepository.save(user);

        return ResponseEntity.ok(updatedPhoto);
    }
    
    private final Path PhotoDeProfile = Paths.get("/usr/share/tomcat/webapps/back-0.0.2/uploads/PDP");
    public String savePhoto(MultipartFile photo) {
        try {
        	String filename =  UUID.randomUUID().toString() + "_" + photo.getOriginalFilename();
        	 System.out.println(filename);
            Files.copy(photo.getInputStream(), this.PhotoDeProfile.resolve(filename));
            return filename;
        } catch (Exception e) {
            throw new RuntimeException("Could not store the photo. Error: " + e.getMessage());
        }
    }
 
    @GetMapping("/PDP/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws MalformedURLException {
        Path path = Paths.get("uploads/PDP/" + filename);
        //System.out.println(filename);
        Resource resource = new UrlResource(path.toUri());
               MediaType mediaType = MediaType.ALL;
        try {
            mediaType = MediaType.parseMediaType(Files.probeContentType(path));
        } catch (IOException e) {
        	
            // Handle the exception
        }

        if (resource.exists() && resource.isReadable()) {
        	//System.out.println(resource);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + resource.getFilename() + "\"")
                    .contentType(mediaType)
                    .body(resource);
        } else {
            throw new RuntimeException("Could not read the photo!");
        }
    }
    
  
    public Map<String, Boolean> delete(Long userId) throws ResourceNotFoundException {
    	User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user non trouvé pour cet id:: " + userId));
   
        Path path= Paths.get(user.getPhoto());
        System.out.println("path delete:  "+path);
        try {
        	  System.out.println("try ");
            // Delete file or directory
            Files.deleteIfExists(path);
            user.setPhoto(null);
         	userRepository.save(user);
            System.out.println("File or directory deleted successfully");
        } catch (NoSuchFileException ex) {
            System.out.printf("No such file or directory: %s\n", path);
        } catch (DirectoryNotEmptyException ex) {
            System.out.printf("Directory %s is not empty\n", path);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    //    documentRepository.delete(document);
        Map<String, Boolean> response = new HashMap<>();
        response.put("supprimé", Boolean.TRUE);
        return response;
    }
    
    
    
    @GetMapping("/photo/delete/{id}")
    public Map<String, Boolean> deletePhoto(@PathVariable(value = "id") Long userId) throws ResourceNotFoundException {
        return delete(userId);
    } 
    
    

    @DeleteMapping("/users/{id}")
    public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long userId)
            throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé pour cet id:: " + userId));

        userRepository.delete(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("supprimé", Boolean.TRUE);
        return response;
    }
    
    @PutMapping("/users/desactive/{id}")
    public Map<String, Boolean> desactivateUser(@PathVariable(value = "id") Long userId)
            throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé pour cet id: " + userId));

        user.setStatut(false);
        userRepository.save(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("desactivé", Boolean.FALSE);
        return response;
    }
    @PutMapping("/users/active/{id}")
    public Map<String, Boolean> activateUser(@PathVariable(value = "id") Long userId)
            throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé pour cet id: " + userId));

        user.setStatut(true);
        userRepository.save(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("activé", Boolean.TRUE);
        return response;
    }
    
    @PutMapping("/users/accept/{id}")
    public Map<String, Boolean> acceptUser(@PathVariable(value = "id") Long userId)
            throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé pour cet id: " + userId));

        user.setEnabled(true);
        userRepository.save(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("accepté", Boolean.TRUE);
        return response;
    }

    @PutMapping("/users/accept/all")
    public Map<String, Boolean> acceptAllUsers() {
        List<User> users = getAllEnAttente();
        for(User user:users){
            user.setEnabled(true);
            userRepository.save(user);}
        Map<String, Boolean> response = new HashMap<>();
        response.put("accepté", Boolean.TRUE);
        return response;
    }
    @GetMapping("/users/formateur")
    public List<User> getAllFormateur() {
    	
     return userRepository.findAllfromateur();
    }


}
