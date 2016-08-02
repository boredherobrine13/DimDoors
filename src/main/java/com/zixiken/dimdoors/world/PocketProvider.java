package com.zixiken.dimdoors.world;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.core.NewDimData;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.ticking.CustomLimboPopulator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;
import com.zixiken.dimdoors.CloudRenderBlank;
import com.zixiken.dimdoors.core.DimensionType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PocketProvider extends WorldProvider
{
	private DDProperties properties;
	protected CustomLimboPopulator spawner;
	protected IRenderHandler skyRenderer;

	public PocketProvider()
	{
		this.hasNoSky = true;
		this.skyRenderer = new PocketSkyProvider();

		this.spawner = DimDoors.spawner;
		this.properties = DimDoors.properties;
	}

	@Override
	protected void registerWorldChunkManager()
	{
		super.worldChunkMgr = new WorldChunkManagerHell(DimDoors.pocketBiome, 1);
	}
	
	@Override
	public String getSaveFolder()
	{
		return (dimensionId == 0 ? null : "DimensionalDoors/pocketDimID" + dimensionId);
	}

	@Override
	public Vec3 getSkyColor(Entity cameraEntity, float partialTicks)
	{
		setCloudRenderer( new CloudRenderBlank());
		return Vec3.createVectorHelper(0d, 0d, 0d);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Vec3 getFogColor(float par1, float par2)
	{
		return Vec3.createVectorHelper(0d, 0d, 0d);
	}

	@Override
	public double getHorizon()
	{
		return worldObj.getHeight();
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new PocketGenerator(worldObj, dimensionId, false, spawner);
	}

	@Override
	public boolean canSnowAt(int x, int y, int z, boolean light)
	{
		return false;
	}
	
	@Override
	public boolean canBlockFreeze(int x, int y, int z, boolean byWater)
	{
		return false;
	}

	 public float calculateCelestialAngle(long par1, float par3)
	 {
		 return .5F;
	 }
	 
	@Override
	protected void generateLightBrightnessTable()
	{
        if (!PocketManager.isLoaded())
        {
            super.generateLightBrightnessTable();
            return;
        }

        NewDimData data = PocketManager.getDimensionData(this.dimensionId);
		if(data == null || data.type() == DimensionType.POCKET)
		{
			super.generateLightBrightnessTable();
			return;
		}
		float modifier = 0.0F;

		for (int steps = 0; steps <= 15; ++steps)
		{
			float var3 = (float) (Math.pow(steps,1.5) / Math.pow(15.0F,1.5));
			this.lightBrightnessTable[15-steps] = var3;
			System.out.println( this.lightBrightnessTable[steps]+"light");
		}
	}
	@Override
	public String getDimensionName() 
	{
		//TODO: This should be a proper name. We need to show people proper names for things whenever possible.
		//The question is whether this should be "Pocket Dimension" or "Pocket Dimension #" -- I'm not going to change
		//it out of concern that it could break something. ~SenseiKiwi
		return "PocketDim " + this.dimensionId;
	}

	@Override
	public int getRespawnDimension(EntityPlayerMP player)
	{
		int respawnDim;

		if (properties.LimboEnabled)
		{
			respawnDim = properties.LimboDimensionID;
		}
		else
		{
			respawnDim = PocketManager.getDimensionData(this.dimensionId).root().id();
		}
		// TODO: Are we sure we need to load the dimension as well? Why can't the game handle that?
		PocketManager.loadDimension(respawnDim);
		return respawnDim;
	}

	@Override
	public boolean canRespawnHere()
	{
		return false;
	}
	
	@Override
	public int getActualHeight()
	{
		return 256;
	}
}