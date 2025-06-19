package com.blocklogic.cratetech.client.renderer;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.component.CollectorSettings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class CollectorWireframeRenderer {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) return;

        Vec3 cameraPos = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        int renderDistance = mc.options.renderDistance().get() * 16;
        BlockPos playerPos = mc.player.blockPosition();

        for (int x = playerPos.getX() - renderDistance; x <= playerPos.getX() + renderDistance; x += 16) {
            for (int z = playerPos.getZ() - renderDistance; z <= playerPos.getZ() + renderDistance; z += 16) {
                if (level.hasChunk(x >> 4, z >> 4)) {
                    var chunk = level.getChunk(x >> 4, z >> 4);
                    for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                        if (blockEntity instanceof BaseCrateBlockEntity crateEntity) {
                            CollectorSettings settings = crateEntity.getCollectorSettings();
                            if (settings.wireframeVisible()) {
                                double distanceSq = blockEntity.getBlockPos().distSqr(playerPos);
                                if (distanceSq <= 64 * 64) {
                                    renderCollectionZone(poseStack, bufferSource, crateEntity, cameraPos);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void renderCollectionZone(PoseStack poseStack, MultiBufferSource bufferSource,
                                             BaseCrateBlockEntity crateEntity, Vec3 cameraPos) {
        BlockPos cratePos = crateEntity.getBlockPos();
        CollectorSettings settings = crateEntity.getCollectorSettings();

        int baseRadius = 3;

        int minX = cratePos.getX() - baseRadius - settings.westAdjustment();
        int maxX = cratePos.getX() + baseRadius + settings.eastAdjustment();
        int minY = cratePos.getY() - baseRadius - settings.downAdjustment();
        int maxY = cratePos.getY() + baseRadius + settings.upAdjustment();
        int minZ = cratePos.getZ() - baseRadius - settings.northAdjustment();
        int maxZ = cratePos.getZ() + baseRadius + settings.southAdjustment();

        AABB collectionZone = new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);

        poseStack.pushPose();

        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

        LevelRenderer.renderLineBox(poseStack, vertexConsumer, collectionZone,
                1.0f, 1.0f, 1.0f, 1.0f);

        poseStack.popPose();
    }
}