import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;



public class Frogger3DC
{

	private final static int col=9;
	private final static int row=12;
	
	static JButton[][] buttons=new JButton[col][row];
	// "..\Games\resource\frog.gif"
	final static JFrame frame=new JFrame("Frogger");
	
	private static int frame_width;
	private static int frame_height;
	

	static Image frogUp;
	static ImageIcon frogImageUp;
	static Image frogDown;
	static ImageIcon frogImageDown;
	static Image frogRight;
	static ImageIcon frogImageRight;
	static Image frogLeft;
	static ImageIcon frogImageLeft;
	static Image dead1;
	static ImageIcon dead;
	
	static Image carR1;
	static ImageIcon carR1ii;
	static Image carR2;
	static ImageIcon carR2ii;
	static Image carR3;
	static ImageIcon carR3ii;
	static Image carR4;
	static ImageIcon carR4ii;
	static Image frog2;
	static ImageIcon frog2ii;
	
	private static int grassRow1=row-2;
	private static int grassRow2=row-7;
	private static int riverRow1=row-8;
	private static int riverRow2=row-9;
	private static int riverRow3=row-10;
	private static int riverRow4=row-11;

	private static FroggerObject[] froggerObjects= new FroggerObject[28]; // 0 is frog, 1-11 are cars, 12-27 are logs
	
	private static FroggerObject frog= froggerObjects[0];
	
	private static Boolean[] frogWinners= {false,false,false,false}; // if you make it to the top left square then frogWinners[0]=true
	
	
	private static int speed=1250; //every level ++, increase speed of cars (speed--)
	private static int level=1;
	private static int score=0; //every row score++- when frog moves up score++
	private static int lives=3; 
	private static boolean lifeLost=false;
	private static boolean playingGame=true;
	
	static ActionListener row1Timer = new row1Timer();
	static Timer t1= new Timer(speed, row1Timer);
	static ActionListener row2Timer = new row2Timer();
	static Timer t2= new Timer(speed-150, row2Timer);
	static ActionListener row3Timer = new row3Timer();
	static Timer t3= new Timer(speed-300, row3Timer);
	static ActionListener row4Timer = new row4Timer();
	static Timer t4= new Timer(speed-450, row4Timer);
	
	static ActionListener GameTracker=new GameTracker();
	static Timer gameTracker= new Timer(1,GameTracker);
	
	public static void setUpJComponents()
	{
		frame.setSize(800,600);
		
		frame_width=frame.getWidth();
		frame_height=frame.getHeight();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		JPanel panel=new JPanel(new GridLayout(row,col)); 
		panel.setLayout(null);
		frame.add(panel);
		for (int c=0; c<col; c++)
		{
			for (int r=0; r<row; r++)
			{
				buttons[c][r]=new JButton();
				buttons[c][r].setSize(frame.getContentPane().getWidth()/col,frame.getContentPane().getHeight()/row);	
				buttons[c][r].setBackground(Color.white);
				if (r==grassRow1 || r==0 || r==grassRow2)
					buttons[c][r].setBackground(Color.green);
				if (r==riverRow1 || r==riverRow2 || r==riverRow3 || r==riverRow4)
					buttons[c][r].setBackground(Color.blue);
				if (r==0 && c%2==0)
					buttons[c][r].setBackground(Color.blue);
				buttons[c][r].setOpaque(true);   
				//buttons[c][r].setBorderPainted(false); 
				buttons[c][r].setLocation(c*frame.getContentPane().getWidth()/col,r*frame.getContentPane().getHeight()/row);
				panel.add(buttons[c][r]);
			}
		}
	}
	public static void updateJFrame()
	{
		frame_width=frame.getWidth();
		frame_height=frame.getHeight();
		for (int c=0; c<col; c++)
		{
			for (int r=0; r<row; r++)
			{
				buttons[c][r].setSize(frame.getContentPane().getWidth()/col,frame.getContentPane().getHeight()/row);	
				buttons[c][r].setLocation(c*frame.getContentPane().getWidth()/col,r*frame.getContentPane().getHeight()/row);
			}
		}
		resizeIcon();
		for (int c=0; c<col; c++)
		{
			for (int r=0; r<row; r++) 
			{
				buttons[c][r].setIcon(null); //clear the skull and cars
			}
		}
		if (frog.y==row-3 || frog.y==row-4 || frog.y==row-5 || frog.y==row-6) //clear the logs and dead square
			buttons[frog.x][frog.y].setBackground(Color.white);
		if (frog.y==row-12 || frog.y==riverRow1 || frog.y==riverRow2 || frog.y==riverRow3 || frog.y==riverRow4)
			buttons[frog.x][frog.y].setBackground(Color.blue);
		boardSetUp();
		for (int n=0; n<frogWinners.length;n++)
		{
			if (frogWinners[n])
				buttons[2*n+1][0].setIcon(frog2ii);
		}
		
	}
	public static void initializeBoard()
	{
		
		frog= new FroggerObject(col/2,grassRow1,Color.GREEN,frogImageUp);
		
		for (int n=1; n<froggerObjects.length; n++) //cars and logs
		{
			if (n==1 || n==2)
				froggerObjects[n]=new FroggerObject(3*n-1,row-3,Color.CYAN,carR1ii); //pink cars
			if (n>=3 && n<=5)
				froggerObjects[n]=new FroggerObject((2*n)%col,row-4,Color.BLUE,carR2ii); //red cars
			if (n>=6 && n<=8)
				froggerObjects[n]=new FroggerObject((5*n)%col,row-5,new Color(100,75,225),carR3ii); //blue cars
			if (n>=9 && n<=11)
				froggerObjects[n]=new FroggerObject((7*n)%col,row-6,new Color(200,25,200),carR4ii); //grey cars
			if(n==12 || n==13)
				froggerObjects[n]=new FroggerObject(n%12, riverRow1, new Color(170,60,25),null); //logs 1,2 
			if (n==14 || n==15) 
				froggerObjects[n]=new FroggerObject(n%10, riverRow1, new Color(170,60,25),null); //logs 4,5 
			if (n==16 || n==17)
				froggerObjects[n]=new FroggerObject(n%14, riverRow2, new Color(200,100,0),null); //logs 7,8 
			if (n==18 || n==19)
				froggerObjects[n]=new FroggerObject(n%12, riverRow2, new Color(200,100,0),null); //logs 9,10 
			if (n==20 || n==21) 
				froggerObjects[n]=new FroggerObject(n%18, riverRow3, new Color(170,60,25),null); //logs 12,13 
			if (n==22 || n==23) 
				froggerObjects[n]=new FroggerObject(n%16, riverRow3, new Color(170,60,25),null); //logs 14,15 
			if (n==24 || n==25)
				froggerObjects[n]=new FroggerObject(n%24, riverRow4, new Color(200,100,0),null); //logs 17,18
			if (n==26 || n==27)
				froggerObjects[n]=new FroggerObject(n%22, riverRow4, new Color(200,100,0),null); //logs 19,20 
			if (n<=11) //if object is a car, set button to image
				buttons[froggerObjects[n].x][froggerObjects[n].y].setIcon(froggerObjects[n].image); 
			else //if object is a log, set button to image
				buttons[froggerObjects[n].x][froggerObjects[n].y].setBackground(froggerObjects[n].color);
		}
		
		buttons[frog.x][frog.y].setIcon(frogImageUp);
		
		
	}
	public static void boardSetUp()
	{
		frog= new FroggerObject(col/2,grassRow1,Color.GREEN,frogImageUp);
		
		for (int n=1; n<froggerObjects.length; n++) //cars and logs
		{
			int prevX= froggerObjects[n].x;
			if (n==1 || n==2)
				froggerObjects[n]=new FroggerObject(prevX,row-3,Color.CYAN,carR1ii); //pink cars
			if (n>=3 && n<=5)
				froggerObjects[n]=new FroggerObject(prevX,row-4,Color.BLUE,carR2ii); //red cars
			if (n>=6 && n<=8)
				froggerObjects[n]=new FroggerObject(prevX,row-5,new Color(100,75,225),carR3ii); //blue cars
			if (n>=9 && n<=11)
				froggerObjects[n]=new FroggerObject(prevX,row-6,new Color(200,25,200),carR4ii); //grey cars
			if(n==12 || n==13)
				froggerObjects[n]=new FroggerObject(prevX, riverRow1, new Color(170,60,25),null); //logs 1,2 n%col
			if (n==14 || n==15) 
				froggerObjects[n]=new FroggerObject(prevX, riverRow1, new Color(170,60,25),null); //logs 4,5 (n+2)%col
			if (n==16 || n==17)
				froggerObjects[n]=new FroggerObject(prevX, riverRow2, new Color(200,100,0),null); //logs 7,8 (n+1)%col
			if (n==18 || n==19)
				froggerObjects[n]=new FroggerObject(prevX, riverRow2, new Color(200,100,0),null); //logs 9,10 (n+3)%col
			if (n==20 || n==21) 
				froggerObjects[n]=new FroggerObject(prevX, riverRow3, new Color(170,60,25),null); //logs 12,13 (n)%col
			if (n==22 || n==23) 
				froggerObjects[n]=new FroggerObject(prevX, riverRow3, new Color(170,60,25),null); //logs 14,15 (n+2)%col
			if (n==24 || n==25)
				froggerObjects[n]=new FroggerObject(prevX, riverRow4, new Color(200,100,0),null); //logs 17,18 (n+4)%col
			if (n==26 || n==27)
				froggerObjects[n]=new FroggerObject(prevX, riverRow4, new Color(200,100,0),null); //logs 19,20 (n+6)%col
			if (n<=11) //if object is a car
				buttons[froggerObjects[n].x][froggerObjects[n].y].setIcon(froggerObjects[n].image); 
			else //if object is a log
				buttons[froggerObjects[n].x][froggerObjects[n].y].setBackground(froggerObjects[n].color);
		}
		
		buttons[frog.x][frog.y].setIcon(frogImageUp);
		
		for (int n=0; n<frogWinners.length; n++)
		{
			if (frogWinners[n]==true)
				buttons[2*n+1][0].setIcon(frog2ii);
		}
		
		
	}
	
	public static void timerSetUp()
	{
		t1.stop();
		t2.stop();
		t3.stop();
		t4.stop();
		t1= new Timer(speed, row1Timer);
		t2= new Timer(speed-150, row2Timer);
		t3= new Timer(speed-300, row3Timer);
		t4= new Timer(speed-450, row4Timer);
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		
	}
	
	public static void scoreTracker()
	{
		Font font1=new Font("SansSerif",0,frame.getContentPane().getWidth()/(col*7)); //size= buttonWidth/7
		Font font2=new Font("SansSerif",0,frame.getContentPane().getWidth()/(col*4));
		
		JButton livesButton1 = buttons[0][row-1];
		livesButton1.setFont(font1);
		livesButton1.setText("lives=");
		
		JButton livesButton2 = buttons[1][row-1];
		livesButton2.setFont(font2);
		livesButton2.setText(""+lives);
		
		JButton scoreButton1 = buttons[3][row-1];
		scoreButton1.setFont(font1);
		scoreButton1.setText("score=");
		
		JButton scoreButton2 = buttons[4][row-1];
		scoreButton2.setFont(font2);
		scoreButton2.setText(""+score);
		
		JButton levelButton1 = buttons[6][row-1];
		levelButton1.setFont(font1);
		levelButton1.setText("level=");
		
		JButton levelButton2 = buttons[7][row-1];
		levelButton2.setFont(font2);
		levelButton2.setText(""+level);
	}
	
	public static void resizeIcon()
	{
		int buttonWidth=frame.getContentPane().getWidth()/col;
		int buttonHeight=frame.getContentPane().getHeight()/row;
		
		int pictureWidth=buttonWidth-buttonWidth/(col-2);
		int pictureHeight=buttonHeight-buttonHeight/(row-2);
		
		int frogWidth=buttonWidth/2;
		int frogHeight=buttonHeight/2;
		
		frogUp=new ImageIcon(".\\Games\\resource\\frog1Up.gif").getImage();
		frogUp=frogUp.getScaledInstance(frogWidth, frogHeight,  java.awt.Image.SCALE_SMOOTH);
		frogImageUp= new ImageIcon(frogUp);
		
		frogDown=new ImageIcon(".\\Games\\resource\\frog1Down.gif").getImage();
		frogDown=frogDown.getScaledInstance(frogWidth, frogHeight,  java.awt.Image.SCALE_SMOOTH);
		frogImageDown= new ImageIcon(frogDown);
		
		frogRight=new ImageIcon(".\\Games\\resource\\frog1Right.gif").getImage();
		frogRight=frogRight.getScaledInstance(frogWidth, frogHeight,  java.awt.Image.SCALE_SMOOTH);
		frogImageRight= new ImageIcon(frogRight);
		
		frogLeft=new ImageIcon(".\\Games\\resource\\frog1Left.gif").getImage();
		frogLeft=frogLeft.getScaledInstance(frogWidth, frogHeight,  java.awt.Image.SCALE_SMOOTH);
		frogImageLeft= new ImageIcon(frogLeft);
		
		dead1=new ImageIcon(".\\Games\\resource\\skullAndCrossbones.png").getImage();
		dead1=dead1.getScaledInstance(pictureWidth, pictureHeight,  java.awt.Image.SCALE_SMOOTH);
		dead= new ImageIcon(dead1);
		
		carR1=new ImageIcon(".\\Games\\resource\\carpink.png").getImage();
        carR1 = carR1.getScaledInstance(pictureWidth, pictureHeight,  java.awt.Image.SCALE_SMOOTH);
        carR1ii = new ImageIcon(carR1);
        
        carR2=new ImageIcon(".\\Games\\resource\\carred.png").getImage();
        carR2 = carR2.getScaledInstance(pictureWidth, pictureHeight,  java.awt.Image.SCALE_SMOOTH);
        carR2ii = new ImageIcon(carR2);
        
        carR3=new ImageIcon(".\\Games\\resource\\carblue.png").getImage();
        carR3 = carR3.getScaledInstance(pictureWidth, pictureHeight,  java.awt.Image.SCALE_SMOOTH);
        carR3ii = new ImageIcon(carR3);
        
        carR4=new ImageIcon(".\\Games\\resource\\cargrey.png").getImage();
        carR4 = carR4.getScaledInstance(pictureWidth, pictureHeight,  java.awt.Image.SCALE_SMOOTH);
        carR4ii = new ImageIcon(carR4);
        
        frog2=new ImageIcon(".\\Games\\resource\\frog3.jpg").getImage();
        frog2 = frog2.getScaledInstance(buttonWidth, buttonHeight,  java.awt.Image.SCALE_SMOOTH);
        frog2ii = new ImageIcon(frog2);
      
	}
	
	public static boolean checkCrash()
	{
		for (int n=1; n<=11; n++)
		{
			if (checkCrashHelper(froggerObjects[n].x,froggerObjects[n].y))
				return true;
		}
		for (int n=0; n<frogWinners.length; n++) //check to see if winning spots are already occupied
		{
			if (checkCrashHelper(2*n+1,0))
				return true;
		}
		
		if (buttons[frog.x][frog.y].getBackground()==Color.blue) //lands in water
		{
			buttons[frog.x][frog.y].setBackground(Color.RED);
			buttons[frog.x][frog.y].setIcon(dead);
			return true;
		}
		return false;
	}
	
	public static boolean checkCrashHelper(int x, int y)
	{
		if (frog.x==x && frog.y==y)
		{
			buttons[frog.x][frog.y].setBackground(Color.RED);
			buttons[frog.x][frog.y].setIcon(dead);
			return true;
		}
		return false;
	}
	
	public static void crashLoseLife()
	{
		if (lives>0)
		{
			if(lifeLost)
			{
				lives--;
				if (lives==0) //if lives is 0, skip rest of this, go down to the else if lives==0
					return;
				t1.stop();
				t2.stop();
				t3.stop();
				t4.stop();
				JOptionPane.showMessageDialog(frame, "Life Lost! \nLives left: "+lives+"\nCurrent score: "+score);
				
				for (int c=0; c<col; c++)
				{
					for (int r=0; r<row; r++) 
					{
						buttons[c][r].setIcon(null); //clear the skull and cars
					}
				}
				if (frog.y==row-3 || frog.y==row-4 || frog.y==row-5 || frog.y==row-6) //clear the logs and dead square
					buttons[frog.x][frog.y].setBackground(Color.white);
				if (frog.y==row-12 || frog.y==riverRow1 || frog.y==riverRow2 || frog.y==riverRow3 || frog.y==riverRow4)
					buttons[frog.x][frog.y].setBackground(Color.blue);
				boardSetUp();
				for (int n=0; n<frogWinners.length;n++)
				{
					if (frogWinners[n]==true)
						buttons[2*n+1][0].setIcon(frog2ii);
				}
				
				t1.start();
				t2.start();
				t3.start();
				t4.start();
				lifeLost=false;
				
			}
		}
		else if (lives==0)
		{
			t1.stop();
			t2.stop();
			t3.stop();
			t4.stop();
			JOptionPane.showMessageDialog(frame, "Game Over! \nLevel: "+level+"\nScore: "+score);
			playingGame=false;
			System.exit(0);
			
		}
	}
	
	public static void lastRowLevelUp()
	{
		for (int n=0; n<frogWinners.length;n++)
		{
			if (frog.y==0 && frog.x==2*n+1)
			{
				if (frogWinners[n]==true)
					return;
				frog.y=grassRow1;
				frog.x=col/2; //set frog to middle of board
				buttons[frog.x][frog.y].setIcon(frog.image);
				frogWinners[n]=true;
				buttons[2*n+1][0].setIcon(frog2ii);
				
			}
		}
		
		
		if (frogWinners[0] && frogWinners[1] && frogWinners[2] && frogWinners[3]) //if all 4 frog images are there, increase level and make them blank again
		{
			level++;
			if (level<=5)
				speed=speed-100; //once gets past lvl 10, dont increase speed
			timerSetUp();
			for(int n=0; n<frogWinners.length;n++) //set buttons images blank and set all frogWinners false
			{
				buttons[2*n+1][0].setIcon(null);
				frogWinners[n]=false;
			}
		}
			
			
	}
	
	public static boolean frogOnLog()
	{
		for (int n=12; n<=27;n++)
		{
			if (frog.x==froggerObjects[n].x && frog.y==froggerObjects[n].y)
				return true; 
		}
		return false;
	}
	
	public static boolean frogOnLogMover(int x, int y)
	{
		if (frog.x==x && frog.y==y)
			return true;
		return false;
	}
	
	static class FroggerObject
	{
		int x;
		int y;
		Color color;
		ImageIcon image;
		
		public FroggerObject()
		{
			x=0;
			y=0;
			color=Color.WHITE;
			image=null;
		}
		public FroggerObject(int xCoord, int yCoord, Color color1, ImageIcon image1)
		{
			x=xCoord%col; 
			y=yCoord%row; 
			color=color1;
			image=image1;
		}
		
		public void moveUp()
		{

			buttons[this.x][this.y].setIcon(null);
			this.y--;
			if (this.y==-1) //if on the highest/top row, move down to lowest/bottom row
			{
				this.y=grassRow1;
				level++;
				if (level<=10)
					speed=speed-50; //once gets past lvl 10, dont increase speed
				timerSetUp();
				frog.x=col/2; //set frog to middle of board
				for (int n=0; n<frogWinners.length; n++)
				{
					if (frogWinners[n])
						buttons[2*n+1][0].setIcon(frog2ii);
				}
					
			}
			else
				score++;
			buttons[this.x][this.y].setIcon(this.image);
		}

		public void moveDown()
		{
			if (this.y==grassRow1) //if on lowest row, cant move down
				return;
			else
			{
				buttons[this.x][this.y].setIcon(null);
				this.y++;
				if (this==frog)
					buttons[this.x][this.y].setIcon(frogImageDown);
				else
					buttons[this.x][this.y].setIcon(this.image);
				score--;
			}
		}
		
		public void moveRight()
		{
			if (this==frog)
			{
				if (frog.x!=(col-1)) //frog cannot loop around
				{
						this.x++;
						buttons[this.x][this.y].setIcon(frogImageRight);
						buttons[this.x-1][this.y].setIcon(null);
				}
	    		else
	    			return;
			}
			else
			{
				this.x++;
				if (this.x>=col) //loops around
					this.x=this.x%col;
				if (this.y==grassRow2 || this.y==riverRow1 || this.y==riverRow2 || this.y==riverRow3) 
					buttons[this.x][this.y].setBackground(this.color);
				buttons[this.x][this.y].setIcon(this.image);
				
				int clearX=this.x-1; //need to clear previous spot, this.x-1
				if (clearX==-1) //if spot looped around to first column in row, last spot was last column in row
					clearX=col-1;
				if (this.y==0 || this.y==grassRow2 || this.y==grassRow1)
					buttons[clearX][this.y].setBackground(Color.green); //dont change the grass
				else if (this.y==riverRow4 || this.y==riverRow1 || this.y==riverRow2 || this.y==riverRow3)
					buttons[clearX][this.y].setBackground(Color.blue);
				else 
					buttons[clearX][this.y].setBackground(Color.white);
				buttons[clearX][this.y].setIcon(null);
				
				if (frogOnLog() && frog.x==col-1)
					buttons[frog.x][frog.y].setIcon(frog.image);
				
			}
		}
		
		public void moveLeft()
		{
			if (this==frog)
			{
				if (frog.x!=0) //frog cannot loop around
				{
						this.x--;
						buttons[this.x][this.y].setIcon(frogImageLeft);
						buttons[this.x+1][this.y].setIcon(null);
				}
	    		else
	    			return;
			}
			else
			{
				this.x--;
				if (this.x==-1) //loops around
					this.x=col-1;
				if (this.y==riverRow4 || this.y==riverRow1 || this.y==riverRow2 || this.y==riverRow3) 
					buttons[this.x][this.y].setBackground(this.color);
				buttons[this.x][this.y].setIcon(this.image);
				
				int clearX=this.x+1; //need to clear previous spot, this.x-1
				if (clearX==col) //if spot looped around to first column in row, last spot was last column in row
					clearX=0;
				if (this.y==0 || this.y==grassRow2 || this.y==grassRow1)
					buttons[clearX][this.y].setBackground(Color.green); //dont change the grass
				else if (this.y==riverRow4 || this.y==riverRow1 || this.y==riverRow2 || this.y==riverRow3)
					buttons[clearX][this.y].setBackground(Color.blue);
				else 
					buttons[clearX][this.y].setBackground(Color.white);
				buttons[clearX][this.y].setIcon(null);
				
				if (frogOnLog() && frog.x==0)
					buttons[frog.x][frog.y].setIcon(frog.image);
			}

		}
		
		public void frogAndLogMoveRight()
		{
			this.x++;
			if (this.x>=col) //loops around
				this.x=this.x%col;
			if (this.y==riverRow4 || this.y==riverRow1 || this.y==riverRow2 || this.y==riverRow3) 
				buttons[this.x][this.y].setBackground(this.color);
			buttons[this.x][this.y].setIcon(this.image);
			
			if (frog.x!=col-1)
			{
				frog.x++;
				buttons[frog.x][frog.y].setIcon(frog.image);
				buttons[frog.x-1][frog.y].setIcon(null);
			}
			else
				buttons[frog.x][frog.y].setIcon(frog.image);
			
			int clearX=this.x-1; //need to clear previous spot, this.x-1
			if (clearX==-1) //if spot looped around to first column in row, last spot was last column in row
				clearX=col-1;
			if (this.y==0 || this.y==grassRow2 || this.y==grassRow1)
				buttons[clearX][this.y].setBackground(Color.green); //dont change the grass
			else if (this.y==riverRow4 || this.y==riverRow1 || this.y==riverRow2 || this.y==riverRow3)
				buttons[clearX][this.y].setBackground(Color.blue);
			else 
				buttons[clearX][this.y].setBackground(Color.white);
			buttons[clearX][this.y].setIcon(null);
			
		}
		
		public void frogAndLogMoveLeft()
		{
			this.x--;
			if (this.x==-1) //loops around
				this.x=col-1;
			if (this.y==riverRow4 || this.y==riverRow1 || this.y==riverRow2 ||this.y==riverRow3) 
				buttons[this.x][this.y].setBackground(this.color);
			buttons[this.x][this.y].setIcon(this.image);
			
			
			if (frog.x!=0)
			{
				frog.x--;
				buttons[frog.x][frog.y].setIcon(frog.image);
				buttons[frog.x+1][frog.y].setIcon(null);
			}
			
			int clearX=this.x+1; //need to clear previous spot, this.x-1
			if (clearX==col) //if spot looped around to first column in row, last spot was last column in row
				clearX=0;
			if (this.y==0 || this.y==grassRow2 || this.y==grassRow1)
				buttons[clearX][this.y].setBackground(Color.green); //dont change the grass
			else if (this.y==riverRow4 || this.y==riverRow1 || this.y==riverRow2 || this.y==riverRow3)
				buttons[clearX][this.y].setBackground(Color.blue);
			else 
				buttons[clearX][this.y].setBackground(Color.white);
			buttons[clearX][this.y].setIcon(null);
		}
	
			
	}
	
	
	static class row1Timer implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{ 
			if (lives>0)
			{
				froggerObjects[1].moveRight();
				froggerObjects[2].moveRight();
				
				for (int n=15; n>=12;n--) //since moving right, have to start moving right with farthest log to the right so the images don't overlap when moving over spaces
				{
					if (frogOnLogMover(froggerObjects[n].x,froggerObjects[n].y))
						froggerObjects[n].frogAndLogMoveRight();
					else
						froggerObjects[n].moveRight();			
				}
				
			}
		
		}
	}
	static class row2Timer implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{ 
			if (lives>0)//(!checkCrash())
			{
				for (int n=5; n>=3; n--) //move cars right
					froggerObjects[n].moveRight(); 
			
				for (int n=16; n<=19; n++)
				{
					if (frogOnLogMover(froggerObjects[n].x,froggerObjects[n].y))
						froggerObjects[n].frogAndLogMoveLeft();
					else
						froggerObjects[n].moveLeft();		
				}
				
			}
			
		}
	}
	static class row3Timer implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{ 	
			if (lives>0)//(!checkCrash())
			{
				for (int n=8; n>=6; n--) //move cars right
					froggerObjects[n].moveRight(); 
				for (int n=23; n>=20; n--)
				{
					if (frogOnLogMover(froggerObjects[n].x,froggerObjects[n].y))
						froggerObjects[n].frogAndLogMoveRight();
					else
						froggerObjects[n].moveRight();
				}
				
			}
			
		}
	}
	static class row4Timer implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{ 
			if (lives>0)//(!checkCrash())
			{	
				for (int n=11; n>=9; n--) //move cars right
					froggerObjects[n].moveRight(); 
				for (int n=24; n<=27; n++)
				{
					if (frogOnLogMover(froggerObjects[n].x,froggerObjects[n].y))
						froggerObjects[n].frogAndLogMoveLeft();
					else
						froggerObjects[n].moveLeft();
				}
				
			}
			
		}
	}
	
	static class GameTracker implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if (playingGame)
			{
				if (lives>0)
				{
					scoreTracker();
					lastRowLevelUp();
					if (frame_width!=frame.getWidth() || frame_height!=frame.getHeight()) //if frame changes height or width
						updateJFrame();
					if (checkCrash())
					{
						lifeLost=true;
						crashLoseLife();
					}
				}
				else if (lives==0)
				{
					crashLoseLife();
				}
				
			}	
		}
	}
	
	public static void main(String[] args)
	{
		
		setUpJComponents();
		resizeIcon();
		
		initializeBoard();
		JOptionPane.showMessageDialog(frame, "Use the arrow keys to move the frog across the road and river to the grass");
		
		class Key implements KeyListener
		{
			public void keyPressed(KeyEvent e) 
		    {
		    	if (e.getKeyCode() == KeyEvent.VK_RIGHT)
		    		frog.moveRight();
		    	if (e.getKeyCode() == KeyEvent.VK_LEFT)
		    		frog.moveLeft();
				if (e.getKeyCode() == KeyEvent.VK_UP)
					frog.moveUp();
				if (e.getKeyCode() == KeyEvent.VK_DOWN)
					frog.moveDown();
		    }
		    
		    public void keyReleased(KeyEvent e) {}
		    public void keyTyped(KeyEvent e) {}
		}
		
		
		
		frame.addKeyListener(new Key());
		timerSetUp();
		gameTracker.start();
		
	}
	
}
