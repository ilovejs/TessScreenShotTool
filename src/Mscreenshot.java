/*Copyright [2012] [Tianhao Zhuang]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.*/
   
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Mscreenshot {
	private JFrame frame;
	private TranslucentFrame tframe;
	private JPanel panel;
	private JButton button;
	private FormListener formListener;
	private Robot robot;
	
	public static GraphicsEnvironment graphenv = GraphicsEnvironment.getLocalGraphicsEnvironment();
	public static GraphicsDevice[] screens = graphenv.getScreenDevices();
	public static DisplayMode mode = screens[0].getDisplayMode();

	public Mscreenshot(){
		frame = new JFrame();
		//call other class related to logic of screen shooter
		tframe = new TranslucentFrame(frame);  //pass parent into second form
		
		panel = new JPanel();

		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}

		button = new JButton("Selected Screen");
		formListener = new FormListener();
		button.addActionListener(formListener);

		// fullScreenFrame.setVisible(false);
		tframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tframe.setUndecorated(true);
		tframe.setAlwaysOnTop(true);
		tframe.setSize(2000, 2000); //fake full screen
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		//size of frame
		Dimension size = new Dimension(190, 80);
		frame.setPreferredSize(size);
		frame.setSize(size);
		frame.setLocation(300, 300);
		frame.setBackground(Color.black);

		panel.setLocation(0, 0);
		panel.setSize(size);
		panel.setPreferredSize(size);
		panel.setLayout(new FlowLayout());

		panel.setOpaque(false);
		panel.add(button);
	
		frame.add(panel);
		frame.pack();
		//set location after pack
		frame.setLocation(1200, 90);
				
		frame.show();
	}
	
	//press button on frame will cause this listener
	class FormListener implements ActionListener {
		@SuppressWarnings("deprecation")
		public void actionPerformed(ActionEvent e) {
			if ( ! tframe.isShowing()) {
				//hide tool bar to avoid being captured in picture
				frame.hide();
				//extent robot capture space to full screen size
				tframe.updateBackground();
				//show capture space, in red line
				tframe.show();
				
				//show frame
				frame.show();
				//switch main frame to the back side
				frame.toBack();
			} else{
				tframe.show();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		new Mscreenshot();
	}
}