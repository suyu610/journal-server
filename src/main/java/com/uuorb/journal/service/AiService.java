package com.uuorb.journal.service;

import com.uuorb.journal.model.AIConfig;
import com.uuorb.journal.model.EngelExpense;
import com.uuorb.journal.model.Expense;

import java.io.OutputStream;
import java.util.List;

public interface AiService {

    final String KIMI_MODEL = "moonshot-v1-8k";

    Expense formatExpense(String naturalSentence);

    String praise(String sentence);

    String praise(String sentence, AIConfig config);

    String greet(AIConfig aiConfig);

    String generateImage(String model, String description, String role);

    void praiseStream(String sentence, AIConfig config, OutputStream outputStream);

    String tts(String sentence, AIConfig aiConfig);

    void engelExplain(List<EngelExpense> expenseList, OutputStream outputStream);
}
