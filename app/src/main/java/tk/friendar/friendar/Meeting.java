package tk.friendar.friendar;

/**
 * Created by lucah on 19/9/17.
 */

public class Meeting {
	private String name;

	public Meeting(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
