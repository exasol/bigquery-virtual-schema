# Big Query Virtual Schema

[![Build Status](https://api.travis-ci.com/exasol/bigquery-virtual-schema.svg?branch=main)](https://travis-ci.com/exasol/bigquery-virtual-schema)

SonarCloud results:

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abigquery-virtual-schema&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.exasol%3Abigquery-virtual-schema)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abigquery-virtual-schema&metric=security_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Abigquery-virtual-schema)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abigquery-virtual-schema&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Abigquery-virtual-schema)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abigquery-virtual-schema&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Abigquery-virtual-schema)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abigquery-virtual-schema&metric=sqale_index)](https://sonarcloud.io/dashboard?id=com.exasol%3Abigquery-virtual-schema)

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abigquery-virtual-schema&metric=code_smells)](https://sonarcloud.io/dashboard?id=com.exasol%3Abigquery-virtual-schema)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abigquery-virtual-schema&metric=coverage)](https://sonarcloud.io/dashboard?id=com.exasol%3Abigquery-virtual-schema)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abigquery-virtual-schema&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=com.exasol%3Abigquery-virtual-schema)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abigquery-virtual-schema&metric=ncloc)](https://sonarcloud.io/dashboard?id=com.exasol%3Abigquery-virtual-schema)

# Overview

The **Big Query Virtual Schema** provides an abstraction layer that makes an external [Google Big Query](https://cloud.google.com/bigquery/) data source accessible from an Exasol database through regular SQL commands. The contents of the external Big Query data source are mapped to virtual tables which look like and can be queried as any regular Exasol table.

If you want to set up a Virtual Schema for a different database system, please head over to the [Virtual Schemas Repository][virtual-schemas].

## Features

* Access a Big Query data source in read only mode from an Exasol database, using a Virtual Schema.

## Table of Contents

### Information for Users

* [Virtual Schemas User Guide][virtual-schemas-user-guide]
* [Big Query Dialect User Guide](doc/user_guide/bigquery_user_guide.md)
* [Changelog](doc/changes/changelog.md)

Find all the documentation in the [Virtual Schemas project][vs-doc].

## Information for Developers 

* [Virtual Schema API Documentation][vs-api]

### Run Time Dependencies

Running the Virtual Schema requires a Java Runtime version 11 or later.

| Dependency                                                         | Purpose                                                | License                                           |
|--------------------------------------------------------------------|--------------------------------------------------------|---------------------------------------------------|
| [Exasol Virtual Schema JDBC][virtual-schema-common-jdbc]           | Common JDBC functions for Virtual Schemas adapters     | MIT License                                       |
| [Big Query JDBC driver][bigquery-jdbc-driver]                      | JDBC driver for Big Query data source                  | See [Big Query JDBC driver][bigquery-jdbc-driver] |
| [Exasol Error Reporting][exasol-error-reporting]                   | Creating unified error messages                        | MIT License                                       |

### Test Dependencies

| Dependency                                                         | Purpose                                                | License                       |
|--------------------------------------------------------------------|--------------------------------------------------------|-------------------------------|
| [Java Hamcrest](http://hamcrest.org/JavaHamcrest/)                 | Checking for conditions in code via matchers           | BSD License                   |
| [JUnit](https://junit.org/junit5)                                  | Unit testing framework                                 | Eclipse Public License 1.0    |
| [Mockito](http://site.mockito.org/)                                | Mocking framework                                      | MIT License                   |
| [Exasol Hamcrest Result Set Matcher][exasol-hamcrest]              | Hamcrest result set matcher for testing                | MIT License                   |

### Maven Plug-ins

| Plug-in                                                            | Purpose                                                | License                       |
|--------------------------------------------------------------------|--------------------------------------------------------|-------------------------------|
| [Maven Jacoco Plugin][maven-jacoco-plugin]                         | Code coverage metering                                 | Eclipse Public License 2.0    |
| [Maven Surefire Plugin][maven-surefire-plugin]                     | Unit testing                                           | Apache License 2.0            |
| [Maven Compiler Plugin][maven-compiler-plugin]                     | Setting required Java version                          | Apache License 2.0            |
| [Maven Assembly Plugin][maven-assembly-plugin]                     | Creating JAR                                           | Apache License 2.0            |
| [Versions Maven Plugin][versions-maven-plugin]                     | Checking if dependencies updates are available         | Apache License 2.0            |
| [Maven Enforcer Plugin][maven-enforcer-plugin]                     | Controlling environment constants                      | Apache License 2.0            |
| [Maven Dependency Plugin][maven-dependency-plugin]                 | Accessing to test dependencies                         | Apache License 2.0            |
| [Artifact Reference Checker Plugin][artifact-ref-checker-plugin]   | Check if artifact is referenced with correct version   | MIT License                   |
| [Project Keeper Maven Plugin][project-keeper-maven-plugin]         | Checking project structure                             | MIT License                   |
| [Sonatype OSS Index Maven Plugin][sonatype-oss-index-maven-plugin] | Checking dependencies vulnerability                    | ASL2                          |

[virtual-schema-common-jdbc]: https://github.com/exasol/virtual-schema-common-jdbc
[bigquery-jdbc-driver]: https://cloud.google.com/bigquery/providers/simba-drivers/
[exasol-error-reporting]: https://github.com/exasol/error-reporting-java/

[exasol-hamcrest]: https://github.com/exasol/hamcrest-resultset-matcher

[maven-jacoco-plugin]: https://www.eclemma.org/jacoco/trunk/doc/maven.html
[maven-surefire-plugin]: https://maven.apache.org/surefire/maven-surefire-plugin/
[maven-compiler-plugin]: https://maven.apache.org/plugins/maven-compiler-plugin/
[maven-assembly-plugin]: https://maven.apache.org/plugins/maven-assembly-plugin/
[versions-maven-plugin]: https://www.mojohaus.org/versions-maven-plugin/
[maven-enforcer-plugin]: http://maven.apache.org/enforcer/maven-enforcer-plugin/
[artifact-ref-checker-plugin]: https://github.com/exasol/artifact-reference-checker-maven-plugin
[maven-dependency-plugin]: https://maven.apache.org/plugins/maven-dependency-plugin/
[project-keeper-maven-plugin]: https://github.com/exasol/project-keeper-maven-plugin
[sonatype-oss-index-maven-plugin]: https://sonatype.github.io/ossindex-maven/maven-plugin/

[virtual-schemas-user-guide]: https://docs.exasol.com/database_concepts/virtual_schemas.htm
[virtual-schemas]: https://github.com/exasol/virtual-schemas
[vs-api]: https://github.com/exasol/virtual-schema-common-java/blob/master/doc/development/api/virtual_schema_api.md
[vs-doc]: https://github.com/exasol/virtual-schemas/tree/master/doc
