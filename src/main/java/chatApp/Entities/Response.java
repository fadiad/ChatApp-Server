package chatApp.Entities;

import java.util.List;

public class Response {
    private String message;
    private List<Object> obj;

    public Response(String message, List<Object> obj) {
        this.message = message;
        this.obj = obj;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Object> getObj() {
        return obj;
    }

    public void setObj(List<Object> obj) {
        this.obj = obj;
    }
}
