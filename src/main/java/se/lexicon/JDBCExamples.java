package se.lexicon;

import se.lexicon.db.MySQLConnection;
import se.lexicon.model.Student;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JDBCExamples {

    public static void main(String[] args) {
        ex4();
    }


    //Get all student data (finAll)
    public static void ex1() {
        try {
            // Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/school_db", "root", "1234");
            Connection connection = MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from student");

            List<Student> studentList = new ArrayList<>();

            while (resultSet.next()) {
                int studentId = resultSet.getInt(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                int age = resultSet.getInt(4);
                String email = resultSet.getString(5);
                LocalDate createDate = resultSet.getDate(6).toLocalDate();

                Student student = new Student(studentId, firstName, lastName, age, email, createDate);
                studentList.add(student);
            }

            studentList.forEach(System.out::println);

        } catch (SQLException e) {
            System.out.println("SQL Exception: ");
            e.printStackTrace();
        }

    }


    //Get students by their id (findById)
    public static void ex2() {
        //step 1: Create a database connection
        try (
                Connection connection = MySQLConnection.getConnection();
                //step 2: Create prepared statement
                PreparedStatement preparedStatement = connection.prepareStatement("select * from student where student_id = ?");
        ) {

            int inputId = 1;
            preparedStatement.setInt(1, inputId);
            try (
                    //step 3: Execute query
                    ResultSet resultSet = preparedStatement.executeQuery();
            ) {
                //step 4: Get data from result set and add it to Java object
                Student student = null;
                if (resultSet.next()) {
                    int studentId = resultSet.getInt(1);
                    String firstName = resultSet.getString(2);
                    String lastName = resultSet.getString(3);
                    int age = resultSet.getInt(4);
                    String email = resultSet.getString(5);
                    LocalDate createDate = resultSet.getDate(6).toLocalDate();
                    student = new Student(studentId, firstName, lastName, age, email, createDate);
                }
                System.out.println(student);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


        //step 5: Close all the resources
        // Java will close -> first resultSet, preparedStatement, connection

    }


    //Create a student (create)
    public static void ex3() {
        Student student = new Student("Test", "Test", 22, "test@test.se");
        String query = "insert into student(first_name, last_name, age, email) values(?, ?, ?, ?)";
        try (
                Connection connection = MySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        ) {

            preparedStatement.setString(1, student.getFirstName());
            preparedStatement.setString(2, student.getLastName());
            preparedStatement.setInt(3, student.getAge());
            preparedStatement.setString(4, student.getEmail());

            //Execute insert query
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Student created successfully.");
            }

            try (
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            ) {
                if (generatedKeys.next()) {
                    int generatedStudentId = generatedKeys.getInt(1);
                    System.out.println("Generated Student Id : " + generatedStudentId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create student + assign student to a course
    public static void ex4() {
        Student student = new Student("Test2", "Test2", 22, "test2@test.se");
        String insertQueryForStudent = "insert into student(first_name, last_name, age, email) values(?, ?, ?, ?)";
        String insertQueryForStudentsCourses = "insert into students_courses(student_id, course_id) values(?,?)";
        try (
                Connection connection = MySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(insertQueryForStudent, PreparedStatement.RETURN_GENERATED_KEYS);
        ) {

            // Setup a transaction
            connection.setAutoCommit(false);

            preparedStatement.setString(1, student.getFirstName());
            preparedStatement.setString(2, student.getLastName());
            preparedStatement.setInt(3, student.getAge());
            preparedStatement.setString(4, student.getEmail());

            //Execute insert query
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Student created successfully.");
            }else {
                connection.rollback();
                throw new IllegalArgumentException("Insert operation is student table failed.");
            }
            try (
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            ) {
                if (generatedKeys.next()) {
                    int generatedStudentId = generatedKeys.getInt(1);
                    System.out.println("Generated Student Id : " + generatedStudentId);

                    int courseId = 1;
                    try(
                            PreparedStatement preparedStatementForStudentCourses = connection.prepareStatement(insertQueryForStudentsCourses);

                    ){
                        preparedStatementForStudentCourses.setInt(1, generatedStudentId);
                        preparedStatementForStudentCourses.setInt(2, courseId);

                        int insertedRows = preparedStatementForStudentCourses.executeUpdate();
                        if(insertedRows > 0){
                            System.out.println("Assigning course to student is done successfully.");
                        }else {
                            connection.rollback();
                            throw new IllegalArgumentException("Insert operation is students_courses table failed.");
                        }
                    }
                }
            }

            connection.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}











