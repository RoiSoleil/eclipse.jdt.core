package org.eclipse.jdt.internal.compiler.ast;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import org.eclipse.jdt.internal.compiler.impl.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.internal.compiler.problem.*;
import org.eclipse.jdt.internal.compiler.IAbstractSyntaxTreeVisitor;

public abstract class AstNode  implements BaseTypes, CompilerModifiers, TypeConstants, TypeIds {
	public int sourceStart, sourceEnd;
	
	//some global provision for the hierarchy
	public final static Constant NotAConstant = Constant.NotAConstant;

/**
 * AstNode constructor comment.
 */
public AstNode() {
	super();
}
public boolean cannotReturn(){
	return false;
}
public AstNode concreteStatement(){
	return this;
}
/* Answer true if the field use is considered deprecated.
* An access in the same compilation unit is allowed.
*/

public final boolean isFieldUseDeprecated(FieldBinding field, Scope scope) {
	return field.isViewedAsDeprecated() && !scope.isDefinedInSameUnit(field.declaringClass);
}
/* Answer true if the method use is considered deprecated.
* An access in the same compilation unit is allowed.
*/

public final boolean isMethodUseDeprecated(MethodBinding method, Scope scope) {
	return method.isViewedAsDeprecated() && !scope.isDefinedInSameUnit(method.declaringClass);
}
public boolean isSuper() {
	
	return false;
}
public boolean isThis() {
	return false;
}
/* Answer true if the type use is considered deprecated.
* An access in the same compilation unit is allowed.
*/

public final boolean isTypeUseDeprecated(TypeBinding type, Scope scope) {
	if (type.isArrayType())
		type = ((ArrayBinding) type).leafComponentType;
	if (type.isBaseType()) return false;

	ReferenceBinding refType = (ReferenceBinding) type;
	return refType.isViewedAsDeprecated() && !scope.isDefinedInSameUnit(refType);
}
public static String modifiersString(int modifiers) {
	
	String s = ""; //$NON-NLS-1$
	if ((modifiers & AccPublic) != 0)
		s = s + "public "; //$NON-NLS-1$
	if ((modifiers & AccPrivate) != 0)
		s = s + "private "; //$NON-NLS-1$
	if ((modifiers & AccProtected) != 0)
		s = s + "protected "; //$NON-NLS-1$
	if ((modifiers & AccStatic) != 0)
		s = s + "static "; //$NON-NLS-1$
	if ((modifiers & AccFinal) != 0)
		s = s + "final "; //$NON-NLS-1$
	if ((modifiers & AccSynchronized) != 0)
		s = s + "synchronized "; //$NON-NLS-1$
	if ((modifiers & AccVolatile) != 0)
		s = s + "volatile "; //$NON-NLS-1$
	if ((modifiers & AccTransient) != 0)
		s = s + "transient "; //$NON-NLS-1$
	if ((modifiers & AccNative) != 0)
		s = s + "native "; //$NON-NLS-1$
	if ((modifiers & AccAbstract) != 0)
		s = s + "abstract "; //$NON-NLS-1$
	return s;
}
/** @Deprecated
*/
public int sourceEnd() {
		return sourceEnd;}
/** @Deprecated
*/
public int sourceStart() {
		return sourceStart;}
public static String tabString(int tab){
	/*slow code*/

	String s = "" ; //$NON-NLS-1$
	for (int i = tab ; i > 0 ; i--) s = s + "  " ; //$NON-NLS-1$
	return s;}
public String toString() {
	/* low speed */

	return toString(0);
}
public String toString(int tab){
	/* low speed */

	/*If reach here fetchs the default display*/
	return "****"+ super.toString()+ "****";} //$NON-NLS-2$ //$NON-NLS-1$
public void traverse(IAbstractSyntaxTreeVisitor visitor, BlockScope scope){
}
}
