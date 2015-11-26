/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Feb 2, 2014, 6:34:45 PM (GMT)]
 */
package vazkii.botania.client.render.tile;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;

import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.model.ModelSpinningCubes;
import vazkii.botania.common.block.tile.TileRuneAltar;

public class RenderTileRuneAltar extends TileEntitySpecialRenderer {

	ModelSpinningCubes cubes = new ModelSpinningCubes();
	RenderBlocks renderBlocks = new RenderBlocks();

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float partticks, int digProgress) {
		TileRuneAltar altar = (TileRuneAltar) tileentity;

		GlStateManager.pushMatrix();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.translate(x, y, z);

		int items = 0;
		for(int i = 0; i < altar.getSizeInventory(); i++)
			if(altar.getStackInSlot(i) == null)
				break;
			else items++;
		float[] angles = new float[altar.getSizeInventory()];

		float anglePer = 360F / items;
		float totalAngle = 0F;
		for(int i = 0; i < angles.length; i++)
			angles[i] = totalAngle += anglePer;

		double time = ClientTickHandler.ticksInGame + partticks;

		for(int i = 0; i < altar.getSizeInventory(); i++) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.translate(1F, 2.5F, 1F);
			GlStateManager.rotate(angles[i] + (float) time, 0F, 1F, 0F);
			GlStateManager.translate(2.25F, 0F, 0.5F);
			GlStateManager.rotate(90F, 0F, 1F, 0F);
			GlStateManager.translate(0D, 0.15 * Math.sin((time + i * 10) / 5D), 0F);
			ItemStack stack = altar.getStackInSlot(i);
			Minecraft mc = Minecraft.getMinecraft();
			if(stack != null) {
				mc.renderEngine.bindTexture(stack.getItem() instanceof ItemBlock ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);

				GlStateManager.scale(2F, 2F, 2F);
				if(!ForgeHooksClient.renderEntityItem(new EntityItem(altar.getWorld(), altar.xCoord, altar.yCoord, altar.zCoord, stack), stack, 0F, 0F, altar.getWorld().rand, mc.renderEngine, renderBlocks, 1)) {
					GlStateManager.scale(0.5F, 0.5F, 0.5F);
					if(stack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(stack.getItem()).getRenderType())) {
						GlStateManager.scale(0.5F, 0.5F, 0.5F);
						GlStateManager.translate(1F, 1.1F, 0F);
						renderBlocks.renderBlockAsItem(Block.getBlockFromItem(stack.getItem()), stack.getItemDamage(), 1F);
						GlStateManager.translate(-1F, -1.1F, 0F);
						GlStateManager.scale(2F, 2F, 2F);
					} else {
						int renderPass = 0;
						do {
							IIcon icon = stack.getItem().getIcon(stack, renderPass);
							if(icon != null) {
								Color color = new Color(stack.getItem().getColorFromItemStack(stack, renderPass));
								GlStateManager.color((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());
								float f = icon.getMinU();
								float f1 = icon.getMaxU();
								float f2 = icon.getMinV();
								float f3 = icon.getMaxV();
								ItemRenderer.renderItemIn2D(Tessellator.getInstance(), f1, f2, f, f3, icon.getIconWidth(), icon.getIconHeight(), 1F / 16F);
								GlStateManager.color(1F, 1F, 1F);
							}
							renderPass++;
						} while(renderPass < stack.getItem().getRenderPasses(stack.getItemDamage()));
					}
				}
			}
			GlStateManager.popMatrix();
		}

		GlStateManager.disableAlpha();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5F, 1.8F, 0.5F);
		GlStateManager.rotate(180F, 1F, 0F, 1F);
		int repeat = 15;
		cubes.renderSpinningCubes(2, repeat, repeat);
		GlStateManager.popMatrix();

		GlStateManager.translate(0F, 0.2F, 0F);
		float scale = altar.getTargetMana() == 0 ? 0 : (float) altar.getCurrentMana() / (float) altar.getTargetMana() / 75F;

		if(scale != 0) {
			int seed = altar.getPos().getX() ^ altar.getPos().getY() ^ altar.getPos().getZ();
			GlStateManager.translate(0.5F, 0.7F, 0.5F);
			RenderHelper.renderStar(0x00E4D7, scale, scale, scale, seed);
		}
		GlStateManager.enableAlpha();

		GlStateManager.popMatrix();
	}
}
