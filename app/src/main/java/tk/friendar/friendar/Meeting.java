package tk.friendar.friendar;

/**
 * Created by lucah on 19/9/17.
 */

public class Meeting {
	private String name;
	private int id;
	private String timeStamp;

	public Meeting(String name, int id, String timeStamp) {
		this.name = name;
		this.id = id;
		this.timeStamp = timeStamp;
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
