package model.convert;

public class ColorPack {

	private int r;
	private int g;
	private int b;
	
	public ColorPack(int a, int b, int c) {
		r = affixColor(a);
		g = affixColor(b);
		b = affixColor(c);
	}
	
	public String getGraphvizColor() {
		String out = "#" + hex(r) + hex(g) + hex(b);
		return out;
	}
	
	private String hex(int in) {
		String out = Integer.toHexString(in);
		if(out.length() == 1) {
			out = "0" + out;
		}
		return out;
	}
	
	public ColorPack cycleColor(int in) {
		ColorPack start = new ColorPack(r, g, b);
		for(int i = 1; i < in; i++) {
			start = start.cycleColor();
		}
		return start;
	}
	
	public ColorPack cycleColor() {
		return new ColorPack(r + (int)((255 - r) * .1), g + (int)((255 - g) * .1), b + (int)((255 - b) * .1));
	}
	
	private int affixColor(int in) {
		return in < 0 ? 0 : in > 255 ? 255 : in;
	}
	
	@Override
	public String toString() {
		return getGraphvizColor();
	}
	
}
