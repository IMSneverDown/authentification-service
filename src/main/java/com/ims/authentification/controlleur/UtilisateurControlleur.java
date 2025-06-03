package com.ims.authentification.controlleur;

import com.ims.authentification.dto.AuthentificationDTO;
import com.ims.authentification.entity.Utilisateur;
import com.ims.authentification.security.JwtService;
import com.ims.authentification.service.UtilisateurService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@Slf4j
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class UtilisateurControlleur {

    private UtilisateurService utilisateurService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @Autowired
   public UtilisateurControlleur(UtilisateurService utilisateurService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.utilisateurService = utilisateurService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    @PostMapping(path = "inscription")
    public void inscription(@RequestBody Utilisateur utilisateur) {
        this.utilisateurService.incription(utilisateur); ;

    }
    @PostMapping(path = "activation")
    public void inscription(@RequestBody Map<String, String> activation) {
        this.utilisateurService.activation(activation);

    }

    @PostMapping(path="connexion")
    public Map<String, String> connexion(@RequestBody AuthentificationDTO authentificationDTO) {
   final Authentication authentication= authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authentificationDTO.username(),
                        authentificationDTO.password())
        );

        if(authentication.isAuthenticated()) {
            return this.jwtService.generate(authentificationDTO.username());
        }

        return null;

    }
    @PostMapping(path = "deconnexion")
    public void deconnexion() {
        this.jwtService.deconnexion();
    }

}
