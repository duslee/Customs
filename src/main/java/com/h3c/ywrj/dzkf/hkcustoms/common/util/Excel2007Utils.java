package com.h3c.ywrj.dzkf.hkcustoms.common.util;

import com.h3c.ywrj.dzkf.hkcustoms.common.prop.ReportPortProperties;
import com.h3c.ywrj.dzkf.hkcustoms.common.prop.ReportProperties;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.h3c.ywrj.dzkf.hkcustoms.common.util.RegexUtils.XLSX_PATTERN;
import static com.h3c.ywrj.dzkf.hkcustoms.common.util.RegexUtils.XLS_PATTERN;

/**
 * Created by Jeff on 2019-12-19 23:50
 */
@Slf4j
public final class Excel2007Utils {
    public static boolean isValidExcel(@NonNull String fileName) {
        return isExcel2003(fileName) || isExcel2007(fileName);
    }

    public static boolean isExcel2003(@NonNull String fileName) {
        return XLS_PATTERN.matcher(fileName).matches();
    }

    public static boolean isExcel2007(@NonNull String fileName) {
        return XLSX_PATTERN.matcher(fileName).matches();
    }

    /**
     * 导出报表数据（xlsx格式）
     *
     * @param excelType      标记导出哪种报表，决定文件名命名
     * @param createTemplate 标记是否创建模板文件
     * @param dataList       待导出的报表数据，createTemplate为true时可指定为null
     * @param request
     * @param response
     * @param <T>
     * @param <P>
     */
    public static <T, P extends ReportProperties> void export2007Excel(
            Integer excelType, boolean createTemplate, List<T> dataList,
            P properties, HttpServletRequest request, HttpServletResponse response) {
        String infoTip = null, dateFormat = null;
        // 分别对应报表表头（从配置文件中拿到），
        // 报表每列对应对象的域名，用于反射获取对应域的值（从配置文件中拿到）
        // 报表要求对应单元格不能为空（从配置文件中拿到，和表头名对应）
        List<String> columnTitleList = null, notNullColumns = null, columnFieldList = null;

        if (properties instanceof ReportPortProperties) {
            final ReportPortProperties portProperties = (ReportPortProperties) properties;
            infoTip = portProperties.getTip();
            dateFormat = portProperties.getDateFormat();
            columnTitleList = portProperties.getColumnTitleList();
            columnFieldList = portProperties.getColumnFieldList();
            notNullColumns = portProperties.getNotNullColumns();
        }

        // 存放每列的最大宽度（由每列的单元格字符串的内容决定
        final Map<Integer, Integer> maxWidthMap = new HashMap<>();

        try {
            // 1. 先获取Workbook（07版本），并填充报表数据（若不是创建模板的情况）
            final Workbook wb = new XSSFWorkbook();
            // 2. 创建Excel表单
            final Sheet sheet = wb.createSheet();

            if (!ListUtils.isEmptyOrNull(columnTitleList)) {
                // 3. 设置表头
                // 设置是否显示信息行（模板可显示）
                configExcelHeader(wb, sheet, createTemplate, infoTip, columnTitleList, notNullColumns);
                // 将表头的每列宽度填充到Map对象中
                for (int i = 0; i < columnTitleList.size(); i++) {
                    maxWidthMap.put(i, widthOfCellValue(columnTitleList.get(i)));
                }

                // 4. 将数据写入到非模板报表文件中，注意日期的格式化（由配置文件决定格式）
                if (!createTemplate) {
                    fillReportFormData(wb, createDateCellStyle(wb, dateFormat), dataList, columnFieldList, maxWidthMap);
                }

                // 定义列的宽度
//            sheet.setDefaultColumnWidth(40 * 256);
                // 设置默认行高，表示2个字符的高度，必须先设置列宽然后设置行高，不然列宽没有效果
//        sheet.setDefaultRowHeight((short) (2 * 256));

                for (int i = 0; i < columnTitleList.size(); i++) {
                    // Auto-size the columns.
//                sheet.autoSizeColumn(i);
                    // 设置列宽以该列最大的宽度自适应
                    sheet.setColumnWidth(i, maxWidthMap.get(i));
                }
                // 释放Map对象
                maxWidthMap.clear();
            }

            // 5. 将wb写入到输出流中
            @Cleanup
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);

            // 6. 设置响应相关属性
            final byte[] content = baos.toByteArray();
            setResponseProperties(generateFileName(excelType, createTemplate), content.length, request, response);

            // 7. 将输出流转成字节数组，并通过缓存的方式读写
            @Cleanup
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(content));
            @Cleanup
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buffer = new byte[8192];
            int readBytes;
            while (-1 != (readBytes = bis.read(buffer, 0, buffer.length))) {
                bos.write(buffer, 0, readBytes);
            }

//            final OutputStream os = response.getOutputStream();
//            wb.write(os);
//            os.flush();
//            os.close();

            wb.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取每列单元格文本内容在单元格中的宽度，若为空，设置为4个字符宽度
     *
     * @param cellValue
     * @return
     */
    private static int widthOfCellValue(String cellValue) {
        return StringUtils.isEmpty(cellValue) ? 4 * 256 : cellValue.getBytes().length * 256 + 320;
    }

    /**
     * 创建标题样式
     *
     * @param wb
     * @return
     */
    private static CellStyle createTitleStyle(Workbook wb) {
        // 创建标题的显示样式
        final CellStyle titleCellStyle = wb.createCellStyle();

        // 设置字体
        final Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 13);
        titleFont.setBold(true);
        titleCellStyle.setFont(titleFont);

        titleCellStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        titleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setBorderTop(BorderStyle.THIN);
        titleCellStyle.setBorderLeft(BorderStyle.THIN);
        titleCellStyle.setBorderBottom(BorderStyle.THIN);
        titleCellStyle.setBorderRight(BorderStyle.THIN);
        titleCellStyle.setWrapText(true);

        return titleCellStyle;
    }

    /**
     * 创建非空单元格样式
     *
     * @param wb
     * @return
     */
    private static CellStyle createNotNullColumnStyle(Workbook wb) {
        final CellStyle notNullColumnStyle = wb.createCellStyle();

        final Font notNUllFont = wb.createFont();
        notNUllFont.setColor(IndexedColors.RED.index);
        notNUllFont.setBold(true);
        notNUllFont.setFontHeightInPoints((short) 13);
        notNullColumnStyle.setFont(notNUllFont);

        notNullColumnStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        notNullColumnStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        notNullColumnStyle.setAlignment(HorizontalAlignment.CENTER);
        notNullColumnStyle.setBorderTop(BorderStyle.THIN);
        notNullColumnStyle.setBorderLeft(BorderStyle.THIN);
        notNullColumnStyle.setBorderBottom(BorderStyle.THIN);
        notNullColumnStyle.setBorderRight(BorderStyle.THIN);
        notNullColumnStyle.setWrapText(true);

        return notNullColumnStyle;
    }

    /**
     * 创建日期单元格样式
     *
     * @param wb
     * @param dateFormat
     * @return
     */
    private static CellStyle createDateCellStyle(Workbook wb, String dateFormat) {
        final CellStyle dateCellStyle = wb.createCellStyle();
        final CreationHelper creationHelper = wb.getCreationHelper();
        dateCellStyle.setDataFormat(creationHelper.createDataFormat()
                .getFormat(StringUtils.isEmpty(dateFormat) ? DateUtils.DATE_FORMAT_4 : dateFormat));
        dateCellStyle.setWrapText(true);
        return dateCellStyle;
    }

    /**
     * 创建单元格文本样式：自动换行
     *
     * @param wb
     * @return
     */
    private static CellStyle createWrapCellStyle(Workbook wb) {
        final CellStyle wrapCellStyle = wb.createCellStyle();
        wrapCellStyle.setWrapText(true);
        return wrapCellStyle;
    }

    /**
     * 创建序号单元格样式：居中显示
     *
     * @param wb
     * @return
     */
    private static CellStyle createNumCellStyle(Workbook wb) {
        final CellStyle numCellStyle = wb.createCellStyle();
        numCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        numCellStyle.setAlignment(HorizontalAlignment.CENTER);
        return numCellStyle;
    }

    /**
     * 配置Excel的表头信息
     *
     * @param wb
     * @param sheet
     * @param createTemplate
     * @param infoTip
     * @param columnTitleList
     * @param notNullColumns
     */
    private static void configExcelHeader(Workbook wb, Sheet sheet, boolean createTemplate, String infoTip,
                                          List<String> columnTitleList, List<String> notNullColumns) {
        // 获取标题单元格样式
        final CellStyle titleStyle = createTitleStyle(wb);
        // 获取非空单元格样式：标记该列不能为空
        final CellStyle notNullColumnStyle = createNotNullColumnStyle(wb);

        // 设置信息提示单元格样式：只适用于模板Excel
        final int rowNum = createTemplate ? 1 : 0;
        if (createTemplate) {
            setInfoTipStyle(wb, sheet, infoTip, columnTitleList.size());
        }
        // 获取表头行
        final Row headerRow = sheet.createRow(rowNum);
        Cell cell;
        String columnTitle;

        for (int i = 0; i < columnTitleList.size(); i++) {
            columnTitle = columnTitleList.get(i);
            cell = headerRow.createCell(i);
            cell.setCellStyle(titleStyle);

            // 若该列要求非空，设置单元格样式为非空风格
            if (!ListUtils.isEmptyOrNull(notNullColumns) && notNullColumns.contains(columnTitle)) {
                cell.setCellStyle(notNullColumnStyle);
            }

            cell.setCellValue(columnTitle);
        }
    }

    /**
     * 在第一行配置信息提示内容及样式
     *
     * @param wb
     * @param sheet
     * @param infoTip
     * @param columns
     */
    private static void setInfoTipStyle(Workbook wb, Sheet sheet, String infoTip, int columns) {
        final Row infoRow = sheet.createRow(0);
        infoRow.setHeight((short) 500);

        final Cell infoCell = infoRow.createCell(0);

        final CellRangeAddress region = new CellRangeAddress(0, 0, 0, columns - 1);
        sheet.addMergedRegion(region);

        final CellStyle infoStyle = wb.createCellStyle();
        final Font infoFont = wb.createFont();
        infoFont.setBold(true);
        infoFont.setColor(IndexedColors.BLUE.index);
        infoFont.setFontHeightInPoints((short) 13);
        infoStyle.setFont(infoFont);
        infoStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        infoStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        infoStyle.setAlignment(HorizontalAlignment.CENTER);
        infoStyle.setBorderTop(BorderStyle.THIN);
        infoStyle.setBorderLeft(BorderStyle.THIN);
        infoStyle.setBorderBottom(BorderStyle.THIN);
        infoStyle.setBorderRight(BorderStyle.THIN);
        infoStyle.setWrapText(true);

        infoCell.setCellValue(StringUtils.isEmpty(infoTip) ? "序号列可以不填，红色字体为必填项!" : infoTip);
        infoCell.setCellStyle(infoStyle);
    }

    /**
     * 填充报表数据
     *
     * @param wb              {@link Workbook}
     * @param list            数据实体列表(此处为ExportAlarm)
     * @param columnFieldList 列名列表
     */
    private static <T> void fillReportFormData(Workbook wb, CellStyle dateCellStyle, List<T> list,
                                               List<String> columnFieldList, Map<Integer, Integer> maxWidthMap) {
        final CellStyle numCellStyle = createNumCellStyle(wb);
        final CellStyle wrapCellStyle = createWrapCellStyle(wb);

        // 存放条目对象
        T exportForm;
        // 条目对象的声明域
        Field field;
        // 存放声明域对应的值
        Object value;
        // 获取excel文档的第一个sheet
        final Sheet sheet = wb.getSheetAt(0);
        // 获取行
        Row row;
        // 获取单元格
        Cell cell;

        for (int i = 0; i < list.size(); i++) {
            // 创建一行
            row = sheet.createRow(i + 1);
            // 从列表中获取对应一行的对象
            exportForm = list.get(i);
            for (int j = 0; j < columnFieldList.size(); j++) {
                cell = row.createCell(j);
                // 序列号自动加1
                if (j == 0) {
                    cell.setCellStyle(numCellStyle);
                    cell.setCellValue(i + 1);
                } else {
                    try {
                        // 根据配置的列获取对应对象的声明域
                        field = exportForm.getClass().getDeclaredField(columnFieldList.get(j));
                        // 设置域可访问
                        field.setAccessible(true);
                        // 获取对应域的值
                        value = field.get(exportForm);
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                        value = null;
                    }

                    // 填充对应单元格的值（若为Date格式，按配置文件中的日期格式转换；若为空，填充“”，其它设置值）
                    if (value instanceof Date) {
                        // 对时间列设置日期格式
                        cell.setCellStyle(dateCellStyle);
                        cell.setCellValue(value == null ? null : (Date) value);
                    } else {
                        cell.setCellStyle(wrapCellStyle);
                        cell.setCellValue(value == null ? "" : String.valueOf(value));
                    }
                }

                int length;
                switch (cell.getCellType()) {
                    case NUMERIC:
                        length = widthOfCellValue(String.valueOf(cell.getNumericCellValue()));
                        break;
                    case FORMULA:
                        length = widthOfCellValue(cell.getCellFormula());
                        break;
                    default:
                        length = widthOfCellValue(cell.getStringCellValue());
                        break;
                }

                if (length > 15000) {
                    length = 15000;
                }
                maxWidthMap.put(j, Math.max(length, maxWidthMap.get(j)));
            }
        }
    }

    /**
     * 设置响应体的属性
     *
     * @param fileName
     * @param contentLength
     * @param request
     * @param response
     * @throws UnsupportedEncodingException
     */
    public static void setResponseProperties(String fileName, int contentLength, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        final String enc = StandardCharsets.UTF_8.name();
        response.reset();
        response.setCharacterEncoding(enc);
        response.setHeader("Cache-Control", "No-cache");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Content-Length", String.valueOf(contentLength));
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        // 支持xls
//        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        // 支持xlsx
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setContentType("Application/x-download");

        // 解决不同浏览器中文文件名乱码问题
        String realFileName;
        final String userAgent = request.getHeader("User-Agent");
//        log.info("userAgent: {}", userAgent);
        if (null != userAgent) {
            if (userAgent.contains("MSIE")) {
                // IE浏览器
                realFileName = URLEncoder.encode(fileName, enc).replace("+", " ");
            } else if (userAgent.contains("Firefox")) {
                // 火狐浏览器
                realFileName = "=?utf-8?B?" + Base64.getEncoder().encodeToString(fileName.getBytes("utf-8")) + "?=";
            } else {
                // 其它浏览器
                realFileName = URLEncoder.encode(fileName, enc);
            }
        } else {
            realFileName = URLEncoder.encode(fileName, enc);
        }
        response.setHeader("Content-disposition", "attachment; filename=" + realFileName);
    }

    /**
     * 生成文件名
     */
    private static String generateFileName(Integer excelType, boolean createTemplate) {
        StringBuilder sb = new StringBuilder();
        switch (excelType) {
            default:
                sb.append("服务器扫描端口统计表");
                break;
        }
        sb.append("-");
        sb.append(DateUtils.convertDateToString(new Date()));
        return sb.append(createTemplate ? "（模板）" : "").append(".xlsx").toString();
    }

    private static void addValidationData(Sheet sheet, String[] constraints, int firstRow, int lastRow, int firstCol, int lastCol) {
        // 初始化下拉框列表
        final DataValidationHelper helper = sheet.getDataValidationHelper();
        final DataValidationConstraint constraint = helper.createExplicitListConstraint(constraints);

        // 设置下拉框列表
        final CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation regionNameValidation = helper.createValidation(constraint, addressList);
        regionNameValidation.setSuppressDropDownArrow(true);
        regionNameValidation.setShowErrorBox(false);
        sheet.addValidationData(regionNameValidation);
    }
}
