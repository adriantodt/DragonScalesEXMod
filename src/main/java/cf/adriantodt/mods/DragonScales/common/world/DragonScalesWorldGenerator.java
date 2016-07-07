package cf.adriantodt.mods.DragonScales.common.world;

import java.util.Random;

import cf.adriantodt.mods.DragonScales.DragonScalesEX;
import cf.adriantodt.mods.DragonScales.Lib;
import cf.adriantodt.mods.DragonScales.common.DragonScalesHandler;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.IWorldGenerator;

public class DragonScalesWorldGenerator implements IWorldGenerator {
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		chunkX*=16; chunkZ*=16;
		switch(world.provider.dimensionId){
			case -1: generateNether(world, random,chunkX,chunkZ); break;
			case 0 : generateSurface(world, random,chunkX,chunkZ); generateVirus(world, random,chunkX,chunkZ); break;
			case 1 : generateEnd(world, random,chunkX,chunkZ); break;
		}
	}

	private void generateEnd(World world, Random rand, int BlockX, int BlockZ) {
		int baseY, baseX, baseZ;
		for(int i=0; i<10;i++) {
			baseX = BlockX; baseY = 48; baseZ = BlockZ;
			baseX += rand.nextInt(16);
			baseY += rand.nextInt(16);
			baseZ += rand.nextInt(16);
			tryGenerateOreOnce(world, baseX, baseY, baseZ, 5, 5, 5, Blocks.end_stone);
		}
	}

	private void generateNether(World world, Random rand, int BlockX, int BlockZ) {
		int baseY, baseX, baseZ;
		for(int i=0; i<5;i++) {
			baseX = BlockX; baseY = 0; baseZ = BlockZ;
			baseX += rand.nextInt(16);
			baseY += rand.nextInt(16);
			baseZ += rand.nextInt(16);
			if (!tryGenerateOreOnce(world, baseX, baseY, baseZ, 5, 5, 5, Blocks.netherrack)) {
				tryGenerateOreOnce(world, baseX, baseY, baseZ, 5, 5, 5, Blocks.bedrock);
			}
		}
		for(int i=0; i<5;i++) {
			baseX = BlockX; baseY = 128; baseZ = BlockZ;
			baseX += rand.nextInt(16);
			baseY -= rand.nextInt(16);
			baseZ += rand.nextInt(16);
			if (!tryGenerateOreOnce(world, baseX, baseY-5, baseZ, 5, 5, 5, Blocks.netherrack)) {
				tryGenerateOreOnce(world, baseX, baseY-5, baseZ, 5, 5, 5, Blocks.bedrock);
			}
		}
	}

	private void generateSurface(World world, Random rand, int BlockX, int BlockZ) {
		int baseY, baseX, baseZ;
		int gens = 5;
		if(world.getBiomeGenForCoords(BlockX, BlockZ).equals(BiomeGenBase.extremeHills)) {
			gens = 8;
		}
		if(world.getBiomeGenForCoords(BlockX, BlockZ).equals(BiomeGenBase.extremeHillsEdge)) {
			gens = 6;
		}
		if(world.getBiomeGenForCoords(BlockX, BlockZ).equals(BiomeGenBase.extremeHillsPlus)) {
			gens = 10;
		}
		for(int i =0; i<5;i++) {
			baseX = BlockX; baseY = 8; baseZ = BlockZ;
			baseX += rand.nextInt(16);
			baseY += rand.nextInt(32);
			baseZ += rand.nextInt(16);
			tryGenerateOreOnce(world, baseX, baseY, baseZ, 5, 5, 5, Blocks.stone);
		}
	}
	
	/**
	 * Number of failed generations, clamped to 500
	 * For each fail, 0,1% is added
	 */
	int fails = 0;
	
	private void generateVirus(World world, Random rand, int BlockX, int BlockZ) {
		if (Lib.Config.DraconyVirus_ChanceMultiplier == 0) return;
		
		int chance = rand.nextInt(5)-(4/(Lib.Config.DraconyVirus_ChanceMultiplier+1)), spread = 7 + rand.nextInt(2);
		if(world.getBiomeGenForCoords(BlockX, BlockZ).equals(BiomeGenBase.extremeHills)) {
			chance = 7 + rand.nextInt(8); spread = 5 + rand.nextInt(2);
		}
		if(world.getBiomeGenForCoords(BlockX, BlockZ).equals(BiomeGenBase.extremeHillsEdge)) {
			chance = 5 + rand.nextInt(8); spread = 7 + rand.nextInt(2); 
		}
		if(world.getBiomeGenForCoords(BlockX, BlockZ).equals(BiomeGenBase.extremeHillsPlus)) {
			chance = 10; spread = 11 + rand.nextInt(2); 
		}
		chance *= Lib.Config.DraconyVirus_ChanceMultiplier;
		spread *= Lib.Config.DraconyVirus_SpreadingMultiplier;
		
		if (rand.nextInt(1000) <= (chance + fails * chance)) {
			for (int y = 128; y > 63; y--) {
				int x = rand.nextInt(16), z = rand.nextInt(16);
					if (
							DraconyVirus.ConvertBlock(world, BlockX+x, y, BlockZ+z)
					) {
						fails = 0;
						DraconyVirus.InfectBiomeAsync(world, BlockX+x, y, BlockZ+z, spread);
						return;
					} else if (!world.isAirBlock(x, y, z)){
						if (!world.canBlockSeeTheSky(x, y, z)) break;
					}
			}
		}
		fails = MathHelper.clamp_int(fails + 1,  0, 500);
		
		if(fails == 500) {
			//DraconyVirus.InfectBiome(world, BlockX+rand.nextInt(16), 63, BlockZ+rand.nextInt(16), spread);
			DraconyVirus.InfectBiomeAsync(world, BlockX+rand.nextInt(16), 63, BlockZ+rand.nextInt(16), spread);
			fails = 0;
		}
		
	}
	
	
	
	private boolean tryGenerateOreOnce(World world, int BaseX, int BaseY, int BaseZ, int RangeX, int RangeY, int RangeZ, Block underBlock) {
		for(int i = 0; i<RangeX; i++) {
			for(int j = 0; j<RangeY; j++) {
				for(int k = 0; k<RangeZ; k++) {
					if (world.rand.nextInt(4)==3) i = - i;
					if (world.rand.nextInt(4)==3) j = - j;
					if (world.rand.nextInt(4)==3) k = - k;
					if (world.getBlock(BaseX+i, BaseY+j, BaseZ+k).isAir(world, BaseX+i, BaseY+j, BaseZ+k) && (world.getBlock(BaseX+i, BaseY+j-1, BaseZ+k) == underBlock || world.getBlock(BaseX+i, BaseY+j+1, BaseZ+k) == underBlock)) {
						world.setBlock(BaseX+i, BaseY+j, BaseZ+k, DragonScalesHandler.dragonCrystal, i*j*k & 0x0F ,3);
						return true;
					}
				}
			}
		}
		return false;
	}
}