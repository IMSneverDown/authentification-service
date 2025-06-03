package com.ims.authentification.service;

import com.ims.authentification.entity.Avis;
import com.ims.authentification.entity.Utilisateur;
import com.ims.authentification.repository.AvisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class AvisService {

    private AvisRepository avisRepository;
    @Autowired
    public AvisService(AvisRepository avisRepository) {
        this.avisRepository = avisRepository;
    }

    public void creer(Avis avis) {
       Utilisateur utilisateur= (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       avis.setUtilisateur(utilisateur);
        this.avisRepository.save(avis);
    }

}
