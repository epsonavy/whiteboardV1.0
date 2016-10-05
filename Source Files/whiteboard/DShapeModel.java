package whiteboard;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.util.ArrayList;

public class DShapeModel {

    private static int uniqueId = 1;
    private int id;
    private int index;
	private double x;
	private double y;
	private double width;
	private double height;
	private Color color;
    ArrayList<ModelListener> listeners = new ArrayList<>();

	public DShapeModel() {

		Random randNum = new Random();
		double x = randNum.nextInt(300) + 10;
		double y = randNum.nextInt(300) + 20;
		double width = randNum.nextInt(70) + 50;
		double height = randNum.nextInt(50) + 55;
		//	This line to show us the value of 
		//System.out.println("X\tY\tWidth\tHeight:\t" + x + "\t" + y + "\t" + width + "\t" + height);
		this.setWidth(width);
		this.setHeight(height);
		this.setX(x);
		this.setY(y);
		this.setId(uniqueId++);
	}
	public void setIndex(int index){
		this.index = index;
	}
	public int getIndex(){
		return index;
	}
	public void setId(int id){
		this.id = id;
	}
	public int getId(){
		return id;
	}
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
		notifyListeners();
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
		notifyListeners();
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
		notifyListeners();
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
		notifyListeners();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
        notifyListeners();
	}
    
	Rectangle2D getBounds() {
		return new Rectangle2D.Double(x, y, width, height);
	}


    public void notifyListeners(){
        for(ModelListener listener : listeners){
            listener.modelChanged(this);
        }
    }
    public void addListener(ModelListener listener){
        listeners.add(listener);
    }
    public void removeListener(ModelListener listener){
        int pos = 0xFF;
        for(int i=0; i<listeners.size(); i++){
            if(listeners.get(i) == listener)
                pos = i;
        }
        if(pos != 0xFF) listeners.remove(pos);
    }
   
}
