# CS4801 DevOps Lab — Continuous Evaluation Examination

**Course:** CS4801 DevOps Lab  
**Institution:** Shiv Nadar University, Chennai — Department of Computer Science & Engineering  
**Question Set:** Set B — Maximum Number of Registered Courses  

---

## 1. Project Overview & Multi-Module Architecture

This project is a multi-module Maven-based Java application managing a Course Registration Service. The project has been refactored from a flat baseline structure into a structured parent-child POM setup supporting POM inheritance, centralized dependency management, automated JUnit 5 testing, CI/CD with Jenkins, and Kubernetes deployment on Minikube.

### Project Structure
```text
course-registration/
├── pom.xml                     # Parent POM (Multi-module & Dependency Management)
├── Jenkinsfile                 # Jenkins Declarative Pipeline Script
├── deployment.yaml            # Kubernetes Deployment Manifest (Nginx Replicas)
├── service.yaml               # Kubernetes NodePort Service Manifest
└── course-registration-app/    # Child Module
    ├── pom.xml                 # Child Module POM
    └── src/
        ├── main/java/com/university/
        │   ├── Course.java
        │   ├── Student.java
        │   └── RegistrationService.java
        └── test/java/com/university/
            ├── CourseTest.java
            └── RegistrationServiceTest.java
```

---

## 2. Maven Configuration

### Parent `pom.xml`
The parent POM manages shared properties, dependency versions via `<dependencyManagement>`, and global build plugins (`maven-compiler-plugin` and `maven-surefire-plugin`).

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.university</groupId>
    <artifactId>course-registration-parent</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <junit.version>5.11.0</junit.version>
    </properties>

    <modules>
        <module>course-registration-app</module>
    </modules>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.junit.jupiter</groupId>
                <artifactId>junit-jupiter</artifactId>
                <version>${junit.version}</version>
                <scope>test</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.5.0</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### Child Module `course-registration-app/pom.xml`
The child POM inherits configurations from the parent POM without duplicating version declarations for managed dependencies.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.university</groupId>
        <artifactId>course-registration-parent</artifactId>
        <version>1.0-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <artifactId>course-registration-app</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

---

## 3. Local Setup and Execution Instructions

Run all commands from the root directory (`course-registration/`):

1. **Compile and Run Tests:**
   ```bash
   mvn clean test
   ```
2. **Package Application:**
   ```bash
   mvn package
   ```
3. **Deploy to Minikube:**
   ```bash
   kubectl apply -f deployment.yaml
   kubectl apply -f service.yaml
   ```

---

## 4. Git Workflow & History Evidence

All tasks were developed on a feature branch named `course-reg-bug` and merged into `main` using a non-fast-forward merge (`--no-ff`).

### Branch Switch & Tracking
```text
C:\Users\snucse\Desktop\Lab_CIA2\Student-Starter> git switch -c course-reg-bug
Switched to a new branch 'course-reg-bug'

C:\Users\snucse\Desktop\Lab_CIA2\Student-Starter> git branch
* course-reg-bug
  main
```

### Git Log (`git log --oneline`)
```text
1622108 (HEAD -> main) Merge bugs and defects in course registration
139201f (course-reg-bug) Restricted students to five registered courses
00214f3 Added course capacity tests
010751f Fixed course capacity check
cef2bff Initial student assessment starter
```

---

## 5. Defect Correction & Feature Summary

### Task 3 — Capacity Boundary Defect Fix
* **Defect:** Registration incorrectly allowed students to register beyond course capacity due to an improper boundary check.
* **Fix:** Corrected the boundary check in `RegistrationService.java`:
  ```java
  if (course.getCurrentEnrollment() >= course.getCapacity()) {
      return false;
  }
  ```
* **Test Coverage:** Added `shouldAllowRegistrationWhenOneSeatRemains()` and `shouldRejectRegistrationWhenCourseIsFullWithoutChangingState()`. Tested full state preservation upon rejection.

### Task 4 — Assigned Change Request (Question Set B)
* **Requirement:** Limit maximum registered courses per student to **5**, regardless of credit limits.
* **Implementation in `RegistrationService.java`:**
  ```java
  private static final int MAX_COURSES = 5;

  if (student.getRegisteredCourses().size() >= MAX_COURSES) {
      return false;
  }
  ```
* **Test Coverage:**
  * `shouldAllowFifthCourseRegistration()`: Ensures 5th registration succeeds.
  * `shouldRejectSixthCourseWithoutChangingState()`: Ensures 6th registration fails without mutating total credits, registered course list, or enrollment count.

### Test Execution Summary
* **Total Tests Executed:** 13
* **Failures:** 0
* **Errors:** 0
* **Skipped:** 0
* **Status:** `BUILD SUCCESS`

---

## 6. Jenkins Declarative Pipeline

The project includes a declarative `Jenkinsfile` targeting a Windows agent environment using `bat` steps.

### `Jenkinsfile`
```groovy
pipeline {
    agent any
    stages {
        stage('Build and Test') {
            steps {
                bat 'mvn clean test'
            }
        }
        stage('Package') {
            steps {
                bat 'mvn package'
            }
        }
    }
    post {
        always {
            junit 'course-registration-app/target/surefire-reports/TEST-*.xml'
        }
        success {
            archiveArtifacts artifacts: 'course-registration-app/target/*.jar', fingerprint: true
        }
    }
}
```

### Pipeline Flow & Execution Logic
* **Failure Handling:** If `mvn clean test` fails during `Build and Test`, Jenkins halts execution. The `Package` stage is skipped, ensuring bad builds are never packaged or archived.
* **Report Publishing:** The `always` block guarantees JUnit test results are archived in Jenkins under `course-registration-app/target/surefire-reports/TEST-*.xml` even if tests fail.
* **Artifact Archiving:** JAR files inside `course-registration-app/target/*.jar` are fingerprinted and archived only upon build success (`success` block).

---

## 7. Kubernetes Deployment (Minikube)

### Deployment Manifest (`deployment.yaml`)
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  replicas: 2
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.27
        ports:
        - containerPort: 80
```

### Service Manifest (`service.yaml`)
```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
spec:
  type: NodePort
  selector:
    app: nginx
  ports:
  - port: 80
    targetPort: 80
    nodePort: 30080
```

### Scaling & Self-Healing Verification

1. **Scaling to 3 Replicas:**
   ```bash
   kubectl scale deployment nginx-deployment --replicas=3
   ```
   *Output:*
   ```text
   deployment.apps/nginx-deployment scaled

   NAME               READY   UP-TO-DATE   AVAILABLE   AGE
   nginx-deployment   3/3     3            3           45s
   ```

2. **Self-Healing Demonstration:**
   Deleting a running pod triggers the Deployment Controller to automatically spin up a replacement pod to maintain the desired count of 3 replicas:
   ```bash
   kubectl delete pod nginx-deployment-75cc477c85-8qdm8
   ```
   *Status Monitoring (`kubectl get pods -w`):*
   ```text
   NAME                                READY   STATUS    RESTARTS   AGE
   nginx-deployment-75cc477c85-4qpqh   1/1     Running   0          2m16s
   nginx-deployment-75cc477c85-52wzw   1/1     Running   0          2s
   ```

### Explanation of Self-Healing Mechanics
The Kubernetes **Deployment Controller** continuously compares the observed cluster state against the desired state defined in the spec (`replicas: 3`). When a pod is deleted, the replica count drops to 2. The controller reconciles this state mismatch by requesting the `ReplicaSet` to schedule and initialize a replacement pod (`nginx-deployment-75cc477c85-52wzw`) immediately.

---

