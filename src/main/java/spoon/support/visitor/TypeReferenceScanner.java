/**
 * Copyright (C) 2006-2015 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.support.visitor;

import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * A scanner that calculates the imports for a given model.
 */
public class TypeReferenceScanner extends CtScanner {

	Set<CtTypeReference<?>> references;

	/**
	 * Constructor.
	 */
	public TypeReferenceScanner() {
		references = new HashSet<CtTypeReference<?>>();
	}

	/**
	 * Constructor.
	 *
	 * @param references
	 * 		a set to fill with the references
	 */
	public TypeReferenceScanner(HashSet<CtTypeReference<?>> references) {
		this.references = references;
	}

	/**
	 * Returns the set of calculated references.
	 */
	public Set<CtTypeReference<?>> getReferences() {
		return references;
	}

	/**
	 * Adds a reference.
	 */
	private <T> boolean addReference(CtTypeReference<T> ref) {
		return references.add(ref);
	}

	@Override
	public <T> void visitCtFieldAccess(CtFieldAccess<T> f) {
		enter(f);
		scan(f.getVariable());
		// scan(fieldAccess.getType());
		scan(f.getAnnotations());
		scan(f.getTypeCasts());
		scan(f.getVariable());
		scan(f.getTarget());
		exit(f);
	}

	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		enter(reference);
		scan(reference.getDeclaringType());
		// scan(reference.getType());
		exit(reference);
	}

	//	public <T> boolean isImported(CtTypeReference<T> ref) {
	//		if (imports.containsKey(ref.getSimpleName())) {
	//			CtTypeReference<?> exist = imports.get(ref.getSimpleName());
	//			if (exist.getQualifiedName().equals(ref.getQualifiedName()))
	//				return true;
	//		}
	//		return false;
	//	}

	@Override
	public <T> void visitCtExecutableReference(
			CtExecutableReference<T> reference) {
		enter(reference);
		scan(reference.getActualTypeArguments());
		exit(reference);
	}

	@Override
	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		if (!(reference instanceof CtArrayTypeReference)) {
			addReference(reference);
		}
		super.visitCtTypeReference(reference);

	}

	@Override
	public <A extends Annotation> void visitCtAnnotationType(
			CtAnnotationType<A> annotationType) {
		addReference(annotationType.getReference());
		super.visitCtAnnotationType(annotationType);
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		addReference(ctEnum.getReference());
		super.visitCtEnum(ctEnum);
	}

	@Override
	public <T> void visitCtInterface(CtInterface<T> intrface) {
		addReference(intrface.getReference());
		for (CtType<?> t : intrface.getNestedTypes()) {
			addReference(t.getReference());
		}
		super.visitCtInterface(intrface);
	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		addReference(ctClass.getReference());
		for (CtType<?> t : ctClass.getNestedTypes()) {
			addReference(t.getReference());
		}
		super.visitCtClass(ctClass);
	}
}

