package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.benefitj.jpuppeteer.Event;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.net.Inet4Address;
import java.util.List;

/**
 * Debugger domain exposes JavaScript debugging capabilities. It allows setting and removing breakpoints, stepping through execution, exploring stack traces, etc.
 */
@ChromiumApi("Debugger")
public interface Debugger {

  /**
   * Continues execution until specific location is reached.
   *
   * @param location         Location
   *                         Location to continue to.
   * @param targetCallFrames string
   *                         Allowed Values: any, current
   */
  void continueToLocation(Location location, String targetCallFrames);

  /**
   * Disables debugger for given page.
   */
  void disable();

  /**
   * Enables debugger for the given page. Clients should not assume that the debugging has been enabled until the result for this command is received.
   *
   * @param maxScriptsCacheSize number
   *                            The maximum size in bytes of collected scripts (not referenced by other heap objects) the debugger can hold. Puts no limit if parameter is omitted. EXPERIMENTAL
   * @return Unique identifier of the debugger. EXPERIMENTAL
   */
  String enable(Integer maxScriptsCacheSize);

  /**
   * Evaluates expression on a given call frame.
   *
   * @param callFrameId           CallFrameId
   *                              Call frame identifier to evaluate on.
   * @param expression            string
   *                              Expression to evaluate.
   * @param objectGroup           string
   *                              String object group name to put result into (allows rapid releasing resulting object handles using releaseObjectGroup).
   * @param includeCommandLineAPI boolean
   *                              Specifies whether command line API should be available to the evaluated expression, defaults to false.
   * @param silent                boolean
   *                              In silent mode exceptions thrown during evaluation are not reported and do not pause execution. Overrides setPauseOnException state.
   * @param returnByValue         boolean
   *                              Whether the result is expected to be a JSON object that should be sent by value.
   * @param generatePreview       boolean
   *                              Whether preview should be generated for the result. EXPERIMENTAL
   * @param throwOnSideEffect     boolean
   *                              Whether to throw an exception if side effect cannot be ruled out during evaluation.
   * @param timeout               Runtime.TimeDelta
   *                              Terminate execution after timing out (number of milliseconds). EXPERIMENTAL
   * @return {
   * result : Runtime.RemoteObject Object wrapper for the evaluation result.
   * exceptionDetails: Runtime.ExceptionDetails  Exception details.
   * }
   */
  JSONObject evaluateOnCallFrame(String callFrameId, String expression, String objectGroup, boolean includeCommandLineAPI,
                                 boolean silent, boolean returnByValue, boolean generatePreview, boolean throwOnSideEffect, long timeout);

  /**
   * Returns possible locations for breakpoint. scriptId in start and end range locations should be the same.
   *
   * @param start              Location
   *                           Start of range to search possible breakpoint locations in.
   * @param end                Location
   *                           End of range to search possible breakpoint locations in (excluding). When not specified, end of scripts is used as end of range.
   * @param restrictToFunction boolean
   *                           Only consider locations which are in the same (non-nested) function as start.
   * @return List of the possible breakpoint locations.
   */
  List<BreakLocation> getPossibleBreakpoints(Location start, Location end, boolean restrictToFunction);

  /**
   * Returns source for the script with given id.
   *
   * @param scriptId Id of the script to get source for.
   * @return {
   * scriptSource: string  Script source (empty in case of Wasm bytecode).
   * bytecode: string Wasm bytecode. (Encoded as a base64 string when passed over JSON)
   * }
   */
  JSONObject getScriptSource(String scriptId);

  /**
   * Stops on the next JavaScript statement.
   */
  void pause();

  /**
   * Removes JavaScript breakpoint.
   */
  void removeBreakpoint(String breakpointId);

  /**
   * Restarts particular call frame from the beginning. The old, deprecated behavior of restartFrame is to stay paused and allow further
   * CDP commands after a restart was scheduled. This can cause problems with restarting, so we now continue execution immediatly after
   * it has been scheduled until we reach the beginning of the restarted frame. To stay back-wards compatible, restartFrame now expects
   * a mode parameter to be present. If the mode parameter is missing, restartFrame errors out. The various return values are deprecated
   * and callFrames is always empty. Use the call frames from the Debugger#paused events instead, that fires once V8 pauses at the beginning of the restarted function.
   *
   * @param callFrameId CallFrameId
   *                    Call frame identifier to evaluate on.
   * @param mode        string
   *                    The mode parameter must be present and set to 'StepInto', otherwise restartFrame will error out.
   *                    Allowed Values: StepInto
   * @return {
   * callFrames:  array[ CallFrame ], New stack trace. DEPRECATED
   * asyncStackTrace: Runtime.StackTrace,   Async stack trace, if any. DEPRECATED
   * asyncStackTraceId: Runtime.StackTraceId, Async stack trace, if any. DEPRECATED
   * }
   */
  JSONObject restartFrame(String callFrameId, String mode);

  /**
   * Resumes JavaScript execution.
   *
   * @param terminateOnResume boolean
   *                          Set to true to terminate execution upon resuming execution. In contrast to Runtime.terminateExecution,
   *                          this will allows to execute further JavaScript (i.e. via evaluation) until execution of the paused code
   *                          is actually resumed, at which point termination is triggered. If execution is currently not paused,
   *                          this parameter has no effect.
   */
  void resume(boolean terminateOnResume);

  /**
   * Searches for given string in script content.
   *
   * @param scriptId      Runtime.ScriptId
   *                      Id of the script to search in.
   * @param query         string
   *                      String to search for.
   * @param caseSensitive boolean
   *                      If true, search is case sensitive.
   * @param isRegex       boolean
   *                      If true, treats string parameter as regex.
   * @return {
   * result: array[ SearchMatch ] List of search matches.
   * }
   */
  JSONObject searchInContent(String scriptId, String query, boolean caseSensitive, boolean isRegex);

  /**
   * Enables or disables async call stacks tracking.
   *
   * @param maxDepth integer
   *                 Maximum depth of async call stacks. Setting to 0 will effectively disable collecting async call stacks (default).
   */
  void setAsyncCallStackDepth(Integer maxDepth);

  /**
   * Sets JavaScript breakpoint at a given location.
   *
   * @param location  Location
   *                  Location to set breakpoint in.
   * @param condition string
   *                  Expression to use as a breakpoint condition. When specified, debugger will only stop on the breakpoint if this expression evaluates to true.
   * @return {
   * breakpointId: BreakpointId,  Id of the created breakpoint for further reference.
   * actualLocation：Location, Location this breakpoint resolved into.
   * }
   */
  JSONObject setBreakpoint(Location location, String condition);

  /**
   * Sets JavaScript breakpoint at given location specified either by URL or URL regex. Once this command is issued, all existing parsed
   * scripts will have breakpoints resolved and returned in locations property. Further matching script parsing will result in subsequent
   * breakpointResolved events issued. This logical breakpoint will survive page reloads.
   *
   * @param lineNumber   integer
   *                     Line number to set breakpoint at.
   * @param url          string
   *                     URL of the resources to set breakpoint on.
   * @param urlRegex     string
   *                     Regex pattern for the URLs of the resources to set breakpoints on. Either url or urlRegex must be specified.
   * @param scriptHash   string
   *                     Script hash of the resources to set breakpoint on.
   * @param columnNumber integer
   *                     Offset in the line to set breakpoint at.
   * @param condition    string
   *                     Expression to use as a breakpoint condition. When specified, debugger will only stop on the breakpoint if this expression evaluates to true.
   * @return {
   * breakpointId: BreakpointId, Id of the created breakpoint for further reference.
   * locations: array[ Location ], List of the locations this breakpoint resolved into upon addition.
   * }
   */
  JSONObject setBreakpointByUrl(Integer lineNumber, String url, String urlRegex, String scriptHash, Integer columnNumber, String condition);

  /**
   * Activates / deactivates all breakpoints on the page.
   *
   * @param active boolean
   *               New value for breakpoints active state.
   */
  void setBreakpointsActive(boolean active);

  /**
   * Sets instrumentation breakpoint.
   *
   * @param instrumentation string
   *                        Instrumentation name.
   *                        Allowed Values: beforeScriptExecution, beforeScriptWithSourceMapExecution
   * @return {
   * breakpointId: BreakpointId, Id of the created breakpoint for further reference.
   * }
   */
  JSONObject setInstrumentationBreakpoint(String instrumentation);

  /**
   * Defines pause on exceptions state. Can be set to stop on all exceptions, uncaught exceptions, or caught exceptions, no exceptions. Initial pause on exceptions state is none.
   *
   * @param state string
   *              Pause on exceptions mode.
   *              Allowed Values: none, caught, uncaught, all
   */
  void setPauseOnExceptions(String state);

  /**
   * Edits JavaScript source live. In general, functions that are currently on the stack can not be edited with a single
   * exception: If the edited function is the top-most stack frame and that is the only activation of that function on the stack.
   * In this case the live edit will be successful and a Debugger.restartFrame for the top-most function is automatically triggered.
   *
   * @param scriptId             Runtime.ScriptId
   *                             Id of the script to edit.
   * @param scriptSource         string
   *                             New content of the script.
   * @param dryRun               boolean
   *                             If true the change will not actually be applied. Dry run may be used to get result description without actually modifying the code.
   * @param allowTopFrameEditing boolean
   *                             If true, then scriptSource is allowed to change the function on top of the stack as long as the top-most stack frame is the only activation of that function. EXPERIMENTAL
   * @return {
   * callFrames: array[ CallFrame ], New stack trace in case editing has happened while VM was stopped. DEPRECATED
   * stackChanged: boolean, Whether current call stack was modified after applying the changes. DEPRECATED
   * asyncStackTrace: Runtime.StackTrace, Async stack trace, if any. DEPRECATED
   * asyncStackTraceId: Runtime.StackTraceId,  Async stack trace, if any. DEPRECATED
   * status: string, Whether the operation was successful or not. Only Ok denotes a successful live edit while the other enum variants denote why the live edit failed.
   * Allowed Values: Ok, CompileError, BlockedByActiveGenerator, BlockedByActiveFunction, BlockedByTopLevelEsModuleChange
   * EXPERIMENTAL
   * exceptionDetails: Runtime.ExceptionDetails， Exception details if any. Only present when status is CompileError.
   * }
   */
  JSONObject setScriptSource(String scriptId, String scriptSource, boolean dryRun, boolean allowTopFrameEditing);

  /**
   * Makes page not interrupt on any pauses (breakpoint, exception, dom exception etc).
   *
   * @param skip boolean
   *             New value for skip pauses state.
   */
  void setSkipAllPauses(boolean skip);

  /**
   * Changes value of variable in a callframe. Object-based scopes are not supported and must be mutated manually.
   *
   * @param scopeNumber  integer
   *                     0-based number of scope as was listed in scope chain. Only 'local', 'closure' and 'catch' scope types are allowed. Other scopes could be manipulated manually.
   * @param variableName string
   *                     Variable name.
   * @param newValue     Runtime.CallArgument
   *                     New variable value.
   * @param callFrameId  CallFrameId
   *                     Id of callframe that holds variable.
   */
  void setVariableValue(Integer scopeNumber, String variableName, Runtime.CallArgument newValue, String callFrameId);

  /**
   * Steps into the function call.
   *
   * @param breakOnAsyncCall boolean
   *                         Debugger will pause on the execution of the first async task which was scheduled before next pause. EXPERIMENTAL
   * @param skipList         array[ LocationRange ]
   *                         The skipList specifies location ranges that should be skipped on step into.
   */
  void stepInto(boolean breakOnAsyncCall, List<LocationRange> skipList);

  /**
   * Steps out of the function call.
   */
  void stepOut();

  /**
   * Steps over the statement.
   *
   * @param skipList array[ LocationRange ]
   *                 The skipList specifies location ranges that should be skipped on step over.
   */
  void stepOver(List<LocationRange> skipList);

  /**
   * This command is deprecated. Use getScriptSource instead.
   *
   * @param scriptId Runtime.ScriptId
   *                 Id of the Wasm script to get source for.
   * @return {
   * bytecode: string, Script source. (Encoded as a base64 string when passed over JSON)
   * }
   */
  JSONObject getWasmBytecode(String scriptId);

  /**
   * @param scriptId Runtime.ScriptId
   *                 Id of the script to disassemble
   * @return {
   * streamId: string For large modules, return a stream from which additional chunks of disassembly can be read successively.
   * totalNumberOfLines: integer, The total number of lines in the disassembly text.
   * functionBodyOffsets: array[ integer ], The offsets of all function bodies, in the format [start1, end1, start2, end2, ...] where all ends are exclusive.
   * chunk: WasmDisassemblyChunk, The first chunk of disassembly.
   * }
   */
  JSONObject disassembleWasmModule(String scriptId);

  /**
   * Returns stack trace with given stackTraceId.
   *
   * @param stackTraceId Runtime.StackTraceId
   * @return {
   * stackTrace: Runtime.StackTrace
   * }
   */
  JSONObject getStackTrace(String stackTraceId);

  /**
   * Disassemble the next chunk of lines for the module corresponding to the stream.
   * If disassembly is complete, this API will invalidate the streamId and return an empty chunk.
   * Any subsequent calls for the now invalid stream will return errors.
   *
   * @param streamId string
   * @return {
   * chunk: WasmDisassemblyChunk, The next chunk of disassembly.
   * }
   */
  JSONObject nextWasmDisassemblyChunk(String streamId);

  /**
   * Makes backend skip steps in the script in blackboxed ranges. VM will try leave blacklisted scripts by performing 'step in'
   * several times, finally resorting to 'step out' if unsuccessful. Positions array contains positions where blackbox state is
   * changed. First interval isn't blackboxed. Array should be sorted.
   *
   * @param scriptId  Runtime.ScriptId
   *                  Id of the script.
   * @param positions array[ ScriptPosition ]
   */
  void setBlackboxedRanges(String scriptId, List<ScriptPosition> positions);

  /**
   * Replace previous blackbox patterns with passed ones. Forces backend to skip stepping/pausing in scripts with url matching one of the patterns.
   * VM will try to leave blackboxed script by performing 'step in' several times, finally resorting to 'step out' if unsuccessful.
   *
   * @param patterns array[ string ]
   *                 Array of regexps that will be used to check script url for blackbox state.
   */
  void setBlackboxPatterns(List<String> patterns);

  /**
   * Sets JavaScript breakpoint before each call to the given function. If another function was created from the same source as a given one, calling it will also trigger the breakpoint.
   *
   * @param objectId  Runtime.RemoteObjectId
   *                  Function object id.
   * @param condition string
   *                  Expression to use as a breakpoint condition. When specified, debugger will stop on the breakpoint if this expression evaluates to true.
   * @return {
   * breakpointId: BreakpointId, Id of the created breakpoint for further reference.
   * }
   */
  JSONObject setBreakpointOnFunctionCall(String objectId, String condition);

  /**
   * Changes return value in top frame. Available only at return break position.
   *
   * @param newValue Runtime.CallArgument
   *                 New return value.
   */
  void setReturnValue(Runtime.CallArgument newValue);

  /**
   * @param parentStackTraceId Runtime.StackTraceId
   *                           Debugger will pause when async call with given stack trace is started.
   */
  void pauseOnAsyncCall(String parentStackTraceId);

  /**
   * Fired when breakpoint is resolved to an actual script and location.
   *
   * @param breakpointId BreakpointId
   *                     Breakpoint unique identifier.
   * @param location     Location
   *                     Actual breakpoint location.
   */
  @Event("breakpointResolved")
  void breakpointResolved(String breakpointId, Location location);

  /**
   * Fired when the virtual machine stopped on breakpoint or exception or any other stop criteria.
   *
   * @param callFrames            array[ CallFrame ]
   *                              Call stack the virtual machine stopped on.
   * @param reason                string
   *                              Pause reason.
   *                              Allowed Values: ambiguous, assert, CSPViolation, debugCommand, DOM, EventListener, exception, instrumentation, OOM, other, promiseRejection, XHR, step
   * @param data                  object
   *                              Object containing break-specific auxiliary properties.
   * @param hitBreakpoints        array[ string ]
   *                              Hit breakpoints IDs
   * @param asyncStackTrace       Runtime.StackTrace
   *                              Async stack trace, if any.
   * @param asyncStackTraceId     Runtime.StackTraceId
   *                              Async stack trace, if any. EXPERIMENTAL
   * @param asyncCallStackTraceId Runtime.StackTraceId
   *                              Never present, will be removed. EXPERIMENTALDEPRECATED
   */
  @Event("paused")
  void paused(List<CallFrame> callFrames, String reason, JSONObject data, JSONObject hitBreakpoints, Runtime.StackTrace asyncStackTrace, String asyncStackTraceId, String asyncCallStackTraceId);

  /**
   * Fired when the virtual machine resumed execution.
   */
  @Event("resumed")
  void resumed();

  /**
   * Fired when virtual machine fails to parse the script.
   *
   * @param scriptId                Runtime.ScriptId
   *                                Identifier of the script parsed.
   * @param url                     string
   *                                URL or name of the script parsed (if any).
   * @param startLine               integer
   *                                Line offset of the script within the resource with given URL (for script tags).
   * @param startColumn             integer
   *                                Column offset of the script within the resource with given URL.
   * @param endLine                 integer
   *                                Last line of the script.
   * @param endColumn               integer
   *                                Length of the last line of the script.
   * @param executionContextId      Runtime.ExecutionContextId
   *                                Specifies script creation context.
   * @param hash                    string
   *                                Content hash of the script, SHA-256.
   * @param executionContextAuxData object
   *                                Embedder-specific auxiliary data likely matching {isDefault: boolean, type: 'default'|'isolated'|'worker', frameId: string}
   * @param sourceMapURL            string
   *                                URL of source map associated with script (if any).
   * @param hasSourceURL            boolean
   *                                True, if this script has sourceURL.
   * @param isModule                boolean
   *                                True, if this script is ES6 module.
   * @param length                  integer
   *                                This script length.
   * @param stackTrace              Runtime.StackTrace
   *                                JavaScript top stack frame of where the script parsed event was triggered if available. EXPERIMENTAL
   * @param codeOffset              integer
   *                                If the scriptLanguage is WebAssembly, the code section offset in the module. EXPERIMENTAL
   * @param scriptLanguage          Debugger.ScriptLanguage
   *                                The language of the script. EXPERIMENTAL
   * @param embedderName            string
   *                                The name the embedder supplied for this script. EXPERIMENTAL
   */
  @Event("scriptFailedToParse")
  void scriptFailedToParse(String scriptId, String url, Integer startLine, Integer startColumn, Inet4Address endLine,
                           Integer endColumn, String executionContextId, String hash, JSONObject executionContextAuxData,
                           String sourceMapURL, boolean hasSourceURL, boolean isModule, Integer length, Runtime.StackTrace stackTrace,
                           Integer codeOffset, ScriptLanguage scriptLanguage, String embedderName);

  /**
   * Fired when virtual machine parses script. This event is also fired for all known and uncollected scripts upon enabling debugger.
   *
   * @param scriptId                Runtime.ScriptId
   *                                Identifier of the script parsed.
   * @param url                     string
   *                                URL or name of the script parsed (if any).
   * @param startLine               integer
   *                                Line offset of the script within the resource with given URL (for script tags).
   * @param startColumn             integer
   *                                Column offset of the script within the resource with given URL.
   * @param endLine                 integer
   *                                Last line of the script.
   * @param endColumn               integer
   *                                Length of the last line of the script.
   * @param executionContextId      Runtime.ExecutionContextId
   *                                Specifies script creation context.
   * @param hash                    string
   *                                Content hash of the script, SHA-256.
   * @param executionContextAuxData object
   *                                Embedder-specific auxiliary data likely matching {isDefault: boolean, type: 'default'|'isolated'|'worker', frameId: string}
   * @param isLiveEdit              boolean
   *                                True, if this script is generated as a result of the live edit operation. EXPERIMENTAL
   * @param sourceMapURL            string
   *                                URL of source map associated with script (if any).
   * @param hasSourceURL            boolean
   *                                True, if this script has sourceURL.
   * @param isModule                boolean
   *                                True, if this script is ES6 module.
   * @param length                  integer
   *                                This script length.
   * @param stackTrace              Runtime.StackTrace
   *                                JavaScript top stack frame of where the script parsed event was triggered if available. EXPERIMENTAL
   * @param codeOffset              integer
   *                                If the scriptLanguage is WebAssembly, the code section offset in the module. EXPERIMENTAL
   * @param scriptLanguage          Debugger.ScriptLanguage
   *                                The language of the script. EXPERIMENTAL
   * @param debugSymbols            Debugger.DebugSymbols
   *                                If the scriptLanguage is WebASsembly, the source of debug symbols for the module. EXPERIMENTAL
   * @param embedderName            string
   *                                The name the embedder supplied for this script. EXPERIMENTAL
   */
  @Event("scriptParsed")
  void scriptParsed(String scriptId, String url, Integer startLine, Integer startColumn, Integer endLine, Integer endColumn, String executionContextId
      , String hash, JSONObject executionContextAuxData, boolean isLiveEdit, String sourceMapURL, boolean hasSourceURL, boolean isModule
      , Integer length, Runtime.StackTrace stackTrace, Integer codeOffset, ScriptLanguage scriptLanguage, DebugSymbols debugSymbols, String embedderName);

  /**
   *
   */
  @Data
  public class BreakLocation {
    /**
     * Script identifier as reported in the Debugger.scriptParsed.
     */
    String scriptId;
    /**
     * Line number in the script (0-based).
     */
    Integer lineNumber;
    /**
     * Column number in the script (0-based).
     */
    Integer columnNumber;
    /**
     * Allowed Values: debuggerStatement, call, return
     */
    String type;
  }

  /**
   * JavaScript call frame. Array of call frames form the call stack.
   */
  @Data
  public class CallFrame {
    /**
     * Call frame identifier. This identifier is only valid while the virtual machine is paused.
     */
    String callFrameId;
    /**
     * Name of the JavaScript function called on this call frame.
     */
    String functionName;
    /**
     * Location in the source code.
     */
    Location functionLocation;
    /**
     * Location in the source code.
     */
    Location location;
    /**
     * JavaScript script name or url. Deprecated in favor of using the location.scriptId to resolve the URL via a previously sent Debugger.scriptParsed event. DEPRECATED
     */
    String url;
    /**
     * Scope chain for this call frame.
     */
    List<Scope> scopeChain;
    /**
     * this object for this call frame.
     */
    @JSONField(name = "this")
    @JsonProperty("this")
    Runtime.RemoteObject _this;
    /**
     * The value being returned, if the function is at return point.
     */
    Runtime.RemoteObject returnValue;
    /**
     * Valid only while the VM is paused and indicates whether this frame can be restarted or not. Note that a true value here does not guarantee that Debugger#restartFrame with this CallFrameId will be successful, but it is very likely. EXPERIMENTAL
     */
    boolean canBeRestarted;
  }

  /**
   * Debug symbols available for a wasm script.
   */
  @Data
  public class DebugSymbols {
    /**
     * Type of the debug symbols.
     * Allowed Values: None, SourceMap, EmbeddedDWARF, ExternalDWARF
     */
    String type;
    /**
     * URL of the external symbol source.
     */
    String externalURL;
  }

  /**
   * Location in the source code.
   */
  @Data
  public class Location {
    /**
     * Script identifier as reported in the Debugger.scriptParsed.
     */
    String scriptId;
    /**
     * Line number in the script (0-based).
     */
    Integer lineNumber;
    /**
     * Column number in the script (0-based).
     */
    Integer columnNumber;
  }

  /**
   * Location range within one script.
   */
  @Data
  public class LocationRange {
    String scriptId;
    ScriptPosition start;
    ScriptPosition end;
  }

  /**
   * Scope description.
   */
  @Data
  public class Scope {
    /**
     * Scope type.
     * Allowed Values: global, local, with, closure, catch, block, script, eval, module, wasm-expression-stack
     */
    String type;

    /**
     * Object representing the scope. For global and with scopes it represents the actual object; for the rest of the scopes, it is artificial transient object enumerating scope variables as its properties.
     */
    Runtime.RemoteObject object;
    /**
     *
     */
    String name;
    /**
     * Location in the source code where scope starts
     */
    Location startLocation;
    /**
     * Location in the source code where scope ends
     */
    Location endLocation;
  }

  /**
   * Enum of possible script languages.
   * Allowed Values: JavaScript, WebAssembly
   */
  public enum ScriptLanguage {
    JavaScript, WebAssembly
  }

  /**
   * Search match for resource.
   */
  @Data
  public class SearchMatch {

    /**
     * Line number in resource content.
     */
    Integer lineNumber;
    /**
     * Line with match content.
     */
    String lineContent;
  }

  /**
   * Location in the source code.
   */
  @Data
  public class ScriptPosition {
    Integer lineNumber;
    Integer columnNumber;
  }

  /**
   *
   */
  @Data
  public class WasmDisassemblyChunk {
    /**
     * The next chunk of disassembled lines.
     */
    List<String> lines;
    /**
     * The bytecode offsets describing the start of each line.
     */
    List<Integer> bytecodeOffsets;
  }

}
