package com.ims.authentification.service;

import com.ims.authentification.entity.Role;
import com.ims.authentification.entity.Utilisateur;
import com.ims.authentification.entity.Validation;
import com.ims.authentification.enums.TypeDeRole;
import com.ims.authentification.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
public class UtilisateurService implements UserDetailsService {


    private  UtilisateurRepository utilisateurRepository;
    private  BCryptPasswordEncoder passwordEncoder;
    private  ValidationService validationService;


    @Autowired
    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                              BCryptPasswordEncoder passwordEncoder,
                              ValidationService validationService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;

    }

    public UtilisateurService() {}

    public void incription(Utilisateur utilisateur) {

        if(!utilisateur.getEmail().contains("@")) {
            throw new RuntimeException("votre email est invalide");
        }
        if(!utilisateur.getEmail().contains(".")) {
            throw new RuntimeException("votre email est invalide");
        }
        Optional<Utilisateur> utilisateurOptional = this.utilisateurRepository.findByEmail(utilisateur.getEmail());
        if(utilisateurOptional.isPresent()) {
            throw new RuntimeException("votre email est déjà utilisé");
        }

        Role roleUtilisateur = new Role();
        roleUtilisateur.setLibelle(TypeDeRole.UTILISATEUR);
        utilisateur.setRole(roleUtilisateur);

        String mdpCrypte = this.passwordEncoder.encode(utilisateur.getMdp());
        utilisateur.setMdp(mdpCrypte);
        utilisateur=this.utilisateurRepository.save(utilisateur);
        this.validationService.enregistrer(utilisateur);
    }

    public void activation(Map<String, String> activation) {
        Validation validation= this.validationService.lireEnFonctionDuCode(activation.get("code"));
        if (Instant.now().isAfter(validation.getExpiration())){
            throw new RuntimeException("votre code est expiré");

        }
       Utilisateur utilisateurActiver= this.utilisateurRepository.findById(validation.getUtilisateur().getId()).orElseThrow(() -> new RuntimeException("utilisateur inconnu"));
       utilisateurActiver.setActif(true);
       this.utilisateurRepository.save(utilisateurActiver);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.utilisateurRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur trouvé"));
    }
}
