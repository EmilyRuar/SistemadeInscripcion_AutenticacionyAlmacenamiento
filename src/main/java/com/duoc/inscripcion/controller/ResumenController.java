package com.duoc.inscripcion.controller;

import com.duoc.inscripcion.dto.InscripcionResponseDTO;
import com.duoc.inscripcion.service.InscripcionService;
import com.duoc.inscripcion.service.PdfService;
import com.duoc.inscripcion.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Controller para gestionar el resumen de inscripción (PDF + AWS S3).
 *
 * Endpoints:
 *   GET  /api/resumenes/{id}/descargar         → Genera y descarga el PDF localmente
 *   POST /api/resumenes/{id}/subir-s3          → Sube el PDF a AWS S3
 *   GET  /api/resumenes/{id}/descargar-s3      → Descarga el PDF desde AWS S3
 *   PUT  /api/resumenes/{id}/modificar-s3      → Reemplaza el PDF en AWS S3
 *   DELETE /api/resumenes/{id}/borrar-s3       → Elimina el PDF de AWS S3
 */
@RestController
@RequestMapping("/api/resumenes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Resúmenes de Inscripción", description = "Gestión de PDF de resúmenes en local y AWS S3")
@SecurityRequirement(name = "bearerAuth")
public class ResumenController {

    private final InscripcionService inscripcionService;
    private final PdfService pdfService;
    private final S3Service s3Service;

    // ── 1. Generar y descargar PDF localmente ────────────────────────────────

    @Operation(
        summary = "Generar y descargar resumen PDF",
        description = "Genera el PDF del resumen de inscripción y lo retorna para guardar en el equipo del usuario."
    )
    @GetMapping("/{id}/descargar")
    public ResponseEntity<byte[]> descargarResumenLocal(
            @Parameter(description = "ID de la inscripción") @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        log.info("Usuario [{}] descargando resumen local del ID: {}", jwt.getSubject(), id);

        InscripcionResponseDTO inscripcion = inscripcionService.obtenerPorId(id);
        byte[] pdfBytes = pdfService.generarResumenPdf(inscripcion);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
            ContentDisposition.attachment()
                .filename("resumen_inscripcion_" + id + ".pdf")
                .build()
        );
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    // ── 2. Subir PDF a S3 ───────────────────────────────────────────────────

    @Operation(
        summary = "Subir resumen a AWS S3",
        description = "Genera el PDF del resumen y lo sube al bucket S3. "
            + "Se guarda en la ruta: {numeroResumen}/resumen_{numeroResumen}.pdf"
    )
    @PostMapping("/{id}/subir-s3")
    public ResponseEntity<Map<String, Object>> subirResumenS3(
            @Parameter(description = "ID de la inscripción") @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        log.info("Usuario [{}] subiendo resumen {} a S3", jwt.getSubject(), id);

        InscripcionResponseDTO inscripcion = inscripcionService.obtenerPorId(id);
        byte[] pdfBytes = pdfService.generarResumenPdf(inscripcion);
        String s3Key = s3Service.subirResumen(id, pdfBytes, "application/pdf");

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "mensaje", "Resumen subido exitosamente a S3",
            "numeroResumen", id,
            "s3Key", s3Key
        ));
    }

    // ── 3. Descargar PDF desde S3 ───────────────────────────────────────────

    @Operation(
        summary = "Descargar resumen desde AWS S3",
        description = "Descarga el PDF del resumen almacenado en el bucket S3."
    )
    @GetMapping("/{id}/descargar-s3")
    public ResponseEntity<byte[]> descargarResumenS3(
            @Parameter(description = "ID / número del resumen") @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        log.info("Usuario [{}] descargando resumen {} desde S3", jwt.getSubject(), id);

        byte[] pdfBytes = s3Service.descargarResumen(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
            ContentDisposition.attachment()
                .filename("resumen_inscripcion_" + id + ".pdf")
                .build()
        );
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    // ── 4. Modificar (reemplazar) PDF en S3 ────────────────────────────────

    @Operation(
        summary = "Modificar resumen en AWS S3",
        description = "Reemplaza el archivo PDF del resumen existente en S3 con uno nuevo. "
            + "Útil para corregir errores en la inscripción."
    )
    @PutMapping(value = "/{id}/modificar-s3", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> modificarResumenS3(
            @Parameter(description = "ID / número del resumen") @PathVariable Long id,
            @Parameter(description = "Archivo PDF nuevo") @RequestPart("archivo") MultipartFile archivo,
            @AuthenticationPrincipal Jwt jwt) throws IOException {

        log.info("Usuario [{}] modificando resumen {} en S3", jwt.getSubject(), id);

        String s3Key = s3Service.modificarResumen(id, archivo);

        return ResponseEntity.ok(Map.of(
            "mensaje", "Resumen modificado exitosamente en S3",
            "numeroResumen", id,
            "s3Key", s3Key
        ));
    }

    // ── 5. Borrar PDF de S3 ─────────────────────────────────────────────────

    @Operation(
        summary = "Borrar resumen de AWS S3",
        description = "Elimina permanentemente el archivo PDF del resumen del bucket S3."
    )
    @DeleteMapping("/{id}/borrar-s3")
    public ResponseEntity<Map<String, Object>> borrarResumenS3(
            @Parameter(description = "ID / número del resumen") @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        log.info("Usuario [{}] borrando resumen {} de S3", jwt.getSubject(), id);

        s3Service.borrarResumen(id);

        return ResponseEntity.ok(Map.of(
            "mensaje", "Resumen eliminado exitosamente de S3",
            "numeroResumen", id
        ));
    }
}
