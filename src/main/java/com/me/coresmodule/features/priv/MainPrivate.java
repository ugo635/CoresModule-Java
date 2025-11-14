package com.me.coresmodule.features.priv;

import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;

import static com.me.coresmodule.CoresModule.mc;

public class MainPrivate {
    public static void register() {
        if (!mc.getSession().getUsername().equals("JudgementCorePls")) return;
        Register.command("CmPrivateRight", args -> {
            char color = mc.getSession().getUsername().equals("JudgementCorePls") ? 'a' : 'c';
            String res = mc.getSession().getUsername().equals("JudgementCorePls") ? "successful" : "failed";
            Chat.chat("§c[CoresModulePrivate] §%cAuthentication %s".formatted(color, res));
        });
        playerTracker.register();
    }
}