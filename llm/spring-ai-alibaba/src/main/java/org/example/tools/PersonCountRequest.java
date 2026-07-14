package org.example.tools;
public class PersonCountRequest {
    private String city;

    public PersonCountRequest() {}

    public PersonCountRequest(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}


