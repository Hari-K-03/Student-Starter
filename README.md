\# Course Registration System



\## Project Structure



This project uses a multi-module Maven structure.



\- \*\*Parent:\*\* `course-registration-parent` — manages shared properties, JUnit dependency versions, and build plugins.

\- \*\*Child:\*\* `course-registration-app` — contains the application source code and unit tests.

\- \*\*Java version:\*\* 17

\- \*\*JUnit version:\*\* 5.11.0



\## Build and Test



Run these commands from the project root:



```bash

mvn clean test

mvn package

```



\- `mvn clean test` cleans previous build output and runs unit tests.

\- `mvn package` runs the build lifecycle, including tests, and packages the application into a JAR file.



