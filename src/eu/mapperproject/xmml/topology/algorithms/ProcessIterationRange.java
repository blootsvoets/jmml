/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mapperproject.xmml.topology.algorithms;

import eu.mapperproject.xmml.definitions.Submodel.SEL;

/**
 *
 * @author Joris Borgdorff
 */
public class ProcessIterationRange {
	private Range range;
	private final int iter;
	private final SEL oper;

	public ProcessIterationRange(int iter, SEL oper) {
		this.range = null;
		this.iter = iter;
		this.oper = oper;
	}

	public void updateRange(ProcessIterationRange pirange, boolean min) {
		if (range == null) {
			range = new Range(this.iter, this.oper);
		}
		int it;
		SEL op;
		if (min) {
			it = pirange.getFromIteration();
			op = pirange.getFromOperator();
			if (it < range.rangeFromIter) {
				range.rangeFromIter = it;
				range.rangeFromOper = op;
			}
			else if (it == range.rangeFromIter && op.compareTo(range.rangeFromOper) < 0) {
				range.rangeFromOper = op;
			}
		}
		else {
			it = pirange.getToIteration();
			op = pirange.getToOperator();
			if (range.rangeToIter < it) {
				range.rangeToIter = it;
				range.rangeToOper = op;
			}
			else if (range.rangeToIter == it && range.rangeToOper.compareTo(op) < 0) {
				range.rangeToOper = op;
			}
		}
	}

	public int getIteration() {
		return this.iter;
	}

	public SEL getOperator() {
		return this.oper;
	}

	private int getToIteration() {
		if (range == null) return iter;
		return range.rangeToIter;
	}

	private SEL getToOperator() {
		if (range == null) return oper;
		return range.rangeToOper;
	}

	private int getFromIteration() {
		if (range == null) return iter;
		return range.rangeFromIter;
	}

	private SEL getFromOperator() {
		if (range == null) return oper;
		return range.rangeFromOper;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(20);
		this.appendToStringBuilder(sb);
		return sb.toString();
	}

	public void appendToStringBuilder(StringBuilder sb) {
		sb.append('(');
		if (range == null) {
			sb.append(iter); sb.append(','); sb.append(oper);
		}
		else {
			range.appendRange(sb);
		}
		sb.append(')');
	}

	private static class Range {
		private SEL rangeFromOper, rangeToOper;
		private int rangeFromIter, rangeToIter;

		Range(int iter, SEL oper) {
			rangeFromIter = rangeToIter = iter;
			rangeFromOper = rangeToOper = oper;
		}

		void appendRange(StringBuilder sb) {
			boolean iterLess = rangeFromIter < rangeToIter;
			sb.append(rangeFromIter);
			if (iterLess) {
				sb.append('-'); sb.append(rangeToIter);
			}
			sb.append(',');
			sb.append(rangeFromOper);

			if (iterLess || rangeFromOper.compareTo(rangeToOper) < 0) {
				sb.append('-'); sb.append(rangeToOper);
			}
		}
	}
}
