package frgp.utn.edu.ar.quepasa.config.interceptors;

import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.exception.ValidationError;
import frgp.utn.edu.ar.quepasa.service.media.StorageFileNotFoundException;
import jakarta.mail.MessagingException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice(basePackages = "frgp.utn.edu.ar.quepasa.controller")
public class CentralizedErrorHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Fail> handleIllegalArgumentException(IllegalArgumentException ex) {
        Fail fail = new Fail("Valor de entrada inválido. ", HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(fail.getStatus()).body(fail);
    }

    @ExceptionHandler(Fail.class)
    public ResponseEntity<Fail> handleFails(Fail ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex);
    }

    @ExceptionHandler(ValidationError.class)
    public ResponseEntity<ValidationError> handleValidationError(ValidationError ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        Fail fail = new Fail("No se puede leer el cuerpo de la solicitud. ", HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(fail.getStatus()).body(fail);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        Fail fail = new Fail("Recurso no encontrado. ", HttpStatus.NOT_FOUND);
        return ResponseEntity.status(fail.getStatus()).body(fail);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Fail> handleNoSuchElement(NoSuchElementException ex) {
        Fail fail = new Fail("Recurso no encontrado. ", HttpStatus.NOT_FOUND);
        return ResponseEntity.status(fail.getStatus()).body(fail);
    }


    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<Fail> handleAuthError(AuthenticationCredentialsNotFoundException e) {
        Fail fail = new Fail("Credenciales incorrectas. ", HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(fail.getStatus()).body(fail);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<Fail> handleMessagingError(MessagingException e) {
        Fail fail = new Fail("No se pudo enviar un correo electrónico. ", HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(fail.getStatus()).body(fail);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<Fail> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        Fail fail = new Fail("Imagen o documento no encontrado. ", HttpStatus.NOT_FOUND);
        return ResponseEntity.status(fail.getStatus()).body(fail);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Fail> handleBadCredentialsError(AuthenticationCredentialsNotFoundException e) {
        Fail fail = new Fail("Credenciales incorrectas. ", HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(fail.getStatus()).body(fail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Fail> handleGeneralException(Exception e) {
        Fail fail = new Fail("Ocurrió un error inesperado.", HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(fail.getStatus()).body(fail);
    }

}
