public class SortableObject implements Comparable<SortableObject> {
	private int value;

	public SortableObject(int value) {
		this.value = value;
	}

	@Override
	public boolean equals(SortableObject other) {
		return other.value == this.value;
	}

	@Override
	public int compareTo(SortableObject other) {
		return this.value - other.value;
	}

	public int getValue() {
		return value;
	}
}
