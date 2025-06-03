package com.ims.authentification.repository;

import com.ims.authentification.entity.Jwt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface JwtRepository extends CrudRepository<Jwt, Integer> {

  Optional <Jwt> findByValeur(String valeur);
  Optional<Jwt> findByValeurAndDesactiveAndExpire(String valeur, boolean desactive, boolean expire);

  @Query("FROM Jwt j WHERE j.expire =:expire AND j.desactive =:desactive AND j.utilisateur.email =:email")
    Optional<Jwt> findByUtilisateurValidToken(boolean expire, boolean desactive, String email);

  @Query("FROM Jwt j WHERE j.utilisateur.email =:email")
  Stream<Jwt> findUtilisateur(String email);

  void deleteByExpireAndDesactive(boolean expire, boolean desactive);
}
