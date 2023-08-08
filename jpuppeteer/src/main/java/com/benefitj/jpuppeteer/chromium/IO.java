package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Input/Output operations for streams produced by DevTools.
 */
public interface IO {

  /**
   * Close the stream, discard any temporary backing storage.
   *
   * @param handle StreamHandle
   *               Handle of the stream to close.
   */
  void close(StreamHandle handle);

  /**
   * Read a chunk of the stream
   *
   * @param handle StreamHandle
   *               Handle of the stream to read.
   * @param offset integer
   *               Seek to the specified offset before reading (if not specificed, proceed with offset following the last read). Some types of streams may only support sequential reads.
   * @param size   integer
   *               Maximum number of bytes to read (left upon the agent discretion if not specified).
   * @return {
   * base64Encoded: boolean, Set if the data is base64-encoded
   * data: string, Data that were read.
   * eof: boolean, Set if the end-of-file condition occurred while reading.
   * }
   */
  JSONObject read(StreamHandle handle, Integer offset, Integer size);

  /**
   * Return UUID of Blob object specified by a remote object id.
   *
   * @param objectId Runtime.RemoteObjectId
   *                 Object id of a Blob object wrapper.
   * @return {
   * uuid: string, UUID of the specified Blob.
   * }
   */
  JSONObject resolveBlob(String objectId);

  /**
   * This is either obtained from another method or specified as blob:<uuid> where <uuid> is an UUID of a Blob.
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  public class StreamHandle extends JSONObject {
  }

}
