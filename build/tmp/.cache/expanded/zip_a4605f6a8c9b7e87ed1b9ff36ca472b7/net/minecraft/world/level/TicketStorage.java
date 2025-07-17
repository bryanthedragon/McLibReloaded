package net.minecraft.world.level;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.slf4j.Logger;

public class TicketStorage extends SavedData {
    private static final int INITIAL_TICKET_LIST_CAPACITY = 4;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Codec<Pair<ChunkPos, Ticket>> TICKET_ENTRY = Codec.mapPair(ChunkPos.CODEC.fieldOf("chunk_pos"), Ticket.CODEC).codec();
    public static final Codec<TicketStorage> CODEC = RecordCodecBuilder.create(
        p_395442_ -> p_395442_.group(TICKET_ENTRY.listOf().optionalFieldOf("tickets", List.of()).forGetter(TicketStorage::packTickets))
            .apply(p_395442_, TicketStorage::fromPacked)
    );
    public static final SavedDataType<TicketStorage> TYPE = new SavedDataType<>(
        "chunks", TicketStorage::new, CODEC, DataFixTypes.SAVED_DATA_FORCED_CHUNKS
    );
    private final Long2ObjectOpenHashMap<List<Ticket>> tickets;
    private final Long2ObjectOpenHashMap<List<Ticket>> deactivatedTickets;
    private LongSet chunksWithForcedTickets = new LongOpenHashSet();
    @Nullable
    private TicketStorage.ChunkUpdated loadingChunkUpdatedListener;
    @Nullable
    private TicketStorage.ChunkUpdated simulationChunkUpdatedListener;

    private TicketStorage(Long2ObjectOpenHashMap<List<Ticket>> p_392328_, Long2ObjectOpenHashMap<List<Ticket>> p_396095_) {
        this.tickets = p_392328_;
        this.deactivatedTickets = p_396095_;
        this.updateForcedChunks();
    }

    public TicketStorage() {
        this(new Long2ObjectOpenHashMap<>(4), new Long2ObjectOpenHashMap<>());
    }

    private static TicketStorage fromPacked(List<Pair<ChunkPos, Ticket>> p_392693_) {
        Long2ObjectOpenHashMap<List<Ticket>> long2objectopenhashmap = new Long2ObjectOpenHashMap<>();

        for (Pair<ChunkPos, Ticket> pair : p_392693_) {
            ChunkPos chunkpos = pair.getFirst();
            List<Ticket> list = long2objectopenhashmap.computeIfAbsent(chunkpos.toLong(), p_396965_ -> new ObjectArrayList<>(4));
            list.add(pair.getSecond());
        }

        return new TicketStorage(new Long2ObjectOpenHashMap<>(4), long2objectopenhashmap);
    }

    private List<Pair<ChunkPos, Ticket>> packTickets() {
        List<Pair<ChunkPos, Ticket>> list = new ArrayList<>();
        this.forEachTicket((p_397558_, p_396676_) -> {
            if (p_396676_.getType().persist()) {
                list.add(new Pair<>(p_397558_, p_396676_));
            }
        });
        return list;
    }

    private void forEachTicket(BiConsumer<ChunkPos, Ticket> p_394872_) {
        forEachTicket(p_394872_, this.tickets);
        forEachTicket(p_394872_, this.deactivatedTickets);
    }

    private static void forEachTicket(BiConsumer<ChunkPos, Ticket> p_392035_, Long2ObjectOpenHashMap<List<Ticket>> p_392917_) {
        for (Entry<List<Ticket>> entry : Long2ObjectMaps.fastIterable(p_392917_)) {
            ChunkPos chunkpos = new ChunkPos(entry.getLongKey());

            for (Ticket ticket : entry.getValue()) {
                p_392035_.accept(chunkpos, ticket);
            }
        }
    }

    public void activateAllDeactivatedTickets() {
        for (Entry<List<Ticket>> entry : Long2ObjectMaps.fastIterable(this.deactivatedTickets)) {
            for (Ticket ticket : entry.getValue()) {
                this.addTicket(entry.getLongKey(), ticket);
            }
        }

        this.deactivatedTickets.clear();
    }

    public void setLoadingChunkUpdatedListener(@Nullable TicketStorage.ChunkUpdated p_395306_) {
        this.loadingChunkUpdatedListener = p_395306_;
    }

    public void setSimulationChunkUpdatedListener(@Nullable TicketStorage.ChunkUpdated p_394573_) {
        this.simulationChunkUpdatedListener = p_394573_;
    }

    public boolean hasTickets() {
        return !this.tickets.isEmpty();
    }

    public List<Ticket> getTickets(long p_394970_) {
        return this.tickets.getOrDefault(p_394970_, List.of());
    }

    private List<Ticket> getOrCreateTickets(long p_391734_) {
        return this.tickets.computeIfAbsent(p_391734_, p_395686_ -> new ObjectArrayList<>(4));
    }

    public void addTicketWithRadius(TicketType p_397648_, ChunkPos p_392624_, int p_397085_) {
        Ticket ticket = new Ticket(p_397648_, ChunkLevel.byStatus(FullChunkStatus.FULL) - p_397085_);
        this.addTicket(p_392624_.toLong(), ticket);
    }

    public void addTicket(Ticket p_391314_, ChunkPos p_393095_) {
        this.addTicket(p_393095_.toLong(), p_391314_);
    }

    public boolean addTicket(long p_391964_, Ticket p_394243_) {
        List<Ticket> list = this.getOrCreateTickets(p_391964_);

        for (Ticket ticket : list) {
            if (isTicketSameTypeAndLevel(p_394243_, ticket)) {
                ticket.resetTicksLeft();
                this.setDirty();
                return false;
            }
        }

        int i = getTicketLevelAt(list, true);
        int j = getTicketLevelAt(list, false);
        list.add(p_394243_);
        if (p_394243_.getType().doesSimulate() && p_394243_.getTicketLevel() < i && this.simulationChunkUpdatedListener != null) {
            this.simulationChunkUpdatedListener.update(p_391964_, p_394243_.getTicketLevel(), true);
        }

        if (p_394243_.getType().doesLoad() && p_394243_.getTicketLevel() < j && this.loadingChunkUpdatedListener != null) {
            this.loadingChunkUpdatedListener.update(p_391964_, p_394243_.getTicketLevel(), true);
        }

        if (p_394243_.getType().equals(TicketType.FORCED)) {
            this.chunksWithForcedTickets.add(p_391964_);
        }

        this.setDirty();
        return true;
    }

    private static boolean isTicketSameTypeAndLevel(Ticket p_394527_, Ticket p_393032_) {
        return p_393032_.getType() == p_394527_.getType() && p_393032_.getTicketLevel() == p_394527_.getTicketLevel();
    }

    public int getTicketLevelAt(long p_397585_, boolean p_392636_) {
        return getTicketLevelAt(this.getTickets(p_397585_), p_392636_);
    }

    private static int getTicketLevelAt(List<Ticket> p_394180_, boolean p_396295_) {
        Ticket ticket = getLowestTicket(p_394180_, p_396295_);
        return ticket == null ? ChunkLevel.MAX_LEVEL + 1 : ticket.getTicketLevel();
    }

    @Nullable
    private static Ticket getLowestTicket(@Nullable List<Ticket> p_394356_, boolean p_394342_) {
        if (p_394356_ == null) {
            return null;
        } else {
            Ticket ticket = null;

            for (Ticket ticket1 : p_394356_) {
                if (ticket == null || ticket1.getTicketLevel() < ticket.getTicketLevel()) {
                    if (p_394342_ && ticket1.getType().doesSimulate()) {
                        ticket = ticket1;
                    } else if (!p_394342_ && ticket1.getType().doesLoad()) {
                        ticket = ticket1;
                    }
                }
            }

            return ticket;
        }
    }

    public void removeTicketWithRadius(TicketType p_393730_, ChunkPos p_393037_, int p_393610_) {
        Ticket ticket = new Ticket(p_393730_, ChunkLevel.byStatus(FullChunkStatus.FULL) - p_393610_);
        this.removeTicket(p_393037_.toLong(), ticket);
    }

    public void removeTicket(Ticket p_392695_, ChunkPos p_391186_) {
        this.removeTicket(p_391186_.toLong(), p_392695_);
    }

    public boolean removeTicket(long p_397743_, Ticket p_395417_) {
        List<Ticket> list = this.tickets.get(p_397743_);
        if (list == null) {
            return false;
        } else {
            boolean flag = false;
            Iterator<Ticket> iterator = list.iterator();

            while (iterator.hasNext()) {
                Ticket ticket = iterator.next();
                if (isTicketSameTypeAndLevel(p_395417_, ticket)) {
                    iterator.remove();
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return false;
            } else {
                if (list.isEmpty()) {
                    this.tickets.remove(p_397743_);
                }

                if (p_395417_.getType().doesSimulate() && this.simulationChunkUpdatedListener != null) {
                    this.simulationChunkUpdatedListener.update(p_397743_, getTicketLevelAt(list, true), false);
                }

                if (p_395417_.getType().doesLoad() && this.loadingChunkUpdatedListener != null) {
                    this.loadingChunkUpdatedListener.update(p_397743_, getTicketLevelAt(list, false), false);
                }

                if (p_395417_.getType().equals(TicketType.FORCED)) {
                    this.updateForcedChunks();
                }

                this.setDirty();
                return true;
            }
        }
    }

    private void updateForcedChunks() {
        this.chunksWithForcedTickets = this.getAllChunksWithTicketThat(p_394883_ -> p_394883_.getType().equals(TicketType.FORCED));
    }

    public String getTicketDebugString(long p_393984_, boolean p_394252_) {
        List<Ticket> list = this.getTickets(p_393984_);
        Ticket ticket = getLowestTicket(list, p_394252_);
        return ticket == null ? "no_ticket" : ticket.toString();
    }

    public void purgeStaleTickets() {
        this.removeTicketIf(p_395227_ -> {
            p_395227_.decreaseTicksLeft();
            return p_395227_.isTimedOut();
        }, null);
        this.setDirty();
    }

    public void deactivateTicketsOnClosing() {
        this.removeTicketIf(p_392990_ -> p_392990_.getType() != TicketType.UNKNOWN, this.deactivatedTickets);
    }

    public void removeTicketIf(Predicate<Ticket> p_394101_, @Nullable Long2ObjectOpenHashMap<List<Ticket>> p_396309_) {
        ObjectIterator<Entry<List<Ticket>>> objectiterator = this.tickets.long2ObjectEntrySet().fastIterator();
        boolean flag = false;

        while (objectiterator.hasNext()) {
            Entry<List<Ticket>> entry = objectiterator.next();
            Iterator<Ticket> iterator = entry.getValue().iterator();
            boolean flag1 = false;
            boolean flag2 = false;

            while (iterator.hasNext()) {
                Ticket ticket = iterator.next();
                if (p_394101_.test(ticket)) {
                    if (p_396309_ != null) {
                        List<Ticket> list = p_396309_.computeIfAbsent(entry.getLongKey(), p_394290_ -> new ObjectArrayList<>(entry.getValue().size()));
                        list.add(ticket);
                    }

                    iterator.remove();
                    if (ticket.getType().doesLoad()) {
                        flag2 = true;
                    }

                    if (ticket.getType().doesSimulate()) {
                        flag1 = true;
                    }

                    if (ticket.getType().equals(TicketType.FORCED)) {
                        flag = true;
                    }
                }
            }

            if (flag2 || flag1) {
                if (flag2 && this.loadingChunkUpdatedListener != null) {
                    this.loadingChunkUpdatedListener.update(entry.getLongKey(), getTicketLevelAt(entry.getValue(), false), false);
                }

                if (flag1 && this.simulationChunkUpdatedListener != null) {
                    this.simulationChunkUpdatedListener.update(entry.getLongKey(), getTicketLevelAt(entry.getValue(), true), false);
                }

                this.setDirty();
                if (entry.getValue().isEmpty()) {
                    objectiterator.remove();
                }
            }
        }

        if (flag) {
            this.updateForcedChunks();
        }
    }

    public void replaceTicketLevelOfType(int p_391433_, TicketType p_396214_) {
        List<Pair<Ticket, Long>> list = new ArrayList<>();

        for (Entry<List<Ticket>> entry : this.tickets.long2ObjectEntrySet()) {
            for (Ticket ticket : entry.getValue()) {
                if (ticket.getType() == p_396214_) {
                    list.add(Pair.of(ticket, entry.getLongKey()));
                }
            }
        }

        for (Pair<Ticket, Long> pair : list) {
            Long olong = pair.getSecond();
            Ticket ticket1 = pair.getFirst();
            this.removeTicket(olong, ticket1);
            TicketType tickettype = ticket1.getType();
            this.addTicket(olong, new Ticket(tickettype, p_391433_));
        }
    }

    public boolean updateChunkForced(ChunkPos p_392116_, boolean p_394247_) {
        Ticket ticket = new Ticket(TicketType.FORCED, ChunkMap.FORCED_TICKET_LEVEL);
        return p_394247_ ? this.addTicket(p_392116_.toLong(), ticket) : this.removeTicket(p_392116_.toLong(), ticket);
    }

    public LongSet getForceLoadedChunks() {
        return this.chunksWithForcedTickets;
    }

    private LongSet getAllChunksWithTicketThat(Predicate<Ticket> p_397128_) {
        LongOpenHashSet longopenhashset = new LongOpenHashSet();

        for (Entry<List<Ticket>> entry : Long2ObjectMaps.fastIterable(this.tickets)) {
            for (Ticket ticket : entry.getValue()) {
                if (p_397128_.test(ticket)) {
                    longopenhashset.add(entry.getLongKey());
                    break;
                }
            }
        }

        return longopenhashset;
    }

    @FunctionalInterface
    public interface ChunkUpdated {
        void update(long p_392076_, int p_391279_, boolean p_392790_);
    }
}