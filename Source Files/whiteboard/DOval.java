package whiteboard;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class DOval extends DShape {
	DOval() {
		super();
	}

	DOval(DShapeModel model) {
		super(model);
	}

	@Override
	void draw(Graphics2D g2) {
		g2.setPaint(pointer.getColor());
		g2.fill(new Ellipse2D.Double(pointer.getBounds().getX(), pointer.getBounds().getY(), pointer.getBounds().getWidth(),
				pointer.getBounds().getHeight()));
	}
	
	public boolean contains(Point2D p) {
	     Ellipse2D circle = new Ellipse2D.Double(pointer.getBounds().getX(), pointer.getBounds().getY(), pointer.getBounds().getWidth(),
					pointer.getBounds().getHeight());
	     return circle.contains(p);
	}
	
}
