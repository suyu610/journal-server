package com.uuorb.journal.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

/**
 * easyExcel所有到的converter
 */
public class PositiveConverter implements Converter<Integer> {

    @Override
    public Class<Integer> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Integer convertToJavaData(ReadCellData cellData, ExcelContentProperty contentProperty,
        GlobalConfiguration globalConfiguration) {
        if ("是".equals(cellData.getStringValue())) {
            return 1;
        }
        if ("否".equals(cellData.getStringValue())) {
            return 0;
        } else {
            return 416;
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty,
        GlobalConfiguration globalConfiguration) throws Exception {
        if (value.equals(0)) {
            return new WriteCellData<>("支出");
        }
        if (value.equals(1)) {
            return new WriteCellData<>("收入");
        }

        return new WriteCellData<>("未知");
    }

}
