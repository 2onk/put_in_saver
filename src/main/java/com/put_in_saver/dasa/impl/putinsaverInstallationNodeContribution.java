package com.put_in_saver.dasa.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.UnknownHostException;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;

import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;


public class putinsaverInstallationNodeContribution implements InstallationNodeContribution{

	
	private InstallationAPIProvider provider;
	private putinsaverInstallationNodeView view;
	private DataModel model;
	
	private static final String ENABLED_KEY = "enabled";
	private static final String ENABLEDSHUTDOWN_KEY = "disabled";
	private static final Boolean DEFAULT_VALUE = false;

	private String dashboardRunning = "Running";
	private String dashboardRobotmode = "Robotmode";
	private Integer tcpX = 0;
	private Integer tcpY = 0;
	private Integer tcpZ = 0;
	private Integer shutdownCounter= 0;
	private ModbusClient modbusClient = new ModbusClient("127.0.0.1",502);
	
	
	public putinsaverInstallationNodeContribution(InstallationAPIProvider apiProvider,
			putinsaverInstallationNodeView view, DataModel model) {
		
		this.provider = apiProvider;
		this.model = model;
		this.view = view;
		applyDesiredSaverStatus();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void openView(){
		updateUI();
		
	}

	@Override
	public void closeView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateScript(ScriptWriter writer) {
		// TODO Auto-generated method stub
		
	}

	public void onStartClick() {
		model.set(ENABLED_KEY, true);
		applyDesiredSaverStatus();
		// TODO Auto-generated method stub
		
	}

	public void onStopClick() {
		model.set(ENABLED_KEY, false);
		applyDesiredSaverStatus();
		resetShutdownCounter();
		// TODO Auto-generated method stube
		
	}
	
	public void onStartClickShutdown() {
		model.set(ENABLEDSHUTDOWN_KEY, true);
		// TODO Auto-generated method stub
		
	}

	public void onStopClickShutdown() {
		model.set(ENABLEDSHUTDOWN_KEY, false);
		resetShutdownCounter();
		// TODO Auto-generated method stub
		
	}
	
	private Boolean issaverEnabled() {
		return model.get(ENABLED_KEY, false); //saver is disabled by default
	}
	
	private Boolean isShutdownEnabled() {
		return model.get(ENABLEDSHUTDOWN_KEY, false); //saver is disabled by default
	}
	
	public void updateUI() {
		if (model.get(ENABLED_KEY, DEFAULT_VALUE) == true) {
			view.setStartButtonEnabled(false);
			view.setStopButtonEnabled(true);
		} else {
			view.setStartButtonEnabled(true);
			view.setStopButtonEnabled(false);
		}
		
		if (model.get(ENABLEDSHUTDOWN_KEY, DEFAULT_VALUE) == true) {
			view.setStartShutdownButtonEnabled(false);
			view.setStopShutdownButtonEnabled(true);
		} else {
			view.setStartShutdownButtonEnabled(true);
			view.setStopShutdownButtonEnabled(false);
		}
	}
	
	private void applyDesiredSaverStatus() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(issaverEnabled()==true) {
					//check if enabled and get robot status
					try {
						getRobotStatus();//get all needed information, running, mode and joints
						ShutdownCountercheck(); // check if it neccessarry to reset shutdown counter
						awaitTimer(480000);
						if(checkRobotStatus() == true && issaverEnabled() == true){ //check after 8 min whether the robot has been used
							poweroffArm();
							//System.err.println("sollte ausgeführt haben");
						}
						else {
							//System.err.println("hat nicht ausgeführt");
						}
						
						if(checkRobotStatusShutdown() == true && issaverEnabled() == true && isShutdownEnabled() == true){
							shutdownCounter++;
							if(shutdownCounter == 11) {
								shutdownRobot();
							}
						}
						
					} catch(Exception e){
						System.err.println("Something went wrong!");
					}
				}
			}
		}).start();
	}
	
	private int getModbusHoldingRegister(int register) throws UnknownHostException, SocketException, ModbusException, IOException {
		modbusClient.Connect();
		int answer = modbusClient.ReadHoldingRegisters(register, 1)[0];
		modbusClient.Disconnect();
		return answer;
	}

	protected boolean checkRobotStatus() throws UnknownHostException, IOException, ModbusException {
		
		String runningActual = sendDashboardCommand("running");
		String robotmodeActual = sendDashboardCommand("robotmode");
		
		Integer tcpXactual = getModbusHoldingRegister(400)/10;
		Integer tcpYactual = getModbusHoldingRegister(401)/10;
		Integer tcpZactual = getModbusHoldingRegister(402)/10;
		
		//System.out.println(tcpX);
		//System.out.println(tcpY);
		//System.out.println(tcpZ);
		//System.out.println(tcpXactual);
		//System.out.println(tcpYactual);
		//System.out.println(tcpZactual);
		
		
		if(dashboardRunning.equals("Program running: false") == true &&  
				dashboardRobotmode.equals("Robotmode: RUNNING") == true ||
				   dashboardRobotmode.equals("Robotmode: IDLE") == true) { //check if program running and robot powered on or in IDLE
			if(dashboardRunning.equals(runningActual) == true &&  
					dashboardRobotmode.equals(robotmodeActual) == true &&
						tcpX.equals(tcpXactual) == true&&
								tcpY.equals(tcpYactual) == true &&
								tcpZ.equals(tcpZactual) == true) { //check if program running and robot powered on and position hasnt changed since 8 min
			return true;
			}
		}
		return false;
	}
	
	protected boolean checkRobotStatusShutdown() throws UnknownHostException, IOException, ModbusException {
		
		String robotmodeActual = sendDashboardCommand("robotmode");
		
		Integer tcpXactual = getModbusHoldingRegister(400)/10;
		Integer tcpYactual = getModbusHoldingRegister(401)/10;
		Integer tcpZactual = getModbusHoldingRegister(402)/10;
		
		System.out.println(shutdownCounter);
		
		if(dashboardRobotmode.equals("Robotmode: POWER_OFF") == true) { //check if robot arm is powered off
			if(dashboardRobotmode.equals(robotmodeActual) == true &&
						tcpX.equals(tcpXactual) == true&&
								tcpY.equals(tcpYactual) == true &&
								tcpZ.equals(tcpZactual) == true) { //check if robot arm is powered off and position hasnt changed since 8 min
			return true;
			}
		}
		return false;
	}
	

	protected void getRobotStatus() throws InterruptedException, ModbusException {
		try {
			//dashboard status
			dashboardRunning =  sendDashboardCommand("running");
			dashboardRobotmode = sendDashboardCommand("robotmode");
			
			//modbus status
			tcpX = getModbusHoldingRegister(400)/10;
			tcpY = getModbusHoldingRegister(401)/10;
			tcpZ = getModbusHoldingRegister(402)/10;
	
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(dashboardRunning);
		//System.out.println(dashboardRobotmode);
		
	}
	
	public void resetShutdownCounter() {
		shutdownCounter = 0;
	}
	
	public void ShutdownCountercheck() {
		if(dashboardRobotmode.equals("Robotmode: RUNNING") == true || dashboardRobotmode.equals("Robotmode: IDLE") == true) { //check if powered on and reset shutdown counter
			resetShutdownCounter();
		}
	}
	protected void poweroffArm() throws UnknownHostException, IOException, InterruptedException {
		sendDashboardCommand("popup Roboterarm wird durch URCap in PowerOff geschickt");
		Thread.sleep(5000);
		sendDashboardCommand("power off");
	}
	
	protected void shutdownRobot() throws UnknownHostException, IOException, InterruptedException {
		resetShutdownCounter();
		sendDashboardCommand("popup Der Roboter wird in 10s heruntergefahren");
		Thread.sleep(10000);
		sendDashboardCommand("shutdown");
	}
	
	private String sendDashboardCommand(String dashboardMessage) throws UnknownHostException, IOException {
		String ip = "127.0.0.1";
		int port = 29999;
		java.net.Socket socket = new java.net.Socket(ip,port);
		
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			printWriter.print(createDashboardMessage(dashboardMessage));
	 		printWriter.flush();
	 		
	 	String message = readMessage(socket);
 		socket.close();
 		
 		return message;  
	}
	
	private String createDashboardMessage(String dashboardMessage) {
		dashboardMessage =  dashboardMessage + "\n";
		return dashboardMessage;
		
	}
	
	String readMessage(java.net.Socket socket) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String buffer = bufferedReader.readLine();		//get first message connected to universal robots dashboard server...
	    buffer = bufferedReader.readLine();				//get useful information
		String message = new String(buffer);
		return message;
	}
	
	private void awaitTimer(long timeOutMilliSeconds) throws InterruptedException {
		long endTime = System.nanoTime() + timeOutMilliSeconds * 1000L * 1000L;
		while(System.nanoTime() < endTime && (issaverEnabled() != false)) {
			Thread.sleep(100);
		}
	}

}
