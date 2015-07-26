package com.sleitnick.installapk;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;



/**
 * App<p>
 * Main application class
 * @author Stephen Leitnick
 *
 */
public class App {
	
	private JFrame frame;
	private JFileChooser fileChooser;
	private JTextArea output;
	private JButton installBtn;
	
	private File selectedApkFile = null;
	private boolean installing = false;
	
	private AppSettings settings = AppSettings.load();
	
	// Install the current 'selectedApkFile' using ADB:
	private void installApk() {
		if (installing) return;
		installBtn.setEnabled(false);
		installing = true;
		new Thread(() -> {
			try {
				
				String sep = System.getProperty("line.separator");
				String cmd = "adb install -r " + selectedApkFile.getAbsolutePath();
				ProcessBuilder builder = new ProcessBuilder("adb", "install", "-r", selectedApkFile.getAbsolutePath());
				output.setText(cmd);
				
				// Run installation:
				Process process = builder.start();
				process.waitFor();
				
				// Out:
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append(sep);
				}
				output.setText(output.getText() + sep + sb.toString());
				
				// Err:
				reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				sb = new StringBuilder();
				line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append(sep);
				}
				output.setText(output.getText() + sep + sb.toString());
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			installing = false;
			installBtn.setEnabled(true);
		}).start();
	}
	
	// Create the user interface:
	private void createGui() {
		
		frame = new JFrame("Install APK");
		frame.setSize(500, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		fileChooser = new JFileChooser(System.getProperty("user.home"));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		// Only show .APK files:
		fileChooser.setFileFilter(new FileFilter() {
			private final String DESCRIPTION = "APK (*.apk)";
			@Override
			public boolean accept(File f) {
				return (f.isDirectory() || f.getName().toLowerCase().endsWith(".apk"));
			}
			@Override
			public String getDescription() {
				return DESCRIPTION;
			}
		});
		
		Container contentPane = frame.getContentPane();
		SpringLayout layout = new SpringLayout();
		contentPane.setLayout(layout);
		
		contentPane.setPreferredSize(new Dimension(465, 300));
		
		JLabel tfieldLabel = new JLabel("APK Path:");
		contentPane.add(tfieldLabel);
		
		JTextField tfield = new JTextField(fileChooser.getCurrentDirectory().getAbsolutePath(), 40);
		tfield.setEditable(false);
		contentPane.add(tfield);
		{
			// Load last selected APK from settings if exists:
			if (settings.containsKey("selectedFile")) {
				String selectedFilePath = (String)settings.get("selectedFile");
				File selectedFile = new File(selectedFilePath);
				if (selectedFile.exists()) {
					tfield.setText(selectedFilePath);
					selectedApkFile = selectedFile;
					fileChooser.setSelectedFile(selectedFile);
				}
			}
		}
		
		JButton browseDir = new JButton("Browse...");
		contentPane.add(browseDir);
		
		installBtn = new JButton("Install APK");
		installBtn.setEnabled(selectedApkFile != null);
		contentPane.add(installBtn);
		
		output = new JTextArea("Output");
		output.setEditable(false);
		output.setFont(new Font("Consolas", Font.PLAIN, 12));
		JScrollPane outputScrollPane = new JScrollPane(output);
		contentPane.add(outputScrollPane);
		
		layout.putConstraint(SpringLayout.WEST, tfieldLabel, 5, SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.NORTH, tfieldLabel, 8, SpringLayout.NORTH, contentPane);
		
		layout.putConstraint(SpringLayout.WEST, tfield, 5, SpringLayout.EAST, tfieldLabel);
		layout.putConstraint(SpringLayout.NORTH, tfield, 5, SpringLayout.NORTH, contentPane);
		
		layout.putConstraint(SpringLayout.WEST, browseDir, 5, SpringLayout.EAST, tfield);
		layout.putConstraint(SpringLayout.NORTH, browseDir, 5, SpringLayout.NORTH, contentPane);
		
		layout.putConstraint(SpringLayout.WEST, installBtn, 5, SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.EAST, installBtn, -5, SpringLayout.EAST, contentPane);
		layout.putConstraint(SpringLayout.NORTH, installBtn, 35, SpringLayout.NORTH, contentPane);
		
		layout.putConstraint(SpringLayout.WEST, outputScrollPane, 5, SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.EAST, outputScrollPane, -5, SpringLayout.EAST, contentPane);
		layout.putConstraint(SpringLayout.NORTH, outputScrollPane, 60, SpringLayout.NORTH, contentPane);
		layout.putConstraint(SpringLayout.SOUTH, outputScrollPane, -5, SpringLayout.SOUTH, contentPane);
		
		// Browse... button:
		browseDir.addActionListener((ActionEvent event) -> {
			int returnVal = fileChooser.showOpenDialog(frame);
			if (JFileChooser.APPROVE_OPTION == returnVal) {
				File f = fileChooser.getSelectedFile();
				selectedApkFile = f;
				tfield.setText(f.getAbsolutePath());
				installBtn.setEnabled(true);
				settings.put("selectedFile", f.getAbsolutePath());
			}
		});
		
		// Install APK button:
		installBtn.addActionListener((ActionEvent event) -> {
			installApk();
		});
		
		// Save settings when window is closed:
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				AppSettings.save();
				super.windowClosing(e);
			}
		});
		
		// Center frame on screen:
		{
			Dimension size = frame.getSize();
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation((int)((screen.getWidth() - size.getWidth()) / 2.0), (int)((screen.getHeight() - size.getHeight()) / 2.0));
		}
		
		// Set icon:
		try {
			BufferedImage icon = ImageIO.read(getClass().getResourceAsStream("icon.png"));
			frame.setIconImage(icon);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Show frame:
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		
	}
	
	public App() {
		createGui();
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// Launch:
		new App();
		
	}

}
