package org.example.tools;
public class BaseRequest {
    private String id;

    public BaseRequest() {}

    public BaseRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}


