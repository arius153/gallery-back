package lt.insoft.gallery.application.exceptions;

public class AuthenticationFailException extends RuntimeException{
    public AuthenticationFailException(String message) {
        super(message);
    }

}
