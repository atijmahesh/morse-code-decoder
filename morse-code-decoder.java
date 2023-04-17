import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;
import java.io.*;


public class MorseCodeTester_Mahesh {
  public static void main(String[] args) {
    TreeDisplay display = new TreeDisplay();
    TreeNode decodingTree = TreeUtilities.createDecodingTree(display);
            
            
    String input = JOptionPane.showInputDialog(null, "Enter morse code: ");
    JOptionPane.showMessageDialog(null, "Translation: " + TreeUtilities.decodeMorse
                                 (decodingTree, input, display));      
  }
}


class TreeNode {
  private Object value;
  private TreeNode left;
  private TreeNode right;
  
  public TreeNode(Object initValue)
    { value = initValue; left = null; right = null; }

  public TreeNode(Object initValue, TreeNode initLeft, TreeNode initRight)
    { value = initValue; left = initLeft; right = initRight; }
  public Object getValue() { return value; }
  public TreeNode getLeft() { return left; }
  public TreeNode getRight() { return right; }
  public void setValue(Object theNewValue) { value = theNewValue; }
  public void setLeft(TreeNode theNewLeft) { left = theNewLeft; }
  public void setRight(TreeNode theNewRight) { right = theNewRight; }
}

class TreeDisplay extends JComponent {
  //number of pixels between text and edge
  private static final int ARC_PAD = 2;

  //the tree being displayed
  private TreeNode root = null;

  //the node last visited
  private TreeNode visiting = null;

  //the set of all nodes visited so far
  private Set visited = new HashSet();

  //number of milliseconds to pause when visiting a node
  private int delay = 20;

  //creates a frame with a new TreeDisplay component.
  //(constructor returns the TreeDisplay component--not the frame).
  public TreeDisplay() {
    //create surrounding frame
	 JFrame frame = new JFrame("Tree Display");
	 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 //add the TreeDisplay component to the frame
	 frame.getContentPane().add(this);
	 //show frame
	 frame.pack();
	 frame.setVisible(true);
	 java.util.Timer timer = new java.util.Timer();
	 TimerTask task = new TimerTask() {
	   public void run() {
		  TreeDisplay.this.repaint();
	   }
	 };
	 timer.schedule(task, 0, 1000);
  }

  //tells the frame the default size of the tree
  public Dimension getPreferredSize() {
    return new Dimension(800, 600);
  }

  //called whenever the TreeDisplay must be drawn on the screen
  public void paint(Graphics g) {
    Graphics2D g2 = (Graphics2D)g;
	 Dimension d = getSize();
	 //draw white background
	 g2.setPaint(Color.white);
	 g2.fill(new Rectangle2D.Double(0, 0, d.width, d.height));
    int depth = TreeUtilities.maxDepth(root);
    if (depth == 0)
      //no tree to draw
      return;
	 //hack to avoid division by zero, if only one level in tree
    if (depth == 1)
      depth = 2;

	 //compute the size of the text
    FontMetrics font = g2.getFontMetrics();
    int leftPad = font.stringWidth(
	 TreeUtilities.leftmost(root).toString()) / 2;
    int rightPad = font.stringWidth(
	 TreeUtilities.rightmost(root).toString()) / 2;
    int textHeight = font.getHeight();

	 //draw the actual tree
    drawTree(g2, root, leftPad + ARC_PAD,
        		 d.width - rightPad - ARC_PAD,
        		 textHeight / 2 + ARC_PAD,
        		 (d.height - textHeight - 2 * ARC_PAD) / (depth - 1));
    }

	 //draws the tree, starting from the given node, in the region with x values ranging
	 //from minX to maxX, with y value beginning at y, and next level at y + yIncr.
    private void drawTree(Graphics2D g2, TreeNode t, int minX, int maxX, int y, int yIncr) {
	   //skip if empty
		if (t == null)
		  return;

		//compute useful coordinates
		int x = (minX + maxX) / 2;
		int nextY = y + yIncr;

		//draw black lines
		g2.setPaint(Color.black);
		if (t.getLeft() != null) {
			int nextX = (minX + x) / 2;
			g2.draw(new Line2D.Double(x, y, nextX, nextY));
		}
		if (t.getRight() != null) {
			int nextX = (x + maxX) / 2;
			g2.draw(new Line2D.Double(x, y, nextX, nextY));
		}

		//measure text
		FontMetrics font = g2.getFontMetrics();
		String text = t.getValue().toString();
		int textHeight = font.getHeight();
		int textWidth = font.stringWidth(text);

		//draw the box around the node
		Rectangle2D.Double box = new Rectangle2D.Double(
			x - textWidth / 2 - ARC_PAD, y - textHeight / 2 - ARC_PAD,
			textWidth + 2 * ARC_PAD, textHeight + 2 * ARC_PAD);//, ARC_PAD, ARC_PAD);
		Color c;
		//color depends on whether we haven't visited, are visiting, or have visited.
		if (t == visiting)
			c = Color.YELLOW;
		else if (visited.contains(t))
			c = Color.ORANGE;
		else
			c = new Color(187, 224, 227);
		g2.setPaint(c);
		g2.fill(box);
		//draw black border
		g2.setPaint(Color.black);
		g2.draw(box);

		//draw text
		g2.drawString(text, x - textWidth / 2, y + textHeight / 2);

		//draw children
		drawTree(g2, t.getLeft(), minX, x, nextY, yIncr);
		drawTree(g2, t.getRight(), x, maxX, nextY, yIncr);
	}

	//tells the component to switch to displaying the given tree
    public void displayTree(TreeNode root)
    {
		this.root = root;

		//signal that the display needs to be redrawn
		repaint();
	}

	//light up this particular node, indicating we're visiting it.
	public void visit(TreeNode t)
	{
		//if we've already visited it, we assume this is a new traversal,
		//and reset the set of visited nodes.
		if (visited.contains(t))
			visited = new HashSet();

		//update visiting and visited
		visiting = t;
		visited.add(t);

		//signal that the display needs to be redrawn
		repaint();

		//pause, so you can see the traversal
		try
		{
			Thread.sleep(delay);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	//change the length of time to pause when visiting a node
	public void setDelay(int delay)
	{
		this.delay = delay;
	}
}

//A container for useful static methods that operate on TreeNode objects.
class TreeUtilities {
  //the random object used by this class
  private static java.util.Random random = new java.util.Random();

  //used to prompt for command line input
  private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  //precondition:  t is non-empty
  //postcondition: returns the value in the leftmost node of t.
  public static Object leftmost(TreeNode t) {
    TreeNode temp = t;
    while(temp.getLeft() != null) {
      temp = temp.getLeft();
    }
    return temp.getValue();
  }

  //precondition:  t is non-empty
  //postcondition: returns the value in the rightmost node of t.
  public static Object rightmost(TreeNode t) {
    if(t.getRight() == null) return t.getValue();
    return rightmost(t.getRight());
  }

  //postcondition: returns the maximum depth of t, where an empty tree
  //               has depth 0, a tree with one node has depth 1, etc
  public static int maxDepth(TreeNode t) {
    if(t == null) return 0;
    int leftDepth = maxDepth(t.getLeft());
    int rightDepth = maxDepth(t.getRight());
    return Math.max(leftDepth, rightDepth)+1;
  }

  //postcondition: each node in t has been lit up on display
  //               in a pre-order traversal
  public static void preOrder(TreeNode t, TreeDisplay display) {
    if (t == null) return;
    display.visit(t);
    if(t.getLeft() != null) preOrder(t.getLeft(), display);
    if(t.getRight() != null) preOrder(t.getRight(), display);
  }

  //postcondition: each node in t has been lit up on display
  //               in an in-order traversal
  public static void inOrder(TreeNode t, TreeDisplay display) {
    if (t == null) return;
    if(t.getLeft() != null) preOrder(t.getLeft(), display);
    display.visit(t);
    if(t.getRight() != null) preOrder(t.getRight(), display);
  }

  //postcondition: each node in t has been lit up on display
  //               in a post-order traversal
  public static void postOrder(TreeNode t, TreeDisplay display) {
    if (t == null) return;
    if(t.getLeft() != null) preOrder(t.getLeft(), display);
    if(t.getRight() != null) preOrder(t.getRight(), display);
    display.visit(t);
  }

  //useful method for building a randomly shaped
  //tree of a given maximum depth
  public static TreeNode createRandom(int depth) {
    if (random.nextInt((int)Math.pow(2, depth)) == 0)
	   return null;
	 return new TreeNode(random.nextInt(10),
               createRandom(depth - 1), createRandom(depth - 1));
  }

  //returns the number of nodes in t
  public static int countNodes(TreeNode t) {
    if (t == null) return 0; 
    int count = 0;
    if (t.getLeft() != null && t.getRight() != null) count++;
    count += (countNodes(t.getLeft()) + countNodes(t.getRight()));
    return count; 
  }

  //returns the number of leaves in t
  public static int countLeaves(TreeNode t) {
    if (t == null) return 0;
    if (t.getLeft() == null && t.getRight() == null) return 1;
    else return countLeaves(t.getLeft()) + countLeaves(t.getRight());
  }

  //precondition:  all values in t are Integer objects
  //postcondition: returns the sum of all values in t
  public static int sum(TreeNode t) {
    if (t == null) return 0;
    return Integer.parseInt(t.getValue().toString()) + sum(t.getLeft())
    + sum(t.getRight());
  }

  //postcondition:  returns a new tree, which is a complete copy
  //                of t with all new TreeNode objects pointing
  //                to the same values as t (in the same order, shape, etc)
  public static TreeNode copy(TreeNode t) {
    if (t == null) return null;
    TreeNode output = new TreeNode(t.getValue());
    output.setLeft(copy(t.getLeft()));
    output.setRight(copy(t.getRight()));
    return output;
  }

  //postcondition:  returns true if t1 and t2 have the same
  //                shape (but not necessarily the same values);
  //                otherwise, returns false
  public static boolean sameShape(TreeNode t1, TreeNode t2) {
    if (t1 == null && t2 == null) return true;
    if (t1 != null && t2 != null) {
    return sameShape(t1.getLeft(), t2.getLeft()) &&
           sameShape(t1.getRight(), t2.getRight());
    }
    return false;
  }
   
  //postcondition:  returns a tree for decoding Morse code
  public static TreeNode createDecodingTree(TreeDisplay display) {
    TreeNode tree = new TreeNode("");
    display.displayTree(tree);
    insertMorse(tree, "a", ".-", display);
    insertMorse(tree, "b", "-...", display);
    insertMorse(tree, "c", "-.-.", display);
    insertMorse(tree, "d", "-..", display);
    insertMorse(tree, "e", ".", display);
    insertMorse(tree, "f", "..-.", display);
    insertMorse(tree, "g", "--.", display);
    insertMorse(tree, "h", "....", display);
    insertMorse(tree, "i", "..", display);
    insertMorse(tree, "j", ".---", display);
    insertMorse(tree, "k", "-.-", display);
    insertMorse(tree, "l", ".-..", display);
    insertMorse(tree, "m", "--", display);
    insertMorse(tree, "n", "-.", display);
	 insertMorse(tree, "o", "---", display);
	 insertMorse(tree, "p", ".--.", display);
	 insertMorse(tree, "q", "--.-", display);
	 insertMorse(tree, "r", ".-.", display);
	 insertMorse(tree, "s", "...", display);
	 insertMorse(tree, "t", "-", display);
    insertMorse(tree, "u", "..-", display);
	 insertMorse(tree, "v", "...-", display);
	 insertMorse(tree, "w", ".--", display);
	 insertMorse(tree, "x", "-..-", display);
	 insertMorse(tree, "y", "-.--", display);
	 insertMorse(tree, "z", "--..", display);
    return tree;
  } 

  //postcondition:  inserts the given letter into the decodingTree,
  //                in the appropriate position, as determined by
  //                the given Morse code sequence; lights up the display
  //                as it walks down the tree
  private static void insertMorse(TreeNode decodingTree, String letter,
									String code, TreeDisplay display) {
    display.visit(decodingTree);
    if(code.length() == 0) {
      decodingTree.setValue(letter);    
    }
    else if(code.charAt(0) == '.') {
      if(decodingTree.getLeft() == null) decodingTree.setLeft(new TreeNode(""));
      insertMorse(decodingTree.getLeft(), letter, code.substring(1), display);
    }  
    else if(code.charAt(0) == '-') {
      if(decodingTree.getRight() == null) decodingTree.setRight(new TreeNode(""));
      insertMorse(decodingTree.getRight(), letter, code.substring(1), display);
    }   
  }

  //precondition:  ciphertext is Morse code, consisting of dots, dashes, and spaces
  //postcondition: uses the given decodingTree to return the decoded message;
  //               lights up the display as it walks down the tree
  public static String decodeMorse(TreeNode decodingTree, String cipherText, TreeDisplay display) {
    String output = "";
    TreeNode root = decodingTree;
    TreeNode curr = decodingTree;
    for(int i = 0; i < cipherText.length(); i++) {
      display.visit(curr);
      if(cipherText.charAt(i) == '.') {
        if(curr.getLeft() != null) curr = curr.getLeft();        
        if(i == (cipherText.length() - 1)) {
          display.visit(curr);
          output += curr.getValue().toString();
        }
      }
      else if(cipherText.charAt(i) == '-') {
        if(curr.getRight() != null) curr = curr.getRight();        
        if(i == (cipherText.length() - 1)) {
          display.visit(curr);
          output += curr.getValue().toString();
        }
      }
      else if(cipherText.charAt(i) == ' '){
        output += curr.getValue().toString();
        curr = root;
      }
    }
    return output;
  }
}