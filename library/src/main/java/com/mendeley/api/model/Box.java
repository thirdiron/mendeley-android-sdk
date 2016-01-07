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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Box box = (Box) o;

        if (bottomRight != null ? !bottomRight.equals(box.bottomRight) : box.bottomRight != null)
            return false;
        if (page != null ? !page.equals(box.page) : box.page != null) return false;
        if (topLeft != null ? !topLeft.equals(box.topLeft) : box.topLeft != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = topLeft != null ? topLeft.hashCode() : 0;
        result = 31 * result + (bottomRight != null ? bottomRight.hashCode() : 0);
        result = 31 * result + (page != null ? page.hashCode() : 0);
        return result;
    }
}
