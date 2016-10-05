package whiteboard;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class DText extends DShape {
	DTextModel p = (DTextModel) pointer;

	DText() {
		super();
	}
	DText(DShapeModel model, String text, String fontName) {
		super(model);
		p.setText(text);
		p.setFont(fontName);
	}
	DText(DShapeModel model) {
		super(model);
		p.setText(p.getText());
		p.setFont(p.getFont());
	}

	public int getFontSize() {
		return (int) p.getHeight();
	}
	
	private double computeFont(Graphics2D g2) {
		double size = 1.0;
		Font f = new Font(p.getFont(), Font.PLAIN, (int)size);
		FontMetrics metrics = g2.getFontMetrics(f);
		while (metrics.getHeight() < getBounds().getHeight()) {
			size = (size * 1.10) + 1;
			f = new Font(p.getFont(), Font.PLAIN, (int)size);
			metrics = g2.getFontMetrics(f);
		}
		return size;
	}
	
	@Override
	void draw(Graphics2D g2) {
		g2.setColor(pointer.getColor());
		Font f = new Font(p.getFont(), Font.PLAIN, (int)computeFont(g2));
		g2.setFont(f);
		Shape clip = g2.getClip();
		g2.setClip(clip.getBounds().createIntersection(getBounds()));
		g2.drawString(p.getText(), (float)p.getX(), (float)(p.getY() + p.getHeight()));
		g2.setClip(clip);
	}
	
	public boolean contains(Point2D p) {
		Rectangle2D rect = pointer.getBounds();
		return rect.contains(p);
	}
	
}
