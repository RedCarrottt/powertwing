import java.awt.event.ActionListener;
 
// T/ODO User info
// T/ODO Direct message

public class TimelineListPopupMenuItem {
	private String name;
	private ActionListener listener;

	public TimelineListPopupMenuItem(String name, ActionListener listener) {
		this.name = name;
		this.listener = listener;
	}

	public String getName() {
		return name;
	}

	public ActionListener getActionListener() {
		return listener;
	}
}
