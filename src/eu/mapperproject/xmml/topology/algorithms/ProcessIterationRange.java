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
		final int it;
		if (min) {
			it = pirange.getFromIteration();
			if (it < range.rangeFromIter) {
				range.rangeFromIter = it;
			}

			range.rangeFromOper = pirange.getFromOperator();
		}
		else {
			it = pirange.getToIteration();
			if (it > range.rangeToIter) {
				range.rangeToIter = it;
			}

			range.rangeToOper = pirange.getToOperator();
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
			sb.append(rangeFromIter);
			if (rangeFromIter < rangeToIter) {
				sb.append('-'); sb.append(rangeToIter);
			}
			sb.append(',');
			sb.append(rangeFromOper); sb.append('-'); sb.append(rangeToOper);
		}
	}
}
