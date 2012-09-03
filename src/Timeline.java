import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import powertwing.supports.LabelTextReformer;
import twitter4j.Status;

class TimelineListEntryEndPoint extends TimelineListEntry {
	private String text;

	public TimelineListEntryEndPoint(String text) {
		this.text = text;
	}

	@Override
	public ImageIcon getImage() {
		return null;
	}

	@Override
	public String getText() {
		return text;
	}
}

class TimelineListEntryStatus extends TimelineListEntry {
	private Status status;
	private ImageIcon image;

	public TimelineListEntryStatus(Status status) {
		this.status = status;
		this.image = new ImageIcon(status.getUser().getProfileImageURL());
	}

	@Override
	public String getText() {
		Dimension mainSize = MainFrameController.getSingleton().mainFrame
				.getSize();
		int mainWidth = (int) mainSize.getWidth();
		// int mainHeight = (int) mainSize.getHeight();
		int diffWidth = mainWidth - MainFrameController.WINDOW_INIT_WIDTH;
		// int diffHeight = mainHeight - frameCon.WINDOW_INIT_HEIGHT;
		int textWidth = MainFrameController.TIMELINELIST_TEXT_INIT_WIDTH
				+ diffWidth;
		if (textWidth < MainFrameController.TIMELINELIST_TEXT_MIN_WIDTH) {
			textWidth = MainFrameController.TIMELINELIST_TEXT_MIN_WIDTH;
		}
		int fontSize = 13;
		int charsPerLine = textWidth * 2 / fontSize - 2;
		// T/ODO WORD WRAP
		return "<html><div style='width:"
				+ textWidth
				+ "; margin:5px 0px 5px 0px; word-wrap:break-all; font-size:"
				+ fontSize
				+ ";'>"
				+ "<u>"
				//+ status.getUser().getName()
				+ LabelTextReformer.reformText(status.getUser().getName(),
						charsPerLine)
				+ "</u>" + "<br />"
				//+ status.getText()
				+ LabelTextReformer.reformText(status.getText(), charsPerLine)
				+ "</div></html>";
	}

	public ImageIcon getImage() {
		if (this.image == null) {
			return null;
		}
		return this.image;
	}

	public Status getStatus() {
		return status;
	}
}

abstract class TimelineListEntry {
	abstract public String getText();

	abstract public ImageIcon getImage();

	public String toString() {
		return getText();
	}
}

public class Timeline {
	private ArrayList<TimelineListEntry> entries = new ArrayList<TimelineListEntry>();

	public Timeline() {
		entries.add(new TimelineListEntryEndPoint("Show more tweets..."));
	}

	public void addStatusesFirst(List<Status> statuses) {
		if (statuses.isEmpty() == true) {
			return;
		}
		for (int i = statuses.size() - 1; i >= 0; i--) {
			Status s = statuses.get(i);
			entries.add(0, new TimelineListEntryStatus(s));
		}
	}

	public void addStatusesLast(List<Status> statuses) {
		if (statuses.isEmpty() == true) {
			return;
		}
		for (Status s : statuses) {
			entries.add(entries.size() - 1, new TimelineListEntryStatus(s));
		}
	}

	public void removeEntry(TimelineListEntry delEntry) {
		if (delEntry instanceof TimelineListEntryEndPoint == true)
			return;
		entries.remove(delEntry);
	}

	public TimelineListEntry getEntryAtRow(int row) {
		return entries.get(row);
	}

	public int getIndexOf(TimelineListEntry entry) {
		return entries.indexOf(entry);
	}

	public ArrayList<TimelineListEntry> getEntries() {
		return entries;
	}

	public TimelineListEntryStatus getEntryStatus(Status status) {
		for (TimelineListEntry entry : entries) {
			if (entry instanceof TimelineListEntryStatus == true) {
				TimelineListEntryStatus entryStatus = (TimelineListEntryStatus) entry;
				if (entryStatus.getStatus().equals(status) == true) {
					return entryStatus;
				}
			}
		}
		return null;
	}

	public long getMostRecentStatusId() {
		if (entries.isEmpty())
			return -1;
		else {
			TimelineListEntry mostRecentEntry = entries.get(0);
			if (mostRecentEntry instanceof TimelineListEntryStatus) {
				TimelineListEntryStatus mostRecentEntryStatus = (TimelineListEntryStatus) mostRecentEntry;
				return mostRecentEntryStatus.getStatus().getId();
			} else {
				return -1;
			}
		}
	}

	public long getLeastRecentStatusId() {
		if (entries.size() < 2)
			return -1;
		else {
			TimelineListEntry leastRecentEntry = entries
					.get(entries.size() - 2);
			if (leastRecentEntry instanceof TimelineListEntryStatus) {
				TimelineListEntryStatus leastRecentEntryStatus = (TimelineListEntryStatus) leastRecentEntry;
				return leastRecentEntryStatus.getStatus().getId();
			} else {
				return -1;
			}
		}
	}
}