package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.List;

/**
 * Runtime domain exposes JavaScript runtime by means of remote evaluation and mirror objects. Evaluation results are returned as
 * mirror object that expose object type, string representation and unique identifier that can be used for further object reference.
 * Original objects are maintained in memory unless they are either explicitly released or are released along with the other objects in their object group.
 */
@ChromiumApi("Runtime")
public interface Runtime {

  /**
   * Add handler to promise with given promise object id.
   *
   * @param promiseObjectId RemoteObjectId
   *                        Identifier of the promise.
   * @param returnByValue   boolean
   *                        Whether the result is expected to be a JSON object that should be sent by value.
   * @param generatePreview boolean
   *                        Whether preview should be generated for the result.
   * @return {
   * result: RemoteObject  Promise result. Will contain rejected value if promise was rejected.
   * exceptionDetails: ExceptionDetails  Exception details if stack strace is available.
   * }
   */
  JSONObject awaitPromise(String promiseObjectId, Boolean returnByValue, Boolean generatePreview);

  /**
   * Calls function with given declaration on the given object. Object group of the result is inherited from the target object.
   *
   * @param functionDeclaration    string
   *                               Declaration of the function to call.
   * @param objectId               RemoteObjectId
   *                               Identifier of the object to call function on. Either objectId or executionContextId should be specified.
   * @param arguments              array[ CallArgument ]
   *                               Call arguments. All call arguments must belong to the same JavaScript world as the target object.
   * @param silent                 boolean
   *                               In silent mode exceptions thrown during evaluation are not reported and do not pause execution. Overrides setPauseOnException state.
   * @param returnByValue          boolean
   *                               Whether the result is expected to be a JSON object which should be sent by value. Can be overriden by serializationOptions.
   * @param generatePreview        boolean
   *                               Whether preview should be generated for the result. EXPERIMENTAL
   * @param userGesture            boolean
   *                               Whether execution should be treated as initiated by user in the UI.
   * @param awaitPromise           boolean
   *                               Whether execution should await for resulting value and return once awaited promise is resolved.
   * @param executionContextId     ExecutionContextId
   *                               Specifies execution context which global object will be used to call function on. Either executionContextId or objectId should be specified.
   * @param objectGroup            string
   *                               Symbolic group name that can be used to release multiple objects. If objectGroup is not specified and objectId is, objectGroup will be inherited from object.
   * @param throwOnSideEffect      boolean
   *                               Whether to throw an exception if side effect cannot be ruled out during evaluation. EXPERIMENTAL
   * @param uniqueContextId        string
   *                               An alternative way to specify the execution context to call function on. Compared to contextId that may be reused across processes, this is guaranteed to be system-unique, so it can be used to prevent accidental function call in context different than intended (e.g. as a result of navigation across process boundaries). This is mutually exclusive with executionContextId. EXPERIMENTAL
   * @param generateWebDriverValue boolean
   *                               Deprecated. Use serializationOptions: {serialization:"deep"} instead. Whether the result should contain webDriverValue, serialized according to https://w3c.github.io/webdriver-bidi. This is mutually exclusive with returnByValue, but resulting objectId is still provided. DEPRECATED
   * @param serializationOptions   SerializationOptions
   *                               Specifies the result serialization. If provided, overrides generatePreview, returnByValue and generateWebDriverValue.
   * @return {
   * result: RemoteObject Call result.
   * exceptionDetails: ExceptionDetails Exception details.
   * }
   */
  JSONObject callFunctionOn(String functionDeclaration, String objectId, List<CallArgument> arguments, Boolean silent,
                            Boolean returnByValue, Boolean generatePreview, Boolean userGesture, Boolean awaitPromise,
                            String executionContextId, String objectGroup, Boolean throwOnSideEffect, String uniqueContextId,
                            Boolean generateWebDriverValue, SerializationOptions serializationOptions);

  /**
   * Compiles expression.
   *
   * @param expression         string
   *                           Expression to compile.
   * @param sourceURL          string
   *                           Source url to be set for the script.
   * @param persistScript      boolean
   *                           Specifies whether the compiled script should be persisted.
   * @param executionContextId ExecutionContextId
   *                           Specifies in which execution context to perform script run. If the parameter is omitted the evaluation will be performed in the context of the inspected page.
   * @return {
   * scriptId: ScriptId  Id of the script.
   * exceptionDetails: ExceptionDetails  Exception details.
   * }
   */
  JSONObject compileScript(String expression, String sourceURL, Boolean persistScript, String executionContextId);

  /**
   * Disables reporting of execution contexts creation.
   */
  void disable();

  /**
   * Discards collected exceptions and console API calls.
   */
  void discardConsoleEntries();

  /**
   * Enables reporting of execution contexts creation by means of executionContextCreated event. When the reporting gets enabled the event will be sent immediately for each existing execution context.
   */
  void enable();

  /**
   * Evaluates expression on global object.
   *
   * @param expression                  string
   *                                    Expression to evaluate.
   * @param objectGroup                 string
   *                                    Symbolic group name that can be used to release multiple objects.
   * @param includeCommandLineAPI       boolean
   *                                    Determines whether Command Line API should be available during the evaluation.
   * @param silent                      boolean
   *                                    In silent mode exceptions thrown during evaluation are not reported and do not pause execution. Overrides setPauseOnException state.
   * @param contextId                   ExecutionContextId
   *                                    Specifies in which execution context to perform evaluation. If the parameter is omitted the evaluation will be performed in the context of the inspected page. This is mutually exclusive with uniqueContextId, which offers an alternative way to identify the execution context that is more reliable in a multi-process environment.
   * @param returnByValue               boolean
   *                                    Whether the result is expected to be a JSON object that should be sent by value.
   * @param generatePreview             boolean
   *                                    Whether preview should be generated for the result. EXPERIMENTAL
   * @param userGesture                 boolean
   *                                    Whether execution should be treated as initiated by user in the UI.
   * @param awaitPromise                boolean
   *                                    Whether execution should await for resulting value and return once awaited promise is resolved.
   * @param throwOnSideEffect           boolean
   *                                    Whether to throw an exception if side effect cannot be ruled out during evaluation. This implies disableBreaks below. EXPERIMENTAL
   * @param timeout                     TimeDelta
   *                                    Terminate execution after timing out (number of milliseconds). EXPERIMENTAL
   * @param disableBreaks               boolean
   *                                    Disable breakpoints during execution. EXPERIMENTAL
   * @param replMode                    boolean
   *                                    Setting this flag to true enables let re-declaration and top-level await. Note that let variables can only be re-declared if they originate from replMode themselves. EXPERIMENTAL
   * @param allowUnsafeEvalBlockedByCSP boolean
   *                                    The Content Security Policy (CSP) for the target might block 'unsafe-eval' which includes eval(), Function(), setTimeout() and setInterval() when called with non-callable arguments. This flag bypasses CSP for this evaluation and allows unsafe-eval. Defaults to true. EXPERIMENTAL
   * @param uniqueContextId             string
   *                                    An alternative way to specify the execution context to evaluate in. Compared to contextId that may be reused across processes, this is guaranteed to be system-unique, so it can be used to prevent accidental evaluation of the expression in context different than intended (e.g. as a result of navigation across process boundaries). This is mutually exclusive with contextId. EXPERIMENTAL
   * @param generateWebDriverValue      boolean
   *                                    Deprecated. Use serializationOptions: {serialization:"deep"} instead. Whether the result should contain webDriverValue, serialized according to https://w3c.github.io/webdriver-bidi. This is mutually exclusive with returnByValue, but resulting objectId is still provided. DEPRECATED
   * @param serializationOptions        SerializationOptions
   *                                    Specifies the result serialization. If provided, overrides generatePreview, returnByValue and generateWebDriverValue.
   * @return {
   * result: RemoteObject  Evaluation result.
   * exceptionDetails: ExceptionDetails  Exception details.
   * }
   */
  JSONObject evaluate(String expression, String objectGroup, Boolean includeCommandLineAPI, Boolean silent, String contextId,
                      Boolean returnByValue, Boolean generatePreview, Boolean userGesture, Boolean awaitPromise, Boolean throwOnSideEffect,
                      Long timeout, Boolean disableBreaks, Boolean replMode, Boolean allowUnsafeEvalBlockedByCSP,
                      String uniqueContextId, Boolean generateWebDriverValue, SerializationOptions serializationOptions);

  /**
   * Returns properties of a given object. Object group of the result is inherited from the target object.
   *
   * @param objectId                 RemoteObjectId
   *                                 Identifier of the object to return properties for.
   * @param ownProperties            boolean
   *                                 If true, returns properties belonging only to the element itself, not to its prototype chain.
   * @param accessorPropertiesOnly   boolean
   *                                 If true, returns accessor properties (with getter/setter) only; internal properties are not returned either. EXPERIMENTAL
   * @param generatePreview          boolean
   *                                 Whether preview should be generated for the results. EXPERIMENTAL
   * @param nonIndexedPropertiesOnly boolean
   *                                 If true, returns non-indexed properties only.
   * @return {
   * result: array[ PropertyDescriptor ]  Object properties.
   * internalProperties: array[ InternalPropertyDescriptor ]  Internal object properties (only of the element itself).
   * privateProperties: array[ PrivatePropertyDescriptor ]  Object private properties. EXPERIMENTAL
   * exceptionDetails: ExceptionDetails  Exception details.
   * }
   */
  JSONObject getProperties(String objectId, Boolean ownProperties, Boolean accessorPropertiesOnly, Boolean generatePreview, Boolean nonIndexedPropertiesOnly);

  /**
   * Returns all let, const and class variables from global scope.
   *
   * @param executionContextId ExecutionContextId
   *                           Specifies in which execution context to lookup global scope variables.
   * @return {
   * names:  array[ string ]
   * }
   */
  JSONObject globalLexicalScopeNames(String executionContextId);

  /**
   * @param prototypeObjectId RemoteObjectId
   *                          Identifier of the prototype to return objects for.
   * @param objectGroup       string
   *                          Symbolic group name that can be used to release the results.
   * @return {
   * objects: RemoteObject  Array with objects.
   * }
   */
  JSONObject queryObjects(String prototypeObjectId, String objectGroup);

  /**
   * Releases remote object with given id.
   *
   * @param objectId RemoteObjectId
   *                 Identifier of the object to release.
   */
  void releaseObject(String objectId);

  /**
   * Releases all remote objects that belong to a given group.
   *
   * @param objectGroup string
   *                    Symbolic object group name.
   */
  void releaseObjectGroup(String objectGroup);

  /**
   * Tells inspected instance to run if it was waiting for debugger to attach.
   */
  void runIfWaitingForDebugger();

  /**
   * Runs script with given id in a given context.
   *
   * @param scriptId              ScriptId
   *                              Id of the script to run.
   * @param executionContextId    ExecutionContextId
   *                              Specifies in which execution context to perform script run. If the parameter is omitted the evaluation will be performed in the context of the inspected page.
   * @param objectGroup           string
   *                              Symbolic group name that can be used to release multiple objects.
   * @param silent                boolean
   *                              In silent mode exceptions thrown during evaluation are not reported and do not pause execution. Overrides setPauseOnException state.
   * @param includeCommandLineAPI boolean
   *                              Determines whether Command Line API should be available during the evaluation.
   * @param returnByValue         boolean
   *                              Whether the result is expected to be a JSON object which should be sent by value.
   * @param generatePreview       boolean
   *                              Whether preview should be generated for the result.
   * @param awaitPromise          boolean
   *                              Whether execution should await for resulting value and return once awaited promise is resolved.
   * @return {
   * result: RemoteObject  Run result.
   * exceptionDetails: ExceptionDetails  Exception details.
   * }
   */
  JSONObject runScript(String scriptId, String executionContextId, String objectGroup, Boolean silent, Boolean includeCommandLineAPI, Boolean returnByValue, Boolean generatePreview, Boolean awaitPromise);

  /**
   * Enables or disables async call stacks tracking.
   *
   * @param maxDepth integer
   *                 Maximum depth of async call stacks. Setting to 0 will effectively disable collecting async call stacks (default).
   */
  void setAsyncCallStackDepth(Integer maxDepth);

  /**
   * If executionContextId is empty, adds binding with the given name on the global objects of all inspected contexts, including
   * those created later, bindings survive reloads. Binding function takes exactly one argument, this argument should be string,
   * in case of any other input, function throws an exception. Each binding function call produces Runtime.bindingCalled notification.
   *
   * @param name                 string
   * @param executionContextId   ExecutionContextId
   *                             If specified, the binding would only be exposed to the specified execution context. If omitted and executionContextName is not set, the binding is exposed to all execution contexts of the target. This parameter is mutually exclusive with executionContextName. Deprecated in favor of executionContextName due to an unclear use case and bugs in implementation (crbug.com/1169639). executionContextId will be removed in the future. DEPRECATED
   * @param executionContextName string
   *                             If specified, the binding is exposed to the executionContext with matching name, even for contexts created after the binding is added. See also ExecutionContext.name and worldName parameter to Page.addScriptToEvaluateOnNewDocument. This parameter is mutually exclusive with executionContextId.
   */
  void addBinding(String name, String executionContextId, String executionContextName);

  /**
   * This method tries to lookup and populate exception details for a JavaScript Error object. Note that the stackTrace portion of
   * the resulting exceptionDetails will only be populated if the Runtime domain was enabled at the time when the Error was thrown.
   *
   * @param errorObjectId RemoteObjectId
   *                      The error object for which to resolve the exception details.
   * @return {
   * exceptionDetails: ExceptionDetails
   * }
   */
  JSONObject getExceptionDetails(String errorObjectId);

  /**
   * Returns the JavaScript heap usage. It is the total usage of the corresponding isolate not scoped to a particular Runtime.
   *
   * @param usedSize  number
   *                  Used heap size in bytes.
   * @param totalSize number
   *                  Allocated heap size in bytes.
   */
  void getHeapUsage(Long usedSize, Long totalSize);

  /**
   * Returns the isolate id.
   *
   * @param id string
   *           The isolate id.
   */
  void getIsolateId(String id);

  /**
   * This method does not remove binding function from global object but unsubscribes current runtime agent from Runtime.bindingCalled notifications.
   *
   * @param name string
   */
  void removeBinding(String name);

  /**
   * @param enabled boolean
   */
  void setCustomObjectFormatterEnabled(Boolean enabled);

  /**
   * @param size integer
   */
  void setMaxCallStackSizeToCapture(Long size);

  /**
   * Terminate current or next JavaScript execution. Will cancel the termination when the outer-most script execution ends.
   */
  void terminateExecution();


  /**
   * 事件
   */
  @Event("Runtime")
  public interface Events {

    /**
     * Issued when console API was called.
     *
     * @param type               string
     *                           Type of the call.
     *                           Allowed Values: log, debug, info, error, warning, dir, dirxml, table, trace, clear, startGroup, startGroupCollapsed, endGroup, assert, profile, profileEnd, count, timeEnd
     * @param args               array[ RemoteObject ]
     *                           Call arguments.
     * @param executionContextId ExecutionContextId
     *                           Identifier of the context where the call was made.
     * @param timestamp          Timestamp
     *                           Call timestamp.
     * @param stackTrace         StackTrace
     *                           Stack trace captured when the call was made. The async stack chain is automatically reported for the following call types: assert, error, trace, warning. For other types the async call chain can be retrieved using Debugger.getStackTrace and stackTrace.parentId field.
     * @param context            string
     *                           Console context descriptor for calls on non-default console context (not console.*): 'anonymous#unique-logger-id' for call on unnamed context, 'name#unique-logger-id' for call on named context.
     */
    @Event("consoleAPICalled")
    void consoleAPICalled(String type, List<RemoteObject> args, String executionContextId, Long timestamp, StackTrace stackTrace, String context);

    /**
     * Issued when unhandled exception was revoked.
     *
     * @param reason      string
     *                    Reason describing why exception was revoked.
     * @param exceptionId integer
     *                    The id of revoked exception, as reported in exceptionThrown.
     */
    @Event("exceptionRevoked")
    void exceptionRevoked(String reason, Integer exceptionId);

    /**
     * Issued when exception was thrown and unhandled.
     *
     * @param timestamp        Timestamp
     *                         Timestamp of the exception.
     * @param exceptionDetails ExceptionDetails
     */
    @Event("exceptionThrown")
    void exceptionThrown(Long timestamp, ExceptionDetails exceptionDetails);

    /**
     * Issued when new execution context is created.
     *
     * @param context ExecutionContextDescription
     *                A newly created execution context.
     */
    @Event("executionContextCreated")
    void executionContextCreated(ExecutionContextDescription context);

    /**
     * Issued when execution context is destroyed.
     *
     * @param executionContextId       ExecutionContextId
     *                                 Id of the destroyed context DEPRECATED
     * @param executionContextUniqueId string
     *                                 Unique Id of the destroyed context
     */
    @Event("executionContextDestroyed")
    void executionContextDestroyed(String executionContextId, String executionContextUniqueId);

    /**
     * Issued when all executionContexts were cleared in browser
     */
    @Event("executionContextsCleared")
    void executionContextsCleared();

    /**
     * Issued when object should be inspected (for example, as a result of inspect() command line API call).
     *
     * @param object             RemoteObject
     * @param hints              object
     * @param executionContextId ExecutionContextId
     *                           Identifier of the context where the call was made.
     */
    @Event("inspectRequested")
    void inspectRequested(RemoteObject object, JSONObject hints, String executionContextId);

    /**
     * Notification is issued every time when binding is called.
     *
     * @param name               string
     * @param payload            string
     * @param executionContextId ExecutionContextId
     *                           Identifier of the context where the call was made.
     */
    @Event("bindingCalled")
    void bindingCalled(String name, String payload, String executionContextId);

  }

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
