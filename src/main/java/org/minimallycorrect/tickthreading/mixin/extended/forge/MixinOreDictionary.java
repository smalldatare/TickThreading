package org.minimallycorrect.tickthreading.mixin.extended.forge;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.val;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.oredict.OreDictionary;
import org.minimallycorrect.mixin.Mixin;
import org.minimallycorrect.mixin.OverrideStatic;

import java.util.*;

@Mixin
public abstract class MixinOreDictionary extends OreDictionary {
	@OverrideStatic
	public static int[] getOreIDs(ItemStack stack) {
		if (stack == null)
			throw new IllegalArgumentException("Stack can not be null!");

		val item = stack.getItem();
		if (item == null)
			throw new IllegalArgumentException("Stack can not be null!");

		val delegate = item.delegate;
		val registryName = delegate.name();
		if (registryName == null) {
			FMLLog.log.debug("Attempted to find the oreIDs for an unregistered object (%s). This won't work very well.", stack);
			return new int[0];
		}

		// TODO: cache this?

		val set = new IntOpenHashSet();
		int id = Item.REGISTRY.getIDForObject(delegate.get());
		List<Integer> ids = stackToId.get(id);
		if (ids != null) {
			set.addAll(ids);
		}

		ids = stackToId.get(id | stack.getItemDamage() + 1 << 16);
		if (ids != null) {
			set.addAll(ids);
		}

		val ret = new int[set.size()];
		val ie = set.iterator();
		int i = 0;
		while (ie.hasNext())
			ret[i++] = ie.nextInt();

		return ret;
	}
}
