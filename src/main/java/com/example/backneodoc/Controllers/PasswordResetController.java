package com.example.backneodoc.Controllers;
import com.example.backneodoc.email.context.AbstractEmailContext;
import com.example.backneodoc.email.service.EmailService;
import com.example.backneodoc.models.User;
import com.example.backneodoc.repository.UserRepository;
import com.example.backneodoc.security.services.MDPUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://10.53.1.149:85")
@RestController
@RequestMapping("/api/auth")
public class PasswordResetController implements EmailService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JavaMailSender emailSender;

    @Autowired
     private MDPUserService MDPUserService;

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody User user) {
    	System.out.println("heyyy forget pwd");
        String response = MDPUserService.forgotPassword(user.getEmail());
        System.out.println(response);
        User existingUser= userRepository.findByEmail(user.getEmail());
        System.out.println(existingUser);
        System.out.println(!response.startsWith("email"));
        if (!response.startsWith("email")) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(existingUser.getEmail());
            mailMessage.setSubject("Compléter la réinitialisation de votre mot de passe!");
            mailMessage.setFrom("issra.khemir@etudiant-isi.utm.tn");
            mailMessage.setText("Pour compléter le processus de la réinitialisation de votre mot de passe, cliquez ici svp: \n "
                   // + "http://localhost:9090/api/auth/reset-password?token="+response);
                    +"http://localhost:4200/resetpassword?token="+response);
            // Send the email
            emailSender.send(mailMessage);

            response = "{\"url\": \"http://localhost:4200/resetpassword?token=" + response + "\"}";}
        return response;
    }

    @PutMapping("/reset-password")
    public String resetPasswordd(@RequestBody User user) {
        String p = encoder.encode(user.getPassword());
        return MDPUserService.resetPassword(user.getToken(),p);}

    @Override
    public void sendMail(AbstractEmailContext email) {}
}