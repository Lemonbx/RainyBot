# RainyBot
基于mirai二次封装

### 快速开始

添加依赖：
```xml
<dependency>
    <groupId>com.luoyuer</groupId>
    <artifactId>RainyBot</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
添加mirai最新依赖
```xml
<dependency>
    <groupId>net.mamoe</groupId>
    <artifactId>mirai-core-jvm</artifactId>
    <version>LATEST</version>
</dependency>
```
添加编译插件
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.3</version>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <compilerArgs>
            <arg>-parameters</arg>
        </compilerArgs>
    </configuration>
</plugin>
```
如需打包运行，添加如下编译插件
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <configuration>
        <archive>
            <manifest>
                <mainClass>此处更换为你的Main所在类</mainClass>
            </manifest>
        </archive>
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
    </configuration>
    <executions>
        <execution>
            <id>make-assembly</id>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

在`resources`下新建`application.properties`文件，内容如下
```properties
qq.acc=此处填写账号
qq.pwd=此处密码，若扫码登录，不填
```

在主方法中编写如下代码
```java
ContextLoader.load(此处更换为Main所在类.class);
```

新建类`action.TestAction.java`,添加`@Bean`注解，添加如下方法：
```java
@Action("你好")
public String hello() {
    return "你好";
}
```

启动项目，根据日志打印的信息处理登录成功后，用另一qq发送`你好`,机器人返回`你好`，到此结束。
若登录失败，参考文档：[常见登录失败原因](https://docs.mirai.mamoe.net/Bots.html#%E5%B8%B8%E8%A7%81%E7%99%BB%E5%BD%95%E5%A4%B1%E8%B4%A5%E5%8E%9F%E5%9B%A0)

### 进阶

- 添加自定义扫描包

在ContextLoader.load之前执行如下代码：
```java
ContextLoader.addPackage("要添加的包");
```

- 自定义Bot类：
```java
ContextLoader.setBot(Bot.Instance....);
```
- 自定义启动banner

在resources下新建banner.txt，在里面写你要的banner

- 注册bean

在类或方法上标注`@Bean`。

- 注入bean

在变量或形参上标注`@Inject`。

- 在Action类标注@Action

会与方法上的@Action进行拼接，用空格分割。示例：
```java
@Action("测试")
@Bean
public class TestAction{
    @Action("你好")
    public String hello(){
        return "你好";
    }
}
```
只需要访问 `测试 你好` 即可

- 对话式访问

在方法中使用`WaitUtil.waitNextMessage` 即可等待用户的下一条信息。可多次使用。若超时未等待到，会抛出RuntimeException。

- 分多条消息发送

使用`MsgUtil.sendToSource`多次调用即可。

- 发送图片/语音

内置了`Aud`和 `Img` 类，构造完成后return或MsgUtil.sendToSource都可发送

- 自定义类型

新建一个消息类型类，在使用前调用`MessageConverter.register(类.class,(值)->处理器)`注册

- 接收用户变量，如 `北京天气`或`南京天气`

在@Action注解中添加变量， `@Action("{location}天气")`。添加形参`String location` 即可获取到北京或者南京

以上在`com.luoyuer.action.DefaultAction`中都有示例
