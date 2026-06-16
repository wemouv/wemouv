package com.diginamic.wemouv.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /** * Logger dédié à la journalisation sécurisée des erreurs interceptées.
     */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Intercepte les erreurs de lecture des requêtes HTTP, typiquement dues à
     * un format JSON mal formé ou à des données incompatibles envoyées par le client.
     * <p>
     * L'erreur détaillée est enregistrée dans les logs du serveur, et un message
     * générique sécurisé est retourné au client pour éviter toute fuite d'informations.
     * </p>
     *
     * @param e l'exception de désérialisation levée par Spring
     * @return un {@link ResponseEntity} avec un message explicatif générique et un statut HTTP 400 (Bad Request)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleJsonError(
            HttpMessageNotReadableException e
    ) {
        logger.error("Erreur de lecture de la requête (JSON mal formé ou données incompatibles)", e);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("La requête est mal formée. Veuillez vérifier la syntaxe de votre JSON et le type des données envoyées.");
    }

    /**
     * Intercepte toutes les autres exceptions non gérées spécifiquement par l'application.
     * <p>
     * Agit comme un filet de sécurité global pour traiter les crashs inattendus.
     * L'erreur critique est tracée dans les logs serveurs, et les détails techniques
     * sont totalement masqués à l'utilisateur final.
     * </p>
     *
     * @param e l'exception générique interceptée
     * @return un {@link ResponseEntity} contenant un message d'erreur générique avec un statut HTTP 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(
            Exception e
    ) {
        logger.error("Une exception non gérée a été interceptée par le gestionnaire global", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Une erreur interne inattendue est survenue sur le serveur.");
    }
}