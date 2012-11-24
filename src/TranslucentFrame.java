/*Copyright [2012] [Tianhao Zhuang]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFrame;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class TranslucentFrame extends JFrame implements ComponentListener, WindowFocusListener, MouseListener, MouseMotionListener {

    private boolean flag_prepare = true;
    private BufferedImage background;
    private Robot robot;
    private Dimension size;
    private Point startPoint;
    private Point lastPoint;
    private int width = 0;
    private int w = 0;
    private int h = 0;
    private int height = 0;
    JFrame mainFrame;
    
    public TranslucentFrame(JFrame m) {
    	mainFrame = m;
    	init();
    }

    private void init() {
        try {
            robot = new Robot();
            size = Toolkit.getDefaultToolkit().getScreenSize();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        startPoint = new Point();
        lastPoint = new Point();
        this.addWindowFocusListener(this);
        this.addComponentListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        //look below
        this.updateBackground();
    }

    public void updateBackground() {
    	//native screen capture API
        background = robot.createScreenCapture(  //fullscreen
        		new Rectangle(0, 0, (int) size.getWidth(),(int) size.getHeight()
        )
        );
    }

    public void refresh() {
        this.repaint();
    }

    public void repaint() {
        Graphics g = this.getGraphics();
        //line color
        g.setColor(Color.red);
        w = lastPoint.x - startPoint.x;
        h = lastPoint.y - startPoint.y;
        width = Math.abs(w);
        height = Math.abs(h);

        // don't need to clear Rect now, just paint the background, it feels good
        // g.clearRect(startPoint.x, startPoint.y, width, height);

        //call function below
        this.paintComponents(g);

        if ( ! this.flag_prepare) {
            if (((w) < 0) && ((h) < 0)) {
                g.drawRect(lastPoint.x, lastPoint.y, width, height);
            } else if (((w) > 0) && ((h) < 0)) {
                g.drawRect(startPoint.x, lastPoint.y, width, height);
            } else if (((w) < 0) && ((h) > 0)) {
                g.drawRect(lastPoint.x, startPoint.y, width, height);
            } else if (((w) > 0) && ((h) > 0)) {
                g.drawRect(startPoint.x, startPoint.y, width, height);
            }
        } else {
            g.drawLine(0, lastPoint.y, size.width, lastPoint.y);
            g.drawLine(lastPoint.x, 0, lastPoint.x, size.height);
        }
    }

    public void paintComponents(Graphics g) {
        Point pos = this.getLocationOnScreen();
        Point offset = new Point(-pos.x, -pos.y);
        g.drawImage(background, offset.x, offset.y, null);
    }

    private static final long serialVersionUID = 3690836343560995785L;

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
        this.refresh();
    }

    public void componentResized(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    	this.refresh();
    }

    public void windowGainedFocus(WindowEvent e) {
    	this.refresh();
    }

    public void windowLostFocus(WindowEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        this.lastPoint.x = e.getX();
        this.lastPoint.y = e.getY();
        repaint();
    }

    @SuppressWarnings("deprecation")
	public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            System.out.println("Get right mouse");
            this.hide();
        } else {
            this.flag_prepare = false;
            this.startPoint.x = e.getX();
            this.startPoint.y = e.getY();
        }
    }

    @SuppressWarnings("deprecation")
	public void mouseReleased(MouseEvent e) {
        this.flag_prepare = true;
        // save to buffer
        if (e.getButton() == MouseEvent.BUTTON1) {          
            BufferedImage bimg = null;
            //第四象限
            if (((w) < 0) && ((h) < 0)) {
                //below is test code:
            	//g.drawRect(lastPoint.x, lastPoint.y, width, height);
                bimg = robot.createScreenCapture(new Rectangle(lastPoint.x+1, lastPoint.y+1, width-1, height-1));
            }//第一象限 
            else if (((w) > 0) && ((h) < 0)) {
                //g.drawRect(startPoint.x, lastPoint.y, width, height);
                bimg = robot.createScreenCapture(new Rectangle(startPoint.x+1, lastPoint.y+1, width-1, height-1));
            }//第3象限
            else if (((w) < 0) && ((h) > 0)) {
                //g.drawRect(lastPoint.x, startPoint.y, width, height);
                bimg = robot.createScreenCapture(new Rectangle(lastPoint.x+1, startPoint.y+1, width-1, height-1));
            }//第2象限 
            else if (((w) > 0) && ((h) > 0)) {
                //g.drawRect(startPoint.x, startPoint.y, width, height);
                bimg = robot.createScreenCapture(new Rectangle(startPoint.x+1, startPoint.y+1, width-1, height-1));
            }           
            
            //start OCR engine
            Tesseract instance = Tesseract.getInstance();
    		try{
    			String result = instance.doOCR(bimg);
    			File tf = new File("OCR-text.txt");
    			FileWriter fs = new FileWriter("OCR-text.txt");
    			BufferedWriter out = new BufferedWriter(fs);
    			if(tf.exists()){
    				tf.delete();
    			}
    			out.write(result);
    			out.close();
    		}catch(TesseractException te){
    			System.err.println(te.getMessage());
    		} catch (IOException se) {
				se.printStackTrace();
			}
            
            //switch to main frame each time it get image
            dispose();
        }
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseDragged(MouseEvent e) {
        this.lastPoint.x = e.getX();
        this.lastPoint.y = e.getY();
        repaint();
    }
    
	//  public static void main(String[] args) {
	//      TranslucentFrame frame = new TranslucentFrame();
	//      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//      frame.setUndecorated(true);
	//      frame.setAlwaysOnTop(true);
	//      frame.setSize(2000, 2000);
	//      frame.show();
    //  }
}