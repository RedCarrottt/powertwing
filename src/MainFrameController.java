import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import powertwing.supports.WebBrowserCaller;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class MainFrameController {
	// ****** Member Attributes ******
	// **** UI size constants ****
	static final int WINDOW_MIN_WIDTH = 300;
	static final int WINDOW_MIN_HEIGHT = 480;
	static final int WINDOW_INIT_WIDTH = WINDOW_MIN_WIDTH;
	static final int WINDOW_INIT_HEIGHT = WINDOW_MIN_HEIGHT;

	static final int WIN_MARGIN_WIDTH = 30;
	static final int WIN_MARGIN_HEIGHT = 30;

	static final int UP_MARGIN_FIXED_HEIGHT = 10;
	static final int DEFAULT_MARGIN_FIXED_HEIGHT = 30;
	static final int UPTEXTPANEL_FIXED_HEIGHT = 80;
	static final int UPBARPANEL_FIXED_HEIGHT = 30;
	static final int MIDBARPANEL_FIXED_HEIGHT = 30;
	static final int CONTENTSPANEL_UPHIDEN_MIN_HEIGHT = WINDOW_MIN_HEIGHT
			- DEFAULT_MARGIN_FIXED_HEIGHT - MIDBARPANEL_FIXED_HEIGHT;
	static final int CONTENTSPANEL_UPHIDEN_INIT_HEIGHT = WINDOW_INIT_HEIGHT
			- DEFAULT_MARGIN_FIXED_HEIGHT - MIDBARPANEL_FIXED_HEIGHT;
	static final int CONTENTSPANEL_UPSHOWN_MIN_HEIGHT = WINDOW_MIN_HEIGHT
			- UP_MARGIN_FIXED_HEIGHT - DEFAULT_MARGIN_FIXED_HEIGHT
			- UPTEXTPANEL_FIXED_HEIGHT - UPBARPANEL_FIXED_HEIGHT
			- MIDBARPANEL_FIXED_HEIGHT;
	static final int CONTENTSPANEL_UPSHOWN_INIT_HEIGHT = WINDOW_INIT_HEIGHT
			- UP_MARGIN_FIXED_HEIGHT - DEFAULT_MARGIN_FIXED_HEIGHT
			- UPTEXTPANEL_FIXED_HEIGHT - UPBARPANEL_FIXED_HEIGHT
			- MIDBARPANEL_FIXED_HEIGHT;

	static final int COUNTLABEL_MIN_WIDTH = 130;
	static final int COUNTLABEL_INIT_WIDTH = COUNTLABEL_MIN_WIDTH;
	static final int TWEETBUTTON_FIXED_WIDTH = 130;

	static final int CONTENTMENU_MIN_WIDTH = 120;
	static final int CONTENTMENU_INIT_WIDTH = CONTENTMENU_MIN_WIDTH;
	static final int REFRESHBUTTON_FIXED_WIDTH = 120;

	static final int TIMELINELIST_TEXT_MIN_WIDTH = 220;
	static final int TIMELINELIST_TEXT_INIT_WIDTH = TIMELINELIST_TEXT_MIN_WIDTH;
	// **** ****

	// **** File path constants ****
	// T/ODO load status file
	// private final String STATUS_FILE_NAME = "status.dat";
	// **** ****

	// **** Flags and Statuses ****
	private boolean initialized = false;
	private boolean upPanelsVisible = false;
	private boolean refreshing = false;
	private boolean composing = false;
	private long refreshId = 0;
	private long inReplyToStatusId = -1;
	private String inReplyToUserScreenName = "";
	// **** ****
	// ****** ******

	// ****** Member UI Components ******
	// // Main Frame and Main Panel ****
	JFrame mainFrame;
	JPanel mainPanel;
	// **** ****

	// **** Main Panel ****
	JPanel upTextPanel;
	JPanel upBarPanel;
	JPanel midBarPanel;
	JPanel contentsPanel;
	// **** ****

	// **** Up Text Panel ****
	JScrollPane tweetScrollPane;
	JTextArea tweetTextArea;
	// **** ****

	// **** Up Bar Panel ****
	JLabel countLabel;
	JButton tweetButton;
	// **** ****

	// **** Mid Bar Panel ****
	JComboBox contentMenu;
	JButton refreshButton;
	ArrayList<ContentMenuItem> contentMenuItems = new ArrayList<ContentMenuItem>();
	int contentMenuItems_curIndex = 0;
	// **** ****

	// **** Contents Bar Panel ****
	JScrollPane contentsScrollPane;
	JList timelineList;
	TimelineListPopupMenu timelineListPopupMenu;
	ArrayList<TimelineListPopupMenuItem> timelineListPopupMenuItems = new ArrayList<TimelineListPopupMenuItem>();

	// **** ****

	// **** Timelines ****
	Timeline presentTimeline;
	Timeline homeTimeline = new Timeline();
	Timeline mentionTimeline = new Timeline();

	// **** ****
	// ****** ******

	// **** Constructor and Getter of Singleton Instance ****
	public MainFrameController(String title) {
		initMainFrame(title);
		initMainPanel();
		enrollMainFrameResizingEvent();
		loadStatus();

		mainFrame.setVisible(true);
		singleton = this;
	}

	private static MainFrameController singleton;

	static public MainFrameController getSingleton() {
		return singleton;
	}

	// **** ****

	// **** Initialize UI ****
	public void init() {
		refreshTimeline();
		initialized = true;
	}

	private void initMainFrame(String title) {
		mainFrame = new JFrame();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setTitle(title);
		mainFrame.setSize(WINDOW_INIT_WIDTH, WINDOW_INIT_HEIGHT);
		mainFrame.setLocation(300, 300);
	}

	private void initMainPanel() {
		mainPanel = new JPanel(new FlowLayout());
		mainFrame.getContentPane().add(mainPanel);

		upTextPanel = new JPanel(new BorderLayout());
		mainPanel.add(upTextPanel);
		initUpTextPanel();

		upBarPanel = new JPanel(new GridLayout(1, 2));
		mainPanel.add(upBarPanel);
		initUpBarPanel();

		midBarPanel = new JPanel(new GridLayout(1, 2));
		mainPanel.add(midBarPanel);
		initMidBarPanel();

		contentsPanel = new JPanel(new BorderLayout());
		mainPanel.add(contentsPanel);
		initContentsPanel();

		hideUpPanels(false); // hide up panels without resizing, because other
								// components does not have size.
		resizeComponents();
		initKeyboardActions();
		initMouseActions();
	}

	private void initUpTextPanel() {
		tweetTextArea = new JTextArea();
		tweetTextArea.setEditable(true);
		tweetTextArea.setLineWrap(true);
		tweetTextArea.setBorder(BorderFactory.createEtchedBorder());
		tweetTextArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				checkDisableReply();
				updateTweetCount();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateTweetCount();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				checkDisableReply();
				updateTweetCount();
			}
		});
		tweetScrollPane = new JScrollPane(tweetTextArea);
		tweetScrollPane.getVerticalScrollBar().setValue(
				tweetScrollPane.getVerticalScrollBar().getMaximum());
		tweetScrollPane.validate();
		upTextPanel.add(tweetScrollPane);
	}

	private void initUpBarPanel() {
		countLabel = new JLabel("0/140");
		countLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		upBarPanel.add(countLabel);
		tweetButton = new JButton("Tweet!");
		tweetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				composeTweet();
			}
		});
		upBarPanel.add(tweetButton);
	}

	private void initMidBarPanel() {
		contentMenu = new JComboBox();
		contentMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contentMenuChanged();
			}
		});
		midBarPanel.add(contentMenu);

		refreshButton = new JButton("Refresh (/)");
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshTimeline();
			}
		});
		midBarPanel.add(refreshButton);
	}

	private void initContentsPanel() {
		timelineList = new JList();
		timelineList.setCellRenderer(new timelineCellRenderer());
		timelineList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		contentsScrollPane = new JScrollPane(timelineList);
		contentsPanel.add(contentsScrollPane);
	}

	private void initKeyboardActions() {
		// Refresh timeline
		ActionListener refreshAction = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshTimeline();
			}
		};
		ActionListener refreshAction2 = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (tweetTextArea.isFocusOwner() == true)
					return;
				refreshTimeline();
			}
		};
		mainPanel.registerKeyboardAction(refreshAction, KeyStroke.getKeyStroke(
				KeyEvent.VK_SLASH, InputEvent.CTRL_DOWN_MASK),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		mainPanel.registerKeyboardAction(refreshAction, KeyStroke.getKeyStroke(
				KeyEvent.VK_SLASH, InputEvent.META_DOWN_MASK),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		mainPanel.registerKeyboardAction(refreshAction2,
				KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		mainPanel.registerKeyboardAction(refreshAction2,
				KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);

		// Hide up panels
		ActionListener hideUpPanelsAction = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				hideUpPanels();
			}
		};
		mainPanel.registerKeyboardAction(hideUpPanelsAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);

		// toggle up panels
		ActionListener toggleUpPanelsAction = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (upPanelsVisible)
					hideUpPanels();
				else
					showUpPanels();
			}
		};
		ActionListener toggle2UpPanelsAction = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (tweetTextArea.isFocusOwner() == true)
					return;
				if (upPanelsVisible)
					hideUpPanels();
				else
					showUpPanels();
			}
		};
		mainPanel.registerKeyboardAction(toggleUpPanelsAction, KeyStroke
				.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		mainPanel.registerKeyboardAction(toggleUpPanelsAction, KeyStroke
				.getKeyStroke(KeyEvent.VK_N, InputEvent.META_DOWN_MASK),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		mainPanel.registerKeyboardAction(toggle2UpPanelsAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_N, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		mainPanel.registerKeyboardAction(toggle2UpPanelsAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);

		// Compose tweet
		ActionListener composeTweetAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				composeTweet();
			}
		};
		tweetTextArea.registerKeyboardAction(composeTweetAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
				JComponent.WHEN_FOCUSED);
		tweetTextArea.registerKeyboardAction(composeTweetAction, KeyStroke
				.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK,
						false), JComponent.WHEN_FOCUSED);
		tweetTextArea.registerKeyboardAction(composeTweetAction, KeyStroke
				.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.META_DOWN_MASK,
						false), JComponent.WHEN_FOCUSED);

		// Clear tweet textarea
		ActionListener clearTweetTextAreaAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearTweetTextArea();
			}
		};
		tweetTextArea.registerKeyboardAction(clearTweetTextAreaAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
						InputEvent.CTRL_DOWN_MASK), JComponent.WHEN_FOCUSED);
		tweetTextArea.registerKeyboardAction(clearTweetTextAreaAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
						InputEvent.META_DOWN_MASK), JComponent.WHEN_FOCUSED);
		tweetTextArea.registerKeyboardAction(clearTweetTextAreaAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,
						InputEvent.META_DOWN_MASK), JComponent.WHEN_FOCUSED);
		tweetTextArea.registerKeyboardAction(clearTweetTextAreaAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,
						InputEvent.META_DOWN_MASK), JComponent.WHEN_FOCUSED);

		// Switch content menu
		final int[] numKeyEvents = { KeyEvent.VK_0, KeyEvent.VK_1,
				KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5,
				KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9 };
		for (int i = 0; i < 10; i++) {
			final int _i = i;
			ActionListener changeContentMenuAction = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					changeContentMenu(_i);
				}
			};
			mainPanel.registerKeyboardAction(changeContentMenuAction, KeyStroke
					.getKeyStroke(numKeyEvents[i], InputEvent.CTRL_DOWN_MASK),
					JComponent.WHEN_IN_FOCUSED_WINDOW);
			mainPanel.registerKeyboardAction(changeContentMenuAction, KeyStroke
					.getKeyStroke(numKeyEvents[i], InputEvent.META_DOWN_MASK),
					JComponent.WHEN_IN_FOCUSED_WINDOW);
			contentMenu.registerKeyboardAction(changeContentMenuAction,
					KeyStroke.getKeyStroke(numKeyEvents[i], 0),
					JComponent.WHEN_FOCUSED);
		}

		// action on listitem
		ActionListener listItemAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = timelineList.getSelectedIndex();
				spaceActionListItem(index);
			}
		};
		timelineList.registerKeyboardAction(listItemAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false),
				JComponent.WHEN_FOCUSED);

		// reply to tweet
		ActionListener replyAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = timelineList.getSelectedIndex();
				if (index >= 0) {
					TimelineListEntry entry = presentTimeline
							.getEntryAtRow(index);
					if (entry instanceof TimelineListEntryStatus) {
						TimelineListEntryStatus entryStatus = (TimelineListEntryStatus) entry;
						Status status = entryStatus.getStatus();
						replyTo(status);
					}
				}
			}
		};
		timelineList.registerKeyboardAction(replyAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_R, 0, false),
				JComponent.WHEN_FOCUSED);
		timelineList.registerKeyboardAction(replyAction, KeyStroke
				.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK, false),
				JComponent.WHEN_FOCUSED);
		timelineList.registerKeyboardAction(replyAction, KeyStroke
				.getKeyStroke(KeyEvent.VK_R, InputEvent.META_DOWN_MASK, false),
				JComponent.WHEN_FOCUSED);
		// retweet
		ActionListener retweetAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = timelineList.getSelectedIndex();
				if (index >= 0) {
					TimelineListEntry entry = presentTimeline
							.getEntryAtRow(index);
					if (entry instanceof TimelineListEntryStatus) {
						TimelineListEntryStatus entryStatus = (TimelineListEntryStatus) entry;
						Status status = entryStatus.getStatus();
						retweet(status);
					}
				}
			}
		};
		timelineList.registerKeyboardAction(retweetAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_T, 0, false),
				JComponent.WHEN_FOCUSED);
		timelineList.registerKeyboardAction(retweetAction, KeyStroke
				.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK, false),
				JComponent.WHEN_FOCUSED);
		timelineList.registerKeyboardAction(retweetAction, KeyStroke
				.getKeyStroke(KeyEvent.VK_T, InputEvent.META_DOWN_MASK, false),
				JComponent.WHEN_FOCUSED);
		// quote
		ActionListener quoteAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = timelineList.getSelectedIndex();
				if (index >= 0) {
					TimelineListEntry entry = presentTimeline
							.getEntryAtRow(index);
					if (entry instanceof TimelineListEntryStatus) {
						TimelineListEntryStatus entryStatus = (TimelineListEntryStatus) entry;
						Status status = entryStatus.getStatus();
						quoteTweet(status);
					}
				}
			}
		};
		timelineList.registerKeyboardAction(quoteAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_T,
						InputEvent.SHIFT_DOWN_MASK, false),
				JComponent.WHEN_FOCUSED);
		timelineList.registerKeyboardAction(
				quoteAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK
						| InputEvent.SHIFT_DOWN_MASK, false),
				JComponent.WHEN_FOCUSED);
		timelineList.registerKeyboardAction(
				quoteAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.META_DOWN_MASK
						| InputEvent.SHIFT_DOWN_MASK, false),
				JComponent.WHEN_FOCUSED);
		// favorite
		ActionListener favoriteAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = timelineList.getSelectedIndex();
				if (index >= 0) {
					TimelineListEntry entry = presentTimeline
							.getEntryAtRow(index);
					if (entry instanceof TimelineListEntryStatus) {
						TimelineListEntryStatus entryStatus = (TimelineListEntryStatus) entry;
						Status status = entryStatus.getStatus();
						if (status.isFavorited()) {
							removeFromFavorite(status);
						} else {
							addToFavorite(status);
						}
					}
				}
			}
		};
		timelineList.registerKeyboardAction(favoriteAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_F, 0, false),
				JComponent.WHEN_FOCUSED);
		timelineList.registerKeyboardAction(favoriteAction, KeyStroke
				.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK, false),
				JComponent.WHEN_FOCUSED);
		timelineList.registerKeyboardAction(favoriteAction, KeyStroke
				.getKeyStroke(KeyEvent.VK_F, InputEvent.META_DOWN_MASK, false),
				JComponent.WHEN_FOCUSED);
		// delete tweet
		ActionListener deleteTweetAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = timelineList.getSelectedIndex();
				if (index >= 0) {
					TimelineListEntry entry = presentTimeline
							.getEntryAtRow(index);
					if (entry instanceof TimelineListEntryStatus) {
						TimelineListEntryStatus entryStatus = (TimelineListEntryStatus) entry;
						Status status = entryStatus.getStatus();
						if (status.getUser().getScreenName()
								.equals(AuthController.getMyScreenName())) {
							deleteTweet(status);
						}
					}
				}
			}
		};
		timelineList.registerKeyboardAction(deleteTweetAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false),
				JComponent.WHEN_FOCUSED);
		timelineList.registerKeyboardAction(deleteTweetAction, KeyStroke
				.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK,
						false), JComponent.WHEN_FOCUSED);
		timelineList.registerKeyboardAction(deleteTweetAction, KeyStroke
				.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.META_DOWN_MASK,
						false), JComponent.WHEN_FOCUSED);
		timelineList.registerKeyboardAction(deleteTweetAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0, false),
				JComponent.WHEN_FOCUSED);
		timelineList.registerKeyboardAction(deleteTweetAction, KeyStroke
				.getKeyStroke(KeyEvent.VK_BACK_SPACE,
						InputEvent.CTRL_DOWN_MASK, false),
				JComponent.WHEN_FOCUSED);
		timelineList.registerKeyboardAction(deleteTweetAction, KeyStroke
				.getKeyStroke(KeyEvent.VK_BACK_SPACE,
						InputEvent.META_DOWN_MASK, false),
				JComponent.WHEN_FOCUSED);
		// T/ODO user info
	}

	private void initMouseActions() {
		timelineList.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				int row = timelineList.locationToIndex(arg0.getPoint());
				maybeShowTimelinePopup(arg0, row);
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				int row = timelineList.locationToIndex(arg0.getPoint());
				maybeMouseActionListItem(arg0, row);

			}
		});
	}

	private void enrollMainFrameResizingEvent() {
		mainFrame.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent arg0) {
				resizeComponents();
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				resizeComponents();
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				resizeComponents();
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				resizeComponents();
			}
		});
	}

	private void resizeComponents() {
		// resizing each components according to resized main frame
		Dimension mainSize = mainFrame.getSize();
		int mainWidth = (int) mainSize.getWidth();
		int mainHeight = (int) mainSize.getHeight();
		if (mainWidth < WINDOW_MIN_WIDTH) {
			mainFrame.setSize(WINDOW_MIN_WIDTH, mainHeight);
			mainWidth = WINDOW_MIN_WIDTH;
		}
		if (mainHeight < WINDOW_MIN_HEIGHT) {
			mainFrame.setSize(mainWidth, WINDOW_MIN_HEIGHT);
			mainHeight = WINDOW_MIN_HEIGHT;
		}
		if (System.getProperty("os.name").startsWith("Windows")) {
			// Resizing for Windows
			mainWidth = mainWidth - WIN_MARGIN_WIDTH;
			mainHeight = mainHeight - WIN_MARGIN_HEIGHT;
		}
		int diffWidth = mainWidth - WINDOW_INIT_WIDTH;
		// int diffHeight = mainHeight - WINDOW_INIT_HEIGHT;

		// up text panel
		upTextPanel.setPreferredSize(new Dimension(mainWidth,
				UPTEXTPANEL_FIXED_HEIGHT));

		// up bar panel
		upBarPanel.setPreferredSize(new Dimension(mainWidth,
				UPBARPANEL_FIXED_HEIGHT));
		tweetButton.setPreferredSize(new Dimension(TWEETBUTTON_FIXED_WIDTH,
				(int) upBarPanel.getSize().getHeight()));
		int countLabelWidth = COUNTLABEL_INIT_WIDTH + diffWidth;
		if (countLabelWidth < COUNTLABEL_MIN_WIDTH)
			countLabelWidth = COUNTLABEL_MIN_WIDTH;
		int countLabelHeight = (int) countLabel.getSize().getSize().getHeight();
		countLabel.setPreferredSize(new Dimension(countLabelWidth,
				countLabelHeight));

		// mid bar panel
		midBarPanel.setPreferredSize(new Dimension(mainWidth,
				MIDBARPANEL_FIXED_HEIGHT));
		int contentMenuWidth = CONTENTMENU_INIT_WIDTH + diffWidth;
		if (contentMenuWidth < CONTENTMENU_MIN_WIDTH)
			contentMenuWidth = CONTENTMENU_MIN_WIDTH;
		int contentMenuHeight = (int) contentMenu.getSize().getSize()
				.getHeight();
		int refreshButtonHeight = (int) refreshButton.getSize().getSize()
				.getHeight();
		contentMenu.setPreferredSize(new Dimension(contentMenuWidth,
				contentMenuHeight));
		refreshButton.setPreferredSize(new Dimension(REFRESHBUTTON_FIXED_WIDTH,
				refreshButtonHeight));

		// contents panel
		resizeContentsPanel();
	}

	private void resizeContentsPanel() {
		// resizing contents panel and its child components
		Dimension mainSize = mainFrame.getSize();
		int mainWidth = (int) mainSize.getWidth();
		int mainHeight = (int) mainSize.getHeight();
		if (System.getProperty("os.name").startsWith("Windows")) {
			// Resizing for Windows
			mainWidth = mainWidth - WIN_MARGIN_WIDTH;
			mainHeight = mainHeight - WIN_MARGIN_HEIGHT;
		}
		// int diffWidth = mainWidth - WINDOW_INIT_WIDTH;
		int diffHeight = mainHeight - WINDOW_INIT_HEIGHT;
		int contentsPanelHeight = mainHeight
				- (UPTEXTPANEL_FIXED_HEIGHT + UPBARPANEL_FIXED_HEIGHT + MIDBARPANEL_FIXED_HEIGHT);
		if (contentsPanelHeight < CONTENTSPANEL_UPHIDEN_MIN_HEIGHT)
			contentsPanelHeight = CONTENTSPANEL_UPHIDEN_MIN_HEIGHT;

		if (upPanelsVisible == true) {
			// up panels are visible
			int contentsPanelShownHeight = CONTENTSPANEL_UPSHOWN_INIT_HEIGHT
					+ diffHeight;
			contentsPanel.setPreferredSize(new Dimension(mainWidth,
					contentsPanelShownHeight));
		} else {
			// up panels are invisiable
			int contentsPanelHidenHeight = CONTENTSPANEL_UPHIDEN_INIT_HEIGHT
					+ diffHeight;
			contentsPanel.setPreferredSize(new Dimension(mainWidth,
					contentsPanelHidenHeight));
		}
		refreshTimelineList();
	}

	// **** ****

	// **** Manage Status ****
	private void initStatus() {
		contentMenuItems.add(new ContentMenuItemTimeline("Timeline", this));
		contentMenuItems.add(new ContentMenuItemMentions("Mentions", this));
		contentMenuItems_curIndex = 0;
		updateContentMenu();
	}

	private void loadStatus() {
		// T/ODO load status
		initStatus();
		/*
		 * try { FileReader reader = new FileReader(STATUS_FILE_NAME);
		 * BufferedReader in = new BufferedReader(reader); } catch
		 * (FileNotFoundException e) { initStatus(); } catch (IOException e) { }
		 */
	}

	// T/ODO save status
	/*
	 * private void saveStatus() { try { FileWriter writer = new
	 * FileWriter(STATUS_FILE_NAME); BufferedWriter out = new
	 * BufferedWriter(writer); out.write(timelineStatusType.toString());
	 * out.newLine(); out.write(timelineStatusName); out.newLine(); out.close();
	 * } catch (IOException e) { new PTDialog(mainFrame,
	 * "Cannot write on status file", new ActionListener() {
	 * 
	 * @Override public void actionPerformed(ActionEvent arg0) { System.exit(0);
	 * } }); } }
	 */
	// **** ****

	// **** Content Menu ****
	private void updateContentMenu() {
		String curStr = "";
		if (contentMenu.getItemCount() != 0) {
			curStr = (String) contentMenu.getItemAt(contentMenuItems_curIndex);
			contentMenu.removeAllItems();
		}
		for (ContentMenuItem item : contentMenuItems) {
			contentMenu.addItem(item.getName());
			if (curStr.equals(item.getName())) {
				contentMenuItems_curIndex = contentMenuItems.indexOf(item);
			}
		}
		contentMenu.setSelectedIndex(contentMenuItems_curIndex);
	}

	private void contentMenuChanged() {
		contentMenuItems_curIndex = contentMenu.getSelectedIndex();
		ContentMenuItem currentItem = contentMenuItems
				.get(contentMenuItems_curIndex);
		if (initialized
				&& currentItem instanceof ContentMenuItemLoadable == true) {
			cancelRefresh();
			refreshTimeline();
		}
	}

	private void changeContentMenu(int index) {
		int modifiedIndex = (index == 0) ? 10 : index - 1;
		if (modifiedIndex < contentMenu.getItemCount()) {
			contentMenu.setSelectedIndex(modifiedIndex);
		}
	}

	// **** ****

	// ****** Timeline List ******
	// **** Refresh Button ****

	public void cancelRefresh() {
		endRefresh();
	}

	public void startLoadMore() {
		refreshing = true;
		refreshButton.setText("(Loading)");
		refreshButton.setEnabled(false);
	}

	public void startRefresh() {
		refreshing = true;
		refreshButton.setText("(Refreshing)");
		refreshButton.setEnabled(false);
	}

	public void endRefresh() {
		refreshing = false;
		refreshButton.setText("Refresh (/)");
		refreshButton.setEnabled(true);
	}

	// **** ****

	// **** Load Timeline List ****

	public void refreshTimelineList() {
		if (presentTimeline != null)
			refreshTimelineList(presentTimeline);
	}

	public void refreshTimelineList(Timeline targetTimeline) {
		presentTimeline = targetTimeline;
		ArrayList<TimelineListEntry> totalEntries = targetTimeline.getEntries();
		timelineList.setListData(totalEntries.toArray());
	}

	public void refreshTimeline() {
		if (refreshing == true)
			return;
		startRefresh();
		ContentMenuItem currentItem = contentMenuItems
				.get(contentMenuItems_curIndex);
		if (currentItem instanceof ContentMenuItemLoadable == true) {
			ContentMenuItemLoadable currentItemRefreshable = (ContentMenuItemLoadable) currentItem;
			currentItemRefreshable.startRefreshThread();
		} else {
			cancelRefresh();
		}
	}

	public long getRefreshId() {
		return ++refreshId;
	}

	public boolean isRefreshable(long refreshId) {
		return (refreshId == this.refreshId);
	}

	public void loadMoreTimeline() {
		if (refreshing == true)
			return;
		startLoadMore();
		ContentMenuItem currentItem = contentMenuItems
				.get(contentMenuItems_curIndex);
		if (currentItem instanceof ContentMenuItemLoadable == true) {
			ContentMenuItemLoadable currentItemRefreshable = (ContentMenuItemLoadable) currentItem;
			currentItemRefreshable.startLoadMoreThread();
		} else {
			cancelRefresh();
		}
	}

	public void spaceActionListItem(int row) {
		if (row >= 0) {
			TimelineListEntry entry = presentTimeline.getEntryAtRow(row);
			Rectangle rect = timelineList.getCellBounds(row, row);
			int x = rect.x + rect.width;
			int y = rect.y + rect.height / 2;
			if (row >= timelineList.getFirstVisibleIndex()
					&& row <= timelineList.getLastVisibleIndex()) {
				if (entry instanceof TimelineListEntryStatus) {
					showTimelinePopup(row, x, y);
				} else {
					loadMoreTimeline();
				}
			}
		}
	}

	private void maybeMouseActionListItem(MouseEvent e, int row) {
		if (e.getButton() == MouseEvent.BUTTON1
				|| e.getButton() == MouseEvent.BUTTON3) {
			if (row >= 0) {
				TimelineListEntry entry = presentTimeline.getEntryAtRow(row);
				if (entry instanceof TimelineListEntryEndPoint) {
					loadMoreTimeline();
				}
			}
		}
	}

	// **** ****

	// **** Timeline List Popup Menu ****
	private void maybeShowTimelinePopup(MouseEvent e, int row) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			showTimelinePopup(row, e.getX(), e.getY());
		}
	}

	private void showTimelinePopup(int row, int x, int y) {
		if (row < 0) {
			return;
		}
		if (timelineListPopupMenu == null) {
			timelineListPopupMenu = new TimelineListPopupMenu();
		}
		timelineListPopupMenu.removeAll();

		// get status and user at selected row
		TimelineListEntry entry = presentTimeline.getEntryAtRow(row);
		if (entry instanceof TimelineListEntryStatus) {
			// If selected cell is status cell
			TimelineListEntryStatus entryStatus = (TimelineListEntryStatus) entry;
			Status status = entryStatus.getStatus();
			User user = status.getUser();

			// make timeline popup menu items
			makeTimelinePopupMenuItems(status, user);

			// make visible
			timelineListPopupMenu.setMenuItems(timelineListPopupMenuItems);
			timelineListPopupMenu.show(timelineList, x, y);
		} else {
			// If selected cell is end point cell
			// Do nothing
		}
	}

	private void makeTimelinePopupMenuItems(Status status, User user) {
		if (!timelineListPopupMenuItems.isEmpty()) {
			timelineListPopupMenuItems.clear();
		}
		final Status _status = status;

		TimelineListPopupMenuItem item;
		item = new TimelineListPopupMenuItem("Reply (R)", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				replyTo(_status);
			}
		});
		timelineListPopupMenuItems.add(item);

		if (user.isProtected() == true) {
			item = new TimelineListPopupMenuItem("(Protected account)",
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							// Do nothing
						}
					});
			timelineListPopupMenuItems.add(item);
		} else {
			if (status.isRetweetedByMe() == true) {
				item = new TimelineListPopupMenuItem("(Retweeted by You)",
						new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								// Do nothing
							}
						});
				timelineListPopupMenuItems.add(item);
			} else {
				item = new TimelineListPopupMenuItem("Retweet (T)",
						new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								retweet(_status);
							}
						});
				timelineListPopupMenuItems.add(item);
			}
			item = new TimelineListPopupMenuItem("Quote (Shift+T)",
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							quoteTweet(_status);
						}
					});
			timelineListPopupMenuItems.add(item);
		}

		/*
		 * if (status.isFavorited() == true) { item = new
		 * TimelineListPopupMenuItem("Remove from Favorite (F)", new
		 * ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * removeFromFavorite(_status); } });
		 * timelineListPopupMenuItems.add(item); } else { item = new
		 * TimelineListPopupMenuItem("Add to Favorite (F)", new ActionListener()
		 * {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * addToFavorite(_status); } }); timelineListPopupMenuItems.add(item); }
		 */

		/*
		 * item = new TimelineListPopupMenuItem("@" + user.getScreenName() +
		 * " (I)", new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { // T/ODO User
		 * Info } }); timelineListPopupMenuItems.add(item);
		 */

		if (user.getScreenName().equals(AuthController.getMyScreenName())) {
			item = new TimelineListPopupMenuItem("Delete (D)",
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							deleteTweet(_status);
						}
					});
			timelineListPopupMenuItems.add(item);
		}

		URLEntity[] links = status.getURLEntities();
		if (links != null) {
			for (URLEntity link : links) {
				final String _urlString = link.getDisplayURL();
				final String _uriString = link.getURL().toString();
				item = new TimelineListPopupMenuItem(_urlString,
						new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								WebBrowserCaller.openURL(_uriString);
							}
						});
				timelineListPopupMenuItems.add(item);
			}
		}

	}

	// **** ****
	// ****** ******

	// ****** Up Panels ******
	// **** Show and Hid Panels ****
	private void showUpPanels() {
		showUpPanels(true);
	}

	private void showUpPanels(boolean resizable) {
		updateTweetCount();
		upTextPanel.setVisible(true);
		upBarPanel.setVisible(true);
		upPanelsVisible = true;
		if (resizable == true)
			resizeContentsPanel();
		tweetTextArea.requestFocusInWindow();
	}

	private void hideUpPanels() {
		hideUpPanels(true);
	}

	private void hideUpPanels(boolean resizable) {
		upTextPanel.setVisible(false);
		upBarPanel.setVisible(false);
		upPanelsVisible = false;
		if (resizable == true)
			resizeContentsPanel();
		timelineList.requestFocusInWindow();
	}

	// **** ****

	// **** Tweet Count Label ****
	private void updateTweetCount() {
		int tweetLength = tweetTextArea.getText().length();
		String countStr = tweetLength + "/140";
		if (inReplyToStatusId >= 0) {
			countStr = countStr + " (Reply)";
		}
		countLabel.setText(countStr);
		if (tweetLength > 140) {
			countLabel.setForeground(Color.red);
		} else {
			countLabel.setForeground(Color.black);
		}
	}

	// **** ****

	// **** Tweet TextArea and Tweet Button ****
	private void clearTweetTextArea() {
		tweetTextArea.setText("");
		disableReply();
	}

	private void disableReply() {
		this.inReplyToStatusId = -1;
	}

	private void checkDisableReply() {
		if (this.inReplyToStatusId >= 0
				&& this.tweetTextArea.getText().contains(
						"@" + this.inReplyToUserScreenName) == false) {
			disableReply();
		}
	}

	// **** ****

	// **** Manage Tweet ****
	private void composeTweet() {
		if (composing == true)
			return;
		String tweetString = tweetTextArea.getText();
		int tweetLength = tweetString.length();
		if (tweetLength == 0)
			return;
		if (tweetLength > 140) {
			new PTDialog(mainFrame,
					"You cannot compose tweet with over 140 characters.");
			return;
		}
		composing = true;
		tweetTextArea.setEnabled(false);
		tweetButton.setEnabled(false);
		tweetButton.setText("(Tweeting)");
		hideUpPanels();
		ComposeTweetThread thread;
		if (inReplyToStatusId >= 0) {
			thread = new ComposeTweetThread(this, inReplyToStatusId);
		} else {
			thread = new ComposeTweetThread(this);
		}
		thread.start();
	}

	private void replyTo(Status status) {
		if (composing == true)
			return;
		String userScreenName = status.getUser().getScreenName();
		long inReplyToStatusId = status.getId();
		tweetTextArea.setText("@" + userScreenName + " ");
		this.inReplyToStatusId = inReplyToStatusId;
		this.inReplyToUserScreenName = userScreenName;
		showUpPanels();
	}

	private void retweet(Status status) {
		if (status.isRetweetedByMe() == true)
			return;
		if (status.getUser().isProtected() == true) {
			new PTDialog(
					mainFrame,
					"Because this account is protected, you cannot retweet it.",
					null);
		}
		final MainFrameController _frameCon = this;
		final long _statusId = status.getId();
		ActionListener retweetAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RetweetThread thread;
				thread = new RetweetThread(_frameCon, _statusId);
				thread.start();
			}
		};
		new PTDialog(mainFrame, "Do you want to retweet it?", retweetAction,
				null);
	}

	private void quoteTweet(Status status) {
		if (status.getUser().isProtected() == true) {
			new PTDialog(mainFrame,
					"Because this account is protected, you cannot quote it.",
					null);
		}
		if (composing == true)
			return;
		String userScreenName = status.getUser().getScreenName();
		String quotedStatusStr = status.getText();
		tweetTextArea.setText("\"@" + userScreenName + ": " + quotedStatusStr
				+ "\"");
		showUpPanels();
	}

	private void addToFavorite(Status status) {
		final MainFrameController _frameCon = this;
		final long _statusId = status.getId();
		AddToFavoriteThread thread;
		thread = new AddToFavoriteThread(_frameCon, _statusId);
		thread.start();
	}

	private void removeFromFavorite(Status status) {
		final MainFrameController _frameCon = this;
		final long _statusId = status.getId();
		ActionListener removeFromFavoriteAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RemoveFromFavoriteThread thread;
				thread = new RemoveFromFavoriteThread(_frameCon, _statusId);
				thread.start();
			}
		};
		new PTDialog(mainFrame, "Do you want to remove it from favorite?",
				removeFromFavoriteAction, null);
	}

	private void deleteTweet(Status status) {
		final MainFrameController _frameCon = this;
		final Status _status = status;
		final long _statusId = status.getId();
		ActionListener deleteAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DeleteTweetThread thread;
				thread = new DeleteTweetThread(_frameCon, _statusId, _status,
						presentTimeline);
				thread.start();
			}
		};
		new PTDialog(mainFrame, "Do you want to delete this tweet?",
				deleteAction, null);
	}

	// **** ****
	// ****** ******

	// ****** Inner Classes ******
	// **** Thread Classes ****
	class ComposeTweetThread extends Thread {
		private MainFrameController frameCon;
		private long statusId;

		public ComposeTweetThread(MainFrameController frameCon) {
			this(frameCon, -1);
		}

		public ComposeTweetThread(MainFrameController frameCon,
				long inReplyToStatusId) {
			this.frameCon = frameCon;
			this.statusId = inReplyToStatusId;
		}

		public void run() {
			String tweetString = frameCon.tweetTextArea.getText();

			Twitter twitter = AuthController.twitter;
			AccessToken accessToken = AuthController.accessToken;
			twitter.setOAuthAccessToken(accessToken);
			try {
				StatusUpdate update;
				if (statusId >= 0) {
					update = new StatusUpdate(tweetString)
							.inReplyToStatusId(statusId);
				} else {
					update = new StatusUpdate(tweetString);
				}
				twitter.updateStatus(update);

				frameCon.clearTweetTextArea();
			} catch (TwitterException e) {
				new PTDialog(frameCon.mainFrame, "Updating status is failed!");
			}
			frameCon.tweetTextArea.setEnabled(true);
			frameCon.tweetButton.setEnabled(true);
			frameCon.tweetButton.setText("Tweet!");
			frameCon.composing = false;
		}
	}

	class RetweetThread extends Thread {
		private MainFrameController frameCon;
		private long statusId;

		public RetweetThread(MainFrameController frameCon, long retweetStatusId) {
			this.frameCon = frameCon;
			this.statusId = retweetStatusId;
		}

		public void run() {
			Twitter twitter = AuthController.twitter;
			AccessToken accessToken = AuthController.accessToken;
			twitter.setOAuthAccessToken(accessToken);
			try {
				twitter.retweetStatus(statusId);
			} catch (TwitterException e) {
				new PTDialog(frameCon.mainFrame, "Retweeting is failed!");
			}
		}
	}

	class AddToFavoriteThread extends Thread {
		private MainFrameController frameCon;
		private long statusId;

		public AddToFavoriteThread(MainFrameController frameCon,
				long favoriteStatusId) {
			this.frameCon = frameCon;
			this.statusId = favoriteStatusId;
		}

		public void run() {
			Twitter twitter = AuthController.twitter;
			AccessToken accessToken = AuthController.accessToken;
			twitter.setOAuthAccessToken(accessToken);
			try {
				twitter.createFavorite(statusId);
			} catch (TwitterException e) {
				new PTDialog(frameCon.mainFrame,
						"Adding to favorite is failed!");
			}
		}
	}

	class RemoveFromFavoriteThread extends Thread {
		private MainFrameController frameCon;
		private long statusId;

		public RemoveFromFavoriteThread(MainFrameController frameCon,
				long favoriteStatusId) {
			this.frameCon = frameCon;
			this.statusId = favoriteStatusId;
		}

		public void run() {
			Twitter twitter = AuthController.twitter;
			AccessToken accessToken = AuthController.accessToken;
			twitter.setOAuthAccessToken(accessToken);
			try {
				twitter.destroyFavorite(statusId);
			} catch (TwitterException e) {
				new PTDialog(frameCon.mainFrame,
						"Removing from favorite is failed!");
			}
		}
	}

	class DeleteTweetThread extends Thread {
		private MainFrameController frameCon;
		private long statusId;
		private Status status;
		private Timeline targetTimeline;

		public DeleteTweetThread(MainFrameController frameCon,
				long deleteStatusId, Status status, Timeline targetTimeline) {
			this.frameCon = frameCon;
			this.statusId = deleteStatusId;
			this.status = status;
			this.targetTimeline = targetTimeline;
		}

		public void run() {
			Twitter twitter = AuthController.twitter;
			AccessToken accessToken = AuthController.accessToken;
			twitter.setOAuthAccessToken(accessToken);
			try {
				twitter.destroyStatus(statusId);
				targetTimeline.removeEntry(targetTimeline
						.getEntryStatus(status));
			} catch (TwitterException e) {
				new PTDialog(frameCon.mainFrame, "Deleting is failed!");
			}
		}
	}

	// **** ****
	// **** Other Inner Classes ****
	class timelineCellRenderer implements ListCellRenderer {
		private final Color HIGHLIGHT_COLOR = new Color(112, 209, 255);

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			TimelineListEntry entry = (TimelineListEntry) value;
			JLabel label = new JLabel(entry.getText());
			label.setOpaque(true);
			label.setIconTextGap(12);
			String labelText = entry.getText();
			label.setText(labelText);
			ImageIcon image = entry.getImage();
			if (image != null)
				label.setIcon(image);
			if (isSelected) {
				label.setBackground(HIGHLIGHT_COLOR);
				label.setForeground(Color.black);
			} else {
				label.setBackground(Color.white);
				label.setForeground(Color.black);
			}
			return label;
		}
	}

	class TimelineListPopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		public void setMenuItems(ArrayList<TimelineListPopupMenuItem> items) {
			for (TimelineListPopupMenuItem it : items) {
				JMenuItem menuItem = new JMenuItem(it.getName());
				menuItem.addActionListener(it.getActionListener());
				timelineListPopupMenu.add(menuItem);
			}
		}
	}
	// **** ****
	// ****** ******
}
