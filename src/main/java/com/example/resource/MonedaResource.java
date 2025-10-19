package com.example.resource;

import com.example.dto.ConversorRequest;
import com.example.dto.ConversorResponse;
import com.example.dto.ErrorResponse;
import com.example.service.MonedaService;
import com.example.util.Util;
import com.example.entity.MonedaEntity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/api/conversor")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Conversor de Monedas", description = "API REST para conversión de tipos de cambio")
public class MonedaResource {
    private static final Logger LOG = Logger.getLogger(MonedaResource.class);

    @Inject
    MonedaService monedaService;

    /**
     * Endpoint de prueba
     */
    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Endpoint de prueba", description = "Verifica que la API está funcionando")
    public String hello() {
        return "¡Hola! API de Conversor de Monedas con Quarkus funcionando 🚀";
    }

    /**
     * 1. REGISTRAR MONEDA
     * POST /api/conversor/moneda
     *
     * Ejemplo Request:
     * {
     *   "nombreMoneda": "EURO",
     *   "tipoCambio": 3.96
     * }
     */
    @POST
    @Path("/moneda")
    @Operation(summary = "Registrar moneda", description = "Crea una nueva moneda en el sistema")
    @APIResponse(responseCode = "201", description = "Moneda creada exitosamente",
            content = @Content(schema = @Schema(implementation = MonedaEntity.class)))
    @APIResponse(responseCode = "409", description = "Moneda ya existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "400", description = "Datos inválidos")
    public Response registrarMoneda(@Valid MonedaEntity moneda) {
        LOG.infof("POST /api/conversor/moneda - Registrando: %s", moneda.nombreMoneda);

        try {
            MonedaEntity monedaCreada = monedaService.registrarMoneda(moneda);
            return Response.status(Response.Status.CREATED).entity(monedaCreada).build();
        } catch (WebApplicationException e) {
            ErrorResponse error = new ErrorResponse(e.getResponse().getStatus(),
                    "Conflicto",
                    e.getMessage());
            return Response.status(e.getResponse().getStatus()).entity(error).build();
        } catch (Exception e) {
            LOG.error("Error al registrar moneda", e);
            ErrorResponse error = new ErrorResponse(500,
                    "Error interno",
                    "Error al registrar la moneda");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    /**
     * 2. LISTAR MONEDAS
     * GET /api/conversor/monedas
     */
    @GET
    @Path("/monedas")
    @Operation(summary = "Listar monedas", description = "Obtiene todas las monedas registradas")
    @APIResponse(responseCode = "200", description = "Lista de monedas",
            content = @Content(schema = @Schema(implementation = MonedaEntity.class)))
    @APIResponse(responseCode = "404", description = "No hay monedas registradas")
    public Response listarMonedas() {
        LOG.info("GET /api/conversor/monedas - Listando todas las monedas");

        try {
            List<MonedaEntity> monedas = monedaService.listarMonedas();
            return Response.ok(monedas).build();
        } catch (NotFoundException e) {
            ErrorResponse error = new ErrorResponse(404,
                    "No hay monedas registradas",
                    e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
    }

    /**
     * 3. BUSCAR MONEDA POR ID
     * GET /api/conversor/moneda?id=?
     */
    @GET
    @Path("/moneda")
    @Operation(summary = "Buscar moneda por ID", description = "Obtiene una moneda específica por su ID")
    @APIResponse(responseCode = "200", description = "Moneda encontrada")
    @APIResponse(responseCode = "404", description = "Moneda no encontrada")
    public Response buscarMonedaId(@QueryParam("id") Long id) {
        LOG.infof("GET /api/conversor/moneda?id=%d - Buscando moneda", id);

        try {
            MonedaEntity moneda = monedaService.buscarPorId(id);
            return Response.ok(moneda).build();
        } catch (NotFoundException e) {
            ErrorResponse error = new ErrorResponse(404,
                    "Not Found",
                    e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
    }


    @GET
    @Path("/moneda/{nombre}")
    public Response buscarNombre(@PathParam("nombre") String nombre) {
        //MonedaEntity monedaOpt = MonedaEntity.findByNombreMoneda(nombre);
        //System.out.println("Metodo @GET:"+monedaOpt.toString());
        /*return monedaOpt
                .map(monedaResult -> Response.ok(monedaResult).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());*/
        LOG.infof("GET /api/conversor/moneda/%d - Buscando moneda", nombre);
        MonedaEntity moneda = MonedaEntity.findByNombreMoneda(nombre);
        if (moneda == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(moneda).build();
    }

    @PATCH
    @Path("/moneda/{nombre}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarParcial(
            @PathParam("nombre") String nombre,
            MonedaEntity cambios) {

        // 1️⃣ Buscar la moneda existente
        MonedaEntity monedaExistente = MonedaEntity.findByNombreMoneda(nombre);
        if (monedaExistente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No se encontró una moneda: " + Util.normalizarCadena(nombre))
                    .build();
        }

        // 2️⃣ Actualizar solo los campos enviados (no nulos)
        if (cambios.nombreMoneda != null && !cambios.nombreMoneda.isBlank()) {
            String nombreNormalizado = Util.normalizarCadena(cambios.nombreMoneda);

            // Verificar duplicados
            MonedaEntity duplicado = MonedaEntity.find("nombreMoneda = ?1", nombreNormalizado).firstResult();
            if (duplicado != null && !duplicado.nombreMoneda.equals(nombre)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Ya existe otra moneda con el nombre: " + nombreNormalizado)
                        .build();
            }

            monedaExistente.nombreMoneda = nombreNormalizado;
        }

        if (cambios.tipoCambio != null) {
            monedaExistente.tipoCambio = cambios.tipoCambio;
        }

        // 3️⃣ Persistir los cambios
        monedaExistente.persist();

        // 4️⃣ Retornar la entidad actualizada
        return Response.ok(monedaExistente).build();
    }

    /**
     * 4. ACTUALIZAR MONEDA
     * PUT /api/conversor/moneda/{nombreMoneda}
     *
     * Ejemplo Request:
     * {
     *   "nombreMoneda": "DOLAR",
     *   "tipoCambio": 3.75
     * }
     */
    @PUT
    @Path("/moneda/{nombreMoneda}")
    @Operation(summary = "Actualizar moneda", description = "Actualiza los datos de una moneda existente")
    @APIResponse(responseCode = "200", description = "Moneda actualizada")
    @APIResponse(responseCode = "404", description = "Moneda no encontrada")
    public Response actualizarMoneda(
            @PathParam("nombreMoneda") String nombreMoneda,
            @Valid MonedaEntity moneda) {

        LOG.infof("PUT /api/conversor/moneda/%s - Actualizando moneda", nombreMoneda);

        try {
            MonedaEntity monedaActualizada = monedaService.actualizarMoneda(nombreMoneda, moneda);
            return Response.ok(monedaActualizada).build();
        } catch (NotFoundException e) {
            ErrorResponse error = new ErrorResponse(404,
                    "Not Found",
                    e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
    }

    /**
     * 5. ELIMINAR MONEDA
     * DELETE /api/conversor/moneda/{id}
     */
    @DELETE
    @Path("/moneda/{id}")
    @Operation(summary = "Eliminar moneda", description = "Elimina una moneda del sistema")
    @APIResponse(responseCode = "204", description = "Moneda eliminada")
    @APIResponse(responseCode = "404", description = "Moneda no encontrada")
    public Response eliminarMoneda(@PathParam("id") Long id) {
        LOG.infof("DELETE /api/conversor/moneda/%d - Eliminando moneda", id);

        try {
            monedaService.eliminarMoneda(id);
            return Response.noContent().build();
        } catch (NotFoundException e) {
            ErrorResponse error = new ErrorResponse(404,
                    "Not Found",
                    e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
    }

    /**
     * 6. CALCULAR CONVERSIÓN (MÉTODO PRINCIPAL)
     * POST /api/conversor/calcular
     *
     * Ejemplo Request:
     * {
     *   "monto": 253.408233,
     *   "monedaOrigen": "Soles",
     *   "monedaDestino": "euro"
     * }
     *
     * Ejemplo Response:
     * {
     *   "montoOriginal": 253.41,
     *   "montoConvertido": 1003.50,
     *   "monedaOrigen": "SOLES",
     *   "monedaDestino": "EURO",
     *   "tipoCambio": 3.96
     * }
     */
    @POST
    @Path("/calcular")
    @Operation(
            summary = "Calcular conversión de moneda",
            description = "Calcula la conversión entre dos monedas aplicando el tipo de cambio registrado"
    )
    @APIResponse(
            responseCode = "200",
            description = "Conversión calculada exitosamente",
            content = @Content(schema = @Schema(implementation = ConversorResponse.class))
    )
    @APIResponse(
            responseCode = "404",
            description = "Moneda destino no encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @APIResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos"
    )
    public Response calcularConversion(@Valid ConversorRequest request) {
        LOG.infof("POST /api/conversor/calcular - Conversión: %s %s -> %s",
                request.getMonto(), request.getMonedaOrigen(), request.getMonedaDestino());

        try {
            ConversorResponse response = monedaService.calcularConversion(request);
            return Response.ok(response).build();

        } catch (NotFoundException e) {
            LOG.warnf("Moneda no encontrada: %s", e.getMessage());
            ErrorResponse error = new ErrorResponse(404,
                    "Not Found",
                    e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();

        } catch (Exception e) {
            LOG.error("Error al calcular conversión", e);
            ErrorResponse error = new ErrorResponse(500,
                    "Error interno",
                    "Error al calcular la conversión");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

}