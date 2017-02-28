import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;

public class CardCanvas extends JPanel implements MouseListener, MouseMotionListener {
	private double cardWidth;
	private double cardHeight;
	private int rows;
	private ArrayList<Integer> cardsPerRow;
	private static final int padding = 5; //20px padding
	private static final int numCardsInSuit = 13;
	private static final int numSuits = 4;
	private int cardCenterY; //holds the y value for all cards centered on the screen
	private ArrayList<CardView> cards = new ArrayList<>();

	private void initCards() {
		try {
			BufferedImage all = ImageIO.read(new File("cards.png"));
			cardWidth = (((double)all.getWidth())/numCardsInSuit);
			cardHeight = (((double)all.getHeight())/numSuits);
			for(int i = 0; i < numSuits; i++) {
				for(int j = 0; j < numCardsInSuit; j++) {
					BufferedImage image = (all.getSubimage((int)(j*cardWidth), (int)(i*cardHeight), (int)cardWidth, (int)cardHeight));
					SortableObject sortable = (i*13 + j);
					cards.add(new CardView(sortable, image, position));
				}
			}
			recalculate();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public CardCanvas() {
		initCards();
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	private Point getPositionInGrid(int gridX, int gridY) {
		int resultantX = 0;
		int resultantY = 0;
		resultantX += (cardWidth + padding) * gridX;
		resultantY += (cardWidth + padding) * gridY;
		resultantX += getWidth()/2 - (cardsPerLine.get(gridY) % 2 == 0 ? cardWidth - padding : cardWidth/2);
		resultantY += getHeight()/2 - (rows % 2 == 0 ? cardHeight - padding/2 : cardHeight/2);
	}

	public void recalculate() {
		cardCenterY = (int)(getHeight()/2-cardHeight/2);
		rows = 1 + (int)(((cardWidth + padding) * numCardsInSuit + padding)/getWidth()); //calculate # of rows
		cardsPerRow = new ArrayList<Integer>();

		if(padding >= getWidth()) {
			System.err.println("Padding value too high: " + padding);
			System.exit(5);
		}

		int cardsRemaining = cardsInSuit;
		int maxCardsPerLine = (getWidth() - padding)/((int)(cardWidth + padding));
		for(int i = 0; i < rows; i++) {
			cardsRemaining -= maxCardsPerLine;
			if(cardsRemaining == 0) {
				cardsPerRow.add(maxCardsPerLine);
				break;
			} else if(cardsRemaining < 0) {
				cardsPerRow.add(cardscardsRemaining + maxCardsPerLine);
				break;
			} else {
				cardsPerRow.add(maxCardsPerLine);
			}
		} //get a list of the number of cards for each row

		//calculate positions of cards
		int currentCard = 0;
		for(int i = 0; i < rows; i++) {
			//point.x = (int)(i*cardWidth)
			//(int)((int)(i*cardWidth)/cardWidthInt)*cardWidthInt
			for(int cards = 0; cards < cardsPerRow.get(i); cards++) {
				cards.get(i).getCachedPosition().setLocation(getPositionInGrid(cards - cardsPerRow.get(i)/2, i));

				if(currentCard++ >= numCardsInSuit) break;
			}
		}
		revalidate();
		repaint();
	}

	public int cardOffset;

	public void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		for(int i = 0; i < 13; i++) {
			if(cardFlipped.get(i)) {
				if(cardSelected.get(i)) {
					g.setColor(Color.BLUE);
				} else {
					g.setColor(Color.GRAY);
				}
				g.fillRect((int)cards.get(i).getCachedPosition().getX()+1, (int)cards.get(i).getCachedPosition().getY()+1, (int)cards.get(i).getCachedPosition().getWidth()-2, (int)cards.get(i).getCachedPosition().getHeight()-2);
			} else {
				g.drawImage(cards.get(i).getImage(), (int)cards.get(i).getCachedPosition().getX(), (int)cards.get(i).getCachedPosition().getY(), null);
			}
			//cardPos.get(i).setLocation(i * cards.get(i).getWidth(), 0);
		}
		if(dragging == true) {
			g.setColor(new Color(0, 100, 255, 100));
			int x = originalClickX;
			int y = originalClickY;
			int w = curMX - originalClickX;
			int h = curMY - originalClickY;
			if(w < 0) {
				x += w;
				w *= -1;
			}
			if(h < 0) {
				y += h;
				h *= -1;
			}
			g.fillRect(x, y, w, h);
		}
	}

	int xOffset = 0;
	int yOffset = 0;
	int originalClickX;
	int originalClickY;
	Rectangle originalRect;
	int originalPos;

	public void mouseClicked(MouseEvent e) {
		int index = cardIndexAtPoint(e.getPoint());
		if(index != -1) {
			cardFlipped.set(index, !cardFlipped.get(index));
		}
		//MAKE THIS END SELECT
	}
	public void mouseEntered(MouseEvent e) {

	}
	public void mouseExited(MouseEvent e) {

	}
	public int cardIndexAtPoint(Point p) {
		for(int i = 0; i < 13; i++) {
			if(i != selectedCard) {
				if(cardPos.get(i).contains(p)) {
					return i;
				}
			}
		}
		return -1;
	}
	public void mousePressed(MouseEvent e) {
		//Point mousePoint = e.getPoint();
		//mousePoint.setX(mousePoint.getX()+cardOffset);
		originalClickX = (int)e.getX();
		originalClickY = (int)e.getY();
		for(int i = 0; i < 13; i++) {
			if(cardPos.get(i).contains(e.getPoint())) {
				System.out.println("Clicked " + i);
				selectedCard = i;
				xOffset = (int)(e.getPoint().getX()-cardPos.get(i).getX());
				yOffset = (int)(e.getPoint().getY()-cardPos.get(i).getY());
				originalRect = (Rectangle)cardPos.get(i).clone();
				originalPos = cardIndex.get(i);
				return;
			}
		}
	}
	public void mouseReleased(MouseEvent e) {
		if(selectedCard != -1) {
			int cardPosition = (int)((e.getPoint().getX()-cardOffset)/cardWidthInt);
			int droppedCard = (cardIndexAtPoint(e.getPoint()));
			if(cardPosition >= 0 && cardPosition < 13) {
				System.out.println("Successful drop");
				cardPos.get(selectedCard).setLocation((int)((e.getPoint().getX()-cardOffset)/cardWidthInt)*cardWidthInt+cardOffset, desiredY);
			} else {
				System.out.println("Failed drop");
				cardPos.get(selectedCard).setLocation((int)(((int)(cardWidth*originalPos)))+cardOffset, desiredY);
			}
			if(droppedCard != -1) {
				System.out.println("Dropped selectedCard " + selectedCard + " onto " + droppedCard);
				//cardPos.get(droppedCard.setPosition((int)(((int)(selectedCard*cardWidth))/cardWidth)+cardOffset, desiredY));
				cardPos.set(droppedCard, originalRect);
				cardIndex.set(droppedCard, originalPos);
				cardIndex.set(selectedCard, cardPosition);
				System.out.println(cardPos.get(selectedCard));
			}
		} else if(dragging == true) {
			int x = originalClickX;
			int y = originalClickY;
			int w = curMX - originalClickX;
			int h = curMY - originalClickY;
			if(w < 0) {
				x += w;
				w *= -1;
			}
			if(h < 0) {
				y += h;
				h *= -1;
			}
			Rectangle dragRect = new Rectangle(x, y, w, h);
			for(int i = 0; i < 13; i++) {
				if(dragRect.intersects(cardPos.get(i))) {
					cardSelected.set(i, true);
				} else {
					cardSelected.set(i, false);
				}
			}
		}
		selectedCard = -1;
		dragging = false;
		revalidate();
		repaint();
		System.out.println("desiredY: " + desiredY);
	}
	public void mouseMoved(MouseEvent e) {

	}
	int curMX;
	int curMY;
	boolean dragging = false;
	public void mouseDragged(MouseEvent e) {
		//System.out.println(selectedCard);
		if(selectedCard != -1) {
			cardPos.get(selectedCard).setLocation((int)(e.getPoint().getX()-xOffset), (int)(e.getPoint().getY()-yOffset));
			revalidate();
			repaint();
		} else {
			curMX = (int)e.getX();
			curMY = (int)e.getY();
			dragging = true;
			revalidate();
			repaint();
		}
	}
}
