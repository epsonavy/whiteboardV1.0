package whiteboard;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class DShape {
  protected DShapeModel pointer;
  
  DShape() {
	  pointer = null;
  } 
  
  DShape(DShapeModel model) {
	  pointer = model;
  } 
  
  void draw(Graphics2D g2) { }
  
  public void resize(Point2D p1, Point2D p2) {
	  double leftTopX = Math.min(p1.getX(), p2.getX());
	  double leftTopY = Math.min(p1.getY(), p2.getY());
	  double width = Math.abs(p1.getX() - p2.getX());
	  double height = Math.abs(p1.getY() - p2.getY());
	  setBounds(leftTopX, leftTopY, width, height);
  }
  
  public void setBounds(double leftTopX, double leftTopY, double width, double height) {
	  pointer.setX(leftTopX);
	  pointer.setY(leftTopY);
	  pointer.setWidth(width);
	  pointer.setHeight(height);
  }
  
  public Rectangle2D getBounds() {
	  return pointer.getBounds();
  }
  
  public boolean contains(Point2D p) {
	  Ellipse2D circle = new Ellipse2D.Double(pointer.getBounds().getX(), pointer.getBounds().getY(), pointer.getBounds().getWidth(),
				pointer.getBounds().getHeight());
	  return circle.contains(p);
  }

  public void moveBy(double dx, double dy){
	  pointer.setX(pointer.getX() + dx);
	  pointer.setY(pointer.getY() + dy);
  }
  
  public Point2D[] getKnobs() {
	  Point2D[] knobs = new Point2D[4]; 
	  knobs[0] = new Point2D.Double(getBounds().getMinX(), getBounds().getMinY());
	  knobs[1] = new Point2D.Double(getBounds().getMaxX(), getBounds().getMinY());
	  knobs[2] = new Point2D.Double(getBounds().getMaxX(), getBounds().getMaxY());
	  knobs[3] = new Point2D.Double(getBounds().getMinX(), getBounds().getMaxY());
	  return knobs;
  }
}
