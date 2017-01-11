
package cc.ak.sdk.update;

public interface CxCheckUpdateListener {

    public void cancelUpdateVersion();

    public void shouldUpdateVersion();

    public void updateVersionFailed(String message);
}
