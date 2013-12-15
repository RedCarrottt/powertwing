

public class PowerTwing {
	public static void main(String[] args) {
		new AuthController();
	}
	
	public static void openMainFrame() {
		MainFrameController mainFrameCon = new MainFrameController("PowerTwing");
		mainFrameCon.init();
	}
}
