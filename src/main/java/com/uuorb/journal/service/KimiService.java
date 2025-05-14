package com.uuorb.journal.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson2.JSONObject;
import com.uuorb.journal.model.AIConfig;
import com.uuorb.journal.model.EngelExpense;
import com.uuorb.journal.model.Expense;
import com.uuorb.journal.model.kimi.KimiMessage;
import com.uuorb.journal.util.KimiUtils;
import com.uuorb.journal.model.kimi.RoleEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service("kimi")
public class KimiService implements AiService {

    @Resource
    KimiUtils kimiUtils;

    final static String FORMAT_PROMPT = "你是记账格式化机器人，请使用如下 JSON 格式输出你的回复： {type:string,price:double,label:string,positive:number,expenseTime:string}。positive0为支出，1为收入，expenseTime:yyyy-mm-dd hh:dd:ss。例：昨天从烟台打车到蓬莱176，返回{type:'交通',price:176.00,label:'打车从烟台到蓬莱',positive:0}，label保持原有意思不变，可以使得更容易理解，label尽量从列表中选择：美食,服装,捐赠,娱乐,燃料,房租,投资,宠物,化妆品,药品,电话费,购物,烟酒,学习,旅游,交通,其他，工资，红包，转账'}";

    final static String PRAISE_DEFAULT_PROMPT = "你是个夸夸机器人，你的角色是女儿，称呼我为爸爸，你的性格是活泼开朗。我会跟你说我的花销，你给我回应。字数在10-30左右";

    final static String PRAISE_PROMPT = "你是个夸夸机器人，你的角色是${relationship}，称呼我为${salutation}，你的性格是${personality}。我会跟你说我的花销，你给我回应。字数在10-30左右";

    final static String GREETING_PROMPT = "你的角色是${relationship}，称呼我为${salutation}，你的性格是${personality}。我们上次见面是${lastLoginTime}，不一定要强调上次见面时间，视情况而定，现在我走到你面前了，你给我打个招呼吧。字数在10-20左右";

    final static String ENGEL_PROMPT = "帮我计算恩格尔系数;";

    @Override
    public Expense formatExpense(String naturalSentence) {
        // 当前的时间
        String now = LocalDateTimeUtil.formatNormal(LocalDateTime.now());
        // 昨天话费了10块 -> expenseTime: 2025-05-11
        List<KimiMessage> messages = CollUtil.newArrayList(
            new KimiMessage(RoleEnum.system.name(),
            FORMAT_PROMPT + "现在时间是: " + now),
            new KimiMessage(RoleEnum.user.name(), naturalSentence));

        KimiMessage chat = kimiUtils.chat(KIMI_MODEL, messages);
        try {
            return JSONObject.parseObject(chat.getContent(), Expense.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String praise(String sentence) {
        List<KimiMessage> messages = CollUtil.newArrayList(new KimiMessage(RoleEnum.system.name(),
            PRAISE_DEFAULT_PROMPT), new KimiMessage(RoleEnum.user.name(), sentence));
        // todo: 记录用户调用的请求和响应
        KimiMessage chat = kimiUtils.chat(KIMI_MODEL, messages);
        return chat.getContent();
    }

    public String praise(String sentence, AIConfig config) {
        var prompt = PRAISE_PROMPT.replace("${relationship}", config.getRelationship())
            .replace("${personality}", config.getPersonality())
            .replace("${salutation}", config.getSalutation());
        List<KimiMessage> messages = CollUtil.newArrayList(new KimiMessage(RoleEnum.system.name(), prompt),
            new KimiMessage(RoleEnum.user.name(), sentence));
        // todo: 记录用户调用的请求和响应
        KimiMessage chat = kimiUtils.chat(KIMI_MODEL, messages);
        return chat.getContent();
    }

    public String greet(AIConfig config) {
        String prompt = GREETING_PROMPT.replace("${relationship}", config.getRelationship())
            .replace("${personality}", config.getPersonality())
            .replace("${salutation}", config.getSalutation())
            .replace("${lastLoginTime}", config.getLastLoginTime());

        List<KimiMessage> messages = CollUtil.newArrayList(new KimiMessage(RoleEnum.system.name(), prompt));
        // todo: 记录用户调用的请求和响应
        KimiMessage chat = kimiUtils.chat(KIMI_MODEL, messages);
        return chat.getContent();
    }

    @Override
    public String generateImage(String model, String description, String role) {
        return "not implement";
    }

    @Override
    public void praiseStream(String sentence, AIConfig config, OutputStream outputStream) {
        var prompt = PRAISE_PROMPT.replace("${relationship}", config.getRelationship())
            .replace("${personality}", config.getPersonality())
            .replace("${salutation}", config.getSalutation());
        List<KimiMessage> messages = CollUtil.newArrayList(new KimiMessage(RoleEnum.system.name(), prompt),
            new KimiMessage(RoleEnum.user.name(), sentence));
        // todo: 记录用户调用的请求和响应
        kimiUtils.chatInStream(KIMI_MODEL, messages, outputStream);
    }

    @Override
    public String tts(String sentence, AIConfig aiConfig) {
        return "";
    }

    @Override
    public void engelExplain(List<EngelExpense> expenses, OutputStream outputStream) {
        List<KimiMessage> messages = CollUtil.newArrayList(new KimiMessage(RoleEnum.system.name(), ENGEL_PROMPT),
            new KimiMessage(RoleEnum.user.name(), expenses.toString()));
        kimiUtils.chatInStream(KIMI_MODEL, messages, outputStream);
    }

}
