import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import powertwing.supports.WebBrowserCaller;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class AuthController implements ActionListener, KeyListener {
	private final String CONSUMER_KEY = "vWgsx4e7yzx4CugR2MhPw";
	private final String CONSUMER_SECRET = "d6VtL11FLHSeLB1txNtppD05kjBgbfIN2E5fTIg3lGc";
	private final String DB_FILE_NAME = "auth.dat";

	public static Twitter twitter;
	private String authURL;
	String token;
	String tokenSecret;
	RequestToken requestToken;
	public static AccessToken accessToken;

	JFrame authFrame;
	JPanel authPanel;
	JLabel msgLabel;
	JTextField pinTextField;
	JButton goButton;
	JButton webpageButton;

	public AuthController() {
		if (loadTokens() == false) {
			initAuthFrame("PowerTwing : Authentication");
			initAuthPanel();

			authFrame.setVisible(true);

			requestAuth();
		} else {
			PowerTwing.openMainFrame();
		}
	}

	private void requestAuth() {
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		requestToken = null;
		try {
			requestToken = twitter.getOAuthRequestToken();

		} catch (TwitterException e) {
			System.out.println("Failed to get access token");
			System.exit(0);
		}
		token = requestToken.getToken();
		tokenSecret = requestToken.getTokenSecret();
		authURL = requestToken.getAuthorizationURL();
		WebBrowserCaller.openURL(authURL);
	}

	private void initAuthFrame(String title) {
		authFrame = new JFrame();
		authFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		authFrame.setTitle(title);
		if (System.getProperty("os.name").startsWith("Windows")) {
			// Sizing for Windows
			authFrame.setSize(350, 180);
		} else {
			authFrame.setSize(300, 130);
		}
		authFrame.setLocation(300, 300);
	}

	private void initAuthPanel() {
		authPanel = new JPanel(new FlowLayout());
		authFrame.getContentPane().add(authPanel);

		msgLabel = new JLabel(
				"<html>Authorize your twitter account and<br /> input your PIN Number.</html>");
		authPanel.add(msgLabel);

		pinTextField = new JTextField(10);
		pinTextField.addKeyListener(this);
		authPanel.add(pinTextField);

		goButton = new JButton("GO!");
		goButton.addActionListener(this);
		authPanel.add(goButton);

		webpageButton = new JButton("Re-open Authentication Site");
		webpageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				WebBrowserCaller.openURL(authURL);
			}
		});
		authPanel.add(webpageButton);
	}

	private void go() {
		String pin = pinTextField.getText();
		try {
			accessToken = null;
			if (pin.length() > 0) {
				accessToken = twitter.getOAuthAccessToken(requestToken, pin);
			} else {
				accessToken = twitter.getOAuthAccessToken();
			}
		} catch (TwitterException e) {
			new PTDialog(authFrame,
					"Invalid pin number or unable to get the access token");
			return;
		}
		twitter.setOAuthAccessToken(accessToken);
		saveTokens(accessToken.getToken(), accessToken.getTokenSecret());
		PowerTwing.openMainFrame();
		authFrame.dispose();
	}

	private void saveTokens(String oauthToken, String secretToken) {
		try {
			FileWriter writer = new FileWriter(DB_FILE_NAME);
			BufferedWriter out = new BufferedWriter(writer);
			out.write(oauthToken);
			out.newLine();
			out.write(secretToken);
			out.newLine();
			out.close();
		} catch (IOException e) {
			new PTDialog(authFrame, "Cannot write on database file",
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							System.exit(0);
						}
					});
		}
	}

	private boolean loadTokens() {
		try {
			FileReader reader = new FileReader(DB_FILE_NAME);
			BufferedReader in = new BufferedReader(reader);
			String oauthToken = in.readLine();
			String secretToken = in.readLine();
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			accessToken = null;
			accessToken = new AccessToken(oauthToken, secretToken);
			twitter.setOAuthAccessToken(accessToken);
			in.close();
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		go();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			go();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			go();
		}
	}

	public static String getMyScreenName() {
		Twitter twitter = AuthController.twitter;
		AccessToken accessToken = AuthController.accessToken;
		twitter.setOAuthAccessToken(accessToken);
		try {
			return twitter.getScreenName();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return "";
		} catch (TwitterException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static User getMyUserInfo() {
		String myScreenName = getMyScreenName();
		if (myScreenName.equals(""))
			return null;
		User myUserInfo;
		try {
			myUserInfo = twitter.showUser(myScreenName);
			return myUserInfo;
		} catch (TwitterException e) {
			e.printStackTrace();
			return null;
		}
	}
}
