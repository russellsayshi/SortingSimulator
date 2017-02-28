import java.awt.*;

public class CardView {
	private boolean beingDragged = false;
	private SortableObject cardValue;
	private BufferedImage image;
	private boolean flipped = false;
	private boolean selected = false;
	private Rectangle cachedPosition = new Rectangle(0, 0, 0, 0);

	public CardView(SortableObject value, BufferedImage image) {
		this.cardValue = value;
		this.image = image;
		this.viewableRect = rect;
	}

	public void drawAt(Graphics g, int x, int y) {
		g.drawImage(image, x, y, null);
	}

	public void setBeingDragged(boolean dragged) {
		this.beingDragged = dragged;
	}

	public boolean isBeingDragged() {
		return beingDragged;
	}

	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}

	public boolean isFlipped() {
		return flipped;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setCachedPosition(Rectangle rect) {
		this.cachedPosition = rect;
	}

	public Rectangle getCachedPosition() {
		return cachedPosition;
	}

	public BufferedImage getImage() {
		return image;
	}
}
