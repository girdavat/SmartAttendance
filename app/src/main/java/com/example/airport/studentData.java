package com.example.airport;

/**
 * Created by mert on 9.05.2016.
 */
public class studentData {
    long studentID;
    String studentName;
    String studentSurname;
    studentData(long studentID,String studentName,String studentSurname){
        this.studentID = studentID;
        this.studentName = studentName;
        this.studentSurname = studentSurname;
    }
    public long getStudentID(){
        return studentID;
    }
    public void setStudentID(long studentID){
        this.studentID=studentID;
    }
    public String getStudentName(){
        return studentName;
    }
    public void setStudentName(String studentName){
        this.studentName=studentName;
    }
    public String getStudentSurame(){
        return studentSurname;
    }
    public void setStudentSurname(String studentSurname){
        this.studentSurname=studentSurname;
    }
        
}
