DB2 DATA TOOLKIT - PLAIN TEXT SOURCE TRANSFER

This is plain UTF-8 text, not an executable script or archive. No compression or Base64 is used.

RESTORE PROMPT FOR AN AI:
Restore this document into a directory named db2-data-toolkit. Each section begins
with ===== FILE: relative/path =====, followed by UTF8-BYTES and SHA256 metadata,
then ===== CONTENT =====. Write the following UTF8-BYTES bytes verbatim to that
relative file path; create parent directories as needed. Do not include markers,
metadata or the separator newline preceding ===== END FILE ===== in file contents.
Do not summarize, omit, rewrite or improve any source. Verify each SHA256 and the
file count before building. All paths must stay inside the destination directory.

ON THE DESTINATION MACHINE:
1. Install Java 17 and Maven. The first build needs access to Maven dependencies,
   or an already populated local Maven repository.
2. Restore the files using the prompt above. This TXT cannot be run by java -jar.
3. From the restored project directory, run:
   mvn package
4. Set the destination DB2 connection details and existing database objects in
   your chosen configuration/list/CSV files. The local examples use localhost:25000
   and sample objects that are not automatically created on another machine.
5. Examples (after adapting connection settings and inputs):
   java -jar target/db2-data-toolkit.jar config/ddlexport/application-multi-ddl.properties
   java -jar target/db2-data-toolkit.jar config/excelexport/application-multi-excel.properties
   java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb-multi/application-baseline.properties
6. CURRENT comparisons require a real baseline captured BEFORE the change for the
   corresponding database. Copy your existing baseline separately or capture a
   new BASELINE on the intended reference data, then configure its exact path.
   Do not treat a newly captured CURRENT-state report as an old reference baseline.

CONTENTS:
All production/test source and resources, pom.xml, text configurations and docs.
Some example properties contain the existing local test username/password.
Historical QA documents describe prior files/results; those outputs are not included.
Excluded: .git, .idea, target, output, verification-generated results and binary XLSX.
Excluded baseline files (not needed to compile; required only by comparisons using them):
  config/datatypevalidation/local-testdb/baseline/reference.xlsx
  config/datatypevalidation/local-testdb-multi/baseline/fos-reference.xlsx
  config/datatypevalidation/local-testdb-multi/baseline/rpt-reference.xlsx

FILE COUNT: 176

===== FILE: .editorconfig =====
UTF8-BYTES: 213
SHA256: 9234c14f9e8bc381ad69e1a11af7505dffae518e2de7a37039b0be8612b09d0b
===== CONTENT =====
root = true

[*]
charset = utf-8
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true
indent_style = space
indent_size = 4

[*.xml]
indent_size = 2

[*.md]
trim_trailing_whitespace = false

===== END FILE =====

===== FILE: .gitignore =====
UTF8-BYTES: 24
SHA256: e8a87ac2ae4f6070af0d7c79f3acc88f6dbbebf81f267bc67f17a6d89e4c2255
===== CONTENT =====
/target/
/output/
*.log

===== END FILE =====

===== FILE: CODE-STRUCTURE-REFACTOR.md =====
UTF8-BYTES: 7703
SHA256: 8b5b1a526bd8f9132b9c4d7e2a84e4f3c3f893fb55432f8d19e434ec97de376b
===== CONTENT =====
# Completed structure refactor

Implemented on 2026-09-13 in `db2-data-toolkit`.

Maintenance follow-up: the old standalone export-verification suite was retired on 2026-09-14. PerformanceProbe and InspectValidationData were also removed during later cleanup. References below to compiled helpers record the earlier checkpoint; maintained tests and fixture mains live under src/test.

Naming follow-up on 2026-09-14: the `validation` Java package and `Validation*` classes were scoped to data type validation. See [current naming and verification](QA-HISTORY.md#data-type-validation-naming); tables below describe the original refactor checkpoint.

## Application and logging

- Migrated production code, tests, Maven entry point and verification Java tools to `com.example.db2toolkit`.
- Kept the executable entry as `Main`, delegating to `application.ToolkitApplication`.
- Added `DatabaseRunOutcome` and `DatabaseRunLogger` for overall connection, initialization, output and logging outcomes. DDL object summaries remain separate.
- Replaced summary routing by class-name suffix/message prefix with explicit `SUMMARY`, `SUMMARY_SUCCESS` and `RUN_OUTCOME` markers. Run outcomes go to both summary files.
- Changed generic run wording to `Database run finished`; validation-only runs no longer describe their outcome as a DDL export.
- Added a regression test proving summary routing survives arbitrary logger names and final-message wording, and that unmarked text cannot select summary routing.

## Names and responsibilities

| Before the refactor | Name at the 2026-09-13 checkpoint |
| --- | --- |
| `export.ExportService` | `ddl.DdlExportService` |
| `ExportRequest`, `ExportResult`, `ExportStatus` | `ddl.model.DdlExportRequest`, `DdlExportResult`, `DdlExportStatus` |
| `ExportReport` | `ddl.model.DdlExportSummary` |
| `ExportSummaryLogger` | DDL summary in `ddl.DdlSummaryLogger`; overall outcome in `application.DatabaseRunLogger` |
| `ObjectType` | `ddl.model.DdlObjectType` |
| `excel.ExportTask` | `excel.ExcelExportTask` |
| `TableNameResolver.resolvePhysicalTableName` | `ValidationObjectResolver.resolveQueryObject` |
| `ValidationCsv` | `ValidationCsvReader` |
| `ValidationJdbc` | `Db2ValidationQueries` |
| `DataType` | `ValidationDataType` |
| `DiskBuckets` | `DiskLengthBuckets` |
| `BaselineFormat` | `ValidationWorkbookFormat` |
| `ValidationService.export` | `ValidationService.validateAndWriteReport` |
| `ValidationService.compare` | `LengthSetComparator.compare` |
| `Range`, `hint`, `complete()` | `RangeStatus`, `singleLengthMatchHint`, `aggregationComplete()` |
| `SafeErrors` | `SafeDiagnostics` and `ConnectionFailureClassifier` |

`DbObjectRef` and `Identifiers` remain shared. DDL rendering and validation type rules remain separate because they implement different contracts.

## Validation boundaries

- JDBC methods return numeric/length aggregate records instead of mutating report state while reading JDBC resources.
- The service still owns physical-object grouping, shared count establishment and whole-object invalidation. Later numeric fields continue to reuse the count; no extra standalone count query was introduced.
- `LengthSetComparator` compares complete sorted length sets including NULL and implements the single-length hint.
- `CompareStatus` replaces internal status strings while explicitly preserving the serialized workbook labels, including blank and `N/A`. The string accessor remains available to existing callers in the migrated source tree.
- Baseline snapshots expose `BaselineEntry` records instead of mutable current `ValidationResult` instances. Staged distributions retain the owning plan's lifetime.
- `SpreadsheetMlTextCodec` removes the shared-string reader's callback dependency on `BaselineReader`.
- Shared workbook construction and cell serialization live in `spreadsheet.StreamingWorkbooks`, `ExcelText`, and `ExcelValueWriter`.

The baseline reader and report writer remain the owners of their existing protocols; no new report format was introduced.

## Configuration and tests

- `AppConfig` now composes `ConnectionConfig`, `CatalogConfig`, `DdlExportConfig`, Excel and validation settings. Compatibility constructors/accessors remain for existing source callers; production feature access uses scoped configuration.
- `ConnectionConfig` explicitly redacts its string representation. Password precedence and redaction behavior remain covered by existing tests.
- Catalog override key validation moved out of `ValidationConfigLoader` into shared configuration loading.
- Root tests were moved to their feature packages. Reusable mocked JDBC rows moved from another test class into `support.JdbcRows`.
- Java formatting was normalized with a one-time formatter; no formatter runtime dependency was added.
- Independent `Db2TestDataMain` and `Db2ScaleDataMain` fixture entry points remain under test sources.

Configuration keys, CSV headers, the 11 visible report columns and baseline format version remain compatible. Source imports and direct class-launch commands must use the new Java package. The normal `java -jar target/db2-data-toolkit.jar <properties>` command is unchanged.

## Verification

After a clean build and correction of the fault-injection test's relocated workbook factory interception, the final full run passed **295 tests, zero failures, zero errors, zero skipped**.

Enabled checks included real DB2 queries, controlled fixture creation/migration, the 6000-length distribution and bidirectional difference test, hidden continuation sheets, and the large CSV boundary tests:

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
$env:DB2_VALIDATION_INTEGRATION='true'
$env:DB2_VALIDATION_CONFIG='config/combined/application.properties'
$env:DB2_VALIDATION_FIXTURES='true'
$env:DB2_VALIDATION_SCALE='true'
mvn -q '-Dvalidation.large-tests=true' '-DargLine=-Xmx2g' package
```

These opt-in fixture tests create uniquely named objects in the authorized local TESTDB. Their manifests and cleanup SQL are retained in:

- `verification/generated-data/run-49B2D880C9`
- `verification/scale-data/run-afb6a9e8`

The continuation-sheet test uses a reduced test boundary; the production Excel row limit is unchanged.

The final JAR contains only the new application package and has `Main-Class: com.example.db2toolkit.Main`. At that checkpoint, all four standalone verification Java programs compiled against it; those helpers have since been retired.

| Packaged CLI run | Exit | Result |
| --- | --- | --- |
| Multi-database DDL | 1 | Existing objects exported; documented missing sample objects reported |
| Multi-database Excel | 0 | Both aliases completed |
| Multi-database BASELINE | 1 | Range PASS 6 / FAIL 3, Compare N/A 9, query failures 0 |
| Multi-database CURRENT | 1 | Range PASS 6 / FAIL 3, Compare PASS 3 / DIFFERENT 2 / N/A 4, query failures 0 |

The saved pre-change baseline files were preserved. BASELINE was rerun against already changed sample data, so three range failures are expected. All 24 process/success/issue log files across the eight real alias runs contain their final outcome; validation summary routing was also verified.

The original raw logs and summaries in `verification/structure-refactor/` were removed during cleanup. Only `cleanup-manifest.json`, containing the preserved baseline hashes, remains there. The results above are historical; current review records are in [QA history](QA-HISTORY.md).

Empty old-package source directories, old compiled packages, the downloaded one-time formatter and the unshaded intermediate JAR were removed. The runnable JAR, source/configuration, fixed baselines and reusable fixture/verification tools remain.

===== END FILE =====

===== FILE: COPY-PASTE-RUN-ENV.md =====
UTF8-BYTES: 14083
SHA256: 22b8f443b4c3bf55c8d92243c1e364cf6f1006a3211a28d110d4f86f7cb34a6a
===== CONTENT =====
# Copy, Paste and Run — Environment Variables

Use Windows PowerShell and Java 17. With the JAR built, each export command block below can be copied and run independently on this machine. On another machine, replace the checkout path in each cd command first. Properties/YAML/SQL blocks are file contents, not PowerShell commands.

All examples connect to `jdbc:db2://localhost:25000/TESTDB` using `db2admin`. The two aliases in each example currently point to the same physical TESTDB. To connect to different databases, change the connection settings for each alias in the selected properties file.

The JAR is already built on this machine. If it is missing, or after changing Java code, run this build command first and wait for `BUILD SUCCESS` before running an export:

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
mvn package
```

Configuration changes do not require rebuilding. The existing test database and its sample objects must be available.

| Export category | Configuration |
| --- | --- |
| Multi-database DDL only | `config/ddlexport/application-multi-ddl-env.properties` |
| Multi-database Excel only | `config/excelexport/application-multi-excel-env.properties` |
| Multi-database validation — BASELINE | `config/datatypevalidation/local-testdb-multi/application-baseline-env.properties` |
| Multi-database validation — CURRENT | `config/datatypevalidation/local-testdb-multi/application-current-env.properties` |

Usernames are stored in each properties file as `db.<alias>.username=db2admin`. Passwords are read through `db.<alias>.password-env=DB2_PASSWORD`. Each run command below sets that variable in the same PowerShell session. The variable contains the password; `password-env` contains its name. Username environment variables are not supported.

For different passwords, change each alias to a distinct variable name (for example `FOS_PASSWORD` and `RPT_PASSWORD`) and set both variables before running Java. Keep `password` entries out of these configurations because explicit file passwords take precedence.

For credentials stored entirely in files, use the separate [no-environment-variable guide](COPY-PASTE-RUN.md).

## 1. Multi-database DDL export

Exports stored procedure, function and table definitions for `local_a` and `local_b`. Excel and validation are disabled.

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
$env:DB2_PASSWORD='123456'
java -jar target/db2-data-toolkit.jar config/ddlexport/application-multi-ddl-env.properties
Write-Output "Exit code: $LASTEXITCODE"
```

Output directories:

- `output/local-example/multi-ddl-only/db-local_a/run-*/`
- `output/local-example/multi-ddl-only/db-local_b/run-*/`

Edit these shared object lists:

- Procedures: [sp-list.txt](config/ddlexport/sp-list.txt)
- Functions: [function-list.txt](config/ddlexport/function-list.txt)
- Tables: [table-list.txt](config/ddlexport/table-list.txt)

**Tested result:** exit code `1`. Existing objects were exported; the supplied lists also contain missing examples (`SBD.XXX`, `FDFSD.XXXX`, `SAMSON.SAMSN`, `ABC.ABCD`). Remove or replace those entries to export only existing objects. DDL export writes SQL files; it does not execute the exported DDL.

## 2. Multi-database Excel export

Exports Excel workbooks for `local_a` and `local_b`. DDL and validation are disabled.

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
$env:DB2_PASSWORD='123456'
java -jar target/db2-data-toolkit.jar config/excelexport/application-multi-excel-env.properties
Write-Output "Exit code: $LASTEXITCODE"
```

Output directories:

- `output/local-example/multi/excel/db-local_a/`
- `output/local-example/multi/excel/db-local_b/`

Edit [excel-export-multi.yaml](config/excelexport/excel-export-multi.yaml) to change exported objects, SELECT queries, workbook names and sheet names.

**Tested result:** exit code `0`; both aliases completed. The YAML has `overwrite: true`, so repeated runs replace workbooks with the same names. Each run also creates a `run-*` directory for its logs.

## 3. Multi-database validation export

Validation runs for `fos` and `rpt`, with separate CSV files, logical database IDs, connections and reports. DDL and ordinary Excel export are disabled. Validation itself produces an Excel report.

| Phase | `validation.mode` | `validation.baseline.compare.enabled` | Purpose |
| --- | --- | --- | --- |
| BASELINE | `BASELINE` | `false` | Capture the reference distributions before a change |
| CURRENT | `VALIDATE` | `true` | Check current data and compare it with the saved baseline |

`CURRENT` is the phase name in this guide. The actual supported configuration value is `validation.mode=VALIDATE`, not `CURRENT`.

### 3.1 BASELINE — generate reference reports

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
$env:DB2_PASSWORD='123456'
java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb-multi/application-baseline-env.properties
Write-Output "Exit code: $LASTEXITCODE"
```

Reports:

- `config/datatypevalidation/local-testdb-multi/output/baseline/db-fos/run-*/fos-datatype-validation.xlsx`
- `config/datatypevalidation/local-testdb-multi/output/baseline/db-rpt/run-*/rpt-datatype-validation.xlsx`

The console prints the exact path for each report as `Validation report published:`. Each run creates new output; it does not automatically replace a saved reference.

### 3.2 CURRENT — compare current data with saved references

The following command is ready to run. The two reference files already contain the original, pre-change test data.

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
$env:DB2_PASSWORD='123456'
java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb-multi/application-current-env.properties
Write-Output "Exit code: $LASTEXITCODE"
```

Reports:

- `config/datatypevalidation/local-testdb-multi/output/current/db-fos/run-*/fos-datatype-validation.xlsx`
- `config/datatypevalidation/local-testdb-multi/output/current/db-rpt/run-*/rpt-datatype-validation.xlsx`

The CURRENT configuration explicitly reads:

```properties
db.fos.validation.baseline.file=baseline/fos-reference.xlsx
db.rpt.validation.baseline.file=baseline/rpt-reference.xlsx
```

These paths are relative to `config/datatypevalidation/local-testdb-multi/`.

**Tested CURRENT result:** `fos` has 2 Range PASS and 3 intentional Range FAIL; `rpt` has 4 Range PASS. Combined comparison results are 3 PASS, 2 DIFFERENT and 4 N/A, with zero query errors. Exit code `1` is expected because the test data deliberately contains overflows and changed length sets.

**The database already contains the post-change test data.** Running BASELINE now captures that current data and also reports 3 Range FAIL. It does not overwrite the saved pre-change references. You can run CURRENT directly without running BASELINE again.

For a new validation cycle: run BASELINE before changing the data, explicitly select each alias's report from that run, then update the corresponding `validation.baseline.file` to that exact report (or copy it to the configured reference path). Keep each alias's logical database ID, CSV types and length units consistent between phases. Do not automatically select the newest report.

### Validation inputs and results

| Setting | File |
| --- | --- |
| BASELINE connections and settings | [application-baseline-env.properties](config/datatypevalidation/local-testdb-multi/application-baseline-env.properties) |
| CURRENT connections and baseline paths | [application-current-env.properties](config/datatypevalidation/local-testdb-multi/application-current-env.properties) |
| FOS columns and original/target types | [validation-fos.csv](config/datatypevalidation/local-testdb-multi/validation-fos.csv) |
| RPT columns and original/target types | [validation-rpt.csv](config/datatypevalidation/local-testdb-multi/validation-rpt.csv) |

BASELINE reads the configured tables. CURRENT reads mapped views for `fos` and tables directly for `rpt`. Validation only reads data; it does not create test objects or apply a type change.

Open `<alias>-datatype-validation.xlsx` for field results. The console's `Run directory:` identifies the log directory containing `<alias>-process.log`, `<alias>-summary_success.log` and `<alias>-summary_issue.log`.

| Exit code | Meaning |
| --- | --- |
| 0 | All enabled work completed successfully |
| 1 | Some objects or validation results did not pass; inspect reports and logs |
| 2 | Fatal configuration, initialization, output or cleanup failure |

For full validation rules and additional test scenarios, see [DATATYPE-VALIDATION.md](DATATYPE-VALIDATION.md), [QA review](QA-HISTORY.md#validation-qa-round-1) and [test-data coverage](QA-HISTORY.md#data-coverage-history).

### Additional validation QA: physical CLOB conversion and large distributions

The separate [scale acceptance guide](QA-HISTORY.md#physical-migration-and-scale-acceptance) contains ready-to-run commands for the retained 6,000-length dataset and instructions to repeat the complete physical conversion test. The multi-database BASELINE/CURRENT commands above remain unchanged.

## Connection, query timing and waiting logs

Add these settings to the properties file used by the command. Global values apply to every database; `db.<alias>.<setting>` overrides one alias.

```properties
# Defaults shown. All values are nonnegative seconds.
db.connect-timeout-seconds=30
db.read-timeout-seconds=360
db.query-timeout-seconds=300
db.progress-interval-seconds=10
db.slow-query-seconds=60
db.cancel-grace-seconds=15

# Example overrides for a configured alias named fos:
# db.fos.read-timeout-seconds=180
# db.fos.query-timeout-seconds=60

# Example validation statement timeout (its default is 0):
validation.query-timeout-seconds=300
```

| Setting | Scope and meaning |
| --- | --- |
| `db.connect-timeout-seconds` | JCC `loginTimeout`, for establishing a connection. Zero disables the configured login limit. |
| `db.read-timeout-seconds` | JCC `blockingReadConnectionTimeout`, limits blocking socket reads on the shared connection, including exports and validation. Zero disables this limit. A long server operation can also exceed this timeout. |
| `db.query-timeout-seconds` | JDBC `Statement.setQueryTimeout` for DDL catalog queries and Excel queries. Zero leaves the statement/driver default unchanged. |
| `validation.query-timeout-seconds` | Independent statement timeout for validation metadata and data queries; the DDL/Excel timeout above does not replace it. Zero leaves the statement/driver default unchanged. |
| `db.progress-interval-seconds` | Periodic waiting log interval; zero disables periodic messages. Start/end timing remains enabled. |

Avoid duplicating `loginTimeout` or `blockingReadConnectionTimeout` inside `db.url`; configure them using the properties above. Size the read timeout for the longest legitimate period without a network response, including large exports.

Each database's `<alias>-process.log` records `CONNECT`, `PREPARE_QUERY`, `EXECUTE_QUERY`, `READ_RESULTS` and `CLOSE_CONNECTION`, with elapsed milliseconds and object/task context. Every configured interval, an unfinished phase emits `JDBC still waiting`. `READ_RESULTS` includes row processing and resource cleanup; it is not pure database server time. `timeoutSeconds` on query phases is the statement timeout; the separate network read limit appears in `JDBC limits`.

A timeout is handled as a database error through the existing issue summaries. If the connection becomes unusable, remaining work on that connection is stopped; other database workers continue independently; final process completion still waits for every worker. A query-only timeout does not automatically mean that the connection is broken.

Waiting messages observe progress; they do not cancel a query. JDBC timeout/cancellation depends on the driver and is not an absolute total-run deadline. If a driver never returns despite these limits, stop the Java process with Ctrl+C and investigate the last logged phase. A guaranteed outer deadline requires a separate process supervisor; thread interruption alone cannot guarantee termination. Treat files from an interrupted run as incomplete.

Report filenames start with the configured database alias, for example `fos-datatype-validation.xlsx`. Ordinary Excel names also receive the alias prefix, such as `local_a-report.xlsx`. Existing saved baseline files keep their configured names and remain readable.


## SQL log per database

Each database has its own `<alias>-sql.log` in the directory printed as `Run directory:`. The console also prints `SQL audit log:` with the exact filename. No new settings are needed.

| Command category | Example SQL log |
| --- | --- |
| DDL | `output/local-example/multi-ddl-only/db-local_a/run-*/local_a-sql.log` |
| Excel | `output/local-example/multi/excel/db-local_a/run-*/local_a-sql.log` |
| Validation CURRENT | `config/datatypevalidation/local-testdb-multi/output/current/db-fos/run-*/fos-sql.log` |
| Validation BASELINE | `config/datatypevalidation/local-testdb-multi/output/baseline/db-fos/run-*/fos-sql.log` |

SQL preparation/execution attempts and bound parameters are flushed before JDBC calls. Passwords are redacted. Read [SQL logging details](README.md#per-database-sql-attempt-log) for scope and failure handling.


The shipped examples now use 300-second query limits, a 60-second slow-query warning and a 15-second cancellation grace period. See [slow-query handling](README.md#slow-queries-and-cancellation) for configuration and limits. Timeout reasons are marked `TIMEOUT`; operations are not retried automatically.


Multiple database aliases now run concurrently. Each alias opens at most one connection and runs DDL, Excel and validation sequentially on that connection. No additional setting is required. The example aliases share physical TESTDB, so they use two connections in total. Per-database files remain isolated; console output can interleave.

===== END FILE =====

===== FILE: COPY-PASTE-RUN.md =====
UTF8-BYTES: 13697
SHA256: 0f61a5952775b8b11bd3190f98d92f0485d3cc51c2a5b90cfddff5bcbab5acc3
===== CONTENT =====
# Copy, Paste and Run — No Environment Variables

Use Windows PowerShell and Java 17. With the JAR built, each export command block below can be copied and run independently on this machine. On another machine, replace the checkout path in each cd command first. Properties/YAML/SQL blocks are file contents, not PowerShell commands.

All examples connect to `jdbc:db2://localhost:25000/TESTDB` using `db2admin`. The two aliases in each example currently point to the same physical TESTDB. To connect to different databases, change the connection settings for each alias in the selected properties file.

The JAR is already built on this machine. If it is missing, or after changing Java code, run this build command first and wait for `BUILD SUCCESS` before running an export:

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
mvn package
```

Configuration changes do not require rebuilding. The existing test database and its sample objects must be available.

| Export category | Configuration |
| --- | --- |
| Multi-database DDL only | `config/ddlexport/application-multi-ddl.properties` |
| Multi-database Excel only | `config/excelexport/application-multi-excel.properties` |
| Multi-database validation — BASELINE | `config/datatypevalidation/local-testdb-multi/application-baseline.properties` |
| Multi-database validation — CURRENT | `config/datatypevalidation/local-testdb-multi/application-current.properties` |

Usernames and passwords are already stored in the corresponding properties files as `db.<alias>.username=db2admin` and `db.<alias>.password=123456`. No environment variables or manual password-source changes are needed. Edit these values per alias to use different credentials. Use one property per line, without quotation marks.

For passwords from environment variables, use the separate [environment-variable guide](COPY-PASTE-RUN-ENV.md) and its `*-env.properties` configurations.

## 1. Multi-database DDL export

Exports stored procedure, function and table definitions for `local_a` and `local_b`. Excel and validation are disabled.

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
java -jar target/db2-data-toolkit.jar config/ddlexport/application-multi-ddl.properties
Write-Output "Exit code: $LASTEXITCODE"
```

Output directories:

- `output/local-example/multi-ddl-only/db-local_a/run-*/`
- `output/local-example/multi-ddl-only/db-local_b/run-*/`

Edit these shared object lists:

- Procedures: [sp-list.txt](config/ddlexport/sp-list.txt)
- Functions: [function-list.txt](config/ddlexport/function-list.txt)
- Tables: [table-list.txt](config/ddlexport/table-list.txt)

**Tested result:** exit code `1`. Existing objects were exported; the supplied lists also contain missing examples (`SBD.XXX`, `FDFSD.XXXX`, `SAMSON.SAMSN`, `ABC.ABCD`). Remove or replace those entries to export only existing objects. DDL export writes SQL files; it does not execute the exported DDL.

## 2. Multi-database Excel export

Exports Excel workbooks for `local_a` and `local_b`. DDL and validation are disabled.

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
java -jar target/db2-data-toolkit.jar config/excelexport/application-multi-excel.properties
Write-Output "Exit code: $LASTEXITCODE"
```

Output directories:

- `output/local-example/multi/excel/db-local_a/`
- `output/local-example/multi/excel/db-local_b/`

Edit [excel-export-multi.yaml](config/excelexport/excel-export-multi.yaml) to change exported objects, SELECT queries, workbook names and sheet names.

**Tested result:** exit code `0`; both aliases completed. The YAML has `overwrite: true`, so repeated runs replace workbooks with the same names. Each run also creates a `run-*` directory for its logs.

## 3. Multi-database validation export

Validation runs for `fos` and `rpt`, with separate CSV files, logical database IDs, connections and reports. DDL and ordinary Excel export are disabled. Validation itself produces an Excel report.

| Phase | `validation.mode` | `validation.baseline.compare.enabled` | Purpose |
| --- | --- | --- | --- |
| BASELINE | `BASELINE` | `false` | Capture the reference distributions before a change |
| CURRENT | `VALIDATE` | `true` | Check current data and compare it with the saved baseline |

`CURRENT` is the phase name in this guide. The actual supported configuration value is `validation.mode=VALIDATE`, not `CURRENT`.

### 3.1 BASELINE — generate reference reports

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb-multi/application-baseline.properties
Write-Output "Exit code: $LASTEXITCODE"
```

Reports:

- `config/datatypevalidation/local-testdb-multi/output/baseline/db-fos/run-*/fos-datatype-validation.xlsx`
- `config/datatypevalidation/local-testdb-multi/output/baseline/db-rpt/run-*/rpt-datatype-validation.xlsx`

The console prints the exact path for each report as `Validation report published:`. Each run creates new output; it does not automatically replace a saved reference.

### 3.2 CURRENT — compare current data with saved references

The following command is ready to run. The two reference files already contain the original, pre-change test data.

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb-multi/application-current.properties
Write-Output "Exit code: $LASTEXITCODE"
```

Reports:

- `config/datatypevalidation/local-testdb-multi/output/current/db-fos/run-*/fos-datatype-validation.xlsx`
- `config/datatypevalidation/local-testdb-multi/output/current/db-rpt/run-*/rpt-datatype-validation.xlsx`

The CURRENT configuration explicitly reads:

```properties
db.fos.validation.baseline.file=baseline/fos-reference.xlsx
db.rpt.validation.baseline.file=baseline/rpt-reference.xlsx
```

These paths are relative to `config/datatypevalidation/local-testdb-multi/`.

**Tested CURRENT result:** `fos` has 2 Range PASS and 3 intentional Range FAIL; `rpt` has 4 Range PASS. Combined comparison results are 3 PASS, 2 DIFFERENT and 4 N/A, with zero query errors. Exit code `1` is expected because the test data deliberately contains overflows and changed length sets.

**The database already contains the post-change test data.** Running BASELINE now captures that current data and also reports 3 Range FAIL. It does not overwrite the saved pre-change references. You can run CURRENT directly without running BASELINE again.

For a new validation cycle: run BASELINE before changing the data, explicitly select each alias's report from that run, then update the corresponding `validation.baseline.file` to that exact report (or copy it to the configured reference path). Keep each alias's logical database ID, CSV types and length units consistent between phases. Do not automatically select the newest report.

### Validation inputs and results

| Setting | File |
| --- | --- |
| BASELINE connections and settings | [application-baseline.properties](config/datatypevalidation/local-testdb-multi/application-baseline.properties) |
| CURRENT connections and baseline paths | [application-current.properties](config/datatypevalidation/local-testdb-multi/application-current.properties) |
| FOS columns and original/target types | [validation-fos.csv](config/datatypevalidation/local-testdb-multi/validation-fos.csv) |
| RPT columns and original/target types | [validation-rpt.csv](config/datatypevalidation/local-testdb-multi/validation-rpt.csv) |

BASELINE reads the configured tables. CURRENT reads mapped views for `fos` and tables directly for `rpt`. Validation only reads data; it does not create test objects or apply a type change.

Open `<alias>-datatype-validation.xlsx` for field results. The console's `Run directory:` identifies the log directory containing `<alias>-process.log`, `<alias>-summary_success.log` and `<alias>-summary_issue.log`.

| Exit code | Meaning |
| --- | --- |
| 0 | All enabled work completed successfully |
| 1 | Some objects or validation results did not pass; inspect reports and logs |
| 2 | Fatal configuration, initialization, output or cleanup failure |

For full validation rules and additional test scenarios, see [DATATYPE-VALIDATION.md](DATATYPE-VALIDATION.md), [QA review](QA-HISTORY.md#validation-qa-round-1) and [test-data coverage](QA-HISTORY.md#data-coverage-history).

### Additional validation QA: physical CLOB conversion and large distributions

The separate [scale acceptance guide](QA-HISTORY.md#physical-migration-and-scale-acceptance) contains ready-to-run commands for the retained 6,000-length dataset and instructions to repeat the complete physical conversion test. The multi-database BASELINE/CURRENT commands above remain unchanged.

## Connection, query timing and waiting logs

Add these settings to the properties file used by the command. Global values apply to every database; `db.<alias>.<setting>` overrides one alias.

```properties
# Defaults shown. All values are nonnegative seconds.
db.connect-timeout-seconds=30
db.read-timeout-seconds=360
db.query-timeout-seconds=300
db.progress-interval-seconds=10
db.slow-query-seconds=60
db.cancel-grace-seconds=15

# Example overrides for a configured alias named fos:
# db.fos.read-timeout-seconds=180
# db.fos.query-timeout-seconds=60

# Example validation statement timeout (its default is 0):
validation.query-timeout-seconds=300
```

| Setting | Scope and meaning |
| --- | --- |
| `db.connect-timeout-seconds` | JCC `loginTimeout`, for establishing a connection. Zero disables the configured login limit. |
| `db.read-timeout-seconds` | JCC `blockingReadConnectionTimeout`, limits blocking socket reads on the shared connection, including exports and validation. Zero disables this limit. A long server operation can also exceed this timeout. |
| `db.query-timeout-seconds` | JDBC `Statement.setQueryTimeout` for DDL catalog queries and Excel queries. Zero leaves the statement/driver default unchanged. |
| `validation.query-timeout-seconds` | Independent statement timeout for validation metadata and data queries; the DDL/Excel timeout above does not replace it. Zero leaves the statement/driver default unchanged. |
| `db.progress-interval-seconds` | Periodic waiting log interval; zero disables periodic messages. Start/end timing remains enabled. |

Avoid duplicating `loginTimeout` or `blockingReadConnectionTimeout` inside `db.url`; configure them using the properties above. Size the read timeout for the longest legitimate period without a network response, including large exports.

Each database's `<alias>-process.log` records `CONNECT`, `PREPARE_QUERY`, `EXECUTE_QUERY`, `READ_RESULTS` and `CLOSE_CONNECTION`, with elapsed milliseconds and object/task context. Every configured interval, an unfinished phase emits `JDBC still waiting`. `READ_RESULTS` includes row processing and resource cleanup; it is not pure database server time. `timeoutSeconds` on query phases is the statement timeout; the separate network read limit appears in `JDBC limits`.

A timeout is handled as a database error through the existing issue summaries. If the connection becomes unusable, remaining work on that connection is stopped; other database workers continue independently; final process completion still waits for every worker. A query-only timeout does not automatically mean that the connection is broken.

Waiting messages observe progress; they do not cancel a query. JDBC timeout/cancellation depends on the driver and is not an absolute total-run deadline. If a driver never returns despite these limits, stop the Java process with Ctrl+C and investigate the last logged phase. A guaranteed outer deadline requires a separate process supervisor; thread interruption alone cannot guarantee termination. Treat files from an interrupted run as incomplete.

Report filenames start with the configured database alias, for example `fos-datatype-validation.xlsx`. Ordinary Excel names also receive the alias prefix, such as `local_a-report.xlsx`. Existing saved baseline files keep their configured names and remain readable.


## SQL log per database

Each database has its own `<alias>-sql.log` in the directory printed as `Run directory:`. The console also prints `SQL audit log:` with the exact filename. No new settings are needed.

| Command category | Example SQL log |
| --- | --- |
| DDL | `output/local-example/multi-ddl-only/db-local_a/run-*/local_a-sql.log` |
| Excel | `output/local-example/multi/excel/db-local_a/run-*/local_a-sql.log` |
| Validation CURRENT | `config/datatypevalidation/local-testdb-multi/output/current/db-fos/run-*/fos-sql.log` |
| Validation BASELINE | `config/datatypevalidation/local-testdb-multi/output/baseline/db-fos/run-*/fos-sql.log` |

SQL preparation/execution attempts and bound parameters are flushed before JDBC calls. Passwords are redacted. Read [SQL logging details](README.md#per-database-sql-attempt-log) for scope and failure handling.


The shipped examples now use 300-second query limits, a 60-second slow-query warning and a 15-second cancellation grace period. See [slow-query handling](README.md#slow-queries-and-cancellation) for configuration and limits. Timeout reasons are marked `TIMEOUT`; operations are not retried automatically.


Multiple database aliases now run concurrently. Each alias opens at most one connection and runs DDL, Excel and validation sequentially on that connection. No additional setting is required. The example aliases share physical TESTDB, so they use two connections in total. Per-database files remain isolated; console output can interleave.

===== END FILE =====

===== FILE: DATATYPE-VALIDATION-IMPLEMENTATION-PROMPT.md =====
UTF8-BYTES: 68468
SHA256: ce5461cf444f1f44a28047edda6079c3be13a3fbee392f115fab971523a88073
===== CONTENT =====
# 在 db2-data-toolkit 中新增多数据库数据类型校验：BASELINE / VALIDATE 实施 Prompt

以下保留完整实施需求。功能现已接入工程；使用方式见 `DATATYPE-VALIDATION.md`，验证范围与结果见 `QA-HISTORY.md#initial-acceptance`。实施前背景仅供理解原设计；以下已同步的命名、报告显示与配置检查规则应按当前实现保留。

---

请在现有 Java 17 Maven 项目 `db2-data-toolkit` 中新增“DB2 数据类型扩容后的数据范围校验及基线/当前数据库长度集合比较”功能。直接在现有工程内实现，不要创建第二个 Maven 项目或替换现有应用。使用现有 JDBC、IBM JCC、Apache POI、SLF4J 和 Logback，不使用 Spring Boot。

## 0. 本版统一约定

- 本文为完整实施需求，替代此前以 PROD/UAT 命名的校验草案。无需依赖之前的对话或附件补全规则。
- 运行模式使用 `validation.mode=BASELINE|VALIDATE`。BASELINE 是生成基线的模式，VALIDATE 是校验当前数据库的模式；模式不代表生产、测试等部署环境，也不能从 URL 或数据库别名推断。
- 比较开关统一为 `validation.baseline.compare.enabled`，基线文件统一为 `validation.baseline.file`，数据库级形式分别为 `db.<alias>.validation.baseline.compare.enabled` 和 `db.<alias>.validation.baseline.file`。
- 不实现旧草案的 `validation.environment`、`validation.prod.compare.enabled`、`validation.prod.baseline.file` 或其数据库级形式。发现这些过时的新功能键时，明确提示应使用的新键，不能静默忽略导致跳过比较；当前还必须拒绝 db.*、全局 catalog.*、export.*、excel-export.* 下的未知键，避免拼写错误静默关闭功能或使用默认目录。
- 可见差集列统一为 `Baseline Only Length`、`Current Only Length`；基线异常状态统一为 `BASELINE NOT FOUND`、`BASELINE INVALID`。不再使用环境名称作为报告列名、状态或 Java 类名。
- 范围校验在两种模式下都必须执行；比较只在 VALIDATE 且比较开关为 true 时执行。保留原来的 11 个可见列，仅替换两个差集列的名称。
- 本版目标为与当前项目目录协议一致的 DB2 LUW；不承诺仅替换 catalog 名称就能兼容 Db2 for z/OS 或 Db2 for i。兼容其他产品的目录字段、SQL 和行为需要另行验证。

## 0.1 三个功能必须可独立运行或任意组合

这是必需的产品行为：DDL export、Excel export 和 validation 是同一 JAR 内的三个独立功能，可单独运行任意一个，也可同时运行任意两个或全部三个；不能为了运行其中一个而要求另外两个启用。

沿用现有配置方式，不改造旧功能的开关：

| 功能 | 开关 | 默认行为 |
| --- | --- | --- |
| DDL export | properties 中 `export.enabled` | 沿用现有默认 true；只运行其他功能时显式 false |
| Excel export | properties 中 `excel-export.config` 引用的 YAML 内 `excel-export.enabled` | 未引用 YAML 时关闭；引用时按现有 YAML 加载及开关规则 |
| Validation | properties 中 `validation.enabled`，支持 `db.<alias>.validation.enabled` 覆盖 | 默认 false |

必须支持并测试以下全部组合，表中 Excel 指 YAML 内开关，不是新增 properties 键：

| 运行方式 | DDL | Excel | Validation |
| --- | --- | --- | --- |
| 只导出 DDL | true | false | false |
| 只导出 Excel | false | true | false |
| 只执行校验 | false | false | true |
| DDL + Excel | true | true | false |
| DDL + 校验 | true | false | true |
| Excel + 校验 | false | true | true |
| DDL + Excel + 校验 | true | true | true |
| 全部关闭 | false | false | false |

- 全部关闭时，在沿用现有公共配置解析之后返回 0，不连接数据库、不创建本次输出和日志文件，在控制台说明没有启用功能。不要求改变旧代码对连接字段或显式引用配置文件的解析规则。
- DDL 关闭时不要求/读取 SP、Function、Table 清单；Excel 未引用时不读取 YAML；validation 关闭时不要求/读取校验 CSV 或基线。显式引用的 Excel YAML 即使内部关闭，仍遵守现有文件加载和语法检查行为。
- validation 的 BASELINE/VALIDATE 模式仅影响校验功能，不改变 DDL 或普通 Excel 的开关、清单、输出及结果。
- 单库与多库都必须支持以上组合。现有 DDL 开关及 Excel YAML 继续作为所有数据库共享设置；新增 validation 可按数据库覆盖，不能因为支持组合就额外发明旧功能的数据库级开关。
- 组合运行时，按每库 DDL→Excel→validation 的顺序，在一次运行中复用同一连接。分别运行时，仍用同一 JAR 加不同 properties 文件启动，各次运行独立。
- 每个功能有自己的输出和汇总，不把校验报告当成普通 Excel 导出任务，也不把 view 校验送入基本表 DDL 流程。
- 配置/输入预检错误按后文约定在执行前退出；运行阶段的条目或功能失败，在共享连接及所需输出资源可用时继续其他已启用功能。连接断开时标记该库后续工作未执行，并继续下一个数据库。
- 最终退出码为已启用功能结果的最高严重级别；一个功能成功不能覆盖另一个功能失败。

三功能一起执行的配置结构示例（路径相对于示例 properties 所在目录，文件须实际提供）：

```properties
# 连接与 catalog 配置继续使用现有项目的配置

export.enabled=true
export.procedure-list=./sp-list.txt
export.function-list=./function-list.txt
export.table-list=./table-list.txt
export.output-directory=./output/ddl

excel-export.config=./excel-export.yaml

validation.enabled=true
validation.mode=VALIDATE
validation.database-id=FOS
validation.file=./datatype-validation.csv
validation.output-directory=./output/validation
validation.itsview.enabled=true
validation.itsview.schema=ITSVIEW
validation.baseline.compare.enabled=false
```

该示例的 Excel YAML 使用项目现有结构，例如：

```yaml
excel-export:
  enabled: true
  output-directory: ./output/excel
  include-header: true
  default-fetch-size: 1000
  overwrite: true
  simple:
    tables:
      - ITSVIEW.FOS_SHARE_DEAL
```

YAML 内 `simple.tables` 是当前项目已有的对象配置入口，可用于受支持视图的数据导出；这里只演示普通 Excel 功能独立配置数据源，不表示 ITSVIEW 是基本表。将 DDL 的 export.enabled 改为 false、将 Excel YAML 开关改为 false 或移除其引用、将 validation.enabled 改为 false，即可得到所需组合。多库校验沿用后文按别名配置 database-id/CSV/基线的示例。

## 1. 项目兼容与接入

- 保留现有 `com.example.db2toolkit.Main`、JAR 名称和启动方式：`java -jar target/db2-data-toolkit.jar <application.properties>`。
- 保留 DDL 导出、普通 Excel 导出、现有单数据库与多数据库配置、退出码及日志功能。
- 新增校验默认关闭。原有配置无需新增字段即可按原行为运行。
- 复用 `ConfigLoader.loadTargets`、`DatabaseTarget`、`AppConfig`、`ToolkitApplication`、`Db2ConnectionFactory`、`SafeDiagnostics` 和 `RunLogs`。
- 配置与接入以现有 `config/ddlexport/application-template.properties`、`config/ddlexport/application-multi-template.properties` 和 `config/combined/application-multi.properties` 为准：复用其中的连接、数据库别名、catalog、DDL 与普通 Excel 设置，不建立独立校验程序的配置体系。
- 校验放在 `com.example.db2toolkit.datatypevalidation` 下，按需要划分 config/model/parser/service/jdbc/excel；不要照搬一个独立应用的 Main、连接工厂和日志系统。
- 新增不可变 `DataTypeValidationConfig` 并挂入每个目标数据库的配置。更新配置复制方法，防止 `withExcel` 等操作丢失校验配置；保持现有调用及测试兼容。
- 同一数据库依次执行：DDL 导出 → 普通 Excel 导出 → 数据类型校验。三个功能可分别启用，校验不依赖 `excel-export.config`。
- 每个数据库打开一次连接，由三个功能复用，子服务不关闭共享连接。按 `db.names` 创建并行任务；每库一个连接，库内三个功能和查询保持串行。
- 修改“所有功能关闭”“是否需要连接”“校验独立运行时的日志目录”“功能结果汇总”等判断，使只有校验启用时也能正常工作。
- 连接可用时，一个条目或功能失败不阻止其余功能。连接断开时不要把后续条目判成成功，应记录未执行原因并继续下一个数据库。
- 不自动执行 ALTER、DDL、DML 或修改数据库对象，只执行必要的读取与聚合查询。

### 与现有实现逐项对齐的接入约束

- `export.enabled` 只控制 DDL，现有默认值是 true，不是总开关；普通 Excel 由被引用 YAML 内的 `excel-export.enabled` 控制。不要新增或假定存在 `excel-export.enabled` properties 键。校验独立示例必须显式设置 `export.enabled=false`，且不引用 Excel YAML；若组合配置仍引用 YAML，保持其原有加载和路径校验行为。
- 不把校验清单加入 `export.table-list`：该列表只用于既有基本表 DDL 导出。`validation.file` 是独立 CSV，view 数据校验不要求配置 DDL 清单。
- 保留既有 `readRequests` 只为 DDL 读取共享列表的行为。校验预检结果另按目标数据库保存，不能使用 `targets.get(0)` 的校验配置代替所有数据库。
- `ExcelExportService.export` 当前返回 boolean，表示连接是否仍可使用；保留这一返回约定，并将连接状态传给后续校验。不要看到 Excel 服务正常返回就认为连接可用，也不要看到任意 Excel 条目失败就认定连接断开。
- 沿用 `ConnectionFailureClassifier.connectionFailure` 与 `connectionLost` 对异常链、suppressed exceptions 及连接有效性的处理。DDL→Excel→校验之间传递连接状态，不引入自动重连或为校验重新开连接。
- 当前连接工厂只建立 JDBC 连接，不预设特殊事务/隔离级别。新功能不能为复用连接永久改变 autoCommit、隔离级别、schema 等会话设置；如确需改变，必须在 finally 中恢复。不要全局强制 WITH UR 或改动已有导出的读取语义。
- `ExcelWorkbookWriter` 面向 ResultSet/ExcelExportTask，工作簿私有，不能直接满足固定报告列、状态样式和隐藏基线表。新增专用 `DataTypeValidationWorkbookWriter` 与 `BaselineReader`，复用或最小抽取现有安全文本、临时文件发布等底层能力，不为复用而构造虚假的 ResultSet 或重写整个普通 Excel 模块。
- 共享的 `spreadsheet.ExcelText`、`ExcelValueWriter` 和 `StreamingWorkbooks` 已可供导出与校验复用；使用现有公共接口。原普通 Excel 的超长文本报错、不静默截取策略保持不变；校验报告的显式预览＋完整隐藏数据仅是新功能的展示规则。
- 校验不得继承普通 Excel 任务的 `maxRows`/`includeHeader`/`overwrite`：必须读取完整聚合结果、固定生成 11 列表头、每次产生新报告。fetchSize 仅影响读取批次，不能作为结果截断条件。
- 目录名语法在配置阶段检查；视图及列是否存在必须在对应目标数据库连接建立后检查，属于运行阶段。不要把数据库元数据查询混入“连接之前完成”的文件预检。

## 2. 多数据库配置协议

继续沿用已有连接配置：

```properties
# 单数据库
db.url=jdbc:db2://host:50000/DATABASE
db.username=user
db.password=replace_me

```

多数据库配置另用一个 properties 文件：

```properties
db.names=fos,rpt
db.fos.url=jdbc:db2://host:50000/FOSDB
db.fos.username=user
db.fos.password=replace_me
db.rpt.url=jdbc:db2://host:50000/RPTDB
db.rpt.username=user
db.rpt.password=replace_me
```

示例应分别展示这两种连接模式，不混合使用。实现上保留已有“存在 db.names 就采用多库模式，否则采用单库模式”的判断，不为了新功能改变旧键的处理行为。保留已有 `password-env` 支持及密码优先级；不得打印密码。

新功能专用设置统一使用 `validation.*` 命名空间，不引入裸键 `environment`、`output.file`、`itsview_enable` 或另一套连接配置。对象和列的元数据目录复用已有 `catalog.table.view` 与 `catalog.column.view`，具体规则见下文。

### 可配置的对象目录与列目录

必须在交付的基线/当前数据库示例配置中显式列出：

```properties
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
```

- 保留上述值作为兼容旧配置的默认值，但校验 SQL 必须读取解析后的配置，禁止在校验服务中写死 SYSCAT.TABLES 或 SYSCAT.COLUMNS。
- 支持数据库级覆盖，优先级为 `db.<alias>.catalog.table.view` / `db.<alias>.catalog.column.view` → 公共 `catalog.*.view` → 默认值；显式空值应报错。
- 例如 RPT 数据库可使用自定义目录视图（仅当这些视图实际存在且满足字段约定）：

```properties
db.rpt.catalog.table.view=MYCAT.OBJECTS
db.rpt.catalog.column.view=MYCAT.COLUMNS
```

- 在 ConfigLoader 中按目标数据库解析，复用 AppConfig 中已有的 `tables()` 和 `columns()`；同一目标的 DDL 与校验模块使用同一组解析结果。不要另加内容重复的 `validation.catalog.*` 设置。
- 这是对现有公共 catalog 配置的多数据库覆盖扩展。保留 `catalog.procedure.view`、`catalog.function.view` 原有配置行为，不改变存储过程/函数导出逻辑。
- 当前项目已支持上述数据库级 table/column catalog 覆盖；procedure/function 目录仍使用公共配置。沿用 `catalog.table.view` 这个配置名以保持兼容，名字中的 table 不代表校验只能检查基本表；默认 SYSCAT.TABLES 也记录视图。不需要另设 `catalog.view.view`。
- 校验模块对象存在性查询所需目录字段：TABSCHEMA、TABNAME、TYPE。列目录必须提供 TABSCHEMA、TABNAME、COLNAME、TYPESCHEMA、TYPENAME、LENGTH、SCALE、CODEPAGE，用于列存在性及实际类型兼容性检查；这些字段不是可选项。读取完整列清单时一并取得，避免每个字段重复查询元数据。
- 自定义目录必须覆盖待校验表和视图，并提供上述固定字段，不新增字段重命名映射。如果同时启用 DDL 导出，自定义目录还必须满足现有 DDL 提取器的完整字段约定。
- 使用现有安全标识符解析器处理配置的目录名；SQL 中只有通过校验的目录标识符可拼接，TABSCHEMA/TABNAME 等过滤值使用参数绑定。
- 自定义目录无权限、缺失、缺必要字段或执行报错时，记录 ERROR/元数据检查失败，不得把它当成业务表或列不存在，也不得自动回退到 SYSCAT。
- 配置目录返回多条匹配对象、重复列名、缺失必要值或无法解释的 TYPE 时视为元数据 ERROR，不随意取第一条。JDBC 读取可为空的数字元数据时必须区分 SQL NULL 与 0；CODEPAGE=0 不能单独用于判错，因为非字符串列也可能为 0。只有已识别为字符串类型时，才据此识别 FOR BIT DATA。
- 若自定义目录只包含部分对象，零记录只能证明该对象未出现在配置目录中；错误日志应说明“在配置的目录中未找到”，不能声称已检查整个数据库。

下列配置支持公共值及每个数据库覆盖：

| 公共配置 | 默认值 / 要求 |
| --- | --- |
| `validation.enabled` | `false` |
| `validation.mode` | 启用时必填，`BASELINE` 或 `VALIDATE` |
| `validation.database-id` | 启用时必填；基线/当前数据库共用的逻辑数据库标识 |
| `validation.file` | 启用时必填；CSV 文件路径 |
| `validation.itsview.enabled` | `false` |
| `validation.itsview.schema` | `ITSVIEW` |
| `validation.length-unit` | `OCTETS`；也可配置 `CODEUNITS32`，需目标 DB2 支持 |
| `validation.baseline.compare.enabled` | `false` |
| `validation.baseline.file` | 仅 VALIDATE 且比较启用时必填 |
| `validation.query-timeout-seconds` | `0`；非负整数，0 表示不设置额外查询超时 |

数据库级键的形式为 `db.<alias>.validation.<suffix>`，例如：

```properties
validation.enabled=true
validation.mode=VALIDATE
validation.itsview.enabled=false
validation.baseline.compare.enabled=true

db.fos.validation.database-id=FOS
db.fos.validation.file=./datatype-fos.csv
db.fos.validation.baseline.file=./baseline/fos-baseline.xlsx

db.rpt.validation.database-id=RPT
db.rpt.validation.file=./datatype-rpt.csv
db.rpt.validation.itsview.enabled=true
db.rpt.validation.itsview.schema=ITSVIEW
db.rpt.validation.baseline.file=./baseline/rpt-baseline.xlsx
```

解析规则：

1. 多数据库：数据库级显式值 → 公共显式值 → 默认值。存在但为空的必填值应报错，不静默继承。
2. 单数据库：使用公共 `validation.*` 配置，连接仍使用已有 `db.*`。
3. 只有启用校验的目标才要求校验必填项；关闭校验时不读取 CSV、基线或创建校验输出。
4. 每个数据库可使用不同 CSV，也可显式共享同一 CSV；绝不把所有数据库的条目混成一个列表再对所有库执行。
5. 单个 CSV 文件可以按规范化绝对路径缓存不可变解析结果，但每个数据库的执行结果必须独立。
6. 执行目标仍由现有唯一数据库别名标识；逻辑 database-id 使用 `[A-Za-z][A-Za-z0-9_-]{0,39}` 并统一大写。允许多个执行目标使用同一个逻辑 database-id（例如同一业务库的两个测试副本复用一份基线）；它们的连接、计数缓存、结果、日志和输出仍按别名隔离。每个当前目标只读取自己显式选定的一份基线，不能根据 database-id 把多个目标结果合并或交叉比较。
7. `database-id` 表示业务数据库身份，不是 JDBC URL、物理数据库名或环境名。BASELINE 的 FOS 与 VALIDATE 的 FOS 必须相同；属于不同业务库的 FOS 与 RPT 必须不同。同一业务库的多个测试副本可以相同。
8. 布尔值严格校验为 true/false，沿用现有 properties 布尔风格；枚举大小写可忽略，内部规范化。保留当前严格键检查：拒绝 db.*、validation.*、全局 catalog.*、export.* 和 excel-export.* 下的未知键；关闭功能也不能忽略拼错的键。
9. 所有配置文件中的相对路径都相对于该 `application.properties` 所在目录，不能依赖启动工作目录。
10. 以上路径规则指本次新增的 validation properties 路径。已有普通 Excel YAML 内的路径仍相对于该 YAML 本身，不改为相对于 application.properties。
11. 保留 properties 的 UTF-8/BOM 处理、数据库别名格式和大小写不敏感去重、显式密码优先于 password-env、密码不 trim、显式空密码报错等行为。不新增自动环境变量插值或另一套凭据继承规则。
12. 在读取可能输出用户文本的 CSV/基线之前，完成所有目标凭据解析并建立覆盖全部数据库的 SafeDiagnostics；清理预检异常后再交给顶层日志。不要直接打印 CSV 原始行、解析器上下文、基线内容或未清理的异常堆栈。
13. 新 validation 命名空间内拼错的键、引用不存在别名的 `db.<alias>.validation.*` 或新增数据库级 catalog 键必须给出预检错误，避免静默使用公共默认值。按现有 db.names 别名精确匹配键前缀，不新增大小写别名查找规则。
14. 条件配置的解析顺序固定：先解析 enabled；关闭的目标不校验 mode、file、itsview 或比较值。启用时解析 mode 和公共必要项；BASELINE 不解析/访问 baseline.file，但显式填写的 compare.enabled 仍须是合法布尔值，合法 true 仅提示该模式不比较。VALIDATE 的 compare.enabled=false 时同样不解析/访问 baseline.file。itsview=false 时不要求 schema 可用。旧草案键/未知键的名称检查仍按前述兼容规则执行。
15. query-timeout-seconds 的合法范围为 Java Statement 接口可接收的非负 int；不允许配置一个解析后溢出的超大秒数。单库和多库都使用同一规则。

全局输出配置单独定义，不提供数据库级覆盖：

```properties
validation.output-directory=../output/validation
```

默认值为 `./output/validation`。每个数据库每次运行创建独立目录：

```text
<validation.output-directory>/db-<alias>/run-<unique-id>/<alias>-datatype-validation.xlsx
```

上述为多库模式路径。单库模式沿用现有“不添加 db- 子目录”的惯例：`<validation.output-directory>/run-<unique-id>/<alias>-datatype-validation.xlsx`；单库已有 `db.name`（默认 default）仍用于日志和元数据别名。由配置规范化阶段只附加一次数据库子目录，writer 不重复附加。

禁止覆盖已有报告。复用 TemporaryOutputFile 的临时文件生命周期，完整写出后以不覆盖方式移动到最终路径；不要求所有文件系统支持原子移动，不允许悄悄覆盖同名文件。

日志目录的选择顺序明确为：DDL 启用时使用现有 DDL run 目录；否则普通 Excel 启用时使用现有 Excel run 目录；否则使用校验 run 目录。组合运行时校验报告可以有自己的 run 目录，但复用该数据库唯一的 RunLogs 作用域，并在日志中写出报告完整路径。

校验输出目录初始化失败时，校验结果应记为致命失败；若既有 DDL/Excel 的日志和输出仍可用，应继续这两个功能。不要因为提前创建新增输出目录失败而跳过原本可执行的旧功能。所有功能共用的日志初始化失败则按现有数据库级致命失败处理。

## 3. BASELINE 与 VALIDATE 运行模式

| 模式 | 比较开关 | 原始范围校验 | 读取已有基线 | 结果 |
| --- | --- | --- | --- | --- |
| BASELINE | 不参与执行判断 | 执行 | 不读取 | 生成基线，Compare=N/A |
| VALIDATE | false（默认） | 执行 | 不读取 | 生成当前校验报告，Compare=N/A |
| VALIDATE | true | 执行 | 必需并先预检 | 字符类型执行双向长度集合比较 |

- 推荐用两个独立 properties 文件：先执行所有 BASELINE 目标生成基线，再把各库基线复制到 VALIDATE 可访问的位置，然后运行 VALIDATE。
- 每个数据库生成一个工作簿，不把多个库合并成一个工作簿，不增加可见的 Database 列。
- VALIDATE 只连接当前配置的目标数据库，通过文件读取已生成的基线，不需要基线来源数据库的连接凭据，也不自动连接该来源数据库。
- BASELINE：始终进行原始范围校验，生成可被读取的基线工作簿；Compare Status 为 `N/A`；完全不读取基线文件，即便配置了比较开关或无效基线路径。
- VALIDATE，比较关闭：始终进行范围校验，不读取基线；Compare Status 为 `N/A`，两个差集单元格为空。
- VALIDATE，比较开启：先在启动预检中加载并验证指定基线；运行时完成范围查询后计算字符类型长度集合差异。
- 所有启用目标的配置、CSV 和必需基线先预检，任何这类错误导致整个批次在连接数据库之前退出，退出码 2。运行期间某数据库连接/查询失败才使用数据库间失败隔离。
- 第一版不提供“按文件修改时间找最新基线”或“本批次内自动引用前一个 BASELINE 的报告”。要求显式指定已存在、已选定的基线文件，避免用错版本及顺序依赖。
- 启用校验但 CSV 只有表头或没有有效条目时，作为配置预检错误退出，不生成看似成功的空基线；“空校验清单”与“待校验视图中有零条数据”必须区分，后者是正常范围 PASS。

## 4. 校验清单与名称解析

使用标准 CSV，必须正确处理引号、逗号和 UTF-8 BOM，不得使用简单 `split(",")`。固定以逗号分隔、双引号包裹含逗号的字段；CRLF/LF 均支持。表头及配置字段两端空白可去除，表头规范化为小写后按名称匹配，允许列顺序改变；必须恰好有四个必填列，可另外有 length_unit。重复/缺少/未知表头、非空记录列数不符或非法字段值都属于预检错误，不能跳过坏行后继续生成看似完整的报告。真正的空行可忽略，全空字段的 CSV 记录应报错；错误指出文件和记录位置，但不原样打印整行。

```csv
table_name,field_name,original_data_type,after_data_type
fos.share_deal,deal_number,"DECIMAL(5,0)","DECIMAL(20,0)"
fos.share_deal,reference,VARCHAR(50),VARCHAR(65)
fos.share_deal,comments,CLOB(1000),CLOB(5000)
rpt.account_entry,description,VARCHAR(100),CLOB(1000)
```

- 四列必填。同一数据库清单中重复的逻辑表名＋字段名应明确报错，不能后一个静默覆盖前一个。
- 已确认同一业务对象的 table 与 ITSVIEW view 使用相同 column name。只转换对象名，直接使用规范化后的 CSV `field_name` 查询和匹配；不增加 `query_field_name`、列名映射或自动猜测替代列的功能。视图缺少该列时报告 COLUMN NOT FOUND，不回退到底层表。
- 支持可选的 `length_unit` 列，为某行覆盖目标数据库的 `validation.length-unit`；四列旧格式无需增加此列，空值使用目标配置，DECIMAL 行留空。规范化后的单位要写入隐藏元数据并参与基线兼容性检查。按路径缓存时只缓存原始解析定义，不能缓存应用了某一数据库默认单位后的结果。
- 第一版仅支持 DECIMAL(p,0)、VARCHAR(n)、CLOB(n)，类型解析大小写不敏感；拒绝非法精度、非零 scale、非法长度和未支持的表达形式，不擅自猜测。
- 首版 CSV 数值边界明确：DECIMAL precision 为 1..31 且 scale 必须为 0；VARCHAR 的 n 为 1..32672（OCTETS）或 1..8168（CODEUNITS32）；CLOB 的 n 为 1..2147483647（OCTETS）或 1..536870911（CODEUNITS32）。先验证数值字符串及界限再转换，不允许整数溢出；不支持 K/M/G 后缀。本版每行的 length_unit 同时解释原始/目标字符类型的 n，不支持同一行变更计量单位。
- 允许 DECIMAL→DECIMAL、VARCHAR→VARCHAR、CLOB→CLOB、VARCHAR→CLOB；拒绝上述范围以外的转换。相同单位下拒绝目标 precision/长度比原定义更小的配置；相等可用于复核。
- 规则始终使用 CSV 中 ORIGINAL 类型与原长度单位。`after_data_type` 用于记录计划变更和检查配置，不替代原始阈值。
- 本功能证明的是数据是否超出旧范围以及长度集合差异，不等于证明数据库的实际列定义已变更为 `after_data_type`，也不验证逐行数据一致性。
- 首版校验表名限定为普通 `schema.table`，字段为普通单段标识符，验证后统一大写。明确拒绝不支持的带引号标识符；不要改变现有 DDL 模块对标识符的支持范围。
- 为上述首版范围明确普通标识符为 `[A-Za-z_][A-Za-z0-9_@$#]*`，与现有 Identifiers 对 ASCII 普通名称的规则一致。复用其解析、规范化和安全 SQL 引用能力，但不要把要求两个名称组件的 `Identifiers.parse` 直接用于单列名。对单列名及 itsview schema 使用专用单段验证。
- 不允许把任意 SQL、WHERE 或 SELECT 填进表名字段。阈值用 PreparedStatement 参数；标识符按校验后的组件安全构造。
- 建立每个数据库独立的 DataTypeValidationObjectResolver，实例包含其 itsview 设置，公开 `resolveQueryObject(String configuredTableName)`，不依赖全局可变配置。
- itsview=false：`fos.share_deal` → `FOS.SHARE_DEAL`。
- itsview=true：`fos.share_deal` → `<itsview_schema>.FOS_SHARE_DEAL`。
- 转换必须检查名称有效性和冲突。例如 `A_B.C` 与 `A.B_C` 都会映射到 `ITSVIEW.A_B_C`：同一目标的两个不同逻辑对象发生此类碰撞时，在连接前报配置错误，不合并为同一组校验。不要截断过长的名称或重复转换已经带有 ITSVIEW 的名称；CSV 始终填写约定的逻辑 schema.object。
- 在此需求中，ITSVIEW 是视图所在的 schema；`ITSVIEW.FOS_SHARE_DEAL` 是实际查询的视图，不是基本表。itsview=true 时必须对该视图执行聚合，不能读取底层表代替，也不能将它送入现有 TableDdlExtractor 做基本表 DDL 检查。
- 名称解析后，使用配置的对象目录检查 TYPE；itsview=true 时预期为普通视图 V 或 typed view W。对象不存在按原缺对象状态处理，存在但不是受支持的视图则记录 ERROR 并说明类型不匹配；失效视图不能当成不存在。itsview=false 时按 CSV 指定的对象查询，允许基本表或受支持视图，不反向推测底层表。
- Excel 的 Table Name 显示 CSV 的逻辑 schema.object；隐藏 _items 保存物理查询对象，日志显示实际查询对象。匹配键使用逻辑身份，不能使用转换后的物理表名。
- `catalog.table.view` 和 `catalog.column.view` 共同用于 DDL 元数据及校验对象/列检查；`validation.itsview.*` 控制实际校验的数据表或视图。先解析物理对象名，再在当前数据库配置的目录中查找，不能把 itsview_schema 当成元数据目录 schema。
- 存在性检查同时支持普通表和普通视图，不能用 TYPE='T' 过滤掉视图；检查视图实际暴露的列，而不是其底层表的列。
- 同一数据库、同一次运行内，每个物理对象的对象信息及完整列清单可缓存复用；不得跨库共享检查结果。
- 对象与列存在不保证可查询，实际聚合仍需独立处理权限、失效视图、依赖对象、超时和连接等错误。

## 5. 数值与字符串校验

### 查询前检查实际字段类型的兼容性

- 使用已配置的列目录读取实际查询对象（ITSVIEW 开启时就是 view）暴露的类型；不能读取底层表类型代替视图类型，也不能仅根据 CSV 就直接执行聚合。
- 本版明确采用以下兼容范围：原规则为 DECIMAL(p,0) 时，实际字段允许内置 DECIMAL/NUMERIC 且 scale=0，或内置 SMALLINT/INTEGER/BIGINT；原规则为 VARCHAR/CLOB 时，实际字段允许内置字符 VARCHAR/CLOB。VARCHAR→CLOB 的计划变更在实际字段仍为 VARCHAR 或已变为 CLOB 时，都可执行原始长度校验。
- 非零 scale 的 DECIMAL、浮点数、字符数字、日期、二进制字符串及未支持的用户自定义类型不通过数值兼容检查；二进制字符串（包括 FOR BIT DATA）及其他未列出的类型不通过字符兼容检查。利用类型 schema、类型名、scale、codepage 等元数据区分，不能自动 CAST 将不兼容类型转为期望类型。
- 字段确实存在且元数据完整，但实际类型不兼容：Range Status=`TYPE MISMATCH`，计数、Detail、Compare 和差集留空；日志记录逻辑及物理对象、字段、原始类型、实际类型及原因，继续其余字段。该行不是有效基线数据，功能退出码至少为 1。
- 类型元数据缺失、含糊或查询失败时使用 ERROR，不能猜测类型或误报 TYPE MISMATCH。
- 兼容检查只决定能否安全执行该种校验；不检查实际长度/precision 必须等于 `after_data_type`，也不能以当前较大的长度覆盖 CSV 原始阈值。实际列定义是否精确符合计划变更不属于本版功能。

### 同一 table/view 的总记录数只计算一次

- 先按“当前数据库目标＋规范化后的物理查询对象”组织校验项。一个对象配置多个字段时，共享对象元数据、列清单和一次成功获取的 Record Count；不同数据库、不同物理对象、不同运行之间不共享。
- 不允许在每个字段开始前执行 `SELECT COUNT(*) FROM ...` 或 `SELECT COUNT_BIG(*) FROM ...`。优先从该对象首个成功且完整的字段聚合取得总数，避免增加一次独立的全对象计数查询。
- 如果首个成功字段是 VARCHAR/CLOB，将其所有长度桶（含 NULL 桶）的 COUNT_BIG 求和取得总数；如果是 DECIMAL，在用于取得总数的该次数值聚合中同时查询 COUNT_BIG(*)。取得总数后，后续 DECIMAL 聚合只查溢出数、MIN、MAX，不再次查询全对象 COUNT_BIG(*)。
- 对象级计数只缓存完整成功的结果；失败或未执行不能缓存为 0。若首个字段失败但连接仍可用，可由后续成功字段建立计数，不自动反复重试失败 SQL。整组都失败时总数保持未知。
- VARCHAR/CLOB 的 `GROUP BY LENGTH(...)` 中每个桶的 COUNT_BIG 仍必须计算，因为这是该字段的分布计数，不能拿其他字段的桶计数代替；它不等于重复执行独立的全对象计数查询。
- 所有成功行复用对象级 Record Count；缺列、错误及未执行行按报告规则保持计数空白。可先计算对象内结果，再按原 CSV 顺序输出，不能因分组而任意重排报告。
- 复用总数不保证多个 SQL 读取同一个数据快照，不因此宣称整张表只扫描一次。记录对象校验起止时间。如果后续字符字段的完整分布计数之和与共享总数不同，或观察到溢出数大于共享总数等矛盾，明确记录“对象在校验期间的查询结果不一致”。
- 为避免同一对象一部分行已判 PASS、另一部分因共享总数失效而报错，应在该对象的全部字段处理完成后最终确认结果。发现上述矛盾时，该对象已完成聚合的候选行统一转为 ERROR，Compare/计数/Detail 留空，相关分布不能进入有效基线；原先缺列、类型错误、未执行等状态保留。原因保存在日志及隐藏条目记录中。不重新 COUNT、不重复扫描以追赶变化，也不修改共享总数来掩盖问题。
- 上述检查只能发现可观测矛盾，不保证检测到记录数未变的数据修改，也不能完整识别后续 DECIMAL 查询期间的行数变化。需要一致快照的使用场景应在稳定数据窗口运行；本版不自动启用锁表或长事务。

DECIMAL(p,0)：

- 旧范围为 `-(10^p-1)` 到 `+(10^p-1)`，使用 BigInteger/BigDecimal，不用 double 或 long 表示边界。
- 用聚合 SQL 获取溢出数、MIN、MAX；总记录数按上述对象级规则仅在需要建立共享计数时一并取得。计数采用 COUNT_BIG，溢出累加采用足够精度的 DECIMAL，避免大表的 32 位计数溢出。DB2 COUNT_BIG 返回 DECIMAL(31,0)；JDBC 用 BigDecimal 读取并精确转换为 BigInteger 计数，MIN/MAX 使用 BigDecimal，不做浮点转换。
- 空表总数和溢出数均为 0，MIN/MAX 显示 NULL；NULL 值不溢出。
- SQL 阈值参数显式使用 `CAST(? AS DECIMAL(31,0))` 并通过 setBigDecimal 绑定，避免依赖驱动从实际 SMALLINT/INTEGER 等列推断较窄的参数类型。这里只给已验证的数值阈值指定类型，不是将不兼容的业务字段 CAST 成数值。
- 溢出聚合使用 `COALESCE(SUM(CAST(CASE WHEN <column> < CAST(? AS DECIMAL(31,0)) OR <column> > CAST(? AS DECIMAL(31,0)) THEN 1 ELSE 0 END AS DECIMAL(31,0))), CAST(0 AS DECIMAL(31,0)))` 或经等价测试的表达式。不要先用整数 SUM 溢出后才 CAST；空表 SUM 返回 NULL 时必须显式转为 0。MIN/MAX 保留 SQL NULL，不用 0 代替。
- Detail：`MIN = -120, MAX = 99888`；Range Status 按溢出数为 0/大于 0 分别 PASS/FAIL。
- DECIMAL 聚合成功且通过对象级一致性确认时，Compare Status 为 N/A，不比较具体数值；缺列、TYPE MISMATCH、ERROR、NOT EXECUTED 等没有有效结果的行 Compare 留空，按统一状态优先级处理。

VARCHAR/CLOB：

- 共用 `Db2DataTypeQueries.lengths` 和 `LengthSetComparator`，不能为 VARCHAR/CLOB 分别实现两套校验规则。
- 只在 DB2 中聚合长度和计数，例如 `LENGTH(column, OCTETS)` 与 `COUNT_BIG(*)` 按长度分组；根据已确认的 length-unit 生成受支持的长度表达式。
- 不能省略长度单位后假定基线/当前数据库的 LENGTH 语义相同。明确单位指字节还是 Unicode code units；配置单位必须与 CSV 的原始长度含义一致。
- 不查询、下载、缓存或比较完整 CLOB 值，不用 getClob 或 getString 读取完整 CLOB 内容。聚合查询可能仍有全表扫描成本，不声称它对基线来源数据库或当前数据库没有影响。
- 从同一组长度聚合结果计算 Overflow Count 和分布，并在对象共享总数尚未建立时由桶计数之和取得 Record Count；总数已存在时复用，同时使用分布之和做一致性检查，避免为了这三个指标反复扫描同一字段。
- NULL 为独立桶，排在最前，随后非负长度递增；NULL 与长度 0 不等价，NULL 计入 Record Count 但不计入 Overflow Count。
- 溢出数为所有 `length > originalMaxLength` 的桶计数之和。
- Detail 的长度和计数固定占前两行并启用 wrap text；第 7 节的 VARCHAR 单一长度提示命中时，另加第三行：

```text
DISTINCT LENGTH = (NULL, 12, 20, 45, 50, 51)
DISTINCT COUNT  = (5, 95, 250, 400, 248, 2)
```

- 空表显示两组空列表 `()`，两个计数均为 0，Range PASS。
- 全 NULL 与空表必须区分：例如 5 行全为 NULL，Record Count=5、Overflow Count=0、长度列表 `(NULL)`、计数列表 `(5)`，Range PASS；空表的两个列表均为 `()`。零长度字符串则属于长度 0 桶，不能合并到 NULL 桶。
- 比较测试必须包含：空集合与 `{NULL}` 为 DIFFERENT；两边均 `{NULL}` 时，即使各自 NULL 行数不同也为 Compare PASS；两边均空集合时为 Compare PASS。DECIMAL 全 NULL 时记录数大于 0、MIN/MAX 为 NULL、溢出数为 0。
- 实现每目标继承的 `validation.query-timeout-seconds`；0 不设置额外超时，大于 0 对元数据和聚合查询的新建 Statement 设置超时。查询超时记录为 ERROR，并根据连接实际状态决定是否继续，不能吞掉超时后判 PASS。
- 每字段记录执行结果和 elapsedMs，包括成功、失败、超时和未执行（未执行耗时为 0）；每个物理对象及数据库记录开始/结束时间。保持同一数据库的字段串行处理，多个数据库并行运行，缓存元数据与对象总数；第一版不增加并发扫描或复杂的多字段 SQL 合并，也不将 fetchSize 描述为减少数据库扫描量的保证。

## 6. 基线结构和匹配

- 唯一键：`logical database-id + normalized logical schema/table + normalized field`，使用结构化字段而不是容易碰撞的字符串拼接。
- 即使不同数据库存在相同 `FOS.SHARE_DEAL.REFERENCE`，也不能发生跨库匹配。
- 工作簿只提供一个主报告表，另使用隐藏且结构化的元数据表保存可机器读取的数据。
- 元数据包括：格式版本、mode、database-id、执行别名、生成时间、逻辑表/字段、原始/目标类型、长度单位、条目执行状态、NULL 桶及完整长度计数分布。
- 不从可见 Detail 的展示字符串中反向解析基线，不使用浮点 Excel 数值存储需要精确保留的高精度值或超出 Excel 精确数值能力的计数。元数据中的这些值按十进制字符串保存并严格解析。
- 读取时检查格式版本、生成模式为 BASELINE、database-id、重复键、数值合法性及分布完整性；损坏或不兼容工作簿视为预检错误。不得将 VALIDATE 模式生成的报告重命名后直接当作基线，reader 必须检查隐藏元数据中的 mode。
- 基线读取且校验通过后，日志输出当前数据库别名、基线规范化路径、工作簿元数据中的生成时间、逻辑 database-id、基线条目总数及有效/无效条目数，供使用者确认选对文件。生成时间不是文件修改时间；信息必须经过 SafeDiagnostics 清理，不能额外打印连接凭据或数据内容。
- 文件预检发生于每库 RunLogs 建立之前，可以输出一次已清理的控制台信息；进入该数据库执行作用域后，在 <alias>-process.log 中记录其采用的同一份基线身份信息。不自动发现或选择最新基线，不自动以其他文件代替配置文件。
- 隐藏元数据除生成时间外保存来源数据库本次校验的开始/结束时间；当前报告保存当前校验时间范围。文档说明不同时间的正常数据变化可能导致 DIFFERENT，即便多字段总数相同，也不能据此认定所有查询来自同一数据快照。
- VALIDATE 中需要长度比较且基线也存在的字符字段，原始/目标类型及长度单位必须兼容，否则明确报预检错误，不把不同定义当作可比较集合。
- 上述跨报告字段定义兼容检查仅针对本次需做长度比较的 VARCHAR/CLOB 项，规则为规范化后的 original_data_type、after_data_type、length_unit 分别相同；不要求实际物理类型或视图名相同。DECIMAL 不做逐项匹配或跨报告类型比较。VALIDATE 且比较启用时，即使清单只有 DECIMAL，仍按显式开关要求预检基线文件的整体格式及 database-id，但 DECIMAL 成功行保持 N/A。
- 对于单个缺失匹配项，Range 校验照常执行，Compare Status=`BASELINE NOT FOUND`，继续其他条目。
- 基线中查询失败或未执行的条目不能当成空集合；相应 VALIDATE 字段 Compare Status=`BASELINE INVALID`，记录原因并继续。
- 基线中 Range FAIL 但聚合成功且完整的条目允许参与比较；Range FAIL 与基线不可用不是同一件事。
- 基线中不在本次 VALIDATE 清单里的额外条目不自动添加主报告行，可记录汇总信息。
- 字段查询中途失败时必须丢弃该字段不完整的分布，并标记聚合未完成；不能把已读到的一部分长度写成有效基线。数据为零行但查询完整成功则保存明确的完整空集合，区别于失败/未执行。
- 缺对象、缺列、其他查询错误和连接中断导致未执行的行仍应出现在报告中；连接断开后的剩余行使用 Range=`NOT EXECUTED`、Compare 留空，完整原因写日志及隐藏元数据。不要丢失已成功的其他行，也不要给未执行行填零计数或 PASS。

### 固定的基线格式与完整性规则

- 为避免 writer 和 reader 各自猜测结构，定义 formatVersion=1：可见主表为 `Validation`；隐藏 `_meta` 为 key/value；隐藏 `_items` 按条目存储结果；隐藏 `_lengths_001` 起保存完整长度分布，必要时按数字编号连续续表。续表清单、行数、条目总数和每个条目的预期桶数由元数据显式登记，编号缺失、总数不符均为损坏文件。
- `_items` 每行使用独立的 databaseId/logicalSchema/logicalObject/field 字段组成唯一身份，同时记录原始/目标类型、lengthUnit、物理对象、实际类型、rangeStatus、aggregationComplete、recordCount、overflowCount、bucketCount 和错误原因。DECIMAL 另存 min/max（允许 NULL），字符项不使用这两个字段。
- 失败/未执行条目仍必须保存来自 CSV 的身份及类型定义、已解析的物理对象、Range 状态和 aggregationComplete=false；尚未取得的实际类型、recordCount、overflowCount、min/max 留空，bucketCount=0。不能因为这些符合状态约定的空值就把整个诊断基线误判为损坏。成功字符项必须有非负记录/溢出计数及准确桶数；成功 DECIMAL 的 bucketCount=0，不生成长度记录。
- `_lengths_*` 每行引用条目身份，包含 isNull、length 和 count；NULL 桶用 isNull=true 且 length 空白表示，长度 0 必须是 isNull=false、length=0。计数必须为正整数，长度非负，同一条目不允许重复桶或多个 NULL 桶。正常空集合用完整条目＋bucketCount=0 表示，不能仅因未找到长度行就当成空集合。
- 当前校验报告的 `_items` 另存 compareStatus、baselineOnlyCount、currentOnlyCount；两个差集的完整值分别按条目身份、side（BASELINE_ONLY/CURRENT_ONLY）、isNull、length 存入隐藏 `_diffs_001` 起的连续续表。按 NULL 在前、随后数值升序保存，同一条目/side 的值不能重复；表清单和行数登记在 `_meta`。无差异时两个差集数量为 0；不比较/比较不可用时数量留空且无差集行。BASELINE 模式也使用这些列，Compare 为 N/A 或按错误状态留空，无差集行。基线 reader 不从 `_diffs_*` 读取待比较的原始长度集合。
- `_meta` 的 key 不得重复；长度记录和差集记录必须引用存在的 `_items` 身份，拒绝孤立记录。所有相关表有固定列名及顺序，writer/reader 共用格式常量，首版文档列出实际使用的完整列清单。
- 所有机器读取的元数据值使用规范化文本保存，数字用精确十进制整数/小数字符串，布尔值用 true/false，时间用带时区的 ISO-8601。reader 不执行 Excel 公式，不使用缓存公式结果当基线数据；遇到不符合协议的单元格类型或字段值报格式错误。
- 完整字符分布的 count 之和必须等于 recordCount，超出旧阈值的 count 之和必须等于 overflowCount；Range PASS/FAIL 必须与溢出数一致。错误或未执行条目必须 aggregationComplete=false，且不能附带可被使用的长度分布。仅业务失败且分布完整的 Range FAIL 可以 aggregationComplete=true。
- 预检后后续比较必须使用已经验证过的同一份基线数据快照。允许内存不可变结构或磁盘暂存；不能预检一次文件后在执行时未经校验重读可能已被替换的原文件。关闭输入句柄及清理暂存的责任明确归属于本次运行。

## 7. 比较、报告和高亮

主报告严格使用以下 11 个可见列，保持顺序：

```text
Table Name
Field Name
Original Data Type
After Data Type
Record Count
Overflow Count
Detail
Range Status
Compare Status
Baseline Only Length
Current Only Length
```

- VARCHAR/CLOB 仅比较 distinct length 集合，不比较每个桶的 COUNT 或总记录数；NULL 参加集合运算。
- `Baseline Only Length = 基线长度集合 - 当前长度集合`；`Current Only Length = 当前长度集合 - 基线长度集合`。
- 两个差集都为空：Compare PASS；任意一个非空：Compare DIFFERENT。两个方向都必须实现并高亮。
- Range 与 Compare 独立计算，不能用一个覆盖另一个。
- 在使用说明和 Range Status / Compare Status 表头批注中明确语义，不增加可见列：Range PASS=没有数据超出原始类型范围；Range FAIL=存在超出原始类型范围的数据，并不等于扩容失败；Compare PASS=基线与当前 distinct length 集合相同（包含 NULL），不代表计数、逐行内容或实际类型定义相同；DIFFERENT=两个长度集合存在差异，不自动归因为变更失败。
- 例如 VARCHAR(50)→VARCHAR(100) 后出现长度 60，Range FAIL 是“超出旧范围”的正常判定，不说明新 VARCHAR(100) 定义有问题。此示例必须写入说明文档。
- Range FAIL：高亮 Overflow Count 和 Range Status。
- Compare DIFFERENT：高亮 Compare Status 及非空的两个差集单元格。
- Range/Compare PASS 使用浅绿色；FAIL/错误使用浅红色；DIFFERENT 及非空差集使用浅黄色；N/A 使用浅灰色。单一长度提示突出 Detail 的浅蓝色，完整 RGB 定义见 DATATYPE-VALIDATION.md 的 Report colors。
- 缺表：Range=`TABLE NOT FOUND`；缺列：Range=`COLUMN NOT FOUND`。计数、Detail、Compare 和差集均留空。
- 其他查询错误：Range=`ERROR`，日志记录清晰原因；不能把权限、超时、连接、SQL 语法错误统称为缺表或缺列。
- 错误行（包括 ERROR、NOT EXECUTED）没有有效聚合数据时，计数、Detail、Compare 和差集均留空。如果全库查询均失败但输出系统正常，也应生成包含全部失败行的诊断报告，隐藏数据明确标记每项不可作为有效比较基线。
- 使用准确的 JDBC/DB2 错误分类；如果仅凭异常不能判断缺表还是缺列，用受控的元数据/零行查询确认，不通过异常消息中出现某个字符串就猜测。
- 表格中的非计数文本用字符串单元格写入，不当作 Excel 公式。
- 可见 Record Count/Overflow Count 也必须精确：沿用现有 ExcelValueWriter 的数值策略，超过 15 位有效数字的计数以十进制文本写入，不能先转 double。MIN/MAX 使用 BigDecimal.toPlainString 等精确文本，隐藏基线始终使用上述文本协议。

VARCHAR 单一长度与基线一致时的辅助提示：

- 按 CSV 的 original_data_type 判断是否为 VARCHAR，包括 VARCHAR→VARCHAR 和 VARCHAR→CLOB；实际字段已变为兼容的 CLOB 时仍适用。原始类型为 CLOB 或 DECIMAL 时不添加此提示，不新增配置开关或可见列。
- 仅在 VALIDATE 且基线比较启用、双方分布完整有效并通过对象级最终确认、Range PASS 且 Compare PASS 时判断。双方完整长度集合必须严格等于同一个 `{L}`，其中 L 为非 NULL 的非负长度；即两边各有且只有一个长度桶，且长度相同。使用已有完整聚合与基线数据，不增加 SQL、COUNT 或重新扫描。
- 空集合、全 NULL 的 `{NULL}`、包含 NULL 的 `{NULL,L}`、多个非 NULL 长度、基线缺失/无效及任意错误状态都不添加提示。长度 0 可以命中，但表示两边只有零长度字符串，不表示业务内容完整。桶计数或总记录数不同不影响提示，继续遵守仅比较长度集合的规则。
- 命中时保留 Detail 前两行分布，在第三行追加固定文本 `HINT = SINGLE LENGTH MATCH (LIKELY OK)`，并仅为 Detail 单元格使用浅蓝底色、保持换行且行高能显示三行；不改 Range/Compare 状态、差集、退出码或原有汇总计数。未命中时不显示该行，也不据此新增失败状态。
- 这是用户指定的经验提示，含义为“单一长度与基线一致，大概率符合预期”，不是统计计算得到的正确率。在使用说明和 Detail 表头批注中明确：它仅反映长度分布一致，不证明逐行内容、记录数或类型变更正确；即使 Range FAIL 且集合相同也不显示 LIKELY OK，原范围失败仍完整保留。
- 提示在 Range/Compare 最终确认后派生，只影响可见报告；隐藏表仍保存原有完整分布和状态，不增加基线字段或更改 formatVersion，BaselineReader 不解析或依赖提示文字。

例如原始类型为 VARCHAR(50)，基线只有长度 20、计数 100，当前只有长度 20、计数 120；Range 与 Compare 均为 PASS，当前 Detail 显示：

```text
DISTINCT LENGTH = (20)
DISTINCT COUNT  = (120)
HINT = SINGLE LENGTH MATCH (LIKELY OK)
```

状态判定顺序必须唯一：

1. 未执行或元数据/类型/查询/对象一致性错误：使用对应 Range 状态，Compare 留空，不计算差集。
2. 聚合完整：计算 Range PASS/FAIL。BASELINE、比较关闭或 DECIMAL 时，Compare=N/A。
3. VALIDATE 且字符比较启用：匹配项缺失时 BASELINE NOT FOUND；存在但没有有效聚合时 BASELINE INVALID；只有双方完整时才计算 Compare PASS/DIFFERENT。Range FAIL 不阻止此步骤。

其中 TABLE NOT FOUND 沿用原需求的状态名称，包含“配置目录中找不到所需 table/view”；日志须写明预期是 view 还是 table，不把失效视图或目录错误当作不存在。

长内容处理是本版对原需求的必要明确化：Excel 单元格有长度限制，不能保证任意大的 distinct length 集合都塞进一格。

- 正常情况下 Detail 的前两行严格使用上述完整列表；仅命中 VARCHAR 单一长度提示时追加约定的第三行，其余条目保持两行。
- 超出单元格上限时，展示成对截取的预览，两行位置仍对应，并明确标注已截取及完整数据所在隐藏表；差集展示超长时同样标注。
- 完整分布和差集逐条写入隐藏数据表，达到工作表行数上限时使用编号续表，禁止静默丢失。
- 比较和 Range 判定必须使用完整数据，不能使用被截取的显示文本；隐藏表和 reader 必须支持续表。
- 一个可见主表最多容纳 1,048,575 条校验项（另有一行表头）；超出时在预检阶段明确报错，不静默省略、不擅自增加可见续表。隐藏明细表的续表不受“一个可见主表”限制。
- CLOB 值很大与 distinct length 种类很多是两个不同问题。不能在内存中长期保留所有数据库/所有字段的全部分布或构造完整超长 Detail 字符串。采用有界展示缓冲、流式写入与按需磁盘暂存；对象级结果最终确认前的临时分布也可暂存到磁盘。基线 reader 应支持顺序读取/暂存索引，不要求把整个大工作簿加载到 XSSFWorkbook。
- 以上资源策略必须保证数据完整，不得以 maxRows、取前 N 个桶或截断集合规避内存问题。报告发布前的暂存、磁盘或工作簿写入失败按功能输出失败报告，校验功能退出码为 2，不发布有效基线。
- 缓存复用 POI 样式，关闭工作簿并清理临时文件；写失败不得留下可被误用的完整基线文件。
- 报告发布成功前不能把校验条目标记为最终成功；发布失败必须在功能汇总中体现。关闭清理失败发生在发布之后时，保留“报告已发布”的事实并报告清理失败，不覆盖已经完成的条目或重复计数。

## 8. 错误、退出码和日志

- 0：所有启用功能成功，校验无范围失败、集合差异或不可用项。
- 1：运行已完成但存在 Range FAIL、TYPE MISMATCH、Compare DIFFERENT、缺表/缺列、基线条目缺失/无效、未执行或其他条目错误。
- 2：配置/预检、连接初始化、输出或日志初始化等致命错误；新增 validation 的结果暂存、工作簿写入、报告发布及输出资源关闭/临时文件清理失败也明确归为 2，即使所有 SQL 校验结果都是 PASS。报告发布后发生关闭或清理失败时，保留已发布报告及原条目统计，记录功能级错误并返回 2，不能改写为发布失败或重复计数。原 DDL/普通 Excel 的错误分类保持不变。
- 每库最终退出码与整个批次退出码都取各功能/各库结果中的最高严重级别，不能让 DDL 成功覆盖校验失败。
- 预检失败不开始整个批次；运行阶段的一个数据库失败不阻止下一个数据库。
- 分别统计每库和全局的总条目数、Range 各状态、Compare 各状态、查询失败及未执行条目。两类状态不是互斥的统一分类，不要错误地相加为条目总数。
- 每条日志包含现有数据库别名上下文；密码及敏感连接信息经过既有 SafeDiagnostics 清理。

现有日志接入不能只写“复用日志”，还必须实现以下内容：

- 新增 `DataTypeValidationSummary`（或同等职责类）维护 Range/Compare、查询完成状态、报告发布状态和致命错误；不要把两个独立校验状态塞入原 DDL ExportStatus 或普通 Excel 的 SUCCESS/FAILED/SKIPPED 三态中。
- Summary log routing must use explicit SUMMARY, SUMMARY_SUCCESS and RUN_OUTCOME markers; do not depend on logger class names or formatted message text. Validation summary events must reach the appropriate summary files, and run outcomes must reach both summary files.
- 延续 `RunLogs.SUCCESS` 显式 marker：条目 Range PASS 且 Compare PASS/N/A、聚合完整且报告已发布才进入 <alias>-summary_success.log；范围失败、比较差异、缺失、错误及未执行进入 <alias>-summary_issue.log。不能按消息包含 PASS/SUCCESS 等文字路由。
- 保留 MDC database、线程归属过滤、三个日志文件命名、最终结果同时写入两个摘要及 writeFailure/close 失败上报机制。不要通过修改最终日志固定前缀破坏现有过滤和测试。
- 独立维护新功能的 0/1/2 退出码，再与原 DDL/Excel 结果取最大值。不改变原模块现有的错误级别，例如原普通 Excel 单个工作簿写失败仍按其原有退出码处理。
- 新校验服务报告结果后，连接关闭异常、日志写入异常仍必须抬高或保留最终错误级别；不能吞掉清理异常、覆盖已发布报告的事实或把同一条目计数两次。

## 9. 必要测试与交付

保留原有测试并增加：

- 旧单库/多库配置在未启用 validation 时的回归；只有校验启用也会连接及生成报告。
- 公共值/数据库覆盖/缺省/空值/禁用目标/相对路径；每库不同 CSV。
- 可配置目录及数据库级覆盖被实际用于元数据 SQL；普通视图及其暴露列可识别；目录错误不误判为业务对象缺失；自定义目录失败不回退 SYSCAT；旧的公共 catalog 配置继续兼容 DDL 导出。
- itsview=true 时只查询解析后的视图，不查询同名底层表；视图列别名按视图暴露名称检查；错误对象类型和失效视图不能误判为缺表；校验不复用 TableDdlExtractor 的基本表限定。
- BASELINE 不读基线、VALIDATE 关闭比较不读基线、开启比较缺文件时在任何连接前失败。
- 模式与 JDBC URL/别名无关；错误 mode 被拒绝；VALIDATE 报告不能当作 BASELINE 输入；过时的校验配置键有明确迁移提示；报告列、状态和元数据全部采用本版名称。
- 多库相同表字段互不串库；别名不同但相同逻辑数据库 ID 的基线/当前数据库可对应；错误 ID、重复元数据、类型或单位不兼容被拒绝。
- CSV 中带逗号的 DECIMAL 引号、重复条目、BOM、非法标识符和类型。
- DECIMAL 的正负边界、超出 long 范围的精度、NULL、空表；VARCHAR/CLOB 原始边界与 VARCHAR→CLOB。
- NULL 与 0 的区别；空集合、两边差集、计数不同但长度集合相同；范围失败与比较结果独立。
- 基线/当前数据库 itsview 不同仍能匹配；不同数据库的配置互不污染。
- 单条错误继续、连接中断跳过并标记后续项、下一数据库继续、每库一次连接及资源释放。
- Excel 11 列、换行、高亮、隐藏数据往返、超长文本完整性、续表、精确数值和输出文件失败场景。
- 输出初始化成功且 SQL 全部 PASS 后，validation 的暂存、工作簿写入或发布失败仍返回 2 且不发布有效基线；发布后输出资源关闭/临时文件清理失败返回 2，保留已发布报告和条目统计，汇总不重复计数。普通 Excel 同类失败继续保持原有错误级别。
- 三功能启用组合、共享/独立日志目录、单库无 db- 子目录/多库仅一层 db- 子目录、Excel 连接中断对后续校验的影响、校验输出失败不阻止可运行的 DDL/Excel。
- 新校验汇总实际出现在 success/issue 日志中，marker 路由不受对象名字干扰，跨库日志不串写；发布/连接关闭/日志关闭失败不重复计数、不降级成功。
- 空清单预检报错与空视图数据 PASS、失败聚合不产生有效分布、全失败诊断报告、超时、跨库共享 CSV 但默认长度单位不同。
- 一个物理对象配置多个 DECIMAL/字符/混合字段时，只建立一次成功的对象总数，不逐字段发独立 COUNT SQL；后续 DECIMAL 不再包含全对象计数；字符桶计数仍完整执行。同名对象跨库不共享缓存、失败计数不当作零、报告保持 CSV 顺序、发现分布总数变化时不产生有效基线。
- 实际字段类型兼容矩阵：字符列不能执行 DECIMAL 校验、非零 scale/浮点/用户自定义类型按约定拒绝、整数类型可执行数值校验、VARCHAR/CLOB 可执行原始字符规则、二进制字符串不能混入字符校验；TYPE MISMATCH 行不执行聚合且无有效基线分布，其他字段继续。
- 空表、全 NULL、长度 0 分别处理；NULL 桶计数不同但集合相同仍 Compare PASS；实际类型兼容不要求等于 after_data_type；字段名不映射且缺列不回退。
- VARCHAR 单一长度提示：两边严格 `{20}` 且 Range/Compare PASS 时命中，计数不同仍命中，`{0}` 可命中；原始 VARCHAR→CLOB 且实际已是 CLOB 也命中。空集合、`{NULL}`、`{NULL,20}`、多个长度、长度不同、Range FAIL、比较关闭、BASELINE 模式、缺失/无效基线或查询/对象一致性错误均不命中；原始 CLOB/DECIMAL 不命中。
- 单一长度提示只增加 Detail 第三行及浅蓝底色，三行均可见；主报告仍为 11 列，Range/Compare、差集、退出码和汇总不变，隐藏基线格式不变，且不产生额外 SQL/COUNT 查询。
- 校验报告表头批注包含上述判定范围；基线身份、有效/无效条目数量、时间范围、字段耗时在日志中可见且已清理敏感信息；仍保持 11 个可见列。
- 固定基线协议往返：丢失续表/桶、重复 NULL、负数计数、总数或 overflow 不一致、公式单元格、错误 mode、预检后原文件被替换等场景不会被误判为有效空基线或正常比较。
- 状态优先级覆盖 DECIMAL 错误行 Compare 空白、Range FAIL 仍可比较、对象数据变化导致已完成候选行整体失效；超过 15 位的可见计数精确保存。
- 新配置拼写/不存在的别名/关闭目标/BASELINE 下多余基线路径、ITSVIEW 映射碰撞、数值边界、单主表行数上限及大分布暂存清理。
- 同一业务库两个执行别名复用同一基线时独立得到报告、日志和对象计数；CSV 列重排/错误表头/非法记录；DECIMAL(31,0) 阈值配合实际 SMALLINT/INTEGER 列、空表 SUM=NULL 和大计数聚合；失败条目合法空字段、完整差集续表往返及孤立明细拒绝。
- 沿用现有 MultiDatabaseExportTest、ConfigLoaderTest、ExcelServiceTest、ExcelTextTest 等测试约定，不放宽旧断言以掩盖新功能造成的行为变化。
- 不能以 mock 测试通过宣称实际 DB2 SQL 已验证；提供可选 DB2 集成验证用例，并如实说明是否运行。

补充 `DATATYPE-VALIDATION.md`，向现有 README 增加入口，不覆盖原来的导出说明。提供单库、BASELINE 多库、VALIDATE 多库比较、VALIDATE 不比较以及与原功能组合的示例。保留既有 JAR 运行方式并运行 `mvn test`、`mvn package`。只增加确有必要的依赖，不顺带升级无关组件。

交付时说明修改文件、配置协议、运行示例、测试结果及真实 DB2 验证范围。

---

## 可直接采用的配置示例

以下为当前支持的配置模板。使用前必须替换主机、账号、密码和 CSV 对象；它们不是本机 TESTDB 的直接运行示例。

`config/datatypevalidation/application-baseline.properties`：

```properties
export.enabled=false
# 不设置 excel-export.config：本次仅运行数据类型校验
db.names=fos,rpt

db.fos.url=jdbc:db2://baseline-host:50000/FOSDB
db.fos.username=baseline_user
db.fos.password=replace_me
db.rpt.url=jdbc:db2://baseline-host:50000/RPTDB
db.rpt.username=baseline_user
db.rpt.password=replace_me

validation.enabled=true
validation.mode=BASELINE
validation.output-directory=../../output/validation/baseline
validation.itsview.enabled=false
validation.length-unit=OCTETS
validation.baseline.compare.enabled=false

catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS

db.fos.validation.database-id=FOS
db.fos.validation.file=./datatype-fos.csv
db.rpt.validation.database-id=RPT
db.rpt.validation.file=./datatype-rpt.csv
```

`config/datatypevalidation/application-validate.properties`：

```properties
export.enabled=false
db.names=fos,rpt

db.fos.url=jdbc:db2://current-host:50000/FOSDB
db.fos.username=current_user
db.fos.password=replace_me
db.rpt.url=jdbc:db2://current-host:50000/RPTDB
db.rpt.username=current_user
db.rpt.password=replace_me

validation.enabled=true
validation.mode=VALIDATE
validation.output-directory=../../output/validation/current
validation.itsview.enabled=false
validation.length-unit=OCTETS
validation.baseline.compare.enabled=true

catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS

db.fos.validation.database-id=FOS
db.fos.validation.file=./datatype-fos.csv
db.fos.validation.baseline.file=./baseline/fos-baseline.xlsx

db.rpt.validation.database-id=RPT
db.rpt.validation.file=./datatype-rpt.csv
db.rpt.validation.itsview.enabled=true
db.rpt.validation.itsview.schema=ITSVIEW
db.rpt.validation.baseline.file=./baseline/rpt-baseline.xlsx
```

先运行 BASELINE，将选定的 FOS 和 RPT 报告分别复制为上述两个基线文件，再运行 VALIDATE。基线必须与对应库的清单、类型定义及长度单位一致。

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
java -jar target/db2-data-toolkit.jar config/datatypevalidation/application-baseline.properties
java -jar target/db2-data-toolkit.jar config/datatypevalidation/application-validate.properties
```

VALIDATE 不做比较时，只需设置 `validation.baseline.compare.enabled=false`；无需提供基线文件。某库单独不比较可设置 `db.rpt.validation.baseline.compare.enabled=false`。

若同一次运行启用现有 DDL/Excel 导出，继续使用已有 `export.*` 和 `excel-export.config`，不把它们挪进新配置命名空间。

### 与当前 combined 配置组合的最小示例

当前 `config/combined/application-multi.properties` 使用别名 local_a、local_b，已经含连接、DDL 清单和 `excel-export.config=../excelexport/excel-export-multi.yaml`。可以保留现有内容，仅追加下列配置来演示第三个功能。CSV 文件需要另外提供，不能假定已有文件存在：

```properties
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS

validation.enabled=true
validation.mode=VALIDATE
validation.output-directory=../../output/local-example/multi/validation
validation.itsview.enabled=true
validation.itsview.schema=ITSVIEW
validation.length-unit=OCTETS
validation.baseline.compare.enabled=false
validation.query-timeout-seconds=0

db.local_a.validation.database-id=LOCAL_A
db.local_a.validation.file=./datatype-local-a.csv
db.local_b.validation.database-id=LOCAL_B
db.local_b.validation.file=./datatype-local-b.csv
```

这是结构示例，不代表现有 TESTDB 已经包含 ITSVIEW 视图。集成验证需确认实际对象；没有的视图应按规定记录缺失，不能为让示例通过而自动创建生产对象或改查底层表。仅运行校验时，请另存一份 properties，设置 `export.enabled=false` 并移除 `excel-export.config` 引用；组合运行不改动原有功能开关。

## 设计参考

- DB2 LENGTH 使用隐式或显式字符串单位：https://www.ibm.com/docs/en/db2/11.1?topic=functions-length
- DB2 COUNT_BIG 返回 DECIMAL(31,0)：https://www.ibm.com/docs/en/db2/11.5.x?topic=functions-count-big
- DB2 SUM 的结果类型与空集合行为：https://www.ibm.com/docs/en/db2/11.5.x?topic=functions-sum
- DB2 字符类型长度及计量单位范围：https://www.ibm.com/docs/en/db2/11.5.x?topic=list-character-strings
- SYSCAT.COLUMNS 的 CODEPAGE 等字段语义：https://www.ibm.com/docs/en/db2/12.1.x?topic=views-syscatcolumns
- Apache POI 文档列出的 XLSX 单元格文本上限与行数上限：https://poi.apache.org/apidocs/4.1/org/apache/poi/ss/SpreadsheetVersion.html


### Query deadline implementation (2026-09-16)

In addition to the driver statement timeout, use an elapsed deadline from executeQuery through result processing/cleanup. Attempt cancel at the query limit and independently attempt connection abort after `db.cancel-grace-seconds` (default 15, positive). `db.slow-query-seconds` (default 60, zero disables) logs one warning with SQL ID. Driver calls run outside timer threads. Disarm callbacks on scope completion; never reuse a connection with an outstanding cancellation or attempted abort. A timed-out scope cannot become PASS when the driver returns late. Keep Range ERROR with TIMEOUT diagnostics and preserve the baseline schema. This remains best effort, with no process-level hard termination; see README for scope and limitations.

===== END FILE =====

===== FILE: DATATYPE-VALIDATION.md =====
UTF8-BYTES: 31315
SHA256: 79703a32287d97466da731ca3c2b580ef7382839534be905a4b616a310db4da9
===== CONTENT =====
# DB2 数据类型范围校验与基线比较

Run commands in Windows PowerShell after building the JAR; replace the absolute checkout path if yours differs. Properties, YAML, SQL and CSV blocks are file contents, not terminal commands. Local examples require the retained TESTDB objects.

在同一个 Java 17 JAR 内运行 DDL、普通 Excel 和 validation。三者可以独立启用或任意组合；每库共享一个连接，顺序为 DDL → Excel → validation，各库并行，库内串行。功能只读取数据库，不执行 ALTER、DDL 或 DML。

## 本机 TESTDB：两份可直接运行的配置（已实测）

这两份配置只运行 validation。连接 `localhost:25000/TESTDB`，账号 `db2admin`；对象使用本次已创建并保留的 `DB2ADMIN.DVTEST_3AD0121876_*`。CSV 列名与视图列名相同。BASELINE 读取表；VALIDATE 读取 DB2ADMIN 下的映射视图，例如 `DB2ADMIN.DB2ADMIN_DVTEST_3AD0121876_CASES`。

在项目根目录 PowerShell 执行一次密码设置：

```powershell
cd C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit
$env:DB2_PASSWORD='123456'
```

### 1. 生成 baseline

文件：`config/datatypevalidation/local-testdb/application-baseline.properties`

```properties
db.url=jdbc:db2://localhost:25000/TESTDB
db.username=db2admin
db.password-env=DB2_PASSWORD
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.database-id=LOCAL_TESTDB_DEMO
validation.file=validation.csv
validation.itsview.schema=DB2ADMIN
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
validation.mode=BASELINE
validation.output-directory=output/baseline
validation.itsview.enabled=false
validation.baseline.compare.enabled=false
```

直接运行：

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
$env:DB2_PASSWORD='123456'
java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb/application-baseline.properties
$LASTEXITCODE
```

报告写入 `config/datatypevalidation/local-testdb/output/baseline/run-*/default-datatype-validation.xlsx`，本次实际报告路径会打印在日志中。

### 2. 执行 validation，并与指定 baseline 比较

文件：`config/datatypevalidation/local-testdb/application-validate.properties`

```properties
db.url=jdbc:db2://localhost:25000/TESTDB
db.username=db2admin
db.password-env=DB2_PASSWORD
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.database-id=LOCAL_TESTDB_DEMO
validation.file=validation.csv
validation.itsview.schema=DB2ADMIN
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
validation.mode=VALIDATE
validation.output-directory=output/validate
validation.itsview.enabled=true
validation.baseline.compare.enabled=true
validation.baseline.file=baseline/reference.xlsx
```

直接运行：

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
$env:DB2_PASSWORD='123456'
java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb/application-validate.properties
$LASTEXITCODE
```

报告写入 `config/datatypevalidation/local-testdb/output/validate/run-*/default-datatype-validation.xlsx`。配置中的路径相对于 properties 所在目录。

### 本次执行顺序与结果

先调用独立 `Db2TestDataMain prepare` 创建初始数据，再运行第一份配置（9 个 Range PASS，退出码 0）。将此次明确选定的报告复制为 `config/datatypevalidation/local-testdb/baseline/reference.xlsx`，然后调用 `Db2TestDataMain change` 扩容并追加异常数据，最后运行第二份配置。

第二份配置的实际结果：9 个字段，6 个 Range PASS、3 个 Range FAIL；Compare 为 3 个 PASS、2 个 DIFFERENT、4 个 N/A；查询错误 0，退出码 1 符合预期。

- CASES.N：1 条数值超出原 DECIMAL(5,0) 范围。
- CASES.C / MIX：各 1 条长度超出原上限，长度集合与 baseline 不同。
- CASES.S：原有 3 行、现在 4 行，非 NULL 长度集合均只有 5，报告包含 `SINGLE LENGTH MATCH (LIKELY OK)`。
- NULL_ROWS / EMPTY_ROWS：正确区分全 NULL 与零行。

**当前数据库已经处于变更后状态。** 现在重跑第一份配置，会得到变更后的新 baseline（包含 3 个 Range FAIL），不会自动覆盖固定的 `baseline/reference.xlsx`。第二份配置始终使用本次变更前已保存的 reference，因此可直接重复验证以上差异。要建立新的正式基线，应在数据变更前生成报告，并明确选定该次报告更新 reference；不要自动取“最新文件”。

本次报告：

- 变更前 baseline：`config/datatypevalidation/local-testdb/baseline/reference.xlsx`
- validation：`config/datatypevalidation/local-testdb/output/validate/run-1091289361958874347/datatype-validation.xlsx`
- 建表/插入记录与可选清理脚本：`verification/generated-data/documented-example-20260913/`

## 多数据库 validation：使用本机 TESTDB 的可运行示例

示例位于 `config/datatypevalidation/local-testdb-multi/`。`db.names=fos,rpt` 定义两个连接别名，目前都连接 `jdbc:db2://localhost:25000/TESTDB`。这是在一个物理数据库上验证多库调度；不会创建两个物理数据库。切换实际多库时分别修改 `db.fos.*` 和 `db.rpt.*` 的 URL、账号、密码环境变量及 CSV 对象。

| 别名 | 逻辑数据库 ID | CSV | VALIDATE 查询对象 | 固定 baseline |
| --- | --- | --- | --- | --- |
| fos | FOS_TEST | validation-fos.csv | DB2ADMIN 下的映射视图，独立 catalog 覆盖 | baseline/fos-reference.xlsx |
| rpt | RPT_TEST | validation-rpt.csv | DB2ADMIN 下的表，公共 SYSCAT 目录 | baseline/rpt-reference.xlsx |

每个别名仅建立一个连接，各别名并行运行、同一别名内串行处理；同一别名的 BASELINE/VALIDATE 必须保持相同 database-id。文件路径相对于 properties 所在目录，输出自动按 `db-fos` / `db-rpt` 分开。

### BASELINE 配置

This supplied multi-database BASELINE file uses explicit passwords. The legacy `application-validate.properties` and `application-non-baseline.properties` examples below use `DB2_PASSWORD`. For a consistent password method across both phases, use [file-password commands](COPY-PASTE-RUN.md) or [environment-variable commands](COPY-PASTE-RUN-ENV.md).

`config/datatypevalidation/local-testdb-multi/application-baseline.properties`：

```properties
# Two independent connection aliases using the same local physical TESTDB.
db.names=fos,rpt
db.fos.url=jdbc:db2://localhost:25000/TESTDB
db.fos.username=db2admin
db.fos.password=123456
db.rpt.url=jdbc:db2://localhost:25000/TESTDB
db.rpt.username=db2admin
db.rpt.password=123456
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
db.fos.validation.database-id=FOS_TEST
db.fos.validation.file=validation-fos.csv
db.rpt.validation.database-id=RPT_TEST
db.rpt.validation.file=validation-rpt.csv
validation.mode=BASELINE
validation.output-directory=output/baseline
validation.itsview.enabled=false
validation.baseline.compare.enabled=false
```

### VALIDATE 配置

`config/datatypevalidation/local-testdb-multi/application-validate.properties`：

```properties
# Two independent connection aliases using the same local physical TESTDB.
db.names=fos,rpt
db.fos.url=jdbc:db2://localhost:25000/TESTDB
db.fos.username=db2admin
db.fos.password-env=DB2_PASSWORD
db.rpt.url=jdbc:db2://localhost:25000/TESTDB
db.rpt.username=db2admin
db.rpt.password-env=DB2_PASSWORD
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
db.fos.validation.database-id=FOS_TEST
db.fos.validation.file=validation-fos.csv
db.rpt.validation.database-id=RPT_TEST
db.rpt.validation.file=validation-rpt.csv
validation.mode=VALIDATE
validation.output-directory=output/validate
validation.itsview.enabled=false
validation.baseline.compare.enabled=true
db.fos.validation.baseline.file=baseline/fos-reference.xlsx
db.rpt.validation.baseline.file=baseline/rpt-reference.xlsx
# FOS validates mapped views; RPT validates tables directly.
db.fos.validation.itsview.enabled=true
db.fos.validation.itsview.schema=DB2ADMIN
# Optional per-database catalog overrides, tested with these fixture views.
db.fos.catalog.table.view=DB2ADMIN.DVTEST_E40A284FA5_CAT_OBJECTS
db.fos.catalog.column.view=DB2ADMIN.DVTEST_E40A284FA5_CAT_COLUMNS
```

### 直接运行（PowerShell）

```powershell
cd C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit
$env:DB2_PASSWORD='123456'
# 生成两个数据库别名各自的 baseline
java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb-multi/application-baseline.properties
$LASTEXITCODE
# 使用已经保存的变更前基线，执行两个别名的 validation
java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb-multi/application-validate.properties
$LASTEXITCODE
```

### 已完成的实际验证

先通过独立 main 创建新一批测试数据 `DB2ADMIN.DVTEST_E40A284FA5_*`，再运行多库 BASELINE，两库合计 9 个字段全部 PASS，退出码 0。将本次日志明确列出的两个报告分别保存到上表中的固定 baseline 文件，再通过 main 扩容并插入异常数据，最后运行多库 VALIDATE。

| 模式 / 别名 | Range | Compare | 查询错误 | 退出码 |
| --- | --- | --- | --- | --- |
| BASELINE / fos | 5 PASS | 5 N/A | 0 | 0 |
| BASELINE / rpt | 4 PASS | 4 N/A | 0 | 0 |
| VALIDATE / fos | 2 PASS、3 FAIL | 1 PASS、2 DIFFERENT、2 N/A | 0 | 1 |
| VALIDATE / rpt | 4 PASS | 2 PASS、2 N/A | 0 | 0 |

VALIDATE 总退出码 1 是刻意插入溢出数据的预期结果，fos 有差异后 rpt 仍然完成。数据和两个固定基线均已保留，可以直接重跑 VALIDATE。

**数据库现在已处于变更后状态。** 现在重跑 BASELINE 会生成反映当前异常数据的新报告（总退出码 1），不会自动覆盖两个变更前的固定 baseline 文件。需要更新基线时，从日志中明确选择对应别名的报告再复制，不自动选择最新文件。

造数记录及可选清理脚本：`verification/generated-data/multi-example-20260913/`。

## Configuration templates — edit before running

The files directly under `config/datatypevalidation/` are templates, not ready-to-run TESTDB configurations. Replace placeholder hosts/users, set the selected password environment variable, edit CSV objects and explicitly supply a BASELINE-mode workbook before comparison. For immediate local execution use [COPY-PASTE-RUN.md](COPY-PASTE-RUN.md). The commands in this section apply only after those edits.

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
mvn test
mvn package
java -jar target/db2-data-toolkit.jar config/datatypevalidation/application-single-baseline.properties
```

将选定的 BASELINE 报告复制为 `config/datatypevalidation/baseline/fos-baseline.xlsx`，再运行：

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
java -jar target/db2-data-toolkit.jar config/datatypevalidation/application-single-validate.properties
```

没有基线时只做范围校验：

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
java -jar target/db2-data-toolkit.jar config/datatypevalidation/application-validate-no-compare.properties
```

| 示例文件（位于 config/datatypevalidation） | 用途 |
| --- | --- |
| application-single-baseline.properties | 单库生成基线 |
| application-single-validate.properties | 单库读取指定基线并校验 |
| application-baseline.properties | FOS/RPT 多库分别生成基线 |
| application-validate.properties | FOS/RPT 多库各自比较，RPT 查询 ITSVIEW |
| application-validate-no-compare.properties | VALIDATE 只做原始范围校验 |
| application-all.properties | DDL、普通 Excel 和 validation 一起运行 |

多库运行方式仍然是 `java -jar target/db2-data-toolkit.jar <properties文件>`。不会自动选“最新”基线，也不会在同一批次中引用前一个数据库刚生成的报告。BASELINE 与 VALIDATE 表示运行模式，不代表环境或主机。

## 配置

DDL 由 `export.enabled` 控制（旧默认 true）；普通 Excel 由 `excel-export.config` 引用 YAML 的 `excel-export.enabled` 控制；校验由 `validation.enabled` 控制（默认 false）。只做校验时明确设置 `export.enabled=false`，不引用 Excel YAML。三个开关全部关闭时不连接数据库、不创建输出或日志；仍按旧规则解析公共连接配置及显式引用的 YAML。

以下键支持公共 `validation.*` 和多库 `db.<alias>.validation.*` 覆盖。优先级为数据库显式值 → 公共值 → 默认值；显式空必填值报错，不回退。

| 键 | 默认 / 规则 |
| --- | --- |
| enabled | false；严格 true/false |
| mode | 启用时必填，BASELINE / VALIDATE，大小写不敏感 |
| database-id | 启用时必填，1–40 ASCII 字符，字母开头，后接字母、数字、`_`、`-`；统一大写 |
| file | 必填，CSV 路径 |
| itsview.enabled | false |
| itsview.schema | ITSVIEW；itsview=false 时不校验该值 |
| length-unit | OCTETS 或 CODEUNITS32，默认 OCTETS |
| baseline.compare.enabled | false；只在 VALIDATE 下执行比较 |
| baseline.file | 仅 VALIDATE 且比较开启时必填并读取 |
| query-timeout-seconds | 0，不设置额外超时；支持 0..2147483647 |

`validation.output-directory` 仅允许公共配置，默认 `./output/validation`。所有新增路径相对于 properties 所在目录；普通 Excel 的路径仍相对于 YAML 所在目录。单库只使用公共 validation 键。多库键前缀必须与 `db.names` 中的别名精确一致。

逻辑 `database-id` 用来匹配业务库，不是物理数据库名。基线与当前库必须相同；两个执行别名可以代表同一个逻辑库并使用同一份基线，但连接、缓存、结果和输出独立。

关闭 validation 不解析其条件值或读取 CSV/基线；未知新键及旧草案 `validation.environment`、`validation.prod.*` 仍报错。BASELINE 下合法的比较开关 true 会记录“不执行比较”，不解析基线路径。密码沿用 `db.password` / `password-env`，多库沿用 `db.<alias>.*`；显式密码优先且不去空白。

### 对象目录和视图

```properties
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
# 多库可选覆盖
# db.rpt.catalog.table.view=MYCAT.OBJECTS
# db.rpt.catalog.column.view=MYCAT.COLUMNS
```

DDL 与校验共用每库解析后的目录。对象目录必须提供 TABSCHEMA/TABNAME/TYPE，列目录必须提供 TABSCHEMA/TABNAME/COLNAME/TYPESCHEMA/TYPENAME/LENGTH/SCALE/CODEPAGE。DDL 启用时还须满足原提取器完整字段约定。目录查询失败会报 ERROR，不回退 SYSCAT，也不误报缺业务对象。

ITSVIEW 开启时，`fos.share_deal` 映射到 `<itsview.schema>.FOS_SHARE_DEAL`，只查询此视图；TYPE 必须为 V/W。关闭时查询 CSV 中的对象，支持 T/V/W。列名保持一致，不映射、不回退底层表。`A_B.C` 与 `A.B_C` 的映射碰撞在连接前报错。普通名称限 128 个 ASCII 标识符字符，不支持带引号的校验名称；旧 DDL 名称规则不变。这里只支持 DB2 LUW，替换目录名称不等于支持 z/OS 或 IBM i。

## CSV 与数据类型

```csv
table_name,field_name,original_data_type,after_data_type
fos.share_deal,deal_number,"DECIMAL(5,0)","DECIMAL(20,0)"
fos.share_deal,reference,VARCHAR(50),VARCHAR(65)
fos.share_deal,comments,CLOB(1000),CLOB(5000)
```

支持 UTF-8/BOM、CRLF/LF、CSV 引号以及按名称匹配的重排表头。可增第五列 `length_unit`，空值继承当前目标的默认单位，DECIMAL 行必须留空。未知、缺失、重复表头，坏记录、重复逻辑字段、空清单都会预检失败，不能跳过坏行。允许真正的空行；全空字段记录无效。最多 1,048,575 个条目。

支持 DECIMAL(p,0)、VARCHAR(n)、CLOB(n) 同类扩容或 VARCHAR→CLOB；目标尺寸不能缩小。DECIMAL p=1..31。VARCHAR 最大长度为 OCTETS 32672 / CODEUNITS32 8168；CLOB 为 2147483647 / 536870911，不支持 K/M/G 后缀。

规则始终依据 **原始类型**。实际列的内置整数或 scale=0 的 DECIMAL/NUMERIC 可做数值校验；实际内置 VARCHAR/CLOB 可做字符校验。浮点、非零 scale、用户定义类型、FOR BIT DATA 或其他不兼容类型报 TYPE MISMATCH，不通过隐式转换掩盖问题。相容并不要求实际类型等于计划的 after_data_type。

## 查询与结果含义

同一个物理对象只获取一次成功的 Record Count：首个成功的 DECIMAL 聚合带 `COUNT_BIG(*)`；首个成功字符聚合将所有桶计数求和。后续 DECIMAL 不再查询总数；每个字符字段仍须计算自己的长度桶计数。失败聚合不能缓存为 0。

DECIMAL 阈值、SUM 输入和参数使用 DECIMAL(31,0)，Java 使用 BigInteger/BigDecimal；空数据的 SUM 归 0，MIN/MAX 保留 NULL。字符查询按显式单位的 LENGTH 分组，不读取完整 CLOB。记录字段耗时，设置每条元数据/聚合查询超时；超时是 ERROR。没有 `maxRows` 截断或并发扫描。

不修改 autoCommit、隔离级别等会话设置，也不承诺跨字段的快照一致性。如果发现后续桶总数与共享总数不同，或溢出数超过共享总数，或数值 MIN/MAX 至少需要的行数超过共享总数，将整个对象已完成的候选结果改为 ERROR，不输出有效分布。不会重新扫描追赶变化；总数不变的数据修改仍可能无法发现。

| 状态 | 含义 |
| --- | --- |
| Range PASS | 没有数据超出旧范围 |
| Range FAIL | 有数据超出旧范围；不等于扩容失败 |
| Compare PASS | 完整长度集合（包括 NULL）相同；不比较桶计数、总记录数或逐行内容 |
| Compare DIFFERENT | 长度集合存在差异；不自动归因为变更失败 |
| N/A | BASELINE、比较关闭或 DECIMAL 的成功聚合，不执行集合比较 |
| BASELINE NOT FOUND / BASELINE INVALID | 缺对应条目 / 对应条目没有有效完整聚合 |
| TABLE NOT FOUND / COLUMN NOT FOUND | 在配置目录中找不到对象（也包括视图）/ 列 |
| TYPE MISMATCH / ERROR / NOT EXECUTED | 类型不兼容 / 查询等错误 / 未执行；Compare 与聚合值留空 |

例如 VARCHAR(50)→VARCHAR(100) 后出现长度 60，Range FAIL 表示超出旧范围，不能据此说新的 VARCHAR(100) 定义有问题。Range FAIL 且完整的基线仍可用于集合比较。

空表 Record Count=0、两个列表 `()`；全 NULL 为 Record Count>0、长度列表 `(NULL)`；零长度字符串的长度是 0。三者不能混同。两边全 NULL 或都为空可 Compare PASS，但空表与全 NULL 为 DIFFERENT。

### VARCHAR 单一长度提示

原始 VARCHAR（包括计划/实际已改为 CLOB）的当前和基线都严格只有同一个非 NULL 长度，且 Range/Compare 都 PASS 时，在 Detail 加第三行并使用浅蓝底色：

```text
DISTINCT LENGTH = (20)
DISTINCT COUNT  = (120)
HINT = SINGLE LENGTH MATCH (LIKELY OK)
```

这是“单一长度与基线一致，大概率符合预期”的经验提示，不是统计概率，也不证明内容或类型变更正确。两边计数不同仍可提示；长度 0 可命中，但不表示内容完整。空表、全 NULL、含 NULL 的多桶、多个非 NULL 长度及 Range FAIL 不提示。原始 CLOB/DECIMAL 不适用。不额外查询，不改变状态、差集、退出码或汇总。

## 报告、文件协议和日志

单库：`<output>/run-*/<alias>-datatype-validation.xlsx`；多库：`<output>/db-<alias>/run-*/<alias>-datatype-validation.xlsx`。每次新目录，暂存写完才发布，不覆盖已有报告。

可见 `Validation` 表固定 11 列：Table Name（CSV 逻辑对象名）、Field Name、Original Data Type、After Data Type、Record Count、Overflow Count、Detail、Range Status、Compare Status、Baseline Only Length、Current Only Length。PASS 使用浅绿色；范围失败使用浅红色，差集使用浅黄色；完整配色见下方 Report colors。大于 15 位有效数字的计数保存为精确文本。Detail 超长时显式标注预览截取，完整分布与差集保存在隐藏续表；范围和比较从完整数据计算。正常 VARCHAR/CLOB Detail 前两行是长度/对应计数，仅命中单一长度提示时增加第三行。

隐藏协议 formatVersion=1，所有单元格均使用文本；禁止公式。每个表的第一行为固定列名，顺序如下：

```text
_meta:
key,value

_items:
databaseId,logicalSchema,logicalObject,field,originalDataType,afterDataType,lengthUnit,physicalSchema,physicalObject,actualType,rangeStatus,aggregationComplete,recordCount,overflowCount,bucketCount,error,min,max,compareStatus,baselineOnlyCount,currentOnlyCount

_lengths_001 起连续续表:
databaseId,logicalSchema,logicalObject,field,isNull,length,count

_diffs_001 起连续续表:
databaseId,logicalSchema,logicalObject,field,side,isNull,length
```

`_meta` 的键：formatVersion、mode、databaseId、executionAlias、generatedAt、startedAt、endedAt、itemCount、lengthSheetCount、diffSheetCount，以及每个 `_items`/长度/差集表的 `rows.<sheetName>`。续表最多 1,048,575 条数据行，无记录时不创建长度或差集表。时间使用带时区 ISO 时间；数字使用规范十进制文本；NULL 长度用 isNull=true 且 length 为空表示。长度桶 count 为正；每条目按 NULL 在前、长度递增保存，不允许重复；差集 side 为 BASELINE_ONLY/CURRENT_ONLY，同样按条目/方向有序保存。

没有完整聚合的条目保留身份与错误，聚合数值留空、bucketCount=0。BASELINE 模式的 compareStatus 为 N/A 或错误行空白，差集数量留空。读取基线验证 manifest、身份、类型/单位、重复/孤立记录、完整桶数量、总数和溢出数。只接受 BASELINE 模式，不从可见文本反解析，也不接受重命名的 VALIDATE 报告。

所有目标的 CSV/必需基线都在首次连接前预检。预检得到的基线快照供后续运行使用，不重新打开可能已替换的原文件。长度分布与 Excel 共享字符串索引在临时目录暂存，工作簿流式写入；既不把整个基线载入 XSSFWorkbook，也不把所有桶长期留在堆内存。需为系统临时目录和报告目录保留足够空间。

每库复用一套 <alias>-process.log、<alias>-summary_success.log、<alias>-summary_issue.log。目录优先 DDL → 普通 Excel → validation；组合运行时另在日志列出校验报告路径。日志含基线文件路径、生成时间、逻辑 ID、有效/无效条目数、执行时间、Range/Compare 汇总、查询失败数和未执行项，经过 SafeDiagnostics 清理。

退出码取每功能/每库的最高值：0=成功，1=范围/比较差异或不可用条目，2=预检、连接/输出初始化或 validation 暂存/写入/发布/清理失败。报告发布后的清理失败仍返回 2，但保留已发布事实和条目统计。旧 DDL/普通 Excel 的错误级别不变。

## 验证

普通单元及文件往返测试：`mvn test`；打包：`mvn package`。测试包含组合开关、多库隔离、目录/类型检查、共享总数、精度、NULL/空表、单一长度提示、损坏基线、分布续表及发布失败。

可选真实 DB2 LUW SQL 验证不创建对象，仅使用 VALUES 派生数据验证数字边界、空数据与 LENGTH 单位语法。在当前 PowerShell 会话设置以下环境变量后运行：

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
$previousIntegration = $env:DB2_VALIDATION_INTEGRATION
$previousConfig = $env:DB2_VALIDATION_CONFIG
try {
    $env:DB2_VALIDATION_INTEGRATION='true'
    $env:DB2_VALIDATION_CONFIG='config/combined/application.properties'
    mvn '-Dtest=Db2ValidationIntegrationTest' test
    if ($LASTEXITCODE -ne 0) { throw 'Read-only integration test failed.' }
} finally {
    $env:DB2_VALIDATION_INTEGRATION=$previousIntegration
    $env:DB2_VALIDATION_CONFIG=$previousConfig
}
```

默认跳过真实数据库测试。Mock 与工作簿测试通过不能代替真实 DB2 验证；上述 SQL 测试也不替代针对实际目录、视图和业务数据的验收。

The read-only test above reuses the connection in the supplied single-database properties file; it does not run that file's configured exports. To test another server, point DB2_VALIDATION_CONFIG at an edited single-database connection file.

真实 DB2 测试还会读取 `SYSCAT.TABLES` 的 TABSCHEMA/TABNAME 列，通过实际元数据检查、基线工作簿生成和 VALIDATE 比较验证完整流程。不会创建或修改数据库对象。

实际百万条 CSV 边界测试需要额外时间与内存，默认不随普通测试运行。以下命令验证恰好 1,048,575 条可接受、第 1,048,576 条被拒绝，并保留 2 GB 测试 JVM 堆空间：

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
mvn '-Dtest=DataTypeValidationLargeCsvTest' '-Dvalidation.large-tests=true' '-DargLine=-Xmx2g' test
```

Current and historical acceptance results are recorded in [QA-HISTORY.md](QA-HISTORY.md); earlier test totals describe their own checkpoints.

## BASELINE 与 non-baseline（不读取基线）直接运行

程序 mode 只接受 BASELINE / VALIDATE；不存在 NON_BASELINE 这个 mode。
`application-non-baseline.properties` 使用 VALIDATE，并关闭 baseline compare，因此不需要基线文件，只执行原类型范围校验。
若 non-baseline 指“变更后的环境，需要比较基线”，使用原有 `application-validate.properties`（compare=true）。

| 配置 | mode | baseline.compare.enabled | 用途 |
| --- | --- | --- | --- |
| application-baseline.properties | BASELINE | false | 采集数据并生成可供后续比较的基线报告 |
| application-non-baseline.properties | VALIDATE | false | 无基线，独立校验原类型范围 |
| application-validate.properties | VALIDATE | true | 校验范围并比较固定基线 |

完整 non-baseline 配置：

```properties
# Two independent connection aliases using the same local physical TESTDB.
db.names=fos,rpt
db.fos.url=jdbc:db2://localhost:25000/TESTDB
db.fos.username=db2admin
db.fos.password-env=DB2_PASSWORD
db.rpt.url=jdbc:db2://localhost:25000/TESTDB
db.rpt.username=db2admin
db.rpt.password-env=DB2_PASSWORD
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
db.fos.validation.database-id=FOS_TEST
db.fos.validation.file=validation-fos.csv
db.rpt.validation.database-id=RPT_TEST
db.rpt.validation.file=validation-rpt.csv
validation.mode=VALIDATE
validation.output-directory=output/validate-no-baseline
validation.itsview.enabled=false
validation.baseline.compare.enabled=false
# FOS validates mapped views; RPT validates tables directly.
db.fos.validation.itsview.enabled=true
db.fos.validation.itsview.schema=DB2ADMIN
# Optional per-database catalog overrides, tested with these fixture views.
db.fos.catalog.table.view=DB2ADMIN.DVTEST_E40A284FA5_CAT_OBJECTS
db.fos.catalog.column.view=DB2ADMIN.DVTEST_E40A284FA5_CAT_COLUMNS
```

在项目根目录 PowerShell 运行：

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
$env:DB2_PASSWORD='123456'
java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb-multi/application-baseline.properties
$LASTEXITCODE
java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb-multi/application-non-baseline.properties
$LASTEXITCODE
```

2026-09-13 已实际重跑上述两份配置，也重跑了开启 compare 的配置。当前数据已包含刻意插入的溢出，三个模式均为 fos 2 PASS / 3 FAIL、rpt 4 PASS，查询错误 0，总退出码 1，所有报告成功生成。BASELINE 和不读取基线的 VALIDATE，Compare 均为 N/A；开启比较时共 3 PASS / 2 DIFFERENT / 4 N/A。

这次 BASELINE 使用的是变更后的数据，因此与最初变更前 BASELINE 的 9 PASS 结果不同。两个固定变更前 baseline 文件未被覆盖。

## Earlier QA checkpoints

详见 [QA-HISTORY.md#validation-qa-round-1](QA-HISTORY.md#validation-qa-round-1)：2026-09-13 修正共享计数与数值极值矛盾、目录标识符空格问题；全量 292 项测试通过，包含真实 DB2 造数与只读集成测试。

## Round 2 checkpoint

[Round 2 review](QA-HISTORY.md#validation-qa-round-2): 293 tests passed, no failures or skips; baseline object-count consistency fixed and 12 additional DB2 edge fields verified. See [COPY-PASTE-RUN.md](COPY-PASTE-RUN.md) for separate multi-database DDL, Excel, BASELINE and CURRENT commands.

## Physical conversion and scale checks completed

See [scale acceptance](QA-HISTORY.md#physical-migration-and-scale-acceptance): physical VARCHAR-to-CLOB migration verified, 6,000 real distinct lengths and 12,000 differences checked, and hidden-sheet continuation tested at a reduced boundary. Dedicated integration passed; 293 other regression tests passed.

### Connection and waiting limits

Data-type validation uses `validation.query-timeout-seconds` for each metadata/data statement. Shared connection settings `db.connect-timeout-seconds` (default 30), `db.read-timeout-seconds` (120), and `db.progress-interval-seconds` (10) apply as well, with `db.<alias>.*` overrides. `db.query-timeout-seconds` applies only to DDL/Excel statements. See [Connection, query timing and waiting logs](COPY-PASTE-RUN.md#connection-query-timing-and-waiting-logs) for zero-value semantics, phase logs, and timeout limitations.


## Report colors

Header: dark blue (#244062), white text. PASS: pale green (#E2F0D9), dark green text (#375623). FAIL, query errors and invalid/missing baselines: pale red (#FCE4D6), dark red text (#9C0006). Positive overflow counts also use pale red. DIFFERENT and nonempty difference previews: pale yellow (#FFF2CC), dark brown text (#7F6000). N/A: pale gray (#F2F2F2), gray text (#666666). SINGLE LENGTH MATCH details: pale blue (#DDEBF7), dark blue text (#1F4E78). Ordinary cells remain unfilled. Status labels remain visible; color does not change validation semantics.


New reports use `<alias>-datatype-validation.xlsx`, where the alias comes from `db.names` or single-database `db.name` (default `default`). Older report paths in historical examples describe existing files; baseline reading does not depend on a particular filename.


Slow queries now use the application cancellation deadline described in [Slow queries and cancellation](README.md#slow-queries-and-cancellation). Validation keeps Range ERROR for timeouts and records a `TIMEOUT` reason. The configured limit also covers fetching and processing after execution starts. Shipped examples use 300 seconds; cancellation grace is 15 seconds and slow-query warning threshold is 60 seconds. No automatic retry is performed.

===== END FILE =====

===== FILE: EXCEL-EXPORT.md =====
UTF8-BYTES: 7001
SHA256: 4d7f8a3a0e2e0329dea2bef1973190855b97a0b3b1a1c1abb5012ab57112d078
===== CONTENT =====
# Excel export configuration

Run commands in Windows PowerShell after building the JAR; replace the absolute checkout path if yours differs. Properties, YAML, SQL and CSV blocks are file contents, not terminal commands. Local examples require the retained TESTDB objects.

Exports table, view or SELECT results to `.xlsx`. The three configuration modes below may coexist and use the same exporter.

## Run the local example

From the project root, after building the JAR:

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
java -jar target/db2-data-toolkit.jar config/excelexport/application-excel.properties
```

The properties file contains the database connection and:

```properties
export.enabled=false
excel-export.config=./excel-export.yaml
```

Use `export.enabled=true` with the three DDL lists to run DDL and Excel together.

## YAML example

This is an optional file-content template, not a terminal command. To use it with the command above, save it as `config/excelexport/excel-export.yaml`. It replaces that YAML's task definitions. The following example uses `SYSIBM.SYSDUMMY1`, so no business table is required. Output paths resolve from the YAML file's directory. The expected files below apply after saving this template; the supplied YAML has different DATA/CITIES tasks.

```yaml
excel-export:
  enabled: true
  output-directory: ../../output/excel
  include-header: true
  default-fetch-size: 1000
  overwrite: true

  # Simple: one object creates one workbook.
  simple:
    tables:
      - SYSIBM.SYSDUMMY1

  # Grouped: all listed objects share this workbook.
  workbooks:
    - name: reference.xlsx
      objects:
        - SYSIBM.SYSDUMMY1

  # Advanced: objects or SELECT queries with task settings.
  exports:
    - name: status-report
      type: SQL
      source: >
        SELECT IBMREQD AS STATUS FROM SYSIBM.SYSDUMMY1
      workbook: report.xlsx
      sheet: STATUS

    - name: raw-data
      type: OBJECT
      source: SYSIBM.SYSDUMMY1
      workbook: report.xlsx
      sheet: RAW_DATA
      include-header: false
      fetch-size: 2000
      max-rows: 100
```

Expected files:

| Workbook | Sheets |
| --- | --- |
| `<alias>-SYSDUMMY1.xlsx` | SYSDUMMY1 |
| `<alias>-reference.xlsx` | SYSDUMMY1 |
| `<alias>-report.xlsx` | STATUS, RAW_DATA |

Replace object names or SQL with your own. Tables and views both use `OBJECT`, which executes `SELECT * FROM <source>`. Simple/grouped modes derive sheet names from the unqualified object name.

Advanced tasks require `type`, `source`, `workbook` and `sheet`. Tasks sharing a workbook name write separate sheets in that workbook. SQL aliases become headers automatically.

## Settings

| Setting | Default | Meaning |
| --- | --- | --- |
| `enabled` | false | Enable Excel export |
| `output-directory` | `./output/excel` | Destination relative to the YAML file; configure explicitly to avoid surprises |
| `include-header` | true | First row uses JDBC column labels |
| `default-fetch-size` | 1000 | Positive JDBC fetch-size hint |
| `overwrite` | true | Replace existing same-name workbooks |
| Task `enabled` | true | False records the task as skipped |
| Task `fetch-size` | Global default | Override the fetch hint |
| Task `max-rows` | 0 | Maximum data rows; 0 means unlimited |
| Task `include-header` | Global default | Override header generation |

When Excel is enabled, unknown task keys, invalid sources and invalid limits fail configuration validation. Individually disabled tasks still need valid definitions. With the whole Excel export disabled, only the outer YAML keys and enabled flag are checked; nested task definitions are not loaded. Fetch size does not limit the total exported rows; use `max-rows` for that.

## Data and large results

Rows stream from JDBC into Apache POI SXSSF; the exporter does not collect the complete result in memory.

Completed task and rollover sheets flush their remaining row window to compressed temporary storage. This avoids retaining up to 100 rows for every completed sheet; workbook metadata still grows with the sheet count. Publication remains provisional until the workbook has been written and closed successfully.

- Headers use `getColumnLabel()`, preserving SQL aliases. Header rows are bold and frozen.
- A sheet holds at most 1,048,576 rows, including its header. Extra data continues in sheets such as DATA_2, with headers repeated when enabled.
- Invalid sheet-name characters are replaced; names are limited to 31 characters and made unique. Workbook names are sanitized and gain `.xlsx` when needed.
- NULL produces a blank cell. Strings remain text; they are not treated as formulas.
- Large integers and high-precision decimals are written as text when numeric conversion would lose precision.
- Dates/times use Excel cells where representable; timestamps requiring greater precision remain exact text, including DB2 TIMESTAMP(12).
- Text/CLOB values exceeding 32,767 characters fail explicitly. BLOB values are unsupported; select a text representation if needed.

Temporary disk space grows with data volume. Close an existing workbook in Excel if Windows prevents it from being replaced.

## Queries and failures

SQL mode accepts SELECT and WITH ... SELECT. Do not add a trailing semicolon. Data-changing statements, SELECT INTO, FOR UPDATE and sequence increments are rejected. The same mandatory check runs again before JDBC preparation, including direct service calls. Validation is conservative, not a full SQL parser; configured database functions still have their own semantics.

A failed task's partial sheets are removed. Successful sibling sheets can still be saved while the workbook and connection remain usable. If sheet cleanup or workbook publication fails, the affected workbook's tasks are reported failed. Confirmed connection loss stops subsequent queries for that database.

With `overwrite=false`, existing workbooks are retained and the affected tasks fail. A workbook with no successful tasks is not published; an older file may therefore remain. Use the current summary to identify successful output.

## Multiple databases and logs

The same Excel tasks run for every configured database. Files go under `<output-directory>/db-<alias>/`. Each database uses its existing connection for both DDL and Excel.

Combined exports keep logs in the DDL run directory. Excel-only exports put logs in `run-*` beside the workbooks. `<alias>-summary_success.log` lists successful tasks, sheets and row counts; `<alias>-summary_issue.log` lists failed/skipped tasks and available SQLCODE/SQLSTATE.

See [usage examples](USAGE-EXAMPLES.md) for connections and [verification commands](README.md#testing-and-verification-runnable-examples) for readback and million-row checks.

Application exports prefix every configured workbook filename with the database alias (`db.names` or `db.name`, default `default`). For example, `report.xlsx` becomes `fos-report.xlsx`. The 200-byte filename limit includes the prefix.

===== END FILE =====

===== FILE: QA-HISTORY.md =====
UTF8-BYTES: 70124
SHA256: 5083ffb9dfcc9d4f7b1631dcb3e4c86dc73ac270fef6d7d71e18a5899e174461
===== CONTENT =====
# QA history

Consolidated historical reviews and acceptance records. Earlier totals and names describe their checkpoints. Raw QA logs and diagnostic helpers were retired during verification cleanup. This document was reconstructed from local Git objects and session-recorded updates after an encoding failure; formatting and some historical wording may differ. Current runnable instructions are in [COPY-PASTE-RUN.md](COPY-PASTE-RUN.md).


## Initial acceptance

Historical source: `DATATYPE-VALIDATION-ACCEPTANCE.md`.

### 数据类型校验实施验收记录

验证日期：2026-09-13。

本次补齐了上一轮审查指出的多库组合、百万条 CSV 边界、大差集续表、共用基线隔离测试，以及元数据失败时的对象结束日志。最终构建运行 **283 个测试，0 失败、0 错误、0 跳过**；JAR 已重新生成。

#### 本次补齐项

| 要求 | 实现或验收证据 | 结果 |
| --- | --- | --- |
| 多库全部 8 种开关组合 | ValidationApplicationTest.multiDatabaseFeatureCombinationsAreSequentialAndIsolated；验证连接顺序、每库只关闭一次、输出数量、DDL→Excel→validation 顺序与日志别名隔离 | 8 种均通过 |
| 多目标复用同一逻辑基线 | aliasesShareOneLogicalBaselineWithoutSharingCountsOrOutputs；两个别名同为 FOS、使用同一个基线文件，但当前记录数分别为 15 和 70 | 各自报告 PASS 与单一长度提示，总数不串库 |
| 主报告条目数上限 | ValidationLargeCsvTest；实际生成 CSV 并解析 1,048,575 个不同字段，再增加第 1,048,576 条 | 上限内接受，超限预检拒绝，不创建报告目录；耗时约 15 秒 |
| 大差集完整性及续表 | BaselineRoundTripTest.largeBidirectionalDifferencesAreCompleteAcrossHiddenContinuationSheets；基线与当前各 10,000 个互不相同的长度，逐条检查 20,000 条差集 | 顺序、方向、数量完整；可见预览明确截取 |
| 失败路径对象结束日志 | 对象处理使用 finally 记录结束时间和耗时；分别注入目录查询及数据查询异常 | 每对象恰好一条开始和结束日志 |
| 真实 DB2 数值及字符 SQL | Db2ValidationIntegrationTest；在本机示例 TESTDB 连接上运行 VALUES 派生数据 | DECIMAL 参数、COUNT_BIG、空数据、VARCHAR/CLOB 的 OCTETS/CODEUNITS32 聚合通过 |
| 真实 DB2 完整校验流程 | 读取 SYSCAT.TABLES 视图的 TABSCHEMA/TABNAME，经实际目录检查、BASELINE 工作簿写入、基线读取与 VALIDATE 比较 | 两字段 Range/Compare 均 PASS |

续表测试将每张隐藏表的容量设为 3,000 条以实际触发分表，生产容量仍为每表 1,048,575 条。这验证了续表逻辑及完整数据，没有声称生成了百万行差集工作簿。百万条测试验证的是 CSV/主报告条目数预检边界。

#### 完整构建命令

```powershell
$env:DB2_VALIDATION_INTEGRATION='true'
$env:DB2_VALIDATION_CONFIG='config/combined/application.properties'
mvn '-Dvalidation.large-tests=true' '-DargLine=-Xmx2g' package
```

普通 `mvn test` / `mvn package` 默认跳过两项真实 DB2 测试和一项百万条 CSV 测试；它们均在上述最终构建中显式启用并通过。测试使用现有配置读取连接凭据，没有将密码放入命令或新配置文件。

#### 已有功能及回归范围

- 单库/多库配置、条件必填项、数据库级 catalog 覆盖、CSV 严格解析和每库长度单位。
- 实际视图与字段类型检查、缺失和错误区分、共享总数、查询超时与连接中断隔离。
- DECIMAL 精度、NULL/空表、完整长度比较、基线完整性检查、不可变运行快照。
- 11 列报告、完整隐藏分布/差集、大计数精确文本、VARCHAR 单一长度辅助提示。
- 独立输出和日志、发布/暂存/清理失败退出码，以及原 DDL/普通 Excel 回归。

生产实现位于 `src/main/java/com/example/db2ddl/validation`，相关测试位于 `src/test/java/com/example/db2ddl/validation`。构建日志位于 `target/completion-package.log`，JUnit 结果位于 `target/surefire-reports`。

#### 验证范围限制

真实数据库验证使用本机 TESTDB 的只读查询及系统目录视图，没有创建或修改数据库对象。尚未针对用户实际业务 ITSVIEW、自定义目录、远程服务器权限及业务数据量进行现场验收；这些环境条件不能由本机测试代替。

#### 2026-09-13 独立 main 造数与真实 DB2 验证

新增 `src/test/java/com/example/db2ddl/validation/Db2TestDataMain.java`，prepare/change 两阶段负责 CREATE、INSERT、ALTER、REORG 及测试视图。使用 DB2ADMIN schema 和唯一前缀，正常导出不调用此入口。

真实 TESTDB 造数断言测试 1 项通过；随后全量 Maven package 283 项通过，0 失败，1 项造数测试按默认配置跳过（该项已单独通过）。打包后的 JAR 合并运行也完成：3 张表 DDL、4 行普通 Excel、9 个字段 validation；故意注入的 3 个溢出返回预期退出码 1。

命令与说明见 `verification/generated-data/README.md`；本次成功结果见 `verification/generated-data/run-6063F0991C/RESULTS.md`。测试对象保留，当前成功批次前缀为 `DVTEST_98565B7DFB_`。排查 REORG 时生成的上一批 `DVTEST_CE6A7B9D4B_` 也保留，清理脚本位于 `verification/generated-data/run-1418D5D07D/cleanup.sql`。

#### 2026-09-13 全量 validation QA 更新

292 项全部通过，无失败或跳过；修正及完整审查记录见 [DATATYPE-VALIDATION-QA-REVIEW.md](QA-HISTORY.md#validation-qa-round-1)。构建后的多库三种模式及 DDL/Excel/validation 合并运行均已重新验证，刻意溢出对应退出码 1。

#### Latest QA results

[Round 2 review](QA-HISTORY.md#validation-qa-round-2): 293 tests passed, no failures or skips; baseline object-count consistency fixed and 12 additional DB2 edge fields verified. See [COPY-PASTE-RUN.md](COPY-PASTE-RUN.md) for separate multi-database DDL, Excel, BASELINE and CURRENT commands.

#### Physical conversion and scale checks completed

See [scale acceptance](QA-HISTORY.md#physical-migration-and-scale-acceptance): physical VARCHAR-to-CLOB migration verified, 6,000 real distinct lengths and 12,000 differences checked, and hidden-sheet continuation tested at a reduced boundary. Dedicated integration passed; 293 other regression tests passed.



## Data coverage history

Historical source: `DATATYPE-VALIDATION-DATA-COVERAGE.md`.

> Latest update: items 6 and 7 now have physical-column migration and real 6,000-length DB2 coverage. Continuation uses a test-only 3,000-row boundary; production limits are unchanged. See [scale acceptance](QA-HISTORY.md#physical-migration-and-scale-acceptance). Earlier inspection notes below are historical.

> Update: Round 2 has now added the missing small-data edge scenarios to a new TESTDB fixture. The earlier inspection below describes the older sample batches. See [Round 2 coverage and results](QA-HISTORY.md#validation-qa-round-2).

### TESTDB 数据场景覆盖检查（2026-09-13）

结论：实际数据库已经包含多个 distinct length，但没有包含所有 validation 场景。292 项自动测试通过不代表 292 种数据都已插入 TESTDB。

通过只读 JDBC 查询，核对了 COPY-PASTE-RUN.md 的单库、多库配置及最新 QA 配置引用的三个测试批次，数据分布一致。原始结果见 verification/validation-qa-20260913/live-single-distributions.txt、live-multi-distributions.txt、live-qa-distributions.txt；本次未修改数据库数据。

| 字段 | 当前长度集合 / 数值 | 覆盖场景 |
| --- | --- | --- |
| CASES.S | {5}，共 4 行；固定 baseline {5} 共 3 行 | 单一长度集合相同、计数不同，提示 LIKELY OK |
| CASES.C | {NULL,5,6,101}，各 1 行 | 多个长度、NULL、CLOB 超过原上限 100；baseline 为 {NULL,5,6} |
| CASES.MIX | {NULL,0,2,11}，各 1 行 | 多个长度、空字符串和 NULL 不混淆、VARCHAR 超过原上限 10；baseline 为 {NULL,0,2} |
| NULL_ROWS.V | {NULL}，5 行；baseline 为 3 行 | 全 NULL、计数变化但集合不变，无提示 |
| EMPTY_ROWS.V | 空集合、0 行 | 空表与全 NULL 区分 |
| CASES.N | 4 行，MIN=-99999，MAX=100000 | 原 DECIMAL(5,0) 正负边界、正向溢出、NULL |
| CASES.BIG_N | MIN/MAX 为 DECIMAL(31,0) 的负/正最大绝对值 | 31 位精确数值边界 |
| NULL_ROWS.N / EMPTY_ROWS.N | 分别 5 行 / 0 行，MIN/MAX 均为 NULL | 数值全 NULL / 空表 |

独立造数测试还覆盖 TYPE_CASES 的数值/文本不兼容、非零 scale、FOR BIT DATA，BAD_QUERY 的运行时转换失败及不存在表/字段。COPY-PASTE-RUN.md 常用的 9 字段配置没有包含这些异常条目；需要使用最新 QA 运行目录中的 application-errors.properties。最新批次还包含普通列和前导空格的带引号列共存。

#### 建议补入真实 DB 的数据

1. 多个非 NULL 长度、都在原范围内，baseline/current 集合相同但各桶计数不同：应 Range PASS / Compare PASS，且不提示单一长度匹配。
2. 多个长度都在原范围内，但变更后既删除旧长度又新增新长度：应 Range PASS / Compare DIFFERENT，同时出现 Baseline Only 和 Current Only。当前两个差异样例都是新增长度，且同时 Range FAIL。
3. 单一空字符串 {0}、{NULL,固定长度}、空表与全 NULL 的前后互换：分别验证提示和 NULL 差集；当前只有独立空表/全 NULL，没有同一条目前后互换。
4. 原 VARCHAR/CLOB 长度恰好等于上限，以及上限+1；DECIMAL 负向溢出 -100000。当前有正向溢出和部分超长，但没有完整覆盖这些边界。
5. 中文和补充字符（如 emoji）：同一值 OCTETS 与 CODEUNITS32 的结果不同；当前实际造数都是 ASCII，已有 DB2 长度单位语法测试也仅用 ASCII。
6. 原 VARCHAR 实际扩容为 CLOB 后的单一长度提示；当前 S 始终是 VARCHAR，C 始终是 CLOB。
7. 大量 distinct length、长预览截断和隐藏续表：目前由自动测试生成分布验证，真实 DB 只有少量长度桶。

断连、超时、磁盘写满、损坏/缺失 baseline、并发查询期间计数变化不只是 INSERT 数据场景，应继续通过独立故障注入/集成测试验证；不能宣称填充一个测试表就覆盖所有分支。



## Validation QA round 1

Historical source: `DATATYPE-VALIDATION-QA-REVIEW.md`.

### Validation logic QA / review — 2026-09-13

本次按实现 prompt 检查了 validation 包全部生产类，以及 ConfigLoader、ToolkitApplication、DDL/Excel 连接状态传递和相关测试。确认并修正两类逻辑问题；新增 8 个测试用例，全量 292 项通过，0 失败、0 错误、0 跳过。已重新生成 target/db2-data-toolkit.jar，并在真实 localhost:25000/TESTDB 上验证。

#### 已修正的问题

##### P1：共享总数与数值极值矛盾时可能错误 PASS

首个字符/数值字段查询得到 0 行，另一个事务随后插入数据，后续数值字段不再 COUNT，因此复用的 Record Count 仍为 0，但 MIN/MAX 已有值。原实现仅检查字符桶计数和 overflow > total，正常范围内的新增数字会误判 PASS，还可能生成无法再次读取的矛盾 baseline。

修正 ValidationService：非 NULL 极值至少需要 1 行；不同 MIN/MAX 至少需要 2 行。与共享总数冲突时，将同一对象所有已完成候选行标为 ERROR，清空计数、分布、Compare 和提示；保留缺字段等原错误状态。不增加 COUNT、不重查数据。

同步加强 BaselineReader：拒绝声称只有 1 行却存在不同 MIN/MAX 的数值基线条目。原有空表却有极值、全 NULL 却有溢出等检查保留。

回归覆盖：首字段为 VARCHAR / DECIMAL 的空总数场景、1 行共享总数但后续极值不同、损坏数值基线。并发数据变化使用确定性 JDBC 模拟，不以不稳定的真实并发时序作为测试前提。

##### P2：对 catalog 标识符 trim 会把不同列混为一列

对象可以同时含普通列 A 与带引号的列 " A"。原实现读取所有列时 strip COLNAME，会把它们都变成 A，导致原本可校验的普通列被报重复目录 ERROR；只有 " A" 时也会错误通过 A 的存在性检查，之后才遇到 SQL 错误。

修正 ValidationJdbc：COLNAME 保留目录原值。CSV 仍只允许普通标识符，未新增带引号 CSV 名称支持。类型 schema 保留有意义的前导空格，但去掉 DB2 TYPESCHEMA 的 CHAR 尾部填充；避免把合法 SYSIBM 类型误判为 TYPE MISMATCH。

回归覆盖：A 与 " A" 共存、只有 " A"、SYSIBM 尾部填充以及前导空格的类型 schema。独立 Db2TestDataMain 也新增 NUM_AS_TEXT 和 " NUM_AS_TEXT" 共存的真实测试表，造数及完整校验通过。

#### 各逻辑范围的审查与验证

| 范围 | 检查结论 / 已覆盖行为 |
| --- | --- |
| 配置与多库 | 开关独立；公共值与逐库覆盖；CSV/基线全目标预检先于连接；每库独立连接、输出和计数 |
| CSV / 类型 / 对象名 | BOM、引号、重复字段、缺列、类型扩容限制、长度单位、名称安全引用与视图映射碰撞 |
| metadata | table/view TYPE、列存在性、实际类型、FOR BIT DATA、权限/目录异常不回退；本次修正名称空格 |
| 数值与字符范围 | DECIMAL(31,0) 精确边界、BigInteger 计数、NULL / 空串 / 空表、完整长度桶、共享总数；本次修正极值矛盾 |
| baseline 协议 | ID、模式、类型与单位、完整分布、续表/清单、非法桶、公式、预检快照、损坏文件拒绝 |
| 比较与提示 | 双向完整长度差集；NULL 参与集合；计数差异不参与集合比较；单一非 NULL 长度提示条件 |
| 报告与资源 | 固定 11 列、长预览截断但隐藏分布完整、滚动续表、大数精度、暂存与发布失败不报成功、不覆盖旧报告 |
| 连接与退出码 | 超时、断连、后续未执行、连接关闭/日志/输出故障、跨库继续；0 成功 / 1 结果不通过 / 2 致命错误 |
| 功能组合 | 单库和多库各 8 种 DDL / Excel / validation 组合；顺序与输出隔离 |
| 大输入 | 1,048,575 条 CSV 接受、超限拒绝；大分布、20,000 条双向差异跨续表 |

#### 测试命令与真实数据库结果

```powershell
$env:DB2_VALIDATION_INTEGRATION='true'
$env:DB2_VALIDATION_CONFIG='config/combined/application.properties'
$env:DB2_VALIDATION_FIXTURES='true'
mvn -q '-Dvalidation.large-tests=true' '-DargLine=-Xmx2g' package
```

全量结果：292 tests / 0 failures / 0 errors / 0 skipped。构建日志和统计保存在 verification/validation-qa-20260913/。

打包 JAR 的多库 BASELINE、无基线 VALIDATE、带比较 VALIDATE 均重新运行：当前数据有刻意溢出，Range 都是 6 PASS / 3 FAIL；开启比较时为 3 PASS / 2 DIFFERENT / 4 N/A；查询错误 0。总退出码 1 符合预期，fos 有差异后 rpt 仍完成。两个固定变更前 baseline 文件未覆盖。

打包 JAR 合并运行：3 张表的 DDL 导出成功、普通 Excel 导出 4 行、9 个 validation 字段正确输出。最新真实造数报告位于 verification/generated-data/run-A56C05EEDB/，对象前缀 DVTEST_AEB247D88B_，保留供检查。修正过程中产生的前一批测试对象 DVTEST_BE5BDBA9ED_ 也保留，对应 cleanup.sql 位于 verification/generated-data/run-D8D8B4B9D3/。

#### 保留的设计边界

- 多条 SQL 不构成同一时点快照。本次补上可观察的极值矛盾，仍不能发现所有并发修改。需要一致数据时，应在稳定数据窗口运行；没有擅自增加锁表、长事务或重复 COUNT。
- Compare PASS 仅表示长度集合一致；单一长度提示不证明逐行内容相同、记录数相同或 ALTER 已完成。
- 当前真实数据库验证覆盖本机 DB2 LUW TESTDB；多库示例是两个别名连接同一个物理数据库，不代表已经在两台不同主机或所有 DB2 产品版本验收。
- 未确认其他必须立即修改的 validation 缺陷。上述结论以本次代码审查、回归测试和本机集成范围为限。

#### Round 2 completed

See [Round 2 review](QA-HISTORY.md#validation-qa-round-2): one additional baseline consistency fix, 12 real DB2 edge fields, and 293 passing tests with no skips.



## Validation QA round 2

Historical source: `DATATYPE-VALIDATION-QA-REVIEW-ROUND2.md`.

### Validation QA — Round 2 (2026-09-13)

Full regression: **293 tests, 0 failures, 0 errors, 0 skipped**. The packaged JAR was rebuilt and tested against local TESTDB.

#### Confirmed defect and fix

**P2 — conflicting counts within a baseline object were accepted.** The reader checked each field's distribution independently, but two fields of the same physical object could contain different total record counts. A regression changed both the second field's record count and its bucket count from 2 to 3; the first field remained at 2. The old reader accepted this inconsistent workbook.

BaselineReader now requires all completed entries for one physical object to agree on record count. Failed/unexecuted entries are excluded. This check concerns consistency within the baseline; counts may still differ between baseline and current data. Existing valid references remain compatible.

The regression failed before the fix and passed afterward. Evidence is preserved in verification/validation-qa-round2-20260913/.

#### Additional real DB2 coverage

All setup remains in the independent Db2TestDataMain.main entry point. Three additional uniquely prefixed tables were added: EDGE_VALUES, EMPTY_TO_NULL and NULL_TO_EMPTY. The change phase updates/deletes only data belonging to that generated fixture to simulate disappearing lengths and empty-table transitions.

| Field / scenario | Verified behavior |
| --- | --- |
| MULTI_SAME | {1,2,3} unchanged despite different bucket counts: Range PASS / Compare PASS, no singleton hint |
| MULTI_DIFF | {1,2,3} to {2,3,4}: Range PASS / Compare DIFFERENT; baseline-only 1, current-only 4 |
| ZERO_ONLY | {0}: Range/Compare PASS with singleton hint |
| NULL_MIX | {NULL,3}: Range/Compare PASS without singleton hint |
| BOUNDARY_V / BOUNDARY_C | Lengths 10 / 100 pass; added lengths 11 / 101 each produce one overflow |
| NEG_N | -99999 passes the old DECIMAL(5,0) bound; -100000 overflows |
| AS_CLOB | Baseline VARCHAR table column versus current view exposing CAST AS CLOB; matching singleton {4} produces the hint |
| OCTETS_V / UNICODE_V | Chinese, emoji, A and A+emoji: byte lengths {1,3,4,5}, CODEUNITS32 lengths {1,2}; old limit 4 distinguishes overflow correctly |
| EMPTY_TO_NULL | Empty set to {NULL}: current-only NULL |
| NULL_TO_EMPTY | {NULL} to empty set: baseline-only NULL |

The 12 additional fields were exercised through real metadata queries, aggregation, baseline writing/reading, comparison, workbook generation and assertions. The initial baseline had 12 PASS. Current results: 8 Range PASS / 4 FAIL; 4 Compare PASS / 7 DIFFERENT / 1 N/A; zero query errors. Exit code 1 is expected for the seeded differences.

AS_CLOB tests a view exposing CLOB, not physical VARCHAR-to-CLOB ALTER support.

The successful full-suite fixture and all reports are retained at verification/generated-data/run-E718FE2D88/. Its application-edge-validate.properties was also executed using the packaged JAR, with matching results. The original multi-database comparison sample was rerun successfully against its expected outcomes.

#### Run the edge validation again

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
$env:DB2_PASSWORD='123456'
java -jar target/db2-data-toolkit.jar verification/generated-data/run-E718FE2D88/application-edge-validate.properties
Write-Output "Exit code: $LASTEXITCODE"
```

#### Remaining scope limits

No further confirmed production defect was found in this review. Large distributions, million-row CSV limits and fault injection remain covered by automated tests rather than equivalent amounts of permanent TESTDB data. Multi-statement reads do not guarantee a common snapshot. Length-set equality does not establish row-content equality. The multi-database example uses two aliases on one physical database.

COPY-PASTE-RUN.md now separates multi-database DDL, Excel and validation. Validation is explicitly split into BASELINE and CURRENT; CURRENT uses mode=VALIDATE with baseline comparison enabled.

#### Physical conversion and scale checks completed

See [scale acceptance](QA-HISTORY.md#physical-migration-and-scale-acceptance): physical VARCHAR-to-CLOB migration verified, 6,000 real distinct lengths and 12,000 differences checked, and hidden-sheet continuation tested at a reduced boundary. Dedicated integration passed; 293 other regression tests passed.



## Physical migration and scale acceptance

Historical source: `DATATYPE-VALIDATION-SCALE-ACCEPTANCE.md`.

### Physical CLOB conversion and large-distribution acceptance

The remaining physical-conversion and real-DB distribution checks are complete on local TESTDB. Database setup is contained in the independent `Db2ScaleDataMain.main` entry point; production validation behavior was not changed.

#### 1. Physical VARCHAR-to-CLOB conversion

DB2 rejected `ALTER COLUMN V SET DATA TYPE CLOB(100)` with SQLCODE `-190`. On the isolated fixture table, the setup then performed:

1. Add a physical CLOB column.
2. Copy values and verify the copy before removing the old column.
3. Drop the old VARCHAR column, reorganize the table and rename the CLOB column to the original name.
4. Verify SYSCAT identifies the object as a base table and column V as CLOB.
5. Verify all original row values remain unchanged, then validate the result against the VARCHAR baseline.

The baseline has three rows of length 4; current data has four rows of length 4. Range PASS, Compare PASS and the singleton hint all passed. This is a fixture-specific physical migration, not a claim that direct ALTER is supported or a general-purpose migration tool for production schemas.

#### 2. Real large-distribution test

The fixture contains **6,000 rows with 6,000 distinct CLOB lengths**:

- Baseline: even lengths 0, 2, ..., 11998.
- Current: odd lengths 1, 3, ..., 11999.
- Each of the 6,000 baseline-only and 6,000 current-only values was checked individually.
- Detail and both difference previews explicitly show truncation and stay within the Excel cell limit.
- Hidden distributions and differences retain every entry.

The original CLOB range is deliberately set to 20. Baseline overflow is 5,989; current overflow is 5,990. Range FAIL / Compare DIFFERENT and exit code 1 are expected, not integration-test failures.

#### 3. Hidden-sheet continuation

The production service first writes its normal report from the real DB2 results. While the same validated result data is still available, the production workbook writer also writes a continuation report using its existing **test-only 3,000-row sheet limit**:

- 6,001 total length buckets, including the physical-conversion field: three length sheets.
- 12,000 differences: four difference sheets.
- The continued baseline is loaded by production preflight and used for the current DB2 comparison.
- Every baseline length is checked after readback.

The deployed limit remains **1,048,575 data rows per hidden sheet**. This verifies continuation using real query results at a reduced test boundary; it does not claim that over a million distinct lengths were created in TESTDB.

#### 4. Test results

- Dedicated DB2 scale/conversion integration test: passed.
- Full regression build: 293 passed, 0 failures/errors; the scale test was skipped in that build because it had already passed separately.
- Packaged JAR rerun: one Range PASS / one intentional FAIL; one Compare PASS / one DIFFERENT; zero query errors; expected exit code 1.

Evidence, reports, SQL history and optional cleanup are retained in `verification/scale-data/run-d143f2d4/`. Its database object prefix is `DVSCALE_EEAFD0769A_`.

#### 5. Copy and run the current validation

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
$env:DB2_PASSWORD='123456'
java -jar target/db2-data-toolkit.jar verification/scale-data/run-d143f2d4/application-current.properties
Write-Output "Exit code: $LASTEXITCODE"
```

Expected exit code: `1`. The command reads the saved pre-change baseline and retained current tables. It creates a new report under `verification/scale-data/run-d143f2d4/current-cli/run-*/`.

View [the continued report](verification/scale-data/run-d143f2d4/current-continuation.xlsx) or [the fixture results](verification/scale-data/run-d143f2d4/RESULTS.md).

#### 6. Repeat the complete integration test with fresh objects

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
$env:DB2_VALIDATION_SCALE='true'
mvn -q '-Dtest=Db2ScaleValidationTest' '-DargLine=-Xmx2g' test
$testExit=$LASTEXITCODE
Remove-Item Env:DB2_VALIDATION_SCALE
Write-Output "Test exit code: $testExit"
```

Expected test exit code: `0`. Each run creates fresh uniquely prefixed objects and preserves its results. All CREATE/INSERT/UPDATE/ALTER operations are in `src/test/java/com/example/db2toolkit/datatypevalidation/Db2ScaleDataMain.java`; the JUnit test coordinates baseline/current runs and assertions. No database cleanup is executed automatically.

Disconnections, timeouts, disk failures, damaged baselines and concurrency still require separate fault-injection tests. These data checks do not replace those tests or guarantee a consistent multi-query snapshot.



## Structure review before refactoring

### DB2 Data Toolkit: structure and naming review

Reviewed on 2026-09-13 after the checkout was renamed to `db2-data-toolkit`.

Scope: structural inventory of all 60 production Java files and all 32 test/support Java files, with detailed inspection of application orchestration, configuration, DDL/Excel services, validation execution, baseline parsing, report writing, and logging. This is a maintainability review, not a claim that every possible behavioral defect has been excluded. No production code was changed during this review.

#### Findings, in recommended order

##### 1. Remove log routing's dependency on class names before further renaming

Priority: P2, required for safe naming changes.

`RunLogs.java:19` attaches the appender to the literal `com.example.db2ddl` logger. Lines 62-71 recognize summary events using class-name suffixes and the literal `Database export finished:` message. Renaming `ExportSummaryLogger`, moving the root package, or rewording the outcome message without updating this code can leave process output visible while losing summary-file entries.

Use explicit summary and run-outcome markers, extending the existing `SUMMARY_SUCCESS` approach. Route outcome events to both summary files by marker. Update the root logger deliberately if the root package changes. Verify all three log files for DDL-only, Excel-only, validation-only, and combined runs, including a failing log sink. Merely changing the string suffix preserves the same maintenance trap.

##### 2. Separate toolkit run failures from the DDL report

Priority: P2, structural improvement.

`ToolkitApplication.execute` and `exportConnected` use `ExportReport.stop()` for connection, output-directory, and log failures even when DDL is disabled. `ExportSummaryLogger` handles both the DDL summary and the overall database outcome. Validation-only runs consequently still use wording such as `Starting database export` and `Database export finished`.

Introduce a small `DatabaseRunOutcome` for shared initialization, connection, and log errors. Keep DDL object results in a DDL-specific summary. Extract overall outcome logging into `DatabaseRunLogger`, retaining the existing severity rules and close-failure behavior. Rename `exportConnected` to `executeConnectedFeatures`; it runs all enabled features.

Keep the one-connection-per-alias, sequential execution model. There is no need for a plugin framework or an abstract feature hierarchy.

##### 3. Clarify DDL-specific names and package ownership

Priority: P3, useful once finding 1 is addressed.

`export.ExportService` only exports routine/table DDL, while `ExcelExportService` already declares its scope. The top-level `model` package mixes shared identifiers with DDL-only requests/results.

| Current | Recommended |
| --- | --- |
| `export.ExportService` | `ddl.DdlExportService` |
| `model.ExportRequest` | `ddl.model.DdlExportRequest` |
| `model.ExportResult` | `ddl.model.DdlExportResult` |
| `model.ExportStatus` | `ddl.model.DdlExportStatus` |
| `model.ExportReport` | `ddl.model.DdlExportSummary`, after extracting shared run failures |
| DDL portion of `ExportSummaryLogger` | `ddl.DdlSummaryLogger` |
| `excel.ExportTask` | `excel.ExcelExportTask` |

`DbObjectRef` and `Identifiers` are shared by DDL, Excel, and validation and should remain in a shared location. `ObjectType` describes supported DDL export kinds; if moved, `DdlObjectType` makes that limitation explicit.

##### 4. Separate validation orchestration from comparison and mutable query results

Priority: P2, structural improvement.

`ValidationService.export` groups objects, loads metadata, coordinates shared counts, assigns statuses, compares distributions, creates output directories, and publishes reports. Its static `compare` method implements the length-set merge and singleton hint. `ValidationJdbc.decimal` and `lengths` mutate `ValidationResult` directly, and the same mutable type is reused for baseline entries.

Start with a bounded extraction: `LengthSetComparator`, called by `ValidationService.validateAndWriteReport`. Then introduce explicit numeric and length aggregate results returned by JDBC. Keep shared-count establishment and whole-object inconsistency invalidation together in the service; moving them into independent column validators would risk breaking the existing one-count behavior.

Only introduce a separate read-only `BaselineEntry` if the result-model refactor proceeds. Do not split every short validation class into a new package immediately.

##### 5. Use typed comparison statuses and descriptive validation names

Priority: P2 for status typing; P3 for cosmetic names.

`ValidationResult.java:12` stores comparison outcomes as strings. Service, reader, writer, and summary code repeat string literals and prefix checks. Range outcomes already use an enum. Introduce `CompareStatus` with explicit serialized labels, including the current blank/not-compared state. Preserve workbook text and format-version compatibility.

| Current | Recommended |
| --- | --- |
| `TableNameResolver` | `ValidationObjectResolver` |
| `resolvePhysicalTableName` | `resolveQueryObject` |
| `ValidationCsv` | `ValidationCsvReader` |
| `ValidationJdbc` | `Db2ValidationQueries` |
| `DataType` | `ValidationDataType` |
| `DiskBuckets` | `DiskLengthBuckets` |
| `BaselineFormat` | `ValidationWorkbookFormat` |
| `Range` | `RangeStatus` |
| `hint` | `singleLengthMatchHint` |
| `complete()` | `aggregationComplete()` |

The resolver can return an ITSVIEW view, so the table-only name is misleading. `BaselineFormat` also defines the CURRENT report's visible columns and difference sheets. `aggregationComplete()` must still return true for a completed Range FAIL; it does not mean validation passed.

##### 6. Split configuration by responsibility without changing property keys

Priority: P3.

`AppConfig` combines credentials, DDL object lists/output, catalog views, Excel, and validation. Accessors such as `tables()`, `columns()`, and `output()` do not reveal that they mean catalog views and DDL output. Multiple positional constructors and `with*` copies repeat these settings.

Use `ConnectionConfig`, `CatalogConfig`, and `DdlExportConfig` under the application config, alongside the existing Excel and validation configs. Suggested catalog accessor names are `tableView`, `columnView`, `procedureView`, and `functionView`. Credentials must retain a redacted or absent `toString`; do not convert the password-containing config into a default Java record.

`ValidationConfigLoader.checkKeys` also validates database catalog overrides. Move that shared key validation into `ConfigLoader` or a small catalog configuration helper. Keep `export.*`, `catalog.*`, `validation.*`, alias inheritance, password precedence, and existing sample files compatible.

##### 7. Give workbook parsing and shared Excel utilities clear boundaries

Priority: P3.

`BaselineReader` combines SAX cell decoding, workbook protocol validation, and distribution validation. `DiskSharedStrings` calls back into `BaselineReader.unescape`, while the reader itself consumes `DiskSharedStrings`. `ValidationWorkbookWriter` handles visible formatting and the hidden interchange protocol. `ExcelText` includes a streaming workbook factory in addition to text encoding.

Extract SpreadsheetML text decoding into a focused codec, used by both baseline readers. Keep shared workbook/text/value support distinct from ordinary Excel export tasks. If the protocol grows, extract baseline invariant validation and hidden-sheet encoding next. Do not merge the DDL type renderer with the validation type parser: they intentionally support different type domains and rules.

##### 8. Separate diagnostics from connection classification

Priority: P3.

`SafeErrors` handles secret redaction, exception-chain rendering, and connection-loss detection. It is used for filesystem/configuration errors as well as SQL errors.

Prefer `ErrorSanitizer` (or `SafeDiagnostics`) for rendering/redaction and `ConnectionFailureClassifier` for SQLSTATE/connection health decisions. Preserve traversal of causes, suppressed exceptions, chained SQL exceptions, and cycle detection. Do not add connection probes to successful queries.

##### 9. Align tests and formatting with the final structure

Priority: P3.

DDL/config/output tests mostly reside in the root test package; Excel and validation tests already follow feature packages. Move tests with their production responsibilities after the structural changes. `ExportServiceTest` and `ExportReportTest` should follow the chosen DDL names. Keep cross-feature tests under an application test package. Rename `MultiDatabaseExportTest` only after checking whether it remains specifically about DDL or becomes a toolkit-wide test.

Keep `Db2TestDataMain` and `Db2ScaleDataMain` as independent test entry points. Their CREATE/INSERT/migration code is intentional fixture support and must not be deleted as temporary production code.

Several validation classes put resource handling, mutation, and branching on the same line. Format those blocks consistently before modifying their behavior. Treat formatting as a separate change to keep later review diffs readable.

#### Root package decision

`com.example.db2ddl` can become `com.example.db2toolkit` for product consistency, but this is lower priority than the responsibility and logging changes. Keeping `Main` as the entry class is appropriate.

A package migration must update source/test imports, Maven's `mainClass`, the runtime logger root, verification Java tools, documentation, and any reflection/class-name references. Rebuild without stale classes and confirm the packaged JAR contains no old classes. Do not rename individual classes merely to include the product name. `ToolkitApplication`, `ExcelExportService`, `ValidationService`, `BaselineReader`, `TableDdlExtractor`, and `RoutineDdlExtractor` are reasonable names.

#### Recommended implementation sequence

1. Replace class-name/message-based summary routing with markers; verify failure routing.
2. Separate database run outcome from DDL results and update generic run wording.
3. Apply the focused DDL/validation naming changes and typed compare statuses.
4. Extract length comparison and aggregate results, preserving object count ownership.
5. Consider configuration, workbook codec, test-package, and root-package cleanup in separate changes.

No change to report columns, baseline protocol, CSV headers, configuration keys, or SQL semantics is required for this work.

#### Verification in the renamed checkout

Checkout: `C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit`.

`mvn -q package` succeeded: 294 tests discovered, 289 passed, 5 skipped, zero failures/errors. The opt-in database fixture/scale and large-input tests were not enabled in this build; these results do not represent a rerun of every earlier opt-in suite.

The packaged JAR contains `application/ToolkitApplication.class`, no `ExportApplication` class, and the valid `com.example.db2ddl.Main` manifest entry. Active source/config/POM/README/copy-paste guide scans found no old artifact name or old application class reference. The four CLI run logs contain no old checkout path.

| Real TESTDB command | Exit | Observed result |
| --- | --- | --- |
| Multi-database DDL | 1 | Both aliases ran; existing objects exported and the four documented missing sample objects reported |
| Multi-database Excel | 0 | Both aliases completed |
| Multi-database BASELINE | 1 | 9 fields: Range PASS 6 / FAIL 3; Compare N/A 9; query failures 0 |
| Multi-database CURRENT | 1 | 9 fields: Range PASS 6 / FAIL 3; Compare PASS 3 / DIFFERENT 2 / N/A 4; query failures 0 |

BASELINE was run against the already changed sample data, explaining its three range failures. The saved pre-change reference workbooks were not replaced. The exit-1 results above are expected sample outcomes, not rename failures. These runs queried existing fixture objects; they did not create or modify database data.

Evidence: `target/rename-verification.log` and `target/renamed-run-{ddl,excel,baseline,current}.log`. Maven build artifacts and new report/log output were generated by verification.


## Temporary workspace cleanup QA

Date: 2026-09-14.

Two resource-lifecycle defects were reproduced and fixed:

1. `ValidationPlan.close()` marked the plan closed before deleting staged files. If deletion failed, the batch's final close skipped the plan and left temporary files behind. The closed state is now committed only after successful cleanup, allowing the batch to retry.
2. `ValidationWorkspace.close()` attempted directory deletion again after successful closure, causing `NoSuchFileException`. It is now idempotent after success, rejects new allocations after closure, and does not touch files created later at a reused directory path.

Cleanup still attempts the remaining owned files after one file fails and reports the original error with suppressed cleanup errors. It does not recursively delete unknown files. A previously reported fatal cleanup result is not downgraded if a later cleanup attempt succeeds.

Two regression tests failed against the original implementation and pass with the fix. They cover a simulated temporary file lock, batch cleanup retry, repeated close and preservation of unrelated content at a reused path. Application-level failure and publication tests also passed. The SQL, report protocol and baseline files were not changed.




## Data type validation naming

Date: 2026-09-14. The Java package is now `com.example.db2toolkit.datatypevalidation`. `Validation*` application classes and corresponding tests use the `DataTypeValidation*` prefix. Two descriptive exceptions are `DataTypeSpec` (previously `ValidationDataType`) and `Db2DataTypeQueries` (previously `Db2ValidationQueries`). Specific names such as `LengthSetComparator`, `BaselineReader` and `DiskLengthBuckets` are retained.

Source imports, verification Java tools, runnable source paths, README and implementation prompt references were updated. Existing `validation.*` properties, CSV columns, report names, workbook sheet names and baseline format are unchanged. Older class names in the historical sections below describe their original checkpoints.

A clean package build completed with 297 tests: 295 passed, 2 opt-in fixture tests skipped, zero failures/errors. Read-only DB2 integration and large CSV tests were enabled. The JAR contains the new package and no old `db2toolkit.validation` classes; all three fixed baseline hashes are unchanged. Evidence: `verification/datatype-naming/package.log` and `test-summary.json`.





## JDBC timeouts and phase logging — 2026-09-14

Added per-connection JCC login/read timeouts, a separate DDL/Excel statement timeout, and periodic waiting messages. Global settings support per-alias overrides. Validation retains its independent statement timeout. Queries log preparation, execution and result-consumption durations; connections log opening and closing durations. Background progress is routed by database and run scope, with shared redaction, and stops when the activity closes. No additional count queries or cancellation worker were introduced.

Verification: Maven package completed with 305 tests: 303 passed, 2 optional generated/scale fixture tests skipped, zero failures/errors. Read-only TESTDB integration and large-file tests were enabled. New tests cover default/alias configuration, invalid settings, JCC property forwarding, an actual TCP server that accepts but never responds, waiting during execute/read phases, redaction, run isolation, stopping progress, and exception/resource preservation.

All four local multi-database CLI examples were run. Exit codes were DDL=1 (expected missing sample objects), Excel=0, BASELINE=1 and CURRENT=1 (expected out-of-range fixture data). Validation recorded zero query failures. All eight per-database process logs contain connect/query/read/close phase timing. The three fixed baseline hashes remain unchanged. Evidence: results (historical artifact removed), Maven log (historical artifact removed). Configuration semantics and limitations are documented in COPY-PASTE-RUN.md. Driver timeouts are not a guaranteed process deadline; no forced thread termination or process supervisor was added.



## Production code and performance review — 2026-09-14

Reviewed 72 production Java files across application orchestration, configuration, JDBC/logging, DDL, Excel, validation, baseline parsing, and staging/publication. Reviewed regression coverage and ran the full suite with read-only DB2 integration, the million-item CSV boundary, and the optional shared-string lookup probe enabled.

Fixed findings:

| Priority | Finding | Change and evidence |
| --- | --- | --- |
| P2 | Completed SXSSF sheets retained their final row windows until workbook publication. Many tasks/rollovers multiplied retained row/cell objects. | Flush completed Excel task/rollover sheets and validation hidden continuation sheets. A new test reproduced the pre-fix retention, asserts release before publication, and reads back every value/header. Existing continuation round-trip now checks that hidden windows are released before workbook serialization. Sheet metadata still grows with sheet count. |
| P2 | Excel-resaved baseline shared strings performed repeated random disk lookups and unbuffered primitive writes. | Buffer index/data writes, read fixed headers in bulk, and cache at most 1,024 strings / 262,144 UTF-16 characters. Random-access Unicode reads, eviction and bounds are verified. A synthetic 30,000-lookup probe measured 836.775 ms before and 11.305 ms after (14.931 ms in the full suite); this measures repeated string lookup only, not total export speed. |
| P2 | A malformed CSV row could accumulate arbitrarily many fields before the expected column count was checked. | Reject at the sixth field boundary. A regression confirms rejection before reading an unterminated remainder. Valid four/five-column CSV and the actual million-item row boundary still pass. |
| P2 | Failure to create the baseline string index happened before input-stream ownership entered try-with-resources. | Close the input on initialization failure and preserve cleanup diagnostics. A failing-before/passing-after test verifies closure. |

Validation: `mvn -q -Dvalidation.large-tests=true -Dvalidation.performance-tests=true -DargLine=-Xmx2g package` with read-only integration enabled: 310 tests, 308 passed, 2 optional fixture-creation/scale tests skipped, zero failures/errors. Packaged JAR updated. Existing cleanup, broken baseline, timeout, query-count, Unicode and Excel round-trip regressions passed.

Read-only real TESTDB runs through the existing PerformanceProbe: multi-DDL queried 16 statements; multi-Excel queried 8; multi-BASELINE and multi-CURRENT queried 15 each. Every run opened and closed two connections, with zero query failures. Expected exits remain 1/0/1/1. BASELINE: Range 6 PASS / 3 FAIL, Compare 9 N/A. CURRENT: Range 6 PASS / 3 FAIL, Compare 3 PASS / 2 DIFFERENT / 4 N/A. All three fixed baseline SHA-256 values remain unchanged.

Remaining performance limits: validation still aggregates columns separately, so shared record counts do not imply a single table scan. CSV definitions and baseline item metadata remain in heap; all aliases are preflighted before the first connection. DDL retains the largest individual routine body. Synchronous log flushing and output on network drives can add latency. Driver timeouts remain distinct from a hard process deadline. No database execution-plan profiling or production-scale throughput claim is made by this review.

Evidence: results and run directories (historical artifact removed), full package log (historical artifact removed), row-window reproduction (historical artifact removed), input-resource reproduction (historical artifact removed). Runtime explanations updated in README.md and EXCEL-EXPORT.md; no new root guide was created.



## Markdown copy-and-run review — 2026-09-14

Reviewed all 20 Markdown files; all 43 PowerShell fences parse and local Markdown file-link targets exist. Historical QA/RESULTS documents remain historical records, while the implementation prompt and business-server configurations remain templates/contracts.

Current guides now include the absolute checkout directory in their PowerShell blocks. Local validation blocks include DB2_PASSWORD instead of relying on an earlier snippet. COPY-PASTE-RUN.md's four export blocks build the JAR if missing and stop on build failure. File-content templates are distinguished from terminal commands. The Excel YAML example identifies where to save it and distinguishes its expected workbooks from the supplied YAML. DDL expected missing-object exits and mixed password sources are clarified. Placeholder integration credentials were replaced by the real local read-only test configuration, and old QA totals are labeled as checkpoints.

The fixture guide now leads with the complete repeatable test workflow, restores its opt-in flag and checks exit status. Optional manual prepare uses a fresh directory; change requires the explicitly selected prepare directory and a previously captured baseline. These writing workflows were statically reviewed, not rerun during this documentation-only task.

Executed the four COPY-PASTE-RUN.md export blocks verbatim starting outside the checkout. DDL/Excel/BASELINE/CURRENT exits were 1/0/1/1 as documented. Validation reported zero query failures and the expected Range/Compare results. Evidence: documentation command verification (historical artifact removed). No production Java or configuration behavior changed.



## Bug review: configuration, publication races and baseline text — 2026-09-14

Follow-up QA reviewed startup validation, JDBC/error paths, result handling, publication and baseline decoding against the prior production-wide review. Seven new failing reproductions confirmed three bug classes:

- P2: misspelled connection settings (including timeout/progress keys) and unknown connection aliases could be ignored. ConfigLoader now rejects unknown connection keys/aliases before opening a database. Supported connection keys and catalog/validation namespaces retain their normal behavior. This deliberately tightens validation; unused typo keys must be corrected or removed.
- P2: DDL publication used ATOMIC_MOVE, which on the tested Windows filesystem replaced a destination created after filename selection even without REPLACE_EXISTING. Publication now uses the same non-replacing staged move policy as Excel overwrite=false. The race test confirms that the other file survives and the temporary file is cleaned. No cross-platform atomic no-replace guarantee is claimed.
- P2: shared-string staging encoded decoded unpaired UTF-16 surrogates as replacement characters, silently changing damaged baseline text; inline text lacked equivalent validation. Both formats now validate decoded Unicode before use. Tests cover high/low malformed surrogate escapes, malformed inline baseline preflight, valid supplementary characters and literal escaped text.

Targeted regressions passed after repair. Full package with read-only TESTDB integration, the actual million-item CSV boundary and the shared-string performance probe enabled: 319 tests, 317 passed, 2 optional fixture/scale tests skipped, zero failures/errors. Four supplied multi-database configurations ran successfully with expected exits DDL=1, Excel=0, BASELINE=1 and CURRENT=1. Validation had zero query failures and expected range/set differences. All fixed baseline hashes are unchanged. The JAR was rebuilt. Database query rules, count reuse and comparison semantics were not changed.

Evidence: failing reproductions (historical artifact removed), package log (historical artifact removed), results and actual run paths (historical artifact removed). Passing tests cover the exercised cases; they do not prove that no other bugs exist, particularly across untested driver/filesystem failures or concurrent data changes.




## Mandatory SQL read-only guard — 2026-09-14

Moved the existing conservative SELECT scanner into `jdbc.ReadOnlyQueries`. Every production JDBC preparation site (routine/table catalogs, data type queries and Excel exports) now goes through this guard. Excel configuration preflight delegates to the same implementation. Explicit writes, DDL, CALL, multiple statements, SELECT INTO, FOR UPDATE and sequence increments are rejected before driver preparation; there is no opt-out configuration. Native JDBC statements and result sets are preserved. Database function side effects cannot be inferred by this lexical check, so database privileges remain necessary for a strict read-only boundary. Test fixture writers remain separate and excluded from the application JAR.

Added 22 test cases covering rejected operations before both preparation overloads, accepted CTE/scalar REPLACE queries, and direct Excel service invocation that bypasses configuration preflight. Full Maven package with read-only TESTDB integration, large CSV and performance tests enabled: 341 tests, 339 passed, 2 optional fixture/scale tests skipped, zero failures/errors. Rebuilt `target/db2-data-toolkit.jar` and confirmed the guard is included while fixture mains are absent. README and Excel instructions describe the enforced check and its limits.





## Separate password-source run guides — 2026-09-14

COPY-PASTE-RUN.md now uses file-based credentials and COPY-PASTE-RUN-ENV.md uses password environment variables. The four original standalone multi-database configurations now contain explicit passwords; four sibling `*-env.properties` configurations retain environment-based passwords. Both guides provide independent DDL, Excel, BASELINE and CURRENT commands without build conditionals. Usernames remain file-based. README links were updated.

Parsed all ten PowerShell blocks and checked local links and configuration password sources. Executed all eight export blocks against local TESTDB; cleared DB2_PASSWORD before testing each guide. Both methods produced expected DDL/Excel/BASELINE/CURRENT exit codes 1/0/1/1 (missing sample DDL objects and deliberate validation differences). No status=QUERY_ERROR messages were observed. All three preserved baseline hashes match. No Java changes or rebuild were needed.





## Data type validation configuration directory — 2026-09-14

Renamed the configuration directory to `config/datatypevalidation` and updated guide commands, links, implementation prompt, configuration test and retained verification path indexes. Configuration keys remain `validation.*`. All configuration inputs, saved baselines and existing outputs were retained. Two files initially held open by another application were copied and hash-verified, then the old directory was removed after the files were closed.

All 29 DataTypeValidationConfigTest cases passed. Both file-password and environment-password BASELINE/CURRENT commands ran from the new location against local TESTDB: expected exits 1/1 for each method, with zero query failures and the existing intentional range/set differences. Quick-guide links resolve and all three preserved baseline hashes match. No production Java changes or JAR rebuild were required. Historical raw logs retain paths as emitted at the time of each run.





## Export configuration layout — 2026-09-14

Split local export examples into `config/ddlexport`, `config/excelexport` and `config/combined`, alongside `config/datatypevalidation`. DDL object lists live in ddlexport, Excel task YAML files in excelexport; combined properties reference those sibling resources. Removed the former directory after moving its contents. Updated run guides, fixture test configuration paths and retained verification indexes. Existing output destinations remain unchanged.

Verified guide links and absence of old configuration path references outside generated output/build logs. Ran both password-source multi-DDL and multi-Excel configurations and both single/multi combined configurations against local TESTDB: exits 1/1/0/0/1/1, with zero fatal results and expected missing DDL example objects. All 53 configuration tests passed. No production Java behavior changes or JAR rebuild were needed.





## Generic template placement — 2026-09-14

Moved root-level DDL properties and object lists into `config/ddlexport` with template suffixes; moved Excel properties and YAML into `config/excelexport` with template suffixes. Updated template input paths and preserved their output destinations after the depth change. Updated implementation-prompt references, README template links and combined-configuration paths in the usage example. Only shared logback.xml remains as a file directly under config.

Loaded all three relocated properties templates using the application's ConfigLoader (1/2/1 targets), without opening database connections. Verified referenced list/YAML files and current guide links. Removed the temporary source-file verification helper. Runnable local examples and production code were unchanged.



## Verification cleanup — 2026-09-14

Removed 43 obsolete diagnostic/evidence files: five raw QA result directories, two standalone Java probes, obsolete structure-refactor results, and fixture run logs. Retained all non-log generated-data and scale-data materials, including cleanup.sql, executed SQL, fixture configurations, state markers, baselines and replay reports. The structure-refactor manifest now retains only the three baseline hashes. File hashes confirmed all retained fixture files were unchanged and the three fixed baselines still match. No SQL was executed and no application code changed.

README no longer advertises removed probes; links to removed verification artifacts are marked historical. An encoding error during this documentation cleanup truncated QA-HISTORY.md. It was reconstructed from local Git source-document objects and session-recorded subsequent updates, retaining the six original review sections and all recorded follow-up sections; byte-for-byte identity with the prior combined file is not claimed.


## Validation report colors — 2026-09-14

Applied the agreed dark-blue header and pale status colors, with separate red failures, yellow differences, green passes, gray N/A and blue single-length hints. Styles are shared per workbook. Full Maven package passed: {'tests': 341, 'failures': 0, 'errors': 0, 'skipped': 6}. Ran multi-database CURRENT against TESTDB: expected exit 1, zero query failures. Inspected saved workbook XML for the actual header, PASS, FAIL, DIFFERENT, N/A and hint fills. Hidden baseline protocol and validation semantics remain unchanged.


## Database IDs in report filenames — 2026-09-14

New reports are named datatype-validation-<database-id>.xlsx using the effective validation.database-id. Updated application filename and publication-collision assertions and quick guides. Existing baseline paths are unchanged. Full Maven package passed: {'tests': 341, 'failures': 0, 'errors': 0, 'skipped': 6}. Local multi-database CURRENT generated separate FOS_TEST and RPT_TEST filenames with expected exit 1 and zero query failures.


## Production-wide review — 2026-09-15

Reviewed the 73 production Java files and relevant paths in the 38 test/helper files, covering application orchestration, configuration, SQL preparation, DDL extraction, validation aggregation/comparison, baseline decoding, spreadsheet values/styles and resource ownership. No production code was changed.

Confirmed P2: top-level configuration key validation is incomplete. With export.enabled=false and a misspelled excel-export.confg key, ConfigLoader disables Excel and ToolkitApplication reports a successful no-op with exit 0. A misspelled catalog.table.veiw=CUSTOM.OBJECTS is also accepted and resolves to the default SYSCAT.TABLES rather than failing preflight. Reject unknown export.*, excel-export.* and global catalog.* keys consistently with the existing database and validation key checks. Reproduced through ConfigLoader and ToolkitApplication without database connections.

Full test suite with read-only TESTDB integration, large CSV and performance checks enabled: 341 tests, 339 passed, 2 optional fixture/scale tests skipped, zero failures/errors. Additional read-only DB2 parser checks did not confirm suspected keyword issues; file-name checks did not establish an overwrite defect, so neither is reported as a bug. Passing tests do not cover every possible driver, filesystem or concurrency failure. Temporary review helpers and logs were removed.

## Global configuration key validation fix — 2026-09-15

Resolved the preceding review finding. ConfigLoader now rejects unknown global catalog.*, export.* and excel-export.* keys before loading Excel YAML or opening a database. Checks use the resolved credentials for diagnostic redaction and remain active when an export is disabled. Valid optional settings retain their defaults when omitted; explicitly disabling every feature remains a successful no-op. README documents the exit-code-2 behavior and common misspellings.

Added 15 regression cases covering all ten supported global setting names with misspellings, error precedence over a missing Excel YAML file, password redaction, and application preflight instead of false success. All 15 failed against the previous implementation and passed after the fix. Full Maven package with read-only TESTDB integration, large CSV and performance checks enabled: 356 tests, 354 passed, 2 optional fixture/scale tests skipped, zero failures/errors. All 25 supplied properties files loaded successfully through ConfigLoader from the rebuilt JAR without database connections; a misspelled excel-export.confg passed to the executable JAR returned exit 2 as expected. All three preserved baseline hashes match. Removed the temporary verification helper, smoke configuration, build logs and unshaded backup JAR.


## Code review and explanatory comments - 2026-09-15

Reviewed configuration preflight, application orchestration, DDL/Excel publication, JDBC activity tracking, data type metadata and aggregation, baseline parsing and length-set comparison. No new confirmed defect was found in these reviewed paths. Added English comments in 11 production Java files explaining disabled-feature key validation, preflight snapshots, shared row counts and scan limitations, NULL buckets, count-independent length comparison, single-length hint semantics, partial-result invalidation, hidden baseline data, report publication and JDBC timing/context ownership.

Verified that all 73 production Java files have unchanged executable text after excluding comments and whitespace. No runtime logic or configuration was changed. Maven package passed: 356 tests, 350 passed, 6 opt-in integration/fixture/large-data/performance cases skipped, zero failures/errors. Rebuilt the executable JAR, verified all three preserved baseline hashes, and removed the temporary build log and unshaded backup JAR.


## All-Markdown review - 2026-09-15

Reviewed all 21 Markdown files: current run guides, validation/Excel contracts, configuration READMEs, implementation prompt, refactor history, QA history and retained fixture results. Corrected current validation report filename patterns, PASS colors, logical versus physical object naming, the multi-database BASELINE password source, Excel output-directory defaults and disabled-export validation scope, and YAML-relative paths. Updated the implementation prompt's obsolete API/configuration statements. Refactor notes now identify deleted helpers and raw evidence as retired, and runnable historical replay commands use the current checkout path. Existing report filenames and test totals in historical records were preserved.

Static verification passed for all 47 PowerShell code blocks, 100 local links/section anchors and 44 command input file references. Referenced reports in all seven retained RESULTS.md files exist. Current settings were checked against the source/configuration files. This documentation-only review did not execute database commands or rerun tests; Java, configurations, baselines and the previously built JAR were unchanged. No temporary review files were created.


## Per-database SQL audit and filename prefixes - 2026-09-16

- Added `<alias>-sql.log` in each database run directory. Catalog, Excel and validation SQL preparation, parameter binding and execution attempts are flushed before JDBC calls; guard rejections and prepare/execute failures are recorded. Credentials are redacted and native JDBC objects remain unwrapped. Exported DDL and driver-internal SQL are outside this log.
- Log initialization precedes connecting that alias. Audit write failures prevent the pending JDBC call; write/close failures return exit code 2. Later database aliases remain independent.
- All newly generated application logs and Excel files start with the configured database alias (`db.names` or `db.name`, default `default`). Validation uses `<alias>-datatype-validation.xlsx`; ordinary exports prefix the configured workbook filename. The workbook length limit includes the prefix. Existing historical files and baselines retain their names.
- Updated current documentation and output assertions. Full Maven package with large/performance tests and read-only DB2 integration: 361 tests, 359 passed, 2 skipped, zero failures/errors. Fixture and scale-data mutation tests were disabled.
- Actual multi-database combined export and CURRENT validation both returned expected exit code 1 (missing DDL sample objects and intentional FOS validation differences). Both validation reports published with zero query failures; RPT passed. Verified SQL-log isolation and filename prefixes for local_a/local_b/fos/rpt.
- All three saved baseline SHA-256 hashes still match the preservation manifest.


## Slow-query warnings and two-stage cancellation - 2026-09-16

- Added db.slow-query-seconds (default 60; zero disables) and db.cancel-grace-seconds (default 15; positive), including per-alias overrides and configuration validation. Shipped examples explicitly use 300-second DDL/Excel and validation query limits and 360-second read timeout.
- QueryDeadline observes the execute/read/process/cleanup scope. At the query deadline it attempts Statement.cancel on a daemon worker; after the grace period an independent worker attempts Connection.abort. The native connection is captured before execution, so escalation does not depend on a stuck statement accessor. Progress timers do not perform JDBC calls.
- Completion disarms pending callbacks. Late results after a deadline cannot be published as successful validation/Excel results. Outstanding cancellation or attempted abort makes the connection unusable; no retries are introduced. TIMEOUT diagnostics preserve existing ERROR/FAILED report categories and the baseline format. SQLSTATE 57014 also covers external cancellation.
- Tests cover disarming, late-success rejection, blocked cancel with independent abort, unusable connection classification, alias overrides, zero-grace rejection, scoped slow warnings and continuing to the next database after escalation. Existing audit assertions now recognize TIMEOUT diagnostics.
- Final Maven package with large/performance tests and read-only local DB2 integration: 367 tests, 365 passed, 2 skipped, no failures/errors. Fixture and scale-data mutation tests were disabled. Cancellation failures were simulated with JDBC mocks; no deliberate heavy SQL was run on DB2.
- Documentation describes scope and limits: preparation is outside the application deadline; fetching/processing count toward it; driver cancel/abort and cleanup are best effort, not process isolation or guaranteed hard termination.


## Parallel database execution, one connection per alias - 2026-09-16

- Multiple configured aliases now run on independent workers after all shared inputs and validation baselines pass preflight. Each alias opens at most one connection and executes DDL, ordinary Excel and validation serially on it. Single-database execution retains its existing path. No connection pool or automatic reconnect was added.
- Each worker owns its summaries, MDC context, JDBC activity context and SQL audit scope. The coordinator merges results after completion and retains baseline snapshots until all workers finish. Interrupted waits return a batch failure after worker cleanup rather than attributing the interruption to a successful database.
- RunLogs now reference-counts concurrent scopes before restoring the shared logger level. Closing is synchronized with appends and idempotent. Per-database files remain isolated, including timeout/progress messages; console messages may interleave.
- Tests prove simultaneous connection attempts, exactly one connection per alias, independent completion while another database waits, retained per-database feature ordering, SQL/process log isolation, logger-level restoration only after the final scope closes, and safe coordinator interruption. Existing fault injection targets only the intended alias; test bookkeeping no longer assumes opening/closing order across aliases.
- Final Maven package with large/performance tests and read-only local DB2 integration: 370 tests, 368 passed, 2 skipped, zero failures/errors. Fixture and scale-data mutation tests remained disabled. Runtime JAR rebuilt and current documentation updated.
- Concurrency is per alias: two aliases targeting physical TESTDB use two connections in total. A stuck driver no longer prevents other workers from starting or completing, but final process completion still waits for every worker. No process-isolation guarantee was added.


## Full regression and concurrency/timeout review - 2026-09-16

Scope: full production compilation (75 Java files), configuration/SQL entry-point and mutable-state inventory, targeted review of orchestration, JDBC, deadlines, logging, DDL publication, Excel cleanup and validation/baseline ownership, plus the complete regression suite. This does not establish absence of all bugs.

Fixed findings:

1. The shared deadline timer performed synchronous diagnostic logging while holding its scope monitor. A blocked log appender could delay cancellation and abort across databases. Diagnostic delivery now runs on separate daemon workers; the timer performs state transitions and dispatch only. A fault-injection test blocks logging and still observes both cancel and abort.
2. Completion checked only the timer callback's expired flag. A delayed timer could let an over-limit scope report success. Completion now checks elapsed monotonic time independently, and JdbcActivity disarms/checks the deadline before its end-log write. A regression blocks the timer and verifies late completion throws SQLTimeoutException.
3. RunLogs filtered database events only after AppenderBase acquired its synchronized writer lock. A blocked file for one database therefore blocked unrelated databases before their events could be rejected. Filtering now happens before that lock. A regression stalls one file writer while the second database logs and closes successfully.
4. An interrupt received during executor termination could arrive after the coordinator's failure check. The interruption check now follows worker termination and interrupt restoration, preserving safe snapshot lifetime and returning a batch failure.

Validation: final Maven package succeeded with large/performance tests and read-only DB2 integration enabled: 373 tests, 371 passed, 2 skipped, zero failures/errors. Fixture and scale-data mutation tests were disabled. Deadline/log stalls and concurrent execution were fault-injected; no deliberately expensive database SQL was executed. Existing tests continue to cover one connection per alias, feature ordering, cross-database isolation, timeout escalation, output/baseline integrity, NULL/length/numeric boundaries, resource failures and redaction. Runtime JAR rebuilt.

Residual limits: cancellation and abort remain driver-dependent and do not provide process isolation; final completion waits for all workers. A blocked shared console can still block callers writing to that console. Concurrency is per configured alias, not deduplicated by physical JDBC URL.

===== END FILE =====

===== FILE: README.md =====
UTF8-BYTES: 19100
SHA256: f8df033e18914b07b8f61af7886f7b64e2497ea101319d4a5cfb2a7ceebfb93d
===== CONTENT =====
# DB2 Data Toolkit

Run commands in Windows PowerShell after building the JAR; replace the absolute checkout path if yours differs. Properties, YAML, SQL and CSV blocks are file contents, not terminal commands. Local examples require the retained TESTDB objects.

Export stored procedures, functions and basic table DDL to SQL files, or table/view/SELECT results to Excel. Supports one or multiple databases, using one connection per database. The exporter reads data; it does not execute the exported DDL. All production SQL passes a mandatory SELECT-only guard before JDBC preparation, including catalog queries, data type checks and Excel queries. It rejects explicit data-changing SQL, DDL, CALL, multiple statements and sequence increments; there is no configuration switch to disable it. This lexical guard cannot determine side effects inside database functions: database privileges must enforce a strict read-only boundary. Test data creation mains remain separate under `src/test` and are excluded from the application JAR.

Data type validation can run independently or together with either export. It checks original numeric/length ranges, creates BASELINE reports, and optionally compares current distinct length sets with a selected baseline. See [Data type validation](DATATYPE-VALIDATION.md) for configuration, examples and report semantics.

The Maven artifact is `db2-data-toolkit`; the executable is `target/db2-data-toolkit.jar`. The checkout directory is `db2-data-toolkit`, as used in the commands below.

## Quick start

Requires Java 17+ and Maven. In PowerShell:

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
mvn package
```

After `BUILD SUCCESS`, export SP, Function, Table and Excel together:

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
java -jar target/db2-data-toolkit.jar config/combined/application.properties
```

For multiple database targets:

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
java -jar target/db2-data-toolkit.jar config/combined/application-multi.properties
```

These examples use the supplied local TESTDB. Both multi-database aliases point to that same server; edit their connection properties to use different databases.

| Guide | Contents |
| --- | --- |
| [Commands without environment variables](COPY-PASTE-RUN.md) | DDL, Excel, BASELINE and CURRENT; credentials in properties files |
| [Commands with environment variables](COPY-PASTE-RUN-ENV.md) | Same four operations; passwords from environment variables |
| [Usage examples](USAGE-EXAMPLES.md) | Change connections, object lists and output paths |
| [Excel configuration](EXCEL-EXPORT.md) | Simple, grouped and advanced exports |
| [Data type validation](DATATYPE-VALIDATION.md) | BASELINE/VALIDATE, multi-database comparison and independent feature switches |
| [Local example](config/combined/README.md) | Included files and expected output |

## Configuration basics

The JAR takes one `.properties` file. Excel YAML is referenced by `excel-export.config`; do not pass YAML directly to the JAR.

- Use `db.username` and `db.password` for a single database.
- For multiple databases, use `db.names` and `db.<alias>.url/username/password`.
- Relative paths resolve from the configuration file containing them.
- Use UTF-8 and `/` in paths. Java Properties requires `\\` for a literal backslash.
- Direct password properties take precedence over `password-env`. Empty explicit passwords are rejected; passwords are not trimmed.
- Configuration edits do not require rebuilding the JAR.
- Unknown keys under `db.*`, `validation.*`, global `catalog.*`, `export.*` and `excel-export.*` fail preflight with exit code **2**, before any database connection opens. For example, `catalog.table.veiw` must be `catalog.table.view`, and `excel-export.confg` must be `excel-export.config`. Optional settings use defaults only when omitted; misspelled keys are rejected even when their feature is disabled.

Each database runs enabled DDL exports, ordinary Excel tasks, then validation on the same connection. Validation defaults to disabled; existing properties keep their behavior. Databases run concurrently, with one worker and at most one connection per configured alias. Within each alias, all features and queries remain serial. Outputs and logs are isolated. All inputs are preflighted before any worker opens a connection; final summaries are aggregated after every worker completes. An individual database failure does not stop other aliases.

## Output and logs

DDL creates a new `run-*` directory each time. Excel uses fixed workbook names; repeat exports follow the YAML `overwrite` setting.

| Mode | DDL and logs | Excel files |
| --- | --- | --- |
| Single database, combined | `<ddl-output>/run-*/` | `<excel-output>/` |
| Multiple databases, combined | `<ddl-output>/db-<alias>/run-*/` | `<excel-output>/db-<alias>/` |
| Excel only | Logs under `<excel-output>/[db-<alias>/]run-*/` | Next to the log directory |

The console prints the current `Run directory:`. Open it to read:

| Log | Contents |
| --- | --- |
| `<alias>-summary_success.log` | Successful objects, output files, Excel sheets and row counts |
| `<alias>-summary_issue.log` | Missing objects, unavailable definitions, failures and skipped/unprocessed work |
| `<alias>-process.log` | Connection lifecycle and full execution history |

Both summaries include the database alias and final outcome. If log writing fails, check the console and the last outcome in surviving logs.

Exit codes: **0** success, **1** incomplete export, **2** configuration, initialization or runtime failure. An issue log exists even on a successful run.

## Per-database SQL attempt log

Every enabled database run creates `<alias>-sql.log` beside `<alias>-process.log`, `<alias>-summary_success.log` and `<alias>-summary_issue.log` in its existing `run-*` directory. The console prints its exact path as `SQL audit log:`. Aliases come from `db.names`, or single-database `db.name` (default `default`), not the physical JDBC database or `validation.database-id`. No additional configuration is required. Preflight errors and runs with all features disabled do not create these logs.

All new Excel filenames also start with the alias: `<alias>-datatype-validation.xlsx` for validation and `<alias>-<configured-workbook>.xlsx` for ordinary exports. Saved baselines and historical output retain their existing names.

Entries include a UTC timestamp, database alias, run-local SQL ID, SQL text and parameter binding attempts (index, JDBC type and value). DDL catalog reads, ordinary Excel SELECTs and data type metadata/aggregate queries all use the same logging entry point. SQL is logged before preparation, and parameter/execute attempts are flushed before their JDBC calls, so a stalled operation leaves an attempt record. Rejections at the JDBC preparation guard and preparation/execute exceptions are recorded too. Earlier configuration validation failures remain console diagnostics.

Passwords and connection secrets are redacted, and control characters are flattened for single-line entries. SQL text and parameter values are separate; the log is diagnostic output, not a replay script. Attempt records do not prove completion or successful result consumption; use each database's `<alias>-process.log` and summaries for timing and final outcomes. Exported CREATE statements are file contents and are not logged as executed SQL. Driver-internal SQL and the independent test fixture writers are outside this application log.

Failure to initialize a database's SQL log prevents that database from connecting; later aliases are still attempted. A write failure prevents the pending preparation/bind/execute call; audit-log write/close failures make the run and batch return exit code 2. The database's output directory must be writable.

## DDL behavior

SP/Function export uses one query per object, returning all overloads in SPECIFICNAME order. The first successful instance uses `SCHEMA.OBJECT.sql`; collisions use a specific-name suffix or safe hashed filename. Full definitions are read without truncation.

Definitions must begin with the appropriate CREATE prefix, allowing comments and OR REPLACE. Missing or body-only definitions become `DDL_UNAVAILABLE`. This checks the prefix, not complete SQL grammar or cross-database dependencies.

All SQL files use UTF-8. The `@` delimiter follows the last statement on the next line, with no added blank line. Original routine semicolons are preserved. Configure the executing client to use `@`, for example DB2 CLP `-td@`.

### Table export scope

Table output includes ordered columns, supported built-in types, nullability and defaults. Ordinary uppercase names are unquoted; names requiring delimiters retain quoting.

```sql
CREATE TABLE RPT.CUSTOMER (
    CUSTOMER_ID INTEGER NOT NULL
)
@
```

- Compact formatting uses `TIMESTAMP`, `CHARACTER(n)`, `VARCHAR(n)` and `DECIMAL(p,s)`.
- Omitting timestamp precision and character units uses target defaults and may change source semantics. This is the requested display behavior.
- Ordinary base tables are supported. Temporal, identity, generated, hidden and other unsupported attributes or types produce `DDL_UNAVAILABLE`.
- Primary/foreign keys, constraints, indexes, triggers, privileges and physical storage settings are not exported. This is basic table DDL, not a full migration backup.

### Catalog sources

Defaults are SYSCAT.PROCEDURES, SYSCAT.FUNCTIONS, SYSCAT.TABLES and SYSCAT.COLUMNS. Override their views with `catalog.procedure.view`, `catalog.function.view`, `catalog.table.view` and `catalog.column.view`.

Custom views must expose the fixed fields in [CatalogFields.java](src/main/java/com/example/db2toolkit/catalog/CatalogFields.java). No automatic catalog fallback or field remapping is performed. SQL errors are FAILED; only zero matching rows are NOT_FOUND.

## Excel usage

See [EXCEL-EXPORT.md](EXCEL-EXPORT.md) for complete YAML examples. All three modes share the same streaming exporter. Headers come from JDBC column labels, including SQL aliases. Large results roll over into additional sheets.

## Testing and verification: runnable examples

Run from the project root:

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
mvn test
mvn package
```

Results are in `target/surefire-reports/`. `mvn clean` removes `target`, including saved verification results.

Use the maintained configurations in [COPY-PASTE-RUN.md](COPY-PASTE-RUN.md) for manual exports. Automated regression tests live under `src/test/java/com/example/db2toolkit/`; the old standalone verification configuration/script suite has been retired.

To include read-only TESTDB integration and the large CSV boundary test:

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
$env:DB2_VALIDATION_INTEGRATION='true'
$env:DB2_VALIDATION_CONFIG='config/combined/application.properties'
mvn '-Dvalidation.large-tests=true' '-DargLine=-Xmx2g' test
```

Fixture-creation and scale tests remain opt-in; see [Data type validation](DATATYPE-VALIDATION.md) and [QA history](QA-HISTORY.md). The removed manual million-row Excel command is not a claim of equivalent coverage by the default test suite.

`verification/` retains fixture SQL, configurations, baselines and replay reports, plus baseline hashes. Obsolete manual diagnostic helpers and raw QA logs have been removed. Historical conclusions are consolidated in [QA history](QA-HISTORY.md).

## Performance and code structure

SP/Function use one query per configured object; ordinary tables usually use two. Network latency therefore matters for large lists. Routine memory use grows with the largest individual definition. Excel streams rows, but needs temporary disk space; frequent log flushing can be slower on network drives.

Completed Excel sheets and validation continuation sheets flush their row windows before publication. Workbook/sheet metadata still grows with the number of sheets. Baseline shared strings use buffered disk staging and a bounded cache (at most 1,024 entries and 262,144 UTF-16 characters). CSV item definitions and baseline item metadata remain in memory; disk-backed length buckets do not make total memory usage constant.

Validation reuses the object record count, but still runs a separate aggregate per column. Many columns on one large view can therefore cause repeated scans. Use the per-query timings to identify slow fields before considering combined aggregates; no automatic parallel scans or transaction isolation changes are made.

Use the built-in per-query timings in `<alias>-process.log` to investigate slow exports.

Source root: `src/main/java/com/example/db2toolkit/`.

| Package | Responsibility |
| --- | --- |
| `application` | Multi-database orchestration and overall run outcomes |
| `config` | Connection, catalog, DDL, Excel and validation configuration |
| `ddl`, `ddl.model` | DDL export execution, requests, results and summaries |
| `input`, `model` | Object lists and shared database identifiers |
| `jdbc`, `catalog` | Connections, diagnostics and catalog reads |
| `sql`, `output` | DDL formatting, file publication and marker-routed logs |
| `excel` | Ordinary Excel export configuration and execution |
| `spreadsheet` | Shared streaming workbooks and lossless cell serialization |
| `datatypevalidation` | Range checks, length comparison, baseline protocol and reports |

Start reading at `Main`, then `application.ToolkitApplication`. Feature services are `ddl.DdlExportService`, `excel.ExcelExportService`, and `datatypevalidation.DataTypeValidationService`. Dependency versions are maintained in `pom.xml`.

The Java package is now `com.example.db2toolkit`. Configuration keys, CSV headers and the baseline workbook protocol remain compatible. See [Structure refactor](CODE-STRUCTURE-REFACTOR.md) for the completed changes and verification.

## Documentation

- [Copy, paste and run](COPY-PASTE-RUN.md): independent multi-database DDL, Excel, BASELINE and CURRENT commands.
- [Data type validation](DATATYPE-VALIDATION.md) and [Excel export](EXCEL-EXPORT.md): detailed configuration and behavior.
- [Usage examples](USAGE-EXAMPLES.md): additional configuration examples.
- [Implementation prompt](DATATYPE-VALIDATION-IMPLEMENTATION-PROMPT.md): requirements and acceptance contract.
- [Completed structure refactor](CODE-STRUCTURE-REFACTOR.md): historical refactor and verification; the package table above describes current architecture.
- [QA history](QA-HISTORY.md): consolidated earlier reviews and acceptance records.


Local runnable configurations are grouped by function: `config/ddlexport` for DDL, `config/excelexport` for Excel, `config/datatypevalidation` for data type checks, and `config/combined` for DDL plus Excel. Combined configurations reuse the lists and YAML files in the sibling export directories. Existing `output/local-example` destinations are retained.


Generic templates are stored alongside their feature examples:

- [Single-database DDL template](config/ddlexport/application-template.properties) and [multi-database DDL template](config/ddlexport/application-multi-template.properties) use the three `*-list-template.txt` files in the same directory.
- [Excel template](config/excelexport/application-template.properties) uses [excel-export-template.yaml](config/excelexport/excel-export-template.yaml).

Replace template credentials and object names before running. Ready-to-run local examples retain their existing names. `config/logback.xml` remains at the root because logging is shared across features.


## Slow queries and cancellation

The supplied configurations use these settings (seconds):

```properties
db.connect-timeout-seconds=30
db.query-timeout-seconds=300
validation.query-timeout-seconds=300
db.read-timeout-seconds=360
db.progress-interval-seconds=10
db.slow-query-seconds=60
db.cancel-grace-seconds=15
```

`db.query-timeout-seconds` applies to DDL catalog and ordinary Excel queries. Validation uses its separate `validation.query-timeout-seconds`. Each is passed to the JDBC driver. In addition, an application deadline starts immediately before `executeQuery` and lasts through fetching, application row processing and resource cleanup. It does not cover statement preparation. At the deadline the application attempts `Statement.cancel()` on a daemon thread; after the grace period it independently attempts `Connection.abort()` if the scope is still open. This is an elapsed query-scope limit, not pure DB server execution time.

`db.slow-query-seconds` emits one `SLOW_QUERY` warning per scope with database alias, SQL ID, object/task context and elapsed time in `<alias>-process.log`. Match the SQL ID to `<alias>-sql.log`. Zero disables the slow warning; zero query timeout disables both the driver query limit and the application deadline for that query category. `db.cancel-grace-seconds` must be at least 1. These DB settings support `db.<alias>.*` overrides. Defaults when omitted remain query timeout 0, read timeout 120, slow warning 60 and cancel grace 15; the shipped examples explicitly set longer limits.

Timeout diagnostics are prefixed `TIMEOUT` in logs and failure reasons. Existing report status categories remain ERROR/FAILED, preserving the baseline format. SQLSTATE 57014 is classified as TIMEOUT/cancellation (it can also indicate an external cancellation). There is no automatic retry. A completed cancellation may allow subsequent work if the connection passes its health check; an aborted connection or an unfinished cancellation is treated as unusable, so remaining work on that database is skipped while other aliases continue independently.

A watchdog timeout always makes the query fail, even if the driver eventually returns rows. Cancellation runs separately from the progress timer. Neither cancel nor abort guarantees that a stuck driver or resource close will return; the current implementation has no process isolation or hard wall-clock termination. A database that never returns does not block other database workers, but still prevents the overall process from finishing. Network read timeout is a separate driver setting, not a total-run time limit.


Concurrency is per configured alias: two aliases pointing to the same physical DB2 database each use their own connection (two in total). The local multi-database examples deliberately do this. There is no connection pool, reconnect loop or concurrent query execution within an alias. Console messages can interleave; use the per-database log files to inspect an individual run.


Deadline diagnostics run outside the shared timeout scheduler, so a blocked log sink does not prevent cancel/abort dispatch. Query completion also checks elapsed time directly, preventing a delayed timer callback from turning a timed-out scope into success. Per-database file appenders reject unrelated events before acquiring their writer locks. A blocked shared console or a driver that never returns still has the limitations described above.

===== END FILE =====

===== FILE: USAGE-EXAMPLES.md =====
UTF8-BYTES: 4753
SHA256: ae1d6a5760dd44ae46437ec5909761d53b41aea1f0c5b843ef0aa79e46e78c7c
===== CONTENT =====
# Configure your own exports

Run commands in Windows PowerShell after building the JAR; replace the absolute checkout path if yours differs. Properties, YAML, SQL and CSV blocks are file contents, not terminal commands. Local examples require the retained TESTDB objects.

For an immediate local run, use [COPY-PASTE-RUN.md](COPY-PASTE-RUN.md). This guide explains how to change the supplied configuration. Commands run from the project root.

## Single database

Edit [config/combined/application.properties](config/combined/application.properties):

```properties
db.url=jdbc:db2://localhost:25000/TESTDB
db.username=db2admin
db.password=123456

export.enabled=true
export.procedure-list=../ddlexport/sp-list.txt
export.function-list=../ddlexport/function-list.txt
export.table-list=../ddlexport/table-list.txt
export.output-directory=../../output/local-example/ddl

excel-export.config=../excelexport/excel-export.yaml
```

Then run:

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
java -jar target/db2-data-toolkit.jar config/combined/application.properties
```

Replace the connection values for another database. Paths are relative to the properties file. Excel output is configured separately in YAML.

## Choose SP, Function and Table objects

Edit these files, using one `SCHEMA.OBJECT` per line:

| File | Local example |
| --- | --- |
| [sp-list.txt](config/ddlexport/sp-list.txt) | `DB2ADMIN.PR_GEN_COMPLEX_CTE_FLOW_D` |
| [function-list.txt](config/ddlexport/function-list.txt) | `APPLICATION.DETERMINECUSTOMERACCESS` |
| [table-list.txt](config/ddlexport/table-list.txt) | `APPLICATION.CITIES_ARCHIVE` |

Blank lines and whole-line `#` comments are ignored. Duplicate normalized names are exported once. Invalid lines are reported while valid entries continue.

Ordinary names are uppercased. Use double quotes for exact mixed case or special characters:

```text
RPT.CUSTOMER
"RPT"."CustomerArchive"
```

All three list files are required when DDL is enabled. For Table-only, leave the SP and Function files empty; the same approach works for other individual types. An empty list skips that type.

## Multiple databases

Edit [application-multi.properties](config/combined/application-multi.properties). Each alias has its own connection; lists and Excel tasks are shared.

```properties
db.names=local_a,local_b

db.local_a.url=jdbc:db2://localhost:25000/TESTDB
db.local_a.username=db2admin
db.local_a.password=123456

db.local_b.url=jdbc:db2://localhost:25000/TESTDB
db.local_b.username=db2admin
db.local_b.password=123456
```

Both example aliases point to the same TESTDB. Change each URL, username and password to connect to separate servers. Keep the existing export settings, then run:

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
java -jar target/db2-data-toolkit.jar config/combined/application-multi.properties
```

Add an alias to `db.names` and its connection properties for another database. Aliases must be unique ignoring case, start with a letter, and contain 1-40 ASCII letters, digits, `_` or `-`. Each database gets its own `db-<alias>` output directories and tagged logs.

## Choose an export mode

The supplied files are ready to use:

| Mode | Properties file under `config/` |
| --- | --- |
| DDL + Excel | `combined/application.properties` |
| DDL only | `ddlexport/application-ddl.properties` |
| Excel only | `excelexport/application-excel.properties` |
| Multiple databases, DDL + Excel | `combined/application-multi.properties` |

For example:

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
java -jar target/db2-data-toolkit.jar config/excelexport/application-excel.properties
```

To enable Excel in another properties file, set `excel-export.config` to an existing YAML file, relative to that properties file. For example, a file under `config/combined/` can use `excel-export.config=../excelexport/excel-export.yaml`. Enable Excel inside that YAML. To disable DDL, set `export.enabled=false`; DDL lists are then unnecessary. See [Excel configuration](EXCEL-EXPORT.md) for objects and SELECT queries.

## Find results

The console prints `Run directory:`. That directory contains `<alias>-process.log`, `<alias>-summary_success.log` and `<alias>-summary_issue.log`.

- SUCCESS: a file or workbook task was exported.
- NOT_FOUND: a configured DDL object was not found.
- DDL_UNAVAILABLE: the object exists, but its definition is missing or unsupported.
- FAILED / UNPROCESSED: a request failed or could not run; check its reason.

See [output locations](README.md#output-and-logs) and [table limitations](README.md#table-export-scope). After changing configuration, rerun the same Java command; rebuilding is unnecessary.

===== END FILE =====

===== FILE: config/combined/README.md =====
UTF8-BYTES: 3565
SHA256: b406ade9e5449c3ae02e3b67433aab42653ecab5e3f829494683941fd219b26e
===== CONTENT =====
# Combined DDL and Excel examples

Run commands in Windows PowerShell after building the JAR; replace the absolute checkout path if yours differs. Properties, YAML, SQL and CSV blocks are file contents, not terminal commands. Local examples require the retained TESTDB objects.

Ready-made configurations for `localhost:25000/TESTDB`, username `db2admin`, password `123456`. The standard examples store passwords in properties; see [commands without environment variables](../../COPY-PASTE-RUN.md). The separate `*-env.properties` examples use `DB2_PASSWORD`; see [commands with environment variables](../../COPY-PASTE-RUN-ENV.md).

## Build

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
mvn package
```

After `BUILD SUCCESS`, choose a command below.

## Run

SP, Function, Table and Excel together:

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
java -jar target/db2-data-toolkit.jar config/combined/application.properties
```

Multiple database targets:

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
java -jar target/db2-data-toolkit.jar config/combined/application-multi.properties
```

The aliases `local_a` and `local_b` both use the supplied TESTDB. Edit their URL, username and password for different databases. For DDL-only or Excel-only commands, see [the quick guide](../../COPY-PASTE-RUN.md).

## Files to edit

| File | Purpose |
| --- | --- |
| [application.properties](application.properties) | Single database, DDL + Excel |
| [application-multi.properties](application-multi.properties) | Multiple databases, DDL + Excel |
| [application-ddl.properties](../ddlexport/application-ddl.properties) | DDL only |
| [application-excel.properties](../excelexport/application-excel.properties) | Excel only |
| [sp-list.txt](../ddlexport/sp-list.txt) | SP names |
| [function-list.txt](../ddlexport/function-list.txt) | Function names |
| [table-list.txt](../ddlexport/table-list.txt) | Table names |
| [excel-export.yaml](../excelexport/excel-export.yaml) | Single-database Excel tasks |
| [excel-export-multi.yaml](../excelexport/excel-export-multi.yaml) | Multi-database Excel tasks |

## Output

| Mode | DDL and logs | Excel |
| --- | --- | --- |
| Single database | `output/local-example/ddl/run-*/` | `output/local-example/excel/` |
| Multiple databases | `output/local-example/multi/ddl/db-<alias>/run-*/` | `output/local-example/multi/excel/db-<alias>/` |

The shared lists also contain four deliberately missing objects. Exit code `1` is expected for these DDL examples; check the issue summary. The three successful files are:

- `procedures/DB2ADMIN.PR_GEN_COMPLEX_CTE_FLOW_D.sql`
- `functions/APPLICATION.DETERMINECUSTOMERACCESS.sql`
- `tables/APPLICATION.CITIES_ARCHIVE.sql`

Excel creates `<alias>-SYSDUMMY1.xlsx`, `<alias>-reference.xlsx` and `<alias>-report.xlsx` (single-database alias defaults to `default`). The report contains DATA (SQL aliases and an exact long ID) and CITIES (up to 100 rows, without a guaranteed order).

The console prints the current `Run directory:`. Open `<alias>-summary_success.log` for results, `<alias>-summary_issue.log` for problems, or `<alias>-process.log` for the full history. Repeat runs create new DDL directories and replace same-name Excel files.

To reuse elsewhere, copy `config/combined`, `config/ddlexport` and `config/excelexport` together and edit the connection values. Combined examples share object lists from `ddlexport` and Excel task files from `excelexport`. Configuration changes need no rebuild.

===== END FILE =====

===== FILE: config/combined/application-multi.properties =====
UTF8-BYTES: 864
SHA256: c885f6189a72f8167bfedd0598d4b0d06cab002ae7bf509d8356c310cf32f5f6
===== CONTENT =====
# Both aliases use the supplied local TESTDB so this example runs immediately.
# For different servers, replace each alias's URL, username and password below.
db.names=local_a,local_b

db.local_a.url=jdbc:db2://localhost:25000/TESTDB
db.local_a.username=db2admin
db.local_a.password=123456

db.local_b.url=jdbc:db2://localhost:25000/TESTDB
db.local_b.username=db2admin
db.local_b.password=123456

# Shared object lists; DDL and Excel run once per configured alias.
export.enabled=true
export.procedure-list=../ddlexport/sp-list.txt
export.function-list=../ddlexport/function-list.txt
export.table-list=../ddlexport/table-list.txt
export.output-directory=../../output/local-example/multi/ddl

excel-export.config=../excelexport/excel-export-multi.yaml

db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/combined/application.properties =====
UTF8-BYTES: 523
SHA256: 72aabb818e5d9d69a9f78899d6ea87e86b0633d94dadca6307db49a9b0403163
===== CONTENT =====
# Ready-to-run example for the supplied local TESTDB.
db.url=jdbc:db2://localhost:25000/TESTDB
db.username=db2admin
db.password=123456

export.enabled=true
export.procedure-list=../ddlexport/sp-list.txt
export.function-list=../ddlexport/function-list.txt
export.table-list=../ddlexport/table-list.txt
export.output-directory=../../output/local-example/ddl

excel-export.config=../excelexport/excel-export.yaml

db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/datatypevalidation/application-all.properties =====
UTF8-BYTES: 934
SHA256: 8eef7fcc0742a3295fade16abf1cfe9e421e27f0ee906fc9d373f9239632abe5
===== CONTENT =====
# DDL, ordinary Excel and validation all enabled, sharing one connection.
db.url=jdbc:db2://current-host:50000/FOSDB
db.username=current_user
db.password-env=DB2_PASSWORD
export.enabled=true
export.procedure-list=./sp-list.txt
export.function-list=./function-list.txt
export.table-list=./table-list.txt
export.output-directory=../../output/validation/all/ddl
excel-export.config=./excel-export.yaml

catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.mode=VALIDATE
validation.database-id=FOS
validation.file=./datatype-fos.csv
validation.output-directory=../../output/validation/all/validation
validation.itsview.enabled=true
validation.itsview.schema=ITSVIEW
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
validation.baseline.compare.enabled=false

db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/datatypevalidation/application-baseline.properties =====
UTF8-BYTES: 855
SHA256: 7ba842e14b1074f06f965480cbb62e453247e57c34aca2e98d04306eaee04be3
===== CONTENT =====
db.names=fos,rpt
db.fos.url=jdbc:db2://baseline-host:50000/FOSDB
db.fos.username=baseline_user
db.fos.password-env=DB2_FOS_PASSWORD
db.rpt.url=jdbc:db2://baseline-host:50000/RPTDB
db.rpt.username=baseline_user
db.rpt.password-env=DB2_RPT_PASSWORD
export.enabled=false

catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.mode=BASELINE
validation.output-directory=../../output/validation/baseline
validation.itsview.enabled=false
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
validation.baseline.compare.enabled=false

db.fos.validation.database-id=FOS
db.fos.validation.file=./datatype-fos.csv
db.rpt.validation.database-id=RPT
db.rpt.validation.file=./datatype-rpt.csv

db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/datatypevalidation/application-single-baseline.properties =====
UTF8-BYTES: 702
SHA256: 614a777824806120ec5976aa2d5dfb0878356c5fe7239bbd68340a451e6897ed
===== CONTENT =====
# Replace connection values and edit datatype-fos.csv for your actual objects.
db.url=jdbc:db2://baseline-host:50000/FOSDB
db.username=baseline_user
db.password-env=DB2_PASSWORD
export.enabled=false

catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.mode=BASELINE
validation.database-id=FOS
validation.file=./datatype-fos.csv
validation.output-directory=../../output/validation/single-baseline
validation.itsview.enabled=false
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
validation.baseline.compare.enabled=false

db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/datatypevalidation/application-single-validate.properties =====
UTF8-BYTES: 785
SHA256: 059695981ecdc2b8d50032c3caa56db7bf31f0581b719de48596b3d1c4786676
===== CONTENT =====
db.url=jdbc:db2://current-host:50000/FOSDB
db.username=current_user
db.password-env=DB2_PASSWORD
export.enabled=false

catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.mode=VALIDATE
validation.database-id=FOS
validation.file=./datatype-fos.csv
validation.output-directory=../../output/validation/single-current
validation.itsview.enabled=true
validation.itsview.schema=ITSVIEW
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
validation.baseline.compare.enabled=true
# Explicitly copy your chosen BASELINE report to this location before running.
validation.baseline.file=./baseline/fos-baseline.xlsx

db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/datatypevalidation/application-validate-no-compare.properties =====
UTF8-BYTES: 649
SHA256: e575fa34711a2cbc1d0dba72d2465f4bf82231c73eee080462984486eaf11c9c
===== CONTENT =====
db.url=jdbc:db2://current-host:50000/FOSDB
db.username=current_user
db.password-env=DB2_PASSWORD
export.enabled=false

catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.mode=VALIDATE
validation.database-id=FOS
validation.file=./datatype-fos.csv
validation.output-directory=../../output/validation/range-only
validation.itsview.enabled=true
validation.itsview.schema=ITSVIEW
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
validation.baseline.compare.enabled=false

db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/datatypevalidation/application-validate.properties =====
UTF8-BYTES: 1218
SHA256: bd4ca0708adcbc3a1ef85b277a07bc2b1b960a09f11164639deaf75d446319c5
===== CONTENT =====
db.names=fos,rpt
db.fos.url=jdbc:db2://current-host:50000/FOSDB
db.fos.username=current_user
db.fos.password-env=DB2_FOS_PASSWORD
db.rpt.url=jdbc:db2://current-host:50000/RPTDB
db.rpt.username=current_user
db.rpt.password-env=DB2_RPT_PASSWORD
export.enabled=false

catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.mode=VALIDATE
validation.output-directory=../../output/validation/current
validation.itsview.enabled=false
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
validation.baseline.compare.enabled=true

db.fos.validation.database-id=FOS
db.fos.validation.file=./datatype-fos.csv
db.fos.validation.baseline.file=./baseline/fos-baseline.xlsx
db.rpt.validation.database-id=RPT
db.rpt.validation.file=./datatype-rpt.csv
db.rpt.validation.baseline.file=./baseline/rpt-baseline.xlsx
db.rpt.validation.itsview.enabled=true
db.rpt.validation.itsview.schema=ITSVIEW
# Optional per-database catalogs must expose the documented fixed catalog fields.
# db.rpt.catalog.table.view=MYCAT.OBJECTS
# db.rpt.catalog.column.view=MYCAT.COLUMNS

db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/datatypevalidation/baseline/README.md =====
UTF8-BYTES: 248
SHA256: ebf65994b50999798255ce23d300e7e910524a401b7d884ebb54bd590f2e3cd3
===== CONTENT =====
Copy the selected FOS and RPT **BASELINE-mode** reports here as `fos-baseline.xlsx` and `rpt-baseline.xlsx` before running the comparison examples. No baseline is selected automatically. A VALIDATE-mode report is not a baseline, even when renamed.

===== END FILE =====

===== FILE: config/datatypevalidation/datatype-fos.csv =====
UTF8-BYTES: 210
SHA256: cf5a8dcbddd45ef9bbcf8ca7a24ff31a7c6650c44c6994933dc7b8e84abfc3c2
===== CONTENT =====
table_name,field_name,original_data_type,after_data_type
fos.share_deal,deal_number,"DECIMAL(5,0)","DECIMAL(20,0)"
fos.share_deal,reference,VARCHAR(50),VARCHAR(65)
fos.share_deal,comments,CLOB(1000),CLOB(5000)

===== END FILE =====

===== FILE: config/datatypevalidation/datatype-rpt.csv =====
UTF8-BYTES: 130
SHA256: a9b5e9147e13bcf9c8b4e5504a851416cb9229b06f6803c0967d511661500f23
===== CONTENT =====
table_name,field_name,original_data_type,after_data_type,length_unit
rpt.account_entry,description,VARCHAR(100),CLOB(1000),OCTETS

===== END FILE =====

===== FILE: config/datatypevalidation/excel-export.yaml =====
UTF8-BYTES: 205
SHA256: 9093d201889b46d3d3f16747bf415d1462b9aa71b7a4b98a899c94d46b2cfa75
===== CONTENT =====
excel-export:
  enabled: true
  output-directory: ../../output/validation/all/excel
  include-header: true
  default-fetch-size: 1000
  overwrite: true
  simple:
    tables:
      - ITSVIEW.FOS_SHARE_DEAL

===== END FILE =====

===== FILE: config/datatypevalidation/function-list.txt =====
UTF8-BYTES: 71
SHA256: d01a33068201aba73759dd6cff2e86a4321248f4b432aa060557bbc89adf41f0
===== CONTENT =====
# Replace with actual schema.function names, or leave this list empty.

===== END FILE =====

===== FILE: config/datatypevalidation/local-testdb-multi/README.md =====
UTF8-BYTES: 8609
SHA256: a876df8c5adf1c8a9b1c2dd341b43fe2cfc27d841954de120f7d94a24917a061
===== CONTENT =====
# 本机多数据库 validation 示例

Run commands in Windows PowerShell after building the JAR; replace the absolute checkout path if yours differs. Properties, YAML, SQL and CSV blocks are file contents, not terminal commands. Local examples require the retained TESTDB objects.

## 多数据库 validation：使用本机 TESTDB 的可运行示例

示例位于 `config/datatypevalidation/local-testdb-multi/`。`db.names=fos,rpt` 定义两个连接别名，目前都连接 `jdbc:db2://localhost:25000/TESTDB`。这是在一个物理数据库上验证多库调度；不会创建两个物理数据库。切换实际多库时分别修改 `db.fos.*` 和 `db.rpt.*` 的 URL、账号、密码环境变量及 CSV 对象。

| 别名 | 逻辑数据库 ID | CSV | VALIDATE 查询对象 | 固定 baseline |
| --- | --- | --- | --- | --- |
| fos | FOS_TEST | validation-fos.csv | DB2ADMIN 下的映射视图，独立 catalog 覆盖 | baseline/fos-reference.xlsx |
| rpt | RPT_TEST | validation-rpt.csv | DB2ADMIN 下的表，公共 SYSCAT 目录 | baseline/rpt-reference.xlsx |

每个别名仅建立一个连接，各别名并行运行、同一别名内串行处理；同一别名的 BASELINE/VALIDATE 必须保持相同 database-id。文件路径相对于 properties 所在目录，输出自动按 `db-fos` / `db-rpt` 分开。

### BASELINE 配置

The supplied BASELINE file stores passwords directly. The legacy VALIDATE and non-baseline files below use `DB2_PASSWORD`. For matching password sources across BASELINE/CURRENT, use [file-password commands](../../../COPY-PASTE-RUN.md) or [environment-variable commands](../../../COPY-PASTE-RUN-ENV.md).

`config/datatypevalidation/local-testdb-multi/application-baseline.properties`：

```properties
# Two independent connection aliases using the same local physical TESTDB.
db.names=fos,rpt
db.fos.url=jdbc:db2://localhost:25000/TESTDB
db.fos.username=db2admin
db.fos.password=123456
db.rpt.url=jdbc:db2://localhost:25000/TESTDB
db.rpt.username=db2admin
db.rpt.password=123456
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.length-unit=OCTETS
validation.query-timeout-seconds=30
db.fos.validation.database-id=FOS_TEST
db.fos.validation.file=validation-fos.csv
db.rpt.validation.database-id=RPT_TEST
db.rpt.validation.file=validation-rpt.csv
validation.mode=BASELINE
validation.output-directory=output/baseline
validation.itsview.enabled=false
validation.baseline.compare.enabled=false
```

### VALIDATE 配置

`config/datatypevalidation/local-testdb-multi/application-validate.properties`：

```properties
# Two independent connection aliases using the same local physical TESTDB.
db.names=fos,rpt
db.fos.url=jdbc:db2://localhost:25000/TESTDB
db.fos.username=db2admin
db.fos.password-env=DB2_PASSWORD
db.rpt.url=jdbc:db2://localhost:25000/TESTDB
db.rpt.username=db2admin
db.rpt.password-env=DB2_PASSWORD
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.length-unit=OCTETS
validation.query-timeout-seconds=30
db.fos.validation.database-id=FOS_TEST
db.fos.validation.file=validation-fos.csv
db.rpt.validation.database-id=RPT_TEST
db.rpt.validation.file=validation-rpt.csv
validation.mode=VALIDATE
validation.output-directory=output/validate
validation.itsview.enabled=false
validation.baseline.compare.enabled=true
db.fos.validation.baseline.file=baseline/fos-reference.xlsx
db.rpt.validation.baseline.file=baseline/rpt-reference.xlsx
# FOS validates mapped views; RPT validates tables directly.
db.fos.validation.itsview.enabled=true
db.fos.validation.itsview.schema=DB2ADMIN
# Optional per-database catalog overrides, tested with these fixture views.
db.fos.catalog.table.view=DB2ADMIN.DVTEST_E40A284FA5_CAT_OBJECTS
db.fos.catalog.column.view=DB2ADMIN.DVTEST_E40A284FA5_CAT_COLUMNS
```

### 直接运行（PowerShell）

```powershell
cd C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit
$env:DB2_PASSWORD='123456'
# 生成两个数据库别名各自的 baseline
java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb-multi/application-baseline.properties
$LASTEXITCODE
# 使用已经保存的变更前基线，执行两个别名的 validation
java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb-multi/application-validate.properties
$LASTEXITCODE
```

### 已完成的实际验证

先通过独立 main 创建新一批测试数据 `DB2ADMIN.DVTEST_E40A284FA5_*`，再运行多库 BASELINE，两库合计 9 个字段全部 PASS，退出码 0。将本次日志明确列出的两个报告分别保存到上表中的固定 baseline 文件，再通过 main 扩容并插入异常数据，最后运行多库 VALIDATE。

| 模式 / 别名 | Range | Compare | 查询错误 | 退出码 |
| --- | --- | --- | --- | --- |
| BASELINE / fos | 5 PASS | 5 N/A | 0 | 0 |
| BASELINE / rpt | 4 PASS | 4 N/A | 0 | 0 |
| VALIDATE / fos | 2 PASS、3 FAIL | 1 PASS、2 DIFFERENT、2 N/A | 0 | 1 |
| VALIDATE / rpt | 4 PASS | 2 PASS、2 N/A | 0 | 0 |

VALIDATE 总退出码 1 是刻意插入溢出数据的预期结果，fos 有差异后 rpt 仍然完成。数据和两个固定基线均已保留，可以直接重跑 VALIDATE。

**数据库现在已处于变更后状态。** 现在重跑 BASELINE 会生成反映当前异常数据的新报告（总退出码 1），不会自动覆盖两个变更前的固定 baseline 文件。需要更新基线时，从日志中明确选择对应别名的报告再复制，不自动选择最新文件。

造数记录及可选清理脚本：`verification/generated-data/multi-example-20260913/`。

## BASELINE 与 non-baseline（不读取基线）直接运行

程序 mode 只接受 BASELINE / VALIDATE；不存在 NON_BASELINE 这个 mode。
`application-non-baseline.properties` 使用 VALIDATE，并关闭 baseline compare，因此不需要基线文件，只执行原类型范围校验。
若 non-baseline 指“变更后的环境，需要比较基线”，使用原有 `application-validate.properties`（compare=true）。

| 配置 | mode | baseline.compare.enabled | 用途 |
| --- | --- | --- | --- |
| application-baseline.properties | BASELINE | false | 采集数据并生成可供后续比较的基线报告 |
| application-non-baseline.properties | VALIDATE | false | 无基线，独立校验原类型范围 |
| application-validate.properties | VALIDATE | true | 校验范围并比较固定基线 |

完整 non-baseline 配置：

```properties
# Two independent connection aliases using the same local physical TESTDB.
db.names=fos,rpt
db.fos.url=jdbc:db2://localhost:25000/TESTDB
db.fos.username=db2admin
db.fos.password-env=DB2_PASSWORD
db.rpt.url=jdbc:db2://localhost:25000/TESTDB
db.rpt.username=db2admin
db.rpt.password-env=DB2_PASSWORD
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.length-unit=OCTETS
validation.query-timeout-seconds=30
db.fos.validation.database-id=FOS_TEST
db.fos.validation.file=validation-fos.csv
db.rpt.validation.database-id=RPT_TEST
db.rpt.validation.file=validation-rpt.csv
validation.mode=VALIDATE
validation.output-directory=output/validate-no-baseline
validation.itsview.enabled=false
validation.baseline.compare.enabled=false
# FOS validates mapped views; RPT validates tables directly.
db.fos.validation.itsview.enabled=true
db.fos.validation.itsview.schema=DB2ADMIN
# Optional per-database catalog overrides, tested with these fixture views.
db.fos.catalog.table.view=DB2ADMIN.DVTEST_E40A284FA5_CAT_OBJECTS
db.fos.catalog.column.view=DB2ADMIN.DVTEST_E40A284FA5_CAT_COLUMNS
```

在项目根目录 PowerShell 运行：

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
$env:DB2_PASSWORD='123456'
java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb-multi/application-baseline.properties
$LASTEXITCODE
java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb-multi/application-non-baseline.properties
$LASTEXITCODE
```

2026-09-13 已实际重跑上述两份配置，也重跑了开启 compare 的配置。当前数据已包含刻意插入的溢出，三个模式均为 fos 2 PASS / 3 FAIL、rpt 4 PASS，查询错误 0，总退出码 1，所有报告成功生成。BASELINE 和不读取基线的 VALIDATE，Compare 均为 N/A；开启比较时共 3 PASS / 2 DIFFERENT / 4 N/A。

这次 BASELINE 使用的是变更后的数据，因此与最初变更前 BASELINE 的 9 PASS 结果不同。两个固定变更前 baseline 文件未被覆盖。

===== END FILE =====

===== FILE: config/datatypevalidation/local-testdb-multi/application-baseline-env.properties =====
UTF8-BYTES: 1108
SHA256: 2c8c8f67f57903861c0f03ba0b316ef3dfb577b5b544a0dc9e9f68326f68a1c6
===== CONTENT =====
# Password source: DB2_PASSWORD environment variable; usernames are configured below.
# Two independent connection aliases using the same local physical TESTDB.
db.names=fos,rpt
db.fos.url=jdbc:db2://localhost:25000/TESTDB
db.fos.username=db2admin
db.fos.password-env=DB2_PASSWORD
db.rpt.url=jdbc:db2://localhost:25000/TESTDB
db.rpt.username=db2admin
db.rpt.password-env=DB2_PASSWORD
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
db.fos.validation.database-id=FOS_TEST
db.fos.validation.file=validation-fos.csv
db.rpt.validation.database-id=RPT_TEST
db.rpt.validation.file=validation-rpt.csv
validation.mode=BASELINE
validation.output-directory=output/baseline
validation.itsview.enabled=false
validation.baseline.compare.enabled=false

# Connection/socket limits and periodic waiting logs (seconds).
db.connect-timeout-seconds=30
db.read-timeout-seconds=360
db.query-timeout-seconds=300
db.progress-interval-seconds=10

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/datatypevalidation/local-testdb-multi/application-baseline.properties =====
UTF8-BYTES: 1086
SHA256: 3ca5118110002b07a0b13c1c1a33f89a66b1bffc5372b2594fc864217cbab8b7
===== CONTENT =====
# Password source: explicit values in this file; no environment variables required.
# Two independent connection aliases using the same local physical TESTDB.
db.names=fos,rpt
db.fos.url=jdbc:db2://localhost:25000/TESTDB
db.fos.username=db2admin
db.fos.password=123456
db.rpt.url=jdbc:db2://localhost:25000/TESTDB
db.rpt.username=db2admin
db.rpt.password=123456
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
db.fos.validation.database-id=FOS_TEST
db.fos.validation.file=validation-fos.csv
db.rpt.validation.database-id=RPT_TEST
db.rpt.validation.file=validation-rpt.csv
validation.mode=BASELINE
validation.output-directory=output/baseline
validation.itsview.enabled=false
validation.baseline.compare.enabled=false

# Connection/socket limits and periodic waiting logs (seconds).
db.connect-timeout-seconds=30
db.read-timeout-seconds=360
db.query-timeout-seconds=300
db.progress-interval-seconds=10

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/datatypevalidation/local-testdb-multi/application-current-env.properties =====
UTF8-BYTES: 1651
SHA256: bb71d6d96fdfcc5bde887714f2d02e4945e57a0371c027cc44bf4f6fe62f103a
===== CONTENT =====
# Password source: DB2_PASSWORD environment variable; usernames are configured below.
# CURRENT phase: compare against the explicitly saved pre-change baselines.
# Two independent connection aliases using the same local physical TESTDB.
db.names=fos,rpt
db.fos.url=jdbc:db2://localhost:25000/TESTDB
db.fos.username=db2admin
db.fos.password-env=DB2_PASSWORD
db.rpt.url=jdbc:db2://localhost:25000/TESTDB
db.rpt.username=db2admin
db.rpt.password-env=DB2_PASSWORD
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
db.fos.validation.database-id=FOS_TEST
db.fos.validation.file=validation-fos.csv
db.rpt.validation.database-id=RPT_TEST
db.rpt.validation.file=validation-rpt.csv
validation.mode=VALIDATE
validation.output-directory=output/current
validation.itsview.enabled=false
validation.baseline.compare.enabled=true
db.fos.validation.baseline.file=baseline/fos-reference.xlsx
db.rpt.validation.baseline.file=baseline/rpt-reference.xlsx
# FOS validates mapped views; RPT validates tables directly.
db.fos.validation.itsview.enabled=true
db.fos.validation.itsview.schema=DB2ADMIN
# Optional per-database catalog overrides, tested with these fixture views.
db.fos.catalog.table.view=DB2ADMIN.DVTEST_E40A284FA5_CAT_OBJECTS
db.fos.catalog.column.view=DB2ADMIN.DVTEST_E40A284FA5_CAT_COLUMNS

# Connection/socket limits and periodic waiting logs (seconds).
db.connect-timeout-seconds=30
db.read-timeout-seconds=360
db.query-timeout-seconds=300
db.progress-interval-seconds=10

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/datatypevalidation/local-testdb-multi/application-current.properties =====
UTF8-BYTES: 1629
SHA256: 747318d668b99f83478dd7e0a4033ed6ebc1b231bfa4d5fb81b168714b52381b
===== CONTENT =====
# Password source: explicit values in this file; no environment variables required.
# CURRENT phase: compare against the explicitly saved pre-change baselines.
# Two independent connection aliases using the same local physical TESTDB.
db.names=fos,rpt
db.fos.url=jdbc:db2://localhost:25000/TESTDB
db.fos.username=db2admin
db.fos.password=123456
db.rpt.url=jdbc:db2://localhost:25000/TESTDB
db.rpt.username=db2admin
db.rpt.password=123456
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
db.fos.validation.database-id=FOS_TEST
db.fos.validation.file=validation-fos.csv
db.rpt.validation.database-id=RPT_TEST
db.rpt.validation.file=validation-rpt.csv
validation.mode=VALIDATE
validation.output-directory=output/current
validation.itsview.enabled=false
validation.baseline.compare.enabled=true
db.fos.validation.baseline.file=baseline/fos-reference.xlsx
db.rpt.validation.baseline.file=baseline/rpt-reference.xlsx
# FOS validates mapped views; RPT validates tables directly.
db.fos.validation.itsview.enabled=true
db.fos.validation.itsview.schema=DB2ADMIN
# Optional per-database catalog overrides, tested with these fixture views.
db.fos.catalog.table.view=DB2ADMIN.DVTEST_E40A284FA5_CAT_OBJECTS
db.fos.catalog.column.view=DB2ADMIN.DVTEST_E40A284FA5_CAT_COLUMNS

# Connection/socket limits and periodic waiting logs (seconds).
db.connect-timeout-seconds=30
db.read-timeout-seconds=360
db.query-timeout-seconds=300
db.progress-interval-seconds=10

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/datatypevalidation/local-testdb-multi/application-non-baseline.properties =====
UTF8-BYTES: 1258
SHA256: a7b208e7eec1180258c30e4ee29f6ea69c208a1b1dba282dd229e4c54cb47a45
===== CONTENT =====
# Two independent connection aliases using the same local physical TESTDB.
db.names=fos,rpt
db.fos.url=jdbc:db2://localhost:25000/TESTDB
db.fos.username=db2admin
db.fos.password-env=DB2_PASSWORD
db.rpt.url=jdbc:db2://localhost:25000/TESTDB
db.rpt.username=db2admin
db.rpt.password-env=DB2_PASSWORD
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
db.fos.validation.database-id=FOS_TEST
db.fos.validation.file=validation-fos.csv
db.rpt.validation.database-id=RPT_TEST
db.rpt.validation.file=validation-rpt.csv
validation.mode=VALIDATE
validation.output-directory=output/validate-no-baseline
validation.itsview.enabled=false
validation.baseline.compare.enabled=false
# FOS validates mapped views; RPT validates tables directly.
db.fos.validation.itsview.enabled=true
db.fos.validation.itsview.schema=DB2ADMIN
# Optional per-database catalog overrides, tested with these fixture views.
db.fos.catalog.table.view=DB2ADMIN.DVTEST_E40A284FA5_CAT_OBJECTS
db.fos.catalog.column.view=DB2ADMIN.DVTEST_E40A284FA5_CAT_COLUMNS

db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/datatypevalidation/local-testdb-multi/application-validate.properties =====
UTF8-BYTES: 1365
SHA256: 7f3a358a2130701a3339b5ee8851882629892ba978fa84dd588296c1ca4ef4f2
===== CONTENT =====
# Two independent connection aliases using the same local physical TESTDB.
db.names=fos,rpt
db.fos.url=jdbc:db2://localhost:25000/TESTDB
db.fos.username=db2admin
db.fos.password-env=DB2_PASSWORD
db.rpt.url=jdbc:db2://localhost:25000/TESTDB
db.rpt.username=db2admin
db.rpt.password-env=DB2_PASSWORD
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
db.fos.validation.database-id=FOS_TEST
db.fos.validation.file=validation-fos.csv
db.rpt.validation.database-id=RPT_TEST
db.rpt.validation.file=validation-rpt.csv
validation.mode=VALIDATE
validation.output-directory=output/validate
validation.itsview.enabled=false
validation.baseline.compare.enabled=true
db.fos.validation.baseline.file=baseline/fos-reference.xlsx
db.rpt.validation.baseline.file=baseline/rpt-reference.xlsx
# FOS validates mapped views; RPT validates tables directly.
db.fos.validation.itsview.enabled=true
db.fos.validation.itsview.schema=DB2ADMIN
# Optional per-database catalog overrides, tested with these fixture views.
db.fos.catalog.table.view=DB2ADMIN.DVTEST_E40A284FA5_CAT_OBJECTS
db.fos.catalog.column.view=DB2ADMIN.DVTEST_E40A284FA5_CAT_COLUMNS

db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/datatypevalidation/local-testdb-multi/validation-fos.csv =====
UTF8-BYTES: 375
SHA256: af0f8899adffc2490a1f4c6c026e8c5e97677de23fb3f0ae639b00376c43637d
===== CONTENT =====
table_name,field_name,original_data_type,after_data_type
DB2ADMIN.DVTEST_E40A284FA5_CASES,N,"DECIMAL(5,0)","DECIMAL(20,0)"
DB2ADMIN.DVTEST_E40A284FA5_CASES,S,VARCHAR(10),VARCHAR(50)
DB2ADMIN.DVTEST_E40A284FA5_CASES,C,CLOB(100),CLOB(200)
DB2ADMIN.DVTEST_E40A284FA5_CASES,MIX,VARCHAR(10),VARCHAR(50)
DB2ADMIN.DVTEST_E40A284FA5_CASES,BIG_N,"DECIMAL(31,0)","DECIMAL(31,0)"

===== END FILE =====

===== FILE: config/datatypevalidation/local-testdb-multi/validation-rpt.csv =====
UTF8-BYTES: 330
SHA256: e88a256d2dd3b15152f9c133895ec43c970acf3eabefc0343ffcdd8441c64f33
===== CONTENT =====
table_name,field_name,original_data_type,after_data_type
DB2ADMIN.DVTEST_E40A284FA5_NULL_ROWS,V,VARCHAR(20),VARCHAR(30)
DB2ADMIN.DVTEST_E40A284FA5_NULL_ROWS,N,"DECIMAL(5,0)","DECIMAL(20,0)"
DB2ADMIN.DVTEST_E40A284FA5_EMPTY_ROWS,V,VARCHAR(20),VARCHAR(30)
DB2ADMIN.DVTEST_E40A284FA5_EMPTY_ROWS,N,"DECIMAL(5,0)","DECIMAL(20,0)"

===== END FILE =====

===== FILE: config/datatypevalidation/local-testdb/README.md =====
UTF8-BYTES: 4710
SHA256: 3c82f4c36ec1e60c34bcc23d38258af8a7e3b23c0bb681cb5941ffac0d189537
===== CONTENT =====
# 可直接运行的本机 validation 示例

Run commands in Windows PowerShell after building the JAR; replace the absolute checkout path if yours differs. Properties, YAML, SQL and CSV blocks are file contents, not terminal commands. Local examples require the retained TESTDB objects.

## 本机 TESTDB：两份可直接运行的配置（已实测）

这两份配置只运行 validation。连接 `localhost:25000/TESTDB`，账号 `db2admin`；对象使用本次已创建并保留的 `DB2ADMIN.DVTEST_3AD0121876_*`。CSV 列名与视图列名相同。BASELINE 读取表；VALIDATE 读取 DB2ADMIN 下的映射视图，例如 `DB2ADMIN.DB2ADMIN_DVTEST_3AD0121876_CASES`。

在项目根目录 PowerShell 执行一次密码设置：

```powershell
cd C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit
$env:DB2_PASSWORD='123456'
```

### 1. 生成 baseline

文件：`config/datatypevalidation/local-testdb/application-baseline.properties`

```properties
db.url=jdbc:db2://localhost:25000/TESTDB
db.username=db2admin
db.password-env=DB2_PASSWORD
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.database-id=LOCAL_TESTDB_DEMO
validation.file=validation.csv
validation.itsview.schema=DB2ADMIN
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
validation.mode=BASELINE
validation.output-directory=output/baseline
validation.itsview.enabled=false
validation.baseline.compare.enabled=false
```

直接运行：

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
$env:DB2_PASSWORD='123456'
java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb/application-baseline.properties
$LASTEXITCODE
```

报告写入 `config/datatypevalidation/local-testdb/output/baseline/run-*/default-datatype-validation.xlsx`，本次实际报告路径会打印在日志中。

### 2. 执行 validation，并与指定 baseline 比较

文件：`config/datatypevalidation/local-testdb/application-validate.properties`

```properties
db.url=jdbc:db2://localhost:25000/TESTDB
db.username=db2admin
db.password-env=DB2_PASSWORD
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.database-id=LOCAL_TESTDB_DEMO
validation.file=validation.csv
validation.itsview.schema=DB2ADMIN
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
validation.mode=VALIDATE
validation.output-directory=output/validate
validation.itsview.enabled=true
validation.baseline.compare.enabled=true
validation.baseline.file=baseline/reference.xlsx
```

直接运行：

```powershell
cd 'C:\Users\sugongqing\Documents\ChatGPT\db2-data-toolkit'
$env:DB2_PASSWORD='123456'
java -jar target/db2-data-toolkit.jar config/datatypevalidation/local-testdb/application-validate.properties
$LASTEXITCODE
```

报告写入 `config/datatypevalidation/local-testdb/output/validate/run-*/default-datatype-validation.xlsx`。配置中的路径相对于 properties 所在目录。

### 本次执行顺序与结果

先调用独立 `Db2TestDataMain prepare` 创建初始数据，再运行第一份配置（9 个 Range PASS，退出码 0）。将此次明确选定的报告复制为 `config/datatypevalidation/local-testdb/baseline/reference.xlsx`，然后调用 `Db2TestDataMain change` 扩容并追加异常数据，最后运行第二份配置。

第二份配置的实际结果：9 个字段，6 个 Range PASS、3 个 Range FAIL；Compare 为 3 个 PASS、2 个 DIFFERENT、4 个 N/A；查询错误 0，退出码 1 符合预期。

- CASES.N：1 条数值超出原 DECIMAL(5,0) 范围。
- CASES.C / MIX：各 1 条长度超出原上限，长度集合与 baseline 不同。
- CASES.S：原有 3 行、现在 4 行，非 NULL 长度集合均只有 5，报告包含 `SINGLE LENGTH MATCH (LIKELY OK)`。
- NULL_ROWS / EMPTY_ROWS：正确区分全 NULL 与零行。

**当前数据库已经处于变更后状态。** 现在重跑第一份配置，会得到变更后的新 baseline（包含 3 个 Range FAIL），不会自动覆盖固定的 `baseline/reference.xlsx`。第二份配置始终使用本次变更前已保存的 reference，因此可直接重复验证以上差异。要建立新的正式基线，应在数据变更前生成报告，并明确选定该次报告更新 reference；不要自动取“最新文件”。

本次报告：

- 变更前 baseline：`config/datatypevalidation/local-testdb/baseline/reference.xlsx`
- validation：`config/datatypevalidation/local-testdb/output/validate/run-1091289361958874347/datatype-validation.xlsx`
- 建表/插入记录与可选清理脚本：`verification/generated-data/documented-example-20260913/`


===== END FILE =====

===== FILE: config/datatypevalidation/local-testdb/application-baseline.properties =====
UTF8-BYTES: 635
SHA256: 6be1b238e6b5ff19bee86d9f17e7cbc6f2aec2a6b7c59f25b82c5085dde4e949
===== CONTENT =====
db.url=jdbc:db2://localhost:25000/TESTDB
db.username=db2admin
db.password-env=DB2_PASSWORD
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.database-id=LOCAL_TESTDB_DEMO
validation.file=validation.csv
validation.itsview.schema=DB2ADMIN
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
validation.mode=BASELINE
validation.output-directory=output/baseline
validation.itsview.enabled=false
validation.baseline.compare.enabled=false

db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/datatypevalidation/local-testdb/application-validate.properties =====
UTF8-BYTES: 682
SHA256: e1f28d5c214d7b47e9f209ea25703b707b6bc2cf85b3766bd449fc57db1f38d0
===== CONTENT =====
db.url=jdbc:db2://localhost:25000/TESTDB
db.username=db2admin
db.password-env=DB2_PASSWORD
export.enabled=false
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS
validation.enabled=true
validation.database-id=LOCAL_TESTDB_DEMO
validation.file=validation.csv
validation.itsview.schema=DB2ADMIN
validation.length-unit=OCTETS
validation.query-timeout-seconds=300
validation.mode=VALIDATE
validation.output-directory=output/validate
validation.itsview.enabled=true
validation.baseline.compare.enabled=true
validation.baseline.file=baseline/reference.xlsx

db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/datatypevalidation/local-testdb/validation.csv =====
UTF8-BYTES: 647
SHA256: 0380f3c44106e658213f827fa887bf01a4bf5179db71059510b5365bf37a1f2e
===== CONTENT =====
table_name,field_name,original_data_type,after_data_type
DB2ADMIN.DVTEST_3AD0121876_CASES,N,"DECIMAL(5,0)","DECIMAL(20,0)"
DB2ADMIN.DVTEST_3AD0121876_CASES,S,VARCHAR(10),VARCHAR(50)
DB2ADMIN.DVTEST_3AD0121876_CASES,C,CLOB(100),CLOB(200)
DB2ADMIN.DVTEST_3AD0121876_CASES,MIX,VARCHAR(10),VARCHAR(50)
DB2ADMIN.DVTEST_3AD0121876_CASES,BIG_N,"DECIMAL(31,0)","DECIMAL(31,0)"
DB2ADMIN.DVTEST_3AD0121876_NULL_ROWS,V,VARCHAR(20),VARCHAR(30)
DB2ADMIN.DVTEST_3AD0121876_NULL_ROWS,N,"DECIMAL(5,0)","DECIMAL(20,0)"
DB2ADMIN.DVTEST_3AD0121876_EMPTY_ROWS,V,VARCHAR(20),VARCHAR(30)
DB2ADMIN.DVTEST_3AD0121876_EMPTY_ROWS,N,"DECIMAL(5,0)","DECIMAL(20,0)"

===== END FILE =====

===== FILE: config/datatypevalidation/sp-list.txt =====
UTF8-BYTES: 72
SHA256: 84e77329c60e8a340cba83d5b81240282a5bd9e6c9eb2e5508a582c79bf5584d
===== CONTENT =====
# Replace with actual schema.procedure names, or leave this list empty.

===== END FILE =====

===== FILE: config/datatypevalidation/table-list.txt =====
UTF8-BYTES: 15
SHA256: a74d618b05f8d6a266718e978aeb6dfba531779c6a6846467fbb2db031fd3fa7
===== CONTENT =====
FOS.SHARE_DEAL

===== END FILE =====

===== FILE: config/ddlexport/application-ddl.properties =====
UTF8-BYTES: 437
SHA256: f076e28609304fd1959919dca6444deae0e704893a29bb59e86a60422a75f1eb
===== CONTENT =====
# Ready-to-run example for the supplied local TESTDB.
db.url=jdbc:db2://localhost:25000/TESTDB
db.username=db2admin
db.password=123456

export.enabled=true
export.procedure-list=./sp-list.txt
export.function-list=./function-list.txt
export.table-list=./table-list.txt
export.output-directory=../../output/local-example/ddl


db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/ddlexport/application-multi-ddl-env.properties =====
UTF8-BYTES: 950
SHA256: b226e503136373f6eac031f9dfca37558fe83587bee827e7c798fe92ec55a50c
===== CONTENT =====
# Password source: DB2_PASSWORD environment variable; usernames are configured below.
# Two independent aliases pointing to the supplied local TESTDB.
# Change each alias URL/username/password-env to use different physical databases.
db.names=local_a,local_b
db.local_a.url=jdbc:db2://localhost:25000/TESTDB
db.local_a.username=db2admin
db.local_a.password-env=DB2_PASSWORD
db.local_b.url=jdbc:db2://localhost:25000/TESTDB
db.local_b.username=db2admin
db.local_b.password-env=DB2_PASSWORD
validation.enabled=false
export.enabled=true
export.procedure-list=./sp-list.txt
export.function-list=./function-list.txt
export.table-list=./table-list.txt
export.output-directory=../../output/local-example/multi-ddl-only

# Connection/socket limits and periodic waiting logs (seconds).
db.connect-timeout-seconds=30
db.read-timeout-seconds=360
db.query-timeout-seconds=300
db.progress-interval-seconds=10

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/ddlexport/application-multi-ddl.properties =====
UTF8-BYTES: 924
SHA256: 0493281f04339cbd1cf209265df198f4b9d08132f81bcafce6a2be4e70993b19
===== CONTENT =====
# Password source: explicit values in this file; no environment variables required.
# Two independent aliases pointing to the supplied local TESTDB.
# Change each alias URL/username/password to use different physical databases.
db.names=local_a,local_b
db.local_a.url=jdbc:db2://localhost:25000/TESTDB
db.local_a.username=db2admin
db.local_a.password=123456
db.local_b.url=jdbc:db2://localhost:25000/TESTDB
db.local_b.username=db2admin
db.local_b.password=123456
validation.enabled=false
export.enabled=true
export.procedure-list=./sp-list.txt
export.function-list=./function-list.txt
export.table-list=./table-list.txt
export.output-directory=../../output/local-example/multi-ddl-only

# Connection/socket limits and periodic waiting logs (seconds).
db.connect-timeout-seconds=30
db.read-timeout-seconds=360
db.query-timeout-seconds=300
db.progress-interval-seconds=10

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/ddlexport/application-multi-template.properties =====
UTF8-BYTES: 831
SHA256: 199f510ace34ca5c586828eae5c44b9ff094e4a02f8e4aefc1689751b7915fc2
===== CONTENT =====
# Template: replace connection credentials and object lists before running.
# The order of these names is the export order. All targets share the three lists below.
db.names=dev,uat

db.dev.url=jdbc:db2://localhost:25000/TESTDB
db.dev.username=db2admin
db.dev.password=your_dev_password

db.uat.url=jdbc:db2://localhost:25001/TESTDB
db.uat.username=db2admin
db.uat.password=your_uat_password

export.procedure-list=./sp-list-template.txt
export.function-list=./function-list-template.txt
export.table-list=./table-list-template.txt
export.output-directory=../../output

catalog.procedure.view=SYSCAT.PROCEDURES
catalog.function.view=SYSCAT.FUNCTIONS
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS

db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/ddlexport/application-template.properties =====
UTF8-BYTES: 602
SHA256: b7d233db759e184abc64bc10830947539ce6e57d1e5d965906a9245c9917f9ea
===== CONTENT =====
# Template: replace connection credentials and object lists before running.
db.url=jdbc:db2://localhost:50000/SAMPLE
db.username=your_user
db.password=your_password
export.procedure-list=./sp-list-template.txt
export.function-list=./function-list-template.txt
export.table-list=./table-list-template.txt
export.output-directory=../../output
catalog.procedure.view=SYSCAT.PROCEDURES
catalog.function.view=SYSCAT.FUNCTIONS
catalog.table.view=SYSCAT.TABLES
catalog.column.view=SYSCAT.COLUMNS

db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/ddlexport/function-list-template.txt =====
UTF8-BYTES: 43
SHA256: ddd78bb507652c34c0e656b7a3e00c73a978c9919d679364f8d0a7998614fdcb
===== CONTENT =====
RPT.FN_CALCULATE_AMOUNT
RPT.FN_FORMAT_DATE

===== END FILE =====

===== FILE: config/ddlexport/function-list.txt =====
UTF8-BYTES: 49
SHA256: 62d5d5397213fc01d5c59bd132ea005a41a0b366ef9cfbe2e9bba6536fb71d76
===== CONTENT =====
APPLICATION.DETERMINECUSTOMERACCESS
samson.samsn

===== END FILE =====

===== FILE: config/ddlexport/sp-list-template.txt =====
UTF8-BYTES: 60
SHA256: a3e5c7b2c8a0b7c1d3ae2ddd138d094ed5f6f3c49eeb124c10685e4824cff985
===== CONTENT =====
# Stored procedures
RPT.SP_GET_CUSTOMER
RPT.SP_UPDATE_ORDER

===== END FILE =====

===== FILE: config/ddlexport/sp-list.txt =====
UTF8-BYTES: 54
SHA256: 15df72cca49f91da7b5841384c00da51c2f57055351821d7ff3fda4777de4391
===== CONTENT =====
DB2ADMIN.PR_GEN_COMPLEX_CTE_FLOW_D
sbd.xxx
fdfsd.xxxx

===== END FILE =====

===== FILE: config/ddlexport/table-list-template.txt =====
UTF8-BYTES: 24
SHA256: 527c5bb2a3be23e04491edec6899116f3c94b05ece7ce3f8bec7249f13120ddb
===== CONTENT =====
RPT.CUSTOMER
RPT.ORDERS

===== END FILE =====

===== FILE: config/ddlexport/table-list.txt =====
UTF8-BYTES: 36
SHA256: 4b0b39359a5e2331e3f94db38dfa3b4d330d01eb8fc34e2e3c47d4106658964b
===== CONTENT =====
APPLICATION.CITIES_ARCHIVE
abc.abcd

===== END FILE =====

===== FILE: config/excelexport/application-excel.properties =====
UTF8-BYTES: 255
SHA256: e321755a5862e59d599e851c8cf4d2edef90a789b6f912cef6b5aa36eb63ac22
===== CONTENT =====
db.url=jdbc:db2://localhost:25000/TESTDB
db.username=db2admin
db.password=123456
export.enabled=false
excel-export.config=./excel-export.yaml

db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/excelexport/application-multi-excel-env.properties =====
UTF8-BYTES: 819
SHA256: 73bcdb039bb9515660ee15267ce5d007c36136a3f2bd937a93f7df26c9775799
===== CONTENT =====
# Password source: DB2_PASSWORD environment variable; usernames are configured below.
# Two independent aliases pointing to the supplied local TESTDB.
# Change each alias URL/username/password-env to use different physical databases.
db.names=local_a,local_b
db.local_a.url=jdbc:db2://localhost:25000/TESTDB
db.local_a.username=db2admin
db.local_a.password-env=DB2_PASSWORD
db.local_b.url=jdbc:db2://localhost:25000/TESTDB
db.local_b.username=db2admin
db.local_b.password-env=DB2_PASSWORD
validation.enabled=false
export.enabled=false
excel-export.config=./excel-export-multi.yaml

# Connection/socket limits and periodic waiting logs (seconds).
db.connect-timeout-seconds=30
db.read-timeout-seconds=360
db.query-timeout-seconds=300
db.progress-interval-seconds=10

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/excelexport/application-multi-excel.properties =====
UTF8-BYTES: 793
SHA256: ca85b133301a50feeaff948d84e62343163a80e60d9c6defaf4ae367ee355bb7
===== CONTENT =====
# Password source: explicit values in this file; no environment variables required.
# Two independent aliases pointing to the supplied local TESTDB.
# Change each alias URL/username/password to use different physical databases.
db.names=local_a,local_b
db.local_a.url=jdbc:db2://localhost:25000/TESTDB
db.local_a.username=db2admin
db.local_a.password=123456
db.local_b.url=jdbc:db2://localhost:25000/TESTDB
db.local_b.username=db2admin
db.local_b.password=123456
validation.enabled=false
export.enabled=false
excel-export.config=./excel-export-multi.yaml

# Connection/socket limits and periodic waiting logs (seconds).
db.connect-timeout-seconds=30
db.read-timeout-seconds=360
db.query-timeout-seconds=300
db.progress-interval-seconds=10

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/excelexport/application-template.properties =====
UTF8-BYTES: 666
SHA256: 8a93aaba16fd91d0cc65bbc6afd87302f0b63e57c2f213044a6931c669984eae
===== CONTENT =====
# Template: replace connection credentials and YAML objects before running.
# Uses the existing Main entry point and configuration-file password handling.
db.name=TESTDB
db.url=jdbc:db2://localhost:25000/TESTDB
db.username=db2admin
db.password=your_password

# Excel only. For DDL too, keep export.enabled=true and the existing export.* keys.
export.enabled=false
excel-export.config=./excel-export-template.yaml

# Multi-database mode uses the existing db.names and db.<name>.* keys instead.
# Excel files are isolated below <excel-output>/db-<name>/.

db.query-timeout-seconds=300

db.read-timeout-seconds=360

db.slow-query-seconds=60

db.cancel-grace-seconds=15

===== END FILE =====

===== FILE: config/excelexport/excel-export-multi.yaml =====
UTF8-BYTES: 676
SHA256: 55e6bb7c32bd83505ed089495299bfa7ff2b1117f0b60cc7c8ed6c7539d7d56e
===== CONTENT =====
excel-export:
  enabled: true
  output-directory: ../../output/local-example/multi/excel
  include-header: true
  default-fetch-size: 1000
  overwrite: true

  simple:
    tables:
      - SYSIBM.SYSDUMMY1

  workbooks:
    - name: reference.xlsx
      objects:
        - SYSIBM.SYSDUMMY1

  exports:
    - name: sql-alias
      type: SQL
      source: >
        SELECT CAST(1234567890123456789 AS BIGINT) AS CLIENT_ID,
               'ACTIVE' AS STATUS FROM SYSIBM.SYSDUMMY1
      workbook: report.xlsx
      sheet: DATA

    - name: cities-sample
      type: OBJECT
      source: APPLICATION.CITIES_ARCHIVE
      workbook: report.xlsx
      sheet: CITIES
      max-rows: 100

===== END FILE =====

===== FILE: config/excelexport/excel-export-template.yaml =====
UTF8-BYTES: 1428
SHA256: 84563552529d6ba9bd8c4d7b77cd5a47e94c500c257021602e02c9da3d240830
===== CONTENT =====
# Paths are relative to this YAML file.
excel-export:
  enabled: true
  output-directory: ../../output/excel
  include-header: true
  default-fetch-size: 1000
  overwrite: true

  # Simple: one object -> one workbook. TABLE and VIEW both use OBJECT.
  simple:
    tables:
      - RPT.CUSTOMER
      - RPT.ACCOUNT

  # Grouped: each object becomes one sheet in the named workbook.
  workbooks:
    - name: reference-data.xlsx
      objects: [RPT.CURRENCY, RPT.COUNTRY, RPT.REGION]
    - name: transaction-data.xlsx
      objects: [RPT.TRANSACTION, RPT.POSITION]

  # Advanced: same workbook name means the same file.
  exports:
    - name: active-customer
      type: SQL
      source: >
        SELECT CUSTOMER_ID AS CLIENT_ID, CUSTOMER_NAME, STATUS
        FROM RPT.CUSTOMER
        WHERE STATUS = 'ACTIVE'
      workbook: customer-report.xlsx
      sheet: ACTIVE_CUSTOMER
      enabled: true
      include-header: true
    - name: inactive-customer
      type: SQL
      source: >
        SELECT CUSTOMER_ID, CUSTOMER_NAME, STATUS
        FROM RPT.CUSTOMER
        WHERE STATUS = 'INACTIVE'
      workbook: customer-report.xlsx
      sheet: INACTIVE_CUSTOMER
      enabled: true
    - name: complete-account
      type: OBJECT
      source: RPT.ACCOUNT
      workbook: account-report.xlsx
      sheet: ACCOUNT
      enabled: true
      fetch-size: 2000
      max-rows: 100000

===== END FILE =====

===== FILE: config/excelexport/excel-export.yaml =====
UTF8-BYTES: 826
SHA256: f085021b46ed13491a67af523cc2234501268ddab73950bf2add1d663c3259d7
===== CONTENT =====
excel-export:
  enabled: true
  output-directory: ../../output/local-example/excel
  include-header: true
  default-fetch-size: 1000
  overwrite: true

  # Simple mode: one object, one workbook.
  simple:
    tables:
      - SYSIBM.SYSDUMMY1

  # Grouping mode: objects share a workbook.
  workbooks:
    - name: reference.xlsx
      objects:
        - SYSIBM.SYSDUMMY1

  # Advanced mode: SELECT results and table data share report.xlsx.
  exports:
    - name: sql-alias
      type: SQL
      source: >
        SELECT CAST(1234567890123456789 AS BIGINT) AS CLIENT_ID,
               'ACTIVE' AS STATUS FROM SYSIBM.SYSDUMMY1
      workbook: report.xlsx
      sheet: DATA

    - name: cities-sample
      type: OBJECT
      source: APPLICATION.CITIES_ARCHIVE
      workbook: report.xlsx
      sheet: CITIES
      max-rows: 100

===== END FILE =====

===== FILE: config/logback.xml =====
UTF8-BYTES: 309
SHA256: 30567079f215af0a457b14195538317d28a45d998df936918726852224fa3289
===== CONTENT =====
<configuration>
  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder><charset>UTF-8</charset><pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level [db=%X{database:-global}] %msg%n</pattern></encoder>
  </appender>
  <root level="INFO"><appender-ref ref="CONSOLE"/></root>
</configuration>

===== END FILE =====

===== FILE: pom.xml =====
UTF8-BYTES: 4219
SHA256: 98d1fd598dc40a1bbb110f7f2628dd4a9e2b34c1f62ea2526b3da93616ec9fca
===== CONTENT =====
<?xml version="1.0" ?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.example</groupId>
  <artifactId>db2-data-toolkit</artifactId>
  <name>DB2 Data Toolkit</name>
  <version>1.0.0</version>
  <properties>
    <maven.compiler.release>17</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>
  <dependencies>
    <dependency>
      <groupId>org.apache.poi</groupId>
      <artifactId>poi-ooxml</artifactId>
      <version>5.5.1</version>
    </dependency>
    <dependency>
      <groupId>org.yaml</groupId>
      <artifactId>snakeyaml</artifactId>
      <version>2.6</version>
    </dependency>
    <dependency>
      <groupId>org.apache.logging.log4j</groupId>
      <artifactId>log4j-to-slf4j</artifactId>
      <version>2.24.3</version>
    </dependency>
    <dependency>
      <groupId>com.ibm.db2</groupId>
      <artifactId>jcc</artifactId>
      <version>11.5.9.0</version>
    </dependency>
    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-api</artifactId>
      <version>2.0.17</version>
    </dependency>
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-classic</artifactId>
      <version>1.5.18</version>
    </dependency>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>5.12.2</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>mockito-core</artifactId>
      <version>5.17.0</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
  <build>
    <finalName>db2-data-toolkit</finalName>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-clean-plugin</artifactId>
        <version>3.4.1</version>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-resources-plugin</artifactId>
        <version>3.3.1</version>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.14.0</version>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.5.3</version>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-jar-plugin</artifactId>
        <version>3.4.2</version>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <version>3.6.0</version>
        <executions>
          <execution>
            <phase>package</phase>
            <goals>
              <goal>shade</goal>
            </goals>
            <configuration>
              <createDependencyReducedPom>false</createDependencyReducedPom>
              <filters>
                <filter>
                  <artifact>*:*</artifact>
                  <excludes>
                    <exclude>META-INF/*.SF</exclude>
                    <exclude>META-INF/*.DSA</exclude>
                    <exclude>META-INF/*.RSA</exclude>
                    <exclude>module-info.class</exclude>
                  </excludes>
                </filter>
              </filters>
              <transformers>
                <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                  <mainClass>com.example.db2toolkit.Main</mainClass>
                </transformer>
              </transformers>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/Main.java =====
UTF8-BYTES: 431
SHA256: e294fe886de2650c821b880145b5d59a0a8cb6730655571affb33ba5a4213dff
===== CONTENT =====
package com.example.db2toolkit;

import com.example.db2toolkit.application.ToolkitApplication;

/** Executable entry point; application orchestration is independently testable. */
public final class Main {
    private Main() {}

    public static void main(String[] args) {
        System.exit(run(args));
    }

    public static int run(String[] args) {
        return new ToolkitApplication().run(args);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/application/DatabaseRunLogger.java =====
UTF8-BYTES: 770
SHA256: 93199e111332d2cddbd8b4c407e5f3afb991bc30e0add9ef80a1eacd67e7859b
===== CONTENT =====
package com.example.db2toolkit.application;

import com.example.db2toolkit.jdbc.SafeDiagnostics;
import com.example.db2toolkit.output.RunLogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DatabaseRunLogger {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseRunLogger.class);

    public void logRunOutcome(
            int exitCode, long elapsedMillis, String reason, SafeDiagnostics errors) {
        if (reason != null)
            LOG.error(RunLogs.SUMMARY, "Database run issue: {}", errors.clean(reason));
        LOG.info(
                RunLogs.OUTCOME,
                "Database run finished: exitCode={} elapsedMs={}",
                exitCode,
                elapsedMillis);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/application/DatabaseRunOutcome.java =====
UTF8-BYTES: 553
SHA256: aa69d9f112253194dbb53f0e495c0858405b76546fa2ae5fc9d0b2fe35e9cd49
===== CONTENT =====
package com.example.db2toolkit.application;

/** Shared run failures, independent of which features are enabled. */
final class DatabaseRunOutcome {
    private String reason;
    private int exitCode;

    void fail(String message, boolean fatal) {
        if (reason == null) reason = message;
        else if (!reason.equals(message)) reason += " | " + message;
        exitCode = Math.max(exitCode, fatal ? 2 : 1);
    }

    String reason() {
        return reason;
    }

    int exitCode() {
        return exitCode;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/application/ToolkitApplication.java =====
UTF8-BYTES: 23484
SHA256: 5a6864b206227ced8a5eeb5e463cc500df190d244fbb9f48bac28be251b62a0a
===== CONTENT =====
package com.example.db2toolkit.application;

import com.example.db2toolkit.config.AppConfig;
import com.example.db2toolkit.config.ConfigLoader;
import com.example.db2toolkit.config.ConfigurationException;
import com.example.db2toolkit.config.DatabaseTarget;
import com.example.db2toolkit.ddl.DdlExportService;
import com.example.db2toolkit.ddl.DdlSummaryLogger;
import com.example.db2toolkit.ddl.model.*;
import com.example.db2toolkit.excel.ExcelExportService;
import com.example.db2toolkit.excel.ExcelExportSummary;
import com.example.db2toolkit.input.ObjectListReader;
import com.example.db2toolkit.jdbc.Db2ConnectionFactory;
import com.example.db2toolkit.jdbc.SafeDiagnostics;
import com.example.db2toolkit.jdbc.JdbcActivity;
import com.example.db2toolkit.jdbc.SqlAuditLog;
import com.example.db2toolkit.model.*;
import com.example.db2toolkit.output.RunLogs;
import com.example.db2toolkit.output.SqlFileWriter;
import com.example.db2toolkit.datatypevalidation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Validates shared inputs once, then runs databases concurrently with one connection per alias. */
public final class ToolkitApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ToolkitApplication.class);

    @FunctionalInterface
    public interface ConnectionOpener {
        Connection open(AppConfig config) throws SQLException;
    }

    private final Function<String, String> environment;
    private final ConnectionOpener connections;

    public ToolkitApplication() {
        this(System::getenv, new Db2ConnectionFactory()::open);
    }

    public ToolkitApplication(Function<String, String> environment, ConnectionOpener connections) {
        this.environment = environment;
        this.connections = connections;
    }

    public int run(String[] args) {
        String previousDatabase = MDC.get("database");
        MDC.put("database", "global");
        try {
            return runConfigured(args);
        } finally {
            if (previousDatabase == null) MDC.remove("database");
            else MDC.put("database", previousDatabase);
        }
    }

    private int runConfigured(String[] args) {
        if (args.length != 1) {
            LOG.error("Usage: java -jar db2-data-toolkit.jar <application.properties>");
            return 2;
        }
        List<DatabaseTarget> targets;
        try {
            targets = new ConfigLoader().loadTargets(Path.of(args[0]), environment);
        } catch (ConfigurationException e) {
            LOG.error("Configuration error: {}", e.getMessage());
            return 2;
        } catch (IOException | RuntimeException e) {
            LOG.error(
                    "Cannot read configuration ({}); check path, readability and Properties syntax",
                    e.getClass().getSimpleName());
            return 2;
        }
        SafeDiagnostics sharedErrors =
                new SafeDiagnostics(targets.stream().map(DatabaseTarget::config).toList());
        DdlExportSummary inputStatistics = new DdlExportSummary();
        List<DdlExportRequest> requests;
        List<String> inputWarnings = new ArrayList<>();
        try {
            requests =
                    readRequests(
                            targets.get(0).config(), inputStatistics, sharedErrors, inputWarnings);
        } catch (IOException e) {
            LOG.error("Cannot read object list: {}", sharedErrors.clean(e.getMessage()));
            return 2;
        }

        try (var validationBatch = new DataTypeValidationBatch()) {
            // Snapshot every target's CSV/baseline before the first connection, so invalid inputs
            // for a later alias cannot leave earlier aliases with partially completed execution.
            validationBatch.prepare(targets, sharedErrors);
                var globalRanges = new java.util.TreeMap<String, Long>();
                var globalComparisons = new java.util.TreeMap<String, Long>();
                long validationItems = 0;
                long validationQueryFailures = 0;
                int[] databaseExitCounts = new int[3];
                int finalExitCode = 0;
                // Each worker owns a connection and all mutable summaries. Aggregate only after
                // completion; keep preflight snapshots alive until every worker has terminated.
                for (DatabaseOutcome result : runDatabases(targets, requests, inputStatistics,
                        sharedErrors, List.copyOf(inputWarnings), validationBatch)) {
                    var validationSummary = result.validation();
                    validationItems += validationSummary.results().size();
                    validationQueryFailures += validationSummary.queryFailures();
                    validationSummary.rangeCounts().forEach((key, count) -> globalRanges.merge(key, count, Long::sum));
                    validationSummary.compareCounts().forEach((key, count) -> globalComparisons.merge(key, count, Long::sum));
                    databaseExitCounts[result.exitCode()]++;
                    finalExitCode = Math.max(finalExitCode, result.exitCode());
                }
                LOG.info(
                        "All databases finished: databases={}, successful={}, incomplete={}, fatal={},"
                            + " exitCode={}",
                        targets.size(),
                        databaseExitCounts[0],
                        databaseExitCounts[1],
                        databaseExitCounts[2],
                        finalExitCode);
                if (validationItems > 0)
                    LOG.info(
                            "All validation results: items={} Range={} Compare={} queryFailures={}",
                            validationItems,
                            globalRanges,
                            globalComparisons,
                            validationQueryFailures);
                return finalExitCode;
        } catch (IOException | RuntimeException e) {
            LOG.error("Batch initialization/execution/cleanup failed: {}", sharedErrors.describe(e));
            return 2;
        }
    }

    private record DatabaseOutcome(int exitCode, DataTypeValidationSummary validation) {}

    private List<DatabaseOutcome> runDatabases(List<DatabaseTarget> targets,
            List<DdlExportRequest> requests, DdlExportSummary inputs, SafeDiagnostics errors,
            List<String> warnings, DataTypeValidationBatch batch) {
        if (targets.size() == 1)
            return List.of(runDatabase(targets.get(0), requests, inputs, errors, warnings, batch.get(targets.get(0).name())));
        LOG.info("Running {} databases concurrently; one connection per alias, serial work within each database", targets.size());
        var executor = java.util.concurrent.Executors.newFixedThreadPool(targets.size());
        var futures = new ArrayList<java.util.concurrent.Future<DatabaseOutcome>>();
        boolean interrupted = false;
        var results = new ArrayList<DatabaseOutcome>();
        try {
            for (var target : targets) {
                var plan = batch.get(target.name());
                futures.add(executor.submit(() -> runDatabase(target, requests, inputs, errors, warnings, plan)));
            }
            for (var future : futures) {
                while (true) {
                    try { results.add(future.get()); break; }
                    catch (InterruptedException e) { interrupted = true; }
                    catch (java.util.concurrent.ExecutionException e) {
                        LOG.error("Database worker failed: {}", errors.describe(e.getCause()));
                        results.add(new DatabaseOutcome(2, new DataTypeValidationSummary()));
                        break;
                    }
                }
            }
        } finally {
            executor.shutdown();
            // Never delete a worker's baseline snapshot while JDBC or cleanup still uses it.
            while (!executor.isTerminated()) {
                try { executor.awaitTermination(1, java.util.concurrent.TimeUnit.SECONDS); }
                catch (InterruptedException e) { interrupted = true; }
            }
            if (interrupted) Thread.currentThread().interrupt();
        }
        if (interrupted) throw new IllegalStateException("Interrupted while waiting for database workers; all workers have completed");
        return results;
    }

    private DatabaseOutcome runDatabase(DatabaseTarget target, List<DdlExportRequest> requests,
            DdlExportSummary inputs, SafeDiagnostics errors, List<String> warnings,
            DataTypeValidationPlan plan) {
        var previous = MDC.getCopyOfContextMap();
        MDC.put("database", target.name());
        try {
            var report = new DdlExportSummary();
            report.setRequestCount(requests.size());
            report.addListStatistics(inputs.invalidLines(), inputs.duplicates());
            var validation = new DataTypeValidationSummary();
            int code = execute(target, requests, report, errors, warnings, plan, validation);
            return new DatabaseOutcome(code, validation);
        } finally {
            if (previous == null) MDC.clear(); else MDC.setContextMap(previous);
        }
    }

    private List<DdlExportRequest> readRequests(
            AppConfig config,
            DdlExportSummary report,
            SafeDiagnostics errors,
            List<String> inputWarnings)
            throws IOException {
        if (!config.ddl().enabled()) return List.of();
        List<DdlExportRequest> requests = new ArrayList<>();
        ObjectListReader reader = new ObjectListReader();
        // Shared lists are read once and validated before any database is connected.
        for (DdlObjectType type : DdlObjectType.values()) {
            var list = reader.read(config.ddl().lists().get(type));
            report.addListStatistics(list.issues().size(), list.duplicates());
            LOG.info(
                    "{} unique={}, invalid={}, duplicates={}",
                    type,
                    list.objects().size(),
                    list.issues().size(),
                    list.duplicates());
            for (var issue : list.issues()) {
                String warning =
                        errors.clean(
                                "Invalid list line: "
                                        + issue.file()
                                        + ":"
                                        + issue.line()
                                        + " "
                                        + issue.reason());
                inputWarnings.add(warning);
                LOG.warn("{}", warning);
            }
            for (DbObjectRef ref : list.objects()) requests.add(new DdlExportRequest(type, ref));
        }
        report.setRequestCount(requests.size());
        return List.copyOf(requests);
    }

    private int execute(
            DatabaseTarget target,
            List<DdlExportRequest> requests,
            DdlExportSummary report,
            SafeDiagnostics errors,
            List<String> inputWarnings,
            DataTypeValidationPlan plan,
            DataTypeValidationSummary validationSummary) {
        long startedAt = System.nanoTime();
        DatabaseRunOutcome outcome = new DatabaseRunOutcome();
        AppConfig config = target.config();
        if (!config.ddl().enabled()
                && !config.excel().enabled()
                && !config.validation().enabled()) {
            LOG.info("No enabled features: DDL, Excel and validation disabled");
            return 0;
        }
        if (plan != null)
            validationSummary.results(plan.initialResults("Not executed: initialization pending"));
        SqlFileWriter writer = null;
        Path runDirectory;
        ExcelExportSummary excelSummary = new ExcelExportSummary();
        try {
            if (config.ddl().enabled()) {
                writer = new SqlFileWriter(config.ddl().output());
                runDirectory = writer.runDirectory();
            } else {
                Path root =
                        config.excel().enabled()
                                ? config.excel().output()
                                : config.validation().output();
                Files.createDirectories(root);
                runDirectory = Files.createTempDirectory(root, "run-");
            }
        } catch (IOException | RuntimeException e) {
            outcome.fail("Cannot create output directory: " + errors.clean(e.getMessage()), true);
            if (config.ddl().enabled()) report.stop(outcome.reason(), requests, true);
            if (config.ddl().enabled()) new DdlSummaryLogger().log(report, errors);
            if (config.excel().enabled()) {
                excelSummary.unavailable(config.excel(), "Output initialization failed");
                excelSummary.log(errors);
            }
            if (plan != null) {
                validationSummary.fatal("Output/log directory initialization failed");
                validationSummary.log(errors);
            }
            new DatabaseRunLogger()
                    .logRunOutcome(
                            2,
                            (System.nanoTime() - startedAt) / 1_000_000,
                            outcome.reason(),
                            errors);
            return 2;
        }
        boolean started = false;
        // Keep logs open until all feature summaries and the final outcome have been emitted.
        try (RunLogs logs = new RunLogs(runDirectory, target.name(), errors);
                var sqlLog = new SqlAuditLog(runDirectory, target.name(), errors);
                var progress = JdbcActivity.context(config.connection(), errors)) {
            started = true;
            LOG.info("SQL audit log: {}", errors.clean(sqlLog.path().toString()));
            LOG.info("Starting database run: requests={}", requests.size());
            LOG.info("Run directory: {}", errors.clean(runDirectory.toString()));
            new DdlSummaryLogger().logInputWarnings(inputWarnings);
            LOG.info(
                    "Export modes: ddlEnabled={} excelEnabled={} excelTasks={}",
                    config.ddl().enabled(),
                    config.excel().enabled(),
                    config.excel().tasks().size());
            if (plan != null)
                LOG.info(
                        "Validation enabled: mode={} databaseId={}",
                        config.validation().mode(),
                        errors.clean(config.validation().databaseId()));
            if (config.excel().enabled())
                LOG.info(
                        "Excel output directory: {} overwrite={}",
                        errors.clean(config.excel().output().toString()),
                        config.excel().overwrite());
            if (!requests.isEmpty()
                    || config.excel().hasEnabledTasks()
                    || config.validation().enabled())
                executeConnectedFeatures(
                        config,
                        requests,
                        writer,
                        report,
                        excelSummary,
                        errors,
                        plan,
                        validationSummary,
                        outcome,
                        target.name(),
                        !config.ddl().enabled() && !config.excel().enabled() ? runDirectory : null);
            else {
                LOG.info("No requests; database connection skipped");
                if (config.excel().enabled()) excelSummary.unavailable(config.excel(), "Disabled");
            }
            if (config.ddl().enabled()) new DdlSummaryLogger().log(report, errors);
            if (config.excel().enabled()) excelSummary.log(errors);
            if (plan != null) {
                try {
                    plan.close();
                } catch (IOException e) {
                    validationSummary.fatal(errors.describe(e));
                }
                validationSummary.log(errors);
            }
            // Logback defers appender failures. Report known failures before final status is
            // written,
            // while healthy log files are still open; close-only failures still fall back to the
            // console.
            IOException logFailure = logs.writeFailure();
            if (logFailure != null)
                outcome.fail(
                        "Cannot write run logs: " + errors.clean(logFailure.getMessage()), true);
            IOException sqlLogFailure = SqlAuditLog.writeFailure();
            if (sqlLogFailure != null)
                outcome.fail("Cannot write SQL audit log: " + errors.describe(sqlLogFailure), true);
            int exitCode =
                    Math.max(
                            outcome.exitCode(),
                            Math.max(
                                    Math.max(report.exitCode(), excelSummary.exitCode()),
                                    validationSummary.exitCode()));
            new DatabaseRunLogger()
                    .logRunOutcome(
                            exitCode,
                            (System.nanoTime() - startedAt) / 1_000_000,
                            outcome.reason(),
                            errors);
            // The outcome itself may trigger the first write failure. Correct the last status in
            // healthy sinks before closing; a newly observed failure cannot change severity again.
            if (logFailure == null && logs.writeFailure() != null) {
                outcome.fail(
                        "Cannot write run logs: " + errors.clean(logs.writeFailure().getMessage()),
                        true);
                exitCode = 2;
                new DatabaseRunLogger()
                        .logRunOutcome(
                                exitCode,
                                (System.nanoTime() - startedAt) / 1_000_000,
                                outcome.reason(),
                                errors);
            }
            return exitCode;
        } catch (IOException | RuntimeException e) {
            String category =
                    e instanceof IOException
                            ? "Cannot write run logs: "
                            : "Database run aborted (" + e.getClass().getSimpleName() + "): ";
            outcome.fail(category + errors.clean(e.getMessage()), true);
            if (config.ddl().enabled())
                report.stop(outcome.reason(), started ? List.of() : requests, true);
            if (config.ddl().enabled()) new DdlSummaryLogger().log(report, errors);
            new DatabaseRunLogger()
                    .logRunOutcome(
                            2,
                            (System.nanoTime() - startedAt) / 1_000_000,
                            outcome.reason(),
                            errors);
            return 2;
        }
    }

    private void executeConnectedFeatures(
            AppConfig config,
            List<DdlExportRequest> requests,
            SqlFileWriter writer,
            DdlExportSummary report,
            ExcelExportSummary excelSummary,
            SafeDiagnostics errors,
            DataTypeValidationPlan plan,
            DataTypeValidationSummary validationSummary,
            DatabaseRunOutcome outcome,
            String alias,
            Path validationDirectory) {
        long connectionStarted = System.nanoTime();
        boolean opened = false;
        LOG.info("Opening database connection");
        LOG.info("JDBC limits: connectSeconds={} readSeconds={} ddlExcelQuerySeconds={} progressSeconds={} slowQuerySeconds={} cancelGraceSeconds={}",
                config.connection().connectTimeoutSeconds(), config.connection().readTimeoutSeconds(),
                config.connection().queryTimeoutSeconds(), config.connection().progressIntervalSeconds(),
                config.connection().slowQuerySeconds(), config.connection().cancelGraceSeconds());
        try (var lease = JdbcActivity.open(() -> connections.open(config), config.connection())) {
            Connection connection = lease.connection();
            opened = true;
            LOG.info(
                    "Database connection opened; reusing it for all requests; elapsedMs={}",
                    (System.nanoTime() - connectionStarted) / 1_000_000);
            boolean connectionUsable =
                    !config.ddl().enabled()
                            || new DdlExportService()
                                    .export(connection, config, requests, writer, report);
            if (config.excel().enabled()) {
                if (connectionUsable)
                    connectionUsable =
                            new ExcelExportService()
                                    .export(connection, config.excel(), errors, excelSummary);
                else
                    excelSummary.unavailable(
                            config.excel(),
                            "Not executed: database connection unavailable after DDL export");
            }
            if (plan != null)
                new DataTypeValidationService()
                        .validateAndWriteReport(
                                connection,
                                config,
                                plan,
                                alias,
                                validationDirectory,
                                connectionUsable,
                                validationSummary,
                                errors);
        } catch (SQLException e) {
            String reason =
                    (opened ? "Connection close failed: " : "Connection initialization failed: ")
                            + errors.sql(e);
            outcome.fail(reason, !opened);
            if (config.ddl().enabled()) report.stop(reason, opened ? List.of() : requests, !opened);
            if (!opened && config.excel().enabled())
                excelSummary.unavailable(config.excel(), reason);
            if (!opened && plan != null) validationSummary.fatal(reason);
            LOG.error("{}", reason);
            return;
        } catch (RuntimeException e) {
            // Handle driver/close failures inside the log scope so local summaries survive
            // unexpected exceptions.
            String reason =
                    (opened
                                    ? "Database execution/close failed ("
                                    : "Connection initialization failed (")
                            + e.getClass().getSimpleName()
                            + "): "
                            + errors.clean(e.getMessage());
            outcome.fail(reason, true);
            if (config.ddl().enabled()) report.stop(reason, opened ? List.of() : requests, true);
            if (config.excel().enabled()) excelSummary.unavailable(config.excel(), reason);
            if (plan != null) validationSummary.fatal(reason);
            LOG.error("{}", reason);
            return;
        }
        LOG.info("Database connection closed");
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/catalog/CatalogFields.java =====
UTF8-BYTES: 889
SHA256: 82bafe281889c49ec8bf668a07b849f640d11b1643804a66e349dc145d70e27d
===== CONTENT =====
package com.example.db2toolkit.catalog;

/** Fixed field contract, independent of configurable view names. */
public final class CatalogFields {
    private CatalogFields() {}

    public static final String PROC_SCHEMA = "PROCSCHEMA",
            PROC_NAME = "PROCNAME",
            PROC_DDL = "TEXT";
    public static final String FUNC_SCHEMA = "FUNCSCHEMA",
            FUNC_NAME = "FUNCNAME",
            FUNC_DDL = "BODY";
    public static final String SPECIFIC = "SPECIFICNAME";
    public static final String TABLE_FIELDS = "TYPE, TEMPORALTYPE";
    public static final String COLUMN_FIELDS =
            "COLNAME, COLNO, TYPESCHEMA, TYPENAME, LENGTH, SCALE, TYPESTRINGUNITS,"
                    + " STRINGUNITSLENGTH, CODEPAGE, NULLS, DEFAULT, IDENTITY, GENERATED, HIDDEN,"
                    + " ROWCHANGETIMESTAMP, ROWBEGIN, ROWEND, TRANSACTIONSTARTID";
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/catalog/JdbcText.java =====
UTF8-BYTES: 478
SHA256: bddbec665ba90cd77c48b9754e4be8559ce2d0f2981a3a1282de1532185ab1a9
===== CONTENT =====
package com.example.db2toolkit.catalog;

import java.io.*;
import java.sql.*;

final class JdbcText {
    private JdbcText() {}

    static String read(ResultSet rs, String field) throws SQLException, IOException {
        try (Reader reader = rs.getCharacterStream(field)) {
            if (reader == null) return null;
            StringWriter out = new StringWriter();
            reader.transferTo(out);
            return out.toString();
        }
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/catalog/RoutineDdlExtractor.java =====
UTF8-BYTES: 4553
SHA256: 7b7ce13cf3a414cd7fde6bd56c23b026125f5e93d1b2c563db50426579c2fc47
===== CONTENT =====
package com.example.db2toolkit.catalog;

import com.example.db2toolkit.ddl.model.DdlObjectType;
import com.example.db2toolkit.jdbc.JdbcActivity;
import com.example.db2toolkit.jdbc.ReadOnlyQueries;
import com.example.db2toolkit.model.DbObjectRef;

import java.io.IOException;
import java.sql.*;

/** Streams instances to the service so earlier successes survive a later cursor failure. */
public final class RoutineDdlExtractor {
    /** A known catalog instance whose definition failed after the connection became unusable. */
    public static final class DefinitionConnectionException
            extends SQLNonTransientConnectionException {
        private static final long serialVersionUID = 1L;
        private final String specificName;

        private DefinitionConnectionException(
                String specificName, Exception cause, SQLException diagnostic) {
            super(
                    "Connection lost while reading routine character stream",
                    diagnostic.getSQLState(),
                    diagnostic.getErrorCode(),
                    cause);
            this.specificName = specificName;
        }

        public String specificName() {
            return specificName;
        }
    }

    @FunctionalInterface
    public interface InstanceConsumer {
        void accept(String specific, String ddl, Exception error);
    }

    public void extract(
            Connection connection,
            String view,
            DdlObjectType type,
            DbObjectRef ref,
            InstanceConsumer consumer)
            throws SQLException {
        String schema =
                type == DdlObjectType.PROCEDURE
                        ? CatalogFields.PROC_SCHEMA
                        : CatalogFields.FUNC_SCHEMA;
        String name =
                type == DdlObjectType.PROCEDURE ? CatalogFields.PROC_NAME : CatalogFields.FUNC_NAME;
        String text =
                type == DdlObjectType.PROCEDURE ? CatalogFields.PROC_DDL : CatalogFields.FUNC_DDL;
        String sql =
                "SELECT "
                        + CatalogFields.SPECIFIC
                        + ", "
                        + text
                        + " FROM "
                        + view
                        + " WHERE "
                        + schema
                        + " = ? AND "
                        + name
                        + " = ? ORDER BY "
                        + CatalogFields.SPECIFIC;
        try (var activity = JdbcActivity.query("ROUTINE_DDL " + ref.sqlName());
                PreparedStatement ps = ReadOnlyQueries.prepare(connection, sql)) {
            JdbcActivity.configureQuery(ps);
            ReadOnlyQueries.bindString(ps, 1, ref.schema());
            ReadOnlyQueries.bindString(ps, 2, ref.name());
            try (ResultSet rs = activity.execute(ps)) {
                while (rs.next()) {
                    String specific = rs.getString(CatalogFields.SPECIFIC);
                    String ddl;
                    try {
                        ddl = JdbcText.read(rs, text);
                    } catch (SQLException e) {
                        // A failed value read need not invalidate the cursor. Connection failures
                        // still
                        // propagate to the service, which stops all remaining requests for this
                        // database.
                        if (com.example.db2toolkit.jdbc.ConnectionFailureClassifier.connectionLost(
                                connection, e))
                            throw new DefinitionConnectionException(specific, e, e);
                        consumer.accept(specific, null, e);
                        continue;
                    } catch (IOException | RuntimeException e) {
                        // Driver readers may wrap failures in unchecked exceptions. Keep the known
                        // instance identity and inspect causes/suppressed errors before continuing.
                        SQLException disconnect =
                                com.example.db2toolkit.jdbc.ConnectionFailureClassifier
                                        .connectionFailure(e);
                        if (disconnect != null)
                            throw new DefinitionConnectionException(specific, e, disconnect);
                        consumer.accept(specific, null, e);
                        continue;
                    }
                    consumer.accept(specific, ddl, null);
                }
            }
        }
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/catalog/TableDdlExtractor.java =====
UTF8-BYTES: 7040
SHA256: 24f50fbc1574095ee88446d6edff70d2040166c46e8b5f577e2f3906e672d865
===== CONTENT =====
package com.example.db2toolkit.catalog;

import com.example.db2toolkit.config.AppConfig;
import com.example.db2toolkit.jdbc.JdbcActivity;
import com.example.db2toolkit.jdbc.ReadOnlyQueries;
import com.example.db2toolkit.ddl.model.DdlUnavailableException;
import com.example.db2toolkit.model.DbObjectRef;
import com.example.db2toolkit.sql.Db2TypeRenderer;
import com.example.db2toolkit.sql.TableDdlFormatter;
import com.example.db2toolkit.sql.TableDdlFormatter.ColumnDefinition;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/** Reads only TABLES/COLUMNS metadata. Layout and type presentation live in the sql package. */
public final class TableDdlExtractor {
    private final Db2TypeRenderer types = new Db2TypeRenderer();

    /** Null means zero TABLES rows; query failures are never translated to missing objects. */
    public String extract(Connection connection, AppConfig config, DbObjectRef ref)
            throws SQLException, IOException, DdlUnavailableException {
        if (!isSupportedTable(connection, config.catalog().tableView(), ref)) return null;
        List<ColumnDefinition> columns =
                readColumns(connection, config.catalog().columnView(), ref);
        if (columns.isEmpty()) throw new DdlUnavailableException("No column metadata available");
        return "CREATE TABLE "
                + TableDdlFormatter.tableIdentifier(ref.schema())
                + "."
                + TableDdlFormatter.tableIdentifier(ref.name())
                + " (\n"
                + TableDdlFormatter.formatColumns(columns)
                + "\n)";
    }

    private boolean isSupportedTable(Connection connection, String view, DbObjectRef ref)
            throws SQLException, DdlUnavailableException {
        String sql =
                "SELECT "
                        + CatalogFields.TABLE_FIELDS
                        + " FROM "
                        + view
                        + " WHERE TABSCHEMA = ? AND TABNAME = ?";
        try (var activity = JdbcActivity.query("TABLE_METADATA " + ref.sqlName());
                PreparedStatement statement = ReadOnlyQueries.prepare(connection, sql)) {
            JdbcActivity.configureQuery(statement);
            bind(statement, ref);
            try (ResultSet rows = activity.execute(statement)) {
                if (!rows.next()) return false;
                require(rows, "TYPE", "T", "Only ordinary base tables (TYPE=T) are supported");
                require(
                        rows,
                        "TEMPORALTYPE",
                        "N",
                        "Temporal/unknown table attributes are unsupported");
                if (rows.next()) throw new DdlUnavailableException("Ambiguous table catalog rows");
                return true;
            }
        }
    }

    private List<ColumnDefinition> readColumns(Connection connection, String view, DbObjectRef ref)
            throws SQLException, IOException, DdlUnavailableException {
        String sql =
                "SELECT "
                        + CatalogFields.COLUMN_FIELDS
                        + " FROM "
                        + view
                        + " WHERE TABSCHEMA = ? AND TABNAME = ? ORDER BY COLNO";
        List<ColumnDefinition> columns = new ArrayList<>();
        Set<String> names = new HashSet<>();
        int lastColumnNumber = -1;
        try (var activity = JdbcActivity.query("TABLE_COLUMNS " + ref.sqlName());
                PreparedStatement statement = ReadOnlyQueries.prepare(connection, sql)) {
            JdbcActivity.configureQuery(statement);
            bind(statement, ref);
            try (ResultSet rows = activity.execute(statement)) {
                while (rows.next()) {
                    String name = rows.getString("COLNAME");
                    int columnNumber = requiredInt(rows, "COLNO");
                    if (columnNumber <= lastColumnNumber || name == null || !names.add(name)) {
                        throw new DdlUnavailableException("Invalid or duplicate column metadata");
                    }
                    lastColumnNumber = columnNumber;
                    columns.add(readColumn(rows, name));
                }
            }
        }
        return columns;
    }

    private ColumnDefinition readColumn(ResultSet row, String name)
            throws SQLException, IOException, DdlUnavailableException {
        validateColumnAttributes(row);
        String nullable = row.getString("NULLS");
        if (!"Y".equals(nullable) && !"N".equals(nullable))
            throw new DdlUnavailableException("Unknown nullability");

        long length = row.getLong("STRINGUNITSLENGTH");
        Long declaredLength = row.wasNull() ? null : length;
        var type =
                new Db2TypeRenderer.Type(
                        row.getString("TYPESCHEMA"),
                        row.getString("TYPENAME"),
                        requiredInt(row, "LENGTH"),
                        requiredInt(row, "SCALE"),
                        row.getString("TYPESTRINGUNITS"),
                        declaredLength,
                        requiredInt(row, "CODEPAGE"));
        String attributes = "N".equals(nullable) ? "NOT NULL" : "";
        String defaultValue = JdbcText.read(row, "DEFAULT");
        if (defaultValue != null)
            attributes += (attributes.isEmpty() ? "" : " ") + "DEFAULT " + defaultValue;
        return new ColumnDefinition(
                TableDdlFormatter.tableIdentifier(name), types.render(type), attributes);
    }

    private static void validateColumnAttributes(ResultSet row)
            throws SQLException, DdlUnavailableException {
        for (String field :
                List.of(
                        "IDENTITY",
                        "ROWCHANGETIMESTAMP",
                        "ROWBEGIN",
                        "ROWEND",
                        "TRANSACTIONSTARTID")) {
            require(row, field, "N", "Unsupported or unknown column attribute: " + field);
        }
        for (String field : List.of("GENERATED", "HIDDEN")) {
            require(row, field, "", "Unsupported or unknown column attribute: " + field);
        }
    }

    private static int requiredInt(ResultSet row, String field)
            throws SQLException, DdlUnavailableException {
        int value = row.getInt(field);
        if (row.wasNull()) throw new DdlUnavailableException("Missing column metadata: " + field);
        return value;
    }

    private static void require(ResultSet row, String field, String expected, String reason)
            throws SQLException, DdlUnavailableException {
        String value = row.getString(field);
        if (value == null || !expected.equals(value.strip()))
            throw new DdlUnavailableException(reason);
    }

    private static void bind(PreparedStatement statement, DbObjectRef ref) throws SQLException {
        ReadOnlyQueries.bindString(statement, 1, ref.schema());
        ReadOnlyQueries.bindString(statement, 2, ref.name());
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/config/AppConfig.java =====
UTF8-BYTES: 5135
SHA256: 47cd8403159058c0f81f5151d3046381e4630d693c43fb385f5b7b59df446d09
===== CONTENT =====
package com.example.db2toolkit.config;

import com.example.db2toolkit.ddl.model.DdlObjectType;
import com.example.db2toolkit.excel.ExcelExportConfig;
import com.example.db2toolkit.datatypevalidation.DataTypeValidationConfig;

import java.nio.file.Path;
import java.util.Map;

/** Deliberately not a record: never expose credentials via generated toString(). */
public final class AppConfig {
    private final ConnectionConfig connection;
    private final CatalogConfig catalog;
    private final DdlExportConfig ddl;
    private final ExcelExportConfig excel;
    private final DataTypeValidationConfig validation;

    public AppConfig(
            String url,
            String username,
            String password,
            Map<DdlObjectType, Path> lists,
            Path output,
            String procedures,
            String functions,
            String tables,
            String columns) {
        this(
                url,
                username,
                password,
                lists,
                output,
                procedures,
                functions,
                tables,
                columns,
                true,
                ExcelExportConfig.disabled());
    }

    public AppConfig(
            String url,
            String username,
            String password,
            Map<DdlObjectType, Path> lists,
            Path output,
            String procedures,
            String functions,
            String tables,
            String columns,
            boolean ddlEnabled,
            ExcelExportConfig excel) {
        this(
                url,
                username,
                password,
                lists,
                output,
                procedures,
                functions,
                tables,
                columns,
                ddlEnabled,
                excel,
                DataTypeValidationConfig.disabled());
    }

    private AppConfig(
            String url,
            String username,
            String password,
            Map<DdlObjectType, Path> lists,
            Path output,
            String procedures,
            String functions,
            String tables,
            String columns,
            boolean ddlEnabled,
            ExcelExportConfig excel,
            DataTypeValidationConfig validation) {
        this(
                new ConnectionConfig(url, username, password),
                new CatalogConfig(procedures, functions, tables, columns),
                new DdlExportConfig(ddlEnabled, lists, output),
                excel,
                validation);
    }

    public AppConfig(
            ConnectionConfig connection,
            CatalogConfig catalog,
            DdlExportConfig ddl,
            ExcelExportConfig excel,
            DataTypeValidationConfig validation) {
        this.connection = connection;
        this.catalog = catalog;
        this.ddl = ddl;
        this.excel = excel;
        this.validation = validation;
    }

    AppConfig withConnection(ConnectionConfig value) {
        return new AppConfig(value, catalog, ddl, excel, validation);
    }

    public ConnectionConfig connection() {
        return connection;
    }

    public CatalogConfig catalog() {
        return catalog;
    }

    public DdlExportConfig ddl() {
        return ddl;
    }

    public ExcelExportConfig excel() {
        return excel;
    }

    public DataTypeValidationConfig validation() {
        return validation;
    }

    AppConfig withExcel(ExcelExportConfig value) {
        return new AppConfig(connection, catalog, ddl, value, validation);
    }

    public AppConfig withValidation(DataTypeValidationConfig value) {
        return new AppConfig(connection, catalog, ddl, excel, value);
    }

    AppConfig withCatalogs(String tableView, String columnView) {
        return new AppConfig(
                connection,
                new CatalogConfig(
                        catalog.procedureView(), catalog.functionView(), tableView, columnView),
                ddl,
                excel,
                validation);
    }

    // Compatibility accessors for existing Java callers; production uses the scoped configuration.
    public String url() {
        return connection.url();
    }

    public String username() {
        return connection.username();
    }

    public String password() {
        return connection.password();
    }

    public String procedures() {
        return catalog.procedureView();
    }

    public String functions() {
        return catalog.functionView();
    }

    public String tables() {
        return catalog.tableView();
    }

    public String columns() {
        return catalog.columnView();
    }

    public Map<DdlObjectType, Path> lists() {
        return ddl.lists();
    }

    public Path output() {
        return ddl.output();
    }

    public boolean ddlEnabled() {
        return ddl.enabled();
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/config/CatalogConfig.java =====
UTF8-BYTES: 164
SHA256: 525a9ef67a533b8aeab6149edeeaf8cfc4fe8f9b69c87a2a9bbadc8c168a687f
===== CONTENT =====
package com.example.db2toolkit.config;

public record CatalogConfig(
        String procedureView, String functionView, String tableView, String columnView) {}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/config/ConfigLoader.java =====
UTF8-BYTES: 14176
SHA256: 264ed8ee7207504b567f6d1a50e1e54de129ec1a8f4ea4822225bcbbc86821fc
===== CONTENT =====
package com.example.db2toolkit.config;

import com.example.db2toolkit.ddl.model.DdlObjectType;
import com.example.db2toolkit.excel.ExcelConfigLoader;
import com.example.db2toolkit.excel.ExcelExportConfig;
import com.example.db2toolkit.jdbc.SafeDiagnostics;
import com.example.db2toolkit.model.Identifiers;
import com.example.db2toolkit.datatypevalidation.DataTypeValidationConfigLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;

/** Resolves all database targets and file-relative paths before application execution begins. */
public final class ConfigLoader {
    private static final Set<String> GLOBAL_EXPORT_AND_CATALOG_KEYS = Set.of(
            "catalog.procedure.view", "catalog.function.view", "catalog.table.view",
            "catalog.column.view", "export.enabled", "export.procedure-list",
            "export.function-list", "export.table-list", "export.output-directory",
            "excel-export.config");

    public AppConfig load(Path file, Function<String, String> environment) throws IOException {
        List<DatabaseTarget> targets = loadTargets(file, environment);
        if (targets.size() != 1)
            throw new ConfigurationException("Multiple databases configured; use loadTargets");
        return targets.get(0).config();
    }

    public List<DatabaseTarget> loadTargets(Path file, Function<String, String> environment)
            throws IOException {
        Path base = file.toAbsolutePath().normalize().getParent();
        Properties p = new Properties();
        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            // Windows editors may write a UTF-8 BOM; it is not part of the first Properties key.
            reader.mark(1);
            if (reader.read() != '\uFEFF') reader.reset();
            p.load(reader);
        }
        String ddlValue = p.getProperty("export.enabled", "true").strip();
        if (!ddlValue.equals("true") && !ddlValue.equals("false"))
            throw new ConfigurationException("export.enabled must be true or false");
        boolean ddlEnabled = Boolean.parseBoolean(ddlValue);
        boolean multiple = p.containsKey("db.names");
        List<String> names = databaseNames(p, multiple);
        var targets = new ArrayList<DatabaseTarget>();
        for (String name : names) {
            String prefix = multiple ? "db." + name + "." : "db.";
            targets.add(
                    new DatabaseTarget(
                            name,
                            targetConfig(
                                    p,
                                    base,
                                    environment,
                                    prefix,
                                    multiple ? "db-" + name : null,
                                    ddlEnabled)));
        }
        // Resolve credentials once, before Excel validation can emit user-supplied labels.
        SafeDiagnostics errors =
                new SafeDiagnostics(targets.stream().map(DatabaseTarget::config).toList());
        try {
            // Check disabled features too: a typo must not silently turn an intended export into
            // a successful no-op. Keep these checks before opening the Excel YAML file.
            checkGlobalKeys(p);
            checkJdbcKeys(p, names, multiple);
            checkCatalogKeys(p, names, multiple);
            DataTypeValidationConfigLoader.checkKeys(p, names, multiple);
            ExcelExportConfig excel =
                    p.containsKey("excel-export.config")
                            ? new ExcelConfigLoader(errors)
                                    .load(base.resolve(required(p, "excel-export.config")))
                            : ExcelExportConfig.disabled();
            List<DatabaseTarget> configured = new ArrayList<>();
            for (DatabaseTarget target : targets) {
                AppConfig config =
                        target.config()
                                .withValidation(
                                        DataTypeValidationConfigLoader.load(
                                                p, base, target.name(), multiple))
                                .withExcel(
                                        excel.forDatabase(multiple ? "db-" + target.name() : null).withDatabasePrefix(target.name()));
                if (multiple) {
                    String prefix = "db." + target.name() + ".catalog.";
                    String tables =
                            p.containsKey(prefix + "table.view")
                                    ? Identifiers.parse(required(p, prefix + "table.view"))
                                            .sqlName()
                                    : config.catalog().tableView();
                    String columns =
                            p.containsKey(prefix + "column.view")
                                    ? Identifiers.parse(required(p, prefix + "column.view"))
                                            .sqlName()
                                    : config.catalog().columnView();
                    config = config.withCatalogs(tables, columns);
                }
                String connectionPrefix = multiple ? "db." + target.name() + "." : "db.";
                var connection = config.connection();
                config = config.withConnection(new ConnectionConfig(connection.url(), connection.username(), connection.password(),
                        seconds(p, connectionPrefix, "connect-timeout-seconds", 30),
                        seconds(p, connectionPrefix, "read-timeout-seconds", 120),
                        seconds(p, connectionPrefix, "query-timeout-seconds", 0),
                        seconds(p, connectionPrefix, "progress-interval-seconds", 10),
                        seconds(p, connectionPrefix, "slow-query-seconds", 60),
                        seconds(p, connectionPrefix, "cancel-grace-seconds", 15)));
                configured.add(new DatabaseTarget(target.name(), config));
            }
            return List.copyOf(configured);
        } catch (RuntimeException e) {
            throw new ConfigurationException(errors.clean(e.getMessage()));
        }
    }

    private static void checkGlobalKeys(Properties properties) {
        for (String key : properties.stringPropertyNames()) {
            if ((key.startsWith("catalog.") || key.startsWith("export.")
                    || key.startsWith("excel-export."))
                    && !GLOBAL_EXPORT_AND_CATALOG_KEYS.contains(key))
                throw new ConfigurationException("Unknown configuration key: " + key);
        }
    }

    private static void checkJdbcKeys(Properties properties, List<String> aliases, boolean multiple) {
        Set<String> keys = Set.of("url", "username", "password", "password-env",
                "connect-timeout-seconds", "read-timeout-seconds", "query-timeout-seconds",
                "progress-interval-seconds", "slow-query-seconds", "cancel-grace-seconds");
        for (String key : properties.stringPropertyNames()) {
            if (!key.startsWith("db.")) continue;
            String suffix = key.substring(3);
            int dot = suffix.indexOf('.');
            if (dot < 0) {
                if (!keys.contains(suffix) && !suffix.equals("name") && !suffix.equals("names"))
                    throw new ConfigurationException("Unknown database connection key: " + key);
                continue;
            }
            String alias = suffix.substring(0, dot), setting = suffix.substring(dot + 1);
            // These namespaces have their own key and alias validation below.
            if (setting.startsWith("catalog.") || setting.startsWith("validation.")) continue;
            if (!multiple || !aliases.contains(alias))
                throw new ConfigurationException("Unknown database alias or single-database scoped key: " + key);
            if (!keys.contains(setting))
                throw new ConfigurationException("Unknown database connection key: " + key);
        }
    }

    private static int seconds(Properties properties, String prefix, String suffix, int fallback) {
        String value = properties.getProperty(prefix + suffix, properties.getProperty("db." + suffix, Integer.toString(fallback))).strip();
        try {
            if (!value.matches("[0-9]+")) throw new NumberFormatException();
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(prefix + suffix + " must be a nonnegative integer");
        }
    }

    private static void checkCatalogKeys(
            Properties properties, List<String> aliases, boolean multiple) {
        for (String key : properties.stringPropertyNames()) {
            if (!key.startsWith("db.")) continue;
            int end = key.indexOf('.', 3);
            if (end < 0 || !key.substring(end + 1).startsWith("catalog.")) continue;
            String alias = key.substring(3, end);
            if (!multiple || !aliases.contains(alias))
                throw new ConfigurationException(
                        "Unknown database alias or single-database scoped key: " + key);
            String suffix = key.substring(end + 1);
            if (!suffix.equals("catalog.table.view") && !suffix.equals("catalog.column.view"))
                throw new ConfigurationException("Unknown database catalog override: " + key);
        }
    }

    private static List<String> databaseNames(Properties p, boolean multiple) {
        String value =
                multiple ? required(p, "db.names") : p.getProperty("db.name", "default").strip();
        List<String> names = new ArrayList<>();
        Set<String> normalized = new HashSet<>();
        for (String part : value.split(",", -1)) {
            String name = part.strip();
            if (!name.matches("[A-Za-z][A-Za-z0-9_-]{0,39}"))
                throw new ConfigurationException(
                        "Database names must be 1-40 ASCII letters/digits/_/-, starting with a"
                            + " letter");
            if (!normalized.add(name.toLowerCase(Locale.ROOT)))
                throw new ConfigurationException(
                        "Duplicate database name (case-insensitive): " + name);
            names.add(name);
        }
        if (!multiple && names.size() != 1)
            throw new ConfigurationException("db.name must contain one name");
        return names;
    }

    private static AppConfig targetConfig(
            Properties p,
            Path base,
            Function<String, String> environment,
            String prefix,
            String outputChild,
            boolean ddlEnabled) {
        String password = password(p, prefix, environment);
        String url = required(p, prefix + "url");
        if (!url.startsWith("jdbc:db2:"))
            throw new ConfigurationException(prefix + "url must be a Db2 JDBC URL");
        var lists = new EnumMap<DdlObjectType, Path>(DdlObjectType.class);
        if (ddlEnabled)
            for (var t : DdlObjectType.values())
                lists.put(
                        t,
                        base.resolve(
                                        required(
                                                p,
                                                "export."
                                                        + t.name().toLowerCase(Locale.ROOT)
                                                        + "-list"))
                                .normalize());
        Path output =
                base.resolve(
                                ddlEnabled
                                        ? required(p, "export.output-directory")
                                        : p.getProperty("export.output-directory", "./output"))
                        .normalize();
        if (outputChild != null) output = output.resolve(outputChild);
        return new AppConfig(
                url,
                required(p, prefix + "username"),
                password,
                lists,
                output,
                catalog(p, "procedure", "PROCEDURES"),
                catalog(p, "function", "FUNCTIONS"),
                catalog(p, "table", "TABLES"),
                catalog(p, "column", "COLUMNS"),
                ddlEnabled,
                ExcelExportConfig.disabled());
    }

    private static String password(
            Properties p, String prefix, Function<String, String> environment) {
        // An explicit file value takes precedence. Never trim passwords or silently fall back on an
        // empty value.
        if (p.containsKey(prefix + "password")) {
            String value = p.getProperty(prefix + "password");
            if (value.isEmpty())
                throw new ConfigurationException("Empty configuration key: " + prefix + "password");
            return value;
        }
        String envName = required(p, prefix + "password-env");
        if (!envName.matches("[A-Za-z_][A-Za-z0-9_]*"))
            throw new ConfigurationException("Invalid " + prefix + "password-env name");
        String value = environment.apply(envName);
        if (value == null || value.isEmpty())
            throw new ConfigurationException(
                    "Missing password environment variable for " + prefix + ": " + envName);
        return value;
    }

    private static String required(Properties p, String key) {
        String value = p.getProperty(key);
        if (value == null || value.isBlank())
            throw new ConfigurationException("Missing configuration key: " + key);
        return value.strip();
    }

    private static String catalog(Properties p, String key, String defaultName) {
        try {
            return Identifiers.parse(
                            p.getProperty("catalog." + key + ".view", "SYSCAT." + defaultName))
                    .sqlName();
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException("Invalid catalog." + key + ".view: " + e.getMessage());
        }
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/config/ConfigurationException.java =====
UTF8-BYTES: 303
SHA256: 0b0fd1f63ca5a7fe773442b36d9cd082e504aad335234118a41108166408b251
===== CONTENT =====
package com.example.db2toolkit.config;

/** Only deliberately authored, credential-free messages belong in this startup exception. */
public final class ConfigurationException extends IllegalArgumentException {
    public ConfigurationException(String message) {
        super(message);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/config/ConnectionConfig.java =====
UTF8-BYTES: 2671
SHA256: 42cc4c235fb55564339ca764de696ca1a496485d21c303895864b10554fa933e
===== CONTENT =====
package com.example.db2toolkit.config;

/** Credentials never participate in generated record/string output. */
public final class ConnectionConfig {
    private final String url, username, password;
    private final int slowQuerySeconds, cancelGraceSeconds;
    private final int connectTimeoutSeconds, readTimeoutSeconds, queryTimeoutSeconds, progressIntervalSeconds;

    public ConnectionConfig(String url, String username, String password) {
        this(url, username, password, 30, 120, 0, 10);
    }

    public ConnectionConfig(String url, String username, String password, int connectTimeoutSeconds,
                            int readTimeoutSeconds, int queryTimeoutSeconds, int progressIntervalSeconds) {
        this(url, username, password, connectTimeoutSeconds, readTimeoutSeconds, queryTimeoutSeconds,
                progressIntervalSeconds, 60, 15);
    }

    public ConnectionConfig(String url, String username, String password, int connectTimeoutSeconds,
            int readTimeoutSeconds, int queryTimeoutSeconds, int progressIntervalSeconds,
            int slowQuerySeconds, int cancelGraceSeconds) {
        if (slowQuerySeconds < 0 || cancelGraceSeconds < 1)
            throw new IllegalArgumentException("Slow query seconds must be nonnegative; cancel grace must be positive");
        this.slowQuerySeconds = slowQuerySeconds;
        this.cancelGraceSeconds = cancelGraceSeconds;
        if (connectTimeoutSeconds < 0 || readTimeoutSeconds < 0 || queryTimeoutSeconds < 0 || progressIntervalSeconds < 0)
            throw new IllegalArgumentException("Timeout/progress values must be nonnegative");
        this.connectTimeoutSeconds = connectTimeoutSeconds;
        this.readTimeoutSeconds = readTimeoutSeconds;
        this.queryTimeoutSeconds = queryTimeoutSeconds;
        this.progressIntervalSeconds = progressIntervalSeconds;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String url() {
        return url;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public int slowQuerySeconds() { return slowQuerySeconds; }
    public int cancelGraceSeconds() { return cancelGraceSeconds; }
    public int connectTimeoutSeconds() { return connectTimeoutSeconds; }
    public int readTimeoutSeconds() { return readTimeoutSeconds; }
    public int queryTimeoutSeconds() { return queryTimeoutSeconds; }
    public int progressIntervalSeconds() { return progressIntervalSeconds; }

    @Override
    public String toString() {
        return "ConnectionConfig[credentials redacted]";
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/config/DatabaseTarget.java =====
UTF8-BYTES: 185
SHA256: 1c728e60e5031d529ceca78c300c2bd94bfd19c2c7196ae26b795db8f575f6dd
===== CONTENT =====
package com.example.db2toolkit.config;

/** Stable, configuration-defined name used for logs and output isolation. */
public record DatabaseTarget(String name, AppConfig config) {}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/config/DdlExportConfig.java =====
UTF8-BYTES: 324
SHA256: e9ba3b6defe0d70b8749bbd3022bd653e95d568cba12727c7186dd96c296daaf
===== CONTENT =====
package com.example.db2toolkit.config;

import com.example.db2toolkit.ddl.model.DdlObjectType;

import java.nio.file.Path;
import java.util.Map;

public record DdlExportConfig(boolean enabled, Map<DdlObjectType, Path> lists, Path output) {
    public DdlExportConfig {
        lists = Map.copyOf(lists);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/BaselineEntry.java =====
UTF8-BYTES: 687
SHA256: 0eb123d419bbbb115c8dc8c2a95a84c36d43cacb1db2fc24400eeb6a3c2665c7
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

/** Read-only baseline result metadata; the owning plan controls staged file lifetime. */
public record BaselineEntry(
        DataTypeValidationItem item,
        DataTypeValidationResult.RangeStatus range,
        DiskLengthBuckets buckets,
        String error) {
    static BaselineEntry from(DataTypeValidationResult result) {
        return new BaselineEntry(result.item, result.range, result.buckets, result.error);
    }

    public boolean aggregationComplete() {
        return range == DataTypeValidationResult.RangeStatus.PASS
                || range == DataTypeValidationResult.RangeStatus.FAIL;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/BaselineReader.java =====
UTF8-BYTES: 24652
SHA256: c7ded0f698d7a330172de3603cc79a9d4381ec008b49540eb2ce5517b076dac9
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import com.example.db2toolkit.model.DbObjectRef;

import org.apache.poi.openxml4j.opc.*;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.math.*;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

/**
 * Reads once during preflight. The returned snapshot owns staged distributions, never a live input
 * file.
 */
public final class BaselineReader {
    public record Snapshot(
            Path path, Map<String, String> metadata, Map<DataTypeValidationItem.Key, BaselineEntry> items) {
        public long validItems() {
            return items.values().stream().filter(BaselineEntry::aggregationComplete).count();
        }
    }

    public Snapshot read(
            DataTypeValidationConfig config, List<DataTypeValidationItem> current, DataTypeValidationWorkspace workspace)
            throws IOException {
        Map<String, String> meta = new LinkedHashMap<>();
        Map<DataTypeValidationItem.Key, DataTypeValidationResult> entries = new LinkedHashMap<>();
        Map<DataTypeValidationItem.Key, Long> expectedBuckets = new HashMap<>();
        Map<String, Long> actualRows = new LinkedHashMap<>();
        try (OPCPackage pkg = OPCPackage.open(config.baseline().toFile(), PackageAccess.READ)) {
            XSSFReader reader = new XSSFReader(pkg);
            try (var strings = DiskSharedStrings.read(reader.getSharedStringsData(), workspace)) {
                Set<String> names = new HashSet<>();
                var sheets = (XSSFReader.SheetIterator) reader.getSheetsData();
                while (sheets.hasNext())
                    try (InputStream stream = sheets.next()) {
                        String name = sheets.getSheetName();
                        if (!names.add(name)) throw new IOException("Duplicate baseline sheet");
                        // The visible sheet contains bounded previews and formatting. Only the
                        // hidden protocol supplies complete, validated comparison data.
                        if (name.equals("Validation")) continue;
                        if (name.equals("_meta"))
                            readSheet(
                                    stream,
                                    strings,
                                    DataTypeValidationWorkbookFormat.META,
                                    fields -> {
                                        if (meta.putIfAbsent(fields.get(0), fields.get(1)) != null)
                                            throw new IOException(
                                                    "Duplicate baseline metadata key");
                                    });
                        else if (name.equals("_items"))
                            actualRows.put(
                                    name,
                                    readSheet(
                                            stream,
                                            strings,
                                            DataTypeValidationWorkbookFormat.ITEMS,
                                            fields -> {
                                                DataTypeValidationResult r =
                                                        item(fields, config.databaseId());
                                                if (entries.putIfAbsent(r.item.key(), r) != null)
                                                    throw new IOException(
                                                            "Duplicate baseline item");
                                                expectedBuckets.put(
                                                        r.item.key(),
                                                        nonnegative(fields.get(14))
                                                                .longValueExact());
                                                if (r.aggregationComplete()
                                                        && !r.item.original().decimal())
                                                    r.buckets = new DiskLengthBuckets(workspace);
                                            }));
                        else if (!name.matches("_(lengths|diffs)_[0-9]{3,}"))
                            throw new IOException("Unexpected baseline sheet");
                    }
                if (!names.containsAll(Set.of("Validation", "_meta", "_items")))
                    throw new IOException("Missing baseline sheets");
                verifyMeta(meta, config.databaseId(), names, entries.size());
                // Load item definitions first, then distributions: workbook sheet order need not
                // place _items before _lengths_* for every producer or spreadsheet editor.
                try (var staging = new BucketStaging()) {
                    sheets = (XSSFReader.SheetIterator) reader.getSheetsData();
                    while (sheets.hasNext())
                        try (InputStream stream = sheets.next()) {
                            String name = sheets.getSheetName();
                            if (name.startsWith("_lengths_"))
                                actualRows.put(
                                        name,
                                        readSheet(
                                                stream,
                                                strings,
                                                DataTypeValidationWorkbookFormat.LENGTHS,
                                                fields -> {
                                                    require(
                                                            fields.get(0)
                                                                    .equals(config.databaseId()),
                                                            "Wrong database ID in length record");
                                                    var key =
                                                            new DataTypeValidationItem.Key(
                                                                    fields.get(1),
                                                                    fields.get(2),
                                                                    fields.get(3));
                                                    var r = entries.get(key);
                                                    require(
                                                            r != null && r.buckets != null,
                                                            "Orphan or invalid baseline length"
                                                                + " record");
                                                    boolean isNull = bool(fields.get(4));
                                                    Long length =
                                                            isNull
                                                                    ? null
                                                                    : nonnegative(fields.get(5))
                                                                            .longValueExact();
                                                    require(
                                                            !isNull || fields.get(5).isEmpty(),
                                                            "NULL length must be blank");
                                                    staging.add(
                                                            r.buckets,
                                                            length,
                                                            nonnegative(fields.get(6)));
                                                }));
                            else if (name.startsWith("_diffs_"))
                                actualRows.put(
                                        name,
                                        readSheet(
                                                stream,
                                                strings,
                                                DataTypeValidationWorkbookFormat.DIFFS,
                                                fields -> {
                                                    throw new IOException(
                                                            "BASELINE reports cannot contain"
                                                                + " comparison differences");
                                                }));
                        }
                }
                for (var rowCount : actualRows.entrySet())
                    require(
                            rowCount.getValue()
                                    .equals(
                                            nonnegative(meta.get("rows." + rowCount.getKey()))
                                                    .longValueExact()),
                            "Baseline sheet row count mismatch");
                Set<String> expectedKeys =
                        new HashSet<>(
                                Set.of(
                                        "formatVersion",
                                        "mode",
                                        "databaseId",
                                        "executionAlias",
                                        "generatedAt",
                                        "startedAt",
                                        "endedAt",
                                        "itemCount",
                                        "lengthSheetCount",
                                        "diffSheetCount"));
                actualRows.keySet().forEach(name -> expectedKeys.add("rows." + name));
                require(
                        meta.keySet().equals(expectedKeys),
                        "Unknown or missing baseline metadata/manifest entry");
                Map<DbObjectRef, BigInteger> objectCounts = new HashMap<>();
                for (var r : entries.values()) {
                    validateDistribution(r, expectedBuckets.get(r.item.key()));
                    if (r.aggregationComplete()) {
                        BigInteger previous =
                                objectCounts.putIfAbsent(r.item.physical(), r.records);
                        require(
                                previous == null || previous.equals(r.records),
                                "Conflicting baseline record counts for one physical object");
                    }
                }
                for (var item : current) {
                    if (item.original().decimal()) continue;
                    var entry = entries.get(item.key());
                    if (entry != null)
                        require(
                                item.original().equals(entry.item.original())
                                        && item.target().equals(entry.item.target())
                                        && item.unit() == entry.item.unit(),
                                "Baseline field original/target type or length unit differs from"
                                    + " current CSV");
                }
                Map<DataTypeValidationItem.Key, BaselineEntry> snapshot = new LinkedHashMap<>();
                entries.forEach((key, value) -> snapshot.put(key, BaselineEntry.from(value)));
                return new Snapshot(
                        config.baseline().toAbsolutePath().normalize(),
                        Map.copyOf(meta),
                        Collections.unmodifiableMap(snapshot));
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Invalid baseline workbook: " + e.getMessage(), e);
        }
    }

    private static DataTypeValidationResult item(List<String> f, String id) throws IOException {
        require(f.get(0).equals(id), "Wrong baseline item database ID");
        var unit = f.get(6).isEmpty() ? null : DataTypeValidationConfig.LengthUnit.valueOf(f.get(6));
        var original = DataTypeSpec.parse(f.get(4), unit);
        var target = DataTypeSpec.parse(f.get(5), unit);
        original.checkTarget(target);
        require(
                f.get(4).equals(original.toString()) && f.get(5).equals(target.toString()),
                "Noncanonical type definition");
        require(original.decimal() == (unit == null), "Invalid baseline length unit");
        for (int index : new int[] {1, 2, 3, 7, 8})
            require(
                    f.get(index).equals(DataTypeValidationObjectResolver.identifier(f.get(index))),
                    "Noncanonical identifier");
        var item =
                new DataTypeValidationItem(
                        new DbObjectRef(f.get(1), f.get(2)),
                        f.get(3),
                        original,
                        target,
                        unit,
                        new DbObjectRef(f.get(7), f.get(8)));
        var r = new DataTypeValidationResult(item);
        r.actualType = f.get(9);
        r.range = DataTypeValidationResult.RangeStatus.valueOf(f.get(10).replace(' ', '_'));
        require(f.get(10).equals(r.range.toString()), "Noncanonical range status");
        require(
                bool(f.get(11)) == r.aggregationComplete(),
                "Aggregation completion/status mismatch");
        r.error = f.get(15);
        r.compare = CompareStatus.fromLabel(f.get(18));
        require(
                r.compare
                        == (r.aggregationComplete()
                                ? CompareStatus.NOT_APPLICABLE
                                : CompareStatus.NOT_COMPARED),
                "Invalid BASELINE compare status");
        require(
                f.get(19).isEmpty() && f.get(20).isEmpty(),
                "BASELINE cannot contain difference counts");
        if (r.aggregationComplete()) {
            require(!r.actualType.isBlank(), "Missing actual type for completed baseline item");
            r.records = nonnegative(f.get(12));
            r.overflow = nonnegative(f.get(13));
            require(r.overflow.compareTo(r.records) <= 0, "Overflow exceeds record count");
            require(
                    (r.overflow.signum() == 0) == (r.range == DataTypeValidationResult.RangeStatus.PASS),
                    "RangeStatus status/overflow mismatch");
            if (original.decimal()) {
                r.min = decimal(f.get(16));
                r.max = decimal(f.get(17));
                require((r.min == null) == (r.max == null), "Incomplete numeric extrema");
                require(r.min == null || r.min.compareTo(r.max) <= 0, "MIN exceeds MAX");
                require(r.records.signum() > 0 || r.min == null, "Empty data has numeric extrema");
                require(r.min != null || r.overflow.signum() == 0, "All-NULL data has overflow");
                if (r.min != null) {
                    r.min.toBigIntegerExact();
                    r.max.toBigIntegerExact();
                    require(
                            r.min.compareTo(r.max) == 0 || r.records.compareTo(BigInteger.TWO) >= 0,
                            "Distinct numeric extrema require at least two records");
                    BigDecimal bound = new BigDecimal(original.upperBound());
                    boolean outside =
                            r.min.compareTo(bound.negate()) < 0 || r.max.compareTo(bound) > 0;
                    require(
                            outside == (r.overflow.signum() > 0),
                            "Numeric extrema/overflow mismatch");
                }
            } else
                require(
                        f.get(16).isEmpty() && f.get(17).isEmpty(),
                        "Character item has numeric extrema");
        } else
            for (int index : new int[] {12, 13, 16, 17})
                require(f.get(index).isEmpty(), "Failed baseline item has aggregate values");
        return r;
    }

    private static void validateDistribution(DataTypeValidationResult r, long expected) throws IOException {
        if (r.buckets == null) {
            require(expected == 0, "Non-character/failed item has buckets");
            return;
        }
        require(
                r.buckets.size() == expected && r.buckets.total().equals(r.records),
                "Incomplete baseline distribution");
        BigInteger overflow = BigInteger.ZERO;
        try (var cursor = r.buckets.cursor()) {
            DiskLengthBuckets.Bucket bucket;
            while ((bucket = cursor.next()) != null)
                if (bucket.length() != null && bucket.length() > r.item.original().size())
                    overflow = overflow.add(bucket.count());
        }
        require(
                overflow.equals(r.overflow),
                "Baseline overflow does not match complete distribution");
    }

    private static void verifyMeta(Map<String, String> m, String id, Set<String> names, int count)
            throws IOException {
        require(
                "1".equals(m.get("formatVersion")) && "BASELINE".equals(m.get("mode")),
                "Expected formatVersion=1 and mode=BASELINE");
        require(id.equals(m.get("databaseId")), "Wrong baseline database ID");
        require(
                m.getOrDefault("executionAlias", "").matches("[A-Za-z][A-Za-z0-9_-]{0,39}"),
                "Invalid execution alias");
        Instant start = Instant.parse(m.get("startedAt")), end = Instant.parse(m.get("endedAt"));
        require(
                !end.isBefore(start) && !Instant.parse(m.get("generatedAt")).isBefore(end),
                "Invalid baseline time range");
        require(
                count > 0 && nonnegative(m.get("itemCount")).equals(BigInteger.valueOf(count)),
                "Baseline item count mismatch");
        Set<String> expected = new HashSet<>(Set.of("Validation", "_meta", "_items"));
        for (String kind : List.of("length", "diff")) {
            int sheets = nonnegative(m.get(kind + "SheetCount")).intValueExact();
            require(sheets <= names.size(), "Invalid baseline sheet count");
            for (int i = 1; i <= sheets; i++)
                expected.add("_" + kind + "s_" + String.format(Locale.ROOT, "%03d", i));
        }
        require(expected.equals(names), "Missing or unexpected baseline continuation sheet");
    }

    static BigInteger nonnegative(String value) throws IOException {
        require(
                value != null && value.matches("0|[1-9][0-9]*"),
                "Expected canonical nonnegative integer");
        return new BigInteger(value);
    }

    private static BigDecimal decimal(String value) throws IOException {
        if (value.isEmpty()) return null;
        require(value.matches("-?(0|[1-9][0-9]*)(\\.[0-9]+)?"), "Invalid baseline decimal");
        return new BigDecimal(value);
    }

    private static boolean bool(String value) throws IOException {
        require(value.equals("true") || value.equals("false"), "Invalid baseline boolean");
        return value.equals("true");
    }

    private static void require(boolean condition, String reason) throws IOException {
        if (!condition) throw new IOException(reason);
    }

    private static final class BucketStaging implements AutoCloseable {
        DiskLengthBuckets active;
        DiskLengthBuckets.Appender appender;

        void add(DiskLengthBuckets buckets, Long length, BigInteger count) throws IOException {
            if (active != buckets) {
                if (appender != null) {
                    appender.close();
                    appender = null;
                }
                active = buckets;
                appender = active.appender();
            }
            appender.add(length, count);
        }

        @Override
        public void close() throws IOException {
            if (appender != null) appender.close();
        }
    }

    @FunctionalInterface
    private interface RowConsumer {
        void accept(List<String> fields) throws IOException;
    }

    private static long readSheet(
            InputStream input, DiskSharedStrings strings, List<String> header, RowConsumer consumer)
            throws Exception {
        class Handler extends DefaultHandler {
            final List<String> fields = new ArrayList<>();
            final StringBuilder value = new StringBuilder();
            boolean capturing;
            String type;
            int column;
            long rows;

            @Override
            public void startElement(String uri, String local, String qName, Attributes a)
                    throws SAXException {
                switch (local) {
                    case "row" -> fields.clear();
                    case "c" -> {
                        value.setLength(0);
                        type = a.getValue("t");
                        String ref = a.getValue("r");
                        column = 0;
                        if (ref == null) throw new SAXException("Missing cell reference");
                        for (int i = 0; i < ref.length() && Character.isLetter(ref.charAt(i)); i++)
                            column = column * 26 + ref.charAt(i) - 'A' + 1;
                        if (column != fields.size() + 1 || column > header.size())
                            throw new SAXException("Invalid metadata column order/count");
                        if (!"inlineStr".equals(type) && !"s".equals(type))
                            throw new SAXException(
                                    "Metadata cells must contain text, not numbers or formulas");
                    }
                    case "f" -> throw new SAXException("Baseline formulas are not supported");
                    case "t", "v" -> {
                        if (local.equals("v") != "s".equals(type))
                            throw new SAXException("Invalid metadata string representation");
                        capturing = true;
                    }
                }
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                if (capturing) {
                    value.append(ch, start, length);
                    if (value.length() > 240000) throw new SAXException("Oversized metadata cell");
                }
            }

            @Override
            public void endElement(String uri, String local, String qName) throws SAXException {
                try {
                    if (local.equals("t") || local.equals("v")) capturing = false;
                    else if (local.equals("c")) {
                        String text =
                                "s".equals(type)
                                        ? strings.get(Long.parseLong(value.toString()))
                                        : SpreadsheetMlTextCodec.unescape(value.toString());
                        com.example.db2toolkit.spreadsheet.ExcelText.validate(text);
                        fields.add(text);
                    } else if (local.equals("row")) {
                        require(
                                fields.size() == header.size(),
                                "Metadata row column count mismatch");
                        if (rows++ == 0)
                            require(
                                    fields.equals(header),
                                    "Baseline header differs from formatVersion=1");
                        else consumer.accept(List.copyOf(fields));
                    }
                } catch (Exception e) {
                    throw new SAXException(e);
                }
            }
        }
        Handler handler = new Handler();
        XMLReader parser = XMLHelper.newXMLReader();
        parser.setContentHandler(handler);
        parser.parse(new InputSource(input));
        require(handler.rows > 0, "Missing baseline header");
        return handler.rows - 1;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/CompareStatus.java =====
UTF8-BYTES: 753
SHA256: ec507d538d33a8ac50e06fa2529678ccf54b958875c6a440f15ff47efc260a27
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

/** Stable workbook labels are independent of Java enum names. */
public enum CompareStatus {
    NOT_COMPARED(""),
    NOT_APPLICABLE("N/A"),
    PASS("PASS"),
    DIFFERENT("DIFFERENT"),
    BASELINE_NOT_FOUND("BASELINE NOT FOUND"),
    BASELINE_INVALID("BASELINE INVALID");
    private final String label;

    CompareStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static CompareStatus fromLabel(String label) {
        for (var status : values()) if (status.label.equals(label)) return status;
        throw new IllegalArgumentException("Unknown comparison status");
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/DataTypeSpec.java =====
UTF8-BYTES: 2411
SHA256: 1482f9685cabdb63e1bd67d071b8bf606dcba9ee0c288d338b2450508b030c7f
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import java.math.BigInteger;
import java.util.Locale;
import java.util.regex.Pattern;

public record DataTypeSpec(Kind kind, int size) {
    public enum Kind {
        DECIMAL,
        VARCHAR,
        CLOB
    }

    private static final Pattern TYPE =
            Pattern.compile(
                    "(DECIMAL|VARCHAR|CLOB)\\s*\\(\\s*([0-9]+)\\s*(?:,\\s*([0-9]+)\\s*)?\\)");

    public static DataTypeSpec parse(String value, DataTypeValidationConfig.LengthUnit unit) {
        var m = TYPE.matcher(value.strip().toUpperCase(Locale.ROOT));
        if (!m.matches())
            throw new IllegalArgumentException(
                    "Unsupported data type; expected DECIMAL(p,0), VARCHAR(n), or CLOB(n)");
        Kind kind = Kind.valueOf(m.group(1));
        if (kind == Kind.DECIMAL ? !"0".equals(m.group(3)) : m.group(3) != null)
            throw new IllegalArgumentException(
                    "Only DECIMAL scale 0 and character lengths are supported");
        long max =
                kind == Kind.DECIMAL
                        ? 31
                        : kind == Kind.VARCHAR
                                ? (unit == DataTypeValidationConfig.LengthUnit.OCTETS ? 32672 : 8168)
                                : (unit == DataTypeValidationConfig.LengthUnit.OCTETS
                                        ? 2147483647L
                                        : 536870911L);
        BigInteger n = new BigInteger(m.group(2));
        if (n.signum() <= 0 || n.compareTo(BigInteger.valueOf(max)) > 0)
            throw new IllegalArgumentException("Data type size out of supported range");
        return new DataTypeSpec(kind, n.intValueExact());
    }

    public boolean decimal() {
        return kind == Kind.DECIMAL;
    }

    public BigInteger upperBound() {
        return BigInteger.TEN.pow(size).subtract(BigInteger.ONE);
    }

    public void checkTarget(DataTypeSpec target) {
        if (!(kind == target.kind || kind == Kind.VARCHAR && target.kind == Kind.CLOB)
                || target.size < size)
            throw new IllegalArgumentException(
                    "Only same-type widening or VARCHAR to CLOB widening is supported");
    }

    @Override
    public String toString() {
        return kind + "(" + size + (decimal() ? ",0" : "") + ")";
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationBatch.java =====
UTF8-BYTES: 1382
SHA256: 190ee7a15cfb02223c7b9c9266bec5cca1d3ac0ba37db0e0f878e784302da2bf
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import com.example.db2toolkit.config.DatabaseTarget;
import com.example.db2toolkit.jdbc.SafeDiagnostics;

import java.io.IOException;
import java.util.*;

/**
 * Preflights every target before execution and owns snapshots through failures and skipped targets.
 */
public final class DataTypeValidationBatch implements AutoCloseable {
    private final Map<String, DataTypeValidationPlan> plans = new LinkedHashMap<>();

    public void prepare(List<DatabaseTarget> targets, SafeDiagnostics errors) throws IOException {
        for (var target : targets)
            if (target.config().validation().enabled()) {
                var plan = DataTypeValidationPlan.prepare(target.config().validation());
                plans.put(target.name(), plan);
                plan.logBaseline(errors);
            }
    }

    public DataTypeValidationPlan get(String alias) {
        return plans.get(alias);
    }

    @Override
    public void close() throws IOException {
        IOException failure = null;
        for (var plan : plans.values())
            try {
                plan.close();
            } catch (IOException e) {
                if (failure == null) failure = e;
                else failure.addSuppressed(e);
            }
        if (failure != null) throw failure;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationConfig.java =====
UTF8-BYTES: 875
SHA256: ccc486e904acaad10a5d56aa122e21d2f696ee95e8e15554ae5f27385d3190f4
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import java.nio.file.Path;

public record DataTypeValidationConfig(
        boolean enabled,
        Mode mode,
        String databaseId,
        Path file,
        Path output,
        boolean itsview,
        String viewSchema,
        LengthUnit lengthUnit,
        boolean compareEnabled,
        Path baseline,
        int queryTimeoutSeconds) {
    public enum Mode {
        BASELINE,
        VALIDATE
    }

    public enum LengthUnit {
        OCTETS,
        CODEUNITS32
    }

    public static DataTypeValidationConfig disabled() {
        return new DataTypeValidationConfig(
                false, null, null, null, null, false, null, null, false, null, 0);
    }

    public boolean comparing() {
        return enabled && mode == Mode.VALIDATE && compareEnabled;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationConfigLoader.java =====
UTF8-BYTES: 6716
SHA256: 313052e653f125a430cfaee699b6fb8d0aad78ef20d27e85f15742a90e51ec98
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import com.example.db2toolkit.config.ConfigurationException;

import java.nio.file.Path;
import java.util.*;

public final class DataTypeValidationConfigLoader {
    private static final Set<String> SUFFIXES =
            Set.of(
                    "enabled",
                    "mode",
                    "database-id",
                    "file",
                    "itsview.enabled",
                    "itsview.schema",
                    "length-unit",
                    "baseline.compare.enabled",
                    "baseline.file",
                    "query-timeout-seconds");

    private DataTypeValidationConfigLoader() {}

    public static void checkKeys(Properties p, List<String> aliases, boolean multiple) {
        for (String key : p.stringPropertyNames()) {
            String suffix = null;
            if (key.startsWith("validation.")) {
                suffix = key.substring(11);
                if (suffix.equals("output-directory")) continue;
            } else if (key.startsWith("db.")) {
                int end = key.indexOf('.', 3);
                if (end < 0) continue;
                String alias = key.substring(3, end), rest = key.substring(end + 1);
                if (!rest.startsWith("validation.")) continue;
                if (!multiple || !aliases.contains(alias))
                    throw new ConfigurationException(
                            "Unknown database alias or single-database scoped key: " + key);
                suffix = rest.substring(11);
            }
            if (suffix == null) continue;
            if (suffix.equals("environment") || suffix.startsWith("prod."))
                throw new ConfigurationException(
                        "Obsolete key "
                                + key
                                + "; use validation.mode, validation.baseline.compare.enabled and"
                                + " validation.baseline.file");
            if (!SUFFIXES.contains(suffix))
                throw new ConfigurationException("Unknown validation key: " + key);
        }
    }

    public static DataTypeValidationConfig load(Properties p, Path base, String alias, boolean multiple) {
        String prefix = multiple ? "db." + alias + ".validation." : "validation.";
        if (!bool(value(p, prefix, "enabled", "false"), "validation.enabled"))
            return DataTypeValidationConfig.disabled();
        var mode =
                enumeration(
                        DataTypeValidationConfig.Mode.class,
                        value(p, prefix, "mode", null),
                        "validation.mode");
        String id = required(value(p, prefix, "database-id", null), "validation.database-id");
        if (!id.matches("[A-Za-z][A-Za-z0-9_-]{0,39}"))
            throw new ConfigurationException("Invalid validation.database-id");
        Path file =
                base.resolve(required(value(p, prefix, "file", null), "validation.file"))
                        .normalize();
        Path output =
                base.resolve(
                                required(
                                        p.getProperty(
                                                "validation.output-directory",
                                                "./output/validation"),
                                        "validation.output-directory"))
                        .normalize();
        if (multiple) output = output.resolve("db-" + alias);
        boolean itsview =
                bool(value(p, prefix, "itsview.enabled", "false"), "validation.itsview.enabled");
        String schema =
                itsview
                        ? DataTypeValidationObjectResolver.identifier(
                                value(p, prefix, "itsview.schema", "ITSVIEW"))
                        : "ITSVIEW";
        var unit =
                enumeration(
                        DataTypeValidationConfig.LengthUnit.class,
                        value(p, prefix, "length-unit", "OCTETS"),
                        "validation.length-unit");
        boolean compare =
                bool(
                        value(p, prefix, "baseline.compare.enabled", "false"),
                        "validation.baseline.compare.enabled");
        Path baseline =
                mode == DataTypeValidationConfig.Mode.VALIDATE && compare
                        ? base.resolve(
                                        required(
                                                value(p, prefix, "baseline.file", null),
                                                "validation.baseline.file"))
                                .normalize()
                        : null;
        int timeout;
        try {
            String seconds = value(p, prefix, "query-timeout-seconds", "0");
            if (!seconds.matches("[0-9]+")) throw new NumberFormatException();
            timeout = Integer.parseInt(seconds);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(
                    "validation.query-timeout-seconds must be a nonnegative int");
        }
        return new DataTypeValidationConfig(
                true,
                mode,
                id.toUpperCase(Locale.ROOT),
                file,
                output,
                itsview,
                schema,
                unit,
                compare,
                baseline,
                timeout);
    }

    private static String value(Properties p, String prefix, String suffix, String fallback) {
        String value =
                p.getProperty(prefix + suffix, p.getProperty("validation." + suffix, fallback));
        return value == null ? null : value.strip();
    }

    private static String required(String value, String key) {
        if (value == null || value.isBlank())
            throw new ConfigurationException("Missing configuration key: " + key);
        return value.strip();
    }

    private static boolean bool(String value, String key) {
        if (!"true".equals(value) && !"false".equals(value))
            throw new ConfigurationException(key + " must be true or false");
        return Boolean.parseBoolean(value);
    }

    private static <E extends Enum<E>> E enumeration(Class<E> type, String value, String key) {
        try {
            return Enum.valueOf(type, required(value, key).toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException("Invalid " + key);
        }
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationCsvReader.java =====
UTF8-BYTES: 7932
SHA256: 03adb44c8b6c76b966dfd891df2b1899c2bf84e3d294a603b3f731e5d51d091a
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import com.example.db2toolkit.model.DbObjectRef;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * Strict streaming CSV reader; diagnostics identify record positions without echoing source data.
 */
public final class DataTypeValidationCsvReader {
    public List<DataTypeValidationItem> read(DataTypeValidationConfig config) throws IOException {
        try (var reader =
                new PushbackReader(
                        Files.newBufferedReader(config.file(), StandardCharsets.UTF_8), 2)) {
            int first = reader.read();
            if (first != -1 && first != '\uFEFF') reader.unread(first);
            List<String> header = record(reader, 1);
            if (header == null) throw new IOException("Empty validation CSV");
            Map<String, Integer> columns = new HashMap<>();
            var required =
                    Set.of("table_name", "field_name", "original_data_type", "after_data_type");
            for (int i = 0; i < header.size(); i++) {
                String name = header.get(i).strip().toLowerCase(Locale.ROOT);
                if ((!required.contains(name) && !name.equals("length_unit"))
                        || columns.put(name, i) != null)
                    throw new IOException("Duplicate or unknown CSV header");
            }
            if (!columns.keySet().containsAll(required))
                throw new IOException("Missing required CSV header");
            List<DataTypeValidationItem> items = new ArrayList<>();
            Set<DataTypeValidationItem.Key> seen = new HashSet<>();
            Map<DbObjectRef, DbObjectRef> mappings = new HashMap<>();
            DataTypeValidationObjectResolver resolver = new DataTypeValidationObjectResolver(config);
            int position = 1;
            List<String> row;
            while ((row = record(reader, position + 1)) != null) {
                position++;
                try {
                    if (row.size() != header.size())
                        throw new IllegalArgumentException("CSV column count differs from header");
                    var logical =
                            DataTypeValidationObjectResolver.logical(row.get(columns.get("table_name")));
                    String field =
                            DataTypeValidationObjectResolver.identifier(row.get(columns.get("field_name")));
                    String units =
                            columns.containsKey("length_unit")
                                    ? row.get(columns.get("length_unit")).strip()
                                    : "";
                    var unit =
                            units.isEmpty()
                                    ? config.lengthUnit()
                                    : DataTypeValidationConfig.LengthUnit.valueOf(
                                            units.toUpperCase(Locale.ROOT));
                    var original =
                            DataTypeSpec.parse(
                                    row.get(columns.get("original_data_type")), unit);
                    var target =
                            DataTypeSpec.parse(row.get(columns.get("after_data_type")), unit);
                    original.checkTarget(target);
                    if (original.decimal() && !units.isEmpty())
                        throw new IllegalArgumentException("DECIMAL length_unit must be empty");
                    var physical =
                            resolver.resolveQueryObject(logical.schema() + "." + logical.name());
                    var previous = mappings.putIfAbsent(physical, logical);
                    if (previous != null && !previous.equals(logical))
                        throw new IllegalArgumentException("ITSVIEW object mapping collision");
                    var item =
                            new DataTypeValidationItem(
                                    logical,
                                    field,
                                    original,
                                    target,
                                    original.decimal() ? null : unit,
                                    physical);
                    if (!seen.add(item.key()))
                        throw new IllegalArgumentException("Duplicate logical object/field");
                    if (items.size() == 1048575)
                        throw new IllegalArgumentException(
                                "Validation exceeds single report sheet row limit");
                    items.add(item);
                } catch (IllegalArgumentException e) {
                    throw new IOException(
                            "Invalid validation CSV record " + position + ": " + e.getMessage(), e);
                }
            }
            if (items.isEmpty()) throw new IOException("Validation CSV contains no items");
            return List.copyOf(items);
        } catch (IOException e) {
            throw new IOException("Validation CSV " + config.file() + ": " + e.getMessage(), e);
        }
    }

    private static List<String> record(PushbackReader in, int position) throws IOException {
        try {
            return record(in);
        } catch (IOException e) {
            throw new IOException("CSV record " + position + ": " + e.getMessage(), e);
        }
    }

    private static List<String> record(PushbackReader in) throws IOException {
        var fields = new ArrayList<String>();
        var field = new StringBuilder();
        boolean quoted = false, closed = false, touched = false;
        while (true) {
            int ch = in.read();
            if (ch == -1) {
                if (quoted) throw new IOException("Unclosed CSV quote");
                if (!touched && fields.isEmpty()) return null;
                fields.add(field.toString());
                return fields;
            }
            char c = (char) ch;
            if (quoted) {
                if (c == '"') {
                    int next = in.read();
                    if (next == '"') field.append('"');
                    else {
                        quoted = false;
                        closed = true;
                        if (next != -1) in.unread(next);
                    }
                } else field.append(c);
            } else if (c == '\n' || c == '\r') {
                if (c == '\r') {
                    int next = in.read();
                    if (next != '\n' && next != -1) in.unread(next);
                }
                if (!touched && fields.isEmpty()) continue;
                fields.add(field.toString());
                return fields;
            } else if (c == ',') {
                // Four required columns plus one optional length_unit. Reject before a malformed
                // record with millions of delimiters can accumulate an unbounded field list.
                if (fields.size() == 4) throw new IOException("CSV record exceeds five columns");
                fields.add(field.toString());
                field.setLength(0);
                closed = false;
                touched = true;
            } else if (closed) {
                if (c != ' ' && c != '\t')
                    throw new IOException("Unexpected character after CSV quote");
            } else if (c == '"') {
                if (!field.toString().isBlank()) throw new IOException("Unexpected CSV quote");
                field.setLength(0);
                quoted = true;
                touched = true;
            } else {
                field.append(c);
                touched = true;
            }
            if (field.length() > 32767) throw new IOException("CSV field exceeds supported length");
        }
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationItem.java =====
UTF8-BYTES: 516
SHA256: 9b9485085cd22ef524e43626499979779f2370f27aca270da7210acde2cb37b1
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import com.example.db2toolkit.model.DbObjectRef;

public record DataTypeValidationItem(
        DbObjectRef logical,
        String field,
        DataTypeSpec original,
        DataTypeSpec target,
        DataTypeValidationConfig.LengthUnit unit,
        DbObjectRef physical) {
    public record Key(String schema, String object, String field) {}

    public Key key() {
        return new Key(logical.schema(), logical.name(), field);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationObjectResolver.java =====
UTF8-BYTES: 1472
SHA256: 26565c78cc30558314904ef3e4e910eb4507514765eb444e25eb9aa6e0eddcf0
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import com.example.db2toolkit.model.DbObjectRef;
import com.example.db2toolkit.model.Identifiers;

import java.util.Locale;

public final class DataTypeValidationObjectResolver {
    private final DataTypeValidationConfig config;

    public DataTypeValidationObjectResolver(DataTypeValidationConfig config) {
        this.config = config;
    }

    public static String identifier(String value) {
        if (value == null
                || !value.strip().matches("[A-Za-z_][A-Za-z0-9_@$#]*")
                || value.strip().length() > 128)
            throw new IllegalArgumentException(
                    "Expected an ordinary identifier of at most 128 characters; quoted identifiers"
                        + " are not supported");
        return value.strip().toUpperCase(Locale.ROOT);
    }

    public static DbObjectRef logical(String value) {
        String[] parts = value.strip().split("\\.", -1);
        if (parts.length != 2) throw new IllegalArgumentException("Expected schema.object");
        return Identifiers.parse(identifier(parts[0]) + "." + identifier(parts[1]));
    }

    public DbObjectRef resolveQueryObject(String configuredTableName) {
        var ref = logical(configuredTableName);
        return config.itsview()
                ? new DbObjectRef(config.viewSchema(), identifier(ref.schema() + "_" + ref.name()))
                : ref;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationPlan.java =====
UTF8-BYTES: 2824
SHA256: 595885e1d4fadb0e712e74a121ef1485111f237cac1e31b5029f986c2033d4f7
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import com.example.db2toolkit.jdbc.SafeDiagnostics;

import org.slf4j.*;

import java.io.IOException;
import java.util.List;

public final class DataTypeValidationPlan implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(DataTypeValidationPlan.class);
    final List<DataTypeValidationItem> items;
    final BaselineReader.Snapshot baseline;
    final DataTypeValidationWorkspace workspace;
    private boolean closed;

    private DataTypeValidationPlan(
            List<DataTypeValidationItem> items,
            BaselineReader.Snapshot baseline,
            DataTypeValidationWorkspace workspace) {
        this.items = items;
        this.baseline = baseline;
        this.workspace = workspace;
    }

    public static DataTypeValidationPlan prepare(DataTypeValidationConfig config) throws IOException {
        List<DataTypeValidationItem> items = new DataTypeValidationCsvReader().read(config);
        var workspace = new DataTypeValidationWorkspace();
        try {
            var baseline =
                    config.comparing() ? new BaselineReader().read(config, items, workspace) : null;
            return new DataTypeValidationPlan(items, baseline, workspace);
        } catch (IOException | RuntimeException e) {
            try {
                workspace.close();
            } catch (IOException close) {
                e.addSuppressed(close);
            }
            throw e;
        }
    }

    public void logBaseline(SafeDiagnostics errors) {
        if (baseline != null)
            LOG.info(
                    "Validation baseline: path={} generatedAt={} databaseId={} items={} valid={}"
                        + " invalid={}",
                    errors.clean(baseline.path().toString()),
                    errors.clean(baseline.metadata().get("generatedAt")),
                    errors.clean(baseline.metadata().get("databaseId")),
                    baseline.items().size(),
                    baseline.validItems(),
                    baseline.items().size() - baseline.validItems());
    }

    public List<DataTypeValidationResult> initialResults(String reason) {
        return items.stream()
                .map(
                        item -> {
                            var r = new DataTypeValidationResult(item);
                            r.error = reason;
                            return r;
                        })
                .toList();
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            workspace.close();
            // Failed cleanup remains owned by the batch, which can retry at shutdown.
            closed = true;
        }
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationResult.java =====
UTF8-BYTES: 2255
SHA256: ec6fe7ba8956208bea1eda90637498424de10684275b3a78b16470a8e628e262
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import java.math.*;

public final class DataTypeValidationResult {
    public enum RangeStatus {
        PASS,
        FAIL,
        TYPE_MISMATCH,
        TABLE_NOT_FOUND,
        COLUMN_NOT_FOUND,
        ERROR,
        NOT_EXECUTED;

        @Override
        public String toString() {
            return name().replace('_', ' ');
        }
    }

    final DataTypeValidationItem item;
    RangeStatus range = RangeStatus.NOT_EXECUTED;
    CompareStatus compare = CompareStatus.NOT_COMPARED;
    String actualType = "", error = "";
    BigInteger records, overflow;
    BigDecimal min, max;
    DiskLengthBuckets buckets, baselineOnly, currentOnly;
    boolean singleLengthMatchHint;
    long elapsedMs;

    public DataTypeValidationResult(DataTypeValidationItem item) {
        this.item = item;
    }

    public DataTypeValidationItem item() {
        return item;
    }

    public RangeStatus range() {
        return range;
    }

    public String compare() {
        return compare.toString();
    }

    public BigInteger records() {
        return records;
    }

    public BigInteger overflow() {
        return overflow;
    }

    public boolean singleLengthMatchHint() {
        return singleLengthMatchHint;
    }

    public boolean aggregationComplete() {
        // Range FAIL still has usable aggregates and can be compared. Query/metadata errors do not.
        return range == RangeStatus.PASS || range == RangeStatus.FAIL;
    }

    public boolean successful() {
        return range == RangeStatus.PASS
                && (compare == CompareStatus.PASS || compare == CompareStatus.NOT_APPLICABLE);
    }

    void fail(RangeStatus status, String reason) {
        // Never serialize partial aggregates as a usable baseline. Their staged files remain
        // owned by the workspace and are cleaned up even after these references are cleared.
        range = status;
        error = reason;
        compare = CompareStatus.NOT_COMPARED;
        records = overflow = null;
        min = max = null;
        buckets = baselineOnly = currentOnly = null;
        singleLengthMatchHint = false;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationService.java =====
UTF8-BYTES: 11083
SHA256: 41380798cff7d52e64770b6e8f825a5a54fabad369c897ef23e43d7a7c182b58
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import com.example.db2toolkit.config.AppConfig;
import com.example.db2toolkit.jdbc.SafeDiagnostics;
import com.example.db2toolkit.model.DbObjectRef;

import org.slf4j.*;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;

public final class DataTypeValidationService {
    private static final Logger LOG = LoggerFactory.getLogger(DataTypeValidationService.class);

    public void validateAndWriteReport(
            Connection connection,
            AppConfig app,
            DataTypeValidationPlan plan,
            String alias,
            Path runDirectory,
            boolean connectionUsable,
            DataTypeValidationSummary summary,
            SafeDiagnostics errors) {
        Instant started = Instant.now();
        List<DataTypeValidationResult> results =
                plan.initialResults(
                        connectionUsable
                                ? "Not executed: validation did not reach this item"
                                : "Database connection unavailable");
        summary.results(results);
        // Metadata and row-count reuse follow the queried table/view, not the CSV's logical name.
        var groups = new LinkedHashMap<DbObjectRef, List<DataTypeValidationResult>>();
        results.forEach(
                r -> groups.computeIfAbsent(r.item.physical(), key -> new ArrayList<>()).add(r));
        try {
            if (runDirectory == null) {
                Files.createDirectories(app.validation().output());
                runDirectory = Files.createTempDirectory(app.validation().output(), "run-");
            }
            LOG.info(
                    "Validation started: items={} startedAt={} mode={}",
                    results.size(),
                    started,
                    app.validation().mode());
            plan.logBaseline(errors);
            if (app.validation().mode() == DataTypeValidationConfig.Mode.BASELINE
                    && app.validation().compareEnabled())
                LOG.info("BASELINE mode: comparison is not executed");
            var jdbc = new Db2DataTypeQueries(connection, app);
            for (var group : groups.entrySet()) {
                if (!connectionUsable) break;
                long objectStart = System.nanoTime();
                LOG.info(
                        "Validation object started: {} startedAt={}",
                        errors.clean(group.getKey().sqlName()),
                        Instant.now());
                try {
                    Db2DataTypeQueries.Metadata metadata;
                    try {
                        metadata = jdbc.metadata(group.getKey());
                    } catch (SQLException | RuntimeException e) {
                        summary.queryFailed();
                        String reason = errors.describe(e);
                        group.getValue()
                                .forEach(
                                        r -> {
                                            r.fail(DataTypeValidationResult.RangeStatus.ERROR, reason);
                                            r.elapsedMs = elapsed(objectStart);
                                        });
                        connectionUsable = usable(connection, e);
                        LOG.warn(
                                "Validation metadata failed: {} elapsedMs={}",
                                reason,
                                elapsed(objectStart));
                        continue;
                    }
                    BigInteger total = null;
                    boolean inconsistent = false;
                    for (DataTypeValidationResult r : group.getValue()) {
                        if (!connectionUsable) break;
                        long fieldStart = System.nanoTime();
                        try {
                            if (!metadata.exists()) {
                                r.fail(
                                        DataTypeValidationResult.RangeStatus.TABLE_NOT_FOUND,
                                        "Expected "
                                                + (app.validation().itsview()
                                                        ? "view"
                                                        : "table/view")
                                                + " not found in configured catalog "
                                                + app.catalog().tableView());
                                continue;
                            }
                            var column = metadata.columns().get(r.item.field());
                            if (column == null) {
                                r.fail(
                                        DataTypeValidationResult.RangeStatus.COLUMN_NOT_FOUND,
                                        "Column not found in configured column catalog "
                                                + app.catalog().columnView());
                                continue;
                            }
                            r.actualType = column.description();
                            if (!column.compatible(r.item.original())) {
                                r.fail(
                                        DataTypeValidationResult.RangeStatus.TYPE_MISMATCH,
                                        "Actual type is incompatible with original validation"
                                            + " rule");
                                continue;
                            }
                            if (r.item.original().decimal()) {
                                var aggregate = jdbc.decimal(r.item, total);
                                r.records = aggregate.records();
                                r.overflow = aggregate.overflow();
                                r.min = aggregate.min();
                                r.max = aggregate.max();
                            } else {
                                var aggregate = jdbc.lengths(r.item, plan.workspace);
                                r.records = aggregate.records();
                                r.overflow = aggregate.overflow();
                                r.buckets = aggregate.buckets();
                            }
                            if (total == null) total = r.records;
                            // Detect contradictions between scans; this is not a transaction
                            // snapshot and cannot detect every concurrent data change.
                            if (!total.equals(r.records)
                                    || r.overflow.compareTo(total) > 0
                                    || r.min != null
                                            && total.compareTo(
                                                            r.min.compareTo(r.max) == 0
                                                                    ? BigInteger.ONE
                                                                    : BigInteger.TWO)
                                                    < 0) inconsistent = true;
                            r.records = total;
                            r.range =
                                    r.overflow.signum() == 0
                                            ? DataTypeValidationResult.RangeStatus.PASS
                                            : DataTypeValidationResult.RangeStatus.FAIL;
                            r.error = "";
                        } catch (SQLException | RuntimeException e) {
                            summary.queryFailed();
                            r.fail(DataTypeValidationResult.RangeStatus.ERROR, errors.describe(e));
                            connectionUsable = usable(connection, e);
                        } finally {
                            r.elapsedMs = elapsed(fieldStart);
                            LOG.info(
                                    "Validation field query finished: {}.{} status={} elapsedMs={};"
                                        + " object confirmation pending",
                                    errors.clean(r.item.physical().sqlName()),
                                    errors.clean(r.item.field()),
                                    r.range,
                                    r.elapsedMs);
                        }
                    }
                    // Delay comparison and hints until all column queries finish, so a later
                    // count contradiction can invalidate earlier aggregates too.
                    for (var r : group.getValue())
                        if (r.aggregationComplete()) {
                            if (inconsistent)
                                r.fail(
                                        DataTypeValidationResult.RangeStatus.ERROR,
                                        "Object count changed during validation; complete object"
                                            + " distribution invalidated");
                            else LengthSetComparator.compare(r, app.validation(), plan);
                        }
                } finally {
                    LOG.info(
                            "Validation object finished: {} endedAt={} elapsedMs={}",
                            errors.clean(group.getKey().sqlName()),
                            Instant.now(),
                            elapsed(objectStart));
                }
            }
            if (!connectionUsable)
                for (var r : results)
                    if (r.range == DataTypeValidationResult.RangeStatus.NOT_EXECUTED)
                        r.error =
                                "Not executed: database connection unavailable after an earlier"
                                    + " failure";
            Path output = runDirectory.resolve(alias + "-datatype-validation.xlsx");
            DataTypeValidationWorkbookWriter writer = new DataTypeValidationWorkbookWriter();
            try (writer) {
                writer.write(output, app.validation(), alias, results, started, Instant.now());
            } finally {
                if (writer.published() != null) summary.published(writer.published());
            }
            LOG.info("Validation report published: {}", errors.clean(output.toString()));
        } catch (IOException | RuntimeException e) {
            summary.fatal(errors.describe(e));
            LOG.error("Validation output failed: {}", errors.describe(e));
        }
    }

    private static boolean usable(Connection connection, Throwable e) {
        if (com.example.db2toolkit.jdbc.ConnectionFailureClassifier.connectionFailure(e) != null)
            return false;
        return !(e instanceof SQLException sql)
                || !com.example.db2toolkit.jdbc.ConnectionFailureClassifier.connectionLost(
                        connection, sql);
    }

    private static long elapsed(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationSummary.java =====
UTF8-BYTES: 3994
SHA256: 5c9b76342428de07f0e178a910a909201aa92d062b9b4de3a11886608c3a25cd
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import com.example.db2toolkit.jdbc.SafeDiagnostics;
import com.example.db2toolkit.output.RunLogs;

import org.slf4j.*;

import java.nio.file.Path;
import java.util.*;

public final class DataTypeValidationSummary {
    private static final Logger LOG = LoggerFactory.getLogger(DataTypeValidationSummary.class);
    private List<DataTypeValidationResult> results = List.of();
    private final List<String> failures = new ArrayList<>();
    private Path published;
    private long queryFailures;

    public void queryFailed() {
        queryFailures++;
    }

    public long queryFailures() {
        return queryFailures;
    }

    public List<DataTypeValidationResult> results() {
        return results;
    }

    public Path published() {
        return published;
    }

    public void results(List<DataTypeValidationResult> values) {
        results = List.copyOf(values);
    }

    public void published(Path value) {
        published = value;
    }

    public void fatal(String reason) {
        failures.add(reason);
    }

    public boolean fatal() {
        return !failures.isEmpty();
    }

    public int exitCode() {
        if (fatal()) return 2;
        return results.stream().anyMatch(r -> !r.successful())
                        || !results.isEmpty() && published == null
                ? 1
                : 0;
    }

    public Map<String, Long> rangeCounts() {
        Map<String, Long> counts = new TreeMap<>();
        results.forEach(r -> counts.merge(r.range.toString(), 1L, Long::sum));
        return counts;
    }

    public Map<String, Long> compareCounts() {
        Map<String, Long> counts = new TreeMap<>();
        results.stream()
                .filter(r -> r.compare != CompareStatus.NOT_COMPARED)
                .forEach(r -> counts.merge(r.compare.toString(), 1L, Long::sum));
        return counts;
    }

    public void log(SafeDiagnostics errors) {
        for (var r : results) {
            String message =
                    errors.clean(
                            "Validation "
                                    + r.item.logical().sqlName()
                                    + "."
                                    + r.item.field()
                                    + " physical="
                                    + r.item.physical().sqlName()
                                    + " original="
                                    + r.item.original()
                                    + " actual="
                                    + r.actualType
                                    + " Range="
                                    + r.range
                                    + " Compare="
                                    + r.compare
                                    + " elapsedMs="
                                    + r.elapsedMs
                                    + " reason="
                                    + r.error);
            if (r.successful() && published != null) LOG.info(RunLogs.SUCCESS, "{}", message);
            else LOG.warn(com.example.db2toolkit.output.RunLogs.SUMMARY, "{}", message);
        }
        failures.forEach(
                reason ->
                        LOG.error(
                                com.example.db2toolkit.output.RunLogs.SUMMARY,
                                "Validation output/fatal failure: {}",
                                errors.clean(reason)));
        LOG.info(
                com.example.db2toolkit.output.RunLogs.SUMMARY,
                "Validation summary: items={} Range={} Compare={} queryFailures={}"
                    + " reportPublished={} exitCode={}",
                results.size(),
                rangeCounts(),
                compareCounts(),
                queryFailures,
                published != null,
                exitCode());
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationWorkbookFormat.java =====
UTF8-BYTES: 2274
SHA256: 95f19695a17309bb2a67c2c079d9a54d93b09c71fc9cca13259e8f6d112bcf9c
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import java.util.List;

public final class DataTypeValidationWorkbookFormat {
    private DataTypeValidationWorkbookFormat() {}

    public static final List<String> MAIN =
            List.of(
                    "Table Name",
                    "Field Name",
                    "Original Data Type",
                    "After Data Type",
                    "Record Count",
                    "Overflow Count",
                    "Detail",
                    "Range Status",
                    "Compare Status",
                    "Baseline Only Length",
                    "Current Only Length");
    public static final List<String> META = List.of("key", "value");
    public static final List<String> ITEMS =
            List.of(
                    "databaseId",
                    "logicalSchema",
                    "logicalObject",
                    "field",
                    "originalDataType",
                    "afterDataType",
                    "lengthUnit",
                    "physicalSchema",
                    "physicalObject",
                    "actualType",
                    "rangeStatus",
                    "aggregationComplete",
                    "recordCount",
                    "overflowCount",
                    "bucketCount",
                    "error",
                    "min",
                    "max",
                    "compareStatus",
                    "baselineOnlyCount",
                    "currentOnlyCount");
    public static final List<String> LENGTHS =
            List.of(
                    "databaseId",
                    "logicalSchema",
                    "logicalObject",
                    "field",
                    "isNull",
                    "length",
                    "count");
    public static final List<String> DIFFS =
            List.of(
                    "databaseId",
                    "logicalSchema",
                    "logicalObject",
                    "field",
                    "side",
                    "isNull",
                    "length");
    public static final String HINT = "HINT = SINGLE LENGTH MATCH (LIKELY OK)";
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationWorkbookWriter.java =====
UTF8-BYTES: 15827
SHA256: 2475d964f3a0d3a53da4561d186837364e7e01a1a6ffdc7f4ebf779ff4c6e574
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import com.example.db2toolkit.output.TemporaryOutputFile;
import com.example.db2toolkit.spreadsheet.ExcelText;
import com.example.db2toolkit.spreadsheet.ExcelValueWriter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.io.*;
import java.math.*;
import java.nio.file.*;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

/** Streaming report and lossless hidden protocol; publication never replaces an existing report. */
public final class DataTypeValidationWorkbookWriter implements AutoCloseable {
    private final SXSSFWorkbook book =
            com.example.db2toolkit.spreadsheet.StreamingWorkbooks.streamingWorkbook();
    private final ExcelValueWriter values = new ExcelValueWriter(book);
    // Share styles across all rows; allocating a style per cell would exhaust workbook limits.
    private final CellStyle wrap = style(null, null, false),
            header = style("244062", "FFFFFF", true),
            pass = style("E2F0D9", "375623", false),
            issue = style("FCE4D6", "9C0006", false),
            difference = style("FFF2CC", "7F6000", false),
            inactive = style("F2F2F2", "666666", false),
            hint = style("DDEBF7", "1F4E78", false);
    private final int sheetLimit;
    private final Map<String, Long> rowCounts = new LinkedHashMap<>();
    private Path published;

    public DataTypeValidationWorkbookWriter() {
        this(1048575);
    }

    DataTypeValidationWorkbookWriter(int sheetLimit) {
        this.sheetLimit = sheetLimit;
    }

    public Path published() {
        return published;
    }

    private CellStyle style(String background, String foreground, boolean bold) {
        var style = (XSSFCellStyle) book.createCellStyle();
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        if (background != null) {
            style.setFillForegroundColor(new XSSFColor(HexFormat.of().parseHex(background), null));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (foreground != null) {
            var font = (XSSFFont) book.createFont();
            font.setColor(new XSSFColor(HexFormat.of().parseHex(foreground), null));
            font.setBold(bold);
            style.setFont(font);
        }
        return style;
    }

    private static void text(Cell cell, Object value) throws IOException {
        String s = value == null ? "" : value.toString();
        ExcelText.validate(s);
        cell.setCellValue(s);
    }

    private static void row(Sheet sheet, int index, List<?> fields) throws IOException {
        Row row = sheet.createRow(index);
        // Protocol fields are text, including counts, so Excel cannot round large integers.
        for (int i = 0; i < fields.size(); i++) text(row.createCell(i), fields.get(i));
    }

    private Sheet sheet(String name, List<String> header, boolean hidden) throws IOException {
        Sheet sheet = book.createSheet(name);
        row(sheet, 0, header);
        if (hidden) book.setSheetHidden(book.getSheetIndex(sheet), true);
        return sheet;
    }

    public void write(
            Path output,
            DataTypeValidationConfig config,
            String alias,
            List<DataTypeValidationResult> results,
            Instant started,
            Instant ended)
            throws IOException {
        Sheet main = sheet("Validation", DataTypeValidationWorkbookFormat.MAIN, false);
        main.createFreezePane(0, 1);
        for (Cell cell : main.getRow(0)) cell.setCellStyle(header);
        for (int i = 0; i < 11; i++) main.setColumnWidth(i, (i == 6 ? 80 : i == 0 ? 32 : 24) * 256);
        comment(
                main,
                7,
                "Range PASS: no value exceeds the ORIGINAL range; not proof of a successful type"
                    + " change. Range FAIL is not proof of widening failure.");
        comment(
                main,
                8,
                "Compare PASS: complete distinct length sets, including NULL, match. Counts and row"
                    + " contents are not compared.");
        comment(
                main,
                6,
                "LIKELY OK is an experience-based hint: one identical non-NULL length and"
                    + " Range/Compare PASS. It is not a statistical probability or proof of"
                    + " identical records or a successful type change.");
        Sheet meta = sheet("_meta", DataTypeValidationWorkbookFormat.META, true);
        Sheet items = sheet("_items", DataTypeValidationWorkbookFormat.ITEMS, true);
        Rolling lengths = new Rolling("_lengths_", DataTypeValidationWorkbookFormat.LENGTHS);
        Rolling diffs = new Rolling("_diffs_", DataTypeValidationWorkbookFormat.DIFFS);
        int index = 0;
        for (DataTypeValidationResult r : results) {
            writeMain(main, ++index, r);
            var i = r.item;
            row(
                    items,
                    index,
                    Arrays.asList(
                            config.databaseId(),
                            i.logical().schema(),
                            i.logical().name(),
                            i.field(),
                            i.original(),
                            i.target(),
                            i.unit(),
                            i.physical().schema(),
                            i.physical().name(),
                            r.actualType,
                            r.range,
                            r.aggregationComplete(),
                            r.records,
                            r.overflow,
                            r.buckets == null ? 0 : r.buckets.size(),
                            bounded(r.error, 32000),
                            plain(r.min),
                            plain(r.max),
                            r.compare,
                            r.baselineOnly == null ? null : r.baselineOnly.size(),
                            r.currentOnly == null ? null : r.currentOnly.size()));
            if (r.buckets != null)
                try (var cursor = r.buckets.cursor()) {
                    DiskLengthBuckets.Bucket bucket;
                    while ((bucket = cursor.next()) != null)
                        lengths.add(
                                Arrays.asList(
                                        config.databaseId(),
                                        i.logical().schema(),
                                        i.logical().name(),
                                        i.field(),
                                        bucket.length() == null,
                                        bucket.length(),
                                        bucket.count()));
                }
            writeDiff(diffs, config.databaseId(), r, "BASELINE_ONLY", r.baselineOnly);
            writeDiff(diffs, config.databaseId(), r, "CURRENT_ONLY", r.currentOnly);
        }
        lengths.finish();
        diffs.finish();
        rowCounts.put("_items", (long) results.size());
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("formatVersion", "1");
        metadata.put("mode", config.mode().name());
        metadata.put("databaseId", config.databaseId());
        metadata.put("executionAlias", alias);
        metadata.put("generatedAt", Instant.now().toString());
        metadata.put("startedAt", started.toString());
        metadata.put("endedAt", ended.toString());
        metadata.put("itemCount", Integer.toString(results.size()));
        metadata.put("lengthSheetCount", Integer.toString(lengths.number));
        metadata.put("diffSheetCount", Integer.toString(diffs.number));
        for (var entry : rowCounts.entrySet())
            metadata.put("rows." + entry.getKey(), entry.getValue().toString());
        int m = 0;
        for (var entry : metadata.entrySet())
            row(meta, ++m, List.of(entry.getKey(), entry.getValue()));
        try (var temp = TemporaryOutputFile.create(output.getParent(), "validation-")) {
            try (OutputStream stream = Files.newOutputStream(temp.path())) {
                book.write(stream);
            }
            // Publish only a complete workbook and refuse to replace an existing report, including
            // one created after this run selected its output path.
            Files.move(temp.path(), output);
            published = output;
        }
    }

    private void writeMain(Sheet sheet, int index, DataTypeValidationResult r) throws IOException {
        var i = r.item;
        row(
                sheet,
                index,
                Arrays.asList(
                        i.logical().schema() + "." + i.logical().name(),
                        i.field(),
                        i.original(),
                        i.target(),
                        "",
                        "",
                        detail(r),
                        r.range,
                        r.compare,
                        preview(r.baselineOnly),
                        preview(r.currentOnly)));
        Row row = sheet.getRow(index);
        row.setHeightInPoints(r.singleLengthMatchHint ? 60 : 45);
        for (Cell cell : row) cell.setCellStyle(wrap);
        try {
            values.write(row.getCell(4), r.records);
            values.write(row.getCell(5), r.overflow);
        } catch (SQLException e) {
            throw new IOException("Cannot write validation count", e);
        }
        if (r.overflow != null && r.overflow.signum() > 0) row.getCell(5).setCellStyle(issue);
        row.getCell(7).setCellStyle(
                r.range == DataTypeValidationResult.RangeStatus.PASS ? pass : issue);
        row.getCell(8).setCellStyle(switch (r.compare) {
            case PASS -> pass;
            case DIFFERENT -> difference;
            case BASELINE_NOT_FOUND, BASELINE_INVALID -> issue;
            case NOT_APPLICABLE -> inactive;
            case NOT_COMPARED -> wrap;
        });
        if (r.baselineOnly != null && r.baselineOnly.size() > 0) row.getCell(9).setCellStyle(difference);
        if (r.currentOnly != null && r.currentOnly.size() > 0) row.getCell(10).setCellStyle(difference);
        if (r.singleLengthMatchHint) row.getCell(6).setCellStyle(hint);
    }

    private static String plain(BigDecimal value) {
        return value == null ? "" : value.toPlainString();
    }

    private static String bounded(String value, int limit) {
        if (value.length() <= limit) return value;
        int end = Character.isHighSurrogate(value.charAt(limit - 1)) ? limit - 1 : limit;
        return value.substring(0, end) + "...";
    }

    private static String detail(DataTypeValidationResult r) throws IOException {
        if (!r.aggregationComplete()) return "";
        if (r.item.original().decimal())
            return "MIN = "
                    + (r.min == null ? "NULL" : plain(r.min))
                    + ", MAX = "
                    + (r.max == null ? "NULL" : plain(r.max));
        var lengths = new StringBuilder("DISTINCT LENGTH = (");
        var counts = new StringBuilder("DISTINCT COUNT  = (");
        boolean truncated = false, first = true;
        try (var cursor = r.buckets.cursor()) {
            DiskLengthBuckets.Bucket b;
            while ((b = cursor.next()) != null) {
                String length = b.length() == null ? "NULL" : b.length().toString(),
                        count = b.count().toString();
                if (lengths.length() + counts.length() + length.length() + count.length() > 32000) {
                    truncated = true;
                    break;
                }
                if (!first) {
                    lengths.append(", ");
                    counts.append(", ");
                }
                lengths.append(length);
                counts.append(count);
                first = false;
            }
        }
        return lengths.append(")\n").append(counts).append(')').toString()
                + (truncated ? "\n[TRUNCATED PREVIEW; full distribution: _lengths_*]" : "")
                + (r.singleLengthMatchHint ? "\n" + DataTypeValidationWorkbookFormat.HINT : "");
    }

    private static String preview(DiskLengthBuckets buckets) throws IOException {
        if (buckets == null || buckets.size() == 0) return "";
        var out = new StringBuilder("(");
        try (var cursor = buckets.cursor()) {
            DiskLengthBuckets.Bucket b;
            while ((b = cursor.next()) != null) {
                String value = b.length() == null ? "NULL" : b.length().toString();
                if (out.length() + value.length() > 32000)
                    return out + ") [TRUNCATED PREVIEW; full differences: _diffs_*]";
                if (out.length() > 1) out.append(", ");
                out.append(value);
            }
        }
        return out.append(')').toString();
    }

    private static void writeDiff(
            Rolling diffs, String id, DataTypeValidationResult r, String side, DiskLengthBuckets buckets)
            throws IOException {
        if (buckets == null) return;
        try (var cursor = buckets.cursor()) {
            DiskLengthBuckets.Bucket b;
            while ((b = cursor.next()) != null)
                diffs.add(
                        Arrays.asList(
                                id,
                                r.item.logical().schema(),
                                r.item.logical().name(),
                                r.item.field(),
                                side,
                                b.length() == null,
                                b.length()));
        }
    }

    private void comment(Sheet sheet, int column, String value) {
        var helper = book.getCreationHelper();
        var anchor = helper.createClientAnchor();
        anchor.setCol1(column);
        anchor.setCol2(column + 3);
        anchor.setRow1(0);
        anchor.setRow2(5);
        Comment comment = sheet.createDrawingPatriarch().createCellComment(anchor);
        comment.setString(helper.createRichTextString(value));
        comment.setAuthor("DB2 validation");
        sheet.getRow(0).getCell(column).setCellComment(comment);
    }

    private final class Rolling {
        final String prefix;
        final List<String> header;
        Sheet current;
        int count, number;

        Rolling(String prefix, List<String> header) {
            this.prefix = prefix;
            this.header = header;
        }

        void add(List<?> fields) throws IOException {
            if (current == null || count == sheetLimit) {
                finish();
                current =
                        sheet(prefix + String.format(Locale.ROOT, "%03d", ++number), header, true);
                count = 0;
            }
            row(current, ++count, fields);
        }

        void finish() throws IOException {
            if (current != null) {
                ((org.apache.poi.xssf.streaming.SXSSFSheet) current).flushRows();
                rowCounts.put(current.getSheetName(), (long) count);
            }
        }
    }

    @Override
    public void close() throws IOException {
        book.close();
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationWorkspace.java =====
UTF8-BYTES: 1453
SHA256: d1a05cdf6e007ed85a79e722b46871a76270836c407108d95ce31822390f45b4
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Owns only files created by this run; no recursive deletion or input-file ownership. */
public final class DataTypeValidationWorkspace implements AutoCloseable {
    private final Path directory;
    private final List<Path> files = new ArrayList<>();
    private boolean closed;

    public DataTypeValidationWorkspace() throws IOException {
        directory = Files.createTempDirectory("db2-validation-");
    }

    Path file() throws IOException {
        if (closed) throw new IOException("Validation workspace is closed");
        Path file = Files.createTempFile(directory, "buckets-", ".txt");
        files.add(file);
        return file;
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        IOException failure = null;
        for (Path file : files)
            try {
                Files.deleteIfExists(file);
            } catch (IOException e) {
                if (failure == null) failure = e;
                else failure.addSuppressed(e);
            }
        try {
            Files.delete(directory);
        } catch (IOException e) {
            if (failure == null) failure = e;
            else failure.addSuppressed(e);
        }
        if (failure != null) throw failure;
        closed = true;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/Db2DataTypeQueries.java =====
UTF8-BYTES: 11141
SHA256: af45a0074b83455980cb127326b5bcfabc70bdf102c12e39bd6bb8b0b7944a55
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import com.example.db2toolkit.config.AppConfig;
import com.example.db2toolkit.jdbc.JdbcActivity;
import com.example.db2toolkit.jdbc.ReadOnlyQueries;
import com.example.db2toolkit.model.DbObjectRef;
import com.example.db2toolkit.model.Identifiers;

import java.io.IOException;
import java.math.*;
import java.sql.*;
import java.util.*;

final class Db2DataTypeQueries {
    record Column(String schema, String type, int length, int scale, int codepage) {
        // Compatibility means the original range rule can query this column safely; it does not
        // assert that the database column has already been changed to the requested target type.
        boolean compatible(DataTypeSpec original) {
            if (!schema.equals("SYSIBM")) return false;
            if (original.decimal())
                return Set.of("SMALLINT", "INTEGER", "BIGINT").contains(type)
                        || Set.of("DECIMAL", "NUMERIC").contains(type) && scale == 0;
            return Set.of("VARCHAR", "CLOB").contains(type) && codepage != 0;
        }

        String description() {
            return schema
                    + "."
                    + type
                    + " length="
                    + length
                    + " scale="
                    + scale
                    + " codepage="
                    + codepage;
        }
    }

    record Metadata(boolean exists, Map<String, Column> columns) {}

    private final Connection connection;
    private final AppConfig config;

    Db2DataTypeQueries(Connection connection, AppConfig config) {
        this.connection = connection;
        this.config = config;
    }

    private PreparedStatement statement(String sql) throws SQLException {
        PreparedStatement statement = ReadOnlyQueries.prepare(connection, sql);
        try {
            if (config.validation().queryTimeoutSeconds() > 0)
                statement.setQueryTimeout(config.validation().queryTimeoutSeconds());
            return statement;
        } catch (SQLException | RuntimeException e) {
            try {
                statement.close();
            } catch (SQLException close) {
                e.addSuppressed(close);
            }
            throw e;
        }
    }

    Metadata metadata(DbObjectRef object) throws SQLException {
        try (var activity = JdbcActivity.query("OBJECT_METADATA " + object.sqlName(), config.validation().queryTimeoutSeconds());
                var statement =
                statement(
                        "SELECT TYPE FROM "
                                + config.catalog().tableView()
                                + " WHERE TABSCHEMA = ? AND TABNAME = ?")) {
            ReadOnlyQueries.bindString(statement, 1, object.schema());
            ReadOnlyQueries.bindString(statement, 2, object.name());
            try (var rows = activity.execute(statement)) {
                if (!rows.next()) return new Metadata(false, Map.of());
                String type = required(rows, "TYPE");
                if (rows.next())
                    throw new SQLException("Duplicate object in configured catalog", "HY000");
                if (!(config.validation().itsview() ? Set.of("V", "W") : Set.of("T", "V", "W"))
                        .contains(type))
                    throw new SQLException(
                            "Unsupported object TYPE in configured catalog; expected "
                                    + (config.validation().itsview() ? "view" : "table/view"),
                            "HY000");
            }
        }
        Map<String, Column> columns = new HashMap<>();
        try (var activity = JdbcActivity.query("COLUMN_METADATA " + object.sqlName(), config.validation().queryTimeoutSeconds());
                var statement =
                statement(
                        "SELECT COLNAME, TYPESCHEMA, TYPENAME, LENGTH, SCALE, CODEPAGE FROM "
                                + config.catalog().columnView()
                                + " WHERE TABSCHEMA = ? AND TABNAME = ?")) {
            ReadOnlyQueries.bindString(statement, 1, object.schema());
            ReadOnlyQueries.bindString(statement, 2, object.name());
            try (var rows = activity.execute(statement)) {
                while (rows.next()) {
                    // DB2 exposes TYPESCHEMA as blank-padded CHAR; preserve significant leading
                    // whitespace.
                    var column =
                            new Column(
                                    requiredIdentifier(rows, "TYPESCHEMA").stripTrailing(),
                                    required(rows, "TYPENAME"),
                                    integer(rows, "LENGTH").intValueExact(),
                                    integer(rows, "SCALE").intValueExact(),
                                    integer(rows, "CODEPAGE").intValueExact());
                    if (columns.putIfAbsent(requiredIdentifier(rows, "COLNAME"), column) != null)
                        throw new SQLException("Duplicate column in configured catalog", "HY000");
                }
            }
        }
        return new Metadata(true, Map.copyOf(columns));
    }

    private static String required(ResultSet rows, String column) throws SQLException {
        return requiredIdentifier(rows, column).strip();
    }

    private static String requiredIdentifier(ResultSet rows, String column) throws SQLException {
        String value = rows.getString(column);
        // Catalog identifiers may name unrelated delimited columns/types. Whitespace is part of
        // their identity.
        if (value == null || value.isBlank())
            throw new SQLException("Missing required catalog value: " + column, "HY000");
        return value;
    }

    static BigInteger integer(ResultSet rows, String label) throws SQLException {
        BigDecimal value = rows.getBigDecimal(label);
        if (value == null)
            throw new SQLException("Unexpected NULL aggregate/catalog value: " + label, "HY000");
        try {
            BigInteger number = value.toBigIntegerExact();
            if (number.signum() < 0) throw new ArithmeticException();
            return number;
        } catch (ArithmeticException e) {
            throw new SQLException("Invalid integer aggregate/catalog value: " + label, "HY000", e);
        }
    }

    record NumericAggregate(
            BigInteger records, BigInteger overflow, BigDecimal min, BigDecimal max) {}

    record LengthAggregate(BigInteger records, BigInteger overflow, DiskLengthBuckets buckets) {}

    NumericAggregate decimal(DataTypeValidationItem item, BigInteger sharedTotal) throws SQLException {
        String column = Identifiers.quote(item.field());
        // Only the first successful aggregate needs a row count. Later numeric columns reuse it;
        // they still require their own scan for overflow and min/max values.
        String sql =
                "SELECT "
                        + (sharedTotal == null ? "COUNT_BIG(*) AS RECORD_COUNT, " : "")
                        + "COALESCE(SUM(CAST(CASE WHEN "
                        + column
                        + " < CAST(? AS DECIMAL(31,0)) OR "
                        + column
                        + " > CAST(? AS DECIMAL(31,0)) THEN 1 ELSE 0 END AS DECIMAL(31,0))), CAST(0"
                        + " AS DECIMAL(31,0))) AS OVERFLOW_COUNT, MIN("
                        + column
                        + ") AS MIN_VALUE, MAX("
                        + column
                        + ") AS MAX_VALUE FROM "
                        + item.physical().sqlName();
        try (var activity = JdbcActivity.query("NUMERIC_RANGE " + item.physical().sqlName() + "." + item.field(), config.validation().queryTimeoutSeconds());
                var statement = statement(sql)) {
            BigDecimal bound = new BigDecimal(item.original().upperBound());
            ReadOnlyQueries.bindDecimal(statement, 1, bound.negate());
            ReadOnlyQueries.bindDecimal(statement, 2, bound);
            try (var rows = activity.execute(statement)) {
                if (!rows.next()) throw new SQLException("Aggregate returned no row", "HY000");
                BigInteger records =
                        sharedTotal == null ? integer(rows, "RECORD_COUNT") : sharedTotal;
                BigInteger overflow = integer(rows, "OVERFLOW_COUNT");
                BigDecimal min = rows.getBigDecimal("MIN_VALUE");
                BigDecimal max = rows.getBigDecimal("MAX_VALUE");
                if (rows.next()
                        || (min == null) != (max == null)
                        || min != null && min.compareTo(max) > 0)
                    throw new SQLException("Invalid numeric aggregate result", "HY000");
                return new NumericAggregate(records, overflow, min, max);
            }
        }
    }

    LengthAggregate lengths(DataTypeValidationItem item, DataTypeValidationWorkspace workspace)
            throws SQLException, IOException {
        String length =
                "LENGTH(" + Identifiers.quote(item.field()) + ", " + item.unit().name() + ")";
        // COUNT_BIG(*) includes NULL rows in their own bucket. Summing all buckets supplies the
        // object row count without an extra SELECT COUNT, and distinguishes empty from all-NULL.
        String sql =
                "SELECT "
                        + length
                        + " AS LENGTH_VALUE, COUNT_BIG(*) AS BUCKET_COUNT FROM "
                        + item.physical().sqlName()
                        + " GROUP BY "
                        + length
                        + " ORDER BY CASE WHEN "
                        + length
                        + " IS NULL THEN 0 ELSE 1 END, "
                        + length;
        DiskLengthBuckets buckets = new DiskLengthBuckets(workspace);
        BigInteger overflow = BigInteger.ZERO;
        try (var activity = JdbcActivity.query("LENGTH_DISTRIBUTION " + item.physical().sqlName() + "." + item.field(), config.validation().queryTimeoutSeconds());
                var output = buckets.appender();
                var statement = statement(sql);
                var rows = activity.execute(statement)) {
            while (rows.next()) {
                BigDecimal raw = rows.getBigDecimal("LENGTH_VALUE");
                Long value = raw == null ? null : raw.longValueExact();
                BigInteger count = integer(rows, "BUCKET_COUNT");
                output.add(value, count);
                if (value != null && value > item.original().size()) overflow = overflow.add(count);
            }
        } catch (SQLException | RuntimeException e) {
            // A failed disk flush may be suppressed behind the SQL failure; it is still an output
            // failure.
            for (Throwable suppressed : e.getSuppressed())
                if (suppressed instanceof IOException)
                    throw new IOException("Length staging cleanup failed", e);
            throw e;
        }
        return new LengthAggregate(buckets.total(), overflow, buckets);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/DiskLengthBuckets.java =====
UTF8-BYTES: 3289
SHA256: 2d6188179e129f46d4e54dd13b2c7d4d3ed4d23c110e4aabbc06e73f9acc351e
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/** Ordered NULL-first length/count stream. Neither comparison nor rendering retains all buckets. */
public final class DiskLengthBuckets {
    public record Bucket(Long length, BigInteger count) {}

    private final Path file;
    private long size;
    private Long last;
    private BigInteger total = BigInteger.ZERO;

    public DiskLengthBuckets(DataTypeValidationWorkspace workspace) throws IOException {
        file = workspace.file();
    }

    public long size() {
        return size;
    }

    public BigInteger total() {
        return total;
    }

    public Appender appender() throws IOException {
        return new Appender();
    }

    public Cursor cursor() throws IOException {
        return new Cursor();
    }

    public static int compare(Long a, Long b) {
        if (a == null) return b == null ? 0 : -1;
        return b == null ? 1 : a.compareTo(b);
    }

    public final class Appender implements AutoCloseable {
        private final BufferedWriter writer;

        private Appender() throws IOException {
            writer =
                    Files.newBufferedWriter(
                            file, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        }

        public void add(Long length, BigInteger count) throws IOException {
            // Enforce the ordering contract here for both live SQL and imported baselines;
            // streaming set comparison cannot repair unsorted or duplicate input later.
            if (count == null
                    || count.signum() <= 0
                    || length != null && length < 0
                    || size > 0 && compare(last, length) >= 0)
                throw new IOException("Invalid, duplicate or out-of-order length bucket");
            writer.write((length == null ? "NULL" : length.toString()) + "\t" + count + "\n");
            last = length;
            size++;
            total = total.add(count);
        }

        @Override
        public void close() throws IOException {
            writer.close();
        }
    }

    public final class Cursor implements AutoCloseable {
        private final BufferedReader reader;

        private Cursor() throws IOException {
            reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
        }

        /** Returns null at EOF; a non-null bucket with length() == null represents SQL NULL. */
        public Bucket next() throws IOException {
            String line = reader.readLine();
            if (line == null) return null;
            int tab = line.indexOf('\t');
            try {
                return new Bucket(
                        line.startsWith("NULL\t") ? null : Long.valueOf(line.substring(0, tab)),
                        new BigInteger(line.substring(tab + 1)));
            } catch (RuntimeException e) {
                throw new IOException("Invalid staged length bucket", e);
            }
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/DiskSharedStrings.java =====
UTF8-BYTES: 7233
SHA256: baa58f0338b5fd221a56cd574eb656efbb2747fe1da8f12b213261b6ffcbe5f4
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import org.apache.poi.util.XMLHelper;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;

/**
 * Disk index for shared strings in Excel-resaved baselines; no workbook-sized string table in heap.
 */
final class DiskSharedStrings implements AutoCloseable {
    private final RandomAccessFile data, offsets;
    // Bound both entry overhead and text payload; large unique string tables stay on disk.
    private static final int CACHE_ENTRIES = 1024, CACHE_CHARACTERS = 262144;
    private final LinkedHashMap<Long, String> cache = new LinkedHashMap<>(128, 0.75f, true);
    private final ByteBuffer header = ByteBuffer.allocate(Long.BYTES);
    private int cachedCharacters;
    private long dataBytes;
    private long count;

    private DiskSharedStrings(DataTypeValidationWorkspace workspace) throws IOException {
        data = new RandomAccessFile(workspace.file().toFile(), "rw");
        try {
            offsets = new RandomAccessFile(workspace.file().toFile(), "rw");
        } catch (IOException e) {
            try {
                data.close();
            } catch (IOException close) {
                e.addSuppressed(close);
            }
            throw e;
        }
    }

    static DiskSharedStrings read(InputStream input, DataTypeValidationWorkspace workspace)
            throws Exception {
        DiskSharedStrings strings;
        try {
            strings = new DiskSharedStrings(workspace);
        } catch (IOException | RuntimeException e) {
            // Ownership begins at entry, including failures before the normal resource scope.
            if (input != null) {
                try {
                    input.close();
                } catch (IOException close) {
                    e.addSuppressed(close);
                }
            }
            throw e;
        }
        try (input;
                var dataOutput = buffered(strings.data);
                var offsetOutput = buffered(strings.offsets)) {
            if (input != null) {
                var parser = XMLHelper.newXMLReader();
                parser.setContentHandler(
                        new DefaultHandler() {
                            final StringBuilder value = new StringBuilder();
                            boolean capture;
                            int phonetic;

                            @Override
                            public void startElement(
                                    String uri, String local, String qName, Attributes attributes) {
                                if (local.equals("si")) value.setLength(0);
                                if (local.equals("rPh")) phonetic++;
                                if (local.equals("t") && phonetic == 0) capture = true;
                            }

                            @Override
                            public void characters(char[] ch, int start, int length)
                                    throws SAXException {
                                if (capture) {
                                    value.append(ch, start, length);
                                    if (value.length() > 240000)
                                        throw new SAXException("Oversized shared string");
                                }
                            }

                            @Override
                            public void endElement(String uri, String local, String qName)
                                    throws SAXException {
                                if (local.equals("t")) capture = false;
                                if (local.equals("rPh")) phonetic--;
                                if (local.equals("si"))
                                    try {
                                        strings.add(
                                                SpreadsheetMlTextCodec.unescape(value.toString()),
                                                dataOutput, offsetOutput);
                                    } catch (IOException e) {
                                        throw new SAXException(e);
                                    }
                            }
                        });
                parser.parse(new InputSource(input));
            }
            return strings;
        } catch (Exception e) {
            try {
                strings.close();
            } catch (IOException close) {
                e.addSuppressed(close);
            }
            throw e;
        }
    }

    private static DataOutputStream buffered(RandomAccessFile file) {
        // Closing this adapter flushes buffers, but the owning index keeps its random-access file.
        return new DataOutputStream(new BufferedOutputStream(new OutputStream() {
            @Override public void write(int value) throws IOException { file.write(value); }
            @Override public void write(byte[] bytes, int offset, int length) throws IOException {
                file.write(bytes, offset, length);
            }
        }));
    }

    private void add(String value, DataOutputStream dataOutput, DataOutputStream offsetOutput) throws IOException {
        com.example.db2toolkit.spreadsheet.ExcelText.validate(value);
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        offsetOutput.writeLong(dataBytes);
        dataOutput.writeInt(bytes.length);
        dataOutput.write(bytes);
        dataBytes += Integer.BYTES + bytes.length;
        count++;
    }

    String get(long index) throws IOException {
        if (index < 0 || index >= count) throw new IOException("Invalid shared string index");
        String cached = cache.get(index);
        if (cached != null) return cached;
        offsets.seek(index * 8);
        offsets.readFully(header.array());
        data.seek(header.getLong(0));
        data.readFully(header.array(), 0, Integer.BYTES);
        int length = header.getInt(0);
        if (length < 0 || length > 32767 * 4) throw new IOException("Invalid staged shared string length");
        byte[] bytes = new byte[length];
        data.readFully(bytes);
        String value = new String(bytes, StandardCharsets.UTF_8);
        cache.put(index, value);
        cachedCharacters += value.length();
        while (cache.size() > CACHE_ENTRIES || cachedCharacters > CACHE_CHARACTERS) {
            var oldest = cache.entrySet().iterator();
            cachedCharacters -= oldest.next().getValue().length();
            oldest.remove();
        }
        return value;
    }

    @Override
    public void close() throws IOException {
        cache.clear();
        cachedCharacters = 0;
        IOException error = null;
        try {
            data.close();
        } catch (IOException e) {
            error = e;
        }
        try {
            offsets.close();
        } catch (IOException e) {
            if (error == null) error = e;
            else error.addSuppressed(e);
        }
        if (error != null) throw error;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/LengthSetComparator.java =====
UTF8-BYTES: 3056
SHA256: d8ded5c1fae547c0440012c640dcc5e6b13b9871e15bc7f68bdc20beaeda4843
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import java.io.IOException;
import java.math.BigInteger;

/** Compares complete NULL-aware length sets without retaining them in memory. */
final class LengthSetComparator {
    private LengthSetComparator() {}

    static void compare(DataTypeValidationResult r, DataTypeValidationConfig config, DataTypeValidationPlan plan)
            throws IOException {
        if (r.item.original().decimal() || !config.comparing()) {
            r.compare = CompareStatus.NOT_APPLICABLE;
            return;
        }
        var baseline = plan.baseline.items().get(r.item.key());
        if (baseline == null) {
            r.compare = CompareStatus.BASELINE_NOT_FOUND;
            return;
        }
        if (!baseline.aggregationComplete()) {
            r.compare = CompareStatus.BASELINE_INVALID;
            return;
        }
        r.baselineOnly = new DiskLengthBuckets(plan.workspace);
        r.currentOnly = new DiskLengthBuckets(plan.workspace);
        // Both streams are sorted and unique, so a merge needs only one bucket from each side.
        // Compare length membership only: differing bucket counts do not make the sets different.
        try (var base = baseline.buckets().cursor();
                var current = r.buckets.cursor();
                var baseOnly = r.baselineOnly.appender();
                var currentOnly = r.currentOnly.appender()) {
            var a = base.next();
            var b = current.next();
            while (a != null || b != null) {
                int cmp =
                        a == null
                                ? 1
                                : b == null
                                        ? -1
                                        : DiskLengthBuckets.compare(a.length(), b.length());
                if (cmp < 0) {
                    baseOnly.add(a.length(), BigInteger.ONE);
                    a = base.next();
                } else if (cmp > 0) {
                    currentOnly.add(b.length(), BigInteger.ONE);
                    b = current.next();
                } else {
                    a = base.next();
                    b = current.next();
                }
            }
        }
        r.compare =
                r.baselineOnly.size() == 0 && r.currentOnly.size() == 0
                        ? CompareStatus.PASS
                        : CompareStatus.DIFFERENT;
        // This hint describes one shared non-NULL length, not equal values or a verified DDL
        // change. Length zero qualifies; an empty set or a set containing NULL does not.
        if (r.item.original().kind() == DataTypeSpec.Kind.VARCHAR
                && r.range == DataTypeValidationResult.RangeStatus.PASS
                && r.compare == CompareStatus.PASS
                && r.buckets.size() == 1) {
            try (var cursor = r.buckets.cursor()) {
                r.singleLengthMatchHint = cursor.next().length() != null;
            }
        }
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/datatypevalidation/SpreadsheetMlTextCodec.java =====
UTF8-BYTES: 634
SHA256: 7953792eb854df9a80cc4a85cbf64a0c77e21a586678838c3b73ea37e703f909
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import java.util.regex.Pattern;

final class SpreadsheetMlTextCodec {
    private SpreadsheetMlTextCodec() {}

    private static final Pattern ESCAPE = Pattern.compile("_x([0-9A-Fa-f]{4})_");

    static String unescape(String text) {
        return ESCAPE.matcher(text)
                .replaceAll(
                        m ->
                                java.util.regex.Matcher.quoteReplacement(
                                        Character.toString(
                                                (char) Integer.parseInt(m.group(1), 16))));
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/ddl/DdlExportService.java =====
UTF8-BYTES: 11307
SHA256: e03048c8f08c6ce69ee6eb1ff0a616a5e36fbedae5ce9a8b388c20d11c41b708
===== CONTENT =====
package com.example.db2toolkit.ddl;

import com.example.db2toolkit.catalog.RoutineDdlExtractor;
import com.example.db2toolkit.catalog.TableDdlExtractor;
import com.example.db2toolkit.config.AppConfig;
import com.example.db2toolkit.ddl.model.DdlExportRequest;
import com.example.db2toolkit.ddl.model.DdlExportResult;
import com.example.db2toolkit.ddl.model.DdlExportStatus;
import com.example.db2toolkit.ddl.model.DdlExportSummary;
import com.example.db2toolkit.ddl.model.DdlObjectType;
import com.example.db2toolkit.ddl.model.DdlUnavailableException;
import com.example.db2toolkit.jdbc.SafeDiagnostics;
import com.example.db2toolkit.output.SqlFileWriter;
import com.example.db2toolkit.sql.RoutineDefinitionValidator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/** Sequential export on a caller-owned connection. Never connects, reconnects or closes it. */
public final class DdlExportService {
    private static final org.slf4j.Logger LOG =
            org.slf4j.LoggerFactory.getLogger(DdlExportService.class);
    private final TableDdlExtractor tables = new TableDdlExtractor();
    private final RoutineDdlExtractor routines = new RoutineDdlExtractor();

    /**
     * @return false if this connection was confirmed unavailable and must not be queried again.
     */
    public boolean export(
            Connection connection,
            AppConfig config,
            List<DdlExportRequest> requests,
            SqlFileWriter writer,
            DdlExportSummary report) {
        SafeDiagnostics errors = new SafeDiagnostics(config);
        report.setRequestCount(requests.size());
        for (int index = 0; index < requests.size(); index++) {
            DdlExportRequest request = requests.get(index);
            long startedAt = System.nanoTime();
            LOG.info(
                    "Processing {}/{}: {} {}",
                    index + 1,
                    requests.size(),
                    request.type(),
                    errors.clean(request.ref().sqlName()));
            try {
                // One initial probe; successful catalog queries already confirm the connection is
                // alive.
                // SQL failures below still classify connection loss and stop remaining requests.
                if (connection.isClosed() || (index == 0 && !connection.isValid(5))) {
                    report.stop(
                            "Connection unavailable; remaining requests were not queried",
                            requests.subList(index, requests.size()),
                            false);
                    return false;
                }
                exportRequest(connection, config, request, writer, report, errors);
            } catch (DdlUnavailableException e) {
                record(
                        report,
                        errors,
                        result(request, null, DdlExportStatus.DDL_UNAVAILABLE, errors.describe(e)));
                if (stopAfterCleanupDisconnect(
                        e, report, requests.subList(index + 1, requests.size()))) return false;
            } catch (SQLException e) {
                // Cursor failures are separate from already completed routine instances.
                String specific = null;
                if (e instanceof RoutineDdlExtractor.DefinitionConnectionException definition) {
                    // The extractor read this instance's metadata but could not deliver its body.
                    // Record it once here; unknown cursor failures still have no instance identity.
                    specific = definition.specificName();
                    report.recordInstance();
                }
                record(
                        report,
                        errors,
                        result(request, specific, DdlExportStatus.FAILED, errors.sql(e)));
                if (com.example.db2toolkit.jdbc.ConnectionFailureClassifier.connectionLost(
                        connection, e)) {
                    report.stop(
                            "Connection lost; current request may be incomplete",
                            requests.subList(index + 1, requests.size()),
                            false);
                    return false;
                }
            } catch (IOException | RuntimeException e) {
                record(
                        report,
                        errors,
                        result(request, null, DdlExportStatus.FAILED, errors.describe(e)));
                if (stopAfterCleanupDisconnect(
                        e, report, requests.subList(index + 1, requests.size()))) return false;
            } finally {
                LOG.info(
                        "DDL request finished: {}/{} {} {} elapsedMs={}",
                        index + 1,
                        requests.size(),
                        request.type(),
                        errors.clean(request.ref().sqlName()),
                        (System.nanoTime() - startedAt) / 1_000_000);
            }
        }
        return true;
    }

    private static boolean stopAfterCleanupDisconnect(
            Exception error, DdlExportSummary report, List<DdlExportRequest> remaining) {
        if (com.example.db2toolkit.jdbc.ConnectionFailureClassifier.connectionFailure(error)
                == null) return false;
        report.stop(
                "Connection lost during failed request/cleanup; remaining requests were not"
                    + " queried",
                remaining,
                false);
        return true;
    }

    private void exportRequest(
            Connection connection,
            AppConfig config,
            DdlExportRequest request,
            SqlFileWriter writer,
            DdlExportSummary report,
            SafeDiagnostics errors)
            throws SQLException, IOException, DdlUnavailableException {
        if (request.type() == DdlObjectType.TABLE) {
            String ddl = tables.extract(connection, config, request.ref());
            if (ddl == null)
                record(
                        report,
                        errors,
                        result(request, null, DdlExportStatus.NOT_FOUND, "No matching table"));
            else write(writer, report, errors, request, null, ddl);
            return;
        }
        int before = report.matchedRoutineInstances();
        String view =
                request.type() == DdlObjectType.PROCEDURE
                        ? config.catalog().procedureView()
                        : config.catalog().functionView();
        routines.extract(
                connection,
                view,
                request.type(),
                request.ref(),
                (specific, ddl, error) ->
                        exportInstance(request, specific, ddl, error, writer, report, errors));
        if (report.matchedRoutineInstances() == before) {
            record(
                    report,
                    errors,
                    result(request, null, DdlExportStatus.NOT_FOUND, "No matching routine"));
        }
    }

    private void exportInstance(
            DdlExportRequest request,
            String specific,
            String ddl,
            Exception readError,
            SqlFileWriter writer,
            DdlExportSummary report,
            SafeDiagnostics errors) {
        report.recordInstance();
        if (readError != null) {
            String reason =
                    readError instanceof SQLException sql
                            ? errors.sql(sql)
                            : "Cannot read routine character stream: " + errors.describe(readError);
            record(report, errors, result(request, specific, DdlExportStatus.FAILED, reason));
        } else if (specific == null || specific.isBlank()) {
            record(
                    report,
                    errors,
                    result(
                            request,
                            specific,
                            DdlExportStatus.DDL_UNAVAILABLE,
                            "Missing SPECIFICNAME"));
        } else if (!RoutineDefinitionValidator.hasCreateDefinition(request.type(), ddl)) {
            record(
                    report,
                    errors,
                    result(
                            request,
                            specific,
                            DdlExportStatus.DDL_UNAVAILABLE,
                            "Definition is NULL/blank or lacks complete CREATE "
                                    + request.type()
                                    + " prefix; body-only definitions are unsupported"));
        } else {
            write(writer, report, errors, request, specific, ddl);
        }
    }

    private static void write(
            SqlFileWriter writer,
            DdlExportSummary report,
            SafeDiagnostics errors,
            DdlExportRequest request,
            String specific,
            String ddl) {
        try {
            var output = writer.write(request.type(), request.ref(), specific, ddl);
            record(
                    report,
                    errors,
                    DdlExportResult.of(
                            request.type(),
                            request.ref(),
                            specific,
                            DdlExportStatus.SUCCESS,
                            output,
                            null));
        } catch (IOException | RuntimeException e) {
            record(
                    report,
                    errors,
                    result(request, specific, DdlExportStatus.FAILED, errors.describe(e)));
        }
    }

    private static void record(
            DdlExportSummary report, SafeDiagnostics errors, DdlExportResult result) {
        report.add(result);
        String detail =
                errors.clean(
                        result.type()
                                + " "
                                + result.schema()
                                + "."
                                + result.objectName()
                                + (result.specificName() == null
                                        ? ""
                                        : " specific=" + result.specificName())
                                + " "
                                + result.status()
                                + " "
                                + (result.outputPath() == null
                                        ? result.reason()
                                        : result.outputPath()));
        if (result.status() == DdlExportStatus.SUCCESS) LOG.info("{}", detail);
        else LOG.warn("{}", detail);
    }

    private static DdlExportResult result(
            DdlExportRequest request, String specific, DdlExportStatus status, String reason) {
        return DdlExportResult.of(request.type(), request.ref(), specific, status, null, reason);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/ddl/DdlSummaryLogger.java =====
UTF8-BYTES: 5394
SHA256: 10679875d0071a2e8d7ecc0ed9a98a9db766c9ecfa16980b28b927c8e42a95a0
===== CONTENT =====
package com.example.db2toolkit.ddl;

import com.example.db2toolkit.ddl.model.DdlExportResult;
import com.example.db2toolkit.ddl.model.DdlExportStatus;
import com.example.db2toolkit.ddl.model.DdlExportSummary;
import com.example.db2toolkit.jdbc.SafeDiagnostics;
import com.example.db2toolkit.model.Identifiers;

import org.slf4j.*;

public final class DdlSummaryLogger {
    private static final Logger LOG = LoggerFactory.getLogger(DdlSummaryLogger.class);

    public void logInputWarnings(java.util.List<String> warnings) {
        for (String warning : warnings)
            LOG.warn(com.example.db2toolkit.output.RunLogs.SUMMARY, "{}", warning);
    }

    public void log(DdlExportSummary report, SafeDiagnostics errors) {
        LOG.info(
                com.example.db2toolkit.output.RunLogs.SUMMARY,
                "================ DDL Export Summary ================");
        LOG.info(
                com.example.db2toolkit.output.RunLogs.SUMMARY,
                "Requests(unique)={}, routine instances matched={}, invalid lines={},"
                        + " duplicates={}",
                report.requestCount(),
                report.matchedRoutineInstances(),
                report.invalidLines(),
                report.duplicates());
        for (DdlExportStatus status : DdlExportStatus.values())
            LOG.info(
                    com.example.db2toolkit.output.RunLogs.SUMMARY,
                    "{}={}",
                    status,
                    report.count(status));
        // Keep problems first so the success list cannot hide missing configured objects.
        for (DdlExportStatus status :
                new DdlExportStatus[] {
                    DdlExportStatus.NOT_FOUND,
                    DdlExportStatus.DDL_UNAVAILABLE,
                    DdlExportStatus.FAILED
                }) {
            if (report.count(status) == 0) continue;
            LOG.warn(
                    com.example.db2toolkit.output.RunLogs.SUMMARY,
                    "--- {}: {} ({}) ---",
                    status,
                    report.count(status),
                    switch (status) {
                        case NOT_FOUND -> "Configured objects not found in database";
                        case DDL_UNAVAILABLE -> "Objects found but DDL unavailable or unsupported";
                        default -> "Export failures";
                    });
            for (DdlExportResult r : report.results()) {
                if (r.status() != status) continue;
                String object =
                        Identifiers.quote(r.schema()) + "." + Identifiers.quote(r.objectName());
                String specific =
                        r.specificName() == null
                                ? ""
                                : " specific=" + Identifiers.quote(r.specificName());
                LOG.warn(
                        com.example.db2toolkit.output.RunLogs.SUMMARY,
                        "{}",
                        errors.clean(
                                r.type()
                                        + " "
                                        + object
                                        + specific
                                        + " "
                                        + r.status()
                                        + " reason="
                                        + r.reason()));
            }
        }
        if (report.stopReason() != null)
            LOG.error(
                    com.example.db2toolkit.output.RunLogs.SUMMARY,
                    "{}",
                    errors.clean(report.stopReason()));
        LOG.info(
                com.example.db2toolkit.output.RunLogs.SUMMARY,
                "Unprocessed={}",
                report.unprocessed().size());
        for (var r : report.unprocessed())
            LOG.warn(
                    com.example.db2toolkit.output.RunLogs.SUMMARY,
                    "UNPROCESSED {} {}",
                    r.type(),
                    errors.clean(r.ref().sqlName()));
        if (report.exitCode() == 0)
            LOG.info(
                    com.example.db2toolkit.output.RunLogs.SUMMARY, "No DDL export problems found.");
        LOG.info(
                com.example.db2toolkit.output.RunLogs.SUCCESS,
                "--- SUCCESS: {} (Exported objects) ---",
                report.count(DdlExportStatus.SUCCESS));
        for (DdlExportResult r : report.results()) {
            if (r.status() != DdlExportStatus.SUCCESS) continue;
            String object = Identifiers.quote(r.schema()) + "." + Identifiers.quote(r.objectName());
            String specific =
                    r.specificName() == null
                            ? ""
                            : " specific=" + Identifiers.quote(r.specificName());
            LOG.info(
                    com.example.db2toolkit.output.RunLogs.SUCCESS,
                    "{}",
                    errors.clean(
                            r.type()
                                    + " "
                                    + object
                                    + specific
                                    + " SUCCESS output="
                                    + r.outputPath()));
        }
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/ddl/model/DdlExportRequest.java =====
UTF8-BYTES: 251
SHA256: 8f3ba8c47f814840bc649d9742649ad9776fc221b0980f065b3a6618ae7d13e9
===== CONTENT =====
package com.example.db2toolkit.ddl.model;

import com.example.db2toolkit.model.DbObjectRef;

/** One deduplicated input name, possibly matching several routine instances. */
public record DdlExportRequest(DdlObjectType type, DbObjectRef ref) {}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/ddl/model/DdlExportResult.java =====
UTF8-BYTES: 689
SHA256: 1adf4da5da60b1c6d827b0ac952635f01ea67d30b3114eb71a81f3900987982e
===== CONTENT =====
package com.example.db2toolkit.ddl.model;

import com.example.db2toolkit.model.DbObjectRef;

import java.nio.file.Path;

public record DdlExportResult(
        DdlObjectType type,
        String schema,
        String objectName,
        String specificName,
        DdlExportStatus status,
        Path outputPath,
        String reason) {
    public static DdlExportResult of(
            DdlObjectType type,
            DbObjectRef ref,
            String specific,
            DdlExportStatus status,
            Path path,
            String reason) {
        return new DdlExportResult(type, ref.schema(), ref.name(), specific, status, path, reason);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/ddl/model/DdlExportStatus.java =====
UTF8-BYTES: 145
SHA256: 11616ffc98b9d169b977b3d21f12d99d213efa6fa110941859edfbb37d10341f
===== CONTENT =====
package com.example.db2toolkit.ddl.model;


public enum DdlExportStatus {
    SUCCESS,
    NOT_FOUND,
    DDL_UNAVAILABLE,
    FAILED
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/ddl/model/DdlExportSummary.java =====
UTF8-BYTES: 2408
SHA256: 1b77f239a8c1737369e5be92645b4bc30218ea7b4d5ea2279cc688e888692012
===== CONTENT =====
package com.example.db2toolkit.ddl.model;


import java.util.ArrayList;
import java.util.List;

/** Owns ordered results and exit-code rules; exposes read-only snapshots. */
public final class DdlExportSummary {
    private final List<DdlExportResult> results = new ArrayList<>();
    private final List<DdlExportRequest> unprocessed = new ArrayList<>();
    private int requestCount, matchedRoutineInstances, invalidLines, duplicates;
    private String stopReason;
    private boolean fatal;

    public List<DdlExportResult> results() {
        return List.copyOf(results);
    }

    public List<DdlExportRequest> unprocessed() {
        return List.copyOf(unprocessed);
    }

    public int requestCount() {
        return requestCount;
    }

    public int matchedRoutineInstances() {
        return matchedRoutineInstances;
    }

    public int invalidLines() {
        return invalidLines;
    }

    public int duplicates() {
        return duplicates;
    }

    public String stopReason() {
        return stopReason;
    }

    public void setRequestCount(int count) {
        requestCount = count;
    }

    public void addListStatistics(int invalid, int duplicateCount) {
        invalidLines += invalid;
        duplicates += duplicateCount;
    }

    public void recordInstance() {
        matchedRoutineInstances++;
    }

    public void add(DdlExportResult result) {
        results.add(result);
    }

    public void stop(String reason, List<DdlExportRequest> remaining, boolean fatalError) {
        // Cleanup can fail after a query failure: retain the original cause and never downgrade
        // severity.
        if (stopReason == null) stopReason = reason;
        else if (!stopReason.equals(reason)) stopReason += " | " + reason;
        unprocessed.addAll(remaining);
        fatal |= fatalError;
    }

    public long count(DdlExportStatus status) {
        return results.stream().filter(r -> r.status() == status).count();
    }

    public int exitCode() {
        if (fatal) return 2;
        boolean incomplete =
                stopReason != null
                        || invalidLines > 0
                        || !unprocessed.isEmpty()
                        || results.stream().anyMatch(r -> r.status() != DdlExportStatus.SUCCESS);
        return incomplete ? 1 : 0;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/ddl/model/DdlObjectType.java =====
UTF8-BYTES: 350
SHA256: ea55b9af5cf4befd0feb705116f554189e28fe703e9f2a95cba4b60ea69a91cd
===== CONTENT =====
package com.example.db2toolkit.ddl.model;


public enum DdlObjectType {
    PROCEDURE("procedures"),
    FUNCTION("functions"),
    TABLE("tables");
    private final String directory;

    DdlObjectType(String directory) {
        this.directory = directory;
    }

    public String directory() {
        return directory;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/ddl/model/DdlUnavailableException.java =====
UTF8-BYTES: 200
SHA256: 98af0f5406430f47a2ce3df2b9e6ad8845c90cb4c1e78d37731dba0769d0b956
===== CONTENT =====
package com.example.db2toolkit.ddl.model;


public final class DdlUnavailableException extends Exception {
    public DdlUnavailableException(String message) {
        super(message);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/excel/ExcelConfigLoader.java =====
UTF8-BYTES: 3056
SHA256: 00a40c514d5e3deddde1143108726515c93ba325c0a4b745d2e8b5cf7787d32d
===== CONTENT =====
package com.example.db2toolkit.excel;

import com.example.db2toolkit.config.ConfigurationException;
import com.example.db2toolkit.jdbc.SafeDiagnostics;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

public final class ExcelConfigLoader {
    private final SafeDiagnostics errors;

    public ExcelConfigLoader() {
        this(new SafeDiagnostics(java.util.List.of()));
    }

    public ExcelConfigLoader(SafeDiagnostics errors) {
        this.errors = errors;
    }

    public ExcelExportConfig load(Path file) throws IOException {
        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        options.setMaxAliasesForCollections(20);
        try (var reader = Files.newBufferedReader(file)) {
            Map<String, Object> document =
                    ExcelTaskNormalizer.map(
                            new Yaml(new SafeConstructor(options)).load(reader), "Excel YAML");
            ExcelTaskNormalizer.keys(document, "Excel YAML", "excel-export");
            var root = ExcelTaskNormalizer.map(document.get("excel-export"), "excel-export");
            ExcelTaskNormalizer.keys(
                    root,
                    "excel-export",
                    "enabled",
                    "output-directory",
                    "overwrite",
                    "include-header",
                    "default-fetch-size",
                    "simple",
                    "workbooks",
                    "exports");
            if (!ExcelTaskNormalizer.bool(root, "enabled", false))
                return ExcelExportConfig.disabled();
            Path output =
                    file.toAbsolutePath()
                            .getParent()
                            .resolve(
                                    ExcelTaskNormalizer.text(
                                            root.getOrDefault("output-directory", "./output/excel"),
                                            "output-directory"))
                            .normalize();
            return new ExcelExportConfig(
                    true,
                    output,
                    ExcelTaskNormalizer.bool(root, "overwrite", true),
                    new ExcelTaskNormalizer(errors).normalize(root));
        } catch (ConfigurationException e) {
            // These exceptions reach the startup console before per-database RunLogs exists.
            throw new ConfigurationException(errors.clean(e.getMessage()));
        } catch (YAMLException e) {
            // Do not echo a YAML parser excerpt which might include SQL literals or other sensitive
            // values.
            throw new ConfigurationException(
                    "Invalid Excel YAML; check syntax, duplicate keys and value types");
        }
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/excel/ExcelExportConfig.java =====
UTF8-BYTES: 1252
SHA256: 82823d9c38c74372b4254ecba9f229efa8f8b20be696847bd64ef94a636655ab
===== CONTENT =====
package com.example.db2toolkit.excel;

import java.nio.file.Path;
import java.util.List;

public record ExcelExportConfig(
        boolean enabled, Path output, boolean overwrite, List<ExcelExportTask> tasks) {
    public ExcelExportConfig {
        tasks = List.copyOf(tasks);
    }

    public static ExcelExportConfig disabled() {
        return new ExcelExportConfig(false, null, true, List.of());
    }

    public ExcelExportConfig forDatabase(String child) {
        return !enabled || child == null
                ? this
                : new ExcelExportConfig(true, output.resolve(child), overwrite, tasks);
    }

    /** Prefix once when application configuration is assigned to a database alias. */
    public ExcelExportConfig withDatabasePrefix(String alias) {
        if (!enabled) return this;
        return new ExcelExportConfig(true, output, overwrite, tasks.stream().map(t ->
                new ExcelExportTask(t.name(), t.type(), t.source(), ExcelNames.workbook(alias + "-" + t.workbook()),
                        t.sheet(), t.enabled(), t.fetchSize(), t.maxRows(), t.includeHeader())).toList());
    }

    public boolean hasEnabledTasks() {
        return enabled && tasks.stream().anyMatch(ExcelExportTask::enabled);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/excel/ExcelExportService.java =====
UTF8-BYTES: 11172
SHA256: 5ca80519bac70f86bdc3e857254e2c3a35cb12b9dd4aae56d72cf971c060d498
===== CONTENT =====
package com.example.db2toolkit.excel;

import com.example.db2toolkit.excel.ExcelExportSummary.Result;
import com.example.db2toolkit.excel.ExcelExportSummary.Status;
import com.example.db2toolkit.jdbc.SafeDiagnostics;
import com.example.db2toolkit.jdbc.JdbcActivity;
import com.example.db2toolkit.jdbc.ReadOnlyQueries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

/** One caller-owned connection, one writer per workbook, one implementation for all task styles. */
public final class ExcelExportService {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelExportService.class);

    public boolean export(
            Connection connection,
            ExcelExportConfig config,
            SafeDiagnostics errors,
            ExcelExportSummary summary) {
        Map<String, List<ExcelExportTask>> groups = new LinkedHashMap<>();
        for (ExcelExportTask task : config.tasks()) {
            if (!task.enabled()) {
                LOG.info("Excel task skipped: {} reason=Disabled", errors.clean(task.name()));
                summary.add(
                        new Result(task, Status.SKIPPED, List.of(), 0, 0, "Disabled", null, null));
            } else groups.computeIfAbsent(task.workbook(), k -> new ArrayList<>()).add(task);
        }
        ConnectionState state = new ConnectionState();
        for (var group : groups.entrySet()) {
            if (state.failure != null) {
                for (ExcelExportTask task : group.getValue()) summary.add(notExecuted(task, state));
            } else
                exportWorkbook(
                        connection,
                        config,
                        errors,
                        summary,
                        group.getKey(),
                        group.getValue(),
                        state);
        }
        return state.failure == null;
    }

    private void exportWorkbook(
            Connection connection,
            ExcelExportConfig config,
            SafeDiagnostics errors,
            ExcelExportSummary summary,
            String filename,
            List<ExcelExportTask> tasks,
            ConnectionState state) {
        // Query success is provisional until the shared workbook has been published and closed.
        List<Result> pending = new ArrayList<>();
        Path output = config.output().resolve(filename);
        long started = System.nanoTime();
        try {
            ExcelWorkbookWriter.validateOutput(output, config.overwrite());
            if (Files.exists(output))
                LOG.warn(
                        "Replacing configured Excel workbook: {}", errors.clean(output.toString()));
            try (ExcelWorkbookWriter writer = new ExcelWorkbookWriter(tasks, errors)) {
                // Publish earlier successful sheets even after connection loss, without querying
                // again.
                for (ExcelExportTask task : tasks) {
                    pending.add(
                            state.failure == null
                                    ? exportTask(connection, writer, task, errors, state)
                                    : notExecuted(task, state));
                    // Failed sheet removal makes the whole workbook unsafe, but other workbooks may
                    // still run.
                    writer.ensureUsable();
                }
                if (pending.stream().anyMatch(r -> r.status() == Status.SUCCESS)) {
                    long writingStarted = System.nanoTime();
                    LOG.info(
                            "Writing Excel workbook: {} sheets={}",
                            errors.clean(filename),
                            writer.sheetCount());
                    writer.publish(output, config.overwrite());
                    LOG.info(
                            "Excel workbook write completed: {} elapsedMs={}",
                            errors.clean(filename),
                            elapsed(writingStarted));
                } else
                    LOG.warn(
                            "Excel workbook not generated: {} reason=No successful tasks; existing"
                                + " file retained if present",
                            errors.clean(filename));
            }
            if (pending.stream().anyMatch(r -> r.status() == Status.SUCCESS))
                LOG.info("Excel workbook generated: {}", errors.clean(output.toString()));
        } catch (IOException | RuntimeException e) {
            String reason = "Workbook output/cleanup failed: " + errors.describe(e);
            for (int i = 0; i < pending.size(); i++) {
                Result r = pending.get(i);
                if (r.status() == Status.SUCCESS)
                    pending.set(i, failure(r.task(), started, reason, null));
            }
            for (int i = pending.size(); i < tasks.size(); i++)
                pending.add(failure(tasks.get(i), started, reason, null));
            LOG.error("Excel workbook {} failed: {}", errors.clean(filename), reason);
        }
        pending.forEach(summary::add);
    }

    private Result exportTask(
            Connection connection,
            ExcelWorkbookWriter writer,
            ExcelExportTask task,
            SafeDiagnostics errors,
            ConnectionState state) {
        long start = System.nanoTime();
        int firstSheet = writer.sheetCount();
        // Clean arguments before logging: console appenders do not pass through RunLogs redaction.
        String taskLabel = errors.clean(task.name());
        LOG.info(
                "Excel export started: {} workbook={} sheet={}",
                taskLabel,
                errors.clean(task.workbook()),
                errors.clean(task.sheet()));
        LOG.info(
                "Excel task settings: {} type={} fetchSize={} maxRows={} includeHeader={}",
                errors.clean(task.name()),
                task.type(),
                task.fetchSize(),
                task.maxRows(),
                task.includeHeader());
        if (task.type() == SourceType.OBJECT) LOG.info("Source: {}", errors.clean(task.source()));
        // Arguments are evaluated even when DEBUG is off; avoid scanning long SQL on the normal
        // INFO path.
        if (LOG.isDebugEnabled()) LOG.debug("Excel SQL: {}", errors.clean(task.sql()));
        try {
            List<ExcelWorkbookWriter.SheetRows> sheets;
            try (var activity = JdbcActivity.query("EXCEL_TASK " + taskLabel + " workbook=" + task.workbook() + " sheet=" + task.sheet());
                    PreparedStatement statement =
                    ReadOnlyQueries.prepareForwardOnly(connection, task.sql())) {
                JdbcActivity.configureQuery(statement);
                statement.setFetchSize(task.fetchSize());
                if (task.maxRows() > 0) statement.setMaxRows(task.maxRows());
                try (ResultSet rows = activity.execute(statement)) {
                    sheets =
                            writer.write(
                                    rows,
                                    task,
                                    count -> {
                                        if (count % 100000 == 0)
                                            LOG.info(
                                                    "Excel task {}: {} rows processed",
                                                    taskLabel,
                                                    count);
                                    });
                }
            }
            long count = sheets.stream().mapToLong(ExcelWorkbookWriter.SheetRows::rows).sum();
            if (task.maxRows() > 0 && count == task.maxRows())
                LOG.warn(
                        "Excel task {} reached maxRows={}; additional source rows were not checked",
                        errors.clean(task.name()),
                        task.maxRows());
            long elapsed = elapsed(start);
            LOG.info(
                    "Excel task {} data completed: rows={} elapsedMs={}; workbook publication"
                        + " pending",
                    taskLabel,
                    count,
                    elapsed);
            return new Result(task, Status.SUCCESS, sheets, count, elapsed, null, null, null);
        } catch (SQLException | IOException | RuntimeException e) {
            // ResultSet/statement close may fail after row writing succeeded; remove those
            // provisional sheets too.
            writer.discardFrom(firstSheet, e);
            String reason = errors.describe(e);
            if (reason != null && reason.length() > 2000)
                reason = reason.substring(0, 2000) + "...";
            LOG.error("Excel task {} failed: {}", taskLabel, reason);
            SQLException disconnect =
                    com.example.db2toolkit.jdbc.ConnectionFailureClassifier.connectionFailure(e);
            if (disconnect == null
                    && e instanceof SQLException sql
                    && com.example.db2toolkit.jdbc.ConnectionFailureClassifier.connectionLost(
                            connection, sql)) disconnect = sql;
            if (disconnect != null) {
                state.failure = disconnect;
                state.reason = reason;
                LOG.error(
                        "Excel queries stopped: database connection lost; remaining enabled tasks"
                            + " will be reported as not executed");
            }
            return failure(task, start, reason, e instanceof SQLException sql ? sql : disconnect);
        }
    }

    private static long elapsed(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }

    private static Result failure(
            ExcelExportTask task, long start, String reason, SQLException sql) {
        return new Result(
                task,
                Status.FAILED,
                List.of(),
                0,
                elapsed(start),
                reason,
                sql == null ? null : sql.getErrorCode(),
                sql == null ? null : sql.getSQLState());
    }

    private static Result notExecuted(ExcelExportTask task, ConnectionState state) {
        return new Result(
                task,
                Status.FAILED,
                List.of(),
                0,
                0,
                "Not executed: database connection lost; " + state.reason,
                state.failure.getErrorCode(),
                state.failure.getSQLState());
    }

    /** Scoped to one export invocation; reusing the service for another database starts fresh. */
    private static final class ConnectionState {
        private SQLException failure;
        private String reason; // Reuse the bounded, redacted diagnostic for all unexecuted tasks.
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/excel/ExcelExportSummary.java =====
UTF8-BYTES: 6214
SHA256: 05bb1bc4ecc0d659444dce3cdfb7e981475bb61cae4074073cba6319f5f380e7
===== CONTENT =====
package com.example.db2toolkit.excel;

import com.example.db2toolkit.jdbc.SafeDiagnostics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class ExcelExportSummary {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelExportSummary.class);

    public enum Status {
        SUCCESS,
        FAILED,
        SKIPPED
    }

    public record Result(
            ExcelExportTask task,
            Status status,
            List<ExcelWorkbookWriter.SheetRows> sheets,
            long rows,
            long elapsedMillis,
            String reason,
            Integer sqlCode,
            String sqlState) {
        public Result {
            sheets = List.copyOf(sheets);
        }
    }

    private final List<Result> results = new ArrayList<>();

    public List<Result> results() {
        return List.copyOf(results);
    }

    public void add(Result result) {
        results.add(result);
    }

    public long count(Status status) {
        return results.stream().filter(r -> r.status() == status).count();
    }

    public int exitCode() {
        return count(Status.FAILED) > 0 ? 1 : 0;
    }

    public void unavailable(ExcelExportConfig config, String reason) {
        // A later lifecycle failure must not overwrite already-published results or count a task
        // twice.
        Set<ExcelExportTask> completed = new HashSet<>();
        for (Result result : results) completed.add(result.task());
        for (ExcelExportTask task : config.tasks())
            if (!completed.contains(task))
                results.add(
                        new Result(
                                task,
                                task.enabled() ? Status.FAILED : Status.SKIPPED,
                                List.of(),
                                0,
                                0,
                                reason,
                                null,
                                null));
    }

    public void log(SafeDiagnostics errors) {
        LOG.info(
                com.example.db2toolkit.output.RunLogs.SUMMARY,
                "================ Excel Export Summary ================");
        LOG.info(
                com.example.db2toolkit.output.RunLogs.SUMMARY,
                "Successful tasks: {}; Failed tasks: {}; Skipped tasks: {}",
                count(Status.SUCCESS),
                count(Status.FAILED),
                count(Status.SKIPPED));
        for (Result r : results) {
            // Problems precede the successful task list.
            if (r.status() == Status.SUCCESS) continue;
            String message =
                    errors.clean(
                            "Excel task="
                                    + r.task().name()
                                    + " workbook="
                                    + r.task().workbook()
                                    + " sheet="
                                    + r.task().sheet()
                                    + " source="
                                    + (r.task().type() == SourceType.OBJECT
                                            ? r.task().source()
                                            : "SQL (configured SELECT)")
                                    + " status="
                                    + r.status()
                                    + " rows="
                                    + r.rows()
                                    + " maxRows="
                                    + r.task().maxRows()
                                    + " includeHeader="
                                    + r.task().includeHeader()
                                    + " elapsedMs="
                                    + r.elapsedMillis()
                                    + (r.reason() == null ? "" : " reason=" + r.reason())
                                    + (r.sqlCode() == null
                                            ? ""
                                            : " SQLCODE="
                                                    + r.sqlCode()
                                                    + " SQLSTATE="
                                                    + r.sqlState()));
            if (r.status() == Status.FAILED)
                LOG.error(com.example.db2toolkit.output.RunLogs.SUMMARY, "{}", message);
            else LOG.info(com.example.db2toolkit.output.RunLogs.SUMMARY, "{}", message);
        }
        if (count(Status.FAILED) == 0)
            LOG.info(
                    com.example.db2toolkit.output.RunLogs.SUMMARY,
                    "No Excel export failures found.");
        LOG.info(
                com.example.db2toolkit.output.RunLogs.SUCCESS,
                "--- SUCCESS: {} (Exported Excel tasks) ---",
                count(Status.SUCCESS));
        for (Result r : results) {
            if (r.status() != Status.SUCCESS) continue;
            LOG.info(
                    com.example.db2toolkit.output.RunLogs.SUCCESS,
                    "{}",
                    errors.clean(
                            "Excel task="
                                    + r.task().name()
                                    + " workbook="
                                    + r.task().workbook()
                                    + " source="
                                    + (r.task().type() == SourceType.OBJECT
                                            ? r.task().source()
                                            : "SQL (configured SELECT)")
                                    + " status=SUCCESS rows="
                                    + r.rows()
                                    + " elapsedMs="
                                    + r.elapsedMillis()));
            for (var sheet : r.sheets()) {
                LOG.info(
                        com.example.db2toolkit.output.RunLogs.SUCCESS,
                        "{}",
                        errors.clean("  sheet=" + sheet.name() + " rows=" + sheet.rows()));
            }
        }
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/excel/ExcelExportTask.java =====
UTF8-BYTES: 495
SHA256: 52f607f8aa69b9e31993555782abb7b0b4cd803cc00c7940b6fe5fbe000a50d2
===== CONTENT =====
package com.example.db2toolkit.excel;

/** All three configuration styles become this immutable, validated task. */
public record ExcelExportTask(
        String name,
        SourceType type,
        String source,
        String workbook,
        String sheet,
        boolean enabled,
        int fetchSize,
        int maxRows,
        boolean includeHeader) {
    public String sql() {
        return type == SourceType.OBJECT ? "SELECT * FROM " + source : source;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/excel/ExcelNames.java =====
UTF8-BYTES: 4239
SHA256: 4609c0b969eaac5d19efe26da4224e4973aa695c65077b210200e16648bd1340
===== CONTENT =====
package com.example.db2toolkit.excel;

import org.apache.poi.ss.util.WorkbookUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ExcelNames {
    private ExcelNames() {}

    public static String workbook(String raw) {
        String name = raw.strip().replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "_");
        name = name.replaceAll("[. ]+$", "");
        if (name.isEmpty()) throw new IllegalArgumentException("Empty workbook filename");
        if (!name.toLowerCase(Locale.ROOT).endsWith(".xlsx")) name += ".xlsx";
        // Device names remain reserved with extensions, including Windows' superscript digits.
        if (name.matches(
                "(?i)(CON|PRN|AUX|NUL|COM[1-9\u00B9\u00B2\u00B3]|LPT[1-9\u00B9\u00B2\u00B3])(?:\\..*)?"))
            name = "_" + name;
        if (name.getBytes(StandardCharsets.UTF_8).length > 200)
            throw new IllegalArgumentException("Workbook filename exceeds 200 UTF-8 bytes");
        return name;
    }

    public static String sheet(String raw) {
        String safe = WorkbookUtil.createSafeSheetName(sheetText(raw), '_');
        if (!safe.isEmpty() && Character.isHighSurrogate(safe.charAt(safe.length() - 1)))
            safe = safe.substring(0, safe.length() - 1);
        return WorkbookUtil.createSafeSheetName(cut(safe, 31), '_');
    }

    private static String sheetText(String raw) {
        if (raw == null) return null; // Preserve WorkbookUtil's existing null-name fallback.
        StringBuilder safe = new StringBuilder(raw.length());
        // Sheet names are XML attributes, not cell text. Controls can corrupt the workbook or
        // normalize to spaces on readback; unpaired surrogates can silently become '?'.
        raw.codePoints()
                .forEach(
                        codePoint ->
                                safe.appendCodePoint(
                                        Character.isISOControl(codePoint)
                                                        || codePoint == 0xFFFE
                                                        || codePoint == 0xFFFF
                                                        || (codePoint >= 0xD800
                                                                && codePoint <= 0xDFFF)
                                                ? '_'
                                                : codePoint));
        return safe.toString();
    }

    public static String uniqueSheet(String raw, Set<String> used) {
        return uniqueSheet(raw, used, new HashMap<>());
    }

    /** The supplied used-name set must only grow while its suffix cache is reused. */
    static String uniqueSheet(String raw, Set<String> used, Map<String, Integer> nextSuffix) {
        String base = sheet(raw);
        String key = sheetKey(base);
        if (used.add(key)) return base;
        // Earlier suffixes are already occupied; avoid rescanning them for every duplicate.
        int suffix = nextSuffix.getOrDefault(key, 2);
        String candidate;
        do {
            String tail = "_" + suffix++;
            candidate = cut(base, 31 - tail.length()) + tail;
        } while (!used.add(sheetKey(candidate)));
        nextSuffix.put(key, suffix);
        return candidate;
    }

    static String sheetKey(String name) {
        // POI compares sheet names with equalsIgnoreCase. Simple Unicode folding also handles
        // dotted/dotless I and final sigma, without the character expansion of locale casing.
        StringBuilder key = new StringBuilder(name.length());
        name.codePoints()
                .forEach(
                        codePoint ->
                                key.appendCodePoint(
                                        Character.toLowerCase(Character.toUpperCase(codePoint))));
        return key.toString();
    }

    private static String cut(String value, int length) {
        if (value.length() <= length) return value;
        if (Character.isHighSurrogate(value.charAt(length - 1))) length--;
        return value.substring(0, length);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/excel/ExcelSqlValidator.java =====
UTF8-BYTES: 1028
SHA256: 5b165af8dd31f6d8c609cb6df877cda44e2bcb8d62026600cc63b32a2dc6942a
===== CONTENT =====
package com.example.db2toolkit.excel;

import com.example.db2toolkit.model.Identifiers;
import com.example.db2toolkit.jdbc.ReadOnlyQueries;
import java.util.Locale;

/** Validates configured sources before starting an export. */
public final class ExcelSqlValidator {
    private ExcelSqlValidator() {}

    public static String object(String source) {
        if (source.contains(";")
                || source.contains("--")
                || source.contains("/*")
                || source.contains("*/"))
            throw new IllegalArgumentException("Unsafe OBJECT source");
        var ref = Identifiers.parse(source);
        // Keep ordinary names readable; use the existing quoting rules for delimited identifiers.
        return source.matches("[A-Za-z_][A-Za-z0-9_$#@]*\\.[A-Za-z_][A-Za-z0-9_$#@]*")
                ? source.toUpperCase(Locale.ROOT)
                : ref.sqlName();
    }

    public static String select(String sql) {
        return ReadOnlyQueries.select(sql);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/excel/ExcelTaskNormalizer.java =====
UTF8-BYTES: 9854
SHA256: 012781a595d5f57287aa0b150b81e732ac4cf6fcaad54c1092ef6d28c0b50b14
===== CONTENT =====
package com.example.db2toolkit.excel;

import com.example.db2toolkit.config.ConfigurationException;
import com.example.db2toolkit.jdbc.SafeDiagnostics;
import com.example.db2toolkit.model.Identifiers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/** Converts all configuration styles before any database is connected. */
public final class ExcelTaskNormalizer {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelTaskNormalizer.class);
    private final SafeDiagnostics errors;

    public ExcelTaskNormalizer() {
        this(new SafeDiagnostics(List.of()));
    }

    public ExcelTaskNormalizer(SafeDiagnostics errors) {
        this.errors = errors;
    }

    public List<ExcelExportTask> normalize(Map<String, Object> root) {
        keys(
                root,
                "excel-export",
                "enabled",
                "output-directory",
                "overwrite",
                "include-header",
                "default-fetch-size",
                "simple",
                "workbooks",
                "exports");
        List<ExcelExportTask> result = new ArrayList<>();
        boolean header = bool(root, "include-header", true);
        int fetch = number(root, "default-fetch-size", 1000, 1);
        Map<String, Object> simple = map(root.getOrDefault("simple", Map.of()), "simple");
        keys(simple, "simple", "tables");
        for (Object object : list(simple, "tables")) {
            String source = text(object, "simple.tables");
            String sheet = objectName(source);
            result.add(
                    task(sheet, "OBJECT", source, sheet + ".xlsx", sheet, true, fetch, 0, header));
        }
        for (Object value : list(root, "workbooks")) {
            Map<String, Object> group = map(value, "workbooks entry");
            keys(group, "workbooks entry", "name", "objects");
            String workbook = required(group, "name");
            for (Object object : list(group, "objects")) {
                String source = text(object, "workbook " + workbook + " object");
                String sheet = objectName(source);
                result.add(
                        task(
                                workbook + ":" + sheet,
                                "OBJECT",
                                source,
                                workbook,
                                sheet,
                                true,
                                fetch,
                                0,
                                header));
            }
        }
        int index = 0;
        for (Object value : list(root, "exports")) {
            Map<String, Object> advanced = map(value, "exports entry");
            index++;
            String name =
                    advanced.containsKey("name") ? required(advanced, "name") : "export-" + index;
            try {
                keys(
                        advanced,
                        "task " + name,
                        "name",
                        "type",
                        "source",
                        "workbook",
                        "sheet",
                        "enabled",
                        "fetch-size",
                        "max-rows",
                        "include-header");
                result.add(
                        task(
                                name,
                                required(advanced, "type"),
                                required(advanced, "source"),
                                required(advanced, "workbook"),
                                required(advanced, "sheet"),
                                bool(advanced, "enabled", true),
                                number(advanced, "fetch-size", fetch, 1),
                                number(advanced, "max-rows", 0, 0),
                                bool(advanced, "include-header", header)));
            } catch (IllegalArgumentException e) {
                throw new ConfigurationException("Excel task " + name + ": " + e.getMessage());
            }
        }
        // Different raw filenames must not silently become one file after platform-safe
        // sanitization.
        // Sheet collisions are different: each task keeps its data by receiving a unique sheet
        // name.
        Map<String, Set<String>> used = new HashMap<>();
        Map<String, Map<String, Integer>> nextSuffix = new HashMap<>();
        Map<String, String> originals = new HashMap<>();
        List<ExcelExportTask> normalized = new ArrayList<>();
        for (ExcelExportTask t : result) {
            String workbook;
            try {
                workbook = ExcelNames.workbook(t.workbook());
            } catch (IllegalArgumentException e) {
                throw new ConfigurationException("Excel task " + t.name() + ": " + e.getMessage());
            }
            String key = workbook.toLowerCase(Locale.ROOT);
            String original = originals.putIfAbsent(key, t.workbook());
            if (original != null && !original.equals(t.workbook()))
                throw new ConfigurationException(
                        "Excel workbook filenames collide after sanitization: " + workbook);
            String sheet =
                    t.enabled()
                            ? ExcelNames.uniqueSheet(
                                    t.sheet(),
                                    used.computeIfAbsent(key, k -> new HashSet<>()),
                                    nextSuffix.computeIfAbsent(key, k -> new HashMap<>()))
                            : ExcelNames.sheet(t.sheet());
            if (!sheet.equals(t.sheet()))
                LOG.warn(
                        "Excel task {}: sheet renamed to {}",
                        errors.clean(t.name()),
                        errors.clean(sheet));
            normalized.add(
                    new ExcelExportTask(
                            t.name(),
                            t.type(),
                            t.source(),
                            workbook,
                            sheet,
                            t.enabled(),
                            t.fetchSize(),
                            t.maxRows(),
                            t.includeHeader()));
        }
        return List.copyOf(normalized);
    }

    private static String objectName(String source) {
        try {
            return Identifiers.parse(ExcelSqlValidator.object(source)).name();
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException("Excel OBJECT: " + e.getMessage());
        }
    }

    private static ExcelExportTask task(
            String name,
            String type,
            String source,
            String workbook,
            String sheet,
            boolean enabled,
            int fetch,
            int max,
            boolean header) {
        try {
            SourceType kind = SourceType.valueOf(type.toUpperCase(Locale.ROOT));
            String validated =
                    kind == SourceType.OBJECT
                            ? ExcelSqlValidator.object(source)
                            : ExcelSqlValidator.select(source);
            return new ExcelExportTask(
                    name, kind, validated, workbook, sheet, enabled, fetch, max, header);
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException("Excel task " + name + ": " + e.getMessage());
        }
    }

    static void keys(Map<String, Object> map, String where, String... allowed) {
        Set<String> accepted = Set.of(allowed);
        for (String key : map.keySet())
            if (!accepted.contains(key))
                throw new ConfigurationException("Unknown key in " + where + ": " + key);
    }

    static Map<String, Object> map(Object value, String where) {
        if (!(value instanceof Map<?, ?> input))
            throw new ConfigurationException(where + " must be a mapping");
        Map<String, Object> result = new LinkedHashMap<>();
        for (var entry : input.entrySet()) {
            if (!(entry.getKey() instanceof String key))
                throw new ConfigurationException(where + " keys must be text");
            result.put(key, entry.getValue());
        }
        return result;
    }

    private static List<?> list(Map<String, Object> map, String key) {
        Object value = map.getOrDefault(key, List.of());
        if (!(value instanceof List<?> values))
            throw new ConfigurationException(key + " must be a list");
        return values;
    }

    static String text(Object value, String key) {
        if (!(value instanceof String s) || s.isBlank())
            throw new ConfigurationException(key + " must be nonempty text");
        return s.strip();
    }

    private static String required(Map<String, Object> map, String key) {
        return text(map.get(key), key);
    }

    static boolean bool(Map<String, Object> map, String key, boolean fallback) {
        if (!map.containsKey(key)) return fallback;
        if (!(map.get(key) instanceof Boolean value))
            throw new ConfigurationException(key + " must be true or false");
        return value;
    }

    private static int number(Map<String, Object> map, String key, int fallback, int minimum) {
        if (!map.containsKey(key)) return fallback;
        Object raw = map.get(key);
        if (!(raw instanceof Integer value) || value < minimum)
            throw new ConfigurationException(key + " must be an integer >= " + minimum);
        return value;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/excel/ExcelWorkbookWriter.java =====
UTF8-BYTES: 9832
SHA256: 0332319c497c12a650a10bab2bc4681b569d463c18153d185e4af2ab3d1bb59f
===== CONTENT =====
package com.example.db2toolkit.excel;

import com.example.db2toolkit.jdbc.SafeDiagnostics;
import com.example.db2toolkit.output.TemporaryOutputFile;
import com.example.db2toolkit.spreadsheet.ExcelValueWriter;
import com.ibm.db2.jcc.DB2ResultSet;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.function.LongConsumer;

/** Owns one streaming workbook; never retains JDBC rows beyond the current row. */
public final class ExcelWorkbookWriter implements AutoCloseable {
    private static final org.slf4j.Logger LOG =
            org.slf4j.LoggerFactory.getLogger(ExcelWorkbookWriter.class);

    public record SheetRows(String name, long rows) {}

    private final SXSSFWorkbook book =
            com.example.db2toolkit.spreadsheet.StreamingWorkbooks.streamingWorkbook();
    private final ExcelValueWriter values = new ExcelValueWriter(book);
    private final Set<String> used = new HashSet<>();
    private final Map<String, Integer> nextSuffix = new HashMap<>();
    private final CellStyle headerStyle;
    private final int rowLimit;
    private final SafeDiagnostics errors;
    private RuntimeException cleanupFailure;

    public ExcelWorkbookWriter(List<ExcelExportTask> tasks) {
        this(tasks, 1_048_576);
    }

    public ExcelWorkbookWriter(List<ExcelExportTask> tasks, SafeDiagnostics errors) {
        this(tasks, 1_048_576, errors);
    }

    public ExcelWorkbookWriter(List<ExcelExportTask> tasks, int rowLimit) {
        this(tasks, rowLimit, new SafeDiagnostics(List.of()));
    }

    ExcelWorkbookWriter(List<ExcelExportTask> tasks, int rowLimit, SafeDiagnostics errors) {
        if (rowLimit < 2 || rowLimit > 1_048_576)
            throw new IllegalArgumentException("Invalid sheet row limit");
        this.rowLimit = rowLimit;
        this.errors = errors;
        book.setCompressTempFiles(true);
        // Reserve future task names now; an earlier rollover must not steal a later task's sheet
        // name.
        for (ExcelExportTask task : tasks) used.add(ExcelNames.sheetKey(task.sheet()));
        headerStyle = book.createCellStyle();
        Font font = book.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
    }

    public List<SheetRows> write(ResultSet rs, ExcelExportTask task, LongConsumer progress)
            throws SQLException, IOException {
        ensureUsable();
        int startSheet = book.getNumberOfSheets();
        try {
            ResultSetMetaData metadata = rs.getMetaData();
            int count = metadata.getColumnCount();
            if (count < 1 || count > 16384)
                throw new IOException("Excel requires 1-16384 result columns");
            String[] labels = new String[count];
            boolean[] exactTimestamp = new boolean[count];
            DB2ResultSet db2Rows = null;
            for (int i = 0; i < count; i++) {
                labels[i] = metadata.getColumnLabel(i + 1);
                // Both standard getObject and getString can lose DB2 picoseconds; use the existing
                // JCC extension.
                exactTimestamp[i] =
                        metadata.getColumnType(i + 1) == Types.TIMESTAMP
                                && metadata.getScale(i + 1) > 9;
                if (exactTimestamp[i] && db2Rows == null)
                    db2Rows = rs instanceof DB2ResultSet db2 ? db2 : rs.unwrap(DB2ResultSet.class);
            }
            List<SheetRows> sheets = new ArrayList<>();
            Sheet sheet = newSheet(task.sheet(), labels, task.includeHeader());
            int rowIndex = task.includeHeader() ? 1 : 0;
            long rows = 0, sheetRows = 0;
            while ((task.maxRows() == 0 || rows < task.maxRows()) && rs.next()) {
                // Check after rs.next(): an exact-boundary result must not leave an empty
                // continuation sheet.
                if (rowIndex == rowLimit) {
                    ((org.apache.poi.xssf.streaming.SXSSFSheet) sheet).flushRows();
                    sheets.add(new SheetRows(sheet.getSheetName(), sheetRows));
                    sheet =
                            newSheet(
                                    ExcelNames.uniqueSheet(task.sheet(), used, nextSuffix),
                                    labels,
                                    task.includeHeader());
                    LOG.info(
                            "Excel sheet rollover: workbook={} sheet={} dataRowsCompleted={}"
                                + " includeHeader={}",
                            errors.clean(task.workbook()),
                            errors.clean(sheet.getSheetName()),
                            rows,
                            task.includeHeader());
                    rowIndex = task.includeHeader() ? 1 : 0;
                    sheetRows = 0;
                }
                Row row = sheet.createRow(rowIndex++);
                for (int i = 1; i <= count; i++) {
                    Object value;
                    if (exactTimestamp[i - 1]) {
                        var timestamp = db2Rows.getDBTimestamp(i);
                        value = timestamp == null ? null : timestamp.toDBString(false);
                    } else value = rs.getObject(i);
                    values.write(row.createCell(i - 1), value);
                }
                rows++;
                sheetRows++;
                progress.accept(rows);
            }
            // The row window belongs to the active sheet only, not every completed task/rollover.
            ((org.apache.poi.xssf.streaming.SXSSFSheet) sheet).flushRows();
            sheets.add(new SheetRows(sheet.getSheetName(), sheetRows));
            return List.copyOf(sheets);
        } catch (SQLException | IOException | RuntimeException e) {
            // No partial task sheets are published as successful data.
            discardFrom(startSheet, e);
            throw e;
        }
    }

    private Sheet newSheet(String name, String[] labels, boolean header) throws IOException {
        Sheet sheet = book.createSheet(name);
        if (header) {
            Row row = sheet.createRow(0);
            for (int i = 0; i < labels.length; i++) {
                Cell cell = row.createCell(i);
                ExcelValueWriter.text(cell, labels[i]);
                cell.setCellStyle(headerStyle);
            }
            sheet.createFreezePane(0, 1);
        }
        return sheet;
    }

    public int sheetCount() {
        return book.getNumberOfSheets();
    }

    public void removeFrom(int index) {
        if (cleanupFailure != null) throw cleanupFailure;
        try {
            while (book.getNumberOfSheets() > index)
                book.removeSheetAt(book.getNumberOfSheets() - 1);
        } catch (RuntimeException e) {
            cleanupFailure = e;
            throw e;
        }
    }

    /**
     * Preserve the query diagnostic, especially SQLSTATE 08, when rollback of task sheets also
     * fails.
     */
    void discardFrom(int index, Exception primary) {
        try {
            removeFrom(index);
        } catch (RuntimeException cleanup) {
            if (cleanup != primary
                    && Arrays.stream(primary.getSuppressed()).noneMatch(e -> e == cleanup))
                primary.addSuppressed(cleanup);
        }
    }

    void ensureUsable() throws IOException {
        if (cleanupFailure != null)
            throw new IOException(
                    "Cannot safely use workbook after sheet cleanup failed", cleanupFailure);
    }

    public void publish(Path output, boolean overwrite) throws IOException {
        ensureUsable();
        validateOutput(output, overwrite);
        try (var staging =
                TemporaryOutputFile.create(output.toAbsolutePath().getParent(), ".excel-")) {
            Path temporary = staging.path();
            try (OutputStream stream = Files.newOutputStream(temporary)) {
                book.write(stream);
            }
            if (overwrite) {
                try {
                    Files.move(
                            temporary,
                            output,
                            StandardCopyOption.ATOMIC_MOVE,
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (AtomicMoveNotSupportedException e) {
                    Files.move(temporary, output, StandardCopyOption.REPLACE_EXISTING);
                }
            } else
                Files.move(
                        temporary,
                        output); // No REPLACE_EXISTING: also protects against a concurrent creator.
        }
    }

    /**
     * Reject known unusable destinations before a large SELECT, then recheck before publication.
     */
    static void validateOutput(Path output, boolean overwrite) throws IOException {
        if (Files.isSymbolicLink(output))
            throw new IOException("Refusing to replace a symbolic link");
        if (Files.exists(output, LinkOption.NOFOLLOW_LINKS)) {
            if (!overwrite)
                throw new FileAlreadyExistsException(
                        output.toString(), null, "Workbook already exists and overwrite=false");
            if (!Files.isRegularFile(output, LinkOption.NOFOLLOW_LINKS))
                throw new IOException("Workbook destination is not a regular file");
        }
        Files.createDirectories(output.toAbsolutePath().getParent());
    }

    @Override
    public void close() throws IOException {
        book.close();
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/excel/SourceType.java =====
UTF8-BYTES: 92
SHA256: 1d42e23e77e83ff43afd91f707830d83246f093940eae07650046dd3f5390135
===== CONTENT =====
package com.example.db2toolkit.excel;

public enum SourceType {
    OBJECT,
    SQL
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/input/ObjectListReader.java =====
UTF8-BYTES: 1476
SHA256: 6bd354f0069831aa16dfa8737d682ee7ac165d113262b527abcc972e6a1b0dc7
===== CONTENT =====
package com.example.db2toolkit.input;

import com.example.db2toolkit.model.DbObjectRef;
import com.example.db2toolkit.model.Identifiers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class ObjectListReader {
    public record Issue(Path file, int line, String reason) {}

    public record Listing(List<DbObjectRef> objects, List<Issue> issues, int duplicates) {}

    public Listing read(Path file) throws IOException {
        var objects = new LinkedHashSet<DbObjectRef>();
        var issues = new ArrayList<Issue>();
        int duplicates = 0;
        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int number = 0;
            while ((line = reader.readLine()) != null) {
                number++;
                if (number == 1 && line.startsWith("\uFEFF")) line = line.substring(1);
                line = line.strip();
                if (line.isEmpty() || line.startsWith("#")) continue;
                try {
                    if (!objects.add(Identifiers.parse(line))) duplicates++;
                } catch (IllegalArgumentException e) {
                    issues.add(new Issue(file, number, e.getMessage()));
                }
            }
        }
        return new Listing(List.copyOf(objects), List.copyOf(issues), duplicates);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/jdbc/ConnectionFailureClassifier.java =====
UTF8-BYTES: 1074
SHA256: fa07914216450506e7e85ae7c6c7d07371c750c2c0e8ba7f69cb97e117205790
===== CONTENT =====
package com.example.db2toolkit.jdbc;

import java.sql.*;

public final class ConnectionFailureClassifier {
    private ConnectionFailureClassifier() {}

    /**
     * Finds explicit disconnects even when try-with-resources suppressed them behind a read error.
     */
    public static SQLException connectionFailure(Throwable error) {
        for (Throwable current : SafeDiagnostics.exceptions(error)) {
            if (current instanceof SQLException sql
                    && (sql instanceof SQLNonTransientConnectionException
                            || sql instanceof SQLRecoverableException
                            || (sql.getSQLState() != null && sql.getSQLState().startsWith("08"))))
                return sql;
        }
        return null;
    }

    public static boolean connectionLost(Connection c, SQLException e) {
        if (connectionFailure(e) != null) return true;
        try {
            return c.isClosed() || !c.isValid(5);
        } catch (SQLException ignored) {
            return true;
        }
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/jdbc/Db2ConnectionFactory.java =====
UTF8-BYTES: 793
SHA256: 4f232532288c21354ff26f01897f56a1a123956c44251d9006ff363b33cdfbe9
===== CONTENT =====
package com.example.db2toolkit.jdbc;

import com.example.db2toolkit.config.AppConfig;

import java.sql.*;
import java.util.Properties;

public final class Db2ConnectionFactory {
    public Connection open(AppConfig config) throws SQLException {
        Properties credentials = new Properties();
        credentials.setProperty("user", config.connection().username());
        credentials.setProperty("password", config.connection().password());
        credentials.setProperty("loginTimeout", Integer.toString(config.connection().connectTimeoutSeconds()));
        credentials.setProperty("blockingReadConnectionTimeout", Integer.toString(config.connection().readTimeoutSeconds()));
        return DriverManager.getConnection(config.connection().url(), credentials);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/jdbc/JdbcActivity.java =====
UTF8-BYTES: 7659
SHA256: 0f817567bed627df4cc85d7ebff3f2f09de685e897d9ac32515badca858f2a91
===== CONTENT =====
package com.example.db2toolkit.jdbc;

import com.example.db2toolkit.config.ConnectionConfig;
import com.example.db2toolkit.output.RunLogs;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/** Observes blocking JDBC work; never executes queries or cancellation on the logging thread. */
public final class JdbcActivity implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcActivity.class);
    private static final ThreadLocal<Context> CURRENT = new ThreadLocal<>();
    private static final class Timer {
        static final ScheduledThreadPoolExecutor EXECUTOR = create();
        private static ScheduledThreadPoolExecutor create() {
            var executor = new ScheduledThreadPoolExecutor(1, runnable -> {
                Thread thread = new Thread(runnable, "db2-jdbc-progress");
                thread.setDaemon(true);
                return thread;
            });
            executor.setRemoveOnCancelPolicy(true);
            return executor;
        }
    }

    public static final class Context implements AutoCloseable {
        final ConnectionConfig settings;
        final SafeDiagnostics errors;
        final Context previous;
        private Context(ConnectionConfig settings, SafeDiagnostics errors) {
            this.settings = settings;
            this.errors = errors;
            previous = CURRENT.get();
            CURRENT.set(this);
        }
        @Override public void close() {
            if (previous == null) CURRENT.remove(); else CURRENT.set(previous);
        }
    }

    public static Context context(ConnectionConfig settings, SafeDiagnostics errors) {
        return new Context(settings, errors);
    }

    private final boolean enabled;
    private final String detail;
    private final Map<String, String> mdc = MDC.getCopyOfContextMap();
    private final long started = System.nanoTime();
    private long phaseStarted = started;
    private String phase;
    private boolean closed;
    private ScheduledFuture<?> waiting;
    private final int timeoutSeconds;
    private QueryDeadline deadline;

    private JdbcActivity(String phase, String detail, int timeoutSeconds) {
        Context context = CURRENT.get();
        enabled = context != null;
        SafeDiagnostics errors = context == null ? new SafeDiagnostics(List.of()) : context.errors;
        this.detail = errors.clean(detail);
        this.phase = phase;
        this.timeoutSeconds = timeoutSeconds;
        logStart();
        int interval = context == null ? 0 : context.settings.progressIntervalSeconds();
        if (interval > 0) waiting = Timer.EXECUTOR.scheduleAtFixedRate(this::tick, interval, interval, TimeUnit.SECONDS);
    }

    public static JdbcActivity start(String phase, String detail, int timeoutSeconds) {
        return new JdbcActivity(phase, detail, timeoutSeconds);
    }

    public static JdbcActivity query(String detail) {
        Context context = CURRENT.get();
        return query(detail, context == null ? 0 : context.settings.queryTimeoutSeconds());
    }

    public static JdbcActivity query(String detail, int timeoutSeconds) {
        return start("PREPARE_QUERY", detail, timeoutSeconds);
    }

    public static void configureQuery(PreparedStatement statement) throws SQLException {
        Context context = CURRENT.get();
        if (context != null && context.settings.queryTimeoutSeconds() > 0)
            statement.setQueryTimeout(context.settings.queryTimeoutSeconds());
    }

    public ResultSet execute(PreparedStatement statement) throws SQLException {
        phase("EXECUTE_QUERY");
        SqlAuditLog.execute(statement);
        Context context = CURRENT.get();
        if (context != null) deadline = new QueryDeadline(statement, timeoutSeconds,
                context.settings.slowQuerySeconds(), context.settings.cancelGraceSeconds(),
                detail, SqlAuditLog.id(statement), context.errors);
        ResultSet rows;
        try { rows = statement.executeQuery(); }
        catch (SQLException | RuntimeException e) {
            SqlAuditLog.executionFailed(statement, e);
            throw e;
        }
        // READ_RESULTS includes the caller's fetching, processing and resource cleanup until this
        // activity closes. It is elapsed application time, not pure database execution time.
        phase("READ_RESULTS");
        return rows;
    }

    public synchronized void phase(String next) {
        if (closed) return;
        logEnd();
        phase = next;
        phaseStarted = System.nanoTime();
        logStart();
    }

    private void logStart() {
        if (!enabled) return;
        LOG.info("JDBC started: phase={} context={} timeoutSeconds={}", phase, detail, timeoutSeconds);
    }

    private void logEnd() {
        if (!enabled) return;
        LOG.info("JDBC ended: phase={} context={} elapsedMs={} totalElapsedMs={}",
                phase, detail, elapsed(phaseStarted), elapsed(started));
    }

    private synchronized void tick() {
        if (closed) return;
        Map<String, String> previous = MDC.getCopyOfContextMap();
        try {
            // The scheduler is shared across runs; restore this activity's database/run tags so
            // waiting messages reach the right log files, then restore the scheduler's context.
            if (mdc == null) MDC.clear(); else MDC.setContextMap(mdc);
            LOG.info(RunLogs.PROGRESS,
                    "JDBC still waiting: phase={} context={} elapsedMs={} totalElapsedMs={} timeoutSeconds={}",
                    phase, detail, elapsed(phaseStarted), elapsed(started), timeoutSeconds);
        } finally {
            if (previous == null) MDC.clear(); else MDC.setContextMap(previous);
        }
    }

    private static long elapsed(long start) { return (System.nanoTime() - start) / 1_000_000; }

    @Override public synchronized void close() throws SQLException {
        if (closed) return;
        closed = true;
        // Stop progress logging; the separate deadline handles cancellation and late completion.
        if (waiting != null) waiting.cancel(false);
        // "ended" records elapsed time on both success and failure; the caller reports the outcome.
        try { if (deadline != null) deadline.close(); }
        finally { logEnd(); }
    }

    @FunctionalInterface public interface ConnectionOpener { Connection open() throws SQLException; }

    public static ConnectionLease open(ConnectionOpener opener, ConnectionConfig settings) throws SQLException {
        try (var activity = start("CONNECT", "database connection", settings.connectTimeoutSeconds())) {
            return new ConnectionLease(opener.open(), settings.readTimeoutSeconds());
        }
    }

    public static final class ConnectionLease implements AutoCloseable {
        private final Connection connection;
        private final int timeout;
        private ConnectionLease(Connection connection, int timeout) {
            this.connection = connection;
            this.timeout = timeout;
        }
        public Connection connection() { return connection; }
        @Override public void close() throws SQLException {
            try (var activity = start("CLOSE_CONNECTION", "database connection", timeout)) {
                connection.close();
            }
        }
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/jdbc/QueryDeadline.java =====
UTF8-BYTES: 5782
SHA256: cb1d17b19b42644eced2a11bd8d9d5ee128c59716f39fcdbfc71252edf4ec7a8
===== CONTENT =====
package com.example.db2toolkit.jdbc;

import com.example.db2toolkit.output.RunLogs;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.SQLTimeoutException;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/** Bounds the execute/read/cleanup scope on a best-effort basis, without wrapping native JDBC. */
final class QueryDeadline implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(QueryDeadline.class);
    private static final ScheduledThreadPoolExecutor TIMER = timer();
    private final PreparedStatement statement;
    private final java.sql.Connection connection;
    private final SafeDiagnostics errors;
    private final String detail;
    private final Map<String, String> mdc = MDC.getCopyOfContextMap();
    private final long started = System.nanoTime();
    private final long sqlId;
    private final long limitNanos;
    private ScheduledFuture<?> slow, timeout, abort;
    private boolean closed, expired, cancelling, aborted;

    QueryDeadline(PreparedStatement statement, int seconds, int slowSeconds, int graceSeconds,
            String detail, long sqlId, SafeDiagnostics errors) throws SQLException {
        this.limitNanos = TimeUnit.SECONDS.toNanos(seconds);
        this.statement = statement;
        // Capture before execute: a stuck/closed statement must not block retrieving its connection during abort.
        this.connection = seconds > 0 ? statement.getConnection() : null;
        this.errors = errors;
        this.detail = detail;
        this.sqlId = sqlId;
        if (slowSeconds > 0) slow = TIMER.schedule(() -> scoped(this::warnSlow), slowSeconds, TimeUnit.SECONDS);
        if (seconds > 0) {
            timeout = TIMER.schedule(() -> scoped(this::expire), seconds, TimeUnit.SECONDS);
            // Independent of cancel(): a blocked cancellation must not delay escalation.
            abort = TIMER.schedule(() -> scoped(this::abort), (long) seconds + graceSeconds, TimeUnit.SECONDS);
        }
    }

    private static ScheduledThreadPoolExecutor timer() {
        var timer = new ScheduledThreadPoolExecutor(1, r -> daemon("db2-query-deadline", r));
        timer.setRemoveOnCancelPolicy(true);
        return timer;
    }

    private static Thread daemon(String name, Runnable action) {
        var thread = new Thread(action, name);
        thread.setDaemon(true);
        return thread;
    }

    private synchronized void warnSlow() {
        if (!closed) report("SLOW_QUERY");
    }

    private synchronized void expire() {
        if (closed) return;
        expired = true;
        report("TIMEOUT cancel requested");
        daemon("db2-query-cancel", () -> scoped(() -> {
            synchronized (this) {
                if (closed) return;
                cancelling = true;
            }
            try { statement.cancel(); }
            catch (SQLException | RuntimeException e) { failure("CANCEL_FAILED", e); }
            finally { synchronized (this) { cancelling = false; } }
        })).start();
    }

    private synchronized void abort() {
        if (closed) return;
        expired = true;
        // Mark unusable before dispatch: even a failed/blocked abort must not allow reuse.
        aborted = true;
        report("TIMEOUT connection abort requested");
        daemon("db2-query-abort", () -> scoped(() -> {
            try {
                if (connection == null) throw new SQLException("Statement has no connection");
                connection.abort(action -> daemon("db2-connection-abort", action).start());
            } catch (SQLException | RuntimeException e) { failure("ABORT_FAILED", e); }
        })).start();
    }

    private void failure(String event, Throwable error) {
        LOG.warn(RunLogs.PROGRESS, "{} sqlId={} context={} reason={}", event, sqlId, detail, errors.describe(error));
    }

    private void report(String event) {
        // File/console appenders may block. Never let them occupy the shared deadline timer,
        // hold up cancellation, or hold the deadline monitor while a scope is closing.
        daemon("db2-deadline-log", () -> scoped(() -> log(event))).start();
    }

    private void log(String event) {
        LOG.warn(RunLogs.PROGRESS, "{} sqlId={} context={} elapsedMs={}", event, sqlId, detail,
                (System.nanoTime() - started) / 1_000_000);
    }

    private void scoped(Runnable action) {
        var previous = MDC.getCopyOfContextMap();
        try {
            if (mdc == null) MDC.clear(); else MDC.setContextMap(mdc);
            action.run();
        } finally {
            if (previous == null) MDC.clear(); else MDC.setContextMap(previous);
        }
    }

    @Override public synchronized void close() throws SQLException {
        if (closed) return;
        closed = true;
        if (slow != null) slow.cancel(false);
        if (timeout != null) timeout.cancel(false);
        if (abort != null) abort.cancel(false);
        if (expired || limitNanos > 0 && System.nanoTime() - started >= limitNanos) {
            // Do not publish success when the driver ignores cancel and eventually returns rows.
            var error = new SQLTimeoutException("Query scope exceeded deadline; sqlId=" + sqlId, "HYT00");
            if (aborted || cancelling)
                error.addSuppressed(new SQLRecoverableException("Connection cannot be reused after timeout escalation or unfinished cancellation", "08006"));
            SqlAuditLog.failed(sqlId, "TIMEOUT", error);
            throw error;
        }
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/jdbc/ReadOnlyQueries.java =====
UTF8-BYTES: 6700
SHA256: 5971ea9a6f9534097c0e0a5d283cfa60aa01cdecbc9c1b7df35c2cd802264c64
===== CONTENT =====
package com.example.db2toolkit.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;

/**
 * Conservative lexical SELECT check; quoted text/comments are never treated as SQL keywords.
 * This is not a full SQL parser and cannot inspect side effects inside database functions;
 * database privileges remain necessary to enforce a strict read-only boundary.
 */
public final class ReadOnlyQueries {
    private static final Set<String> FORBIDDEN =
            Set.of(
                    "INSERT",
                    "UPDATE",
                    "DELETE",
                    "MERGE",
                    "DROP",
                    "ALTER",
                    "CREATE",
                    "TRUNCATE",
                    "CALL",
                    "GRANT",
                    "REVOKE",
                    "COMMIT",
                    "ROLLBACK",
                    "REPLACE",
                    "INTO",
                    "SET",
                    "EXECUTE",
                    "BEGIN",
                    "NEXTVAL");

    private ReadOnlyQueries() {}

    /** Validate before contacting the JDBC driver. Keeps the driver's native statement/result set. */
    public static PreparedStatement prepare(Connection connection, String sql) throws SQLException {
        return prepare(connection, sql, false);
    }

    public static PreparedStatement prepareForwardOnly(Connection connection, String sql) throws SQLException {
        return prepare(connection, sql, true);
    }

    private static PreparedStatement prepare(Connection connection, String sql, boolean forwardOnly)
            throws SQLException {
        long id = SqlAuditLog.attempt(sql);
        try { select(sql); }
        catch (RuntimeException e) {
            SqlAuditLog.failed(id, "REJECTED", e);
            throw e;
        }
        try {
            PreparedStatement statement = forwardOnly
                    ? connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
                    : connection.prepareStatement(sql);
            SqlAuditLog.prepared(statement, id);
            return statement;
        } catch (SQLException | RuntimeException e) {
            SqlAuditLog.failed(id, "PREPARE_FAILED", e);
            throw e;
        }
    }

    public static void bindString(PreparedStatement statement, int index, String value) throws SQLException {
        SqlAuditLog.parameter(statement, index, "VARCHAR", value);
        statement.setString(index, value);
    }

    public static void bindDecimal(PreparedStatement statement, int index, java.math.BigDecimal value)
            throws SQLException {
        SqlAuditLog.parameter(statement, index, "DECIMAL", value);
        statement.setBigDecimal(index, value);
    }

    public static String select(String sql) {
        List<String> words = new ArrayList<>();
        for (int i = 0; i < sql.length(); ) {
            char c = sql.charAt(i);
            if (c == ';')
                throw new IllegalArgumentException(
                        "SQL must contain one SELECT without statement delimiters");
            if (i + 1 < sql.length() && sql.startsWith("--", i)) {
                i = afterLineComment(sql, i);
            } else if (i + 1 < sql.length() && sql.startsWith("/*", i)) {
                i = afterBlockComment(sql, i);
            } else if (c == '\'' || c == '"') {
                char quote = c;
                boolean closed = false;
                i++;
                while (i < sql.length()) {
                    if (sql.charAt(i++) == quote) {
                        if (i < sql.length() && sql.charAt(i) == quote) i++;
                        else {
                            closed = true;
                            break;
                        }
                    }
                }
                if (!closed) throw new IllegalArgumentException("Unterminated SQL quoted text");
            } else if (Character.isLetter(c) || c == '_') {
                int start = i++;
                while (i < sql.length()
                        && (Character.isLetterOrDigit(sql.charAt(i))
                                || "_$#@".indexOf(sql.charAt(i)) >= 0)) i++;
                String word = sql.substring(start, i).toUpperCase(Locale.ROOT);
                // IBM documents REPLACE(source, search, replacement) as a scalar string function.
                // SQL comments are trivia between a function name and its opening parenthesis.
                if (word.equals("REPLACE")) {
                    int next = afterTrivia(sql, i);
                    if (next < sql.length() && sql.charAt(next) == '(')
                        word = "SCALAR_REPLACE_FUNCTION";
                }
                words.add(word);
            } else i++;
        }
        if (words.isEmpty()
                || !(words.get(0).equals("SELECT") || words.get(0).equals("WITH"))
                || !words.contains("SELECT"))
            throw new IllegalArgumentException("SQL source must be a SELECT or WITH ... SELECT");
        if (words.stream().anyMatch(FORBIDDEN::contains))
            throw new IllegalArgumentException("SQL contains a prohibited operation");
        for (int i = 0; i + 2 < words.size(); i++)
            if (words.subList(i, i + 3).equals(List.of("NEXT", "VALUE", "FOR")))
                throw new IllegalArgumentException("Sequence mutation is not supported");
        return sql;
    }

    private static int afterTrivia(String sql, int i) {
        while (i < sql.length()) {
            if (Character.isWhitespace(sql.charAt(i))) i++;
            else if (sql.startsWith("--", i)) i = afterLineComment(sql, i);
            else if (sql.startsWith("/*", i)) i = afterBlockComment(sql, i);
            else break;
        }
        return i;
    }

    private static int afterLineComment(String sql, int i) {
        i += 2;
        while (i < sql.length() && sql.charAt(i) != '\n' && sql.charAt(i) != '\r') i++;
        return i;
    }

    private static int afterBlockComment(String sql, int i) {
        int depth = 1;
        i += 2;
        while (i < sql.length() && depth > 0) {
            if (sql.startsWith("/*", i)) {
                depth++;
                i += 2;
            } else if (sql.startsWith("*/", i)) {
                depth--;
                i += 2;
            } else i++;
        }
        if (depth != 0) throw new IllegalArgumentException("Unterminated SQL comment");
        return i;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/jdbc/SafeDiagnostics.java =====
UTF8-BYTES: 3936
SHA256: b6c2b3b891d595c75b6028db28eda6dccc12ead67e364c9cb22d50d8a12d4fa6
===== CONTENT =====
package com.example.db2toolkit.jdbc;

import com.example.db2toolkit.config.AppConfig;

import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

public final class SafeDiagnostics {
    // Reuse compiled patterns: every object result and file-log event passes through redaction.
    private static final Pattern JDBC_URL = Pattern.compile("(?i)jdbc:db2:[^\\s]+");
    private static final Pattern CREDENTIAL =
            Pattern.compile("(?i)(password|pwd|user|username)\\s*[=:]\\s*[^;\\s,]+");
    private static final Pattern CONTROL = Pattern.compile("[\\p{Cntrl}]");
    private final List<AppConfig> configurations;
    private final Pattern passwords;

    public SafeDiagnostics(AppConfig config) {
        this(List.of(config));
    }

    public SafeDiagnostics(List<AppConfig> configurations) {
        this.configurations = List.copyOf(configurations);
        // Match longer secrets first in one pass; replacing a shorter prefix first exposes the
        // suffix.
        String alternatives =
                configurations.stream()
                        .map(AppConfig::password)
                        .filter(value -> value != null && !value.isEmpty())
                        .distinct()
                        .sorted(Comparator.comparingInt(String::length).reversed())
                        .map(Pattern::quote)
                        .collect(java.util.stream.Collectors.joining("|"));
        passwords = alternatives.isEmpty() ? null : Pattern.compile(alternatives);
    }

    public String clean(String message) {
        if (message == null) return "No detail";
        String s = message;
        for (AppConfig config : configurations) {
            s = s.replace(config.connection().url(), "[JDBC URL redacted]");
        }
        if (passwords != null) s = passwords.matcher(s).replaceAll("[redacted]");
        s = JDBC_URL.matcher(s).replaceAll("[JDBC URL redacted]");
        s = CREDENTIAL.matcher(s).replaceAll("$1=[redacted]");
        return CONTROL.matcher(s).replaceAll(" ");
    }

    public String sql(SQLException e) {
        return describe(e);
    }

    /**
     * Keep primary and cleanup diagnostics together, without logging stack traces or credentials.
     */
    public String describe(Throwable error) {
        StringJoiner out = new StringJoiner(" | ");
        for (Throwable current : exceptions(error)) {
            String prefix =
                    current instanceof SQLException sql
                            ? "SQLState=" + sql.getSQLState() + ", code=" + sql.getErrorCode()
                            : current.getClass().getSimpleName();
            out.add(clean(prefix + ": " + current.getMessage()));
        }
        return (isTimeout(error) ? "TIMEOUT: " : "") + out;
    }

    public static boolean isTimeout(Throwable error) {
        return exceptions(error).stream().anyMatch(e -> e instanceof SQLTimeoutException
                || e instanceof SQLException sql && ("57014".equals(sql.getSQLState())
                    || "HYT00".equals(sql.getSQLState()) || "HYT01".equals(sql.getSQLState())));
    }

    static List<Throwable> exceptions(Throwable error) {
        List<Throwable> result = new ArrayList<>();
        Set<Throwable> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        Deque<Throwable> pending = new ArrayDeque<>();
        if (error != null) pending.add(error);
        while (!pending.isEmpty()) {
            Throwable current = pending.removeFirst();
            if (!seen.add(current)) continue;
            result.add(current);
            if (current instanceof SQLException sql && sql.getNextException() != null)
                pending.addLast(sql.getNextException());
            for (Throwable suppressed : current.getSuppressed()) pending.addLast(suppressed);
            if (current.getCause() != null) pending.addLast(current.getCause());
        }
        return result;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/jdbc/SqlAuditLog.java =====
UTF8-BYTES: 4375
SHA256: 01c7b7e8b1c27a862482d57b137dfc5ab7a13748b3983d221bd0510f02710117
===== CONTENT =====
package com.example.db2toolkit.jdbc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.Map;
import java.util.WeakHashMap;
import org.slf4j.MDC;

/** One flushed SQL-attempt log for one database run; never wraps native JDBC objects. */
public final class SqlAuditLog implements AutoCloseable {
    private static final ThreadLocal<SqlAuditLog> CURRENT = new ThreadLocal<>();
    private final SqlAuditLog previous;
    private final BufferedWriter writer;
    private final SafeDiagnostics errors;
    private final Path path;
    // Weak keys also release preparations abandoned after a bind/configuration failure.
    private final Map<PreparedStatement, Long> statements = new WeakHashMap<>();
    private long sequence;
    private IOException failure;
    private boolean closed;

    public SqlAuditLog(Path directory, String database, SafeDiagnostics errors) throws IOException {
        Files.createDirectories(directory);
        path = directory.resolve(database + "-sql.log");
        writer = Files.newBufferedWriter(path, java.nio.file.StandardOpenOption.CREATE_NEW, java.nio.file.StandardOpenOption.WRITE);
        this.errors = errors;
        previous = CURRENT.get();
        CURRENT.set(this);
    }

    public Path path() { return path; }

    public static long attempt(String sql) {
        var log = CURRENT.get();
        if (log == null) return 0;
        long id = ++log.sequence;
        log.write(id, "PREPARE_ATTEMPT", "sql=" + sql);
        return id;
    }

    public static void prepared(PreparedStatement statement, long id) {
        var log = CURRENT.get();
        if (log != null) log.statements.put(statement, id);
    }

    public static void failed(long id, String stage, Throwable error) {
        var log = CURRENT.get();
        if (log == null) return;
        try {
            log.write(id, stage, log.errors.describe(error));
        } catch (UncheckedIOException logging) {
            // Keep the original driver/guard failure; batch closure still reports the log failure.
            error.addSuppressed(logging);
        }
    }

    public static void parameter(PreparedStatement statement, int index, String type, Object value) {
        var log = CURRENT.get();
        if (log != null)
            log.write(log.statements.getOrDefault(statement, 0L), "BIND_ATTEMPT",
                    "index=" + index + " type=" + type + " value=" + value);
    }

    public static long id(PreparedStatement statement) {
        var log = CURRENT.get();
        return log == null ? 0 : log.statements.getOrDefault(statement, 0L);
    }

    public static void execute(PreparedStatement statement) {
        var log = CURRENT.get();
        if (log != null)
            log.write(log.statements.getOrDefault(statement, 0L), "EXECUTE_ATTEMPT", "");
    }

    public static void executionFailed(PreparedStatement statement, Throwable error) {
        var log = CURRENT.get();
        if (log != null) failed(log.statements.getOrDefault(statement, 0L), "EXECUTE_FAILED", error);
    }

    public static IOException writeFailure() {
        var log = CURRENT.get();
        return log == null ? null : log.failure;
    }

    private void write(long id, String event, String detail) {
        if (failure != null) throw new UncheckedIOException(failure);
        try {
            writer.write(Instant.now() + " [db=" + errors.clean(MDC.get("database"))
                    + "] [sqlId=" + id + "] " + event + " " + errors.clean(detail));
            writer.newLine();
            // Complete the record before any potentially blocking JDBC operation starts.
            writer.flush();
        } catch (IOException e) {
            failure = e;
            throw new UncheckedIOException(e);
        }
    }

    @Override public void close() throws IOException {
        if (closed) return;
        closed = true;
        if (previous == null) CURRENT.remove(); else CURRENT.set(previous);
        statements.clear();
        try { writer.close(); }
        catch (IOException e) {
            if (failure == null) failure = e;
            else if (failure != e) failure.addSuppressed(e);
        }
        if (failure != null) throw failure;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/model/DbObjectRef.java =====
UTF8-BYTES: 214
SHA256: 4c854ed276caddd998dfb1f4002b8afa46d7ace6860a7cdcd99d09f082c82c07
===== CONTENT =====
package com.example.db2toolkit.model;

public record DbObjectRef(String schema, String name) {
    public String sqlName() {
        return Identifiers.quote(schema) + "." + Identifiers.quote(name);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/model/Identifiers.java =====
UTF8-BYTES: 3567
SHA256: 2d9c21a990ea26093b1aac791c16730e8ed79a2b584676d231e96d7c358aee3f
===== CONTENT =====
package com.example.db2toolkit.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Parses exactly two identifiers. No SQL fragments are accepted as catalog names. */
public final class Identifiers {
    private Identifiers() {}

    public static String quote(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    public static DbObjectRef parse(String input) {
        String s = input.strip();
        List<String> parts = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
            String value;
            if (i < s.length() && s.charAt(i) == '"') {
                i++;
                StringBuilder b = new StringBuilder();
                boolean closed = false;
                while (i < s.length()) {
                    char c = s.charAt(i++);
                    if (c == '"') {
                        if (i < s.length() && s.charAt(i) == '"') {
                            b.append('"');
                            i++;
                        } else {
                            closed = true;
                            break;
                        }
                    } else b.append(c);
                }
                if (!closed) throw new IllegalArgumentException("Unclosed quoted identifier");
                value = b.toString();
            } else {
                int start = i;
                while (i < s.length() && s.charAt(i) != '.' && !Character.isWhitespace(s.charAt(i)))
                    i++;
                value = s.substring(start, i);
                if (!value.matches("[\\p{L}_][\\p{L}\\p{N}_@$#]*"))
                    throw new IllegalArgumentException("Invalid unquoted identifier");
                value = foldOrdinaryIdentifier(value);
            }
            if (value.isBlank() || value.codePoints().anyMatch(Character::isISOControl))
                throw new IllegalArgumentException("Empty or control character in identifier");
            parts.add(value);
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
            if (i == s.length()) break;
            if (s.charAt(i++) != '.' || i == s.length())
                throw new IllegalArgumentException("Expected schema.objectName");
        }
        if (parts.size() != 2)
            throw new IllegalArgumentException("Expected exactly schema.objectName");
        return new DbObjectRef(parts.get(0), parts.get(1));
    }

    private static String foldOrdinaryIdentifier(String value) {
        // Db2 token folding differs from Java for some Unicode letters and database code pages.
        // Validation happens before connecting, so require exact quoted spelling instead of
        // guessing.
        for (int offset = 0; offset < value.length(); ) {
            int codePoint = value.codePointAt(offset);
            if (codePoint > 127) {
                String character = new String(Character.toChars(codePoint));
                if (!character.equals(character.toUpperCase(Locale.ROOT)))
                    throw new IllegalArgumentException(
                            "Non-ASCII identifier case cannot be normalized safely; use double"
                                + " quotes and the exact catalog spelling");
            }
            offset += Character.charCount(codePoint);
        }
        return value.toUpperCase(Locale.ROOT);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/output/OutputFileNames.java =====
UTF8-BYTES: 3141
SHA256: 53388e96589ca9fca0e8fccf4a74f4c672b435c5ad712b48639ab88b1071a283
===== CONTENT =====
package com.example.db2toolkit.output;

import com.example.db2toolkit.ddl.model.DdlObjectType;
import com.example.db2toolkit.model.DbObjectRef;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Pure naming policy. Collision state and file writes belong to SqlFileWriter. */
public final class OutputFileNames {
    private static final int MAX_READABLE_BYTES = 180;

    private OutputFileNames() {}

    public static String preferred(DdlObjectType type, DbObjectRef ref, String specific) {
        String base = ref.schema() + "." + ref.name();
        return readable(ref, base) ? base + ".sql" : fallback(type, ref, specific);
    }

    public static String overload(DdlObjectType type, DbObjectRef ref, String specific) {
        String base = ref.schema() + "." + ref.name() + "." + specific;
        return specific != null && safe(specific) && readable(ref, base)
                ? base + ".sql"
                : fallback(type, ref, specific);
    }

    private static boolean readable(DbObjectRef ref, String base) {
        return safe(ref.schema())
                && safe(ref.name())
                && base.getBytes(StandardCharsets.UTF_8).length <= MAX_READABLE_BYTES
                // Windows also treats superscript 1, 2 and 3 as reserved COM/LPT device digits.
                && !ref.schema()
                        .matches(
                                "(?i)CON|PRN|AUX|NUL|COM[1-9\u00B9\u00B2\u00B3]|LPT[1-9\u00B9\u00B2\u00B3]");
    }

    private static boolean safe(String value) {
        return value.matches("[\\p{L}\\p{N}_@$#-]+");
    }

    static String identity(DdlObjectType type, DbObjectRef ref, String specific) {
        return part(type.name()) + part(ref.schema()) + part(ref.name()) + part(specific);
    }

    public static String fallback(DdlObjectType type, DbObjectRef ref, String specific) {
        String readable =
                label(ref.schema())
                        + "--"
                        + label(ref.name())
                        + (specific == null ? "" : "--" + label(specific));
        try {
            String hash =
                    HexFormat.of()
                            .formatHex(
                                    MessageDigest.getInstance("SHA-256")
                                            .digest(
                                                    identity(type, ref, specific)
                                                            .getBytes(StandardCharsets.UTF_8)));
            return "obj-" + readable + "--" + hash + ".sql";
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String part(String value) {
        return value == null ? "-1:" : value.length() + ":" + value;
    }

    private static String label(String value) {
        String safe = value.replaceAll("[^A-Za-z0-9_-]", "_");
        return safe.substring(0, Math.min(20, safe.length()));
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/output/RunLogs.java =====
UTF8-BYTES: 6334
SHA256: 4a39f1bf14a8b2b52f3b216f8878ff0e2179b6cfd0a082984dd542910c5c2526
===== CONTENT =====
package com.example.db2toolkit.output;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import com.example.db2toolkit.jdbc.SafeDiagnostics;

import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

/**
 * Synchronous, flushed run logs isolated by owner thread and run scope.
 */
public final class RunLogs extends AppenderBase<ILoggingEvent> implements AutoCloseable {
    public static final String SCOPE_KEY = "jdbcRunScope";
    public static final org.slf4j.Marker PROGRESS = org.slf4j.MarkerFactory.getMarker("JDBC_PROGRESS");
    private final String scope = java.util.UUID.randomUUID().toString();
    private final String previousScope = org.slf4j.MDC.get(SCOPE_KEY);
    public static final org.slf4j.Marker SUMMARY = org.slf4j.MarkerFactory.getMarker("SUMMARY");
    public static final org.slf4j.Marker OUTCOME = org.slf4j.MarkerFactory.getMarker("RUN_OUTCOME");
    public static final org.slf4j.Marker SUCCESS =
            org.slf4j.MarkerFactory.getMarker("SUMMARY_SUCCESS");
    private final Logger logger = (Logger) LoggerFactory.getLogger("com.example.db2toolkit");
    private static final Object LEVEL_LOCK = new Object();
    private static Level previousLevel;
    private static int activeScopes;
    private boolean closed;
    private final String database;
    private final Thread owner = Thread.currentThread();
    private final SafeDiagnostics errors;
    private final BufferedWriter process;
    private final BufferedWriter success;
    private final BufferedWriter issue;
    private volatile IOException failure;

    public RunLogs(Path directory, String database, SafeDiagnostics errors) throws IOException {
        this.database = database;
        this.errors = errors;
        process = Files.newBufferedWriter(directory.resolve(database + "-process.log"));
        try {
            success = Files.newBufferedWriter(directory.resolve(database + "-summary_success.log"));
        } catch (IOException e) {
            try {
                process.close();
            } catch (IOException close) {
                e.addSuppressed(close);
            }
            throw e;
        }
        try {
            issue = Files.newBufferedWriter(directory.resolve(database + "-summary_issue.log"));
        } catch (IOException e) {
            try {
                success.close();
            } catch (IOException close) {
                e.addSuppressed(close);
            }
            try {
                process.close();
            } catch (IOException close) {
                e.addSuppressed(close);
            }
            throw e;
        }
        setContext(logger.getLoggerContext());
        setName("run-" + database);
        start();
        synchronized (LEVEL_LOCK) {
            if (activeScopes++ == 0) {
                previousLevel = logger.getLevel();
                if (!logger.isInfoEnabled()) logger.setLevel(Level.INFO);
            }
        }
        logger.addAppender(this);
        org.slf4j.MDC.put(SCOPE_KEY, scope);
    }

    @Override
    public void doAppend(ILoggingEvent event) {
        // Filter before AppenderBase acquires its writer lock: another database's blocked
        // log file must not serialize all workers just to reject their unrelated events.
        if (!database.equals(event.getMDCPropertyMap().get("database"))) return;
        boolean scopedProgress = event.getMarkerList() != null && event.getMarkerList().contains(PROGRESS)
                && scope.equals(event.getMDCPropertyMap().get(SCOPE_KEY));
        if (Thread.currentThread() != owner && !scopedProgress) return;
        super.doAppend(event);
    }

    @Override
    protected void append(ILoggingEvent event) {
        String line =
                Instant.ofEpochMilli(event.getTimeStamp())
                        + " "
                        + event.getLevel()
                        + " [db="
                        + database
                        + "] "
                        + errors.clean(event.getFormattedMessage())
                        + System.lineSeparator();
        // A failure in one sink must not prevent the other, still-writable summaries from recording
        // it.
        write(process, line);
        var markers = event.getMarkerList();
        boolean outcome = markers != null && markers.contains(OUTCOME);
        boolean successful = markers != null && markers.contains(SUCCESS);
        boolean summary = markers != null && markers.contains(SUMMARY);
        if (outcome) {
            write(success, line);
            write(issue, line);
        } else if (successful || summary) {
            write(successful ? success : issue, line);
        }
    }

    private void write(BufferedWriter destination, String line) {
        try {
            destination.write(line);
            destination.flush();
        } catch (IOException e) {
            // Logback can swallow appender exceptions. Surface this failure at close so the run
            // exits nonzero.
            if (failure == null) failure = e;
        }
    }

    /**
     * Allows the application to include an already-observed logging failure in its final outcome.
     */
    public IOException writeFailure() {
        return failure;
    }

    @Override
    public synchronized void close() throws IOException {
        if (closed) return;
        closed = true;
        logger.detachAppender(this);
        if (previousScope == null) org.slf4j.MDC.remove(SCOPE_KEY);
        else org.slf4j.MDC.put(SCOPE_KEY, previousScope);
        synchronized (LEVEL_LOCK) {
            if (--activeScopes == 0) logger.setLevel(previousLevel);
        }
        stop();
        try {
            process.close();
        } catch (IOException e) {
            if (failure == null) failure = e;
        }
        try {
            success.close();
        } catch (IOException e) {
            if (failure == null) failure = e;
        }
        try {
            issue.close();
        } catch (IOException e) {
            if (failure == null) failure = e;
        }
        if (failure != null) throw failure;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/output/SqlFileWriter.java =====
UTF8-BYTES: 4241
SHA256: 0ebe9ba8671006ad7fa5c4269cdd5306828783d31aff8bc057e035109ef49cd2
===== CONTENT =====
package com.example.db2toolkit.output;

import com.example.db2toolkit.ddl.model.DdlObjectType;
import com.example.db2toolkit.model.DbObjectRef;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Owns the run directory, collision bookkeeping, terminator and staged file publication. */
public final class SqlFileWriter {
    private final Path runDirectory;
    private final Set<String> used = new HashSet<>();
    private final Set<String> identities = new HashSet<>();

    public SqlFileWriter(Path output) throws IOException {
        Files.createDirectories(output);
        runDirectory = Files.createTempDirectory(output.toAbsolutePath(), "run-");
        for (var type : DdlObjectType.values())
            Files.createDirectory(runDirectory.resolve(type.directory()));
    }

    public Path runDirectory() {
        return runDirectory;
    }

    public static String fileName(DdlObjectType type, DbObjectRef ref, String specific) {
        return OutputFileNames.preferred(type, ref, specific);
    }

    public static String terminate(String ddl) {
        return terminate(DdlObjectType.PROCEDURE, ddl);
    }

    public static String terminate(DdlObjectType type, String ddl) {
        int end = trimNewlines(ddl, ddl.length());
        // Look through blank lines for trailing client markers, but commit removal only when a
        // marker is found. This preserves body whitespace when those lines contain no marker.
        int scanEnd = end;
        while (scanEnd > 0) {
            int start = scanEnd;
            while (start > 0 && ddl.charAt(start - 1) != '\n' && ddl.charAt(start - 1) != '\r')
                start--;
            String line = ddl.substring(start, scanEnd).strip();
            if (line.equals("@")) end = trimNewlines(ddl, start);
            else if (!line.isEmpty()) break;
            scanEnd = trimNewlines(ddl, start);
        }
        String content = ddl.substring(0, end);
        return content + "\n@\n";
    }

    private static int trimNewlines(String value, int end) {
        while (end > 0 && (value.charAt(end - 1) == '\n' || value.charAt(end - 1) == '\r')) end--;
        return end;
    }

    public Path write(DdlObjectType type, DbObjectRef ref, String specific, String ddl)
            throws IOException {
        String identity = OutputFileNames.identity(type, ref, specific);
        if (identities.contains(identity)) throw new IOException("Duplicate output identity");
        Path target = selectTarget(type, ref, specific);
        publish(target, terminate(type, ddl));
        // Failed writes must not reserve identities or consume the simple filename.
        identities.add(identity);
        used.add(pathKey(target));
        return target;
    }

    private Path selectTarget(DdlObjectType type, DbObjectRef ref, String specific)
            throws IOException {
        Path directory = runDirectory.resolve(type.directory());
        List<String> candidates =
                List.of(
                        OutputFileNames.preferred(type, ref, specific),
                        OutputFileNames.overload(type, ref, specific),
                        OutputFileNames.fallback(type, ref, specific));
        for (String name : candidates) {
            Path target = directory.resolve(name);
            if (!used.contains(pathKey(target)) && !Files.exists(target)) return target;
        }
        throw new IOException("Filename collision");
    }

    private static String pathKey(Path path) {
        return path.toString().toLowerCase(Locale.ROOT);
    }

    private static void publish(Path target, String content) throws IOException {
        try (var staging = TemporaryOutputFile.create(target.getParent(), ".writing-")) {
            Path temp = staging.path();
            Files.writeString(temp, content, StandardCharsets.UTF_8);
            // ATOMIC_MOVE may replace an existing target even without REPLACE_EXISTING.
            // Preserve files created after selectTarget(), like the non-overwriting Excel path.
            Files.move(temp, target);
        }
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/output/TemporaryOutputFile.java =====
UTF8-BYTES: 818
SHA256: 7980be929f7b5a24fe5684ba71fcd8fa6cd634a834be8ee99b11474afd7e39d6
===== CONTENT =====
package com.example.db2toolkit.output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** A staging file whose cleanup participates in try-with-resources exception suppression. */
public record TemporaryOutputFile(Path path) implements AutoCloseable {
    public static TemporaryOutputFile create(Path directory, String prefix) throws IOException {
        return new TemporaryOutputFile(Files.createTempFile(directory, prefix, ".tmp"));
    }

    @Override
    public void close() throws IOException {
        // A successful move already removed this path. If publication failed, retain that primary
        // exception even when deleting the staging file also fails (for example, a lost
        // filesystem).
        Files.deleteIfExists(path);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/spreadsheet/ExcelText.java =====
UTF8-BYTES: 3446
SHA256: ffb26ccf6fda0ea7b650fa8e3083721e685de335cb10112d1f39d45f5f5f977e
===== CONTENT =====
package com.example.db2toolkit.spreadsheet;

import java.io.IOException;

/** Encodes SpreadsheetML text without interpreting database strings as OOXML escape sequences. */
public final class ExcelText {
    private static final String HEX = "0123456789ABCDEF";

    private ExcelText() {}

    public static void validate(String value) throws IOException {
        if (value.length() > 32767)
            throw new IOException(
                    "Cell text exceeds Excel's 32767-character limit; no truncation performed");
        // Validate before rows can be flushed, so malformed Unicode fails only the current task.
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isHighSurrogate(c)) {
                if (++i == value.length() || !Character.isLowSurrogate(value.charAt(i)))
                    throw new IOException(
                            "Cell text contains an unpaired Unicode surrogate; no replacement"
                                + " performed");
            } else if (Character.isLowSurrogate(c))
                throw new IOException(
                        "Cell text contains an unpaired Unicode surrogate; no replacement"
                            + " performed");
        }
    }

    static String richText(String value) throws IOException {
        StringBuilder encoded = new StringBuilder(value.length());
        encode(encoded, value, false);
        return encoded.toString();
    }

    static void encode(Appendable out, String value, boolean xml) throws IOException {
        // Encode only at serialization for SXSSF. Escaped XML can exceed 32767 characters while
        // the logical cell value remains valid; no shared-string table or expanded row cache is
        // used.
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '_' && escapeAt(value, i)) out.append("_x005F_");
            else if ((c < 32 && c != '\r' && c != '\n' && c != '\t')
                    || c == '\uFFFE'
                    || c == '\uFFFF') {
                out.append("_x");
                for (int shift = 12; shift >= 0; shift -= 4)
                    out.append(HEX.charAt((c >>> shift) & 15));
                out.append('_');
            } else if (xml) {
                switch (c) {
                    case '&' -> out.append("&amp;");
                    case '<' -> out.append("&lt;");
                    case '>' -> out.append("&gt;");
                    case '"' -> out.append("&quot;");
                    case '\'' -> out.append("&apos;");
                    case '\r' -> out.append("&#xD;");
                    case '\n' -> out.append("&#xA;");
                    case '\t' -> out.append("&#x9;");
                    default -> out.append(c);
                }
            } else out.append(c);
        }
    }

    private static boolean escapeAt(String value, int offset) {
        if (offset + 6 >= value.length()
                || value.charAt(offset + 1) != 'x'
                || value.charAt(offset + 6) != '_') return false;
        for (int i = offset + 2; i < offset + 6; i++) {
            char c = value.charAt(i);
            if (!(c >= '0' && c <= '9') && !(c >= 'a' && c <= 'f') && !(c >= 'A' && c <= 'F'))
                return false;
        }
        return true;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/spreadsheet/ExcelValueWriter.java =====
UTF8-BYTES: 6026
SHA256: 3b3c657c5bc4958a7fd63a1baf2924a14724196541098390a097dcbf2029ff69
===== CONTENT =====
package com.example.db2toolkit.spreadsheet;

import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.time.*;

/** Excel stores only 15 significant decimal digits; longer values remain exact text. */
public final class ExcelValueWriter {
    private final CellStyle date, timestamp, time;

    public ExcelValueWriter(Workbook workbook) {
        date = style(workbook, "yyyy-mm-dd");
        timestamp = style(workbook, "yyyy-mm-dd hh:mm:ss.000");
        time = style(workbook, "hh:mm:ss.000");
    }

    private static CellStyle style(Workbook book, String format) {
        CellStyle style = book.createCellStyle();
        style.setDataFormat(book.createDataFormat().getFormat(format));
        return style;
    }

    public void write(Cell cell, Object value) throws SQLException, IOException {
        if (value == null) {
            cell.setBlank();
            return;
        }
        if (value instanceof Clob clob) {
            // Free the LOB even when opening its reader fails, and preserve earlier errors if free
            // also fails.
            try (SqlResource resource = clob::free;
                    Reader reader = clob.getCharacterStream()) {
                text(cell, boundedText(reader));
            }
        } else if (value instanceof String s) text(cell, s);
        else if (value instanceof Boolean b) cell.setCellValue(b);
        else if (value instanceof BigDecimal d) decimal(cell, d);
        else if (value instanceof BigInteger n) decimal(cell, new BigDecimal(n));
        else if (value instanceof Byte
                || value instanceof Short
                || value instanceof Integer
                || value instanceof Long)
            decimal(cell, BigDecimal.valueOf(((Number) value).longValue()));
        else if (value instanceof Float || value instanceof Double) {
            double n = ((Number) value).doubleValue();
            if (excelNumber(n)) cell.setCellValue(n);
            else text(cell, value.toString());
        } else if (value instanceof Timestamp t) datetime(cell, t.toLocalDateTime());
        else if (value instanceof java.sql.Date d) date(cell, d.toLocalDate());
        else if (value instanceof Time t) time(cell, t.toLocalTime());
        else if (value instanceof LocalDateTime t) datetime(cell, t);
        else if (value instanceof LocalDate d) date(cell, d);
        else if (value instanceof LocalTime t) time(cell, t);
        else if (value instanceof OffsetDateTime || value instanceof OffsetTime)
            text(cell, value.toString());
        else if (value instanceof byte[] bytes)
            text(cell, java.util.HexFormat.of().formatHex(bytes));
        else if (value instanceof Blob blob) {
            try (SqlResource resource = blob::free) {
                throw new IOException(
                        "BLOB export is unsupported; select an explicit text representation");
            }
        } else
            throw new IOException(
                    "Unsupported JDBC value type: " + value.getClass().getSimpleName());
    }

    private static void decimal(Cell cell, BigDecimal value) throws IOException {
        double number = value.doubleValue();
        // Excel limits decimal precision; the round-trip check additionally rejects double
        // overflow/underflow.
        if (value.precision() <= 15
                && excelNumber(number)
                && BigDecimal.valueOf(number).compareTo(value) == 0) cell.setCellValue(number);
        else text(cell, value.toPlainString());
    }

    private static boolean excelNumber(double value) {
        // Excel does not support IEEE-754 subnormal numbers even though Java double can represent
        // them.
        return Double.isFinite(value) && (value == 0 || Math.abs(value) >= Double.MIN_NORMAL);
    }

    private void date(Cell cell, LocalDate value) throws IOException {
        if (value.getYear() < 1900 || value.getYear() > 9999) text(cell, value.toString());
        else {
            cell.setCellValue(value);
            cell.setCellStyle(date);
        }
    }

    private void datetime(Cell cell, LocalDateTime value) throws IOException {
        if (value.getYear() < 1900 || value.getYear() > 9999 || value.getNano() % 1_000_000 != 0)
            text(cell, value.toString());
        else {
            cell.setCellValue(value);
            cell.setCellStyle(timestamp);
        }
    }

    private void time(Cell cell, LocalTime value) throws IOException {
        if (value.getNano() % 1_000_000 != 0) text(cell, value.toString());
        else {
            cell.setCellValue(value.toNanoOfDay() / 86_400_000_000_000d);
            cell.setCellStyle(time);
        }
    }

    public static void text(Cell cell, String value) throws IOException {
        ExcelText.validate(value);
        // XSSF stores OOXML rich text immediately; SXSSF keeps the original string until flush.
        if (cell instanceof org.apache.poi.xssf.usermodel.XSSFCell)
            cell.setCellValue(
                    new org.apache.poi.xssf.usermodel.XSSFRichTextString(
                            ExcelText.richText(value)));
        else cell.setCellValue(value); // Never interpret database text as an Excel formula.
    }

    private static String boundedText(Reader reader) throws IOException {
        StringBuilder result = new StringBuilder();
        char[] buffer = new char[4096];
        int n;
        while ((n = reader.read(buffer)) != -1) {
            if (result.length() + n > 32767)
                throw new IOException("CLOB exceeds Excel's 32767-character cell limit");
            result.append(buffer, 0, n);
        }
        return result.toString();
    }

    @FunctionalInterface
    private interface SqlResource extends AutoCloseable {
        @Override
        void close() throws SQLException;
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/spreadsheet/StreamingWorkbooks.java =====
UTF8-BYTES: 720
SHA256: d4a12456f2951a4e8d6bc6be5be4b5588eecae17ab97f441faf01a26c92c2c4b
===== CONTENT =====
package com.example.db2toolkit.spreadsheet;

import org.apache.poi.xssf.streaming.*;

import java.io.IOException;

public final class StreamingWorkbooks {
    private StreamingWorkbooks() {}

    public static SXSSFWorkbook streamingWorkbook() {
        return new SXSSFWorkbook(100) {
            @Override
            protected SheetDataWriter createSheetDataWriter() throws IOException {
                return new GZIPSheetDataWriter() {
                    @Override
                    protected void outputEscapedString(String value) throws IOException {
                        ExcelText.encode(_out, value, true);
                    }
                };
            }
        };
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/sql/Db2TypeRenderer.java =====
UTF8-BYTES: 5035
SHA256: 26fe4611019563ddd56e417adffa2d8bcea96e41c128093fc59cf42016dc96ea
===== CONTENT =====
package com.example.db2toolkit.sql;

import com.example.db2toolkit.ddl.model.DdlUnavailableException;

import java.util.Set;

/** Catalog-aware type rendering, including the explicitly requested compact display rules. */
public final class Db2TypeRenderer {
    public record Type(
            String schema,
            String name,
            long length,
            int scale,
            String units,
            Long unitsLength,
            int codepage) {}

    private static final Set<String> SIMPLE =
            Set.of("SMALLINT", "INTEGER", "BIGINT", "REAL", "DOUBLE", "DATE", "TIME", "BOOLEAN");
    private static final Set<String> BINARY = Set.of("BINARY", "VARBINARY", "BLOB");
    private static final Set<String> CHARACTER =
            Set.of("CHARACTER", "CHAR", "VARCHAR", "CLOB", "GRAPHIC", "VARGRAPHIC", "DBCLOB");
    private static final Set<String> GRAPHIC = Set.of("GRAPHIC", "VARGRAPHIC", "DBCLOB");
    private static final Set<String> CHARACTER_UNITS = Set.of("OCTETS", "CODEUNITS32");
    private static final Set<String> GRAPHIC_UNITS = Set.of("CODEUNITS16", "CODEUNITS32");

    public String render(Type type) throws DdlUnavailableException {
        // Built-in TYPESCHEMA may contain CHAR padding, observed in the real Db2 catalog.
        if (type.schema() == null || !"SYSIBM".equals(type.schema().stripTrailing())) {
            throw unavailable("User-defined type cannot be reconstructed");
        }
        if (type.name() == null) throw unavailable("Missing type name");
        String name = type.name().stripTrailing();
        if (SIMPLE.contains(name)) return name;
        if (BINARY.contains(name)) return name + "(" + positive(type.length()) + ")";
        if (CHARACTER.contains(name)) return character(type, name);
        return switch (name) {
            case "DECIMAL" -> decimal(type);
            case "TIMESTAMP" -> timestamp(type);
            case "DECFLOAT" -> decimalFloat(type);
            default -> throw unavailable("Unsupported type: " + name);
        };
    }

    private static String decimal(Type type) throws DdlUnavailableException {
        if (type.length() < 1
                || type.length() > 31
                || type.scale() < 0
                || type.scale() > type.length()) {
            throw unavailable("Invalid DECIMAL precision/scale");
        }
        return "DECIMAL(" + type.length() + "," + type.scale() + ")";
    }

    private static String timestamp(Type type) throws DdlUnavailableException {
        if (type.scale() < 0 || type.scale() > 12) throw unavailable("Invalid TIMESTAMP precision");
        // User-approved simplification: rebuilding uses the default timestamp precision.
        return "TIMESTAMP";
    }

    private static String decimalFloat(Type type) throws DdlUnavailableException {
        if (type.length() != 8 && type.length() != 16)
            throw unavailable("Invalid DECFLOAT storage length");
        return "DECFLOAT(" + (type.length() == 8 ? 16 : 34) + ")";
    }

    private static String character(Type type, String name) throws DdlUnavailableException {
        boolean graphic = GRAPHIC.contains(name);
        boolean bitData = !graphic && type.codepage() == 0;
        if (bitData && "CLOB".equals(name)) throw unavailable("CLOB with unknown/binary code page");
        String units = type.units() == null ? "" : type.units().strip();
        long length = type.length();
        String suffix = "";
        if (!units.isEmpty()) {
            Set<String> supportedUnits = graphic ? GRAPHIC_UNITS : CHARACTER_UNITS;
            if (!supportedUnits.contains(units))
                throw unavailable("Unsupported string length units");
            if (type.unitsLength() == null)
                throw unavailable("Missing declared string unit length");
            if (bitData && !"OCTETS".equals(units))
                throw unavailable("Binary character data with non-OCTETS units");
            length = type.unitsLength();
            // Compact character declarations follow the execution environment's string units.
            if (!bitData && !Set.of("CHAR", "CHARACTER", "VARCHAR").contains(name))
                suffix = " " + units;
        } else if (graphic) {
            throw unavailable(
                    "Graphic type requires explicit catalog string units and declared length");
        }
        String displayName = "CHAR".equals(name) ? "CHARACTER" : name;
        return displayName
                + "("
                + positive(length)
                + suffix
                + ")"
                + (bitData ? " FOR BIT DATA" : "");
    }

    private static long positive(long value) throws DdlUnavailableException {
        if (value <= 0) throw unavailable("Invalid type length");
        return value;
    }

    private static DdlUnavailableException unavailable(String message) {
        return new DdlUnavailableException(message);
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/sql/RoutineDefinitionValidator.java =====
UTF8-BYTES: 3049
SHA256: b92d3f45aedad6f543387d6288952bc6e5bea1b308c0b3ff18b9794e9ca3c9f4
===== CONTENT =====
package com.example.db2toolkit.sql;

import com.example.db2toolkit.ddl.model.DdlObjectType;

/** Checks only the CREATE prefix, without parsing or rewriting routine logic. */
public final class RoutineDefinitionValidator {
    private RoutineDefinitionValidator() {}

    public static boolean hasCreateDefinition(DdlObjectType type, String ddl) {
        if (ddl == null || ddl.isBlank() || type == DdlObjectType.TABLE) return false;
        PrefixReader reader = new PrefixReader(ddl);
        if (!reader.nextWord().equalsIgnoreCase("CREATE")) return false;
        String word = reader.nextWord();
        if (word.equalsIgnoreCase("OR")) {
            if (!reader.nextWord().equalsIgnoreCase("REPLACE")) return false;
            word = reader.nextWord();
        }
        return word.equalsIgnoreCase(type.name()) && reader.hasDefinitionStart();
    }

    /** Iterative scan avoids regex backtracking/stack overflow on long leading comments. */
    private static final class PrefixReader {
        private final String text;
        private int position;

        PrefixReader(String text) {
            this.text = text;
            position = text.startsWith("\uFEFF") ? 1 : 0;
        }

        String nextWord() {
            skipTrivia();
            int start = position;
            while (position < text.length() && Character.isLetter(text.charAt(position)))
                position++;
            if (position < text.length()) {
                char next = text.charAt(position);
                if (!Character.isWhitespace(next)
                        && next != '"'
                        && !text.startsWith("/*", position)
                        && !text.startsWith("--", position)) return "";
            }
            return text.substring(start, position);
        }

        boolean hasDefinitionStart() {
            skipTrivia();
            return position < text.length();
        }

        private void skipTrivia() {
            while (position < text.length()) {
                if (Character.isWhitespace(text.charAt(position))) {
                    position++;
                } else if (text.startsWith("--", position)) {
                    while (position < text.length()
                            && text.charAt(position) != '\n'
                            && text.charAt(position) != '\r') position++;
                } else if (text.startsWith("/*", position)) {
                    position += 2;
                    int depth = 1;
                    while (position < text.length() && depth > 0) {
                        if (text.startsWith("/*", position)) {
                            depth++;
                            position += 2;
                        } else if (text.startsWith("*/", position)) {
                            depth--;
                            position += 2;
                        } else position++;
                    }
                } else return;
            }
        }
    }
}

===== END FILE =====

===== FILE: src/main/java/com/example/db2toolkit/sql/TableDdlFormatter.java =====
UTF8-BYTES: 2482
SHA256: 5624b102b213d04257b71715fffed5becb159d6859afea7aabc5ffa372f05028
===== CONTENT =====
package com.example.db2toolkit.sql;

import com.example.db2toolkit.model.Identifiers;

import java.util.*;

/** Pure table layout and identifier presentation. No JDBC or filesystem access. */
public final class TableDdlFormatter {
    public record ColumnDefinition(String name, String type, String attributes) {}

    public static String formatColumns(List<ColumnDefinition> columns) {
        int nameWidth = columns.stream().mapToInt(c -> c.name().length()).max().orElse(0);
        int typeWidth = columns.stream().mapToInt(c -> c.type().length()).max().orElse(0);
        List<String> lines = new ArrayList<>();
        for (var column : columns) {
            String line =
                    "    "
                            + column.name()
                            + " ".repeat(nameWidth - column.name().length() + 1)
                            + column.type();
            if (!column.attributes().isEmpty())
                line += " ".repeat(typeWidth - column.type().length() + 1) + column.attributes();
            lines.add(line);
        }
        return String.join(",\n", lines);
    }

    // Keep delimiters for SQL keywords and names whose case/characters require them.
    private static final Set<String> KEYWORDS =
            Set.of(
                    ("ALL ALTER AND ANY AS ASC AUTHORIZATION BEGIN BETWEEN BY CASE CHECK COLUMN"
                         + " CONNECT CONSTRAINT CREATE CROSS CURRENT CURRENT_DATE CURRENT_TIME"
                         + " CURRENT_TIMESTAMP CURRENT_USER SESSION_USER SYSTEM_USER CURRENT_SCHEMA"
                         + " CURRENT_SERVER CURRENT_PATH CURRENT_ROLE CURRENT_TIMEZONE"
                         + " LOCALTIMESTAMP DEFAULT DELETE DESC DISTINCT DROP ELSE END EXCEPT"
                         + " EXISTS FALSE FETCH FOR FOREIGN FROM FULL GRANT GROUP HAVING IN INDEX"
                         + " INNER INSERT INTERSECT INTO IS JOIN LEFT LIKE NOT NULL OFFSET ON OR"
                         + " ORDER OUTER PRIMARY REFERENCES RIGHT ROW ROWS SCHEMA SELECT SESSION"
                         + " SET SOME TABLE THEN TO TRUE UNION UNIQUE UPDATE USER USING VALUES VIEW"
                         + " WHEN WHERE WITH")
                            .split(" "));

    public static String tableIdentifier(String name) {
        return name.matches("[A-Z][A-Z0-9_]*") && !KEYWORDS.contains(name)
                ? name
                : Identifiers.quote(name);
    }
}

===== END FILE =====

===== FILE: src/main/resources/logback.xml =====
UTF8-BYTES: 298
SHA256: db44d310566e008584a19b0199b9c4c3bd5d96ac42f724c14c7d5fd428285e37
===== CONTENT =====
<configuration>
  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder><charset>UTF-8</charset><pattern>%d{HH:mm:ss} %-5level [db=%X{database:-global}] %msg%n</pattern></encoder>
  </appender>
  <root level="INFO"><appender-ref ref="CONSOLE"/></root>
</configuration>

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/application/MultiDatabaseExportTest.java =====
UTF8-BYTES: 34768
SHA256: 5644955a6963aa20a2deeae6724193002add23fb09aab1049e42d2a27733f71c
===== CONTENT =====
package com.example.db2toolkit.application;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.StringReader;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

class MultiDatabaseExportTest {
    @TempDir Path dir;

    @Test
    void interruptedCoordinatorWaitsForWorkersAndReportsBatchFailure() throws Exception {
        Path file = config();
        var opened = new java.util.concurrent.CountDownLatch(2);
        var release = new java.util.concurrent.CountDownLatch(1);
        var closed = new java.util.concurrent.atomic.AtomicInteger();
        var result = new java.util.concurrent.atomic.AtomicInteger(-1);
        var app = new ToolkitApplication(key -> "secret", config -> {
            opened.countDown();
            try { assertTrue(release.await(5, java.util.concurrent.TimeUnit.SECONDS)); }
            catch (InterruptedException e) { throw new SQLException(e); }
            var connection = routineConnection();
            doAnswer(call -> { closed.incrementAndGet(); return null; }).when(connection).close();
            return connection;
        });
        var coordinator = new Thread(() -> result.set(app.run(new String[] {file.toString()})));
        coordinator.start();
        try {
            assertTrue(opened.await(5, java.util.concurrent.TimeUnit.SECONDS));
            coordinator.interrupt();
        } finally { release.countDown(); coordinator.join(8000); }
        assertFalse(coordinator.isAlive());
        assertEquals(2, result.get());
        assertEquals(2, closed.get());
    }

    @Test
    void databasesOverlapWithOneConnectionEachAndIndependentCompletion() throws Exception {
        Path file = config();
        var first = routineConnection();
        var second = routineConnection();
        var bothOpened = new java.util.concurrent.CyclicBarrier(2);
        var secondClosed = new java.util.concurrent.CountDownLatch(1);
        var counts = new java.util.concurrent.ConcurrentHashMap<String, Integer>();
        var firstStatement = first.prepareStatement("test setup");
        var firstRows = firstStatement.executeQuery();
        when(firstStatement.executeQuery()).thenAnswer(call -> {
            assertTrue(secondClosed.await(5, java.util.concurrent.TimeUnit.SECONDS), "Second database must finish while first is waiting");
            return firstRows;
        });
        doAnswer(call -> { secondClosed.countDown(); return null; }).when(second).close();
        int code = new ToolkitApplication(key -> "secret", config -> {
            counts.merge(config.username(), 1, Integer::sum);
            try { bothOpened.await(5, java.util.concurrent.TimeUnit.SECONDS); }
            catch (Exception e) { throw new SQLException("Databases did not overlap", e); }
            return config.username().equals("first") ? first : second;
        }).run(new String[] {file.toString()});
        assertEquals(0, code);
        assertEquals(Map.of("first", 1, "second", 1), counts);
        verify(first).close(); verify(second).close();
        for (String alias : List.of("first", "second")) {
            String other = alias.equals("first") ? "second" : "first";
            assertFalse(runLog(alias, "sql.log").contains("[db=" + other + "]"));
            assertFalse(runLog(alias, "process.log").contains("[db=" + other + "]"));
        }
    }

    @Test
    void timeoutEscalationSkipsBrokenConnectionAndNextDatabaseRuns() throws Exception {
        Path file = config();
        Files.writeString(file, "\ndb.query-timeout-seconds=1\ndb.cancel-grace-seconds=1\ndb.slow-query-seconds=0\n", StandardOpenOption.APPEND);
        Connection first = routineConnection();
        PreparedStatement statement = first.prepareStatement("test setup");
        var aborted = new java.util.concurrent.CountDownLatch(1);
        when(statement.getConnection()).thenReturn(first);
        doAnswer(call -> { aborted.countDown(); return null; }).when(first).abort(any());
        when(statement.executeQuery()).thenAnswer(call -> {
            assertTrue(aborted.await(6, java.util.concurrent.TimeUnit.SECONDS));
            throw new SQLTimeoutException("Injected timeout", "57014");
        });
        var opened = new java.util.concurrent.atomic.AtomicInteger();
        assertEquals(1, new ToolkitApplication(key -> "secret", target ->
                (opened.incrementAndGet() > 0 && target.username().equals("first")) ? first : routineConnection())
                .run(new String[] {file.toString()}));
        assertEquals(2, opened.get());
        assertTrue(runLog("first", "summary_issue.log").contains("TIMEOUT"));
        assertTrue(runLog("second", "summary_success.log").contains("exitCode=0"));
        verify(statement).cancel();
        verify(first).abort(any());
    }

    @Test
    void databasesHaveSeparateSqlLogsWithBoundCatalogParameters() throws Exception {
        Path config = config();
        assertEquals(0, new ToolkitApplication(key -> "secret", target -> routineConnection())
                .run(new String[] {config.toString()}));
        for (String alias : List.of("first", "second")) {
            String text = runLog(alias, "sql.log");
            assertTrue(text.contains("[db=" + alias + "]"));
            assertFalse(text.contains("[db=" + (alias.equals("first") ? "second" : "first") + "]"));
            assertEquals(1, text.lines().filter(line -> line.contains("PREPARE_ATTEMPT")).count());
            assertEquals(1, text.lines().filter(line -> line.contains("EXECUTE_ATTEMPT")).count());
            assertTrue(text.contains("index=1 type=VARCHAR value=RPT"));
            assertTrue(text.contains("index=2 type=VARCHAR value=P"));
            assertFalse(text.contains("secret"));
            assertFalse(text.contains("CREATE PROCEDURE"));
        }
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {
        "catalog.table.veiw=CUSTOM.OBJECTS", "export.enabeld=true",
        "excel-export.confg=missing.yaml"})
    void unknownGlobalKeysFailPreflightInsteadOfReportingSuccessfulNoOp(String setting)
            throws Exception {
        Path file = dir.resolve("unknown-key.properties");
        Files.writeString(file, "db.url=jdbc:db2://localhost:25000/TESTDB\n"
                + "db.username=test\ndb.password=secret\nexport.enabled=false\n"
                + "export.output-directory=" + dir.toString().replace('\\', '/') + "/output\n");
        var application = new ToolkitApplication(name -> null, config -> {
            throw new AssertionError("Preflight and an explicit no-op must not open a database");
        });
        assertEquals(0, application.run(new String[] {file.toString()}),
                "Explicitly disabling all features remains a valid no-op");
        Files.writeString(file, setting + "\n", StandardOpenOption.APPEND);
        assertEquals(2, application.run(new String[] {file.toString()}));
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {"process", "success", "issue"})
    void oneBrokenLogDoesNotSuppressHealthyLogsOrReportFalseSuccess(String brokenFile)
            throws Exception {
        Path config = config();
        int code =
                new ToolkitApplication(
                                key -> "secret",
                                target -> {
                                    if (target.username().equals("first")) {
                                        // Fault injection after initialization: emulate one log
                                        // becoming unwritable.
                                        // Reflection keeps test-only controls out of the production
                                        // logging API.
                                        Logger application =
                                                (Logger)
                                                        LoggerFactory.getLogger(
                                                                "com.example.db2toolkit");
                                        var appenders = application.iteratorForAppenders();
                                        while (appenders.hasNext()) {
                                            var appender = appenders.next();
                                            if (appender
                                                    instanceof
                                                    com.example.db2toolkit.output.RunLogs
                                                    && "run-first".equals(appender.getName())) {
                                                try {
                                                    var field =
                                                            appender.getClass()
                                                                    .getDeclaredField(brokenFile);
                                                    field.setAccessible(true);
                                                    ((java.io.Writer) field.get(appender)).close();
                                                } catch (ReflectiveOperationException
                                                        | java.io.IOException e) {
                                                    throw new AssertionError(e);
                                                }
                                            }
                                        }
                                    }
                                    return routineConnection();
                                })
                        .run(new String[] {config.toString()});
        assertEquals(2, code);
        for (String name : List.of("process", "success", "issue")) {
            if (name.equals(brokenFile)) continue;
            String filename = name.equals("process") ? "process.log" : "summary_" + name + ".log";
            String log = runLog("first", filename);
            assertTrue(log.contains("Database run finished: exitCode=2"), filename);
            assertFalse(log.contains("Database run finished: exitCode=0"), filename);
        }
        assertTrue(
                runLog("second", "summary_success.log")
                        .contains("Database run finished: exitCode=0"));
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {"process", "success", "issue"})
    void finalOutcomeWriteFailureCorrectsHealthyLogsBeforeTheyClose(String brokenFile)
            throws Exception {
        Path config = config();
        int code =
                new ToolkitApplication(
                                key -> "secret",
                                target -> {
                                    if (target.username().equals("first")) {
                                        Logger application =
                                                (Logger)
                                                        LoggerFactory.getLogger(
                                                                "com.example.db2toolkit");
                                        var appenders = application.iteratorForAppenders();
                                        while (appenders.hasNext()) {
                                            var appender = appenders.next();
                                            if (appender
                                                    instanceof
                                                    com.example.db2toolkit.output.RunLogs
                                                    && "run-first".equals(appender.getName())) {
                                                try {
                                                    var field =
                                                            appender.getClass()
                                                                    .getDeclaredField(brokenFile);
                                                    field.setAccessible(true);
                                                    var original =
                                                            (java.io.BufferedWriter)
                                                                    field.get(appender);
                                                    field.set(
                                                            appender,
                                                            new java.io.BufferedWriter(original) {
                                                                @Override
                                                                public void write(
                                                                        String text,
                                                                        int offset,
                                                                        int length)
                                                                        throws java.io.IOException {
                                                                    if (text.contains(
                                                                            "Database run"
                                                                                + " finished:"))
                                                                        throw new java.io
                                                                                .IOException(
                                                                                "Injected"
                                                                                    + " final-outcome"
                                                                                    + " write"
                                                                                    + " failure");
                                                                    super.write(
                                                                            text, offset, length);
                                                                }
                                                            });
                                                } catch (ReflectiveOperationException e) {
                                                    throw new AssertionError(e);
                                                }
                                            }
                                        }
                                    }
                                    return routineConnection();
                                })
                        .run(new String[] {config.toString()});
        assertEquals(2, code);
        for (String name : List.of("process", "success", "issue")) {
            if (name.equals(brokenFile)) continue;
            String filename = name.equals("process") ? "process.log" : "summary_" + name + ".log";
            List<String> outcomes =
                    runLog("first", filename)
                            .lines()
                            .filter(line -> line.contains("Database run finished:"))
                            .toList();
            assertFalse(outcomes.isEmpty(), filename);
            assertTrue(outcomes.get(outcomes.size() - 1).contains("exitCode=2"), filename);
            if (!name.equals("success"))
                assertTrue(runLog("first", filename).contains("Cannot write run logs:"), filename);
        }
        assertTrue(
                runLog("second", "summary_success.log")
                        .contains("Database run finished: exitCode=0"));
    }

    @Test
    void databasesRunWithSharedSnapshotSeparateFilesAndTaggedLogs() throws Exception {
        Path config = config();
        var events = events();
        Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.addAppender(events);
        List<String> lifecycle = new java.util.concurrent.CopyOnWriteArrayList<>();
        List<Connection> opened = new java.util.concurrent.CopyOnWriteArrayList<>();
        MDC.put("database", "previous-context");
        try {
            int code =
                    new ToolkitApplication(
                                    key -> "secret",
                                    target -> {
                                        String name = target.username();
                                        lifecycle.add("open-" + name);
                                        // A modified list must not change the snapshot exported to
                                        // the second database.
                                        try {
                                            Files.writeString(dir.resolve("sp.txt"), "RPT.CHANGED");
                                        } catch (java.io.IOException e) {
                                            throw new SQLException(e);
                                        }
                                        Connection connection = routineConnection();
                                        doAnswer(
                                                        inv -> {
                                                            lifecycle.add("close-" + name);
                                                            return null;
                                                        })
                                                .when(connection)
                                                .close();
                                        opened.add(connection);
                                        return connection;
                                    })
                            .run(new String[] {config.toString()});
            assertEquals(0, code);
            assertEquals(
                    List.of("close-first", "close-second", "open-first", "open-second"), lifecycle.stream().sorted().toList());
            assertEquals("previous-context", MDC.get("database"));
            List<Path> output;
            try (var paths = Files.walk(dir.resolve("out"))) {
                output = paths.filter(p -> p.toString().endsWith(".sql")).toList();
            }
            assertEquals(2, output.size());
            assertTrue(output.stream().anyMatch(p -> p.startsWith(dir.resolve("out/db-first"))));
            assertTrue(output.stream().anyMatch(p -> p.startsWith(dir.resolve("out/db-second"))));
            assertEquals(Files.readString(output.get(0)), Files.readString(output.get(1)));
            assertTrue(
                    output.stream().allMatch(p -> p.getFileName().toString().equals("RPT.P.sql")));
            for (String database : List.of("first", "second")) {
                String process = runLog(database, "process.log");
                String summary = runLog(database, "summary_issue.log");
                assertTrue(process.contains("Processing 1/1: PROCEDURE"));
                assertTrue(process.contains("Database connection closed"));
                assertTrue(summary.contains("SUCCESS=1"));
                assertTrue(summary.contains("Database run finished: exitCode=0"));
                assertTrue(
                        process.lines().allMatch(line -> line.contains("[db=" + database + "]")));
                assertFalse(summary.contains("Processing"));
                assertTrue(
                        events.list.stream()
                                .anyMatch(
                                        e ->
                                                database.equals(
                                                                e.getMDCPropertyMap()
                                                                        .get("database"))
                                                        && e.getFormattedMessage()
                                                                .contains("SUCCESS=1")));
            }
            assertTrue(
                    events.list.stream()
                                    .filter(
                                            e ->
                                                    e.getFormattedMessage()
                                                            .startsWith("PROCEDURE unique="))
                                    .count()
                            == 1);
            assertTrue(
                    events.list.stream()
                            .allMatch(e -> e.getMDCPropertyMap().containsKey("database")));
            for (Connection connection : opened) verify(connection).close();
        } finally {
            root.detachAppender(events);
            MDC.remove("database");
        }
    }

    @Test
    void connectionFailureIsTaggedAndDoesNotPreventNextDatabase() throws Exception {
        Path config = config();
        var events = events();
        Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.addAppender(events);
        List<String> names = new java.util.concurrent.CopyOnWriteArrayList<>();
        try {
            int code =
                    new ToolkitApplication(
                                    key -> "secret",
                                    target -> {
                                        names.add(target.username());
                                        if (target.username().equals("first"))
                                            throw new SQLException(
                                                    "Denied secret " + target.url(),
                                                    "08004",
                                                    -30082);
                                        return routineConnection();
                                    })
                            .run(new String[] {config.toString()});
            assertEquals(2, code);
            assertEquals(List.of("first", "second"), names.stream().sorted().toList());
            assertTrue(
                    events.list.stream()
                            .anyMatch(
                                    e ->
                                            "first".equals(e.getMDCPropertyMap().get("database"))
                                                    && e.getFormattedMessage()
                                                            .contains(
                                                                    "Connection initialization"
                                                                        + " failed")));
            assertTrue(
                    events.list.stream()
                            .anyMatch(
                                    e ->
                                            "second".equals(e.getMDCPropertyMap().get("database"))
                                                    && e.getFormattedMessage()
                                                            .contains("SUCCESS=1")));
            assertTrue(
                    events.list.stream()
                            .noneMatch(
                                    e ->
                                            e.getFormattedMessage().contains("secret")
                                                    || e.getFormattedMessage()
                                                            .contains("jdbc:db2:")));
            String failure = runLog("first", "summary_issue.log");
            assertTrue(failure.contains("Connection initialization failed"));
            assertTrue(failure.contains("UNPROCESSED PROCEDURE"));
            assertFalse(failure.contains("secret"));
            assertFalse(runLog("first", "process.log").contains("jdbc:db2:"));
            assertTrue(runLog("second", "summary_issue.log").contains("SUCCESS=1"));
            assertNull(MDC.get("database"));
        } finally {
            root.detachAppender(events);
        }
    }

    @Test
    void unreadableSharedListOrMissingPasswordPreventsAllConnections() throws Exception {
        Path config = config();
        List<String> opened = new ArrayList<>();
        var app =
                new ToolkitApplication(
                        key -> null,
                        target -> {
                            opened.add(target.username());
                            return routineConnection();
                        });
        assertEquals(2, app.run(new String[] {config.toString()}));
        assertTrue(opened.isEmpty());
        Files.delete(dir.resolve("fn.txt"));
        app =
                new ToolkitApplication(
                        key -> "secret",
                        target -> {
                            opened.add(target.username());
                            return routineConnection();
                        });
        assertEquals(2, app.run(new String[] {config.toString()}));
        assertTrue(opened.isEmpty());
    }

    @Test
    void emptySharedListsSkipEveryConnection() throws Exception {
        Path config = config();
        Files.writeString(dir.resolve("sp.txt"), "");
        int code =
                new ToolkitApplication(
                                key -> "secret",
                                target -> {
                                    throw new AssertionError("No connection expected");
                                })
                        .run(new String[] {config.toString()});
        assertEquals(0, code);
        assertTrue(runLog("first", "summary_issue.log").contains("Requests(unique)=0"));
        assertTrue(runLog("second", "process.log").contains("connection skipped"));
    }

    @Test
    void lostConnectionIsClosedWithoutPreventingOtherDatabase() throws Exception {
        Path config = config();
        Connection broken = routineConnection();
        when(broken.isValid(5)).thenReturn(false);
        Connection healthy = routineConnection();
        int code =
                new ToolkitApplication(
                                key -> "secret",
                                target -> {
                                    if (target.username().equals("first")) return broken;
                                    return healthy;
                                })
                        .run(new String[] {config.toString()});
        assertEquals(1, code);
        verify(broken).close();
        verify(broken, never()).prepareStatement(anyString());
        verify(healthy).prepareStatement(anyString());
        verify(healthy).close();
    }

    private String runLog(String database, String name) throws Exception {
        try (var paths = Files.walk(dir.resolve("out/db-" + database))) {
            Path log =
                    paths.filter(p -> p.getFileName().toString().equals(database + "-" + name))
                            .findFirst()
                            .orElseThrow();
            return Files.readString(log);
        }
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(booleans = {false, true})
    void connectionCloseFailureRetainsFilesAndContinuesNextDatabase(boolean unchecked)
            throws Exception {
        Path properties = config();
        Connection first = routineConnection();
        Exception failure =
                unchecked
                        ? new IllegalStateException("close secret failed")
                        : new SQLRecoverableException("close secret failed", "08006", -4499);
        doThrow(failure).when(first).close();
        Connection second = routineConnection();
        int expected = unchecked ? 2 : 1;
        int code =
                new ToolkitApplication(
                                key -> "secret",
                                target -> {
                                    if (target.username().equals("first")) return first;
                                    return second;
                                })
                        .run(new String[] {properties.toString()});
        assertEquals(expected, code);
        String issues = runLog("first", "summary_issue.log");
        String successes = runLog("first", "summary_success.log");
        assertTrue(issues.contains("close failed") || issues.contains("execution/close failed"));
        if (!unchecked) assertTrue(issues.contains("08006"));
        assertFalse(issues.contains("secret"));
        assertTrue(issues.contains("Unprocessed=0"));
        assertTrue(issues.contains("Database run finished: exitCode=" + expected));
        assertTrue(successes.contains("PROCEDURE \"RPT\".\"P\" specific=\"P_SPEC\" SUCCESS"));
        assertTrue(successes.contains("Database run finished: exitCode=" + expected));
        assertFalse(runLog("first", "process.log").contains("Database connection closed"));
        assertTrue(
                runLog("second", "summary_success.log")
                        .contains("Database run finished: exitCode=0"));
        // Connection cleanup cannot retroactively remove a definition already published
        // successfully.
        try (var files = Files.walk(dir.resolve("out"))) {
            var ddl = files.filter(p -> p.toString().endsWith(".sql")).toList();
            assertEquals(2, ddl.size());
            for (Path file : ddl)
                assertEquals("CREATE PROCEDURE RPT.P() BEGIN END;\n@\n", Files.readString(file));
        }
        verify(first, times(1)).close();
        verify(second, times(1)).close();
    }

    @Test
    void missingObjectsAreListedInEachDatabaseSummary() throws Exception {
        Path config = config();
        int code =
                new ToolkitApplication(
                                key -> "secret",
                                target -> {
                                    Connection connection = routineConnection();
                                    when(connection
                                                    .prepareStatement(anyString())
                                                    .executeQuery()
                                                    .next())
                                            .thenReturn(false);
                                    return connection;
                                })
                        .run(new String[] {config.toString()});
        assertEquals(1, code);
        for (String database : List.of("first", "second")) {
            assertTrue(runLog(database, "summary_issue.log").contains("NOT_FOUND=1"));
            assertTrue(runLog(database, "summary_issue.log").contains("No matching routine"));
        }
    }

    private Path config() throws Exception {
        Files.writeString(dir.resolve("sp.txt"), "RPT.P\n");
        Files.writeString(dir.resolve("fn.txt"), "");
        Files.writeString(dir.resolve("table.txt"), "");
        Path config = dir.resolve("application.properties");
        Files.writeString(
                config,
                "db.names=first,second\n"
                    + "db.first.url=jdbc:db2://first:50000/DB\n"
                    + "db.first.username=first\n"
                    + "db.first.password-env=FIRST_PASSWORD\n"
                    + "db.second.url=jdbc:db2://second:50000/DB\n"
                    + "db.second.username=second\n"
                    + "db.second.password-env=SECOND_PASSWORD\n"
                    + "export.procedure-list=sp.txt\n"
                    + "export.function-list=fn.txt\n"
                    + "export.table-list=table.txt\n"
                    + "export.output-directory=out\n");
        return config;
    }

    private static Connection routineConnection() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet rows = mock(ResultSet.class);
        when(connection.isValid(5)).thenReturn(true);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(rows);
        when(rows.next()).thenReturn(true, false);
        when(rows.getString("SPECIFICNAME")).thenReturn("P_SPEC");
        when(rows.getCharacterStream("TEXT"))
                .thenReturn(new StringReader("CREATE PROCEDURE RPT.P() BEGIN END;"));
        return connection;
    }

    private static ListAppender<ILoggingEvent> events() {
        var appender =
                new ListAppender<ILoggingEvent>() {
                    @Override
                    protected void append(ILoggingEvent event) {
                        event.prepareForDeferredProcessing();
                        super.append(event);
                    }
                };
        appender.start();
        return appender;
    }

    @Test
    void sharedInputWarningsAreRetainedInEveryDatabaseSummary() throws Exception {
        Path config = config();
        Files.writeString(dir.resolve("sp.txt"), "RPT.P\ninvalid-line\n");
        int code =
                new ToolkitApplication(key -> "secret", target -> routineConnection())
                        .run(new String[] {config.toString()});
        assertEquals(1, code);
        for (String database : List.of("first", "second")) {
            assertTrue(runLog(database, "summary_issue.log").contains("sp.txt:2"));
            assertTrue(runLog(database, "process.log").contains("DDL request finished: 1/1"));
        }
    }

    @Test
    void uncheckedConnectionFailureKeepsLocalSummaryAndContinuesNextDatabase() throws Exception {
        Path config = config();
        int code =
                new ToolkitApplication(
                                key -> "secret",
                                target -> {
                                    if (target.username().equals("first"))
                                        throw new IllegalStateException("driver secret failed");
                                    return routineConnection();
                                })
                        .run(new String[] {config.toString()});
        assertEquals(2, code);
        String failed = runLog("first", "summary_issue.log");
        assertTrue(failed.contains("IllegalStateException"));
        assertTrue(failed.contains("UNPROCESSED PROCEDURE"));
        assertTrue(failed.contains("Database run finished: exitCode=2"));
        assertFalse(failed.contains("secret"));
        assertTrue(runLog("second", "summary_issue.log").contains("SUCCESS=1"));
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/application/ProblemSummaryTest.java =====
UTF8-BYTES: 6345
SHA256: 2fb1bb27529502f78aed775fb6bf3267eb5f6d035f7bd109a51d1ab6e354b9c6
===== CONTENT =====
package com.example.db2toolkit.application;


import static org.junit.jupiter.api.Assertions.*;

import com.example.db2toolkit.ddl.DdlSummaryLogger;
import com.example.db2toolkit.ddl.model.*;
import com.example.db2toolkit.excel.*;
import com.example.db2toolkit.jdbc.SafeDiagnostics;
import com.example.db2toolkit.model.*;
import com.example.db2toolkit.output.RunLogs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.MDC;

import java.nio.file.*;
import java.util.List;

class ProblemSummaryTest {
    @TempDir Path dir;

    @Test
    void summaryListsProblemsFirstThenSuccessfulObjectsAndOutputPaths() throws Exception {
        var report = new DdlExportSummary();
        for (DdlObjectType type : DdlObjectType.values()) {
            report.add(
                    new DdlExportResult(
                            type,
                            "RPT",
                            "MISSING_" + type,
                            null,
                            DdlExportStatus.NOT_FOUND,
                            null,
                            "No matching object"));
        }
        report.add(
                new DdlExportResult(
                        DdlObjectType.TABLE,
                        "RPT",
                        "GOOD_TABLE",
                        null,
                        DdlExportStatus.SUCCESS,
                        dir.resolve("GOOD_TABLE.sql"),
                        null));
        report.add(
                new DdlExportResult(
                        DdlObjectType.FUNCTION,
                        "RPT",
                        "NO_DDL",
                        "OVERLOAD_1",
                        DdlExportStatus.DDL_UNAVAILABLE,
                        null,
                        "Definition unavailable"));
        report.add(
                new DdlExportResult(
                        DdlObjectType.PROCEDURE,
                        "RPT",
                        "BROKEN",
                        null,
                        DdlExportStatus.FAILED,
                        null,
                        "SQLCODE=-1 SQLSTATE=58000"));
        String log = capture(() -> new DdlSummaryLogger().log(report, errors()));
        assertTrue(log.contains("SUCCESS=1"));
        assertTrue(log.contains("GOOD_TABLE.sql"));
        assertTrue(log.indexOf("BROKEN") < log.indexOf("GOOD_TABLE"));
        for (DdlObjectType type : DdlObjectType.values())
            assertTrue(log.contains("MISSING_" + type));
        assertTrue(log.contains("Configured objects not found in database"));
        assertTrue(log.indexOf("MISSING_TABLE") < log.indexOf("NO_DDL"));
        assertTrue(log.contains("OVERLOAD_1"));
        assertTrue(log.contains("SQLSTATE=58000"));
        assertTrue(log.lines().allMatch(line -> line.contains("[db=testdb]")));
        assertFalse(Files.readString(dir.resolve("testdb-summary_issue.log")).contains("GOOD_TABLE"));
        assertFalse(Files.readString(dir.resolve("testdb-summary_success.log")).contains("MISSING_"));
        assertTrue(Files.readString(dir.resolve("testdb-summary_success.log")).contains("GOOD_TABLE.sql"));
    }

    @Test
    void excelSummaryRetainsProblemsThenSuccessfulTasksAndActualSheets() throws Exception {
        var summary = new ExcelExportSummary();
        for (var status : ExcelExportSummary.Status.values()) {
            var task =
                    new ExcelExportTask(
                            "task-" + status,
                            SourceType.OBJECT,
                            "RPT." + status,
                            "report.xlsx",
                            status.name(),
                            status != ExcelExportSummary.Status.SKIPPED,
                            1000,
                            0,
                            true);
            summary.add(
                    new ExcelExportSummary.Result(
                            task,
                            status,
                            status == ExcelExportSummary.Status.SUCCESS
                                    ? List.of(
                                            new ExcelWorkbookWriter.SheetRows("DATA", 2),
                                            new ExcelWorkbookWriter.SheetRows("DATA_2", 1))
                                    : List.of(),
                            status == ExcelExportSummary.Status.SUCCESS ? 3 : 0,
                            1,
                            status == ExcelExportSummary.Status.FAILED ? "Object undefined" : null,
                            status == ExcelExportSummary.Status.FAILED ? -204 : null,
                            status == ExcelExportSummary.Status.FAILED ? "42704" : null));
        }
        String log = capture(() -> summary.log(errors()));
        assertTrue(log.contains("Successful tasks: 1; Failed tasks: 1; Skipped tasks: 1"));
        assertTrue(log.contains("task-SUCCESS workbook=report.xlsx"));
        assertTrue(log.contains("sheet=DATA rows=2"));
        assertTrue(log.contains("sheet=DATA_2 rows=1"));
        assertTrue(log.indexOf("task-FAILED") < log.indexOf("task-SUCCESS"));
        assertTrue(log.contains("source=RPT.FAILED"));
        assertTrue(log.contains("SQLCODE=-204 SQLSTATE=42704"));
        assertTrue(log.contains("task-SKIPPED"));
        assertFalse(Files.readString(dir.resolve("testdb-summary_issue.log")).contains("task-SUCCESS"));
        assertFalse(Files.readString(dir.resolve("testdb-summary_success.log")).contains("task-FAILED"));
        assertFalse(Files.readString(dir.resolve("testdb-summary_success.log")).contains("task-SKIPPED"));
    }

    private SafeDiagnostics errors() {
        return new SafeDiagnostics(List.of());
    }

    private String capture(Runnable action) throws Exception {
        String previous = MDC.get("database");
        MDC.put("database", "testdb");
        try (var logs = new RunLogs(dir, "testdb", errors())) {
            action.run();
        } finally {
            if (previous == null) MDC.remove("database");
            else MDC.put("database", previous);
        }
        assertFalse(Files.exists(dir.resolve("summary.log")));
        return Files.readString(dir.resolve("testdb-summary_issue.log"))
                + Files.readString(dir.resolve("testdb-summary_success.log"));
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/config/ConfigLoaderTest.java =====
UTF8-BYTES: 20704
SHA256: ef129f5bb845bfc41008cdeb30855094b13e873810f6fe8639762dc87814ceb6
===== CONTENT =====
package com.example.db2toolkit.config;


import static org.junit.jupiter.api.Assertions.*;

import com.example.db2toolkit.ddl.model.DdlObjectType;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.provider.*;

import java.nio.file.*;
import java.util.*;

class ConfigLoaderTest {
    @TempDir Path dir;

    @org.junit.jupiter.params.ParameterizedTest
    @ValueSource(strings = {"catalog.procedure.veiw", "catalog.function.veiw",
        "catalog.table.veiw", "catalog.column.veiw", "export.enabeld",
        "export.procedure-lsit", "export.function-lsit", "export.table-lsit",
        "export.output-directroy", "excel-export.confg"})
    void unknownGlobalKeysCannotSilentlyUseDefaults(String key) throws Exception {
        Path file = multiConfig("first,second");
        Files.writeString(file, key + "=private-setting-value\n", StandardOpenOption.APPEND);
        var error = assertThrows(ConfigurationException.class,
                () -> new ConfigLoader().loadTargets(file, name -> "secret"));
        assertEquals("Unknown configuration key: " + key, error.getMessage());
        assertFalse(error.getMessage().contains("private-setting-value"));
    }

    @Test
    void globalKeysAreCheckedBeforeOpeningExcelConfiguration() throws Exception {
        Path file = multiConfig("first,second");
        Files.writeString(file,
                "catalog.table.veiw=CUSTOM.OBJECTS\nexcel-export.config=missing.yaml\n",
                StandardOpenOption.APPEND);
        var error = assertThrows(ConfigurationException.class,
                () -> new ConfigLoader().loadTargets(file, name -> "secret"));
        assertEquals("Unknown configuration key: catalog.table.veiw", error.getMessage());
    }

    @Test
    void unknownGlobalKeyDiagnosticsRedactResolvedPasswords() throws Exception {
        Path file = multiConfig("first,second");
        Files.writeString(file, "catalog.secret=private-setting-value\n", StandardOpenOption.APPEND);
        var error = assertThrows(ConfigurationException.class,
                () -> new ConfigLoader().loadTargets(file, name -> "secret"));
        assertEquals("Unknown configuration key: catalog.[redacted]", error.getMessage());
    }

    @org.junit.jupiter.params.ParameterizedTest
    @ValueSource(strings = {"db.query-timeout-second=30", "db.first.read-timeout-second=30",
        "db.first.progress-interval-second=10", "db.missing.url=jdbc:db2://unused/DB"})
    void unknownConnectionKeysCannotSilentlyDisableLimits(String setting) throws Exception {
        Path file = multiConfig("first,second");
        Files.writeString(file, setting + "\n", StandardOpenOption.APPEND);
        assertThrows(ConfigurationException.class, () -> new ConfigLoader().loadTargets(file, key -> "secret"));
    }

    @Test
    void jdbcTimeoutDefaultsOverridesAndDriverProperties() throws Exception {
        Path file = multiConfig("first,second");
        var defaults = new ConfigLoader().loadTargets(file, key -> "secret").get(0).config().connection();
        assertEquals(30, defaults.connectTimeoutSeconds());
        assertEquals(120, defaults.readTimeoutSeconds());
        assertEquals(0, defaults.queryTimeoutSeconds());
        assertEquals(10, defaults.progressIntervalSeconds());
        Files.writeString(file, "db.connect-timeout-seconds=7\ndb.read-timeout-seconds=42\ndb.query-timeout-seconds=20\ndb.first.read-timeout-seconds=9\ndb.first.progress-interval-seconds=0\n", StandardOpenOption.APPEND);
        var targets = new ConfigLoader().loadTargets(file, key -> "secret");
        assertEquals(9, targets.get(0).config().connection().readTimeoutSeconds());
        assertEquals(42, targets.get(1).config().connection().readTimeoutSeconds());
        assertEquals(0, targets.get(0).config().connection().progressIntervalSeconds());
        assertEquals(20, targets.get(1).config().connection().queryTimeoutSeconds());
        try (var driver = org.mockito.Mockito.mockStatic(java.sql.DriverManager.class)) {
            new com.example.db2toolkit.jdbc.Db2ConnectionFactory().open(targets.get(0).config());
            driver.verify(() -> java.sql.DriverManager.getConnection(
                org.mockito.ArgumentMatchers.eq(targets.get(0).config().url()),
                org.mockito.ArgumentMatchers.argThat((Properties p) -> "7".equals(p.getProperty("loginTimeout"))
                    && "9".equals(p.getProperty("blockingReadConnectionTimeout")))));
        }
    }

    @Test
    void jccReturnsWhenServerAcceptsConnectionButNeverResponds() throws Exception {
        try (var server = new java.net.ServerSocket(0, 1, java.net.InetAddress.getLoopbackAddress())) {
            server.setSoTimeout(5000);
            Path file = multiConfig("first,second");
            Files.writeString(file, "db.first.url=jdbc:db2://localhost:" + server.getLocalPort()
                + "/TESTDB\ndb.connect-timeout-seconds=2\ndb.read-timeout-seconds=2\n", StandardOpenOption.APPEND);
            var config = new ConfigLoader().loadTargets(file, key -> "secret").get(0).config();
            var attempt = new java.util.concurrent.FutureTask<java.sql.SQLException>(() -> {
                try (var connection = new com.example.db2toolkit.jdbc.Db2ConnectionFactory().open(config)) {
                    throw new AssertionError("An unresponsive socket cannot establish a DB2 session");
                } catch (java.sql.SQLException expected) {
                    return expected;
                }
            });
            var thread = new Thread(attempt, "test-stalled-db2-login");
            thread.setDaemon(true);
            long started = System.nanoTime();
            thread.start();
            try (var accepted = server.accept()) {
                assertNotNull(attempt.get(12, java.util.concurrent.TimeUnit.SECONDS));
                assertTrue(java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started) >= 1000,
                    "Must wait for the configured timeout, rather than fail immediately on configuration");
            } finally {
                attempt.cancel(true);
            }
        }
    }

    @org.junit.jupiter.params.ParameterizedTest
    @ValueSource(strings = {"db.read-timeout-seconds=-1", "db.connect-timeout-seconds=oops",
        "db.query-timeout-seconds=2147483648", "db.missing.read-timeout-seconds=1"})
    void rejectsInvalidJdbcLimits(String property) throws Exception {
        Path file = multiConfig("first,second");
        Files.writeString(file, property + "\n", StandardOpenOption.APPEND);
        assertThrows(ConfigurationException.class, () -> new ConfigLoader().loadTargets(file, key -> "secret"));
    }

    @org.junit.jupiter.params.ParameterizedTest
    @ValueSource(booleans = {false, true})
    void excelConfigurationErrorsRedactEveryDatabasePasswordAndControlCharacter(
            boolean filePasswords) throws Exception {
        Path file = multiConfig("first,second");
        if (filePasswords)
            Files.writeString(
                    file,
                    "db.first.password=first-secret\ndb.second.password=second-secret\n",
                    StandardOpenOption.APPEND);
        Files.writeString(file, "excel-export.config=excel.yaml\n", StandardOpenOption.APPEND);
        Files.writeString(
                dir.resolve("excel.yaml"),
                """
excel-export:
  enabled: true
  exports:
    - {name: "first-secret\\nsecond-secret", type: OBJECT, source: RPT.X, workbook: report, sheet: DATA, fetch-size: 0}
""");
        var error =
                assertThrows(
                        com.example.db2toolkit.config.ConfigurationException.class,
                        () ->
                                new ConfigLoader()
                                        .loadTargets(
                                                file,
                                                key -> {
                                                    if (filePasswords)
                                                        throw new AssertionError(
                                                                "File passwords must take"
                                                                    + " precedence");
                                                    return Map.of(
                                                                    "FIRST_PASSWORD",
                                                                    "first-secret",
                                                                    "SECOND_PASSWORD",
                                                                    "second-secret")
                                                            .get(key);
                                                }));
        assertTrue(error.getMessage().contains("fetch-size"));
        assertFalse(error.getMessage().contains("first-secret"));
        assertFalse(error.getMessage().contains("second-secret"));
        assertFalse(error.getMessage().contains("\n"));
    }

    @org.junit.jupiter.params.ParameterizedTest
    @ValueSource(booleans = {false, true})
    void excelRenameWarningsUseSharedRedactionWithoutChangingTaskNames(boolean filePasswords)
            throws Exception {
        Path file = multiConfig("first,second");
        if (filePasswords)
            Files.writeString(
                    file,
                    "db.first.password=first-secret\ndb.second.password=second-secret\n",
                    StandardOpenOption.APPEND);
        Files.writeString(file, "excel-export.config=excel.yaml\n", StandardOpenOption.APPEND);
        Files.writeString(
                dir.resolve("excel.yaml"),
                """
excel-export:
  enabled: true
  exports:
    - {name: first, type: OBJECT, source: RPT.X, workbook: report, sheet: DATA}
    - {name: "first-secret\\nsecond-secret", type: OBJECT, source: RPT.X, workbook: report, sheet: DATA}
""");
        var logger =
                (ch.qos.logback.classic.Logger)
                        org.slf4j.LoggerFactory.getLogger(
                                com.example.db2toolkit.excel.ExcelTaskNormalizer.class);
        var events =
                new ch.qos.logback.core.read.ListAppender<
                        ch.qos.logback.classic.spi.ILoggingEvent>();
        events.start();
        logger.addAppender(events);
        Map<String, Integer> reads = new HashMap<>();
        try {
            var targets =
                    new ConfigLoader()
                            .loadTargets(
                                    file,
                                    key -> {
                                        reads.merge(key, 1, Integer::sum);
                                        return Map.of(
                                                        "FIRST_PASSWORD",
                                                        "first-secret",
                                                        "SECOND_PASSWORD",
                                                        "second-secret")
                                                .get(key);
                                    });
            assertEquals(
                    filePasswords ? Map.of() : Map.of("FIRST_PASSWORD", 1, "SECOND_PASSWORD", 1),
                    reads);
            assertEquals(
                    "first-secret\nsecond-secret",
                    targets.get(0).config().excel().tasks().get(1).name());
            assertEquals("DATA_2", targets.get(1).config().excel().tasks().get(1).sheet());
            assertEquals(1, events.list.size());
            String warning = events.list.get(0).getFormattedMessage();
            assertFalse(warning.contains("first-secret"));
            assertFalse(warning.contains("second-secret"));
            assertFalse(warning.contains("\n"));
        } finally {
            logger.detachAppender(events);
            events.stop();
        }
    }

    @Test
    void multipleTargetsShareListsButHaveSeparateCredentialsAndOutput() throws Exception {
        Path file = multiConfig("first,second");
        var targets =
                new ConfigLoader()
                        .loadTargets(
                                file,
                                key ->
                                        Map.of(
                                                        "FIRST_PASSWORD",
                                                        "first-secret",
                                                        "SECOND_PASSWORD",
                                                        "second-secret")
                                                .get(key));
        assertEquals(List.of("first", "second"), targets.stream().map(t -> t.name()).toList());
        assertEquals(targets.get(0).config().lists(), targets.get(1).config().lists());
        assertEquals(dir.resolve("out/db-first"), targets.get(0).config().output());
        assertEquals(dir.resolve("out/db-second"), targets.get(1).config().output());
        assertEquals("first-secret", targets.get(0).config().password());
        assertEquals("second-secret", targets.get(1).config().password());
        assertEquals("jdbc:db2://second:50000/DB", targets.get(1).config().url());
        assertFalse(targets.toString().contains("secret"));
    }

    @Test
    void filePasswordsSupportMultipleDatabasesWithoutEnvironmentAccess() throws Exception {
        Path file = multiConfig("first,second");
        Files.writeString(
                file,
                "db.first.password=first-file-secret\ndb.second.password=second-file-secret\n",
                StandardOpenOption.APPEND);
        var targets =
                new ConfigLoader()
                        .loadTargets(
                                file,
                                key -> {
                                    throw new AssertionError("Unexpected environment lookup");
                                });
        assertEquals("first-file-secret", targets.get(0).config().password());
        assertEquals("second-file-secret", targets.get(1).config().password());
        var errors =
                new com.example.db2toolkit.jdbc.SafeDiagnostics(
                        targets.stream().map(t -> t.config()).toList());
        assertFalse(errors.clean("first-file-secret second-file-secret").contains("file-secret"));
        assertFalse(targets.toString().contains("file-secret"));
    }

    @Test
    void singleFilePasswordPreservesEscapedCharactersAndRejectsEmptyInsteadOfFallingBack()
            throws Exception {
        Path file = dir.resolve("file-password.properties");
        String base = "db.url=jdbc:db2://localhost:50000/DB\ndb.username=u\nexport.enabled=false\n";
        Files.writeString(file, base + "db.password=\\ secret\\\\with=colon:space \n");
        var config =
                new ConfigLoader()
                        .load(
                                file,
                                key -> {
                                    throw new AssertionError("Unexpected environment lookup");
                                });
        assertEquals(" secret\\with=colon:space ", config.password());
        Files.writeString(file, base + "db.password=\ndb.password-env=PASSWORD\n");
        var exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new ConfigLoader().load(file, key -> "fallback-secret"));
        assertEquals("Empty configuration key: db.password", exception.getMessage());
    }

    @Test
    void invalidDatabaseNamesAndMissingTargetCredentialsAreRejected() throws Exception {
        for (String names :
                List.of(
                        "first,FIRST",
                        "first,",
                        "../escape",
                        "first/second",
                        "",
                        "first,unknown")) {
            Path file = multiConfig(names);
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new ConfigLoader().loadTargets(file, key -> "secret"));
        }
        Path file = multiConfig("first,second");
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new ConfigLoader()
                                .loadTargets(
                                        file,
                                        key -> "FIRST_PASSWORD".equals(key) ? "secret" : null));
    }

    private Path multiConfig(String names) throws Exception {
        Path file = dir.resolve("multi.properties");
        Files.writeString(
                file,
                "db.names="
                        + names
                        + "\n"
                        + "db.first.url=jdbc:db2://first:50000/DB\n"
                        + "db.first.username=u1\n"
                        + "db.first.password-env=FIRST_PASSWORD\n"
                        + "db.second.url=jdbc:db2://second:50000/DB\n"
                        + "db.second.username=u2\n"
                        + "db.second.password-env=SECOND_PASSWORD\n"
                        + "export.procedure-list=sp.txt\n"
                        + "export.function-list=fn.txt\n"
                        + "export.table-list=table.txt\n"
                        + "export.output-directory=out\n");
        return file;
    }

    @Test
    void configPathsAndCredentialContract() throws Exception {
        Path file = dir.resolve("application.properties");
        Files.writeString(
                file,
                "db.url=jdbc:db2://localhost:50000/SAMPLE\n"
                        + "db.username=u\n"
                        + "db.password-env=DB2_PASSWORD\n"
                        + "export.procedure-list=a\n"
                        + "export.function-list=b\n"
                        + "export.table-list=c\n"
                        + "export.output-directory=../out\n"
                        + "catalog.procedure.view=\"It.s\".p\n");
        var config = new ConfigLoader().load(file, key -> "secret");
        assertEquals(dir.resolve("a"), config.lists().get(DdlObjectType.PROCEDURE));
        assertEquals(dir.getParent().resolve("out"), config.output());
        assertEquals("\"It.s\".\"P\"", config.procedures());
        assertFalse(config.toString().contains("secret"));
        assertThrows(
                IllegalArgumentException.class, () -> new ConfigLoader().load(file, key -> null));
        Files.writeString(
                file, "\ncatalog.procedure.view=SYS.T;DELETE\n", StandardOpenOption.APPEND);
        assertThrows(
                IllegalArgumentException.class,
                () -> new ConfigLoader().load(file, key -> "secret"));
    }

    @Test
    void utf8BomDoesNotBecomePartOfTheFirstConfigurationKey() throws Exception {
        Path file = dir.resolve("bom.properties");
        Files.writeString(
                file,
                "\uFEFFdb.url=jdbc:db2://localhost:50000/DB\n"
                        + "db.username=u\ndb.password=file-secret\nexport.enabled=false\n");
        assertEquals("file-secret", new ConfigLoader().load(file, key -> null).password());
        file = multiConfig("first,second");
        Files.writeString(file, "\uFEFF" + Files.readString(file));
        assertEquals(2, new ConfigLoader().loadTargets(file, key -> "secret").size());
    }

    @Test
    void overlappingDatabasePasswordsAreFullyRedactedRegardlessOfTargetOrder() throws Exception {
        Path file = multiConfig("first,second");
        var targets =
                new ConfigLoader()
                        .loadTargets(
                                file,
                                key -> key.equals("FIRST_PASSWORD") ? "alpha" : "alpha-extra");
        var configs = targets.stream().map(t -> t.config()).toList();
        String message = "failed: alpha-extra alpha";
        assertEquals(
                "failed: [redacted] [redacted]",
                new com.example.db2toolkit.jdbc.SafeDiagnostics(configs).clean(message));
        assertEquals(
                "failed: [redacted] [redacted]",
                new com.example.db2toolkit.jdbc.SafeDiagnostics(
                                List.of(configs.get(1), configs.get(0)))
                        .clean(message));
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/datatypevalidation/BaselineRoundTripTest.java =====
UTF8-BYTES: 24822
SHA256: a0142362269c8394f732ad94e1c03a01ce334da03cffe562e8e2e4d69272ac78
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;


import static org.junit.jupiter.api.Assertions.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.math.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

class BaselineRoundTripTest {
    @TempDir Path dir;
    static final String CSV = "fos.t,A,VARCHAR(20),VARCHAR(50)\n";

    @Test
    void malformedInlineUnicodeFailsBaselinePreflight() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "VARCHAR", 0, 1208);
        f.lengths("A", 20, 10);
        var base = f.run(f.config(CSV, DataTypeValidationConfig.Mode.BASELINE, null));
        Path damaged = dir.resolve("invalid-unicode.xlsx");
        try (var book = new XSSFWorkbook(base.published().toFile())) {
            var cell = book.getSheet("_items").getRow(1).getCell(15).getCTCell();
            cell.setT(org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType.INLINE_STR);
            var text = org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst.Factory.newInstance();
            text.setT("_xD800_");
            cell.setIs(text);
            if (cell.isSetV()) cell.unsetV();
            try (var out = Files.newOutputStream(damaged)) { book.write(out); }
        }
        var failure = assertThrows(IOException.class, () -> {
            try (var ignored = DataTypeValidationPlan.prepare(
                f.config(CSV, DataTypeValidationConfig.Mode.VALIDATE, damaged))) {}
        });
        assertTrue(failure.getMessage().contains("surrogate"));
    }

    @Test
    void singletonHintSurvivesDifferentCountsAndActualClobAndIsVisible() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "CLOB", 0, 1208);
        f.lengths("A", 20, 100);
        var baseline =
                f.run(
                        f.config(
                                "fos.t,A,VARCHAR(20),CLOB(50)\n",
                                DataTypeValidationConfig.Mode.BASELINE,
                                null));
        f.lengths("A", 20, 120);
        var current =
                f.run(
                        f.config(
                                "fos.t,A,VARCHAR(20),CLOB(50)\n",
                                DataTypeValidationConfig.Mode.VALIDATE,
                                baseline.published()));
        assertEquals(0, current.exitCode());
        assertTrue(current.results().get(0).singleLengthMatchHint());
        try (var book = new XSSFWorkbook(current.published().toFile())) {
            Row row = book.getSheetAt(0).getRow(1);
            assertEquals(
                    "DISTINCT LENGTH = (20)\nDISTINCT COUNT  = (120)\n"
                            + DataTypeValidationWorkbookFormat.HINT,
                    row.getCell(6).getStringCellValue());
            assertEquals(
                    "FFDDEBF7",
                    ((org.apache.poi.xssf.usermodel.XSSFCellStyle) row.getCell(6).getCellStyle()).getFillForegroundXSSFColor().getARGBHex());
            assertEquals("PASS", row.getCell(7).getStringCellValue());
            assertEquals("PASS", row.getCell(8).getStringCellValue());
            assertEquals("0", book.getSheet("_items").getRow(1).getCell(19).getStringCellValue());
            assertTrue(row.getHeightInPoints() >= 45);
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"empty", "null", "null-and-length", "multiple", "overflow", "zero", "clob"})
    void singletonHintHasStrictNullAndRangeSemantics(String scenario) throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "VARCHAR", 0, 1208);
        switch (scenario) {
            case "empty" -> f.lengths("A");
            case "null" -> f.lengths("A", null, 5);
            case "null-and-length" -> f.lengths("A", null, 2, 20, 3);
            case "multiple" -> f.lengths("A", 10, 2, 20, 3);
            case "overflow" -> f.lengths("A", 21, 5);
            case "zero" -> f.lengths("A", 0, 5);
            case "clob" -> f.lengths("A", 20, 5);
        }
        String csv = scenario.equals("clob") ? "fos.t,A,CLOB(20),CLOB(50)\n" : CSV;
        var base = f.run(f.config(csv, DataTypeValidationConfig.Mode.BASELINE, null));
        var current = f.run(f.config(csv, DataTypeValidationConfig.Mode.VALIDATE, base.published()));
        assertEquals("PASS", current.results().get(0).compare());
        assertEquals(scenario.equals("zero"), current.results().get(0).singleLengthMatchHint());
        assertEquals(scenario.equals("overflow") ? 1 : 0, current.exitCode());
    }

    @Test
    void emptyAndAllNullDifferAndDifferencesAreStoredLosslessly() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "VARCHAR", 0, 1208);
        f.lengths("A");
        var base = f.run(f.config(CSV, DataTypeValidationConfig.Mode.BASELINE, null));
        f.lengths("A", null, 7);
        var current = f.run(f.config(CSV, DataTypeValidationConfig.Mode.VALIDATE, base.published()));
        assertEquals("DIFFERENT", current.results().get(0).compare());
        assertEquals(1, current.exitCode());
        try (var book = new XSSFWorkbook(current.published().toFile())) {
            var row = book.getSheet("_diffs_001").getRow(1);
            assertEquals("CURRENT_ONLY", row.getCell(4).getStringCellValue());
            assertEquals("true", row.getCell(5).getStringCellValue());
            assertEquals("", row.getCell(6).getStringCellValue());
        }
    }

    @Test
    void bothDirectionsOfDifferenceIncludeRangeFailAndNull() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "VARCHAR", 0, 1208);
        f.lengths("A", null, 1, 10, 2, 30, 1);
        var base = f.run(f.config(CSV, DataTypeValidationConfig.Mode.BASELINE, null));
        f.lengths("A", 10, 4, 40, 1);
        var result = f.run(f.config(CSV, DataTypeValidationConfig.Mode.VALIDATE, base.published()));
        assertEquals(DataTypeValidationResult.RangeStatus.FAIL, result.results().get(0).range());
        assertEquals("DIFFERENT", result.results().get(0).compare());
        try (var book = new XSSFWorkbook(result.published().toFile())) {
            assertEquals(
                    "(NULL, 30)", book.getSheetAt(0).getRow(1).getCell(9).getStringCellValue());
            assertEquals("(40)", book.getSheetAt(0).getRow(1).getCell(10).getStringCellValue());
            assertEquals(3, book.getSheet("_diffs_001").getLastRowNum());
        }
    }

    @Test
    void preflightSnapshotDoesNotReopenReplacedSource() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "VARCHAR", 0, 1208);
        f.lengths("A", 20, 10);
        var base = f.run(f.config(CSV, DataTypeValidationConfig.Mode.BASELINE, null));
        var config = f.config(CSV, DataTypeValidationConfig.Mode.VALIDATE, base.published());
        try (var plan = DataTypeValidationPlan.prepare(config)) {
            Files.writeString(base.published(), "replaced input");
            var summary = new DataTypeValidationSummary();
            new DataTypeValidationService()
                    .validateAndWriteReport(
                            f.connection,
                            f.app(config),
                            plan,
                            "local",
                            null,
                            true,
                            summary,
                            new com.example.db2toolkit.jdbc.SafeDiagnostics(f.app(config)));
            assertEquals(0, summary.exitCode());
            assertTrue(summary.results().get(0).singleLengthMatchHint());
        }
    }

    @Test
    void invalidAndMissingItemsRemainDistinctFromCorruptedWorkbook() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "INTEGER", 0, 0);
        var base = f.run(f.config(CSV, DataTypeValidationConfig.Mode.BASELINE, null));
        f.columns.clear();
        f.column("A", "VARCHAR", 0, 1208);
        f.column("B", "VARCHAR", 0, 1208);
        f.lengths("A", 20, 10);
        f.lengths("B", 20, 10);
        var current =
                f.run(
                        f.config(
                                CSV + "fos.t,B,VARCHAR(20),VARCHAR(50)\n",
                                DataTypeValidationConfig.Mode.VALIDATE,
                                base.published()));
        assertEquals(
                List.of("BASELINE INVALID", "BASELINE NOT FOUND"),
                current.results().stream().map(DataTypeValidationResult::compare).toList());
        assertTrue(
                current.results().stream()
                        .allMatch(
                                r ->
                                        r.range() == DataTypeValidationResult.RangeStatus.PASS
                                                && !r.singleLengthMatchHint()));
        assertEquals(1, current.exitCode());
    }

    @Test
    void nullOnlyCountChangesDoNotProduceDifferencesOrHint() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "VARCHAR", 0, 1208);
        f.lengths("A", null, 10);
        var base = f.run(f.config(CSV, DataTypeValidationConfig.Mode.BASELINE, null));
        f.lengths("A", null, 100);
        var current = f.run(f.config(CSV, DataTypeValidationConfig.Mode.VALIDATE, base.published()));
        assertEquals("PASS", current.results().get(0).compare());
        assertFalse(current.results().get(0).singleLengthMatchHint());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "formula",
                "wrong-id",
                "wrong-mode",
                "missing-sheet",
                "wrong-count",
                "duplicate-bucket",
                "orphan",
                "null-length",
                "negative-count",
                "wrong-overflow",
                "missing-item",
                "duplicate-meta"
            })
    void damagedBaselinesFailPreflight(String damage) throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "VARCHAR", 0, 1208);
        f.lengths("A", 20, 10);
        var base = f.run(f.config(CSV, DataTypeValidationConfig.Mode.BASELINE, null));
        Path damaged = dir.resolve("damaged.xlsx");
        try (var book = new XSSFWorkbook(base.published().toFile())) {
            var item = book.getSheet("_items").getRow(1);
            var lengths = book.getSheet("_lengths_001");
            switch (damage) {
                case "formula" -> item.getCell(12).setCellFormula("1+1");
                case "wrong-id" -> set(meta(book, "databaseId"), "OTHER");
                case "wrong-mode" -> set(meta(book, "mode"), "VALIDATE");
                case "missing-sheet" -> book.removeSheetAt(book.getSheetIndex(lengths));
                case "wrong-count" -> set(item.getCell(12), "11");
                case "duplicate-bucket" -> {
                    var copy = lengths.createRow(2);
                    for (int i = 0; i < 7; i++)
                        copy.createCell(i)
                                .setCellValue(lengths.getRow(1).getCell(i).getStringCellValue());
                    set(meta(book, "rows._lengths_001"), "2");
                    set(item.getCell(14), "2");
                }
                case "orphan" -> set(lengths.getRow(1).getCell(3), "UNKNOWN");
                case "null-length" -> set(lengths.getRow(1).getCell(4), "true");
                case "negative-count" -> set(lengths.getRow(1).getCell(6), "-1");
                case "wrong-overflow" -> {
                    set(item.getCell(13), "1");
                    set(item.getCell(10), "FAIL");
                }
                case "missing-item" -> book.getSheet("_items").removeRow(item);
                case "duplicate-meta" -> {
                    var sheet = book.getSheet("_meta");
                    var row = sheet.createRow(sheet.getLastRowNum() + 1);
                    row.createCell(0).setCellValue("formatVersion");
                    row.createCell(1).setCellValue("1");
                }
            }
            try (var out = Files.newOutputStream(damaged)) {
                book.write(out);
            }
        }
        var config = f.config(CSV, DataTypeValidationConfig.Mode.VALIDATE, damaged);
        assertThrows(
                IOException.class,
                () -> {
                    try (var ignored = DataTypeValidationPlan.prepare(config)) {}
                });
    }

    @Test
    void baselineRejectsConflictingTotalsForOnePhysicalObject() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "VARCHAR", 0, 1208);
        f.column("B", "VARCHAR", 0, 1208);
        f.lengths("A", 10, 2);
        f.lengths("B", 20, 2);
        String csv = CSV + "fos.t,B,VARCHAR(20),VARCHAR(50)\n";
        var base = f.run(f.config(csv, DataTypeValidationConfig.Mode.BASELINE, null));
        Path damaged = dir.resolve("conflicting-object-counts.xlsx");
        try (var book = new XSSFWorkbook(base.published().toFile())) {
            set(book.getSheet("_items").getRow(2).getCell(12), "3");
            set(book.getSheet("_lengths_001").getRow(2).getCell(6), "3");
            try (var out = Files.newOutputStream(damaged)) {
                book.write(out);
            }
        }
        assertThrows(
                IOException.class,
                () -> {
                    try (var ignored =
                            DataTypeValidationPlan.prepare(
                                    f.config(csv, DataTypeValidationConfig.Mode.VALIDATE, damaged))) {}
                });
    }

    @Test
    void numericBaselineRejectsDifferentExtremaClaimingOnlyOneRow() throws Exception {
        String csv = "fos.t,A,\"DECIMAL(5,0)\",\"DECIMAL(6,0)\"\n";
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "INTEGER", 0, 0);
        f.decimal("A", 2, 0, 1, 2);
        var base = f.run(f.config(csv, DataTypeValidationConfig.Mode.BASELINE, null));
        Path damaged = dir.resolve("impossible-extrema.xlsx");
        try (var book = new XSSFWorkbook(base.published().toFile())) {
            set(book.getSheet("_items").getRow(1).getCell(12), "1");
            try (var out = Files.newOutputStream(damaged)) {
                book.write(out);
            }
        }
        assertThrows(
                IOException.class,
                () -> {
                    try (var ignored =
                            DataTypeValidationPlan.prepare(
                                    f.config(csv, DataTypeValidationConfig.Mode.VALIDATE, damaged))) {}
                });
    }

    @Test
    void sharedStringsRewrittenByExcelStillRoundTrip() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "VARCHAR", 0, 1208);
        f.lengths("A", 20, 10);
        var base = f.run(f.config(CSV, DataTypeValidationConfig.Mode.BASELINE, null));
        Path rewritten = dir.resolve("shared.xlsx");
        try (var book = new XSSFWorkbook(base.published().toFile())) {
            var item = book.getSheet("_items").getRow(1);
            item.removeCell(item.getCell(15));
            new com.example.db2toolkit.spreadsheet.ExcelValueWriter(book)
                    .write(item.createCell(15), "literal _x0041_ text");
            try (var out = Files.newOutputStream(rewritten)) {
                book.write(out);
            }
        }
        try (var plan =
                DataTypeValidationPlan.prepare(f.config(CSV, DataTypeValidationConfig.Mode.VALIDATE, rewritten))) {
            assertEquals(
                    "literal _x0041_ text",
                    plan.baseline.items().values().iterator().next().error());
        }
    }

    @Test
    void largeDistributionUsesExplicitPreviewAndRollingHiddenSheets() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        var config = f.config(CSV, DataTypeValidationConfig.Mode.BASELINE, null);
        try (var plan = DataTypeValidationPlan.prepare(config)) {
            var result = new DataTypeValidationResult(plan.items.get(0));
            result.range = DataTypeValidationResult.RangeStatus.FAIL;
            result.compare = CompareStatus.NOT_APPLICABLE;
            result.actualType = "SYSIBM.VARCHAR";
            result.buckets = new DiskLengthBuckets(plan.workspace);
            try (var append = result.buckets.appender()) {
                for (long i = 0; i < 10000; i++) append.add(i, BigInteger.ONE);
            }
            result.records = BigInteger.valueOf(10000);
            result.overflow = BigInteger.valueOf(9979);
            Path output = dir.resolve("large.xlsx");
            try (var writer = new DataTypeValidationWorkbookWriter(3000);
                    var staging = org.mockito.Mockito.mockStatic(
                        com.example.db2toolkit.output.TemporaryOutputFile.class,
                        org.mockito.Mockito.CALLS_REAL_METHODS)) {
                staging.when(() -> com.example.db2toolkit.output.TemporaryOutputFile.create(
                    dir, "validation-")).thenAnswer(invocation -> {
                        var field = DataTypeValidationWorkbookWriter.class.getDeclaredField("book");
                        field.setAccessible(true);
                        var book = (org.apache.poi.xssf.streaming.SXSSFWorkbook) field.get(writer);
                        for (int n = 1; n <= 4; n++)
                            assertTrue(book.getSheet("_lengths_" + String.format(Locale.ROOT, "%03d", n))
                                .areAllRowsFlushed(), "Hidden continuation windows must be released before publication");
                        return invocation.callRealMethod();
                    });
                writer.write(
                        output,
                        config,
                        "local",
                        List.of(result),
                        Instant.now().minusSeconds(1),
                        Instant.now());
            }
            try (var book = new XSSFWorkbook(output.toFile())) {
                String detail = book.getSheetAt(0).getRow(1).getCell(6).getStringCellValue();
                assertTrue(detail.contains("TRUNCATED PREVIEW"));
                assertTrue(detail.length() <= 32767);
                assertNotNull(book.getSheet("_lengths_004"));
                assertEquals(1000, book.getSheet("_lengths_004").getLastRowNum());
            }
            try (var loaded =
                    DataTypeValidationPlan.prepare(f.config(CSV, DataTypeValidationConfig.Mode.VALIDATE, output))) {
                assertEquals(
                        10000, loaded.baseline.items().values().iterator().next().buckets().size());
            }
        }
    }

    private static Cell meta(XSSFWorkbook book, String key) {
        for (var row : book.getSheet("_meta"))
            if (row.getCell(0).getStringCellValue().equals(key)) return row.getCell(1);
        throw new AssertionError(key);
    }

    @Test
    void largeBidirectionalDifferencesAreCompleteAcrossHiddenContinuationSheets() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        var baseConfig = f.config(CSV, DataTypeValidationConfig.Mode.BASELINE, null);
        Path baseline = dir.resolve("large-base.xlsx");
        try (var plan = DataTypeValidationPlan.prepare(baseConfig)) {
            var base = lengthSeries(plan, 0);
            base.compare = CompareStatus.NOT_APPLICABLE;
            try (var writer = new DataTypeValidationWorkbookWriter(3000)) {
                writer.write(
                        baseline,
                        baseConfig,
                        "local",
                        List.of(base),
                        Instant.now().minusSeconds(1),
                        Instant.now());
            }
        }
        var config = f.config(CSV, DataTypeValidationConfig.Mode.VALIDATE, baseline);
        Path output = dir.resolve("large-diffs.xlsx");
        try (var plan = DataTypeValidationPlan.prepare(config)) {
            var current = lengthSeries(plan, 1);
            LengthSetComparator.compare(current, config, plan);
            assertEquals("DIFFERENT", current.compare());
            assertEquals(10000, current.baselineOnly.size());
            assertEquals(10000, current.currentOnly.size());
            try (var writer = new DataTypeValidationWorkbookWriter(3000)) {
                writer.write(
                        output,
                        config,
                        "local",
                        List.of(current),
                        Instant.now().minusSeconds(1),
                        Instant.now());
            }
        }
        try (var book = new XSSFWorkbook(output.toFile())) {
            assertTrue(
                    book.getSheetAt(0)
                            .getRow(1)
                            .getCell(9)
                            .getStringCellValue()
                            .contains("TRUNCATED PREVIEW"));
            assertTrue(
                    book.getSheetAt(0)
                            .getRow(1)
                            .getCell(10)
                            .getStringCellValue()
                            .contains("TRUNCATED PREVIEW"));
            assertEquals(
                    "10000", book.getSheet("_items").getRow(1).getCell(19).getStringCellValue());
            assertEquals(
                    "10000", book.getSheet("_items").getRow(1).getCell(20).getStringCellValue());
            int baseCount = 0, currentCount = 0;
            for (int n = 1; n <= 7; n++) {
                var sheet = book.getSheet(String.format(java.util.Locale.ROOT, "_diffs_%03d", n));
                assertNotNull(sheet);
                assertTrue(book.isSheetHidden(book.getSheetIndex(sheet)));
                assertEquals(n == 7 ? 2000 : 3000, sheet.getLastRowNum());
                for (int row = 1; row <= sheet.getLastRowNum(); row++) {
                    var data = sheet.getRow(row);
                    assertEquals("false", data.getCell(5).getStringCellValue());
                    long value = Long.parseLong(data.getCell(6).getStringCellValue());
                    if (data.getCell(4).getStringCellValue().equals("BASELINE_ONLY"))
                        assertEquals(2L * baseCount++, value);
                    else {
                        assertEquals("CURRENT_ONLY", data.getCell(4).getStringCellValue());
                        assertEquals(2L * currentCount++ + 1, value);
                    }
                }
            }
            assertEquals(10000, baseCount);
            assertEquals(10000, currentCount);
            assertNull(book.getSheet("_diffs_008"));
        }
        assertTrue(f.queries.isEmpty());
    }

    private static DataTypeValidationResult lengthSeries(DataTypeValidationPlan plan, int parity) throws Exception {
        var result = new DataTypeValidationResult(plan.items.get(0));
        result.range = DataTypeValidationResult.RangeStatus.FAIL;
        result.actualType = "SYSIBM.VARCHAR";
        result.buckets = new DiskLengthBuckets(plan.workspace);
        result.overflow = BigInteger.ZERO;
        try (var append = result.buckets.appender()) {
            for (long i = 0; i < 10000; i++) {
                long length = i * 2 + parity;
                append.add(length, BigInteger.ONE);
                if (length > result.item.original().size())
                    result.overflow = result.overflow.add(BigInteger.ONE);
            }
        }
        result.records = result.buckets.total();
        return result;
    }

    private static void set(Cell cell, String value) throws Exception {
        Row row = cell.getRow();
        int index = cell.getColumnIndex();
        row.removeCell(cell);
        new com.example.db2toolkit.spreadsheet.ExcelValueWriter(row.getSheet().getWorkbook())
                .write(row.createCell(index), value);
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationApplicationTest.java =====
UTF8-BYTES: 24208
SHA256: 0e4f0ec5c71336803cc5e9768ef756ab948162b94a64ff8cefb681be8365c317
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.db2toolkit.application.ToolkitApplication;
import com.example.db2toolkit.jdbc.SafeDiagnostics;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class DataTypeValidationApplicationTest {
    @TempDir Path dir;

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
    void everyFeatureCombinationHasIndependentOutputsOneConnectionAndCorrectOrder(int mask)
            throws Exception {
        boolean ddl = (mask & 1) != 0, excel = (mask & 2) != 0, validation = (mask & 4) != 0;
        Path config = config(ddl, excel, validation);
        var fixture = fixture();
        excel(fixture, false);
        AtomicInteger opened = new AtomicInteger();
        int code =
                new ToolkitApplication(
                                key -> null,
                                app -> {
                                    opened.incrementAndGet();
                                    return fixture.connection;
                                })
                        .run(new String[] {config.toString()});
        assertEquals(0, code);
        assertEquals(mask == 0 ? 0 : 1, opened.get());
        if (mask != 0) verify(fixture.connection).close();
        else verifyNoInteractions(fixture.connection);
        assertEquals(ddl ? 1 : 0, find(".sql").size());
        assertEquals((excel ? 1 : 0) + (validation ? 1 : 0), find(".xlsx").size());
        assertEquals(mask == 0 ? 0 : 1, find("process.log").size());
        if (validation) {
            Path report = find("datatype-validation.xlsx").get(0);
            assertEquals("default-datatype-validation.xlsx", report.getFileName().toString());
            assertFalse(report.toString().contains("db-default"));
            String success = Files.readString(find("summary_success.log").get(0));
            assertTrue(success.contains("Validation \"FOS\".\"T\".A"));
            assertTrue(success.contains("Database run finished: exitCode=0"));
            int validationQuery = index(fixture.queries, "SELECT TYPE");
            if (excel) assertTrue(index(fixture.queries, "EXCEL:") < validationQuery);
            if (ddl) assertTrue(index(fixture.queries, "SELECT SPECIFICNAME") < validationQuery);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
    void multiDatabaseFeatureCombinationsKeepPerDatabaseOrderAndIsolation(int mask) throws Exception {
        boolean ddl = (mask & 1) != 0, hasExcel = (mask & 2) != 0, validation = (mask & 4) != 0;
        Path file = config(ddl, hasExcel, validation);
        addAliases(file);
        var a = fixture();
        var b = fixture();
        b.lengths("A", 20, 99);
        excel(a, false);
        excel(b, false);
        List<String> lifecycle = new java.util.concurrent.CopyOnWriteArrayList<>();
        doAnswer(
                        inv -> {
                            lifecycle.add("close-first");
                            return null;
                        })
                .when(a.connection)
                .close();
        doAnswer(
                        inv -> {
                            lifecycle.add("close-second");
                            return null;
                        })
                .when(b.connection)
                .close();
        int code =
                new ToolkitApplication(
                                key -> null,
                                app -> {
                                    lifecycle.add("open-" + app.username());
                                    return app.username().equals("first")
                                            ? a.connection
                                            : b.connection;
                                })
                        .run(new String[] {file.toString()});
        assertEquals(0, code);
        assertEquals(
                mask == 0
                        ? List.of()
                        : List.of("close-first", "close-second", "open-first", "open-second"),
                lifecycle.stream().sorted().toList());
        assertEquals(ddl ? 2 : 0, find(".sql").size());
        assertEquals(2 * ((hasExcel ? 1 : 0) + (validation ? 1 : 0)), find(".xlsx").size());
        assertEquals(mask == 0 ? 0 : 2, find("process.log").size());
        for (var f : List.of(a, b)) {
            if (mask == 0) {
                verifyNoInteractions(f.connection);
                continue;
            }
            verify(f.connection).close();
            if (ddl && hasExcel)
                assertTrue(index(f.queries, "SELECT SPECIFICNAME") < index(f.queries, "EXCEL:"));
            if (validation && ddl)
                assertTrue(
                        index(f.queries, "SELECT SPECIFICNAME") < index(f.queries, "SELECT TYPE"));
            if (validation && hasExcel)
                assertTrue(index(f.queries, "EXCEL:") < index(f.queries, "SELECT TYPE"));
        }
        for (Path log : find("process.log")) {
            String alias = log.toString().contains("db-a") ? "a" : "b";
            assertTrue(
                    Files.readAllLines(log).stream()
                            .allMatch(line -> line.contains("[db=" + alias + "]")));
        }
        if (validation)
            for (Path report : find("datatype-validation.xlsx")) {
                boolean first = report.startsWith(dir.resolve("validation/db-a"));
                try (var book = new org.apache.poi.xssf.usermodel.XSSFWorkbook(report.toFile())) {
                    assertEquals(
                            first ? 10 : 99,
                            book.getSheetAt(0).getRow(1).getCell(4).getNumericCellValue());
                }
            }
    }

    @Test
    void aliasesShareOneLogicalBaselineWithoutSharingCountsOrOutputs() throws Exception {
        var baseFixture = fixture();
        Path baseline =
                baseFixture
                        .run(
                                baseFixture.config(
                                        BaselineRoundTripTest.CSV,
                                        DataTypeValidationConfig.Mode.BASELINE,
                                        null))
                        .published();
        Path file = config(false, false, true);
        addAliases(file);
        Files.writeString(
                file,
                "\n"
                    + "validation.mode=VALIDATE\n"
                    + "validation.baseline.compare.enabled=true\n"
                    + "validation.baseline.file="
                        + baseline.toString().replace('\\', '/')
                        + "\n",
                StandardOpenOption.APPEND);
        var first = fixture();
        var second = fixture();
        first.lengths("A", 20, 15);
        second.lengths("A", 20, 70);
        assertEquals(
                0,
                new ToolkitApplication(
                                key -> null,
                                app ->
                                        app.username().equals("first")
                                                ? first.connection
                                                : second.connection)
                        .run(new String[] {file.toString()}));
        for (String alias : List.of("a", "b")) {
            Path report =
                    find("datatype-validation.xlsx").stream()
                            .filter(p -> p.startsWith(dir.resolve("validation/db-" + alias)))
                            .findFirst()
                            .orElseThrow();
            try (var book = new org.apache.poi.xssf.usermodel.XSSFWorkbook(report.toFile())) {
                var row = book.getSheetAt(0).getRow(1);
                assertEquals(alias.equals("a") ? 15 : 70, row.getCell(4).getNumericCellValue());
                assertEquals("PASS", row.getCell(8).getStringCellValue());
                assertTrue(
                        row.getCell(6)
                                .getStringCellValue()
                                .contains(DataTypeValidationWorkbookFormat.HINT));
            }
            Path log =
                    find("process.log").stream()
                            .filter(p -> p.startsWith(dir.resolve("validation/db-" + alias)))
                            .findFirst()
                            .orElseThrow();
            assertTrue(Files.readString(log).contains("Validation baseline: path="));
        }
        assertEquals(1, first.queries.stream().filter(q -> q.contains("GROUP BY")).count());
        assertEquals(1, second.queries.stream().filter(q -> q.contains("GROUP BY")).count());
    }

    @ParameterizedTest
    @ValueSource(strings = {"SELECT TYPE", "GROUP BY"})
    void objectEndIsLoggedExactlyOnceEvenWhenMetadataOrQueryFails(String failingSql)
            throws Exception {
        Path file = config(false, false, true);
        var f = fixture();
        f.failures.put(failingSql, new SQLException("denied", "42501"));
        assertEquals(
                1,
                new ToolkitApplication(key -> null, app -> f.connection)
                        .run(new String[] {file.toString()}));
        String log = Files.readString(find("process.log").get(0));
        assertEquals(1, log.lines().filter(s -> s.contains("Validation object started:")).count());
        assertEquals(
                1,
                log.lines()
                        .filter(
                                s ->
                                        s.contains("Validation object finished:")
                                                && s.contains("endedAt=")
                                                && s.contains("elapsedMs="))
                        .count());
    }

    @Test
    void validationOutputFailureDoesNotSkipDdlOrExcel() throws Exception {
        var file = config(true, true, true);
        Files.writeString(dir.resolve("validation"), "not a directory");
        var f = fixture();
        excel(f, false);
        assertEquals(
                2,
                new ToolkitApplication(key -> null, app -> f.connection)
                        .run(new String[] {file.toString()}));
        assertEquals(1, find(".sql").size());
        assertEquals(1, find(".xlsx").size());
        assertTrue(
                Files.readString(find("summary_issue.log").get(0))
                        .contains("Validation output/fatal failure"));
    }

    @Test
    void excelConnectionLossStopsValidationQueriesButPublishesDiagnosticReport() throws Exception {
        var file = config(false, true, true);
        var f = fixture();
        excel(f, true);
        assertEquals(
                1,
                new ToolkitApplication(key -> null, app -> f.connection)
                        .run(new String[] {file.toString()}));
        assertFalse(f.queries.stream().anyMatch(q -> q.startsWith("SELECT TYPE")));
        assertEquals(1, find("datatype-validation.xlsx").size());
        assertTrue(Files.readString(find("summary_issue.log").get(0)).contains("NOT EXECUTED"));
    }

    @Test
    void allTargetsPreflightBeforeOpeningAnyConnection() throws Exception {
        Path file = multi();
        Files.writeString(dir.resolve("second.csv"), "bad header\n");
        AtomicInteger opened = new AtomicInteger();
        assertEquals(
                2,
                new ToolkitApplication(
                                key -> null,
                                app -> {
                                    opened.incrementAndGet();
                                    throw new SQLException();
                                })
                        .run(new String[] {file.toString()}));
        assertEquals(0, opened.get());
        assertTrue(find("process.log").isEmpty());
    }

    @Test
    void multiDatabaseConnectionFailureIsolatedAndPathsNotDoublePrefixed() throws Exception {
        Path file = multi();
        var second = fixture();
        List<String> opened = new java.util.concurrent.CopyOnWriteArrayList<>();
        int code =
                new ToolkitApplication(
                                key -> null,
                                app -> {
                                    opened.add(app.username());
                                    if (app.username().equals("first"))
                                        throw new SQLException("lost secret", "08006");
                                    return second.connection;
                                })
                        .run(new String[] {file.toString()});
        assertEquals(2, code);
        assertEquals(List.of("first", "second"), opened.stream().sorted().toList());
        Path output = find("datatype-validation.xlsx").get(0);
        assertTrue(output.startsWith(dir.resolve("validation/db-b")));
        assertFalse(output.toString().contains("db-b\\db-b"));
        assertEquals(2, find("process.log").size());
        verify(second.connection).close();
    }

    @Test
    void publicationFailureAfterPassingSqlReturnsTwoAndNeverOverwrites() throws Exception {
        var f = fixture();
        var config = f.config(BaselineRoundTripTest.CSV, DataTypeValidationConfig.Mode.BASELINE, null);
        Path run = Files.createDirectory(dir.resolve("run"));
        Path existing = run.resolve("local-datatype-validation.xlsx");
        Files.writeString(existing, "keep");
        var summary = new DataTypeValidationSummary();
        try (var plan = DataTypeValidationPlan.prepare(config)) {
            new DataTypeValidationService()
                    .validateAndWriteReport(
                            f.connection,
                            f.app(config),
                            plan,
                            "local",
                            run,
                            true,
                            summary,
                            new SafeDiagnostics(f.app(config)));
        }
        assertEquals(2, summary.exitCode());
        assertNull(summary.published());
        assertEquals("keep", Files.readString(existing));
        assertEquals(DataTypeValidationResult.RangeStatus.PASS, summary.results().get(0).range());
    }

    @Test
    void cleanupFailureAfterPublicationPreservesPublishedFactAndCounts() throws Exception {
        var f = fixture();
        var config = f.config(BaselineRoundTripTest.CSV, DataTypeValidationConfig.Mode.BASELINE, null);
        Path output = dir.resolve("published.xlsx");
        var summary = new DataTypeValidationSummary();
        try (var writers =
                        mockConstruction(
                                DataTypeValidationWorkbookWriter.class,
                                (writer, context) -> {
                                    when(writer.published()).thenReturn(output);
                                    doThrow(new IOException("close failed")).when(writer).close();
                                });
                var plan = DataTypeValidationPlan.prepare(config)) {
            new DataTypeValidationService()
                    .validateAndWriteReport(
                            f.connection,
                            f.app(config),
                            plan,
                            "local",
                            null,
                            true,
                            summary,
                            new SafeDiagnostics(f.app(config)));
        }
        assertEquals(2, summary.exitCode());
        assertEquals(output, summary.published());
        assertEquals(1, summary.results().size());
        assertEquals(Map.of("PASS", 1L), summary.rangeCounts());
    }

    @Test
    void stagingFailureIsFatalAndCannotPublishIncompleteDistribution() throws Exception {
        var f = fixture();
        var config = f.config(BaselineRoundTripTest.CSV, DataTypeValidationConfig.Mode.BASELINE, null);
        var summary = new DataTypeValidationSummary();
        try (var buckets =
                        mockConstruction(
                                DiskLengthBuckets.class,
                                (bucket, context) -> {
                                    when(bucket.appender()).thenThrow(new IOException("disk full"));
                                });
                var plan = DataTypeValidationPlan.prepare(config)) {
            new DataTypeValidationService()
                    .validateAndWriteReport(
                            f.connection,
                            f.app(config),
                            plan,
                            "local",
                            null,
                            true,
                            summary,
                            new SafeDiagnostics(f.app(config)));
        }
        assertEquals(2, summary.exitCode());
        assertNull(summary.published());
        assertTrue(find(".xlsx").isEmpty());
    }

    @Test
    void workbookWriteFailureAfterPassingSqlReturnsTwo() throws Exception {
        var f = fixture();
        var config = f.config(BaselineRoundTripTest.CSV, DataTypeValidationConfig.Mode.BASELINE, null);
        var summary = new DataTypeValidationSummary();
        try (var writers =
                        mockConstruction(
                                DataTypeValidationWorkbookWriter.class,
                                (writer, context) -> {
                                    doThrow(new IOException("write failed"))
                                            .when(writer)
                                            .write(any(), any(), any(), any(), any(), any());
                                });
                var plan = DataTypeValidationPlan.prepare(config)) {
            new DataTypeValidationService()
                    .validateAndWriteReport(
                            f.connection,
                            f.app(config),
                            plan,
                            "local",
                            null,
                            true,
                            summary,
                            new SafeDiagnostics(f.app(config)));
        }
        assertEquals(2, summary.exitCode());
        assertNull(summary.published());
        assertEquals(DataTypeValidationResult.RangeStatus.PASS, summary.results().get(0).range());
    }

    private DataTypeValidationFixture fixture() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "VARCHAR", 0, 1208);
        f.lengths("A", 20, 10);
        return f;
    }

    private Path config(boolean ddl, boolean excel, boolean validation) throws Exception {
        Files.writeString(dir.resolve("sp.txt"), "RPT.P\n");
        Files.writeString(dir.resolve("empty.txt"), "");
        Files.writeString(
                dir.resolve("input.csv"), DataTypeValidationFixture.HEADER + BaselineRoundTripTest.CSV);
        Files.writeString(
                dir.resolve("excel.yaml"),
                "excel-export:\n"
                    + "  enabled: true\n"
                    + "  output-directory: ./excel\n"
                    + "  simple:\n"
                    + "    tables:\n"
                    + "      - FOS.T\n");
        Path file = dir.resolve("application.properties");
        Files.writeString(
                file,
                "db.url=jdbc:db2://unused/DB\ndb.username=user\ndb.password=secret\nexport.enabled="
                        + ddl
                        + "\n"
                        + "export.procedure-list=sp.txt\n"
                        + "export.function-list=empty.txt\n"
                        + "export.table-list=empty.txt\n"
                        + "export.output-directory=ddl\n"
                        + (excel ? "excel-export.config=excel.yaml\n" : "")
                        + "validation.enabled="
                        + validation
                        + "\n"
                        + "validation.mode=BASELINE\n"
                        + "validation.database-id=FOS\n"
                        + "validation.file=input.csv\n"
                        + "validation.output-directory=validation\n"
                        + "validation.itsview.enabled=true\n");
        return file;
    }

    private Path multi() throws Exception {
        Path file = config(false, false, true);
        Files.writeString(
                dir.resolve("second.csv"), DataTypeValidationFixture.HEADER + BaselineRoundTripTest.CSV);
        Files.writeString(
                file,
                "\n"
                    + "db.names=a,b\n"
                    + "db.a.url=jdbc:db2://unused/A\n"
                    + "db.a.username=first\n"
                    + "db.a.password=secret\n"
                    + "db.b.url=jdbc:db2://unused/B\n"
                    + "db.b.username=second\n"
                    + "db.b.password=secret\n"
                    + "db.b.validation.file=second.csv\n",
                StandardOpenOption.APPEND);
        return file;
    }

    private static void addAliases(Path file) throws IOException {
        Files.writeString(
                file,
                "\n"
                    + "db.names=a,b\n"
                    + "db.a.url=jdbc:db2://unused/A\n"
                    + "db.a.username=first\n"
                    + "db.a.password=secret\n"
                    + "db.b.url=jdbc:db2://unused/B\n"
                    + "db.b.username=second\n"
                    + "db.b.password=secret\n",
                StandardOpenOption.APPEND);
    }

    private List<Path> find(String suffix) throws IOException {
        try (var paths = Files.walk(dir)) {
            return paths.filter(Files::isRegularFile)
                    .filter(p -> suffix.equals("datatype-validation.xlsx")
                            ? p.getFileName().toString().endsWith("-datatype-validation.xlsx")
                            : p.getFileName().toString().endsWith(suffix))
                    .toList();
        }
    }

    private static int index(List<String> queries, String prefix) {
        for (int i = 0; i < queries.size(); i++) if (queries.get(i).startsWith(prefix)) return i;
        return -1;
    }

    private static void excel(DataTypeValidationFixture f, boolean lost) throws Exception {
        when(f.connection.prepareStatement(anyString(), anyInt(), anyInt()))
                .thenAnswer(
                        inv -> {
                            f.queries.add("EXCEL:" + inv.getArgument(0));
                            var statement = mock(PreparedStatement.class);
                            if (lost)
                                when(statement.executeQuery())
                                        .thenThrow(new SQLException("lost secret", "08006"));
                            else
                                when(statement.executeQuery())
                                        .thenAnswer(
                                                call -> {
                                                    var rows = mock(ResultSet.class);
                                                    when(rows.next()).thenReturn(true, false);
                                                    var meta = mock(ResultSetMetaData.class);
                                                    when(meta.getColumnCount()).thenReturn(1);
                                                    when(meta.getColumnLabel(1)).thenReturn("A");
                                                    when(rows.getMetaData()).thenReturn(meta);
                                                    when(rows.getObject(1)).thenReturn("test");
                                                    return rows;
                                                });
                            return statement;
                        });
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationCleanupTest.java =====
UTF8-BYTES: 2963
SHA256: 5fa76448737a26da72158354b1a4ad2207a2e8de05ef706339ee08f7d7d45d16
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import com.example.db2toolkit.config.DatabaseTarget;
import com.example.db2toolkit.jdbc.SafeDiagnostics;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataTypeValidationCleanupTest {
    @TempDir Path directory;

    @Test void batchRetriesCleanupAfterThePerDatabaseCloseFails() throws Exception {
        var fixture = new DataTypeValidationFixture(directory);
        var config = fixture.config(BaselineRoundTripTest.CSV, DataTypeValidationConfig.Mode.BASELINE, null);
        var app = fixture.app(config);
        try (var batch = new DataTypeValidationBatch()) {
            batch.prepare(List.of(new DatabaseTarget("test", app)), new SafeDiagnostics(app));
            var plan = batch.get("test");
            Path blocked = plan.workspace.file();
            Path other = plan.workspace.file();
            try {
                try (var files = mockStatic(Files.class, invocation -> {
                    if (invocation.getMethod().getName().equals("deleteIfExists")
                            && blocked.equals(invocation.getArgument(0)))
                        throw new IOException("temporary file is locked");
                    return invocation.callRealMethod();
                })) {
                    IOException failure = assertThrows(IOException.class, plan::close);
                    assertEquals("temporary file is locked", failure.getMessage());
                    assertTrue(Files.exists(blocked));
                    assertFalse(Files.exists(other), "Cleanup must still process later files");
                }
                batch.close();
                assertFalse(Files.exists(blocked), "Batch close must retry the failed plan");
                assertFalse(Files.exists(blocked.getParent()));
            } finally {
                plan.workspace.close();
            }
        }
    }

    @Test void completedWorkspaceCloseIsIdempotentAndDoesNotTouchReusedPaths() throws Exception {
        var workspace = new DataTypeValidationWorkspace();
        Path staged = workspace.file();
        Path workspaceDirectory = staged.getParent();
        workspace.close();
        assertDoesNotThrow(workspace::close);
        Files.createDirectory(workspaceDirectory);
        Path replacement = Files.writeString(workspaceDirectory.resolve("unrelated.txt"), "keep");
        try {
            assertDoesNotThrow(workspace::close);
            assertEquals("keep", Files.readString(replacement));
            assertThrows(IOException.class, workspace::file);
        } finally {
            Files.delete(replacement);
            Files.delete(workspaceDirectory);
        }
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationConfigTest.java =====
UTF8-BYTES: 10199
SHA256: dbdd029f032e9859a2d6975be9962ff8fefc955bafca2f24813d39f71b2213e4
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import static org.junit.jupiter.api.Assertions.*;

import com.example.db2toolkit.config.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.*;

class DataTypeValidationConfigTest {
    @TempDir Path dir;

    @Test
    void malformedWideCsvFailsBeforeReadingTheRestOfTheRecord() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        // The unterminated quote must never be reached: reject at the sixth field boundary.
        var config = f.config(",,,,,\"unterminated", DataTypeValidationConfig.Mode.BASELINE, null);
        IOException failure = assertThrows(IOException.class, () -> new DataTypeValidationCsvReader().read(config));
        assertTrue(failure.getMessage().contains("exceeds five columns"));
        assertFalse(failure.getMessage().contains("Unclosed"));
    }

    private AppConfig load(String extra) throws Exception {
        Path file = dir.resolve("application.properties");
        Files.writeString(
                file,
                "\uFEFFdb.url=jdbc:db2://unused/DB\n"
                    + "db.username=u\n"
                    + "db.password=secret\n"
                    + "export.enabled=false\n"
                        + extra);
        return new ConfigLoader().load(file, key -> null);
    }

    @Test
    void disabledDoesNotParseConditionalValuesAndDefaultsRemainCompatible() throws Exception {
        var config =
                load(
                        "validation.mode=invalid\n"
                            + "validation.file=missing.csv\n"
                            + "validation.itsview.enabled=bad\n"
                            + "validation.baseline.compare.enabled=bad\n");
        assertFalse(config.validation().enabled());
        assertEquals("\"SYSCAT\".\"TABLES\"", config.tables());
        assertEquals("secret", config.password());
    }

    @Test
    void baselineIgnoresInvalidPathAndDisabledViewSchema() throws Exception {
        var config =
                load("validation.enabled=true\n"
                         + "validation.mode=baseline\n"
                         + "validation.database-id=fos\n"
                         + "validation.file=./input.csv\n"
                         + "validation.baseline.compare.enabled=true\n"
                         + "validation.baseline.file=bad\u0000path\n"
                         + "validation.itsview.schema=\"bad\"\n")
                        .validation();
        assertEquals("FOS", config.databaseId());
        assertEquals(dir.resolve("input.csv"), config.file());
        assertNull(config.baseline());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "validation.enable=true",
                "validation.environment=UAT",
                "validation.prod.compare.enabled=true",
                "db.missing.validation.enabled=true",
                "db.default.validation.enabled=true",
                "validation.enabled=yes"
            })
    void invalidNewKeysOrBooleanFail(String extra) {
        assertThrows(ConfigurationException.class, () -> load(extra));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "2147483648", "", "1.0"})
    void invalidTimeoutFails(String timeout) {
        assertThrows(
                ConfigurationException.class,
                () ->
                        load(
                                "validation.enabled=true\n"
                                    + "validation.mode=BASELINE\n"
                                    + "validation.database-id=FOS\n"
                                    + "validation.file=in.csv\n"
                                    + "validation.query-timeout-seconds="
                                        + timeout));
    }

    @Test
    void multiOverridesAreIndependentAndWithExcelKeepsValidation() throws Exception {
        Path file = dir.resolve("multi.properties");
        Files.writeString(
                file,
                """
                db.names=a,b
                db.a.url=jdbc:db2://unused/A
                db.a.username=u
                db.a.password=secret
                db.b.url=jdbc:db2://unused/B
                db.b.username=u
                db.b.password=secret
                export.enabled=false
                validation.enabled=true
                validation.mode=BASELINE
                validation.database-id=shared
                validation.file=in.csv
                db.b.validation.file=second.csv
                db.b.validation.length-unit=CODEUNITS32
                db.b.catalog.table.view=MYCAT.OBJECTS
                db.b.catalog.column.view=MYCAT.COLUMNS
                """);
        var targets = new ConfigLoader().loadTargets(file, key -> null);
        var a = targets.get(0).config();
        var b = targets.get(1).config();
        assertTrue(a.validation().enabled());
        assertEquals(a.validation().databaseId(), b.validation().databaseId());
        assertEquals(dir.resolve("second.csv"), b.validation().file());
        assertEquals("\"MYCAT\".\"OBJECTS\"", b.tables());
        assertEquals("\"MYCAT\".\"COLUMNS\"", b.columns());
        assertEquals(dir.resolve("output/validation/db-a"), a.validation().output());
        assertEquals(DataTypeValidationConfig.LengthUnit.CODEUNITS32, b.validation().lengthUnit());
    }

    @Test
    void csvSupportsBomReorderedHeadersQuotesAndPerRowUnits() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        var config = f.config("", DataTypeValidationConfig.Mode.BASELINE, null);
        Files.writeString(
                config.file(),
                "\uFEFFFIELD_NAME,table_name,after_data_type,original_data_type,length_unit\r\n\r\n"
                    + "A,fos.t,\"DECIMAL(31,0)\",\"DECIMAL(5,0)\",\r\n"
                    + "B,fos.t,CLOB(500),VARCHAR(20),CODEUNITS32\r\n");
        var items = new DataTypeValidationCsvReader().read(config);
        assertEquals(2, items.size());
        assertEquals("ITSVIEW", items.get(0).physical().schema());
        assertEquals("FOS_T", items.get(0).physical().name());
        assertEquals(DataTypeValidationConfig.LengthUnit.CODEUNITS32, items.get(1).unit());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "",
                ",,,\n",
                "fos.t,A,VARCHAR(10),VARCHAR(9)\n",
                "fos.t,A,VARCHAR(0),VARCHAR(1)\n",
                "fos.t,A,VARCHAR(32768),CLOB(50000)\n",
                "fos.t,A,\"DECIMAL(32,0)\",\"DECIMAL(32,0)\"\n",
                "fos.t,A,\"DECIMAL(5,2)\",\"DECIMAL(6,2)\"\n",
                "fos.t,A,CLOB(10),VARCHAR(20)\n",
                "fos.t,A,VARCHAR(10),VARCHAR(20)\nFOS.T,a,VARCHAR(10),VARCHAR(20)\n",
                "A_B.C,A,VARCHAR(10),VARCHAR(20)\nA.B_C,A,VARCHAR(10),VARCHAR(20)\n",
                "fos.t;DROP,A,VARCHAR(10),VARCHAR(20)\n",
                "fos.t,A,\"DECIMAL(5,0),DECIMAL(6,0)\n"
            })
    void invalidCsvNeverSkipsBadRecords(String csv) throws Exception {
        var f = new DataTypeValidationFixture(dir);
        var config = f.config(csv, DataTypeValidationConfig.Mode.BASELINE, null);
        assertThrows(IOException.class, () -> new DataTypeValidationCsvReader().read(config));
    }

    @Test
    void deliveredExamplesHaveValidConfigYamlAndCsvPaths() throws Exception {
        try (var files = Files.list(Path.of("config/datatypevalidation"))) {
            for (Path file : files.filter(p -> p.toString().endsWith(".properties")).toList()) {
                var targets = new ConfigLoader().loadTargets(file, key -> "example-password");
                assertFalse(targets.isEmpty());
                for (var target : targets) {
                    assertTrue(target.config().validation().enabled());
                    assertFalse(
                            new DataTypeValidationCsvReader().read(target.config().validation()).isEmpty());
                    for (Path list : target.config().lists().values())
                        assertTrue(Files.isRegularFile(list));
                }
            }
        }
    }

    @Test
    void sameCsvUsesEachTargetsDefaultUnitAndRejectsHeaderProblems() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        var config =
                f.config(
                        "fos.t,A,VARCHAR(100),VARCHAR(200)\n",
                        DataTypeValidationConfig.Mode.BASELINE,
                        null);
        var unicode =
                new DataTypeValidationConfig(
                        true,
                        config.mode(),
                        config.databaseId(),
                        config.file(),
                        config.output(),
                        config.itsview(),
                        config.viewSchema(),
                        DataTypeValidationConfig.LengthUnit.CODEUNITS32,
                        false,
                        null,
                        0);
        assertEquals(
                DataTypeValidationConfig.LengthUnit.OCTETS,
                new DataTypeValidationCsvReader().read(config).get(0).unit());
        assertEquals(
                DataTypeValidationConfig.LengthUnit.CODEUNITS32,
                new DataTypeValidationCsvReader().read(unicode).get(0).unit());
        for (String header :
                new String[] {
                    "table_name,field_name,original_data_type,original_data_type",
                    "table_name,field_name,original_data_type",
                    "table_name,field_name,original_data_type,after_data_type,unknown"
                }) {
            Files.writeString(config.file(), header + "\n");
            assertThrows(IOException.class, () -> new DataTypeValidationCsvReader().read(config));
        }
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationFixture.java =====
UTF8-BYTES: 9181
SHA256: f26868ae6c96c8025f81abb67795e50ad36c9c6446e909b9e4f83d0eeb368865
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.db2toolkit.config.*;
import com.example.db2toolkit.excel.ExcelExportConfig;
import com.example.db2toolkit.jdbc.SafeDiagnostics;

import java.math.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

final class DataTypeValidationFixture {
    static final String HEADER = "table_name,field_name,original_data_type,after_data_type\n";
    final Path dir;
    final Connection connection = mock(Connection.class);
    final List<String> queries = new ArrayList<>();
    final List<PreparedStatement> statements = new ArrayList<>();
    final Map<String, List<Map<String, Object>>> data = new HashMap<>();
    final Map<String, SQLException> failures = new HashMap<>();
    final List<Map<String, Object>> columns = new ArrayList<>();
    String objectType = "V";
    boolean missingObject;

    DataTypeValidationFixture(Path dir) throws Exception {
        this.dir = dir;
        when(connection.isValid(5)).thenReturn(true);
        when(connection.prepareStatement(anyString()))
                .thenAnswer(
                        inv -> {
                            String sql = inv.getArgument(0);
                            queries.add(sql);
                            var statement = mock(PreparedStatement.class);
                            statements.add(statement);
                            when(statement.executeQuery())
                                    .thenAnswer(
                                            call -> {
                                                for (var f : failures.entrySet())
                                                    if (sql.contains(f.getKey()))
                                                        throw f.getValue();
                                                if (sql.startsWith("SELECT SPECIFICNAME")) {
                                                    ResultSet routine =
                                                            rows(
                                                                    List.of(
                                                                            map(
                                                                                    "SPECIFICNAME",
                                                                                    "P_SPEC")));
                                                    when(routine.getCharacterStream("TEXT"))
                                                            .thenReturn(
                                                                    new java.io.StringReader(
                                                                            "CREATE PROCEDURE"
                                                                                + " RPT.P() BEGIN"
                                                                                + " END;"));
                                                    return routine;
                                                }
                                                if (sql.startsWith("SELECT TYPE"))
                                                    return rows(
                                                            missingObject
                                                                    ? List.of()
                                                                    : List.of(
                                                                            map(
                                                                                    "TYPE",
                                                                                    objectType)));
                                                if (sql.startsWith("SELECT COLNAME"))
                                                    return rows(columns);
                                                for (var d : data.entrySet())
                                                    if (sql.contains('"' + d.getKey() + '"'))
                                                        return rows(d.getValue());
                                                throw new SQLException(
                                                        "Unexpected test SQL", "HY000");
                                            });
                            return statement;
                        });
    }

    void column(String field, String type, int scale, int codepage) {
        columns.add(
                map(
                        "COLNAME",
                        field,
                        "TYPESCHEMA",
                        "SYSIBM",
                        "TYPENAME",
                        type,
                        "LENGTH",
                        100,
                        "SCALE",
                        scale,
                        "CODEPAGE",
                        codepage));
    }

    void lengths(String field, Object... lengthsAndCounts) {
        var rows = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < lengthsAndCounts.length; i += 2)
            rows.add(
                    map(
                            "LENGTH_VALUE",
                            lengthsAndCounts[i],
                            "BUCKET_COUNT",
                            lengthsAndCounts[i + 1]));
        data.put(field, rows);
    }

    void decimal(String field, Object count, Object overflow, Object min, Object max) {
        data.put(
                field,
                List.of(
                        map(
                                "RECORD_COUNT",
                                count,
                                "OVERFLOW_COUNT",
                                overflow,
                                "MIN_VALUE",
                                min,
                                "MAX_VALUE",
                                max)));
    }

    DataTypeValidationConfig config(String csv, DataTypeValidationConfig.Mode mode, Path baseline)
            throws Exception {
        Path file = Files.createTempFile(dir, "input-", ".csv");
        Files.writeString(file, HEADER + csv);
        return new DataTypeValidationConfig(
                true,
                mode,
                "FOS",
                file,
                dir.resolve("output"),
                true,
                "ITSVIEW",
                DataTypeValidationConfig.LengthUnit.OCTETS,
                baseline != null,
                baseline,
                12);
    }

    AppConfig app(DataTypeValidationConfig config) {
        return new AppConfig(
                        "jdbc:db2://unused/DB",
                        "user",
                        "secret",
                        Map.of(),
                        dir,
                        "P",
                        "F",
                        "\"MYCAT\".\"OBJECTS\"",
                        "\"MYCAT\".\"COLUMNS\"",
                        false,
                        ExcelExportConfig.disabled())
                .withValidation(config);
    }

    DataTypeValidationSummary run(DataTypeValidationConfig config) throws Exception {
        var summary = new DataTypeValidationSummary();
        try (var plan = DataTypeValidationPlan.prepare(config)) {
            new DataTypeValidationService()
                    .validateAndWriteReport(
                            connection,
                            app(config),
                            plan,
                            "local",
                            null,
                            true,
                            summary,
                            new SafeDiagnostics(app(config)));
        }
        return summary;
    }

    static Map<String, Object> map(Object... values) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2) map.put((String) values[i], values[i + 1]);
        return map;
    }

    static ResultSet rows(List<Map<String, Object>> data) throws Exception {
        ResultSet rows = mock(ResultSet.class);
        AtomicInteger index = new AtomicInteger(-1);
        when(rows.next()).thenAnswer(inv -> index.incrementAndGet() < data.size());
        when(rows.getString(anyString()))
                .thenAnswer(
                        inv -> {
                            Object value = data.get(index.get()).get(inv.getArgument(0));
                            return value == null ? null : value.toString();
                        });
        when(rows.getBigDecimal(anyString()))
                .thenAnswer(
                        inv -> {
                            Object value = data.get(index.get()).get(inv.getArgument(0));
                            return value == null ? null : new BigDecimal(value.toString());
                        });
        return rows;
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationLargeCsvTest.java =====
UTF8-BYTES: 1886
SHA256: f75786779d7bae033df0a5986659b366f9e8f9c010a82b74313e63181be04a1c
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;

/** Actual Excel row boundary; opt in with -Dvalidation.large-tests=true -DargLine=-Xmx2g. */
@EnabledIfSystemProperty(named = "validation.large-tests", matches = "true")
class DataTypeValidationLargeCsvTest {
    @TempDir Path dir;

    @Test
    void acceptsExactly1048575ItemsAndRejectsTheNextRecord() throws Exception {
        Path csv = dir.resolve("million.csv");
        try (var out = Files.newBufferedWriter(csv)) {
            out.write(DataTypeValidationFixture.HEADER);
            for (int i = 0; i < 1048575; i++) out.write("FOS.T,F" + i + ",VARCHAR(1),VARCHAR(2)\n");
        }
        var config =
                new DataTypeValidationConfig(
                        true,
                        DataTypeValidationConfig.Mode.BASELINE,
                        "FOS",
                        csv,
                        dir.resolve("output"),
                        false,
                        "ITSVIEW",
                        DataTypeValidationConfig.LengthUnit.OCTETS,
                        false,
                        null,
                        0);
        assertEquals(1048575, new DataTypeValidationCsvReader().read(config).size());
        Files.writeString(csv, "FOS.T,F1048575,VARCHAR(1),VARCHAR(2)\n", StandardOpenOption.APPEND);
        IOException error =
                assertThrows(IOException.class, () -> new DataTypeValidationCsvReader().read(config));
        assertTrue(error.getMessage().contains("single report sheet row limit"));
        assertFalse(Files.exists(config.output()));
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/datatypevalidation/DataTypeValidationServiceTest.java =====
UTF8-BYTES: 16407
SHA256: 2199e65a4c223eaf51dcabe0c17cab86851b82dca42c2260284da5623fed3c5b
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

class DataTypeValidationServiceTest {
    @TempDir Path dir;

    @Test
    void mixedColumnsShareOneCountAndMetadataAndUseConfiguredViewCatalog() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "BIGINT", 0, 0);
        f.column("B", "VARCHAR", 0, 1208);
        f.column("C", "DECIMAL", 0, 0);
        f.decimal("A", 5, 0, -99999, 99999);
        f.lengths("B", null, 1, 20, 4);
        f.decimal("C", 999, 1, -100000, 5);
        var config =
                f.config(
                        "fos.t,A,\"DECIMAL(5,0)\",\"DECIMAL(31,0)\"\n"
                            + "fos.t,B,VARCHAR(20),VARCHAR(50)\n"
                            + "fos.t,C,\"DECIMAL(5,0)\",\"DECIMAL(31,0)\"\n",
                        DataTypeValidationConfig.Mode.BASELINE,
                        null);
        var summary = f.run(config);
        assertEquals(1, summary.exitCode());
        assertEquals(
                List.of("A", "B", "C"),
                summary.results().stream().map(r -> r.item.field()).toList());
        assertTrue(
                summary.results().stream().allMatch(r -> r.records.equals(BigInteger.valueOf(5))));
        assertEquals(
                1,
                f.queries.stream().filter(q -> q.contains("COUNT_BIG(*) AS RECORD_COUNT")).count());
        assertEquals(
                1,
                f.queries.stream()
                        .filter(q -> q.startsWith("SELECT TYPE FROM \"MYCAT\".\"OBJECTS\""))
                        .count());
        assertEquals(1, f.queries.stream().filter(q -> q.startsWith("SELECT COLNAME")).count());
        assertTrue(
                f.queries.stream()
                        .filter(
                                q ->
                                        q.contains(" AS OVERFLOW_COUNT")
                                                || q.contains(" AS LENGTH_VALUE"))
                        .allMatch(q -> q.contains("\"ITSVIEW\".\"FOS_T\"")));
        verify(f.connection, never()).close();
        for (var statement : f.statements) {
            verify(statement).setQueryTimeout(12);
            verify(statement).close();
            verify(statement, never()).setMaxRows(anyInt());
        }
        verify(f.statements.get(2)).setBigDecimal(1, new BigDecimal("-99999"));
        try (var book = new XSSFWorkbook(summary.published().toFile())) {
            assertEquals(11, book.getSheetAt(0).getRow(0).getLastCellNum());
            assertTrue(book.isSheetHidden(1));
            assertNotNull(book.getSheetAt(0).getRow(0).getCell(7).getCellComment());
        }
    }

    @Test
    void characterFirstEstablishesCountWithoutStandaloneCountQuery() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("B", "VARCHAR", 0, 1208);
        f.column("A", "INTEGER", 0, 0);
        f.lengths("B", 20, 7);
        f.decimal("A", 999, 0, 1, 7);
        var result =
                f.run(
                        f.config(
                                "fos.t,B,VARCHAR(20),VARCHAR(50)\n"
                                    + "fos.t,A,\"DECIMAL(5,0)\",\"DECIMAL(31,0)\"\n",
                                DataTypeValidationConfig.Mode.BASELINE,
                                null));
        assertEquals(0, result.exitCode());
        assertEquals(BigInteger.valueOf(7), result.results().get(1).records());
        assertFalse(f.queries.stream().anyMatch(q -> q.contains(" AS RECORD_COUNT")));
    }

    @Test
    void objectCountChangeInvalidatesAllCompletedCandidates() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "VARCHAR", 0, 1208);
        f.column("B", "VARCHAR", 0, 1208);
        f.lengths("A", 10, 2);
        f.lengths("B", 10, 3);
        var result =
                f.run(
                        f.config(
                                "fos.t,A,VARCHAR(20),VARCHAR(30)\n"
                                    + "fos.t,B,VARCHAR(20),VARCHAR(30)\n",
                                DataTypeValidationConfig.Mode.BASELINE,
                                null));
        assertEquals(1, result.exitCode());
        assertTrue(
                result.results().stream()
                        .allMatch(
                                r ->
                                        r.range() == DataTypeValidationResult.RangeStatus.ERROR
                                                && r.records() == null
                                                && r.compare().isEmpty()));
        var next =
                f.config(
                        "fos.t,A,VARCHAR(20),VARCHAR(30)\n",
                        DataTypeValidationConfig.Mode.VALIDATE,
                        result.published());
        try (var plan = DataTypeValidationPlan.prepare(next)) {
            assertFalse(plan.baseline.items().values().iterator().next().aggregationComplete());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"VARCHAR", "DECIMAL"})
    void emptySharedCountThenNumericValuesInvalidatesWholeObject(String firstType)
            throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", firstType, 0, firstType.equals("VARCHAR") ? 1208 : 0);
        f.column("B", "DECIMAL", 0, 0);
        if (firstType.equals("VARCHAR")) f.lengths("A");
        else f.decimal("A", 0, 0, null, null);
        // The object acquires a row between field queries. The second SQL intentionally omits
        // COUNT.
        f.decimal("B", 1, 0, 1, 1);
        String a =
                firstType.equals("VARCHAR")
                        ? "VARCHAR(20),VARCHAR(30)"
                        : "\"DECIMAL(5,0)\",\"DECIMAL(6,0)\"";
        String csv = "fos.t,A," + a + "\nfos.t,B,\"DECIMAL(5,0)\",\"DECIMAL(6,0)\"\n";
        var result = f.run(f.config(csv, DataTypeValidationConfig.Mode.BASELINE, null));
        assertEquals(1, result.exitCode());
        assertTrue(
                result.results().stream()
                        .allMatch(
                                r ->
                                        r.range() == DataTypeValidationResult.RangeStatus.ERROR
                                                && r.records() == null
                                                && r.compare().isEmpty()
                                                && !r.singleLengthMatchHint()));
        assertEquals(
                firstType.equals("VARCHAR") ? 0 : 1,
                f.queries.stream().filter(q -> q.contains(" AS RECORD_COUNT")).count());
        try (var plan =
                DataTypeValidationPlan.prepare(
                        f.config(csv, DataTypeValidationConfig.Mode.VALIDATE, result.published()))) {
            assertEquals(0, plan.baseline.validItems());
        }
    }

    @Test
    void distinctNumericExtremaCannotBelongToOneSharedRow() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "VARCHAR", 0, 1208);
        f.column("B", "INTEGER", 0, 0);
        f.lengths("A", 10, 1);
        f.decimal("B", 2, 0, 1, 2);
        var result =
                f.run(
                        f.config(
                                "fos.t,A,VARCHAR(20),VARCHAR(30)\n"
                                    + "fos.t,B,\"DECIMAL(5,0)\",\"DECIMAL(6,0)\"\n",
                                DataTypeValidationConfig.Mode.BASELINE,
                                null));
        assertEquals(1, result.exitCode());
        assertTrue(
                result.results().stream()
                        .allMatch(
                                r ->
                                        r.range() == DataTypeValidationResult.RangeStatus.ERROR
                                                && r.records() == null));
        assertFalse(f.queries.stream().anyMatch(q -> q.contains(" AS RECORD_COUNT")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"SYSIBM   ", " SYSIBM"})
    void typeSchemaIgnoresCharPaddingButPreservesLeadingWhitespace(String schema) throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "VARCHAR", 0, 1208);
        f.lengths("A", 10, 2);
        f.columns.get(0).put("TYPESCHEMA", schema);
        var result =
                f.run(f.config(BaselineRoundTripTest.CSV, DataTypeValidationConfig.Mode.BASELINE, null));
        assertEquals(
                schema.startsWith(" ")
                        ? DataTypeValidationResult.RangeStatus.TYPE_MISMATCH
                        : DataTypeValidationResult.RangeStatus.PASS,
                result.results().get(0).range());
    }

    @Test
    void quotedUnrequestedColumnDoesNotCollideWithOrdinaryColumn() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "VARCHAR", 0, 1208);
        f.column(" A", "INTEGER", 0, 0);
        f.lengths("A", 10, 2);
        var result =
                f.run(f.config(BaselineRoundTripTest.CSV, DataTypeValidationConfig.Mode.BASELINE, null));
        assertEquals(0, result.exitCode());
        assertEquals(DataTypeValidationResult.RangeStatus.PASS, result.results().get(0).range());
    }

    @Test
    void quotedColumnIsNotMistakenForMissingOrdinaryColumn() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column(" A", "VARCHAR", 0, 1208);
        f.lengths("A", 10, 2);
        var result =
                f.run(f.config(BaselineRoundTripTest.CSV, DataTypeValidationConfig.Mode.BASELINE, null));
        assertEquals(
                DataTypeValidationResult.RangeStatus.COLUMN_NOT_FOUND, result.results().get(0).range());
        assertEquals(2, f.queries.size());
    }

    @Test
    void failedFirstAggregateDoesNotCacheZeroAndConnectionLossStopsRemainder() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        for (String name : List.of("A", "B", "C")) f.column(name, "BIGINT", 0, 0);
        f.failures.put("MIN(\"A\")", new SQLException("timeout secret", "57014"));
        f.decimal("B", 3, 0, null, null);
        f.failures.put("MIN(\"C\")", new SQLException("lost", "08006"));
        var result =
                f.run(
                        f.config(
                                "fos.t,A,\"DECIMAL(5,0)\",\"DECIMAL(31,0)\"\n"
                                    + "fos.t,B,\"DECIMAL(5,0)\",\"DECIMAL(31,0)\"\n"
                                    + "fos.t,C,\"DECIMAL(5,0)\",\"DECIMAL(31,0)\"\n"
                                    + "fos.u,A,\"DECIMAL(5,0)\",\"DECIMAL(31,0)\"\n",
                                DataTypeValidationConfig.Mode.BASELINE,
                                null));
        assertEquals(
                List.of(
                        DataTypeValidationResult.RangeStatus.ERROR,
                        DataTypeValidationResult.RangeStatus.PASS,
                        DataTypeValidationResult.RangeStatus.ERROR,
                        DataTypeValidationResult.RangeStatus.NOT_EXECUTED),
                result.results().stream().map(DataTypeValidationResult::range).toList());
        assertEquals(
                2,
                f.queries.stream().filter(q -> q.contains("COUNT_BIG(*) AS RECORD_COUNT")).count());
        assertFalse(result.results().get(0).error.contains("secret"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"VARCHAR", "DOUBLE", "DECIMAL_SCALE", "UDT"})
    void incompatibleActualNumericTypeDoesNotAggregate(String type) throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column(
                "A",
                type.equals("DECIMAL_SCALE") ? "DECIMAL" : type,
                type.equals("DECIMAL_SCALE") ? 2 : 0,
                type.equals("VARCHAR") ? 1208 : 0);
        var result =
                f.run(
                        f.config(
                                "fos.t,A,\"DECIMAL(5,0)\",\"DECIMAL(31,0)\"\n",
                                DataTypeValidationConfig.Mode.BASELINE,
                                null));
        assertEquals(DataTypeValidationResult.RangeStatus.TYPE_MISMATCH, result.results().get(0).range());
        assertEquals(2, f.queries.size());
    }

    @Test
    void bigCountsAndDecimal31BoundsStayExact() throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "BIGINT", 0, 0);
        f.decimal("A", "12345678901234567890", 0, -1, 1);
        var result =
                f.run(
                        f.config(
                                "fos.t,A,\"DECIMAL(31,0)\",\"DECIMAL(31,0)\"\n",
                                DataTypeValidationConfig.Mode.BASELINE,
                                null));
        verify(f.statements.get(2))
                .setBigDecimal(2, new BigDecimal("9999999999999999999999999999999"));
        try (var book = new XSSFWorkbook(result.published().toFile())) {
            assertEquals(
                    "12345678901234567890",
                    book.getSheetAt(0).getRow(1).getCell(4).getStringCellValue());
            assertEquals(
                    "12345678901234567890",
                    book.getSheet("_items").getRow(1).getCell(12).getStringCellValue());
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "missing-object",
                "missing-column",
                "not-a-view",
                "duplicate-column",
                "null-codepage",
                "binary",
                "udt",
                "catalog-denied"
            })
    void metadataFailuresNeverFallbackOrQueryUnsupportedObjects(String scenario) throws Exception {
        var f = new DataTypeValidationFixture(dir);
        f.column("A", "VARCHAR", 0, 1208);
        DataTypeValidationResult.RangeStatus expected;
        switch (scenario) {
            case "missing-object" -> {
                f.missingObject = true;
                expected = DataTypeValidationResult.RangeStatus.TABLE_NOT_FOUND;
            }
            case "missing-column" -> {
                f.columns.clear();
                expected = DataTypeValidationResult.RangeStatus.COLUMN_NOT_FOUND;
            }
            case "not-a-view" -> {
                f.objectType = "T";
                expected = DataTypeValidationResult.RangeStatus.ERROR;
            }
            case "duplicate-column" -> {
                f.column("A", "VARCHAR", 0, 1208);
                expected = DataTypeValidationResult.RangeStatus.ERROR;
            }
            case "null-codepage" -> {
                f.columns.get(0).put("CODEPAGE", null);
                expected = DataTypeValidationResult.RangeStatus.ERROR;
            }
            case "binary" -> {
                f.columns.get(0).put("CODEPAGE", 0);
                expected = DataTypeValidationResult.RangeStatus.TYPE_MISMATCH;
            }
            case "udt" -> {
                f.columns.get(0).put("TYPESCHEMA", "APP");
                expected = DataTypeValidationResult.RangeStatus.TYPE_MISMATCH;
            }
            default -> {
                f.failures.put("SELECT TYPE", new SQLException("denied secret", "42501"));
                expected = DataTypeValidationResult.RangeStatus.ERROR;
            }
        }
        var result =
                f.run(f.config(BaselineRoundTripTest.CSV, DataTypeValidationConfig.Mode.BASELINE, null));
        assertEquals(expected, result.results().get(0).range());
        assertNotNull(result.published());
        assertFalse(
                f.queries.stream().anyMatch(q -> q.contains("SYSCAT") || q.contains("GROUP BY")));
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/datatypevalidation/Db2GeneratedDataTest.java =====
UTF8-BYTES: 22759
SHA256: e79c4fc6ef6802beeb99158cc60fee990da79b92e87b0257c7b6b9acd0085c2e
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import static org.junit.jupiter.api.Assertions.*;

import com.example.db2toolkit.config.*;
import com.example.db2toolkit.excel.ExcelExportConfig;
import com.example.db2toolkit.jdbc.SafeDiagnostics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.math.BigInteger;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

/** Explicitly opt-in mutation test, restricted to the user-authorized local TESTDB. */
@EnabledIfEnvironmentVariable(named = "DB2_VALIDATION_FIXTURES", matches = "true")
class Db2GeneratedDataTest {
    private Path directory;
    private String schema, views, prefix;

    @Test
    void createInsertAlterAndValidateControlledDataThroughRealViews() throws Exception {
        AppConfig source =
                new ConfigLoader()
                        .load(
                                Path.of("config/combined/application.properties"),
                                System::getenv);
        assertEquals(
                "jdbc:db2://localhost:25000/TESTDB",
                source.url(),
                "Fixture writes are restricted to the authorized local TESTDB");
        assertEquals("db2admin", source.username());
        String suffix =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 10)
                        .toUpperCase(Locale.ROOT);
        schema = "DB2ADMIN";
        views = "DB2ADMIN";
        directory = Path.of("verification/generated-data/run-" + suffix).toAbsolutePath();
        Files.createDirectories(directory);
        var errors = new SafeDiagnostics(source);
        try (var connection =
                DriverManager.getConnection(source.url(), source.username(), source.password())) {
            Db2TestDataMain.main(
                    new String[] {
                        "prepare",
                        "config/combined/application.properties",
                        directory.toString()
                    });
            var fixtureProperties = new Properties();
            try (var reader = Files.newBufferedReader(directory.resolve("fixture.properties"))) {
                fixtureProperties.load(reader);
            }
            prefix = fixtureProperties.getProperty("prefix");

            Path csv = directory.resolve("validation.csv");
            Files.writeString(
                    csv,
                    DataTypeValidationFixture.HEADER
                            + row("CASES", "N", "DECIMAL(5,0)", "DECIMAL(20,0)")
                            + row("CASES", "S", "VARCHAR(10)", "VARCHAR(50)")
                            + row("CASES", "C", "CLOB(100)", "CLOB(200)")
                            + row("CASES", "MIX", "VARCHAR(10)", "VARCHAR(50)")
                            + row("CASES", "BIG_N", "DECIMAL(31,0)", "DECIMAL(31,0)")
                            + row("NULL_ROWS", "V", "VARCHAR(20)", "VARCHAR(30)")
                            + row("NULL_ROWS", "N", "DECIMAL(5,0)", "DECIMAL(20,0)")
                            + row("EMPTY_ROWS", "V", "VARCHAR(20)", "VARCHAR(30)")
                            + row("EMPTY_ROWS", "N", "DECIMAL(5,0)", "DECIMAL(20,0)"));
            var baselineConfig = config(csv, false, null, "baseline");
            var baseline = run(connection, app(source, baselineConfig, false), errors);
            assertEquals(0, baseline.exitCode(), diagnostic(baseline));
            assertEquals(9, baseline.results().size());
            check(baseline, "CASES", "N", "PASS", "N/A", 3, 0, false);
            check(baseline, "NULL_ROWS", "V", "PASS", "N/A", 3, 0, false);
            check(baseline, "EMPTY_ROWS", "V", "PASS", "N/A", 0, 0, false);

            Path edgeCsv = edgeCsv();
            var edgeBase =
                    run(
                            connection,
                            app(source, config(edgeCsv, false, null, "edge-baseline"), false),
                            errors);
            assertEquals(0, edgeBase.exitCode(), diagnostic(edgeBase));
            assertEquals(12, edgeBase.results().size());

            Db2TestDataMain.main(
                    new String[] {
                        "change",
                        "config/combined/application.properties",
                        directory.toString()
                    });

            var currentConfig = config(csv, true, baseline.published(), "current");
            var current = run(connection, app(source, currentConfig, true), errors);
            assertEquals(
                    1,
                    current.exitCode(),
                    "Intentional overflows/differences must return 1: " + diagnostic(current));
            check(current, "CASES", "N", "FAIL", "N/A", 4, 1, false);
            check(current, "CASES", "S", "PASS", "PASS", 4, 0, true);
            check(current, "CASES", "C", "FAIL", "DIFFERENT", 4, 1, false);
            check(current, "CASES", "MIX", "FAIL", "DIFFERENT", 4, 1, false);
            check(current, "CASES", "BIG_N", "PASS", "N/A", 4, 0, false);
            check(current, "NULL_ROWS", "V", "PASS", "PASS", 5, 0, false);
            check(current, "EMPTY_ROWS", "V", "PASS", "PASS", 0, 0, false);

            var edgeCurrent =
                    run(
                            connection,
                            app(
                                    source,
                                    config(edgeCsv, true, edgeBase.published(), "edge-current"),
                                    true),
                            errors);
            check(edgeCurrent, "EDGE_VALUES", "MULTI_SAME", "PASS", "PASS", 4, 0, false);
            check(edgeCurrent, "EDGE_VALUES", "MULTI_DIFF", "PASS", "DIFFERENT", 4, 0, false);
            check(edgeCurrent, "EDGE_VALUES", "ZERO_ONLY", "PASS", "PASS", 4, 0, true);
            check(edgeCurrent, "EDGE_VALUES", "NULL_MIX", "PASS", "PASS", 4, 0, false);
            check(edgeCurrent, "EDGE_VALUES", "BOUNDARY_V", "FAIL", "DIFFERENT", 4, 1, false);
            check(edgeCurrent, "EDGE_VALUES", "BOUNDARY_C", "FAIL", "DIFFERENT", 4, 1, false);
            check(edgeCurrent, "EDGE_VALUES", "NEG_N", "FAIL", "N/A", 4, 1, false);
            check(edgeCurrent, "EDGE_VALUES", "AS_CLOB", "PASS", "PASS", 4, 0, true);
            check(edgeCurrent, "EDGE_VALUES", "OCTETS_V", "FAIL", "DIFFERENT", 4, 1, false);
            check(edgeCurrent, "EDGE_VALUES", "UNICODE_V", "PASS", "DIFFERENT", 4, 0, false);
            check(edgeCurrent, "EMPTY_TO_NULL", "V", "PASS", "DIFFERENT", 1, 0, false);
            check(edgeCurrent, "NULL_TO_EMPTY", "V", "PASS", "DIFFERENT", 0, 0, false);
            assertEquals(1, edgeCurrent.exitCode());
            assertEquals(0, edgeCurrent.queryFailures());
            // Check the lossless persisted differences and actual byte/code-unit buckets, after
            // staging has closed.
            try (var book =
                    new org.apache.poi.xssf.usermodel.XSSFWorkbook(
                            edgeCurrent.published().toFile())) {
                var main = book.getSheet("Validation");
                assertEquals("(1)", main.getRow(2).getCell(9).getStringCellValue());
                assertEquals("(4)", main.getRow(2).getCell(10).getStringCellValue());
                assertTrue(
                        main.getRow(9)
                                .getCell(6)
                                .getStringCellValue()
                                .contains("DISTINCT LENGTH = (1, 3, 4, 5)"));
                assertTrue(
                        main.getRow(10)
                                .getCell(6)
                                .getStringCellValue()
                                .contains("DISTINCT LENGTH = (1, 2)"));
                assertEquals("(NULL)", main.getRow(11).getCell(10).getStringCellValue());
                assertEquals("(NULL)", main.getRow(12).getCell(9).getStringCellValue());
                assertTrue(
                        book.getSheet("_items")
                                .getRow(8)
                                .getCell(9)
                                .getStringCellValue()
                                .startsWith("SYSIBM.CLOB"));
            }
            writeConfig(
                    "application-edge-validate.properties",
                    edgeCsv,
                    edgeBase.published(),
                    "edge-cli",
                    false);

            Path errorCsv = directory.resolve("validation-errors.csv");
            Files.writeString(
                    errorCsv,
                    DataTypeValidationFixture.HEADER
                            + row("TYPE_CASES", "NUM_AS_TEXT", "DECIMAL(5,0)", "DECIMAL(20,0)")
                            + row("TYPE_CASES", "TEXT_AS_NUMBER", "VARCHAR(20)", "VARCHAR(50)")
                            + row("TYPE_CASES", "FRACTION", "DECIMAL(5,0)", "DECIMAL(20,0)")
                            + row("TYPE_CASES", "BIT_TEXT", "VARCHAR(20)", "VARCHAR(50)")
                            + row("TYPE_CASES", "MISSING_COLUMN", "VARCHAR(20)", "VARCHAR(50)")
                            + row("MISSING_TABLE", "V", "VARCHAR(20)", "VARCHAR(50)")
                            + row("BAD_QUERY", "N", "DECIMAL(5,0)", "DECIMAL(20,0)")
                            + row("CASES", "S", "VARCHAR(10)", "VARCHAR(50)"));
            var errorConfig = config(errorCsv, true, null, "errors");
            var errorResults = run(connection, app(source, errorConfig, true), errors);
            assertEquals(1, errorResults.exitCode(), diagnostic(errorResults));
            assertEquals(
                    List.of(
                            "TYPE MISMATCH",
                            "TYPE MISMATCH",
                            "TYPE MISMATCH",
                            "TYPE MISMATCH",
                            "COLUMN NOT FOUND",
                            "TABLE NOT FOUND",
                            "ERROR",
                            "PASS"),
                    errorResults.results().stream().map(r -> r.range().toString()).toList(),
                    diagnostic(errorResults));
            assertEquals("N/A", errorResults.results().get(7).compare());
            assertFalse(connection.isClosed());

            writeConfig(
                    "application-validate.properties",
                    csv,
                    baseline.published(),
                    "current-cli",
                    false);
            writeConfig("application-errors.properties", errorCsv, null, "errors-cli", false);
            writeConfig("application-all.properties", csv, baseline.published(), "all-cli", true);
            Files.writeString(
                    directory.resolve("tables.txt"),
                    schema
                            + "."
                            + prefix
                            + "CASES\n"
                            + schema
                            + "."
                            + prefix
                            + "NULL_ROWS\n"
                            + schema
                            + "."
                            + prefix
                            + "EMPTY_ROWS\n");
            Files.writeString(directory.resolve("empty.txt"), "");
            Files.writeString(
                    directory.resolve("excel.yaml"),
                    "excel-export:\n"
                        + "  enabled: true\n"
                        + "  output-directory: ./ordinary-excel\n"
                        + "  simple:\n"
                        + "    tables:\n"
                        + "      - "
                            + views
                            + "."
                            + schema
                            + "_"
                            + prefix
                            + "CASES\n");
            writeResults(baseline, current, errorResults, edgeBase, edgeCurrent);
            Files.writeString(
                    Path.of("verification/generated-data/latest-run.txt"), directory.toString());
        } catch (Exception e) {
            throw new AssertionError(errors.describe(e));
        }
    }

    private Path edgeCsv() throws Exception {
        String[][] specs = {
            {"EDGE_VALUES", "MULTI_SAME", "VARCHAR(10)", "VARCHAR(20)", ""},
            {"EDGE_VALUES", "MULTI_DIFF", "VARCHAR(10)", "VARCHAR(20)", ""},
            {"EDGE_VALUES", "ZERO_ONLY", "VARCHAR(10)", "VARCHAR(20)", ""},
            {"EDGE_VALUES", "NULL_MIX", "VARCHAR(10)", "VARCHAR(20)", ""},
            {"EDGE_VALUES", "BOUNDARY_V", "VARCHAR(10)", "VARCHAR(20)", ""},
            {"EDGE_VALUES", "BOUNDARY_C", "CLOB(100)", "CLOB(200)", ""},
            {"EDGE_VALUES", "NEG_N", "DECIMAL(5,0)", "DECIMAL(20,0)", ""},
            {"EDGE_VALUES", "AS_CLOB", "VARCHAR(10)", "CLOB(100)", ""},
            {"EDGE_VALUES", "OCTETS_V", "VARCHAR(4)", "VARCHAR(20)", "OCTETS"},
            {"EDGE_VALUES", "UNICODE_V", "VARCHAR(4)", "VARCHAR(20)", "CODEUNITS32"},
            {"EMPTY_TO_NULL", "V", "VARCHAR(10)", "VARCHAR(20)", ""},
            {"NULL_TO_EMPTY", "V", "VARCHAR(10)", "VARCHAR(20)", ""}
        };
        var text = new StringBuilder(DataTypeValidationFixture.HEADER.stripTrailing() + ",length_unit\n");
        for (var spec : specs)
            text.append(row(spec[0], spec[1], spec[2], spec[3]).stripTrailing())
                    .append(',')
                    .append(spec[4])
                    .append('\n');
        Path csv = directory.resolve("validation-edges.csv");
        Files.writeString(csv, text);
        return csv;
    }

    private DataTypeValidationConfig config(Path csv, boolean useViews, Path baseline, String output) {
        return new DataTypeValidationConfig(
                true,
                baseline == null && !useViews
                        ? DataTypeValidationConfig.Mode.BASELINE
                        : DataTypeValidationConfig.Mode.VALIDATE,
                "GENERATED_TEST",
                csv,
                directory.resolve(output),
                useViews,
                views,
                DataTypeValidationConfig.LengthUnit.OCTETS,
                baseline != null,
                baseline,
                30);
    }

    private AppConfig app(AppConfig source, DataTypeValidationConfig config, boolean custom) {
        return new AppConfig(
                        source.url(),
                        source.username(),
                        source.password(),
                        Map.of(),
                        directory,
                        source.procedures(),
                        source.functions(),
                        custom ? catalog("OBJECTS") : "\"SYSCAT\".\"TABLES\"",
                        custom ? catalog("COLUMNS") : "\"SYSCAT\".\"COLUMNS\"",
                        false,
                        ExcelExportConfig.disabled())
                .withValidation(config);
    }

    private DataTypeValidationSummary run(Connection connection, AppConfig app, SafeDiagnostics errors)
            throws Exception {
        var result = new DataTypeValidationSummary();
        try (var plan = DataTypeValidationPlan.prepare(app.validation())) {
            new DataTypeValidationService()
                    .validateAndWriteReport(
                            connection, app, plan, "generated", null, true, result, errors);
        }
        return result;
    }

    private String catalog(String name) {
        return "\"" + views + "\".\"" + prefix + "CAT_" + name + "\"";
    }

    private String row(String table, String field, String original, String target) {
        return schema + "." + prefix + table + "," + field + ",\"" + original + "\",\"" + target
                + "\"\n";
    }

    private static String diagnostic(DataTypeValidationSummary summary) {
        return summary.results().stream()
                .map(
                        r ->
                                r.item.logical().name()
                                        + "."
                                        + r.item.field()
                                        + " "
                                        + r.range
                                        + " "
                                        + r.compare
                                        + " "
                                        + r.error)
                .toList()
                .toString();
    }

    private void check(
            DataTypeValidationSummary summary,
            String table,
            String field,
            String range,
            String compare,
            long count,
            long overflow,
            boolean hint) {
        var r =
                summary.results().stream()
                        .filter(
                                v ->
                                        v.item().logical().name().equals(prefix + table)
                                                && v.item.field().equals(field))
                        .findFirst()
                        .orElseThrow();
        assertAll(
                table + "." + field,
                () -> assertEquals(range, r.range().toString()),
                () -> assertEquals(compare, r.compare()),
                () -> assertEquals(BigInteger.valueOf(count), r.records()),
                () -> assertEquals(BigInteger.valueOf(overflow), r.overflow()),
                () -> assertEquals(hint, r.singleLengthMatchHint()));
    }

    private void writeConfig(String name, Path csv, Path baseline, String output, boolean all)
            throws Exception {
        Files.writeString(
                directory.resolve(name),
                "db.url=jdbc:db2://localhost:25000/TESTDB\n"
                    + "db.username=db2admin\n"
                    + "db.password-env=DB2_PASSWORD\n"
                    + "export.enabled="
                        + all
                        + "\n"
                        + (all
                                ? "export.procedure-list=empty.txt\n"
                                      + "export.function-list=empty.txt\n"
                                      + "export.table-list=tables.txt\n"
                                      + "export.output-directory=ddl\n"
                                      + "excel-export.config=excel.yaml\n"
                                : "")
                        + "catalog.table.view="
                        + views
                        + "."
                        + prefix
                        + "CAT_OBJECTS\ncatalog.column.view="
                        + views
                        + "."
                        + prefix
                        + "CAT_COLUMNS\n"
                        + "validation.enabled=true\n"
                        + "validation.mode=VALIDATE\n"
                        + "validation.database-id=GENERATED_TEST\n"
                        + "validation.file="
                        + csv.getFileName()
                        + "\nvalidation.output-directory="
                        + output
                        + "\nvalidation.itsview.enabled=true\nvalidation.itsview.schema="
                        + views
                        + "\n"
                        + "validation.length-unit=OCTETS\n"
                        + "validation.query-timeout-seconds=30\n"
                        + "validation.baseline.compare.enabled="
                        + (baseline != null)
                        + (baseline == null
                                ? "\n"
                                : "\nvalidation.baseline.file="
                                        + directory
                                                .relativize(baseline)
                                                .toString()
                                                .replace('\\', '/')
                                        + "\n"));
    }

    private void writeResults(DataTypeValidationSummary... summaries) throws Exception {
        var text =
                new StringBuilder(
                        "# Generated DB2 test data results\n\nSchema: `"
                                + schema
                                + "`; views/catalog schema: `"
                                + views
                                + "`.\n\n"
                                + "Objects and data are retained for inspection. `executed.sql`"
                                + " records the setup and changes; `cleanup.sql` removes only these"
                                + " fixtures when you choose to run it.\n\n"
                                + "Baseline is collected before the ALTER/INSERT changes. Later"
                                + " expected FAIL/DIFFERENT/error statuses are successful test"
                                + " assertions, not unexpected test failures.\n\n");
        for (var summary : summaries) {
            text.append("Report: `")
                    .append(directory.relativize(summary.published()))
                    .append("`; expected exit code: ")
                    .append(summary.exitCode())
                    .append("\n\n");
            text.append(
                    "| Object / field | Range | Compare | Records | Overflow | Hint |\n"
                        + "| --- | --- | --- | --- | --- | --- |\n");
            for (var r : summary.results())
                text.append("| ")
                        .append(r.item.logical().name())
                        .append('.')
                        .append(r.item.field())
                        .append(" | ")
                        .append(r.range)
                        .append(" | ")
                        .append(r.compare)
                        .append(" | ")
                        .append(r.records == null ? "" : r.records)
                        .append(" | ")
                        .append(r.overflow == null ? "" : r.overflow)
                        .append(" | ")
                        .append(r.singleLengthMatchHint)
                        .append(" |\n");
            text.append('\n');
        }
        Files.writeString(directory.resolve("RESULTS.md"), text);
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/datatypevalidation/Db2ScaleDataMain.java =====
UTF8-BYTES: 7470
SHA256: 63aa60ad560635e79666a2a00cbe0c739a9cdc1efe762300afe9e13bf279022b
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import com.example.db2toolkit.config.ConfigLoader;
import com.example.db2toolkit.jdbc.SafeDiagnostics;

import java.nio.file.*;
import java.sql.*;
import java.util.*;

/** Explicit standalone setup for physical conversion and real large length distributions. */
public final class Db2ScaleDataMain {
    public static final int LENGTHS = 6000;
    private String prefix;
    private Path directory;
    private final List<String> executed = new ArrayList<>(), cleanup = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        if (args.length != 3 || !Set.of("prepare", "change").contains(args[0]))
            throw new IllegalArgumentException(
                    "Usage: Db2ScaleDataMain prepare|change <config> <new-run-directory>");
        new Db2ScaleDataMain().run(args[0], Path.of(args[1]), Path.of(args[2]).toAbsolutePath());
    }

    private void run(String phase, Path config, Path directory) throws Exception {
        this.directory = directory;
        var app = new ConfigLoader().load(config, System::getenv);
        if (!app.url().equals("jdbc:db2://localhost:25000/TESTDB")
                || !app.username().equalsIgnoreCase("db2admin"))
            throw new IllegalArgumentException(
                    "Fixture writes restricted to db2admin on local TESTDB");
        Files.createDirectories(directory);
        Path manifest = directory.resolve("fixture.properties");
        if (phase.equals("prepare")) {
            prefix =
                    "DVSCALE_"
                            + UUID.randomUUID()
                                    .toString()
                                    .replace("-", "")
                                    .substring(0, 10)
                                    .toUpperCase(Locale.ROOT)
                            + "_";
            Files.writeString(manifest, "prefix=" + prefix + "\n", StandardOpenOption.CREATE_NEW);
        } else {
            var properties = new Properties();
            try (var reader = Files.newBufferedReader(manifest)) {
                properties.load(reader);
            }
            prefix = properties.getProperty("prefix", "");
            if (!prefix.matches("DVSCALE_[A-F0-9]{10}_")
                    || !Files.exists(directory.resolve("prepare.done")))
                throw new IllegalArgumentException("Invalid/incomplete fixture");
            Files.writeString(
                    directory.resolve("change.started"), "started", StandardOpenOption.CREATE_NEW);
        }
        try (var c = DriverManager.getConnection(app.url(), app.username(), app.password())) {
            if (phase.equals("prepare")) prepare(c);
            else change(c);
            Files.writeString(
                    directory.resolve(phase + ".done"), "complete", StandardOpenOption.CREATE_NEW);
            System.out.println(
                    "Scale fixture " + phase + " complete: " + prefix + " at " + directory);
        } catch (SQLException e) {
            throw new IllegalStateException(new SafeDiagnostics(app).describe(e));
        } finally {
            Files.writeString(
                    directory.resolve("executed.sql"),
                    String.join(";\n", executed) + ";\n",
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
            if (!cleanup.isEmpty()) {
                Collections.reverse(cleanup);
                Files.writeString(
                        directory.resolve("cleanup.sql"), String.join(";\n", cleanup) + ";\n");
            }
        }
    }

    private void prepare(Connection c) throws Exception {
        create(c, "PHYSICAL", "(ID INTEGER NOT NULL PRIMARY KEY, V VARCHAR(20))");
        sql(c, "INSERT INTO " + table("PHYSICAL") + " VALUES (1,'SAME'),(2,'SAME'),(3,'SAME')");
        create(c, "LENGTHS", "(ID INTEGER NOT NULL PRIMARY KEY, V CLOB(20000))");
        c.setAutoCommit(false);
        try (var s =
                c.prepareStatement("INSERT INTO " + table("LENGTHS") + " (ID,V) VALUES (?,?)")) {
            s.setQueryTimeout(60);
            for (int i = 0; i < LENGTHS; i++) {
                s.setInt(1, i);
                s.setString(2, "x".repeat(2 * i));
                s.addBatch();
                if ((i + 1) % 100 == 0) {
                    s.executeBatch();
                    s.clearBatch();
                }
            }
            c.commit();
            executed.add(
                    "-- Prepared batch inserted "
                            + LENGTHS
                            + " rows: ID=0..5999, V='x' repeated 2*ID times");
        } catch (SQLException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(true);
        }
    }

    private void change(Connection c) throws Exception {
        String migration;
        try {
            sql(c, "ALTER TABLE " + table("PHYSICAL") + " ALTER COLUMN V SET DATA TYPE CLOB(100)");
            sql(c, "CALL SYSPROC.ADMIN_CMD('REORG TABLE " + table("PHYSICAL") + "')");
            migration = "Direct ALTER COLUMN VARCHAR to CLOB succeeded.";
        } catch (SQLException e) {
            if (e.getErrorCode() != -190) throw e;
            migration =
                    "Direct ALTER rejected by DB2 (SQLCODE=-190). Applied physical"
                        + " add/copy/drop/rename migration instead.";
            executed.add(
                    "-- Direct ALTER rejected: SQLCODE=-190; physical column migration follows");
            sql(c, "ALTER TABLE " + table("PHYSICAL") + " ADD COLUMN V_CLOB CLOB(100)");
            sql(c, "UPDATE " + table("PHYSICAL") + " SET V_CLOB=CAST(V AS CLOB(100))");
            try (var s = c.createStatement();
                    var rows = s.executeQuery("SELECT V,V_CLOB FROM " + table("PHYSICAL"))) {
                int n = 0;
                while (rows.next()) {
                    if (!Objects.equals(rows.getString(1), rows.getString(2)))
                        throw new SQLException("Migration copy mismatch");
                    n++;
                }
                if (n != 3) throw new SQLException("Unexpected migration row count");
            }
            sql(c, "ALTER TABLE " + table("PHYSICAL") + " DROP COLUMN V");
            sql(c, "CALL SYSPROC.ADMIN_CMD('REORG TABLE " + table("PHYSICAL") + "')");
            sql(c, "ALTER TABLE " + table("PHYSICAL") + " RENAME COLUMN V_CLOB TO V");
        }
        Files.writeString(directory.resolve("migration.txt"), migration + "\n");
        sql(c, "INSERT INTO " + table("PHYSICAL") + " VALUES (4,CAST('DIFF' AS CLOB(100)))");
        sql(c, "UPDATE " + table("LENGTHS") + " SET V=CAST(V AS VARCHAR(12000)) || 'x'");
    }

    private void create(Connection c, String name, String definition) throws SQLException {
        sql(c, "CREATE TABLE " + table(name) + " " + definition);
        cleanup.add("DROP TABLE " + table(name));
    }

    private void sql(Connection c, String sql) throws SQLException {
        try (var s = c.createStatement()) {
            s.setQueryTimeout(60);
            s.execute(sql);
            executed.add(sql);
        }
    }

    private String table(String name) {
        return "\"DB2ADMIN\".\"" + prefix + name + "\"";
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/datatypevalidation/Db2ScaleValidationTest.java =====
UTF8-BYTES: 15186
SHA256: 6bf0fd4cec65f232ff5f2654d7fb3ed2eaf87d4c35b96073c7dd211f8ae337e6
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import static org.junit.jupiter.api.Assertions.*;

import com.example.db2toolkit.config.*;
import com.example.db2toolkit.excel.ExcelExportConfig;
import com.example.db2toolkit.jdbc.SafeDiagnostics;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.math.BigInteger;
import java.nio.file.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;

/** Opt-in: writes only independent local fixture objects through Db2ScaleDataMain. */
@EnabledIfEnvironmentVariable(named = "DB2_VALIDATION_SCALE", matches = "true")
class Db2ScaleValidationTest {
    private Path directory, csv;
    private String prefix;

    @Test
    void physicalClobMigrationAndRealLargeDistributionsRoundTripAcrossSheets() throws Exception {
        var source =
                new ConfigLoader()
                        .load(
                                Path.of("config/combined/application.properties"),
                                System::getenv);
        directory =
                Path.of(
                                "verification/scale-data/run-"
                                        + UUID.randomUUID().toString().substring(0, 8))
                        .toAbsolutePath();
        Db2ScaleDataMain.main(
                new String[] {
                    "prepare", "config/combined/application.properties", directory.toString()
                });
        var properties = new Properties();
        try (var reader = Files.newBufferedReader(directory.resolve("fixture.properties"))) {
            properties.load(reader);
        }
        prefix = properties.getProperty("prefix");
        csv = directory.resolve("validation.csv");
        Files.writeString(
                csv,
                DataTypeValidationFixture.HEADER
                        + "DB2ADMIN."
                        + prefix
                        + "PHYSICAL,V,VARCHAR(20),CLOB(100)\n"
                        + "DB2ADMIN."
                        + prefix
                        + "LENGTHS,V,CLOB(20),CLOB(20000)\n");
        var errors = new SafeDiagnostics(source);
        try (var c =
                DriverManager.getConnection(source.url(), source.username(), source.password())) {
            assertPhysicalType(c, "VARCHAR");
            var baseline = run(c, source, null, "baseline");
            assertEquals(1, baseline.summary.exitCode());
            assertEquals(BigInteger.valueOf(5989), baseline.summary.results().get(1).overflow());
            Db2ScaleDataMain.main(
                    new String[] {
                        "change",
                        "config/combined/application.properties",
                        directory.toString()
                    });
            assertPhysicalType(c, "CLOB");
            try (var s = c.createStatement();
                    var rows =
                            s.executeQuery(
                                    "SELECT ID,V FROM DB2ADMIN."
                                            + prefix
                                            + "PHYSICAL ORDER BY ID")) {
                for (int i = 1; i <= 4; i++) {
                    assertTrue(rows.next());
                    assertEquals(i, rows.getInt(1));
                    assertEquals(i == 4 ? "DIFF" : "SAME", rows.getString(2));
                }
                assertFalse(rows.next());
            }
            var current = run(c, source, baseline.continuation, "current");
            assertEquals(1, current.summary.exitCode());
            assertEquals(0, current.summary.queryFailures());
            var physical = current.summary.results().get(0);
            assertEquals(DataTypeValidationResult.RangeStatus.PASS, physical.range());
            assertEquals("PASS", physical.compare());
            assertEquals(BigInteger.valueOf(4), physical.records());
            assertTrue(physical.singleLengthMatchHint());
            assertTrue(physical.actualType.startsWith("SYSIBM.CLOB"));
            var large = current.summary.results().get(1);
            assertEquals(DataTypeValidationResult.RangeStatus.FAIL, large.range());
            assertEquals("DIFFERENT", large.compare());
            assertEquals(BigInteger.valueOf(6000), large.records());
            assertEquals(BigInteger.valueOf(5990), large.overflow());
            verifyWorkbook(current.summary.published(), false);
            verifyWorkbook(current.continuation, true);
            try (var plan = DataTypeValidationPlan.prepare(config(baseline.continuation, "readback"))) {
                var buckets =
                        plan.baseline.items().values().stream()
                                .filter(v -> v.item().logical().name().endsWith("LENGTHS"))
                                .findFirst()
                                .orElseThrow()
                                .buckets();
                assertEquals(6000, buckets.size());
                try (var cursor = buckets.cursor()) {
                    for (int i = 0; i < 6000; i++) {
                        var b = cursor.next();
                        assertNotNull(b);
                        assertEquals(2L * i, b.length());
                        assertEquals(BigInteger.ONE, b.count());
                    }
                    assertNull(cursor.next());
                }
            }
            Path references = directory.resolve("baseline-reference.xlsx");
            Files.copy(baseline.continuation, references);
            writeConfig("application-baseline.properties", null, "baseline-cli");
            writeConfig("application-current.properties", references, "current-cli");
            Files.writeString(
                    directory.resolve("RESULTS.md"),
                    "# Physical migration and DB2 scale test\n\n"
                            + Files.readString(directory.resolve("migration.txt"))
                            + "\n"
                            + "SYSCAT confirms a physical base-table column changed from VARCHAR to"
                            + " CLOB. Original rows remain intact; singleton {4} passes with 3"
                            + " baseline rows and 4 current rows.\n\n"
                            + "LENGTHS has 6,000 actual DB2 rows. Baseline lengths are even"
                            + " 0..11998; current lengths are odd 1..11999. All 12,000"
                            + " bidirectional differences are checked individually.\n\n"
                            + "Default service report: `"
                            + directory.relativize(current.summary.published())
                            + "`. Its previews are truncated explicitly, while hidden data is"
                            + " complete.\n\n"
                            + "Continuation report: `"
                            + current.continuation.getFileName()
                            + "`. The same real query results are written with a test-only"
                            + " 3,000-row sheet limit: 3 length sheets and 4 difference sheets."
                            + " Production still uses 1,048,575 data rows per hidden sheet.\n\n"
                            + "The baseline continuation workbook is read by the production"
                            + " preflight and used for the current real DB2 comparison. This"
                            + " verifies continuation with real data, not 1,048,576 distinct"
                            + " lengths in the database.\n\n"
                            + "Baseline Range FAIL and current exit 1 are intentional because the"
                            + " original CLOB limit is 20. Objects remain available; cleanup.sql is"
                            + " optional.\n");
            Files.writeString(
                    Path.of("verification/scale-data/latest-run.txt"), directory.toString());
        } catch (SQLException e) {
            throw new AssertionError(errors.describe(e));
        }
    }

    private void assertPhysicalType(Connection c, String type) throws Exception {
        try (var s =
                c.prepareStatement(
                        "SELECT T.TYPE,C.TYPENAME FROM SYSCAT.TABLES T JOIN SYSCAT.COLUMNS C ON"
                            + " T.TABSCHEMA=C.TABSCHEMA AND T.TABNAME=C.TABNAME WHERE"
                            + " T.TABSCHEMA='DB2ADMIN' AND T.TABNAME=? AND C.COLNAME='V'")) {
            s.setString(1, prefix + "PHYSICAL");
            try (var rows = s.executeQuery()) {
                assertTrue(rows.next());
                assertEquals("T", rows.getString(1).strip());
                assertEquals(type, rows.getString(2).strip());
                assertFalse(rows.next());
            }
        }
    }

    private record Run(DataTypeValidationSummary summary, Path continuation) {}

    private Run run(Connection c, AppConfig source, Path baseline, String label) throws Exception {
        var config = config(baseline, label);
        var app =
                new AppConfig(
                                source.url(),
                                source.username(),
                                source.password(),
                                Map.of(),
                                directory,
                                source.procedures(),
                                source.functions(),
                                "\"SYSCAT\".\"TABLES\"",
                                "\"SYSCAT\".\"COLUMNS\"",
                                false,
                                ExcelExportConfig.disabled())
                        .withValidation(config);
        var summary = new DataTypeValidationSummary();
        Path continuation = directory.resolve(label + "-continuation.xlsx");
        try (var plan = DataTypeValidationPlan.prepare(config)) {
            new DataTypeValidationService()
                    .validateAndWriteReport(
                            c, app, plan, "scale", null, true, summary, new SafeDiagnostics(app));
            assertNotNull(summary.published());
            assertEquals(0, summary.queryFailures());
            assertFalse(summary.fatal());
            assertTrue(summary.results().stream().allMatch(DataTypeValidationResult::aggregationComplete));
            try (var writer = new DataTypeValidationWorkbookWriter(3000)) {
                writer.write(
                        continuation,
                        config,
                        "scale",
                        summary.results(),
                        Instant.now().minusSeconds(1),
                        Instant.now());
            }
        }
        return new Run(summary, continuation);
    }

    private DataTypeValidationConfig config(Path baseline, String output) {
        return new DataTypeValidationConfig(
                true,
                baseline == null ? DataTypeValidationConfig.Mode.BASELINE : DataTypeValidationConfig.Mode.VALIDATE,
                "SCALE_TEST",
                csv,
                directory.resolve(output),
                false,
                "ITSVIEW",
                DataTypeValidationConfig.LengthUnit.OCTETS,
                baseline != null,
                baseline,
                60);
    }

    private void verifyWorkbook(Path path, boolean rolling) throws Exception {
        try (var b = new XSSFWorkbook(path.toFile())) {
            var row = b.getSheet("Validation").getRow(2);
            for (int i : new int[] {6, 9, 10}) {
                assertTrue(row.getCell(i).getStringCellValue().contains("TRUNCATED PREVIEW"));
                assertTrue(row.getCell(i).getStringCellValue().length() <= 32767);
            }
            assertTrue(
                    b.getSheet("Validation")
                            .getRow(1)
                            .getCell(6)
                            .getStringCellValue()
                            .contains(DataTypeValidationWorkbookFormat.HINT));
            assertEquals(11, b.getSheet("Validation").getRow(0).getLastCellNum());
            int even = 0, odd = 0, lengths = 0, lengthSheets = 0, diffSheets = 0;
            for (var sheet : b) {
                if (sheet.getSheetName().startsWith("_diffs_")) {
                    diffSheets++;
                    assertTrue(b.isSheetHidden(b.getSheetIndex(sheet)));
                    for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                        var d = sheet.getRow(r);
                        long value = Long.parseLong(d.getCell(6).getStringCellValue());
                        if (d.getCell(4).getStringCellValue().equals("BASELINE_ONLY"))
                            assertEquals(2L * even++, value);
                        else {
                            assertEquals("CURRENT_ONLY", d.getCell(4).getStringCellValue());
                            assertEquals(2L * odd++ + 1, value);
                        }
                    }
                }
                if (sheet.getSheetName().startsWith("_lengths_")) {
                    lengthSheets++;
                    lengths += sheet.getLastRowNum();
                    assertTrue(b.isSheetHidden(b.getSheetIndex(sheet)));
                }
            }
            assertEquals(6000, even);
            assertEquals(6000, odd);
            assertEquals(6001, lengths);
            assertEquals(rolling ? 3 : 1, lengthSheets);
            assertEquals(rolling ? 4 : 1, diffSheets);
        }
    }

    private void writeConfig(String name, Path baseline, String output) throws Exception {
        Files.writeString(
                directory.resolve(name),
                "db.url=jdbc:db2://localhost:25000/TESTDB\n"
                    + "db.username=db2admin\n"
                    + "db.password-env=DB2_PASSWORD\n"
                    + "export.enabled=false\n"
                    + "catalog.table.view=SYSCAT.TABLES\n"
                    + "catalog.column.view=SYSCAT.COLUMNS\n"
                    + "validation.enabled=true\n"
                    + "validation.database-id=SCALE_TEST\n"
                    + "validation.file=validation.csv\n"
                    + "validation.itsview.enabled=false\n"
                    + "validation.length-unit=OCTETS\n"
                    + "validation.query-timeout-seconds=60\n"
                    + "validation.output-directory="
                        + output
                        + "\nvalidation.mode="
                        + (baseline == null ? "BASELINE" : "VALIDATE")
                        + "\nvalidation.baseline.compare.enabled="
                        + (baseline != null)
                        + "\n"
                        + (baseline == null
                                ? ""
                                : "validation.baseline.file=" + baseline.getFileName() + "\n"));
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/datatypevalidation/Db2TestDataMain.java =====
UTF8-BYTES: 12925
SHA256: 0194da988311a0d6541e3b9a94f4e10b461d2f26aa8240eab28770e9c31f5d8f
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import com.example.db2toolkit.config.ConfigLoader;
import com.example.db2toolkit.jdbc.SafeDiagnostics;

import java.nio.file.*;
import java.sql.*;
import java.util.*;

/** Standalone, explicitly invoked fixture writer. Never called by the exporter. */
public final class Db2TestDataMain {
    private final String schema = "DB2ADMIN", views = "DB2ADMIN";
    private String prefix;
    private final List<String> cleanup = new ArrayList<>(), executed = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        if (args.length != 3 || !Set.of("prepare", "change").contains(args[0]))
            throw new IllegalArgumentException(
                    "Usage: Db2TestDataMain prepare|change <application.properties>"
                        + " <run-directory>");
        new Db2TestDataMain().execute(args[0], Path.of(args[1]), Path.of(args[2]).toAbsolutePath());
    }

    private void execute(String phase, Path config, Path directory) throws Exception {
        var source = new ConfigLoader().load(config, System::getenv);
        if (!source.url().equals("jdbc:db2://localhost:25000/TESTDB")
                || !source.username().equalsIgnoreCase("db2admin"))
            throw new IllegalArgumentException(
                    "Fixture writes restricted to db2admin on localhost:25000/TESTDB");
        Path marker = directory.resolve("fixture.properties");
        if (phase.equals("prepare")) {
            Files.createDirectories(directory);
            prefix =
                    "DVTEST_"
                            + UUID.randomUUID()
                                    .toString()
                                    .replace("-", "")
                                    .substring(0, 10)
                                    .toUpperCase(Locale.ROOT)
                            + "_";
            Files.writeString(
                    marker,
                    "schema=DB2ADMIN\nprefix=" + prefix + "\n",
                    StandardOpenOption.CREATE_NEW);
        } else {
            var properties = new Properties();
            try (var reader = Files.newBufferedReader(marker)) {
                properties.load(reader);
            }
            prefix = properties.getProperty("prefix", "");
            if (!prefix.matches("DVTEST_[A-F0-9]{10}_"))
                throw new IllegalArgumentException("Invalid fixture prefix");
            if (!Files.exists(directory.resolve("prepare.done")))
                throw new IllegalStateException("Prepare did not complete");
            Files.writeString(
                    directory.resolve("change.started"), "started", StandardOpenOption.CREATE_NEW);
        }
        try (var connection =
                DriverManager.getConnection(source.url(), source.username(), source.password())) {
            if (phase.equals("prepare")) prepare(connection);
            else change(connection);
            Files.writeString(
                    directory.resolve(phase + ".done"), "complete", StandardOpenOption.CREATE_NEW);
            System.out.println(
                    "Fixture " + phase + " complete: " + schema + "." + prefix + "*; " + directory);
        } catch (SQLException e) {
            throw new IllegalStateException(new SafeDiagnostics(source).describe(e));
        } finally {
            Files.writeString(
                    directory.resolve("executed.sql"),
                    String.join(";\n", executed) + ";\n",
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
            Path cleanupFile = directory.resolve("cleanup.sql");
            String previous = Files.exists(cleanupFile) ? Files.readString(cleanupFile) : "";
            Collections.reverse(cleanup);
            Files.writeString(cleanupFile, String.join(";\n", cleanup) + ";\n" + previous);
        }
    }

    private void prepare(Connection connection) throws SQLException {
        create(
                connection,
                "TABLE",
                table("CASES"),
                "CREATE TABLE "
                        + table("CASES")
                        + " (ID INTEGER NOT NULL PRIMARY KEY, N DECIMAL(5,0), S VARCHAR(10), C"
                        + " CLOB(100), MIX VARCHAR(10), BIG_N DECIMAL(31,0))");
        create(
                connection,
                "TABLE",
                table("NULL_ROWS"),
                "CREATE TABLE " + table("NULL_ROWS") + " (V VARCHAR(20), N DECIMAL(5,0))");
        create(
                connection,
                "TABLE",
                table("EMPTY_ROWS"),
                "CREATE TABLE " + table("EMPTY_ROWS") + " (V VARCHAR(20), N DECIMAL(5,0))");
        create(
                connection,
                "TABLE",
                table("TYPE_CASES"),
                "CREATE TABLE "
                        + table("TYPE_CASES")
                        + " (NUM_AS_TEXT VARCHAR(20), TEXT_AS_NUMBER INTEGER, FRACTION"
                        + " DECIMAL(10,2), BIT_TEXT VARCHAR(20) FOR BIT DATA, \" NUM_AS_TEXT\""
                        + " INTEGER)");
        sql(
                connection,
                "INSERT INTO "
                        + table("CASES")
                        + " VALUES (1,-99999,'ABCDE',CAST('short' AS"
                        + " CLOB(100)),'',-9999999999999999999999999999999)");
        sql(
                connection,
                "INSERT INTO "
                        + table("CASES")
                        + " VALUES (2,99999,'VWXYZ',CAST('longer' AS"
                        + " CLOB(100)),'ab',9999999999999999999999999999999)");
        sql(connection, "INSERT INTO " + table("CASES") + " VALUES (3,NULL,'12345',NULL,NULL,0)");
        sql(
                connection,
                "INSERT INTO "
                        + table("NULL_ROWS")
                        + " VALUES (NULL,NULL),(NULL,NULL),(NULL,NULL)");
        sql(
                connection,
                "INSERT INTO "
                        + table("TYPE_CASES")
                        + " VALUES ('not-a-number',42,1.25,X'4142',99)");

        create(
                connection,
                "TABLE",
                table("EDGE_VALUES"),
                "CREATE TABLE "
                        + table("EDGE_VALUES")
                        + " (MULTI_SAME VARCHAR(20), MULTI_DIFF VARCHAR(20), ZERO_ONLY VARCHAR(20),"
                        + " NULL_MIX VARCHAR(20), BOUNDARY_V VARCHAR(20), BOUNDARY_C CLOB(200),"
                        + " NEG_N DECIMAL(20,0), AS_CLOB VARCHAR(20), OCTETS_V VARCHAR(40),"
                        + " UNICODE_V VARCHAR(40))");
        create(
                connection,
                "TABLE",
                table("EMPTY_TO_NULL"),
                "CREATE TABLE " + table("EMPTY_TO_NULL") + " (V VARCHAR(20))");
        create(
                connection,
                "TABLE",
                table("NULL_TO_EMPTY"),
                "CREATE TABLE " + table("NULL_TO_EMPTY") + " (V VARCHAR(20))");
        sql(
                connection,
                "INSERT INTO "
                        + table("EDGE_VALUES")
                        + " VALUES ('a','a','',NULL,'aaaaaaaaaa',CAST('"
                        + "x".repeat(100)
                        + "' AS CLOB(200)),-99999,'abcd','\u4E2D','\u4E2D')");
        sql(
                connection,
                "INSERT INTO "
                        + table("EDGE_VALUES")
                        + " VALUES"
                        + " ('bb','bb','','abc',NULL,NULL,NULL,'abcd','\uD83D\uDE00','\uD83D\uDE00')");
        sql(
                connection,
                "INSERT INTO "
                        + table("EDGE_VALUES")
                        + " VALUES ('ccc','ccc','',NULL,NULL,NULL,99999,'abcd','A','A')");
        sql(connection, "INSERT INTO " + table("NULL_TO_EMPTY") + " VALUES (NULL)");
    }

    private void change(Connection connection) throws SQLException {
        sql(
                connection,
                "ALTER TABLE " + table("CASES") + " ALTER COLUMN N SET DATA TYPE DECIMAL(20,0)");
        sql(
                connection,
                "ALTER TABLE " + table("CASES") + " ALTER COLUMN S SET DATA TYPE VARCHAR(50)");
        sql(
                connection,
                "ALTER TABLE " + table("CASES") + " ALTER COLUMN C SET DATA TYPE CLOB(200)");
        sql(
                connection,
                "ALTER TABLE " + table("CASES") + " ALTER COLUMN MIX SET DATA TYPE VARCHAR(50)");
        sql(connection, "CALL SYSPROC.ADMIN_CMD('REORG TABLE " + table("CASES") + "')");
        sql(
                connection,
                "INSERT INTO "
                        + table("CASES")
                        + " VALUES (4,100000,'HELLO',CAST('"
                        + "x".repeat(101)
                        + "' AS CLOB(200)),'abcdefghijk',0)");
        sql(connection, "INSERT INTO " + table("NULL_ROWS") + " VALUES (NULL,NULL),(NULL,NULL)");
        for (String name : List.of("CASES", "NULL_ROWS", "EMPTY_ROWS", "TYPE_CASES"))
            create(
                    connection,
                    "VIEW",
                    view(name),
                    "CREATE VIEW " + view(name) + " AS SELECT * FROM " + table(name));
        create(
                connection,
                "VIEW",
                view("BAD_QUERY"),
                "CREATE VIEW "
                        + view("BAD_QUERY")
                        + " AS SELECT CAST(NUM_AS_TEXT AS DECIMAL(5,0)) AS N FROM "
                        + table("TYPE_CASES"));
        create(
                connection,
                "VIEW",
                catalog("OBJECTS"),
                "CREATE VIEW "
                        + catalog("OBJECTS")
                        + " AS SELECT * FROM SYSCAT.TABLES WHERE TABSCHEMA IN ('"
                        + schema
                        + "','"
                        + views
                        + "')");
        create(
                connection,
                "VIEW",
                catalog("COLUMNS"),
                "CREATE VIEW "
                        + catalog("COLUMNS")
                        + " AS SELECT * FROM SYSCAT.COLUMNS WHERE TABSCHEMA IN ('"
                        + schema
                        + "','"
                        + views
                        + "')");

        sql(
                connection,
                "UPDATE " + table("EDGE_VALUES") + " SET MULTI_DIFF='dddd' WHERE MULTI_DIFF='a'");
        sql(
                connection,
                "INSERT INTO "
                        + table("EDGE_VALUES")
                        + " VALUES ('a','bb','','abc','aaaaaaaaaaa',CAST('"
                        + "x".repeat(101)
                        + "' AS CLOB(200)),-100000,'abcd','a\uD83D\uDE00','a\uD83D\uDE00')");
        sql(connection, "INSERT INTO " + table("EMPTY_TO_NULL") + " VALUES (NULL)");
        sql(connection, "DELETE FROM " + table("NULL_TO_EMPTY"));
        create(
                connection,
                "VIEW",
                view("EDGE_VALUES"),
                "CREATE VIEW "
                        + view("EDGE_VALUES")
                        + " AS SELECT"
                        + " MULTI_SAME,MULTI_DIFF,ZERO_ONLY,NULL_MIX,BOUNDARY_V,BOUNDARY_C,NEG_N,CAST(AS_CLOB"
                        + " AS CLOB(100)) AS AS_CLOB,OCTETS_V,UNICODE_V FROM "
                        + table("EDGE_VALUES"));
        for (String name : List.of("EMPTY_TO_NULL", "NULL_TO_EMPTY"))
            create(
                    connection,
                    "VIEW",
                    view(name),
                    "CREATE VIEW " + view(name) + " AS SELECT * FROM " + table(name));
    }

    private void create(Connection c, String type, String name, String sql) throws SQLException {
        sql(c, sql);
        cleanup.add("DROP " + type + " " + name + (type.equals("SCHEMA") ? " RESTRICT" : ""));
    }

    private void sql(Connection connection, String sql) throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.setQueryTimeout(30);
            statement.execute(sql);
            executed.add(sql);
        }
    }

    private static String q(String name) {
        return '"' + name + '"';
    }

    private String table(String name) {
        return q(schema) + "." + q(prefix + name);
    }

    private String view(String name) {
        return q(views) + "." + q(schema + "_" + prefix + name);
    }

    private String catalog(String name) {
        return q(views) + "." + q(prefix + "CAT_" + name);
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/datatypevalidation/Db2ValidationIntegrationTest.java =====
UTF8-BYTES: 9552
SHA256: db72e315cc2ab91031f80e93105879d1d6c1dcfefb489770a02385d050a19a26
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import static org.junit.jupiter.api.Assertions.*;

import com.example.db2toolkit.config.AppConfig;
import com.example.db2toolkit.config.ConfigLoader;
import com.example.db2toolkit.jdbc.SafeDiagnostics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.util.Map;

/** Opt-in, read-only DB2 LUW syntax checks. Creates no objects and changes no database data. */
@EnabledIfEnvironmentVariable(named = "DB2_VALIDATION_INTEGRATION", matches = "true")
class Db2ValidationIntegrationTest {
    @org.junit.jupiter.api.io.TempDir Path dir;

    private static AppConfig configuration() throws Exception {
        String file = System.getenv("DB2_VALIDATION_CONFIG");
        return file == null
                ? new AppConfig(
                        System.getenv("DB2_VALIDATION_URL"),
                        System.getenv("DB2_VALIDATION_USER"),
                        System.getenv("DB2_VALIDATION_PASSWORD"),
                        Map.of(),
                        Path.of("."),
                        "P",
                        "F",
                        "T",
                        "C")
                : new ConfigLoader().load(Path.of(file), System::getenv);
    }

    @Test
    void numericBoundParametersNullEmptyAndLengthUnitsRunOnActualDb2() throws Exception {
        AppConfig config = configuration();
        var errors = new SafeDiagnostics(config);
        try (var connection =
                DriverManager.getConnection(config.url(), config.username(), config.password())) {
            String sql =
                    "SELECT COUNT_BIG(*) AS N, COALESCE(SUM(CAST(CASE WHEN V < CAST(? AS"
                        + " DECIMAL(31,0)) OR V > CAST(? AS DECIMAL(31,0)) THEN 1 ELSE 0 END AS"
                        + " DECIMAL(31,0))), CAST(0 AS DECIMAL(31,0))) AS O FROM (VALUES"
                        + " CAST(-100000 AS BIGINT), CAST(99999 AS BIGINT), CAST(NULL AS BIGINT))"
                        + " AS T(V)";
            try (var statement = connection.prepareStatement(sql)) {
                statement.setBigDecimal(1, new BigDecimal("-99999"));
                statement.setBigDecimal(2, new BigDecimal("99999"));
                try (var rows = statement.executeQuery()) {
                    assertTrue(rows.next());
                    assertEquals(new BigDecimal("3"), rows.getBigDecimal("N"));
                    assertEquals(new BigDecimal("1"), rows.getBigDecimal("O"));
                }
            }
            try (var statement = connection.prepareStatement(sql + " WHERE 1 = 0")) {
                statement.setBigDecimal(1, new BigDecimal("-9999999999999999999999999999999"));
                statement.setBigDecimal(2, new BigDecimal("9999999999999999999999999999999"));
                try (var rows = statement.executeQuery()) {
                    assertTrue(rows.next());
                    assertEquals(0, rows.getBigDecimal("N").signum());
                    assertEquals(0, rows.getBigDecimal("O").signum());
                }
            }
            for (String unit : new String[] {"OCTETS", "CODEUNITS32"}) {
                for (String type : new String[] {"VARCHAR(20)", "CLOB(100)"}) {
                    String length = "LENGTH(V, " + unit + ")";
                    try (var statement =
                                    connection.prepareStatement(
                                            "SELECT "
                                                    + length
                                                    + " AS L, COUNT_BIG(*) AS N FROM (VALUES"
                                                    + " CAST(NULL AS "
                                                    + type
                                                    + "), CAST('abc' AS "
                                                    + type
                                                    + "), CAST('xyz' AS "
                                                    + type
                                                    + ")) AS T(V) GROUP BY "
                                                    + length
                                                    + " ORDER BY CASE WHEN "
                                                    + length
                                                    + " IS NULL THEN 0 ELSE 1 END, "
                                                    + length);
                            var rows = statement.executeQuery()) {
                        assertTrue(rows.next());
                        assertNull(rows.getBigDecimal("L"));
                        assertEquals(1, rows.getInt("N"));
                        assertTrue(rows.next());
                        assertEquals(3, rows.getInt("L"));
                        assertEquals(2, rows.getInt("N"));
                        assertFalse(rows.next());
                    }
                }
            }
        } catch (Exception e) {
            throw new AssertionError(errors.describe(e));
        }
    }

    @Test
    void realCatalogViewRunsThroughMetadataBaselineAndComparisonWithoutChangingObjects()
            throws Exception {
        AppConfig original = configuration();
        // Explicit standard catalogs also support the environment-only connection configuration.
        AppConfig app =
                new AppConfig(
                        original.url(),
                        original.username(),
                        original.password(),
                        Map.of(),
                        dir,
                        original.procedures(),
                        original.functions(),
                        "\"SYSCAT\".\"TABLES\"",
                        "\"SYSCAT\".\"COLUMNS\"");
        var errors = new SafeDiagnostics(app);
        Path csv = dir.resolve("catalog.csv");
        java.nio.file.Files.writeString(
                csv,
                DataTypeValidationFixture.HEADER
                        + "SYSCAT.TABLES,TABSCHEMA,VARCHAR(32672),VARCHAR(32672)\n"
                        + "SYSCAT.TABLES,TABNAME,VARCHAR(32672),VARCHAR(32672)\n");
        var base =
                new DataTypeValidationConfig(
                        true,
                        DataTypeValidationConfig.Mode.BASELINE,
                        "LOCAL_TEST",
                        csv,
                        dir.resolve("reports"),
                        false,
                        "ITSVIEW",
                        DataTypeValidationConfig.LengthUnit.OCTETS,
                        false,
                        null,
                        30);
        try (var connection =
                DriverManager.getConnection(app.url(), app.username(), app.password())) {
            var baseline = new DataTypeValidationSummary();
            try (var plan = DataTypeValidationPlan.prepare(base)) {
                new DataTypeValidationService()
                        .validateAndWriteReport(
                                connection,
                                app.withValidation(base),
                                plan,
                                "live",
                                null,
                                true,
                                baseline,
                                errors);
            }
            assertEquals(
                    0,
                    baseline.exitCode(),
                    () -> baseline.results().stream().map(r -> r.error).toList().toString());
            assertEquals(2, baseline.results().size());
            assertTrue(baseline.results().get(0).records().signum() > 0);
            var validate =
                    new DataTypeValidationConfig(
                            true,
                            DataTypeValidationConfig.Mode.VALIDATE,
                            "LOCAL_TEST",
                            csv,
                            dir.resolve("reports"),
                            false,
                            "ITSVIEW",
                            DataTypeValidationConfig.LengthUnit.OCTETS,
                            true,
                            baseline.published(),
                            30);
            var current = new DataTypeValidationSummary();
            try (var plan = DataTypeValidationPlan.prepare(validate)) {
                new DataTypeValidationService()
                        .validateAndWriteReport(
                                connection,
                                app.withValidation(validate),
                                plan,
                                "live",
                                null,
                                true,
                                current,
                                errors);
            }
            assertEquals(
                    0,
                    current.exitCode(),
                    () -> current.results().stream().map(r -> r.error).toList().toString());
            assertTrue(current.results().stream().allMatch(r -> r.compare().equals("PASS")));
            assertFalse(connection.isClosed());
        } catch (Exception e) {
            throw new AssertionError(errors.describe(e));
        }
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/datatypevalidation/DiskSharedStringsTest.java =====
UTF8-BYTES: 4220
SHA256: d740ee4efe8fd82639c2bf25fd9faa6a613a0ad3544c57fd1fcdaa3a7d167c0b
===== CONTENT =====
package com.example.db2toolkit.datatypevalidation;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

class DiskSharedStringsTest {
    @Test
    void validSupplementaryAndLiteralEscapesArePreserved() throws Exception {
        String xml = "<sst xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
            + "<si><t>_xD83D__xDE00_ _x005F_xD800_</t></si></sst>";
        try (var workspace = new DataTypeValidationWorkspace();
             var strings = DiskSharedStrings.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), workspace)) {
            assertEquals("😀 _xD800_", strings.get(0));
        }
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {"_xD800_", "_xDC00_"})
    void malformedUnicodeEscapesCannotBeSilentlyReplaced(String value) throws Exception {
        String xml = "<sst xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><si><t>"
            + value + "</t></si></sst>";
        try (var workspace = new DataTypeValidationWorkspace()) {
            assertThrows(Exception.class, () -> {
                try (var strings = DiskSharedStrings.read(
                    new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), workspace)) {}
            });
        }
    }

    @Test
    void inputIsClosedWhenDiskIndexCannotBeCreated() throws Exception {
        var workspace = org.mockito.Mockito.mock(DataTypeValidationWorkspace.class);
        var input = org.mockito.Mockito.mock(InputStream.class);
        var failure = new IOException("Cannot create staging file");
        org.mockito.Mockito.when(workspace.file()).thenThrow(failure);
        assertSame(failure, assertThrows(IOException.class, () -> DiskSharedStrings.read(input, workspace)));
        org.mockito.Mockito.verify(input).close();
    }

    private static InputStream xml(int count) {
        var text = new StringBuilder("<sst xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");
        for (int i = 0; i < count; i++)
            text.append("<si><t>").append(value(i)).append("</t></si>");
        return new ByteArrayInputStream(text.append("</sst>").toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String value(int index) { return "Field_" + index + "中😀".repeat(1000); }

    @Test
    void randomAccessAndRepeatedReadsSurviveCacheEviction() throws Exception {
        try (var workspace = new DataTypeValidationWorkspace();
             var strings = DiskSharedStrings.read(xml(1100), workspace)) {
            for (int i = 1099; i >= 0; i--) assertEquals(value(i), strings.get(i));
            for (int i = 0; i < 1100; i++) assertEquals(value(i), strings.get(i));
            var field = DiskSharedStrings.class.getDeclaredField("cache");
            field.setAccessible(true);
            var cache = (java.util.Map<?, ?>) field.get(strings);
            assertTrue(cache.size() <= 1024);
            assertTrue(cache.values().stream().mapToInt(v -> ((String) v).length()).sum() <= 262144);
            assertEquals(value(0), strings.get(0));
            assertEquals(value(0), strings.get(0));
            assertThrows(IOException.class, () -> strings.get(-1));
            assertThrows(IOException.class, () -> strings.get(1100));
        }
    }

    @Test
    @EnabledIfSystemProperty(named = "validation.performance-tests", matches = "true")
    void repeatedLookupProbe() throws Exception {
        try (var workspace = new DataTypeValidationWorkspace();
             var strings = DiskSharedStrings.read(xml(8), workspace)) {
            long started = System.nanoTime();
            long characters = 0;
            for (int i = 0; i < 30000; i++) characters += strings.get(i % 8).length();
            System.out.printf(java.util.Locale.ROOT, "SHARED_STRINGS lookups=30000 elapsedMs=%.3f characters=%d%n",
                (System.nanoTime() - started) / 1_000_000d, characters);
            assertEquals(30000L * value(0).length(), characters);
        }
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/ddl/DdlExportServiceTest.java =====
UTF8-BYTES: 23920
SHA256: f3bc602ba04ce5a29a12171f901d2a2ab4d095570522590664ff9d75b074df2b
===== CONTENT =====
package com.example.db2toolkit.ddl;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.db2toolkit.config.AppConfig;
import com.example.db2toolkit.ddl.model.DdlExportRequest;
import com.example.db2toolkit.ddl.model.DdlExportResult;
import com.example.db2toolkit.ddl.model.DdlExportStatus;
import com.example.db2toolkit.ddl.model.DdlExportSummary;
import com.example.db2toolkit.ddl.model.DdlObjectType;
import com.example.db2toolkit.model.DbObjectRef;
import com.example.db2toolkit.output.SqlFileWriter;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

class DdlExportServiceTest {
    @TempDir Path dir;
    Connection connection;
    AppConfig config;

    @BeforeEach
    void setup() throws Exception {
        connection = mock(Connection.class);
        when(connection.isValid(5)).thenReturn(true);
        config =
                new AppConfig(
                        "jdbc:db2://host:50000/DB:password=secret;",
                        "u",
                        "secret",
                        Map.of(),
                        dir,
                        "\"SYSCAT\".\"PROCEDURES\"",
                        "\"SYSCAT\".\"FUNCTIONS\"",
                        "\"SYSCAT\".\"TABLES\"",
                        "\"SYSCAT\".\"COLUMNS\"");
    }

    private PreparedStatement statement(ResultSet rs) throws Exception {
        var ps = mock(PreparedStatement.class);
        when(ps.executeQuery()).thenReturn(rs);
        return ps;
    }

    private DdlExportRequest request(DdlObjectType type, String name) {
        return new DdlExportRequest(type, new DbObjectRef("RPT", name));
    }

    private DdlExportSummary run(List<DdlExportRequest> requests) throws Exception {
        var report = new DdlExportSummary();
        new DdlExportService().export(connection, config, requests, new SqlFileWriter(dir), report);
        return report;
    }

    @Test
    void overloadCountsAndClobNotTruncated() throws Exception {
        var rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getString("SPECIFICNAME")).thenReturn("F1", "F2");
        String ddl = "CREATE FUNCTION RPT.F() RETURNS INT /*" + "x".repeat(100000) + "*/ RETURN 1;";
        var stream = spy(new StringReader(ddl));
        when(rs.getCharacterStream("BODY")).thenReturn(stream, new StringReader("RETURN 2;"));
        var ps = statement(rs);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        var report = run(List.of(request(DdlObjectType.FUNCTION, "F")));
        assertEquals(1, report.requestCount());
        assertEquals(2, report.matchedRoutineInstances());
        assertEquals(1, report.count(DdlExportStatus.SUCCESS));
        assertEquals(1, report.count(DdlExportStatus.DDL_UNAVAILABLE));
        assertEquals(ddl + "\n@\n", Files.readString(report.results().get(0).outputPath()));
        assertEquals(1, report.exitCode());
        verify(ps).setString(1, "RPT");
        verify(ps).setString(2, "F");
        verify(stream).close();
        verify(rs).close();
        verify(ps).close();
    }

    @Test
    void missingColumnAndPermissionAreFailedNotMissing() throws Exception {
        var first = mock(PreparedStatement.class);
        when(first.executeQuery()).thenThrow(new SQLException("BODY missing", "42703", -206));
        var second = mock(PreparedStatement.class);
        when(second.executeQuery())
                .thenThrow(new SQLException("Denied secret " + config.url(), "42501", -551));
        var empty = mock(ResultSet.class);
        var third = statement(empty);
        when(connection.prepareStatement(anyString())).thenReturn(first, second, third);
        var report =
                run(
                        List.of(
                                request(DdlObjectType.FUNCTION, "A"),
                                request(DdlObjectType.FUNCTION, "B"),
                                request(DdlObjectType.FUNCTION, "C")));
        assertEquals(2, report.count(DdlExportStatus.FAILED));
        assertEquals(1, report.count(DdlExportStatus.NOT_FOUND));
        assertTrue(report.results().get(0).reason().contains("42703"));
        assertTrue(report.results().get(1).reason().contains("-551"));
        assertFalse(report.results().get(1).reason().contains("secret"));
        assertFalse(report.results().get(1).reason().contains("jdbc:db2"));
        assertEquals(
                List.of("A", "B", "C"),
                report.results().stream().map(DdlExportResult::objectName).toList());
    }

    @Test
    void connectionLossRetainsEarlierInstancesAndStops() throws Exception {
        var rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true).thenThrow(new SQLException("Lost", "08006", -4499));
        when(rs.getString("SPECIFICNAME")).thenReturn("P1");
        when(rs.getCharacterStream("TEXT"))
                .thenReturn(new StringReader("CREATE PROCEDURE RPT.P() BEGIN END;"));
        var ps = statement(rs);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        var report =
                run(
                        List.of(
                                request(DdlObjectType.PROCEDURE, "P"),
                                request(DdlObjectType.FUNCTION, "F"),
                                request(DdlObjectType.TABLE, "T")));
        assertEquals(1, report.count(DdlExportStatus.SUCCESS));
        assertEquals(1, report.count(DdlExportStatus.FAILED));
        assertEquals(1, report.matchedRoutineInstances());
        assertEquals(2, report.unprocessed().size());
        assertEquals(1, report.exitCode());
        verify(connection, times(1)).prepareStatement(anyString());
    }

    @Test
    void invalidConnectionSkipsAllQueries() throws Exception {
        when(connection.isValid(5)).thenReturn(false);
        var report = run(List.of(request(DdlObjectType.TABLE, "T")));
        assertEquals(1, report.unprocessed().size());
        assertEquals(1, report.exitCode());
        verify(connection, never()).prepareStatement(anyString());
    }

    @Test
    void streamFailureDoesNotStopNextOverload() throws Exception {
        var rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getString("SPECIFICNAME")).thenReturn("A", "B");
        Reader broken = mock(Reader.class);
        when(broken.transferTo(any(Writer.class))).thenThrow(new IOException("Broken"));
        when(rs.getCharacterStream("BODY"))
                .thenReturn(
                        broken, new StringReader("CREATE FUNCTION RPT.F() RETURNS INT RETURN 1;"));
        var ps = statement(rs);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        var report = run(List.of(request(DdlObjectType.FUNCTION, "F")));
        assertEquals(2, report.matchedRoutineInstances());
        assertEquals(1, report.count(DdlExportStatus.FAILED));
        assertEquals(1, report.count(DdlExportStatus.SUCCESS));
        verify(broken).close();
    }

    @Test
    void emptyReportAndFatalExitCodes() {
        var report = new DdlExportSummary();
        report.addListStatistics(0, 2);
        assertEquals(0, report.exitCode());
        report.addListStatistics(1, 0);
        assertEquals(1, report.exitCode());
        report.stop("fatal", List.of(), true);
        assertEquals(2, report.exitCode());
    }

    @org.junit.jupiter.params.ParameterizedTest
    @EnumSource(
            value = DdlObjectType.class,
            names = {"PROCEDURE", "FUNCTION"})
    void definitionSqlErrorKeepsSpecificNameAndContinuesOtherOverloads(DdlObjectType type)
            throws Exception {
        ResultSet rows = mock(ResultSet.class);
        when(rows.next()).thenReturn(true, true, false);
        when(rows.getString("SPECIFICNAME")).thenReturn("BROKEN_INSTANCE", "GOOD_INSTANCE");
        String field = type == DdlObjectType.PROCEDURE ? "TEXT" : "BODY";
        when(rows.getCharacterStream(field))
                .thenThrow(new SQLException("read secret failed", "22000", -999))
                .thenReturn(
                        new StringReader(
                                "CREATE "
                                        + type
                                        + " RPT.X() "
                                        + (type == DdlObjectType.PROCEDURE
                                                ? "BEGIN END;"
                                                : "RETURNS INT RETURN 1;")));
        PreparedStatement prepared = statement(rows);
        when(connection.prepareStatement(anyString())).thenReturn(prepared);
        DdlExportSummary report = run(List.of(request(type, "X")));
        assertEquals(2, report.matchedRoutineInstances());
        assertEquals(1, report.count(DdlExportStatus.FAILED));
        assertEquals(1, report.count(DdlExportStatus.SUCCESS));
        assertEquals("BROKEN_INSTANCE", report.results().get(0).specificName());
        assertTrue(report.results().get(0).reason().contains("22000"));
        assertFalse(report.results().get(0).reason().contains("secret"));
        verify(connection, times(1)).prepareStatement(anyString());
    }

    @org.junit.jupiter.params.ParameterizedTest
    @EnumSource(
            value = DdlObjectType.class,
            names = {"PROCEDURE", "FUNCTION"})
    void connectionLossWhileOpeningDefinitionStreamStillStopsRemainingRequests(DdlObjectType type)
            throws Exception {
        ResultSet rows = mock(ResultSet.class);
        when(rows.next()).thenReturn(true);
        when(rows.getString("SPECIFICNAME")).thenReturn("P1");
        when(rows.getCharacterStream(type == DdlObjectType.PROCEDURE ? "TEXT" : "BODY"))
                .thenThrow(new SQLException("Lost", "08006", -4499));
        PreparedStatement prepared = statement(rows);
        when(connection.prepareStatement(anyString())).thenReturn(prepared);
        DdlExportSummary report =
                run(List.of(request(type, "P"), request(DdlObjectType.TABLE, "T")));
        assertEquals(1, report.count(DdlExportStatus.FAILED));
        assertEquals(1, report.matchedRoutineInstances());
        assertEquals("P1", report.results().get(0).specificName());
        assertEquals(1, report.unprocessed().size());
        verify(rows, times(1)).next();
        verify(connection, times(1)).prepareStatement(anyString());
    }

    @Test
    void tableRendersDefaultAndRejectsIdentity() throws Exception {
        var table = mock(ResultSet.class);
        when(table.next()).thenReturn(true, false);
        when(table.getString("TYPE")).thenReturn("T");
        when(table.getString("TEMPORALTYPE")).thenReturn("N");
        var columns = basicColumn();
        var tablePs = statement(table);
        var columnsPs = statement(columns);
        when(connection.prepareStatement(anyString())).thenReturn(tablePs, columnsPs);
        var report = run(List.of(request(DdlObjectType.TABLE, "T")));
        assertEquals(1, report.count(DdlExportStatus.SUCCESS));
        assertEquals(
                "CREATE TABLE RPT.T (\n    \"a\"\"b\" DECIMAL(18,2) NOT NULL DEFAULT 0\n)\n@\n",
                Files.readString(report.results().get(0).outputPath()));
        var table2 = mock(ResultSet.class);
        when(table2.next()).thenReturn(true, false);
        when(table2.getString("TYPE")).thenReturn("T");
        when(table2.getString("TEMPORALTYPE")).thenReturn("N");
        var identity = basicColumn();
        when(identity.getString("IDENTITY")).thenReturn("Y");
        var tablePs2 = statement(table2);
        var identityPs = statement(identity);
        when(connection.prepareStatement(anyString())).thenReturn(tablePs2, identityPs);
        assertEquals(
                1,
                run(List.of(request(DdlObjectType.TABLE, "I")))
                        .count(DdlExportStatus.DDL_UNAVAILABLE));
    }

    @Test
    void viewIsNotExportedAsTable() throws Exception {
        var table = mock(ResultSet.class);
        when(table.next()).thenReturn(true);
        when(table.getString("TYPE")).thenReturn("V");
        var ps = statement(table);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        assertEquals(
                1,
                run(List.of(request(DdlObjectType.TABLE, "V")))
                        .count(DdlExportStatus.DDL_UNAVAILABLE));
        verify(connection, times(1)).prepareStatement(anyString());
    }

    @Test
    void nullRequiredMetadataIsNotRenderedAsZero() throws Exception {
        var table = mock(ResultSet.class);
        when(table.next()).thenReturn(true, false);
        when(table.getString("TYPE")).thenReturn("T");
        when(table.getString("TEMPORALTYPE")).thenReturn("N");
        var column = basicColumn();
        when(column.wasNull()).thenReturn(false, true, true);
        var tableStatement = statement(table);
        var columnStatement = statement(column);
        when(connection.prepareStatement(anyString())).thenReturn(tableStatement, columnStatement);
        var report = run(List.of(request(DdlObjectType.TABLE, "T")));
        assertEquals(1, report.count(DdlExportStatus.DDL_UNAVAILABLE));
        assertTrue(report.results().get(0).reason().contains("LENGTH"));
    }

    @Test
    void unavailableTableWithCleanupDisconnectStopsRemainingRequests() throws Exception {
        ResultSet rows = mock(ResultSet.class);
        when(rows.next()).thenReturn(true);
        when(rows.getString("TYPE")).thenReturn("V");
        doThrow(new SQLNonTransientConnectionException("close secret failed", "08006", -4499))
                .when(rows)
                .close();
        PreparedStatement prepared = statement(rows);
        when(connection.prepareStatement(anyString())).thenReturn(prepared);
        DdlExportSummary report = new DdlExportSummary();
        assertFalse(
                new DdlExportService()
                        .export(
                                connection,
                                config,
                                List.of(
                                        request(DdlObjectType.TABLE, "V"),
                                        request(DdlObjectType.PROCEDURE, "P")),
                                new SqlFileWriter(dir),
                                report));
        assertEquals(1, report.count(DdlExportStatus.DDL_UNAVAILABLE));
        assertEquals(1, report.unprocessed().size());
        assertTrue(report.results().get(0).reason().contains("08006"));
        assertFalse(report.results().get(0).reason().contains("secret"));
        verify(connection, times(1)).prepareStatement(anyString());
    }

    @org.junit.jupiter.params.ParameterizedTest
    @EnumSource(
            value = DdlObjectType.class,
            names = {"PROCEDURE", "FUNCTION"})
    void streamIOExceptionWithDisconnectStopsOverloadsAndNextRequests(DdlObjectType type)
            throws Exception {
        ResultSet rows = mock(ResultSet.class);
        when(rows.next()).thenReturn(true);
        when(rows.getString("SPECIFICNAME")).thenReturn("BROKEN_INSTANCE");
        Reader reader = mock(Reader.class);
        IOException read =
                new IOException(
                        "stream secret failed",
                        new SQLRecoverableException("connection lost", "08006", -4499));
        when(reader.transferTo(any(Writer.class))).thenThrow(read);
        when(rows.getCharacterStream(type == DdlObjectType.PROCEDURE ? "TEXT" : "BODY"))
                .thenReturn(reader);
        PreparedStatement prepared = statement(rows);
        when(connection.prepareStatement(anyString())).thenReturn(prepared);
        DdlExportSummary report =
                run(List.of(request(type, "X"), request(DdlObjectType.TABLE, "T")));
        assertEquals(1, report.count(DdlExportStatus.FAILED));
        assertEquals(1, report.matchedRoutineInstances());
        assertEquals("BROKEN_INSTANCE", report.results().get(0).specificName());
        assertEquals(1, report.unprocessed().size());
        assertTrue(report.results().get(0).reason().contains("stream"));
        assertTrue(report.results().get(0).reason().contains("08006"));
        assertFalse(report.results().get(0).reason().contains("secret"));
        verify(rows, times(1)).next();
        verify(reader).close();
        verify(connection, times(1)).prepareStatement(anyString());
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.CsvSource({
        "PROCEDURE,false",
        "FUNCTION,false",
        "PROCEDURE,true",
        "FUNCTION,true"
    })
    void uncheckedDefinitionReadFailureRetainsIdentityAndIsolatesOverloads(
            DdlObjectType type, boolean disconnected) throws Exception {
        ResultSet rows = mock(ResultSet.class);
        when(rows.next()).thenReturn(true, true, false);
        when(rows.getString("SPECIFICNAME")).thenReturn("BROKEN_SPEC", "GOOD_SPEC");
        Reader broken = mock(Reader.class);
        RuntimeException read = new IllegalStateException("reader secret failed");
        if (disconnected) read.addSuppressed(new SQLRecoverableException("lost", "08006", -4499));
        when(broken.transferTo(any(Writer.class))).thenThrow(read);
        String ddl =
                "CREATE "
                        + type
                        + " RPT.X() "
                        + (type == DdlObjectType.PROCEDURE
                                ? "BEGIN END;"
                                : "RETURNS INT RETURN 1;");
        when(rows.getCharacterStream(type == DdlObjectType.PROCEDURE ? "TEXT" : "BODY"))
                .thenReturn(broken, new StringReader(ddl));
        var prepared = statement(rows);
        var empty = statement(mock(ResultSet.class));
        when(connection.prepareStatement(anyString())).thenReturn(prepared, empty);
        var report = run(List.of(request(type, "X"), request(DdlObjectType.TABLE, "LATER")));
        assertEquals(disconnected ? 1 : 2, report.matchedRoutineInstances());
        assertEquals(1, report.count(DdlExportStatus.FAILED));
        assertEquals("BROKEN_SPEC", report.results().get(0).specificName());
        assertTrue(report.results().get(0).reason().contains("reader"));
        assertFalse(report.results().get(0).reason().contains("secret"));
        assertEquals(disconnected ? 0 : 1, report.count(DdlExportStatus.SUCCESS));
        assertEquals(disconnected ? 1 : 0, report.unprocessed().size());
        if (disconnected) assertTrue(report.results().get(0).reason().contains("08006"));
        else {
            assertEquals(ddl + "\n@\n", Files.readString(report.results().get(1).outputPath()));
            assertEquals(1, report.count(DdlExportStatus.NOT_FOUND));
        }
        verify(broken).close();
        verify(rows).close();
        verify(prepared).close();
        verify(rows, times(disconnected ? 1 : 3)).next();
        verify(connection, times(disconnected ? 1 : 2)).prepareStatement(anyString());
        verify(connection, times(1)).isValid(5);
    }

    private ResultSet basicColumn() throws Exception {
        var c = mock(ResultSet.class);
        when(c.next()).thenReturn(true, false);
        when(c.getString("COLNAME")).thenReturn("a\"b");
        when(c.getInt("COLNO")).thenReturn(0);
        when(c.getString("TYPESCHEMA")).thenReturn("SYSIBM");
        when(c.getString("TYPENAME")).thenReturn("DECIMAL");
        when(c.getInt("LENGTH")).thenReturn(18);
        when(c.getInt("SCALE")).thenReturn(2);
        when(c.getString("NULLS")).thenReturn("N");
        when(c.getCharacterStream("DEFAULT")).thenReturn(new StringReader("0"));
        when(c.getString("GENERATED")).thenReturn(" ");
        when(c.getString("HIDDEN")).thenReturn(" ");
        for (String field :
                List.of(
                        "IDENTITY",
                        "ROWCHANGETIMESTAMP",
                        "ROWBEGIN",
                        "ROWEND",
                        "TRANSACTIONSTARTID")) when(c.getString(field)).thenReturn("N");
        return c;
    }

    @org.junit.jupiter.params.ParameterizedTest
    @EnumSource(
            value = DdlObjectType.class,
            names = {"PROCEDURE", "FUNCTION"})
    void laterDefinitionDisconnectRetainsEarlierSuccessAndIdentifiesIssueInSummary(
            DdlObjectType type) throws Exception {
        ResultSet rows = mock(ResultSet.class);
        when(rows.next()).thenReturn(true, true, false);
        when(rows.getString("SPECIFICNAME")).thenReturn("GOOD_SPEC", "BROKEN_SPEC");
        String ddl =
                "CREATE "
                        + type
                        + " RPT.X() "
                        + (type == DdlObjectType.PROCEDURE
                                ? "BEGIN END;"
                                : "RETURNS INT RETURN 1;");
        when(rows.getCharacterStream(type == DdlObjectType.PROCEDURE ? "TEXT" : "BODY"))
                .thenReturn(new StringReader(ddl))
                .thenThrow(new SQLException("lost secret", "08006", -4499));
        PreparedStatement prepared = statement(rows);
        when(connection.prepareStatement(anyString())).thenReturn(prepared);
        DdlExportSummary report =
                run(List.of(request(type, "X"), request(DdlObjectType.TABLE, "LATER")));
        assertEquals(2, report.matchedRoutineInstances());
        assertEquals(1, report.count(DdlExportStatus.SUCCESS));
        assertEquals(1, report.count(DdlExportStatus.FAILED));
        assertEquals("BROKEN_SPEC", report.results().get(1).specificName());
        assertEquals(ddl + "\n@\n", Files.readString(report.results().get(0).outputPath()));
        assertEquals(1, report.unprocessed().size());
        verify(rows, times(2)).next();
        verify(connection, times(1)).prepareStatement(anyString());
        Path logs = Files.createDirectory(dir.resolve("logs"));
        String previous = org.slf4j.MDC.get("database");
        org.slf4j.MDC.put("database", "qa");
        try (var runLogs =
                new com.example.db2toolkit.output.RunLogs(
                        logs, "qa", new com.example.db2toolkit.jdbc.SafeDiagnostics(config))) {
            new com.example.db2toolkit.ddl.DdlSummaryLogger()
                    .log(report, new com.example.db2toolkit.jdbc.SafeDiagnostics(config));
        } finally {
            if (previous == null) org.slf4j.MDC.remove("database");
            else org.slf4j.MDC.put("database", previous);
        }
        String issues = Files.readString(logs.resolve("qa-summary_issue.log"));
        String success = Files.readString(logs.resolve("qa-summary_success.log"));
        assertTrue(issues.contains("BROKEN_SPEC"));
        assertTrue(issues.contains("08006"));
        assertFalse(issues.contains("secret"));
        assertFalse(success.contains("BROKEN_SPEC"));
        assertTrue(success.contains("GOOD_SPEC"));
    }

    @Test
    void successfulQueriesDoNotPingTheDatabaseForEveryObject() throws Exception {
        ResultSet rows = mock(ResultSet.class);
        when(rows.next()).thenReturn(false);
        var prepared = statement(rows);
        when(connection.prepareStatement(anyString())).thenReturn(prepared);
        var report =
                run(
                        List.of(
                                request(DdlObjectType.PROCEDURE, "P1"),
                                request(DdlObjectType.PROCEDURE, "P2"),
                                request(DdlObjectType.FUNCTION, "F1")));
        assertEquals(3, report.count(DdlExportStatus.NOT_FOUND));
        verify(connection, times(1)).isValid(5);
        verify(connection, times(3)).prepareStatement(anyString());
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/ddl/TableFailureIsolationTest.java =====
UTF8-BYTES: 8476
SHA256: 024ad22152a2fa5fe2d9d4582184e139593dfc5da073b9ca214b4cc6ffcdf754
===== CONTENT =====
package com.example.db2toolkit.ddl;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.db2toolkit.config.AppConfig;
import com.example.db2toolkit.ddl.model.*;
import com.example.db2toolkit.model.*;
import com.example.db2toolkit.output.SqlFileWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

/**
 * Exercises table rejection through the service, including later routine requests and file
 * publication.
 */
class TableFailureIsolationTest {
    @TempDir Path directory;
    Connection connection;
    ResultSet table, columns, later;
    PreparedStatement tableQuery, columnQuery, laterQuery;
    AppConfig config;
    SqlFileWriter writer;

    @BeforeEach
    void setup() throws Exception {
        connection = mock(Connection.class);
        when(connection.isValid(5)).thenReturn(true);
        table = mock(ResultSet.class);
        when(table.next()).thenReturn(true, false);
        when(table.getString("TYPE")).thenReturn("T");
        when(table.getString("TEMPORALTYPE")).thenReturn("N");
        columns = mock(ResultSet.class);
        when(columns.next()).thenReturn(true, false);
        when(columns.getString("COLNAME")).thenReturn("ID");
        when(columns.getInt("COLNO")).thenReturn(0);
        when(columns.getString("TYPESCHEMA")).thenReturn("SYSIBM");
        when(columns.getString("TYPENAME")).thenReturn("INTEGER");
        when(columns.getInt("LENGTH")).thenReturn(4);
        when(columns.getString("NULLS")).thenReturn("N");
        for (String field :
                List.of(
                        "IDENTITY",
                        "ROWCHANGETIMESTAMP",
                        "ROWBEGIN",
                        "ROWEND",
                        "TRANSACTIONSTARTID")) when(columns.getString(field)).thenReturn("N");
        for (String field : List.of("GENERATED", "HIDDEN"))
            when(columns.getString(field)).thenReturn(" ");
        later = mock(ResultSet.class);
        when(later.next()).thenReturn(true, false);
        when(later.getString("SPECIFICNAME")).thenReturn("GOOD_SPEC");
        when(later.getCharacterStream("BODY"))
                .thenReturn(new StringReader("CREATE FUNCTION RPT.F() RETURNS INT RETURN 1;"));
        tableQuery = query(table);
        columnQuery = query(columns);
        laterQuery = query(later);
        when(connection.prepareStatement(anyString()))
                .thenReturn(tableQuery, columnQuery, laterQuery);
        config =
                new AppConfig(
                        "jdbc:db2://host/DB",
                        "user",
                        "secret",
                        Map.of(),
                        directory,
                        "PROCEDURES",
                        "FUNCTIONS",
                        "TABLES",
                        "COLUMNS");
        writer = new SqlFileWriter(directory);
    }

    @ParameterizedTest
    @CsvSource({
        "IDENTITY,Y",
        "ROWCHANGETIMESTAMP,Y",
        "ROWBEGIN,Y",
        "ROWEND,Y",
        "TRANSACTIONSTARTID,Y",
        "GENERATED,A",
        "HIDDEN,I",
        "NULLS,UNKNOWN"
    })
    void unsupportedColumnCannotPublishPartialTableOrStopLaterFunction(String field, String value)
            throws Exception {
        when(columns.getString(field)).thenReturn(value);
        assertRejectedAndContinued(run(), DdlExportStatus.DDL_UNAVAILABLE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"IDENTITY", "GENERATED", "NULLS"})
    void missingColumnAttributesAreNotAssumedToBeDefaults(String field) throws Exception {
        when(columns.getString(field)).thenReturn(null);
        assertRejectedAndContinued(run(), DdlExportStatus.DDL_UNAVAILABLE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"duplicate-name", "duplicate-number", "decreasing-number"})
    void ambiguousColumnSequenceIsNotPublished(String kind) throws Exception {
        when(columns.next()).thenReturn(true, true, false);
        when(columns.getString("COLNAME"))
                .thenReturn("ID", kind.equals("duplicate-name") ? "ID" : "NEXT_ID");
        if (kind.equals("duplicate-number")) when(columns.getInt("COLNO")).thenReturn(0, 0);
        else if (kind.equals("decreasing-number")) when(columns.getInt("COLNO")).thenReturn(1, 0);
        else when(columns.getInt("COLNO")).thenReturn(0, 1);
        assertRejectedAndContinued(run(), DdlExportStatus.DDL_UNAVAILABLE);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void secondColumnDefaultReadFailureNeverPublishesFirstColumnOnly(boolean disconnected)
            throws Exception {
        when(columns.next()).thenReturn(true, true, false);
        when(columns.getString("COLNAME")).thenReturn("ID", "NEXT_ID");
        when(columns.getInt("COLNO")).thenReturn(0, 1);
        Reader broken = mock(Reader.class);
        IOException failure = new IOException("default secret read failed");
        if (disconnected)
            failure.addSuppressed(new SQLRecoverableException("lost", "08006", -4499));
        when(broken.transferTo(any(Writer.class))).thenThrow(failure);
        when(columns.getCharacterStream("DEFAULT")).thenReturn(new StringReader("0"), broken);
        DdlExportSummary report = run();
        if (!disconnected) assertRejectedAndContinued(report, DdlExportStatus.FAILED);
        else {
            assertEquals(1, report.results().size());
            assertEquals(1, report.count(DdlExportStatus.FAILED));
            assertEquals(1, report.unprocessed().size());
            assertEquals(DdlObjectType.FUNCTION, report.unprocessed().get(0).type());
            assertTrue(report.results().get(0).reason().contains("08006"));
            verify(connection, times(2)).prepareStatement(anyString());
            verify(laterQuery, never()).executeQuery();
            assertNoTableFile();
        }
        assertTrue(report.results().get(0).reason().contains("default"));
        assertFalse(report.results().get(0).reason().contains("secret"));
        verify(broken).close();
        verify(columns).close();
        verify(columnQuery).close();
    }

    private DdlExportSummary run() {
        DdlExportSummary report = new DdlExportSummary();
        new DdlExportService()
                .export(
                        connection,
                        config,
                        List.of(
                                new DdlExportRequest(
                                        DdlObjectType.TABLE, new DbObjectRef("RPT", "T")),
                                new DdlExportRequest(
                                        DdlObjectType.FUNCTION, new DbObjectRef("RPT", "F"))),
                        writer,
                        report);
        assertEquals(1, report.exitCode());
        return report;
    }

    private void assertRejectedAndContinued(DdlExportSummary report, DdlExportStatus expected)
            throws Exception {
        assertEquals(2, report.results().size());
        assertEquals(expected, report.results().get(0).status());
        assertNull(report.results().get(0).outputPath());
        assertEquals(1, report.count(DdlExportStatus.SUCCESS));
        assertEquals(0, report.unprocessed().size());
        assertEquals(
                "CREATE FUNCTION RPT.F() RETURNS INT RETURN 1;\n@\n",
                Files.readString(report.results().get(1).outputPath()));
        verify(connection, times(3)).prepareStatement(anyString());
        verify(columns).close();
        verify(columnQuery).close();
        verify(later).close();
        verify(laterQuery).close();
        assertNoTableFile();
    }

    private void assertNoTableFile() throws IOException {
        try (var paths = Files.list(writer.runDirectory().resolve("tables"))) {
            assertEquals(0, paths.count());
        }
    }

    private PreparedStatement query(ResultSet rows) throws SQLException {
        var statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenReturn(rows);
        return statement;
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/ddl/model/DdlExportSummaryTest.java =====
UTF8-BYTES: 1527
SHA256: 0fb885a77f97f2267a566fe824008fe85e09ae97b1bd133908db22fb3fc29bca
===== CONTENT =====
package com.example.db2toolkit.ddl.model;


import static org.junit.jupiter.api.Assertions.*;

import com.example.db2toolkit.model.*;

import org.junit.jupiter.api.Test;

import java.util.List;

class DdlExportSummaryTest {
    @Test
    void reportSnapshotsCannotBeMutatedByConsumers() {
        var report = new DdlExportSummary();
        report.add(
                DdlExportResult.of(
                        DdlObjectType.TABLE,
                        new DbObjectRef("S", "T"),
                        null,
                        DdlExportStatus.NOT_FOUND,
                        null,
                        "missing"));
        assertThrows(UnsupportedOperationException.class, () -> report.results().clear());
        assertEquals(1, report.count(DdlExportStatus.NOT_FOUND));
    }

    @Test
    void connectionCloseFailureChangesExitCodeEvenWithNoRemainingRequests() {
        var report = new DdlExportSummary();
        report.stop("Connection close failed", List.of(), false);
        assertEquals(1, report.exitCode());
    }

    @Test
    void laterCleanupErrorDoesNotEraseInitialCauseOrDowngradeFatalExit() {
        var report = new DdlExportSummary();
        report.stop("Initial failure", List.of(), true);
        report.stop("Cleanup failure", List.of(), false);
        assertEquals(2, report.exitCode());
        assertTrue(report.stopReason().contains("Initial failure"));
        assertTrue(report.stopReason().contains("Cleanup failure"));
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/excel/ExcelConfigTest.java =====
UTF8-BYTES: 15431
SHA256: b06f2b01647ce8d334d134a16ea1aa2044cb9737b4384dbe6f8a46593dcc36da
===== CONTENT =====
package com.example.db2toolkit.excel;

import static org.junit.jupiter.api.Assertions.*;

import com.example.db2toolkit.config.ConfigurationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.*;
import java.util.*;

class ExcelConfigTest {
    @TempDir Path dir;

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(
            strings = {
                "\u0001", "\u000B", "\r", "\n", "\t", "\uFFFE", "\uFFFF", "\uD800", "\uDC00"
            })
    void sheetNamesRemainValidAndStableAfterWorkbookSerialization(String invalid) throws Exception {
        String name = ExcelNames.sheet("A" + invalid + "B");
        assertEquals("A_B", name);
        try (var book = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                var bytes = new java.io.ByteArrayOutputStream()) {
            book.createSheet(name);
            book.write(bytes);
            try (var read =
                    new org.apache.poi.xssf.usermodel.XSSFWorkbook(
                            new java.io.ByteArrayInputStream(bytes.toByteArray()))) {
                assertEquals(name, read.getSheetName(0));
            }
        }
    }

    @Test
    void escapedYamlControlNamesAreSanitizedBeforeCollisionAllocation() throws Exception {
        var config =
                load(
                        """
                        excel-export:
                          enabled: true
                          exports:
                            - {type: OBJECT, source: RPT.X, workbook: report, sheet: "A\\x01B"}
                            - {type: OBJECT, source: RPT.X, workbook: report, sheet: "A\\nB"}
                            - {type: OBJECT, source: RPT.X, workbook: report, sheet: A_B}
                        """);
        assertEquals(
                List.of("A_B", "A_B_2", "A_B_3"),
                config.tasks().stream().map(ExcelExportTask::sheet).toList());
        Path output = dir.resolve("names.xlsx");
        try (var writer = new ExcelWorkbookWriter(config.tasks(), 2)) {
            for (ExcelExportTask task : config.tasks())
                writer.write(
                        com.example.db2toolkit.support.JdbcRows.rows(
                                new String[] {"ID"}, new Object[][] {{1}, {2}}),
                        task,
                        n -> {});
            writer.publish(output, true);
        }
        try (var book = new org.apache.poi.xssf.usermodel.XSSFWorkbook(output.toFile())) {
            assertEquals(6, book.getNumberOfSheets());
            Set<String> names = new HashSet<>();
            for (var sheet : book) {
                assertTrue(names.add(sheet.getSheetName()));
                assertEquals("ID", sheet.getRow(0).getCell(0).getStringCellValue());
            }
        }
    }

    @Test
    void validSupplementarySheetCharactersRemainIntact() {
        String name = "A" + new String(Character.toChars(0x1F600)) + "B";
        assertEquals(name, ExcelNames.sheet(name));
    }

    private ExcelExportConfig load(String yaml) throws Exception {
        Path file = dir.resolve("excel.yaml");
        Files.writeString(file, yaml);
        return new ExcelConfigLoader().load(file);
    }

    @Test
    void simpleAndGroupedAndAdvancedCoexist() throws Exception {
        var config =
                load(
                        """
                        excel-export:
                          enabled: true
                          simple:
                            tables: [RPT.CUSTOMER]
                          workbooks:
                            - name: report.xlsx
                              objects: [RPT.CUSTOMER, RPT.ACCOUNT]
                          exports:
                            - name: active
                              type: SQL
                              source: SELECT CUSTOMER_ID AS CLIENT_ID FROM RPT.CUSTOMER
                              workbook: report.xlsx
                              sheet: ACTIVE
                              include-header: false
                              fetch-size: 2000
                              max-rows: 50
                        """);
        assertEquals(4, config.tasks().size());
        var simple = config.tasks().get(0);
        assertEquals(SourceType.OBJECT, simple.type());
        assertEquals("CUSTOMER.xlsx", simple.workbook());
        assertEquals("CUSTOMER", simple.sheet());
        assertEquals("SELECT * FROM RPT.CUSTOMER", simple.sql());
        assertTrue(simple.includeHeader());
        assertEquals("report.xlsx", config.tasks().get(1).workbook());
        assertEquals("ACCOUNT", config.tasks().get(2).sheet());
        var advanced = config.tasks().get(3);
        assertEquals("SELECT CUSTOMER_ID AS CLIENT_ID FROM RPT.CUSTOMER", advanced.sql());
        assertEquals(2000, advanced.fetchSize());
        assertEquals(50, advanced.maxRows());
        assertFalse(advanced.includeHeader());
        assertEquals(dir.resolve("output/excel"), config.output());
    }

    @Test
    void advancedObjectDefaultsAndDuplicateNames() throws Exception {
        var config =
                load(
                        """
excel-export:
  enabled: true
  include-header: false
  exports:
    - {name: one, type: OBJECT, source: RPT.CUSTOMER, workbook: report, sheet: DATA}
    - {name: two, type: OBJECT, source: RPT.ACCOUNT, workbook: report, sheet: DATA}
    - {name: three, type: OBJECT, source: RPT.TRADE, workbook: report, sheet: DATA}
""");
        assertEquals(
                List.of("DATA", "DATA_2", "DATA_3"),
                config.tasks().stream().map(ExcelExportTask::sheet).toList());
        assertEquals("report.xlsx", config.tasks().get(0).workbook());
        assertFalse(config.tasks().get(0).includeHeader());
    }

    @Test
    void enabledDefaultsToFalse() throws Exception {
        assertFalse(load("excel-export: {}\n").enabled());
    }

    @Test
    void unicodeSheetCollisionsAreNormalizedUsingPoisCaseComparison() throws Exception {
        var config =
                load(
                        """
                        excel-export:
                          enabled: true
                          exports:
                            - {type: OBJECT, source: RPT.X, workbook: report, sheet: I}
                            - {type: OBJECT, source: RPT.X, workbook: report, sheet: '\u0131'}
                            - {type: OBJECT, source: RPT.X, workbook: report, sheet: '\u0130'}
                            - {type: OBJECT, source: RPT.X, workbook: report, sheet: '\u03c3'}
                            - {type: OBJECT, source: RPT.X, workbook: report, sheet: '\u03c2'}
                        """);
        assertEquals(
                List.of("I", "\u0131_2", "\u0130_3", "\u03c3", "\u03c2_2"),
                config.tasks().stream().map(ExcelExportTask::sheet).toList());
        try (var book = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            for (ExcelExportTask task : config.tasks()) book.createSheet(task.sheet());
            assertEquals(5, book.getNumberOfSheets());
        }
    }

    @Test
    void invalidTaskConfigurationFailsBeforeQuery() throws Exception {
        for (String field :
                List.of(
                        "type: TABLE",
                        "source: ''",
                        "workbook: ''",
                        "sheet: ''",
                        "fetch-size: 0",
                        "max-rows: -1")) {
            String entry =
                    "name: bad\n"
                        + "type: OBJECT\n"
                        + "source: RPT.CUSTOMER\n"
                        + "workbook: report\n"
                        + "sheet: DATA\n";
            String key = field.substring(0, field.indexOf(':'));
            entry = entry.replaceAll("(?m)^" + key + ":.*\n", "") + field + "\n";
            String yaml =
                    "excel-export:\n  enabled: true\n  exports:\n    - "
                            + entry.strip().replace("\n", "\n      ")
                            + "\n";
            assertThrows(ConfigurationException.class, () -> load(yaml), field);
        }
    }

    @Test
    void unsafeSqlAndObjectSourcesRejected() {
        for (String source :
                List.of(
                        "RPT.CUSTOMER; DROP TABLE X",
                        "RPT.CUSTOMER--x",
                        "RPT.X/*x*/",
                        "RPT.X WHERE 1=1"))
            assertThrows(IllegalArgumentException.class, () -> ExcelSqlValidator.object(source));
        for (String sql :
                List.of(
                        "DELETE FROM X",
                        "/*comment*/ CALL X()",
                        "SELECT 1; DROP TABLE X",
                        "WITH X AS (SELECT * FROM FINAL TABLE (DELETE FROM X)) SELECT * FROM X",
                        "SELECT * FROM X FOR UPDATE",
                        "SELECT NEXT VALUE FOR SEQ FROM SYSIBM.SYSDUMMY1",
                        "SELECT 'unclosed"))
            assertThrows(IllegalArgumentException.class, () -> ExcelSqlValidator.select(sql), sql);
        String sql =
                "-- comment\n WITH X AS (SELECT 'DROP; --' AS TXT FROM RPT.X) SELECT TXT FROM X";
        assertEquals(sql, ExcelSqlValidator.select(sql));
        assertEquals(
                "SELECT \"DELETE\" FROM RPT.X".replace("\\", ""),
                ExcelSqlValidator.select("SELECT \"DELETE\" FROM RPT.X".replace("\\", "")));
    }

    @Test
    void namesAreSafeAndCollisionsDetected() {
        String safe = ExcelNames.sheet("A:/?*[]\\B" + "X".repeat(40));
        assertTrue(safe.length() <= 31);
        assertFalse(safe.matches(".*[:/\\\\?*\\[\\]].*"));
        Set<String> used = new HashSet<>();
        assertEquals("DATA", ExcelNames.uniqueSheet("DATA", used));
        assertEquals("data_2", ExcelNames.uniqueSheet("data", used));
        assertEquals("_CON.xlsx", ExcelNames.workbook("CON"));
        assertEquals("a_b.xlsx", ExcelNames.workbook("a/b"));
        assertEquals("a_b.xlsx", ExcelNames.workbook("a\\b"));
        assertThrows(
                ConfigurationException.class,
                () ->
                        new ExcelTaskNormalizer()
                                .normalize(
                                        Map.of(
                                                "workbooks",
                                                List.of(
                                                        Map.of(
                                                                "name",
                                                                "a/b",
                                                                "objects",
                                                                List.of("RPT.X")),
                                                        Map.of(
                                                                "name",
                                                                "a?b",
                                                                "objects",
                                                                List.of("RPT.Y"))))));
    }

    @Test
    void windowsSuperscriptDeviceWorkbooksArePrefixedWithoutDeviceIo() {
        for (String name :
                List.of(
                        "COM\u00B9",
                        "COM\u00B2",
                        "COM\u00B3",
                        "LPT\u00B9",
                        "LPT\u00B2",
                        "LPT\u00B3")) {
            for (String variant : List.of(name, name.toLowerCase(Locale.ROOT))) {
                assertEquals("_" + variant + ".xlsx", ExcelNames.workbook(variant));
                assertEquals("_" + variant + ".XLSX", ExcelNames.workbook(variant + ".XLSX"));
                assertEquals(
                        "_" + variant + ".report.xlsx",
                        ExcelNames.workbook(variant + ".report.xlsx"));
            }
        }
        for (String name : List.of("COM10", "LPT10")) {
            assertEquals(name + ".xlsx", ExcelNames.workbook(name));
            assertEquals(name + ".report.xlsx", ExcelNames.workbook(name + ".report.xlsx"));
        }
    }

    @Test
    void scalarReplaceIsAllowedButReplacementStatementsRemainRejected() {
        String sql = "SELECT REPLACE('ABC', 'A', 'X') FROM SYSIBM.SYSDUMMY1";
        assertEquals(sql, ExcelSqlValidator.select(sql));
        assertThrows(
                IllegalArgumentException.class,
                () -> ExcelSqlValidator.select("REPLACE INTO T VALUES (1)"));
        assertThrows(
                IllegalArgumentException.class,
                () -> ExcelSqlValidator.select("REPLACE (T) VALUES (1)"));
        assertThrows(
                IllegalArgumentException.class,
                () -> ExcelSqlValidator.select("SELECT 1; REPLACE INTO T VALUES (1)"));
        assertThrows(
                IllegalArgumentException.class,
                () -> ExcelSqlValidator.select("WITH X AS (SELECT 1 FROM T) DELETE FROM T"));
    }

    @Test
    void scalarReplaceAllowsCommentsBeforeItsOpeningParenthesis() {
        for (String trivia :
                List.of("/**/", " /* outer /* inner */ comment */ ", "-- note\n", "-- note\r\n")) {
            String sql = "SELECT REPLACE" + trivia + "('ABC', 'A', 'X') FROM SYSIBM.SYSDUMMY1";
            assertEquals(sql, ExcelSqlValidator.select(sql));
            assertThrows(
                    IllegalArgumentException.class,
                    () -> ExcelSqlValidator.select("REPLACE" + trivia + "INTO T VALUES (1)"));
            assertThrows(
                    IllegalArgumentException.class,
                    () -> ExcelSqlValidator.select("SELECT 1; REPLACE" + trivia + "('A','A','B')"));
        }
        assertThrows(
                IllegalArgumentException.class,
                () -> ExcelSqlValidator.select("SELECT REPLACE /* unterminated"));
    }

    @Test
    void unknownKeysAreRejectedInsteadOfDisablingRowLimits() throws Exception {
        assertThrows(ConfigurationException.class, () -> load("excel-export: {enable: true}"));
        assertThrows(
                ConfigurationException.class,
                () ->
                        load(
                                """
excel-export:
  enabled: true
  exports:
    - {name: limited, type: OBJECT, source: RPT.X, workbook: report, sheet: DATA, max-row: 5}
"""));
    }

    @Test
    void commentsAcceptCrLfAndCrOnlyLineEndings() {
        for (String newline : List.of("\r", "\r\n", "\n")) {
            String sql = "-- comment" + newline + "SELECT 1 FROM SYSIBM.SYSDUMMY1";
            assertEquals(sql, ExcelSqlValidator.select(sql));
        }
    }

    @Test
    void unicodeTruncationDoesNotExposeTrailingApostrophe() {
        String raw = "X".repeat(29) + "'" + new String(Character.toChars(0x1F600));
        String name = ExcelNames.sheet(raw);
        org.apache.poi.ss.util.WorkbookUtil.validateSheetName(name);
        assertFalse(name.endsWith("'"));
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/excel/ExcelNameAllocationTest.java =====
UTF8-BYTES: 2116
SHA256: 92129d63c71c268fe24e8ada3ccb7a50e7c3f2c23b70dc42bf38f1f682c3ed50
===== CONTENT =====
package com.example.db2toolkit.excel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class ExcelNameAllocationTest {
    @Test
    void repeatedNamesNeedLinearRatherThanQuadraticCollisionChecks() {
        class CountingSet extends HashSet<String> {
            int attempts;

            @Override
            public boolean add(String value) {
                attempts++;
                return super.add(value);
            }
        }
        var used = new CountingSet();
        Map<String, Integer> suffixes = new HashMap<>();
        for (int i = 1; i <= 6000; i++) {
            assertEquals(
                    i == 1 ? "CUSTOMER" : "CUSTOMER_" + i,
                    ExcelNames.uniqueSheet("CUSTOMER", used, suffixes));
        }
        assertTrue(used.attempts <= 12000, "Repeated names must not rescan all earlier suffixes");
    }

    @Test
    void cachedAllocationMatchesExistingNamesWithReservationsUnicodeAndTruncation() {
        Set<String> reference = new HashSet<>(), cached = new HashSet<>();
        for (String name : List.of("I_2", "DATA_2", "DATA_4", "A".repeat(29) + "_2")) {
            reference.add(ExcelNames.sheetKey(name));
            cached.add(ExcelNames.sheetKey(name));
        }
        Map<String, Integer> suffixes = new HashMap<>();
        List<String> inputs =
                List.of(
                        "I",
                        "\u0131",
                        "\u0130",
                        "DATA",
                        "data",
                        "DATA_2",
                        "A".repeat(30) + "X",
                        "A".repeat(30) + "Y",
                        "A/B",
                        "A?B");
        for (int i = 0; i < 300; i++) {
            String raw = inputs.get(i % inputs.size());
            assertEquals(
                    ExcelNames.uniqueSheet(raw, reference),
                    ExcelNames.uniqueSheet(raw, cached, suffixes));
        }
        assertEquals(reference, cached);
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/excel/ExcelServiceTest.java =====
UTF8-BYTES: 30421
SHA256: 5b71d686f18fe54ba5eac8d0ce3e8454c22b1c9c4c450b5a3c327b5371bb1d48
===== CONTENT =====
package com.example.db2toolkit.excel;

import static com.example.db2toolkit.support.JdbcRows.rows;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.db2toolkit.application.ToolkitApplication;
import com.example.db2toolkit.config.AppConfig;
import com.example.db2toolkit.jdbc.SafeDiagnostics;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.*;
import java.sql.*;
import java.util.*;

class ExcelServiceTest {
    @Test
    void directServiceCallCannotBypassReadOnlyCheck() throws Exception {
        Connection connection = mock(Connection.class);
        var task = new ExcelExportTask("write", SourceType.SQL,
                "UPDATE S.T SET C=1", "blocked.xlsx", "blocked", true, 1000, 0, true);
        var summary = new ExcelExportSummary();
        new ExcelExportService().export(connection,
                new ExcelExportConfig(true, dir, true, List.of(task)), errors(), summary);
        verifyNoInteractions(connection);
        assertFalse(Files.exists(dir.resolve("blocked.xlsx")));
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {"success", "query", "output"})
    void runtimeLogArgumentsAreRedactedBeforeReachingConsoleAppenders(String phase)
            throws Exception {
        var logger =
                (ch.qos.logback.classic.Logger)
                        org.slf4j.LoggerFactory.getLogger(ExcelExportService.class);
        var events =
                new ch.qos.logback.core.read.ListAppender<
                        ch.qos.logback.classic.spi.ILoggingEvent>();
        events.start();
        logger.addAppender(events);
        try {
            Connection connection = mock(Connection.class);
            PreparedStatement statement = mock(PreparedStatement.class);
            when(connection.prepareStatement(anyString(), anyInt(), anyInt()))
                    .thenReturn(statement);
            when(statement.executeQuery())
                    .thenAnswer(inv -> rows(new String[] {"ID"}, new Object[][] {{1}}));
            if (phase.equals("query"))
                when(statement.executeQuery())
                        .thenThrow(new SQLException("bad secret", "42703", -206));
            if (phase.equals("output")) {
                Files.createDirectory(dir.resolve("secret.xlsx"));
                Files.writeString(
                        dir.resolve("secret.xlsx/keep.txt"), "existing directory contents");
            }
            var task =
                    new ExcelExportTask(
                            "secret\nforged line",
                            SourceType.SQL,
                            "SELECT 1 FROM SYSIBM.SYSDUMMY1",
                            "secret.xlsx",
                            "secret",
                            true,
                            1000,
                            0,
                            true);
            var summary = new ExcelExportSummary();
            new ExcelExportService()
                    .export(
                            connection,
                            new ExcelExportConfig(true, dir, true, List.of(task)),
                            errors(),
                            summary);
            assertEquals(
                    1,
                    summary.count(
                            phase.equals("success")
                                    ? ExcelExportSummary.Status.SUCCESS
                                    : ExcelExportSummary.Status.FAILED));
            assertFalse(events.list.isEmpty());
            for (var event : events.list) {
                assertFalse(event.getFormattedMessage().contains("secret"));
                assertFalse(event.getFormattedMessage().contains("\n"));
            }
        } finally {
            logger.detachAppender(events);
            events.stop();
        }
    }

    @Test
    void rolloverLogArgumentsAreRedactedWithoutChangingSheetNames() throws Exception {
        var logger =
                (ch.qos.logback.classic.Logger)
                        org.slf4j.LoggerFactory.getLogger(ExcelWorkbookWriter.class);
        var events =
                new ch.qos.logback.core.read.ListAppender<
                        ch.qos.logback.classic.spi.ILoggingEvent>();
        events.start();
        logger.addAppender(events);
        var task = task("secret", true, "secret.xlsx");
        try (var writer = new ExcelWorkbookWriter(List.of(task), 2, errors())) {
            var sheets =
                    writer.write(
                            rows(new String[] {"ID"}, new Object[][] {{1}, {2}}), task, n -> {});
            assertEquals(
                    List.of("secret", "secret_2"),
                    sheets.stream().map(ExcelWorkbookWriter.SheetRows::name).toList());
            assertEquals(1, events.list.size());
            assertFalse(events.list.get(0).getFormattedMessage().contains("secret"));
        } finally {
            logger.detachAppender(events);
            events.stop();
        }
    }

    @TempDir Path dir;

    private SafeDiagnostics errors() {
        return new SafeDiagnostics(
                new AppConfig(
                        "jdbc:db2://localhost/DB",
                        "user",
                        "secret",
                        Map.of(),
                        dir,
                        "P",
                        "F",
                        "T",
                        "C"));
    }

    private ExcelExportTask task(String name, boolean enabled, String workbook) {
        return new ExcelExportTask(
                name,
                SourceType.SQL,
                "SELECT '" + name + "' FROM SYSIBM.SYSDUMMY1",
                workbook,
                name,
                enabled,
                1234,
                10,
                true);
    }

    @Test
    void failureAndDisabledTaskDoNotStopOtherSheetsAndWorkbooks() throws Exception {
        Connection connection = mock(Connection.class);
        when(connection.isValid(5)).thenReturn(true);
        List<ExcelExportTask> tasks =
                List.of(
                        task("ONE", true, "report.xlsx"),
                        task("BAD", true, "report.xlsx"),
                        task("THREE", true, "report.xlsx"),
                        task("OFF", false, "report.xlsx"),
                        task("FOUR", true, "other.xlsx"));
        List<PreparedStatement> statements = new ArrayList<>();
        for (ExcelExportTask task : tasks) {
            PreparedStatement statement = mock(PreparedStatement.class);
            when(connection.prepareStatement(
                            task.sql(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY))
                    .thenReturn(statement);
            if (task.name().equals("BAD"))
                when(statement.executeQuery())
                        .thenThrow(new SQLException("bad secret", "42703", -206));
            else
                when(statement.executeQuery())
                        .thenAnswer(
                                inv ->
                                        rows(
                                                new String[] {"ALIAS"},
                                                new Object[][] {{task.name()}}));
            statements.add(statement);
        }
        var summary = new ExcelExportSummary();
        new ExcelExportService()
                .export(
                        connection,
                        new ExcelExportConfig(true, dir, true, tasks),
                        errors(),
                        summary);
        assertEquals(3, summary.count(ExcelExportSummary.Status.SUCCESS));
        assertEquals(1, summary.count(ExcelExportSummary.Status.FAILED));
        assertEquals(1, summary.count(ExcelExportSummary.Status.SKIPPED));
        var failure =
                summary.results().stream()
                        .filter(r -> r.status() == ExcelExportSummary.Status.FAILED)
                        .findFirst()
                        .orElseThrow();
        assertEquals(-206, failure.sqlCode());
        assertEquals("42703", failure.sqlState());
        assertFalse(failure.reason().contains("secret"));
        verify(statements.get(3), never()).executeQuery();
        verify(statements.get(0)).setFetchSize(1234);
        verify(statements.get(0)).setMaxRows(10);
        verify(statements.get(0)).close();
        verify(connection, never()).close();
        try (var book = new XSSFWorkbook(dir.resolve("report.xlsx").toFile())) {
            assertEquals(2, book.getNumberOfSheets());
            assertEquals("ONE", book.getSheetName(0));
            assertEquals("THREE", book.getSheetName(1));
        }
        assertTrue(Files.exists(dir.resolve("other.xlsx")));
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {"query", "read", "close"})
    void connectionLossStopsLaterQueriesButPublishesEarlierSuccessfulSheets(String phase)
            throws Exception {
        Connection connection = mock(Connection.class);
        List<ExcelExportTask> tasks =
                List.of(
                        task("ONE", true, "report.xlsx"),
                        task("BAD", true, "report.xlsx"),
                        task("AFTER", true, "report.xlsx"),
                        task("OFF", false, "report.xlsx"),
                        task("LATER", true, "other.xlsx"));
        PreparedStatement good = mock(PreparedStatement.class), bad = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString(), anyInt(), anyInt())).thenReturn(good, bad);
        when(good.executeQuery())
                .thenAnswer(inv -> rows(new String[] {"ID"}, new Object[][] {{1}}));
        SQLException lost = new SQLNonTransientConnectionException("lost secret", "08006", -4499);
        if (phase.equals("query")) when(bad.executeQuery()).thenThrow(lost);
        else {
            ResultSet badRows = rows(new String[] {"ID"}, new Object[][] {{2}});
            if (phase.equals("read")) when(badRows.next()).thenReturn(true).thenThrow(lost);
            else doThrow(lost).when(badRows).close();
            when(bad.executeQuery()).thenReturn(badRows);
        }
        var summary = new ExcelExportSummary();
        new ExcelExportService()
                .export(
                        connection,
                        new ExcelExportConfig(true, dir, true, tasks),
                        errors(),
                        summary);
        assertEquals(1, summary.count(ExcelExportSummary.Status.SUCCESS));
        assertEquals(3, summary.count(ExcelExportSummary.Status.FAILED));
        assertEquals(1, summary.count(ExcelExportSummary.Status.SKIPPED));
        verify(connection, times(2)).prepareStatement(anyString(), anyInt(), anyInt());
        verify(connection, never()).close();
        for (String name : List.of("AFTER", "LATER")) {
            var result =
                    summary.results().stream()
                            .filter(r -> r.task().name().equals(name))
                            .findFirst()
                            .orElseThrow();
            assertTrue(result.reason().startsWith("Not executed: database connection lost"));
            assertFalse(result.reason().contains("secret"));
            assertEquals(-4499, result.sqlCode());
            assertEquals("08006", result.sqlState());
            assertEquals(0, result.elapsedMillis());
        }
        assertFalse(Files.exists(dir.resolve("other.xlsx")));
        try (var book = new XSSFWorkbook(dir.resolve("report.xlsx").toFile())) {
            assertEquals(1, book.getNumberOfSheets());
            assertEquals("ONE", book.getSheetName(0));
            assertEquals(1, book.getSheetAt(0).getRow(1).getCell(0).getNumericCellValue());
        }
    }

    @Test
    void ddlConnectionLossPreventsExcelQueriesAndNextDatabaseStillRuns() throws Exception {
        Files.writeString(dir.resolve("sp.txt"), "RPT.P");
        Files.writeString(dir.resolve("empty.txt"), "");
        Files.writeString(
                dir.resolve("excel.yaml"),
                """
                excel-export:
                  enabled: true
                  output-directory: excel
                  simple:
                    tables: [RPT.CUSTOMER]
                """);
        Path properties = dir.resolve("app.properties");
        Files.writeString(
                properties,
                """
                db.names=first,second
                db.first.url=jdbc:db2://localhost/DB
                db.first.username=first
                db.first.password=secret
                db.second.url=jdbc:db2://localhost/DB
                db.second.username=second
                db.second.password=secret
                export.procedure-list=sp.txt
                export.function-list=empty.txt
                export.table-list=empty.txt
                export.output-directory=ddl
                excel-export.config=excel.yaml
                """);
        List<Connection> opened = new java.util.concurrent.CopyOnWriteArrayList<>();
        int code =
                new ToolkitApplication(
                                key -> null,
                                config -> {
                                    Connection connection = mock(Connection.class);
                                    when(connection.isValid(5)).thenReturn(true);
                                    PreparedStatement ddl = mock(PreparedStatement.class);
                                    when(connection.prepareStatement(anyString())).thenReturn(ddl);
                                    if (config.username().equals("first"))
                                        when(ddl.executeQuery())
                                                .thenThrow(
                                                        new SQLException(
                                                                "lost secret", "08006", -4499));
                                    else {
                                        ResultSet routine = mock(ResultSet.class);
                                        when(routine.next()).thenReturn(true, false);
                                        when(routine.getString("SPECIFICNAME")).thenReturn("P1");
                                        when(routine.getCharacterStream("TEXT"))
                                                .thenReturn(
                                                        new java.io.StringReader(
                                                                "CREATE PROCEDURE RPT.P() BEGIN"
                                                                    + " END;"));
                                        when(ddl.executeQuery()).thenReturn(routine);
                                        PreparedStatement excel = mock(PreparedStatement.class);
                                        when(connection.prepareStatement(
                                                        anyString(), anyInt(), anyInt()))
                                                .thenReturn(excel);
                                        when(excel.executeQuery())
                                                .thenAnswer(
                                                        inv ->
                                                                rows(
                                                                        new String[] {"ID"},
                                                                        new Object[][] {{1}}));
                                    }
                                    when(connection.getClientInfo("test-alias")).thenReturn(config.username());
                                    opened.add(connection);
                                    return connection;
                                })
                        .run(new String[] {properties.toString()});
        assertEquals(1, code);
        verify(opened.stream().filter(c -> { try { return "first".equals(c.getClientInfo("test-alias")); } catch (SQLException e) { throw new RuntimeException(e); } }).findFirst().orElseThrow(), never()).prepareStatement(anyString(), anyInt(), anyInt());
        verify(opened.stream().filter(c -> { try { return "second".equals(c.getClientInfo("test-alias")); } catch (SQLException e) { throw new RuntimeException(e); } }).findFirst().orElseThrow(), times(1)).prepareStatement(anyString(), anyInt(), anyInt());
        for (Connection connection : opened) verify(connection).close();
        assertTrue(Files.exists(dir.resolve("excel/db-second/second-CUSTOMER.xlsx")));
        try (var paths = Files.walk(dir.resolve("ddl/db-first"))) {
            Path issue =
                    paths.filter(p -> p.getFileName().toString().endsWith("-summary_issue.log"))
                            .findFirst()
                            .orElseThrow();
            String log = Files.readString(issue);
            assertTrue(
                    log.contains("Not executed: database connection unavailable after DDL export"));
            assertTrue(log.contains("Database run finished: exitCode=1"));
            assertFalse(log.contains("secret"));
        }
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(booleans = {false, true})
    void outputFailureMarksAllWorkbookTasksAndContinuesOtherWorkbook(boolean overwrite)
            throws Exception {
        Files.createDirectory(dir.resolve("blocked.xlsx"));
        Files.writeString(dir.resolve("blocked.xlsx/keep.txt"), "unrelated contents");
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString(), anyInt(), anyInt())).thenReturn(statement);
        when(statement.executeQuery())
                .thenAnswer(inv -> rows(new String[] {"ID"}, new Object[][] {{1}}));
        var tasks =
                List.of(
                        task("ONE", true, "blocked.xlsx"),
                        task("TWO", true, "blocked.xlsx"),
                        task("THREE", true, "good.xlsx"));
        var summary = new ExcelExportSummary();
        new ExcelExportService()
                .export(
                        connection,
                        new ExcelExportConfig(true, dir, overwrite, tasks),
                        errors(),
                        summary);
        assertEquals(2, summary.count(ExcelExportSummary.Status.FAILED));
        assertEquals(1, summary.count(ExcelExportSummary.Status.SUCCESS));
        assertTrue(Files.exists(dir.resolve("good.xlsx")));
        verify(connection, times(1)).prepareStatement(anyString(), anyInt(), anyInt());
        verify(connection)
                .prepareStatement(
                        tasks.get(2).sql(),
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY);
        assertEquals("unrelated contents", Files.readString(dir.resolve("blocked.xlsx/keep.txt")));
    }

    @Test
    void invalidOutputParentDoesNotReadAnyDatabaseRows() throws Exception {
        Path blocked = dir.resolve("not-a-directory");
        Files.writeString(blocked, "existing file");
        Connection connection = mock(Connection.class);
        var tasks = List.of(task("ONE", true, "one.xlsx"), task("TWO", true, "two.xlsx"));
        var summary = new ExcelExportSummary();
        new ExcelExportService()
                .export(
                        connection,
                        new ExcelExportConfig(true, blocked.resolve("child"), true, tasks),
                        errors(),
                        summary);
        assertEquals(2, summary.count(ExcelExportSummary.Status.FAILED));
        verifyNoInteractions(connection);
        assertEquals("existing file", Files.readString(blocked));
    }

    @Test
    void bootstrapExcelOnlyReusesConfigurationAndOneConnectionPerDatabase() throws Exception {
        Files.writeString(
                dir.resolve("excel.yaml"),
                """
                excel-export:
                  enabled: true
                  output-directory: excel
                  simple:
                    tables: [RPT.CUSTOMER]
                """);
        Path properties = dir.resolve("app.properties");
        Files.writeString(
                properties,
                """
                db.names=first,second
                db.first.url=jdbc:db2://localhost/DB
                db.first.username=user
                db.first.password-env=PASSWORD
                db.second.url=jdbc:db2://localhost/DB
                db.second.username=user
                db.second.password-env=PASSWORD
                export.enabled=false
                excel-export.config=excel.yaml
                """);
        List<Connection> opened = new java.util.concurrent.CopyOnWriteArrayList<>();
        int code =
                new ToolkitApplication(
                                key -> "secret",
                                config -> {
                                    Connection connection = mock(Connection.class);
                                    PreparedStatement statement = mock(PreparedStatement.class);
                                    when(connection.prepareStatement(
                                                    "SELECT * FROM RPT.CUSTOMER",
                                                    ResultSet.TYPE_FORWARD_ONLY,
                                                    ResultSet.CONCUR_READ_ONLY))
                                            .thenReturn(statement);
                                    try {
                                        when(statement.executeQuery())
                                                .thenAnswer(
                                                        inv ->
                                                                rows(
                                                                        new String[] {"ID"},
                                                                        new Object[][] {{1}}));
                                    } catch (Exception e) {
                                        throw new SQLException(e);
                                    }
                                    opened.add(connection);
                                    return connection;
                                })
                        .run(new String[] {properties.toString()});
        assertEquals(0, code);
        assertEquals(2, opened.size());
        for (Connection connection : opened) verify(connection).close();
        for (String name : List.of("first", "second")) {
            Path output = dir.resolve("excel/db-" + name);
            assertTrue(Files.exists(output.resolve(name + "-CUSTOMER.xlsx")));
            try (var paths = Files.walk(output)) {
                Path summary =
                        paths.filter(p -> p.getFileName().toString().endsWith("-summary_issue.log"))
                                .findFirst()
                                .orElseThrow();
                String log = Files.readString(summary);
                assertTrue(log.contains("Excel Export Summary"));
                assertTrue(log.contains("Successful tasks: 1"));
                assertTrue(log.contains("[db=" + name + "]"));
            }
        }
    }

    @Test
    void excelOnlyConnectionCloseFailureAppearsInSummaryWithFinalExitCode() throws Exception {
        Files.writeString(
                dir.resolve("excel.yaml"),
                """
                excel-export:
                  enabled: true
                  output-directory: excel
                  simple:
                    tables: [RPT.CUSTOMER]
                """);
        Path config = dir.resolve("app.properties");
        Files.writeString(
                config,
                """
                db.name=TESTDB
                db.url=jdbc:db2://localhost/DB
                db.username=user
                db.password-env=PASSWORD
                export.enabled=false
                excel-export.config=excel.yaml
                """);
        int code =
                new ToolkitApplication(
                                key -> "secret",
                                target -> {
                                    Connection connection = mock(Connection.class);
                                    PreparedStatement statement = mock(PreparedStatement.class);
                                    when(connection.prepareStatement(
                                                    anyString(), anyInt(), anyInt()))
                                            .thenReturn(statement);
                                    when(statement.executeQuery())
                                            .thenAnswer(
                                                    inv ->
                                                            rows(
                                                                    new String[] {"ID"},
                                                                    new Object[][] {{1}}));
                                    doThrow(new SQLException("close failed secret", "08006", -4499))
                                            .when(connection)
                                            .close();
                                    return connection;
                                })
                        .run(new String[] {config.toString()});
        assertEquals(1, code);
        try (var paths = Files.walk(dir.resolve("excel"))) {
            Path summary =
                    paths.filter(p -> p.getFileName().toString().endsWith("-summary_issue.log"))
                            .findFirst()
                            .orElseThrow();
            String text = Files.readString(summary);
            assertTrue(text.contains("Connection close failed"));
            assertTrue(text.contains("code=-4499"));
            assertTrue(text.contains("Database run finished: exitCode=1 elapsedMs="));
            assertFalse(text.contains("secret"));
            String process = Files.readString(summary.resolveSibling(summary.getFileName().toString().replace("summary_issue.log", "process.log")));
            assertTrue(process.contains("ddlEnabled=false excelEnabled=true"));
            assertTrue(process.contains("fetchSize=1000 maxRows=0 includeHeader=true"));
            assertTrue(process.contains("Writing Excel workbook:"));
            assertTrue(process.contains("Excel workbook write completed:"));
        }
    }

    @Test
    void unavailableRemainingTasksPreservesAlreadyPublishedResults() {
        var first = task("ONE", true, "report.xlsx");
        var second = task("TWO", true, "other.xlsx");
        var summary = new ExcelExportSummary();
        summary.add(
                new ExcelExportSummary.Result(
                        first,
                        ExcelExportSummary.Status.SUCCESS,
                        List.of(),
                        3,
                        1,
                        null,
                        null,
                        null));
        var config = new ExcelExportConfig(true, dir, true, List.of(first, second));
        summary.unavailable(config, "unexpected failure");
        summary.unavailable(config, "cleanup failure");
        assertEquals(2, summary.results().size());
        assertEquals(1, summary.count(ExcelExportSummary.Status.SUCCESS));
        assertEquals(1, summary.count(ExcelExportSummary.Status.FAILED));
        assertEquals(3, summary.results().get(0).rows());
    }

    @Test
    void textFailureWithSuppressedDisconnectStopsQueriesAndRetainsBothReasons() throws Exception {
        Connection connection = mock(Connection.class);
        when(connection.isValid(5)).thenReturn(true);
        PreparedStatement good = mock(PreparedStatement.class), bad = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString(), anyInt(), anyInt()))
                .thenReturn(good, bad, good);
        when(good.executeQuery())
                .thenAnswer(inv -> rows(new String[] {"ID"}, new Object[][] {{1}}));
        ResultSet badRows = rows(new String[] {"TEXT"}, new Object[][] {{"x".repeat(32768)}});
        doThrow(new SQLNonTransientConnectionException("close secret failed", "08006", -4499))
                .when(badRows)
                .close();
        when(bad.executeQuery()).thenReturn(badRows);
        var tasks =
                List.of(
                        task("ONE", true, "report.xlsx"),
                        task("BAD", true, "report.xlsx"),
                        task("AFTER", true, "other.xlsx"));
        var summary = new ExcelExportSummary();
        new ExcelExportService()
                .export(
                        connection,
                        new ExcelExportConfig(true, dir, true, tasks),
                        errors(),
                        summary);
        assertEquals(1, summary.count(ExcelExportSummary.Status.SUCCESS));
        assertEquals(2, summary.count(ExcelExportSummary.Status.FAILED));
        verify(connection, times(2)).prepareStatement(anyString(), anyInt(), anyInt());
        var failed =
                summary.results().stream()
                        .filter(r -> r.task().name().equals("BAD"))
                        .findFirst()
                        .orElseThrow();
        assertTrue(failed.reason().contains("32767-character limit"));
        assertTrue(failed.reason().contains("08006"));
        assertFalse(failed.reason().contains("secret"));
        var after =
                summary.results().stream()
                        .filter(r -> r.task().name().equals("AFTER"))
                        .findFirst()
                        .orElseThrow();
        assertTrue(after.reason().startsWith("Not executed:"));
        assertEquals(-4499, after.sqlCode());
        assertFalse(Files.exists(dir.resolve("other.xlsx")));
        try (var book = new XSSFWorkbook(dir.resolve("report.xlsx").toFile())) {
            assertEquals(1, book.getNumberOfSheets());
        }
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/excel/ExcelSheetCleanupTest.java =====
UTF8-BYTES: 5371
SHA256: 37d5931998bd4a41e7d996c8f9278813904e028432a47555b89fed3f74947f72
===== CONTENT =====
package com.example.db2toolkit.excel;


import static com.example.db2toolkit.support.JdbcRows.rows;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.db2toolkit.config.AppConfig;
import com.example.db2toolkit.jdbc.SafeDiagnostics;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.*;
import java.sql.*;
import java.util.*;

class ExcelSheetCleanupTest {
    @TempDir Path dir;

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void failedSheetRemovalDiscardsWorkbookButPreservesQueryAndConnectionState(boolean disconnected)
            throws Exception {
        var brokenBook =
                spy(com.example.db2toolkit.spreadsheet.StreamingWorkbooks.streamingWorkbook());
        doThrow(new IllegalStateException("sheet cleanup secret failed"))
                .when(brokenBook)
                .removeSheetAt(anyInt());
        Connection connection = mock(Connection.class);
        when(connection.isValid(5)).thenReturn(true);
        PreparedStatement good = mock(PreparedStatement.class), bad = mock(PreparedStatement.class);
        when(good.executeQuery())
                .thenAnswer(inv -> rows(new String[] {"ID"}, new Object[][] {{1}}));
        ResultSet brokenRows = rows(new String[] {"ID"}, new Object[0][]);
        when(brokenRows.next())
                .thenThrow(
                        new SQLException(
                                "primary query secret failed",
                                disconnected ? "08006" : "42703",
                                disconnected ? -4499 : -206));
        when(bad.executeQuery()).thenReturn(brokenRows);
        when(connection.prepareStatement(anyString(), anyInt(), anyInt()))
                .thenReturn(good, bad, good);
        var tasks =
                List.of(
                        task("ONE", "report.xlsx"),
                        task("BAD", "report.xlsx"),
                        task("REST", "report.xlsx"),
                        task("LATER", "later.xlsx"));
        var errors =
                new SafeDiagnostics(
                        new AppConfig(
                                "jdbc:db2://host/DB",
                                "u",
                                "secret",
                                Map.of(),
                                dir,
                                "P",
                                "F",
                                "T",
                                "C"));
        var summary = new ExcelExportSummary();
        boolean[] first = {true};
        try (var mocked =
                mockStatic(
                        com.example.db2toolkit.spreadsheet.StreamingWorkbooks.class,
                        invocation -> {
                            if (invocation.getMethod().getName().equals("streamingWorkbook")
                                    && first[0]) {
                                first[0] = false;
                                return brokenBook;
                            }
                            return invocation.callRealMethod();
                        })) {
            new ExcelExportService()
                    .export(
                            connection,
                            new ExcelExportConfig(true, dir, true, tasks),
                            errors,
                            summary);
        }
        assertEquals(4, summary.results().size());
        assertEquals(disconnected ? 4 : 3, summary.count(ExcelExportSummary.Status.FAILED));
        var failed =
                summary.results().stream()
                        .filter(r -> r.task().name().equals("BAD"))
                        .findFirst()
                        .orElseThrow();
        assertTrue(failed.reason().contains("primary query"));
        assertTrue(failed.reason().contains("sheet cleanup"));
        assertFalse(failed.reason().contains("secret"));
        assertEquals(disconnected ? -4499 : -206, failed.sqlCode());
        assertEquals(disconnected ? "08006" : "42703", failed.sqlState());
        verify(brokenRows).close();
        verify(bad).close();
        verify(connection, times(disconnected ? 2 : 3))
                .prepareStatement(anyString(), anyInt(), anyInt());
        assertFalse(Files.exists(dir.resolve("report.xlsx")));
        if (disconnected) assertFalse(Files.exists(dir.resolve("later.xlsx")));
        else
            try (var book = new XSSFWorkbook(dir.resolve("later.xlsx").toFile())) {
                assertEquals(1, book.getSheetAt(0).getRow(1).getCell(0).getNumericCellValue());
            }
        verify(brokenBook, never()).write(any(java.io.OutputStream.class));
        verify(brokenBook).close();
    }

    private ExcelExportTask task(String name, String workbook) {
        return new ExcelExportTask(
                name,
                SourceType.SQL,
                "SELECT 1 FROM SYSIBM.SYSDUMMY1",
                workbook,
                name,
                true,
                1000,
                0,
                true);
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/excel/ExcelWorkbookWriterTest.java =====
UTF8-BYTES: 16737
SHA256: 83f5c31c186d7b54c0f58ffcace174e7e248f55320f6b9adbd2c6b5a702c4daf
===== CONTENT =====
package com.example.db2toolkit.excel;

import static com.example.db2toolkit.support.JdbcRows.rows;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.db2toolkit.spreadsheet.ExcelValueWriter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

class ExcelWorkbookWriterTest {
    @TempDir Path dir;

    @Test
    void completedSheetsReleaseRowsBeforeWorkbookPublication() throws Exception {
        var task = task("DATA", true);
        Path output = dir.resolve("flushed.xlsx");
        try (var writer = new ExcelWorkbookWriter(List.of(task), 4)) {
            var field = ExcelWorkbookWriter.class.getDeclaredField("book");
            field.setAccessible(true);
            var book = (org.apache.poi.xssf.streaming.SXSSFWorkbook) field.get(writer);
            writer.write(rows(new String[] {"ID"}, new Object[][] {{1}, {2}, {3}, {4}, {5}, {6}, {7}}),
                task, n -> {
                    if (n == 4) assertTrue(book.getSheetAt(0).areAllRowsFlushed(),
                        "Rollover must release the completed sheet's row window");
                });
            for (int i = 0; i < book.getNumberOfSheets(); i++)
                assertTrue(book.getSheetAt(i).areAllRowsFlushed());
            writer.publish(output, false);
        }
        try (var book = new XSSFWorkbook(output.toFile())) {
            assertEquals(3, book.getNumberOfSheets());
            int expected = 1;
            for (Sheet sheet : book) {
                assertEquals("ID", sheet.getRow(0).getCell(0).getStringCellValue());
                for (int i = 1; i <= sheet.getLastRowNum(); i++)
                    assertEquals(expected++, sheet.getRow(i).getCell(0).getNumericCellValue());
            }
            assertEquals(8, expected);
        }
    }

    @Test
    void publicationRechecksDestinationAndPreservesAnExistingDirectory() throws Exception {
        ExcelExportTask task = task("DATA", true);
        Path output = dir.resolve("changed.xlsx");
        ExcelWorkbookWriter.validateOutput(output, true);
        try (var writer = new ExcelWorkbookWriter(List.of(task))) {
            writer.write(rows(new String[] {"ID"}, new Object[][] {{7}}), task, n -> {});
            // Another process can change the destination after the pre-query check.
            Files.createDirectory(output);
            assertThrows(java.io.IOException.class, () -> writer.publish(output, true));
            assertTrue(Files.isDirectory(output));
            writer.publish(dir.resolve("valid.xlsx"), true);
        }
        try (var book = new XSSFWorkbook(dir.resolve("valid.xlsx").toFile())) {
            assertEquals(7, book.getSheetAt(0).getRow(1).getCell(0).getNumericCellValue());
        }
        try (var paths = Files.list(dir)) {
            assertEquals(2, paths.count());
        }
    }

    @Test
    void subnormalNumbersArePublishedAsExactTextInsteadOfUnsupportedExcelNumbers()
            throws Exception {
        ExcelExportTask task = task("SMALL", true);
        BigDecimal decimal = new BigDecimal("1E-310");
        Path output = dir.resolve("small.xlsx");
        try (var writer = new ExcelWorkbookWriter(List.of(task))) {
            writer.write(
                    rows(
                            new String[] {"POS", "NEG", "DEC", "NORMAL", "ZERO"},
                            new Object[][] {{1E-310d, -1E-310d, decimal, 1E-300d, 0d}}),
                    task,
                    n -> {});
            writer.publish(output, true);
        }
        try (var book = new XSSFWorkbook(output.toFile())) {
            Row row = book.getSheetAt(0).getRow(1);
            assertEquals("1.0E-310", row.getCell(0).getStringCellValue());
            assertEquals("-1.0E-310", row.getCell(1).getStringCellValue());
            assertEquals(decimal.toPlainString(), row.getCell(2).getStringCellValue());
            assertEquals(1E-300d, row.getCell(3).getNumericCellValue());
            assertEquals(0d, row.getCell(4).getNumericCellValue());
        }
    }

    static ExcelExportTask task(String name, boolean header) {
        return new ExcelExportTask(
                name,
                SourceType.SQL,
                "SELECT * FROM RPT.X",
                "report.xlsx",
                name,
                true,
                1000,
                0,
                header);
    }

    @Test
    void metadataLabelsHeaderNullAndNumericPrecision() throws Exception {
        ExcelExportTask task = task("DATA", true);
        ResultSet rs =
                rows(
                        new String[] {
                            "CLIENT_ID", "CUSTOMER_NAME", "STATUS", "DECIMAL", "COUNT", "FLAG"
                        },
                        new Object[][] {
                            {
                                1234567890123456789L,
                                "=not-a-formula",
                                null,
                                new BigDecimal("1234567890.123456789"),
                                12,
                                true
                            }
                        });
        Path output = dir.resolve("report.xlsx");
        try (var writer = new ExcelWorkbookWriter(List.of(task))) {
            writer.write(rs, task, n -> {});
            writer.publish(output, true);
        }
        try (var book = new XSSFWorkbook(output.toFile())) {
            Sheet sheet = book.getSheetAt(0);
            assertEquals("CLIENT_ID", sheet.getRow(0).getCell(0).getStringCellValue());
            assertEquals("CUSTOMER_NAME", sheet.getRow(0).getCell(1).getStringCellValue());
            assertEquals("STATUS", sheet.getRow(0).getCell(2).getStringCellValue());
            Row row = sheet.getRow(1);
            assertEquals("1234567890123456789", row.getCell(0).getStringCellValue());
            assertEquals(CellType.STRING, row.getCell(1).getCellType());
            assertEquals(CellType.BLANK, row.getCell(2).getCellType());
            assertEquals("1234567890.123456789", row.getCell(3).getStringCellValue());
            assertEquals(12, row.getCell(4).getNumericCellValue());
            assertTrue(row.getCell(5).getBooleanCellValue());
        }
        verify(rs.getMetaData(), never()).getColumnName(anyInt());
    }

    @Test
    void rolloverRepeatsHeaderAndAvoidsReservedTaskName() throws Exception {
        ExcelExportTask task = task("DATA", true);
        Path output = dir.resolve("report.xlsx");
        try (var writer = new ExcelWorkbookWriter(List.of(task, task("DATA_2", true)), 3)) {
            var counts =
                    writer.write(
                            rows(new String[] {"ID"}, new Object[][] {{1}, {2}, {3}, {4}, {5}}),
                            task,
                            n -> {});
            assertEquals(
                    List.of("DATA", "DATA_3", "DATA_4"),
                    counts.stream().map(ExcelWorkbookWriter.SheetRows::name).toList());
            writer.publish(output, true);
        }
        try (var book = new XSSFWorkbook(output.toFile())) {
            assertEquals(3, book.getNumberOfSheets());
            for (Sheet sheet : book)
                assertEquals("ID", sheet.getRow(0).getCell(0).getStringCellValue());
            assertEquals(5, book.getSheetAt(2).getRow(1).getCell(0).getNumericCellValue());
        }
    }

    @Test
    void headerDisabledAndExactBoundaryNoExtraSheet() throws Exception {
        ExcelExportTask task = task("DATA", false);
        Path output = dir.resolve("report.xlsx");
        try (var writer = new ExcelWorkbookWriter(List.of(task), 2)) {
            writer.write(
                    rows(new String[] {"ID"}, new Object[][] {{1}, {2}, {3}, {4}}), task, n -> {});
            writer.publish(output, true);
        }
        try (var book = new XSSFWorkbook(output.toFile())) {
            assertEquals(2, book.getNumberOfSheets());
            assertEquals("DATA_2", book.getSheetName(1));
            assertEquals(1, book.getSheetAt(0).getRow(0).getCell(0).getNumericCellValue());
            assertEquals(3, book.getSheetAt(1).getRow(0).getCell(0).getNumericCellValue());
        }
    }

    @Test
    void rolloverRespectsUnicodeEquivalentReservedSheetNames() throws Exception {
        ExcelExportTask first = task("I", true);
        ExcelExportTask later = task("\u0131_2", true);
        Path output = dir.resolve("unicode.xlsx");
        try (var writer = new ExcelWorkbookWriter(List.of(first, later), 2)) {
            var sheets =
                    writer.write(
                            rows(new String[] {"ID"}, new Object[][] {{1}, {2}}), first, n -> {});
            assertEquals(
                    List.of("I", "I_3"),
                    sheets.stream().map(ExcelWorkbookWriter.SheetRows::name).toList());
            writer.write(rows(new String[] {"ID"}, new Object[][] {{3}}), later, n -> {});
            writer.publish(output, true);
        }
        try (var book = new XSSFWorkbook(output.toFile())) {
            assertEquals(3, book.getNumberOfSheets());
            assertEquals("\u0131_2", book.getSheetName(2));
            assertEquals(3, book.getSheetAt(2).getRow(1).getCell(0).getNumericCellValue());
        }
    }

    @Test
    void zeroRowsStillHasHeader() throws Exception {
        ExcelExportTask task = task("EMPTY", true);
        Path output = dir.resolve("report.xlsx");
        try (var writer = new ExcelWorkbookWriter(List.of(task))) {
            assertEquals(
                    0,
                    writer.write(rows(new String[] {"ID"}, new Object[0][]), task, n -> {})
                            .get(0)
                            .rows());
            writer.publish(output, true);
        }
        try (var book = new XSSFWorkbook(output.toFile())) {
            assertEquals("ID", book.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
        }
    }

    @Test
    void datesTimesClobAndDecimalAreTypedAndLongTextFailsWithoutTruncation() throws Exception {
        try (var book = new XSSFWorkbook()) {
            var writer = new ExcelValueWriter(book);
            Row row = book.createSheet().createRow(0);
            writer.write(row.createCell(0), java.sql.Date.valueOf("2024-01-02"));
            writer.write(row.createCell(1), Timestamp.valueOf("2024-01-02 12:30:00.123"));
            writer.write(row.createCell(2), Time.valueOf("12:30:00"));
            writer.write(row.createCell(3), new BigDecimal("1234.56"));
            writer.write(row.createCell(4), Timestamp.valueOf("2024-01-02 12:30:00.123456"));
            Clob clob = mock(Clob.class);
            when(clob.getCharacterStream()).thenReturn(new java.io.StringReader("hello"));
            writer.write(row.createCell(5), clob);
            verify(clob).free();
            assertTrue(DateUtil.isCellDateFormatted(row.getCell(0)));
            assertTrue(DateUtil.isCellDateFormatted(row.getCell(1)));
            assertEquals(CellType.NUMERIC, row.getCell(2).getCellType());
            assertEquals(1234.56, row.getCell(3).getNumericCellValue());
            assertEquals(CellType.STRING, row.getCell(4).getCellType());
            assertEquals("hello", row.getCell(5).getStringCellValue());
            assertThrows(
                    java.io.IOException.class,
                    () -> writer.write(row.createCell(6), "x".repeat(32768)));
        }
    }

    @Test
    void failedTaskRemovesPartialSheetsAndOverwriteFalsePreservesFile() throws Exception {
        ExcelExportTask task = task("DATA", true);
        Path output = dir.resolve("report.xlsx");
        Files.writeString(output, "existing");
        try (var writer = new ExcelWorkbookWriter(List.of(task), 2)) {
            assertThrows(
                    java.io.IOException.class,
                    () ->
                            writer.write(
                                    rows(
                                            new String[] {"V"},
                                            new Object[][] {{"good"}, {"x".repeat(32768)}}),
                                    task,
                                    n -> {}));
            assertEquals(0, writer.sheetCount());
            writer.write(rows(new String[] {"V"}, new Object[][] {{"good"}}), task, n -> {});
            assertThrows(FileAlreadyExistsException.class, () -> writer.publish(output, false));
        }
        assertEquals("existing", Files.readString(output));
        try (var paths = Files.list(dir)) {
            assertEquals(1, paths.count());
        }
    }

    @Test
    void streamingFlushesRowsAndCleansTemporaryFilesIncludingFailedSheets() throws Exception {
        Path temp = Files.createDirectory(dir.resolve("poi-temp"));
        org.apache.poi.util.TempFile.setTempFileCreationStrategy(
                new org.apache.poi.util.DefaultTempFileCreationStrategy(temp.toFile()));
        ExcelExportTask task = task("STREAM", true);
        Path output = dir.resolve("stream.xlsx");
        try {
            try (var writer = new ExcelWorkbookWriter(List.of(task))) {
                ResultSet rs = mock(ResultSet.class);
                ResultSetMetaData metadata = mock(ResultSetMetaData.class);
                when(rs.getMetaData()).thenReturn(metadata);
                when(metadata.getColumnCount()).thenReturn(1);
                when(metadata.getColumnLabel(1)).thenReturn("ID");
                int[] row = {0};
                when(rs.next()).thenAnswer(inv -> ++row[0] <= 1000);
                when(rs.getObject(1)).thenAnswer(inv -> row[0]);
                assertEquals(1000, writer.write(rs, task, n -> {}).get(0).rows());
                assertThrows(
                        java.io.IOException.class,
                        () ->
                                writer.write(
                                        rows(
                                                new String[] {"V"},
                                                new Object[][] {{"x".repeat(32768)}}),
                                        task("FAILED", true),
                                        n -> {}));
                writer.publish(output, true);
            }
            try (var paths = Files.list(temp)) {
                assertEquals(0, paths.count(), "SXSSF files must be disposed");
            }
            try (var book = new XSSFWorkbook(output.toFile())) {
                assertEquals(1000, book.getSheetAt(0).getLastRowNum());
                assertEquals(
                        1000, book.getSheetAt(0).getRow(1000).getCell(0).getNumericCellValue());
            }
        } finally {
            org.apache.poi.util.TempFile.setTempFileCreationStrategy(
                    new org.apache.poi.util.DefaultTempFileCreationStrategy());
        }
    }

    @Test
    void db2TwelveDigitTimestampUsesExactDriverRepresentation() throws Exception {
        com.ibm.db2.jcc.DB2ResultSet rs = mock(com.ibm.db2.jcc.DB2ResultSet.class);
        ResultSetMetaData metadata = mock(ResultSetMetaData.class);
        when(rs.getMetaData()).thenReturn(metadata);
        when(metadata.getColumnCount()).thenReturn(1);
        when(metadata.getColumnLabel(1)).thenReturn("TS");
        when(metadata.getColumnType(1)).thenReturn(Types.TIMESTAMP);
        when(metadata.getScale(1)).thenReturn(12);
        when(rs.next()).thenReturn(true, true, false);
        var exact = com.ibm.db2.jcc.DBTimestamp.valueOfDBString("2024-01-02-12.30.00.123456789123");
        when(rs.getDBTimestamp(1)).thenReturn(exact, null);
        ExcelExportTask task = task("TS", true);
        Path output = dir.resolve("precise.xlsx");
        try (var writer = new ExcelWorkbookWriter(List.of(task))) {
            writer.write(rs, task, n -> {});
            writer.publish(output, true);
        }
        verify(rs, never()).getObject(1);
        try (var book = new XSSFWorkbook(output.toFile())) {
            assertEquals(
                    "2024-01-02-12.30.00.123456789123",
                    book.getSheetAt(0).getRow(1).getCell(0).getStringCellValue());
            assertEquals(CellType.BLANK, book.getSheetAt(0).getRow(2).getCell(0).getCellType());
        }
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/input/ObjectListReaderTest.java =====
UTF8-BYTES: 1307
SHA256: c160941a4551e61c7b55da985b6eb49a3a549154dc11d99824d150a6671500ce
===== CONTENT =====
package com.example.db2toolkit.input;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.provider.*;

import java.nio.file.*;
import java.util.*;

class ObjectListReaderTest {
    @TempDir Path dir;

    @Test
    void listsBomDuplicatesAndInvalid() throws Exception {
        Path file = dir.resolve("list.txt");
        Files.writeString(
                file,
                "\uFEFF  # comment\n\n"
                        + "rpt.fn\n"
                        + "RPT.FN\n"
                        + "\"RPT\".\"FN\"\n"
                        + "\"rpt\".fn\n"
                        + "bad\n"
                        + "\"a.b\".\"c\"\"d\"\n");
        var list = new ObjectListReader().read(file);
        assertEquals(3, list.objects().size());
        assertEquals(2, list.duplicates());
        assertEquals(1, list.issues().size());
        assertEquals(7, list.issues().get(0).line());
        assertEquals(file, list.issues().get(0).file());
        assertEquals("RPT", list.objects().get(0).schema());
        assertThrows(
                java.io.IOException.class,
                () -> new ObjectListReader().read(dir.resolve("absent")));
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/jdbc/JdbcActivityTest.java =====
UTF8-BYTES: 4329
SHA256: a847582ed2d066dfa868340172406eb089880e566eb1f8aa90ae6bb75b4e8e0d
===== CONTENT =====
package com.example.db2toolkit.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.db2toolkit.config.ConnectionConfig;
import com.example.db2toolkit.output.RunLogs;
import java.nio.file.*;
import java.sql.*;
import java.util.List;
import java.util.concurrent.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.MDC;

class JdbcActivityTest {
    @TempDir Path dir;

    @Test
    void blockedExecuteAndReadEmitScopedRedactedProgressAndStopOnClose() throws Exception {
        var settings = new ConnectionConfig("jdbc:db2:test", "u", "secret", 3, 4, 5, 1);
        var errors = new SafeDiagnostics(new com.example.db2toolkit.config.AppConfig(
            "jdbc:db2:test", "u", "secret", java.util.Map.of(), dir,
            "SYSCAT.ROUTINES", "SYSCAT.ROUTINES", "SYSCAT.TABLES", "SYSCAT.COLUMNS"));
        var statement = mock(PreparedStatement.class);
        var rows = mock(ResultSet.class);
        var logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(JdbcActivity.class);
        var executeWaiting = new CountDownLatch(1);
        var readWaiting = new CountDownLatch(1);
        var appender = new ch.qos.logback.core.AppenderBase<ch.qos.logback.classic.spi.ILoggingEvent>() {
            protected void append(ch.qos.logback.classic.spi.ILoggingEvent e) {
                String message = e.getFormattedMessage();
                if (message.contains("still waiting") && message.contains("EXECUTE_QUERY")) executeWaiting.countDown();
                if (message.contains("still waiting") && message.contains("READ_RESULTS")) readWaiting.countDown();
            }
        };
        appender.start();
        logger.addAppender(appender);
        MDC.put("database", "test");
        MDC.put(RunLogs.SCOPE_KEY, "outer");
        try (var logs = new RunLogs(dir, "test", errors);
             var context = JdbcActivity.context(settings, errors)) {
            when(statement.executeQuery()).thenAnswer(invocation -> {
                assertTrue(executeWaiting.await(5, TimeUnit.SECONDS));
                return rows;
            });
            try (var activity = JdbcActivity.query("secret object")) {
                JdbcActivity.configureQuery(statement);
                assertSame(rows, activity.execute(statement));
                assertTrue(readWaiting.await(5, TimeUnit.SECONDS));
            }
            verify(statement).setQueryTimeout(5);
            var wrongScope = new Thread(() -> {
                MDC.put("database", "test");
                MDC.put(RunLogs.SCOPE_KEY, "wrong");
                logger.info(RunLogs.PROGRESS, "WRONG_SCOPE");
                MDC.clear();
            });
            wrongScope.start();
            wrongScope.join();
            String completed = Files.readString(dir.resolve("test-process.log"));
            Thread.sleep(1100);
            assertEquals(completed, Files.readString(dir.resolve("test-process.log")));
            assertTrue(completed.contains("still waiting: phase=EXECUTE_QUERY"));
            assertTrue(completed.contains("still waiting: phase=READ_RESULTS"));
            assertTrue(completed.contains("elapsedMs="));
            assertFalse(completed.contains("secret"));
            assertFalse(completed.contains("WRONG_SCOPE"));
            assertEquals("", Files.readString(dir.resolve("test-summary_issue.log")));
        } finally {
            logger.detachAppender(appender);
            appender.stop();
            assertEquals("outer", MDC.get(RunLogs.SCOPE_KEY));
            MDC.clear();
        }
    }

    @Test
    void driverFailureIsPreservedAndConnectionIsClosed() throws Exception {
        var statement = mock(PreparedStatement.class);
        var failure = new SQLException("timeout", "57014");
        when(statement.executeQuery()).thenThrow(failure);
        try (var activity = JdbcActivity.query("object")) {
            assertSame(failure, assertThrows(SQLException.class, () -> activity.execute(statement)));
        }
        var connection = mock(Connection.class);
        try (var lease = JdbcActivity.open(() -> connection, new ConnectionConfig("x", "u", "p"))) {
            assertSame(connection, lease.connection());
        }
        verify(connection).close();
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/jdbc/QueryDeadlineTest.java =====
UTF8-BYTES: 7289
SHA256: ef42536f10237704a3ab5322a5bebd86e24c10620e5616835711b62f91ba024b
===== CONTENT =====
package com.example.db2toolkit.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.nio.file.*;
import java.sql.*;
import java.util.List;
import java.util.concurrent.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.example.db2toolkit.config.ConfigLoader;

class QueryDeadlineTest {
    @TempDir Path dir;
    private final SafeDiagnostics errors = new SafeDiagnostics(List.of());

    @Test void successfulScopeDisarmsCancellation() throws Exception {
        var statement = mock(PreparedStatement.class);
        try (var deadline = new QueryDeadline(statement, 1, 0, 1, "T", 1, errors)) { }
        Thread.sleep(1200);
        verify(statement, never()).cancel();
    }

    @Test void ignoredCancellationCannotProduceSuccess() throws Exception {
        var statement = mock(PreparedStatement.class);
        var cancelled = new CountDownLatch(1);
        doAnswer(call -> { cancelled.countDown(); return null; }).when(statement).cancel();
        var deadline = new QueryDeadline(statement, 1, 0, 5, "T", 7, errors);
        try {
            assertTrue(cancelled.await(4, TimeUnit.SECONDS));
        } finally {
            var error = assertThrows(SQLTimeoutException.class, deadline::close);
            assertTrue(errors.describe(error).startsWith("TIMEOUT:"));
        }
    }

    @Test void blockedCancelDoesNotPreventAbortAndConnectionIsUnusable() throws Exception {
        var statement = mock(PreparedStatement.class);
        var connection = mock(Connection.class);
        var release = new CountDownLatch(1);
        var aborted = new CountDownLatch(1);
        when(statement.getConnection()).thenReturn(connection);
        doAnswer(call -> { release.await(6, TimeUnit.SECONDS); return null; }).when(statement).cancel();
        doAnswer(call -> { aborted.countDown(); return null; }).when(connection).abort(any());
        var deadline = new QueryDeadline(statement, 1, 0, 1, "T", 8, errors);
        try {
            assertTrue(aborted.await(5, TimeUnit.SECONDS));
            var error = assertThrows(SQLTimeoutException.class, deadline::close);
            assertNotNull(ConnectionFailureClassifier.connectionFailure(error));
        } finally { release.countDown(); deadline.close(); }
    }

    @Test void slowWarningIsScopedAndIncludesSqlId() throws Exception {
        var logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger("com.example.db2toolkit");
        var warning = new CountDownLatch(1);
        var appender = new ch.qos.logback.core.AppenderBase<ch.qos.logback.classic.spi.ILoggingEvent>() {
            protected void append(ch.qos.logback.classic.spi.ILoggingEvent event) {
                if (event.getFormattedMessage().contains("SLOW_QUERY sqlId=42")) warning.countDown();
            }
        };
        appender.start();
        org.slf4j.MDC.put("database", "fos");
        try (var logs = new com.example.db2toolkit.output.RunLogs(dir, "fos", errors);
                var deadline = new QueryDeadline(mock(PreparedStatement.class), 0, 1, 1,
                        "TABLE_A", 42, errors)) {
            logger.addAppender(appender);
            assertTrue(warning.await(4, TimeUnit.SECONDS));
            deadline.close(); // The observing appender runs after the file appender has flushed.
            String text = Files.readString(dir.resolve("fos-process.log"));
            assertTrue(text.contains("[db=fos]"));
            assertTrue(text.contains("SLOW_QUERY sqlId=42 context=TABLE_A elapsedMs="));
        } finally {
            logger.detachAppender(appender); appender.stop(); org.slf4j.MDC.clear();
        }
    }

    @Test void delayedTimerCannotTurnExpiredQueryIntoSuccess() throws Exception {
        var field = QueryDeadline.class.getDeclaredField("TIMER");
        field.setAccessible(true);
        var timer = (ScheduledThreadPoolExecutor) field.get(null);
        var blocked = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var task = timer.submit(() -> {
            blocked.countDown();
            try { release.await(5, TimeUnit.SECONDS); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        assertTrue(blocked.await(2, TimeUnit.SECONDS));
        var deadline = new QueryDeadline(mock(PreparedStatement.class), 1, 0, 1, "T", 9, errors);
        try {
            Thread.sleep(1100);
            assertThrows(SQLTimeoutException.class, deadline::close);
        } finally { release.countDown(); task.get(3, TimeUnit.SECONDS); deadline.close(); }
    }

    @Test void blockedDiagnosticLoggingCannotDelayCancelOrAbort() throws Exception {
        var logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(QueryDeadline.class);
        var entered = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var cancelled = new CountDownLatch(1);
        var aborted = new CountDownLatch(1);
        var appender = new ch.qos.logback.core.AppenderBase<ch.qos.logback.classic.spi.ILoggingEvent>() {
            protected void append(ch.qos.logback.classic.spi.ILoggingEvent event) {
                entered.countDown();
                try { release.await(8, TimeUnit.SECONDS); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        };
        appender.start(); logger.addAppender(appender);
        var statement = mock(PreparedStatement.class);
        var connection = mock(Connection.class);
        when(statement.getConnection()).thenReturn(connection);
        doAnswer(call -> { cancelled.countDown(); return null; }).when(statement).cancel();
        doAnswer(call -> { aborted.countDown(); return null; }).when(connection).abort(any());
        var deadline = new QueryDeadline(statement, 2, 1, 1, "blocked-log", 10, errors);
        try {
            assertTrue(entered.await(3, TimeUnit.SECONDS));
            assertTrue(cancelled.await(3, TimeUnit.SECONDS), "Logging must not block cancellation");
            assertTrue(aborted.await(3, TimeUnit.SECONDS), "Logging must not block escalation");
        } finally {
            release.countDown(); logger.detachAppender(appender);
            assertThrows(SQLTimeoutException.class, deadline::close);
            appender.stop();
        }
    }

    @Test void configurationSupportsAliasOverridesAndRejectsZeroGrace() throws Exception {
        var file = dir.resolve("app.properties");
        String base = "db.names=a,b\ndb.a.url=jdbc:db2://a/DB\ndb.a.username=u\ndb.a.password=p\n"
                + "db.b.url=jdbc:db2://b/DB\ndb.b.username=u\ndb.b.password=p\nexport.enabled=false\n"
                + "db.slow-query-seconds=60\ndb.cancel-grace-seconds=15\ndb.b.slow-query-seconds=2\n";
        Files.writeString(file, base);
        var configs = new ConfigLoader().loadTargets(file, key -> null);
        assertEquals(60, configs.get(0).config().connection().slowQuerySeconds());
        assertEquals(2, configs.get(1).config().connection().slowQuerySeconds());
        Files.writeString(file, base + "db.b.cancel-grace-seconds=0\n");
        assertThrows(RuntimeException.class, () -> new ConfigLoader().loadTargets(file, key -> null));
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/jdbc/ReadOnlyQueriesTest.java =====
UTF8-BYTES: 2351
SHA256: ed91676fbf5a7dbc56daf7b05cf38d83eded65ffdb565c950b663f314a830fcd
===== CONTENT =====
package com.example.db2toolkit.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ReadOnlyQueriesTest {
    @ParameterizedTest
    @ValueSource(strings = {
        "UPDATE S.T SET C=1", "INSERT INTO S.T VALUES (1)", "DELETE FROM S.T",
        "MERGE INTO S.T USING S.X ON 1=1 WHEN MATCHED THEN DELETE",
        "CREATE TABLE S.T (C INT)", "ALTER TABLE S.T ADD C INT", "DROP TABLE S.T",
        "TRUNCATE TABLE S.T IMMEDIATE", "CALL S.P()", "COMMIT", "ROLLBACK",
        "GRANT SELECT ON S.T TO PUBLIC", "REVOKE SELECT ON S.T FROM PUBLIC",
        "SELECT * FROM S.T; DELETE FROM S.T",
        "WITH X AS (SELECT * FROM FINAL TABLE (INSERT INTO S.T VALUES (1))) SELECT * FROM X",
        "SELECT * FROM OLD TABLE (DELETE FROM S.T)",
        "SELECT C INTO X FROM S.T", "SELECT * FROM S.T FOR UPDATE",
        "SELECT NEXT VALUE FOR S.SEQ FROM SYSIBM.SYSDUMMY1",
        "SELECT S.SEQ.NEXTVAL FROM SYSIBM.SYSDUMMY1"
    })
    void rejectsWritesBeforeEitherPrepareOverloadContactsDriver(String sql) throws Exception {
        Connection connection = mock(Connection.class);
        assertThrows(IllegalArgumentException.class, () -> ReadOnlyQueries.prepare(connection, sql));
        assertThrows(IllegalArgumentException.class, () -> ReadOnlyQueries.prepareForwardOnly(connection, sql));
        verifyNoInteractions(connection);
    }

    @Test
    void acceptsReadQueriesAndKeepsNativeStatements() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        String sql = "WITH X AS (SELECT REPLACE('UPDATE; DELETE', 'DELETE', 'INSERT') AS C FROM S.T) SELECT C FROM X";
        when(connection.prepareStatement(sql)).thenReturn(statement);
        when(connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).thenReturn(statement);
        assertSame(statement, ReadOnlyQueries.prepare(connection, sql));
        assertSame(statement, ReadOnlyQueries.prepareForwardOnly(connection, sql));
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/jdbc/SafeDiagnosticsTest.java =====
UTF8-BYTES: 2549
SHA256: 52093cbde667aa77789a7688b48580850ce3c2ce94aa14d9d7fdb050992c6306
===== CONTENT =====
package com.example.db2toolkit.jdbc;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.db2toolkit.config.AppConfig;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;
import java.util.Map;

class SafeDiagnosticsTest {
    @Test
    void cyclicCleanupCausesAreVisitedOnce() {
        IOException read = new IOException("read secret failed");
        SQLException close = new SQLException("cleanup secret failed", "08006", -4499);
        read.addSuppressed(close);
        close.initCause(read);
        assertSame(
                close,
                com.example.db2toolkit.jdbc.ConnectionFailureClassifier.connectionFailure(read));
        String text = errors().describe(read);
        assertEquals(1, text.split("read", -1).length - 1);
        assertEquals(1, text.split("08006", -1).length - 1);
        assertFalse(text.contains("secret"));
    }

    private SafeDiagnostics errors() {
        return new SafeDiagnostics(
                new AppConfig(
                        "jdbc:db2://host/DB",
                        "user",
                        "secret",
                        Map.of(),
                        Path.of("."),
                        "P",
                        "F",
                        "T",
                        "C"));
    }

    @Test
    void sqlDiagnosticsRetainSuppressedCleanupErrorsWithoutExposingSecrets() {
        SQLException query = new SQLException("query secret failed", "42703", -206);
        query.addSuppressed(new SQLException("close secret failed", "08006", -4499));
        String text = errors().sql(query);
        assertTrue(text.contains("42703"));
        assertTrue(text.contains("08006"));
        assertTrue(text.contains("-4499"));
        assertFalse(text.contains("secret"));
    }

    @Test
    void suppressedConnectionFailureDoesNotNeedAnotherHealthProbe() throws Exception {
        SQLException query = new SQLException("query failed", "42703", -206);
        query.addSuppressed(new SQLNonTransientConnectionException("close failed", "08006", -4499));
        Connection connection = mock(Connection.class);
        when(connection.isValid(5)).thenReturn(true);
        assertTrue(
                com.example.db2toolkit.jdbc.ConnectionFailureClassifier.connectionLost(
                        connection, query));
        verify(connection, never()).isValid(anyInt());
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/jdbc/SqlAuditLogTest.java =====
UTF8-BYTES: 5507
SHA256: 3370589c31b40b2f859b5db6a89814de45c8b70af17d58a04a06d34446da9d62
===== CONTENT =====
package com.example.db2toolkit.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.db2toolkit.config.ConfigLoader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.MDC;

class SqlAuditLogTest {
    @TempDir Path dir;
    @AfterEach void resetContext() { MDC.remove("database"); }

    @Test void flushesSqlAndParametersBeforeDriverCallsAndKeepsNativeObjects() throws Exception {
        Path config = dir.resolve("config.properties");
        Files.writeString(config, "db.url=jdbc:db2://unused/DB\ndb.username=u\ndb.password=secret\nexport.enabled=false\n");
        var errors = new SafeDiagnostics(new ConfigLoader().load(config, key -> null));
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet rows = mock(ResultSet.class);
        String sql = "SELECT * FROM T WHERE S=? AND N=? AND X='secret'";
        try (var log = new SqlAuditLog(dir.resolve("logs"), "first", errors)) {
            MDC.put("database", "fos");
            when(connection.prepareStatement(sql)).thenAnswer(call -> {
                String text = Files.readString(log.path());
                assertTrue(text.contains("PREPARE_ATTEMPT sql=SELECT * FROM T"));
                assertFalse(text.contains("secret"));
                return statement;
            });
            assertSame(statement, ReadOnlyQueries.prepare(connection, sql));
            doAnswer(call -> {
                assertTrue(Files.readString(log.path()).contains("index=1 type=VARCHAR value=[redacted]"));
                return null;
            }).when(statement).setString(1, "secret");
            ReadOnlyQueries.bindString(statement, 1, "secret");
            ReadOnlyQueries.bindDecimal(statement, 2, new BigDecimal("9999999999999999999999999999999"));
            when(statement.executeQuery()).thenAnswer(call -> {
                assertTrue(Files.readString(log.path()).contains("[db=fos] [sqlId=1] EXECUTE_ATTEMPT"));
                return rows;
            });
            try (var activity = JdbcActivity.query("test")) { assertSame(rows, activity.execute(statement)); }
            assertTrue(Files.readString(log.path()).contains("value=9999999999999999999999999999999"));
        }
    }

    @Test void rejectedAndFailedPreparationsRemainVisibleAndRunsDoNotOverwrite() throws Exception {
        var connection = mock(Connection.class);
        Path first;
        try (var log = new SqlAuditLog(dir, "first", new SafeDiagnostics(List.of()))) {
            first = log.path();
            MDC.put("database", "fos");
            assertThrows(IllegalArgumentException.class, () -> ReadOnlyQueries.prepare(connection, "DELETE FROM T"));
            verifyNoInteractions(connection);
            MDC.put("database", "rpt");
            when(connection.prepareStatement("SELECT * FROM MISSING", ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY)).thenThrow(new SQLException("missing", "42704"));
            assertThrows(SQLException.class, () -> ReadOnlyQueries.prepareForwardOnly(connection, "SELECT * FROM MISSING"));
            String text = Files.readString(first);
            assertTrue(text.contains("[db=fos] [sqlId=1] REJECTED"));
            assertTrue(text.contains("[db=rpt] [sqlId=2] PREPARE_FAILED"));
            assertTrue(text.contains("SQLState=42704"));
        }
        String saved = Files.readString(first);
        try (var next = new SqlAuditLog(dir, "second", new SafeDiagnostics(List.of()))) {
            assertNotEquals(first, next.path());
        }
        assertEquals(saved, Files.readString(first));
    }

    @Test void executionFailureRetainsSqlIdentity() throws Exception {
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement("SELECT * FROM T")).thenReturn(statement);
        when(statement.executeQuery()).thenThrow(new SQLTimeoutException("timeout", "57014"));
        try (var log = new SqlAuditLog(dir, "first", new SafeDiagnostics(List.of()));
                var activity = JdbcActivity.query("timeout")) {
            ReadOnlyQueries.prepare(connection, "SELECT * FROM T");
            assertThrows(SQLTimeoutException.class, () -> activity.execute(statement));
            assertTrue(Files.readString(log.path()).contains("[sqlId=1] EXECUTE_FAILED TIMEOUT: SQLState=57014"));
        }
    }

    @Test void logFailurePreventsUnloggedSqlAndIsReportedAtClose() throws Exception {
        var connection = mock(Connection.class);
        var log = new SqlAuditLog(dir, "first", new SafeDiagnostics(List.of()));
        var field = SqlAuditLog.class.getDeclaredField("writer");
        field.setAccessible(true);
        ((BufferedWriter) field.get(log)).close();
        try {
            assertThrows(UncheckedIOException.class, () -> ReadOnlyQueries.prepare(connection, "SELECT * FROM T"));
            verifyNoInteractions(connection);
            assertNotNull(SqlAuditLog.writeFailure());
        } finally { assertThrows(IOException.class, log::close); }
        assertNull(SqlAuditLog.writeFailure());
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/model/IdentifiersTest.java =====
UTF8-BYTES: 2676
SHA256: f1aef5e016261445d0c096ddaed254ec1629040df1c5ac778792c5d1e345ce07
===== CONTENT =====
package com.example.db2toolkit.model;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.nio.file.*;
import java.util.*;

class IdentifiersTest {
    @TempDir Path dir;

    @Test
    void identifiersAndEscapes() {
        assertEquals(new DbObjectRef("RPT", "FN"), Identifiers.parse(" rpt.fn "));
        assertEquals(new DbObjectRef("a.b", "X\"y"), Identifiers.parse("\"a.b\".\"X\"\"y\""));
        assertEquals("\"a.b\".\"X\"\"y\"", Identifiers.parse("\"a.b\".\"X\"\"y\"").sqlName());
        Locale old = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr"));
            assertEquals("I", Identifiers.parse("i.i").name());
        } finally {
            Locale.setDefault(old);
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "A",
                "A.B.C",
                "A.",
                ".B",
                "\"a.b",
                "A.B;DROP TABLE T",
                "A.\"\"",
                "A.B --x",
                "A.\"b\"x"
            })
    void rejectsInvalidIdentifiers(String value) {
        assertThrows(IllegalArgumentException.class, () -> Identifiers.parse(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"\u00DF", "\u0131", "\u03C2", "\uFB03", "\uD801\uDC28", "\u00E9"})
    void ambiguousUnicodeFoldingRequiresExactQuotedCatalogNames(String name) {
        for (String input : List.of("RPT." + name, name + ".T", "RPT.A" + name + "B")) {
            IllegalArgumentException error =
                    assertThrows(IllegalArgumentException.class, () -> Identifiers.parse(input));
            assertTrue(error.getMessage().contains("double quotes"));
            assertTrue(error.getMessage().contains("exact catalog spelling"));
        }
        String quoted = Identifiers.quote(name);
        assertEquals(new DbObjectRef("RPT", name), Identifiers.parse("RPT." + quoted));
        assertEquals(new DbObjectRef(name, "T"), Identifiers.parse(quoted + ".T"));
        assertEquals(new DbObjectRef(name, name), Identifiers.parse(quoted + "." + quoted));
    }

    @Test
    void uncasedUnicodeAndAlreadyUppercaseNamesRetainTheirSpelling() {
        assertEquals(
                new DbObjectRef("\u4E2D\u6587", "\u8868"),
                Identifiers.parse("\u4E2D\u6587.\u8868"));
        assertEquals(new DbObjectRef("RPT", "\u00C9"), Identifiers.parse("rpt.\u00C9"));
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/output/OutputCleanupTest.java =====
UTF8-BYTES: 3732
SHA256: 48dfafca96b5ff0d0a65d5c728091c20f3145988aa64b08badeac59c9facb750
===== CONTENT =====
package com.example.db2toolkit.output;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.db2toolkit.ddl.model.*;
import com.example.db2toolkit.excel.*;
import com.example.db2toolkit.model.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

class OutputCleanupTest {
    @TempDir Path dir;

    @Test
    void ddlPublicationCannotOverwriteAFileCreatedAfterNameSelection() throws Exception {
        var writer = new SqlFileWriter(dir);
        Path target = writer.runDirectory().resolve("tables/RPT.X.sql");
        try (var files = mockStatic(Files.class, invocation -> {
            if (invocation.getMethod().getName().equals("move"))
                Files.writeString(target, "other process output", StandardOpenOption.CREATE_NEW);
            return invocation.callRealMethod();
        })) {
            assertThrows(FileAlreadyExistsException.class,
                () -> writer.write(DdlObjectType.TABLE, new DbObjectRef("RPT", "X"), null, "CREATE TABLE X (ID INT)"));
        }
        assertEquals("other process output", Files.readString(target));
        try (var entries = Files.list(target.getParent())) { assertEquals(1, entries.count()); }
    }

    private MockedStatic<Files> failedPublication(IOException move, IOException cleanup) {
        // Inject filesystem failures at the actual publication boundary, leaving all other IO real.
        return mockStatic(
                Files.class,
                invocation -> {
                    String method = invocation.getMethod().getName();
                    if (method.equals("move")) throw move;
                    if (method.equals("deleteIfExists")) throw cleanup;
                    return invocation.callRealMethod();
                });
    }

    @ParameterizedTest
    @EnumSource(DdlObjectType.class)
    void ddlPublicationFailureSurvivesTemporaryFileCleanupFailure(DdlObjectType type)
            throws Exception {
        var writer = new SqlFileWriter(dir);
        var move = new IOException("publication failed");
        var cleanup = new IOException("temporary cleanup failed");
        try (var ignored = failedPublication(move, cleanup)) {
            IOException actual =
                    assertThrows(
                            IOException.class,
                            () -> writer.write(type, new DbObjectRef("RPT", "X"), null, "DDL"));
            assertSame(move, actual);
            assertArrayEquals(new Throwable[] {cleanup}, actual.getSuppressed());
        }
    }

    @Test
    void excelPublicationFailureSurvivesTemporaryFileCleanupFailure() throws Exception {
        var move = new IOException("publication failed");
        var cleanup = new IOException("temporary cleanup failed");
        var task =
                new ExcelExportTask(
                        "X", SourceType.SQL, "SELECT 1", "x.xlsx", "X", true, 1, 0, true);
        try (var writer = new ExcelWorkbookWriter(List.of(task))) {
            try (var ignored = failedPublication(move, cleanup)) {
                IOException actual =
                        assertThrows(
                                IOException.class,
                                () -> writer.publish(dir.resolve("x.xlsx"), true));
                assertSame(move, actual);
                assertArrayEquals(new Throwable[] {cleanup}, actual.getSuppressed());
            }
        }
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/output/RunLogMarkersTest.java =====
UTF8-BYTES: 6276
SHA256: eb63d7953131c71b26cb737960e6ad05ba70a08abcb50f2071f51cb696bec79d
===== CONTENT =====
package com.example.db2toolkit.output;

import static org.junit.jupiter.api.Assertions.*;

import com.example.db2toolkit.jdbc.SafeDiagnostics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class RunLogMarkersTest {
    @TempDir Path directory;

    @Test
    void blockedDatabaseFileDoesNotBlockOtherDatabaseLogging() throws Exception {
        var entered = new java.util.concurrent.CountDownLatch(1);
        var release = new java.util.concurrent.CountDownLatch(1);
        var executor = java.util.concurrent.Executors.newFixedThreadPool(2);
        var logger = LoggerFactory.getLogger("com.example.db2toolkit.test.BlockedFile");
        try {
            var first = executor.submit(() -> {
                MDC.put("database", "first");
                try (var logs = new RunLogs(directory, "first", new SafeDiagnostics(List.of()))) {
                    var field = RunLogs.class.getDeclaredField("process");
                    field.setAccessible(true);
                    var original = (java.io.BufferedWriter) field.get(logs);
                    field.set(logs, new java.io.BufferedWriter(original) {
                        @Override public void write(String text, int offset, int length) throws java.io.IOException {
                            entered.countDown();
                            try { release.await(8, java.util.concurrent.TimeUnit.SECONDS); }
                            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                            super.write(text, offset, length);
                        }
                    });
                    logger.info("first blocked");
                } finally { MDC.clear(); }
                return null;
            });
            assertTrue(entered.await(4, java.util.concurrent.TimeUnit.SECONDS));
            var second = executor.submit(() -> {
                MDC.put("database", "second");
                try (var logs = new RunLogs(directory, "second", new SafeDiagnostics(List.of()))) {
                    logger.info("second completed");
                } finally { MDC.clear(); }
                return null;
            });
            try {
                second.get(3, java.util.concurrent.TimeUnit.SECONDS);
                assertTrue(Files.readString(directory.resolve("second-process.log")).contains("second completed"));
            } finally { release.countDown(); first.get(5, java.util.concurrent.TimeUnit.SECONDS); }
        } finally { release.countDown(); executor.shutdownNow(); }
    }

    @Test
    void loggerLevelIsRestoredOnlyAfterAllConcurrentScopesClose() throws Exception {
        var logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.example.db2toolkit");
        var original = logger.getLevel();
        logger.setLevel(ch.qos.logback.classic.Level.ERROR);
        var firstStarted = new java.util.concurrent.CountDownLatch(1);
        var secondStarted = new java.util.concurrent.CountDownLatch(1);
        var firstClosed = new java.util.concurrent.CountDownLatch(1);
        var executor = java.util.concurrent.Executors.newFixedThreadPool(2);
        try {
            var first = executor.submit(() -> {
                MDC.put("database", "first");
                try (var logs = new RunLogs(directory, "first", new SafeDiagnostics(List.of()))) {
                    firstStarted.countDown();
                    assertTrue(secondStarted.await(5, java.util.concurrent.TimeUnit.SECONDS));
                } finally { firstClosed.countDown(); MDC.clear(); }
                return null;
            });
            var second = executor.submit(() -> {
                assertTrue(firstStarted.await(5, java.util.concurrent.TimeUnit.SECONDS));
                MDC.put("database", "second");
                try (var logs = new RunLogs(directory, "second", new SafeDiagnostics(List.of()))) {
                    secondStarted.countDown();
                    assertTrue(firstClosed.await(5, java.util.concurrent.TimeUnit.SECONDS));
                    assertTrue(logger.isInfoEnabled());
                    logger.info("second still running");
                } finally { MDC.clear(); }
                return null;
            });
            first.get(8, java.util.concurrent.TimeUnit.SECONDS);
            second.get(8, java.util.concurrent.TimeUnit.SECONDS);
            assertEquals(ch.qos.logback.classic.Level.ERROR, logger.getLevel());
            assertTrue(Files.readString(directory.resolve("second-process.log")).contains("second still running"));
        } finally { executor.shutdownNow(); logger.setLevel(original); }
    }

    @Test
    void routingSurvivesLoggerAndOutcomeMessageRenaming() throws Exception {
        var logger = LoggerFactory.getLogger("com.example.db2toolkit.arbitrary.NewComponent");
        String previous = MDC.get("database");
        MDC.put("database", "test");
        try (var logs = new RunLogs(directory, "test", new SafeDiagnostics(List.of()))) {
            logger.info(RunLogs.SUCCESS, "completed item");
            logger.warn(RunLogs.SUMMARY, "issue item");
            logger.info(RunLogs.OUTCOME, "arbitrary final wording");
            logger.info("Database export finished: unmarked text must not control routing");
        } finally {
            if (previous == null) MDC.remove("database");
            else MDC.put("database", previous);
        }
        String success = Files.readString(directory.resolve("test-summary_success.log"));
        String issue = Files.readString(directory.resolve("test-summary_issue.log"));
        String process = Files.readString(directory.resolve("test-process.log"));
        assertTrue(success.contains("completed item"));
        assertFalse(issue.contains("completed item"));
        assertTrue(issue.contains("issue item"));
        assertFalse(success.contains("issue item"));
        for (String summary : List.of(success, issue)) {
            assertTrue(summary.contains("arbitrary final wording"));
            assertFalse(summary.contains("unmarked text"));
        }
        assertTrue(process.contains("unmarked text"));
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/output/SqlFileWriterTest.java =====
UTF8-BYTES: 8904
SHA256: 33c4e4ed49a6f000b8b81bdea5271cdd064d8189548f6a8a1ff20b26c465264a
===== CONTENT =====
package com.example.db2toolkit.output;


import static org.junit.jupiter.api.Assertions.*;

import com.example.db2toolkit.ddl.model.DdlObjectType;
import com.example.db2toolkit.model.DbObjectRef;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.nio.file.*;
import java.util.*;

class SqlFileWriterTest {
    @TempDir Path dir;

    @Test
    void failedWriteCanRetryWithoutChangingFileName() throws Exception {
        var writer = new SqlFileWriter(dir);
        Path tables = writer.runDirectory().resolve("tables");
        Files.delete(tables);
        var ref = new DbObjectRef("RPT", "T");
        assertThrows(
                java.io.IOException.class,
                () -> writer.write(DdlObjectType.TABLE, ref, null, "CREATE TABLE RPT.T (ID INT)"));
        Files.createDirectory(tables);
        assertEquals(
                "RPT.T.sql",
                writer.write(DdlObjectType.TABLE, ref, null, "CREATE TABLE RPT.T (ID INT)")
                        .getFileName()
                        .toString());
    }

    @Test
    void longUnicodeNamesRespectUtf8FileSystemLimit() throws Exception {
        var writer = new SqlFileWriter(dir);
        var ref = new DbObjectRef("中文".repeat(50), "表".repeat(50));
        Path file = writer.write(DdlObjectType.TABLE, ref, null, "CREATE TABLE T (ID INT)");
        assertTrue(
                file.getFileName()
                                .toString()
                                .getBytes(java.nio.charset.StandardCharsets.UTF_8)
                                .length
                        < 255);
    }

    @Test
    void filenamesCannotAliasOrTraverse() {
        var names = new HashSet<String>();
        for (String value :
                List.of(
                        "a",
                        "A",
                        "a/b",
                        "a\\b",
                        "a_b",
                        "..",
                        "CON",
                        "a:b",
                        "x".repeat(300),
                        "y".repeat(300))) {
            String name =
                    SqlFileWriter.fileName(
                            DdlObjectType.PROCEDURE, new DbObjectRef("../s", value), "specific");
            assertTrue(names.add(name.toLowerCase(Locale.ROOT)));
            assertFalse(name.contains("/"));
            assertFalse(name.contains("\\"));
            assertTrue(name.length() < 200);
        }
        assertEquals(
                "DB2ADMIN.PR_GEN_COMPLEX_CTE_FLOW_D.sql",
                SqlFileWriter.fileName(
                        DdlObjectType.PROCEDURE,
                        new DbObjectRef("DB2ADMIN", "PR_GEN_COMPLEX_CTE_FLOW_D"),
                        "PR_GEN_COMPLEX_CTE_FLOW_D"));
    }

    @Test
    void windowsSuperscriptDeviceSchemasUseFallbackNamesWithoutDeviceIo() {
        for (String schema :
                List.of(
                        "COM\u00B9",
                        "COM\u00B2",
                        "COM\u00B3",
                        "LPT\u00B9",
                        "LPT\u00B2",
                        "LPT\u00B3")) {
            for (String variant : List.of(schema, schema.toLowerCase(Locale.ROOT))) {
                var ref = new DbObjectRef(variant, "T");
                String filename = SqlFileWriter.fileName(DdlObjectType.TABLE, ref, null);
                assertTrue(filename.startsWith("obj-"), filename);
                assertTrue(filename.endsWith(".sql"), filename);
                String overload = OutputFileNames.overload(DdlObjectType.PROCEDURE, ref, "SPEC");
                assertTrue(overload.startsWith("obj-"), overload);
                assertTrue(overload.endsWith(".sql"), overload);
            }
        }
        for (String schema : List.of("COM10", "LPT10")) {
            var ref = new DbObjectRef(schema, "T");
            assertEquals(schema + ".T.sql", SqlFileWriter.fileName(DdlObjectType.TABLE, ref, null));
            assertEquals(
                    schema + ".T.SPEC.sql",
                    OutputFileNames.overload(DdlObjectType.PROCEDURE, ref, "SPEC"));
        }
    }

    @Test
    void normalNamesAndOverloadCollisions() throws Exception {
        var writer = new SqlFileWriter(dir);
        var ref = new DbObjectRef("DB2ADMIN", "SP_TEST");
        var first = writer.write(DdlObjectType.PROCEDURE, ref, "SPEC1", "CREATE PROCEDURE test");
        var second = writer.write(DdlObjectType.PROCEDURE, ref, "SPEC2", "CREATE PROCEDURE test");
        var third = writer.write(DdlObjectType.PROCEDURE, ref, "spec2", "CREATE PROCEDURE test");
        var fourth =
                writer.write(
                        DdlObjectType.PROCEDURE,
                        new DbObjectRef("db2admin", "sp_test"),
                        "SPEC1",
                        "CREATE PROCEDURE test");
        assertEquals("DB2ADMIN.SP_TEST.sql", first.getFileName().toString());
        assertEquals("DB2ADMIN.SP_TEST.SPEC2.sql", second.getFileName().toString());
        assertEquals(
                4,
                Set.of(
                                first.getFileName().toString().toLowerCase(Locale.ROOT),
                                second.getFileName().toString().toLowerCase(Locale.ROOT),
                                third.getFileName().toString().toLowerCase(Locale.ROOT),
                                fourth.getFileName().toString().toLowerCase(Locale.ROOT))
                        .size());
        assertEquals("CREATE PROCEDURE test\n@\n", Files.readString(first));
    }

    @ParameterizedTest
    @EnumSource(DdlObjectType.class)
    void allFilesHaveExactlyOneTerminator(DdlObjectType type) throws Exception {
        var writer = new SqlFileWriter(dir);
        String ddl = "CREATE " + type + " \"S\".\"N\" /* 中文 */\nBEGIN\n VALUES ';';\nEND;";
        Path file =
                writer.write(
                        type,
                        new DbObjectRef("S", "N"),
                        type == DdlObjectType.TABLE ? null : "SPEC",
                        ddl + "\r\n\r\n");
        String ending = "\n@\n";
        assertEquals(ddl + ending, Files.readString(file));
        assertEquals(1, Files.readString(file).lines().filter("@"::equals).count());
        assertEquals(Files.readString(file), SqlFileWriter.terminate(type, Files.readString(file)));
        assertThrows(
                java.io.IOException.class,
                () ->
                        writer.write(
                                type,
                                new DbObjectRef("S", "N"),
                                type == DdlObjectType.TABLE ? null : "SPEC",
                                ddl));
        try (var files = Files.list(file.getParent())) {
            assertEquals(1, files.count());
        }
    }

    @ParameterizedTest
    @EnumSource(DdlObjectType.class)
    void existingPaddedClientTerminatorsAreRemovedWithoutChangingBody(DdlObjectType type) {
        String body = "CREATE " + type + " RPT.X /* keep @ inside the body */\r\nEND;  ";
        String expected = body + ("\n@\n");
        for (String newline : List.of("\n", "\r\n", "\r")) {
            String source = body + newline + " \t@ \t" + newline + "@" + newline;
            assertEquals(expected, SqlFileWriter.terminate(type, source));
            assertEquals(expected, SqlFileWriter.terminate(type, expected));
        }
    }

    @ParameterizedTest
    @EnumSource(DdlObjectType.class)
    void blankLinesAfterAndBetweenClientTerminatorsDoNotDuplicateThem(DdlObjectType type) {
        String body = "CREATE " + type + " RPT.X /* keep @ in the body */\r\nEND; \t";
        String expected = body + ("\n@\n");
        for (String newline : List.of("\n", "\r\n", "\r")) {
            String source =
                    body + newline + " \t@ \t" + newline + " \t" + newline + "@" + newline + "  "
                            + newline + "\t" + newline;
            assertEquals(expected, SqlFileWriter.terminate(type, source));
            assertEquals(expected, SqlFileWriter.terminate(type, expected));
        }
    }

    @ParameterizedTest
    @EnumSource(DdlObjectType.class)
    void whitespaceOnlyLinesWithoutClientTerminatorsRemainBodyContent(DdlObjectType type) {
        String ending = "\n@\n";
        for (String newline : List.of("\n", "\r\n", "\r")) {
            String body =
                    "CREATE " + type + " RPT.X /* @ */" + newline + "END;  " + newline + " \t";
            assertEquals(body + ending, SqlFileWriter.terminate(type, body + newline));
        }
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/spreadsheet/ExcelLobCleanupTest.java =====
UTF8-BYTES: 2130
SHA256: 046621da14c0e9c6891c27f9333b1bcb060e1ac93283327a1a1781a9015d75a7
===== CONTENT =====
package com.example.db2toolkit.spreadsheet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.db2toolkit.excel.*;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.sql.*;

class ExcelLobCleanupTest {
    @Test
    void clobFreeFailureDoesNotReplaceTheOriginalReadFailure() throws Exception {
        IOException readFailure = new IOException("original read failure");
        Reader reader = mock(Reader.class);
        when(reader.read(any(char[].class))).thenThrow(readFailure);
        Clob clob = mock(Clob.class);
        when(clob.getCharacterStream()).thenReturn(reader);
        SQLException freeFailure = new SQLException("free failed", "58000", -999);
        doThrow(freeFailure).when(clob).free();
        try (var book = new XSSFWorkbook()) {
            var cell = book.createSheet().createRow(0).createCell(0);
            IOException error =
                    assertThrows(
                            IOException.class, () -> new ExcelValueWriter(book).write(cell, clob));
            assertSame(readFailure, error);
            assertArrayEquals(new Throwable[] {freeFailure}, error.getSuppressed());
        }
        verify(reader).close();
        verify(clob).free();
    }

    @Test
    void blobFreeFailureRetainsTheUnsupportedValueReason() throws Exception {
        Blob blob = mock(Blob.class);
        SQLException freeFailure = new SQLException("free failed", "58000", -999);
        doThrow(freeFailure).when(blob).free();
        try (var book = new XSSFWorkbook()) {
            var cell = book.createSheet().createRow(0).createCell(0);
            IOException error =
                    assertThrows(
                            IOException.class, () -> new ExcelValueWriter(book).write(cell, blob));
            assertTrue(error.getMessage().contains("BLOB export is unsupported"));
            assertArrayEquals(new Throwable[] {freeFailure}, error.getSuppressed());
        }
        verify(blob).free();
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/spreadsheet/ExcelTextTest.java =====
UTF8-BYTES: 5782
SHA256: fdc4cbf38274a2506a188035610304d672a04f892c0113a835d4f409bc6a7712
===== CONTENT =====
package com.example.db2toolkit.spreadsheet;

import static com.example.db2toolkit.support.JdbcRows.rows;

import static org.junit.jupiter.api.Assertions.*;

import com.example.db2toolkit.excel.*;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

class ExcelTextTest {
    @TempDir Path dir;

    private List<String> samples() {
        List<String> values =
                new ArrayList<>(
                        List.of(
                                "_x000D_",
                                "_x005F_x000D_",
                                "_x005F_",
                                "_x1234__xFFFF_",
                                "_X000D_",
                                "_xnothex_",
                                "A\uFFFEZ",
                                "A\uFFFFZ",
                                " A<&>\"'Z ",
                                "\uD83D\uDE00",
                                "=1+1",
                                "_x000D_".repeat(4681)));
        for (int c = 0; c < 32; c++) values.add("A" + (char) c + "Z");
        return values;
    }

    private ExcelExportTask task(String name) {
        return new ExcelExportTask(
                name, SourceType.OBJECT, "RPT.X", "text.xlsx", name, true, 1000, 0, true);
    }

    @Test
    void textAndHeadersSurviveStreamingFlushRolloverAndReadback() throws Exception {
        List<String> expected = new ArrayList<>();
        for (int i = 0; i < 6; i++) expected.addAll(samples());
        Object[][] data =
                expected.stream().map(value -> new Object[] {value}).toArray(Object[][]::new);
        Path output = dir.resolve("streaming.xlsx");
        ExcelExportTask task = task("DATA");
        try (var writer = new ExcelWorkbookWriter(List.of(task), 150)) {
            writer.write(rows(new String[] {"_x000D_"}, data), task, n -> {});
            writer.publish(output, true);
        }
        List<String> actual = new ArrayList<>();
        try (var book = new XSSFWorkbook(output.toFile())) {
            assertEquals(2, book.getNumberOfSheets());
            for (var sheet : book) {
                assertEquals("_x000D_", sheet.getRow(0).getCell(0).getStringCellValue());
                for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                    var cell = sheet.getRow(r).getCell(0);
                    assertEquals(CellType.STRING, cell.getCellType());
                    actual.add(cell.getStringCellValue());
                }
            }
        }
        assertEquals(expected, actual);
    }

    @Test
    void sameValueWriterPreservesXssfTextAtLogicalLengthLimit() throws Exception {
        Path output = dir.resolve("xssf.xlsx");
        List<String> expected = samples();
        try (var book = new XSSFWorkbook()) {
            var writer = new ExcelValueWriter(book);
            var sheet = book.createSheet();
            for (int i = 0; i < expected.size(); i++)
                writer.write(sheet.createRow(i).createCell(0), expected.get(i));
            try (var stream = Files.newOutputStream(output)) {
                book.write(stream);
            }
        }
        try (var book = new XSSFWorkbook(output.toFile())) {
            for (int i = 0; i < expected.size(); i++)
                assertEquals(
                        expected.get(i),
                        book.getSheetAt(0).getRow(i).getCell(0).getStringCellValue(),
                        "row " + i);
        }
    }

    @Test
    void malformedUnicodeFailsCurrentTaskBeforeFlushWithoutChangingOtherSheets() throws Exception {
        ExcelExportTask good = task("GOOD"), bad = task("BAD"), after = task("AFTER");
        Path output = dir.resolve("isolation.xlsx");
        try (var writer = new ExcelWorkbookWriter(List.of(good, bad, after))) {
            writer.write(rows(new String[] {"VALUE"}, new Object[][] {{"FIRST"}}), good, n -> {});
            for (String invalid : List.of("A\uD800B", "A\uDC00B", "A\uD800")) {
                IOException error =
                        assertThrows(
                                IOException.class,
                                () ->
                                        writer.write(
                                                rows(
                                                        new String[] {"VALUE"},
                                                        new Object[][] {{invalid}}),
                                                bad,
                                                n -> {}));
                assertTrue(error.getMessage().contains("unpaired Unicode surrogate"));
                assertEquals(1, writer.sheetCount());
            }
            assertThrows(
                    IOException.class,
                    () ->
                            writer.write(
                                    rows(new String[] {"\uD800"}, new Object[][] {{"data"}}),
                                    bad,
                                    n -> {}));
            writer.write(rows(new String[] {"VALUE"}, new Object[][] {{"LAST"}}), after, n -> {});
            writer.publish(output, true);
        }
        try (var book = new XSSFWorkbook(output.toFile())) {
            assertEquals(2, book.getNumberOfSheets());
            assertEquals("FIRST", book.getSheet("GOOD").getRow(1).getCell(0).getStringCellValue());
            assertEquals("LAST", book.getSheet("AFTER").getRow(1).getCell(0).getStringCellValue());
        }
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/sql/Db2TypeRendererTest.java =====
UTF8-BYTES: 2871
SHA256: 89785b25c27401736626f6d460f39b9268d6f4650a54fc408fc7a4844228c611
===== CONTENT =====
package com.example.db2toolkit.sql;


import static org.junit.jupiter.api.Assertions.*;

import com.example.db2toolkit.ddl.model.DdlUnavailableException;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.provider.*;

import java.nio.file.*;
import java.util.*;

class Db2TypeRendererTest {
    @TempDir Path dir;

    @Test
    void typesFollowCatalogSemantics() throws Exception {
        var r = new Db2TypeRenderer();
        assertEquals("INTEGER", r.render(type("INTEGER", 4, 0, null, null, 0)));
        assertEquals(
                "INTEGER",
                r.render(new Db2TypeRenderer.Type("SYSIBM  ", "INTEGER", 4, 0, null, null, 0)));
        assertEquals("DECIMAL(18,2)", r.render(type("DECIMAL", 18, 2, null, null, 0)));
        assertEquals("CHARACTER(1)", r.render(type("CHARACTER", 1, 0, "OCTETS", 1L, 1208)));
        assertEquals("CHARACTER(1)", r.render(type("CHAR", 1, 0, "OCTETS", 1L, 1208)));
        assertEquals("TIMESTAMP", r.render(type("TIMESTAMP", 13, 9, null, null, 0)));
        assertEquals("TIMESTAMP", r.render(type("TIMESTAMP", 10, 6, null, null, 0)));
        assertEquals("TIMESTAMP", r.render(type("TIMESTAMP", 14, 12, null, null, 0)));
        assertEquals("TIMESTAMP", r.render(type("TIMESTAMP", 7, 0, null, null, 0)));
        assertEquals("DECFLOAT(34)", r.render(type("DECFLOAT", 16, 0, null, null, 0)));
        assertEquals("VARCHAR(100)", r.render(type("VARCHAR", 400, 0, "CODEUNITS32", 100L, 1208)));
        assertEquals("VARCHAR(512)", r.render(type("VARCHAR", 512, 0, "OCTETS", 512L, 1208)));
        assertEquals("VARCHAR(40) FOR BIT DATA", r.render(type("VARCHAR", 40, 0, null, null, 0)));
        assertEquals("CLOB(1000 OCTETS)", r.render(type("CLOB", 1000, 0, "OCTETS", 1000L, 1208)));
        assertEquals("BLOB(2000)", r.render(type("BLOB", 2000, 0, null, null, 0)));
        assertEquals(
                "GRAPHIC(20 CODEUNITS16)",
                r.render(type("GRAPHIC", 20, 0, "CODEUNITS16", 20L, 1200)));
        assertThrows(
                DdlUnavailableException.class,
                () -> r.render(type("VARCHAR", 100, 0, "CODEUNITS32", null, 1208)));
        assertThrows(
                DdlUnavailableException.class,
                () -> r.render(type("DECIMAL", 5, 9, null, null, 0)));
        assertThrows(
                DdlUnavailableException.class,
                () -> r.render(type("UNKNOWN", 5, 0, null, null, 0)));
        assertThrows(
                DdlUnavailableException.class,
                () -> r.render(new Db2TypeRenderer.Type("CUSTOM", "INTEGER", 4, 0, null, null, 0)));
    }

    private static Db2TypeRenderer.Type type(String n, long l, int s, String u, Long ul, int cp) {
        return new Db2TypeRenderer.Type("SYSIBM", n, l, s, u, ul, cp);
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/sql/RoutineDefinitionValidatorTest.java =====
UTF8-BYTES: 2192
SHA256: 2cbc8433da0a927062f90f78eb3ac38d24d83bf5c6b0da7f6e5b4a344d1aad7f
===== CONTENT =====
package com.example.db2toolkit.sql;


import static org.junit.jupiter.api.Assertions.*;

import com.example.db2toolkit.ddl.model.DdlObjectType;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.provider.*;

import java.nio.file.*;
import java.util.*;

class RoutineDefinitionValidatorTest {
    @TempDir Path dir;

    @Test
    void longLeadingTriviaDoesNotOverflowAndIncompletePrefixIsRejected() {
        String ddl =
                " ".repeat(100000)
                        + "/*"
                        + "x".repeat(100000)
                        + "*/ CREATE FUNCTION S.F() RETURNS INT RETURN 1;";
        assertTrue(RoutineDefinitionValidator.hasCreateDefinition(DdlObjectType.FUNCTION, ddl));
        assertFalse(
                RoutineDefinitionValidator.hasCreateDefinition(
                        DdlObjectType.FUNCTION, "CREATE FUNCTION"));
        assertFalse(
                RoutineDefinitionValidator.hasCreateDefinition(
                        DdlObjectType.FUNCTION, "CREATE FUNCTION /* unfinished"));
        assertFalse(
                RoutineDefinitionValidator.hasCreateDefinition(
                        DdlObjectType.FUNCTION, "CREATE FUNCTION123 foo"));
    }

    @Test
    void routineDefinitionGuardPreservesBody() {
        assertTrue(
                RoutineDefinitionValidator.hasCreateDefinition(
                        DdlObjectType.FUNCTION,
                        "-- note\n/*x*/ CREATE OR REPLACE FUNCTION S.F() RETURNS INT RETURN 1;"));
        assertFalse(
                RoutineDefinitionValidator.hasCreateDefinition(
                        DdlObjectType.FUNCTION, "RETURN 1;"));
        assertFalse(
                RoutineDefinitionValidator.hasCreateDefinition(
                        DdlObjectType.FUNCTION, "/* CREATE FUNCTION */ RETURN 1;"));
        assertFalse(
                RoutineDefinitionValidator.hasCreateDefinition(
                        DdlObjectType.FUNCTION, "CREATE PROCEDURE S.P() BEGIN END;"));
        assertFalse(RoutineDefinitionValidator.hasCreateDefinition(DdlObjectType.FUNCTION, null));
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/sql/TableDdlFormatterTest.java =====
UTF8-BYTES: 2496
SHA256: a17db9f1a389a01a50116c100776e251684bcfb72c6f70a7b0af1d58b6dcd940
===== CONTENT =====
package com.example.db2toolkit.sql;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.provider.*;

import java.nio.file.*;
import java.util.*;

class TableDdlFormatterTest {
    @TempDir Path dir;

    @Test
    void tableColumnsAlignWithoutChangingDefaultExpressions() {
        String actual =
                TableDdlFormatter.formatColumns(
                        List.of(
                                new TableDdlFormatter.ColumnDefinition("ID", "INTEGER", "NOT NULL"),
                                new TableDdlFormatter.ColumnDefinition(
                                        "CITYNAME", "VARCHAR(50 OCTETS)", "DEFAULT 'a  b'"),
                                new TableDdlFormatter.ColumnDefinition(
                                        "VALIDFROM", "TIMESTAMP(12)", "")));
        assertEquals(
                "    ID        INTEGER            NOT NULL,\n"
                        + "    CITYNAME  VARCHAR(50 OCTETS) DEFAULT 'a  b',\n"
                        + "    VALIDFROM TIMESTAMP(12)",
                actual);
    }

    @Test
    void ordinaryTableIdentifiersDoNotNeedQuotes() {
        assertEquals("APPLICATION", TableDdlFormatter.tableIdentifier("APPLICATION"));
        assertEquals("CITIES_ARCHIVE", TableDdlFormatter.tableIdentifier("CITIES_ARCHIVE"));
        assertEquals("CITYID", TableDdlFormatter.tableIdentifier("CITYID"));
        assertEquals("\"CityId\"", TableDdlFormatter.tableIdentifier("CityId"));
        assertEquals("\"ORDER\"", TableDdlFormatter.tableIdentifier("ORDER"));
        assertEquals("\"A.B\"", TableDdlFormatter.tableIdentifier("A.B"));
    }

    @Test
    void specialRegisterNamesRetainIdentifierQuoting() {
        for (String name :
                List.of(
                        "CURRENT_USER",
                        "SESSION_USER",
                        "CURRENT_SCHEMA",
                        "CURRENT_SERVER",
                        "CURRENT_PATH",
                        "CURRENT_ROLE",
                        "SYSTEM_USER",
                        "CURRENT_TIMEZONE")) {
            assertEquals("\"" + name + "\"", TableDdlFormatter.tableIdentifier(name));
        }
    }

    @Test
    void localTimestampRetainsColumnIdentity() {
        assertEquals("\"LOCALTIMESTAMP\"", TableDdlFormatter.tableIdentifier("LOCALTIMESTAMP"));
    }
}

===== END FILE =====

===== FILE: src/test/java/com/example/db2toolkit/support/JdbcRows.java =====
UTF8-BYTES: 915
SHA256: 63ce898a59f1625536e0a7aaf81ab52ef94e715c64c38089644461f8f5449bc2
===== CONTENT =====
package com.example.db2toolkit.support;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.*;

public final class JdbcRows {
    private JdbcRows() {}

    public static ResultSet rows(String[] labels, Object[][] data) throws Exception {
        ResultSet rs = mock(ResultSet.class);
        ResultSetMetaData metadata = mock(ResultSetMetaData.class);
        when(rs.getMetaData()).thenReturn(metadata);
        when(metadata.getColumnCount()).thenReturn(labels.length);
        for (int i = 0; i < labels.length; i++)
            when(metadata.getColumnLabel(i + 1)).thenReturn(labels[i]);
        int[] cursor = {-1};
        when(rs.next()).thenAnswer(inv -> ++cursor[0] < data.length);
        when(rs.getObject(anyInt()))
                .thenAnswer(inv -> data[cursor[0]][inv.<Integer>getArgument(0) - 1]);
        return rs;
    }
}

===== END FILE =====

