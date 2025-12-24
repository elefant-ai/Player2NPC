package com.goodbird.player2npc.mixins.impl;

import com.goodbird.player2npc.mixins.IEntityPersistentData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Entity.class)
public class EntityPersistentData implements IEntityPersistentData {
    @Unique
    private CompoundTag CNPC_tag;

    @Unique
    public CompoundTag getPersistentData(){
        if(CNPC_tag==null){
            CNPC_tag = new CompoundTag();
        }
        return CNPC_tag;
    }

    @Inject(method = "saveWithoutId",at=@At("TAIL"))
    public void save(ValueOutput valueOutput, CallbackInfo ci){
        if(CNPC_tag!=null && ((Object)this) instanceof LivingEntity) {
            valueOutput.store("Player2NPC_persistantData", CompoundTag.CODEC, CNPC_tag);
        }
    }

    @Inject(method = "load",at=@At("TAIL"))
    public void read(ValueInput valueInput, CallbackInfo ci){
        if(((Object)this) instanceof LivingEntity) {
            Optional<CompoundTag> tag = valueInput.read("Player2NPC_persistantData", CompoundTag.CODEC);
            tag.ifPresent(compoundTag -> CNPC_tag = compoundTag);
        }
    }
}
