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

package bryanthedragon.mclibreloaded.forge.fml.common.network.internal;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import bryanthedragon.mclibreloaded.forge.fml.client.FMLClientHandler;
import bryanthedragon.mclibreloaded.forge.fml.common.FMLCommonHandler;
import bryanthedragon.mclibreloaded.forge.fml.common.FMLLog;
import bryanthedragon.mclibreloaded.forge.fml.common.Loader;
import bryanthedragon.mclibreloaded.forge.fml.common.ModContainer;
import bryanthedragon.mclibreloaded.forge.fml.common.network.NetworkRegistry;
import bryanthedragon.mclibreloaded.forge.fml.common.network.internal.FMLMessage.EntityMessage;
import bryanthedragon.mclibreloaded.forge.fml.common.registry.EntityRegistry;
import bryanthedragon.mclibreloaded.forge.fml.common.registry.IEntityAdditionalSpawnData;
import bryanthedragon.mclibreloaded.forge.fml.common.registry.IThrowableEntity;
import bryanthedragon.mclibreloaded.forge.fml.common.registry.EntityRegistry.EntityRegistration;

public class EntitySpawnHandler extends SimpleChannelInboundHandler<FMLMessage.EntityMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, final EntityMessage msg) throws Exception
    {
        IThreadListener thread = FMLCommonHandler.instance().getWorldThread(ctx.channel().attr(NetworkRegistry.NET_HANDLER).get());
        if (thread.isCallingFromMinecraftThread())
        {
            process(msg);
        }
        else
        {
            thread.addScheduledTask(() -> EntitySpawnHandler.this.process(msg));
        }
    }

    private void process(EntityMessage msg)
    {
        if (msg.getClass().equals(FMLMessage.EntitySpawnMessage.class))
        {
            FMLMessage.EntitySpawnMessage spawnMsg = (FMLMessage.EntitySpawnMessage) msg;
            spawnEntity(spawnMsg);
            spawnMsg.dataStream.release();
        }
    }

    private void spawnEntity(FMLMessage.EntitySpawnMessage spawnMsg)
    {
        ModContainer mc = Loader.instance().getIndexedModList().get(spawnMsg.modId);
        EntityRegistration er = EntityRegistry.instance().lookupModSpawn(mc, spawnMsg.modEntityTypeId);
        if (er == null)
        {
            throw new RuntimeException( "Could not spawn mod entity ModID: " + spawnMsg.modId + " EntityID: " + spawnMsg.modEntityTypeId +
                    " at ( " + spawnMsg.rawX + "," + spawnMsg.rawY + ", " + spawnMsg.rawZ + ") Please contact mod author or server admin.");
        }
        WorldClient wc = FMLClientHandler.instance().getWorldClient();
        try
        {
            Entity entity;
            if (er.hasCustomSpawning())
            {
                entity = er.doCustomSpawning(spawnMsg);
            } else
            {
                entity = er.newInstance(wc);

                int offset = spawnMsg.entityId - entity.getEntityId();
                entity.setEntityId(spawnMsg.entityId);
                entity.setUniqueId(spawnMsg.entityUUID);
                entity.setLocationAndAngles(spawnMsg.rawX, spawnMsg.rawY, spawnMsg.rawZ, spawnMsg.scaledYaw, spawnMsg.scaledPitch);
                if (entity instanceof EntityLiving)
                {
                    ((EntityLiving) entity).rotationYawHead = spawnMsg.scaledHeadYaw;
                }

                Entity parts[] = entity.getParts();
                if (parts != null)
                {
                    for (int j = 0; j < parts.length; j++)
                    {
                        parts[j].setEntityId(parts[j].getEntityId() + offset);
                    }
                }
            }

            EntityTracker.updateServerPosition(entity, spawnMsg.rawX, spawnMsg.rawY, spawnMsg.rawZ);

            EntityPlayerSP clientPlayer = FMLClientHandler.instance().getClientPlayerEntity();
            if (entity instanceof IThrowableEntity)
            {
                Entity thrower = clientPlayer.getEntityId() == spawnMsg.throwerId ? clientPlayer : wc.getEntityByID(spawnMsg.throwerId);
                ((IThrowableEntity) entity).setThrower(thrower);
            }

            if (spawnMsg.dataWatcherList != null)
            {
                entity.getDataManager().setEntryValues(spawnMsg.dataWatcherList);
            }

            if (spawnMsg.throwerId > 0)
            {
                entity.setVelocity(spawnMsg.speedScaledX, spawnMsg.speedScaledY, spawnMsg.speedScaledZ);
            }

            if (entity instanceof IEntityAdditionalSpawnData)
            {
                ((IEntityAdditionalSpawnData) entity).readSpawnData(spawnMsg.dataStream);
            }
            wc.addEntityToWorld(spawnMsg.entityId, entity);
        }
        catch (Exception e)
        {
            throw new RuntimeException("A severe problem occurred during the spawning of an entity at (" + spawnMsg.rawX + ", " + spawnMsg.rawY + ", " + spawnMsg.rawZ + ")", e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log.error("EntitySpawnHandler exception", cause);
        super.exceptionCaught(ctx, cause);
    }
}
