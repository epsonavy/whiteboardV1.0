package whiteboard;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class DLine extends DShape {

	DLineModel p = (DLineModel) pointer; 
	
	DLine() {
		super();
	}

	DLine(DShapeModel model) {
		super(model);
	}

	@Override
	void draw(Graphics2D g2) {
		g2.setColor(p.getColor());
		g2.draw(new Line2D.Double(p.getP1(), p.getP2()));	
	}
	
	@Override
	public void resize(Point2D p1, Point2D p2) {
		p.setP1(p1);
		p.setP2(p2);
	}
	
	@Override
	public boolean contains(Point2D p) {
		final double MAX_DISTANCE = 2;
	    return getConnectionPoints().ptSegDist(p) < MAX_DISTANCE;
	}
	
	public Line2D getConnectionPoints() {
	    return new Line2D.Double(p.getP1(), p.getP2());
	}
	
	@Override
	public void moveBy(double dx, double dy){
		p.setP1(new Point2D.Double(p.getP1().getX() + dx, p.getP1().getY() + dy));
		p.setP2(new Point2D.Double(p.getP2().getX() + dx, p.getP2().getY() + dy));
	}
	
	@Override
	public Point2D[] getKnobs() {
		  Point2D[] knobs = new Point2D[2]; 
		  knobs[0] = p.getP1();
		  knobs[1] = p.getP2();
		  return knobs;
	}
}
