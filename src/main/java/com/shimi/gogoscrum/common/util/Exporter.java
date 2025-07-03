package com.shimi.gogoscrum.common.util;

import com.shimi.gsf.core.exception.BaseServiceException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Utility class for exporting data to Excel files.
 */
public class Exporter {
    public static final Logger log = LoggerFactory.getLogger(Exporter.class);

    public static byte[] exportExcel(String sheetName, List<String> headers, List<List<Object>> rows) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        writeHeader(headers, workbook, sheet);
        writeBody(rows, workbook, sheet);
        autoSizeColumns(headers.size(), sheet);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headers.size() - 1));
        sheet.createFreezePane(0, 1);

        return writeWorkBook(workbook).toByteArray();
    }

    private static void writeHeader(List<String> headers, Workbook workbook, Sheet sheet) {
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row header = sheet.createRow(0);

        IntStream.range(0, headers.size()).forEach(index -> {
            Cell cell = header.createCell(index);
            cell.setCellValue(headers.get(index));
            cell.setCellStyle(headerCellStyle);
        });
    }

    protected static void writeBody(List<List<Object>> dataRows, Workbook workbook, Sheet sheet) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);

        CellStyle defaultCellStyle = workbook.createCellStyle();
        defaultCellStyle.setFont(font);
        defaultCellStyle.setWrapText(true);
        defaultCellStyle.setVerticalAlignment(VerticalAlignment.TOP);

        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setFont(font);
        dateCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        dateCellStyle.setDataFormat(
                workbook.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));

        IntStream.range(0, dataRows.size()).forEach(rowIndex -> {
            List<Object> dataRow = dataRows.get(rowIndex);
            Row row = sheet.createRow(rowIndex + 1);

            IntStream.range(0, dataRow.size()).forEach(columnIndex -> {
                Cell cell = row.createCell(columnIndex);
                Object cellValue = dataRow.get(columnIndex);

                if (cellValue != null) {
                    switch (cellValue) {
                        case Date date -> cell.setCellValue(date);
                        case Number number -> cell.setCellValue(number.doubleValue());
                        case LocalDateTime localDateTime -> cell.setCellValue(localDateTime);
                        case Boolean b -> cell.setCellValue(b);
                        default -> cell.setCellValue(cellValue.toString());
                    }
                }

                if (cellValue instanceof Date || cellValue instanceof LocalDateTime) {
                    cell.setCellStyle(dateCellStyle);
                } else {
                    cell.setCellStyle(defaultCellStyle);
                }
            });
        });
    }

    private static void autoSizeColumns(int columns, Sheet sheet) {
        IntStream.range(0, columns).forEach(columnIndex -> {
            sheet.autoSizeColumn(columnIndex);
            if (sheet.getColumnWidth(columnIndex) > 50 * 256) {
                sheet.setColumnWidth(columnIndex, 50 * 256);
            }
        });
    }

    private static ByteArrayOutputStream writeWorkBook(Workbook workbook) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            workbook.write(out);
            return out;
        } catch (IOException e) {
            throw new BaseServiceException("exportFailed", "Failed to export excel file",
                    HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}