package com.uuorb.journal.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.uuorb.journal.constant.ResultStatus;
import com.uuorb.journal.exception.CustomException;
import com.uuorb.journal.mapper.ExpenseMapper;
import com.uuorb.journal.model.Expense;
import com.uuorb.journal.util.IDUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    @Resource
    ActivityService activityService;

    @Resource
    ExpenseMapper expenseMapper;

    public PageInfo queryList(String activityId, Integer pageNum) {
        PageHelper.startPage(pageNum, 20);
        List<Expense> expenseList = expenseMapper.queryList(activityId);
        return new PageInfo<>(expenseList);
    }

    public List<Expense> queryListUnlimited(String activityId) {
        return expenseMapper.queryList(activityId);
    }

    public Expense queryById(String expenseId) {
        return expenseMapper.queryById(expenseId);
    }

    public void insertExpenseAndCalcRemainingBudget(Expense expense) {
        expense.setExpenseId(IDUtil.expenseId());
        // 入库
        expenseMapper.insert(expense);
        // 计算剩余预算
        activityService.refreshActivityRemainingBudget(expense.getActivityId());
    }

    public void update(Expense expense) {
        expenseMapper.update(expense);
        //todo: 需要重新算一遍activity的余额
        activityService.refreshActivityRemainingBudget(expense.getActivityId());
    }

    public void checkIsOwner(Expense expense) throws CustomException {
        Integer count = expenseMapper.count(expense);
        if (count <= 0) {
            throw new CustomException(ResultStatus.RESOURCE_NOT_FOUND);
        }
    }

    public void deleteExpense(String expenseId, String activityId) {
        expenseMapper.delete(expenseId);
        activityService.refreshActivityRemainingBudget(activityId);

    }

    // todo: 删除
}
