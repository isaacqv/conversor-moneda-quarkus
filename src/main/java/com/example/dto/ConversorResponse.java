package com.example.dto;

import java.math.BigDecimal;

public class  ConversorResponse {

    private BigDecimal montoOriginal;
    private BigDecimal montoConvertido;
    private String monedaOrigen;
    private String monedaDestino;
    private BigDecimal tipoCambio;

    // Constructores
    public ConversorResponse() {
    }

    public ConversorResponse(BigDecimal montoOriginal, BigDecimal montoConvertido,
                             String monedaOrigen, String monedaDestino, BigDecimal tipoCambio) {
        this.montoOriginal = montoOriginal;
        this.montoConvertido = montoConvertido;
        this.monedaOrigen = monedaOrigen;
        this.monedaDestino = monedaDestino;
        this.tipoCambio = tipoCambio;
    }

    // Getters y Setters
    public BigDecimal getMontoOriginal() {
        return montoOriginal;
    }

    public void setMontoOriginal(BigDecimal montoOriginal) {
        this.montoOriginal = montoOriginal;
    }

    public BigDecimal getMontoConvertido() {
        return montoConvertido;
    }

    public void setMontoConvertido(BigDecimal montoConvertido) {
        this.montoConvertido = montoConvertido;
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

    public BigDecimal getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(BigDecimal tipoCambio) {
        this.tipoCambio = tipoCambio;
    }
}