/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package bryanthedragon.mclibreloaded.forge.fml.common.network.handshake;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;


import bryanthedragon.mclibreloaded.forge.fml.common.FMLLog;
import bryanthedragon.mclibreloaded.forge.fml.common.Loader;
import bryanthedragon.mclibreloaded.forge.fml.common.network.NetworkRegistry;
import bryanthedragon.mclibreloaded.forge.fml.common.network.internal.FMLMessage;
import bryanthedragon.mclibreloaded.forge.fml.common.network.internal.FMLNetworkHandler;
import bryanthedragon.mclibreloaded.forge.fml.relauncher.Side;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

enum FMLHandshakeServerState implements IHandshakeState<FMLHandshakeServerState>
{
    START
    {
        @SuppressWarnings("null")
        @Override
        public void accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg, Consumer<? super FMLHandshakeServerState> cons)
        {
            cons.accept(HELLO);
            NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
            int overrideDim = dispatcher.serverInitiateHandshake();
            ctx.writeAndFlush(FMLHandshakeMessage.makeCustomChannelRegistration(NetworkRegistry.INSTANCE.channelNamesFor(Side.SERVER))).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            ctx.writeAndFlush(new FMLHandshakeMessage.ServerHello(overrideDim)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    },
    HELLO
    {
        @SuppressWarnings("null")
        @Override
        public void accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg, Consumer<? super FMLHandshakeServerState> cons)
        {
            // Hello packet first
            if (msg instanceof FMLHandshakeMessage.ClientHello)
            {
                FMLLog.log.info("Client protocol version {}", Integer.toHexString(((FMLHandshakeMessage.ClientHello)msg).protocolVersion()));
                return;
            }

            FMLHandshakeMessage.ModList client = (FMLHandshakeMessage.ModList)msg;
            NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
            dispatcher.setModList(client.modList());
            FMLLog.log.info("Client attempting to join with {} mods : {}", client.modListSize(), client.modListAsString());
            String modRejections = FMLNetworkHandler.checkModList(client, Side.CLIENT);
            if (modRejections != null)
            {
                cons.accept(ERROR);
                dispatcher.rejectHandshake(modRejections);
                return;
            }
            cons.accept(WAITINGCACK);
            ctx.writeAndFlush(new FMLHandshakeMessage.ModList(Loader.instance().getActiveModList()));
        }
    },
    WAITINGCACK
    {
        @SuppressWarnings("null")
        @Override
        public void accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg, Consumer<? super FMLHandshakeServerState> cons)
        {
            cons.accept(COMPLETE);
            if (!ctx.channel().attr(NetworkDispatcher.IS_LOCAL).get())
            {
                Map<ResourceLocation, ForgeRegistry.Snapshot> snapshot = RegistryManager.ACTIVE.takeSnapshot(false);
                Iterator<Map.Entry<ResourceLocation, ForgeRegistry.Snapshot>> itr = snapshot.entrySet().iterator();
                while (itr.hasNext())
                {
                    Entry<ResourceLocation, ForgeRegistry.Snapshot> e = itr.next();
                    ctx.writeAndFlush(new FMLHandshakeMessage.RegistryData(itr.hasNext(), e.getKey(), e.getValue())).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            }
            ctx.writeAndFlush(new FMLHandshakeMessage.HandshakeAck(ordinal())).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            NetworkRegistry.INSTANCE.fireNetworkHandshake(ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get(), Side.SERVER);
        }
    },
    COMPLETE
    {
        @SuppressWarnings("null")
        @Override
        public void accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg, Consumer<? super FMLHandshakeServerState> cons)
        {
            cons.accept(DONE);
            // Poke the client
            ctx.writeAndFlush(new FMLHandshakeMessage.HandshakeAck(ordinal())).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            FMLMessage.CompleteHandshake complete = new FMLMessage.CompleteHandshake(Side.SERVER);
            ctx.fireChannelRead(complete);
        }
    },
    DONE
    {
        @SuppressWarnings("null")
        @Override
        public void accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg, Consumer<? super FMLHandshakeServerState> cons)
        {
        }
    },
    ERROR
    {
        @SuppressWarnings("null")
        @Override
        public void accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg, Consumer<? super FMLHandshakeServerState> cons)
        {
        }
    };
}
