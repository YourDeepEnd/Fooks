package com.example.fooks.entity;

public class Book {
    private int id;
    private String BookName;
    private String BookPath;
    private String CreateUser;
    private String CreateDate;

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", BookName='" + BookName + '\'' +
                ", BookPath='" + BookPath + '\'' +
                ", CreateUser='" + CreateUser + '\'' +
                ", CreateDate='" + CreateDate + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getBookPath() {
        return BookPath;
    }

    public void setBookPath(String bookPath) {
        BookPath = bookPath;
    }

    public String getCreateUser() {
        return CreateUser;
    }

    public void setCreateUser(String createUser) {
        CreateUser = createUser;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }




    public Book() {

    }

    public Book(String bookName, String bookPath, String createUser, String createDate) {
        BookName = bookName;
        BookPath = bookPath;
        CreateUser = createUser;
        CreateDate = createDate;
    }

    public Book(int id, String bookName, String bookPath, String createUser, String createDate) {
        this.id = id;
        BookName = bookName;
        BookPath = bookPath;
        CreateUser = createUser;
        CreateDate = createDate;
    }
}
