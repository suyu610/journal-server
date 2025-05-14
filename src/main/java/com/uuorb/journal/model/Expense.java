package com.uuorb.journal.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.uuorb.journal.converter.PositiveConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Expense {
    @ExcelIgnore
    Integer id;

    @ExcelIgnore
    String expenseId;

    @ExcelProperty(value = "分类", index = 1)
    String type;

    @ExcelProperty(value = "金额")
    double price;

    @ColumnWidth(60)
    @ExcelProperty("备注")
    String label;

    @ExcelProperty(value = "方向", converter = PositiveConverter.class, index = 0)
    Integer positive;

    @ExcelIgnore
    String userId;
    @ColumnWidth(20)
    @ExcelProperty("记录者")
    String userNickname;

    @ExcelIgnore
    String userAvatar;

    @ExcelIgnore
    String activityId;

    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    @DateTimeFormat("yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    @ExcelProperty("账单日期")
    @ColumnWidth(20)
    @DateTimeFormat("yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date expenseTime;



    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;
}
