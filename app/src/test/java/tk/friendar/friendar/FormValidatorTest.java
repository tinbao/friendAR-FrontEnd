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

	@Test
	public void meetingNameValidator() {
		// Valid
		assertTrue(FormValidator.isValidMeetingName("Party"));
		assertTrue(FormValidator.isValidMeetingName("Massive Rad Day"));
		assertTrue(FormValidator.isValidMeetingName("96124612496"));
		assertTrue(FormValidator.isValidMeetingName("m's!? O_O';;,.//\\"));
		assertTrue(FormValidator.isValidMeetingName("L"));

		// Invalid
		assertFalse(FormValidator.isValidMeetingName(""));  // empty
		assertFalse(FormValidator.isValidMeetingName("     "));  // spaces
		assertFalse(FormValidator.isValidMeetingName("			"));  // tabs
		assertFalse(FormValidator.isValidMeetingName(" 	   	 		  	"));  // mix
		assertFalse(FormValidator.isValidMeetingName("asrhbghasgrasbrgkjsarbgkasrgikarjsbgkasjrbgkajsbrgkjsarbgkajsbgrkjsarbgkjasrngkjsarngkjasrgnkasjrgnkjasnrgkjasrngkjaksjgnrajrgnkasrngkasnrgkjasnrg"));
	}
}
