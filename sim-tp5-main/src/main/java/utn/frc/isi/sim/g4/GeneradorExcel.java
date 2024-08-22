package utn.frc.isi.sim.g4;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class GeneradorExcel {

    static final String NOMBRE_ARCHIVO = "Simulacion Biblioteca.xls";

    public static void crearExcel(String nombreArchivo, Object[][] datos) {
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
        Sheet sheet = wb.createSheet("Modelo de Simulación Dinámico");

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
                if (datos[currentRowNumber][currentColumnNumber] instanceof Number) {
                    Number num = (Number) datos[currentRowNumber][currentColumnNumber];
                    double value = num.doubleValue();
                    String formattedValue;

                    if (value == 0 && !((currentRowNumber == 0 && (currentColumnNumber == 0 || currentColumnNumber == 1)) ||
                            currentColumnNumber == 26 || currentColumnNumber == 27 || currentColumnNumber == 29 ||
                            currentColumnNumber == 30 || currentColumnNumber == 31 || currentColumnNumber == 32 ||
                            currentColumnNumber == 33)) {
                        formattedValue = "";
                    } else if (value == Math.floor(value)) {
                        formattedValue = String.valueOf(num.intValue());
                    } else {
                        formattedValue = String.format("%.4f", value);
                    }

                    cell.setCellValue(formattedValue);
                } else if (datos[currentRowNumber][currentColumnNumber] instanceof String) {
                    cell.setCellValue((String) datos[currentRowNumber][currentColumnNumber]);
                }
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
                "Numero de evento" , "Reloj(minutos)", "Eventos", "RND1", "RND1 EXP (Tiempo)", "Próxima Llegada",
                "Estado servidor 1(L/O)", "Estado servidor 2(L/O)", "RND2", "Tipo Llegada",
                "RND3", "RND3 exp (Tiempo petición)", "Fin Petición Serv 1", "Fin Petición Serv 2",
                "RNDM", "M", "Fin Consulta Servidor 1", "Fin Consulta Servidor 2",
                "RND5", "RND5 Unif(tiempo Devolución)", "Fin Devolución Servidor 1", "Fin Devolucion Servidor 2",
                "RND6", "Se va/Se queda",
                "RND7", "RND7 exp(tiempo lectura)",
                "Personas en Cola", "Personas en la biblioteca", "Estado Biblioteca", "Contador Personas no entran",
                "Cont Pers Total", "Promedio Personas no entran", "Acumulador Permanencia", "Promedio Permanencia"
        ));

        for (int i = 1; i <= 20; i++) {
            columnTitles.add("Estado Cliente " + String.format("%02d", i));
            columnTitles.add("Hora llegada");
            columnTitles.add("Hora salida");
            columnTitles.add("Tiempo en el sistema");
            columnTitles.add("Hora Fin Lectura");
        }

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
