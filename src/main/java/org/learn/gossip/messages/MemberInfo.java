// automatically generated by the FlatBuffers compiler, do not modify

package org.learn.gossip.messages;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class MemberInfo extends Table {
  public static MemberInfo getRootAsMemberInfo(ByteBuffer _bb) { return getRootAsMemberInfo(_bb, new MemberInfo()); }
  public static MemberInfo getRootAsMemberInfo(ByteBuffer _bb, MemberInfo obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; }
  public MemberInfo __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String uuid() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer uuidAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public String hostname() { int o = __offset(6); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer hostnameAsByteBuffer() { return __vector_as_bytebuffer(6, 1); }
  public int port() { int o = __offset(8); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int incarnation() { int o = __offset(10); return o != 0 ? bb.getInt(o + bb_pos) : 0; }

  public static int createMemberInfo(FlatBufferBuilder builder,
      int uuidOffset,
      int hostnameOffset,
      int port,
      int incarnation) {
    builder.startObject(4);
    MemberInfo.addIncarnation(builder, incarnation);
    MemberInfo.addPort(builder, port);
    MemberInfo.addHostname(builder, hostnameOffset);
    MemberInfo.addUuid(builder, uuidOffset);
    return MemberInfo.endMemberInfo(builder);
  }

  public static void startMemberInfo(FlatBufferBuilder builder) { builder.startObject(4); }
  public static void addUuid(FlatBufferBuilder builder, int uuidOffset) { builder.addOffset(0, uuidOffset, 0); }
  public static void addHostname(FlatBufferBuilder builder, int hostnameOffset) { builder.addOffset(1, hostnameOffset, 0); }
  public static void addPort(FlatBufferBuilder builder, int port) { builder.addInt(2, port, 0); }
  public static void addIncarnation(FlatBufferBuilder builder, int incarnation) { builder.addInt(3, incarnation, 0); }
  public static int endMemberInfo(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

