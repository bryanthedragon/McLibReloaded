package bryanthedragon.mclibreloaded.network.mclib.client;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.GuiConfirmationScreen;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.IKey;
import bryanthedragon.mclibreloaded.network.ClientMessageHandler;
import bryanthedragon.mclibreloaded.network.mclib.Dispatcher;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfirm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class ClientHandlerConfirm extends ClientMessageHandler<PacketConfirm>
{

    /**
     * Renders the GUI based on the enum value of packet. Every GUI confirmation screen dispatches the packet back to the server
     * @param packet
     */
    @Override
    @SideOnly(Dist.CLIENT)
    public void run(LocalPlayer entityPlayerSP, PacketConfirm packet)
    {
        switch(packet.gui)
        {
            case MCSCREEN:
                Minecraft.getInstance().displayGuiScreen(new GuiConfirmationScreen(packet.langKey, (value) ->
                {
                    this.dispatchPacket(packet, value);
                }));
        }
    }

    private void dispatchPacket(PacketConfirm packet, boolean value)
    {
        packet.confirm = value;

        Dispatcher.sendToServer(packet);
    }

    public enum GUI {
        MCSCREEN;
    }
}
