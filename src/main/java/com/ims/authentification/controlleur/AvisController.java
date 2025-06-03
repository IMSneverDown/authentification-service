package com.ims.authentification.controlleur;

import com.ims.authentification.entity.Avis;
import com.ims.authentification.service.AvisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("avis")
public class AvisController {


    private final AvisService avisService;

    @Autowired
    public AvisController(AvisService avisService) {
        this.avisService = avisService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void saveAvis(@RequestBody Avis avis) {
        this.avisService.creer(avis);
    }


}
