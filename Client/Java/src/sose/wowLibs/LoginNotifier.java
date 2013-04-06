package src.sose.wowLibs;

/**
 * Interface which allows a LoginWindow to inform a higher level class of a failed or
 * successful login.
 */
public interface LoginNotifier {
	public void successfulLogin();
	public void loginFailed(String errorMessage);
}
