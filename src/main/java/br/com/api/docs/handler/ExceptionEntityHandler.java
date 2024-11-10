package br.com.api.docs.handler;

import br.com.api.docs.exceptions.DocumentDownloadException;
import br.com.api.docs.exceptions.DocumentsUploadException;
import br.com.api.docs.exceptions.InputException;
import br.com.api.docs.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionEntityHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DocumentsUploadException.class)
    public ResponseEntity<ErrorMessage> handleDocumentUploadException(DocumentsUploadException e) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .status(400)
                .message(e.getMessage())
                .build();

        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(InputException.class)
    public ResponseEntity<ErrorMessage> handleInputException(InputException e) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .status(400)
                .message(e.getMessage())
                .build();

        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFoundException(NotFoundException e) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .status(404)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(DocumentDownloadException.class)
    public ResponseEntity<ErrorMessage> handleDocumentDownloadException(DocumentDownloadException e) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .status(400)
                .message(e.getMessage())
                .build();

        return ResponseEntity.badRequest().body(errorMessage);
    }
}
