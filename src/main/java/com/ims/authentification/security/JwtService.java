package com.ims.authentification.security;

import com.ims.authentification.entity.Jwt;
import com.ims.authentification.entity.Utilisateur;
import com.ims.authentification.repository.JwtRepository;
import com.ims.authentification.service.UtilisateurService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.security.Key;

import java.util.*;

import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Service
public class JwtService {


    public static final String BEARER = "bearer";
    private String ENCRIPTION_KEY= "b9430c18c89a1160503104d36c5e76b5062d4fd627c48ee93ed0ad4caa3ba549";

    private UtilisateurService utilisateurService;
    private JwtRepository jwtRepository;

    public JwtService(UtilisateurService utilisateurService, JwtRepository jwtRepository ) {
        this.utilisateurService = utilisateurService;
        this.jwtRepository = jwtRepository;
    }

    public void disableTokens(Utilisateur utilisateur) {
        final List<Jwt> jwtList=this.jwtRepository.findUtilisateur(utilisateur.getEmail()).peek(
               jwt -> {
                   jwt.setDesactive(true);
                   jwt.setExpire(true);
               }
        ).collect(Collectors.toList());

        this.jwtRepository.saveAll(jwtList);
    }

    public String extractUsername(String token) {
        return this.getClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate= this.getClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }
    public Jwt tokenByValue(String token) {
       return this.jwtRepository.findByValeur(token)
                .orElseThrow(()-> new RuntimeException("token inconnu"));
    }

    private <T> T getClaim(String token, Function<Claims, T> function) {
        Claims claims = getAllClaims(token);
        return function.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(this.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Map<String, String> generate(String username) {
        Utilisateur utilisateur = (Utilisateur) this.utilisateurService.loadUserByUsername(username);
        this.disableTokens(utilisateur);
        Map<String, String> jwtMap = this.generateJwt(utilisateur);
        Jwt jwt = Jwt
                .builder()
                .valeur(jwtMap.get(BEARER))
                .desactive(false)
                .expire(false)
                .utilisateur(utilisateur)
                .build();
        this.jwtRepository.save(jwt);
        return jwtMap;
    }

    private Map<String, String> generateJwt(Utilisateur utilisateur) {

        final long currentTime = System.currentTimeMillis();
        final long expirationTime= currentTime + 30 * 60 * 1000;

        final Map<String, Object> claims = Map.of(
                "nom", utilisateur.getNom(),
                Claims.EXPIRATION,new Date(expirationTime),
                Claims.SUBJECT,utilisateur.getEmail()
        );

        final String bearer = Jwts.builder()
                .issuedAt(new Date(currentTime))
                .expiration(new Date(expirationTime))
                .setSubject(utilisateur.getEmail())
                .signWith(getKey(), SignatureAlgorithm.HS384)
                .claims(claims)
                .compact();
        return Map.of("bearer",bearer);
    }

    private Key getKey() {
     final  byte[] decoder = Decoders.BASE64.decode(ENCRIPTION_KEY);
     return Keys.hmacShaKeyFor(decoder);
    }


    public void deconnexion() {
       Utilisateur utilisateur= (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Jwt jwt = this.jwtRepository.findByUtilisateurValidToken(false,
              false,
              utilisateur.getEmail())
              .orElseThrow(()-> new RuntimeException("Token Invalide")
              );
      jwt.setDesactive(true);
      jwt.setExpire(true);
      this.jwtRepository.save(jwt);
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void removeUselessJwt(){
        this.jwtRepository.deleteByExpireAndDesactive(true,true);

    }
}
