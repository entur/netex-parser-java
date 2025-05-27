package org.entur.netex;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rutebanken.netex.model.Line;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.StopPlace;

public class IntrospectionDemo {

  // Track processed classes to avoid infinite recursion with circular references
  private static Set<Class<?>> processedClasses = new HashSet<>();

  // Store generated schemas to avoid duplicating them
  private static Map<Class<?>, String> schemaCache = new HashMap<>();

  /**
   * Retrieves all fields from a class and its superclasses, up to (but not including) Object.class.
   */
  public static List<Field> getAllFields(Class<?> clazz) {
    List<Field> fields = new ArrayList<>();
    Class<?> currentClass = clazz;
    while (currentClass != null && currentClass != Object.class) {
      fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
      currentClass = currentClass.getSuperclass();
    }
    return fields;
  }

  /**
   * Gets the field name to use in the Avro schema, checking for XML annotations.
   * Prioritizes @XmlAttribute name if present, otherwise uses the field's name.
   */
  private static String getFieldNameForAvro(Field field) {
    // Check for XmlAttribute annotation
    XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);
    if (xmlAttribute != null && !xmlAttribute.name().isEmpty()) {
      return xmlAttribute.name();
    }

    // Check for XmlElement annotation as a fallback
    XmlElement xmlElement = field.getAnnotation(XmlElement.class);
    if (
      xmlElement != null &&
      !xmlElement.name().isEmpty() &&
      !xmlElement.name().equals("##default")
    ) {
      return xmlElement.name();
    }

    // Default to the field's name
    return field.getName();
  }

  /**
   * Maps a Java Field to its corresponding Avro type JSON string.
   * Makes fields nullable by default.
   * Handles complex types by creating sub-records.
   */
  private static String getAvroTypeJson(
    Field field,
    Set<Class<?>> nestedTypesToProcess
  ) {
    Class<?> type = field.getType();

    // Handle JAXBElement list fields with XmlElementRefs annotation
    if (
      List.class.isAssignableFrom(type) &&
      field.getGenericType() instanceof ParameterizedType
    ) {
      ParameterizedType pType = (ParameterizedType) field.getGenericType();
      Type[] actualTypeArguments = pType.getActualTypeArguments();

      if (
        actualTypeArguments.length > 0 &&
        actualTypeArguments[0] instanceof Class &&
        JAXBElement.class.isAssignableFrom((Class<?>) actualTypeArguments[0])
      ) {
        XmlElementRefs xmlElementRefs = field.getAnnotation(
          XmlElementRefs.class
        );
        if (xmlElementRefs != null) {
          // Return null to signal that this field should be handled specially
          // in the generateAvroSchemaString method
          return null;
        }
      }
    }

    // Handle JAXBElement<?> fields with @XmlElementRef
    if (JAXBElement.class.isAssignableFrom(type)) {
      XmlElementRef xmlElementRef = field.getAnnotation(XmlElementRef.class);
      if (xmlElementRef != null) {
        // Return null to signal that this field should be handled specially
        // in the generateAvroSchemaString method
        return null;
      }
    }

    // Handle primitive types
    if (type == String.class) {
      return "\"string\"";
    } else if (type == Boolean.class || type == boolean.class) {
      return "\"boolean\"";
    } else if (type == Integer.class || type == int.class) {
      return "\"int\"";
    } else if (type == Long.class || type == long.class) {
      return "\"long\"";
    } else if (type == Float.class || type == float.class) {
      return "\"float\"";
    } else if (type == Double.class || type == double.class) {
      return "\"double\"";
    }

    // Handle List types
    if (List.class.isAssignableFrom(type)) {
      Type genericType = field.getGenericType();
      if (genericType instanceof ParameterizedType) {
        ParameterizedType pType = (ParameterizedType) genericType;
        Type[] actualTypeArguments = pType.getActualTypeArguments();

        if (actualTypeArguments.length > 0) {
          Type itemType = actualTypeArguments[0];
          if (itemType == String.class) {
            return "{\"type\": \"array\", \"items\": \"string\"}";
          } else if (itemType instanceof Class) {
            Class<?> itemClass = (Class<?>) itemType;
            nestedTypesToProcess.add(itemClass);
            return (
              "{\"type\": \"array\", \"items\": \"" +
              itemClass.getSimpleName() +
              "\"}"
            );
          }
        }
      }
      // Default to array of strings if we can't determine the item type
      return "{\"type\": \"array\", \"items\": \"string\"}";
    }

    // Handle Map types
    if (Map.class.isAssignableFrom(type)) {
      Type genericType = field.getGenericType();
      if (genericType instanceof ParameterizedType) {
        ParameterizedType pType = (ParameterizedType) genericType;
        Type[] actualTypeArguments = pType.getActualTypeArguments();

        if (
          actualTypeArguments.length > 1 &&
          actualTypeArguments[0] == String.class
        ) {
          Type valueType = actualTypeArguments[1];
          if (valueType == String.class) {
            return "{\"type\": \"map\", \"values\": \"string\"}";
          } else if (valueType instanceof Class) {
            Class<?> valueClass = (Class<?>) valueType;
            nestedTypesToProcess.add(valueClass);
            return (
              "{\"type\": \"map\", \"values\": \"" +
              valueClass.getSimpleName() +
              "\"}"
            );
          }
        }
      }
      // Default to map of strings if we can't determine the value type
      return "{\"type\": \"map\", \"values\": \"string\"}";
    }

    // Handle Enum types
    if (type.isEnum()) {
      StringBuilder enumBuilder = new StringBuilder();
      enumBuilder.append(
        "{\"type\": \"enum\", \"name\": \"" +
        type.getSimpleName() +
        "\", \"symbols\": ["
      );

      Object[] enumConstants = type.getEnumConstants();
      for (int i = 0; i < enumConstants.length; i++) {
        enumBuilder.append("\"" + enumConstants[i] + "\"");
        if (i < enumConstants.length - 1) {
          enumBuilder.append(", ");
        }
      }

      enumBuilder.append("]}\n");
      return enumBuilder.toString();
    }

    // Handle complex types (classes)
    if (!type.isPrimitive() && !type.isArray()) {
      // Skip certain Java types that we can't handle
      if (type.getName().startsWith("java.")) {
        System.err.println(
          "Warning: Unhandled Java type '" +
          type.getName() +
          "' for field '" +
          field.getName() +
          "'. Skipping."
        );
        return "SKIP";
      }

      nestedTypesToProcess.add(type);
      return "\"" + type.getSimpleName() + "\"";
    }

    // Skip array types for now
    if (type.isArray()) {
      System.err.println(
        "Warning: Array type '" +
        type.getName() +
        "' for field '" +
        field.getName() +
        "' is not supported. Skipping."
      );
      return "SKIP";
    }

    // Default case
    System.err.println(
      "Warning: Unsupported type '" +
      type.getName() +
      "' for field '" +
      field.getName() +
      "'. Skipping."
    );
    return "SKIP";
  }

  /**
   * Generates an Avro schema JSON string for the given class.
   */
  public static String generateAvroSchemaString(
    Class<?> clazz,
    String namespace,
    String doc
  ) {
    if (schemaCache.containsKey(clazz)) {
      return schemaCache.get(clazz);
    }

    // Store an empty schema in the cache to avoid infinite recursion
    schemaCache.put(clazz, "");

    StringBuilder schemaBuilder = new StringBuilder();
    schemaBuilder.append("{");
    schemaBuilder.append("\n  \"type\": \"record\",");
    schemaBuilder.append("\n  \"name\": \"" + clazz.getSimpleName() + "\",");
    schemaBuilder.append("\n  \"namespace\": \"" + namespace + "\",");
    schemaBuilder.append("\n  \"doc\": \"" + doc + "\",");
    schemaBuilder.append("\n  \"fields\": [");

    List<String> fieldJsons = new ArrayList<>();
    Set<Class<?>> nestedTypesToProcess = new HashSet<>();

    // Get all fields from the class and its superclasses
    List<Field> allFields = getAllFields(clazz);

    for (Field field : allFields) {
      // Skip static and transient fields
      if (
        Modifier.isStatic(field.getModifiers()) ||
        Modifier.isTransient(field.getModifiers())
      ) {
        continue;
      }

      // Get the field name, respecting XML annotations
      String fieldName = getFieldNameForAvro(field);
      if (fieldName == null) {
        continue; // Skip fields that should be ignored
      }

      // Special handling for List<JAXBElement<?>> fields with @XmlElementRefs
      if (
        List.class.isAssignableFrom(field.getType()) &&
        field.getGenericType() instanceof ParameterizedType
      ) {
        ParameterizedType pType = (ParameterizedType) field.getGenericType();
        Type[] actualTypeArguments = pType.getActualTypeArguments();

        if (
          actualTypeArguments.length > 0 &&
          actualTypeArguments[0] instanceof ParameterizedType &&
          ((ParameterizedType) actualTypeArguments[0]).getRawType() ==
          JAXBElement.class
        ) {
          XmlElementRefs xmlElementRefs = field.getAnnotation(
            XmlElementRefs.class
          );
          if (xmlElementRefs != null) {
            // Create separate fields for each XmlElementRef
            for (XmlElementRef ref : xmlElementRefs.value()) {
              String refName = ref.name();
              // Convert to camelCase if needed (e.g., QuayRef -> quayRef)
              String camelCaseRefName =
                refName.substring(0, 1).toLowerCase() + refName.substring(1);

              // Get the referenced type
              Class<?> refType = ref.type();
              if (refType == JAXBElement.class) {
                // Try to determine the actual type from the name
                String typeName = refName;
                if (refName.endsWith("Ref")) {
                  typeName += "Structure";
                }

                try {
                  // Try to find the class in the same package as the current class
                  String packageName = clazz.getPackage().getName();
                  Class<?> actualType = Class.forName(
                    packageName + "." + typeName
                  );

                  // Check if the type is abstract and has @XmlSeeAlso annotation
                  if (Modifier.isAbstract(actualType.getModifiers())) {
                    XmlSeeAlso xmlSeeAlso = actualType.getAnnotation(
                      XmlSeeAlso.class
                    );
                    if (xmlSeeAlso != null && xmlSeeAlso.value().length > 0) {
                      // Use the first concrete implementation
                      for (Class<?> concreteType : xmlSeeAlso.value()) {
                        if (!Modifier.isAbstract(concreteType.getModifiers())) {
                          actualType = concreteType;
                          break;
                        }
                      }
                    }
                  }

                  nestedTypesToProcess.add(actualType);

                  // Add a field for this reference type
                  String fieldJson =
                    "\n    {" +
                    "\n      \"name\": \"" +
                    camelCaseRefName +
                    "\"," +
                    "\n      \"type\": [\"null\", {\"type\": \"array\", \"items\": \"" +
                    actualType.getSimpleName() +
                    "\"}]," +
                    "\n      \"default\": null" +
                    "\n    }";
                  fieldJsons.add(fieldJson);
                } catch (ClassNotFoundException e) {
                  // Fallback to a string field
                  String fieldJson =
                    "\n    {" +
                    "\n      \"name\": \"" +
                    camelCaseRefName +
                    "\"," +
                    "\n      \"type\": [\"null\", {\"type\": \"array\", \"items\": \"string\"}]," +
                    "\n      \"default\": null" +
                    "\n    }";
                  fieldJsons.add(fieldJson);
                }
              }
            }
            continue; // Skip the original field
          }
        } else if (
          actualTypeArguments.length > 0 &&
          actualTypeArguments[0] instanceof Class &&
          JAXBElement.class.isAssignableFrom((Class<?>) actualTypeArguments[0])
        ) {
          XmlElementRefs xmlElementRefs = field.getAnnotation(
            XmlElementRefs.class
          );
          if (xmlElementRefs != null) {
            // Create separate fields for each XmlElementRef
            for (XmlElementRef ref : xmlElementRefs.value()) {
              String refName = ref.name();
              // Convert to camelCase if needed (e.g., QuayRef -> quayRef)
              String camelCaseRefName =
                refName.substring(0, 1).toLowerCase() + refName.substring(1);

              // Get the referenced type
              Class<?> refType = ref.type();
              if (refType == JAXBElement.class) {
                // Try to determine the actual type from the name
                String typeName = refName;
                if (refName.endsWith("Ref")) {
                  typeName += "Structure";
                }

                try {
                  // Try to find the class in the same package as the current class
                  String packageName = clazz.getPackage().getName();
                  Class<?> actualType = Class.forName(
                    packageName + "." + typeName
                  );

                  // Check if the type is abstract and has @XmlSeeAlso annotation
                  if (Modifier.isAbstract(actualType.getModifiers())) {
                    XmlSeeAlso xmlSeeAlso = actualType.getAnnotation(
                      XmlSeeAlso.class
                    );
                    if (xmlSeeAlso != null && xmlSeeAlso.value().length > 0) {
                      // Use the first concrete implementation
                      for (Class<?> concreteType : xmlSeeAlso.value()) {
                        if (!Modifier.isAbstract(concreteType.getModifiers())) {
                          actualType = concreteType;
                          break;
                        }
                      }
                    }
                  }

                  nestedTypesToProcess.add(actualType);

                  // Add a field for this reference type
                  String fieldJson =
                    "\n    {" +
                    "\n      \"name\": \"" +
                    camelCaseRefName +
                    "\"," +
                    "\n      \"type\": [\"null\", {\"type\": \"array\", \"items\": \"" +
                    actualType.getSimpleName() +
                    "\"}]," +
                    "\n      \"default\": null" +
                    "\n    }";
                  fieldJsons.add(fieldJson);
                } catch (ClassNotFoundException e) {
                  // Fallback to a string field
                  String fieldJson =
                    "\n    {" +
                    "\n      \"name\": \"" +
                    camelCaseRefName +
                    "\"," +
                    "\n      \"type\": [\"null\", {\"type\": \"array\", \"items\": \"string\"}]," +
                    "\n      \"default\": null" +
                    "\n    }";
                  fieldJsons.add(fieldJson);
                }
              }
            }
            continue; // Skip the original field
          }
        }
      }

      // Handle JAXBElement<?> fields with @XmlElementRef
      if (JAXBElement.class.isAssignableFrom(field.getType())) {
        XmlElementRef xmlElementRef = field.getAnnotation(XmlElementRef.class);
        if (xmlElementRef != null) {
          // Try to extract the actual type from the generic parameter
          if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType pType =
              (ParameterizedType) field.getGenericType();
            Type[] actualTypeArguments = pType.getActualTypeArguments();

            if (actualTypeArguments.length > 0) {
              Type typeArg = actualTypeArguments[0];

              // Handle wildcards with extends bound
              if (typeArg instanceof WildcardType) {
                WildcardType wildcardType = (WildcardType) typeArg;
                Type[] upperBounds = wildcardType.getUpperBounds();

                if (upperBounds.length > 0 && upperBounds[0] instanceof Class) {
                  Class<?> actualType = (Class<?>) upperBounds[0];

                  // Check if the type is abstract and has @XmlSeeAlso annotation
                  if (Modifier.isAbstract(actualType.getModifiers())) {
                    XmlSeeAlso xmlSeeAlso = actualType.getAnnotation(
                      XmlSeeAlso.class
                    );
                    if (xmlSeeAlso != null && xmlSeeAlso.value().length > 0) {
                      // Add all concrete implementations to nestedTypesToProcess
                      for (Class<?> concreteType : xmlSeeAlso.value()) {
                        if (!Modifier.isAbstract(concreteType.getModifiers())) {
                          nestedTypesToProcess.add(concreteType);
                        }
                      }

                      // Use the first concrete implementation for the field type
                      for (Class<?> concreteType : xmlSeeAlso.value()) {
                        if (!Modifier.isAbstract(concreteType.getModifiers())) {
                          actualType = concreteType;
                          break;
                        }
                      }
                    }
                  }

                  nestedTypesToProcess.add(actualType);

                  // Add a field for this reference type
                  String fieldJson =
                    "\n    {" +
                    "\n      \"name\": \"" +
                    fieldName +
                    "\"," +
                    "\n      \"type\": [\"null\", \"" +
                    actualType.getSimpleName() +
                    "\"]," +
                    "\n      \"default\": null" +
                    "\n    }";
                  fieldJsons.add(fieldJson);
                  continue;
                }
              }
              // Handle direct class reference
              else if (typeArg instanceof Class) {
                Class<?> actualType = (Class<?>) typeArg;

                // Check if the type is abstract and has @XmlSeeAlso annotation
                if (Modifier.isAbstract(actualType.getModifiers())) {
                  XmlSeeAlso xmlSeeAlso = actualType.getAnnotation(
                    XmlSeeAlso.class
                  );
                  if (xmlSeeAlso != null && xmlSeeAlso.value().length > 0) {
                    // Add all concrete implementations to nestedTypesToProcess
                    for (Class<?> concreteType : xmlSeeAlso.value()) {
                      if (!Modifier.isAbstract(concreteType.getModifiers())) {
                        nestedTypesToProcess.add(concreteType);
                      }
                    }

                    // Use the first concrete implementation for the field type
                    for (Class<?> concreteType : xmlSeeAlso.value()) {
                      if (!Modifier.isAbstract(concreteType.getModifiers())) {
                        actualType = concreteType;
                        break;
                      }
                    }
                  }
                }

                nestedTypesToProcess.add(actualType);

                // Add a field for this reference type
                String fieldJson =
                  "\n    {" +
                  "\n      \"name\": \"" +
                  fieldName +
                  "\"," +
                  "\n      \"type\": [\"null\", \"" +
                  actualType.getSimpleName() +
                  "\"]," +
                  "\n      \"default\": null" +
                  "\n    }";
                fieldJsons.add(fieldJson);
                continue;
              }
            }
          }

          // Fallback: Try to determine the actual type from the XML element name
          String refName = xmlElementRef.name();
          String typeName = refName;
          if (refName.endsWith("Ref")) {
            typeName += "Structure";
          } else if (!refName.endsWith("Type")) {
            typeName += "Type";
          }

          try {
            // Try to find the class in the same package as the field's declaring class
            String packageName = field
              .getDeclaringClass()
              .getPackage()
              .getName();
            Class<?> actualType = Class.forName(packageName + "." + typeName);

            // Check if the type is abstract and has @XmlSeeAlso annotation
            if (Modifier.isAbstract(actualType.getModifiers())) {
              XmlSeeAlso xmlSeeAlso = actualType.getAnnotation(
                XmlSeeAlso.class
              );
              if (xmlSeeAlso != null && xmlSeeAlso.value().length > 0) {
                // Add all concrete implementations to nestedTypesToProcess
                for (Class<?> concreteType : xmlSeeAlso.value()) {
                  if (!Modifier.isAbstract(concreteType.getModifiers())) {
                    nestedTypesToProcess.add(concreteType);
                  }
                }

                // Use the first concrete implementation for the field type
                for (Class<?> concreteType : xmlSeeAlso.value()) {
                  if (!Modifier.isAbstract(concreteType.getModifiers())) {
                    actualType = concreteType;
                    break;
                  }
                }
              }
            }

            nestedTypesToProcess.add(actualType);

            // Add a field for this reference type
            String fieldJson =
              "\n    {" +
              "\n      \"name\": \"" +
              fieldName +
              "\"," +
              "\n      \"type\": [\"null\", \"" +
              actualType.getSimpleName() +
              "\"]," +
              "\n      \"default\": null" +
              "\n    }";
            fieldJsons.add(fieldJson);
            continue;
          } catch (ClassNotFoundException e) {
            // Try in the net.opengis.gml._3 package if it's a GML type
            try {
              Class<?> actualType = Class.forName(
                "net.opengis.gml._3." + typeName
              );

              // Check if the type is abstract and has @XmlSeeAlso annotation
              if (Modifier.isAbstract(actualType.getModifiers())) {
                XmlSeeAlso xmlSeeAlso = actualType.getAnnotation(
                  XmlSeeAlso.class
                );
                if (xmlSeeAlso != null && xmlSeeAlso.value().length > 0) {
                  // Add all concrete implementations to nestedTypesToProcess
                  for (Class<?> concreteType : xmlSeeAlso.value()) {
                    if (!Modifier.isAbstract(concreteType.getModifiers())) {
                      nestedTypesToProcess.add(concreteType);
                    }
                  }

                  // Use the first concrete implementation for the field type
                  for (Class<?> concreteType : xmlSeeAlso.value()) {
                    if (!Modifier.isAbstract(concreteType.getModifiers())) {
                      actualType = concreteType;
                      break;
                    }
                  }
                }
              }

              nestedTypesToProcess.add(actualType);

              // Add a field for this reference type
              String fieldJson =
                "\n    {" +
                "\n      \"name\": \"" +
                fieldName +
                "\"," +
                "\n      \"type\": [\"null\", \"" +
                actualType.getSimpleName() +
                "\"]," +
                "\n      \"default\": null" +
                "\n    }";
              fieldJsons.add(fieldJson);
              continue;
            } catch (ClassNotFoundException e2) {
              System.err.println(
                "Warning: Could not find class for " +
                typeName +
                ". Using string type instead."
              );

              // Fallback to a string field
              String fieldJson =
                "\n    {" +
                "\n      \"name\": \"" +
                fieldName +
                "\"," +
                "\n      \"type\": [\"null\", \"string\"]," +
                "\n      \"default\": null" +
                "\n    }";
              fieldJsons.add(fieldJson);
              continue;
            }
          }
        }
      }

      // Get the Avro type for this field
      String typeJson = getAvroTypeJson(field, nestedTypesToProcess);
      if (typeJson == null) {
        // This is a field that should be handled specially (e.g., JAXBElement)
        continue;
      }

      if (typeJson.equals("SKIP")) {
        // This is a field that should be skipped
        continue;
      }

      // Add the field to the schema
      String fieldJson =
        "\n    {" +
        "\n      \"name\": \"" +
        fieldName +
        "\"," +
        "\n      \"type\": [\"null\", " +
        typeJson +
        "]," +
        "\n      \"default\": null" +
        "\n    }";
      fieldJsons.add(fieldJson);
    }

    // Add the fields to the schema
    for (int i = 0; i < fieldJsons.size(); i++) {
      schemaBuilder.append(fieldJsons.get(i));
      if (i < fieldJsons.size() - 1) {
        schemaBuilder.append(",");
      }
    }

    schemaBuilder.append("\n  ]");
    schemaBuilder.append("\n}");

    String schema = schemaBuilder.toString();
    schemaCache.put(clazz, schema);

    // Process nested types
    for (Class<?> nestedType : nestedTypesToProcess) {
      if (
        !schemaCache.containsKey(nestedType) ||
        schemaCache.get(nestedType).isEmpty()
      ) {
        generateAvroSchemaString(
          nestedType,
          namespace,
          "Nested type for " + clazz.getSimpleName()
        );
      }
    }

    return schema;
  }

  /**
   * Get all schemas that were generated, including nested types
   */
  public static List<String> getAllGeneratedSchemas() {
    return new ArrayList<>(schemaCache.values());
  }

  public static void main(String[] args) {
    try {
      // Import the necessary classes
      Class.forName("jakarta.xml.bind.annotation.XmlSeeAlso");

      // Create directory for schema files
      File schemaDir = new File("target/avro-schemas");
      if (!schemaDir.exists()) {
        schemaDir.mkdirs();
      }

      // Generate Avro schema for StopPlace
      System.out.println("\nGenerating Avro schema for StopPlace...");
      String avroSchema = generateAvroSchemaString(
        ServiceJourney.class,
        "org.entur.netex",
        "Avro schema for StopPlace"
      );

      // Write all generated schemas to files
      System.out.println(
        "\nWriting schema files to " + schemaDir.getAbsolutePath() + ":"
      );
      for (Map.Entry<Class<?>, String> entry : schemaCache.entrySet()) {
        String className = entry.getKey().getSimpleName();
        String schemaContent = entry.getValue();

        if (schemaContent != null && !schemaContent.isEmpty()) {
          File schemaFile = new File(schemaDir, className + ".avsc");
          try (FileWriter writer = new FileWriter(schemaFile)) {
            writer.write(schemaContent);
          }

          System.out.println("  - " + schemaFile.getName());
        }
      }

      System.out.println(
        "\nSchema generation complete. " +
        schemaCache.size() +
        " schema files created."
      );
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
