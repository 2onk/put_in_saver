package com.put_in_saver.dasa.impl;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeView;

public class putinsaverInstallationNodeView implements SwingInstallationNodeView<putinsaverInstallationNodeContribution>{
	
	private JButton startButton;
	private JButton stopButton;
	private JButton startShutdownButton;
	private JButton stopShutdownButton;

	public putinsaverInstallationNodeView(ViewAPIProvider apiProvider) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void buildUI(JPanel panel, putinsaverInstallationNodeContribution contribution) {
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		panel.add(createInfo("Start to save energy after 8min"));
		panel.add(createInfo(" "));
		panel.add(createStartStopButtons(contribution));
		panel.add(createInfo(" "));
		panel.add(createInfo(" "));
		panel.add(createInfo("Shutdown Robot while arm is being 1,5h in power off"));
		panel.add(createInfo(" "));
		panel.add(createStartStopButtonsShutdown(contribution));
		// TODO Auto-generated method stub
		
	}
	
	private Box createInfo(String Text) {
		Box infoBox = Box.createHorizontalBox();
		infoBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		infoBox.add(new JLabel(Text));
		return infoBox;
	}
	
	private Box createStartStopButtons(final putinsaverInstallationNodeContribution contribution) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);

		startButton = new JButton("Start Saver");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contribution.onStartClick();
				contribution.updateUI();
			}
		});
		box.add(startButton);


		stopButton = new JButton("Stop Saver");
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contribution.onStopClick();
				contribution.updateUI();
			}
		});
		box.add(stopButton);

		return box;
	}
	
	private Box createStartStopButtonsShutdown(final putinsaverInstallationNodeContribution contribution) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);

		startShutdownButton = new JButton("Shutdown ON");
		startShutdownButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contribution.onStartClickShutdown();
				contribution.updateUI();
			}
		});
		box.add(startShutdownButton);


		stopShutdownButton = new JButton("Shutdown OFF");
		stopShutdownButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contribution.onStopClickShutdown();
				contribution.updateUI();
			}
		});
		box.add(stopShutdownButton);

		return box;
	}
	
	public void setStartButtonEnabled(boolean enabled) {
		startButton.setEnabled(enabled);
	}

	public void setStopButtonEnabled(boolean enabled) {
		stopButton.setEnabled(enabled);
	}
	
	public void setStartShutdownButtonEnabled(boolean enabled) {
		startShutdownButton.setEnabled(enabled);
	}

	public void setStopShutdownButtonEnabled(boolean enabled) {
		stopShutdownButton.setEnabled(enabled);
	}
	

}
