package com.mendeley.api.model;

public class Box {
    public final Point topLeft;
    public final Point bottomRight;
    public final Integer page;

    public Box(Point topLeft, Point bottomRight, Integer page) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.page = page;
    }
}
