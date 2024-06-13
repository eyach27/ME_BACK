package com.example.backneodoc.Controllers;

import com.example.backneodoc.Exceptions.ResourceNotFoundException;
import com.example.backneodoc.models.Department;
import com.example.backneodoc.models.Document;
import com.example.backneodoc.models.Tag;
import com.example.backneodoc.payload.response.MessageResponse;
import com.example.backneodoc.repository.DocumentRepository;
import com.example.backneodoc.repository.TagRepository;

import com.example.backneodoc.services.DocumentServices;
import com.example.backneodoc.services.VideoStreamService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;



//@CrossOrigin(origins = "http://10.53.1.149:85")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("document")
public class FileUploadController {

    @Autowired
    public DocumentServices documentServices;

    @Autowired
    public DocumentRepository documentRepository;

    @Autowired
    public TagRepository tagRepository;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    VideoStreamService videoStreamService;

    @PostMapping(path = "/upload")
    public ResponseEntity<?> uploadMultipleFiles(@RequestParam("files")  MultipartFile[] uploadedFiles, @RequestParam("tags") Set<String> tags, @RequestParam("dep")  Set<String> dep, @RequestParam("name")  String name) {
        documentServices.saveMultiple(uploadedFiles,tags, dep, name);
        return ResponseEntity.ok(new MessageResponse("success"));
    }

        @GetMapping("/list")
    public ResponseEntity<List<Document>> getListFiles() {
        return ResponseEntity.status(HttpStatus.OK).body(documentServices.getAllFiles());
    }

    @DeleteMapping("/delete/{id}")
    public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long userId) throws ResourceNotFoundException {
        return documentServices.deleteDoc(userId);
    } 

    @GetMapping("/name/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws MalformedURLException {
        Path path = Paths.get("uploads/" + filename);
       // Path path = Paths.get("usr/share/tomcat/webapps/uploads/" + filename);
        Resource resource = new UrlResource(path.toAbsolutePath().toUri());
        System.out.println(resource);
        MediaType mediaType = MediaType.ALL;
        try {
            mediaType = MediaType.parseMediaType(Files.probeContentType(path));
        } catch (IOException e) {
        	
            // Handle the exception
        }

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + resource.getFilename() + "\"")
                    .contentType(mediaType)
                    .body(resource);
        } else {
            throw new RuntimeException("Could not read the document!");
        }
    }

    @GetMapping("/files/stream/{fileName}")
    public ResponseEntity<byte[]> streamVideo(@RequestHeader(value = "Range", required = false) String httpRangeList,
                                              @PathVariable("fileName") String fileName) {
        return videoStreamService.prepareContent(fileName, httpRangeList);
    }


    @GetMapping("/tags")
    public ResponseEntity<List<Tag>> getTags(){
        return new ResponseEntity<>(tagRepository.findAll(),HttpStatus.OK);
    }

    @GetMapping("/download/{id:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {
        // Load file as Resource
        Document document = documentServices.getDocById(id);
        Path path = Paths.get(document.getPath());
        System.out.println(path);
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .contentLength(document.getSize())
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"")
                .body(resource);
    }

    @GetMapping("/recherche/type/{type}")
    public List<Document> getDocumentByType( @PathVariable(value="type") String type) {
        return documentServices.getSearchDocType(type);
    }
    @GetMapping("/recherche/titre/{titre}")
    public List<Document> getDocumentByTitre( @PathVariable(value="titre") String titre) {
        return documentServices.getSearchDocTitre(titre);
    }
    @GetMapping("/recherche/tag/{tag}")
    public List<Document> getDocumentByTag(@PathVariable(value="tag") String tag) {
        return documentServices.getSearchDocTag(tag);
    }
    @GetMapping("/recherche/dep/{dep}")
    public List<Document> getDocumentByDep(@PathVariable(value="dep") String dep) {
        return documentServices.getSearchDocDep(dep);
    }
    @GetMapping("/getAll/inDep/{dep}")
    public List<Document> getAllByDep(@PathVariable(value="dep") String dep) {
        return documentServices.getbyDep(dep);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Document> updateDoc(@PathVariable(value = "id") Long docId,
                                              @RequestParam(value="titre", required = false) String titre,@RequestParam(value="dep", required = false) Set<String> dep,
                                              @RequestParam(value="tags", required = false) Set<String> tags) throws ResourceNotFoundException {
        return documentServices.updateDoc(docId,titre,dep,tags);}


    @GetMapping("/{id}")
    public Document getFilebyid(@PathVariable Long id) throws IOException {
        return documentServices.getDocById(id);
    }
    
    @GetMapping("/{docId}/departments")
    public List<Department> getDepartmentsByDocId(@PathVariable Long docId) {
        return documentServices.getDepartmentsByDocumentId(docId);
    }
}
    


