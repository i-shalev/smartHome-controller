package smartHome;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import tau.smlab.syntech.executor.ControllerExecutor;
import tau.smlab.syntech.executor.ControllerExecutorException;

public class smartHome extends JPanel{
	
	static listMoves moves;
	static boolean inScenario = false;
	static boolean startScenario = false;
	static boolean wait = false;
	static String nightmode;
	static int numberOfTurnsForNightMode;
	static JTextArea outputArea = new JTextArea("Here will be the output for scenarios and events \n", 0, 20);
	private static final long serialVersionUID = 1L;
	
	ControllerExecutor executor;
	private Image home;
	private Image motherImg;
	private Image fatherImg;
	private Image babyImg;
	private Image thiefImg;
	private Image cameraImg;
	private Image bulbOff;
	private Image bulbOn;
	private Image lockClose;
	private Image lockOpen;
	private Image alarmOffImg;
	private Image thiefAlarmImg;
	private Image babyAlarmImg;
	private Image sunImg;
	private Image moonImg;
	private HashMap<String, Boolean> mapBulbs;
	private int mother;
	private int father;
	private int baby;
	private int thief;
	private int camera;
	private boolean doorLock;
	private boolean thiefAlarm;
	private boolean babyAlarm;
	private boolean nightMode;

	private void initialFields() throws IOException
	{
		home 			= ImageIO.read(new File("img/home.jpg"));
		motherImg 		= ImageIO.read(new File("img/mother.jpg"));
		fatherImg 		= ImageIO.read(new File("img/father.jpg"));
		babyImg	 		= ImageIO.read(new File("img/baby.jpg"));
		thiefImg 		= ImageIO.read(new File("img/thief.jpg"));
		cameraImg		= ImageIO.read(new File("img/camera.jpg"));
		bulbOff 		= ImageIO.read(new File("img/bulbOff.jpg"));
		bulbOn 			= ImageIO.read(new File("img/bulbOn.jpg"));
		lockOpen 		= ImageIO.read(new File("img/lockOpen.jpg"));
		lockClose 		= ImageIO.read(new File("img/lockClose.jpg"));
		thiefAlarmImg 	= ImageIO.read(new File("img/alarmBlue.jpg"));
		babyAlarmImg 	= ImageIO.read(new File("img/alarmYellow.jpg"));
		alarmOffImg 	= ImageIO.read(new File("img/alarmOff.jpg"));
		sunImg 			= ImageIO.read(new File("img/sun.jpg"));
		moonImg 		= ImageIO.read(new File("img/moon.jpg"));
		
		mother 			= 2;
		father 			= 2;
		baby 			= 1;
		thief			= 5;
		camera			= 5;
		doorLock		= true;
		thiefAlarm		= false;
		babyAlarm		= false;
		nightMode		= false;
		
		mapBulbs = new HashMap<>();

		boolean[] defaultVaules = {false, false, false, false};
		setBulbsValue(defaultVaules);
	}
	
	public smartHome() throws IOException  {
		initialFields();
		
		Thread animationThread = new Thread(new Runnable() {
			public void run() {
				
				executor = new ControllerExecutor(true, false);
				try {
					executor.updateState(true);
				} catch (ControllerExecutorException e) {
					e.printStackTrace();
				}
				repaint();
				while (true) {
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					
					while(wait)
					{
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					
					if(numberOfTurnsForNightMode > 0)
					{
						try {
							executor.setInputValue("nightMode" ,nightmode);
						} catch (ControllerExecutorException e) {
							e.printStackTrace();
						}
						numberOfTurnsForNightMode--;
					}
					
					if(inScenario)
					{
						int[] envMoves = moves.getNextTurnValues();
						if(envMoves == null)
						{
							outputArea.setText(outputArea.getText()+"Done\n");
//							System.out.println("Done");
							inScenario = false;
							startScenario = false;
							try {
								Thread.sleep(4000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
						else
						{
							try {
								executor.setInputValue("mother"	,getRoomName(envMoves[0]));
								executor.setInputValue("father"	,getRoomName(envMoves[1]));
								executor.setInputValue("baby"	,getRoomName(envMoves[2]));
								executor.setInputValue("thief"	,getRoomName(envMoves[3]));
							} catch (ControllerExecutorException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
					try {
						executor.updateState(true);
					} catch (ControllerExecutorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					
					mother = getRoomNumber(executor.getCurInputs().get("mother"));
					father = getRoomNumber(executor.getCurInputs().get("father"));
					baby = getRoomNumber(executor.getCurInputs().get("baby"));
					thief = getRoomNumber(executor.getCurInputs().get("thief"));
					camera = getRoomNumber(executor.getCurOutputs().get("camera"));
					
					if(executor.getCurInputs().get("nightMode").equals("true"))
						nightMode = true;
					else
						nightMode = false;

					if(executor.getCurOutputs().get("lightLivingRoom").equals("true"))
						mapBulbs.put("livingRoom", true);
					else
						mapBulbs.put("livingRoom", false);

					if(executor.getCurOutputs().get("lightBabyRoom").equals("true"))
						mapBulbs.put("babyRoom", true);
					else
						mapBulbs.put("babyRoom", false);

					if(executor.getCurOutputs().get("lightParantsRoom").equals("true"))
						mapBulbs.put("parentsRoom", true);
					else
						mapBulbs.put("parentsRoom", false);
					
					if(executor.getCurOutputs().get("lightBathRoom").equals("true"))
						mapBulbs.put("bathRoom", true);
					else
						mapBulbs.put("bathRoom", false);
					
					if(executor.getCurOutputs().get("doorLocked").equals("true"))
						doorLock = true;
					else
						doorLock = false;
					
					if(executor.getCurOutputs().get("thiefAlarm").equals("true"))
						thiefAlarm = true;
					else
						thiefAlarm = false;
					
					if(executor.getCurOutputs().get("babyAloneAlarm").equals("true"))
						babyAlarm = true;
					else
						babyAlarm = false;

					repaint();
					
				}

			}
		});
		
		animationThread.start();
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(home, 0, 0, this);
		drawFather(g, father);
		drawMother(g, mother);
		drawBaby(g, baby);
		drawBulbs(g, mapBulbs);
		drawLock(g, doorLock);
		drawThief(g, thief);
		drawPoliceman(g, camera);
		drawAlarms(g, babyAlarm, thiefAlarm);
		drawNightMode(g, nightMode);
	}

	public void setBulbsValue(boolean[] arr)
	{
		if(arr.length != 4)
		{
			System.out.println("length of array is wrong in - setBulbsValue");
			return;
		}
		mapBulbs.put("livingRoom", arr[0]);
		mapBulbs.put("babyRoom", arr[1]);
		mapBulbs.put("parentsRoom", arr[2]);
		mapBulbs.put("bathRoom", arr[3]);  
	}

	private void drawFather(Graphics g, int room)
	{
		switch(room) {
		case 0:
			// living room
			g.drawImage(fatherImg, 210, 220, this);
			return;
		case 1:
			// baby room
			g.drawImage(fatherImg, 80, 50, this);
			return;
		case 2:
			// parents room
			g.drawImage(fatherImg, 350, 50, this);
			return;
		case 3:
			// bath room
			g.drawImage(fatherImg, 200, 40, this);
			return;
		case 4:
			//waiting outside
			g.drawImage(fatherImg, 210, 440, this);
			return;
		}

	}

	private void drawMother(Graphics g, int room)
	{
		switch(room) {
		case 0:
			// living room
			g.drawImage(motherImg, 210, 260, this);
			return;
		case 1:
			// baby room
			g.drawImage(motherImg, 80, 90, this);
			return;
		case 2:
			// parents room
			g.drawImage(motherImg, 310, 50, this);
			return;
		case 3:
			// bath room
			g.drawImage(motherImg, 200, 80, this);
			return;
		case 4:
			//waiting outside
			g.drawImage(motherImg, 250, 440, this);
			return;
		}
	}

	private void drawBaby(Graphics g, int room)
	{
		switch(room) {
		case 0:
			// living room
			g.drawImage(babyImg, 210, 302, this);
			return;
		case 1:
			// baby room
			g.drawImage(babyImg, 80, 132, this);
			return;
		case 2:
			// parents room
			g.drawImage(babyImg, 330, 92, this);
			return;
		case 3:
			// bath room
			g.drawImage(babyImg, 200, 122, this);
			return;
		case 4:
			//waiting outside
			g.drawImage(babyImg, 170, 440, this);
			return;
		}
	}

	private void drawBulbs(Graphics g, HashMap<String, Boolean> map)
	{
		if(map.get("parentsRoom"))
			g.drawImage(bulbOn, 420, 12, this);
		else
			g.drawImage(bulbOff, 420, 12, this);

		if(map.get("babyRoom"))
			g.drawImage(bulbOn, 130, 12, this);
		else
			g.drawImage(bulbOff, 130, 12, this);

		if(map.get("bathRoom"))
			g.drawImage(bulbOn, 240, 12, this);
		else
			g.drawImage(bulbOff, 240, 12, this);		  

		if(map.get("livingRoom"))
		{
			g.drawImage(bulbOn, 70, 225, this);
			g.drawImage(bulbOn, 420, 225, this);
		}
		else
		{
			g.drawImage(bulbOff, 70, 225, this);	
			g.drawImage(bulbOff, 420, 225, this);	
		}
	}

	private void drawLock(Graphics g, boolean lock)
	{
		if(lock)
			g.drawImage(lockClose, 270, 385, this);
		else
			g.drawImage(lockOpen, 270, 385, this);
	}
	
	private void drawThief(Graphics g, int room)
	{
		switch(room) {
		case 0:
			// living room
			g.drawImage(thiefImg, 210, 340, this);
			return;
		case 1:
			// baby room
			g.drawImage(thiefImg, 80, 180, this);
			return;
		case 2:
			// parents room
			g.drawImage(thiefImg, 330, 165, this);
			return;
		case 3:
			// bath room
			g.drawImage(thiefImg, 240, 85, this);
			return;
		case 4:
			//waiting outside
			g.drawImage(thiefImg, 320, 440, this);
			return;
		}
	}
	
	private void drawAlarms(Graphics g, boolean babyAlarm, boolean thiefAlarm)
	{
		if(babyAlarm)
			g.drawImage(babyAlarmImg, 10, 440, this);
		else
			g.drawImage(alarmOffImg, 10, 440, this);
		
		if(thiefAlarm)
			g.drawImage(thiefAlarmImg, 60, 440, this);
		else
			g.drawImage(alarmOffImg, 60, 440, this);
		
	}
	
	private void drawNightMode(Graphics g, boolean nightMode)
	{
		if(nightMode)
			g.drawImage(moonImg, 400, 440, this);
		else
			g.drawImage(sunImg, 400, 440, this);
		
	}
	
	private void drawPoliceman(Graphics g, int room)
	{
		switch(room) {
		case 0:
			// living room
			g.drawImage(cameraImg, 250, 302, this);
			return;
		case 1:
			// baby room
			g.drawImage(cameraImg, 122, 132, this);
			return;
		case 2:
			// parents room
			g.drawImage(cameraImg, 372, 92, this);
			return;
		case 3:
			// bath room
			g.drawImage(cameraImg, 240, 122, this);
			return;
		case 4:
			//waiting outside
			g.drawImage(cameraImg, 128, 440, this);
			return;
		}
	}
	
	public static String getRoomName(int room)
	{
		switch(room) {
		case 0:
			return "LIVING_ROOM";
		case 1:
			return "BABY_ROOM";
		case 2:
			return "PARANTS_ROOM";
		case 3:
			return "BATH_ROOM";
		case 4:
			return "WAIT_OUTSIDE";
		case 5:
			return "OUT";
		}
		return "ERROR";
	}
	
	public static int getRoomNumber(String room)
	{
		switch(room) {
		case "LIVING_ROOM":
		case "Living room":
			return 0;
		case "BABY_ROOM":
		case "Baby room":
			return 1;
		case "PARANTS_ROOM":
		case "Parents room":
			return 2;
		case "BATH_ROOM":
		case "Bathroom":
			return 3;
		case "WAIT_OUTSIDE":
		case "Wait outside":
			return 4;
		case "OUT":
		case "Outside":
			return 5;
		case "-----":
			int num = (int)(Math.random()*4);
			if( num > 4 || num < 0)
				num = 0;
			return num;
		}
		return -1;
	}
	
	public static void main(String[] args) throws IOException {
		
		JFrame home = new JFrame("SmartHome Simulator");
		smartHome smarthome = new smartHome();
		
		home.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		home.setSize(800, 550);
        
		home.setContentPane(createMainPanel(smarthome));
        home.setVisible(true);
	}  

	public static JPanel createHeadLinePanel()
	{
		JPanel headPanel = new JPanel(); 
		JLabel headLineLabel = new JLabel("<html><span style='font-size:20px'>SmartHome Simulator</span></html>");
        headPanel.add(headLineLabel);
		return headPanel;
	}
	
	public static JPanel createEventsPanel(smartHome smarthome)
	{
		JPanel eventsPanel 						= new JPanel(new BorderLayout());
		JPanel eventsPanelTop 					= new JPanel(new BorderLayout());
		JPanel eventsPanelTopwithLabel			= new JPanel(new BorderLayout());
		JPanel eventsPanelButtom			  	= new JPanel(new BorderLayout());
		JPanel eventsPanelButtomWithNightMode 	= new JPanel(new BorderLayout());
		JPanel nightModePanelWithLabel			= createNightModePanel();
		JPanel headLinePanel 					= new JPanel();
		JPanel motherPanel 						= new JPanel();
		JPanel fatherPanel 						= new JPanel();
		JPanel babyPanel 						= new JPanel();
		JPanel buttonPanel 						= new JPanel();
		
		
		String[] roomNames 	= { "-----", "Living room", "Baby room", "Parents room", "Bathroom", "Wait outside", "Outside"};
		
		JComboBox<String> roomListMother 	= new JComboBox<>(roomNames);
		JComboBox<String> roomListFather 	= new JComboBox<>(roomNames);
		JComboBox<String> roomListBaby 	= new JComboBox<>(roomNames);
		
		JLabel motherLabel = new JLabel("<html><span style='font-size:10px'>mother: </span></html>");
		JLabel fatherLabel = new JLabel("<html><span style='font-size:10px'>father: </span></html>");
		JLabel babyLabel = new JLabel("<html><span style='font-size:10px'>baby: </span></html>");
		JLabel headLineLabel = new JLabel("<html><span style='font-size:14px'>move family members </span></html>");
		
		motherPanel.add(motherLabel);
		motherPanel.add(roomListMother);
		
		fatherPanel.add(fatherLabel);
		fatherPanel.add(roomListFather);
		
		babyPanel.add(babyLabel);
		babyPanel.add(roomListBaby);
		
		headLinePanel.add(headLineLabel);
		
		JButton eventOkButton = new JButton("Go");  
        eventOkButton.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e) { 
        		if(startScenario)
        			return;
        		startScenario = true;
        		sendMembersToRooms(smarthome, roomListMother.getSelectedItem().toString(), roomListFather.getSelectedItem().toString(), roomListBaby.getSelectedItem().toString());
            }  
        });
        buttonPanel.add(eventOkButton);
        
		
        eventsPanelTop.add(motherPanel, BorderLayout.NORTH);
        eventsPanelTop.add(fatherPanel, BorderLayout.SOUTH);
        
        eventsPanelTopwithLabel.add(headLinePanel, BorderLayout.NORTH);
        eventsPanelTopwithLabel.add(eventsPanelTop, BorderLayout.SOUTH);
        
        eventsPanelButtom.add(babyPanel, BorderLayout.NORTH);
        eventsPanelButtom.add(buttonPanel, BorderLayout.SOUTH);
        
        eventsPanelButtomWithNightMode.add(eventsPanelButtom, BorderLayout.NORTH);
        eventsPanelButtomWithNightMode.add(nightModePanelWithLabel, BorderLayout.SOUTH);
        
        eventsPanel.add(eventsPanelTopwithLabel, BorderLayout.NORTH);
        eventsPanel.add(eventsPanelButtomWithNightMode, BorderLayout.SOUTH);
        
        return eventsPanel;
        
	}
	
	public static JPanel createNightModePanelWithThief(smartHome s)
	{
		JPanel nightModePanelWithThief	= new JPanel(new BorderLayout());
		JPanel thiefPanel			 	= new JPanel();
		JPanel nightModePanelWithLabel	= createNightModePanel();
		
		JButton thiefButton = new JButton("bring thief");  
		thiefButton.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e) { 
        		if(inScenario || startScenario)
        			return;
        		startScenario = true;
        		wait = true;
        		outputArea.setText("bring thief \n");

        		try {
        			Thread.sleep(2000);
        		} catch (InterruptedException e1) {
        			e1.printStackTrace();
        		}
        		
        		if(s.baby == 4)
        		{
        			getAllToLivingRoom(s);
        			moves.add(new move(0, 0, 0, 4));
        		}
        		else
        		{
        			moves = new listMoves();
            		
            		if(s.father == 4 && s.mother == 4)
            		{
            			moves.add(new move(0, 0, s.baby, 5));
            			moves.add(new move(0, 0, s.baby, 4));
            		}
            		else if(s.father == 4)
            		{
            			moves.add(new move(s.mother, 0, s.baby, 5));
            			moves.add(new move(s.mother, 0, s.baby, 4));
            		}
            		else if(s.mother == 4)
            		{
            			moves.add(new move(0, s.father, s.baby, 5));
            			moves.add(new move(0, s.father, s.baby, 4));
            		}
            		else
            		{
            			moves.add(new move(s.mother, s.father, s.baby, 5));
                		moves.add(new move(s.mother, s.father, s.baby, 4));
            		}
            		
            		
        		}
        		
        		
        		
        		inScenario = true;
        		wait = false;
        		
            }  
        });
		thiefPanel.add(thiefButton);
		
		nightModePanelWithThief.add(nightModePanelWithLabel, BorderLayout.NORTH);
		nightModePanelWithThief.add(thiefPanel, BorderLayout.SOUTH);
		
		return nightModePanelWithThief;
	}
	
	public static JPanel createNightModePanel()
	{
		String[] modesNames = { "Day", "Night"};
		
		JPanel nightModePanelWithLabel 	= new JPanel(new BorderLayout());
		JPanel nightModePanel			= new JPanel();
		JPanel nightModeLabelPanel		= new JPanel();
		
		JLabel nightModeLabel = new JLabel("<html><span style='font-size:14px'>set day / night mode </span></html>");
        
		JComboBox<String> modesList	= new JComboBox<>(modesNames);
		
		JButton modeOkButton = new JButton("Go");  
        modeOkButton.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e) {
        		if(inScenario || startScenario)
        			return;
        		outputArea.setText("Set mode to " + modesList.getSelectedItem().toString() + "\n");
        		setNightMode(modesList.getSelectedItem().toString(), 1);
            }  
        });
        
        nightModeLabelPanel.add(nightModeLabel);
        nightModePanel.add(modesList);
        nightModePanel.add(modeOkButton);
        
        nightModePanelWithLabel.add(nightModeLabelPanel, BorderLayout.NORTH);
        nightModePanelWithLabel.add(nightModePanel, BorderLayout.SOUTH);
		
		return nightModePanelWithLabel;
	}
	
	public static JPanel createScenariosPanel(smartHome smarthome)
	{
		String[] scenarioNames 	= { "Movie time", "Empty home", "Home alone", "Good night", "What a smart home", "Baby at day care", "Saturday at the beach", "Bring the thief"};
		
		JPanel headLinePanel 	 = new JPanel();
		JPanel scenariosPanel 	 = new JPanel(); 
		JPanel scenariosMainPanel = new JPanel(new BorderLayout());
		
		JLabel headLineLabel = new JLabel("<html><span style='font-size:14px'>scenarios </span></html>");
		
		headLinePanel.add(headLineLabel);
		
		JComboBox<String> scenatioList 	= new JComboBox<>(scenarioNames);
		
		JButton sendAllToLivingRoomButton = new JButton("Go");  
        sendAllToLivingRoomButton.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e) {  
        		if(startScenario)
        			return;
        		startScenario = true;
                doScenario(smarthome, scenatioList.getSelectedItem().toString());
            }  
        });
        
        scenariosPanel.add(scenatioList);
        scenariosPanel.add(sendAllToLivingRoomButton);
        
        scenariosMainPanel.add(headLinePanel, BorderLayout.NORTH);
        scenariosMainPanel.add(scenariosPanel, BorderLayout.CENTER);
        
		return scenariosMainPanel;
	}

	public static JPanel createControlPanel(smartHome smarthome)
	{
		JPanel controlPanel 					= new JPanel(new BorderLayout());
		JPanel controlPanelHeadAndEvents 		= new JPanel(new BorderLayout());
		JPanel controlPanelScenarionsAndOutput 	= new JPanel(new BorderLayout());
		JPanel outputPanelAndLabel			 	= new JPanel(new BorderLayout());
		JPanel headPanel 						= createHeadLinePanel(); 
		JPanel eventsPanel 						= createEventsPanel(smarthome); 
		JPanel scenariosPanel 					= createScenariosPanel(smarthome); 
		JPanel outputPanel						= new JPanel();
		JPanel outputLabelPanel						= new JPanel();
		
		JLabel outputLabel = new JLabel("<html><span style='font-size:14px'>output </span></html>");
		
		outputLabelPanel.add(outputLabel);
		outputPanel.add(outputArea);
		outputPanelAndLabel.add(outputLabelPanel, BorderLayout.NORTH);
		outputPanelAndLabel.add(outputPanel, BorderLayout.CENTER);
		
		controlPanelHeadAndEvents.add(headPanel, BorderLayout.NORTH);
        controlPanelHeadAndEvents.add(eventsPanel, BorderLayout.SOUTH);
        
        controlPanelScenarionsAndOutput.add(scenariosPanel, BorderLayout.NORTH);
        controlPanelScenarionsAndOutput.add(outputPanelAndLabel, BorderLayout.CENTER);
        
        controlPanel.add(controlPanelHeadAndEvents, BorderLayout.NORTH);
        controlPanel.add(controlPanelScenarionsAndOutput, BorderLayout.CENTER);
		
		return controlPanel;
	}
	
	public static JSplitPane createMainPanel(smartHome smarthome)
	{
		JPanel controlPanel = createControlPanel(smarthome);
		JSplitPane splitPanel = new JSplitPane(SwingConstants.VERTICAL, smarthome, controlPanel);
		splitPanel.setDividerLocation(480); 
		return splitPanel;
	}
	
	public static void doScenario(smartHome smarthome, String name)
	{
		if(inScenario)
			return;
		wait = true;
		outputArea.setText("Start scenario: " + name + "\n");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		moves = new listMoves();
		switch(name)
		{
			case "Movie time":
				movieTimeScenario(smarthome);
                break;
			case "Good night":
				goodNightScenario(smarthome);
				break;
			case "Empty home":
				emptyHomeScenario(smarthome);
				break;
			case "Home alone":
				homeAloneScenario(smarthome);
				break;
			case "What a smart home":
				whatSmartHomeScenario(smarthome);
				break;
			case "Baby at day care":
				babyAtDayCareScenario(smarthome);
				break;
			case "Saturday at the beach":
				saturdayAtTheBeachScenario(smarthome);
				break;
			case "Bring the thief":
				bringTheThiefScenario(smarthome);
				break;
		}
		
		inScenario = true;
		wait = false;
//		moves.print();
	}
	
	public static void setNightMode(String nightMode, int numOfTurns)
	{
		if(nightMode.equals("Night"))
			nightmode = "true";
		else
			nightmode = "false";
		numberOfTurnsForNightMode = numOfTurns;
	}
	
	public static void getAllToLivingRoom(smartHome s)
	{
		moves.add(new move(s.mother, s.father, s.baby, 5));
		getAllToLivingRoomStep(s.mother, s.father, s.baby, 5);
	}
	
	public static void getAllToLivingRoomStep(int mother, int father, int baby, int thief)
	{		
		if(father == 0 && mother == 0 && baby == 0)
		{
			return;
		}
		
		if(father == 0 && mother == 0)
		{
			if(baby == 1 || baby == 2 || baby == 3 || baby == 4)
			{
				moves.add(new move(mother, baby, baby, 5));
				moves.add(new move(mother, 0, 0, 5));
			}
			else // baby is OUT
			{
				moves.add(new move(mother, 4, baby, 5));
				moves.add(new move(mother, 5, baby, 5));
				moves.add(new move(mother, 4, 4, 5));
				moves.add(new move(mother, 0, 0, 5));
			}
			return;
		}
		
		if(father == 0)
		{
			if(mother == 1 || mother == 2 || mother == 3 || mother == 4)
			{ // mother can make 1 move to get into home
				if(baby == 0)
				{
					moves.add(new move(0, 0, 0, 5));
				}
				else if(baby == 1 || baby == 2 || baby == 3 || baby == 4)
				{
					moves.add(new move(0, baby, baby, 5));
					moves.add(new move(0, 0, 0, 5));
				}
				else // baby is OUT
				{
					moves.add(new move(0, 4, baby, 5));
					moves.add(new move(0, 5, baby, 5));
					moves.add(new move(0, 4, 4, 5));
					moves.add(new move(0, 0, 0, 5));
				}
				return;
			}
			else
			{ // mother is OUT
				if(baby == 0)
				{
					moves.add(new move(4, 0, 0, 5));
					moves.add(new move(0, 0, 0, 5));
				}
				else if(baby == 1 || baby == 2 || baby == 3 || baby == 4)
				{
					moves.add(new move(4, baby, baby, 5));
					moves.add(new move(0, 0, 0, 5));
				}
				else // baby is OUT
				{
					moves.add(new move(4, 4, baby, 5));
					moves.add(new move(0, 5, baby, 5));
					moves.add(new move(0, 4, 4, 5));
					moves.add(new move(0, 0, 0, 5));
				}
				return;				
			}
		}
		
		if(mother == 0)
		{
			if(father == 1 || father == 2 || father == 3 || father == 4)
			{ // father can make 1 move to get into home
				if(baby == 0)
				{
					moves.add(new move(0, 0, 0, 5));
				}
				else if(baby == 1 || baby == 2 || baby == 3 || baby == 4)
				{
					moves.add(new move(baby, 0, baby, 5));
					moves.add(new move(0, 0, 0, 5));
				}
				else // baby is OUT
				{
					moves.add(new move(4, 0, baby, 5));
					moves.add(new move(5, 0, baby, 5));
					moves.add(new move(4, 0, 4, 5));
					moves.add(new move(0, 0, 0, 5));
				}
				return;
			}
			else
			{ // father is OUT
				if(baby == 0)
				{
					moves.add(new move(0, 4, 0, 5));
					moves.add(new move(0, 0, 0, 5));
				}
				else if(baby == 1 || baby == 2 || baby == 3 || baby == 4)
				{
					moves.add(new move(baby, 4, baby, 5));
					moves.add(new move(0, 0, 0, 5));
				}
				else // baby is OUT
				{
					moves.add(new move(4, 4, baby, 5));
					moves.add(new move(5, 0, baby, 5));
					moves.add(new move(4, 0, 4, 5));
					moves.add(new move(0, 0, 0, 5));
				}
				return;			
			}
		}
		
		if(father != 0 && mother != 0)
		{
			int fatherNext, motherNext;
			if(father == 1 || father == 2 || father == 3 || father == 4)
				fatherNext = 0;
			else
				fatherNext = 4;
			if(mother == 1 || mother == 2 || mother == 3 || mother == 4)
				motherNext = 0;
			else
				motherNext = 4;
			moves.add(new move(motherNext, fatherNext, baby, 5));
			getAllToLivingRoomStep(motherNext, fatherNext, baby, 5);
			return;
		}
		System.out.println("Should not get here!");
	}

	public static void goodNightScenario(smartHome s)
	{
		getAllToLivingRoom(s);
		moves.add(new move(1, 2, 1, 5));
		moves.add(new move(0, 2, 1, 5));
		moves.add(new move(2, 2, 1, 5));
//		moves.add(new move(2, 2, 1, 5));
//		moves.add(new move(2, 2, 1, 5));
		setNightMode("Night", moves.length());
	}
	
	public static void emptyHomeScenario(smartHome s)
	{
		getAllToLivingRoom(s);
		moves.add(new move(4, 4, 4, 5));
		moves.add(new move(5, 5, 5, 5));
//		moves.add(new move(5, 5, 5, 5));
//		moves.add(new move(5, 5, 5, 5));
	}
	
	public static void movieTimeScenario(smartHome s)
	{
		getAllToLivingRoom(s);
//		moves.add(new move(0, 0, 0, 5));
//		moves.add(new move(0, 0, 0, 5));
	}
	
	public static void homeAloneScenario(smartHome s)
	{
		getAllToLivingRoom(s);
		moves.add(new move(4, 4, 0, 5));
		moves.add(new move(5, 5, 0, 5));
//		moves.add(new move(5, 5, 0, 5));
//		moves.add(new move(5, 5, 0, 5));
	}
	
	public static void whatSmartHomeScenario(smartHome s)
	{
		getAllToLivingRoom(s);
		moves.add(new move(3, 0, 3, 5));
		moves.add(new move(0, 0, 3, 5));
		moves.add(new move(0, 0, 3, 5));
		moves.add(new move(3, 0, 3, 5));
		moves.add(new move(0, 0, 0, 5));
//		moves.add(new move(0, 0, 0, 5));
//		moves.add(new move(0, 0, 0, 5));
	}

	public static void babyAtDayCareScenario(smartHome s)
	{
		getAllToLivingRoom(s);
		moves.add(new move(4, 0, 4, 5));
		moves.add(new move(5, 2, 5, 5));
		moves.add(new move(4, 2, 5, 5));
		moves.add(new move(0, 2, 5, 5));
		moves.add(new move(3, 2, 5, 5));
		moves.add(new move(0, 2, 5, 5));
		moves.add(new move(1, 0, 5, 5));
		moves.add(new move(0, 4, 5, 5));
		moves.add(new move(3, 5, 5, 4));
		moves.add(new move(3, 4, 4, 5));
		moves.add(new move(3, 0, 0, 5));
		
		setNightMode("Sun", moves.length());
	}
	
	public static void saturdayAtTheBeachScenario(smartHome s)
	{
		getAllToLivingRoom(s);
		moves.add(new move(4, 4, 4, 5));
		moves.add(new move(5, 5, 5, 4));
		moves.add(new move(5, 5, 5, 5));
		moves.add(new move(4, 4, 4, 5));
		moves.add(new move(0, 0, 0, 5));
		moves.add(new move(3, 0, 3, 5));
		moves.add(new move(0, 0, 0, 5));
		moves.add(new move(1, 3, 1, 5));
		moves.add(new move(0, 3, 1, 5));
		moves.add(new move(3, 0, 1, 5));
		moves.add(new move(3, 2, 1, 5));
		moves.add(new move(0, 2, 1, 5));
		moves.add(new move(2, 2, 1, 5));
//		moves.add(new move(2, 2, 1, 5));
//		moves.add(new move(2, 2, 1, 5));
		
		setNightMode("Sun", moves.length());
	}
	
	public static void bringTheThiefScenario(smartHome s)
	{
		getAllToLivingRoom(s);
		moves.add(new move(1, 3, 0, 4));
	}
	
	public static void sendMembersToRooms(smartHome s, String motherRoom, String fatherRoom, String babyRoom)
	{
		if(inScenario || (motherRoom == "-----" && fatherRoom == "-----" && babyRoom == "-----"))
			return;
		int mother = getRoomNumber(motherRoom);
		int father = getRoomNumber(fatherRoom);
		int baby = getRoomNumber(babyRoom);

		outputArea.setText("Start move family members: \n");
		if(motherRoom != "-----")
			outputArea.setText(outputArea.getText() + "mother to " + motherRoom + "\n");
		if(fatherRoom != "-----")
			outputArea.setText(outputArea.getText() + "father to " + fatherRoom + "\n");
		if(babyRoom != "-----")
			outputArea.setText(outputArea.getText() + "baby to " + babyRoom + "\n");
		
		wait = true;
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		moves = new listMoves();
		getAllToLivingRoom(s);
		if(baby == 5)
		{
			moves.add(new move(0, 4, 4, 5));
			moves.add(new move(0, 5, 5, 5));
			moves.add(new move(0, 4, 5, 5));
			moves.add(new move(0, 0, 5, 5));
		}
		else if(baby != 0)
		{
			moves.add(new move(0, baby, baby, 5));
			moves.add(new move(0, 0, baby, 5));
		}
		
		if(father != 5 && mother != 5)
		{
			moves.add(new move(mother, father, baby, 5));
//			moves.add(new move(mother, father, baby, 5));
//			moves.add(new move(mother, father, baby, 5));
		}
		else if(father != 5 && mother == 5)
		{
			moves.add(new move(4, father, baby, 5));
			moves.add(new move(5, father, baby, 5));
//			moves.add(new move(5, father, baby, 5));
//			moves.add(new move(5, father, baby, 5));
		}
		else if(mother != 5 && father == 5)
		{
			moves.add(new move(mother, 4, baby, 5));
			moves.add(new move(mother, 5, baby, 5));
//			moves.add(new move(mother, 5, baby, 5));
//			moves.add(new move(mother, 5, baby, 5));
		}
		else
		{
			moves.add(new move(4, 4, baby, 5));
			moves.add(new move(5, 5, baby, 5));
//			moves.add(new move(5, 5, baby, 5));
//			moves.add(new move(5, 5, baby, 5));
		}
		
		inScenario = true;
		wait = false;
	}
}
