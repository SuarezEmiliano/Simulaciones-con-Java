package utn.frc.isi.sim.gcuatro;

import java.io.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class Excel {

    public static void crearExcel(String nombreArchivo, double[][] datos) {
        Workbook wb = new XSSFWorkbook();
        FileOutputStream fileOut = null;

        try {
            fileOut = new FileOutputStream(nombreArchivo);

            // Crear hoja
            Sheet sheet = wb.createSheet("Tabla Montecarlo");

            // Crear títulos de columnas
            String[] columnTitles = {"Vuelos", "RND", "Pasajeros", "Costo por sobreventa", "Utilidad", "Beneficio", "Beneficio acumulado", "Promedio beneficio"};

            // Crear la fila de títulos y aplicar estilo
            Row titleRow = sheet.createRow(0);
            CellStyle titleStyle = wb.createCellStyle();
            Font titleFont = wb.createFont();
            titleFont.setBold(true);
            titleFont.setColor(IndexedColors.WHITE.getIndex());
            titleStyle.setFont(titleFont);
            titleStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            titleStyle.setBorderTop(BorderStyle.THIN);
            titleStyle.setBorderBottom(BorderStyle.THIN);
            titleStyle.setBorderLeft(BorderStyle.THIN);
            titleStyle.setBorderRight(BorderStyle.THIN);

            for (int i = 0; i < columnTitles.length; i++) {
                Cell cell = titleRow.createCell(i);
                cell.setCellValue(columnTitles[i]);
                cell.setCellStyle(titleStyle);
            }

            // Agregar datos y aplicar estilo a las celdas
            CellStyle dataStyle = wb.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            for (int fila = 0; fila < datos.length; fila++) {
                Row row = sheet.createRow(fila + 1);
                for (int columna = 0; columna < datos[fila].length; columna++) {
                    Cell cell = row.createCell(columna);
                    cell.setCellValue(datos[fila][columna]);
                    cell.setCellStyle(dataStyle);
                }
            }

            // Ajustar el ancho de las columnas automáticamente
            for (int i = 0; i < columnTitles.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Mostrar mensaje en la consola para la ejecución exitosa del programa
            System.out.println("Tabla creada exitosamente");

            wb.write(fileOut);

        } catch (IOException e) {
            // No se puede abrir o cerrar el archivo
            System.out.println("No se puede generar nuevamente el excel mientras el archivo está abierto. Cierrelo antes de volver a generar");
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
