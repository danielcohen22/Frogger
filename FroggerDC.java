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

public class FroggerDC
{

	private static final String TITLE= "Frogger";
	private static final int FRAME_WIDTH = 800, FRAME_HEIGHT = 600;
	private static final int NUM_GRID_COLUMNS = 9, NUM_GRID_ROWS = 12;
	private final JButton[][] buttons = new JButton[NUM_GRID_COLUMNS][NUM_GRID_ROWS];
	private final JFrame frame = new JFrame(TITLE);
	
	private int frame_width = FRAME_WIDTH;
    private int frame_height = FRAME_HEIGHT;
    
    private static final int FROGUP=0,FROGDOWN=1,FROGRIGHT=2,FROGLEFT=3,FROGDEAD=4,FROGSMILE=5,CARPINK=6,CARRED=7,CARBLUE=8,CARGREY=9;
    private static final String[] imgFiles = {"frog1Up.gif", "frog1Down.gif", "frog1Right.gif", "frog1Left.gif", 
    	"skullAndCrossbones.png", "frog3.jpg", "carpink.png", "carred.png", "carblue.png", "cargrey.png"};
    private static final String IMAGE_PATH = "Games/resource/";
    private ImageIcon imgIcon[] = new ImageIcon[imgFiles.length];
    
    private static int grassRow1=NUM_GRID_ROWS-2, grassRow2=NUM_GRID_ROWS-7;
    private static int riverRow1=NUM_GRID_ROWS-8, riverRow2=NUM_GRID_ROWS-9, riverRow3=NUM_GRID_ROWS-10, riverRow4=NUM_GRID_ROWS-11;
    private static int winningRow=0;
    private static int carsRow1=2, carsRow2=5, carsRow3=8, carsRow4=11, allCars=11;
    private static int logsRow1Pair1=13, logsRow1Pair2=15, logsRow2Pair1=17, logsRow2Pair2=19, logsRow3Pair1=21, 
    		logsRow3Pair2=23, logsRow4Pair1=25, logsRow4Pair2=27, allLogs=27;
   
    private FroggerObject[] froggerObjects= new FroggerObject[28]; // 0 is frog, 1-11 are cars, 12-27 are logs
    private FroggerObject frog= froggerObjects[0];

    private Boolean[] frogWinners= {false,false,false,false}; // if you make it to the top left square then frogWinners[0]=true
    
    private Color previousButtonColor = Color.WHITE;
    
    private int speed=1250; //every level ++, increase speed of cars (speed--)
    private int level=1;
    private int score=0; //every row score++- when frog moves up score++
    private int lives=3;
    private boolean lifeLost=false;
    private boolean playingGame=true;

    private RowTimer rowTimers[] = {null, null, null, null};

    private GameTracker gameTracker;
    
    /** 
     * sets up frame, panel, and buttons 
     */  
    public void setUpJComponents() 
    {
    	
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame_width=frame.getWidth();
        frame_height=frame.getHeight();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JPanel panel=new JPanel(new GridLayout(NUM_GRID_ROWS,NUM_GRID_COLUMNS));
        panel.setLayout(null);
        frame.add(panel);

        for (int col=0; col<NUM_GRID_COLUMNS; col++) //loop through all buttons
        {
               for (int row=0; row<NUM_GRID_ROWS; row++) 
               {
                      buttons[col][row]=new JButton();
                      buttons[col][row].setSize(frame.getContentPane().getWidth()/NUM_GRID_COLUMNS,frame.getContentPane().getHeight()/NUM_GRID_ROWS);      
                      buttons[col][row].setBackground(Color.white);
                      if (row==grassRow1 || row==winningRow || row==grassRow2)
                            buttons[col][row].setBackground(Color.green);
                      if (row==riverRow1 || row==riverRow2 || row==riverRow3 || row==riverRow4)
                            buttons[col][row].setBackground(Color.blue);
                      if (row==winningRow && col%2==0)
                            buttons[col][row].setBackground(Color.blue);
                      buttons[col][row].setOpaque(true);  
                      buttons[col][row].setLocation(col*frame.getContentPane().getWidth()/NUM_GRID_COLUMNS,row*frame.getContentPane().getHeight()/NUM_GRID_ROWS);
                      panel.add(buttons[col][row]);
               }
        }
    }   
    
    /**
     * updates JFrame and JButtons if dimensions are changed
     */
    public void updateJFrame() 		
    {
          frame_width=frame.getWidth();
          frame_height=frame.getHeight();

          for (int col=0; col<NUM_GRID_COLUMNS; col++) 
          {
                 for (int row=0; row<NUM_GRID_ROWS; row++) 
                 {
                        buttons[col][row].setSize(frame.getContentPane().getWidth()/NUM_GRID_COLUMNS,frame.getContentPane().getHeight()/NUM_GRID_ROWS);      
                        buttons[col][row].setLocation(col*frame.getContentPane().getWidth()/NUM_GRID_COLUMNS,row*frame.getContentPane().getHeight()/NUM_GRID_ROWS);
                 }
          }
          frog= new FroggerObject(NUM_GRID_COLUMNS/2,grassRow1,Color.GREEN,imgIcon[FROGUP]);
          resizeIcons();
    }
    
    
    /**
     * initializes all froggerObjects, images on board, and timers
     */
    public void initializeBoard() 
    {
    	resizeIcons(); 	//set image icons to correct size before froggerObjects are even initialized (so that initial screen shows images)  

        frog= new FroggerObject(NUM_GRID_COLUMNS/2,grassRow1,Color.GREEN,null);

        for (int n=1; n<froggerObjects.length; n++) // initialize cars and logs
        {
               if (n<=carsRow1)
                      froggerObjects[n]=new FroggerObject(3*n-1,NUM_GRID_ROWS-3,Color.CYAN,imgIcon[CARPINK]); //pink cars
               else if (n<=carsRow2)
                      froggerObjects[n]=new FroggerObject((2*n)%NUM_GRID_COLUMNS,NUM_GRID_ROWS-4,Color.BLUE,imgIcon[CARRED]); //red cars
               else if (n<=carsRow3)
                      froggerObjects[n]=new FroggerObject((5*n)%NUM_GRID_COLUMNS,NUM_GRID_ROWS-5,new Color(100,75,225),imgIcon[CARBLUE]); //blue cars
               else if (n<=carsRow4)
                      froggerObjects[n]=new FroggerObject((7*n)%NUM_GRID_COLUMNS,NUM_GRID_ROWS-6,new Color(200,25,200),imgIcon[CARGREY]); //grey cars
               else if (n<=logsRow1Pair1)
                      froggerObjects[n]=new FroggerObject(n%12, riverRow1, new Color(170,60,25),null); //logs 1,2
               else if (n<=logsRow1Pair2)
                      froggerObjects[n]=new FroggerObject(n%10, riverRow1, new Color(170,60,25),null); //logs 4,5
               else if (n<=logsRow2Pair1)
                      froggerObjects[n]=new FroggerObject(n%14, riverRow2, new Color(200,100,0),null); //logs 7,8
               else if (n<=logsRow2Pair2)
                      froggerObjects[n]=new FroggerObject(n%12, riverRow2, new Color(200,100,0),null); //logs 9,10
               else if (n<=logsRow3Pair1)
                      froggerObjects[n]=new FroggerObject(n%18, riverRow3, new Color(170,60,25),null); //logs 12,13
               else if (n<=logsRow3Pair2)
                      froggerObjects[n]=new FroggerObject(n%16, riverRow3, new Color(170,60,25),null); //logs 14,15
               else if (n<=logsRow4Pair1)
                      froggerObjects[n]=new FroggerObject(n%24, riverRow4, new Color(200,100,0),null); //logs 17,18
               else if (n<=logsRow4Pair2)
                      froggerObjects[n]=new FroggerObject(n%22, riverRow4, new Color(200,100,0),null); //logs 19,20
               if (n<=allCars) 				//if object is a car, set button to image
                      buttons[froggerObjects[n].x][froggerObjects[n].y].setIcon(froggerObjects[n].image);
               else 					//if object is a log, set button to image
                      buttons[froggerObjects[n].x][froggerObjects[n].y].setBackground(froggerObjects[n].color);
        }

        resizeIcons();

        timerSetUp();
    }
    
    /**
     * resizes an image to its proper size
     * @param file - string with the path to the image
     * @param width - image width
     * @param height - image height
     * @return - an image of the proper size
     */
    public static ImageIcon resizeIcon(String file, int width, int height) 		
    {
        ImageIcon imageIcon = new ImageIcon(file);
        Image image = imageIcon.getImage();
        image = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }
    
	/**
	 * resizes all icons 
	 */
	public void resizeIcons() 		
	{
		int buttonWidth=frame.getContentPane().getWidth()/NUM_GRID_COLUMNS;
	    int buttonHeight=frame.getContentPane().getHeight()/NUM_GRID_ROWS;
	       
	    int pictureWidth=buttonWidth-buttonWidth/(NUM_GRID_COLUMNS-2);
	    int pictureHeight=buttonHeight-buttonHeight/(NUM_GRID_ROWS-2);
	      
	    int frogWidth=buttonWidth/2;
	    int frogHeight=buttonHeight/2;

	    for (int i=0;i<imgFiles.length;i++) 
	    {
	    	if (i<FROGDEAD)
	    		imgIcon[i] = resizeIcon(IMAGE_PATH+imgFiles[i], frogWidth, frogHeight);
	        else
	        	imgIcon[i] = resizeIcon(IMAGE_PATH+imgFiles[i], pictureWidth, pictureHeight);
	    }
	    if (frog!=null) //if board is initialized already (frog exists)
	    {
	    	frog.image =  imgIcon[FROGUP];
	               
	    	for (int n=1;n<=allCars;n++) 
	    	{
	    		if (n<=carsRow1)
	    			froggerObjects[n].image = imgIcon[CARPINK];
	    		else if (n<=carsRow2)
	    			froggerObjects[n].image = imgIcon[CARRED];
	    		else if (n<=carsRow3)
	    			froggerObjects[n].image = imgIcon[CARBLUE];
	    		else
	    			froggerObjects[n].image = imgIcon[CARGREY];
	    	}
	    	
	    	for (int n=0; n<frogWinners.length; n++)
	    	{
	    		if (frogWinners[n]==true)
	    			buttons[2*n+1][0].setIcon(imgIcon[FROGSMILE]);    		
	        }
	             
	    	buttons[frog.x][frog.y].setIcon(imgIcon[FROGUP]);
	    }
	  }
	 
    /**
     * sets up timers - initializes them or stops and restarts timers
     */
    public void timerSetUp() 
    {
        for (int i=0;i<rowTimers.length;i++) 
        {
        	if (rowTimers[i]!=null) 
        		rowTimers[i].timer.stop();
            rowTimers[i] = new RowTimer(i);
        }
        if (gameTracker==null)
            gameTracker = new GameTracker();
    }
     
    static final int LIVESTEXT=0, LIVESNUM=1, SCORETEXT=3, SCORENUM=4, LEVELTEXT=6, LEVELNUM=7;
    /**
     * keeps track of lives, score, level on buttons on the bottom row
     */
    public void scoreTracker() 	
    {    	
    	Font font1=new Font("SansSerif",0,frame.getContentPane().getWidth()/(NUM_GRID_COLUMNS*7)); //size= buttonWidth/7
        Font font2=new Font("SansSerif",0,frame.getContentPane().getWidth()/(NUM_GRID_COLUMNS*4));

        JButton livesButton1 = buttons[LIVESTEXT][NUM_GRID_ROWS-1];
        livesButton1.setFont(font1);
        livesButton1.setText("lives=");
         
        JButton livesButton2 = buttons[LIVESNUM][NUM_GRID_ROWS-1];
        livesButton2.setFont(font2);
        livesButton2.setText(""+lives);
         
        JButton scoreButton1 = buttons[SCORETEXT][NUM_GRID_ROWS-1];
        scoreButton1.setFont(font1);
        scoreButton1.setText("score=");
         
        JButton scoreButton2 = buttons[SCORENUM][NUM_GRID_ROWS-1];
        scoreButton2.setFont(font2);
        scoreButton2.setText(""+score);
         
        JButton levelButton1 = buttons[LEVELTEXT][NUM_GRID_ROWS-1];
        levelButton1.setFont(font1);
        levelButton1.setText("level=");
         
        JButton levelButton2 = buttons[LEVELNUM][NUM_GRID_ROWS-1];
        levelButton2.setFont(font2);
        levelButton2.setText(""+level);
    }
      
    /**
     * checks if there is a collision with any car, water, or occupied winning spot (frogSmile)
     * @return - true if crash
     */
    public boolean checkCrash()
    {
        for (int n=1; n<=allCars; n++)
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
            previousButtonColor = buttons[frog.x][frog.y].getBackground();
            buttons[frog.x][frog.y].setBackground(Color.RED);
            buttons[frog.x][frog.y].setIcon(imgIcon[FROGDEAD]);
            return true;
        }
        return false;
    }
     
    /**
     * checks frog's coordinate's compared to imputed froggerObjects parameters to se if they are the same
     * @param x - x coordinate of a froggerObject
     * @param y - y coordinate of a froggerObject
     * @return - true if frog and inputed parameters of a froggerObject's coordinates are the same
     */
    public boolean checkCrashHelper(int x, int y)
    {	
        if (frog.x==x && frog.y==y)
        {        	
            previousButtonColor = buttons[frog.x][frog.y].getBackground();
            buttons[frog.x][frog.y].setBackground(Color.RED);
            buttons[frog.x][frog.y].setIcon(imgIcon[FROGDEAD]);
            return true;
        }
        return false;
    }
     
    /**
     * method is called if there is a crash
     * lose a life, stops row timers, reset crashed button and renew frog image, restart timers
     */
    public void crashLoseLife()
    {    	
        if (lives>0)
        {
            if(lifeLost)
            {
                lives--;
                if (lives==0) //if lives is 0, skip rest of this, go down to the else if lives==0
                	return;
                for (RowTimer rowTimer: rowTimers) //stop all row timers 
                	rowTimer.timer.stop();
                JOptionPane.showMessageDialog(frame, "Life Lost! \nLives left: "+lives+"\nCurrent score: "+score);
                
                buttons[frog.x][frog.y].setBackground(previousButtonColor); //reset crash button's previous color
                buttons[frog.x][frog.y].setIcon(getFroggerObjectIcon(frog.x,frog.y)); //reset crash button's previous image
                frog= new FroggerObject(NUM_GRID_COLUMNS/2,grassRow1,Color.GREEN,imgIcon[FROGUP]); //reset frog
                buttons[frog.x][frog.y].setIcon(imgIcon[FROGUP]);

                for (int n=0; n<frogWinners.length;n++) //check to see if player has reached a winning row, if so, keep image up there
                {
                	if (frogWinners[n]==true)
                		buttons[2*n+1][0].setIcon(imgIcon[FROGSMILE]);
                }
                for (RowTimer rowTimer: rowTimers) 	//start timers
                	rowTimer.timer.start();
                lifeLost=false;              
            }
          }
          else if (lives==0) 	//then game over, exit game
          {
        	  for (RowTimer rowTimer: rowTimers)
        		  rowTimer.timer.stop();
        	  JOptionPane.showMessageDialog(frame, "Game Over! \nLevel: "+level+"\nScore: "+score);
        	  playingGame=false;
        	  System.exit(0);
          }
    }
       
    /**
     * if inputted coordinates (of frog) are the same as another froggerObjet, then get froggerObject's imageicon
     * @param col - x coord of a specific froggerObject
     * @param row - y coord of a specific froggerObject
     * @return - returns a froggerObject's imageicon
     */
    public ImageIcon getFroggerObjectIcon(int col, int row) 	
    {
    	for (int n=1;n<froggerObjects.length;n++)
    	{
    		if (froggerObjects[n].x==col && froggerObjects[n].y==row)
    			return froggerObjects[n].image;
    	}      
        return null;
    }  
    
    /**
     * if player makes it to the winning row  
     */
    public void lastRowLevelUp()
    {
    	for (int n=0; n<frogWinners.length;n++)
    	{
    		if (frog.y==0 && frog.x==2*n+1)
    		{
    			if (frogWinners[n]==true)
    				return;
    			frog.y=grassRow1;
    			frog.x=NUM_GRID_COLUMNS/2; //set frog to middle of board
    			buttons[frog.x][frog.y].setIcon(frog.image);
    			frogWinners[n]=true;
    			buttons[2*n+1][0].setIcon(imgIcon[FROGSMILE]); 			
    		}
    	}
    	if (frogWinners[0] && frogWinners[1] && frogWinners[2] && frogWinners[3]) //if all 4 frog frogSmile imgages are there, increase level and make them blank again
    	{
    		level++;
    		if (level<=5)
    			speed=speed-100; //once gets past lvl 5, dont increase speed            
    		timerSetUp(); 	//restart timers
    		for(int n=0; n<frogWinners.length;n++) //set buttons frogWinner buttons blank and set all frogWinners false
    		{
    			buttons[2*n+1][0].setIcon(null);
    			frogWinners[n]=false;
    		}
    	}
    }
	
	/**
	 * @return - true if frog is on a log
	 */
	public boolean frogOnLog()
	{
		for (int n=allCars+1; n<=allLogs;n++)
		{
			if (frog.x==froggerObjects[n].x && frog.y==froggerObjects[n].y)
				return true; 
		}
		return false;
	}
	
	/**
	 * @param x - xcoord of inputted log
	 * @param y - ycoord of inputted log
	 * @return - true if frog is on a specific log
	 */
	public boolean frogOnLogMover(int x, int y)
	{
		if (frog.x==x && frog.y==y)
			return true;
		return false;
	}
	
	class FroggerObject
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
			x=xCoord%NUM_GRID_COLUMNS; 
			y=yCoord%NUM_GRID_ROWS; 
			color=color1;
			image=image1;
		}
		
		public void setImage(ImageIcon image)
		{
			this.image=image;
		}
		
		/**
		 * method that moves a froggerObject up
		 */
		public void moveUp()
		{
			buttons[this.x][this.y].setIcon(null);
			this.y--;
			if (this.y==-1) //if on the highest/top row, move down to lowest/bottom row
			{
				this.y=grassRow1;
				level++;
				if (level<=5)
					speed=speed-50; //once gets past lvl 5, dont increase speed
				timerSetUp();
				frog.x=NUM_GRID_COLUMNS/2; //set frog to middle of board
				for (int n=0; n<frogWinners.length; n++)
				{
					if (frogWinners[n])
						buttons[2*n+1][0].setIcon(imgIcon[FROGSMILE]);
				}					
			}
			else
				score++;
			buttons[this.x][this.y].setIcon(this.image);
		}

		/**
		 * method that moves a froggerObject down
		 */
		public void moveDown()
		{
			if (this.y==grassRow1) //if on lowest row, cant move down
				return;
			else
			{
				buttons[this.x][this.y].setIcon(null);
				this.y++;
				if (this==frog)
					buttons[this.x][this.y].setIcon(imgIcon[FROGDOWN]);
				else
					buttons[this.x][this.y].setIcon(this.image);
				score--;
			}
		}
		
		/**
		 * method that moves a froggerObject right
		 */
		public void moveRight()
		{
			if (this==frog)
			{
				if (frog.x!=(NUM_GRID_COLUMNS-1)) //frog cannot loop around
				{
						this.x++;
						buttons[this.x][this.y].setIcon(imgIcon[FROGRIGHT]);
						buttons[this.x-1][this.y].setIcon(null);
				}
	    		else
	    			return;
			}
			else
			{
				this.x++;
				if (this.x>=NUM_GRID_COLUMNS) //loops around
					this.x=this.x%NUM_GRID_COLUMNS;
				if (this.y==grassRow2 || this.y==riverRow1 || this.y==riverRow2 || this.y==riverRow3) 
					buttons[this.x][this.y].setBackground(this.color);
				buttons[this.x][this.y].setIcon(this.image);
				
				int clearX=this.x-1; //need to clear previous spot, this.x-1
				if (clearX==-1) //if spot looped around to first column in row, last spot was last column in row
					clearX=NUM_GRID_COLUMNS-1;
				if (this.y==0 || this.y==grassRow2 || this.y==grassRow1)
					buttons[clearX][this.y].setBackground(Color.green); //dont change the grass
				else if (this.y==riverRow4 || this.y==riverRow1 || this.y==riverRow2 || this.y==riverRow3)
					buttons[clearX][this.y].setBackground(Color.blue);
				else 
					buttons[clearX][this.y].setBackground(Color.white);
				buttons[clearX][this.y].setIcon(null);
				
				if (frogOnLog() && frog.x==NUM_GRID_COLUMNS-1)
					buttons[frog.x][frog.y].setIcon(frog.image);				
			}
		}
		
		/**
		 * method that moves a froggerObject left
		 */
		public void moveLeft()
		{
			if (this==frog)
			{
				if (frog.x!=0) //frog cannot loop around
				{
						this.x--;
						buttons[this.x][this.y].setIcon(imgIcon[FROGLEFT]);
						buttons[this.x+1][this.y].setIcon(null);
				}
	    		else
	    			return;
			}
			else
			{
				this.x--;
				if (this.x==-1) //loops around
					this.x=NUM_GRID_COLUMNS-1;
				if (this.y==riverRow4 || this.y==riverRow1 || this.y==riverRow2 || this.y==riverRow3) 
					buttons[this.x][this.y].setBackground(this.color);
				buttons[this.x][this.y].setIcon(this.image);
				
				int clearX=this.x+1; //need to clear previous spot, this.x-1
				if (clearX==NUM_GRID_COLUMNS) //if spot looped around to first column in row, last spot was last column in row
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
		
		/**
		 * if frog is on a log, move both the frog and log right
		 */
		public void frogAndLogMoveRight()
		{
			this.x++;
			if (this.x>=NUM_GRID_COLUMNS) //loops around
				this.x=this.x%NUM_GRID_COLUMNS;
			if (this.y==riverRow4 || this.y==riverRow1 || this.y==riverRow2 || this.y==riverRow3) 
				buttons[this.x][this.y].setBackground(this.color);
			buttons[this.x][this.y].setIcon(this.image);
			
			if (frog.x!=NUM_GRID_COLUMNS-1)
			{
				frog.x++;
				buttons[frog.x][frog.y].setIcon(frog.image);
				buttons[frog.x-1][frog.y].setIcon(null);
			}
			else
				buttons[frog.x][frog.y].setIcon(frog.image);
			
			int clearX=this.x-1; //need to clear previous spot, this.x-1
			if (clearX==-1) //if spot looped around to first column in row, last spot was last column in row
				clearX=NUM_GRID_COLUMNS-1;
			if (this.y==0 || this.y==grassRow2 || this.y==grassRow1)
				buttons[clearX][this.y].setBackground(Color.green); //dont change the grass
			else if (this.y==riverRow4 || this.y==riverRow1 || this.y==riverRow2 || this.y==riverRow3)
				buttons[clearX][this.y].setBackground(Color.blue);
			else 
				buttons[clearX][this.y].setBackground(Color.white);
			buttons[clearX][this.y].setIcon(null);			
		}
		
		/**
		 * if frog is on a log, move both the frog and log left
		 */
		public void frogAndLogMoveLeft()
		{
			this.x--;
			if (this.x==-1) //loops around
				this.x=NUM_GRID_COLUMNS-1;
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
			if (clearX==NUM_GRID_COLUMNS) //if spot looped around to first column in row, last spot was last column in row
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
	
	 /**
	 * @author Daniel
	 * row timers move cars and logs
	 */
	class RowTimer implements ActionListener 
	 {
		 private final int[][] froggerObjIdx = {{1,2,15,14,13,12},
				 								{5,4,3,16,17,18,19},
				 								{8,7,6,23,22,21,20},
				 								{11,10,9,24,25,26,27}}; //array [row][froggerobject numbers]
         private int row;
         public Timer timer;
     
         public RowTimer(int row) 
         {
                this.row = row;
                this.timer = new Timer(speed-150*row, this);
                timer.start();
         }
         @Override
         public void actionPerformed(ActionEvent arg0) //moves cars and logs
         {        	 
        	 if (lives>0)
        	 {
        		 for (int i:froggerObjIdx[row]) 
        		 {
        			 if (i<=allCars) //move cars right
        			 {
        				 froggerObjects[i].moveRight();
        			 } 
        			 else if (row%2==0) //move logs right
        			 {
        				 if (frogOnLogMover(froggerObjects[i].x,froggerObjects[i].y))
        					 froggerObjects[i].frogAndLogMoveRight();
        				 else
        					 froggerObjects[i].moveRight();               
        			 } 
        			 else //move logs left
        			 {
        				 if (frogOnLogMover(froggerObjects[i].x,froggerObjects[i].y))
        					 froggerObjects[i].frogAndLogMoveLeft();
        				 else
        					 froggerObjects[i].moveLeft();                
        			 }
        		 }
        	 }
         }
    }
	 
	/**
	 * @author Daniel
	 * game tracker is a timer that keeps track of the game play
	 */
	class GameTracker implements ActionListener 
	{		
		public Timer timer;
		
		public GameTracker() 
		{
			timer = new Timer(1,this);
			timer.start();
		}  
		public void actionPerformed(ActionEvent event) //keeps track of score and crashes
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
					crashLoseLife();
			}     
		}
	}
	
	class Key implements KeyListener 
	{
		public void keyPressed(KeyEvent e)
		{
			switch (e.getKeyCode()) 
			{
				case KeyEvent.VK_RIGHT: 
					frog.moveRight(); 
					break;
				case KeyEvent.VK_LEFT: 
					frog.moveLeft(); 
					break;
				case KeyEvent.VK_UP: 
					frog.moveUp(); 
					break;
				case KeyEvent.VK_DOWN: 
					frog.moveDown(); 
					break;
			}
		}
        @Override public void keyReleased(KeyEvent arg0) {}
        @Override public void keyTyped(KeyEvent arg0) {}
	}
	
	 public FroggerDC() 
	 {
		 setUpJComponents();
         initializeBoard();
         JOptionPane.showMessageDialog(frame, "Use the keys to direct the frog across the road and river to its home");
         frame.addKeyListener(new Key());
	 }

	 public static void main(String[] args) 
	 {
         new FroggerDC();
	 }
}
