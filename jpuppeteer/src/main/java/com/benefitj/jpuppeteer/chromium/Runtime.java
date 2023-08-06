package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * 运行时
 */
public interface Runtime {

  /**
   * Represents function call argument. Either remote object id objectId, primitive value, unserializable primitive value or neither of (for undefined) them should be specified.
   */
  @Data
  public class CallArgument {
    /**
     * Primitive value or serializable javascript object.
     */
    JSONObject value;
    /**
     * Primitive value which can not be JSON-stringified.
     */
    String unserializableValue;
    /**
     * Remote object handle.
     */
    String objectId;

  }

  /**
   * Stack entry for runtime errors and assertions.
   */
  @Data
  public class CallFrame {
    /**
     * JavaScript function name.
     */
    String functionName;
    /**
     * JavaScript script id.
     */
    String scriptId;
    /**
     * JavaScript script name or url.
     */
    String url;
    /**
     * JavaScript script line number (0-based).
     */
    Integer lineNumber;
    /**
     * JavaScript script column number (0-based).
     */
    Integer columnNumber;
  }

  /**
   * Represents deep serialized value.
   */
  @Data
  public class DeepSerializedValue {
    /**
     * Allowed Values: undefined, null, string, number, boolean, bigint, regexp, date, symbol, array, object, function, map, set, weakmap, weakset, error, proxy, promise, typedarray, arraybuffer, node, window
     */
    String type;
    JSONObject value;
    String objectId;
    /**
     * Set if value reference met more then once during serialization. In such case, value is provided only to one of the serialized values. Unique per value in the scope of one CDP call.
     */
    Integer weakLocalObjectReference;
  }

  /**
   * Detailed information about exception (or error) that was thrown during script compilation or execution.
   */
  @Data
  public class ExceptionDetails {
    /**
     * Exception id.
     */
    Integer exceptionId;
    /**
     * Line number of the exception location (0-based).
     */
    String text;
    /**
     * Line number of the exception location (0-based).
     */
    Integer lineNumber;
    /**
     * Column number of the exception location (0-based).
     */
    Integer columnNumber;
    /**
     * Script ID of the exception location.
     */
    String scriptId;
    /**
     * URL of the exception location, to be used when the script was not reported.
     */
    String url;
    /**
     * JavaScript stack trace if available.
     */
    StackTrace stackTrace;
    /**
     * Exception object if available.
     */
    RemoteObject exception;
    /**
     * Identifier of the context where exception happened.
     */
    Integer Integer;
    /**
     * Dictionary with entries of meta data that the client associated with this exception, such as information about associated network requests, etc. EXPERIMENTAL
     */
    JSONObject exceptionMetaData;
  }

  /**
   * Description of an isolated world.
   */
  @Data
  public class ExecutionContextDescription {
    /**
     * Unique id of the execution context. It can be used to specify in which execution context script evaluation should be performed.
     */
    Integer id;
    /**
     * Execution context origin.
     */
    String origin;
    /**
     * Human readable name describing given context.
     */
    String name;
    /**
     * A system-unique execution context identifier. Unlike the id, this is unique across multiple processes, so can be reliably used to identify specific context while backend performs a cross-process navigation. EXPERIMENTAL
     */
    String uniqueId;
    /**
     * Embedder-specific auxiliary data likely matching {isDefault: boolean, type: 'default'|'isolated'|'worker', frameId: string}
     */
    JSONObject auxData;
  }

  /**
   * Object internal property descriptor. This property isn't normally visible in JavaScript code.
   */
  @Data
  public class InternalPropertyDescriptor {
    /**
     * Conventional property name.
     */
    String name;
    /**
     * The value associated with the property.
     */
    RemoteObject value;
  }

  /**
   * Object property descriptor.
   */
  @Data
  public class PropertyDescriptor {
    /**
     * Property name or symbol description.
     */
    String name;
    /**
     * The value associated with the property.
     */
    RemoteObject value;
    /**
     * True if the value associated with the property may be changed (data descriptors only).
     */
    boolean writable;
    /**
     * A function which serves as a getter for the property, or undefined if there is no getter (accessor descriptors only).
     */
    RemoteObject get;
    /**
     * A function which serves as a setter for the property, or undefined if there is no setter (accessor descriptors only).
     */
    RemoteObject set;
    /**
     * True if the type of this property descriptor may be changed and if the property may be deleted from the corresponding object.
     */
    boolean configurable;
    /**
     * True if this property shows up during enumeration of the properties on the corresponding object.
     */
    boolean enumerable;
    /**
     * True if the result was thrown during the evaluation.
     */
    boolean wasThrown;
    /**
     * True if the property is owned for the object.
     */
    boolean isOwn;
    /**
     * Property symbol object, if the property is of the symbol type.
     */
    RemoteObject symbol;
  }

  /**
   * Mirror object referencing original JavaScript object.
   */
  @Data
  public class RemoteObject {
    /**
     * Object type.
     * Allowed Values: object, function, undefined, string, number, boolean, symbol, bigint
     */
    String type;
    /**
     * Object subtype hint. Specified for object type values only. NOTE: If you change anything here, make sure to also update subtype in ObjectPreview and PropertyPreview below.
     * Allowed Values: array, null, node, regexp, date, map, set, weakmap, weakset, iterator, generator, error, proxy, promise, typedarray, arraybuffer, dataview, webassemblymemory, wasmvalue
     */
    String subtype;
    /**
     * Object class (constructor) name. Specified for object type values only.
     */
    String className;
    /**
     * Remote object value in case of primitive values or JSON values (if it was requested).
     */
    JSONObject value;
    /**
     * Primitive value which can not be JSON-stringified does not have value, but gets this property.
     */
    String unserializableValue;
    /**
     * String representation of the object.
     */
    String description;
    /**
     * Deprecated. Use deepSerializedValue instead. WebDriver BiDi representation of the value. DEPRECATED
     */
    DeepSerializedValue webDriverValue;
    /**
     * Deep serialized value. EXPERIMENTAL
     */
    DeepSerializedValue deepSerializedValue;
    /**
     * Unique object identifier (for non-primitive values).
     */
    String objectId;
    /**
     * Preview containing abbreviated property values. Specified for object type values only. EXPERIMENTAL
     */
    ObjectPreview preview;
    /**
     *
     */
    CustomPreview customPreview;
  }

  /**
   * Represents options for serialization. Overrides generatePreview, returnByValue and generateWebDriverValue.
   */
  @Data
  public class SerializationOptions {
    /**
     * Allowed Values: deep, json, idOnly
     */
    String serialization;
    /**
     * Deep serialization depth. Default is full depth. Respected only in deep serialization mode.
     */
    Integer maxDepth;
    /**
     * Embedder-specific parameters. For example if connected to V8 in Chrome these control DOM serialization via maxNodeDepth: integer and includeShadowTree: "none" | "open" | "all". Values can be only of type string or integer.
     */
    JSONObject additionalParameters;
  }

  /**
   * Call frames for assertions or error messages.
   */
  @Data
  public class StackTrace {
    /**
     * String label of this stack trace. For async traces this may be a name of the function that initiated the async call.
     */
    String description;
    /**
     * JavaScript function name.
     */
    List<CallFrame> callFrames;
    /**
     * Asynchronous JavaScript stack trace that preceded this stack, if available.
     */
    StackTrace parent;
    /**
     * Asynchronous JavaScript stack trace that preceded this stack, if available.
     */
    StackTraceId parentId;
  }

  /**
   * If debuggerId is set stack trace comes from another debugger and can be resolved there. This allows to track cross-debugger calls. See Runtime.StackTrace and Debugger.paused for usages.
   */
  @Data
  public class StackTraceId {
    String id;
    String debuggerId;
  }

  /**
   *
   */
  @Data
  public class CustomPreview {
    /**
     * The JSON-stringified result of formatter.header(object, config) call. It contains json ML array that represents RemoteObject.
     */
    String header;
    /**
     * If formatter returns true as a result of formatter.hasBody call then bodyGetterId will contain RemoteObjectId for the function that returns result of formatter.body(object, config) call. The result value is json ML array.
     */
    String bodyGetterId;
  }

  /**
   *
   */
  @Data
  public class EntryPreview {
    /**
     * Preview of the key. Specified for map-like collection entries.
     */
    ObjectPreview key;
    /**
     * Preview of the value.
     */
    ObjectPreview value;
  }

  /**
   * Object containing abbreviated remote object value.
   */
  @Data
  public class ObjectPreview {
    /**
     * Object type.
     * Allowed Values: object, function, undefined, string, number, boolean, symbol, bigint
     */
    String type;
    /**
     * Object subtype hint. Specified for object type values only.
     * Allowed Values: array, null, node, regexp, date, map, set, weakmap, weakset, iterator, generator, error, proxy, promise, typedarray, arraybuffer, dataview, webassemblymemory, wasmvalue
     */
    String subtype;
    /**
     * String representation of the object.
     */
    String description;
    /**
     * True iff some of the properties or entries of the original object did not fit.
     */
    boolean overflow;
    /**
     * List of the properties.
     */
    List<PropertyPreview> properties;
    /**
     * List of the entries. Specified for map and set subtype values only.
     */
    List<EntryPreview> entries;
  }

  /**
   * Object private field descriptor.
   */
  @Data
  public class PrivatePropertyDescriptor {
    /**
     * Private property name.
     */
    String name;
    /**
     * The value associated with the private property.
     */
    RemoteObject value;
    /**
     * A function which serves as a getter for the private property, or undefined if there is no getter (accessor descriptors only).
     */
    RemoteObject get;
    /**
     * A function which serves as a setter for the private property, or undefined if there is no setter (accessor descriptors only).
     */
    RemoteObject set;
  }

  /**
   *
   */
  @Data
  public class PropertyPreview {
    /**
     * Property name.
     */
    String name;
    /**
     * Object type. Accessor means that the property itself is an accessor property.
     * Allowed Values: object, function, undefined, string, number, boolean, symbol, accessor, bigint
     */
    String type;
    /**
     * User-friendly property value string.
     */
    String value;
    /**
     * Nested value preview.
     */
    ObjectPreview valuePreview;
    /**
     * Object subtype hint. Specified for object type values only.
     * Allowed Values: array, null, node, regexp, date, map, set, weakmap, weakset, iterator, generator, error, proxy, promise, typedarray, arraybuffer, dataview, webassemblymemory, wasmvalue
     */
    String subtype;
  }

}
