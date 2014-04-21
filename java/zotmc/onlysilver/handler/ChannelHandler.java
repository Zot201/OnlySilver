package zotmc.onlysilver.handler;

import static zotmc.onlysilver.Contents.everlasting;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import zotmc.onlysilver.handler.ChannelHandler.Message;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;

public class ChannelHandler extends FMLIndexedMessageToMessageCodec<Message> {
	
	public interface Message {
		
		public void write(ByteBuf target);
		
		public void read(ByteBuf source);
		
		public void processClient();
		
	}
	
	public static class EverlastingMessage implements Message {
		
		private boolean increaseLifespan;
		private int entityId;
		
		public EverlastingMessage() { }
		public EverlastingMessage(boolean increaseLifespan, int entityId) {
			this.increaseLifespan = increaseLifespan;
			this.entityId = entityId;
		}

		@Override public void write(ByteBuf target) {
			target.writeBoolean(increaseLifespan);
			target.writeInt(entityId);
		}

		@Override public void read(ByteBuf source) {
			increaseLifespan = source.readBoolean();
			entityId = source.readInt();
		}

		@Override public void processClient() {
			everlasting.get().addClientPending(increaseLifespan, entityId);
			
		}
		
	}
	
	
	{
		addDiscriminator(0, EverlastingMessage.class);
	}

	@Override public void encodeInto(ChannelHandlerContext ctx, Message msg, ByteBuf target) throws Exception {
		msg.write(target);
	}

	@Override public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, Message msg) {
		msg.read(source);
		msg.processClient();
	}

}
