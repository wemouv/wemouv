package com.diginamic.wemouv.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestionnaire global des exceptions pour l'ensemble des contrôleurs REST de l'application.
 * <p>
 * Cette classe intercepte les exceptions levées lors du traitement des requêtes HTTP
 * et renvoie des réponses formatées et standardisées au client,
 * évitant ainsi les plantages silencieux de l'application.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Intercepte les erreurs de lecture des requêtes HTTP, typiquement dues à
     * un format JSON mal formé ou à des données incompatibles envoyées par le client.
     *
     * @param e l'exception de désérialisation levée par Spring
     * @return un {@link ResponseEntity} contenant la cause spécifique de l'erreur avec un statut HTTP 400 (Bad Request)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleJsonError(
            HttpMessageNotReadableException e
    ) {

        e.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMostSpecificCause().getMessage());
    }

    /**
     * Intercepte toutes les autres exceptions non gérées spécifiquement par l'application.
     * <p>Agit comme un filet de sécurité global pour traiter les crashs inattendus
     * et empêcher le serveur d'exposer des traces techniques complètes à l'utilisateur final.</p>
     *
     * @param e l'exception générique interceptée
     * @return un {@link ResponseEntity} contenant le message de l'exception avec un statut HTTP 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(
            Exception e
    ) {

        e.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }
}