package cn.haitaoss.tinyioc;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-22 19:42
 *
 */
public class Anything {
    private Point point;

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "Anything{" +
                "point=" + point +
                '}';
    }
}

