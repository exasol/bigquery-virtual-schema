<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                      | License          |
| ------------------------------- | ---------------- |
| [Virtual Schema Common JDBC][0] | [MIT License][1] |
| [error-reporting-java][2]       | [MIT License][3] |

## Test Dependencies

| Dependency                                      | License                                        |
| ----------------------------------------------- | ---------------------------------------------- |
| [Virtual Schema Common JDBC][0]                 | [MIT License][1]                               |
| [Hamcrest][4]                                   | [BSD-3-Clause][5]                              |
| [JUnit Jupiter Params][6]                       | [Eclipse Public License v2.0][7]               |
| [mockito-junit-jupiter][8]                      | [MIT][9]                                       |
| [Testcontainers :: JUnit Jupiter Extension][10] | [MIT][11]                                      |
| [Testcontainers :: JDBC][10]                    | [MIT][11]                                      |
| [exasol-test-setup-abstraction-java][12]        | [MIT License][13]                              |
| [Test Database Builder for Java][14]            | [MIT License][15]                              |
| [udf-debugging-java][16]                        | [MIT License][17]                              |
| [Matcher for SQL Result Sets][18]               | [MIT License][19]                              |
| [BigQuery][20]                                  | [Apache-2.0][21]                               |
| [Jackson-core][22]                              | [The Apache Software License, Version 2.0][21] |
| [Jackson-annotations][23]                       | [The Apache Software License, Version 2.0][21] |
| [jackson-databind][23]                          | [The Apache Software License, Version 2.0][21] |

## Runtime Dependencies

| Dependency                | License           |
| ------------------------- | ----------------- |
| [SLF4J JDK14 Binding][24] | [MIT License][25] |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [SonarQube Scanner for Maven][26]                       | [GNU LGPL 3][27]                               |
| [Apache Maven Toolchains Plugin][28]                    | [Apache-2.0][21]                               |
| [Apache Maven Compiler Plugin][29]                      | [Apache-2.0][21]                               |
| [Apache Maven Enforcer Plugin][30]                      | [Apache-2.0][21]                               |
| [Maven Flatten Plugin][31]                              | [Apache Software License][21]                  |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][32] | [ASL2][33]                                     |
| [Maven Surefire Plugin][34]                             | [Apache-2.0][21]                               |
| [Versions Maven Plugin][35]                             | [Apache License, Version 2.0][21]              |
| [duplicate-finder-maven-plugin Maven Mojo][36]          | [Apache License 2.0][37]                       |
| [Apache Maven Artifact Plugin][38]                      | [Apache-2.0][21]                               |
| [Apache Maven Assembly Plugin][39]                      | [Apache-2.0][21]                               |
| [Apache Maven JAR Plugin][40]                           | [Apache-2.0][21]                               |
| [Artifact reference checker and unifier][41]            | [MIT License][42]                              |
| [spdx-maven-plugin Maven Plugin][43]                    | [The Apache Software License, Version 2.0][33] |
| [Project Keeper Maven plugin][44]                       | [The MIT License][45]                          |
| [Maven Failsafe Plugin][46]                             | [Apache-2.0][21]                               |
| [JaCoCo :: Maven Plugin][47]                            | [EPL-2.0][48]                                  |
| [error-code-crawler-maven-plugin][49]                   | [MIT License][50]                              |
| [Git Commit Id Maven Plugin][51]                        | [GNU Lesser General Public License 3.0][52]    |
| [Apache Maven Clean Plugin][53]                         | [Apache-2.0][21]                               |
| [Apache Maven Resources Plugin][54]                     | [Apache-2.0][21]                               |
| [Apache Maven Install Plugin][55]                       | [Apache-2.0][21]                               |
| [Apache Maven Site Plugin][56]                          | [Apache-2.0][21]                               |

[0]: https://github.com/exasol/virtual-schema-common-jdbc/
[1]: https://github.com/exasol/virtual-schema-common-jdbc/blob/main/LICENSE
[2]: https://github.com/exasol/error-reporting-java/
[3]: https://github.com/exasol/error-reporting-java/blob/main/LICENSE
[4]: http://hamcrest.org/JavaHamcrest/
[5]: https://raw.githubusercontent.com/hamcrest/JavaHamcrest/master/LICENSE
[6]: https://junit.org/
[7]: https://www.eclipse.org/legal/epl-v20.html
[8]: https://github.com/mockito/mockito
[9]: https://opensource.org/licenses/MIT
[10]: https://java.testcontainers.org
[11]: http://opensource.org/licenses/MIT
[12]: https://github.com/exasol/exasol-test-setup-abstraction-java/
[13]: https://github.com/exasol/exasol-test-setup-abstraction-java/blob/main/LICENSE
[14]: https://github.com/exasol/test-db-builder-java/
[15]: https://github.com/exasol/test-db-builder-java/blob/main/LICENSE
[16]: https://github.com/exasol/udf-debugging-java/
[17]: https://github.com/exasol/udf-debugging-java/blob/main/LICENSE
[18]: https://github.com/exasol/hamcrest-resultset-matcher/
[19]: https://github.com/exasol/hamcrest-resultset-matcher/blob/main/LICENSE
[20]: https://github.com/googleapis/google-cloud-java
[21]: https://www.apache.org/licenses/LICENSE-2.0.txt
[22]: https://github.com/FasterXML/jackson-core
[23]: https://github.com/FasterXML/jackson
[24]: http://www.slf4j.org
[25]: http://www.opensource.org/licenses/mit-license.php
[26]: https://docs.sonarsource.com/sonarqube-server/latest/extension-guide/developing-a-plugin/plugin-basics/sonar-scanner-maven/sonar-maven-plugin/
[27]: http://www.gnu.org/licenses/lgpl.txt
[28]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[29]: https://maven.apache.org/plugins/maven-compiler-plugin/
[30]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[31]: https://www.mojohaus.org/flatten-maven-plugin/
[32]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[33]: http://www.apache.org/licenses/LICENSE-2.0.txt
[34]: https://maven.apache.org/surefire/maven-surefire-plugin/
[35]: https://www.mojohaus.org/versions/versions-maven-plugin/
[36]: https://basepom.github.io/duplicate-finder-maven-plugin
[37]: http://www.apache.org/licenses/LICENSE-2.0.html
[38]: https://maven.apache.org/plugins/maven-artifact-plugin/
[39]: https://maven.apache.org/plugins/maven-assembly-plugin/
[40]: https://maven.apache.org/plugins/maven-jar-plugin/
[41]: https://github.com/exasol/artifact-reference-checker-maven-plugin/
[42]: https://github.com/exasol/artifact-reference-checker-maven-plugin/blob/main/LICENSE
[43]: https://github.com/spdx/spdx-maven-plugin
[44]: https://github.com/exasol/project-keeper/
[45]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[46]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[47]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[48]: https://www.eclipse.org/legal/epl-2.0/
[49]: https://github.com/exasol/error-code-crawler-maven-plugin/
[50]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[51]: https://github.com/git-commit-id/git-commit-id-maven-plugin
[52]: http://www.gnu.org/licenses/lgpl-3.0.txt
[53]: https://maven.apache.org/plugins/maven-clean-plugin/
[54]: https://maven.apache.org/plugins/maven-resources-plugin/
[55]: https://maven.apache.org/plugins/maven-install-plugin/
[56]: https://maven.apache.org/plugins/maven-site-plugin/
