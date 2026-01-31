package com.me.coresmodule.utils.render.gui.guis.ItemCustomizer;

import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.ItemHelper;
import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.Tuples.Quadruple;
import com.me.coresmodule.utils.Tuples.Triple;
import com.me.coresmodule.utils.render.CustomItem.CustomItemRender;
import gg.essential.elementa.ElementaVersion;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.components.UIRoundedRectangle;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.components.input.UITextInput;
import gg.essential.elementa.constraints.*;
import com.me.coresmodule.utils.render.gui.GUIs;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.awt.*;

import static com.me.coresmodule.CoresModule.overrides;
import static com.me.coresmodule.utils.render.CustomItem.CustomItemRender.overridesPut;

public class ItemCustomization extends WindowScreen {

    public ItemCustomization() {
        super(ElementaVersion.V10, true, false);

        // Main container
        UIComponent main = new UIRoundedRectangle(7.5f)
                .setX(new CenterConstraint())
                .setY(new CenterConstraint())
                .setWidth(new PixelConstraint(325f))
                .setHeight(new PixelConstraint(400f))
                .setColor(new Color(50, 50, 50, 255))
                .setChildOf(getWindow());

        // Header
        UIComponent titleBox = new UIRoundedRectangle(7.5f)
                .setX(new CenterConstraint())
                .setY(new PixelConstraint(12.5f))
                .setWidth(new PixelConstraint(0.8f * main.getWidth()))
                .setHeight(new PixelConstraint(20f))
                .setColor(new Color(40, 40, 40, 255));

        UIText titleText = (UIText) new UIText()
                .setText("CoresModule Item Customization")
                .setX(new CenterConstraint())
                .setY(new CenterConstraint())
                .setTextScale(new PixelConstraint(1.5f))
                .setColor(new Color(145, 5, 145, 255));



        // Name Input
        UIComponent inputBox = new UIRoundedRectangle(5f)
                .setX(new CenterConstraint())
                .setY(new PixelConstraint(50f))
                .setWidth(new PixelConstraint(200f))
                .setHeight(new PixelConstraint(25f))
                .setColor(new Color(40, 40, 40, 255));

        UITextInput input = (UITextInput) new UITextInput()
                .setX(new PixelConstraint(new RelativeConstraint().getValue() + 5f))
                .setY(new PixelConstraint(7.5f))
                .setWidth(new PixelConstraint(200f))
                .setHeight(new PixelConstraint(20f))
                .setColor(new Color(255, 255, 255, 255));

        input.setText(CustomItemRender.getItemName(ItemHelper.getHeldItem()));

        input.onMouseClick((inp, ignored) -> {
            input.grabWindowFocus();
            return null;
        });
        input.onKeyType((comp, ignored, ignored2) -> {
            String uuid = ItemHelper.getUUID(ItemHelper.getHeldItem());
            if (overrides.containsKey(uuid)) {
                Quadruple<ItemStack, ItemStack, Boolean, String> overrideQuadruple = overrides.get(uuid);
                overridesPut(
                        uuid,
                        new Quadruple<>(
                                overrideQuadruple.first,
                                overrideQuadruple.second,
                                overrideQuadruple.third,
                                input.getText().replace("&&", "§")
                        )
                );
            } else {
                overridesPut(
                        ItemHelper.getUUID(ItemHelper.getHeldItem()),
                        new Quadruple<>(
                                ItemHelper.getHeldItem(),
                                ItemHelper.createSecond(ItemHelper.getHeldItem(), ItemHelper.getUUID(ItemHelper.getHeldItem())),
                                ItemHelper.getHeldItem().hasGlint(),
                                input.getText().replace("&&", "§")
                        )
                );
            }

            return null;
        });

        // Item Texture Display
        UIComponent itemDisplayBox = new UIRoundedRectangle(5f)
                .setX(new CenterConstraint())
                .setY(new PixelConstraint(100f))
                .setWidth(new PixelConstraint(192f))
                .setHeight(new PixelConstraint(192f))
                .setColor(new Color(40, 40, 40, 255));

        AnimatedItemComponent itemDisplay = (AnimatedItemComponent) GUIs.createItemTextureComponent()
                .setX(new CenterConstraint())
                .setY(new CenterConstraint())
                .setWidth(new PixelConstraint(128f))
                .setHeight(new PixelConstraint(128f));

        // Glint Toggle
        UIComponent glintToggleBox = new UIRoundedRectangle(5f)
                .setX(new CenterConstraint())
                .setY(new PixelConstraint(300f))
                .setWidth(new PixelConstraint(150f))
                .setHeight(new PixelConstraint(20f))
                .setColor(new Color(40, 40, 40, 255));

        UIComponent glintToggleText = new UIText()
                .setText("Toggle Glint Effect")
                .setX(new PixelConstraint(10f))
                .setY(new CenterConstraint())
                .setColor(new Color(255, 255, 255, 255))
                .setTextScale(new PixelConstraint(1.0f));

        UIComponent glintToggleButton = new UIRoundedRectangle(5f)
                .setX(new PixelConstraint(130f))
                .setY(new CenterConstraint())
                .setWidth(new PixelConstraint(12.5f))
                .setHeight(new PixelConstraint(12.5f))
                .setColor(ItemHelper.getHeldItem().hasGlint() ? Color.GREEN : Color.RED);

        glintToggleButton.onMouseClick((comp, event) -> {
            String uuid = ItemHelper.getUUID(ItemHelper.getHeldItem());
            if (overrides.containsKey(uuid)) {
                Quadruple<ItemStack, ItemStack, Boolean, String> overrideQuadruple = overrides.get(uuid);
                overridesPut(
                        uuid,
                        new Quadruple<>(
                                overrideQuadruple.first,
                                overrideQuadruple.second,
                                !overrideQuadruple.third,
                                input.getText().replace("&&", "§")
                        )
                );
            } else {
                overridesPut(
                        ItemHelper.getUUID(ItemHelper.getHeldItem()),
                        new Quadruple<ItemStack, ItemStack, Boolean, String>(
                                ItemHelper.getHeldItem(),
                                ItemHelper.createSecond(ItemHelper.getHeldItem(), ItemHelper.getUUID(ItemHelper.getHeldItem())),
                                !ItemHelper.getHeldItem().hasGlint(),
                                input.getText().replace("&&", "§")
                        )
                );
            }
            glintToggleButton.setColor(
                    glintToggleButton.getColor().equals(Color.RED)
                            ? Color.GREEN
                            : Color.RED
            );

            // Not sure if needed, TODO: Test if needed
            ItemTooltipCallback.EVENT.register((stack, ctx, type, list) -> {
                if (uuid == null || !overrides.containsKey(uuid)) return;
                ItemHelper.replaceTooltipAt(0, list, overrides.get(uuid).fourth);
            });
            return null;
        });

        // Item ID Input
        UIComponent itemIDInputBox = new UIRoundedRectangle(5f)
                .setX(new CenterConstraint())
                .setY(new PixelConstraint(340f))
                .setWidth(new PixelConstraint(200f))
                .setHeight(new PixelConstraint(25f))
                .setColor(new Color(40, 40, 40, 255));

        UITextInput itemIDInput = (UITextInput) new UITextInput()
                .setX(new CenterConstraint())
                .setY(new PixelConstraint(8f))
                .setWidth(new PixelConstraint(175f))
                .setHeight(new PixelConstraint(20f))
                .setColor(Color.WHITE);

        UIComponent itemIdCheckBox = new UIRoundedRectangle(5f)
                .setX(new PixelConstraint(175f))
                .setY(new CenterConstraint())
                .setWidth(new PixelConstraint(20f))
                .setHeight(new PixelConstraint(20f))
                .setColor(new Color(40, 40, 40, 255));

        UIText itemIdCheck = (UIText) new UIText("✔")
                .setX(new CenterConstraint())
                .setY(new CenterConstraint())
                .setWidth(new PixelConstraint(10f))
                .setHeight(new PixelConstraint(10f))
                .setColor(Color.GREEN);

        itemIDInput.setText(ItemHelper.getItemId(ItemHelper.getHeldItem()));
        itemIDInput.onMouseClick((inp, ignored) -> {
            itemIDInput.grabWindowFocus();
            return null;
        });
        itemIdCheck.onMouseClick((comp, event) -> {
            if (CustomItemRender.canReplace(itemIDInput.getText())) {
                CustomItemRender.replaceItem(itemIDInput.getText());
                itemDisplay.reload();

                ItemTooltipCallback.EVENT.register((stack, ctx, type, list) -> {
                    String uuid = ItemHelper.getUUID(stack);
                    if (uuid == null || !overrides.containsKey(uuid)) return;
                    ItemHelper.replaceTooltipAt(0, list, overrides.get(uuid).fourth);
                });
            } else {
                itemIdCheck.setText("✘");
                itemIdCheck.setColor(Color.RED);
                Helper.sleep(1000, () -> {
                    itemIdCheck.setColor(Color.GREEN);
                    itemIdCheck.setText("✔");
                });
            }
            return null;
        });

        /*
        main.onKeyType((comp, character, keycode) -> {
            if (keycode == 256) { // Escape key
                this.close();
            }
           return null;
        });
        */

        // Header
        main.addChild(titleBox);
        titleBox.addChild(titleText);
        GUIs.addShadow(titleBox);

        // Name Input
        main.addChild(inputBox);
        inputBox.addChild(input);
        GUIs.addBorder(inputBox, 1.5f, Color.WHITE);

        // Item texture display
        main.addChild(itemDisplayBox);
        itemDisplayBox.addChild(itemDisplay);
        GUIs.addShadow(itemDisplayBox);

        // Glint toggle
        main.addChild(glintToggleBox);
        glintToggleBox.addChild(glintToggleText);
        glintToggleBox.addChild(glintToggleButton);
        GUIs.addRoundedBorder(glintToggleButton, 1f, new Color(80, 80, 80, 255));
        GUIs.addRoundedBorder(glintToggleBox, 1.5f, new Color (80, 80, 80, 255));

        // Item ID Input
        main.addChild(itemIDInputBox);
        itemIDInputBox.addChild(itemIDInput);
        itemIDInputBox.addChild(itemIdCheckBox);
        itemIdCheckBox.addChild(itemIdCheck);
        GUIs.addShadow(itemIdCheckBox, 1f);
        GUIs.addBorder(itemIDInputBox, 1.5f, Color.WHITE);

    }

}
