package com.uuorb.journal.mapper;

import com.uuorb.journal.model.EngelExpense;
import com.uuorb.journal.model.Expense;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExpenseMapper {

    List<Expense> queryList(String activityId);

    List<EngelExpense> queryListBrief(String activityId);
    Integer insert(Expense expense);

    void update(Expense expense);
    @Select("SELECT COUNT(*) FROM expense WHERE user_id = #{userId} AND expense_id = #{expenseId}")
    Integer count(Expense expense);

    Expense queryById(String expenseId);
    @Delete("DELETE FROM expense WHERE expense_id = #{expenseId}")
    void delete(String expenseId);
}
