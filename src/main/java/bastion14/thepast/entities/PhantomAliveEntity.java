package bastion14.thepast.entities;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PhantomAliveEntity extends FlyingEntity implements IMob {
    private static final DataParameter<Integer> SIZE = EntityDataManager.createKey(PhantomAliveEntity.class, DataSerializers.VARINT);
    private PhantomAliveEntity.AttackPhase attackPhase = AttackPhase.NEUTRAL;
    private Vec3d orbitOffset = Vec3d.ZERO;
    private BlockPos orbitPosition = BlockPos.ZERO;

    protected PhantomAliveEntity(EntityType<? extends FlyingEntity> type, World worldIn) {
        super(type, worldIn);
        this.experienceValue = 3;
        this.moveController = new MoveHelperController(this);
        this.lookController = new LookHelperController(this);
    }

    protected BodyController createBodyController() {
        return new BodyHelperController(this);
    }
    protected void registerGoals(){
        //register the goals that this entity follows. (Unclear)
        this.goalSelector.addGoal(1, new PickAttackGoal());
        this.goalSelector.addGoal(2, new SweepAttackGoal());
        this.goalSelector.addGoal(3, new OrbitPointGoal());
        this.targetSelector.addGoal(1, new AttackUndeadPhantomGoal());
    }

    protected void registerAttributes(){
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
    }
    protected void registerData(){
        super.registerData();
        this.dataManager.register(SIZE, 0);
    }
    public void setPhantomAliveSize(int sizeIn) {
        this.dataManager.set(SIZE, MathHelper.clamp(sizeIn, 0, 64));
    }
    private void updatePhantomSize(){
        this.recalculateSize();
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue((double) (6+ this.getPhantomAliveSize()));
    }
    public int getPhantomAliveSize(){
        return this.dataManager.get(SIZE);
    }
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn){
        return sizeIn.height * 0.35f;
    }
    public void notifyDataManagerChange(DataParameter<?> key){
        if(SIZE.equals(key)){
            this.updatePhantomSize();
        }
        super.notifyDataManagerChange(key);
    }
    protected boolean isDespawnPeaceful() {return true;}

    public void tick() {
        super.tick();
        if(this.world.isRemote){
            // we are on the client, copied from phantom class. Hope they don't mind.
            float f = MathHelper.cos((float)(this.getEntityId() * 3 + this.ticksExisted) * 0.13F + (float)Math.PI);
            float f1 = MathHelper.cos((float)(this.getEntityId() * 3 + this.ticksExisted + 1) * 0.13F + (float)Math.PI);
            if (f > 0.0F && f1 <= 0.0F) {
                this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PHANTOM_FLAP, this.getSoundCategory(), 0.95F + this.rand.nextFloat() * 0.05F, 0.95F + this.rand.nextFloat() * 0.05F, false);
            }

            int i = this.getPhantomAliveSize();
            float f2 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
            float f3 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
            float f4 = (0.3F + f * 0.45F) * ((float)i * 0.2F + 1.0F);
            this.world.addParticle(ParticleTypes.MYCELIUM, this.getPosX() + (double)f2, this.getPosY() + (double)f4, this.getPosZ() + (double)f3, 0.0D, 0.0D, 0.0D);
            this.world.addParticle(ParticleTypes.MYCELIUM, this.getPosX() - (double)f2, this.getPosY() + (double)f4, this.getPosZ() - (double)f3, 0.0D, 0.0D, 0.0D);
        }
    }

    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyInstance, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag){
        this.orbitPosition = new BlockPos(this).up(5);
        this.setPhantomAliveSize(0);
        return super.onInitialSpawn(worldIn, difficultyInstance, reason, spawnDataIn, dataTag);
    }

    public void readAdditional(CompoundNBT tag){
        super.readAdditional(tag);
        this.setPhantomAliveSize(tag.getInt("Size"));
        if(tag.contains("AX")){
            orbitPosition = new BlockPos(tag.getInt("AX"), tag.getInt("AY"), tag.getInt("AZ"));
        }
    }

    public void writeAdditional(CompoundNBT tag){
        super.writeAdditional(tag);
        tag.putInt("AX", orbitPosition.getX());
        tag.putInt("AY", orbitPosition.getY());
        tag.putInt("AZ", orbitPosition.getZ());
        tag.putInt("Size", this.getPhantomAliveSize());
    }

    public SoundCategory getSoundCategory(){
        return SoundCategory.NEUTRAL;
    }
    protected SoundEvent getAmbientSound(){
        return SoundEvents.ENTITY_PHANTOM_AMBIENT;
    }
    protected SoundEvent getHurtSound(DamageSource damageSource){
        return SoundEvents.ENTITY_PHANTOM_HURT;
    }
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PHANTOM_DEATH;
    }
    public CreatureAttribute getCreatureAttribute(){
        return CreatureAttribute.ARTHROPOD;
    }
    protected float getSoundVolume() {
        return 1.0f;
    }
    public boolean canAttack(EntityType<?> typeIn){
        return true;
    }
    public EntitySize getSize(Pose poseIn){
        int i = getPhantomAliveSize();
        EntitySize size = super.getSize(poseIn);
        float f = (size.width + 0.2f * (float)i) / size.width;
        return size.scale(f);
    }

    // goals, what does this phantom aspire to.
    class MoveHelperController extends MovementController {
        private float speedFactor = 0.1f;
        public MoveHelperController(PhantomAliveEntity ent){
            super(ent);
        }

        @Override
        public void tick() {
            if(collidedHorizontally){
                rotationYaw += 180.0f;
                this.speedFactor = 0.1f;
            }
            float offX = (float)(orbitOffset.x - getPosX());
            float offY = (float)(orbitOffset.y - getPosY());
            float offZ = (float)(orbitOffset.z - getPosZ());
            double horizontalDistance = MathHelper.sqrt(offX * offX + offZ + offZ);
            double totalDistance = MathHelper.sqrt(offX * offX + offY * offY + offZ * offZ);
            float yawOld = rotationYaw;
            float yaw =(float) MathHelper.atan2(offZ, offX);
            float yawWrapped = MathHelper.wrapDegrees(rotationYaw + 90.0f);
            float newYawWrapped = MathHelper.wrapDegrees(yaw * 180f/ (float)Math.PI);
            rotationYaw = MathHelper.approachDegrees(yawWrapped, newYawWrapped, 4.0f) - 90.0f;
            renderYawOffset = rotationYaw;
            if(MathHelper.degreesDifferenceAbs(yawOld, rotationYaw) < 3f){
                speedFactor = MathHelper.approach(speedFactor, 1.8f, 0.005f * 1.8f/ speedFactor);
            }else{
                speedFactor = MathHelper.approach(speedFactor, 0.2f, 0.025f);
            }
            // vertical angle.
            float pitch = (float) (-MathHelper.atan2(-offY, horizontalDistance) * 180f / (float)Math.PI);
            rotationPitch = pitch;

            float newYaw = rotationYaw + 90.0F;
            double vx = (double)(this.speedFactor * MathHelper.cos(newYaw * ((float)Math.PI / 180F))) * Math.abs((double)offX / totalDistance);
            double vz = (double)(this.speedFactor * MathHelper.sin(newYaw * ((float)Math.PI / 180F))) * Math.abs((double)offZ / totalDistance);
            double vy = (double)(this.speedFactor * MathHelper.sin(pitch * ((float)Math.PI / 180F))) * Math.abs((double)offY / totalDistance);
            Vec3d motion = getMotion();
            setMotion(motion.add(new Vec3d(vx, vy, vz).subtract(motion).scale(0.2)));
        }
    }
    class AttackUndeadPhantomGoal extends Goal {
        private final EntityPredicate undeadPhantom = new EntityPredicate().setDistance(64.0);
        private int tickDelay = 20;
        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean shouldExecute() {
            if(tickDelay > 0){
                --tickDelay;
                return false;
            }else{
                this.tickDelay = 60;
                List<PhantomEntity> phantoms = PhantomAliveEntity.this.world.getTargettableEntitiesWithinAABB(
                        PhantomEntity.class,
                        undeadPhantom,
                        PhantomAliveEntity.this,
                        PhantomAliveEntity.this.getBoundingBox().grow(16.0d, 64.0d, 16.0d)
                );
                if(!phantoms.isEmpty()){
                    phantoms.sort((phantom1, phantom2) -> phantom1.getPosY() > phantom2.getPosY() ? -1 : 1);
                    for(PhantomEntity ent : phantoms){
                        if(PhantomAliveEntity.this.canAttack(ent, EntityPredicate.DEFAULT)) {
                            PhantomAliveEntity.this.setAttackTarget(ent);
                            PhantomAliveEntity.this.attackPhase = AttackPhase.CIRCLE;
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }
    abstract class MoveGoal extends Goal {
        public MoveGoal() {
            this.setMutexFlags(EnumSet.of(Flag.MOVE));
        }
        // I assume that's what it means.
        protected boolean shouldUpdateOrbit(){
            return orbitOffset.squareDistanceTo(getPosX(), getPosY(), getPosZ()) < 4.0d;
        }

    }
    class OrbitPointGoal extends MoveGoal {
        private float turn_angle;
        private float rotSpeed;
        private float verticalSpeed;
        private float turn_direction;

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean shouldExecute() {
            return getAttackTarget() == null || attackPhase == AttackPhase.CIRCLE;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        @Override
        public void startExecuting() {
            rotSpeed = 5.0f + rand.nextFloat() * 10.0f;
            verticalSpeed = -4.0f + rand.nextFloat() * 9.0f;
            turn_direction = rand.nextBoolean() ? 1.0f: -1.0f;
            updateOrbit();
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        @Override
        public void tick() {
            if(rand.nextInt(350) == 0){
                verticalSpeed = -4.0f + rand.nextFloat() * 9.0f;
            }
            if(rand.nextInt(250) == 0){
                ++rotSpeed;
                if(rotSpeed > 15.0f) {
                    rotSpeed = 5.0f;
                    turn_direction = -turn_direction;
                }
            }
            if(rand.nextInt(450) == 0){
                turn_angle = 2.0f * rand.nextFloat() *(float) Math.PI;
                updateOrbit();
            }
            if(shouldUpdateOrbit()){
                updateOrbit();
            }
            if(orbitOffset.y < getPosY() && !world.isAirBlock(new BlockPos(PhantomAliveEntity.this).down(1))){
                verticalSpeed = Math.max(1.0f, verticalSpeed);
                updateOrbit();
            }
            if(orbitOffset.y > getPosY() && !world.isAirBlock(new BlockPos(PhantomAliveEntity.this).up(1))){
                verticalSpeed = Math.min(-1.0f, verticalSpeed);
                updateOrbit();
            }
        }

        private void updateOrbit(){
            //set orbit position if orbitinh
            if(BlockPos.ZERO.equals(orbitPosition)){
                orbitPosition = new BlockPos(PhantomAliveEntity.this);
            }
            //calculate the angle we are turning at.
            turn_angle += turn_direction * 15.0f * ((float)Math.PI/180f);
            // update where we are in relation to the orbit position.
            orbitOffset = new Vec3d(orbitPosition).add(rotSpeed * MathHelper.cos(turn_angle), -4.0 * verticalSpeed, rotSpeed * MathHelper.sin(turn_angle));
        }
    }
    class PickAttackGoal extends Goal {
        private int tickDelay;

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean shouldExecute() {
            LivingEntity target = getAttackTarget();
            return target != null && canAttack(target, EntityPredicate.DEFAULT);
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        @Override
        public void startExecuting() {
            this.tickDelay = 10;
            attackPhase = AttackPhase.CIRCLE;
            updateOrbitRelativeToTarget();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        @Override
        public void resetTask() {
            orbitPosition = world.getHeight(Heightmap.Type.MOTION_BLOCKING, orbitPosition).up(10 + rand.nextInt(20));
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        @Override
        public void tick() {
            if(attackPhase == AttackPhase.CIRCLE) {
                -- tickDelay;
                if (tickDelay <= 0){
                    attackPhase = AttackPhase.SWOOP;
                    updateOrbitRelativeToTarget();
                    tickDelay = (8 + rand.nextInt(4)) * 20;
                    playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0f, 0.95f + rand.nextFloat() * 0.1f);
                }
            }
        }

        private void updateOrbitRelativeToTarget(){
            //attacks phantoms, which are already pretty high up.
            orbitPosition = new BlockPos(getAttackTarget()).up(5 + rand.nextInt(5));
            if(orbitPosition.getY() < world.getSeaLevel()){
                orbitPosition = new BlockPos(orbitPosition.getX(), world.getSeaLevel() + 1, orbitPosition.getZ());
            }
        }
    }
    class SweepAttackGoal extends MoveGoal{
        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean shouldExecute() {
            return getAttackTarget() != null && attackPhase == AttackPhase.SWOOP;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        @Override
        public boolean shouldContinueExecuting() {
            LivingEntity target = getAttackTarget();
            if(target == null || !target.isAlive()){
                return false;
            } else return !(target instanceof PhantomEntity) && shouldExecute();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        @Override
        public void resetTask() {
            setAttackTarget(null);
            attackPhase = AttackPhase.NEUTRAL;
        }

        @Override
        public void tick() {
            LivingEntity target = Objects.requireNonNull(getAttackTarget());
            orbitOffset = new Vec3d(target.getPosX(), target.getPosYHeight(0.5d), target.getPosZ());
            if(getBoundingBox().grow(0.2).intersects(target.getBoundingBox())){
                attackEntityAsMob(target);
                attackPhase = AttackPhase.CIRCLE;
                world.playEvent(1039, new BlockPos(PhantomAliveEntity.this), 0);
            } else if (collidedHorizontally || hurtTime > 0){
                attackPhase = AttackPhase.CIRCLE;
            }
        }
    }
    class LookHelperController extends LookController {
        public LookHelperController(PhantomAliveEntity ent){
            super(ent);
        }

        /**
         * Updates look
         */
        @Override
        public void tick() {
            super.tick();
        }
        // no op.
    }
    class BodyHelperController extends BodyController{
        public BodyHelperController(PhantomAliveEntity ent){
            super(ent);
        }

        /**
         * Update the Head and Body rendenring angles
         */
        @Override
        public void updateRenderAngles() {
            rotationYawHead = PhantomAliveEntity.this.renderYawOffset;
            renderYawOffset = PhantomAliveEntity.this.rotationYaw;
        }
    }

    enum AttackPhase{
        NEUTRAL, CIRCLE, SWOOP
    }

}
