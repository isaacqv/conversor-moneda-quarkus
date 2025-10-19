package com.example.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ConversorRequest {

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @NotBlank(message = "La moneda origen no puede estar vacía")
    private String monedaOrigen;

    @NotBlank(message = "La moneda destino no puede estar vacía")
    private String monedaDestino;

    // Constructores
    public ConversorRequest() {
    }

    public ConversorRequest(BigDecimal monto, String monedaOrigen, String monedaDestino) {
        this.monto = monto;
        this.monedaOrigen = monedaOrigen;
        this.monedaDestino = monedaDestino;
    }

    // Getters y Setters
    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMonedaOrigen() {
        return monedaOrigen;
    }

    public void setMonedaOrigen(String monedaOrigen) {
        this.monedaOrigen = monedaOrigen;
    }

    public String getMonedaDestino() {
        return monedaDestino;
    }

    public void setMonedaDestino(String monedaDestino) {
        this.monedaDestino = monedaDestino;
    }
}
