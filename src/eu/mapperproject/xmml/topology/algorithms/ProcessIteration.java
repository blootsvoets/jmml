package eu.mapperproject.xmml.topology.algorithms;

import eu.mapperproject.xmml.definitions.Submodel.SEL;
import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.topology.Instance;
import eu.mapperproject.xmml.topology.algorithms.Annotation.AnnotationType;

/**
 * A process iteration represents one single operator executing of a single submodel
 * instance.
 * @author Joris Borgdorff
 */
public class ProcessIteration {

	private static final ProcessIterationCache cache = new ProcessIterationCache();
	private AnnotationSet givenAnnot, annot;
	private final Instance instance;
	// As equals is the most costly operation in processiteration
	// this cache was added
	private String asString;
	private final String origString;
	private boolean initial, isfinal, deadlock;
	private final ProcessIterationRange range;
	private final int instCounter;

	public enum ProgressType {
		INSTANCE, ITERATION;
	}
	
	public ProcessIteration(Instance pd) {
		this(pd, null);
	}
	
	ProcessIteration(Instance pd, AnnotationSet annot) {
		this.instance = pd;
		if (annot == null) {
			annot = new AnnotationSet();
			annot.setSubject(pd);
			annot.applySubject();
		}
		this.givenAnnot = annot;
		this.annot = null;
		this.range = new ProcessIterationRange(annot.getIteration(), annot.getOperator());
		this.instCounter = annot.getInstance();

		this.isfinal = false;
		this.deadlock = false;
		this.initial = this.instance.isInitial() && firstInstance() && initializing();
		this.asString = this.updateString(true);
		this.origString = this.updateString(false);
	}
	
	public boolean isFinal() {
		return this.isfinal;
	}
	
	public boolean isInitial() {
		return this.initial;
	}

	public boolean hasDeadlock() {
		return this.deadlock;
	}
	
	public void setFinal() {
		this.isfinal = true;
	}

	public void setInitial() {
		this.initial = true;
	}

	public void setDeadlock() {
		this.deadlock = true;
	}

	public final SEL getOperator() {
		return this.range.getOperator();
	}


	public final int getIteration() {
		return this.range.getIteration();
	}
	
	public int getInstanceCounter() {
		return this.instCounter;
	}

	public boolean instanceCompleted() {
		return this.range.getOperator() == SEL.Of;
	}
	
	public boolean finalLoop() {
		return this.instance.isCompleted(range.getIteration()) && this.range.getOperator() != SEL.finit;
	}
	
	public Instance getInstance() {
		return this.instance;
	}

	public final boolean firstInstance() {
		return this.instCounter == 0;
	}

	public boolean firstLoop() {
		return this.range.getIteration() == 0 && this.range.getOperator().compareTo(SEL.S) <= 0;
	}
	
	public final boolean initializing() {
		return this.range.getOperator() == SEL.finit;
	}
	
	public boolean needsState() {
		return initializing() && !firstInstance() && instance.getSubmodel().isStateful();
	}
	
	public SEL receivingType() {
		SEL op = this.givenAnnot.getOperator();
		if (op.isReceiving()) return op;
		else return null;
	}

	public ProcessIteration nextStep() {
		return this.progress(ProgressType.ITERATION);
	}
	
	public ProcessIteration nextState() {
		return this.progress(ProgressType.INSTANCE);
	}

	public ProcessIteration nextIteration(Coupling pd) {
		if (this.instance.equals(pd.getTo())) {
			throw new IllegalArgumentException("In a progression that is an internal iteration, an coupling may not be specified.");
		}
		return this.progress(pd, ProgressType.ITERATION);
	}
	
	public ProcessIteration nextInstance(Coupling pd) {
		return this.progress(pd, ProgressType.INSTANCE);
	}

	public CouplingInstance calculateCouplingInstance(Coupling cd) {
		ProcessIteration pnext;
		
		if (cd.getToOperator().getOperator() == SEL.finit) {
			pnext = this.nextInstance(cd);
		}
		else {
			pnext = this.nextIteration(cd);
		}
		
		return new CouplingInstance(this, pnext, cd);
	}

	private ProcessIteration progress(ProgressType instance) {		
		if (this.givenAnnot == null) {
			throw new IllegalStateException("Can not progress to another state of " + this + " if the state has already progressed.");
		}

		Instance pd = this.instance;
		
		AnnotationSet set = new AnnotationSet(this.annot == null ? this.givenAnnot : this.annot);
		switch (instance) {
			case ITERATION:
				SEL currentOp = this.range.getOperator();
				if (currentOp == SEL.Of) {
					throw new IllegalStateException("Process '" + pd + "' was already finished but is called for a next step, in iteration " + this.annot.getInstance());
				}

				// Loop until the end condition is met or sequentially within the loop
				if (currentOp.compareTo(SEL.S) < 0 || this.finalLoop()) {
					set.next(AnnotationType.OPERATOR);
				}
				else {
					set.setOperater(SEL.Oi);
					set.next(AnnotationType.ITERATION);
				}
				break;
			case INSTANCE:
				set.next(AnnotationType.INSTANCE);
				set.reset(AnnotationType.ITERATION);
				set.reset(AnnotationType.OPERATOR);
				break;
			default:
				throw new IllegalArgumentException("Only ITERATION and INSTANCE operations are allowed for instance progress.");
		}

		set.applySubject();

		// Since no more progress may be made, we can free the annotations
		this.annot = null;
		this.givenAnnot = null;

		return cache.getIteration(pd, set);
	}
	
	private ProcessIteration progress(Coupling cd, ProgressType instance) {		
		if (this.givenAnnot == null) {
			throw new IllegalStateException("Can not progress to another processiteration " + cd + " if the state has already progressed.");
		}
		
		Instance pd = cd.getTo();		
		AnnotationSet set = new AnnotationSet(this.givenAnnot);
		set.setSubject(pd);
		SEL nextOp = cd.getToOperator().getOperator();
		
		switch (instance) {
			case ITERATION:
				if (nextOp == SEL.Of) {
					throw new IllegalStateException("Process '" + pd + "' was already finished but is called again, in iteration " + set.getIteration());
				}
				
				SEL currentOp = set.getOperator();

				if (currentOp.compareTo(nextOp) >= 0) {
					set.next(AnnotationType.ITERATION);
				}

				set.setOperater(nextOp);
				break;
			case INSTANCE:
				set.next(AnnotationType.INSTANCE);
				set.reset(AnnotationType.ITERATION);
				set.setOperater(nextOp);
				break;
			default:
				throw new IllegalArgumentException("Only ITERATION and INSTANCE operations are allowed for instance progress.");
		}
		
		set.applySubject();
		if (this.annot == null) {
			this.annot = new AnnotationSet(this.givenAnnot);
			cache.remove(this.instance, this.givenAnnot);
		}
		this.annot.merge(set);

		return cache.getIteration(pd, set);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return this.origString.equals(((ProcessIteration)o).origString);
	}

	@Override
	public String toString() {
		return this.asString;
	}
	
	@Override
	public int hashCode() {
		return this.origString.hashCode();
	}

		/**
	 * Merge an annotationset with the current annotation sets
	 */
	void merge(AnnotationSet key) {
		if (this.annot != null) {
			throw new IllegalStateException("Can not add more initialization information after processiteration has sent information.");
		}
		this.givenAnnot.merge(key);
	}

	public void updateRange(ProcessIteration pi, boolean min) {
		this.range.updateRange(pi.range, min);

		this.asString = this.updateString(true);
	}

	private String updateString(boolean useId) {
		String id = useId ? this.instance.getId() : String.valueOf(this.instance.getNumber());
		StringBuilder sb = new StringBuilder(id.length() + 30);
		sb.append(id);
		if (this.instCounter > 0) {
			sb.append(this.instCounter);
		}
		this.range.appendToStringBuilder(sb);
		return sb.toString();
	}
}
