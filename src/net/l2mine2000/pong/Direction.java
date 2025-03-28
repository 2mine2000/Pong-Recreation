package net.l2mine2000.pong;

public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public Direction getOpposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    public Direction getClockwise() {
        return switch (this) {
            case UP -> RIGHT;
            case DOWN -> LEFT;
            case LEFT -> UP;
            case RIGHT -> DOWN;
        };
    }

    public Direction getCounterClockwise() {
        return switch (this) {
            case UP -> LEFT;
            case DOWN -> RIGHT;
            case LEFT -> DOWN;
            case RIGHT -> UP;
        };
    }

    public Axis getAxis() {
        return switch (this) {
            case UP, DOWN -> Axis.Y;
            case LEFT, RIGHT -> Axis.X;
        };
    }

    public enum Axis {
        X,
        Y;

        public Direction getDirection(Pong.DiagonalDirection pCorner) {
            return switch (pCorner) {
                case TOP_LEFT -> this.equals(X) ? LEFT : UP;
                case TOP_RIGHT -> this.equals(X) ? RIGHT : UP;
                case BOTTOM_LEFT -> this.equals(X) ? LEFT : DOWN;
                case BOTTOM_RIGHT -> this.equals(X) ? RIGHT : DOWN;
            };
        }

        public Direction[] getDirections() {
            Direction[] directions = new Direction[2];
            switch (this) {
                case X -> {
                    directions[0] = LEFT;
                    directions[1] = RIGHT;
                }
                case Y -> {
                    directions[0] = UP;
                    directions[1] = DOWN;
                }
            }return directions;
        }
    }

    public enum AxisX {
        LEFT,
        RIGHT;

        public Direction get() {
            return switch (this) {
                case LEFT -> Direction.LEFT;
                case RIGHT -> Direction.RIGHT;
            };
        }
    }

    public enum AxisY {
        UP,
        DOWN;

        public Direction get() {
            return switch (this) {
                case UP -> Direction.UP;
                case DOWN -> Direction.DOWN;
            };
        }
    }
}
