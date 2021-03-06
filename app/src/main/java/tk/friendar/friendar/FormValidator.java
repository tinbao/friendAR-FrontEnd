package tk.friendar.friendar;

/**
 * Validate common form entries
 */

public class FormValidator {
	public static final int MAX_USERNAME_LENGTH = 80;
	private static final int MAX_MEETING_NAME_LENGTH = 80;

	public static boolean isValidUsername(String s) {
		if (s.isEmpty() || s.length() > MAX_USERNAME_LENGTH || justWhitespace(s)) {
			return false;
		}
		for (char c : s.toCharArray()) {
			if (!legalUsernameChar(c)) return false;
		}
		return true;
	}

	public static boolean isValidMeetingName(String name) {
		if (name.length() <= 0 || name.length() > MAX_MEETING_NAME_LENGTH) return false;
		if (name.trim().length() == 0) return false;
		return true;
	}

	private static boolean justWhitespace(String s) {
		return (s.trim().length() == 0);
	}

	private static boolean legalUsernameChar(char c) {
		return (Character.isDigit(c) || Character.isAlphabetic(c) || c == '_');
	}
}
