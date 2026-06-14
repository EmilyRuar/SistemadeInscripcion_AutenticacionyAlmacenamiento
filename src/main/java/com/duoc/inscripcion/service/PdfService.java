package com.duoc.inscripcion.service;

import com.duoc.inscripcion.dto.CursoResumenDTO;
import com.duoc.inscripcion.dto.InscripcionResponseDTO;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para generar el resumen de inscripción en formato PDF.
 */
@Service
public class PdfService {

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Genera el PDF del resumen de inscripción y lo retorna como arreglo de bytes.
     */
    public byte[] generarResumenPdf(InscripcionResponseDTO inscripcion) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputStream));
             Document document = new Document(pdfDoc)) {

            // ── Encabezado ──────────────────────────────────────────────────
            Paragraph titulo = new Paragraph("PLATAFORMA EDUCATIVA DUOC UC")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(0, 70, 127));
            document.add(titulo);

            Paragraph subtitulo = new Paragraph("Resumen de Inscripción de Cursos")
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(80, 80, 80));
            document.add(subtitulo);

            document.add(new Paragraph("\n"));

            // ── Número de Resumen ────────────────────────────────────────────
            Paragraph numResumen = new Paragraph("N° Resumen: " + inscripcion.getId())
                .setFontSize(12)
                .setBold()
                .setBackgroundColor(new DeviceRgb(230, 240, 255))
                .setPadding(5);
            document.add(numResumen);

            // ── Datos del Estudiante ─────────────────────────────────────────
            document.add(new Paragraph("DATOS DEL ESTUDIANTE")
                .setFontSize(12).setBold().setFontColor(new DeviceRgb(0, 70, 127))
                .setMarginTop(10));

            Table datosTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                .setWidth(UnitValue.createPercentValue(100));

            addRow(datosTable, "Nombre:", inscripcion.getNombreEstudiante());
            addRow(datosTable, "Email:", inscripcion.getEmailEstudiante());
            addRow(datosTable, "Fecha Inscripción:",
                inscripcion.getFechaInscripcion() != null
                    ? inscripcion.getFechaInscripcion().format(FORMATTER) : "N/A");
            document.add(datosTable);

            // ── Cursos Inscritos ─────────────────────────────────────────────
            document.add(new Paragraph("CURSOS INSCRITOS")
                .setFontSize(12).setBold().setFontColor(new DeviceRgb(0, 70, 127))
                .setMarginTop(15));

            Table cursosTable = new Table(UnitValue.createPercentArray(new float[]{10, 35, 30, 25}))
                .setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            addHeader(cursosTable, "#");
            addHeader(cursosTable, "Nombre del Curso");
            addHeader(cursosTable, "Instructor");
            addHeader(cursosTable, "Costo");

            // Filas de cursos
            int i = 1;
            for (CursoResumenDTO curso : inscripcion.getCursos()) {
                cursosTable.addCell(new Cell().add(new Paragraph(String.valueOf(i++))));
                cursosTable.addCell(new Cell().add(new Paragraph(curso.getNombre())));
                cursosTable.addCell(new Cell().add(new Paragraph(curso.getInstructor())));
                cursosTable.addCell(new Cell().add(
                    new Paragraph("$" + curso.getCosto().toPlainString())));
            }
            document.add(cursosTable);

            // ── Total ────────────────────────────────────────────────────────
            Paragraph total = new Paragraph("TOTAL A PAGAR: $" +
                inscripcion.getTotalPagar().toPlainString())
                .setFontSize(14).setBold()
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontColor(new DeviceRgb(0, 120, 0))
                .setMarginTop(10);
            document.add(total);

            // ── Pie de página ────────────────────────────────────────────────
            document.add(new Paragraph("\n\nEste documento es el comprobante oficial de su inscripción.")
                .setFontSize(9).setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER));

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del resumen", e);
        }

        return outputStream.toByteArray();
    }

    private void addRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold()));
        table.addCell(new Cell().add(new Paragraph(value)));
    }

    private void addHeader(Table table, String text) {
        table.addCell(new Cell().add(new Paragraph(text).setBold())
            .setBackgroundColor(new DeviceRgb(0, 70, 127))
            .setFontColor(ColorConstants.WHITE));
    }
}
