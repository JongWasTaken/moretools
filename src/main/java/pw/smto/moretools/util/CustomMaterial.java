package pw.smto.moretools.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("unused")
public class CustomMaterial {
    public static CustomMaterial of(ToolMaterial base) {
        return new CustomMaterial(base.durability(), base.speed(), base.attackDamageBonus(), base.incorrectBlocksForDrops(), base.enchantmentValue(), base.repairItems());
    }
    private int durability;
    private float miningSpeedMultiplier;
    private float attackDamage;
    private TagKey<Block> inverseTag;
    private int enchantability;
    private TagKey<Item> repairIngredient;
    public CustomMaterial(int durability, float miningSpeedMultiplier, float attackDamage, TagKey<Block> inverseTag, int enchantability, TagKey<Item> repairIngredient) {
        this.durability = durability;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.attackDamage = attackDamage;
        this.inverseTag = inverseTag;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }
    public int getDurability() {
        return this.durability;
    }
    public CustomMaterial setDurability(int newDurability) {
        this.durability = newDurability;
        return this;
    }
    public CustomMaterial multiplyDurability(int multiplier) {
        this.durability *= multiplier;
        return this;
    }
    public float getMiningSpeedMultiplier() {
        return this.miningSpeedMultiplier;
    }
    public CustomMaterial setMiningSpeedMultiplier(float newMiningSpeedMultiplier) {
        this.miningSpeedMultiplier = newMiningSpeedMultiplier;
        return this;
    }
    public float getAttackDamage() {
        return this.attackDamage;
    }
    public CustomMaterial setAttackDamage(float newAttackDamage) {
        this.attackDamage = newAttackDamage;
        return this;
    }
    public TagKey<Block> getInverseTag() {
        return this.inverseTag;
    }
    public CustomMaterial setInverseTag(TagKey<Block> newInverseTag) {
        this.inverseTag = newInverseTag;
        return this;
    }
    public int getEnchantability() {
        return this.enchantability;
    }
    public CustomMaterial setEnchantability(int newEnchantability) {
        this.enchantability = newEnchantability;
        return this;
    }
    public TagKey<Item> getRepairIngredient() {
        return this.repairIngredient;
    }
    public CustomMaterial setRepairIngredient(TagKey<Item> newRepairIngredient) {
        this.repairIngredient = newRepairIngredient;
        return this;
    }
    public ToolMaterial toVanilla() {
        return new ToolMaterial(this.inverseTag, this.durability, this.miningSpeedMultiplier, this.attackDamage, this.enchantability, this.repairIngredient);
    }
}
