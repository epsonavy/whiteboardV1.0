package whiteboard;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class DRect extends DShape {
  
	DRect() {
		super();
	}
  
	DRect(DShapeModel model) {
		super(model);
	}
  
	void draw(Graphics2D g2) {
		g2.setPaint(pointer.getColor());
	    g2.fill(pointer.getBounds());
	}
  
	public boolean contains(Point2D p) {
		Rectangle2D rect = pointer.getBounds();
		return rect.contains(p);
	}
}
