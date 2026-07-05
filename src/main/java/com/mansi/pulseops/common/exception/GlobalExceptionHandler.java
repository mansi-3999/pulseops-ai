package com.mansi.pulseops.common.exception;
import com.mansi.pulseops.common.api.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.OffsetDateTime;
import java.util.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(IncidentNotFoundException.class)
 ResponseEntity<ApiError> notFound(IncidentNotFoundException ex,HttpServletRequest req){return build(HttpStatus.NOT_FOUND,ex.getMessage(),req.getRequestURI(),Map.of());}
 @ExceptionHandler(MethodArgumentNotValidException.class)
 ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex,HttpServletRequest req){
  Map<String,String> errors=new LinkedHashMap<>();ex.getBindingResult().getFieldErrors().forEach(e->errors.put(e.getField(),e.getDefaultMessage()));
  return build(HttpStatus.BAD_REQUEST,"Request validation failed",req.getRequestURI(),errors);
 }
 @ExceptionHandler(Exception.class)
 ResponseEntity<ApiError> unexpected(Exception ex,HttpServletRequest req){return build(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected server error",req.getRequestURI(),Map.of());}
 private ResponseEntity<ApiError> build(HttpStatus s,String m,String p,Map<String,String> v){return ResponseEntity.status(s).body(new ApiError(OffsetDateTime.now(),s.value(),s.getReasonPhrase(),m,p,v));}
}
