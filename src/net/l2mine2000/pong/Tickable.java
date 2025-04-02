package net.l2mine2000.pong;

public interface Tickable {
    void tick(Pong pPong);

    default void tick() {
        this.tick(Pong.getInstance());
    }
}
