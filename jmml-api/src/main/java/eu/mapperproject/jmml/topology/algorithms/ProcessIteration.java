package eu.mapperproject.jmml.topology.algorithms;

import eu.mapperproject.jmml.specification.OptionalChoice;
import eu.mapperproject.jmml.specification.SEL;
import eu.mapperproject.jmml.specification.annotated.AnnotatedCoupling;
import eu.mapperproject.jmml.specification.annotated.AnnotatedInstance;

/**
 * A process iteration represents one single operator executing of a single submodel
 * instance.
 * @author Joris Borgdorff
 */
public class ProcessIteration {

	private static final ProcessIterationCache cache = new ProcessIterationCache();
	private AnnotationSet annot;
	private AnnotationSet annotOut;
	private final AnnotatedInstance instance;
	// As equals is the most costly operation in processiteration
	// this cache was added
	private String asString;
	private final String origString;
	private boolean initial, isfinal, deadlock;
	private ProcessIterationRange range;
	private boolean stateProgressed;

	public enum ProgressType {
		INSTANCE, ITERATION;
	}
	
	public ProcessIteration(AnnotatedInstance pd) {
		this(pd, null);
	}
	
	ProcessIteration(AnnotatedInstance pd, AnnotationSet annot) {
		this.instance = pd;
		if (annot == null) {
			SEL defOp = instance.ofSubmodel() ? SEL.FINIT : null;
			this.annot = new AnnotationSet(defOp);
			this.annot.setSubject(pd, defOp);
			this.annot.applySubject();
		}
		else this.annot = annot;
		
		this.annotOut = null;
		this.range = null;
		
		this.isfinal = false;
		this.deadlock = false;
		this.initial = this.instance.isInit() && firstInstance() && initializing();
		this.asString = this.updateString(true);
		this.origString = this.updateString(false);
		this.stateProgressed = false;
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
		return this.annot.getOperator();
	}


	public final int getIteration() {
		return this.annot.getIteration();
	}
	
	public int getInstanceCounter() {
		return this.annot.getInstance();
	}

	public boolean instanceCompleted() {
		return !this.instance.ofSubmodel() || this.annot.getOperator() == SEL.OF;
	}
	
	public boolean finalLoop() {
		return !this.instance.ofSubmodel() || this.instance.isCompleted(annot.getIteration()) && annot.getOperator() != SEL.FINIT;
	}
	
	public AnnotatedInstance getInstance() {
		return this.instance;
	}

	public final boolean firstInstance() {
		return this.annot.getInstance() == 0;
	}

	public boolean firstLoop() {
		return !this.instance.ofSubmodel() || this.annot.getIteration() == 0 && this.annot.getOperator().compareTo(SEL.B) <= 0;
	}
	
	public final boolean initializing() {
		return !this.instance.ofSubmodel() || this.annot.getOperator() == SEL.FINIT;
	}
	
	public boolean needsState() {
		return instance.ofSubmodel() && initializing() && !firstInstance() && instance.getStateful() != OptionalChoice.NO;
	}

	public boolean isSingle() {
		return this.range == null;
	}
	
	public boolean isSending() {
		return this.annot.getOperator() == null || this.annot.getOperator() == SEL.OI || this.annot.getOperator() == SEL.OF;
	}
	
	public ProcessIteration nextStep(boolean collapse) {
		if (!this.instance.ofSubmodel()) throw new IllegalStateException("Can not make a step with a mapper.");
		return this.progress(ProgressType.ITERATION, collapse);
	}
	
	public ProcessIteration nextState() {
		if (!this.instance.ofSubmodel()) throw new IllegalStateException("Can not store a state with a mapper.");
		return this.progress(ProgressType.INSTANCE, false);
	}

	public ProcessIteration nextIteration(AnnotatedCoupling pd) {
		if (!pd.getTo().getInstance().ofSubmodel()) throw new IllegalStateException("Can not go to an iteration of a mapper.");
		if (this.instance.equals(pd.getTo().getInstance())) {
			throw new IllegalArgumentException("In a progression that is an internal iteration, an coupling may not be specified.");
		}
		return this.progress(pd, ProgressType.ITERATION);
	}
	
	public ProcessIteration nextInstance(AnnotatedCoupling pd) {
		return this.progress(pd, ProgressType.INSTANCE);
	}

	public CouplingInstance calculateCouplingInstance(AnnotatedCoupling cd, boolean nextInstance) {
		ProcessIteration pnext;
		
		SEL nextOp = cd.getTo().getPort().getOperator();
		if (nextInstance || nextOp == null || nextOp == SEL.FINIT) {
			pnext = this.nextInstance(cd);
		}
		else {
			pnext = this.nextIteration(cd);
		}
		
		return new CouplingInstance(this, pnext, cd);
	}

	private ProcessIteration progress(ProgressType instance, boolean collapse) {		
		if (this.stateProgressed) {
			throw new IllegalStateException("Can not progress to another state of " + this + " if the state has already progressed.");
		}

		AnnotatedInstance pd = this.instance;

		boolean hasEdgesOut = this.annotOut != null;
		AnnotationSet set = new AnnotationSet(hasEdgesOut ? this.annotOut : this.annot);

		switch (instance) {
			case ITERATION:
				SEL currentOp = this.annot.getOperator();
				if (currentOp == SEL.OF) {
					throw new IllegalStateException("Process '" + pd + "' was already finished but is called for a next step, in iteration " + this.annot.getIteration());
				}

				// Loop until the end condition is met or sequentially within the loop
				if (currentOp.compareTo(SEL.B) < 0 || this.finalLoop()) {
					set.nextOperator();
				}
				else {
					set.setOperater(SEL.OI);
					set.nextIteration();
				}
				break;
			case INSTANCE:
				set.nextInstance();
				set.resetIteration();
				set.resetOperator();
				break;
			default:
				throw new IllegalArgumentException("Only ITERATION and INSTANCE operations are allowed for instance progress.");
		}

		set.applySubject();

		if (!collapse || hasEdgesOut || instance == ProgressType.INSTANCE) {
			// Since no more progress may be made, we can free the annotations
			this.annotOut = null;
			this.annot.freeTraces();
			this.stateProgressed = true;

			return cache.getIteration(pd, set);
		}
		else {
			if (range == null) {
				range = new ProcessIterationRange(this.annot);
			}
			this.range.updateRange(set, false);
			this.annot = set;
			cache.putIteration(pd, set, this);
			this.asString = this.updateString(true);

			return null;
		}
	}
	
	private ProcessIteration progress(AnnotatedCoupling cd, ProgressType instance) {		
		if (this.stateProgressed) {
			throw new IllegalStateException("Can not progress to another processiteration " + cd + " if the state has already progressed.");
		}
		
		AnnotatedInstance pd = cd.getTo().getInstance();		
		AnnotationSet set = new AnnotationSet(this.annot);
		set.setSubject(pd, pd.ofSubmodel() ? SEL.FINIT : null);
		SEL nextOp = cd.getTo().getPort().getOperator();
		
		switch (instance) {
			case ITERATION:
				if (nextOp == SEL.OF) {
					throw new IllegalStateException("Process '" + pd + "' was already finished but is called again, in iteration " + set.getIteration());
				}
				
				SEL currentOp = set.getOperator();

				if (currentOp.compareTo(nextOp) >= 0) {
					set.nextIteration();
				}

				set.setOperater(nextOp);
				break;
			case INSTANCE:
				set.nextInstance();
				set.resetIteration();
				set.setOperater(nextOp);
				break;
			default:
				throw new IllegalArgumentException("Only ITERATION and INSTANCE operations are allowed for instance progress.");
		}

		set.applySubject();

		if (this.annotOut == null) {
			this.annotOut = new AnnotationSet(this.annot);
			cache.remove(this.instance, this.annot);
		}
		this.annotOut.merge(set);

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
		if (this.annotOut != null) {
			throw new IllegalStateException("Can not add more initialization information after processiteration has sent information.");
		}
		this.annot.merge(key);
	}

	public void updateRange(ProcessIteration pi, boolean min) {
		if (range == null) {
			range = new ProcessIterationRange(this.annot);
		}

		if (pi.range == null) {
			this.range.updateRange(pi.annot, min);
		}
		else {
			this.range.updateRange(pi.range, min);
		}

		this.asString = this.updateString(true);
	}

	private String updateString(boolean useId) {
		String id = useId ? this.instance.getId() : Integer.toString(this.instance.getNumber());
		StringBuilder sb = new StringBuilder(id.length() + 30);
		sb.append(id);
		if (this.range == null) {
			                                     // Mappers can do with simple notation.
			this.annot.appendToStringBuilder(sb, instance.ofSubmodel());
		}
		else {
			this.annot.appendToStringBuilder(sb, false);
			this.range.appendToStringBuilder(sb);
		}
		return sb.toString();
	}
}
