package cn.paindar.academymonster.entity;

import cn.lambdalib.util.generic.RandUtils;
import cn.paindar.academymonster.ability.AIBodyIntensify;
import cn.paindar.academymonster.ability.AIElectronBomb;
import cn.paindar.academymonster.ability.AIFleshRipping;
import cn.paindar.academymonster.ability.BaseAbility;
import cn.paindar.academymonster.entity.ai.EntityAIBodyIntensify;
import cn.paindar.academymonster.entity.ai.EntityAIElectronBomb;
import cn.paindar.academymonster.entity.ai.EntityAIFleshRipping;
import javafx.util.Pair;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.world.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Paindar on 2017/2/11.
 */
public class EntityAcademyEnderman extends EntityEnderman
{

    private static HashMap<Class<? extends EntityAIBase>,Integer> aiLevel=new HashMap<>();
    private static List<Pair<Class<? extends BaseAbility>,Class<? extends EntityAIBase>>> validSkillList=new ArrayList<>();
    static
    {
        validSkillList.add(new Pair<>(AIBodyIntensify.class, EntityAIBodyIntensify.class));
        aiLevel.put(EntityAIBodyIntensify.class,5);
        validSkillList.add(new Pair<>(AIElectronBomb.class, EntityAIElectronBomb.class));
        aiLevel.put(EntityAIElectronBomb.class,5);
        validSkillList.add(new Pair<>(AIFleshRipping.class, EntityAIFleshRipping.class));
        aiLevel.put(EntityAIFleshRipping.class,5);
    }

    private float factor = 0.3f;
    public EntityAcademyEnderman(World world)
    {
        super(world);
        Collections.shuffle(validSkillList);
        float prob=1f;
        int level=0;
        while(RandUtils.nextFloat()<=prob && level>=validSkillList.size())
        {
            try
            {
                Pair<Class<? extends BaseAbility>,Class<? extends EntityAIBase>> elem=validSkillList.get(level);
                Constructor constructor=elem.getKey().getConstructor(EntityLivingBase.class,float.class);
                BaseAbility skill=(BaseAbility)constructor.newInstance(this,1-RandUtils.rangef(0,1)*RandUtils.rangef(0,1));//动态生成技能对象
                Class aclass=elem.getValue();
                Constructor[] tempconstructor=aclass.getDeclaredConstructors();
                Class[] parameterTypes=tempconstructor[0].getParameterTypes();
                constructor=elem.getValue().getConstructor(parameterTypes[0],parameterTypes[1]);
                //AcademyMonster.log.info("param1="+parameterTypes[0]+" param2 "+parameterTypes[1]+" skill= "+skill);
                EntityAIBase baseAI=(EntityAIBase)constructor.newInstance(this,skill);//动态生成怪物AI
                this.tasks.addTask(aiLevel.get(baseAI.getClass()),baseAI);//加入怪物AI至任务
                prob*=factor;
                level++;
            }
            catch (Exception e)
            {
                throw new RuntimeException();
            }
        }
    }
}
