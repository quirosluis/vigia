package io.github.quirosluis.vigia.backend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Comparator;
import java.util.List;

@RestControllerAdvice

public class ManejadorErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);

    public record ErrorCampo(String campo, String mensaje) {}
    public record RespuestaErrores(List<ErrorCampo> errores) {}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaErrores validacion(MethodArgumentNotValidException ex) {
        List<ErrorCampo> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ErrorCampo(e.getField(), e.getDefaultMessage()))
                .sorted(Comparator.comparing(ErrorCampo::campo))
                .toList();
        log.warn("Lote rechazado: {} errores, primer campo: {}",
                errores.size(), errores.isEmpty() ? "-" : errores.get(0).campo());
        return new RespuestaErrores(errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaErrores ilegible(HttpMessageNotReadableException ex) {
        log.warn("Lote rechazado: cuerpo ausente, ilegible o con tipos incorrectos");
        return new RespuestaErrores(List.of(
                new ErrorCampo("cuerpo", "cuerpo ausente, ilegible o con tipos incorrectos")));
    }
}
