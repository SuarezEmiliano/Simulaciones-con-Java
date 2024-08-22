package utn.frc.isi.sim.g4.RungeKutta;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class GeneradorExcelRG {

    static final String NOMBRE_ARCHIVO = "Tabla de RungeKutta.xls";

    public static void crearExcel(String nombreArchivo, double[][] datos) {
        Workbook wb = new XSSFWorkbook();
        FileOutputStream fileOut = null;

        // Abrir el archivo
        try {
            fileOut = new FileOutputStream(NOMBRE_ARCHIVO);
        } catch (FileNotFoundException e) {
            System.out.println("No se puede abrir el archivo. Revise que se encuentre cerrado e intentelo nuevamente");
            try {
                wb.close();
            } catch (IOException e1) {
                System.out.println("No se puede cerrar el archivo. Revise que se encuentre cerrado e intentelo nuevamente");
            }
            return;
        }

        // Crear hoja
        Sheet sheet = wb.createSheet("Metodo de integracion");

        // Crear cabeceras de columnas
        ArrayList<String> columnTitles = obtenerEncabezadosColumnas();

        // Crear la fila de títulos y aplicar estilo
        Row titleRow = sheet.createRow(0);
        CellStyle estiloEncabezados = obtenerCellStyleEncabezadosColumnas(wb);
        for (int i = 0; i < columnTitles.size(); i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(columnTitles.get(i));
            cell.setCellStyle(estiloEncabezados);
        }

        // Crear las filas de datos y aplicar estilo
        CellStyle estiloDatos = obtenerCellStyleDatos(wb);
        for (int currentRowNumber = 0; currentRowNumber < datos.length; currentRowNumber++) {
            Row row = sheet.createRow(currentRowNumber + 1);
            for (int currentColumnNumber = 0; currentColumnNumber < datos[currentRowNumber].length; currentColumnNumber++) {
                Cell cell = row.createCell(currentColumnNumber);
                double value = datos[currentRowNumber][currentColumnNumber];
                String formattedValue;

                if (value == Math.floor(value)) {
                    formattedValue = String.valueOf((int) value);
                } else {
                    formattedValue = String.format("%.4f", value);
                }

                cell.setCellValue(formattedValue);
                cell.setCellStyle(estiloDatos);
            }
        }

        // Ajustar el ancho de las columnas automáticamente
        for (int i = 0; i < columnTitles.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Mostrar mensaje en la consola para la ejecución exitosa del programa
        System.out.println("Tabla creada exitosamente");

        try {
            wb.write(fileOut);
        } catch (IOException e) {
            System.out.println("Error al intentar escribir el archivo");
        } finally {
            try {
                fileOut.close();
            } catch (IOException e) {
                System.out.println("Error al intentar cerrar el archivo");
            }
        }
    }

    private static ArrayList<String> obtenerEncabezadosColumnas() {
        ArrayList<String> columnTitles = new ArrayList<>(Arrays.asList(
                "t" , "M", "k1", "t+h/2", "M+k1/2", "k2",
                "t+h/2", "M+k2/2", "k3", "t+h", "M+k3", "k4"
        ));
        return columnTitles;
    }

    static CellStyle obtenerCellStyleEncabezadosColumnas(Workbook wb) {
        CellStyle titleStyle = crearCellStyle(wb, IndexedColors.WHITE.getIndex(), true);
        titleStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return titleStyle;
    }

    static CellStyle obtenerCellStyleDatos(Workbook wb) {
        return crearCellStyle(wb, IndexedColors.BLACK.getIndex(), false);
    }

    static CellStyle crearCellStyle(Workbook wb, short color, boolean esNegrita) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(esNegrita);
        font.setColor(color);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

}
