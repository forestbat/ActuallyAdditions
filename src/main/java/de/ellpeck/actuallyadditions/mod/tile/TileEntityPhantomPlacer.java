/*
 * This file ("TileEntityPhantomPlacer.java") is part of the Actually Additions Mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense/
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.tile;

import de.ellpeck.actuallyadditions.api.tile.IPhantomTile;
import de.ellpeck.actuallyadditions.mod.inventory.GuiHandler;
import de.ellpeck.actuallyadditions.mod.network.PacketParticle;
import de.ellpeck.actuallyadditions.mod.util.PosUtil;
import de.ellpeck.actuallyadditions.mod.util.Util;
import de.ellpeck.actuallyadditions.mod.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public class TileEntityPhantomPlacer extends TileEntityInventoryBase implements IPhantomTile, IRedstoneToggle{

    public static final int RANGE = 3;
    public BlockPos boundPosition;
    public int currentTime;
    public int range;
    public boolean isBreaker;
    private boolean activateOnceWithSignal;
    private int oldRange;

    public TileEntityPhantomPlacer(int slots, String name){
        super(slots, name);
    }

    public TileEntityPhantomPlacer(){
        super(9, "phantomPlacer");
        this.isBreaker = false;
    }

    @Override
    public void updateEntity(){
        super.updateEntity();
        if(!worldObj.isRemote){
            this.range = TileEntityPhantomface.upgradeRange(RANGE, worldObj, this.pos);

            if(!this.hasBoundPosition()){
                this.boundPosition = null;
            }

            if(this.isBoundThingInRange()){
                if(!this.isRedstonePowered && !this.activateOnceWithSignal){
                    if(this.currentTime > 0){
                        this.currentTime--;
                        if(this.currentTime <= 0){
                            this.doWork();
                        }
                    }
                    else{
                        this.currentTime = 30;
                    }
                }
            }

            if(this.oldRange != this.range){
                this.oldRange = this.range;

                this.sendUpdate();
            }
        }
        else{
            if(this.boundPosition != null){
                this.renderParticles();
            }
        }
    }

    @Override
    public boolean hasBoundPosition(){
        if(this.boundPosition != null){
            if(this.worldObj.getTileEntity(boundPosition) instanceof IPhantomTile || (this.getPos().getX() == this.boundPosition.getX() && this.getPos().getY() == this.boundPosition.getY() && this.getPos().getZ() == this.boundPosition.getZ() && this.worldObj.provider.getDimensionId() == this.worldObj.provider.getDimensionId())){
                this.boundPosition = null;
                return false;
            }
            return this.worldObj.provider.getDimensionId() == this.worldObj.provider.getDimensionId();
        }
        return false;
    }

    private void doWork(){
        if(this.isBreaker){
            Block blockToBreak = PosUtil.getBlock(boundPosition, worldObj);
            if(blockToBreak != null && blockToBreak.getBlockHardness(worldObj, boundPosition) > -1.0F){
                ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
                int meta = PosUtil.getMetadata(boundPosition, worldObj);
                drops.addAll(blockToBreak.getDrops(worldObj, boundPosition, worldObj.getBlockState(boundPosition), 0));

                if(WorldUtil.addToInventory(this, drops, false, true)){
                    worldObj.playAuxSFX(2001, this.boundPosition, Block.getIdFromBlock(blockToBreak)+(meta << 12));
                    worldObj.setBlockToAir(this.boundPosition);
                    WorldUtil.addToInventory(this, drops, true, true);
                    this.markDirty();
                }
            }
        }
        else{
            if(PosUtil.getBlock(boundPosition, worldObj).isReplaceable(worldObj, boundPosition)){
                int theSlot = WorldUtil.findFirstFilledSlot(this.slots);
                this.setInventorySlotContents(theSlot, WorldUtil.placeBlockAtSide(EnumFacing.UP, worldObj, boundPosition, this.slots[theSlot]));
                if(this.slots[theSlot] != null && this.slots[theSlot].stackSize <= 0){
                    this.slots[theSlot] = null;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderParticles(){
        if(Util.RANDOM.nextInt(2) == 0){
            double d1 = (double)((float)this.boundPosition.getY()+Util.RANDOM.nextFloat());
            int i1 = Util.RANDOM.nextInt(2)*2-1;
            int j1 = Util.RANDOM.nextInt(2)*2-1;
            double d4 = ((double)Util.RANDOM.nextFloat()-0.5D)*0.125D;
            double d2 = (double)this.boundPosition.getZ()+0.5D+0.25D*(double)j1;
            double d5 = (double)(Util.RANDOM.nextFloat()*1.0F*(float)j1);
            double d0 = (double)this.boundPosition.getX()+0.5D+0.25D*(double)i1;
            double d3 = (double)(Util.RANDOM.nextFloat()*1.0F*(float)i1);
            worldObj.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
        }

        if(this.ticksElapsed%80 == 0){
            PacketParticle.renderParticlesFromAToB(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), boundPosition.getX(), boundPosition.getY(), boundPosition.getZ(), 2, 0.35F, TileEntityPhantomface.COLORS, 3);
        }
    }

    @Override
    public boolean isBoundThingInRange(){
        return this.hasBoundPosition() && PosUtil.toVec(this.boundPosition).distanceTo(new Vec3(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ())) <= this.range;
    }

    @Override
    public BlockPos getBoundPosition(){
        return this.boundPosition;
    }

    @Override
    public void setBoundPosition(BlockPos pos){
        this.boundPosition = pos == null ? null : PosUtil.copyPos(pos);
    }

    @Override
    public int getGuiID(){
        return GuiHandler.GuiTypes.PHANTOM_PLACER.ordinal();
    }

    @Override
    public int getRange(){
        return this.range;
    }

    @Override
    public void writeSyncableNBT(NBTTagCompound compound, boolean sync){
        super.writeSyncableNBT(compound, sync);
        compound.setInteger("Range", this.range);
        if(this.boundPosition != null){
            compound.setInteger("XCoordOfTileStored", boundPosition.getX());
            compound.setInteger("YCoordOfTileStored", boundPosition.getY());
            compound.setInteger("ZCoordOfTileStored", boundPosition.getZ());
        }
    }

    @Override
    public void readSyncableNBT(NBTTagCompound compound, boolean sync){
        super.readSyncableNBT(compound, sync);
        int x = compound.getInteger("XCoordOfTileStored");
        int y = compound.getInteger("YCoordOfTileStored");
        int z = compound.getInteger("ZCoordOfTileStored");
        this.range = compound.getInteger("Range");
        if(!(x == 0 && y == 0 && z == 0)){
            this.boundPosition = new BlockPos(x, y, z);
            this.markDirty();
        }
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side){
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack){
        return !this.isBreaker;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side){
        return this.isBreaker;
    }

    @Override
    public void toggle(boolean to){
        this.activateOnceWithSignal = to;
    }

    @Override
    public boolean isPulseMode(){
        return this.activateOnceWithSignal;
    }

    @Override
    public void activateOnPulse(){
        this.doWork();
    }

    public static class TileEntityPhantomBreaker extends TileEntityPhantomPlacer{

        public TileEntityPhantomBreaker(){
            super(9, "phantomBreaker");
            this.isBreaker = true;
        }

    }
}
