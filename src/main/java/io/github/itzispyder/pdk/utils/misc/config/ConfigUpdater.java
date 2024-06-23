package io.github.itzispyder.pdk.utils.misc.config;

import io.github.itzispyder.pdk.utils.SchedulerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ConfigUpdater<E extends Event, C extends JsonSerializable<?>> {

    private final C config;
    private final List<CallbackReceiver<E, C, ?>> listeners = new ArrayList<>();

    public ConfigUpdater(C config) {
        this.config = config;
    }

    public void invokeCallbacks(E event) {
        new ArrayList<>(this.listeners).forEach(item -> item.invoke(config, event));
    }

    public <R> void queuePlayer(Player player, int timeoutTicks, Function<E, R> supplier, BiConsumer<C, R> onSuccess) {
        CallbackReceiver<E, C, R> receiver = new CallbackReceiver<>(player.getUniqueId(), this, supplier, onSuccess);
        listeners.add(receiver);
        SchedulerUtils.later(timeoutTicks, () -> listeners.remove(receiver));
    }

    public void removePlayer(Player player) {
        UUID id = player.getUniqueId();
        new ArrayList<>(this.listeners).forEach(item -> {
            if (id.equals(item.player))
                this.listeners.remove(item);
        });
    }

    private record CallbackReceiver<E extends Event, C extends JsonSerializable<?>, R>
            (UUID player, ConfigUpdater<E, C> updater, Function<E, R> supplier, BiConsumer<C, R> onSuccess) {
        public void invoke(C config, E event) {
            if (config == null || event == null)
                return;

            R obj = supplier.apply(event);
            if (obj != null)
                onSuccess.accept(config, obj);
            updater.listeners.remove(this);
        }
    }
}
