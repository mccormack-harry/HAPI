# HAPI
[<img src="https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fhazedev.me%2Fjenkins%2Fjob%2FHAPI%2F&label=build">](https://hazedev.me/jenkins/job/HAPI)
[<img src="https://img.shields.io/github/issues/haz8989/HAPI">](https://github.com/haz8989/HAPI/issues)
[<img src="https://img.shields.io/github/last-commit/haz8989/HAPI">](https://github.com/haz8989/HAPI/commits/master)
[<img src="https://img.shields.io/tokei/lines/github/haz8989/HAPI">](https://chrome.google.com/webstore/detail/github-gloc/kaodcnpebhdbpaeeemkiobcokcnegdki)
#### WARNING! HAPI is in a beta state and is not recommended for use in production servers, expect major changes in API!<br>
HAPI (HazeDev's API) is an API to assist in the creation of Spigot plugins.
This is targeted for developers who write a lot of features for a single server.
## Usage
[HapiExample](https://github.com/haz8989/HapiExample) is an example of a plugin created using HAPI, Feel free to clone it.<br>
You can add HAPI as a maven dependency in your poject:
```xml
<repository>
    <id>hazedev</id>
    <url>https://hazedev.me/jenkins/plugin/repository/everything/</url>
</repository>
```
```xml
<dependency>
    <groupId>me.hazedev</groupId>
    <artifactId>HAPI</artifactId>
    <version>1.1.3</version>
    <scope>compile</scope>
</dependency>
```
However you must include HAPI in your final Jar, you can configure the shade plugin to do that:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.2.4</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <minimizeJar>true</minimizeJar>
                        <createDependencyReducedPom>false</createDependencyReducedPom>
                        <artifactSet>
                            <includes>
                                <include>me.hazedev:HAPI</include>
                            </includes>
                        </artifactSet>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
## Features
- [Components](src/main/java/me/hazedev/hapi/component)
- [Command API](src/main/java/me/hazedev/hapi/command)
- [Config API](src/main/java/me/hazedev/hapi/config)
- [Log Utils](src/main/java/me/hazedev/hapi/logging)
- [Custom Events](src/main/java/me/hazedev/hapi/event) (Some require a component to be enabled e.g. FirstJoinEvent is called by JoinQuitHandler component)
- [QuestAPI](src/main/java/me/hazedev/hapi/quest)
- [UserDataManager](src/main/java/me/hazedev/hapi/userdata)
- [Statistics API](src/main/java/me/hazedev/hapi/stats)
- [Economy API](src/main/java/me/hazedev/hapi/economy) - _With Vault support_
- [Validation Utils](src/main/java/me/hazedev/hapi/validation) - _Mainly used by CommandHandlers_
- [Inventory/Item Utils](src/main/java/me/hazedev/hapi/inventory)
- [ChatUtils](src/main/java/me/hazedev/hapi/chat)
- [BossBarComponent](src/main/java/me/hazedev/hapi/bossbar)
- [Essential Components](src/main/java/me/hazedev/hapi/essentials)
