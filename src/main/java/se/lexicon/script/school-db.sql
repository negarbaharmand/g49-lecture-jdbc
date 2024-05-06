create database school_db;
use school_db;

create table student(
student_id int primary key auto_increment,
first_name varchar(50),
last_name varchar(50),
age int,
email varchar(100),
create_date datetime default now()
);

-- Insert sample data into the student table
insert into student(first_name, last_name, age, email) values
('John', 'Doe', 20, 'john.de@example.com'),
('Alice', 'Smith', 22, 'alice.smith@example.com'),
('Bob', 'Johnson', 21, 'bob.johnson@example.com'),
('Emily', 'Davis', 23, 'emily.davis@example.com')
;

select * from student;

create table course(
course_id int primary key auto_increment,
course_name varchar(100)
);

insert into course (course_name) values
('Mathematics'),
('History'),
('Computer science');

select * from course;

-- 1  1, 1  2, 1  1
create table students_courses(
primary key(student_id, course_id),
-- id int primary key auto_increment,
student_id int,
course_id int,
foreign key (student_id) references student(student_id),
foreign key (course_id) references course(course_id)

);

select * from students_courses;
select * from student;





