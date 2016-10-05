package whiteboard;

public class DTextModel extends DShapeModel {
	private String text;
	private String fontName;
	
	public DTextModel() {
		super();
	}
	
	public String getText() {
		return text;
	}
	
	public String getFont() {
		return fontName;
	}
	
	public void setText(String text) {
		this.text = text;
		notifyListeners();
	}
	
	public void setFont(String font) {
		this.fontName = font;
	}
}
