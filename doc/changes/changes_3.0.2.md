# Virtual Schema for BigQuery 3.0.2, released 2024-04-10

Code name: Excluded vulnerabilities CVE-2024-23081, CVE-2024-23082

## Summary

We assume that google-cloud-storage uses the library correctly.
This release excludes the following 2 vulnerabilities:

### CVE-2024-23081 (CWE-476) in dependency `org.threeten:threetenbp:jar:1.6.8:test`
ThreeTen Backport v1.6.8 was discovered to contain a NullPointerException via the component org.threeten.bp.LocalDate::compareTo(ChronoLocalDate).
#### References
* https://ossindex.sonatype.org/vulnerability/CVE-2024-23081?component-type=maven&component-name=org.threeten%2Fthreetenbp&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2024-23081
* https://gist.github.com/LLM4IG/3cc9183dcd887020368a0bafeafec5e3

### CVE-2024-23082 (CWE-190) in dependency `org.threeten:threetenbp:jar:1.6.8:test`
ThreeTen Backport v1.6.8 was discovered to contain an integer overflow via the component org.threeten.bp.format.DateTimeFormatter::parse(CharSequence, ParsePosition).
#### References
* https://ossindex.sonatype.org/vulnerability/CVE-2024-23082?component-type=maven&component-name=org.threeten%2Fthreetenbp&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2024-23082
* https://gist.github.com/LLM4IG/d2618f5f4e5ac37eb75cff5617e58b90

## Security

* #35: Excluded vulnerability CVE-2024-23081 in dependency `org.threeten:threetenbp:jar:1.6.8:test`
* #36: Excluded vulnerability CVE-2024-23082 in dependency `org.threeten:threetenbp:jar:1.6.8:test`

