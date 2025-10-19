package com.example.service;

import com.example.dto.ConversorRequest;
import com.example.dto.ConversorResponse;
import com.example.entity.MonedaEntity;
import com.example.util.Util;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class MonedaService {

    private static final Logger LOG = Logger.getLogger(MonedaService.class);

    /**
     * Registra una nueva moneda en el sistema
     *
     * @param moneda Entidad moneda a registrar
     * @return Moneda registrada con ID asignado
     */
    @Transactional
    public MonedaEntity registrarMoneda(MonedaEntity moneda) {
        // Normalizar el nombre de la moneda
        String nombreNormalizado = Util.normalizarCadena(moneda.nombreMoneda);

        // Verificar si ya existe
        MonedaEntity monedaExistente = MonedaEntity.findByNombreMoneda(nombreNormalizado);
        if (monedaExistente != null) {
            LOG.warnf("Intento de registrar moneda duplicada: %s", nombreNormalizado);
            throw new WebApplicationException(
                    "Ya existe una moneda registrada con ese nombre: [" + nombreNormalizado + "]",
                    409 // HTTP 409 Conflict
            );
        }

        // Asignar nombre normalizado
        moneda.nombreMoneda = nombreNormalizado;

        // Persistir en base de datos
        moneda.persist();

        LOG.infof("Moneda registrada exitosamente: ID=%d, Nombre=%s, TipoCambio=%s",
                moneda.id, moneda.nombreMoneda, moneda.tipoCambio);

        return moneda;
    }

    /**
     * Lista todas las monedas registradas
     *
     * @return Lista de monedas
     */
    public List<MonedaEntity> listarMonedas() {
        List<MonedaEntity> monedas = MonedaEntity.listAll();

        if (monedas.isEmpty()) {
            LOG.warn("No se encontraron monedas registradas");
            throw new NotFoundException("No se encontraron registros de monedas");
        }

        LOG.infof("Se encontraron %d monedas registradas", monedas.size());
        return monedas;
    }

    /**
     * Busca una moneda por su ID
     *
     * @param id ID de la moneda
     * @return Moneda encontrada
     */
    public MonedaEntity buscarPorId(Long id) {
        MonedaEntity moneda = MonedaEntity.findById(id);

        if (moneda == null) {
            LOG.warnf("Moneda no encontrada con ID: %d", id);
            throw new NotFoundException("Moneda no encontrada con ID: " + id);
        }

        return moneda;
    }

    /**
     * Actualiza una moneda existente
     *
     * @param nombreMoneda      Nombre de la moneda a actualizar
     * @param monedaActualizada Datos actualizados
     * @return Moneda actualizada
     */
    @Transactional
    public MonedaEntity actualizarMoneda(String nombreMoneda, MonedaEntity monedaActualizada) throws Exception {
        // Normalizar nombre
        String nombreNormalizado = Util.normalizarCadena(nombreMoneda);

        // Buscar moneda existente
        MonedaEntity monedaExistente = MonedaEntity.findByNombreMoneda(nombreNormalizado);

        if (monedaExistente == null) {
            LOG.warnf("Intento de actualizar moneda inexistente: %s", nombreNormalizado);
            throw new NotFoundException("Moneda no encontrada o registrada: [" + nombreNormalizado + "]");
        }

        // Actualizar campos
        monedaExistente.nombreMoneda = Util.normalizarCadena(monedaActualizada.nombreMoneda);
        monedaExistente.tipoCambio = monedaActualizada.tipoCambio;

        // Panache actualiza automáticamente al estar en transacción
        monedaExistente.persist();
        LOG.infof("Moneda actualizada: ID=%d, Nuevo nombre=%s, Nuevo tipo cambio=%s",
                monedaExistente.id, monedaExistente.nombreMoneda, monedaExistente.tipoCambio);

        return monedaExistente;
    }

    /**
     * Elimina una moneda por su ID
     *
     * @param id ID de la moneda a eliminar
     */
    @Transactional
    public void eliminarMoneda(Long id) {
        MonedaEntity moneda = buscarPorId(id);
        moneda.delete();
        LOG.infof("Moneda eliminada: ID=%d, Nombre=%s", id, moneda.nombreMoneda);
    }

    /**
     * MÉTODO PRINCIPAL: Calcula la conversión entre monedas
     * <p>
     * Equivalente al método calcularCambio() de Spring Boot
     *
     * @param request Request con monto, moneda origen y destino
     * @return Response con resultado de la conversión
     */
    public ConversorResponse calcularConversion(ConversorRequest request) {
        LOG.infof("=== INICIANDO CONVERSIÓN ===");
        LOG.infof("Monto: %s, Origen: %s, Destino: %s",
                request.getMonto(), request.getMonedaOrigen(), request.getMonedaDestino());

        // 1. Normalizar el nombre de la moneda destino
        String nombreDestino = Util.normalizarCadena(request.getMonedaDestino());
        LOG.debugf("Moneda destino normalizada: %s", nombreDestino);

        // 2. Buscar la moneda destino en la base de datos
        MonedaEntity monedaDestino = MonedaEntity.findByNombreMoneda(nombreDestino);

        if (monedaDestino == null) {
            LOG.errorf("Moneda destino no encontrada: %s", nombreDestino);
            throw new NotFoundException("Moneda no encontrada o registrada: [" + nombreDestino + "]");
        }

        LOG.infof("Moneda destino encontrada: ID=%d, Tipo cambio=%s",
                monedaDestino.id, monedaDestino.tipoCambio);

        // 3. Calcular el monto convertido
        // Fórmula: montoConvertido = monto * tipoCambio
        BigDecimal resultado = Util.multiplicar(request.getMonto(), monedaDestino.tipoCambio);

        LOG.debugf("Cálculo: %s * %s = %s",
                request.getMonto(), monedaDestino.tipoCambio, resultado);

        // 4. Construir la respuesta
        ConversorResponse response = new ConversorResponse();

        // Redondear monto original a 2 decimales
        response.setMontoOriginal(Util.redondarDecimales(request.getMonto(), 2));

        // Redondear monto convertido a 2 decimales
        response.setMontoConvertido(Util.redondarDecimales(resultado, 2));

        // Normalizar moneda origen
        response.setMonedaOrigen(Util.normalizarCadena(request.getMonedaOrigen()));

        // Moneda destino ya normalizada
        response.setMonedaDestino(nombreDestino);

        // Tipo de cambio utilizado
        response.setTipoCambio(monedaDestino.tipoCambio);

        LOG.infof("=== CONVERSIÓN EXITOSA ===");
        LOG.infof("Resultado: %s %s -> %s %s (Tipo cambio: %s)",
                response.getMontoOriginal(),
                response.getMonedaOrigen(),
                response.getMontoConvertido(),
                response.getMonedaDestino(),
                response.getTipoCambio());

        return response;
    }
}