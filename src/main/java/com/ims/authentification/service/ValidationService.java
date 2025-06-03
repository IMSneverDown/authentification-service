package com.ims.authentification.service;

import com.ims.authentification.entity.Utilisateur;
import com.ims.authentification.entity.Validation;
import com.ims.authentification.repository.ValidationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Random;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class ValidationService {

    private ValidationRepository validationRepository;
    private NoticationService noticationService;


    @Autowired
    public ValidationService(ValidationRepository validationRepository, NoticationService noticationService) {
        this.validationRepository = validationRepository;
        this.noticationService = noticationService;
    }

    public void enregistrer(Utilisateur utilisateur) {
        Validation validation = new Validation();
        validation.setUtilisateur(utilisateur);
        Instant creation = Instant.now();
        validation.setCreation(creation);
        Instant expiration = creation.plus(10,MINUTES);
        validation.setExpiration(expiration);

        Random random = new Random();
        int randomInteger =random.nextInt(99999);
        String code = String.format("%06d",randomInteger);
        validation.setCode(code);
        this.validationRepository.save(validation);
        this.noticationService.envoyer(validation);


    }

    public  Validation lireEnFonctionDuCode(String code) {
       return this.validationRepository.findByCode(code).orElseThrow(()-> new RuntimeException("Le code n'existe valide "));

    }

}
