package com.example.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Optional;

@Entity
@Table(name = "moneda")
public class MonedaEntity extends PanacheEntity {

    @NotBlank(message = "El nombre de la moneda no puede estar vacío")
    @Column(unique = true, nullable = false)
    public String nombreMoneda;

    @NotNull(message = "El tipo de cambio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El tipo de cambio debe ser mayor a 0.0")
    @Column(nullable = false)
    public BigDecimal tipoCambio;

    // Constructor vacío
    public MonedaEntity() {
    }

    // Constructor con parámetros
    public MonedaEntity(String nombreMoneda, BigDecimal tipoCambio) {
        this.nombreMoneda = normalizarCadena(nombreMoneda);
        this.tipoCambio = tipoCambio;
    }

    // ✅ Se ejecuta justo antes de guardar o actualizar en BD
    @PrePersist
    @PreUpdate
    public void normalizarCampos() {
        this.nombreMoneda = normalizarCadena(this.nombreMoneda);
    }

    // Método de búsqueda personalizado
    public static MonedaEntity findByNombreMoneda(String nombreMoneda) {
        System.out.println("findByNombreMoneda:"+normalizarCadena(nombreMoneda));
        return find("nombreMoneda = ?1", normalizarCadena(nombreMoneda)).firstResult();
    }


    // Método utilitario
    private static String normalizarCadena(String cadena) {
        if (cadena == null) return null;
        return Normalizer.normalize(cadena.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase();
    }
}