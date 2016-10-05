package whiteboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class Whiteboard extends JFrame {
	private static final long serialVersionUID = 1L;

	private final JButton rect = new JButton("Rect");
	private final JButton oval = new JButton("Oval");
	private final JButton line = new JButton("Line");
	private final JButton text = new JButton("Text");
	private final JButton setColor = new JButton("Set Color");
	private final JTextField textContent = new JTextField("Hello");
	private final JLabel showJLColor = new JLabel("   Current Color   ");

	private final JComboBox<String> fontComboBox = new JComboBox<>();
	private final JButton moveToFront = new JButton("Move To Front");
	private final JButton moveToBack = new JButton("Move To Back");
	private final JButton removeShape = new JButton("Remove Shape");
	private final Canvas canvas = new Canvas();

	private String[] dataTable = new String[4];
	private String[] columns = new String[] { "X", "Y", "Width", "Height" };
	private DefaultTableModel model = new DefaultTableModel(columns, 0);
	private JTable table = new JTable(model);
	private JScrollPane jsp = new JScrollPane(table);

	private int networkState = 0;
	private NetworkModel networkModel = new NetworkModel();
	private NetworkServer networkServer;
	private NetworkClient networkClient; 

	public Whiteboard() {
		super("Whiteboard v1.0");
		setPreferredSize(new java.awt.Dimension(800, 400));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		constructFrameComponents();

		setVisible(true);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		JMenuItem openItem = new JMenuItem("Open");
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				openFile();
			}
		});
		fileMenu.add(openItem);

		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				saveFile();
			}
		});
		fileMenu.add(saveItem);

		JMenuItem saveAsPNG = new JMenuItem("Save As PNG");
		saveAsPNG.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				saveImage();
			}
		});
		fileMenu.add(saveAsPNG);

		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);
		JMenu networkMenu = new JMenu("Networking");
		menuBar.add(networkMenu);

		JMenuItem hostServerItem = new JMenuItem("Host Server");
		hostServerItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				initServer();
			}
		});
		networkMenu.add(hostServerItem);

		JMenuItem connectServerItem = new JMenuItem("Connect to Server");
		connectServerItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				initClient();
			}
		});
		networkMenu.add(connectServerItem);
	}

	private void constructFrameComponents() {
		Box addingBox = Box.createHorizontalBox();
		addingBox.setBorder(BorderFactory.createTitledBorder("Add"));
		addingBox.add(rect);
		addingBox.add(Box.createHorizontalStrut(15));
		addingBox.add(oval);
		addingBox.add(Box.createHorizontalStrut(15));
		addingBox.add(line);
		addingBox.add(Box.createHorizontalStrut(15));
		addingBox.add(text);

		Box settingBox = Box.createHorizontalBox();
		settingBox.setBorder(BorderFactory.createTitledBorder("Set Color"));
		settingBox.add(setColor);
		settingBox.add(Box.createHorizontalStrut(15));
		settingBox.add(Box.createHorizontalStrut(15));
		settingBox.add(showJLColor);
		showJLColor.setBackground(Color.GRAY);
		showJLColor.setFont(new Font("Serif", Font.BOLD, 20));
		showJLColor.setBorder(BorderFactory.createLineBorder(Color.black));
		showJLColor.setOpaque(true);

		settingBox.add(Box.createHorizontalStrut(15));

		Box editingBox = Box.createHorizontalBox();
		editingBox.setBorder(BorderFactory.createTitledBorder("Notes"));
		editingBox.add(textContent);
		editingBox.add(Box.createHorizontalStrut(15));
		editingBox.add(fontComboBox);

		Box modifyBox = Box.createHorizontalBox();
		modifyBox.setBorder(BorderFactory.createTitledBorder("Modify Objects"));
		modifyBox.add(moveToFront);
		modifyBox.add(Box.createHorizontalStrut(5));
		modifyBox.add(moveToBack);
		modifyBox.add(Box.createHorizontalStrut(5));
		modifyBox.add(removeShape);

		Box tableBox = Box.createHorizontalBox();
		tableBox.setBorder(BorderFactory.createTitledBorder("Attributes"));
		jsp.setPreferredSize(new Dimension(0, 1400));
		tableBox.add(jsp);
		table.setGridColor(Color.BLACK);

		Box controlBox = Box.createVerticalBox();

		controlBox.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Control Panel"));
		controlBox.add(Box.createVerticalStrut(1));
		controlBox.add(addingBox);
		controlBox.add(Box.createVerticalStrut(1));
		controlBox.add(settingBox);
		controlBox.add(Box.createVerticalStrut(1));
		controlBox.add(editingBox);
		controlBox.add(modifyBox);
		controlBox.add(Box.createVerticalStrut(1));
		controlBox.add(tableBox);

		for (Component comp : controlBox.getComponents()) {
			((JComponent) comp).setAlignmentX(Box.LEFT_ALIGNMENT);
		}

		add(controlBox, BorderLayout.WEST);
		add(canvas, BorderLayout.CENTER);

		pack();

		setLocationRelativeTo(null);

		rect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DShapeModel model = new DRectModel();
				model.addListener(networkModel);
				canvas.addShape(model);
				if (getNetworkState() == 1)
					networkServer.addModel(model);
				updateAll();
			}
		});

		oval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DShapeModel model = new DOvalModel();
				model.addListener(networkModel);
				canvas.addShape(model);
				if (getNetworkState() == 1)
					networkServer.addModel(model);
				updateAll();
			}
		});

		line.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DShapeModel model = new DLineModel();
				model.addListener(networkModel);
				canvas.addShape(model);
				if (getNetworkState() == 1)
					networkServer.addModel(model);
				updateAll();
			}
		});

		text.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFonts();
				DTextModel model = new DTextModel();
				model.addListener(networkModel);
				canvas.addShape(model);
				if (getNetworkState() == 1)
					networkServer.addModel(model);
				updateAll();
			}
		});

		setColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color color = null;
				color = JColorChooser.showDialog(null, "Choose Color", color);
				if (color == null) {
					color = (Color.GRAY);
				}
				if (canvas.getSelected() != null) {
					DShape s = (DShape) canvas.getSelected();
					s.pointer.setColor(color);
					repaint();
				}
				canvas.updateColor(color);
				
				showJLColor.setVisible(true);
				showJLColor.setForeground(color);
				showJLColor.setBackground(color);
				showJLColor.setOpaque(true);
			}
		});

		textContent.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (canvas.getSelected() instanceof DText) {
					DText s = (DText) canvas.getSelected();
					s.p.setText(textContent.getText());
					repaint();
				}
			}
		});

		fontComboBox.setEditable(false);
		GraphicsEnvironment e1 = GraphicsEnvironment.getLocalGraphicsEnvironment();

		Font[] fonts = e1.getAllFonts();
		for (Font f : fonts) {
			fontComboBox.addItem(f.getFontName());
			applyCurrentFont();
		}
		fontComboBox.setSelectedIndex(3); 
		
		fontComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (canvas.getSelected() instanceof DText) {
					DText s = (DText) canvas.getSelected();
					s.p.setFont((String) fontComboBox.getSelectedItem());
					setFonts();
					repaint();
				} else {
					applyCurrentFont();
				}
			}
		});
		moveToFront.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(canvas.getSelected() != null) 
					moveToFront(((DShape) canvas.getSelected()).pointer);
			}

		});

		moveToBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(canvas.getSelected() != null) 
					moveToBack(((DShape) canvas.getSelected()).pointer);
			}
		});

		removeShape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() >= 0) {
					DShapeModel removed = canvas.removeShape(table.getSelectedRow());
					removed.removeListener(networkModel);
					removeRow(table.getSelectedRow());
					if (Main.debugWhiteboard)
						System.out.println("Index in Whiteboard: " + canvas.getIndexOfAtributes());
					if (getNetworkState() == 1)
						networkServer.removeModel(removed);
				} else if (table.getSelectedRow() < 0) {
					DShapeModel removed = canvas.removeShape();
					if(removed != null){
						removed.removeListener(networkModel);
						if (getNetworkState() == 1)
							networkServer.removeModel(removed);
						if (canvas.getIndexOfAtributes() >= 0) {
							removeRow(canvas.getIndexOfAtributes());
							if (Main.debugWhiteboard)
								System.out.println("Index in Whiteboard: " + canvas.getIndexOfAtributes());
						}
					}
				} else {
					JOptionPane.showMessageDialog(canvas, "Need to select an item to remove!");
				}
				table.setSelectionBackground(Color.GRAY);
				repaint();
			}
		});

		canvas.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				table.clearSelection();
				table.setSelectionBackground(Color.GRAY);
				Point2D mousePoint = event.getPoint();
				if (mousePoint != null) {
					if (Main.debugWhiteboard)
						System.out.println("mouse inisde Whiteboard is kicked in");
					int tempIndex = canvas.getCurrentIndexOfOject();
					if (canvas.isShapeChange()) {
						table.addRowSelectionInterval(tempIndex, tempIndex);
						String[] temp = canvas.getData();
						for (int i = 0; i < table.getColumnCount(); i++) {
							if (Main.debugWhiteboard)
								System.out.println("canvas.getCurrentIndexOfOject()++++: " + tempIndex);
							table.setValueAt(temp[i], canvas.getCurrentIndexOfOject(), i);
							canvas.setShapeChange(false);
						}

					}
				}
				repaint();
			}
		});
	}

	private void moveToFront(DShapeModel move) {
		DShape[] shapes = canvas.getShapesList();
		for (DShape s : shapes) {
			if (s.pointer.getId() == move.getId()) {
				ArrayList<DShape> list = canvas.getArrayList();
				if (Main.debugWhiteboard)
					System.out.println("canvas.getCurrentIndexOfOject(): " + canvas.getCurrentIndexOfOject());
				if (Main.debugWhiteboard)
					System.out.println("list.size(): " + list.size());
				int selectIndex = canvas.findShapeIndexById(move.getId());
				if (selectIndex < (list.size() - 1)) {
					model.removeRow(selectIndex);
					list.add(s);
					list.remove(s);
					model.insertRow(list.size() - 1, canvas.getData());
					table.clearSelection();
					table.addRowSelectionInterval(list.size() - 1, list.size() - 1);
				}
			}
		}
		repaint();
		if (getNetworkState() == 1)
			networkServer.moveFrontModel(move);
	}

	private void moveToBack(DShapeModel move) {
		DShape[] shapes = canvas.getShapesList();
		for (DShape s : shapes) {
			if (s.pointer.getId() == move.getId()) {
				ArrayList<DShape> list = canvas.getArrayList();
				int selectIndex = canvas.findShapeIndexById(move.getId());
				if (selectIndex != 0) {
					model.removeRow(selectIndex);
					DShape firstShape = list.get(0);
					list.set(0, s);
					list.set(selectIndex, firstShape);
					model.insertRow(0, canvas.getData());
					table.clearSelection();
					table.addRowSelectionInterval(0, 0);
				}
			}
		}

		repaint();
		if (getNetworkState() == 1)
			networkServer.moveBackModel(move);
	}

	/**
	 * Asks the user to open a graph file.
	 */
	private void openFile() {
		JFileChooser fileChooser = new JFileChooser();
		int r = fileChooser.showOpenDialog(this);
		if (r == JFileChooser.APPROVE_OPTION) {
			try {
				File file = fileChooser.getSelectedFile();
				XMLDecoder xmlIn = new XMLDecoder(new BufferedInputStream(new FileInputStream(file)));
				DShapeModel[] modelArray = (DShapeModel[]) xmlIn.readObject();
				xmlIn.close();
				canvas.clear();
				for (DShapeModel m : modelArray) {
					canvas.addShape(m);
				}
				validate();
				repaint();
			} catch (IOException exception) {
				JOptionPane.showMessageDialog(null, exception);
			}
		}
	}

	/**
	 * Saves the current graph in a file.
	 */
	private void saveFile() {
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				File file = fileChooser.getSelectedFile();
				XMLEncoder xmlOut = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)));
				DShapeModel[] modelArray = new DShapeModel[canvas.getListSize()];
				modelArray = canvas.getShapeModelList();
				xmlOut.writeObject(modelArray);
				xmlOut.close();
			} catch (IOException exception) {
				JOptionPane.showMessageDialog(null, exception);
			}
		}
	}

	public void saveImage() {
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				File file = fileChooser.getSelectedFile();
				BufferedImage image = (BufferedImage) createImage(canvas.getWidth(), canvas.getHeight());
				Graphics g = image.getGraphics();
				canvas.paint(g);
				g.dispose();
				javax.imageio.ImageIO.write(image, "PNG", new File(file.toString() + ".png"));
			} catch (IOException exception) {
				JOptionPane.showMessageDialog(null, exception);
			}
		}
	}

	private void addRow(String x, String y, String w, String h) {
		Object[] row = new Object[4];
		row[0] = x;
		row[1] = y;
		row[2] = w;
		row[3] = h;
		model.addRow(row);
	}

	private void removeRow(int rowNumber) {
		if (model.getRowCount() > 0) {
			if (rowNumber >= 0) {
				model.removeRow(rowNumber);
			}
		}
	}

	public void paint(Graphics g) {
		super.paint(g);
		if (canvas.getSelected() instanceof DText || canvas.getSelected() == null)
			setTextEnable(true);
		else
			setTextEnable(false);

	}

	private void updateAll() {
		dataTable = canvas.getData();
		addRow(dataTable[0], dataTable[1], dataTable[2], dataTable[3]);
		repaint();
	}
	private void setFonts() {
		canvas.updateText(textContent.getText(), applyCurrentFont());
	}

	private String applyCurrentFont() {
		String fontName = (String) fontComboBox.getSelectedItem();
		textContent.setFont(new Font(fontName, Font.PLAIN, 14));
		return fontName;
	}

	public void setTextEnable(Boolean trueFalse) {
		textContent.setEnabled(trueFalse);
	}

	public void setNetworkState(int networkState) {
		this.networkState = networkState;
	}

	public int getNetworkState() {
		return networkState;
	}

	public void initServer() {
		String input = JOptionPane.showInputDialog("Run server on what port?", "39587");
		if (input != null) {
			networkServer = new NetworkServer(Integer.parseInt(input.trim()));
			if (networkServer.getOnline()) {
				JOptionPane.showMessageDialog(null, "Successfully started server", "Host a server",
						JOptionPane.PLAIN_MESSAGE);
				setNetworkState(1);
				networkServer.start();
			}
		}
	}

	public void initClient() {
		String input = JOptionPane.showInputDialog("Connect to what server? (Host:Port)", "127.0.0.1:39587");
		if (input != null) {
			String[] addressAndPort = input.split(":");
			networkClient = new NetworkClient(addressAndPort[0].trim(), Integer.parseInt(addressAndPort[1].trim()));
			if (networkClient.getOnline()) {
				JOptionPane.showMessageDialog(null, "Successfully connected", "Host a server",
						JOptionPane.PLAIN_MESSAGE);
				setNetworkState(2);
				networkClient.start();
			}
		}
	}
	private class NetworkModel extends Thread implements ModelListener {
		public NetworkModel() {

		}

		public void modelChanged(DShapeModel model) {
			if (getNetworkState() == 1) {
				ByteArrayOutputStream objectData = new ByteArrayOutputStream();
				XMLEncoder e = new XMLEncoder(objectData);
				e.writeObject(model);
				e.close();

				NetworkPacket changePacket = new NetworkPacket(NetworkPacket.PACKET_CHANGE,
						new String(objectData.toByteArray()));
				networkServer.writeAll(changePacket);
			}
		}
	}

	private class NetworkPacket {
		public final static String PACKET_ADD = "ADD";
		public final static String PACKET_REMOVE = "REMOVE";
		public final static String PACKET_CHANGE = "CHANGE";
		public final static String PACKET_FRONT = "FRONT";
		public final static String PACKET_BACK = "BACK";

		private String type;
		private String data;

		public NetworkPacket() {
			type = null;
			data = null;
		}

		public NetworkPacket(String type, String data) {
			this.type = type;
			this.data = data;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public void writeStream(ObjectOutputStream out) {
			try {
				out.writeObject(getType());
				out.writeObject(getData());
			} catch (Exception e) {
				if (Main.debugNetwork)
					System.out.println(e.getMessage());
			}
		}

		public void readStream(ObjectInputStream in) {
			try {
				setType((String) in.readObject());
				setData((String) in.readObject());
			} catch (Exception e) {
				if (Main.debugNetwork)
					System.out.println(e.getMessage());
			}
		}
	}

	private class NetworkServer extends Thread {
		private ArrayList<Socket> clients;
		private ServerSocket socket;
		private boolean online;

		public NetworkServer(int port) {
			clients = new ArrayList<>();
			setOnline(true);
			try {
				socket = new ServerSocket(port);
			} catch (BindException e) {
				if (Main.debugNetwork)
					System.out.println(e.getMessage());
				setOnline(false);
				JOptionPane.showMessageDialog(null, e.getMessage(), "Host a server", JOptionPane.ERROR_MESSAGE);
			} catch (SocketException e) {
				if (Main.debugNetwork)
					System.out.println(e.getMessage());
			} catch (Exception e) {
				if (Main.debugNetwork)
					System.out.println(e.getMessage());
				setOnline(false);
				JOptionPane.showMessageDialog(null, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
			}
		}

		public void run() {
			while (online) {
				try {
					Socket addSocket = socket.accept();
					if (Main.debugNetwork)
						System.out.println("Client has connected!");
					onConnection(addSocket);
					clients.add(addSocket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void onConnection(Socket client) throws IOException {
			if (Main.debugNetwork)
				System.out.println("Sending all current objects in model...");
			DShapeModel[] modelArray = new DShapeModel[canvas.getListSize()];
			modelArray = canvas.getShapeModelList();
			for (DShapeModel model : modelArray) {
				ByteArrayOutputStream objectData = new ByteArrayOutputStream();
				XMLEncoder e = new XMLEncoder(objectData);
				e.writeObject(model);
				e.close();
				NetworkPacket addPacket = new NetworkPacket(NetworkPacket.PACKET_ADD,
						new String(objectData.toByteArray()));
				write(addPacket, client);
			}
		}

		private void write(NetworkPacket packet, Socket client) {
			try {
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
				packet.writeStream(out);
			} catch (IOException e) {
				if (Main.debugNetwork)
					System.out.println(e.getMessage());
			}
		}

		private void writeAll(NetworkPacket packet) {
			for (Socket client : clients) {
				try {
					ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
					packet.writeStream(out);
				} catch (IOException e) {
					if (Main.debugNetwork)
						System.out.println(e.getMessage());
				}
			}
		}
		
		public void addModel(DShapeModel model) {
			ByteArrayOutputStream objectData = new ByteArrayOutputStream();
			XMLEncoder e = new XMLEncoder(objectData);
			e.writeObject(model);
			e.close();
			NetworkPacket addPacket = new NetworkPacket(NetworkPacket.PACKET_ADD, new String(objectData.toByteArray()));
			writeAll(addPacket);
		}

		public void moveFrontModel(DShapeModel model) {
			ByteArrayOutputStream objectData = new ByteArrayOutputStream();
			XMLEncoder e = new XMLEncoder(objectData);
			e.writeObject(model);
			e.close();
			NetworkPacket frontPacket = new NetworkPacket(NetworkPacket.PACKET_FRONT,
					new String(objectData.toByteArray()));
			writeAll(frontPacket);
		}

		public void moveBackModel(DShapeModel model) {
			ByteArrayOutputStream objectData = new ByteArrayOutputStream();
			XMLEncoder e = new XMLEncoder(objectData);
			e.writeObject(model);
			e.close();
			NetworkPacket backPacket = new NetworkPacket(NetworkPacket.PACKET_BACK,
					new String(objectData.toByteArray()));
			writeAll(backPacket);
		}

		public void removeModel(DShapeModel model) {
			ByteArrayOutputStream objectData = new ByteArrayOutputStream();
			XMLEncoder e = new XMLEncoder(objectData);
			e.writeObject(model);
			e.close();
			NetworkPacket removePacket = new NetworkPacket(NetworkPacket.PACKET_REMOVE,
					new String(objectData.toByteArray()));
			writeAll(removePacket);
		}

		public boolean getOnline() {
			return online;
		}

		public void setOnline(boolean online) {
			this.online = online;
		}
	}

	private class NetworkClient extends Thread {
		private Socket socket;
		private boolean online;

		public NetworkClient(String address, int port) {
			setOnline(true);
			try {
				socket = new Socket(address, port);
			} catch (SocketException e) {
				if (Main.debugNetwork)
					System.out.println(e.getMessage());
				setOnline(false);
				JOptionPane.showMessageDialog(null, e.getMessage(), "SocketException", JOptionPane.ERROR_MESSAGE);
			} catch (UnknownHostException e) {
				if (Main.debugNetwork)
					System.out.println(e.getMessage());
				setOnline(false);
				JOptionPane.showMessageDialog(null, e.getMessage(), "UnknownHostException", JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				if (Main.debugNetwork)
					System.out.println(e.getMessage());
				setOnline(false);
				JOptionPane.showMessageDialog(null, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
			}
		}

		public void run() {
			while (online) {
				try {
					NetworkPacket packetIncoming = new NetworkPacket();
					ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
					packetIncoming.readStream(in);
					
					ByteArrayInputStream objectData = new ByteArrayInputStream(packetIncoming.getData().getBytes());
					XMLDecoder d = new XMLDecoder(objectData);
					DShapeModel model = (DShapeModel) d.readObject();
					d.close();
					
					if (model != null) {
						switch (packetIncoming.getType()) {
						case NetworkPacket.PACKET_ADD:
							if (model instanceof DTextModel)
								setFonts();
							canvas.addShape(model);
							updateAll();
							break;
						case NetworkPacket.PACKET_BACK:
							moveToBack(model);
							repaint();
							break;
						case NetworkPacket.PACKET_CHANGE:
							canvas.updateModel(model);
							repaint();
							break;
						case NetworkPacket.PACKET_FRONT:
							moveToFront(model);
							repaint();
							break;
						case NetworkPacket.PACKET_REMOVE:
							canvas.removeShape(model);
							removeRow(model.getIndex());
							repaint();
							break;
						}
					}
				} catch (IOException e) {
					if (Main.debugNetwork)
						System.out.println(e.getMessage());
				}
			}
		}

		public boolean getOnline() {
			return online;
		}

		public void setOnline(boolean online) {
			this.online = online;
		}
	}

}