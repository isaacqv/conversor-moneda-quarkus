package com.example.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;

/**
 * Clase utilitaria con métodos helper
 * (Equivalente al Utilitario.java de Spring Boot)
 */
public class Util {

    /**
     * Multiplica dos valores BigDecimal
     * @param valorA Primer valor
     * @param valorB Segundo valor
     * @return Resultado de la multiplicación o null si alguno es null
     */
    public static BigDecimal multiplicar(BigDecimal valorA, BigDecimal valorB) {
        return (valorA != null && valorB != null) ? valorA.multiply(valorB) : null;
    }

    /**
     * Redondea un BigDecimal a un número específico de decimales
     * @param valor Valor a redondear
     * @param numDecimales Número de decimales
     * @return Valor redondeado o el mismo valor si es null
     */
    public static BigDecimal redondarDecimales(BigDecimal valor, int numDecimales) {
        return (valor == null) ? null : valor.setScale(numDecimales, RoundingMode.HALF_UP);
    }

    /**
     * Normaliza una cadena: elimina acentos, trim y convierte a mayúsculas
     * Útil para comparación de nombres de monedas
     *
     * Ejemplos:
     * - "dólar" -> "DOLAR"
     * - "  Euro  " -> "EURO"
     * - "Libra Esterlina" -> "LIBRA ESTERLINA"
     *
     * @param cadena Cadena a normalizar
     * @return Cadena normalizada en mayúsculas sin acentos
     */
    public static String normalizarCadena(String cadena) {
        if (cadena == null) {
            return null;
        }

        // Trim para eliminar espacios al inicio y final
        cadena = cadena.trim();

        // Normaliza la cadena para separar los diacríticos (acentos)
        String normalized = Normalizer.normalize(cadena, Normalizer.Form.NFD);

        // Remueve los diacríticos usando expresión regular
        // \\p{M} = marcas diacríticas
        normalized = normalized.replaceAll("\\p{M}", "");

        // Convierte a mayúsculas
        return normalized.toUpperCase();
    }

    /**
     * Valida que un BigDecimal sea mayor a cero
     * @param valor Valor a validar
     * @return true si es mayor a cero, false en caso contrario
     */
    public static boolean esMayorACero(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Formatea un BigDecimal como String con un número específico de decimales
     * @param valor Valor a formatear
     * @param decimales Número de decimales
     * @return String formateado
     */
    public static String formatearDecimal(BigDecimal valor, int decimales) {
        if (valor == null) {
            return "0.00";
        }
        return String.format("%." + decimales + "f", valor);
    }
/*
    public static ResponseEntity<?> errorDTOResponse(HttpStatus status, String error, String message) {
        ErrorDTO errorResponse = new ErrorDTO();
        errorResponse.setStatus(status.value());
        errorResponse.setError(error);
        errorResponse.setMessage(message);
        return new ResponseEntity<>(errorResponse, status);
    }*/

}

