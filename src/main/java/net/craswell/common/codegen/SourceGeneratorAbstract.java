package net.craswell.common.codegen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

/**
 * The base source generator.
 * 
 * @author scraswell@gmail.com
 *
 */
public abstract class SourceGeneratorAbstract {
  /**
   * @return The predicate for filtering annotations when copying.
   */
  protected Predicate<AnnotationMirror> getAnnotationFilter() {
    return a -> {
      return true;
    };
  }

  /**
   * Copies the annotations from an element supporting annotations into a list of annotations.
   * 
   * @param elementSupportingAnnotations The element supporting annotations.
   * 
   * @return The list of annotations.
   */
  protected List<AnnotationSpec> copyAnnotations(Object elementSupportingAnnotations) {
    Element element = null;

    List<AnnotationSpec> annotationSpecs = new ArrayList<AnnotationSpec>();
    List<? extends AnnotationMirror> annotationMirrors = null;

    try {
      element = (Element) elementSupportingAnnotations;
    } catch (ClassCastException cce) {
      // If we can't cast, it's not an element we want to process.
    }

    if (element != null) {
      annotationMirrors = element
          .getAnnotationMirrors()
          .stream()
          .filter(this.getAnnotationFilter())
          .collect(Collectors.toList());

      for (AnnotationMirror annotationMirror : annotationMirrors) {
        annotationSpecs.add(AnnotationSpec.get(annotationMirror));
      }
    }

    return annotationSpecs;
  }

  /**
   * Creates a basic setter given a field name and type.
   * 
   * @param fieldName The field name.
   * @param fieldType The field type.
   * 
   * @return The method specification to create the setter for a field.
   */
  protected MethodSpec constructBasicSetterSpecForFieldName(
      String fieldName,
      TypeName fieldType) {
    if (fieldName == null
        || fieldName.isEmpty()) {
      throw new IllegalArgumentException("field");
    }

    if (fieldType == null) {
      throw new IllegalArgumentException("field");
    }

    return this.constructBasicSetterSpecBuilderForFieldName(
        fieldName,
        fieldType)
        .build();
  }

  /**
   * Creates a basic setter builder given a field name and type.
   * 
   * @param fieldName The field name.
   * @param fieldType The field type.
   * 
   * @return The method specification to create the setter for a field.
   */
  protected MethodSpec.Builder constructBasicSetterSpecBuilderForFieldName(
      String fieldName,
      TypeName fieldType) {
    if (fieldName == null
        || fieldName.isEmpty()) {
      throw new IllegalArgumentException("field");
    }

    if (fieldType == null) {
      throw new IllegalArgumentException("field");
    }

    return this.constructMethodSpecBuilder(
        this.constructBasicSetterJavadoc(fieldName),
        this.determineSetterNameForFieldName(fieldName),
        new ArrayList<Modifier>(Arrays.asList(Modifier.PUBLIC)),
        (TypeName) null,
        (Iterable<AnnotationSpec>) null,
        (Iterable<? extends TypeName>) null,
        this.constructBasicSetterParameters(
            fieldName,
            fieldType),
        this.constructBasicSetterMethodBody(fieldName));
  }

  /**
   * Creates a basic getter given a field name and type.
   * 
   * @param fieldName The field name.
   * @param fieldType The field type.
   * 
   * @return The method specification to create the getter for a field.
   */
  protected MethodSpec constructBasicGetterSpecForFieldName(
      String fieldName,
      TypeName fieldType) {
    if (fieldName == null
        || fieldName.isEmpty()) {
      throw new IllegalArgumentException("field");
    }

    if (fieldType == null) {
      throw new IllegalArgumentException("field");
    }

    return this.constructBasicGetterSpecBuilderForFieldName(
        fieldName,
        fieldType)
        .build();
  }

  /**
   * Creates a basic getter spec builder given a field name and type.
   * 
   * @param fieldName The field name.
   * @param fieldType The field type.
   * 
   * @return The method specification builder for a getter for a field.
   */
  protected MethodSpec.Builder constructBasicGetterSpecBuilderForFieldName(
      String fieldName,
      TypeName fieldType) {
    if (fieldName == null
        || fieldName.isEmpty()) {
      throw new IllegalArgumentException("field");
    }

    if (fieldType == null) {
      throw new IllegalArgumentException("field");
    }

    return this.constructMethodSpecBuilder(
        this.constructBasicGetterJavadoc(fieldName),
        this.determineGetterNameForFieldName(fieldName),
        new ArrayList<Modifier>(Arrays.asList(Modifier.PUBLIC)),
        fieldType,
        (Iterable<AnnotationSpec>) null,
        (Iterable<? extends TypeName>) null,
        (Iterable<ParameterSpec>) null,
        this.constructBasicGetterMethodBody(fieldName));
  }

  /**
   * Creates a field given the parameters.
   * 
   * @param fieldName The field name.
   * @param fieldType The field type.
   * @param modifiers The modifiers.
   * @param annotationSpecs The annotation specifications.
   * 
   * @return The field specification.
   */
  protected FieldSpec constructField(
      String fieldName,
      TypeName fieldType,
      Modifier[] modifiers,
      Iterable<AnnotationSpec> annotationSpecs) {
    return FieldSpec
        .builder(fieldType, fieldName, modifiers)
        .addAnnotations(annotationSpecs)
        .build();
  }

  /**
   * Constructs the method body for a basic setter given a field name.
   * 
   * @param fieldName The field name.
   * @return The code block for a basic setter.
   */
  protected CodeBlock constructBasicSetterMethodBody(String fieldName) {
    return this.constructBasicSetterMethodBodyBuilder(fieldName)
        .build();
  }

  /**
   * Constructs the method body for a basic getter.
   * 
   * @param fieldName
   * @return
   */
  protected CodeBlock constructBasicGetterMethodBody(String fieldName) {
    return this.constructBasicGetterMethodBodyBuilder(fieldName)
        .build();
  }

  /**
   * Creates a method specification given a set of parameters.
   * 
   * @param javaDoc The JavaDoc associated with the method.
   * @param methodName The name of the method.
   * @param modifiers The method modifiers.
   * @param returnType The method return type.
   * @param annotationSpecs The annotations associated with the method.
   * @param exceptionsThrown The exceptions that could be thrown by the method.
   * @param parameterSpecs The parameters that the method will accept as input.
   * @param methodBody The method body.
   * 
   * @return The method specification.
   */
  protected MethodSpec constructMethodSpec(
      CodeBlock javaDoc,
      String methodName,
      Iterable<Modifier> modifiers,
      TypeName returnType,
      Iterable<AnnotationSpec> annotationSpecs,
      Iterable<? extends TypeName> exceptionsThrown,
      Iterable<ParameterSpec> parameterSpecs,
      CodeBlock methodBody) {

    return this.constructMethodSpecBuilder(
        javaDoc,
        methodName,
        modifiers,
        returnType,
        annotationSpecs,
        exceptionsThrown,
        parameterSpecs,
        methodBody)
        .build();
  }

  /**
   * Creates a method specification given a set of parameters.
   * 
   * @param javaDoc The JavaDoc associated with the method.
   * @param methodName The name of the method.
   * @param modifiers The method modifiers.
   * @param returnType The method return type.
   * @param annotationSpecs The annotations associated with the method.
   * @param exceptionsThrown The exceptions that could be thrown by the method.
   * @param parameterSpecs The parameters that the method will accept as input.
   * @param methodBody The method body.
   * 
   * @return The method specification.
   */
  protected MethodSpec.Builder constructMethodSpecBuilder(
      CodeBlock javaDoc,
      String methodName,
      Iterable<Modifier> modifiers,
      TypeName returnType,
      Iterable<AnnotationSpec> annotationSpecs,
      Iterable<? extends TypeName> exceptionsThrown,
      Iterable<ParameterSpec> parameterSpecs,
      CodeBlock methodBody) {
    if (methodName == null
        || methodName.isEmpty()) {
      throw new IllegalArgumentException("methodName");
    }

    if (methodBody == null) {
      throw new IllegalArgumentException("methodBody");
    }

    MethodSpec.Builder methodSpecBuilder = MethodSpec
        .methodBuilder(methodName)
        .addCode(methodBody);

    if (javaDoc != null) {
      methodSpecBuilder.addJavadoc(javaDoc);
    }

    if (modifiers != null) {
      methodSpecBuilder.addModifiers(modifiers);
    }

    if (returnType != null) {
      methodSpecBuilder.returns(returnType);
    }

    if (annotationSpecs != null) {
      methodSpecBuilder.addAnnotations(annotationSpecs);
    }

    if (exceptionsThrown != null) {
      methodSpecBuilder.addExceptions(exceptionsThrown);
    }

    if (parameterSpecs != null) {
      methodSpecBuilder.addParameters(parameterSpecs);
    }

    return methodSpecBuilder;
  }

  /**
   * Gets the modifiers applied to a class member.
   * 
   * @param modifiers The integer representation.
   * 
   * @return The modifiers applied to a class member.
   */
  protected Modifier[] getMemberModifiers(int modifiers) {
    List<Modifier> modifierList = new ArrayList<Modifier>();

    if (java.lang.reflect.Modifier.isAbstract(modifiers)) {
      modifierList.add(Modifier.ABSTRACT);
    }

    if (java.lang.reflect.Modifier.isFinal(modifiers)) {
      modifierList.add(Modifier.FINAL);
    }

    if (java.lang.reflect.Modifier.isPrivate(modifiers)) {
      modifierList.add(Modifier.PRIVATE);
    }

    if (java.lang.reflect.Modifier.isProtected(modifiers)) {
      modifierList.add(Modifier.PROTECTED);
    }

    if (java.lang.reflect.Modifier.isPublic(modifiers)) {
      modifierList.add(Modifier.PUBLIC);
    }

    if (java.lang.reflect.Modifier.isStatic(modifiers)) {
      modifierList.add(Modifier.STATIC);
    }

    if (java.lang.reflect.Modifier.isStrict(modifiers)) {
      modifierList.add(Modifier.STRICTFP);
    }

    if (java.lang.reflect.Modifier.isTransient(modifiers)) {
      modifierList.add(Modifier.TRANSIENT);
    }

    if (java.lang.reflect.Modifier.isSynchronized(modifiers)) {
      modifierList.add(Modifier.SYNCHRONIZED);
    }

    if (java.lang.reflect.Modifier.isVolatile(modifiers)) {
      modifierList.add(Modifier.VOLATILE);
    }

    return modifierList.toArray(
        new Modifier[modifierList.size()]);
  }

  /**
   * Determines the method name to be used by a setter generated for a given field.
   * 
   * @param field The field.
   * @return The generated setter name.
   */
  protected String determineSetterNameForField(Field field) {
    return this.determineSetterNameForFieldName(
        field.getName());
  }

  /**
   * Determines the method name to be used by a setter generated for a given field name.
   * 
   * @param field The field name.
   * @return The generated setter name.
   */
  protected String determineSetterNameForFieldName(String fieldName) {
    StringBuilder methodNameBuilder = new StringBuilder();

    methodNameBuilder.append("set");

    methodNameBuilder
        .append(this.firstLetterToUpperCase(fieldName));

    return methodNameBuilder.toString();
  }

  /**
   * Determines the method name to be used by a getter generated for a given field name.
   * 
   * @param field The field.
   * @return The generated getter name.
   */
  protected String determineGetterNameForField(Field field) {
    return this.determineGetterNameForFieldName(
        field.getName());
  }

  /**
   * Determines the method name to be used by a getter generated for a given field name.
   * 
   * @param field The field name.
   * @return The generated getter name.
   */
  protected String determineGetterNameForFieldName(String fieldName) {
    StringBuilder methodNameBuilder = new StringBuilder();

    methodNameBuilder.append("get");

    methodNameBuilder
        .append(this.firstLetterToUpperCase(fieldName));

    return methodNameBuilder.toString();
  }

  /**
   * Changes the first letter of the string to upper case.
   * 
   * @param input The input string.
   * @return The input string with its first letter changed to upper case.
   */
  protected String firstLetterToUpperCase(String input) {
    StringBuilder nameBuilder = new StringBuilder();

    nameBuilder
        .append(input.substring(0, 1).toUpperCase())
        .append(input.substring(1, input.length()));

    return nameBuilder
        .toString();
  }

  /**
   * Changes the first letter of the string to lower case.
   * 
   * @param input The input string.
   * @return The input string with its first letter changed to lower case.
   */
  protected String firstLetterToLowerCase(String input) {
    StringBuilder nameBuilder = new StringBuilder();

    nameBuilder
        .append(input.substring(0, 1).toLowerCase())
        .append(input.substring(1, input.length()));

    return nameBuilder
        .toString();
  }

  /**
   * Constructs the method body for a basic setter given a field name.
   * 
   * @param fieldName The field name.
   * @return The code block for a basic setter.
   */
  protected CodeBlock.Builder constructBasicSetterMethodBodyBuilder(String fieldName) {
    return CodeBlock.builder()
        .addStatement(
            "this.$L = $L",
            fieldName,
            fieldName);
  }

  /**
   * Constructs the parameter specifications for a basic setter.
   * 
   * @param fieldName The field name.
   * @param fieldType The field type.
   * 
   * @return The parameter specifications for a basic setter.
   */
  protected Iterable<ParameterSpec> constructBasicSetterParameters(
      String fieldName,
      Class<?> fieldType) {
    Modifier[] parameterModifiers = new Modifier[] {
        Modifier.FINAL
    };

    ParameterSpec parameterSpec = ParameterSpec.builder(
        fieldType,
        fieldName,
        parameterModifiers)
        .build();

    Iterable<ParameterSpec> parameterSpecs = new ArrayList<ParameterSpec>(
        Arrays.asList(parameterSpec));

    return parameterSpecs;
  }

  /**
   * Constructs the parameter specifications for a basic setter.
   * 
   * @param fieldName The field name.
   * @param fieldType The field type.
   * 
   * @return The parameter specifications for a basic setter.
   */
  protected Iterable<ParameterSpec> constructBasicSetterParameters(
      String fieldName,
      TypeName fieldType) {
    Modifier[] parameterModifiers = new Modifier[] {
        Modifier.FINAL
    };

    ParameterSpec parameterSpec = ParameterSpec.builder(
        fieldType,
        fieldName,
        parameterModifiers)
        .build();

    Iterable<ParameterSpec> parameterSpecs = new ArrayList<ParameterSpec>(
        Arrays.asList(parameterSpec));

    return parameterSpecs;
  }

  /**
   * Constructs the JavaDoc for a basic setter.
   * 
   * @param fieldName The field name.
   * 
   * @return The JavaDoc for a basic setter.
   */
  protected CodeBlock constructBasicSetterJavadoc(String fieldName) {
    CodeBlock javaDoc = CodeBlock.builder()
        .add("Sets the $L.\n", fieldName)
        .build();
    return javaDoc;
  }

  /**
   * Constructs the method body for a basic getter.
   * 
   * @param fieldName
   * @return
   */
  protected CodeBlock.Builder constructBasicGetterMethodBodyBuilder(String fieldName) {
    return CodeBlock.builder()
        .addStatement(
            "return this.$L",
            fieldName);
  }

  /**
   * Constructs the javadoc for a basic getter.
   * 
   * @param fieldName The field name.
   * 
   * @return The code block for a basic getter.
   */
  protected CodeBlock constructBasicGetterJavadoc(String fieldName) {
    CodeBlock javaDoc = CodeBlock.builder()
        .add("Gets the $L.\n", fieldName)
        .add("@return The $L.\n", fieldName)
        .build();

    return javaDoc;
  }
}

