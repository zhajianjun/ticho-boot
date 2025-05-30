package top.ticho.intranet.common.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;


/**
 * 消息解码器
 * <p>
 * 将接收到的字节数据按照特定的协议进行解码，生成对应的消息对象
 * 该类是Netty提供的一个解码器，用于处理基于长度字段的消息帧。通过指定长度字段的偏移量、长度、调整值和需要跳过的字节数，可以将接收到的字节数据按照指定的长度字段进行解码。
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * @param maxFrameLength      表示的是包的最大长度，超出包的最大长度netty将会做一些特殊处理
     * @param lengthFieldOffset   指的是长度域的偏移量，表示跳过指定长度个字节之后的才是长度域
     * @param lengthFieldLength   记录该帧数据长度的字段本身的长度
     * @param lengthAdjustment    该字段加长度字段等于数据帧的长度，包体长度调整的大小，长度域的数值表示的长度加上这个修正值表示的就是带header的包
     * @param initialBytesToStrip 从数据帧中跳过的字节数，表示获取完一个完整的数据包之后，忽略前面的指定的位数个字节，应用解码器拿到的就是不带长度域的数据包
     */
    public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    /**
     * 解码方法
     *
     * @param ctx   ChannelHandlerContext对象
     * @param bufIn bufIn
     * @return 解码后的Message对象
     * @throws Exception 解码过程中可能抛出的异常
     */
    @Override
    protected Message decode(ChannelHandlerContext ctx, ByteBuf bufIn) throws Exception {
        // 调用父类的解码方法，获取解码后的ByteBuf对象
        ByteBuf in = (ByteBuf) super.decode(ctx, bufIn);
        if (null == in) {
            return null;
        }
        // 读取的字节数小于4，则表明数据异常，因为长度数据为int，至少需要4字节数
        if (in.readableBytes() < CommConst.INT_BYTES) {
            return null;
        }
        // [HEADER]读取消息体的总字节数
        int messageBytes = in.readInt();
        // 如果可读字节数不等于消息体总字节数，数据异常，返回null
        if (in.readableBytes() != messageBytes) {
            return null;
        }
        // 创建Message对象
        // 2-读取消息类型
        byte type = in.readByte();
        // 4-读取requestid长度 和 数据
        byte requestIdLength = in.readByte();
        // 读取URI字节数组
        byte[] requestIdBytes = new byte[requestIdLength];
        in.readBytes(requestIdBytes);
        // 将requestId字节数组转换为字符串
        String requestId = new String(requestIdBytes);
        // 计算数据部分的长度
        int dateLength = messageBytes - CommConst.TYPE_SIZE - CommConst.REQUEST_ID_LEN_SIZE - requestIdLength;
        // 读取数据部分字节数组
        byte[] data = new byte[dateLength];
        in.readBytes(data);
        // 释放ByteBuf对象
        in.release();
        // 返回解码后的Message对象
        return new Message(type, requestId, data);
    }

}
