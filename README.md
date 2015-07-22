# ortolan-maven-plugin

Um plugin de testes para criar uma listagem das dependências do projetos em um arquivo com formato HTML.

## Modo de uso

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.ortolan.plugins</groupId>
                <artifactId>ortolan-maven-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <executions>
                    <execution>
                        <phase>install</phase>
                        <goals>
                            <goal>informacoesBasicas|informacoesSuperPom</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```