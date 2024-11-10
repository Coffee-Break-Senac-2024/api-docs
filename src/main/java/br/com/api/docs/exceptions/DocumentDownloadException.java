package br.com.api.docs.exceptions;

public class DocumentDownloadException extends RuntimeException {
    public DocumentDownloadException(String message) {
        super(message);
    }
}
