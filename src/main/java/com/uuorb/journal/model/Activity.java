package com.uuorb.journal.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Activity {
    private Integer id;
    private List<Expense> expenseList;
    String creatorName;
    String creatorUserId;
    private String activityName;
    private BigDecimal budget;
    private String userId;

    private User user;

    private BigDecimal remainingBudget;
    private BigDecimal totalExpense;

    private BigDecimal totalIncome;

    private Boolean activated;

    private String activityId;

    private List<User> userList;



    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
