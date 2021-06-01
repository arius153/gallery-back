package lt.insoft.gallery.application;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.tomcat.jni.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> resourceNotFound(ResourceNotFoundException ex)
    {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("NOT_FOUND");
        response.setErrorMessage(ex.getMessage());
        response.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ParameterFormatException.class)
    public ResponseEntity<ExceptionResponse> parameterIsWrong(ParameterFormatException ex)
    {
        // @formatter:off
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorMessage(ex.getMessage());
        response.setErrorCode("Bad_REQUEST");
        response.setTimestamp(LocalDateTime.now());
                // .builder()
                // .errorCode("BAD_REQUEST")
                // .errorMessage(ex.getMessage())
                // .timestamp(LocalDateTime.now())
                // .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        // @formatter:on
    }
}
