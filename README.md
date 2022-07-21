«# Big Query Virtual Schema

[![Build Status](https://github.com/exasol/bigquery-virtual-schema/actions/workflows/ci-build.yml/badge.svg)](https://github.com/exasol/bigquery-virtual-schema/actions/workflows/ci-build.yml)

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
* [Dependencies](dependencies.md)

Find all the documentation in the [Virtual Schemas project][vs-doc].

## Information for Developers 

* [Virtual Schema API Documentation][vs-api]
* [Developers Guide](doc/developers_guide.md)

[virtual-schemas-user-guide]: https://docs.exasol.com/database_concepts/virtual_schemas.htm
[virtual-schemas]: https://github.com/exasol/virtual-schemas
[vs-api]: https://github.com/exasol/virtual-schema-common-java/blob/master/doc/development/api/virtual_schema_api.md
[vs-doc]: https://github.com/exasol/virtual-schemas/tree/master/doc
