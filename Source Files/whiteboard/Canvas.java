package whiteboard;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.awt.event.*;

import javax.swing.JPanel;

public class Canvas extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<DShape> ShapesList = new ArrayList<>();
	
	private Color currentColor = Color.GRAY;
	private String text;
	private String fontName;
	
	private Object selected;
	private Point2D lastMousePoint;
	private Point2D dragStartPoint;
	private Rectangle2D dragStartBounds;
	private Point2D movingPoint;
	private Point2D anchorPoint;
	
	private int indexOfOject = -1;
	private int currentIndexOfOject = -1;

	public int getCurrentIndexOfOject() {
		return currentIndexOfOject;
	}

	public void setCurrentIndexOfOject(int currentIndexOfOject) {
		this.currentIndexOfOject = currentIndexOfOject;
	}

	private boolean isShapeChange = false;

	public boolean isShapeChange() {
		return isShapeChange;
	}

	public void setShapeChange(boolean isShapeChange) {
		this.isShapeChange = isShapeChange;
	}

	private String[] data = new String[4];

	public Canvas() {
		setBackground(Color.WHITE);
		setPreferredSize(new java.awt.Dimension(400, 400));
		setVisible(true);
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				Point2D mousePoint = event.getPoint();
				lastMousePoint = mousePoint;
				DShape s = findShape(mousePoint);
				indexOfOject = findIndexOfObject(s);
				if (indexOfOject != -1) {
					currentIndexOfOject = indexOfOject;
				}
				if(Main.debugCanvas) System.out.println("indexOfOject: " + indexOfOject);
				if(Main.debugCanvas) System.out.println("currentIndexOfOject:" + currentIndexOfOject);

				if (s != null) {
					selected = s;
					dragStartPoint = mousePoint;
					dragStartBounds = s.getBounds();

				} else if (selected != null) {
					DShape s_selected = (DShape) selected;
					int indexOfKnob = findKnob(s_selected, mousePoint);
					if (indexOfKnob != -1) {
						if (s_selected instanceof DLine) {
							DLine line = (DLine) s_selected;
							Point2D[] knobs = line.getKnobs();
							movingPoint = knobs[indexOfKnob];
							anchorPoint = knobs[(indexOfKnob + 1) % 2];
						} else {
							Point2D[] knobs = s_selected.getKnobs();
							movingPoint = knobs[indexOfKnob];
							anchorPoint = knobs[(indexOfKnob + 2) % 4];
						}
					} else {
						selected = null;
					}
				} else {
					selected = null;
				}

				repaint();
			}

			public void mouseReleased(MouseEvent event) {
				if (dragStartBounds != null) {
					data[0] = Double.toString(dragStartBounds.getX());
					data[1] = Double.toString(dragStartBounds.getY());
					data[2] = Double.toString(dragStartBounds.getWidth());
					data[3] = Double.toString(dragStartBounds.getHeight());
					isShapeChange = true;
				} else if (movingPoint != null) { 

					if (movingPoint.getX() > anchorPoint.getX()) {
						data[2] = Double.toString((movingPoint.getX() > anchorPoint.getX())
								? movingPoint.getX() - anchorPoint.getX() : anchorPoint.getX() - movingPoint.getX());
						data[3] = Double.toString((movingPoint.getY() > anchorPoint.getY())
								? movingPoint.getY() - anchorPoint.getY() : anchorPoint.getY() - movingPoint.getY());

					} else {

						data[2] = Double.toString((anchorPoint.getX() > movingPoint.getX())
								? anchorPoint.getX() - movingPoint.getX() : movingPoint.getX() - anchorPoint.getX());
						data[3] = Double.toString((anchorPoint.getY() > movingPoint.getY())
								? anchorPoint.getY() - movingPoint.getY() : movingPoint.getY() - anchorPoint.getY());
					}
					isShapeChange = true;
				}
				for (String s : data) {
					if(Main.debugCanvas) System.out.print("------ " + s + " ");
				}
				if(Main.debugCanvas) System.out.println("\n");
				if(Main.debugCanvas) System.out.println("lastMousePoint: " + lastMousePoint);
				if(Main.debugCanvas) System.out.println("dragStartBounds: " + dragStartBounds);
				if(Main.debugCanvas) System.out.println("movingPoint: " + movingPoint);
				if(Main.debugCanvas) System.out.println("anchorPoint: " + anchorPoint);

				validate();
				repaint();

				lastMousePoint = null;
				dragStartBounds = null;
				movingPoint = null;
				anchorPoint = null;
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent event) {
				Point2D mousePoint = event.getPoint();
				if (movingPoint != null) {
					DShape s = (DShape) selected;
					movingPoint = mousePoint;
					s.resize(movingPoint, anchorPoint);
				}

				if (dragStartBounds != null) {
					if (selected instanceof DShape) {
						DShape s = (DShape) selected;
						Rectangle2D bounds = s.getBounds();
						s.moveBy(dragStartBounds.getX() - bounds.getX() + mousePoint.getX() - dragStartPoint.getX(),
								dragStartBounds.getY() - bounds.getY() + mousePoint.getY() - dragStartPoint.getY());
						s.getBounds();

					}
					isShapeChange = true;
				}
				lastMousePoint = mousePoint;
				repaint();
			}
		});
	}


	public Object getSelected() {
		return selected;
	}

	public String[] getData() {
		if ((ShapesList.size() - 1 >= 0) && !isShapeChange) {
			DShape shape = (DShape) ShapesList.get(ShapesList.size() - 1);
			data[0] = Double.toString(shape.pointer.getBounds().getX());
			data[1] = Double.toString(shape.pointer.getBounds().getY());
			data[2] = Double.toString(shape.pointer.getBounds().getWidth());
			data[3] = Double.toString(shape.pointer.getBounds().getHeight());

		}
		return data;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		for (int i = 0; i < ShapesList.size(); i++) {
			DShape shape = (DShape) ShapesList.get(i);
			shape.draw(g2);
		}

		if (selected instanceof DRect || selected instanceof DOval || selected instanceof DText) {
			Rectangle2D grabberBounds = ((DShape) selected).getBounds();
			drawKnob(g2, grabberBounds.getMinX(), grabberBounds.getMinY());
			drawKnob(g2, grabberBounds.getMinX(), grabberBounds.getMaxY());
			drawKnob(g2, grabberBounds.getMaxX(), grabberBounds.getMinY());
			drawKnob(g2, grabberBounds.getMaxX(), grabberBounds.getMaxY());
		}

		if (selected instanceof DLine) {
			Line2D line = ((DLine) selected).getConnectionPoints();
			drawKnob(g2, line.getX1(), line.getY1());
			drawKnob(g2, line.getX2(), line.getY2());
		}
	}

	public void updateColor(Color c) {
		currentColor = c;
	}

	public void updateText(String text, String fontName) {
		this.text = text;
		this.fontName = fontName;
	}

	public void updateModel(DShapeModel update) {
		for (DShape shape : getShapesList()) {
			if (shape.pointer.getId() == update.getId()) {
				if (update instanceof DLineModel) {
					DLine line = (DLine) shape;
					line.p = (DLineModel) update;
				}
				if (update instanceof DTextModel) {
					DText text = (DText) shape;
					text.p = (DTextModel) update;
				}
				shape.pointer = update;
			}
		}
	}

	public void addShape(DShapeModel m) {
		if (m.getColor() == null)
			m.setColor(currentColor);
		if (m instanceof DRectModel)
			ShapesList.add(new DRect(m));
		if (m instanceof DOvalModel)
			ShapesList.add(new DOval(m));
		if (m instanceof DLineModel)
			ShapesList.add(new DLine(m));
		if (m instanceof DTextModel)
			if (((DTextModel) m).getText() == null)
				ShapesList.add(new DText(m, text, fontName));
			else
				ShapesList.add(new DText(m));
	}
	public void removeShape(DShapeModel model){
		for(int i=0; i<ShapesList.size(); i++){
			if(ShapesList.get(i).pointer.getId() == model.getId()){
				ShapesList.remove(i);
				selected = null;
				return;
			}
		}
		
	}
	public DShapeModel removeShape(int index) {
		DShapeModel removed = ShapesList.get(index).pointer;
		removed.setIndex(index);
		ShapesList.remove(index);
		selected = null;
		if(Main.debugCanvas) System.out.println("Index in Canvas: " + index);
		return removed;
	}

	public DShapeModel removeShape() {
		indexOfOject = findIndexOfObject();
		if(Main.debugCanvas) System.out.println("Index in Canvas: " + indexOfOject);

		if (findIndexOfObject() != -1) {
			DShapeModel removed = ShapesList.get(findIndexOfObject()).pointer;
			removed.setIndex(findIndexOfObject());
			ShapesList.remove(indexOfOject);
			selected = null;
		}
		return null;
	}
	public int findShapeIndexById(int id){
		for(int i=0; i<ShapesList.size(); i++){
			if(ShapesList.get(i).pointer.getId() == id) return i;
 		}
		return -1;
	}
	/**
	 * Finds a shape containing the given point.
	 * 
	 * @param p
	 *            a point
	 * @return a shape containing p or null if no shapes contain p
	 */
	public DShape findShape(Point2D p) {
		for (int i = ShapesList.size() - 1; i >= 0; i--) {
			DShape s = (DShape) ShapesList.get(i);
			if (s.contains(p))
				return s;
		}
		return null;
	}

	/**
	 * Finds a knob
	 * 
	 * @param s
	 *            a DShape
	 * @param p
	 *            a point
	 * @return the number of the knob or return -1 indicate not found
	 */
	public int findKnob(DShape s, Point2D p) {
		final int SIZE = 9;
		Point2D[] knobs = s.getKnobs();
		for (int i = knobs.length - 1; i >= 0; i--) {
			Rectangle2D area = new Rectangle2D.Double(knobs[i].getX() - SIZE / 2, knobs[i].getY() - SIZE / 2, SIZE,
					SIZE);
			if (area.contains(p))
				return i;
		}
		return -1;
	}

	/**
	 * Draws a single "knob", a filled square
	 * 
	 * @param g2
	 *            the graphics context
	 * @param x
	 *            the x coordinate of the center of the knob
	 * @param y
	 *            the y coordinate of the center of the knob
	 */
	public static void drawKnob(Graphics2D g2, double x, double y) {
		final int SIZE = 9;
		Color oldColor = g2.getColor();
		g2.setColor(Color.BLACK);
		g2.fill(new Rectangle2D.Double(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE));
		g2.setColor(oldColor);
	}

	public DShapeModel[] getShapeModelList() {
		DShapeModel[] modelList = new DShapeModel[ShapesList.size()];
		for (int i = 0; i < ShapesList.size(); i++) {
			modelList[i] = ShapesList.get(i).pointer;
		}
		return modelList;
	}
	
	public ArrayList<DShape> getArrayList() {
		return ShapesList;
	}

	public int getListSize() {
		return ShapesList.size();
	}

	public void clear() {
		ShapesList.clear();
	}

	public DShape[] getShapesList() {
		DShape[] shapesList = ShapesList.toArray(new DShape[0]);
		return shapesList;
	}

	public int getIndexOfAtributes() {
		return indexOfOject;
	}

	public void setIndexOfAtributes() {
		indexOfOject = -1;
	}

	private int findIndexOfObject() {
		if (ShapesList.size() > 0) {
			for (int i = 0; i < ShapesList.size(); i++) {
				DShape shape = (DShape) ShapesList.get(i);
				if (shape.equals(selected)) {
					return i;
				}
			}
		}
		return -1;
	}

	private int findIndexOfObject(DShape inputShape) {
		if (ShapesList.size() > 0) {
			for (int i = 0; i < ShapesList.size(); i++) {
				DShape shape = (DShape) ShapesList.get(i);
				if (shape.equals(inputShape)) {
					return i;
				}
			}
		}
		return -1;
	}

}
