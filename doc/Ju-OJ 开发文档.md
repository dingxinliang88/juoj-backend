# Ju-OJ 开发文档

> 前期：
>
> - 数据库设计 ✅
> - 接口设计 ✅



# 用户模块

核心功能实现完成，具体详见[OpenAPI 文档](./api/user-module.md)



# 题目模块

完成基本框架搭建（实体类、Mapper、Service、Controller）

添加额外DTO：JudgeCase、 JudgeConfig、JudgeInfo

## 一、判题机模块架构

> 目的：跑通完整的业务流程，再进行代码沙箱复杂的实现4

### 1.1 判题模块和代码沙箱的关系

判题模块：调用代码沙箱，把代码和输入交给代码沙箱去执行

代码沙箱：只负责接收代码和输入，返回编译执行后的结果，不负责判题 => 可以作为独立的项目 / 服务，提供给其他需要执行代码的项目使用

这两个模块完全解耦：

![image-20230917122404752](assets/image-20230917122404752.png)

### 1.2 思考：为什么代码沙箱要接受和输出一组运行用例？

前提：题目有多组测试用例

如果是每个用例单独调用一次代码沙箱，会调用多次接口、需要多次网络传输、程序需要多次编译、记录多次程序的执行状态。因此主要是为了性能优化，重复的代码不重复编译。

> 常见的性能优化方式：**批处理**



### 1.3 判题机模块开发

1）定义代码沙箱的接口，提高通用鑫

- 之后项目只调用接口，不调用具体的实现类，这样在使用其他的代码沙箱实现类时，不需要修改名称，便于扩展
- 代码沙箱的请求接口中，timeLimit参数可加可不加，可以自行扩展，即时中断程序
- 扩展思路：增加一个查看代码沙箱状态的接口

```java
/**
 * @author codejuzi
 */
public interface CodeSandbox {

    /**
     * 代码沙箱执行接口
     *
     * @param executeCodeRequest 执行代码请求
     * @return 执行代码响应
     */
    ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest);
}
```

2）定义多种不同的代码沙箱实现

- 示例代码沙箱（ExampleCodeSandbox）：仅仅为了跑通业务流程
- 远程代码沙箱（RemoteCodeSandbox）
- 第三方代码沙箱（ThirdPartySandbox）：调用网络中现成的代码沙箱，比如https://github.com/criyle/go-judge

3）编写单元测试，验证单个代码沙箱的执行

```java
class CodeSandboxTest {

    @Test
    void execute() {
        CodeSandbox codeSandbox = new ExampleCodeSandbox();
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code("int main() {}")
                .language(QuestionSubmitLanguageEnum.JAVA.getValue())
                .inputList(Arrays.asList("1 2", "3 4"))
                .build();
        ExecuteCodeResponse response = codeSandbox.execute(request);
      	System.out.println(response);
    }
}
```

存在的问题：我们把创建某个沙箱的代码写死了，如果后面项目要改用其他沙箱，可能要修改很多地方的代码



4）**使用工厂模式**：根据用户传入的字符串参数（沙箱类别），来生成对应的代码沙箱实现类。（此处使用静态工厂模式，实现简单，符合需求）

```java
public class CodeSandboxFactory {

    private CodeSandboxFactory() {
    }


    /**
     * 创建代码沙箱
     *
     * @param type 代码沙箱类别
     * @return code sandbox
     */
    public static CodeSandbox newInstance(final String type) {
        switch (type) {
            case "remote":
                return new RemoteCodeSandbox();
            case "third-party":
                return new ThirdPartyCodeSandbox();
            default:
                // example
                return new ExampleCodeSandbox();
        }
    }

}
```

由此，我们可以根据字符串动态生成实例，提高通用性



5）参数配置化：把项目中的一些可以交给用户自定义的选项或字符串，写到配置文件中。这样开发者只需要改动配置文件，不需要深入代码，就可以自定义使用项目的更多功能。

```yaml
codesandbox:
	type: example
```

在代码中通过@Value注解读取即可

```java
@SpringBootTest
class CodeSandboxTest {

    @Value("${codesandbox.type:example}")
    private String type;

    @Test
    void execute() {
//        CodeSandbox codeSandbox = new ExampleCodeSandbox();
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code("int main() {}")
                .language(QuestionSubmitLanguageEnum.JAVA.getValue())
                .inputList(Arrays.asList("1 2", "3 4"))
                .build();
        ExecuteCodeResponse response = codeSandbox.execute(request);
        assertNull(response);
    }
}
```

6）代码沙箱能力增强，使用代理模式

比如：

- 我们需要在调用代码沙箱前，输出请求参数日志
- 在代码沙箱调用后，输出响应结果日志，便于管理员分析

传统的代码沙箱，需要在每个代码沙箱实现类前后书写输出日志代码。

**使用代理模式**，提供一个Proxy，**来增强代码沙箱的能力**（代理模式的作用就是增强能力）

原本：需要开发者调用多次输出日志

![image-20230917144958632](assets/image-20230917144958632.png)

使用代理后：不仅不用改变原本的代码沙箱实现类，而且对调用者来说，调用方式几乎没有改变，也不需要在每个调用沙箱的地方去写统计代码

![image-20230917145010583](assets/image-20230917145010583.png)



> 代理模式的实现原理：
>
> 实现被代理的接口，通过构造函数接收一个被代理的接口实现类，调用被代理的接口实现类，在调用前后增加对应的操作

```java
@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

    private final CodeSandbox codeSandbox;

    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息: {}", executeCodeRequest);
        ExecuteCodeResponse response = codeSandbox.execute(executeCodeRequest);
        log.info("代码沙箱响应信息: {}", response);
        return response;
    }
}
```

使用方式：

```java
@Test
void execute() {
    //        CodeSandbox codeSandbox = new ExampleCodeSandbox();
    CodeSandbox codeSandbox = new CodeSandboxProxy(CodeSandboxFactory.newInstance(type));
    ExecuteCodeRequest request = ExecuteCodeRequest.builder()
      .code("int main() {}")
      .language(QuestionSubmitLanguageEnum.JAVA.getValue())
      .inputList(Arrays.asList("1 2", "3 4"))
      .build();
    ExecuteCodeResponse response = codeSandbox.execute(request);
  	System.out.println(response);
}
```



### 1.4 判题服务

定义单独的JudgeService类，而不是把所有判题相关的代码写到QuestionSubmitInfoService中，有利于后续的模块抽离、微服务改造

```java
public interface JudgeService {

    /**
     * 判题
     *
     * @param questionSubmitId 题目提交信息id
     * @return 题目提交信息汇总
     */
    QuestionSubmitInfo doJudge(Long questionSubmitId);
}
```

### 1.5 判题服务业务流程

1）传入题目提交信息的id，获取到对应的题目、提交信息（包含代码、编程语言等）

2）如果题目提交状态不为等待中，不需要重复执行

3）更改判题（题目提交信息）的状态为“判题中”，防止重复执行，也能够让用户即时看到状态

4）调用代码沙箱，获取执行结果

5）根据沙箱的执行结果，设置题目的判题状态和信息



### 1.6 判断逻辑

1）判断题目的限制是否符合要求

2）检查异常情况

3）先判断代码沙箱执行的结果输出数量是否和预期数量相等

4）依次判断每一项输出和预期输出是否相等



### 1.7 策略模式优化

判题策略可能会有很多种，比如：我们的代码沙箱本身执行程序需要消耗时间，这个时间可能不同的编程语言是不同的，比如沙箱执行Java需要额外花几秒。

因此我们可以采用策略模式，针对不同的情况，定义独立的策略，便于分别修改策略和维护。而不是把所有的判题逻辑（if...else...）全部混在一起写



实现步骤：

1）定义判题上下文对象，用于定义在策略中传递的参数（理解为一种DTO）

```java
/**
 * 判题策略上下文
 *
 * @author codejuzi
 */
@Data
@Builder
public class JudgeContext {

    /**
     * 代码沙箱执行程序信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 题目输入
     */
    private List<String> inputList;

    /**
     * 程序执行的实际输出
     */
    private List<String> outputList;

    /**
     * 题目判题用例
     */
    private List<JudgeCase> judgeCaseList;

    /**
     * 题目信息
     */
    private Question question;

    /**
     * 题目提交信息
     */
    private QuestionSubmitInfo questionSubmitInfo;
}
```

2）定义判题策略接口，让代码更加通用化

```java
/**
 * 判题策略
 *
 * @author codejuzi
 */
public interface JudgeStrategy {

    /**
     * 执行判题
     *
     * @param judgeContext 判题上下文对象
     * @return 判题信息
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}

```



3）实现默认判题策略，先把 `judgeService`中的代码搬运过来

4）再新增一种判题策略，通过 `if ... else ...`的方式选择使用哪种策略

但是，如果选择某种判题策略的过程比较复杂，如果都写在调用判题服务的代码中，代码会越来越复杂，会有大量 if ... else ...，所以建议单独编写一个判断策略的类。

```java
/**
 * 判题策略工厂
 *
 * @author codejuzi
 */
public class JudgeStrategyFactory {
  private JudgeStrategyFactory() {
  }

  private static final Map<Enum<QuestionSubmitLanguageEnum>, JudgeStrategy> JUDGE_STRATEGY_MAP;
  private static final DefaultJudgeStrategy DEFAULT_JUDGE_STRATEGY = new DefaultJudgeStrategy();

  static {
    JavaLanguageJudgeStrategy javaLanguageJudgeStrategy = new JavaLanguageJudgeStrategy();
    JUDGE_STRATEGY_MAP = new ConcurrentHashMap<>();
    JUDGE_STRATEGY_MAP.put(DEFAULT, DEFAULT_JUDGE_STRATEGY);
    JUDGE_STRATEGY_MAP.put(JAVA, javaLanguageJudgeStrategy);
    // TODO 实现不同语言具体的判题逻辑
    JUDGE_STRATEGY_MAP.put(CPLUSPLUS, DEFAULT_JUDGE_STRATEGY);
    JUDGE_STRATEGY_MAP.put(C_SHAPE, DEFAULT_JUDGE_STRATEGY);
    JUDGE_STRATEGY_MAP.put(GOLANG, DEFAULT_JUDGE_STRATEGY);
    JUDGE_STRATEGY_MAP.put(PYTHON, DEFAULT_JUDGE_STRATEGY);
    JUDGE_STRATEGY_MAP.put(JAVASCRIPT, DEFAULT_JUDGE_STRATEGY);
  }

  public static JudgeStrategy newInstance(QuestionSubmitLanguageEnum languageEnum) {
    assert languageEnum != null;
    return JUDGE_STRATEGY_MAP.getOrDefault(languageEnum, DEFAULT_JUDGE_STRATEGY);
  }
}
```

5）定义JudgeManager，目的就是尽量简化对判题功能的调用，让调用方写最少的代码，调用简单。对于判题策略的选取，也是在JudgeManager中处理的。

```java
/**
 * 判题管理器，简化代码
 *
 * @author codejuzi
 */
public class JudgeManager {

    /**
     * 执行判题
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        String submitLanguage = judgeContext.getQuestionSubmitInfo().getSubmitLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if (QuestionSubmitLanguageEnum.JAVA.getValue().equals(submitLanguage)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
```



6）实现题目提交服务，执行判题

通过异步的形式，自定义线程池，提高多个用户判题效率

```java
/**
  * 判题线程池
  */
private final Executor JUDGE_EXECUTOR_POOL = new ThreadPoolExecutor(2, 4, 10000, TimeUnit.MILLISECONDS,
                                                                    new LinkedBlockingQueue<>(100));

@Override
public Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
  // ...

  Long submitInfoId = submitInfo.getId();

  // 执行判题服务
  CompletableFuture.runAsync(() -> judgeService.doJudge(submitInfoId), JUDGE_EXECUTOR_POOL);

  return submitInfoId;
}
```



# 代码沙箱原生JDK实现

> 代码沙箱的作用：**只负责接收代码和输入，返回编译运行的结果，不负责判题，可以作为独立的服务暴露出去**。

Java原生实现代码沙箱，尽可能不借助第三方库和依赖，用最干净原始的方式实现代码沙箱。

## 一、通过命令行执行Java代码

### 1.1 Java程序执行的流程

接收代码 => 编译代码(javac) => 执行代码(java)

程序实例代码，注意没有package语句，文件位置位于项目的resource 目录下：

```java
public class SimpleCompute {
    public static void main(String[] args) {
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        System.out.println("结果：" + (a + b));
    }
}
```

Java编译代码

```sh
javac {Java类代码路径}
```

> PS：此处可能出现乱码问题，可以在编译时指定编码：
>
> ```sh
> javac -encoding utf-8 {Java类代码路径}
> ```



Java执行代码，并携带参数：

```sh
java -cp {class文件所在路径} {class文件名} {参数}
```



示例：

```bash
➜  simple_computed_args git:(fea_jdk_sandbox) ✗ javac -encoding utf-8 ./SimpleCompute.java
➜  simple_computed_args git:(fea_jdk_sandbox) ✗ java -cp . SimpleCompute 3 7              
结果：10
```



### 1.2 统一类名

在做 acm 模式的题目的时候。算法的类名都是统一的 Main，会对用户的输入的代码有一定的要求，便于后台进行统一的处理和判题。

作用：可以减少编译时类名不一致的风险，而且不用用户代码中提取类名，更方便。

Main.java：

```java
public class Main {
    public static void main(String[] args) {
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        System.out.println("结果：" + (a + b));
    }
}
```

实际执行命令时，可以统一使用 Main 类名（在要编译的 Java 文件目录下）

```bash
➜  simple_computed_args git:(fea_jdk_sandbox) ✗ javac -encoding utf-8 ./Main.java         
➜  simple_computed_args git:(fea_jdk_sandbox) ✗ java -cp . Main 3 7                       
结果：10
```



## 二、核心流程实现

核心思路：使用程序代替人工，用程序来操作命令行完成编译执行代码

核心依赖：需要依赖Java的进程类Process



1. 把用户代码保存为文件
2. 编译代码，得到class文件
3. 执行代码，得到输出结果
4. 收集整理输出结果
5. 文件清理，释放空间
6. 错误处理，提升程序健壮性



### 2.1 保存为代码文件

为了提高操作文件的效率，此处引入hutool核心类

```xml
<properties>
  <hutool.version>5.8.21</hutool.version>
</properties>

<dependencies>
  <dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-core</artifactId>
    <version>${hutool.version}</version>
  </dependency>
</dependencies>
```

新建目录，将每个用户提交的代码都存放独立的目录下，通过UUID随机生成目录名，便于隔离和维护：

```java
public class JavaNativeCodeSandbox implements CodeSandbox {

  private static final String GLOBAL_CODE_DIR_NAME = "tmp_code";

  private static final String GLOBAL_CODE_FILE_NAME = "Main.java";

  @Override
  public ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest) {
    // 1、保存用户代码为文件
    String code = executeCodeRequest.getCode();
    // 获取用户工作文件路径
    String userDir = System.getProperty("user.dir");
    String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;

    // 判断全局文件路径是否存在
    if (!FileUtil.exist(globalCodePathName)) {
      // 不存在，则创建
      FileUtil.mkdir(globalCodePathName);
    }
    // 存在，则保存用户提交代码，用户代码隔离存放
    String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
    // 实际存放文件的目录：Main.java
    String userCodePath = userCodeParentPath + File.separator + GLOBAL_CODE_FILE_NAME;
    File userCodeFile = FileUtil.writeBytes(code.getBytes(StandardCharsets.UTF_8), userCodePath);

    // ...
  }
}
```

### 2.2 编译代码

使用Process类在终端执行命令：

```java
String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
Process compileProcess = Runtime.getRuntime().exec(compileCmd);
```

执行 `process.waitFor` 等待程序执行完成，并通过返回的 `exitValue`判断程序是否正常返回，然后从 Process 的输入流 `inputStream`和错误流 `errorStream`获取控制台输出。

```java
// 2、编译代码，得到class文件
String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());

try {
  Process compileProcess = Runtime.getRuntime().exec(compileCmd);
  // 等待Process执行结束，得到退出状态码
  int exitValue = compileProcess.waitFor();
  // 正常编译，退出
  if (exitValue == 0) {
    log.info("Compile Java Code Success!");
    // 通过进程获取正常输出到控制台的信息
    BufferedReader logReader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
    StringBuilder compileLogStrBuilder = new StringBuilder();
    // 逐行读取
    String compileLogLine;
    while ((compileLogLine = logReader.readLine()) != null) {
      compileLogStrBuilder.append(compileLogLine);
    }
    log.info("Compile Code Logs: {}", compileLogStrBuilder);
  }
  // 出现异常
  else {
    log.error("Compile Java Code Failed!");
    // 通过进程获取正常输出到控制台的信息
    BufferedReader logReader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
    StringBuilder compileLogStrBuilder = new StringBuilder();
    // 逐行读取
    String compileLogLine;
    while ((compileLogLine = logReader.readLine()) != null) {
      compileLogStrBuilder.append(compileLogLine);
    }
    log.info("Compile Code Logs: {}", compileLogStrBuilder);
    // 分批获取进程的错误输出
    BufferedReader errorLogReader = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()));
    StringBuilder errorLogStrBuilder = new StringBuilder();
    // 逐行读取
    String errorLogLine;
    while ((errorLogLine = errorLogReader.readLine()) != null) {
      errorLogStrBuilder.append(errorLogLine);
    }
    log.error("Compile Code Error Logs: {}", errorLogStrBuilder);
  }
} catch (IOException | InterruptedException e) {
  throw new RuntimeException(e);
}
```

封装ExecuteMessage类，将上述代码抽取成工具类ProcessUtil

```java
import com.juzi.codesandbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 程序进程执行工具类
 *
 * @author codejuzi
 */
@Slf4j
public class ProcessUtil {

  public static ExecuteMessage getRunProcessMessage(String processType, Process runProcess) {
    ExecuteMessage executeMessage = new ExecuteMessage();
    StopWatch stopWatch = new StopWatch();

    try {
      stopWatch.start();
      // 等待Process执行结束，得到退出状态码
      int exitValue = runProcess.waitFor();
      executeMessage.setExitValue(exitValue);

      // 正常编译，退出
      if (exitValue == 0) {
        log.info("{} Success!", processType);
      }
      // 出现异常
      else {
        log.error("{} Failed! ExitValue: {}", processType, exitValue);
        executeMessage.setErrorMessage(getMessage(runProcess.getErrorStream()));
      }
      executeMessage.setMessage(getMessage(runProcess.getInputStream()));
      stopWatch.stop();
      executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
    return executeMessage;
  }

  private static String getMessage(InputStream is) throws IOException {
    // 通过进程获取正常输出到控制台的信息
    BufferedReader logReader = new BufferedReader(new InputStreamReader(is));
    List<String> logLineList = new ArrayList<>();
    // 逐行读取
    String logLine;
    while ((logLine = logReader.readLine()) != null) {
      logLineList.add(logLine);
    }
    return StringUtils.join(logLineList, "\n");
  }
}
```



### 2.3 执行程序

使用Process类执行Java命令，为了解决编译或者运行的时候出现乱码问题，在指令中加入：

```bash
-Dfile.encoding=UTF-8
```

此命令适用于执行从输入参数（args）中获取值的代码。

```java
// 3、执行代码
String runCmdPattern = "java -Dfile.encoding=UTF-8 -cp %s Main %s";
List<String> inputList = executeCodeRequest.getInputList();
for (String inputArgs : inputList) {
  String runCmd = String.format(runCmdPattern, userCodeParentPath, inputArgs);

  try {
    Process runProcess = Runtime.getRuntime().exec(runCmd);
    ExecuteMessage executeMessage = ProcessUtil.getRunProcessMessage("Run Code", runProcess);
    log.info("execute message: {}", executeMessage);
  } catch (IOException e) {
    throw new RuntimeException(e);
  }
}
```

很多 OJ 都是 ACM  赛制，需要和用户交互，让用户不断输入内容并获取输出。

部分 acm 赛制需要进行`Scanner`控制台输入。对此类程序，需要使用`OutPutStream`向程序终端发送参数，并及时获取结果，最后需要记得关闭字节流释放资源。

新增交互式方法：

```java
public static ExecuteMessage getInteractProcessMessage(Process runProcess, String args) {
  ExecuteMessage executeMessage = new ExecuteMessage();

  try (OutputStream outputStream = runProcess.getOutputStream();
       OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
    // 从控制台输入参数
    String[] arguments = args.split(" ");
    String join = StringUtils.join(arguments, "\n") + "\n";
    outputStreamWriter.write(join);
    // 回车，发送参数
    outputStreamWriter.flush();

    executeMessage.setMessage(getMessage(runProcess.getInputStream()));

    // 释放资源
    runProcess.destroy();
  } catch (Exception e) {
    e.printStackTrace();
  }
  return executeMessage;
}
```



测试程序：

```java
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner cin = new Scanner(System.in);
        int a = cin.nextInt(), b = cin.nextInt();
        System.out.println("结果：" + (a + b));
    }
}
```

执行程序：

```java
// 3、执行代码
String runCmdPattern = "java -Dfile.encoding=UTF-8 -cp %s Main %s";
List<String> inputList = executeCodeRequest.getInputList();
for (String inputArgs : inputList) {
  String runCmd = String.format(runCmdPattern, userCodeParentPath, inputArgs);

  try {
    Process runProcess = Runtime.getRuntime().exec(runCmd);
    ExecuteMessage executeMessage = ProcessUtil.getInteractProcessMessage(runProcess, inputArgs); // 交互式
    // ExecuteMessage executeMessage = ProcessUtil.getRunProcessMessage("Run Code", runProcess);
    log.info("execute message: {}", executeMessage);
  } catch (IOException e) {
    throw new RuntimeException(e);
  }
}
```



### 2.4 整理输出

1. 通过循环遍历执行结果，从中获取输出列表
2. 获取程序执行时间（使用Spring的StopWatch计时）

可以使用最大值来统计时间，便于后续判题服务计算程序是否超时

```java
// 4、整理输出结果
ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
long maxExecTime = 0L;
List<String> outputList = new ArrayList<>();
for (ExecuteMessage executeMessage : executeMessageList) {
  String errorMessage = executeMessage.getErrorMessage();
  if (StringUtils.isNotBlank(errorMessage)) {
    // 执行中出错
    executeCodeResponse.setMessage(errorMessage);
    executeCodeResponse.setStatus(FAILED.getValue());
    break;
  }
  outputList.add(executeMessage.getMessage());

  Long execTime = Optional.ofNullable(executeMessage.getTime()).orElse(0L);
  maxExecTime = Math.max(maxExecTime, execTime);
}
// 正常执行
if (outputList.size() == executeMessageList.size()) {
  executeCodeResponse.setStatus(RUNNING.getValue());
}
executeCodeResponse.setOutputList(outputList);

JudgeInfo judgeInfo = new JudgeInfo();
// todo Java原生获取内存占用
judgeInfo.setMemory(0L);
judgeInfo.setTime(maxExecTime);
executeCodeResponse.setJudgeInfo(judgeInfo);

return executeCodeResponse;
```

获取内存信息：实现比较复杂，因为无法从 Process 对象中获取到子进程号，也不推荐在 Java 原生实现代码沙箱的过程中获取。

> 扩展：可以每个测试用例都有一个独立的内存、时间占用的统计



### 2.5 文件清理

防止服务器空间不足，删除代码目录：

```java
// 5、清理文件
if (userCodeFile.getParentFile() != null) {
  boolean delFileRes = FileUtil.del(userCodeParentPath);
  log.info("Delete File Result: {}", delFileRes);
}
```



### 2.6 统一错误处理

封装一个错误处理方法，当程序抛出异常的时候，直接返回错误响应。

```java
private ExecuteCodeResponse handleError(Throwable e) {
  ExecuteCodeResponse response = new ExecuteCodeResponse();
  response.setOutputList(Collections.emptyList());
  response.setMessage(e.getMessage());
  response.setStatus(FAILED.getValue());
  response.setJudgeInfo(new JudgeInfo());
  return response;
}
```



## 三、Java程序异常情况

> 针对于本项目：用户提交恶意代码等



### 3.1 执行超时（时间上）

占用时间资源，导致程序卡死，不释放资源。

例如：

```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        long ONE_HOUR = 60 * 60 * 1000L;
        Thread.sleep(ONE_HOUR);
        System.out.println("睡醒了");
    }
}
```



### 3.2 占用内存（空间上）

占用内存资源，导致空间浪费。

例如：

```java
public class MemoryError {
    public static void main(String[] args) {
        List<byte[]> bytes = new ArrayList<>();
        while (true) {
            bytes.add(new byte[100000]);
        }
    }
}
```

实际运行上述程序时，我们会发现，内存占用到达一定空间后，程序就自动报错：`java.lang.OutOfMemoryError: Java heap space`，而不是无限增加内存占用，直到系统死机。这是 JVM 的一个保护机制。

> 可以使用 JVisualVM 或 JConsole 工具，连接到 JVM 虚拟机上来可视化查看运行状态。



### 3.3 读文件，信息泄露

如：可以直接通过相对路径获取项目配置文件，获取到项目敏感配置信息

```java
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
      String userDir = System.getProperty("user.dir");
      String filePath = userDir + File.separator + "src/main/resources/application.yml";
      List<String> allLines = Files.readAllLines(Paths.get(filePath));
      System.out.println(String.join("\n", allLines));
  }
}
```



### 3.4 写文件，植入病毒

可以直接向服务器上写入文件。

比如一个木马程序：`java -version 2>&1`（示例命令）

1. `java -version` 用于显示 Java 版本信息。这会将版本信息输出到标准错误流（stderr）而不是标准输出流（stdout）。
2. `2>&1` 将标准错误流重定向到标准输出流。这样，Java 版本信息就会被发送到标准输出流。

```java
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        String userDir = System.getProperty("user.dir");
        String filePath = userDir + File.separator + "src/main/resources/木马程序.bat";
        String errorProgram = " java -version 2>&1";
        Files.write(Paths.get(filePath), Arrays.asList(errorProgram));
        System.out.println("执行异常程序成功");
    }
}
```



### 3.5 运行其他程序

直接通过Process执行危险程序或者电脑上的其他程序

```java
public static void main(String[] args) throws InterruptedException, IOException {
    String userDir = System.getProperty("user.dir");
    String filePath = userDir + File.separator + "src/main/resources/木马程序.bat";
    Process process = Runtime.getRuntime().exec(filePath);
    process.waitFor();
    // 分批获取进程的正常输出
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    // 逐行读取
    String compileOutputLine;
    while ((compileOutputLine = bufferedReader.readLine()) != null) {
        System.out.println(compileOutputLine);
    }
    System.out.println("执行异常程序成功");
}
```



### 3.6 执行高危操作

甚至都不用写木马文件，直接执行系统自带的危险命令！

- 比如删除服务器的所有文件（太残暴，不要轻易学习）
- 比如执行 dir（windows）、ls（linux） 获取你系统上的所有文件信息



## 四、Java程序安全管理

针对上面的异常情况，分别有如下方案，可以提高程序安全性：

### 4.1 超时控制

创建一个守护线程，超时后自动终止Process线程

```java
// 超时控制
new Thread(() -> {
    try {
        Thread.sleep(TIME_OUT);
        System.out.println("程序运行超时，已经中断");
        runProcess.destroy();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}).start();
```



### 4.2 限制给用户程序分配的资源

我们不能让每个 java 进程的执行占用的 JVM 最大堆内存空间都和系统默认的一致，实际上应该更小（执行用户的题目代码也不需要这么多），比如说 256MB。在启动 Java 程序时，可以指定 JVM 的参数：-Xmx256m（最大堆空间大小）

> 注意！-Xmx 参数、JVM 的堆内存限制，不等同于系统实际占用的最大资源，可能会超出。 
>
> 如果需要更严格的内存限制，要在系统层面去限制，而不是 JVM 层面的限制。
>
> 如果是 Linux 系统，可以使用 cgroup 来实现对某个进程的 CPU、内存等资源的分配。

#### 4.2.1 cgroup简介

`cgroup`是 Linux 内核提供的一种机制，可以用来限制进程组（包括子进程）的资源使用，例如内存、CPU、磁盘 I/O 等。通过将 Java 进程放置在特定的 `cgroup`中，可以实现限制其使用的内存和 CPU 数。

#### 4.2.2 常用的JVM启动参数

1. 内存相关参数：

- -  `-Xms`: 设置 JVM 的初始堆内存大小
    - `-Xmx`: 设置 JVM 的最大堆内存大小
    -  `-Xss`: 设置线程的栈大小
    - `-XX:MaxMetaspaceSize`: 设置 Metaspace（元空间）的最大大小
    -  `-XX:MaxDirectMemorySize`: 设置直接内存（Direct Memory）的最大大小。

2. 垃圾回收相关参数

- -  `-XX:+UseSerialGC`: 使用串行垃圾回收器
    -  `-XX:+UseParallelGC`: 使用并行垃圾回收器
    - `-XX:+UseConcMarkSweepGC`: 使用 CMS 垃圾回收器
    -  `-XX:+UseG1GC`: 使用 G1 垃圾回收器

3. 线程相关参数

- -  `-XX:ParallelGCThreads`: 设置并行垃圾回收的线程数
    - `-XX:ConcGCThreads`: 设置并发垃圾回收的线程数
    - `-XX:ThreadStackSize`: 设置线程的栈大小

4. JIT 编译器相关参数

- - `-XX:TieredStopAtLevel`: 设置 JIT 编译器停止编译的层次

5. 其他资源限制参数

- - `-XX:MaxRAM`: 设置 JVM 使用的最大内存

### 4.3 限制代码 - 黑白名单

#### 4.3.1 实现

先定义一个黑白名单，比如哪些操作是禁止的，可以是一个列表：

```java
// 黑名单
public static final List<String> blackList = Arrays.asList("Files", "exec");
```

还可以使用字典树代替列表存储单词，用 **更少的空间** 存储更多的敏感词汇，并且实现**更高效**的敏感词查找。



#### 4.3.2 字典树

字典树（Trie树，也称为前缀树）是一种树形数据结构，用于高效地存储和搜索字符串集合。

字典树的**基本原理**：

1. 字典树单根节点的树形结构，根节点不包含任何字符信息
2. 每个节点都包含一个存储的字符和子节点的列表或映射
3. 从根节点到任意一个节点的路径上的字符连接起来就组成该节点对应的字符串
4. 插入操作：将一个字符串的每个字符依次插入到树中的节点上
5. 查询操作：从树的根节点开始，按照要查询的字符顺序，逐级匹配字符并向下遍历树
6. 终止节点：可以用一个标志来标记某个节点是否为一个字符串的终止节点，比如一个布尔变量

字典树的**主要优点**： 

1. 高效地存储和查找字符串集合，特别适合处理大量字符串的前缀匹配和前缀搜索
2. 提供了最长公共前缀的查找能力
3. 可以快速地查找指定前缀的所有字符串

总结来说，字典树是一种高效的数据结构，用于存储和搜索字符串集合。通过利用字符串的共同前缀，字典树可以大大提高字符串存储和检索的效率。



示例：

现在包含有Carrot、Car、Can三个违禁词：

若采用List存储，需要12字符的存储空间

![image-20230919222046785](assets/image-20230919222046785.png)

若采用字典树，能够节约空间

![image-20230919222401210](assets/image-20230919222401210.png)

此处引入Hutool工具库中的字典树工具类：WordTree

1）初始化字典数，插入禁用词

```java
private static final WordTree WORD_TREE;

static {
    // 初始化字典树
    WORD_TREE = new WordTree();
    WORD_TREE.addWords(blackList);
}
```

2）检测用户代码是否包含敏感词

```java
// 校验代码是否包含有黑名单中命令
FoundWord foundWord = WORD_TREE.matchWord(code);
if (foundWord != null) {
    System.out.println("此文件包含敏感词：" + foundWord.getFoundWord());
    return null;
}
```



**缺点**

1. 无法遍历所有的黑名单
2. 不同的编程语言，你对应的领域、关键词都不一样，限制人工成本很大



### 4.4 限制用户的操作权限（文件、网络、执行等）

目标：限制用户对文件、内存、CPU、网络等资源的操作和访问

#### 4.4.1 Java安全管理器使用

Java 安全管理器（Security Manager）是 Java 提供的保护 JVM、Java 安全的机制，可以实现更严格的资源和操作限制。

编写安全管理器，只需要继承 SecurityManager。

实际情况下，不应该在主类（开发者自己写的程序）中做限制，只需要限制子程序的权限即可。启动子进程执行命令时，设置安全管理器，而不是在外层设置（会限制住测试用例的读写和子命令的执行）。

具体操作如下：

1. 根据需要开发自定义的安全管理器（比如 `UserCodeSecurityManager`）
2. 复制 `UserCodeSecurityManager` 类到 `resources/security`目录下，**移除类的包名**
3. 手动输入命令编译 `UserCodeSecurityManager`类，得到 `class`文件
4. 在运行 java 程序时，指定安全管理器 class 文件的路径、安全管理器的名称。

```java
private static final String SECURITY_MANAGER_CLASS_PATH = "/Users/codejuzi/Documents/CodeWorkSpace/Projects/JuOj/code-sandbox/src/main/resources/security";

private static final String SECURITY_CLASS_NAME = "UserCodeSecurityManager";

// 3、执行代码
// 此处mac下是使用 : 分割不同类，windows下是使用 ; 分割不同类名
String runCmdPattern = "java -Dfile.encoding=UTF-8 -cp %s:%s -Djava.security.manager=%s Main %s";
List<String> inputList = executeCodeRequest.getInputList();
List<ExecuteMessage> executeMessageList = new ArrayList<>();
for (String inputArgs : inputList) {
  String runCmd = String.format(runCmdPattern, userCodeParentPath,
                                SECURITY_MANAGER_CLASS_PATH, SECURITY_CLASS_NAME, inputArgs);
  // ...
}
```



#### 4.4.2 安全管理器优点

1. 权限控制灵活
2. 实现简单

#### 4.4.3 安全管理器缺点

1. 如果要做比较严格的权限限制，需要自己去判断哪些文件、包名需要允许读写。粒度太细了，难以精细化控制。
2. 安全管理器本身也是 Java 代码，也有可能存在漏洞。本质上还是程序层面的限制，没深入系统的层面。



### 4.5 运行环境隔离(Docker代码沙箱实现)

原理：操作系统层面上，把用户程序封装到沙箱里，和宿主机（PC主机 / 服务器）隔离开，使得用户的程序无法影响宿主机。

实现方式：Docker 容器技术（底层是用 cgroup、namespace 等方式实现的），也可以直接使用 cgroup 实现。



# 代码沙箱Docker实现

## 一、Docker容器技术

为什么要使用Docker容器技术？

**为了进一步提升系统的安全性，把不同的程序和宿主机进行隔离，使得某个程序（应用）的执行不会影响到系统本身。**

Docker技术可以实现程序和宿主机的隔离。

### 1.1 什么是容器？

理解为对一系列应用程序、服务和环境的封装，从而把程序运行在一个隔离的、密闭的、隐私的空间内，对外整体提供服务。

> 可以把一个容器理解为一个新的电脑（定制化的操作系统）



### 1.2 Docker基本概念

**镜像image**：用来创建容器的安装包，可以理解为给电脑安装操作系统的系统镜像

**容器Container**：通过镜像来创建一套运行环境，一个容器里可以运行多个程序，可以理解为一个电脑实例

**Dockerfile**：制作镜像的文件，可以理解为制作镜像的一个清单

**镜像仓库**：存放镜像的仓库，用户可以从仓库下载现成的镜像，也可以把做好的容器放到仓库内（推荐使用Docker官方的镜像仓库DockerHub：https://hub.docker.com/)

![](assets/docker-relations.png)



### 1.3 Docker实现核心

![image-20230920192031343](assets/image-20230920192031343.png)

> Docker可以实现上述资源的隔离

1）Docker运行在Linux内核上

2）CGroups：实现了容器的资源隔离，底层是Linux cgoup命令，能够控制进程使用的资源

3）Network：实现容器的网络隔离，Docker容器内部的网络互不影响

4）Namespaces：可以把进程隔离在不同的命名空间下，每个容器都可以有自己的命名空间，不同命名空间下的进程互不影响

5）Storage：容器内的文件是相互隔离的，也可以去使用宿主机的文件

Docker Compose：是一种同时启动多个容器的集群操作工具（容器管理工具），一般情况下，开发者仅做了解，实际使用Docker Compose时Copy配置文件即可。



### 1.4 安装Docker

参照Docker官网：https://docs.docker.com/desktop/install/linux-install/



### 1.5 命令行操作Docker

1）查看命令用法：

```sh
docker --help
# 查看具体子命令的用法，比如docker run 命令
docker run --help
```



2）从远程仓库拉取现成的镜像

```sh
docker pull [OPTIONS] NAME[:TAG|@DIGEST]
```



3）根据镜像创建容器实例

启动实例，得到容器实例containerId

```sh
docker create [OPTIONS] IMAGE [COMMAND] [ARG...]

sudo docker create hello-world
```

4）查看所有容器状态（包括未启动的）

```sh
sudo docker ps -a
```

5）启动容器

```sh
docker start [OPTIONS] CONTAINER [CONTAINER...]
```

6）查看容器日志

```sh
docker logs [OPTIONS] CONTAINER
```

7）删除容器

```sh
docker rm [OPTIONS] CONTAINER [CONTAINER...]
```

8）删除镜像：

删除镜像之前要删除掉使用到此镜像的容器，或者停止运行使用此镜像的容器

```sh
docker rmi [OPTIONS] IMAGE [IMAGE...]
```

9） 其他：构建镜像（build）、推送镜像（push）、运行容器（run）、执行容器命令（exec）等 



## 二、Java操作Docker

### 2.1 前置准备

使用 Docker-Java：https://github.com/docker-java/docker-java

官方入门：https://github.com/docker-java/docker-java/blob/main/docs/getting_started.md

引入依赖：

```xml
<!-- https://mvnrepository.com/artifact/com.github.docker-java/docker-java -->
<dependency>
    <groupId>com.github.docker-java</groupId>
    <artifactId>docker-java</artifactId>
    <version>3.3.0</version>
</dependency>
<!-- https://mvnrepository.com/artifact/com.github.docker-java/docker-java-transport-httpclient5 -->
<dependency>
    <groupId>com.github.docker-java</groupId>
    <artifactId>docker-java-transport-httpclient5</artifactId>
    <version>3.3.0</version>
</dependency>
```

`DockerClientConfig`：用于定义初始化 DockerClient 的配置（类比 MySQL 的连接、线程数配置)

`DockerHttpClient`（不推荐使用）：用于向 Docker 守护进程（操作 Docker 的接口）发送请求的客户端，低层封装，还要自己构建请求参数（简单地理解成 JDBC）

`DockerClient`（推荐）：才是真正和 Docker 守护进程交互的、最方便的 SDK，高层封装，对 `DockerHttpClient`再进行了一层封装（理解成 MyBatis），提供了现成的增删改查

### 2.2 常用操作

1）创建DockerClient

> 参考https://juejin.cn/post/7172149049284362247#heading-15

```java
DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(DockerProperties.DOCKER_HOST)
                .withApiVersion(DockerProperties.API_VERSION)
                .build();
DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();
```



2）拉取镜像

```java
// 1、拉取镜像
String image = "nginx:stable";
PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
try {
  PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
    @Override
    public void onNext(PullResponseItem item) {
      System.out.println("Pull Image => " + item.getStatus());
      super.onNext(item);
    }
  };
  pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
  System.out.println("拉取完成");
} catch (InterruptedException e) {
  throw new RuntimeException(e);
}
```

3）创建容器

```java
// 2、创建容器
CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
CreateContainerResponse response = containerCmd.withCmd("echo", "Hello Nginx").exec();
String containerId = response.getId();
System.out.println("Container Id: " + containerId);
```



4）查看容器状态

```java
// 3、查看容器状态
ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
List<Container> containerList = listContainersCmd.withShowAll(true).exec();
for (Container container : containerList) {
  System.out.println(container);
}
```

5）启动容器

```java
// 4、启动容器
dockerClient.startContainerCmd(containerId).exec();
```

6）查看容器日志

```java
// 5、查看启动容器日志
LogContainerResultCallback resultCallback = new LogContainerResultCallback() {
  @Override
  public void onNext(Frame item) {
    System.out.println(containerId + " 此容器日志:" + new String(item.getPayload()));
    super.onNext(item);
  }
};
dockerClient.logContainerCmd(containerId)
  .withStdErr(true) // 错误输出
  .withStdOut(true) // 标准输出
  .exec(resultCallback)
  .awaitCompletion(); // 异步操作
```

7）删除容器

```java
// 6、删除容器
dockerClient.removeContainerCmd(containerId)
  .withForce(true) // 强制删除
  .exec();

// 删除所有容器
for (Container container : containerList) {
  if (container.getId() != null) {
    dockerClient.removeContainerCmd(container.getId())
      .withForce(true) // 强制删除
      .exec();
  }
}
```

8）删除镜像

```java
// 7、删除镜像
dockerClient.removeImageCmd(image).exec();
```



## 三、Docker实现代码沙箱

实现思路：Docker负责运行Java程序，并且得到结果

流程几乎和Java原生代码沙箱实现一致：

1. 把用户的代码保存为文件
2. 编译代码，得到 class 文件
3. 把编译好的文件上传到容器环境内
4. 在容器中执行代码，得到输出结果
5. 收集整理输出结果
6. 文件清理，释放空间
7. 错误处理，提升程序健壮性

> 模板方法模式：定义同一套实现流程，让不同的子类去负责不同流程中的具体实现，执行步骤一样，每个步骤的实现方式不一样。



### 3.1 创建容器，上传编译文件

自定义容器的两种方式：

1. 在已有镜像的基础上再扩充，比如拉取现成的Java镜像，再把编译后的文件复制到容器里。（适合新项目，跑通流程）
2. 完全自定义容器：适合比较成熟的项目，比如封装多个语言的环境和实现

```java
String image = "openjdk:8-alpine";
if (FIRST_INIT) { // true
    PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
    try {
      PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
        @Override
        public void onNext(PullResponseItem item) {
          log.info("Pull Image => " + item.getStatus());
          super.onNext(item);
        }
      };
      pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
      log.info("Pull Image Succeed!");
      FIRST_INIT = false;
    } catch (InterruptedException e) {
      log.error("Pull Image Failed!");
      throw new RuntimeException(e);
    }
}
```

思考：每个测试用例都单独创建一个容器，每个容器只执行一次 java 命令？

这是很浪费性能，所以要创建一**可交互**的容器，能接受多次输入并且输出。

创建容器时，可以指定文件路径（Volumn） 映射，作用是把本地的文件同步到容器中，可以让容器访问。

```java
// 3.2 创建容器
CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
// 创建容器配置
HostConfig hostConfig = new HostConfig();
// 限制内存
hostConfig.withMemory(100 * 1000 * 1000L);
// 设置CPU核数
hostConfig.withCpuCount(1L);
// 设置容器挂载目录
hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app/code")));
CreateContainerResponse response = containerCmd
  .withHostConfig(hostConfig)
  // 开启输入输出
  .withAttachStderr(true)
  .withAttachStdin(true)
  .withAttachStdout(true)
  // 开启交互终端
  .withTty(true)
  .exec();

String containerId = response.getId();
log.info("container id: {}", containerId);
```



### 3.2 启动容器，执行代码

#### 3.2.1 执行代码



Docker执行容器命令（操作已启动的容器）：

```sh
docker exec [OPTIONS] CONTAINER COMMAND [ARG...]
```

> 注意：要把命令按照空格拆分，作为一个数组传递，否则可能会被识别为一个字符串，而不是多个参数。

创建命令：

```java
String[] inputArgsArr = inputArgs.split(" ");
String[] cmdArr = ArrayUtil.append(new String[]{"java", "-cp", "/app/code", "Main"}, inputArgsArr);
ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
  .withCmd(cmdArr)
  .withAttachStderr(true)
  .withAttachStdin(true)
  .withAttachStdout(true)
  .exec();
```

执行命令，通过回调接口来获取程序的输出结果，并且通过 StreamType 来区分标准输出和错误输出。

```java
List<String> inputList = executeCodeRequest.getInputList();
for (String inputArgs : inputList) {
  String[] inputArgsArr = inputArgs.split(" ");
  String[] cmdArr = ArrayUtil.append(new String[]{"java", "-cp", "/app/code", "Main"}, inputArgsArr);
  ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
    .withCmd(cmdArr)
    .withAttachStderr(true)
    .withAttachStdin(true)
    .withAttachStdout(true)
    .exec();
  String execId = execCreateCmdResponse.getId();
  log.info("创建执行命令ID：{}", execId);

  final boolean[] isTimeOut = {true};
  if (execId == null) {
    throw new RuntimeException("执行命令不存在");
  }
  ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
    @Override
    public void onNext(Frame frame) {
      // 获取程序执行信息
      StreamType streamType = frame.getStreamType();
      if (StreamType.STDERR.equals(streamType)) {
        errorDockerMessage[0] = new String(frame.getPayload());
        log.error("错误输出结果：{}", errorDockerMessage[0]);
      } else {
        dockerMessage[0] = new String(frame.getPayload());
        log.info("输出结果：{}", dockerMessage[0]);
      }
      super.onNext(frame);
    }

    @Override
    public void onComplete() {
      // 设置不超时
      isTimeOut[0] = false;
      super.onComplete();
    }
  };
  try {
    // 执行启动命令
    dockerClient.execStartCmd(execId).exec(execStartResultCallback).awaitCompletion();
  } catch (InterruptedException e) {
    throw new RuntimeException(e);
  }
```

> 尽量复用之前的 ExecuteMessage 对象，在异步接口中填充正常和异常信息，这样之后流程的代码都可以复用。

#### 3.2.2 获取程序执行时间

使用StopWatch统计前后执行时间

```java
// 执行启动命令
stopWatch.start();
dockerClient.execStartCmd(execId)
  .exec(execStartResultCallback)
  .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
stopWatch.stop();
// 获取总时间
time = stopWatch.getLastTaskTimeMillis();
```



#### 3.2.3 获取程序占用内存

程序占用的内存每个时刻都在变化，所以不可能获取到所有时间点的内存。

定义一个周期，定期地获取程序的内存。

Docker-Java 提供了内存定期统计的操作:

```java
StatsCmd statsCmd = dockerClient.statsCmd(containerId);
ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
  @Override
  public void onStart(Closeable closeable) {
  }

  @Override
  public void onNext(Statistics statistics) {
    Long usageMemory = Optional.ofNullable(statistics.getMemoryStats().getUsage()).orElse(0L);
    maxMemory[0] = Math.max(usageMemory, maxMemory[0]);
    log.info("内存占用：{}", usageMemory);
  }

  @Override
  public void onError(Throwable throwable) {
  }

  @Override
  public void onComplete() {
  }

  @Override
  public void close() {

  }
});
statsCmd.exec(statisticsResultCallback);
```

注意，程序执行完后要关闭统计命令，统计完时间后要关闭：

```java
statsCmd.close();
```

### 四、Docker容器安全性

### 4.1 超时控制

执行容器时，可以增加超时参数控制值：

但是，这种方式无论超时与否，都会往下执行，无法判断是否超时。

解决方案：可以定义一个标志，如果程序执行完成，把超时标志设置为 false。

```java
@Override
public void onComplete() {
  // 设置不超时
  isTimeOut[0] = false;
  super.onComplete();
}


// 执行启动命令
stopWatch.start();
dockerClient.execStartCmd(execId)
  .exec(execStartResultCallback)
  .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
stopWatch.stop();
```



### 4.2 内存资源

通过`HostConfig`的`withMemory`等方法，设置容器的最大内存和资源限制：

```java
// 3.2 创建容器
CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
// 创建容器配置
HostConfig hostConfig = new HostConfig();
// 限制内存
hostConfig.withMemory(100 * 1000 * 1000L);
hostConfig.withMemorySwap(0L);
// 设置CPU核数
hostConfig.withCpuCount(1L);
// 设置容器挂载目录
hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app/code")));
CreateContainerResponse response = containerCmd
  .withHostConfig(hostConfig)
  // 开启输入输出
  .withAttachStderr(true)
  .withAttachStdin(true)
  .withAttachStdout(true)
  // 开启交互终端
  .withTty(true)
  .exec();
```

### 4.3 网络资源

创建容器时，设置网络配置为关闭：

```java
CreateContainerResponse response = containerCmd
                .withHostConfig(hostConfig)
                // 禁用网络
                .withNetworkDisabled(true)
  // ...
```

### 4.4 权限管理

Docker 容器已经做了系统层面的隔离，比较安全，但不能保证绝对安全。

1.  结合 Java 安全管理器和其他策略去使用 

2. 限制用户不能向 root 根目录写文件

    ```java
    hostConfig.withReadonlyRootfs(true);
    ```



3.  Linux 自带的一些安全管理措施，比如 seccomp（Secure Computing Mode）是一个用于 Linux 内核的安全功能，它允许限制进程可以执行的系统调用，从而减少潜在的攻击面和提高容器的安全性。通过配置 seccomp，可以控制容器内进程可以使用的系统调用类型和参数。

示例：seccomp 配置文件 security_config.json

```json
{
  "defaultAction": "SCMP_ACT_ALLOW",
  "syscalls": [
    {
      "name": "write",
      "action": "SCMP_ACT_ALLOW"
    },
    {
      "name": "read",
      "action": "SCMP_ACT_ALLOW"
    }
  ]
}
```

```java
// 开启Linux安全配置
String linuxSecurityConfig = ResourceUtil.readUtf8Str("linux/security_config.json");
hostConfig.withSecurityOpts(Collections.singletonList("seccomp=" + linuxSecurityConfig));
```

# 模板方法模式优化代码沙箱

模板方法：定义一套通用的执行流程，让子类负责每个执行步骤的具体实现

模板方法的适用场景：适用于有规范的流程，且执行流程可以复用

作用：大幅节省重复代码量，便于项目扩展、更好维护

**模板**：

```java
@Override
public ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest) {
  List<String> inputList = executeCodeRequest.getInputList();
  String code = executeCodeRequest.getCode();
  // todo 考虑不同的language
  //        String language = executeCodeRequest.getLanguage();

  File userCodeFile = null;
  ExecuteCodeResponse response = null;
  try {
    // 1、保存文件
    userCodeFile = save2File(code);

    // 2、编译代码
    ExecuteMessage executeMessage = compileCode(userCodeFile);
    log.info("Compile Code: {}", executeMessage);

    // 3、执行代码
    List<ExecuteMessage> executeMessageList = runCode(userCodeFile, inputList);

    // 4、获取输出
    response = getOutputResponse(executeMessageList);

  } catch (Exception e) {
    log.error("发生异常：", e);
    // 处理异常
    return handleError(e);
  } finally {
    // 5、清理文件
    assert userCodeFile != null;
    clearFile(userCodeFile);
  }

  return response;
}
```



# 代码沙箱向外界暴露API

## 一、调用安全性考虑

需要考虑调用安全性：

如果将服务不做任何的权限校验，直接发到公网，是不安全的。



1）调用方与服务提供方之间约定一个字符串 **（最好加密）**

优点：实现最简单，比较适合内部系统之间相互调用（相对可信的环境内部调用）

缺点：不够灵活，如果 key 泄露或变更，需要重启代码

代码沙箱服务，先定义约定的字符串：

```java
/**
 * 定义鉴权请求头和密钥
 *
 * @author codejuzi
 */
public interface AuthRequest {
  // 请求头
  String AUTH_REQUEST_HEADER = "auth";

  // 密钥
  String AUTH_REQUEST_SECRET = "secret_key_code_sandbox";
}
```

暴露接口时判断：

```java
@RestController
@RequestMapping("/")
public class CodeSandboxController {

    @Resource
    private JavaNativeCodeSandbox codeSandbox;

    /**
     * 执行代码接口
     *
     * @param executeCodeRequest 请求
     * @return response
     */
    @PostMapping("/exec_code")
    public ExecuteCodeResponse execCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(AUTH_REQUEST_HEADER);

        if (!AUTH_REQUEST_SECRET.equals(authHeader)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        if (executeCodeRequest == null) {
            return null;
        }
        return codeSandbox.execute(executeCodeRequest);
    }
}
```

调用方，在调用时补充请求头：

```java
public class RemoteCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest) {
        String url = "http://localhost:8888/exec_code";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String respStr;
        try (HttpResponse response = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()) {
            respStr = response.body();
        }
        if (StrUtil.isBlank(respStr)) {
            throw new BusinessException(StatusCode.API_REQUEST_ERROR, "executeCode remoteSandbox error, message = " + respStr);
        }

        return JSONUtil.toBean(respStr, ExecuteCodeResponse.class);
    }
}
```

2）API 签名认证

给允许调用的人员分配 accessKey、secretKey，然后校验这两组 key 是否匹配



## 二、消息队列解耦

此处选用消息队列（RabbitMQ）改造优化项目，解耦判题服务和题目服务，题目服务只需要向消息队列发送消息，判题服务从消息队列中取消息去执行判题，然后异步更新数据库即可。

### 2.1 MQ构建

1）引入依赖

```java
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```



2）添加配置

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```



3）创建交换机和队列（包括死信队列）

```java
public interface OjMQConstant {

    // region common
    String OJ_EXCHANGE_NAME = "oj_exchange";
    String OJ_QUEUE_NAME = "oj_queue";
    String OJ_ROUTING_KEY = "oj_question_judge";
    String OJ_DIRECT_EXCHANGE = "direct";

    // endregion


    // region dlx
    String OJ_DLX_EXCHANGE = "oj_dlx_exchange";
    String OJ_DLX_QUEUE = "oj_dlx_queue";
    String OJ_DLX_ROUTING_KEY = "oj_dlx_question_judge";

    // endregion

}

/**
 * 创建测试程序用例的交换机和队列
 *
 * @author codejuzi
 */
@Slf4j
public class OjMQInitTask {

    public static void doInitMQ() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        // TODO 配置远程服务MQ信息
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        try (Connection connection = connectionFactory.newConnection()) {
            Channel channel = connection.createChannel();
            // 创建交换机
            channel.exchangeDeclare(OJ_EXCHANGE_NAME, OJ_DIRECT_EXCHANGE);

            // 创建队列，绑定死信交换机
            Map<String, Object> queueArgs = new HashMap<>();
            queueArgs.put("x-dead-letter-exchange", OJ_DLX_EXCHANGE);
            queueArgs.put("x-dead-letter-routing-key", OJ_DLX_ROUTING_KEY);
            channel.queueDeclare(OJ_QUEUE_NAME, true, false, false, queueArgs);

            // 队列绑定交换机
            channel.queueBind(OJ_QUEUE_NAME, OJ_EXCHANGE_NAME, OJ_ROUTING_KEY);

            // 创建死信交换机、死信队列，绑定二者
            channel.exchangeDeclare(OJ_DLX_EXCHANGE, OJ_DIRECT_EXCHANGE);
            channel.queueDeclare(OJ_DLX_QUEUE, true, false, false, null);
            channel.queueBind(OJ_DLX_QUEUE, OJ_DLX_EXCHANGE, OJ_DLX_ROUTING_KEY);

            log.info("MQ Init Successful!");

        } catch (Exception e) {
            log.error("MQ Init Failed!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        doInitMQ();
    }
}
```

项目启动前调用一次即可，不需要重复执行。



4）生产者

```java
@Component
public class OjMQProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 生产者发送消息
     *
     * @param exchange   交换机名称
     * @param routingKey 路由键
     * @param message    消息体
     */
    public void sendMessage(String exchange, String routingKey, String message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
```

> 生产者发送的消息是 questionSubmitInfoId，在消费端执行判题



5）消费者

```java
@Slf4j
@Component
public class OjMQConsumer {

    @Resource
    private JudgeService judgeService;

    @Resource
    private QuestionSubmitInfoService questionSubmitInfoService;

    @Resource
    private QuestionService questionService;

    /**
     * 消费者接收并消费消息，如果消费失败，进入死信队列
     *
     * @param message     消息体
     * @param channel     channel
     * @param deliveryTag 消息Tag（ID）
     */
    @SneakyThrows
    @RabbitListener(
            queues = {OJ_QUEUE_NAME},
            ackMode = "MANUAL"
    )
    public void receiveMessage(String message, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) {

        try {
            log.info("MQ Consumer Receive Message: {}", message);

            if (message == null) {
                // 消息为空，报错
                channel.basicAck(deliveryTag, false);
                throw new BusinessException(StatusCode.PARAMS_ERROR, "消息为空");
            }

            Long questionSubmitInfoId = Long.parseLong(message);
            // 判题服务
            judgeService.doJudge(questionSubmitInfoId);
            // 获取判题结果
            QuestionSubmitInfo submitInfo = questionSubmitInfoService.getById(questionSubmitInfoId);
            String judgeInfoJson = submitInfo.getJudgeInfo();
            JudgeInfo judgeInfo = JSONUtil.toBean(judgeInfoJson, JudgeInfo.class);

            String judgeInfoMessage = judgeInfo.getMessage();
            if (!JudgeInfoMessageEnum.ACCEPTED.getValue().equals(judgeInfoMessage)) {
                // 说明提交的代码有误
                channel.basicAck(deliveryTag, false);
                return;
            }

            Long questionId = submitInfo.getQuestionId();
            Question question = questionService.getById(questionId);

            // TODO: 2023/9/22 考虑是否需要加重量级锁，还是允许一定的误差
            LambdaUpdateWrapper<Question> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Question::getId, questionId)
                    .set(Question::getAcNum, question.getAcNum() + 1);
            boolean updateRes = questionService.update(updateWrapper);
            if (!updateRes) {
                throw new BusinessException(StatusCode.SYSTEM_ERROR, "数据保存失败");
            }

            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            // 消息接收失败，发送到死信队列
            channel.basicNack(deliveryTag, false, false);
        }

    }
}
```



5）死信队列

```java
@Slf4j
@Component
public class OjMQDlxConsumer {

    @Resource
    private QuestionSubmitInfoService questionSubmitInfoService;

    @SneakyThrows
    @RabbitListener(
            queues = {OJ_DLX_QUEUE},
            ackMode = "MANUAL"
    )
    public void receiveMessage(String message, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) {
        log.info("Dlx Queue Receive Message: {}", message);

        Long questionSubmitInfoId = Long.parseLong(message);
        QuestionSubmitInfo submitInfo = questionSubmitInfoService.getById(questionSubmitInfoId);
        if (submitInfo == null) {
            channel.basicAck(deliveryTag, false);
            throw new BusinessException(StatusCode.PARAMS_ERROR, "提交的题目信息不存在");
        }

        LambdaUpdateWrapper<QuestionSubmitInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(QuestionSubmitInfo::getId, questionSubmitInfoId)
                .set(QuestionSubmitInfo::getSubmitState, QuestionSubmitStatusEnum.FAILED.getValue());
        boolean updateRes = questionSubmitInfoService.update(updateWrapper);
        if (!updateRes) {
            log.info("Dlx Consumer Message {} Failed!", questionSubmitInfoId);
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "Error");
        }
        // 手动确认消息
        channel.basicAck(deliveryTag, false);
    }

}
```



### 2.2 项目异步化改造

要传递的消息：题目提交信息ID

在题目提交服务中，把原本本地异步执行的判题逻辑改造成生产者发送消息

```java
// 改成MQ解耦
@Resource
private OjMQProducer ojMQProducer;
//    @Resource
//    private JudgeService judgeService;
//
//    /**
//     * 判题线程池
//     */
//    private final Executor JUDGE_EXECUTOR_POOL = new ThreadPoolExecutor(2, 4, 10000, TimeUnit.MILLISECONDS,
//            new LinkedBlockingQueue<>(100));

@Override
public Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
  // ...

  Long submitInfoId = submitInfo.getId();

  // 执行判题服务
  //        CompletableFuture.runAsync(() -> judgeService.doJudge(submitInfoId), JUDGE_EXECUTOR_POOL);

  // 发送MQ消息
  ojMQProducer.sendMessage(OJ_EXCHANGE_NAME, OJ_ROUTING_KEY, String.valueOf(submitInfoId));

  return submitInfoId;
}
```



# 单体改造为微服务



## 一、什么是微服务

服务：提供某类功能的代码

微服务：专注于提供某类特定功能的代码，而不是把所有的代码全部放到同一个项目里，会把整个大的项目按照一定的功能、逻辑进行拆分，拆分为多个子模块，每个子模块可以独立运行、独立负责一类功能，子模块之间相互调用、互不影响。

微服务的几个重要的实现因素：服务管理、服务调用、服务拆分



## 二、微服务实现技术

Spring Cloud

Spring Cloud Alibaba

Dubbo (DubboX)

RPC (gRPC, tRPC)



本质上都是通过HTTP，或者其他的网络协议进行通讯来实现的。



### 三、Spring Cloud Alibaba

>  https://github.com/alibaba/spring-cloud-alibaba

中文文档：https://sca.aliyun.com/zh-cn/

本质是在Spring Cloud的基础上，进行了增强，补充了一些额外的能力：

1. Spring Cloud Gateway：网关
2. Nacos：服务注册和配置中心
3. Sentinel：熔断限流
4. Seata：分布式事务
5. RocketMQ：消息队列



## 四、改造前思考

> 从业务需求出发，思考单机和分布式的区别

用户登录改造为分布式登录

单机锁 => 分布式锁

本地缓存 => 分布式缓存

本地事务 => 分布式事务

### 4.1 改为分布式登录

1）补充依赖

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.session</groupId>
  <artifactId>spring-session-data-redis</artifactId>
</dependency>
```

2）配置文件增加Redis配置以及修改Session存储方式

```yaml
spring:
  session:
    timeout: 86400
    store-type: redis
  redis:
    port: 6379
    host: ${redis.dev.host}
    database: 0
```

### 4.2 微服务的划分

依赖服务：

- 注册中心：Nacos
- 微服务网关(oj-backend-gateway, 8104)：Gateway聚合所有的接口，统一接受、处理前端的请求

公共服务：

- 公共模块(oj-backend-common)：全局异常处理器、请求响应封装类、公用的工具类等
- 模型模块(oj-backend-model)：服务公用的实体类
- 公用接口模块(oj-backend-service-client)：只存放接口，不存放实现



代码沙箱(oj-code-sandbox, 8081)：判题，得到判题结果

> 代码沙箱服务本身就是独立的，不需要纳入Spring Cloud管理



业务功能：

1）题目模块(oj-backend-user-service, 8105)

管理员：

1. 创建题目
2. 删除题目
3. 修改题目

用户：

1. 搜索题目
2. 在线做题
3. 提交题目代码

2）用户模块 (oj-backend-question-service, 8106)

1. 注册
2. 登录
3. 用户管理（管理员）

3）判题模块 (oj-backend-judge-service, 8107)

1. 提交判题（结果是否正确）
2. 错误处理（内存溢出、安全性、超时）
3. 自主实现代码沙箱（安全沙箱）
4. 开放接口（提供一个独立的新服务）



### 4.3 路由划分

使用SpringBoot的context-path统一修改各服务的接口前缀：

`/inner` => 内部调用，网关层面要做限制

1. 用户服务：
    - `/api/user`
    - `/api/user/inner`
2. 题目服务
    - `/api/question`
    - `/api/question/inner`
3. 判题服务
    - `/api/judge`
    - `/api/judge/inner`







# 扩展

## 一、用户提交限流

此处使用Redisson实现限流，需要限制用户提交题目次数，每秒一次

1）引入依赖

```xml
<dependency>
  <groupId>org.redisson</groupId>
  <artifactId>redisson</artifactId>
  <version>3.25.3</version>
</dependency>
```

2）配置Redisson

```java
@Data
@Configuration
@ConfigurationProperties("spring.redis")
public class RedissonConfig {

    private Integer database;
    private String host;
    private Integer port;
//    private String password;


    @Bean
    public RedissonClient redissonClient() {
        Config redissonConfig = new Config();
        redissonConfig.useSingleServer()
                .setDatabase(database)
                .setAddress(String.format("redis://%s:%s", host, port));
//                .setPassword(password);
        return Redisson.create(redissonConfig);
    }
}
```



3）创建限流管理器

```java
@Component
@Slf4j
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 执行限流，设定每秒最多访问一次
     *
     * @param key 限流key
     * @return true - 可以操作
     */
    public boolean doRateLimit(String key) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);

        boolean rateRes = rateLimiter.trySetRate(RateType.OVERALL, 1, 1, RateIntervalUnit.SECONDS);
        if (rateRes) {
            log.info("init rate: {}, interval: {}", rateLimiter.getConfig().getRate(), rateLimiter.getConfig().getRateInterval());
        }

        // 每一个操作来，申请一个令牌
        return rateLimiter.tryAcquire(1);
    }
}
```



4）执行限流

```java
@PostMapping("/submit")
@ApiOperation(value = "题目提交")
public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                           HttpServletRequest request) {
  if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
    throw new BusinessException(StatusCode.PARAMS_ERROR);
  }
  // 限流
  final User loginUser = userService.getLoginUser(request);
  boolean limitRes = limiterManager.doRateLimit(loginUser.getId().toString());
  if (!limitRes) {
    throw new BusinessException(StatusCode.TOO_MANY_REQUEST, "提交过于频繁，请稍后重试");
  }
  return ResultUtils.success(questionSubmitInfoService.doQuestionSubmit(questionSubmitAddRequest, request));
}
```





# TODO

1. ~~实现判题机~~
2. ~~实现代码沙箱（原生JDK、Docker实现）~~
3. ~~编译、执行代码错误也要删除tmp_code文件~~
4. ~~消息队列削峰填谷、实现解耦，必须要做~~
5. 用户题目收藏关系
6. 微服务改造
7. 加入timeLimit参数，可以自行扩展，即时中断程序
8. 增加一个查看代码沙箱状态的接口
9. Java原生获取内存占用（可选）

