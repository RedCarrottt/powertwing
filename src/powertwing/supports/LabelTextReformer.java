package powertwing.supports;

public class LabelTextReformer {

	public static String reformText(String targetString, int halfCharsPerLine) {
		String outString = textToHtmlSign(wordwrap(targetString,
				halfCharsPerLine));
		return outString;
	}

	public static String textToHtmlSign(String targetString) {
		return textToHtmlSign(targetString, "<br />");
	}

	public static String textToHtmlSign(String targetString,
			String wrapperString) {
		String result = "";
		int size = targetString.length();
		boolean contSpace = false;
		for (int i = 0; i < size; i++) {
			char c = targetString.charAt(i);
			String newChar = String.valueOf(c);
			if (c == ' ') {
				if (contSpace == true)
					newChar = "&nbsp;";
				else
					newChar = " ";
				contSpace = true;
			} else {
				contSpace = false;
				switch (c) {
				case '<':
					if (i + wrapperString.length() < targetString.length())
						if (targetString.substring(i,
								i + wrapperString.length()).compareTo(
								wrapperString) != 0)
							newChar = "&lt;";
					break;
				case '>':
					if (i + 1 < targetString.length()
							&& i - wrapperString.length() + 1 >= 0)
						if (targetString.substring(
								i - wrapperString.length() + 1, i + 1)
								.compareTo(wrapperString) != 0)
							newChar = "&rt;";
					break;
				case '&':
					newChar = "&amp;";
					break;
				case '\"':
					newChar = "&quot;";
					break;
				case '\n':
					newChar = "<br />";
					break;
				}
			}
			result = result + newChar;
		}
		return result;
	}

	public static String wordwrap(String targetString, int halfCharsPerLine) {
		return wordwrap(targetString, "<br />", halfCharsPerLine);
	}

	public static String wordwrap(String targetString, String wrapperString,
			int halfCharsPerLine) {
		String result = "";
		int size = targetString.length();
		int contCount = 0;
		boolean contSpace = false;
		for (int i = 0; i < size; i++) {
			char c = targetString.charAt(i);
			if (c == ' ') {
				if(contSpace == false) {
					contCount = 0;
					contSpace = true;
				}
			} else {
				contSpace = false;
			}
			if (c < 256) {
				// ASCII character
				contCount = contCount + 1;
			} else {
				// non-ASCII character
				contCount = contCount + 2;
			}
			if (contCount >= halfCharsPerLine) {
				result = result + wrapperString;
				contCount = 0;
			}
			result = result + c;
		}
		return result;
	}
}
