package tk.friendar.friendar;

/**
 * Created by lucah on 19/9/17.
 */

public class Meeting {
	private String name;
	private int id;

	public Meeting(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return name;
	}
}
