package bastion14.thepast.entities.goal;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.AxisAlignedBB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;
import java.util.List;

public class FlyingRevengeGoal extends TargetGoal {
    public static final EntityPredicate predicate = new EntityPredicate().setLineOfSiteRequired().setUseInvisibilityCheck();
    private boolean callsForHelp;
    private int revengeTimerOld;

    private Class<?>[] reinforcementType;

    private static final Logger LOGGER = LogManager.getLogger("FlyingRevenge");

    public FlyingRevengeGoal(FlyingEntity entity){
        super(entity, true);
        setMutexFlags(EnumSet.of(Flag.TARGET));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    @Override
    public boolean shouldExecute() {
        int i = this.goalOwner.getRevengeTimer();
        LivingEntity target = this.goalOwner.getRevengeTarget();
        if(i != revengeTimerOld && target != null){
            return isSuitableTarget(target, predicate);
        }
        return false;
    }
    public FlyingRevengeGoal setCallsForHelp(Class<?>... types){
        callsForHelp = true;
        reinforcementType = types;
        return this;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        goalOwner.setAttackTarget(goalOwner.getRevengeTarget());
        target = goalOwner.getAttackTarget();

        revengeTimerOld = goalOwner.getRevengeTimer();
        unseenMemoryTicks = 300;
        if(callsForHelp){
            alertOthers();
        }
        super.startExecuting();
    }
    protected void alertOthers() {
        double distance = getTargetDistance();
        List<MobEntity> list = goalOwner.world.getLoadedEntitiesWithinAABB(goalOwner.getClass(),
                new AxisAlignedBB(
                        goalOwner.getPosX(),
                        goalOwner.getPosY(),
                        goalOwner.getPosZ(),
                        goalOwner.getPosX() + 1.0
                        , goalOwner.getPosY() + 1.0,
                        goalOwner.getPosZ() + 1.0).grow(distance, 10.0, distance)
        );
        //stands for potential
        for(MobEntity pot : list){
            if(goalOwner != pot && pot.getAttackTarget() == null && (!(this.goalOwner instanceof TameableEntity) || ((TameableEntity)this.goalOwner).getOwner() == ((TameableEntity)pot).getOwner()) && !pot.isOnSameTeam(goalOwner.getRevengeTarget())){
                if(reinforcementType == null){
                    setAttackTarget(pot, goalOwner.getRevengeTarget());
                }
                boolean found = false;
                for(Class<?> oc : this.reinforcementType){
                    if(oc == pot.getClass()){
                        found = true;
                        break;
                    }
                }
                if(found){
                    setAttackTarget(pot, goalOwner.getRevengeTarget());
                }
            }
        }
    }
    protected void setAttackTarget(MobEntity aggressor, LivingEntity target){
        aggressor.setAttackTarget(target);
    }
}
