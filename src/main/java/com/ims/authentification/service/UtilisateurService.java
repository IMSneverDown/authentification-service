package com.ims.authentification.service;

import com.ims.authentification.entity.Utilisateur;
import com.ims.authentification.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtilsateurService {

    private UtilisateurRepository utilisateurRepository;

    @Autowired
    public UtilsateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public void incription(Utilisateur utilisateur) {
        this.utilisateurRepository.save(utilisateur);
    }
}
