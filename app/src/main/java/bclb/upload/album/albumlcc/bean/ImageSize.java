package bclb.upload.album.albumlcc.bean;

public class ImageSize {
	private int width;
	private int height;
	public ImageSize() {
	}

	public ImageSize(int width, int height) {
		this.width = width;
		this.height = height;
	}



	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}