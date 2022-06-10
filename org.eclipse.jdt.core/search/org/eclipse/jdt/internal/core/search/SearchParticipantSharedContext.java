package org.eclipse.jdt.internal.core.search;

import java.util.Arrays;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ITypeRequestor;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.core.search.matching.JavaSearchNameEnvironment;

public class SearchParticipantSharedContext implements AutoCloseable {

	private final static ThreadLocal<SearchParticipantSharedContext> context = new ThreadLocal<>();

	private IJavaProject javaProject;
	private ICompilationUnit[] workingWopies;
	private LookupEnvironment lookupEnvironment;

	public SearchParticipantSharedContext() {
		context.set(this);
	}

	@Override
	public void close() {
		context.remove();
	}

	public LookupEnvironment getSharedLookupEnvironment(ITypeRequestor typeRequestor, IJavaProject actualJavaProject,
			ICompilationUnit[] actualWorkingWopies, CompilerOptions compilerOptions, ProblemReporter problemReporter) {
		if (this.lookupEnvironment == null || !actualJavaProject.equals(this.javaProject)
				|| !Arrays.deepEquals(actualWorkingWopies, this.workingWopies)) {
			JavaSearchNameEnvironment javaSearchNameEnvironment = new JavaSearchNameEnvironment(actualJavaProject,
					actualWorkingWopies);
			this.lookupEnvironment = new LookupEnvironment(typeRequestor, compilerOptions, problemReporter,
					javaSearchNameEnvironment);
			this.javaProject = actualJavaProject;
			this.workingWopies = actualWorkingWopies;
		}
		this.lookupEnvironment.globalOptions = compilerOptions;
		this.lookupEnvironment.problemReporter = problemReporter;
		this.lookupEnvironment.typeRequestor = typeRequestor;
		return this.lookupEnvironment;
	}

	public static SearchParticipantSharedContext create() {
		return new SearchParticipantSharedContext();
	}

	public static SearchParticipantSharedContext get() {
		return context.get();
	}
}
