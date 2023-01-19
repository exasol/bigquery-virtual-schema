# Big Query SQL Dialect User Guide

The Big Query SQL dialect allows you connecting to the [Google Big Query](https://cloud.google.com/bigquery/), Google's serverless, enterprise data warehouse.

## JDBC Driver

Download the [Simba JDBC Driver for Google BigQuery](https://cloud.google.com/bigquery/docs/reference/odbc-jdbc-drivers#current_jdbc_driver).

## Uploading the JDBC Driver to BucketFS

1. [Create a bucket in BucketFS](https://docs.exasol.com/administration/on-premise/bucketfs/create_new_bucket_in_bucketfs_service.htm)
1. [Upload the driver to BucketFS](https://docs.exasol.com/administration/on-premise/bucketfs/accessfiles.htm)

When uploading the archive to the bucket then the Exasol database will automatically extract the contents of the archive and your UDF can access the files using the following path pattern `<your bucket>/<archive's name without extension>/<name of a file from the archive>.jar`.

See the [Exasol documentation](https://docs.exasol.com/db/latest/database_concepts/bucketfs/database_access.htm) for accessing BucketFS.

Leave only `.jar` files in the archive. It will help you to generate a list for adapter script later.

## Installing the Adapter Script

Upload the latest available release of [Big Query Virtual Schema](https://github.com/exasol/bigquery-virtual-schema/releases) to BucketFS.

Then create a schema to hold the adapter script.

```sql
CREATE SCHEMA SCHEMA_FOR_VS_SCRIPT;
```

The SQL statement below creates the adapter script, defines the Java class that serves as entry point and tells the UDF framework where to find the libraries (JAR files) for Virtual Schema and database driver.

List all the JAR files from the JDBC driver.

```sql
CREATE JAVA ADAPTER SCRIPT SCHEMA_FOR_VS_SCRIPT.ADAPTER_SCRIPT_BIGQUERY AS
    %scriptclass com.exasol.adapter.RequestDispatcher;
    %jar /buckets/<BFS service>/<bucket>/virtual-schema-dist-10.1.0-bigquery-2.1.1.jar;
    %jar /buckets/<BFS service>/<bucket>/GoogleBigQueryJDBC42.jar;
    ...
    ...
    ...
/
;
```

**Hint**: to avoid filling the list by hands, use a convenience UDF script [bucketfs_ls](https://github.com/exasol/exa-toolbox/blob/master/utilities/bucketfs_ls.sql). Create a script and run it as in the following example:

```sql
SELECT '%jar /buckets/<BFS service>/<bucket>/<archive's name without extension if used>/'|| files || ';' FROM (SELECT EXA_toolbox.bucketfs_ls('/buckets/<BFS service>/<bucket>/<archive's name without extension if used>/') files );
```

## Defining a Named Connection

Please follow the [Authenticating to a Cloud API Service article](https://cloud.google.com/docs/authentication/) to get Google service account credentials.

Upload the key as a JSON file to BucketFS, then create a named connection:

```sql
CREATE OR REPLACE CONNECTION BIGQUERY_JDBC_CONNECTION
TO 'jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;ProjectId=<your project id>;OAuthType=0;OAuthServiceAcctEmail=<service account email>;OAuthPvtKeyPath=/<path to the bucket>/<name of the key file>';
```

You can find additional information about the JDBC connection URL [in the Big Query JDBC installation guide](https://storage.googleapis.com/simba-bq-release/jdbc/Simba%20Google%20BigQuery%20JDBC%20Connector%20Install%20and%20Configuration%20Guide.pdf).

## Creating a Virtual Schema

Below you see how a Big Query Virtual Schema is created. Please note that you have to provide the name of a catalog (=the project name) and the name of a schema (=the dataset name).

```sql
CREATE VIRTUAL SCHEMA <virtual schema name>
    USING SCHEMA_FOR_VS_SCRIPT.ADAPTER_SCRIPT_BIGQUERY
    WITH
    CONNECTION_NAME = 'BIGQUERY_JDBC_CONNECTION'
    CATALOG_NAME = '<project name>'
    SCHEMA_NAME = '<dataset name>';
```

## Data Types Conversion

BigQuery Data Type | Supported | Converted Exasol Data Type | Known limitations
-------------------|-----------|----------------------------|-------------------
BOOL/ BOOLEAN      |  ✓        | BOOLEAN                    |
DATE               |  ✓        | DATE                       |
DATETIME           |  ✓        | TIMESTAMP                  |
FLOAT / FLOAT64    |  ✓        | DOUBLE                     | Expected range for correct mapping: `-99999999.99999999` .. `99999999.99999999`.
GEOGRAPHY          |  ✓        | GEOMETRY                   |
INTEGER / INT64    |  ✓        | DECIMAL                    |
BIGNUMERIC         |  ✓        | DOUBLE PRECISION           | Expected range for correct mapping: `-99999999.99999999` .. `99999999.99999999`.
NUMERIC            |  ✓        | DOUBLE PRECISION           | Expected range for correct mapping: `-99999999.99999999` .. `99999999.99999999`.
STRING             |  ✓        | VARCHAR(65535)             |
TIME               |  ✓        | VARCHAR                    |
TIMESTAMP          |  ✓        | TIMESTAMP                  | Expected range for correct mapping: `1582-10-15 00:00:01` .. `9999-12-31 23:59:59.9999`. JDBC driver maps dates before `1582-10-15 00:00:01` incorrectly. Example of incorrect mapping: `1582-10-14 22:00:01` -> `1582-10-04 22:00:01`
BYTES              |  ×        |                            |
STRUCT             |  ×        |                            |
ARRAY              |  ×        |                            |
JSON               |  ×        |                            |
INTERVAL           |  ×        |                            |

## Known Limitations

### Performance

Please be aware that the current implementation of the dialect can only handle result sets with limited size (a few thousand rows).

If you need to process a large amount of data, please contact the Exasol support team. Another implementation of the dialect with a performance improvement (using `IMPORT INTO`) is available, but not documented for self-service because of:

1. the complex installation process
1. security risks (a user has to disable the driver's security manager to use it)

### Mapping of Empty Result

If a query returns an empty result set, the Virtual Schema will map all columns to type `SMALLINT`.

## Testing information

In the following matrix you find combinations of JDBC driver and dialect version that Exasol developer have tested successfully:

Virtual Schema Version | Big Query Version   | Driver Name                              | Driver Version
-----------------------|---------------------|------------------------------------------|-----------------
 1.0.0                 | Google BigQuery 2.0 | Magnitude Simba JDBC driver for BigQuery | 1.2.2.1004
 2.0.2                 | Google BigQuery 2.0 | Magnitude Simba JDBC driver for BigQuery | 1.2.25.1029
