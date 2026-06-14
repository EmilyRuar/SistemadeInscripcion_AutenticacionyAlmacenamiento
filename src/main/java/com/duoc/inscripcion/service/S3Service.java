package com.duoc.inscripcion.service;

import com.duoc.inscripcion.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

/**
 * Servicio para operaciones CRUD de resumenes en AWS S3.
 * Estructura del bucket: {bucketName}/{numeroResumen}/resumen_{numeroResumen}.pdf
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /**
     * Sube el PDF del resumen al bucket S3.
     * El archivo se guarda en: {numeroResumen}/resumen_{numeroResumen}.pdf
     */
    public String subirResumen(Long numeroResumen, byte[] contenidoPdf, String contentType) {
        String key = buildKey(numeroResumen);
        log.info("Subiendo resumen al bucket S3: {}/{}", bucketName, key);

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .contentLength((long) contenidoPdf.length)
                .build(),
            RequestBody.fromBytes(contenidoPdf)
        );

        log.info("Resumen {} subido exitosamente a S3", numeroResumen);
        return key;
    }

    /**
     * Descarga el PDF del resumen desde S3.
     */
    public byte[] descargarResumen(Long numeroResumen) {
        String key = buildKey(numeroResumen);
        log.info("Descargando resumen desde S3: {}/{}", bucketName, key);

        try {
            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()
            );
            return response.asByteArray();
        } catch (NoSuchKeyException e) {
            throw new ResourceNotFoundException(
                "Resumen no encontrado en S3 con número: " + numeroResumen);
        }
    }

    /**
     * Reemplaza (modifica) el PDF del resumen en S3.
     */
    public String modificarResumen(Long numeroResumen, MultipartFile archivo) throws IOException {
        String key = buildKey(numeroResumen);
        log.info("Modificando resumen en S3: {}/{}", bucketName, key);

        // Verificar que existe
        verificarExistencia(numeroResumen, key);

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(archivo.getContentType())
                .contentLength(archivo.getSize())
                .build(),
            RequestBody.fromBytes(archivo.getBytes())
        );

        log.info("Resumen {} modificado exitosamente en S3", numeroResumen);
        return key;
    }

    /**
     * Elimina el PDF del resumen de S3.
     */
    public void borrarResumen(Long numeroResumen) {
        String key = buildKey(numeroResumen);
        log.info("Borrando resumen de S3: {}/{}", bucketName, key);

        // Verificar que existe antes de borrar
        verificarExistencia(numeroResumen, key);

        s3Client.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build()
        );

        log.info("Resumen {} borrado exitosamente de S3", numeroResumen);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private String buildKey(Long numeroResumen) {
        return numeroResumen + "/resumen_" + numeroResumen + ".pdf";
    }

    private void verificarExistencia(Long numeroResumen, String key) {
        try {
            s3Client.headObject(
                HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()
            );
        } catch (NoSuchKeyException e) {
            throw new ResourceNotFoundException(
                "Resumen no encontrado en S3 con número: " + numeroResumen);
        }
    }
}
