package eu.mapperproject.jmml.topology.algorithms;

import eu.mapperproject.jmml.specification.SEL;

/**
 *
 * @author Joris Borgdorff
 */
public class ProcessIterationRange {
	private SEL rangeFromOper, rangeToOper;
	private int rangeFromIter, rangeToIter;

	public ProcessIterationRange(AnnotationSet set) {
		rangeFromIter = rangeToIter = set.getIteration();
		rangeFromOper = rangeToOper = set.getOperator();
	}

	public void updateRange(ProcessIterationRange range, boolean min) {
		if (min) {
			this.updateRange(range.rangeFromIter, range.rangeFromOper, min);
		}
		else {
			this.updateRange(range.rangeToIter, range.rangeToOper, min);
		}
	}

	void updateRange(AnnotationSet set, boolean min) {
		this.updateRange(set.getIteration(), set.getOperator(), min);
	}

	private void updateRange(int it, SEL op, boolean min) {
		if (min) {
			if (it < rangeFromIter) {
				rangeFromIter = it;
			}

			rangeFromOper = op;
		}
		else {
			if (it > rangeToIter) {
				rangeToIter = it;
			}

			rangeToOper = op;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(20);
		this.appendToStringBuilder(sb);
		return sb.toString();
	}

	/** Append a the counter of this range to given StringBuilder */
	public void appendToStringBuilder(StringBuilder sb) {
		sb.append('(');
		sb.append(rangeFromIter);
		if (rangeFromIter < rangeToIter) {
			sb.append('-'); sb.append(rangeToIter);
		}
		sb.append(',');
		sb.append(rangeFromOper.value()); sb.append('-'); sb.append(rangeToOper.value());
		sb.append(')');
	}
}
