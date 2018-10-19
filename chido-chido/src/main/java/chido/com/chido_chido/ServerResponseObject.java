package chido.com.chido_chido;

public class ServerResponseObject {
   private  boolean success ;
   private String message;
   private String ussdString;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUssdString() {
        return ussdString;
    }

    public void setUssdString(String ussdString) {
        this.ussdString = ussdString;
    }
}
