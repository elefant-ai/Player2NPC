//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.goodbird.player2npc.network;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Streams;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.player2.playerengine.player2api.Character;
import com.goodbird.player2npc.Player2NPC;
import com.goodbird.player2npc.companion.AutomatoneEntity;
import com.player2.playerengine.automaton.api.entity.LivingEntityInventory;
import com.player2.playerengine.player2api.utils.CharacterUtils;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;

import java.util.*;
import java.util.stream.Stream;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.*;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class AutomatonSpawnPacket {
    private final int id;
    private final UUID uuid;
    private final Vec3 pos;
    private final Vec3 velocity;
    private final float pitch;
    private final float yaw;
    private final Character character;
    private final LivingEntityInventory inventory;

    private AutomatonSpawnPacket(AutomatoneEntity entity) {
        this.id = entity.getId();
        this.uuid = entity.getUUID();
        this.pos = entity.position();
        this.velocity = entity.getDeltaMovement();
        this.pitch = entity.getXRot();
        this.yaw = entity.getYRot();
        this.character = entity.getCharacter();
        this.inventory = entity.inventory;
    }

    public AutomatonSpawnPacket(RegistryFriendlyByteBuf buf) {
        this.id = buf.readVarInt();
        this.uuid = buf.readUUID();
        this.pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.velocity = new Vec3((double)buf.readShort(), (double)buf.readShort(), (double)buf.readShort());
        this.pitch = (float)(buf.readByte() * 360) / 256.0F;
        this.yaw = (float)(buf.readByte() * 360) / 256.0F;
        this.character = CharacterUtils.readFromBuf(buf);
        this.inventory = new LivingEntityInventory((LivingEntity)null);
        TypedInputListWrapper<ItemStackWithSlot> wrapper = new TypedInputListWrapper<ItemStackWithSlot>(ProblemReporter.DISCARDING, "inv", new ValueInputContextHelper(buf.registryAccess(), NbtOps.INSTANCE), ItemStackWithSlot.CODEC, buf.readNbt().getList("inv").get());
        this.inventory.readNbt(wrapper);
    }

    public static Packet<ClientGamePacketListener> create(RegistryAccess access, AutomatoneEntity entity) {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), access);
        (new AutomatonSpawnPacket(entity)).write(access, buf);
        return (Packet<ClientGamePacketListener>) NetworkManager.toPacket(NetworkManager.Side.S2C, Player2NPC.SPAWN_PACKET_ID, buf);
    }

    public void write(RegistryAccess access, FriendlyByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeUUID(this.uuid);
        buf.writeDouble(this.pos.x);
        buf.writeDouble(this.pos.y);
        buf.writeDouble(this.pos.z);
        buf.writeShort((int)(Math.min(3.9, this.velocity.x) * (double)8000.0F));
        buf.writeShort((int)(Math.min(3.9, this.velocity.y) * (double)8000.0F));
        buf.writeShort((int)(Math.min(3.9, this.velocity.z) * (double)8000.0F));
        buf.writeByte((byte)((int)(this.pitch * 256.0F / 360.0F)));
        buf.writeByte((byte)((int)(this.yaw * 256.0F / 360.0F)));
        CharacterUtils.writeToBuf(buf, this.character);
        CompoundTag compound = new CompoundTag();
        ListTag list = new ListTag();
        TypedOutputListWrapper<ItemStackWithSlot> wrapper = new TypedOutputListWrapper<>(ProblemReporter.DISCARDING, "inv", NbtOps.INSTANCE, ItemStackWithSlot.CODEC, list);
        this.inventory.writeNbt(wrapper);
        compound.put("inv", list);
        buf.writeNbt(compound);
    }

    public static void handle(RegistryFriendlyByteBuf var3, NetworkManager.PacketContext var4) {
        AutomatonSpawnPacket packet = new AutomatonSpawnPacket(var3);
        var4.queue(() -> {
            ClientLevel world = (ClientLevel) var4.getPlayer().level();
            AutomatoneEntity entity = new AutomatoneEntity(Player2NPC.AUTOMATONE.get(), world);
            entity.setId(packet.id);
            entity.setUUID(packet.uuid);
            entity.syncPacketPositionCodec(packet.pos.x, packet.pos.y, packet.pos.z);
            entity.snapTo(packet.pos.x, packet.pos.y, packet.pos.z, packet.pitch, packet.yaw);
            entity.setDeltaMovement(packet.velocity);
            entity.setCharacter(packet.character);
            packet.inventory.player = entity;
            entity.inventory = packet.inventory;
            world.addEntity(entity);
        });
    }

    static class TypedInputListWrapper<T> implements ValueInput.TypedInputList<T> {
        private final ProblemReporter problemReporter;
        private final String name;
        final ValueInputContextHelper context;
        final Codec<T> codec;
        private final ListTag list;

        TypedInputListWrapper(ProblemReporter problemReporter, String string, ValueInputContextHelper valueInputContextHelper, Codec<T> codec, ListTag listTag) {
            this.problemReporter = problemReporter;
            this.name = string;
            this.context = valueInputContextHelper;
            this.codec = codec;
            this.list = listTag;
        }

        public boolean isEmpty() {
            return this.list.isEmpty();
        }

        void reportIndexUnwrapProblem(int i, Tag tag, DataResult.Error<?> error) {
            this.problemReporter.report(new TagValueInput.DecodeFromListFailedProblem(this.name, i, tag, error));
        }

        public Stream<T> stream() {
            return Streams.mapWithIndex(this.list.stream(), (tag, l) -> {
                DataResult<T> var10000 = this.codec.parse(this.context.ops(), tag);
                T var8;
                switch (var10000) {
                    case DataResult.Success<T> success:
                        var8 = success.value();
                        break;
                    case DataResult.Error<T> error:
                        this.reportIndexUnwrapProblem((int)l, tag, error);
                        var8 = error.partialValue().orElse(null);
                        break;
                }

                return var8;
            }).filter(Objects::nonNull);
        }

        public Iterator<T> iterator() {
            final ListIterator<Tag> listIterator = this.list.listIterator();
            return new AbstractIterator<>() {
                @Nullable
                protected T computeNext() {
                    while (true) {
                        if (listIterator.hasNext()) {
                            int i = listIterator.nextIndex();
                            Tag tag = listIterator.next();
                            DataResult<T> var10000 = codec.parse(context.ops(), tag);
                            switch (var10000) {
                                case DataResult.Success<T> success:
                                    return success.value();
                                case DataResult.Error<T> error:
                                    reportIndexUnwrapProblem(i, tag, error);
                                    if (!error.partialValue().isPresent()) {
                                        continue;
                                    }

                                    return error.partialValue().get();
                            }
                        }

                        return this.endOfData();
                    }
                }
            };
        }
    }


    static class TypedOutputListWrapper<T> implements ValueOutput.TypedOutputList<T> {
        private final ProblemReporter problemReporter;
        private final String name;
        private final DynamicOps<Tag> ops;
        private final Codec<T> codec;
        private final ListTag output;

        TypedOutputListWrapper(ProblemReporter problemReporter, String string, DynamicOps<Tag> dynamicOps, Codec<T> codec, ListTag listTag) {
            this.problemReporter = problemReporter;
            this.name = string;
            this.ops = dynamicOps;
            this.codec = codec;
            this.output = listTag;
        }

        public void add(T object) {
            DataResult<Tag> var10000 = this.codec.encodeStart(this.ops, object);
            switch (var10000) {
                case DataResult.Success<Tag> success:
                    this.output.add(success.value());
                    break;
                case DataResult.Error<Tag> error:
                    this.problemReporter.report(new TagValueOutput.EncodeToListFailedProblem(this.name, object, error));
                    Optional<Tag> var6 = error.partialValue();
                    ListTag var10001 = this.output;
                    Objects.requireNonNull(var10001);
                    var6.ifPresent(var10001::add);
                    break;
            }

        }

        public boolean isEmpty() {
            return this.output.isEmpty();
        }
    }
}
