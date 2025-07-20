/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package GameSettings;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CustomGameMenuSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newGameMenu() {
                //return new SimpleGameMenu();
                return new MenuPausa();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
