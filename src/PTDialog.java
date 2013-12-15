import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class PTDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JPanel messagePanel;
	private JLabel messageLabel;
	public JButton confirmButton;
	public JButton closeButton;

	public PTDialog(JFrame frame, String text) {
		this(frame, text, null);
	}

	public PTDialog(JFrame frame, String text, ActionListener closeAction) {
		this(frame, text, null, closeAction);
	}

	public PTDialog(JFrame frame, String text, ActionListener yesAction,
			ActionListener noAction) {
		super(frame, "PowerTwing", true);
		this.setLocationRelativeTo(frame);
		messagePanel = new JPanel(new FlowLayout());
		messagePanel.setPreferredSize(new Dimension(300, 120));
		messageLabel = new JLabel(
				"<html><div style='width:250px; height:50px; text-align:center'>"
						+ text + "</div></html>");
		messagePanel.add(messageLabel);
		getContentPane().add(messagePanel);
		final ActionListener _noAction = noAction;
		if (yesAction != null) {
			final ActionListener _yesAction = yesAction;
			ActionListener confirmAction = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					_yesAction.actionPerformed(e);
					dispose();
				}
			};
			confirmButton = new JButton("Yes");
			confirmButton.addActionListener(confirmAction);
			confirmButton.registerKeyboardAction(confirmAction,
					KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
					JComponent.WHEN_IN_FOCUSED_WINDOW);
			messagePanel.add(confirmButton);
			ActionListener disposeAction = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (_noAction != null)
						_noAction.actionPerformed(e);
					dispose();
				}
			};
			closeButton = new JButton("No");
			closeButton.addActionListener(disposeAction);
			closeButton.registerKeyboardAction(disposeAction,
					KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
					JComponent.WHEN_IN_FOCUSED_WINDOW);
			messagePanel.add(closeButton);
		} else {
			ActionListener disposeAction = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (_noAction != null)
						_noAction.actionPerformed(e);
					dispose();
				}
			};
			closeButton = new JButton("Close");
			closeButton.addActionListener(disposeAction);
			closeButton.registerKeyboardAction(disposeAction,
					KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
					JComponent.WHEN_IN_FOCUSED_WINDOW);
			closeButton.registerKeyboardAction(disposeAction,
					KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
					JComponent.WHEN_IN_FOCUSED_WINDOW);
			messagePanel.add(closeButton);
		}

		pack();

		setVisible(true);
	}

}