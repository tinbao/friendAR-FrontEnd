package tk.friendar.friendar;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test validation of meeting creation
 */

public class NewMeetingValidatorTest {

	@Test
	public void meetingNameValidator() {
		// Valid
		assertTrue(NewMeetingActivity.isValidMeetingName("Party"));
		assertTrue(NewMeetingActivity.isValidMeetingName("Massive Rad Day"));
		assertTrue(NewMeetingActivity.isValidMeetingName("96124612496"));
		assertTrue(NewMeetingActivity.isValidMeetingName("m's!? O_O';;,.//\\"));
		assertTrue(NewMeetingActivity.isValidMeetingName("L"));

		// Invalid
		assertFalse(NewMeetingActivity.isValidMeetingName(""));  // empty
		assertFalse(NewMeetingActivity.isValidMeetingName("     "));  // spaces
		assertFalse(NewMeetingActivity.isValidMeetingName("			"));  // tabs
		assertFalse(NewMeetingActivity.isValidMeetingName(" 	   	 		  	"));  // mix
		assertFalse(NewMeetingActivity.isValidMeetingName("asrhbghasgrasbrgkjsarbgkasrgikarjsbgkasjrbgkajsbrgkjsarbgkajsbgrkjsarbgkjasrngkjsarngkjasrgnkasjrgnkjasnrgkjasrngkjaksjgnrajrgnkasrngkasnrgkjasnrg"));

		assertTrue(false); // temp test if travis actually tests this file
	}
}
