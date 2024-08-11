package pw.smto.moretools.util;

import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;

public class MutableMaterial implements ToolMaterial {
    public static MutableMaterial of(ToolMaterial base) {
        return new MutableMaterial(base.getDurability(), base.getMiningSpeedMultiplier(), base.getAttackDamage(), base.getInverseTag(), base.getEnchantability(),base.getRepairIngredient());
    }
    private int durability;
    private float miningSpeedMultiplier;
    private float attackDamage;
    private TagKey<Block> inverseTag;
    private int enchantability;
    private Ingredient repairIngredient;
    public MutableMaterial(int durability, float miningSpeedMultiplier, float attackDamage, TagKey<Block> inverseTag, int enchantability, Ingredient repairIngredient) {
        this.durability = durability;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.attackDamage = attackDamage;
        this.inverseTag = inverseTag;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }
    @Override
    public int getDurability() {
        return this.durability;
    }
    public MutableMaterial setDurability(int newDurability) {
        this.durability = newDurability;
        return this;
    }
    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeedMultiplier;
    }
    public MutableMaterial setMiningSpeedMultiplier(float newMiningSpeedMultiplier) {
        this.miningSpeedMultiplier = newMiningSpeedMultiplier;
        return this;
    }
    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }
    public MutableMaterial setAttackDamage(float newAttackDamage) {
        this.attackDamage = newAttackDamage;
        return this;
    }
    @Override
    public TagKey<Block> getInverseTag() {
        return this.inverseTag;
    }
    public MutableMaterial setInverseTag(TagKey<Block> newInverseTag) {
        this.inverseTag = newInverseTag;
        return this;
    }
    @Override
    public int getEnchantability() {
        return this.enchantability;
    }
    public MutableMaterial setEnchantability(int newEnchantability) {
        this.enchantability = newEnchantability;
        return this;
    }
    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient;
    }
    public MutableMaterial setRepairIngredient(Ingredient newRepairIngredient) {
        this.repairIngredient = newRepairIngredient;
        return this;
    }
}
