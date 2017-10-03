package tk.friendar.friendar;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test validation of usernames
 */

public class FormValidatorTest {
	@Test
	public void testUsernames() {
		// valid
		assertTrue(FormValidator.isValidUsername("lucatron"));
		assertTrue(FormValidator.isValidUsername("MikeWazowski"));
		assertTrue(FormValidator.isValidUsername("a"));
		assertTrue(FormValidator.isValidUsername("jake_rodkin"));
		assertTrue(FormValidator.isValidUsername("super1337"));

		// invalid
		assertFalse(FormValidator.isValidUsername("luca harris"));
		assertFalse(FormValidator.isValidUsername("     "));
		assertFalse(FormValidator.isValidUsername("				"));
		assertFalse(FormValidator.isValidUsername("woah!?"));
	}
}
