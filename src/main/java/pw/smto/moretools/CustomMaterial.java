package pw.smto.moretools;

import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;

public class CustomMaterial implements ToolMaterial {

    private final ToolMaterial base;
    private final int newDurability;

    public CustomMaterial(ToolMaterial base, int newDurability) {
        this.base = base;
        this.newDurability = newDurability;
    }

    @Override
    public int getDurability() {
        return newDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return base.getMiningSpeedMultiplier();
    }

    @Override
    public float getAttackDamage() {
        return base.getAttackDamage();
    }

    @Override
    public TagKey<Block> getInverseTag() {
        return base.getInverseTag();
    }

    @Override
    public int getEnchantability() {
        return base.getEnchantability();
    }

    @Override
    public Ingredient getRepairIngredient() {
        return base.getRepairIngredient();
    }
}
