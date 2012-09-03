// Done Requirement : "Timeline", "Mentions"
// T/ODO Requirements : "Direct Messages", "List", "My Info", "Settings"

class ContentMenuItemMentions extends ContentMenuItemLoadable {
	public ContentMenuItemMentions(String name, MainFrameController frameCon) {
		super(name, frameCon);
	}

	public void startRefreshThread() {
		new RefreshMentionsTimelineThread(frameCon).start();
	}

	public void startLoadMoreThread() {
		new LoadMoreMentionsTimelineThread(frameCon).start();
	}
}

class ContentMenuItemTimeline extends ContentMenuItemLoadable {
	public ContentMenuItemTimeline(String name, MainFrameController frameCon) {
		super(name, frameCon);
	}

	public void startRefreshThread() {
		new RefreshHomeTimelineThread(frameCon).start();
	}

	public void startLoadMoreThread() {
		new LoadMoreHomeTimelineThread(frameCon).start();
	}
}

abstract class ContentMenuItemLoadable extends ContentMenuItem {
	MainFrameController frameCon;

	public ContentMenuItemLoadable(String name, MainFrameController frameCon) {
		super(name);
		this.frameCon = frameCon;
	}

	abstract public void startRefreshThread();

	abstract public void startLoadMoreThread();
}

public abstract class ContentMenuItem {
	private String name;

	public ContentMenuItem(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}