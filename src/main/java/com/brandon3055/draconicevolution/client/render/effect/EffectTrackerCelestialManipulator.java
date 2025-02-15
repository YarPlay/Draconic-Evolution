package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.render.CCModelLibrary;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * Created by brandon3055 on 23/06/2016.
 */
public class EffectTrackerCelestialManipulator {
    public static double interpPosX = 0;
    public static double interpPosY = 0;
    public static double interpPosZ = 0;
    private Random rand = new Random();
    public Vec3D effectFocus;
    public Vec3D linkPos = null;
    private int renderBolt = 0;
    private float rotation;
    private float rotationSpeed = 2;
    private float aRandomFloat = 0;
    public boolean positionLocked = false;
    public Vec3D startPos;
    public Vec3D pos;
    public Vec3D prevPos = new Vec3D();
    public Vec3D circlePosition = new Vec3D();
    private World world;
    private long boltSeed = 0;
    public boolean renderBolts = true;

    public float alpha = 0F;
    public float scale = 1F;
    public float red = 0F;
    public float green = 1F;
    public float blue = 1F;

    public EffectTrackerCelestialManipulator(World world, Vec3D pos, Vec3D effectFocus) {
        this.world = world;
        this.effectFocus = effectFocus;
        this.rotation = rand.nextInt(1000);
        this.aRandomFloat = rand.nextFloat();
        this.pos = pos.copy();
        this.startPos = pos.copy();
        this.prevPos.set(pos);
        red = 0.1F;
        green = 0.1F;
        alpha = 1F;
    }

    public void onUpdate() {
        prevPos.set(pos);

        if (renderBolt > 0) {
            renderBolt--;
        }

        renderBolt = 1;
        boltSeed = rand.nextLong();
        //TODO Particles
//        BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, new SubParticle(world, effectFocus), 128, true);

        rotationSpeed = -1F;
        rotation += rotationSpeed;
    }

    public void renderEffect(Tessellator tessellator, float partialTicks) {
        BufferBuilder vertexbuffer = tessellator.getBuilder();
        CCRenderState ccrs = CCRenderState.instance();
        //region Icosahedron

        float relativeX = (float) (this.prevPos.x + (this.pos.x - this.prevPos.x) * (double) partialTicks - interpPosX);
        float relativeY = (float) (this.prevPos.y + (this.pos.y - this.prevPos.y) * (double) partialTicks - interpPosY);
        float relativeZ = (float) (this.prevPos.z + (this.pos.z - this.prevPos.z) * (double) partialTicks - interpPosZ);
        float correctX = (float) (this.prevPos.x + (this.pos.x - this.prevPos.x) * (double) partialTicks);
        float correctY = (float) (this.prevPos.y + (this.pos.y - this.prevPos.y) * (double) partialTicks);
        float correctZ = (float) (this.prevPos.z + (this.pos.z - this.prevPos.z) * (double) partialTicks);

        RenderSystem.pushMatrix();
//        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 200, 200);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(red, green, blue, alpha);
        RenderSystem.translatef(relativeX, relativeY, relativeZ);
        RenderSystem.rotatef(rotation + (partialTicks * rotationSpeed), 0F, 1F, 0F);
        RenderSystem.translatef(-relativeX, -relativeY, -relativeZ);
        ccrs.reset();
        ccrs.startDrawing(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX, vertexbuffer);
        Matrix4 pearlMat = RenderUtils.getMatrix(new Vector3(relativeX, relativeY, relativeZ), new Rotation(0F, new Vector3(0, 0, 0)), 0.15 * scale);
        ccrs.bind(vertexbuffer);
        CCModelLibrary.icosahedron7.render(ccrs, pearlMat);
        tessellator.end();
        RenderSystem.popMatrix();
        RenderSystem.color4f(1F, 1F, 1F, 1F);

        //endregion

        RenderSystem.pushMatrix();
        RenderSystem.translatef(relativeX, relativeY, relativeZ);

        int segments = Math.max(4, (int) (8 * scale));
        if (renderBolt > 0 && scale > 0 && renderBolts) {
            RenderEnergyBolt.renderBoltBetween(new Vec3D(), effectFocus.copy().subtract(correctX, correctY, correctZ), 0.05 * scale, scale * 0.5, segments, boltSeed, false);
        }

        if (linkPos != null && scale > 0) {
            RenderEnergyBolt.renderBoltBetween(new Vec3D(), linkPos.copy().subtract(correctX, correctY, correctZ), 0.05 * scale, scale * 0.5, segments, boltSeed, false);
        }

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableLighting();
        RenderSystem.popMatrix();
    }

//    public static class SubParticle extends Particle {
//
//        public SubParticle(World worldIn, Vec3D pos) {
//            super(worldIn, pos);
//
//            double speed = 0.1;
//            this.motionX = (-0.5 + rand.nextDouble()) * speed;
//            this.motionY = (-0.5 + rand.nextDouble()) * speed;
//            this.motionZ = (-0.5 + rand.nextDouble()) * speed;
//
//            this.maxAge = 10 + rand.nextInt(10);
//            this.baseScale = 1F;
////            this.particleTextureIndexY = 1;
//
//            this.particleRed = 0;
//        }
//
////        @Override
////        public BCParticle setScale(float scale) {
////            super.setScale(scale);
////
////            double speed = 0.1 * scale;
////            this.motionX = (-0.5 + rand.nextDouble()) * speed;
////            this.motionY = (-0.5 + rand.nextDouble()) * speed;
////            this.motionZ = (-0.5 + rand.nextDouble()) * speed;
////            return this;
////        }
//
////        @Override
////        public boolean shouldDisableDepth() {
////            return true;
////        }
//
//        @Override
//        public void tick() {
//            this.prevPosX = this.posX;
//            this.prevPosY = this.posY;
//            this.prevPosZ = this.posZ;
//
////            particleTextureIndexX = rand.nextInt(5);
//            int ttd = maxAge - age;
//            if (ttd < 10) {
//                baseScale = ttd / 10F;
//            }
//
//            moveEntityNoClip(motionX, motionY, motionZ);
//
//            if (age++ > maxAge) {
//                setExpired();
//            }
//        }
//
//        @Override
//        public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
////            if (age == 0) {
////                return;
////            }
////            float minU = (float) this.particleTextureIndexX / 8.0F;
////            float maxU = minU + 0.125F;
////            float minV = (float) this.particleTextureIndexY / 8.0F;
////            float maxV = minV + 0.125F;
////            float scale = 0.1F * this.particleScale;
////
////            float renderX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
////            float renderY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
////            float renderZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
////            int brightnessForRender = this.getBrightnessForRender(partialTicks);
////            int j = brightnessForRender >> 16 & 65535;
////            int k = brightnessForRender & 65535;
////            vertexbuffer.pos((double) (renderX - rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ - rotationYZ * scale - rotationXZ * scale)).tex((double) maxU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
////            vertexbuffer.pos((double) (renderX - rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ - rotationYZ * scale + rotationXZ * scale)).tex((double) maxU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
////            vertexbuffer.pos((double) (renderX + rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ + rotationYZ * scale + rotationXZ * scale)).tex((double) minU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
////            vertexbuffer.pos((double) (renderX + rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ + rotationYZ * scale - rotationXZ * scale)).tex((double) minU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
//        }
//    }

//    public static class SubParticle2 extends BCParticle {
//
//        private final EffectTrackerCelestialManipulator target;
//        public boolean targetMode = false;
//
//        public SubParticle2(World worldIn, Vec3D pos, EffectTrackerCelestialManipulator target) {
//            super(worldIn, pos);
//            this.target = target;
//
//            double speed = 1;
//            this.motionX = (-0.5 + rand.nextDouble()) * speed;
//            this.motionY = (-0.5 + rand.nextDouble()) * speed;
//            this.motionZ = (-0.5 + rand.nextDouble()) * speed;
//
//            this.maxAge = 150 + rand.nextInt(10);
//            this.baseScale = 1F;
////            this.particleTextureIndexY = 1;
//
//            this.particleRed = 0;
//        }
//
////        @Override
////        public BCParticle setScale(float scale) {
////            super.setScale(scale);
////
////            double speed = 1;
////            this.motionX = (-0.5 + rand.nextDouble()) * speed;
////            this.motionY = (-0.5 + rand.nextDouble()) * speed;
////            this.motionZ = (-0.5 + rand.nextDouble()) * speed;
////            return this;
////        }
//
////        @Override
////        public boolean shouldDisableDepth() {
////            return true;
////        }
//
//        @Override
//        public void tick() {
////            float b = 1F - (world.getSunBrightness(0) - 0.2F) * 1.2F;
////            particleGreen = particleBlue = b;
//
//            this.prevPosX = this.posX;
//            this.prevPosY = this.posY;
//            this.prevPosZ = this.posZ;
//
//            Vec3D thisPos = new Vec3D(posX, posY, posZ);
//            Vec3D dir = Vec3D.getDirectionVec(thisPos, target.pos);
//            double distance = Utils.getDistanceAtoB(thisPos, target.pos) * 0.8;
//            double speed = 0.01 * distance;
//            if (distance > 2 && rand.nextInt(90) == 0) {
//                targetMode = true;
//            }
//
//            double dragModifier = 0.95 / distance;
//            motionX *= dragModifier;
//            motionY *= dragModifier;
//            motionZ *= dragModifier;
//
//            motionX += dir.x * speed;
//            motionY += dir.y * speed;
//            motionZ += dir.z * speed;
//
//            if (targetMode) {
//                motionX = dir.x * 0.1;
//                motionY = dir.y * 0.1;
//                motionZ = dir.z * 0.1;
//            }
//
////            particleTextureIndexX = rand.nextInt(5);
//            int ttd = maxAge - age;
//            if (ttd < 10) {
//                baseScale = ttd / 10F;
//            }
//
//            moveEntityNoClip(motionX, motionY, motionZ);
//
//            if (distance < 0.5) {
//                age += 4;
//            }
//
//            if (age++ > maxAge) {
//                setExpired();
//            }
//        }
//
//        @Override
//        public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
////            if (age == 0) {
////                return;
////            }
////            float minU = (float) this.particleTextureIndexX / 8.0F;
////            float maxU = minU + 0.125F;
////            float minV = (float) this.particleTextureIndexY / 8.0F;
////            float maxV = minV + 0.125F;
////            float scale = 0.1F * this.particleScale;
////
////            float renderX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
////            float renderY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
////            float renderZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
////            int brightnessForRender = this.getBrightnessForRender(partialTicks);
////            int j = brightnessForRender >> 16 & 65535;
////            int k = brightnessForRender & 65535;
////            vertexbuffer.pos((double) (renderX - rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ - rotationYZ * scale - rotationXZ * scale)).tex((double) maxU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
////            vertexbuffer.pos((double) (renderX - rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ - rotationYZ * scale + rotationXZ * scale)).tex((double) maxU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
////            vertexbuffer.pos((double) (renderX + rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ + rotationYZ * scale + rotationXZ * scale)).tex((double) minU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
////            vertexbuffer.pos((double) (renderX + rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ + rotationYZ * scale - rotationXZ * scale)).tex((double) minU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
//        }
//    }
}
