import java.awt.*;
import java.awt.image.*;

public class CardView {
	private SortableObject cardValue;
	private int index = -1;

	private transient boolean beingDragged = false;
	private transient BufferedImage image;
	private transient boolean flipped = false;
	private transient boolean selected = false;
	private transient Rectangle cachedPosition = new Rectangle(0, 0, 0, 0);

	public CardView(SortableObject value, BufferedImage image, int index) {
		this.cardValue = value;
		this.image = image;
		this.viewableRect = rect;
		this.index = index;
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

	public void toggleFlipped() {
		this.flipped = !flipped;
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

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public boolean equals(CardView other) {
		return this.cardValue.equals(other.cardValue);
	}

	@Override
	public boolean compareTo(CardView other) {
		return this.cardValue.compareTo(other.cardValue);
	}
}
