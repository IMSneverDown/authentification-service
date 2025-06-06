package com.ims.authentification.service;

import com.ims.authentification.entity.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NoticationService {

    JavaMailSender javaMailSender;

     @Autowired
    public NoticationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void envoyer(Validation validation){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@ims.com");
        message.setTo(validation.getUtilisateur().getEmail());
        message.setSubject("Votre code d'activation");

        String texte= String.format("Bonjour %s,<br/> Votre code d'activation est %s, Merci",
                validation.getUtilisateur().getNom(),
                validation.getCode()
                );
        message.setText(texte);
        javaMailSender.send(message);
    }
}
