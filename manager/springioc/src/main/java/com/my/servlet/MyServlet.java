package com.my.servlet;


import com.my.pojp.Person;
import org.springframework.beans.factory.annotation.Autowired;

public class MyServlet implements BaseServlet {
    private Person person;

    @Override
    public void run() {
        System.out.println("MyServlet run");
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    private void init() {
        System.out.println("MyServlet run");
    }
}
