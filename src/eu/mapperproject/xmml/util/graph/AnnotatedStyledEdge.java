package eu.mapperproject.xmml.util.graph;

/**
 * An simple styled edge with label and style.
 * @author JOris Borgdorff
 */
public class AnnotatedStyledEdge extends SimpleStyledEdge {
	private final String style, label;

	public AnnotatedStyledEdge(StyledNode from, StyledNode to, String style, String label) {
		super(from, to);
		this.style = style;
		this.label = label;
	}

	@Override
	public String getStyle() {
		return this.style;
	}

	@Override
	public String getLabel() {
		return this.label;
	}
}
