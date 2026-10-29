# Virtual Schema for BigQuery 5.0.0, released 2026-??-??

Code name: Security fixes

## Summary

This update fixes 19 dependency vulnerabilities and records one finding awaiting a matching repository issue.

- Fixes #69
- Fixes #70
- Fixes #72
- Fixes #74
- Fixes #75
- Fixes #76
- Fixes #77
- Fixes #79
- Fixes #80
- Fixes #82
- Fixes #83
- Fixes #84
- Fixes #85
- Fixes #86
- Fixes #87
- Fixes #88
- Fixes #89
- Fixes #90
- Fixes #92

## Dependency Updates

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:5.7.4` to `5.8.2`

## Security

* #74: Fixed CVE-2026-68498 (unbounded resource allocation) in dependency `com.fasterxml.jackson.core:jackson-core:jar:2.22.1:test`
* #79: Fixed CVE-2026-89407 (quadratic-time number validation) in dependency `com.fasterxml.jackson.core:jackson-core:jar:2.22.1:test`
* #80: Fixed CVE-2026-89425 (unbounded error-token accumulation) in dependency `com.fasterxml.jackson.core:jackson-core:jar:2.22.1:test`
* #90: Fixed CVE-2026-102554 (unsafe deserialization) in dependency `com.google.guava:guava:jar:33.5.0-jre:test`
* #88: Fixed CVE-2026-110262 (unbounded resource allocation) in dependency `org.ow2.asm:asm-commons:jar:9.9:test`
* #89: Fixed CVE-2026-110270 (algorithmic complexity) in dependency `org.ow2.asm:asm-commons:jar:9.9:test`
* #82: Fixed CVE-2026-110257 (uncontrolled memory allocation) in dependency `org.ow2.asm:asm:jar:9.9:test`
* #83: Fixed CVE-2026-110258 (uncontrolled memory allocation) in dependency `org.ow2.asm:asm:jar:9.9:test`
* #84: Fixed CVE-2026-110259 (uncontrolled recursion) in dependency `org.ow2.asm:asm:jar:9.9:test`
* #85: Fixed CVE-2026-110260 (algorithmic complexity) in dependency `org.ow2.asm:asm:jar:9.9:test`
* #86: Fixed CVE-2026-110261 (algorithmic complexity) in dependency `org.ow2.asm:asm:jar:9.9:test`
* #87: Fixed CVE-2026-110263 (uncontrolled recursion) in dependency `org.ow2.asm:asm:jar:9.9:test`
* #92: Fixed CVE-2026-110879 (unsafe deserialization) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`
* #69: Fixed CVE-2026-19032 (unsafe URI scheme selection) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`
* #70: Fixed CVE-2026-68497 (unbounded resource allocation) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`
* #75: Fixed CVE-2026-91776 (unbounded type ID cache growth) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`
* #76: Fixed CVE-2026-91777 (denial of service) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`
* #77: Fixed CVE-2026-83557 (unsafe deserialization) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`
* #72: Fixed CVE-2026-86231 (certificate revocation validation) in dependency `com.github.mwiede:jsch:jar:2.28.4:test`
* CVE-2026-116486 (incorrect symbolic name mapping) in dependency `org.objenesis:objenesis:jar:3.3:test`
