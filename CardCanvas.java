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
					SortableObject sortable = new SortableObject(i*13 + j);
					cards.add(new CardView(sortable, image, i*numCardsInSuit + j));
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
		resultantX += getWidth()/2 - (cardsPerRow.get(gridY) % 2 == 0 ? cardWidth - padding : cardWidth/2);
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

		int cardsRemaining = numCardsInSuit;
		int maxCardsPerRow = (getWidth() - padding)/((int)(cardWidth + padding));
		for(int i = 0; i < rows; i++) {
			cardsRemaining -= maxCardsPerRow;
			if(cardsRemaining == 0) {
				cardsPerRow.add(maxCardsPerRow);
				break;
			} else if(cardsRemaining < 0) {
				cardsPerRow.add(cardsRemaining + maxCardsPerRow);
				break;
			} else {
				cardsPerRow.add(maxCardsPerRow);
			}
		} //get a list of the number of cards for each row

		//calculate positions of cards
		int currentCard = 0;
		for(int i = 0; i < rows; i++) {
			//point.x = (int)(i*cardWidth)
			//(int)((int)(i*cardWidth)/cardWidthInt)*cardWidthInt
			for(int numCards = 0; numCards < cardsPerRow.get(i); numCards++) {
				cards.get(i).getCachedPosition().setLocation(getPositionInGrid(numCards - cardsPerRow.get(i)/2, i));

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
			CardView card = cards.get(i);
			if(card.isFlipped()) {
				if(card.isSelected()) {
					g.setColor(Color.BLUE);
				} else {
					g.setColor(Color.GRAY);
				}
				g.fillRect((int)card.getCachedPosition().getX()+1, (int)card.getCachedPosition().getY()+1, (int)card.getCachedPosition().getWidth()-2, (int)card.getCachedPosition().getHeight()-2);
			} else {
				g.drawImage(card.getImage(), (int)card.getCachedPosition().getX(), (int)card.getCachedPosition().getY(), null);
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
	CardView selectedCard;
	int originalIndex;

	public void mouseClicked(MouseEvent e) {
		CardView card = getCardAtPoint(e.getPoint());
		if(card != null) {
			card.toggleFlipped();
		}
		//MAKE THIS END SELECT
	}
	public void mouseEntered(MouseEvent e) {

	}
	public void mouseExited(MouseEvent e) {

	}
	public CardView getCardAtPoint(Point p) {
		for(int i = 0; i < 13; i++) {
			CardView card = cards.get(i);
			if(!card.isBeingDragged()) {
				if(card.getCachedPosition().contains(p)) {
					return card;
				}
			}
		}
		return null;
	}
	public void mousePressed(MouseEvent e) {
		//Point mousePoint = e.getPoint();
		//mousePoint.setX(mousePoint.getX()+cardOffset);
		originalClickX = (int)e.getX();
		originalClickY = (int)e.getY();
		for(int i = 0; i < numCardsInSuit; i++) {
			CardView card = cards.get(i);
			if(card.getCachedPosition().contains(e.getPoint())) {
				System.out.println("Clicked " + i);
				selectedCard = card;
				xOffset = (int)(e.getPoint().getX()-card.getCachedPosition().getX());
				yOffset = (int)(e.getPoint().getY()-card.getCachedPosition().getY());
				originalRect = (Rectangle)card.getCachedPosition().clone();
				originalIndex = card.getIndex();
				return;
			}
		}
	}
	public void mouseReleased(MouseEvent e) {
		if(selectedCard != null) {
			int cardWidthInt = (int)cardWidth;
			int cardIndex = (int)((e.getPoint().getX()-cardOffset)/cardWidthInt);
			CardView droppedCard = (getCardAtPoint(e.getPoint()));
			if(cardIndex >= 0 && cardIndex < 13) {
				System.out.println("Successful drop");
				selectedCard.setLocation((int)((e.getPoint().getX()-cardOffset)/cardWidthInt)*cardWidthInt+cardOffset, cardCenterY);
			} else {
				System.out.println("Failed drop");
				selectedCard.setLocation((int)(((int)(cardWidth*originalPos)))+cardOffset, cardCenterY);
			}
			if(droppedCard != null) {
				System.out.println("Dropped selectedCard " + selectedCard + " onto " + droppedCard);
				//cardPos.get(droppedCard.setPosition((int)(((int)(selectedCard*cardWidth))/cardWidth)+cardOffset, desiredY));
				droppedCard.setCachedPosition(originalRect);
				droppedCard.setIndex(originalPos);
				selectedCard.setIndex(cardPosition);
				System.out.println(selectedCard);
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
			for(int i = 0; i < numCardsInSuit; i++) {
				if(dragRect.intersects(cards.get(i).getCachedPosition())) {
					cardSelected.set(i, true);
				} else {
					cardSelected.set(i, false);
				}
			}
		}
		selectedCard = null;
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
		if(selectedCard != null) {
			selectedCard.setCachedPosition((int)(e.getPoint().getX()-xOffset), (int)(e.getPoint().getY()-yOffset));
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
