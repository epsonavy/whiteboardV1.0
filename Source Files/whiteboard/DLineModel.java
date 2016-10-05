package whiteboard;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class DLineModel extends DShapeModel {
	private Point2D p1;
	private Point2D p2;
	
	public DLineModel() {
		super();
		p1 = new Point2D.Double(getX(), getY());
		p2 = new Point2D.Double(getX() + getWidth(), getY() + getHeight());
	}
	
	public Point2D getP1() {
		return p1;
	}

	public void setP1(Point2D p) {
		this.p1 = p;
		notifyListeners();
	}

	public Point2D getP2() {
		return p2;
	}

	public void setP2(Point2D p) {
		this.p2 = p;
		notifyListeners();
	}
	
	@Override
	Rectangle2D getBounds() {
		double width = Math.abs(p1.getX() - p2.getX());
		double height = Math.abs(p1.getY() - p2.getY());
		return new Rectangle2D.Double(p1.getX(), p1.getY(), width, height);
	}
}
