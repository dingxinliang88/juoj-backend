package com.juzi.oj.mapper;

import com.juzi.oj.model.entity.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author codejuzi
 */
@SpringBootTest
class QuestionMapperTest {

    @Autowired
    private QuestionMapper questionMapper;

    @Test
    void testAddQuestion() {
        Question question = new Question();
        question.setUserId(1L);
        question.setTitle("A+B Problem");
        question.setContent("Description\n" +
                "\n" +
                "Calculate a+b\n" +
                "Input\n" +
                "\n" +
                "Two integer a,b (0<=a,b<=10)\n" +
                "Output:\n" +
                "\n" +
                "Output a+b");
        question.setTags("[\"简单\", \"模拟\"]");
        question.setAnswer("public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        System.out.println(a + b);\n" +
                "    }\n" +
                "}");
        question.setJudgeCase("[{\"input\":\"1 2\",\"output\":\"3\"},{\"input\":\"3 4\",\"output\":\"7\"}]");
        question.setJudgeConfig("{\"timeLimit\":1000,\"memoryLimit\":10000,\"stackLimit\":1000}");

        questionMapper.insert(question);

        System.out.println(question.getId());
    }

}