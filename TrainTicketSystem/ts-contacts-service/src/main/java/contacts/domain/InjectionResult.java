package contacts.domain;

public class InjectionResult {

    boolean status;

    public InjectionResult(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
