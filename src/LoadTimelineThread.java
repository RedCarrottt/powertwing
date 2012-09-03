import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

// T/ODO refresh user info
// T/ODO refresh list
// T/ODO refresh direct messages 

// **** Load More Timeline Threads ****
class LoadMoreMentionsTimelineThread extends LoadMoreTimelineThread {
	static int pageNum = 1;

	public LoadMoreMentionsTimelineThread(MainFrameController frameCon) {
		super(frameCon, frameCon.mentionTimeline);
	}

	@Override
	protected List<Status> getStatuses() throws TwitterException {
		Twitter twitter = AuthController.twitter;
		AccessToken accessToken = AuthController.accessToken;
		twitter.setOAuthAccessToken(accessToken);
		long leastRecentId = targetTimeline.getLeastRecentStatusId();
		Paging paging;
		if (leastRecentId > 0) {
			pageNum++;
			paging = new Paging(pageNum, 20);
		} else {
			paging = new Paging();
			paging.setCount(20);
		}
		statuses = twitter.getMentions(paging);
		return statuses;
	}
}

class LoadMoreHomeTimelineThread extends LoadMoreTimelineThread {
	static int pageNum = 1;

	public LoadMoreHomeTimelineThread(MainFrameController frameCon) {
		super(frameCon, frameCon.homeTimeline);
	}

	@Override
	protected List<Status> getStatuses() throws TwitterException {
		Twitter twitter = AuthController.twitter;
		AccessToken accessToken = AuthController.accessToken;
		twitter.setOAuthAccessToken(accessToken);
		long leastRecentId = targetTimeline.getLeastRecentStatusId();
		Paging paging;
		if (leastRecentId > 0) {
			pageNum++;
			paging = new Paging(pageNum, 20);
		} else {
			paging = new Paging();
			paging.setCount(20);
		}
		statuses = twitter.getHomeTimeline(paging);
		return statuses;
	}
}

abstract class LoadMoreTimelineThread extends LoadTimelineThread {
	protected List<Status> statuses;

	public LoadMoreTimelineThread(MainFrameController frameCon,
			Timeline targetTimeline) {
		super(frameCon, targetTimeline);
	}

	abstract protected List<Status> getStatuses() throws TwitterException;

	public void run() {
		try {
			List<Status> statuses = getStatuses();
			if (frameCon.isRefreshable(refreshId)) {
				while (loading == true)
					;
				loading = true;
				targetTimeline.addStatusesLast(statuses);
				frameCon.refreshTimelineList(targetTimeline);
				frameCon.endRefresh();
				loading = false;
			}
		} catch (TwitterException e) {
			new PTDialog(frameCon.mainFrame, e.getErrorMessage());
		}
	}
}

// **** ****

// **** Refresh Timeline Threads ****
class RefreshMentionsTimelineThread extends RefreshTimelineThread {

	public RefreshMentionsTimelineThread(MainFrameController frameCon) {
		super(frameCon, frameCon.mentionTimeline);
	}
	protected List<Status> getStatuses() throws TwitterException {
		Twitter twitter = AuthController.twitter;
		AccessToken accessToken = AuthController.accessToken;
		twitter.setOAuthAccessToken(accessToken);
		long mostRecentId = targetTimeline.getMostRecentStatusId();
		Paging paging;
		if (mostRecentId > 0)
			paging = new Paging(mostRecentId);
		else {
			paging = new Paging();
			paging.setCount(20);
		}
		statuses = twitter.getMentions(paging);
		return statuses;
	}
}

class RefreshHomeTimelineThread extends RefreshTimelineThread {

	public RefreshHomeTimelineThread(MainFrameController frameCon) {
		super(frameCon, frameCon.homeTimeline);
	}

	protected List<Status> getStatuses() throws TwitterException {
		Twitter twitter = AuthController.twitter;
		AccessToken accessToken = AuthController.accessToken;
		twitter.setOAuthAccessToken(accessToken);
		long mostRecentId = targetTimeline.getMostRecentStatusId();
		Paging paging;
		if (mostRecentId > 0)
			paging = new Paging(mostRecentId);
		else {
			paging = new Paging();
			paging.setCount(20);
		}
		statuses = twitter.getHomeTimeline(paging);
		return statuses;
	}
}

abstract class RefreshTimelineThread extends LoadTimelineThread {
	protected List<Status> statuses;

	public RefreshTimelineThread(MainFrameController frameCon,
			Timeline targetTimeline) {
		super(frameCon, targetTimeline);
	}

	abstract protected List<Status> getStatuses() throws TwitterException;

	public void run() {
		try {
			List<Status> statuses = getStatuses();
			if (frameCon.isRefreshable(refreshId)) {
				while (loading == true)
					;
				loading = true;
				targetTimeline.addStatusesFirst(statuses);
				frameCon.refreshTimelineList(targetTimeline);
				frameCon.endRefresh();
				loading = false;
			}
		} catch (TwitterException e) {
			new PTDialog(frameCon.mainFrame, e.getErrorMessage());
		}
	}
}

// **** ****

// **** Load Timeline Threads ****
abstract public class LoadTimelineThread extends Thread {
	protected static boolean loading = false;
	protected MainFrameController frameCon;
	protected Timeline targetTimeline;
	protected long refreshId;

	public LoadTimelineThread(MainFrameController frameCon,
			Timeline targetTimeline) {
		this.frameCon = frameCon;
		this.refreshId = frameCon.getRefreshId();
		this.targetTimeline = targetTimeline;
	}

	abstract protected List<Status> getStatuses() throws TwitterException;
}
// **** ****