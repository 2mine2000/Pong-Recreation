package pong;

import pong.shapes.Player;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Pong pong = Pong.getInstance();
        for (Player player : pong.players) {
            if (player.isUp(e.getKeyCode())) {
                player.up = true;
            }
            if (player.isDown(e.getKeyCode())) {
                player.down = true;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (!pong.resume()) {
                pong.pause();
            }
        }

        if (e.getKeyChar() == '1') {
            pong.startNewGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        for (Player player : Pong.getInstance().players) {
            if (player.isUp(e.getKeyCode())) {
                player.up = false;
            }
            if (player.isDown(e.getKeyCode())) {
                player.down = false;
            }
        }
    }
}
