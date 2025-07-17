package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.VisibleForDebug;

public interface ProtocolInfo<T extends PacketListener> {
    ConnectionProtocol id();

    PacketFlow flow();

    StreamCodec<ByteBuf, Packet<? super T>> codec();

    @Nullable
    BundlerInfo bundlerInfo();

    public interface Details {
        ConnectionProtocol id();

        PacketFlow flow();

        @VisibleForDebug
        void listPackets(ProtocolInfo.Details.PacketVisitor p_391257_);

        @FunctionalInterface
        public interface PacketVisitor {
            void accept(PacketType<?> p_394380_, int p_394885_);
        }
    }

    public interface DetailsProvider {
        ProtocolInfo.Details details();
    }
}