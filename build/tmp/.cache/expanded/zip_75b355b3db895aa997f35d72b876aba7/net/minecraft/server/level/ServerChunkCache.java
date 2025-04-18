package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class ServerChunkCache extends ChunkSource {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final DistanceManager distanceManager;
    public final ServerLevel level;
    final Thread mainThread;
    final ThreadedLevelLightEngine lightEngine;
    private final ServerChunkCache.MainThreadExecutor mainThreadProcessor;
    public final ChunkMap chunkMap;
    private final DimensionDataStorage dataStorage;
    private final TicketStorage ticketStorage;
    private long lastInhabitedUpdate;
    private boolean spawnEnemies = true;
    private boolean spawnFriendlies = true;
    private static final int CACHE_SIZE = 4;
    private final long[] lastChunkPos = new long[4];
    private final ChunkStatus[] lastChunkStatus = new ChunkStatus[4];
    private final ChunkAccess[] lastChunk = new ChunkAccess[4];
    private final List<LevelChunk> spawningChunks = new ObjectArrayList<>();
    private final Set<ChunkHolder> chunkHoldersToBroadcast = new ReferenceOpenHashSet<>();
    @Nullable
    @VisibleForDebug
    private NaturalSpawner.SpawnState lastSpawnState;

    public ServerChunkCache(
        ServerLevel p_214982_,
        LevelStorageSource.LevelStorageAccess p_214983_,
        DataFixer p_214984_,
        StructureTemplateManager p_214985_,
        Executor p_214986_,
        ChunkGenerator p_214987_,
        int p_214988_,
        int p_214989_,
        boolean p_214990_,
        ChunkProgressListener p_214991_,
        ChunkStatusUpdateListener p_214992_,
        Supplier<DimensionDataStorage> p_214993_
    ) {
        this.level = p_214982_;
        this.mainThreadProcessor = new ServerChunkCache.MainThreadExecutor(p_214982_);
        this.mainThread = Thread.currentThread();
        Path path = p_214983_.getDimensionPath(p_214982_.dimension()).resolve("data");

        try {
            FileUtil.createDirectoriesSafe(path);
        } catch (IOException ioexception) {
            LOGGER.error("Failed to create dimension data storage directory", (Throwable)ioexception);
        }

        this.dataStorage = new DimensionDataStorage(new SavedData.Context(p_214982_), path, p_214984_, p_214982_.registryAccess());
        this.ticketStorage = this.dataStorage.computeIfAbsent(TicketStorage.TYPE);
        this.chunkMap = new ChunkMap(
            p_214982_,
            p_214983_,
            p_214984_,
            p_214985_,
            p_214986_,
            this.mainThreadProcessor,
            this,
            p_214987_,
            p_214991_,
            p_214992_,
            p_214993_,
            this.ticketStorage,
            p_214988_,
            p_214990_
        );
        this.lightEngine = this.chunkMap.getLightEngine();
        this.distanceManager = this.chunkMap.getDistanceManager();
        this.distanceManager.updateSimulationDistance(p_214989_);
        this.clearCache();
    }

    public ThreadedLevelLightEngine getLightEngine() {
        return this.lightEngine;
    }

    @Nullable
    private ChunkHolder getVisibleChunkIfPresent(long p_8365_) {
        return this.chunkMap.getVisibleChunkIfPresent(p_8365_);
    }

    public int getTickingGenerated() {
        return this.chunkMap.getTickingGenerated();
    }

    private void storeInCache(long p_8367_, @Nullable ChunkAccess p_8368_, ChunkStatus p_333650_) {
        for (int i = 3; i > 0; i--) {
            this.lastChunkPos[i] = this.lastChunkPos[i - 1];
            this.lastChunkStatus[i] = this.lastChunkStatus[i - 1];
            this.lastChunk[i] = this.lastChunk[i - 1];
        }

        this.lastChunkPos[0] = p_8367_;
        this.lastChunkStatus[0] = p_333650_;
        this.lastChunk[0] = p_8368_;
    }

    @Nullable
    @Override
    public ChunkAccess getChunk(int p_8360_, int p_8361_, ChunkStatus p_334940_, boolean p_8363_) {
        if (Thread.currentThread() != this.mainThread) {
            return CompletableFuture.<ChunkAccess>supplyAsync(() -> this.getChunk(p_8360_, p_8361_, p_334940_, p_8363_), this.mainThreadProcessor).join();
        } else {
            ProfilerFiller profilerfiller = Profiler.get();
            profilerfiller.incrementCounter("getChunk");
            long i = ChunkPos.asLong(p_8360_, p_8361_);

            for (int j = 0; j < 4; j++) {
                if (i == this.lastChunkPos[j] && p_334940_ == this.lastChunkStatus[j]) {
                    ChunkAccess chunkaccess = this.lastChunk[j];
                    if (chunkaccess != null || !p_8363_) {
                        return chunkaccess;
                    }
                }
            }

            profilerfiller.incrementCounter("getChunkCacheMiss");
            CompletableFuture<ChunkResult<ChunkAccess>> completablefuture = this.getChunkFutureMainThread(p_8360_, p_8361_, p_334940_, p_8363_);
            this.mainThreadProcessor.managedBlock(completablefuture::isDone);
            ChunkResult<ChunkAccess> chunkresult = completablefuture.join();
            ChunkAccess chunkaccess1 = chunkresult.orElse(null);
            if (chunkaccess1 == null && p_8363_) {
                throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Chunk not there when requested: " + chunkresult.getError()));
            } else {
                this.storeInCache(i, chunkaccess1, p_334940_);
                return chunkaccess1;
            }
        }
    }

    @Nullable
    @Override
    public LevelChunk getChunkNow(int p_8357_, int p_8358_) {
        if (Thread.currentThread() != this.mainThread) {
            return null;
        } else {
            Profiler.get().incrementCounter("getChunkNow");
            long i = ChunkPos.asLong(p_8357_, p_8358_);

            for (int j = 0; j < 4; j++) {
                if (i == this.lastChunkPos[j] && this.lastChunkStatus[j] == ChunkStatus.FULL) {
                    ChunkAccess chunkaccess = this.lastChunk[j];
                    return chunkaccess instanceof LevelChunk ? (LevelChunk)chunkaccess : null;
                }
            }

            ChunkHolder chunkholder = this.getVisibleChunkIfPresent(i);
            if (chunkholder == null) {
                return null;
            } else {
                // Forge: If the requested chunk is loading, bypass the future chain to prevent a deadlock.
                if (chunkholder.currentlyLoading != null) {
                    return chunkholder.currentlyLoading;
                }
                ChunkAccess chunkaccess1 = chunkholder.getChunkIfPresent(ChunkStatus.FULL);
                if (chunkaccess1 != null) {
                    this.storeInCache(i, chunkaccess1, ChunkStatus.FULL);
                    if (chunkaccess1 instanceof LevelChunk) {
                        return (LevelChunk)chunkaccess1;
                    }
                }

                return null;
            }
        }
    }

    private void clearCache() {
        Arrays.fill(this.lastChunkPos, ChunkPos.INVALID_CHUNK_POS);
        Arrays.fill(this.lastChunkStatus, null);
        Arrays.fill(this.lastChunk, null);
    }

    public CompletableFuture<ChunkResult<ChunkAccess>> getChunkFuture(int p_8432_, int p_8433_, ChunkStatus p_329681_, boolean p_8435_) {
        boolean flag = Thread.currentThread() == this.mainThread;
        CompletableFuture<ChunkResult<ChunkAccess>> completablefuture;
        if (flag) {
            completablefuture = this.getChunkFutureMainThread(p_8432_, p_8433_, p_329681_, p_8435_);
            this.mainThreadProcessor.managedBlock(completablefuture::isDone);
        } else {
            completablefuture = CompletableFuture.<CompletableFuture<ChunkResult<ChunkAccess>>>supplyAsync(
                    () -> this.getChunkFutureMainThread(p_8432_, p_8433_, p_329681_, p_8435_), this.mainThreadProcessor
                )
                .thenCompose(p_333930_ -> (CompletionStage<ChunkResult<ChunkAccess>>)p_333930_);
        }

        return completablefuture;
    }

    private CompletableFuture<ChunkResult<ChunkAccess>> getChunkFutureMainThread(int p_8457_, int p_8458_, ChunkStatus p_334479_, boolean p_8460_) {
        ChunkPos chunkpos = new ChunkPos(p_8457_, p_8458_);
        long i = chunkpos.toLong();
        int j = ChunkLevel.byStatus(p_334479_);
        ChunkHolder chunkholder = this.getVisibleChunkIfPresent(i);
        if (p_8460_) {
            this.addTicket(new Ticket(TicketType.UNKNOWN, j), chunkpos);
            if (this.chunkAbsent(chunkholder, j)) {
                ProfilerFiller profilerfiller = Profiler.get();
                profilerfiller.push("chunkLoad");
                this.runDistanceManagerUpdates();
                chunkholder = this.getVisibleChunkIfPresent(i);
                profilerfiller.pop();
                if (this.chunkAbsent(chunkholder, j)) {
                    throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("No chunk holder after ticket has been added"));
                }
            }
        }

        return this.chunkAbsent(chunkholder, j) ? GenerationChunkHolder.UNLOADED_CHUNK_FUTURE : chunkholder.scheduleChunkGenerationTask(p_334479_, this.chunkMap);
    }

    private boolean chunkAbsent(@Nullable ChunkHolder p_8417_, int p_8418_) {
        return p_8417_ == null || p_8417_.getTicketLevel() > p_8418_;
    }

    @Override
    public boolean hasChunk(int p_8429_, int p_8430_) {
        ChunkHolder chunkholder = this.getVisibleChunkIfPresent(new ChunkPos(p_8429_, p_8430_).toLong());
        int i = ChunkLevel.byStatus(ChunkStatus.FULL);
        return !this.chunkAbsent(chunkholder, i);
    }

    @Nullable
    @Override
    public LightChunk getChunkForLighting(int p_8454_, int p_8455_) {
        long i = ChunkPos.asLong(p_8454_, p_8455_);
        ChunkHolder chunkholder = this.getVisibleChunkIfPresent(i);
        return chunkholder == null ? null : chunkholder.getChunkIfPresentUnchecked(ChunkStatus.INITIALIZE_LIGHT.getParent());
    }

    public Level getLevel() {
        return this.level;
    }

    public boolean pollTask() {
        return this.mainThreadProcessor.pollTask();
    }

    boolean runDistanceManagerUpdates() {
        boolean flag = this.distanceManager.runAllUpdates(this.chunkMap);
        boolean flag1 = this.chunkMap.promoteChunkMap();
        this.chunkMap.runGenerationTasks();
        if (!flag && !flag1) {
            return false;
        } else {
            this.clearCache();
            return true;
        }
    }

    public boolean isPositionTicking(long p_143240_) {
        if (!this.level.shouldTickBlocksAt(p_143240_)) {
            return false;
        } else {
            ChunkHolder chunkholder = this.getVisibleChunkIfPresent(p_143240_);
            return chunkholder == null ? false : chunkholder.getTickingChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).isSuccess();
        }
    }

    public void save(boolean p_8420_) {
        this.runDistanceManagerUpdates();
        this.chunkMap.saveAllChunks(p_8420_);
    }

    @Override
    public void close() throws IOException {
        this.save(true);
        this.dataStorage.close();
        this.lightEngine.close();
        this.chunkMap.close();
    }

    @Override
    public void tick(BooleanSupplier p_201913_, boolean p_201914_) {
        ProfilerFiller profilerfiller = Profiler.get();
        profilerfiller.push("purge");
        if (this.level.tickRateManager().runsNormally() || !p_201914_) {
            this.ticketStorage.purgeStaleTickets();
        }

        this.runDistanceManagerUpdates();
        profilerfiller.popPush("chunks");
        if (p_201914_) {
            this.tickChunks();
            this.chunkMap.tick();
        }

        profilerfiller.popPush("unload");
        this.chunkMap.tick(p_201913_);
        profilerfiller.pop();
        this.clearCache();
    }

    private void tickChunks() {
        long i = this.level.getGameTime();
        long j = i - this.lastInhabitedUpdate;
        this.lastInhabitedUpdate = i;
        if (!this.level.isDebug()) {
            ProfilerFiller profilerfiller = Profiler.get();
            profilerfiller.push("pollingChunks");
            if (this.level.tickRateManager().runsNormally()) {
                profilerfiller.push("tickingChunks");
                this.tickChunks(profilerfiller, j);
                profilerfiller.pop();
            }

            this.broadcastChangedChunks(profilerfiller);
            profilerfiller.pop();
        }
    }

    private void broadcastChangedChunks(ProfilerFiller p_369706_) {
        p_369706_.push("broadcast");

        for (ChunkHolder chunkholder : this.chunkHoldersToBroadcast) {
            LevelChunk levelchunk = chunkholder.getTickingChunk();
            if (levelchunk != null) {
                chunkholder.broadcastChanges(levelchunk);
            }
        }

        this.chunkHoldersToBroadcast.clear();
        p_369706_.pop();
    }

    private void tickChunks(ProfilerFiller p_368327_, long p_362313_) {
        p_368327_.popPush("naturalSpawnCount");
        int i = this.distanceManager.getNaturalSpawnChunkCount();
        NaturalSpawner.SpawnState naturalspawner$spawnstate = NaturalSpawner.createState(
            i, this.level.getAllEntities(), this::getFullChunk, new LocalMobCapCalculator(this.chunkMap)
        );
        this.lastSpawnState = naturalspawner$spawnstate;
        p_368327_.popPush("spawnAndTick");
        boolean flag = this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
        int j = this.level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
        List<MobCategory> list;
        if (flag && (this.spawnEnemies || this.spawnFriendlies)) {
            boolean flag1 = this.level.getLevelData().getGameTime() % 400L == 0L;
            list = NaturalSpawner.getFilteredSpawningCategories(naturalspawner$spawnstate, this.spawnFriendlies, this.spawnEnemies, flag1);
        } else {
            list = List.of();
        }

        List<LevelChunk> list1 = this.spawningChunks;

        try {
            p_368327_.push("filteringSpawningChunks");
            this.chunkMap.collectSpawningChunks(list1);
            p_368327_.popPush("shuffleSpawningChunks");
            Util.shuffle(list1, this.level.random);
            p_368327_.popPush("tickSpawningChunks");

            for (LevelChunk levelchunk : list1) {
                this.tickSpawningChunk(levelchunk, p_362313_, list, naturalspawner$spawnstate);
            }
        } finally {
            list1.clear();
        }

        p_368327_.popPush("tickTickingChunks");
        this.chunkMap.forEachBlockTickingChunk(p_390143_ -> this.level.tickChunk(p_390143_, j));
        p_368327_.pop();
        p_368327_.popPush("customSpawners");
        if (flag) {
            this.level.tickCustomSpawners(this.spawnEnemies, this.spawnFriendlies);
        }
    }

    private void tickSpawningChunk(LevelChunk p_394359_, long p_392468_, List<MobCategory> p_396237_, NaturalSpawner.SpawnState p_392979_) {
        ChunkPos chunkpos = p_394359_.getPos();
        p_394359_.incrementInhabitedTime(p_392468_);
        if (this.distanceManager.inEntityTickingRange(chunkpos.toLong())) {
            this.level.tickThunder(p_394359_);
        }

        if (!p_396237_.isEmpty()) {
            if (this.level.canSpawnEntitiesInChunk(chunkpos)) {
                NaturalSpawner.spawnForChunk(this.level, p_394359_, p_392979_, p_396237_);
            }
        }
    }

    private void getFullChunk(long p_8371_, Consumer<LevelChunk> p_8372_) {
        ChunkHolder chunkholder = this.getVisibleChunkIfPresent(p_8371_);
        if (chunkholder != null) {
            chunkholder.getFullChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).ifSuccess(p_8372_);
        }
    }

    @Override
    public String gatherStats() {
        return Integer.toString(this.getLoadedChunksCount());
    }

    @VisibleForTesting
    public int getPendingTasksCount() {
        return this.mainThreadProcessor.getPendingTasksCount();
    }

    public ChunkGenerator getGenerator() {
        return this.chunkMap.generator();
    }

    public ChunkGeneratorStructureState getGeneratorState() {
        return this.chunkMap.generatorState();
    }

    public RandomState randomState() {
        return this.chunkMap.randomState();
    }

    @Override
    public int getLoadedChunksCount() {
        return this.chunkMap.size();
    }

    public void blockChanged(BlockPos p_8451_) {
        int i = SectionPos.blockToSectionCoord(p_8451_.getX());
        int j = SectionPos.blockToSectionCoord(p_8451_.getZ());
        ChunkHolder chunkholder = this.getVisibleChunkIfPresent(ChunkPos.asLong(i, j));
        if (chunkholder != null && chunkholder.blockChanged(p_8451_)) {
            this.chunkHoldersToBroadcast.add(chunkholder);
        }
    }

    @Override
    public void onLightUpdate(LightLayer p_8403_, SectionPos p_8404_) {
        this.mainThreadProcessor.execute(() -> {
            ChunkHolder chunkholder = this.getVisibleChunkIfPresent(p_8404_.chunk().toLong());
            if (chunkholder != null && chunkholder.sectionLightChanged(p_8403_, p_8404_.y())) {
                this.chunkHoldersToBroadcast.add(chunkholder);
            }
        });
    }

    public void addTicket(Ticket p_392821_, ChunkPos p_393687_) {
        this.ticketStorage.addTicket(p_392821_, p_393687_);
    }

    public void addTicketWithRadius(TicketType p_391769_, ChunkPos p_392988_, int p_397576_) {
        this.ticketStorage.addTicketWithRadius(p_391769_, p_392988_, p_397576_);
    }

    public void removeTicketWithRadius(TicketType p_392479_, ChunkPos p_394338_, int p_393500_) {
        this.ticketStorage.removeTicketWithRadius(p_392479_, p_394338_, p_393500_);
    }

    @Override
    public boolean updateChunkForced(ChunkPos p_8400_, boolean p_8401_) {
        return this.ticketStorage.updateChunkForced(p_8400_, p_8401_);
    }

    @Override
    public LongSet getForceLoadedChunks() {
        return this.ticketStorage.getForceLoadedChunks();
    }

    public void move(ServerPlayer p_8386_) {
        if (!p_8386_.isRemoved()) {
            this.chunkMap.move(p_8386_);
        }
    }

    public void removeEntity(Entity p_8444_) {
        this.chunkMap.removeEntity(p_8444_);
    }

    public void addEntity(Entity p_8464_) {
        this.chunkMap.addEntity(p_8464_);
    }

    public void broadcastAndSend(Entity p_8395_, Packet<?> p_8396_) {
        this.chunkMap.broadcastAndSend(p_8395_, p_8396_);
    }

    public void broadcast(Entity p_8446_, Packet<?> p_8447_) {
        this.chunkMap.broadcast(p_8446_, p_8447_);
    }

    public void setViewDistance(int p_8355_) {
        this.chunkMap.setServerViewDistance(p_8355_);
    }

    public void setSimulationDistance(int p_184027_) {
        this.distanceManager.updateSimulationDistance(p_184027_);
    }

    @Override
    public void setSpawnSettings(boolean p_8425_) {
        this.spawnEnemies = p_8425_;
        this.spawnFriendlies = this.spawnFriendlies;
    }

    public String getChunkDebugData(ChunkPos p_8449_) {
        return this.chunkMap.getChunkDebugData(p_8449_);
    }

    public DimensionDataStorage getDataStorage() {
        return this.dataStorage;
    }

    public PoiManager getPoiManager() {
        return this.chunkMap.getPoiManager();
    }

    public ChunkScanAccess chunkScanner() {
        return this.chunkMap.chunkScanner();
    }

    @Nullable
    @VisibleForDebug
    public NaturalSpawner.SpawnState getLastSpawnState() {
        return this.lastSpawnState;
    }

    public void deactivateTicketsOnClosing() {
        this.ticketStorage.deactivateTicketsOnClosing();
    }

    public void onChunkReadyToSend(ChunkHolder p_370261_) {
        if (p_370261_.hasChangesToBroadcast()) {
            this.chunkHoldersToBroadcast.add(p_370261_);
        }
    }

    final class MainThreadExecutor extends BlockableEventLoop<Runnable> {
        MainThreadExecutor(final Level p_8494_) {
            super("Chunk source main thread executor for " + p_8494_.dimension().location());
        }

        @Override
        public void managedBlock(BooleanSupplier p_344943_) {
            super.managedBlock(() -> MinecraftServer.throwIfFatalException() && p_344943_.getAsBoolean());
        }

        @Override
        public Runnable wrapRunnable(Runnable p_8506_) {
            return p_8506_;
        }

        @Override
        protected boolean shouldRun(Runnable p_8504_) {
            return true;
        }

        @Override
        protected boolean scheduleExecutables() {
            return true;
        }

        @Override
        protected Thread getRunningThread() {
            return ServerChunkCache.this.mainThread;
        }

        @Override
        protected void doRunTask(Runnable p_8502_) {
            Profiler.get().incrementCounter("runTask");
            super.doRunTask(p_8502_);
        }

        @Override
        public boolean pollTask() {
            if (ServerChunkCache.this.runDistanceManagerUpdates()) {
                return true;
            } else {
                ServerChunkCache.this.lightEngine.tryScheduleUpdate();
                return super.pollTask();
            }
        }
    }

    /** @deprecated Use {@link #addTicketWithRadius(TicketType, ChunkPos, int)} with {@link TicketType#FORCED} */
    @Deprecated(forRemoval = true, since = "1.21.5")
    public void addRegionTicket(TicketType type, ChunkPos pos, int ticketLevel, boolean forceTicks) {
        this.addTicketWithRadius(forceTicks ? TicketType.FORCED : type, pos, ticketLevel);
    }

    /** @deprecated Use {@link #removeTicketWithRadius(TicketType, ChunkPos, int)} with {@link TicketType#FORCED} */
    @Deprecated(forRemoval = true, since = "1.21.5")
    public void removeRegionTicket(TicketType type, ChunkPos pos, int ticketLevel, boolean forceTicks) {
        this.removeTicketWithRadius(forceTicks ? TicketType.FORCED : type, pos, ticketLevel);
    }
}
