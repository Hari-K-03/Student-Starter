package com.university;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationServiceTest {

    @Test
    void shouldRegisterStudentForValidCourse() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                true,
                null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS301"));
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectInactiveCourse() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                false,
                null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
    }

    @Test
    void shouldRejectMissingPrerequisite() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS401",
                "Advanced Algorithms",
                4,
                30,
                true,
                "CS301"
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS401"));
    }

    @Test
    void shouldAllowRegistrationWhenPrerequisiteIsCompleted() {

        Student student = new Student("S101", "Anita");
        student.addCompletedPrerequisite("CS301");

        Course course = new Course(
                "CS401",
                "Advanced Algorithms",
                4,
                30,
                true,
                "CS301"
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS401"));
    }

    @Test
    void shouldRejectDuplicateRegistration() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                true,
                null
        );

        RegistrationService service = new RegistrationService();

        assertTrue(service.register(student, course));

        boolean secondAttempt = service.register(student, course);

        assertFalse(secondAttempt);
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectRegistrationWhenCreditLimitIsExceeded() {

        Student student = new Student("S101", "Anita");

        // Simulate 20 existing credits
        student.addRegistration("CS101", 4);
        student.addRegistration("CS102", 4);
        student.addRegistration("CS201", 4);
        student.addRegistration("CS202", 4);
        student.addRegistration("CS203", 4);

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                true,
                null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
    }

    
    @Test
    void shouldAllowRegistrationWhenOneSeatRemains() {

        Student student = new Student("S102", "Rahul");

        Course course = new Course(
                "CS302",
                "Operating Systems",
                4,
                30,
                true,
                null
        );

        // Fill 29 of the 30 available seats
        for (int i = 0; i < 29; i++) {
            course.incrementEnrollment();
        }

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS302"));
        assertEquals(4, student.getTotalCredits());
        assertEquals(30, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectRegistrationWhenCourseIsFullWithoutChangingState() {

        Student student = new Student("S103", "Priya");

        Course course = new Course(
                "CS303",
                "Computer Networks",
                4,
                30,
                true,
                null
        );

        // Fill all 30 seats
        for (int i = 0; i < 30; i++) {
            course.incrementEnrollment();
        }

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS303"));
        assertEquals(0, student.getTotalCredits());
        assertEquals(30, course.getCurrentEnrollment());
    }

    
    @Test
    void shouldAllowFifthCourseRegistration() {

        Student student = new Student("S104", "Arun");
        RegistrationService service = new RegistrationService();

        for (int i = 1; i <= 4; i++) {
            Course course = new Course(
                    "CS" + (300 + i),
                    "Course " + i,
                    4,
                    30,
                    true,
                    null
            );

            assertTrue(service.register(student, course));
        }

        Course fifthCourse = new Course(
                "CS305",
                "Course 5",
                4,
                30,
                true,
                null
        );

        assertTrue(service.register(student, fifthCourse));
        assertEquals(5, student.getRegisteredCourses().size());
        assertEquals(20, student.getTotalCredits());
        assertEquals(1, fifthCourse.getCurrentEnrollment());
    }

    @Test
    void shouldRejectSixthCourseWithoutChangingState() {

        Student student = new Student("S105", "Meera");
        RegistrationService service = new RegistrationService();

        for (int i = 1; i <= 5; i++) {
            Course course = new Course(
                    "CS" + (400 + i),
                    "Course " + i,
                    2,
                    30,
                    true,
                    null
            );

            assertTrue(service.register(student, course));
        }

        Course sixthCourse = new Course(
                "CS406",
                "Course 6",
                2,
                30,
                true,
                null
        );

        int creditsBefore = student.getTotalCredits();
        int coursesBefore = student.getRegisteredCourses().size();
        int enrollmentBefore = sixthCourse.getCurrentEnrollment();

        boolean result = service.register(student, sixthCourse);

        assertFalse(result);
        assertEquals(coursesBefore, student.getRegisteredCourses().size());
        assertEquals(creditsBefore, student.getTotalCredits());
        assertFalse(student.isRegistered("CS406"));
        assertEquals(enrollmentBefore, sixthCourse.getCurrentEnrollment());
    }

}