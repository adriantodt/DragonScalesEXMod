package cf.adriantodt.mods.DragonScales.common.items;

import com.google.common.collect.ImmutableSet;

import cf.adriantodt.mods.DragonScales.DragonScalesEX;
import cf.adriantodt.mods.DragonScales.common.DragonScalesHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemDragonScepter extends Item//Tool
{
	public ItemDragonScepter(ToolMaterial material)
	{
		super(/*-8,material,ImmutableSet.of()*/);
		setMaxStackSize(1);
		setMaxDamage(325);
	}
	
	public EnumRarity getRarity(ItemStack par1ItemStack)
	{
		return EnumRarity.rare;
	}
	
	@SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }
	
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		stack.damageItem(1, player);
		if (!world.isRemote)
		{
			Vec3 look = player.getLookVec();
			EntityLargeFireball fireBall = new EntityLargeFireball(world, player, 1, 1, 1);
			fireBall.field_92057_e = player.worldObj.rand.nextInt(5) + 2;
			fireBall.setPosition(
				player.posX + look.xCoord * 2.5,
				player.posY + look.yCoord + 1,
				player.posZ + look.zCoord * 2.5
			);
			double speed = 0.1 + MathHelper.clamp_double(player.worldObj.rand.nextDouble()/5 - 0.1, 0.0, 1.0); 
			fireBall.accelerationX = look.xCoord * speed;
			fireBall.accelerationY = look.yCoord * speed;
			fireBall.accelerationZ = look.zCoord * speed;
			world.spawnEntityInWorld(fireBall);
		}
		
		return stack;
	}
}