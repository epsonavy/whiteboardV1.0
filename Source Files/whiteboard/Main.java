package whiteboard;

import javax.swing.SwingUtilities;

/**
	@Authors:
	Pei Liu - epsonavy@yahoo.com
	Long Trinh - kevintrinh255@gmail.com
	Long Vu - tuanngo95117@yahoo.com
	Paul Vu - paul.hung.vu@gmail.com
 */

public class Main {
	public static final boolean debugNetwork = false;
	public static final boolean debugCanvas = false;
	public static final boolean debugWhiteboard = false;
	public static void main(String[] args) throws Exception {
		SwingUtilities.invokeLater( ()->new Whiteboard() );
	}
  
}
